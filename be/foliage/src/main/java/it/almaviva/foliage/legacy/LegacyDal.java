package it.almaviva.foliage.legacy;

import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.bean.AbilitazioniIstanza;
import it.almaviva.foliage.bean.DatiIstanza;
import it.almaviva.foliage.bean.EntitaGeometrica;
import it.almaviva.foliage.bean.FileIstanzaApp;
import it.almaviva.foliage.function.Function;
import it.almaviva.foliage.istanze.db.CampoSelect;
import it.almaviva.foliage.istanze.db.CondizioneEq;
import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.istanze.db.RecuperoDb;
import it.almaviva.foliage.legacy.bean.CatalogoLayer;
import it.almaviva.foliage.legacy.bean.File;
import it.almaviva.foliage.legacy.bean.FotoRilevamento;
import it.almaviva.foliage.legacy.bean.Istanza;
import it.almaviva.foliage.legacy.bean.PartForestale;
import it.almaviva.foliage.legacy.bean.Regione;
import it.almaviva.foliage.legacy.bean.Rilevamenti;
import it.almaviva.foliage.legacy.bean.Utente;
import it.almaviva.foliage.services.WebDal;

@Component
public class LegacyDal extends WebDal {

	@Value("${foliage.geometry_srid}")
	protected Integer sridGeometrie;

	@Autowired
	public LegacyDal(
		@Autowired JdbcTemplate jdbcTemplate,
		@Autowired TransactionTemplate transactionTemplate,
		@Autowired PlatformTransactionManager platformTransactionManager
	) throws Exception {
		super(jdbcTemplate, transactionTemplate, platformTransactionManager, "Legacy");
	}
	
	public List<Regione> getRegioniLegacy() {
		
		String sql="select * from foliage2.flgregi_tab";
		
		return jdbcTemplate.query(sql, (rs, rowNum)->{
			Regione r=new Regione();
			r.setCodiRegi(rs.getString("codi_regi"));
			r.setDescRegi(rs.getString("desc_regi"));
			return r;
		});
	}

	public Utente getLoginUtente(String username) {

		HashMap<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("user", username);
		String sql = """
select a.*
from foliage2.flguten_tab a
where a.user_name = :user""";
		Utente utente = queryForObject(sql, mapParam, Utente.RowMapper);
//TODO: Aggiungere condizioni regione
		return utente;
	}

// 	public List<Istanza> getIstanze(String cf) {

// 		Map<String, String> mapParam = new HashMap<String, String>();
// 		mapParam.put("cf", cf);
// 		mapParam.put("codiRegi", codRegione);
// 		SqlParameterSource parameters = new MapSqlParameterSource(mapParam);

// 		String sql = """
// select a.*
// from foliage2.flgista_tab a,
// 	foliage2.flgisso_tab b,
// 	foliage2.flgsogg_tab c
// where a.id_ista =b.id_ista
// 	and b.id_sogg =c.id_sogg
// 	and c.codi_fisc =:cf
// 	and a.flag_valido =1
// 	and a.data_fine_vali is null
// 	and c.flag_valido =1
// 	and a.codi_regi=:codiRegi
// 	and b.tipo_sogg = 1
// 	and c.data_fine_vali is null
// 				""";
		
// 		List<Istanza> i = template.query(sql, parameters, Istanza.RowMapper());
// 		return i;
// 	}

	public List<PartForestale> getPartForestaliForApp(int idIsta) {

		Map<String,Object> mapParam1 = new HashMap<String,Object>();
		mapParam1.put("idIsta", idIsta);
		mapParam1.put("sridGeometrie", sridGeometrie);
		SqlParameterSource params1 = new MapSqlParameterSource(mapParam1);
		
		String sql = """
SELECT a.*, st_asgeojson(st_transform(ST_SetSRID(a.shape, :sridGeometrie), 4326)) as geom 
FROM  foliage2.flgpfor_tab a
WHERE a.id_ista=:idIsta
""";

		List<PartForestale> res = template.query(sql.toString(), params1, PartForestale.RowMapper());
				
		if (res == null || res.size() == 0){
			return new ArrayList<PartForestale>();
		}
		else {
			for (PartForestale part : res) {
				
				sql = """
select pfpc.id_part 
from foliage2.flgpfpc_tab as pfpc
where pfpc.id_pfor = :idPfor
						""";
				mapParam1 = new HashMap<String, Object>();
				mapParam1.put("idPfor", part.getIdPfor());
				params1 = new MapSqlParameterSource(mapParam1);
				List<Integer> idsPart = template.queryForList(sql.toString(), params1, Integer.class);
				part.setIdPart(idsPart);
			}
			return res;    
		}
	}

	public List<File> getFilesIstanza(int idIsta) {
		Map<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("idIsta", idIsta);
		SqlParameterSource parameters = new MapSqlParameterSource(mapParam);
		
		String sql="select a.*,c.nome as tipoFile from foliage2.flgdocu_tab a, foliage2.flgisdo_tab b, foliage2.flgalle_tab c where b.id_ista=:idIsta and "
				+ "a.id_docu=b.id_docu and a.flag_valido=1 and a.data_fine_vali is null and a.id_alle=c.id_alle";

		  List<File> file=template.query(sql, parameters, File.RowMapper());
		  return file;
	}


