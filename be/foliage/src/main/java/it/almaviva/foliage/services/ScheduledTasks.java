package it.almaviva.foliage.services;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.threeten.extra.PeriodDuration;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.bean.Base64FormioFile;
import it.almaviva.foliage.bean.DettagliIstruttoria;
import it.almaviva.foliage.bean.FoliagePeriodicTask;
import it.almaviva.foliage.bean.FoliagePeriodicTaskExecution;
//import it.almaviva.foliage.bean.FoliageRequestedTask;
import it.almaviva.foliage.bean.FoliageRequestedTaskExecution;
import it.almaviva.foliage.bean.FoliageTask;
import it.almaviva.foliage.bean.FoliageTaskExecution;
import it.almaviva.foliage.bean.IstanzaAutoaccettazione;
import it.almaviva.foliage.document.ModuloIstruttoria;
import it.almaviva.foliage.istanze.db.DbUtils;
import jakarta.ws.rs.core.MediaType;

@Component
@Slf4j
public class ScheduledTasks extends AbstractDal {
	
	@Autowired
	public ScheduledTasks(
		JdbcTemplate jdbcTemplate,
		TransactionTemplate transactionTemplate,
		PlatformTransactionManager platformTransactionManager
	) throws Exception {
		super(jdbcTemplate, transactionTemplate, platformTransactionManager, "ScheduledTasks");
		//this.codRegione = codRegione;
	}

	@Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
	public synchronized void executeHourly() {

		log.info("Avvio attività schedulate");


		log.info("Inizio esecuzione attività periodiche");

		HashMap<Integer, FoliagePeriodicTask> periodicTasksMap = this.getPeriodicTasksMap();
		List<FoliagePeriodicTaskExecution> periodicTasks = this.generatePeriodicPendingTasksExecutions(periodicTasksMap);
		log.info("Trovate {} attività periodiche", periodicTasks.size());
		for (FoliagePeriodicTaskExecution taskExec : periodicTasks) {
			log.info("Registrazione attività {} per {}", taskExec.task.codice, taskExec.batchTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
			try {
				this.registerPendingTask(taskExec);
			}
			catch (Exception e) {
				log.error(
					String.format("Impossibile avviare attività %s per %s", taskExec.task.codice, taskExec.batchTime.format(DateTimeFormatter.ISO_LOCAL_TIME)),
					e
				);
			}
		}

		for (FoliagePeriodicTaskExecution taskExec : periodicTasks) {

			try {
				log.info("Avvio attività {}", taskExec.task.codice);
				if (taskExec.submissionTime != null) {
					{
						DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
						TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );
						try {
							
							taskExec.numRecordElaborati = this.executeTask(taskExec);
							platformTransactionManager.commit(status);
						}
						catch (Exception e) {
							platformTransactionManager.rollback(status);
							taskExec.error = e;
						}
					}

					if (taskExec.error == null) {
						log.info("Conclusione attività {}", taskExec.task.codice);
						this.registerTaskTermination(taskExec);
					}
					else {
						this.registerTaskError(taskExec);
					}
				}

			}
			catch (Exception e) {
				throw e;
			}
		}
		log.info("Fine esecuzione attività periodiche");


		log.info("Inizio esecuzione attività richieste");
		
		HashMap<Integer, FoliageTask> requestedTasksMap = this.getTasksMap();
		List<FoliageRequestedTaskExecution> requestedTasks = this.generateRequestedPendingTasksExecutions(requestedTasksMap);
		log.info("Trovate {} attività richieste", requestedTasks.size());
		for (FoliageRequestedTaskExecution taskExec : requestedTasks) {
			log.info("Registrazione attività {} per {}", taskExec.task.codice, taskExec.batchTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
			try {
				this.registerPendingTask(taskExec);
			}
			catch (Exception e) {
				log.error(
					String.format("Impossibile avviare attività %s per %s", taskExec.task.codice, taskExec.batchTime.format(DateTimeFormatter.ISO_LOCAL_TIME)),
					e
				);
			}
		}

		for (FoliageRequestedTaskExecution taskExec : requestedTasks) {
			try {
				log.info("Avvio attività {}", taskExec.task.codice);
				if (taskExec.submissionTime != null) {
					{
						DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
						TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );
						try {
							
							taskExec.numRecordElaborati = this.executeTask(taskExec);
							platformTransactionManager.commit(status);
						}
						catch (Exception e) {
							platformTransactionManager.rollback(status);
							taskExec.error = e;
						}
					}

					if (taskExec.error == null) {
						log.info("Conclusione attività {}", taskExec.task.codice);
						this.registerTaskTermination(taskExec);
					}
					else {
						this.registerTaskError(taskExec);
					}
				}

			}
			catch (Exception e) {
				throw e;
			}
		}
		log.info("Fine esecuzione attività richieste");
	}
	public List<FoliageRequestedTaskExecution> generateRequestedPendingTasksExecutions(HashMap<Integer, FoliageTask> tasksMap) {
		String sql = """
select id_batch, id_batch_ondemand, data_avvio, data_rife, parametri, id_utente
from foliage2.flgbatch_ondemand_tab bd
	join foliage2.flgconf_batch_tab b using (id_batch)
where bd.data_avvio <= localtimestamp
	and b.cod_batch != 'MONITORAGGIO_SAT'
	and bd.data_rife != all (
		select eb.data_rife
		from foliage2.flgexecuted_batch_tab eb
		where eb.id_batch = b.id_batch
	)
	and bd.data_rife != all (
		select pb.data_rife
		from foliage2.flgpending_batch_tab pb
		where pb.id_batch = b.id_batch
	)""";
		HashMap<String, Object> pars = new HashMap<>();
		List<FoliageRequestedTaskExecution> tasksList = this.query(sql, pars, FoliageRequestedTaskExecution.getRequestedPendingRowMapper(tasksMap));
		return tasksList;
	}
	
