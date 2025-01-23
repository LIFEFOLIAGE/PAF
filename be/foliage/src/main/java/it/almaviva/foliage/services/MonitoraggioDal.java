package it.almaviva.foliage.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.bean.AttivitaMonitoraggioBean;
import it.almaviva.foliage.bean.RecordPreelaborazioneMonitoraggio;
import it.almaviva.foliage.bean.RisultatiMonitoraggioBean;
import it.almaviva.foliage.istanze.CaricatoreIstanza;
import it.almaviva.foliage.istanze.SchedaIstanza;
import it.almaviva.foliage.istanze.db.DbUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MonitoraggioDal extends AbstractDal {

	@Value("${foliage.geometry_srid}")
	protected Integer sridGeometrie;
	
	@Value("${foliage.cod_regione}")
	protected void setCodRegione(String value) throws Exception {
		this.codRegione = value;
		if (codRegione != null) {
			String queryNomeRegione = """
select regione
from foliage2.flgregi_viw fv
where codi_istat_regione = :codRegione""";
			HashMap<String, Object> pars = new HashMap<>();
			pars.put("codRegione", codRegione);
			nomeRegione = queryForObject(queryNomeRegione, pars, DbUtils.GetStringRowMapper("regione"));
			
			SchedaIstanza.initSchedaIstanza(nomeRegione);
			CaricatoreIstanza.Inizializza();
		}
	}
	protected String codRegione;
	protected String nomeRegione;
	public String getCodRegione() {
		return this.codRegione;
	}
	
	public MonitoraggioDal(
		JdbcTemplate jdbcTemplate,
		TransactionTemplate transactionTemplate,
		PlatformTransactionManager platformTransactionManager,
		String name
	) throws Exception {
		super(jdbcTemplate, transactionTemplate, platformTransactionManager, name);
	}

	@Autowired
	public MonitoraggioDal(
		JdbcTemplate jdbcTemplate,
		TransactionTemplate transactionTemplate,
		PlatformTransactionManager platformTransactionManager
	) throws Exception {
		this(jdbcTemplate, transactionTemplate, platformTransactionManager, "MonitoraggioDal");
		//this.codRegione = codRegione;
	}

	public void salvataggioAttivita(String clientId, RisultatiMonitoraggioBean attivita) {
		String sql = """
INSERT INTO foliage2.flgexecuted_batch_tab (
		id_batch, data_batch, data_rife, data_submission, data_avvio, data_termine, num_record_elaborati
	)
select bd.id_batch, bd.data_rife, bd.data_rife, em.data_acquisizione, :dataAvvio, localtimestamp, 0
from foliage2.flgbatch_ondemand_tab bd
	join foliage2.flgesecuzioni_monitoraggio_tab em on (em.id_batch_ondemand = bd.id_batch_ondemand)
where bd.id_batch_ondemand = :idRichiesta""";
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("idRichiesta", attivita.idRichiesta);
		LocalDateTime dataInizioElaborazione = LocalDateTime.parse(attivita.dataInizioElaborazione, DateTimeFormatter.ISO_DATE_TIME);
		pars.put("dataAvvio", dataInizioElaborazione);
		//pars.put("dataTermine", attivita.dataFineElaborazione);
		update(sql, pars);
	}

	public String getGeoJsonDatiPreelaborazione(int idRichiesta) {
		String sql = """
select codi_ista, cod_tipo_istanza,
	nome_uog, cod_forma_trattamento_fustaia, cod_forma_trattamento_ceduo,
	superficie_utile, data_inizio_autorizzazione, data_fine_autorizzazione,
	shape
from FOLIAGE2.FLGDATI_PRE_MONITORAGGIO_TAB dpm
where dpm.id_batch_ondemand = :idRichiesta""";

		sql = """
SELECT json_build_object(
		'type', 'FeatureCollection',
		'features', json_agg(ST_AsGeoJSON(t.*)::json)
    ) as res
FROM (
		select codi_ista, cod_tipo_istanza,
			nome_uog, cod_forma_trattamento_fustaia, cod_forma_trattamento_ceduo,
			superficie_utile, data_inizio_autorizzazione, data_fine_autorizzazione,
			shape
		from FOLIAGE2.FLGDATI_PRE_MONITORAGGIO_TAB dpm
		where dpm.id_batch_ondemand = :idRichiesta
     ) as t;
				""";
		HashMap<String, Object> pars = new HashMap<>();	
		pars.put("idRichiesta", idRichiesta);
		//List<RecordPreelaborazioneMonitoraggio> outVal = query(sql, pars, RecordPreelaborazioneMonitoraggio.RowMapper);

		
		String geoJson = queryForObject(sql, pars, DbUtils.GetStringRowMapper("res"));

		return geoJson;
	}

	public AttivitaMonitoraggioBean recuperoAttivita(HttpServletRequest request, String clientId) {
		AttivitaMonitoraggioBean outVal = null;
		String hostName = request.getRemoteHost();
		String ipAddress = request.getRemoteAddr();
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );
		try {
			String sql = """
delete from FOLIAGE2.FLGESECUZIONI_MONITORAGGIO_TAB as em
where em.id_client = :clientId
	and not exists (
		select *
		from foliage2.flgbatch_ondemand_tab bd
			join foliage2.flgexecuted_batch_tab ex on (
				ex.data_rife = bd.data_rife
				and ex.id_batch = bd.id_batch
			)
		where bd.id_batch_ondemand = em.id_batch_ondemand
	)""";
			HashMap<String, Object> pars = new HashMap<>();	
			pars.put("clientId", clientId);
			int n = update(sql, pars);
			log.debug(String.format("Annullate %d elaborazioni incomplete del client %s", n, clientId));

			sql = """
with execution as (
		insert into FOLIAGE2.FLGESECUZIONI_MONITORAGGIO_TAB(
				id_batch_ondemand, data_acquisizione,
				id_client, hostname, ip_addres
			)
		select id_batch_ondemand, localtimestamp,
			:clientId, :hostName, :ipAddress
		from foliage2.flgbatch_ondemand_tab bd
			join foliage2.flgconf_batch_tab b using (id_batch)
		where bd.data_avvio <= localtimestamp
			and b.cod_batch = 'MONITORAGGIO_SAT'
			and bd.id_batch_ondemand not in (
				select m.id_batch_ondemand
				from FOLIAGE2.FLGESECUZIONI_MONITORAGGIO_TAB m
			)
		limit 1
		returning id_batch_ondemand
	)
select m.id_batch_ondemand, bd2.parametri
from execution as m
	join foliage2.flgbatch_ondemand_tab bd2 using (id_batch_ondemand)""";
			pars.clear();
			pars.put("clientId", clientId);
			pars.put("hostName", hostName);
			pars.put("ipAddress", ipAddress);
			try {
				outVal = queryForObject(sql, pars, AttivitaMonitoraggioBean.RowMapper);
				log.debug(String.format("Ottenuta elaborazione %d", outVal.idRichiesta));
			}
			catch(EmptyResultDataAccessException e) {
				log.debug(String.format("Nessuna elaborazione da eseguire per client ", clientId));
			}
			if (outVal != null) {
/// TODO: rivedere le condizioni per selezionare i dati solo per il periodo d'interesse
				sql = """
insert into FOLIAGE2.FLGDATI_PRE_MONITORAGGIO_TAB (
		id_batch_ondemand,
		codi_ista, cod_tipo_istanza, nome_uog,
		cod_forma_trattamento_fustaia, cod_forma_trattamento_ceduo, superficie_utile,
		data_inizio_autorizzazione, data_fine_autorizzazione,
		shape
	)
select :idRichiesta as id_batch_ondemand,
	codi_ista, c.cod_tipo_istanza, nome_uog,
	cod_forma_trattamento_fustaia, cod_forma_trattamento_ceduo,
	uo.superficie_utile,
	val.data_valutazione as data_inizio_autorizzazione, val.data_fine_validita as data_fine_autorizzazione,
	uo.shape
from foliage2.flgista_tab i
	join foliage2.flgista_invio_tab inv using (id_ista)
	join foliage2.flgtipo_istanza_tab using (id_tipo_istanza)
	join foliage2.flgcist_tab c using (id_cist)
	join foliage2.flgstato_istanza_tab s on (s.id_stato = i.stato)
	left join foliage2.flgvalutazione_istanza_tab val using (id_ista)
	cross join lateral (
		select id_ista, nome_uog,
			(
				select desc_forma_trattamento
				from foliage2.flgunita_omogenee_trattamento_tab uoft
					join foliage2.flgforme_trattamento_tab ft on (ft.id_forma_trattamento = uoft.id_forma_trattamento)
					join foliage2.flggove_tab g on (g.id_gove = uoft.id_gove)
				where uoft.id_ista = i.id_ista
					and uoft.prog_uog = uo.prog_uog
					and g.desc_gove = 'Fustaia'
			) as cod_forma_trattamento_fustaia,
			(
				select desc_forma_trattamento
				from foliage2.flgunita_omogenee_trattamento_tab uoft
					join foliage2.flgforme_trattamento_tab ft on (ft.id_forma_trattamento = uoft.id_forma_trattamento)
					join foliage2.flggove_tab g on (g.id_gove = uoft.id_gove)
				where uoft.id_ista = i.id_ista
					and uoft.prog_uog = uo.prog_uog
					and g.desc_gove = 'Ceduo'
			) as cod_forma_trattamento_ceduo,
			superficie_utile as superficie_utile,
			uo.shape
		from foliage2.flgunita_omogenee_tab uo
		where uo.id_ista = i.id_ista
		union all
		select id_ista, '' as nome_uog,
			(
				select desc_forma_trattamento
				from foliage2.flgista_taglio_boschivo_trattamento_tab ift
					join foliage2.flgforme_trattamento_tab ft on (ft.id_forma_trattamento = ift.id_forma_trattamento)
					join foliage2.flggove_tab g on (g.id_gove = ift.id_gove)
				where ift.id_ista = i.id_ista
					and g.desc_gove = 'Fustaia'
			) as forma_trattamento_fustaia,
			(
				select desc_forma_trattamento
				from foliage2.flgista_taglio_boschivo_trattamento_tab ift
					join foliage2.flgforme_trattamento_tab ft on (ft.id_forma_trattamento = ift.id_forma_trattamento)
					join foliage2.flggove_tab g on (g.id_gove = ift.id_gove)
				where ift.id_ista = i.id_ista
					and g.desc_gove = 'Ceduo'
			) as forma_trattamento_ceduo,
			superficie_intervento as superficie_utile,
			shape
		from foliage2.flgista_taglio_boschivo_tab itb
			left join lateral (
				select st_union(pf.shape) as shape
				from foliage2.flgparticella_forestale_shape_tab pf
				where id_ista = i.id_ista
			) as pf on (true)
		where itb.id_ista = i.id_ista
	) as uo
where cod_tipo_istanza_specifico in ('SOPRA_SOGLIA', 'IN_DEROGA', 'TAGLIO_BOSCHIVO', 'ATTUAZIONE_PIANI')
	and val.esito_valutazione
--	and (
--		inv.data_invio < :dataRife
--		and inv.data_invio >= :dataRife - :interval
--	)
--returning codi_ista, nome_uog, cod_forma_trattamento_fustaia, cod_forma_trattamento_ceduo, superficie_utile, data_inizio_autorizzazione, data_fine_autorizzazione, shape
""";
				pars.clear();
				pars.put("idRichiesta", outVal.idRichiesta);
				update(sql, pars);
				// outVal.datiPreelaborazione = query(
				// 	sql,
				// 	pars,
				// 	RecordPreelaborazioneMonitoraggio.RowMapper
				// );
			}
			//platformTransactionManager.rollback(status);

			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return outVal;
	}
}