	public AbilitazioniIstanza getAbilitazioniIdIstanza(Integer idUtente, String codFiscaleUtente, Integer idIstanza, String authority, String authScope) throws Exception {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("idIstanza", idIstanza);
		parameters.put("idUtente", idUtente);
		parameters.put("tipoAuth", authority);
		parameters.put("authScope", authScope);

		LocalDate dataAttuale = LocalDate.now();
		Function<HashMap<String, Boolean>, HashMap<String, Object>> checkFunction = null;

		
		String query = null;
		CampoSelect[] campiSelect = null;
		CondizioneEq[] condizioniWhere = new CondizioneEq[] {
			new CondizioneEq("ID_ISTA", "idIstanza")
		};
		String[] proprietaUtilizzateQuery = null;

		
		String queryAmministratore = """
(
				select i.ID_ISTA, s.COD_STATO
				from foliage2.flgista_tab as i
					join foliage2.flgstato_istanza_tab as s on (s.id_stato = i.stato)
			) as T""";
		switch (authority) {
			case "PROP": {
				query = """
(
				select i.ID_ISTA, t.CODICE_FISCALE as cod_fiscale_titolare, i.id_utente_compilazione,
					vi.data_fine_validita, p.MESI_DURATA as mesi_proroga,
					s.COD_STATO, il.data_inizio_lavori, fl.data_fine_lavori
				from foliage2.flgista_tab as i
						join foliage2.flgstato_istanza_tab as s on (s.id_stato = i.stato)
						join foliage2.FLGTITOLARE_ISTANZA_TAB as t using (ID_TITOLARE)
						left join foliage2.flgvalutazione_istanza_tab as vi using (ID_ISTA)
						left join foliage2.FLGISTA_PROROGA_TAB as p using (ID_ISTA)
						left join foliage2.FLGDATE_INIZIO_LAVORI_ISTANZA_TAB as il using (ID_ISTA)
						left join foliage2.FLGDATE_FINE_LAVORI_ISTANZA_TAB as fl using (ID_ISTA)
			) as T""";
				campiSelect = new CampoSelect[] {
					new CampoSelect("cod_fiscale_titolare", DbUtils.Get),
					new CampoSelect("id_utente_compilazione", DbUtils.Get),
					new CampoSelect("cod_stato", DbUtils.Get),
					new CampoSelect("data_fine_validita", DbUtils.GetDate),
					new CampoSelect("mesi_proroga", DbUtils.GetInteger),
					new CampoSelect("data_inizio_lavori", DbUtils.GetDate),
					new CampoSelect("data_fine_lavori", DbUtils.GetDate)
				};
				checkFunction = (HashMap<String, Object> hm) -> {
					HashMap<String, Boolean> outVal = new HashMap<String, Boolean>();
					String codFiscaleTitolare = (String) hm.get("cod_fiscale_titolare");
					Integer idGestore = (Integer) hm.get("id_utente_compilazione");
					String codStato = (String) hm.get("cod_stato");
					LocalDate dataInizioLavori = (LocalDate) hm.get("data_inizio_lavori");
					LocalDate dataFineLavori = (LocalDate) hm.get("data_fine_lavori");
					LocalDate dataFineValidita = (LocalDate) hm.get("data_fine_validita");
					Integer mesiProroga = (Integer)hm.get("mesi_proroga");
					boolean isTitolare = (codFiscaleUtente.equals(codFiscaleTitolare));
					boolean isGestore = (idUtente.equals(idGestore));
					Boolean read = isTitolare && !("COMPILAZIONE".equals(codStato));
					Boolean write= isGestore && ( "COMPILAZIONE".equals(codStato));
					Boolean allega_documenti = isGestore && ("ISTRUTTORIA".equals(codStato));
					Boolean consulta_valutazione = isGestore && (("APPROVATA".equals(codStato)) || ("RESPINTA".equals(codStato)));
					Boolean invio = write;
					Boolean access = write || read;
					Boolean changeGestore = isTitolare;
					Boolean proroga = isGestore && dataFineLavori == null && dataFineValidita != null && dataFineValidita.isAfter(LocalDate.now().minusDays(30)) && mesiProroga == null;
					

					Boolean tellInizio = isGestore && dataFineValidita != null && dataFineValidita.isAfter(dataAttuale) && (dataInizioLavori == null);
					Boolean tellFine = isGestore && dataInizioLavori != null && !dataInizioLavori.isAfter(LocalDate.now()) && dataFineLavori == null;
					outVal.put("allega_documenti", allega_documenti);
					outVal.put("access", access);
					outVal.put("consultazione", read);
					outVal.put("compilazione", write);
					outVal.put("consulta_valutazione", consulta_valutazione);
					outVal.put("invio", invio);
					outVal.put("passaggio_gestore", changeGestore);
					outVal.put("richiesta_proroga", proroga);
					outVal.put("inizio_lavori", tellInizio);
					outVal.put("fine_lavori", tellFine);
					return outVal;
				};
			}; break;
			case "PROF": {
				query = """
(
				select i.ID_ISTA, i.id_utente_compilazione, s.COD_STATO, il.data_inizio_lavori, fl.data_fine_lavori,
					vi.data_fine_validita, p.MESI_DURATA as mesi_proroga
				from foliage2.flgista_tab as i
					join foliage2.flgstato_istanza_tab as s on (s.id_stato = i.stato)
					left join foliage2.flgvalutazione_istanza_tab as vi using (ID_ISTA)
					left join foliage2.FLGISTA_PROROGA_TAB as p using (ID_ISTA)
					left join foliage2.FLGDATE_INIZIO_LAVORI_ISTANZA_TAB as il using (ID_ISTA)
					left join foliage2.FLGDATE_FINE_LAVORI_ISTANZA_TAB as fl using (ID_ISTA)
			) as T""";
				campiSelect = new CampoSelect[] {
					new CampoSelect("id_utente_compilazione", DbUtils.Get),
					new CampoSelect("cod_stato", DbUtils.Get),
					new CampoSelect("data_fine_validita", DbUtils.GetDate),
					new CampoSelect("mesi_proroga", DbUtils.GetInteger),
					new CampoSelect("data_inizio_lavori", DbUtils.GetDate),
					new CampoSelect("data_fine_lavori", DbUtils.GetDate)
				};
				checkFunction = (HashMap<String, Object> hm) -> {
					HashMap<String, Boolean> outVal = new HashMap<>();
					Integer idGestore = (Integer) hm.get("id_utente_compilazione");
					String codStato = (String) hm.get("cod_stato");
					LocalDate dataInizioLavori = (LocalDate) hm.get("data_inizio_lavori");
					LocalDate dataFineLavori = (LocalDate) hm.get("data_fine_lavori");
					LocalDate dataFineValidita = (LocalDate) hm.get("data_fine_validita");
					Integer mesiProroga = (Integer)hm.get("mesi_proroga");
					boolean isGestore = (idUtente.equals(idGestore));


					Boolean read = isGestore && ( !"COMPILAZIONE".equals(codStato));
					Boolean write= isGestore && ( "COMPILAZIONE".equals(codStato));
					Boolean allega_documenti = isGestore && ("ISTRUTTORIA".equals(codStato));
					Boolean consulta_valutazione = isGestore && (("APPROVATA".equals(codStato)) || ("RESPINTA".equals(codStato)));
					Boolean invio = write;
					Boolean access = write || read;
					Boolean changeGestore = isGestore;
					
					
					Boolean proroga = isGestore && dataFineLavori == null && dataFineValidita != null && dataFineValidita.isAfter(LocalDate.now().minusDays(30)) && mesiProroga == null;
					

					Boolean tellInizio = isGestore && ("APPROVATA".equals(codStato)) && (dataInizioLavori == null);
					Boolean tellFine = isGestore && dataInizioLavori != null && !dataInizioLavori.isAfter(LocalDate.now()) && dataFineLavori == null;
					outVal.put("allega_documenti", allega_documenti);
					outVal.put("access", access);
					outVal.put("consultazione", read);
					outVal.put("compilazione", write);
					outVal.put("consulta_valutazione", consulta_valutazione);
					outVal.put("invio", invio);
					outVal.put("passaggio_gestore", changeGestore);
					outVal.put("richiesta_proroga", proroga);
					outVal.put("inizio_lavori", tellInizio);
					outVal.put("fine_lavori", tellFine);
					return outVal;
				};
				//TODO: gestire storico gestori
			}; break;
			case "ISTR": {
				query = """
(
		select i.ID_ISTA, s.COD_STATO, id_utente_istruttore
		from foliage2.flgista_tab as i
			join foliage2.flgstato_istanza_tab as s on (s.id_stato = i.stato)
			left join foliage2.flgassegnazione_istanza_tab fit using (id_ista)
	) as T""";
				campiSelect = new CampoSelect[] {
					new CampoSelect("id_utente_istruttore", DbUtils.GetInteger),
					new CampoSelect("cod_stato", DbUtils.Get)
				};
				checkFunction = (HashMap<String, Object> hm) -> {
					Integer idIstruttore = (Integer) hm.get("id_utente_istruttore");
					String codStato = (String) hm.get("cod_stato");
					Boolean isIstruttore = (idUtente.equals(idIstruttore));
					Boolean valutazione = isIstruttore && "ISTRUTTORIA".equals(codStato);
					Boolean consulta_valutazione = isIstruttore && (("APPROVATA".equals(codStato)) || ("RESPINTA".equals(codStato)));
					HashMap<String, Boolean> outVal = new HashMap<>();

					outVal.put("consultazione", isIstruttore);
					outVal.put("consulta_valutazione", consulta_valutazione);
					outVal.put("valutazione", valutazione);
					outVal.put("access", isIstruttore);
					return outVal;
				};
			}; break;
			case "DIRI": {
				query = """
(
		select i.ID_ISTA, s.COD_STATO, id_utente_istruttore
		from foliage2.flgista_tab as i
			join foliage2.flgstato_istanza_tab as s on (s.id_stato = i.stato)
			left join foliage2.flgassegnazione_istanza_tab fit using (id_ista)
		where i.id_ente_terr in (
				select ep.id_ente 
				from foliage2.flgenti_profilo_tab ep 
					join foliage2.flgprof_tab p using (id_profilo)
				where p.tipo_auth = :tipoAuth
					and p.tipo_ambito = :authScope
					and ep.id_utente = :idUtente
			)
	) as T""";
				proprietaUtilizzateQuery = new String[] {"tipoAuth", "authScope", "idUtente"};
				campiSelect = new CampoSelect[] {
					new CampoSelect("cod_stato", DbUtils.Get),
					new CampoSelect("id_utente_istruttore", DbUtils.GetInteger)
				};
				checkFunction = (HashMap<String, Object> hm) -> {
					HashMap<String, Boolean> outVal = new HashMap<>();
					String codStato = (String) hm.get("cod_stato");
					Integer idIstruttore = (Integer) hm.get("id_utente_istruttore");

					Boolean isIstruttore = (idUtente.equals(idIstruttore));
					Boolean read = (!"COMPILAZIONE".equals(codStato));
					Boolean lavora = ("PRESENTATA".equals(codStato));
					Boolean revoca = ("ISTRUTTORIA".equals(codStato));
					Boolean valutazione = (isIstruttore && "ISTRUTTORIA".equals(codStato)) || lavora;
					Boolean consulta_valutazione = (("ISTRUTTORIA".equals(codStato)) && !isIstruttore) || ("APPROVATA".equals(codStato)) || ("RESPINTA".equals(codStato));
					Boolean cambiaIstr = lavora;
					
					outVal.put("access", read);
					outVal.put("consulta_valutazione", consulta_valutazione);
					outVal.put("valutazione", valutazione);
					outVal.put("consultazione", read);
					outVal.put("assegna_istruttore", cambiaIstr);
					outVal.put("revoca_istruttore", revoca);
					return outVal;
				};
			}; break;
			case "RESP": {
				
				query = """
(
		select i.ID_ISTA, s.COD_STATO
		from foliage2.flgista_tab as i
			join foliage2.flgstato_istanza_tab as s on (s.id_stato = i.stato)
		where i.id_ente_terr in (
				select ep.id_ente 
				from foliage2.flgenti_profilo_tab ep 
					join foliage2.flgprof_tab p using (id_profilo)
				where p.tipo_auth = :tipoAuth
					and p.tipo_ambito = :authScope
					and ep.id_utente = :idUtente
			)
	) as T""";
				proprietaUtilizzateQuery = new String[] {"tipoAuth", "authScope", "idUtente"};
				campiSelect = new CampoSelect[] {
					new CampoSelect("cod_stato", DbUtils.Get)
				};
				checkFunction = (HashMap<String, Object> hm) -> {
					HashMap<String, Boolean> outVal = new HashMap<>();
					String codStato = (String) hm.get("cod_stato");
					

					Boolean read = (!"COMPILAZIONE".equals(codStato));
					
					outVal.put("access", true);
					outVal.put("consultazione", read);
					return outVal;
				};
			}; break;
			case "AMMI":
			case "SORV": {
				query = queryAmministratore;
				campiSelect = new CampoSelect[] {
					new CampoSelect("cod_stato", DbUtils.Get)
				};
				checkFunction = (HashMap<String, Object> hm) -> {
					HashMap<String, Boolean> outVal = new HashMap<>();
					String codStato = (String) hm.get("cod_stato");
					Boolean access = true;
					Boolean read= (!"COMPILAZIONE".equals(codStato));
					Boolean consulta_valutazione = ("AMMI".equals(authority))|| ("APPROVATA".equals(codStato)) || ("RESPINTA".equals(codStato));

					outVal.put("access", access);
					outVal.put("consultazione", read);
					outVal.put("consulta_valutazione", consulta_valutazione);
					
					return outVal;
				};
			}; break;
		}

		HashMap<String, Boolean> res = null;
		if (checkFunction != null) {
			RecuperoDb rec = new RecuperoDb(
				query, 
				campiSelect,
				condizioniWhere,
				proprietaUtilizzateQuery
			);
			rec.applica(this, parameters);
	
			res = checkFunction.get(parameters);
		}
		else {
			res = new HashMap<>();
		}
		//final HashMap<String, Boolean> finalRes = res;
		//res = null;
		return new AbilitazioniIstanza(res);
		// return new Object() {
		// 	public boolean consultazione = (finalRes.containsKey("consultazione") && finalRes.get("consultazione") == true);
		// 	public boolean compilazione = (finalRes.containsKey("compilazione") && finalRes.get("compilazione") == true);
		// 	public boolean invio = (finalRes.containsKey("invio") && finalRes.get("invio") == true);
		// 	public boolean assegna_istruttore = (finalRes.containsKey("assegna_istruttore") && finalRes.get("assegna_istruttore") == true);
		// 	public boolean valutazione = (finalRes.containsKey("valutazione") && finalRes.get("valutazione") == true);
		// 	public boolean cambia_gestore = (finalRes.containsKey("passaggio_gestore") && finalRes.get("passaggio_gestore") == true);
		// 	public boolean passaggio_gestore = (finalRes.containsKey("passaggio_gestore") && finalRes.get("passaggio_gestore") == true);
		// 	public boolean richiesta_proroga = (finalRes.containsKey("richiesta_proroga") && finalRes.get("richiesta_proroga") == true);
		// 	public boolean inizio_lavori = (finalRes.containsKey("inizio_lavori") && finalRes.get("inizio_lavori") == true);
		// 	public boolean fine_lavori = (finalRes.containsKey("fine_lavori") && finalRes.get("fine_lavori") == true);
		// };
	}

