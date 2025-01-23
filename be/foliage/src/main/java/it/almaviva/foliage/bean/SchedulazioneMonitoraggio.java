package it.almaviva.foliage.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;

import org.javatuples.Pair;
import org.threeten.extra.PeriodDuration;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.services.AbstractDal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedulazioneMonitoraggio {
	public LocalDateTime dataAvvioRichiesta;
	public LocalDate dataInizio;
	public LocalDate dataFine;

	public static org.springframework.jdbc.core.RowMapper<SchedulazioneMonitoraggio> RowMapper = (rs, rn) -> {
		SchedulazioneMonitoraggio outVal = new SchedulazioneMonitoraggio();
		outVal.dataAvvioRichiesta = DbUtils.GetLocalDateTime(rs, rn, "data_rife");
		outVal.dataInizio = DbUtils.GetLocalDate(rs, rn, "data_inizio");
		outVal.dataFine = DbUtils.GetLocalDate(rs, rn, "data_fine");
		return outVal;
	};

	public static SchedulazioneMonitoraggio carica(AbstractDal dal, Integer idRichiesta) {
		String sql = """
select data_rife,
	to_date(parametri->>'dataInizio', 'YYYY-MM-dd') as data_inizio, 
	to_date(parametri->>'dataFine', 'YYYY-MM-dd') as data_fine
from foliage2.flgbatch_ondemand_tab bd
	join foliage2.flgconf_batch_tab b using (id_batch)
where b.cod_batch = 'MONITORAGGIO_SAT'
	and bd.id_batch_ondemand = :idRichiesta""";
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("idRichiesta", idRichiesta);
		
		SchedulazioneMonitoraggio sm = dal.queryForObject(
			sql,
			pars,
			SchedulazioneMonitoraggio.RowMapper
		);
		return sm;
	}

	public static void elimina(AbstractDal dal, Integer idRichiesta) {
		String sql = """
delete
from foliage2.flgbatch_ondemand_tab as bd
	using foliage2.flgconf_batch_tab b
where b.id_batch = bd.id_batch
	and b.cod_batch = 'MONITORAGGIO_SAT'
	and bd.id_batch_ondemand = :idRichiesta
returning b.cod_batch, bd.data_rife""";
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("idRichiesta", idRichiesta);
		
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
			// case "AUTO_ACCETTAZIONE": {
			// 	tableName = null;
			// }; break;
			// case "REPORT_P1_2_M": {
			// 	tableName = "foliage2.flgreport_p1_2_tab";
			// 	period = Period.ofMonths(1);
			// }; break;
			// case "REPORT_P1_2_A": {
			// 	tableName = "foliage2.flgreport_p1_2_tab";
			// 	period = Period.ofYears(1);
			// }; break;
			// case "REPORT_P3": {
			// 	tableName = "foliage2.flgreport_p3_tab";
			// }; break;
			// case "REPORT_P4": {
			// 	tableName = "foliage2.flgreport_p4_tab";
			// }; break;
			case "MONITORAGGIO_SAT": {
				// tableName = "foliage2.flgreport_p1_2_tab";
				// period = Period.ofYears(1);
				tableName = null;
			}; break;
			default: {
				throw new FoliageException("Non è possibile eliminare la richiesta");
			}
		}

// 		if (tableName != null) {
// 			pars.clear();;
// 			pars.put("dataRife", dataRife);
// 			if (period == null) {
// 				sql = String.format(
// """
// delete
// from %s
// where data_rife = :dataRife""",
// 					tableName
// 				);
// 			}
// 			else {
// 				pars.put("period", DbUtils.GetPgInterval(PeriodDuration.of(period)));
// 				sql = String.format(
// """
// delete
// from %s
// where data_rife = :dataRife
// 	and durata = :period""",
// 					tableName
// 				);
// 			}
// 			int nRows = dal.update(sql, pars);
// 			log.info(
// 				String.format(
// 					"Eliminati %d record per i risultati dell'elaborazione",
// 					nRows
// 				)
// 			);
// 		}
	}

	public void salva(AbstractDal dal, Integer idRichiesta, Integer idUtente) {
		if (dataAvvioRichiesta == null) {
			throw new FoliageException("La data di avvio richiesta non è stata indicata");
		}
		else {
			if (dataInizio == null) {
				throw new FoliageException("La data di inizio non è stata indicata");
			}
			else {		
				if (dataFine == null) {
					throw new FoliageException("La data di fine non è stata indicata");
				}
				else {
					if (dataFine.isBefore(dataAvvioRichiesta.toLocalDate())) {
						if (dataFine.isAfter(dataInizio)) {
							HashMap<String, Object> pars = new HashMap<>();
							pars.put("dataAvvioRichiesta", dataAvvioRichiesta);
							pars.put("dataInizio", dataInizio);
							pars.put("dataFine", dataFine);
							pars.put("idUtente", idUtente);
							String sql = null;
							if (idRichiesta == null) {
								sql = """
INSERT INTO foliage2.flgbatch_ondemand_tab (
		id_batch, 
		id_utente, data_inserimento, data_rife, data_avvio,
		parametri
	)
values(
		(
			select id_batch
			from foliage2.flgconf_batch_tab
			where cod_batch = 'MONITORAGGIO_SAT'
		),
		:idUtente, localtimestamp, :dataAvvioRichiesta, :dataAvvioRichiesta,
		json_build_object(
			'dataInizio', to_char(:dataInizio, 'YYYY-MM-dd'),
			'dataFine', to_char(:dataFine, 'YYYY-MM-dd')
		)
	)
	""";
							}
							else {
								pars.put("idRichiesta", idRichiesta);
								sql = """
update foliage2.flgbatch_ondemand_tab
set data_rife = :dataAvvioRichiesta,
	data_avvio = :dataAvvioRichiesta,
	id_utente = :idUtente,
	parametri = json_build_object(
			'dataInizio', to_char(:dataInizio, 'YYYY-MM-dd'),
			'dataFine', to_char(:dataFine, 'YYYY-MM-dd')
		)
where id_batch_ondemand = :idRichiesta""";
							}
							dal.update(sql, pars);
						}
						else {
							throw new FoliageException("La data di fine deve essere successiva alla data di inizio");
						}
					}
					else {
						throw new FoliageException("La data di fine non può essere successiva alla data di avvio richiesta");
					}
				}
			}
		}
	}
}
