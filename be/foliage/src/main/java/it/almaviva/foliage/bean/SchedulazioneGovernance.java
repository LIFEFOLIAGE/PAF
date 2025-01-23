package it.almaviva.foliage.bean;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.dao.DataAccessException;
import org.threeten.extra.PeriodDuration;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.services.AbstractDal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedulazioneGovernance {
	public LocalDateTime dataAvvioRichiesta;
	public LocalDate dataRife;
	public Integer idBatch;

	public static org.springframework.jdbc.core.RowMapper<SchedulazioneGovernance> RowMapper = (rs, rn) -> {
		SchedulazioneGovernance outVal = new SchedulazioneGovernance();
		outVal.dataAvvioRichiesta = DbUtils.GetLocalDateTime(rs, rn, "data_avvio");
		outVal.dataRife = DbUtils.GetLocalDate(rs, rn, "data_rife");
		outVal.idBatch = DbUtils.GetInteger(rs, rn, "id_batch");
		return outVal;
	};

	public static SchedulazioneGovernance carica(AbstractDal dal, Integer idRichiesta) {
		String sql = """
select data_rife, b.id_batch, data_avvio
from foliage2.flgbatch_ondemand_tab bd
	join foliage2.flgconf_batch_tab b using (id_batch)
where b.id_batch = any (
		select r.id_batch 
		from foliage2.flgconf_batch_report_tab r
	)
	and bd.id_batch_ondemand = :idRichiesta""";
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("idRichiesta", idRichiesta);
		
		SchedulazioneGovernance sm = dal.queryForObject(
			sql,
			pars,
			SchedulazioneGovernance.RowMapper
		);
		return sm;
	}

	public static void elimina(AbstractDal dal, Integer idRichiesta) {
// 		String sql = """
// delete
// from foliage2.flgbatch_ondemand_tab as bd
// 	using foliage2.flgconf_batch_tab b
// where b.id_batch = bd.id_batch
// 	and b.id_batch = any (
// 		select r.id_batch 
// 		from foliage2.flgconf_batch_report_tab r
// 	)
// 	and bd.id_batch_ondemand = :idRichiesta
// returning b.cod_batch, bd.data_rife""";

		HashMap<String, Object> pars = new HashMap<>();
		pars.put("idRichiesta", idRichiesta);

		String sql = """
select exists(
		select *
		from foliage2.flgexecuted_batch_tab eb
			join foliage2.flgbatch_ondemand_tab as bd using (id_batch, data_rife)
		where bd.id_batch = any (
				select r.id_batch 
				from foliage2.flgconf_batch_report_tab r
			)
			and bd.id_batch_ondemand = :idRichiesta
	) as ret""";
		Boolean check = dal.queryForObject(sql, pars, DbUtils.GetBooleanRowMapper("ret"));
		if (check.booleanValue()) {
			throw new FoliageException("Cancellazione non eseguita correttamente");
		}
		
		sql = """
delete
from foliage2.flgbatch_ondemand_tab as bd
	using foliage2.flgconf_batch_tab b
where b.id_batch = bd.id_batch
	and b.id_batch = any (
		select r.id_batch 
		from foliage2.flgconf_batch_report_tab r
	)
	and bd.id_batch_ondemand = :idRichiesta
returning b.cod_batch, bd.data_rife""";
		Pair<String, LocalDate> tipoElab = dal.queryForObject(
			sql,
			pars,
			(rs, rn) -> {
				String elab = rs.getString("cod_batch");
				LocalDate dataRife = DbUtils.GetLocalDate(rs, rn, "data_rife");
				return new Pair<String, LocalDate>(elab, dataRife);
			}
		);



		String elab = tipoElab.getValue0();
		String tableName = null;
		Period period = null;
		LocalDate dataRife  = tipoElab.getValue1();

		switch (elab) {
			case "AUTO_ACCETTAZIONE": {
				tableName = null;
			}; break;
			case "REPORT_P1_2_M": {
				tableName = "foliage2.flgreport_p1_2_tab";
				period = Period.ofMonths(1);
			}; break;
			case "REPORT_P1_2_A": {
				tableName = "foliage2.flgreport_p1_2_tab";
				period = Period.ofYears(1);
			}; break;
			case "REPORT_P3": {
				tableName = "foliage2.flgreport_p3_tab";
			}; break;
			case "REPORT_P4": {
				tableName = "foliage2.flgreport_p4_tab";
			}; break;
			// case "MONITORAGGIO_SAT": {
			// }; break;
			default: {
				throw new FoliageException("Non è possibile eliminare la richiesta");
			}
		}

		if (tableName != null) {
			pars.clear();;
			pars.put("dataRife", dataRife);
			if (period == null) {
				sql = String.format(
"""
delete
from %s
where data_rife = :dataRife""",
					tableName
				);
			}
			else {
				pars.put("period", DbUtils.GetPgInterval(PeriodDuration.of(period)));
				sql = String.format(
"""
delete
from %s
where data_rife = :dataRife
	and durata = :period""",
					tableName
				);
			}
			int nRows = dal.update(sql, pars);
			log.info(
				String.format(
					"Eliminati %d record per i risultati dell'elaborazione",
					nRows
				)
			);
		}
	}

	public void salva(AbstractDal dal, Integer idRichiesta, Integer idUtente) {
		if (dataAvvioRichiesta == null) {
			throw new FoliageException("La data di avvio richiesta non è stata indicata");
		}
		else {
			if (dataRife == null) {
				throw new FoliageException("La data di riferimento non è stata indicata");
			}
			else {
				if (idBatch == null) {
					throw new FoliageException("La tipologia di elaborazione non è stata indicata");
				}
				else {
					HashMap<String, Object> pars = new HashMap<>();
					String sqlCheckEsistenza = """
select exists(
		select *
		from foliage2.flgexecuted_batch_tab
		where id_batch = :idBatch
			and data_rife = :dataRife
	) as ret""";
					pars.put("dataRife", dataRife);
					pars.put("idBatch", idBatch);
					Boolean res = dal.queryForObject(
						sqlCheckEsistenza,
						pars,
						DbUtils.GetBooleanRowMapper("ret")
					);
					if (res) {
						throw new FoliageException("Un'elaborazione dello stesso tipo per la stessa data di riferimento è stata già eseguita!");
					}

					pars.clear();
					String sqlCheckData = """
select data_partenza, intervallo_frequenza
from foliage2.flgbatch_scheduling_tab
where id_batch = :idBatch""";
					pars.put("idBatch", idBatch);

					Pair<LocalDate, PeriodDuration> infoBatch = null;
					try {
						infoBatch = dal.queryForObject(
							sqlCheckData,
							pars,
							(rs, rn) -> {
								LocalDate dataStart = DbUtils.GetLocalDate(rs, rn, "data_partenza");
								PeriodDuration freq = DbUtils.GetInterval(rs, idUtente, "intervallo_frequenza");
								Pair<LocalDate, PeriodDuration> outVal = new Pair<LocalDate, PeriodDuration>(dataStart, freq);
								return outVal;
							}
						);
					}
					catch (DataAccessException e) {
					}

					if (infoBatch != null) {
						LocalDate dataStart = infoBatch.getValue0();

						if (dataStart.isAfter(dataRife)) {
							throw new FoliageException(
								String.format(
									"Non è possibile eseguire quest'elaborazione per date precedenti al %s",
									dataStart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
								)
							);
						}
						else {
							PeriodDuration freq = infoBatch.getValue1();
							Period perFreq = freq.getPeriod();
							Duration durFreq = freq.getDuration();
	
							if (
								durFreq == null 
								|| (
									durFreq.getSeconds() == 0 && durFreq.getNano() == 0
								)
							) {
								LocalDate currData = dataStart;
								LocalDate prevData = null;
								while (currData.isBefore(dataRife)) {
									prevData = currData;
									currData = currData.plus(perFreq);
								}
								if (!dataRife.isEqual(currData)) {
									throw new FoliageException(
										String.format(
											"La data di riferimento indicata non è adatta per questo tipo di elaborazione. Nello stesso periodo è possibile scegliere tra %s e %s",
											prevData.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
											currData.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
										)
									);
								}
							}
						}

					}

					pars.put("dataAvvioRichiesta", dataAvvioRichiesta);
					pars.put("dataRife", dataRife);
					pars.put("idUtente", idUtente);
					String sql = null;
					if (idRichiesta == null) {
						sql = """
INSERT INTO foliage2.flgbatch_ondemand_tab (
		id_batch, 
		id_utente, data_inserimento, data_rife, data_avvio
	)
values(
	:idBatch,
	:idUtente, localtimestamp, :dataRife, :dataAvvioRichiesta
)""";
					}
					else {
						pars.put("idRichiesta", idRichiesta);
						sql = """
update foliage2.flgbatch_ondemand_tab
set data_rife = :dataRife,
	data_avvio = :dataAvvioRichiesta,
	id_batch = :idBatch,
	id_utente = :idUtente
where id_batch_ondemand = :idRichiesta""";
					}
					HashMap<String, String> errMap = new HashMap<>();
					errMap.put("\"flgbatch_ondemand_unq\"", "Non è possibile fare richieste multiple per la stessa elaborazione e la stessa data di riferimento");
					dal.update(sql, pars, errMap);
				}
			}
		}
	}
}