	public DatiIstanza getDatiIstanza(Integer idIsta, String authority, String authScope) {
		String queryUOG = """
select nome_uog as nome, ST_AsGeoJSON(st_transform(ST_SetSRID(shape, :sridGeometrie), 4326)) as geometria, superficie
from foliage2.flgunita_omogenee_tab
where id_ista = :idIsta""";

		String queryAS = """
select nome_strato as nome, ST_AsGeoJSON(st_transform(ST_SetSRID(shape, :sridGeometrie), 4326)) as geometria, superficie_strato as superficie
from foliage2.FLGSTRATI_ISTA_TAB
where id_ista = :idIsta
	and is_area_saggio_tradizionale""";
	

		String queryPF = """
select '' as nome, ST_AsGeoJSON(st_transform(ST_SetSRID(ST_UNION(P.SHAPE), :sridGeometrie), 4326)) as geometria, sum(superficie) as superficie
from foliage2.flgparticella_forestale_shape_tab P
where id_ista = :idIsta""";
			
		
		HashMap<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("idIsta", idIsta);
		mapParam.put("sridGeometrie", sridGeometrie);

		DatiIstanza outVal = new DatiIstanza();
		outVal.unitaOmogenee = query(queryUOG, mapParam, EntitaGeometrica.RowMapper()).toArray((size) -> new EntitaGeometrica[size]);
		outVal.areeDiSaggio = query(queryAS, mapParam, EntitaGeometrica.RowMapper()).toArray((size) -> new EntitaGeometrica[size]);
		outVal.particellaForestale = queryForObject(queryPF, mapParam, EntitaGeometrica.RowMapper());

		return outVal;
	}