	public List<FoliagePeriodicTaskExecution> generatePeriodicPendingTasksExecutions(HashMap<Integer, FoliagePeriodicTask> tasksMap) {
		String sql = """
select *
from (
		select b.*,
			coalesce(t1.data_batch, t2.data_batch) as data_batch
		from foliage2.flgconf_batch_tab as b
			join foliage2.flgbatch_scheduling_tab as bs using (id_batch)
			left join lateral (
				select data_batch
				from (
						values (
							(bs.data_partenza + coalesce(bs.intervallo_offset, '0 hour'::interval))
							+ (
								trunc(
									extract(EPOCH from (
										localtimestamp - (bs.data_partenza + coalesce(bs.intervallo_offset, '0 hour'::interval))
									))
									/ extract(EPOCH from bs.intervallo_frequenza))
								* bs.intervallo_frequenza
							)
						)
					) as t(data_batch)
				where t.data_batch > coalesce(
						greatest(
							(select max(e.data_rife) from foliage2.flgexecuted_batch_tab e where e.id_batch = b.id_batch),
							(select max(p.data_rife) from foliage2.flgpending_batch_tab p where p.id_batch = b.id_batch)
						),
						bs.data_partenza
					)
			) as t1(data_batch) on (not bs.has_recupero_esecuzioni_mancanti)
			left join lateral (
				select data_batch
				from (
						select data_batch, data_batch - coalesce(bs.intervallo_offset, '0 hour'::interval) as data_rife
						from generate_series(
								bs.data_partenza + coalesce(bs.intervallo_offset, '0 hour'::interval),
								localtimestamp,
								bs.intervallo_frequenza
							) as t(data_batch)
					)  as t(data_batch, data_rife)
				where t.data_rife != all (
						select eb.data_rife
						from foliage2.flgexecuted_batch_tab eb
						where eb.id_batch = b.id_batch
					)
					and t.data_rife != all (
						select pb.data_rife
						from foliage2.flgpending_batch_tab pb
						where pb.id_batch = b.id_batch
					)
			) as t2(data_batch) on (bs.has_recupero_esecuzioni_mancanti)
	) as t
where data_batch is not null""";
		HashMap<String, Object> pars = new HashMap<>();
		List<FoliagePeriodicTaskExecution> tasksList = this.query(sql, pars, FoliagePeriodicTaskExecution.getPeriodicPendingRowMapper(tasksMap));
		return tasksList;
	}

	public HashMap<Integer, FoliagePeriodicTask> getPeriodicTasksMap() {
		String query = """
select id_batch, cod_batch, intervallo_offset, intervallo_frequenza
from foliage2.flgconf_batch_tab
	join foliage2.flgbatch_scheduling_tab using (id_batch)""";
		HashMap<String, Object> pars = new HashMap<>();
		List<FoliagePeriodicTask> tasksList = this.query(query, pars, FoliagePeriodicTask.RowMapper);
		HashMap<Integer, FoliagePeriodicTask> outVal = new HashMap<>();
		for (FoliagePeriodicTask task : tasksList) {
			outVal.put(task.id, task);
		}
		return outVal;
	}

	public HashMap<Integer, FoliageTask> getTasksMap() {
		String query = """
select id_batch, cod_batch
from foliage2.flgconf_batch_tab""";
		HashMap<String, Object> pars = new HashMap<>();
		List<FoliageTask> tasksList = this.query(query, pars, FoliageTask.RowMapper);
		HashMap<Integer, FoliageTask> outVal = new HashMap<>();
		for (FoliageTask task : tasksList) {
			outVal.put(task.id, task);
		}
		return outVal;
	}


	public <T extends FoliageTask> void registerPendingTask(FoliageTaskExecution<T> taskExec) {
		T task = taskExec.task;
		taskExec.rifeTime = taskExec.evalRifeTime();
		taskExec.submissionTime = LocalDateTime.now();
		String sql = """
INSERT INTO foliage2.flgpending_batch_tab (
		id_batch, data_batch, data_rife, data_submission
	)
	VALUES(
		:idBatch, :dataBatch, :dataRife, :dataSubmission
	) returning id_pend_batch""";
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("idBatch", task.id);
		pars.put("dataBatch", taskExec.batchTime);
		pars.put("dataRife", taskExec.rifeTime);
		pars.put("dataSubmission", taskExec.submissionTime);

		taskExec.pendingId = queryForObject(sql, pars, DbUtils.GetIntegerRowMapper("id_pend_batch"));
	}

	<T extends FoliageTask> void delPending(FoliageTaskExecution<T> taskExec) {
		Integer delPendId = taskExec.pendingId;
		if (delPendId == null) {
			throw new FoliageException("booooo");
		}
		else {
			String sql2 = """
delete from foliage2.flgpending_batch_tab
where id_pend_batch = :pendingId""";
			HashMap<String, Object> pars2 = new HashMap<>();
			pars2.put("pendingId", 	taskExec.pendingId);
			int nRows = this.update(sql2, pars2);
			if (nRows == 0) {
				throw new FoliageException("booooo");
			}
		}

	}

