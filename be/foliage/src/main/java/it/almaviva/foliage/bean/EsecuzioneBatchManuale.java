package it.almaviva.foliage.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.services.AbstractDal;

public class EsecuzioneBatchManuale extends EsecuzioneBatch {
	public LocalDateTime dataRichiestaElaborazione;
	public LocalDateTime dataAvvioPianificata;
	public Integer idRichiesta;
	public static void elimina(AbstractDal dal, Integer idRichiesta) {
		String sql = """
delete
from foliage2.flgexecuted_batch_tab eb
	using foliage2.flgbatch_ondemand_tab as bd
where eb.id_batch = bd.id_batch
	and eb.data_rife = bd.data_rife 
	and bd.id_batch = any (
		select r.id_batch 
		from foliage2.flgconf_batch_report_tab r
	)
	and bd.id_batch_ondemand = :idRichiesta""";
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("idRichiesta", idRichiesta);
		
		dal.update(
			sql,
			pars
		);

// 		sql = """
// select exists(
// 		select *
// 		from foliage2.flgexecuted_batch_tab eb
// 			using foliage2.flgbatch_ondemand_tab as bd
// 		where eb.id_batch = bd.id_batch
// 			and eb.data_rife = bd.data_rife 
// 			and bd.id_batch = any (
// 				select r.id_batch 
// 				from foliage2.flgconf_batch_report_tab r
// 			)
// 			and bd.id_batch_ondemand = :idRichiesta
// 	) as ret""";
// 		Boolean check = dal.queryForObject(sql, pars, DbUtils.GetBooleanRowMapper("ret"));
// 		if (check.booleanValue()) {
// 			throw new FoliageException("Cancellazione non eseguita correttamente");
// 		}
	}
	public static RowMapper<EsecuzioneBatchManuale> RowMapper = (rs, rn) -> {
		EsecuzioneBatch baseOutVal = EsecuzioneBatch.RowMapper.mapRow(rs, rn);

		EsecuzioneBatchManuale outVal = new EsecuzioneBatchManuale();
		outVal.idEsecuzione = baseOutVal.idEsecuzione;
		outVal.dataSottomissione = baseOutVal.dataSottomissione;
		outVal.dataInizio = baseOutVal.dataInizio;
		outVal.dataFine = baseOutVal.dataFine;
		
		outVal.idRichiesta = DbUtils.GetInteger(rs, rn, "id_batch_ondemand");
		outVal.dataRichiestaElaborazione = DbUtils.GetLocalDateTime(rs, rn, "data_inserimento");
		outVal.dataAvvioPianificata = DbUtils.GetLocalDateTime(rs, rn, "data_avvio_pianificata");

		
		return outVal;
	};
	public static EsecuzioneBatchManuale caricaMonitoraggio(AbstractDal dal, LocalDateTime dataRife) {
		String sql = """
select id_batch_ondemand, bd.data_inserimento, bd.data_avvio as data_avvio_pianificata,
	eb.id_exec_batch, eb.data_submission, eb.data_avvio, eb.data_termine
from foliage2.flgexecuted_batch_tab eb
	join foliage2.flgbatch_ondemand_tab bd using (id_batch, data_rife)
	join foliage2.flgconf_batch_tab b using (id_batch)
where eb.data_rife = :dataRife
	and b.cod_batch = 'MONITORAGGIO_SAT'""";
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("dataRife", dataRife);
		EsecuzioneBatchManuale eb = null;
		try {
			eb = dal.queryForObject(
				sql,
				pars,
				RowMapper
			);
		}
		catch (EmptyResultDataAccessException e) {}
		return eb; 
	}

	public static EsecuzioneBatchManuale carica(AbstractDal dal, Integer idBatch, LocalDate dataRife) {
		String sql = """
select id_batch_ondemand, bd.data_inserimento, bd.data_avvio as data_avvio_pianificata,
	eb.id_exec_batch, eb.data_submission, eb.data_avvio, eb.data_termine
from foliage2.flgexecuted_batch_tab eb
	join foliage2.flgbatch_ondemand_tab bd using (id_batch, data_rife)
where eb.id_batch = :idBatch
	and eb.data_rife = :dataRife
	and bd.id_batch = any (
		select r.id_batch 
		from foliage2.flgconf_batch_report_tab r
	)""";
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("idBatch", idBatch);
		pars.put("dataRife", dataRife);
		EsecuzioneBatchManuale eb = null;
		try {
			eb = dal.queryForObject(
				sql,
				pars,
				RowMapper
			);
		}
		catch (EmptyResultDataAccessException e) {}
		return eb; 
	}
}