	public List<FileIstanzaApp> getFileIstanzaApp(Integer idIsta) {
		String query = """
			select 'Allegato Istanza' as tipo, cod_tipo_allegato as categoria, desc_altro_allegato as descrizione, id_file_allegato as id_file
			from foliage2.flgallegati_ista_tab
			where id_ista = :idIsta
			union all
			select 'Richiesta Istruttoria' as tipo, categoria, tipo_documento, id_file
			from foliage2.FLGRICHIESTE_ISTUTTORIA_ISTANZA_TAB
				join foliage2.flgdocumenti_istuttoria_istanza_tab using (ID_RICHIESTE_ISTUTTORIA_ISTANZA)
			where id_ista = :idIsta
			union all
			select 'Allegato Gestione' as tipo, categoria, null as desc, id_file
			from foliage2.flgfiletipo_gestione_tab a
				LEFT JOIN LATERAL (
					 values ('Delega titolarità', a.id_file_delega_titolarita), 
						 ('Autocertificazione Proprietà', a.id_file_autocertificazione_proprieta)
				 ) as t(categoria, id_file) on (true)
			where id_ista = :idIsta
				and id_file is not null
			union all
			select 'Elaborato VIncA' as tipo, null, null as desc, id_file_vinca as id_file
			from foliage2.flgista_elaborato_vinca_tab
			where id_ista = :idIsta
			union all
			select 'Bollo Invio' as tipo, null, null as desc, id_file_ricevute as id_file
			from foliage2.flgista_invio_tab
			where id_ista = :idIsta
			union all
			select 'Diritti di istruttoria' as tipo, null, null as desc, id_file_diritti_istruttoria as id_file
			from foliage2.flgista_invio_tab
			where id_ista = :idIsta
			union all
			select 'Bollo Proroga' as tipo, null, null as desc, id_file_pagamento as id_file
			from foliage2.flgista_proroga_tab
			where id_ista = :idIsta""";
		HashMap<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("idIsta", idIsta);
		return query(query, mapParam, FileIstanzaApp.RowMapper(this));
	}