	<T extends FoliageTask> void registerTaskError(FoliageTaskExecution<T> taskExec) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		taskExec.error.printStackTrace(pw);
		String errore = sw.toString();

		taskExec.endTime = LocalDateTime.now();
		String sql1 = """
INSERT INTO foliage2.flgerror_batch_tab (
		id_batch, data_batch, data_rife, data_submission, data_avvio, data_termine, dett_errore
	)
	VALUES(
		:idBatch, :dataBatch, :dataRife, :dataSubmission, :dataInizio, :dataFine, :errore
	) returning id_err_batch""";
		HashMap<String, Object> pars1 = new HashMap<>();
		pars1.put("idBatch", taskExec.task.id);
		pars1.put("dataBatch", taskExec.batchTime);
		pars1.put("dataRife", taskExec.rifeTime);
		pars1.put("dataSubmission", taskExec.submissionTime);
		pars1.put("dataInizio", taskExec.startTime);
		pars1.put("dataFine", taskExec.endTime);
		pars1.put("errore", errore);

		Integer idErr = queryForObject(sql1, pars1, DbUtils.GetIntegerRowMapper("id_err_batch"));

		delPending(taskExec);
	}
	<T extends FoliageTask> void registerTaskTermination(FoliageTaskExecution<T> taskExec) {

		taskExec.endTime = LocalDateTime.now();
		String sql1 = """
INSERT INTO foliage2.flgexecuted_batch_tab (
		id_batch, data_batch, data_rife, data_submission, data_avvio, data_termine, num_record_elaborati
	)
	VALUES(
		:idBatch, :dataBatch, :dataRife, :dataSubmission, :dataAvvio, :dataFine, :numRecordElaborati
	) returning id_exec_batch""";
		HashMap<String, Object> pars1 = new HashMap<>();
		pars1.put("idBatch", taskExec.task.id);
		pars1.put("dataBatch", taskExec.batchTime);
		pars1.put("dataRife", taskExec.rifeTime);
		pars1.put("dataSubmission", taskExec.submissionTime);
		pars1.put("dataAvvio", taskExec.startTime);
		pars1.put("dataFine", taskExec.endTime);
		pars1.put("numRecordElaborati", taskExec.numRecordElaborati);

		Integer idExec = queryForObject(sql1, pars1, DbUtils.GetIntegerRowMapper("id_exec_batch"));
		taskExec.idExec = idExec;

		delPending(taskExec);
	}

	Integer jobAutoAccettazioneIstanzeNonValutate(LocalDateTime dataRife) throws Exception {

		HashMap<String, Object> pars = new HashMap<>();
		pars.put("dataRife", dataRife);

		String sql2 = """
select id_ista, id_ente_terr, codi_ista
from FOLIAGE2.flgista_tab
	join FOLIAGE2.flgtipo_istanza_tab t using (id_tipo_istanza)
	join FOLIAGE2.flgcist_tab c using (id_cist)
	join FOLIAGE2.FLGISTA_INVIO_TAB using (id_ista)
where c.durata_timer_autoaccettazione is not null
	and data_invio + c.durata_timer_autoaccettazione < :dataRife
	--and data_invio  < :dataRife -- per fare i test
	and id_ista != all (
		select v.id_ista
		from foliage2.flgvalutazione_istanza_tab v
	)""";
		List<IstanzaAutoaccettazione> list = this.query(sql2, pars, IstanzaAutoaccettazione.RowMapper);
		int numIstanze = 0;
		for (IstanzaAutoaccettazione istanzaAutoaccettazione : list) {
			numIstanze++;
			LocalDateTime currTime = LocalDateTime.now();
			String codIstanza = istanzaAutoaccettazione.codIstanza;
			Integer idIstanza = istanzaAutoaccettazione.idIstanza;

			pars.clear();
			String sqlSelUteAssegnato = """
select id_utente_istruttore
from foliage2.flgassegnazione_istanza_tab
where id_ista = :idIstanza""";
			pars.put("idIstanza", idIstanza);
			Integer idUtenteAssegnato = null;
			try {
				idUtenteAssegnato = queryForObject(sqlSelUteAssegnato, pars, DbUtils.GetIntegerRowMapper("id_utente_istruttore"));
			}
			catch (EmptyResultDataAccessException ee) {
				idUtenteAssegnato = null;
			}
			
			if (idUtenteAssegnato == null) {
				idUtenteAssegnato = -1;
				String sqlInsUteAssegnato = """
insert into foliage2.flgassegnazione_istanza_tab(id_ista, id_utente_istruttore, id_utente_assegnazione, data_assegnazione)
	values (:idIstanza, :idUtenteAssegnato, :idUtenteAssegnato, :currTime)""";
				pars.put("idUtenteAssegnato", idUtenteAssegnato);
				pars.put("currTime", currTime);
				int nRows = update(sqlInsUteAssegnato, pars);
				if (nRows != 1) {
					throw new FoliageException("Problema nel salvare i dati");
				}
			}
			
			String oggetto = "";
			String testo = "Istanza approvata automaticamente per decorrenza dei termini";
			String uleterioriDest = null;

			pars.clear();
			pars.put("idIstanza", idIstanza);
			pars.put("oggetto", oggetto);
			pars.put("testo", testo);
			pars.put("uleterioriDest", uleterioriDest);
			
			String sqlInsDati = """
insert into FOLIAGE2.FLGISTA_DATI_ISTRUTTORIA_TAB(ID_ISTA, OGGETTO, ULTERIORI_DESTINATARI, TESTO)
	values (:idIstanza, :oggetto, :uleterioriDest, :testo)
	on conflict(ID_ISTA)
	do update set (OGGETTO, ULTERIORI_DESTINATARI, TESTO)
		= (excluded.OGGETTO, excluded.ULTERIORI_DESTINATARI, excluded.TESTO)""";
			
			int nRows = update(sqlInsDati, pars);
			if (nRows != 1) {
				throw new FoliageException("Problema nel salvare i dati");
			}

			pars.clear();
			pars.put("codIstanza", codIstanza);

			LocalDateTime dataValutazione = LocalDateTime.now();
			ModuloIstruttoria pdfModulo = queryForObject(
				ModuloIstruttoria.queryFromCodiIsta,
				pars,
				ModuloIstruttoria.RowMapper(this, dataValutazione, codIstanza)
			);
			
			ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
			pdfModulo.creaPdf(pdfStream);
			Base64FormioFile fileModulo = new Base64FormioFile();
			byte[] arr = pdfStream.toByteArray();
			String name = String.format("%s_modulo.pdf", codIstanza);
			fileModulo.setName(name);
			fileModulo.setOriginalName(name);
			fileModulo.setHash("");
			fileModulo.setSize(arr.length);
			fileModulo.setStorage("base64");
			fileModulo.setType("");
			
			fileModulo.loadUrlFromArray(
				arr, 
				MediaType.APPLICATION_OCTET_STREAM//"application/octet-stream"
			);

			Base64FormioFile[] modulo = new Base64FormioFile[] { fileModulo };
			
			Integer idFileModulo = DbUtils.saveBase64FormioFiles(this, modulo);
			if (idFileModulo == null) {
				throw new FoliageException("Si è verificato un problema nel caricamento del modulo");
			}

			Integer idUtente = -1;
			Boolean esito = true;
			String noteValutazione = "Istanza approvata automaticamente";
			pars.put("idUtente", idUtente);
			pars.put("esito", esito);
			pars.put("noteValutazione", noteValutazione);
			pars.put("dataValutazione", dataValutazione);
			pars.put("idFileModulo", idFileModulo);

			String sqlInsValutazione = """
insert into foliage2.flgvalutazione_istanza_tab (
		ID_ISTA, ESITO_VALUTAZIONE, NOTE_VALUTAZIONE, DATA_VALUTAZIONE, ID_UTENTE_ISTRUTTORE, DATA_FINE_VALIDITA, ID_FILE_MODULO_ISTRUTTORIA
	)
select ID_ISTA, :esito, :noteValutazione, :dataValutazione, :idUtente, current_date + make_interval(months => c.mesi_validita), :idFileModulo
from foliage2.FLGISTA_TAB i
	join foliage2.flgtipo_istanza_tab t on (t.id_tipo_istanza = i.id_tipo_istanza)
	join foliage2.flgcist_tab c on (c.id_cist= t.id_cist)
where CODI_ISTA = :codIstanza
	and STATO = any(
			select id_stato
			from foliage2.flgstato_istanza_tab s
			where s.cod_stato in ('ISTRUTTORIA', 'PRESENTATA')
		)""";
			int numRows = update(sqlInsValutazione, pars);
			if (numRows != 1) {
				throw new FoliageException("L'istanza indicata non risultata tra quelle in istruttoria");
			}


			pars.clear();
			pars.put("codIstanza", codIstanza);

			String sqlIdGestore = """
select id_utente_compilazione
from FOLIAGE2.FLGISTA_TAB
where CODI_ISTA = :codIstanza""";
			
			Integer idUtenteGestore = template.queryForObject(
				sqlIdGestore, 
				pars,
				(rs, rn) -> {
					return rs.getInt("id_utente_compilazione");
				}
			);

			pars.put("codStato", "APPROVATA");
			String sqlAggStato = """
update foliage2.flgista_tab
set stato = (
		select id_stato
		from foliage2.flgstato_istanza_tab s
		where s.cod_stato = :codStato
	)
where codi_ista = :codIstanza
	and stato = any (
			select id_stato
			from foliage2.flgstato_istanza_tab s
			where s.cod_stato in ('ISTRUTTORIA', 'PRESENTATA')
		)""";
			numRows = update(
				sqlAggStato,
				pars
			);
			if (numRows != 1) {
				throw new FoliageException("L'istanza indicata non risultata tra quelle in istruttoria");
			}
			

			inserisciNotificaUtente(
				idUtenteGestore,
				"Un'istanza che avevi presentato è stata approvata",
				String.format("istanze/%s/consulta-valutazione", codIstanza)
			);

			pars.clear();
			String sql3 = """
insert into foliage2.flgreport_autoaccettazione_istanze_tab(
		data_rife, id_ista, id_ente_terr 
	)
values(
		:dataRife, :idIsta, :idEnte
	)""";
			pars.put("dataRife", dataRife);
			pars.put("idIsta", istanzaAutoaccettazione.idIstanza);
			pars.put("idEnte", istanzaAutoaccettazione.idEnte);
			this.update(sql3, pars);
		}
		return numIstanze;
	}
	
	private int inserisciNotificaUtente(Integer idUtente, String messaggio, String link) {
		try {
			Map<String, Object> mapParam = new HashMap<String, Object>();
			mapParam.put("idUtente", idUtente);
			mapParam.put("messaggio", messaggio);
			mapParam.put("link", link);
			SqlParameterSource parameters = new MapSqlParameterSource(mapParam);
			String sql = """
insert into FOLIAGE2.FLGNOTIFICHE_TAB(ID_UTENTE, TESTO, LINK, DATA_NOTIFICA)
	values(:idUtente, :messaggio, :link, localtimestamp)
				""";

			int res = template.update(sql, parameters);
			return res;
		}catch (Exception e) {
			throw e;
		}
		finally {
		}
	}

	Integer jobReportP3(LocalDateTime dataRife) throws Exception {
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("dataRife", dataRife);
		String sql = """
insert into foliage2.flgreport_p3_tab (
		data_rife, codice, id_ista, esito_valutazione,
		shape_vinc, superficie_vinc, superficie_pf, superficie_utile, massa,
		perc_dist
	)
select :dataRife, sn2.codice, id_ista, val.esito_valutazione,
	shape_vinc, superficie_vinc, pf.superficie_pf, uo.superficie_utile, massa_mc as massa,
	(st_area(sn2.geom) / superficie_vinc) as perc_dist
from foliage_extra.sitiprotetti_natura_2000 as sn2
	join lateral (
		select id_ista,
			sum(vi.superficie) as superficie_vinc,
			st_union(shape) as shape_vinc
		from foliage2.flgvincoli_ista_tab as vi
			join foliage2.flgvincoli_tab as v using (id_vincolo)
		where v.cod_vincolo = 'NAT2K'
			and vi.cod_area = sn2.codice
			and vi.superficie > 0
		group by id_ista
	) as vi on (true)
	join foliage2.flgista_tab as i using (id_ista)
	join foliage2.flgtipo_istanza_tab as ti using (id_tipo_istanza)
	join foliage2.flgista_invio_tab inv using (id_ista)
	left join foliage2.flgvalutazione_istanza_tab as val using (id_ista)
	left join lateral (
		select sum(pf.superficie) as superficie_pf
		from foliage2.flgparticella_forestale_shape_tab as pf
		where pf.superficie > 0
			and pf.id_ista = vi.id_ista
	) as pf on (true)
	left join lateral (
		select sum(uo.superficie_utile) as superficie_utile
		from foliage2.flgunita_omogenee_tab as uo
		where uo.id_ista = vi.id_ista
	) as uo on (true)
	left join lateral (
		select sum(valore_mq_ha) as massa_mc
		from foliage2.flgunita_omogenee_val_cubatura_tab as uov
		where uov.id_ista = vi.id_ista
			and uov.cat_cubatura = 'volTOTALE'
			and uov.cod_gruppo_cubatura = 'TAGLIA'
	) as v on (true)
where ti.cod_tipo_istanza_specifico in ('SOPRA_SOGLIA', 'IN_DEROGA', 'ATTUAZIONE_PIANI')
	and (
		inv.data_invio < :dataRife
		and inv.data_invio >= :dataRife - '1years'::interval
	)
	and val.esito_valutazione""";
		int nRows = this.update(sql, pars);
		return nRows;
	}
	Integer jobReportP4(LocalDateTime dataRife) throws Exception {
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("dataRife", dataRife);
		String sql = """
INSERT INTO foliage2.flgreport_p4_tab (
		data_rife, cod_indicatore, numero_istanze, numero_istanze_autorizzate, numero_istanze_non_autorizzate, supe_istanze_autorizzate, supe_privata,
		supe_pubblica, supe_uso_civico, supe_altro, supe_ceduo, supe_fustaia, supe_misto,
		vol_totale, vol_ardere_conifere, vol_ardere_nonconifere, vol_legname_conifere, vol_legname_nonconifere,
		vol_impiallaccitura_conifere, vol_impiallaccitura_nonconifere, vol_paste_conifere, vol_paste_nonconifere,
		vol_altro_conifere, vol_altro_nonconifere,
		supe_cat_1, supe_cat_2, supe_cat_3, supe_cat_4, supe_cat_5, supe_cat_6, supe_cat_7, supe_cat_8, supe_cat_9, supe_cat_10,
		supe_cat_11, supe_cat_12, supe_cat_13, supe_cat_14, supe_cat_15, supe_cat_16, supe_cat_17, supe_cat_18, supe_cat_19, supe_cat_20,
		supe_cat_21, supe_cat_22, supe_cat_23
	)
select :dataRife as data_rife,
	INDICATORE,
	coalesce(count(*),0) as numero_istanze,
	sum(coalesce(case when esito then 1 end, 0)) as numero_istanze_autorizzate,
	sum(coalesce(case when not esito then 1 end, 0)) as numero_istanze_non_autorizzate,
	sum(coalesce(case when esito then supe end, 0)) as supe_istanze_autorizzate,
	sum(coalesce(supe_privata, 0)) as supe_privata,
	sum(coalesce(supe_pubblica, 0)) as supe_pubblica,
	sum(coalesce(supe_uso_civico, 0)) as supe_uso_civico,
	sum(coalesce(supe_altro, 0)) as supe_altro,
	sum(coalesce(supe_ceduo, 0)) as supe_ceduo,
	sum(coalesce(supe_fustaia, 0)) as supe_fustaia,
	sum(coalesce(supe_misto, 0)) as supe_misto,
	coalesce(sum(vol_legna_ardere_con + vol_legna_ardere_noncon + vol_tronchi_con + vol_tronchi_noncon + vol_cellulosa_con + vol_cellulosa_noncon + vol_altro_con + vol_altro_noncon), 0) as vol_totale,
	coalesce(sum(vol_legna_ardere_con), 0) as vol_ardere_conifere,
	coalesce(sum(vol_legna_ardere_noncon), 0) as vol_ardere_nonconifere,
	coalesce(sum(vol_tronchi_con + vol_cellulosa_con + vol_altro_con), 0) as vol_legname_conifere,
	coalesce(sum(vol_tronchi_noncon + vol_cellulosa_noncon + vol_altro_noncon), 0) as vol_legname_nonconifere,
	coalesce(sum(vol_tronchi_con), 0) as vol_impiallaccitura_conifere,
	coalesce(sum(vol_tronchi_noncon), 0) as vol_impiallaccitura_nonconifere,
	coalesce(sum(vol_cellulosa_con), 0) as vol_paste_conifere,
	coalesce(sum(vol_cellulosa_noncon), 0) as vol_paste_nonconifere,
	coalesce(sum(vol_altro_con), 0) as vol_altro_conifere,
	coalesce(sum(vol_altro_noncon), 0) as vol_altro_nonconifere,
	coalesce(sum(case when cod_categoria = '1' then supe end), 0) as supe_cat_1,
	coalesce(sum(case when cod_categoria = '2' then supe end), 0) as supe_cat_2,
	coalesce(sum(case when cod_categoria = '3' then supe end), 0) as supe_cat_3,
	coalesce(sum(case when cod_categoria = '4' then supe end), 0) as supe_cat_4,
	coalesce(sum(case when cod_categoria = '5' then supe end), 0) as supe_cat_5,
	coalesce(sum(case when cod_categoria = '6' then supe end), 0) as supe_cat_6,
	coalesce(sum(case when cod_categoria = '7' then supe end), 0) as supe_cat_7,
	coalesce(sum(case when cod_categoria = '8' then supe end), 0) as supe_cat_8,
	coalesce(sum(case when cod_categoria = '9' then supe end), 0) as supe_cat_9,
	coalesce(sum(case when cod_categoria = '10' then supe end), 0) as supe_cat_10,
	coalesce(sum(case when cod_categoria = '11' then supe end), 0) as supe_cat_11,
	coalesce(sum(case when cod_categoria = '12' then supe end), 0) as supe_cat_12,
	coalesce(sum(case when cod_categoria = '13' then supe end), 0) as supe_cat_13,
	coalesce(sum(case when cod_categoria = '14' then supe end), 0) as supe_cat_14,
	coalesce(sum(case when cod_categoria = '15' then supe end), 0) as supe_cat_15,
	coalesce(sum(case when cod_categoria = '16' then supe end), 0) as supe_cat_16,
	coalesce(sum(case when cod_categoria = '17' then supe end), 0) as supe_cat_17,
	coalesce(sum(case when cod_categoria = '18' then supe end), 0) as supe_cat_18,
	coalesce(sum(case when cod_categoria = '19' then supe end), 0) as supe_cat_19,
	coalesce(sum(case when cod_categoria = '20' then supe end), 0) as supe_cat_20,
	coalesce(sum(case when cod_categoria = '21' then supe end), 0) as supe_cat_21,
	coalesce(sum(case when cod_categoria = '22' then supe end), 0) as supe_cat_22,
	coalesce(sum(case when cod_categoria = '23' then supe end), 0) as supe_cat_23
from (
		select i.id_ente_terr, i.id_ista, prog_uog, cod_categoria,
			case when cod_tipo_istanza_specifico in ('SOPRA_SOGLIA', 'IN_DEROGA') then 'A' else 'B' end as INDICATORE,
			coalesce(v.esito_valutazione, false) as esito,
			--pf.superficie_pfor as supe,
			supe_uo as supe,
			case when n.desc_nprp = 'P.Privata' then supe_uo end as supe_privata,
			case when n.desc_nprp = 'P.Pubblica' then supe_uo end as supe_pubblica,
			case when 1 = 0 then supe_uo end as supe_uso_civico,
			case when coalesce(n.desc_nprp, '') not in ('P.Privata', 'P.Pubblica') then supe_uo end as supe_altro,
			supe_ceduo, supe_fustaia, supe_misto,
			vol.vol_totale, vol.vol_ceduo, vol.vol_fustaia,
			vol.vol_legna_ardere_con, vol.vol_legna_ardere_noncon,
			vol.vol_tronchi_con, vol.vol_tronchi_noncon,
			vol.vol_cellulosa_con, vol.vol_cellulosa_noncon,
			vol.vol_altro_con, vol.vol_altro_noncon
		from foliage2.flgista_tab i
			join foliage2.flgista_invio_tab inv using (id_ista)
			join foliage2.flgtipo_istanza_tab using (id_tipo_istanza)
			join foliage2.flgnprp_tab n using (id_nprp)
			left join foliage2.flgvalutazione_istanza_tab val using (id_ista)
			left join lateral (
				select suo.prog_uog, suo.cod_categoria,
					coalesce(suo.supe_uo, sitb.supe_uo) as supe_uo,
					coalesce(suo.supe_ceduo, sitb.supe_ceduo) as supe_ceduo,
					coalesce(suo.supe_fustaia, sitb.supe_fustaia) as supe_fustaia,
					coalesce(suo.supe_misto, sitb.supe_misto) as supe_misto
				from (
						select uo.prog_uog, c.cod_categoria,
							(superficie_utile/10000) as supe_uo,
							case when desc_gove = 'Ceduo' then (superficie_utile/10000) else 0 end as supe_ceduo,
							case when desc_gove = 'Fustaia' then (superficie_utile/10000) else 0 end as supe_fustaia,
							case when coalesce(desc_gove, 'Misto') = 'Misto' then (superficie_utile/10000) else 0  end as supe_misto
						from foliage2.flgunita_omogenee_tab uo
							join foliage2.flgcategorie_tab as c using (id_categoria)
						where uo.id_ista = i.id_ista
					) suo
					full outer join (
						select superficie_intervento as supe_uo,
							coalesce(case when desc_gove = 'Ceduo' then superficie_intervento end, 0) as supe_ceduo,
							coalesce(case when desc_gove = 'Fustaia' then superficie_intervento end, 0) as supe_fustaia,
							coalesce(case when coalesce(desc_gove, 'Misto') = 'Misto' then superficie_intervento end, 0) as supe_misto
						from foliage2.flgista_taglio_boschivo_tab itb
						where itb.id_ista = i.id_ista
					) sitb on (true)
			) uo on (true)
			left join lateral (
				select sum(vol_totale) as vol_totale,
					sum(vol_ceduo) as vol_ceduo,
					sum(vol_fustaia) as vol_fustaia,
					sum(vol_totale * cx_legna_con) as vol_legna_ardere_con,
					sum(vol_totale * cx_legna_ardere_noncon) as vol_legna_ardere_noncon,
					sum(vol_totale * cx_tronchi_con) as vol_tronchi_con,
					sum(vol_totale * cx_tronchi_noncon) as vol_tronchi_noncon,
					sum(vol_totale * cx_cellulosa_con) as vol_cellulosa_con,
					sum(vol_totale * cx_cellulosa_noncon) as vol_cellulosa_noncon,
					sum(vol_totale * cx_altro_con) as vol_altro_con,
					sum(vol_totale * cx_altro_noncon) as vol_altro_noncon 
				from (
						select prog_uog,
							vol_ceduo + vol_fustaia as vol_totale,
							vol_ceduo, vol_fustaia,
							cx_legna_con, cx_legna_ardere_noncon,
							cx_tronchi_con, cx_tronchi_noncon,
							cx_cellulosa_con, cx_cellulosa_noncon,
							cx_altro_con, cx_altro_noncon
						from (
								select max(case when uovc.cat_cubatura = 'volCEDUO' then uovc.valore_mq_ha else 0 end) as vol_ceduo,
									max(case when uovc.cat_cubatura = 'volFUSTAIA' then uovc.valore_mq_ha else 0 end) as vol_fustaia
								from foliage2.flgunita_omogenee_val_cubatura_tab uovc
								where uovc.cod_gruppo_cubatura = 'TAGLIA'
									and uovc.cat_cubatura in ('volCEDUO', 'volFUSTAIA')
									and uovc.id_ista = i.id_ista
									and uovc.prog_uog = uo.prog_uog
							) as vuo
							left join (
								select sum(case when is_conifiera and id_assortimento in (1, 2) then percentuale_intervento/100 * percentuale_ass/100 else 0 end) as cx_legna_con,
									sum(case when not is_conifiera and id_assortimento in (1, 2) then percentuale_intervento/100 * percentuale_ass/100 else 0 end) as cx_legna_ardere_noncon,
									sum(case when is_conifiera and id_assortimento = 3 then percentuale_intervento/100 * percentuale_ass/100 else 0 end) as cx_tronchi_con,
									sum(case when not is_conifiera and id_assortimento = 3 then percentuale_intervento/100 * percentuale_ass/100 else 0 end) as cx_tronchi_noncon,
									sum(case when is_conifiera and id_assortimento = 4 then percentuale_intervento/100 * percentuale_ass/100 else 0 end) as cx_cellulosa_con,
									sum(case when not is_conifiera and id_assortimento = 4 then percentuale_intervento/100 * percentuale_ass/100 else 0 end) as cx_cellulosa_noncon,
									sum(case when is_conifiera and id_assortimento = 5 then percentuale_intervento/100 * percentuale_ass/100 else 0 end) as cx_altro_con,
									sum(case when not is_conifiera and id_assortimento = 5 then percentuale_intervento/100 * percentuale_ass/100 else 0 end) as cx_altro_noncon
								from (
										select id_ista, prog_uog, id_specie,
											is_conifiera, id_assortimento,
											percentuale_intervento, percentuale_ass
										from (
												select id_ista, prog_uog, id_specie,
													tipo_soprasuolo = 'Conifera' as is_conifiera, asu.id_assortimento, 
													su.percentuale_intervento, 
													asu.percentuale_ass
												from foliage2.flgspeci_uog_tab as su
													join foliage2.flgass_speci_uog_tab as asu using (id_ista, prog_uog, id_specie)
													join foliage2.flgspecie_tab as s using (id_specie)
												where id_ista = i.id_ista 
													and su.prog_uog = uo.prog_uog
											) as ass
									) as ass
							) as pass on (true)
				) as t
			) as vol on (true)
		--where id_ente_terr = :idEnte
		where cod_tipo_istanza_specifico in ('SOPRA_SOGLIA', 'IN_DEROGA', 'TAGLIO_BOSCHIVO', 'ATTUAZIONE_PIANI')
			and (
				inv.data_invio < :dataRife
				and inv.data_invio >= :dataRife - '1years'::interval
			)
	) as T
group by INDICATORE""";
		this.update(sql, pars);
		return 0;
	}
	Integer jobReportP1eP2(LocalDateTime dataRife, PeriodDuration pd) throws Exception {
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("dataRife", dataRife);
		pars.put("interval", DbUtils.GetPgInterval(pd));

		String sql = """
INSERT INTO foliage2.flgreport_p1_2_tab (
		data_rife, durata,id, prog_uog,
		tipologia, data, id_ente_terr, stato, 
		id_prop, id_prof,
		tratt_uo, supe_uo, vol_uo, shape
	)
select :dataRife, :interval, i.codi_ista as id, prog_uog,
	c.desc_cist as tipologia, inv.data_invio as data, id_ente_terr, s.desc_stato as stato,
	t.codice_fiscale as id_prop, ug.codi_fisc as id_prof,
	forma_trattamento as tratt_uo, supe_uo as sup_uo, vol_uo, shape
from foliage2.flgista_tab i
	join foliage2.flgista_invio_tab inv using (id_ista)
	join foliage2.flgtitolare_istanza_tab t using (id_titolare)
	join foliage2.flgtipo_istanza_tab using (id_tipo_istanza)
	join foliage2.flgcist_tab c using (id_cist)
	join foliage2.flgstato_istanza_tab s on (s.id_stato = i.stato)
	left join foliage2.flgnprp_tab n using (id_nprp)
	left join foliage2.flgvalutazione_istanza_tab v using (id_ista)
	left join foliage2.flguten_tab ug on (ug.id_uten = i.id_utente_compilazione)
	cross join lateral (
		select id_ista, prog_uog, ft.desc_gove||':'||forma_trattamento as forma_trattamento,
			case when uo.desc_gove = 'Misto' then 0 else round((superficie_utile/10000), 2) end as supe_uo,
			case when uo.desc_gove = 'Misto' then 0 else round(coalesce(volume, 0), 2) end as vol_uo,
			uo.shape
		from foliage2.flgunita_omogenee_tab uo
			left join (
				select id_ista, prog_uog, g.desc_gove, ft.desc_forma_trattamento as forma_trattamento
				from foliage2.flgunita_omogenee_trattamento_tab uoft
					join foliage2.flgforme_trattamento_tab ft on (ft.id_forma_trattamento = uoft.id_forma_trattamento)
					join foliage2.flggove_tab g on (g.id_gove = uoft.id_gove)
			) as ft using (id_ista, prog_uog)
			left join (
				select id_ista, prog_uog, valore_mq_ha as volume
				from foliage2.flgunita_omogenee_val_cubatura_tab
				where cat_cubatura = 'volTOTALE'
					and cod_gruppo_cubatura = 'PRES'
			) as vc using (id_ista, prog_uog)
		where uo.id_ista = i.id_ista
		union all
		select id_ista, 0 as prog_uog, t.desc_gove||':'||forma_trattamento as forma_trattamento,
			case when itb.desc_gove = 'Misto' then 0 else round((superficie_intervento/10000), 2) end as supe_uo,
			0 as vol_uo,
			shape
		from foliage2.flgista_taglio_boschivo_tab itb
			left join lateral (
				select st_union(pf.shape) as shape
				from foliage2.flgparticella_forestale_shape_tab pf
				where id_ista = i.id_ista
			) as pf on (true)
			left join (
				select id_ista, g.desc_gove, ft.desc_forma_trattamento as forma_trattamento
				from foliage2.flgista_taglio_boschivo_trattamento_tab fttb
					join foliage2.flgforme_trattamento_tab ft on (ft.id_forma_trattamento = fttb.id_forma_trattamento)
					join foliage2.flggove_tab g on (g.id_gove = fttb.id_gove)
			) as t using (id_ista )
		where itb.id_ista = i.id_ista
	) as uo
--where id_ente_terr = :idEnte
where cod_tipo_istanza_specifico in ('SOPRA_SOGLIA', 'IN_DEROGA', 'TAGLIO_BOSCHIVO', 'ATTUAZIONE_PIANI')
	and (
		inv.data_invio < :dataRife
		and inv.data_invio >= :dataRife - :interval
	)""";
		int nRows = this.update(sql, pars);
		return nRows;
	}

	Integer jobMonitoraggio(FoliageRequestedTaskExecution taskExec) {
		return 1;

	}
	
	<T extends FoliageTask> Integer executeTask(FoliageTaskExecution<T> taskExec) throws Exception {
		T task = taskExec.task;
		taskExec.startTime = LocalDateTime.now();
		Integer nRows = null;
		switch (task.codice) {
			case "AUTO_ACCETTAZIONE": {
				nRows = jobAutoAccettazioneIstanzeNonValutate(taskExec.rifeTime);
			}; break;
			case "REPORT_P1_2_M": {
				nRows = jobReportP1eP2(taskExec.rifeTime, PeriodDuration.of(Period.of(0, 1, 0)));
			}; break;
			case "REPORT_P1_2_A": {
				nRows = jobReportP1eP2(taskExec.rifeTime, PeriodDuration.of(Period.of(1, 0, 0)));
			}; break;
			case "REPORT_P3": {
				nRows = jobReportP3(taskExec.rifeTime);
			}; break;
			case "REPORT_P4": {
				nRows = jobReportP4(taskExec.rifeTime);
			}; break;
			case "MONITORAGGIO_SAT": {
				nRows = jobMonitoraggio((FoliageRequestedTaskExecution)taskExec);
			}; break;
			default: {
				throw new FoliageException(
					String.format("Codice attività non censito %s", task.codice)
				);
			}
		}
		return nRows;
	}

}