	private void eliminaFotoRilevamento_TRANS(Long idRile) {
		Map<String,Object> mapParam = new HashMap<String,Object>();
		mapParam.put("idRile",idRile);	
		SqlParameterSource params = new MapSqlParameterSource(mapParam);
		
		String sql = "delete from foliage2.flgfoto_tab where id_rile=:idRile";
		
		template.update(sql,params);
		
	}

	public void inserisciRilevamenti(
		Integer idIsta,
		List<Rilevamenti> rilevamenti,
		Integer idUtente,
		String authority,
		String authScope
	) throws Exception{

		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );
		
		try {

			String delFotoSql = """
delete
from foliage2.flgfoto_tab
where id_rile in (
		select id_rile
		from foliage2.flgrile_tab r
		where r.id_ista = :idIsta
			and r.id_utente = :idUtente
			and r.tipo_auth = :authority
			and r.tipo_ambito = :authScope
			and id_rile in (
				select id
				from unnest(:arrRileId) as T(id)
			)
	)
""";
			String delRileSql = """
delete
from foliage2.flgrile_tab r
where r.id_ista = :idIsta
	and r.id_utente = :idUtente
	and r.tipo_auth = :authority
	and r.tipo_ambito = :authScope
	and id_rile in (
		select id
		from unnest(:arrRileId) as T(id)
	)
""";
			Long[] arrRileId = rilevamenti.stream().map(r->r.getIdRile()).toArray(Long[]::new);
			Array arrRile = connection.createArrayOf("numeric", arrRileId);
			Map<String,Object> delPars = new HashMap<String,Object>();
			delPars.put("idIsta", idIsta);
			delPars.put("idUtente", idUtente);
			delPars.put("authority", authority);
			delPars.put("authScope", authScope);
			delPars.put("arrRileId", arrRile);
			
			
			int nDelFoto = update(delFotoSql, delPars);
			int nDelRile = update(delRileSql, delPars);

			String insRileSql = """
insert into foliage2.flgrile_tab (
		id_rile, tipo_rilevamento,id_ista, nome, note, id_clay,
		id_utente, tipo_auth, tipo_ambito,
		shape, flag_valido, data_ins
	) values(
		:idRile, :tipoRile, :idIsta, :nome, :note, :idClay,
		:idUtente, :authority, :authScope,
		st_geomfromtext(:wktGeom) , 1, current_date
	)
""";
			String insFotoSql = """
insert into foliage2.flgfoto_tab (
		id_foto, id_rile, nome, file
	)
	values(
		NEXTVAL('foliage2.flgfoto_seq'), :idRile, :nome, :file
	)
""";
			Map<String,Object> insRilePars = new HashMap<String,Object>();
			insRilePars.put("idIsta", idIsta);
			insRilePars.put("idUtente", idUtente);
			insRilePars.put("authority", authority);
			insRilePars.put("authScope", authScope);

			Map<String,Object> insFotoPars = new HashMap<String,Object>();

			for (Rilevamenti rilevamento : rilevamenti) {
				Long idRile = rilevamento.getIdRile();
				insRilePars.put("idRile", idRile);
				insRilePars.put("tipoRile", rilevamento.getTipoRile());
				insRilePars.put("nome", rilevamento.getNome());
				insRilePars.put("note", rilevamento.getNote());
				insRilePars.put("idClay", rilevamento.getIdClay());
				insRilePars.put("wktGeom", rilevamento.getGeometry());
				int nInsRile = update(insRileSql, insRilePars);
				List<FotoRilevamento> listFoto = rilevamento.getFoto();
				if (listFoto != null) {
					insFotoPars.put("idRile", idRile);
					for (FotoRilevamento foto : listFoto) {
						insFotoPars.put("nome", foto.getNome());
						insFotoPars.put("file", foto.getFile());
						int nInsFoto = update(insFotoSql, insFotoPars);
					}
				}
			}

			// for (Rilevamenti rilevamento : rilevamenti) {
			// 	//Integer id = (idIsta == null) ? 
			// 	// if (idIsta != null) {
			// 	// 	rilevamento.setIdIsta(idIsta);
			// 	// }
			// 	Long idRile = rilevamento.getIdRile();
			// 	Map<String,Object> mapParam1 = new HashMap<String,Object>();
			// 	mapParam1.put("idIsta", idIsta);
			// 	mapParam1.put("idRile", idRile);
			// 	mapParam1.put("idUtente", idUtente);
			// 	mapParam1.put("authority", authority);
			// 	mapParam1.put("authScope", authScope);
	   
			// 	StringBuilder sb = new StringBuilder("");
			// 	sb.append(" SELECT max(id_Ista) FROM  foliage2.flgrile_tab a  " );
			// 	sb.append(" WHERE a.id_rile=:idRile and id_utente = :idUtente and tipo_auth = :authority and tipo_ambito = :authScope");
				
			// 	Integer idIstaEff  = queryForObject(
			// 		sb.toString(),
			// 		mapParam1, 
			// 		(rs, rn) -> {
			// 			Integer outVal = Integer.valueOf(rs.getInt(1));
			// 			if (rs.wasNull()) {
			// 				outVal = null;
			// 			}
			// 			return outVal;
			// 		}
			// 	);
			// 	if(idIstaEff == null) {
			// 		inserisciRilevamento_TRANS(rilevamento, idUtente, authority, authScope);
			// 	} else {
			// 		if (idIstaEff == rilevamento.getIdIsta()) {
			// 			updateRilevamento_TRANS(rilevamento, idUtente, authority, authScope);
			// 		}
			// 		else {
			// 			throw new FoliageException(String.format("Il rilevamento (%d) non appartiene all'istanza %d", idRile, idIsta));
			// 		}
			// 	}

			// }
			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
	}
	public void inserisciRilevamenti(
		List<Rilevamenti> rilevamenti,
		Integer idUtente,
		String authority,
		String authScope
	) {

		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );
	
		try {

			for (Rilevamenti rilevamento : rilevamenti) {

				Map<String,Object> mapParam1 = new HashMap<String,Object>();
				mapParam1.put("idRile", rilevamento.getIdRile());
				SqlParameterSource params1 = new MapSqlParameterSource(mapParam1);
	   
				StringBuilder sb = new StringBuilder("");
				sb.append(" SELECT count(a.id_rile) FROM  foliage2.flgrile_tab a  " );
				sb.append(" WHERE a.id_rile=:idRile and flag_valido=1 ");
	   
				Long rilExist  = template.queryForObject(sb.toString(), params1, Long.class);
	
	
				if(rilExist==0) {
					inserisciRilevamento_TRANS(rilevamento, idUtente, authority, authScope);
				} else {
					updateRilevamento_TRANS(rilevamento, idUtente, authority, authScope);
				}
			}
			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
	}
	private void inserisciRilevamento_TRANS(
		Rilevamenti rilevamento,
		Integer idUtente,
		String authority,
		String authScope
	) {
		Map<String,Object> mapParam = new HashMap<String,Object>();
		mapParam.put("idRile",rilevamento.getIdRile());
		mapParam.put("idIsta",rilevamento.getIdIsta());
		mapParam.put("tipoRile",rilevamento.getTipoRile());
		mapParam.put("nome",rilevamento.getNome());
		mapParam.put("note",rilevamento.getNote());
		mapParam.put("idClay",rilevamento.getIdClay());
		mapParam.put("geometry", rilevamento.getGeometry());
		mapParam.put("userIns",rilevamento.getUserIns());

		mapParam.put("idUtente", idUtente);
		mapParam.put("authority", authority);
		mapParam.put("authScope", authScope);
	
		String sql1 = """
insert into foliage2.flgrile_tab (
		id_rile, tipo_rilevamento,id_ista, nome, note, id_clay,
		id_utente, tipo_auth, tipo_ambito,
		shape, flag_valido, user_ins, data_ins, data_ini_vali
	) values(
		:idRile, :tipoRile,:idIsta,:nome,:note, :idClay,
		:idUtente, :authority, :authScope,
		st_geomfromtext(:geometry), 1, :userIns, current_date, current_date
	)""";
		
		update(sql1,mapParam);
		
		if(rilevamento.getFoto()!=null && !rilevamento.getFoto().isEmpty()) {
			for (FotoRilevamento curr : rilevamento.getFoto()) {
				curr.setIdRile(rilevamento.getIdRile());

				inserisciFotoRilevamento_TRANS(curr,rilevamento.getUserIns());
			}	
		}
	}
	
	private void updateRilevamento_TRANS(
		Rilevamenti rilevamento,
		Integer idUtente,
		String authority,
		String authScope
	) {
		Map<String,Object> mapParam = new HashMap<String,Object>();
		mapParam.put("idRile",rilevamento.getIdRile());
		mapParam.put("tipoRile",rilevamento.getTipoRile());
		mapParam.put("nome",rilevamento.getNome());
		mapParam.put("note",rilevamento.getNote());
		mapParam.put("idClay",rilevamento.getIdClay());
		mapParam.put("geometry", rilevamento.getGeometry());
		mapParam.put("userUpd",rilevamento.getUserIns());
		
		mapParam.put("idUtente", idUtente);
		mapParam.put("authority", authority);
		mapParam.put("authScope", authScope);
	
		//SqlParameterSource params = new MapSqlParameterSource(mapParam);
		
		String sql = """
update foliage2.flgrile_tab
set tipo_rilevamento=:tipoRile, nome=:nome, note=:note, id_clay=:idClay,
	shape= st_geometryfromtext(:geometry), user_upd=:userUpd, data_upd=current_date
where id_rile=:idRile
	and id_utente = :idUtente and tipo_auth = :authority and tipo_ambito = :authScope
				""";

		int nRows = update(sql,mapParam);
		if (nRows == 1) {
		
			eliminaFotoRilevamento_TRANS(rilevamento.getIdRile());
			
			if(rilevamento.getFoto()!=null && !rilevamento.getFoto().isEmpty()) {
				for (FotoRilevamento curr : rilevamento.getFoto()) {
					curr.setIdRile(rilevamento.getIdRile());
	
					inserisciFotoRilevamento_TRANS(curr,rilevamento.getUserIns());
				}
				
			}
		}
	}

	private void inserisciFotoRilevamento_TRANS(
		FotoRilevamento foto,
		String user
	) {
		
		Map<String,Object> mapParam = new HashMap<String,Object>();
		mapParam.put("idRile",foto.getIdRile());
		mapParam.put("nome",foto.getNome());
		mapParam.put("file",foto.getFile());
		mapParam.put("userIns",user);
	
		SqlParameterSource params = new MapSqlParameterSource(mapParam);
		
		String sql = """
insert into foliage2.flgfoto_tab (
		id_foto,id_rile, nome, file,
		flag_valido, user_ins, data_ins, data_ini_vali
	)
	values(
		NEXTVAL('foliage2.flgfoto_seq'), :idRile,:nome,:file,
		1, :userIns,current_date, current_date
	)
""";
		
		
		template.update(sql,params);
	}

	public List<Rilevamenti> getRilevamentiForIdIsta(
		Integer idIsta,
		Integer idUtente,
		String authority,
		String authScope
	) {

		Map<String,Object> mapParam1 = new HashMap<String,Object>();
		mapParam1.put("idIsta", idIsta);
		mapParam1.put("idUtente", idUtente);
		mapParam1.put("authority", authority);
		mapParam1.put("authScope", authScope);

		SqlParameterSource params1 = new MapSqlParameterSource(mapParam1);
		
		String sql = """
SELECT a.*, ST_AsGeoJSON(shape) as geom
FROM foliage2.flgrile_tab a
WHERE a.id_ista=:idIsta
	and a.id_utente = :idUtente
	and a.tipo_auth = :authority
	and a.tipo_ambito = :authScope
order by a.id_rile
""";
        
		List<Rilevamenti> res = template.query(sql, params1, Rilevamenti.RowMapper());
                
        if (res == null || res.size() == 0){
			return new ArrayList<Rilevamenti>();
		}
		else{
			for (Rilevamenti ril : res) {
				mapParam1.put("idRile", ril.getIdRile());
				params1 = new MapSqlParameterSource(mapParam1);
				String sqlFoto = """
SELECT a.*
FROM foliage2.flgfoto_tab a
WHERE a.id_rile=:idRile
""";
				List<FotoRilevamento> foto = template.query(sqlFoto, params1, FotoRilevamento.RowMapper());
				ril.setFoto(foto);
			}
			return res;    
		}
	}


	public void eliminaRilevamentoNew(
		Integer idIsta,
		Long idRile,
		Integer idUtente,
		String authority,
		String authScope
	) {

		DefaultTransactionDefinition paramTransactionDefinition = new    DefaultTransactionDefinition();
		TransactionStatus status=platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			Map<String,Object> mapParam = new HashMap<String,Object>();
			mapParam.put("idRile", idRile);
			mapParam.put("idIsta", idIsta);
			mapParam.put("idUtente", idUtente);
			mapParam.put("authority", authority);
			mapParam.put("authScope", authScope);
		
			
			//eliminaFotoRilevamento_TRANS(Long.valueOf(idRile));
			String sqlFoto = """
delete
from foliage2.flgfoto_tab
where id_rile = (
		select r.id_rile
		from foliage2.flgrile_tab r
		where r.id_rile=:idRile
			and r.id_ista = :idIsta
			and r.id_utente = :idUtente
			and r.tipo_auth = :authority
			and r.tipo_ambito = :authScope
	)
""";
			update(sqlFoto, mapParam);
			
			String sql = """
delete
from foliage2.flgrile_tab	
where id_rile=:idRile
	and id_ista = :idIsta
	and id_utente = :idUtente
	and tipo_auth = :authority
	and tipo_ambito = :authScope
""";
			
			update(sql, mapParam);
			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
	}

	public Integer getIdIstanzaForIdRile(Long idRile) {
		String sql = """
select id_ista
from foliage2.flgrile_tab
where id_rile = :idRile
""";

		Map<String,Object> mapParam = new HashMap<String,Object>();
		mapParam.put("idRile",idRile);
		return queryForObject(sql, mapParam, DbUtils.GetIntegerRowMapper("id_ista"));
	}
	public void eliminaRilevamento(
		Long idRile,
		String user,
		String codFiscale,
		Integer idUtente,
		String authority,
		String authScope
	) throws Exception
	{
		Integer idIsta = getIdIstanzaForIdRile(idRile);
		AbilitazioniIstanza abil = getAbilitazioniIdIstanza(idUtente, codFiscale, idIsta, authority, authScope);
		if (abil.compilazione || abil.consultazione) {
			this.eliminaRilevamentoNew(idIsta, idRile, idUtente, authority, authScope);
		}

		// DefaultTransactionDefinition paramTransactionDefinition = new    DefaultTransactionDefinition();
		// TransactionStatus status=platformTransactionManager.getTransaction(paramTransactionDefinition );

		// try {
		// 	Map<String,Object> mapParam = new HashMap<String,Object>();
		// 	mapParam.put("idRile",idRile);
		// 	mapParam.put("userUpd",user);
		
		// 	SqlParameterSource params = new MapSqlParameterSource(mapParam);
			
		// 	String sql = "update foliage2.flgrile_tab set flag_valido=0, "
		// 			+ " user_upd=:userUpd, data_upd=current_date, data_fine_vali=current_date where id_rile=:idRile";
			
		// 	template.update(sql,params);
			
		// 	eliminaFotoRilevamento_TRANS(idRile);
		// 	platformTransactionManager.commit(status);
		// }
		// catch (Exception e) {
		// 	platformTransactionManager.rollback(status);
		// 	throw e;
		// }
	}

	public List<CatalogoLayer> getTipolRilev() {
		String sql="select * from foliage2.flgclay_tab where flag_valido=1";
		
		return template.query(sql, CatalogoLayer.RowMapper());
	}

	public Rilevamenti getDettRilevamento(
		Long idRilev,
		Integer idUtente,
		String authority,
		String authScope
	) {
		Map<String,Object> mapParam1 = new HashMap<String,Object>();
		mapParam1.put("idRile", idRilev);
		mapParam1.put("idUtente", idUtente);
		mapParam1.put("authority", authority);
		mapParam1.put("authScope", authScope);
		//SqlParameterSource params1 = new MapSqlParameterSource(mapParam1);
		
		StringBuilder sql = new StringBuilder("");
		sql.append(" SELECT a.*, ST_AsGeoJSON(shape) as geom FROM  foliage2.flgrile_tab a  " );
		sql.append(" WHERE a.id_rile=:idRile  and id_utente = :idUtente and tipo_auth = :authority and tipo_ambito = :authScope");
        
		Rilevamenti ril = queryForObject(sql.toString(), mapParam1, Rilevamenti.RowMapper());
		if (ril != null) {
			sql = new StringBuilder("");
			sql.append(" SELECT a.* FROM  foliage2.flgfoto_tab a  " );
			sql.append(" WHERE a.id_rile=:idRile");
			List<FotoRilevamento> foto = query(sql.toString(), mapParam1, FotoRilevamento.RowMapper());
			ril.setFoto(foto);	
		}
				
			
		return ril;    
	}


	public Rilevamenti getDettRilevamento(
		Integer idIsta,
		Integer idRile,
		Integer idUtente,
		String authority,
		String authScope
	) throws Exception {
		Map<String,Object> mapParam1 = new HashMap<String,Object>();
		mapParam1.put("idRile", idRile);
		mapParam1.put("idIsta", idIsta);
		
		StringBuilder sql = new StringBuilder("");
		sql.append(" SELECT a.*, ST_AsGeoJSON(shape) as geom FROM  foliage2.flgrile_tab a  " );
		sql.append(" WHERE a.id_rile=:idRile and id_ista = :idIsta");
        
		Rilevamenti ril = template.queryForObject(sql.toString(), mapParam1, Rilevamenti.RowMapper());

		if (ril == null) {
			throw new FoliageException(String.format("Il rilevamento %d non è stato trovato nell'istanza %d", idRile, idIsta));
		}
		mapParam1.remove("idIsta");
 		sql = new StringBuilder("");
		sql.append(" SELECT a.* FROM  foliage2.flgfoto_tab a  " );
		sql.append(" WHERE a.id_rile=:idRile");
		List<FotoRilevamento> foto = template.query(sql.toString(), mapParam1, FotoRilevamento.RowMapper());
		ril.setFoto(foto);	
				
			
		return ril;    
	}

	public void updateRilevamento(
		Rilevamenti rilevamento,
		Integer idUtente,
		String authority,
		String authScope
	) {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status=platformTransactionManager.getTransaction(paramTransactionDefinition );
		
		try {
			updateRilevamento_TRANS(rilevamento, idUtente, authority, authScope);
			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
	}
}
