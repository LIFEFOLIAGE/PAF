package it.almaviva.foliage.services;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import it.almaviva.foliage.FoliageAuthorizationException;
import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.bean.ChiaviRicercaIstanza;
import it.almaviva.foliage.bean.CreazioneIstanza;
import it.almaviva.foliage.bean.DatiInvioIstanza;
import it.almaviva.foliage.bean.DatiIstruttoria;
import it.almaviva.foliage.bean.DettagliIstruttoria;
import it.almaviva.foliage.bean.ElaborazioneGovernance;
import it.almaviva.foliage.bean.ElaborazioneMonitoraggio;
import lombok.extern.slf4j.Slf4j;


import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.JsonSerializable.Base;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import it.almaviva.foliage.bean.AbilitazioniIstanza;
import it.almaviva.foliage.bean.AutocertificazioneProfessionista;
import it.almaviva.foliage.bean.Base64FormioFile;
import it.almaviva.foliage.bean.DatiRichiestaResponsabile;
import it.almaviva.foliage.bean.DatiTitolare;
import it.almaviva.foliage.bean.DatiUtente;
import it.almaviva.foliage.bean.ElementoValutazioneIstanza;
import it.almaviva.foliage.bean.EntitaGeometrica;
import it.almaviva.foliage.bean.FileIstanzaApp;
import it.almaviva.foliage.bean.FileIstanzaWeb;
import it.almaviva.foliage.bean.FileValutazioneIstanza;
import it.almaviva.foliage.bean.FoliageReport;
import it.almaviva.foliage.bean.Foto;
import it.almaviva.foliage.bean.ParticellaCatastaleModulo;
import it.almaviva.foliage.bean.ProrogaIstanza;
import it.almaviva.foliage.bean.RichiestaProfilo;
import it.almaviva.foliage.bean.Rilevamento;
import it.almaviva.foliage.bean.RisultatoRicercaIstanza;
import it.almaviva.foliage.bean.UnitaOmogeneaModulo;
import it.almaviva.foliage.bean.ValutazioneIstanza;
import it.almaviva.foliage.bean.ValutazioneRichiestaProfilo;
import it.almaviva.foliage.controllers.WebController;
import it.almaviva.foliage.document.ModuloIstanza;
import it.almaviva.foliage.document.ModuloIstruttoria;
import it.almaviva.foliage.function.BiProcedure;
import it.almaviva.foliage.function.Function;
import it.almaviva.foliage.function.JsonIO;
import it.almaviva.foliage.function.Procedure;
import it.almaviva.foliage.istanze.CaricatoreIstanza;
import it.almaviva.foliage.istanze.SchedaIstanza;
import it.almaviva.foliage.istanze.db.CampoSelect;
import it.almaviva.foliage.istanze.db.CondizioneEq;
import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.istanze.db.RecuperoDb;
import it.almaviva.foliage.istanze.db.ReportBuiler;
import it.almaviva.foliage.legacy.bean.RicercaUtenti;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.ws.rs.core.MediaType;

import org.threeten.extra.PeriodDuration;


@Slf4j
@Component
public class WebDal extends AbstractDal {
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
	
	public WebDal(
		JdbcTemplate jdbcTemplate,
		TransactionTemplate transactionTemplate,
		PlatformTransactionManager platformTransactionManager,
		String name
	) throws Exception {
		super(jdbcTemplate, transactionTemplate, platformTransactionManager, name);
	}

	@Autowired
	public WebDal(
		JdbcTemplate jdbcTemplate,
		TransactionTemplate transactionTemplate,
		PlatformTransactionManager platformTransactionManager
	) throws Exception {
		this(jdbcTemplate, transactionTemplate, platformTransactionManager, "WebDal");
		//this.codRegione = codRegione;
	}

	
	

	public boolean verificaRuoloInEnte(Integer idUtente, String authority, String authScope, Integer idEnte) {
		if ("AMMI".equals(authority)) {
			return true;
		}
		else {
			HashMap<String, Object> mapParam = new HashMap<String, Object>();
			mapParam.put("idUtente", idUtente);
			mapParam.put("authority", authority);
			mapParam.put("idEnte", idEnte);
			mapParam.put("authScope", authScope);
			String sql = """
select count(*)
from foliage2.flgprof_tab p 
	join foliage2.flgenti_profilo_tab ep1 on (ep1.id_profilo = p.id_profilo)
where p.tipo_auth = :authority
	and p.tipo_ambito = :authScope
	and ep1.id_utente = :idUtente
	and ep1.id_ente = :idEnte""";
			int val = template.queryForObject(
				sql,
				mapParam,
				(rs, rn) -> {
					return rs.getInt(1);
				}
			);
			return val != 0;
		}
	}

	public boolean verificaAccessoAUtente(
		Integer idUtente,
		String authority,
		String authScope,
		String username
	) {
		boolean isOk = false;

		switch (authority) {
			case "DIRI": 
			case "RESP": {
				HashMap<String, Object> mapParam = new HashMap<String, Object>();
				mapParam.put("idUtente", idUtente);
				mapParam.put("authority", authority);
				mapParam.put("username", username);
				mapParam.put("authScope", authScope);
				String sql = """
select count(*)
from foliage2.flgprof_tab p 
	join foliage2.flgenti_profilo_tab ep1 on (ep1.id_profilo = p.id_profilo)
	join foliage2.flgenti_profilo_tab ep2 on (ep2.id_ente = ep1.id_ente)
	join foliage2.flgprof_tab p2 on (ep2.id_profilo = p2.id_profilo)
	join foliage2.flguten_tab u on (u.id_uten = ep2.id_utente)
where p.tipo_auth = :authority
	and p.tipo_ambito = :authScope
	and ep1.id_utente = :idUtente
	and (:authority = 'RESP' or p2.tipo_auth = 'ISTR')
	and u.user_name = :username""";
	
				int val = queryForObject(
					sql,
					mapParam,
					(rs, rn) -> {
						return rs.getInt(1);
					}
				);
				isOk = val != 0;
			}; break;
			case "AMMI": {
				isOk = true;
			}; break;
		}
		return isOk;
	}

	public ResultSet getRegioni() throws SQLException, Exception {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select codi_regi as \"CodiRegi\", desc_regi as \"DescRegi\"
from foliage2.flgregi_tab""");
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet getProvincie(String codRegi) throws SQLException, Exception {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select id_prov as \"IdProv\", desc_prov as \"DescProv\", codi_prov as \"CodiProv\", codi_regi as \"CodiRegi\"
from foliage2.flgprov_tab
where codi_regi= ?
	and flag_valido=1"""
				);
				statement.setString(1, codRegi);
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet getUtente(String username) throws SQLException, Exception {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
SELECT a.*
FROM foliage2.flguten_tab a
WHERE a.user_name= ?""");
				statement.setString(1, username);
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet ricercaInstanzeProprietario(
		Integer idUtente, 
		String codFiscaleUtente,
		ChiaviRicercaIstanza parametri
	) throws SQLException {
		String testo = parametri.getTesto();
		Integer statoIstanza = parametri.getStatoIstanza();
		Integer tipoIstanza = parametri.getTipoIstanza();
		Integer idEnte = parametri.getIdEnte();

		//TODO: lo stato lavori va capito con quali colonne andrebbe gestito
		Integer statoLavori = parametri.getStatoAvanzamento();

		LinkedList<String> conditions = new LinkedList<String>();
		if (tipoIstanza != null) {
			String cond = """
				and c.id_cist = ?
			""";
			conditions.add(cond);
		}
		if (statoIstanza != null) {
			String cond = """
				and i.stato = ?
			""";
			conditions.add(cond);
		}
		if (idEnte != null) {
			String cond = """
				and i.id_ente_terr = ?
			""";
			conditions.add(cond);
		}
		if (testo != null && !testo.equals("")) {
			String cond = """
				and ? in (i.nome_ista, i.codi_ista)
			""";
			conditions.add(cond);
		}

		String sqlCommand = String.format("""
select i.id_ista, codi_ista, id_tipo_istanza, c.desc_cist, nome_ista, 
	s.codice_fiscale, s.cognome, s.nome, 
	i.data_istanza, u.codi_fisc as codi_fisc_ute,
	i.id_utente_compilazione,
	i.id_ente_terr, e.tipo_ente, e.nome_ente
from foliage2.FLGTITOLARE_ISTANZA_TAB s
	join foliage2.flgista_tab i using (id_titolare)
	join foliage2.flgtipo_istanza_tab t using (id_tipo_istanza)
	join foliage2.flgcist_tab c on (c.id_cist = t.id_cist)
	join foliage2.flguten_tab u on (u.id_uten = i.id_utente_compilazione)
	left join foliage2.flgente_root_tab e on (e.id_ente = i.id_ente_terr)
where s.CODICE_FISCALE = ?%s
order by i.id_ista desc
			""",
			String.join("", conditions)
		);

		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement(
					sqlCommand
				);
				int parIdx = 1;
				statement.setString(parIdx++, codFiscaleUtente);
				if (tipoIstanza != null) {
					statement.setInt(parIdx++, tipoIstanza);
				}
				if (statoIstanza != null) {
					statement.setInt(parIdx++, statoIstanza);
				}
				if (idEnte != null) {
					statement.setInt(parIdx++, idEnte);
				}
				if (testo != null && !testo.equals("")) {
					statement.setString(parIdx++, testo);
				}
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet ricercaInstanzeProfessionista(
		Integer idUtente, 
		String codFiscaleUtente,
		ChiaviRicercaIstanza parametri
	) throws SQLException {
		String testo = parametri.getTesto();
		Integer statoIstanza = parametri.getStatoIstanza();
		Integer tipoIstanza = parametri.getTipoIstanza();
		Integer idEnte = parametri.getIdEnte();
		String codFiscaleTitolare = parametri.getCodFiscaleTitolare();

		//TODO: lo stato lavori va capito su quale colonna andrebbe
		Integer statoLavori = parametri.getStatoAvanzamento();

		LinkedList<String> conditions = new LinkedList<String>();
		if (tipoIstanza != null) {
			String cond = """
				and c.id_cist = ?
			""";
			conditions.add(cond);
		}
		if (statoIstanza != null) {
			String cond = """
				and i.stato = ?
			""";
			conditions.add(cond);
		}
		if (idEnte != null) {
			String cond = """
				and i.id_ente_terr = ?
			""";
			conditions.add(cond);
		}
		if (testo != null && !testo.equals("")) {
			String cond = """
				and ? in (i.nome_ista, i.codi_ista)
			""";
			conditions.add(cond);
		}
		if (codFiscaleTitolare != null && !codFiscaleTitolare.equals("")) {
			String cond = """
				and s.codice_fiscale = ?
			""";
			conditions.add(cond);
		}

		String sqlCommand = String.format("""
select i.id_ista, codi_ista, id_tipo_istanza, desc_cist, nome_ista, 
	s.codice_fiscale, s.cognome, s.nome, 
	i.data_istanza, u.codi_fisc as codi_fisc_ute,
	i.id_utente_compilazione,
	i.id_ente_terr, e.tipo_ente, e.nome_ente
from foliage2.flgista_tab i
	join foliage2.flgtitolare_istanza_tab s using (id_titolare)
	join foliage2.flgtipo_istanza_tab as t using (id_tipo_istanza)
	join foliage2.flgcist_tab c on (c.id_cist = t.id_cist)
	join foliage2.flguten_tab u on (u.id_uten = i.id_utente_compilazione)
	left join foliage2.flgente_root_tab e on (e.id_ente = i.id_ente_terr)
where i.id_utente_compilazione = ?%s
order by i.id_ista desc
			""",
			String.join("", conditions)
		);

		log.debug(sqlCommand);
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement(
					sqlCommand
				);
				int parIdx = 1;
				statement.setInt(parIdx++, idUtente);
				if (tipoIstanza != null) {
					statement.setInt(parIdx++, tipoIstanza);
				}
				if (statoIstanza != null) {
					statement.setInt(parIdx++, statoIstanza);
				}
				if (idEnte != null) {
					statement.setInt(parIdx++, idEnte);
				}
				if (testo != null && !testo.equals("")) {
					statement.setString(parIdx++, testo);
				}
				if (codFiscaleTitolare != null && !codFiscaleTitolare.equals("")) {
					statement.setString(parIdx++, codFiscaleTitolare);
				}
				return statement.executeQuery();
			}
		);
		return result;
	}
	public ResultSet ricercaInstanzeAmministratore(
		Integer idUtente,
		String codFiscaleUtente,
		ChiaviRicercaIstanza parametri
	) throws SQLException {
		String testo = parametri.getTesto();
		Integer statoIstanza = parametri.getStatoIstanza();
		Integer tipoIstanza = parametri.getTipoIstanza();
		Integer idEnte = parametri.getIdEnte();
		String codFiscaleTitolare = parametri.getCodFiscaleTitolare();

		//TODO: lo stato lavori va capito su quale colonna andrebbe
		Integer statoLavori = parametri.getStatoAvanzamento();

		LinkedList<String> conditions = new LinkedList<String>();
		if (tipoIstanza != null) {
			String cond = """
				c.id_cist = ?
			""";
			conditions.add(cond);
		}
		if (statoIstanza != null) {
			String cond = """
				i.stato = ?
			""";
			conditions.add(cond);
		}
		if (idEnte != null) {
			String cond = """
				i.id_ente_terr = ?
			""";
			conditions.add(cond);
		}
		if (testo != null && !testo.equals("")) {
			String cond = """
				? in (i.nome_ista, i.codi_ista)
			""";
			conditions.add(cond);
		}
		if (codFiscaleTitolare != null && !codFiscaleTitolare.equals("")) {
			String cond = """
				s.codice_fiscale = ?
			""";
			conditions.add(cond);
		}

		boolean hasConditions = conditions.size() > 0;
		String sqlCommand = String.format("""
select i.id_ista, codi_ista, id_tipo_istanza, desc_cist, nome_ista, 
	s.codice_fiscale, s.cognome, s.nome, 
	i.data_istanza, u.codi_fisc as codi_fisc_ute,
	i.id_utente_compilazione,
	i.id_ente_terr, e.tipo_ente, e.nome_ente
from foliage2.flgista_tab i
	join foliage2.flgtitolare_istanza_tab s using (id_titolare)
	join foliage2.flgtipo_istanza_tab as t using (id_tipo_istanza)
	join foliage2.flgcist_tab c on (c.id_cist = t.id_cist)
	join foliage2.flguten_tab u on (u.id_uten = i.id_utente_compilazione)
	left join foliage2.flgente_root_tab e on (e.id_ente = i.id_ente_terr)%s
order by i.id_ista desc
			""",
			(
				hasConditions ?
					String.format("\nwhere %s", String.join(
			"""
	and """,
			conditions))
					: ""
			)
		);

		log.debug(sqlCommand);
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement(
					sqlCommand
				);
				int parIdx = 1;
				if (tipoIstanza != null) {
					statement.setInt(parIdx++, tipoIstanza);
				}
				if (statoIstanza != null) {
					statement.setInt(parIdx++, statoIstanza);
				}
				if (idEnte != null) {
					statement.setInt(parIdx++, idEnte);
				}
				if (testo != null && !testo.equals("")) {
					statement.setString(parIdx++, testo);
				}
				if (codFiscaleTitolare != null && !codFiscaleTitolare.equals("")) {
					statement.setString(parIdx++, codFiscaleTitolare);
				}
				return statement.executeQuery();
			}
		);
		return result;
	}
	// public ResultSet ricercaInstanzeDirigente(
	// 	Integer idUtente,
	// 	String codFiscaleUtente,
	// 	ChiaviRicercaIstanza parametri
	// ) throws SQLException {
	// 	return null;
	// }
	// public ResultSet ricercaInstanzeIstruttore(
	// 	Integer idUtente,
	// 	String codFiscaleUtente,
	// 	ChiaviRicercaIstanza parametri
	// ) throws SQLException {
	// 	return null;
	// }

	public <T> List<T> ricercaInstanze(
		String tipoProfilo, String authScope,
		Integer idUtente,
		String codFiscaleUtente,
		ChiaviRicercaIstanza parametri,
		RowMapper<T> rowMapper
	) throws SQLException, FoliageException, Exception {
		List<T> result = null;
		HashMap<String, Object> parameters = new HashMap<>();
		LinkedList<String> conditions = new LinkedList<String>();

		String sqlBodyPattern = """
select i.id_ista, i.codi_ista, i.nome_ista, i.note, i.data_istanza,
	tit.id_titolare, tit.codice_fiscale, tit.cognome, tit.nome,
	t.id_cist, t.id_tipo_istanza, t.cod_tipo_istanza_specifico, t.nome_istanza_specifico,
	c.desc_cist,
	e.id_ente, e.tipo_ente, e.nome_ente, 
	s.id_stato, s.cod_stato, s.desc_stato,
	uc.id_uten as id_ute_gestore, uc.user_name as username_gestore, uc.codi_fisc as cod_fisc_gestore, uc.cognome as cognome_gestore, uc.nome as nome_gestore,
	ai.data_assegnazione,
	ui.id_uten as id_ute_istruttore, ui.user_name as username_istruttore, ui.codi_fisc as cod_fisc_istruttore, ui.cognome as cognome_istruttore, ui.nome as nome_istruttore,
	ua.id_uten as id_ute_dirigente, ua.user_name as username_dirigente, ua.codi_fisc as cod_fisc_dirigente, ua.cognome as cognome_dirigente, ua.nome as nome_dirigente,
	v.data_valutazione, v.note_valutazione,
	di.data_inizio_lavori, di.data_comunicazione_inizio_lavori,
	df.data_fine_lavori, df.data_comunicazione_fine_lavori
from foliage2.flgista_tab i
	join foliage2.flgtitolare_istanza_tab tit on (tit.id_titolare = i.id_titolare)
	join foliage2.flgtipo_istanza_tab t on (t.id_tipo_istanza = i.id_tipo_istanza)
	join foliage2.flgcist_tab c on (c.id_cist = t.id_cist)
	join foliage2.flgente_root_tab e on (e.id_ente = i.id_ente_terr)
	join foliage2.flgstato_istanza_tab s on (s.id_stato = i.stato)
	left join foliage2.flguten_tab uc on (uc.id_uten = i.id_utente_compilazione)
	left join foliage2.flgassegnazione_istanza_tab ai on (ai.id_ista = i.id_ista)
	left join foliage2.flguten_tab ui on (ui.id_uten = ai.id_utente_istruttore)
	left join foliage2.flguten_tab ua on (ua.id_uten = ai.id_utente_assegnazione)
	left join foliage2.flgvalutazione_istanza_tab v on (v.id_ista = i.id_ista)
	left join foliage2.flgdate_inizio_lavori_istanza_tab di on (di.id_ista = i.id_ista)
	left join foliage2.flgdate_fine_lavori_istanza_tab df on (df.id_ista = di.id_ista)%s
order by i.id_ista desc
			""";


		Integer tipoIstanza = parametri.getTipoIstanza();
		Integer statoIstanza = parametri.getStatoIstanza();
		Integer statoAvanzamento = parametri.getStatoAvanzamento();
		String testo = parametri.getTesto();
		Integer idEnte = parametri.getIdEnte();
		String codFiscaleTitolare = parametri.getCodFiscaleTitolare();
		LocalDate validitaDa = parametri.getValiditaDa();
		LocalDate validitaA = parametri.getValiditaA();
		
		LocalDate approvazioneDa = parametri.getApprovazioneDa();
		LocalDate approvazioneA = parametri.getApprovazioneA();

		String codFiscaleIstruttore = parametri.getCodFiscaleIstruttore();
		String usernameIstruttore = parametri.getUsernameIstruttore();
		
		if (tipoIstanza != null) {
			parameters.put("tipoIstanza", tipoIstanza);
			conditions.addLast("t.id_cist = :tipoIstanza");
		}
		
		if (statoIstanza != null) {
			parameters.put("statoIstanza", statoIstanza);
			conditions.addLast("s.id_stato = :statoIstanza");
		}
		
		if (approvazioneDa != null) {
			parameters.put("approvazioneDa", approvazioneDa);
			conditions.addLast("v.data_valutazione >= :approvazioneDa");
		}
		if (approvazioneA != null) {
			parameters.put("approvazioneA", approvazioneA.plusDays(1));
			conditions.addLast("v.data_valutazione < :approvazioneA");
		}
		
		if (validitaDa != null) {
			parameters.put("validitaDa", validitaDa);
			conditions.addLast("v.data_fine_validita >= :validitaDa");
		}
		if (validitaA != null) {
			parameters.put("validitaA", validitaA.plusDays(1));
			conditions.addLast("v.data_fine_validita < :validitaA");
		}

		if (statoAvanzamento != null) {
			switch (statoAvanzamento) {
				case 0: { // Non pianificati
					conditions.addLast("di.data_inizio_lavori is null");
				}; break;
				case 1: { // Pianificati
					conditions.addLast("di.data_inizio_lavori < current_date");
				}; break;
				case 2: { // Iniziati
					conditions.addLast("di.data_inizio_lavori >= current_date and df.data_fine_lavori is null");
				}; break;
				case 3: { // Con scadenza
					conditions.addLast("df.data_fine_lavori <= current_date");
				}; break;
				case 4: { // Terminati
					conditions.addLast("df.data_fine_lavori > current_date");
				}; break;
			}
		}

		if (testo != null && !testo.equals("")){
			parameters.put("testo", testo);
			conditions.addLast(":testo in (i.codi_ista, i.nome_ista)");
		}

		if (idEnte != null) {
			parameters.put("idEnte", idEnte);
			conditions.addLast("e.id_ente = :idEnte");
		}

		if (codFiscaleTitolare != null && !codFiscaleTitolare.equals("")) {
			parameters.put("codFiscaleTitolare", codFiscaleTitolare);
			conditions.addLast("tit.codice_fiscale = :codFiscaleTitolare");
		}

		if (codFiscaleIstruttore != null && !codFiscaleIstruttore.equals("")) {
			parameters.put("codFiscaleIstruttore", codFiscaleIstruttore);
			conditions.addLast("ui.codi_fisc = :codFiscaleIstruttore");
		}

		if (usernameIstruttore != null && !usernameIstruttore.equals("")) {
			parameters.put("usernameIstruttore", usernameIstruttore);
			conditions.addLast("ui.user_name = :usernameIstruttore");
		}

		Procedure addTerritorialita = () -> {
			parameters.put("idUtente", idUtente);
			parameters.put("tipoProfilo", tipoProfilo);
			parameters.put("authScope", authScope);
			conditions.addLast("""
exists (
		select *
		from foliage2.flgprof_tab p 
			join foliage2.flgprofili_utente_tab pu on (pu.id_profilo = p.id_profilo)
			join foliage2.flgenti_profilo_tab ep on (
				ep.id_utente = pu.id_utente and
				ep.id_profilo = pu.id_profilo 
			)
		where pu.id_utente = :idUtente
			and p.tipo_auth = :tipoProfilo
			and p.tipo_ambito = :authScope
			and ep.id_ente = e.id_ente
	)
"""
			);
		};


		switch (tipoProfilo) {
			case "PROP": {

				parameters.put("codFiscaleUtente", codFiscaleUtente);
				conditions.addLast("tit.codice_fiscale = :codFiscaleUtente");
				
			}; break;
			case "PROF": {
				parameters.put("codFiscaleUtente", codFiscaleUtente);
				parameters.put("idUtente", idUtente);

				conditions.addLast("uc.codi_fisc = :codFiscaleUtente");
				conditions.addLast("uc.id_uten = :idUtente");
			}; break;
			case "ISTR": {
				parameters.put("codFiscaleUtente", codFiscaleUtente);
				parameters.put("idUtente", idUtente);

				conditions.addLast("ui.codi_fisc = :codFiscaleUtente");
				conditions.addLast("ui.id_uten = :idUtente");
				conditions.addLast("s.cod_stato != 'COMPILAZIONE'");

				addTerritorialita.eval();
			}; break;
			case "DIRI":
			case "RESP": {
				conditions.addLast("s.cod_stato != 'COMPILAZIONE'");
				addTerritorialita.eval();
			}; break;
			case "AMMI":
			case "SORV": {
				conditions.addLast("s.cod_stato != 'COMPILAZIONE'");
			}; break;
			default: {
				throw new FoliageException("Profilo non gestito");
			}
		}
		String whereCondition = "";
		if (conditions.size() > 0) {
			whereCondition = String.format("""

			where %s""",
				conditions.stream().map(s -> s.replaceAll("\\n", "\n\t")).collect(Collectors.joining("\n\tand "))
			);
		}
		String sqlCommand = String.format(sqlBodyPattern, whereCondition);
		//log.debug(sqlCommand);
		result = query(sqlCommand, parameters,
			rowMapper
		);

		return result;
	}
	public List<RisultatoRicercaIstanza> ricercaInstanze(
		String tipoProfilo, String authScope,
		Integer idUtente,
		String codFiscaleUtente,
		ChiaviRicercaIstanza parametri
	) throws SQLException, FoliageException, Exception {
		return ricercaInstanze(
			tipoProfilo, authScope, 
			idUtente, codFiscaleUtente,
			parametri,
			RisultatoRicercaIstanza.RowMapper(tipoProfilo, authScope)
		);

// 		List<RisultatoRicercaIstanza> result = null;

// 		HashMap<String, Object> parameters = new HashMap<>();
// 		LinkedList<String> conditions = new LinkedList<String>();

// 		String sqlBodyPattern = """
// select i.id_ista, i.codi_ista, i.nome_ista, i.note, i.data_istanza,
// 	tit.id_titolare, tit.codice_fiscale, tit.cognome, tit.nome,
// 	t.id_cist, t.id_tipo_istanza, t.cod_tipo_istanza_specifico, t.nome_istanza_specifico,
// 	c.desc_cist,
// 	e.id_ente, e.tipo_ente, e.nome_ente, 
// 	s.id_stato, s.cod_stato, s.desc_stato,
// 	uc.id_uten as id_ute_gestore, uc.user_name as username_gestore, uc.codi_fisc as cod_fisc_gestore, uc.cognome as cognome_gestore, uc.nome as nome_gestore,
// 	ai.data_assegnazione,
// 	ui.id_uten as id_ute_istruttore, ui.user_name as username_istruttore, ui.codi_fisc as cod_fisc_istruttore, ui.cognome as cognome_istruttore, ui.nome as nome_istruttore,
// 	ua.id_uten as id_ute_dirigente, ua.user_name as username_dirigente, ua.codi_fisc as cod_fisc_dirigente, ua.cognome as cognome_dirigente, ua.nome as nome_dirigente,
// 	v.data_valutazione, v.note_valutazione,
// 	di.data_inizio_lavori, di.data_comunicazione_inizio_lavori,
// 	df.data_fine_lavori, df.data_comunicazione_fine_lavori
// from foliage2.flgista_tab i
// 	join foliage2.flgtitolare_istanza_tab tit on (tit.id_titolare = i.id_titolare)
// 	join foliage2.flgtipo_istanza_tab t on (t.id_tipo_istanza = i.id_tipo_istanza)
// 	join foliage2.flgcist_tab c on (c.id_cist = t.id_cist)
// 	join foliage2.flgente_root_tab e on (e.id_ente = i.id_ente_terr)
// 	join foliage2.flgstato_istanza_tab s on (s.id_stato = i.stato)
// 	left join foliage2.flguten_tab uc on (uc.id_uten = i.id_utente_compilazione)
// 	left join foliage2.flgassegnazione_istanza_tab ai on (ai.id_ista = i.id_ista)
// 	left join foliage2.flguten_tab ui on (ui.id_uten = ai.id_utente_istruttore)
// 	left join foliage2.flguten_tab ua on (ua.id_uten = ai.id_utente_assegnazione)
// 	left join foliage2.flgvalutazione_istanza_tab v on (v.id_ista = i.id_ista)
// 	left join foliage2.flgdate_inizio_lavori_istanza_tab di on (di.id_ista = i.id_ista)
// 	left join foliage2.flgdate_fine_lavori_istanza_tab df on (df.id_ista = di.id_ista)%s
// order by i.id_ista desc
// 			""";


// 		Integer tipoIstanza = parametri.getTipoIstanza();
// 		Integer statoIstanza = parametri.getStatoIstanza();
// 		Integer statoAvanzamento = parametri.getStatoAvanzamento();
// 		String testo = parametri.getTesto();
// 		Integer idEnte = parametri.getIdEnte();
// 		String codFiscaleTitolare = parametri.getCodFiscaleTitolare();
// 		LocalDate validitaDa = parametri.getValiditaDa();
// 		LocalDate validitaA = parametri.getValiditaA();
		
// 		LocalDate approvazioneDa = parametri.getApprovazioneDa();
// 		LocalDate approvazioneA = parametri.getApprovazioneA();

// 		String codFiscaleIstruttore = parametri.getCodFiscaleIstruttore();
// 		String usernameIstruttore = parametri.getUsernameIstruttore();
		
// 		if (tipoIstanza != null) {
// 			parameters.put("tipoIstanza", tipoIstanza);
// 			conditions.addLast("t.id_cist = :tipoIstanza");
// 		}
		
// 		if (statoIstanza != null) {
// 			parameters.put("statoIstanza", statoIstanza);
// 			conditions.addLast("s.id_stato = :statoIstanza");
// 		}
		
// 		if (approvazioneDa != null) {
// 			parameters.put("approvazioneDa", approvazioneDa);
// 			conditions.addLast("v.data_valutazione >= :approvazioneDa");
// 		}
// 		if (approvazioneA != null) {
// 			parameters.put("approvazioneA", approvazioneA.plusDays(1));
// 			conditions.addLast("v.data_valutazione < :approvazioneA");
// 		}
		
// 		if (validitaDa != null) {
// 			parameters.put("validitaDa", validitaDa);
// 			conditions.addLast("v.data_fine_validita >= :validitaDa");
// 		}
// 		if (validitaA != null) {
// 			parameters.put("validitaA", validitaA.plusDays(1));
// 			conditions.addLast("v.data_fine_validita < :validitaA");
// 		}

// 		if (statoAvanzamento != null) {
// 			switch (statoAvanzamento) {
// 				case 0: { // Non pianificati
// 					conditions.addLast("di.data_inizio_lavori is null");
// 				}; break;
// 				case 1: { // Pianificati
// 					conditions.addLast("di.data_inizio_lavori < current_date");
// 				}; break;
// 				case 2: { // Iniziati
// 					conditions.addLast("di.data_inizio_lavori >= current_date and df.data_fine_lavori is null");
// 				}; break;
// 				case 3: { // Con scadenza
// 					conditions.addLast("df.data_fine_lavori <= current_date");
// 				}; break;
// 				case 4: { // Terminati
// 					conditions.addLast("df.data_fine_lavori > current_date");
// 				}; break;
// 			}
// 		}

// 		if (testo != null && !testo.equals("")){
// 			parameters.put("testo", testo);
// 			conditions.addLast(":testo in (i.codi_ista, i.nome_ista)");
// 		}

// 		if (idEnte != null) {
// 			parameters.put("idEnte", idEnte);
// 			conditions.addLast("e.id_ente = :idEnte");
// 		}

// 		if (codFiscaleTitolare != null && !codFiscaleTitolare.equals("")) {
// 			parameters.put("codFiscaleTitolare", codFiscaleTitolare);
// 			conditions.addLast("tit.codice_fiscale = :codFiscaleTitolare");
// 		}

// 		if (codFiscaleIstruttore != null && !codFiscaleIstruttore.equals("")) {
// 			parameters.put("codFiscaleIstruttore", codFiscaleIstruttore);
// 			conditions.addLast("ui.codi_fisc = :codFiscaleIstruttore");
// 		}

// 		if (usernameIstruttore != null && !usernameIstruttore.equals("")) {
// 			parameters.put("usernameIstruttore", usernameIstruttore);
// 			conditions.addLast("ui.user_name = :usernameIstruttore");
// 		}

// 		Procedure addTerritorialita = () -> {
// 			parameters.put("idUtente", idUtente);
// 			parameters.put("tipoProfilo", tipoProfilo);
// 			parameters.put("authScope", authScope);
// 			conditions.addLast("""
// exists (
// 		select *
// 		from foliage2.flgprof_tab p 
// 			join foliage2.flgprofili_utente_tab pu on (pu.id_profilo = p.id_profilo)
// 			join foliage2.flgenti_profilo_tab ep on (
// 				ep.id_utente = pu.id_utente and
// 				ep.id_profilo = pu.id_profilo 
// 			)
// 		where pu.id_utente = :idUtente
// 			and p.tipo_auth = :tipoProfilo
// 			and p.tipo_ambito = :authScope
// 			and ep.id_ente = e.id_ente
// 	)
// """
// 			);
// 		};


// 		switch (tipoProfilo) {
// 			case "PROP": {

// 				parameters.put("codFiscaleUtente", codFiscaleUtente);
// 				conditions.addLast("tit.codice_fiscale = :codFiscaleUtente");
				
// 			}; break;
// 			case "PROF": {
// 				parameters.put("codFiscaleUtente", codFiscaleUtente);
// 				parameters.put("idUtente", idUtente);

// 				conditions.addLast("uc.codi_fisc = :codFiscaleUtente");
// 				conditions.addLast("uc.id_uten = :idUtente");
// 			}; break;
// 			case "ISTR": {
// 				parameters.put("codFiscaleUtente", codFiscaleUtente);
// 				parameters.put("idUtente", idUtente);

// 				conditions.addLast("ui.codi_fisc = :codFiscaleUtente");
// 				conditions.addLast("ui.id_uten = :idUtente");
// 				conditions.addLast("s.cod_stato != 'COMPILAZIONE'");

// 				addTerritorialita.eval();
// 			}; break;
// 			case "DIRI":
// 			case "RESP": {
// 				conditions.addLast("s.cod_stato != 'COMPILAZIONE'");
// 				addTerritorialita.eval();
// 			}; break;
// 			case "AMMI":
// 			case "SORV": {
// 				conditions.addLast("s.cod_stato != 'COMPILAZIONE'");
// 			}; break;
// 			default: {
// 				throw new FoliageException("Profilo non gestito");
// 			}
// 		}
// 		String whereCondition = "";
// 		if (conditions.size() > 0) {
// 			whereCondition = String.format("""

// 			where %s""",
// 				conditions.stream().map(s -> s.replaceAll("\\n", "\n\t")).collect(Collectors.joining("\n\tand "))
// 			);
// 		}
// 		String sqlCommand = String.format(sqlBodyPattern, whereCondition);
// 		//log.debug(sqlCommand);
// 		result = query(sqlCommand, parameters,
// 			RisultatoRicercaIstanza.RowMapper
// 		);

// 		return result;
	}

	public Integer getNextProgIstanza(
		Integer year
	) {
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("year", year);

		String updSql = """
update FOLIAGE2.FLGISTA_PROGRESSIVI_TAB
set PROG_SUCCESSIVO = PROG_SUCCESSIVO + 1
where ANNO = :year
returning PROG_SUCCESSIVO - 1  as PROG""";
		Integer prog = null;
		try {
			prog = this.queryForObject(updSql, pars, DbUtils.GetIntegerRowMapper("PROG"));
		}
		catch (org.springframework.dao.EmptyResultDataAccessException e) {
			log.info(
				String.format("Nessun progressivo trovato per l'anno %d", year)
			);
		}
		if (prog == null) {
			String insSql = """
insert into FOLIAGE2.FLGISTA_PROGRESSIVI_TAB(ANNO, PROG_SUCCESSIVO)
	values (:year, 1)""";
			int nRows = this.update(insSql, pars);
			if (nRows != 1) {
				throw new FoliageException("Rilevati problemi nella determinazione del codice istanza");
			}
			else {
				prog = 0;
			}
		}
		return prog;
	}

	public Object creaIstanza(
		Integer idUtente,
		String username,
		CreazioneIstanza parametri
		// Integer tipoInsta, Integer sottotipoInsta,
		// Integer tipoProprieta, Integer tipoNaturaProprieta,
		// String nomeIsta,
		// String username, String surname, String name, String gender,
		// LocalDate birthDate, String birthPlace
	) throws SQLException, Exception {

		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {

			//String codRegione = "10";
			Map<String, Object> mapParam = new HashMap<String, Object>();
			//SqlParameterSource parameters = new MapSqlParameterSource(mapParam);
			RowMapper<Integer> intRowmapper = (rs, rn) -> {
				return rs.getInt(1);
			};


			String sql = null;
			DatiTitolare datiTitolare = parametri.getDatiTitolare();
			if (datiTitolare == null) {
				sql = """
insert into FOLIAGE2.FLGTITOLARE_ISTANZA_TAB(
	CODICE_FISCALE, COGNOME, NOME,
	DATA_NASCITA, LUOGO_NASCITA,
	--EMAIL, PEC,
	id_comune, cap, indirizzo, num_civico, telefono, genere
)
select CODI_FISC, COGNOME, NOME,
	DATA_NASCITA, LUOGO_NASCITA,
	--EMAIL, PEC,
	id_comune, cap, indirizzo, num_civico, telefono, sesso
from FOLIAGE2.FLGUTEN_TAB
where ID_UTEN = :idUtente
returning ID_TITOLARE
			""";
				mapParam.put("idUtente", idUtente);
			}
			else {
				Base64FormioFile[] delegaArr = datiTitolare.getFileDelegaProfesssionista();
				//if ()
				Integer idFileDelega = DbUtils.saveBase64FormioFiles(this, delegaArr);


				sql = """
insert into FOLIAGE2.FLGTITOLARE_ISTANZA_TAB(
	CODICE_FISCALE, COGNOME, NOME,
	DATA_NASCITA, LUOGO_NASCITA,
	--EMAIL, PEC, 
	ID_FILE_DELEGA,
	id_comune, cap, indirizzo, num_civico,
	--telefono,
	genere
)
values (
	:codiceFiscale, :cognome, :nome,
	:dataNascita, :luogoNascita,
	--:email, :pec,
	:idFileDelega,
	:idComune, :cap, :indirizzo, :numCivico,
	--:telefono,
	:genere
)
returning ID_TITOLARE
			""";
				mapParam.put("codiceFiscale", datiTitolare.getCodiceFiscale());
				mapParam.put("cognome", datiTitolare.getCognome());
				mapParam.put("nome", datiTitolare.getNome());
				mapParam.put("dataNascita", datiTitolare.getDataDiNascita());
				mapParam.put("luogoNascita", datiTitolare.getLuogoDiNascita());
				// mapParam.put("email", datiTitolare.getEmail());
				// mapParam.put("pec", datiTitolare.getPostaCertificata());
				mapParam.put("idFileDelega", idFileDelega);
				mapParam.put("idComune", datiTitolare.getComune());
				mapParam.put("cap", datiTitolare.getCap());
				mapParam.put("indirizzo", datiTitolare.getIndirizzo());
				mapParam.put("numCivico", datiTitolare.getNumeroCivico());
				
				//mapParam.put("telefono", datiTitolare.getTelefono());
				mapParam.put("genere", datiTitolare.getGenere());

			}
			//parameters = new MapSqlParameterSource(mapParam);
			Integer idTitolare = queryForObject(
					sql, mapParam,
					intRowmapper
			);

			Integer idIsta = template.queryForObject(
					"select nextval('foliage2.flgista_seq')",
					(SqlParameterSource)null,
					intRowmapper
			);

			int currentYear = LocalDate.now().getYear();
			int progIstanza = getNextProgIstanza(currentYear);


			final String codiIsta = String.format(
					"%s_%d_%08d",
					codRegione,
					currentYear,
					progIstanza
			);

			String tipoInsta = parametri.getTipoInsta();
			Integer idEnte = parametri.getIdEnte();
			// Integer sottotipoInsta = parametri.getSottotipoInsta();
			// Integer tipoProprieta = parametri.getTipoProprieta();
			// Integer tipoNaturaProprieta = parametri.getTipoNaturaProprieta();
			String nomeIsta = parametri.getNomeIsta();
			String noteIsta = parametri.getNoteIsta();
			mapParam.clear();
			mapParam.put("idIsta", idIsta);
			mapParam.put("codiIsta", codiIsta);
			mapParam.put("idTitolare", idTitolare);
			mapParam.put("tipoInsta", tipoInsta);
			mapParam.put("nomeIsta", nomeIsta);
			mapParam.put("noteIsta", noteIsta);
			mapParam.put("idUtente", idUtente);
			mapParam.put("username", username);
			mapParam.put("idEnte", idEnte);

		//parameters = new MapSqlParameterSource(mapParam);
		// := concat(codRegione, extract(year from dataIsta), 'I1', LPAD(idIsta::text, 6, '0'));

			sql = """
insert into foliage2.flgista_tab(
	id_ista, codi_ista, nome_ista, data_istanza, id_titolare, id_ente_terr, note,
	stato, fase, id_tipo_istanza, flag_valido, id_utente_compilazione, user_ins, data_ins, data_ini_vali, data_fine_vali
)
select :idIsta, :codiIsta, :nomeIsta, current_date, :idTitolare, :idEnte, :noteIsta,
	1, 1, id_tipo_istanza, 1, :idUtente, :username, localtimestamp(0), current_date, date'9999-12-31'
from foliage2.flgtipo_istanza_tab
where cod_tipo_istanza_specifico = :tipoInsta""";
			update(sql, mapParam);


			platformTransactionManager.commit(status);
			return new Object() {
				public String codIsta = codiIsta;
			};

		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
	}

	public Object getInfoIstanza(String codIstanza) throws SQLException, FoliageException {
		HashMap<String, Object> map = new HashMap<>();
		map.put("codIstanza", codIstanza);
		String query = """
select i.id_ista, i.codi_ista, i.nome_ista, i.note,
	tit.id_titolare, tit.codice_fiscale, tit.cognome, tit.nome,
	t.id_cist, t.id_tipo_istanza, t.cod_tipo_istanza_specifico, t.nome_istanza_specifico,
	e.id_ente, e.tipo_ente, e.nome_ente, 
	s.id_stato, s.cod_stato, s.desc_stato,
	uc.id_uten as id_ute_gestore, uc.user_name as username_gestore, uc.codi_fisc as cod_fisc_gestore, uc.cognome as cognome_gestore, uc.nome as nome_gestore,
	ii.data_invio, ii.data_firma,
	ai.data_assegnazione,
	ui.id_uten as id_ute_istruttore, ui.user_name as username_istruttore, ui.codi_fisc as cod_fisc_istruttore, ui.cognome as cognome_istruttore, ui.nome as nome_istruttore,
	ua.id_uten as id_ute_dirigente, ua.user_name as username_dirigente, ua.codi_fisc as cod_fisc_dirigente, ua.cognome as cognome_dirigente, ua.nome as nome_dirigente,
	v.data_valutazione, v.note_valutazione, v.data_fine_validita,
	p.mesi_durata as mesi_proroga, p.data_proroga,
	di.data_inizio_lavori, di.data_comunicazione_inizio_lavori,
	df.data_fine_lavori, df.data_comunicazione_fine_lavori,
	cnts.cnt_shede_obbl,
	cnts.cnt_shede,
	cnts.cnt_schede_salv,
	cnts.cnt_schede_obbl_salv
from foliage2.flgista_tab i
	join foliage2.flgtitolare_istanza_tab tit on (tit.id_titolare = i.id_titolare)
	join foliage2.flgtipo_istanza_tab t on (t.id_tipo_istanza = i.id_tipo_istanza)
	--join foliage2.flgcist_tab c on (c.id_cist = t.id_cist)
	join foliage2.flgente_root_tab e on (e.id_ente = i.id_ente_terr)
	join foliage2.flgstato_istanza_tab s on (s.id_stato = i.stato)
	left join foliage2.flguten_tab uc on (uc.id_uten = i.id_utente_compilazione)
	left join foliage2.flgista_invio_tab ii on (ii.id_ista = i.id_ista)
	left join foliage2.flgassegnazione_istanza_tab ai on (ai.id_ista = i.id_ista)
	left join foliage2.flguten_tab ui on (ui.id_uten = ai.id_utente_istruttore)
	left join foliage2.flguten_tab ua on (ua.id_uten = ai.id_utente_assegnazione)
	left join foliage2.flgvalutazione_istanza_tab v on (v.id_ista = i.id_ista)
	left join foliage2.FLGISTA_PROROGA_TAB p on (p.id_ista = i.id_ista)
	left join foliage2.flgdate_inizio_lavori_istanza_tab di on (di.id_ista = i.id_ista)
	left join foliage2.flgdate_fine_lavori_istanza_tab df on (df.id_ista = di.id_ista)
	cross join LATERAL (
			select count(case when IS_OBBLIGATORIA then 1 end) as cnt_shede_obbl,
					count(*) as cnt_shede,
					count(SS.PROG_SCHEDA) as cnt_schede_salv,
					count(case when SS.PROG_SCHEDA is not null and IS_OBBLIGATORIA then 1 end) as cnt_schede_obbl_salv
			from FOLIAGE2.FLGSCHEDE_TIPOISTANZA_TAB STI
					left join FOLIAGE2.FLGISTA_SCHEDE_SALVATE_TAB SS on (
							SS.ID_ISTA = i.id_ista
							and SS.PROG_SCHEDA = STI.PROG_SCHEDA
					)
			where STI.id_tipo_istanza = i.id_tipo_istanza
	) cnts
WHERE i.codi_ista = :codIstanza""";

		SqlRowSet result = queryForRowSet(
				query, map
			);

// 		ResultSet result = this.GetResult(
// 			(conn) -> {
// 				PreparedStatement statement = conn.prepareStatement("""
// select i.id_ista, i.codi_ista, i.nome_ista, i.note,
// 	tit.id_titolare, tit.codice_fiscale, tit.cognome, tit.nome,
// 	t.id_cist, t.id_tipo_istanza, t.cod_tipo_istanza_specifico, t.nome_istanza_specifico,
// 	e.id_ente, e.tipo_ente, e.nome_ente, 
// 	s.id_stato, s.cod_stato, s.desc_stato,
// 	uc.id_uten as id_ute_gestore, uc.user_name as username_gestore, uc.codi_fisc as cod_fisc_gestore, uc.cognome as cognome_gestore, uc.nome as nome_gestore,
// 	ai.data_assegnazione,
// 	ui.id_uten as id_ute_istruttore, ui.user_name as username_istruttore, ui.codi_fisc as cod_fisc_istruttore, ui.cognome as cognome_istruttore, ui.nome as nome_istruttore,
// 	ua.id_uten as id_ute_dirigente, ua.user_name as username_dirigente, ua.codi_fisc as cod_fisc_dirigente, ua.cognome as cognome_dirigente, ua.nome as nome_dirigente,
// 	v.data_valutazione, v.note_valutazione, v.data_fine_validita,
// 	p.mesi_durata as mesi_proroga, p.data_proroga,
// 	di.data_inizio_lavori, di.data_comunicazione_inizio_lavori,
// 	df.data_fine_lavori, df.data_comunicazione_fine_lavori
// from foliage2.flgista_tab i
// 	join foliage2.flgtitolare_istanza_tab tit on (tit.id_titolare = i.id_titolare)
// 	join foliage2.flgtipo_istanza_tab t on (t.id_tipo_istanza = i.id_tipo_istanza)
// 	--join foliage2.flgcist_tab c on (c.id_cist = t.id_cist)
// 	join foliage2.flgente_root_tab e on (e.id_ente = i.id_ente_terr)
// 	join foliage2.flgstato_istanza_tab s on (s.id_stato = i.stato)
// 	join foliage2.flguten_tab uc on (uc.id_uten = i.id_utente_compilazione)
// 	left join flgassegnazione_istanza_tab ai on (ai.id_ista = i.id_ista)
// 	left join foliage2.flguten_tab ui on (ui.id_uten = ai.id_utente_istruttore)
// 	left join foliage2.flguten_tab ua on (ua.id_uten = ai.id_utente_assegnazione)
// 	left join foliage2.flgvalutazione_istanza_tab v on (v.id_ista = i.id_ista)
// 	left join foliage2.FLGISTA_PROROGA_TAB p on (p.id_ista = i.id_ista)
// 	left join foliage2.flgdate_inizio_lavori_istanza_tab di on (di.id_ista = i.id_ista)
// 	left join foliage2.flgdate_fine_lavori_istanza_tab df on (df.id_ista = di.id_ista)
// WHERE i.codi_ista = ?""");
// 				statement.setString(1, codIstanza);
// 				return statement.executeQuery();
// 			}
// 		);
		
		if (result.next()) {
			Integer idIstanza = result.getInt("id_ista");
			if (result.wasNull()) {
				idIstanza = null;
			}
			final String statoStr = result.getString("desc_stato");
			final String tipo = result.getString("nome_istanza_specifico");
			final String codStatoStr = result.getString("cod_stato");

			String tipoEnte = result.getString("tipo_ente");
			String nomeEnte = result.getString("nome_ente");

			String descEnteStrNonFin = null;
			if (tipoEnte != null) {
				tipoEnte = tipoEnte.toLowerCase();
				descEnteStrNonFin = String.format(
					(tipoEnte.equals("regione")) ? "%s%s %s" : "%s%s di %s",
					tipoEnte.substring(0, 1).toUpperCase(), tipoEnte.substring(1), nomeEnte
				);

			}

			final String descEnteStr = descEnteStrNonFin;

			Integer idEnte = result.getInt("id_ente");
			if (result.wasNull()) {
				idEnte = null;
			}
			final Integer idEnteFin = idEnte;

			java.sql.Date dataValuSql = result.getDate("data_valutazione");
			final LocalDate dataVal = dataValuSql == null ? null : dataValuSql.toLocalDate();

			java.sql.Date dataFineValiSql = result.getDate("data_fine_validita");
			final LocalDate dataFineVali = dataFineValiSql == null ? null : dataFineValiSql.toLocalDate();

			java.sql.Date dataIniSql = result.getDate("data_inizio_lavori");
			final LocalDate dataInizLav = dataIniSql == null ? null : dataIniSql.toLocalDate();


			java.sql.Date dataFinSql = result.getDate("data_fine_lavori");
			final LocalDate dataFineLav = dataFinSql == null ? null : dataFinSql.toLocalDate();

			java.sql.Date dataProrogaSql = result.getDate("data_proroga");
			final LocalDate dataProrog = dataProrogaSql == null ? null : dataProrogaSql.toLocalDate();

			java.sql.Date dataInvioSql = result.getDate("data_invio");
			final LocalDate dataInv = dataInvioSql == null ? null : dataInvioSql.toLocalDate();

			LocalDate dataFirm = DbUtils.GetLocalDate(result, 0, "data_firma");

			Integer mesiP = result.getInt("mesi_proroga");
			if (result.wasNull()) {
				mesiP = null;
			}
			final Integer mesiPfin = mesiP;

			final String codFisc = result.getString("codice_fiscale");
			final String cogn = result.getString("cognome");
			final String nom = result.getString("nome");
			final Object titolareObj = new Object() {
				public String codFiscale = codFisc;
				public String nome = nom;
				public String cognome = cogn;
			};


			final String userIstr = result.getString("username_istruttore");
			final String codFiscIstr = result.getString("cod_fisc_istruttore");
			final String cognIstr = result.getString("cognome_istruttore");
			final String nomIstr = result.getString("nome_istruttore");
			final Object istruttoreObj = new Object() {
				public String username = userIstr;
				public String codFiscale = codFiscIstr;
				public String nome = nomIstr;
				public String cognome = cognIstr;
			};


			final String userGest = result.getString("username_gestore");
			final String codFiscGest = result.getString("cod_fisc_gestore");
			final String cognGest = result.getString("cognome_gestore");
			final String nomGest = result.getString("nome_gestore");
			final Object gestoreObj = new Object() {
				public String username = userGest;
				public String codFiscale = codFiscGest;
				public String nome = nomGest;
				public String cognome = cognGest;
			};

			Integer numSchedeObbl = result.getInt("cnt_shede_obbl");
			Integer numSchedeTot = result.getInt("cnt_shede");
			Integer numSchedeSalv = result.getInt("cnt_schede_salv");
			Integer numSchedeObblSalv = result.getInt("cnt_schede_obbl_salv");

			Object outVal = new Object() {
				public String stato = codStatoStr;
				public String descrizioneTipo = tipo;
				public String descrizioneStato = statoStr;
				public String enteTerritoriale = descEnteStr;
				public Integer idEnteTerritoriale = idEnteFin;
				public LocalDate dataInvio = dataInv;
				public LocalDate dataFirma = dataFirm;
				public LocalDate dataValutazione = dataVal;
				public LocalDate dataFineValidita = dataFineVali;
				public LocalDate dataInizioLavori = dataInizLav;
				public LocalDate dataFineLavori = dataFineLav;
				public Object titolare = titolareObj;
				public Object gestore = gestoreObj;
				public Object istruttore = istruttoreObj;
				public LocalDate dataProroga = dataProrog;
				public Integer mesiProroga = mesiPfin;
				public Integer numSchedeObblig = numSchedeObbl;
				public Integer numSchedeObbligComp = numSchedeObblSalv;
				public Integer numSchede = numSchedeTot;
				public Integer numSchedeComp = numSchedeSalv;
			};

			return outVal;
		}
		else {
			throw new FoliageException("Istanza non trovata");
		}
	}

	public Object salvaSchedaIstanza(
			String codIstanza, Integer idxScheda,
			String codFiscaleUtente, Integer idUtente,
			ServletInputStream inputStream
	) throws Exception {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );
		Object objOut = null;
		try {

			JsonElement elem = JsonParser.parseReader(new InputStreamReader(inputStream));
			Pair<Collection<JsonElement>, HashMap<String, Object>> outVal = CaricatoreIstanza.salvaScheda(
					this, 
					codRegione, codIstanza, elem, idxScheda, idUtente, this.sridGeometrie
				);
	
			//return JsonIO.gson.fromJson(outVal, Object.class);
			Collection<JsonElement> newSchedeJson = outVal.getValue0();
			Object[] newSchedeObj = newSchedeJson.stream().map(x -> JsonIO.gson.fromJson(x, Object.class)).toArray();
			HashMap<String, Object> newContext = outVal.getValue1();
			objOut = new Object() {
				public Object schede = newSchedeObj;
				public HashMap<String, Object> contesto = newContext;
				public List<Object> statoCompilazione = getStatoCompilazioneIstanza(codIstanza);
			};
			platformTransactionManager.commit(status);
		}catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return objOut;
	}

	public List<Foto> getFotoRilevamento(Long idRile) {
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("idRile", idRile);

		List<Foto> outVal = template.query(
			Foto.queryForIdRile,
			pars,
			Foto.RowMapper
		);
		return outVal;
	}
	public List<Rilevamento> getRilevamenti(String codIstanza, Integer idUtente, String authority, String authScope) {
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("codIstanza", codIstanza);
		pars.put("idUtente", idUtente);
		pars.put("authority", authority);
		pars.put("authScope", authScope);

		List<Rilevamento> outVal = template.query(
			Rilevamento.queryForCodiIstaIdUtenteProf,
			pars,
			Rilevamento.RowMapper(this)
		);
		return outVal;
	}

	public List<Object> getStatoCompilazioneIstanza(String codIstanza) {
		String sql = """
select STI.PROG_SCHEDA, STI.COD_SCHEDA, STI.IS_OBBLIGATORIA, DATA_ULTIMO_SALVATAGGIO,
	STI.*, SS.*
from FOLIAGE2.FLGISTA_TAB i
	join FOLIAGE2.FLGSCHEDE_TIPOISTANZA_TAB STI using (ID_TIPO_ISTANZA)
	left join FOLIAGE2.FLGISTA_SCHEDE_SALVATE_TAB SS on (
		SS.ID_ISTA = i.id_ista
		and SS.PROG_SCHEDA = STI.PROG_SCHEDA
	)
where I.CODI_ISTA = :codIstanza""";
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("codIstanza", codIstanza);

		List<Object> outVal = template.query(
			sql,
			pars,
			(rs, rn) -> {
				return new Object() {
					public Integer prog = DbUtils.GetInteger(rs, 0, "PROG_SCHEDA");
					public String codScheda = rs.getString("COD_SCHEDA");
					public Boolean isObbligatoria = DbUtils.GetBoolean(rs, 0, "IS_OBBLIGATORIA");
					public LocalDateTime dataSalvataggio = DbUtils.GetLocalDateTime(rs, 0, "DATA_ULTIMO_SALVATAGGIO");
				};
			}
		);
		return outVal;
	}
	public Object apriIstanza(String codIstanza, Integer idUtente, String authority, String authScope) throws Exception {

		final Pair<Collection<JsonElement>, HashMap<String, Object>> pair =  CaricatoreIstanza.load(this, codRegione, codIstanza, this.sridGeometrie);
		
		Object outVal = new Object() {
			public Object[] schede = pair.getValue0().stream().map(x -> JsonIO.gson.fromJson(x, Object.class)).toArray();
			public HashMap<String, Object> contesto = pair.getValue1();
			public List<Rilevamento> rilevamenti = getRilevamenti(codIstanza, idUtente, authority, authScope);
			public List<Object> statoCompilazione = getStatoCompilazioneIstanza(codIstanza);
		};
		return outVal;
	}
// 	public Object apriIstanza2(String codIstanza) throws Exception {
// 		ResultSet result = this.GetResult(
// 			(conn) -> {
// 				PreparedStatement statement = conn.prepareStatement("""
// SELECT id_ista, id_titolare, cod_tipo_istanza, cod_tipo_istanza_specifico, desc_cist, nome_istanza_specifico,
// 	i.id_qual, i.id_tprp, i.id_nprp, i.id_tazi,
// 	nome_ista, e.tipo_ente, e.nome_ente, i.note,
// 	ST_AsText(la.shape_vinc) as shape_ente, 
// 	ST_AsText(la.shape_envelope_vinc) as shape_envel_ente, 
// 	--ST_AsText(ST_Transform(la.shape_envelope_vinc, 3857)) as shape_envel_ente,
// 	la.srid
// FROM foliage2.flgista_tab i
// 	join foliage2.flgtipo_istanza_tab t on (t.id_tipo_istanza = i.id_tipo_istanza)
// 	join foliage2.flgcist_tab c on (c.id_cist = t.id_cist)
// 	left join foliage2.flgente_root_tab e on (e.id_ente = i.id_ente_terr)
// 	left join foliage2.flglimiti_amministrativi_tab la on (la.id_ente_terr = i.id_ente_terr)
// WHERE i.codi_ista = ?""");
// 				statement.setString(1, codIstanza);
// 				return statement.executeQuery();
// 			}
// 		);
// 		if (result.next()) {
// 			Integer idIstanza = result.getInt("id_ista");
// 			if (result.wasNull()) {
// 				idIstanza = null;
// 			}
// 			Integer idTitolare =  result.getInt("id_titolare");
// 			if (result.wasNull()) {
// 				idTitolare = null;
// 			}
// 			String tipoIsta = result.getString("cod_tipo_istanza");
// 			String tipoSpecifico = result.getString("cod_tipo_istanza_specifico");
// 			String nomeTipo = result.getString("desc_cist");
// 			String nomeTipoSpecifico = result.getString("nome_istanza_specifico");
// 			String nomeIstanza = result.getString("nome_ista");
// 			String tipoEnte = result.getString("tipo_ente");
// 			String nomeEnte = result.getString("nome_ente");
// 			String ente = (tipoEnte == null) ? null : String.format("%s %s", tipoEnte, nomeEnte);
// 			String noteIstanza = result.getString("note");
// 			String boxInquadr = result.getString("shape_envel_ente");
// 			String shape = result.getString("shape_ente");
// 			Integer sridInquadr = result.getInt("srid");
// 			if (result.wasNull()) {
// 				sridInquadr = null;
// 			}
// 			final Integer sridInquadrFin = sridInquadr;

// 			Integer tipoAzi = result.getInt("id_tazi");
// 			if (result.wasNull()) {
// 				tipoAzi = null;
// 			}
// 			final Integer tipoAziFin = tipoAzi;

// 			Integer natProp = result.getInt("id_nprp");
// 			if (result.wasNull()) {
// 				natProp = null;
// 			}
// 			final Integer natPropFin = natProp;

// 			Integer tipoProp = result.getInt("id_tprp");
// 			if (result.wasNull()) {
// 				tipoProp = null;
// 			}
// 			final Integer tipoPropFin = tipoProp;

// 			Integer qualTito = result.getInt("id_qual");
// 			if (result.wasNull()) {
// 				qualTito = null;
// 			}
// 			final Integer qualTitoFin = qualTito;

// 			String codIstanzaApp = codIstanza;
// 			log.debug(tipoSpecifico);
// 			Object outVal = null;

// 			final Object tipologia = new Object() {
// 				public String codice = codIstanza;
// 				public String nome = nomeIstanza;
// 				public String tipo = nomeTipo;
// 				public String tipoSpecifico = nomeTipoSpecifico;
// 				public String enteCompetente = ente;
// 				public String note = noteIstanza;
// 			};
// 			final DatiTitolare datiTitolare = readTitolareIstanza(idTitolare);
// 			final Object areaGest = new Object() {
// 				public Integer tipoAzienda = tipoAziFin;
// 				public Integer naturaProprieta = natPropFin;
// 				public Integer tipoProprieta = tipoPropFin;
// 				public Integer qualificaTitolare = qualTitoFin;
// 			};
// 			final Object particelleCata = readParticelleCatastali(idIstanza);
// 			//final Object particellaFor = readParticellForestale(idIstanza);

// 			final Object context = new Object() {
// 				public String boxInquadramento = boxInquadr;
// 				public String shapeLimiti = shape;
// 				public Integer sridInquadramento = sridInquadrFin;
// 				public String codIstanza = codIstanzaApp;
// 				public String codTipoSpecifico = tipoSpecifico;
// 				public String cfTitolare = datiTitolare.getCodiceFiscale();
// 			};
// 			Gson gson = new Gson();
// 			HashMap<String, Object> initialCont = new HashMap<>();
// 			initialCont.put("codIstanza", codIstanzaApp);
// 			final Object testObj = gson.fromJson(SchedaIstanza.TipologiaIstanza.analizzaCaricaScheda(template, initialCont), Object.class);
// 			switch (tipoSpecifico) {
// 				case "TAGLIO_BOSCHIVO": {
// 					outVal = new Object() {
// 						public Object contesto = context;
// 						public Object tipo = tipologia;
// 						public Object titolare = datiTitolare;
// 						public Object areaGestione = areaGest;
// 						public Object particelleCatastali = particelleCata;
// 						public Object titolNew = testObj;
// 					};
// 				}; break;
// 				case "INTERVENTO_A_COMUNICAZIONE": {
// 					outVal = new Object() {
// 						public Object contesto = context;
// 						public Object tipo = tipologia;
// 						public Object titolare = datiTitolare;
// 						public Object areaGestione = areaGest;
// 						public Object particelleCatastali = particelleCata;
// 					};
// 				}; break;
// 				case "SOPRA_SOGLIA": {
// 					outVal = new Object() {
// 						public Object contesto = context;
// 						public Object tipo = tipologia;
// 						public Object titolare = datiTitolare;
// 						public Object areaGestione = areaGest;
// 						public Object particelleCatastali = particelleCata;
// 						public Object titolNew = testObj;
// 					};
// 				}; break;
// 				case "ATTUAZIONE_PIANI": {
// 					outVal = new Object() {
// 						public Object contesto = context;
// 						public Object tipo = tipologia;
// 						public Object titolare = datiTitolare;
// 						public Object areaGestione = areaGest;
// 						public Object particelleCatastali = particelleCata;
// 					};
// 				}; break;
// 				case "IN_DEROGA": {
// 					outVal = new Object() {
// 						public Object contesto = context;
// 						public Object tipo = tipologia;
// 						public Object titolare = datiTitolare;
// 						public Object areaGestione = areaGest;
// 						public Object particelleCatastali = particelleCata;
// 					};
// 				}; break;
// 				default: {
// 					throw new FoliageException("Tipo di istanza non gestito");
// 				}
// 			}
// 			return outVal;
// 		}
// 		else {
// 			throw new FoliageException("Istanza non trovata");
// 		}
// 	}

	public Object readParticelleCatastali(Integer idIstanza) throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select ID_COMUNE as COMUNE, SEZIONE, FOGLIO, PARTICELLA, SUB
from FOLIAGE2.FLGPART_CATASTALI_TAB
where ID_ISTA = ?
""");
				statement.setInt(1, idIstanza);
				return statement.executeQuery();
			}
		);
		return result;
	}

	public DatiTitolare readTitolareIstanza(Integer idTitolare) throws SQLException {
// 		ResultSet result = this.GetResult(
// 			(conn) -> {
// 				PreparedStatement statement = conn.prepareStatement("""
// select CODICE_FISCALE, COGNOME, NOME, DATA_NASCITA, LUOGO_NASCITA,
// 	EMAIL, PEC, ID_FILE_DELEGA
// from FOLIAGE2.FLGTITOLARE_ISTANZA_TAB
// where ID_TITOLARE = ?
// """);
// 				statement.setInt(1, idTitolare);
// 				return statement.executeQuery();
// 			}
// 		);
		String query = """
select CODICE_FISCALE, COGNOME, NOME, DATA_NASCITA, LUOGO_NASCITA,
	ID_PROVINCIA, ID_COMUNE, CAP, INDIRIZZO, NUM_CIVICO,
	TELEFONO, EMAIL, PEC, 
	ID_FILE_DELEGA
from FOLIAGE2.FLGTITOLARE_ISTANZA_TAB
	left join FOLIAGE2.FLGENTE_COMUNE_TAB using (ID_COMUNE)
where ID_TITOLARE = :idTitolare""";
		HashMap<String, Object> pars = new HashMap<>(){{
			put("idTitolare", idTitolare);
		}};
		SqlRowSet result = queryForRowSet(query, pars);

		if (result.next()) {
			Integer idFileDelega = result.getInt(8);
			if (result.wasNull()) {
				idFileDelega = null;
			}
			final Integer idFileDelegaFin = idFileDelega;
			java.sql.Date dataNasc = result.getDate("data_nascita");
			if (result.wasNull()) {
				dataNasc = null;
			}
			final java.sql.Date dataNascFin = dataNasc;
			return new DatiTitolare() {{
				codiceFiscale = result.getString("codice_fiscale");
				cognome = result.getString("cognome");
				nome = result.getString("nome");
				dataDiNascita = (dataNascFin == null) ? null : dataNascFin.toLocalDate();
				luogoDiNascita = result.getString("luogo_nascita");
				provincia = result.getInt("id_provincia");
				if (result.wasNull()) {
					provincia = null;
				}
				comune = result.getInt("id_comune");
				if (result.wasNull()) {
					comune = null;
				}
				cap = result.getString("cap");
				indirizzo = result.getString("indirizzo");
				numeroCivico = result.getString("num_civico");
				fileDelegaProfesssionista = getBase64FormioFiles(idFileDelegaFin);
			}};
		}
		else {
			return null;
		}
	}

	public ResultSet getProfiliTerritoriali() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select ID_PROFILO, DESCRIZIONE
from FOLIAGE2.FLGPROF_TAB
where FLAG_TERRITORIALE
""");
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet getProfiliRichiesta() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select ID_PROFILO, DESCRIZIONE, TIPO_AMBITO, TIPO_AUTH
from FOLIAGE2.FLGPROF_TAB
where TIPO_AMBITO != 'GENERICO'
""");
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet getCaserme() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select ID_ENTE, NOME_ENTE
from FLGENTE_ROOT_TAB
where TIPO_ENTE = 'CASERMA'
				""");
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet getParchi() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select ID_ENTE, NOME_ENTE
from FLGENTE_ROOT_TAB
where TIPO_ENTE = 'PARCO'
				""");
				return statement.executeQuery();
			}
		);
		return result;
	}
	public ResultSet GetProvincie(String codiRegi) throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select id_provincia as id_prov, provincia as desc_prov
from flgprov_viw p
	join flgente_regione_tab r on (r.id_regione = p.id_regione)
where r.codi_istat = ?
order by desc_prov
""");
				statement.setString(1, codiRegi);
				return statement.executeQuery();
			}
		);
		return result;
	}

	
	public ResultSet GetAllProvincie() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select id_provincia as id_prov, provincia as desc_prov
from flgprov_viw p
	join flgente_regione_tab r on (r.id_regione = p.id_regione)
order by desc_prov""");
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet GetComuniProvincia(Integer idProvincia) throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select ID_COMUNE as ID_COMU, COMUNE as DESC_COMU
from FLGCOMU_VIW as C
where ID_PROVINCIA = ?
order by DESC_COMU
""");
				statement.setInt(1, idProvincia);
				return statement.executeQuery();
			}
		);
		return result;
	}
	public ResultSet GetProvincieRegioneHost() throws SQLException {
		return GetProvincie(codRegione);
	}

	public Object GetAmbitiReportP4(Integer idUtente, String authority, String tipoAmbito) throws Exception {
		String sqlQuery = null;
		HashMap<String, Object> map = new HashMap<>();
		map.put("codRegione", codRegione);
		switch (authority) {
			case "RESP": {
				map.put("idUtente", idUtente);
				map.put("authority", authority);
				map.put("tipoAmbito", tipoAmbito);
				sqlQuery = """
with tipo_enti as (
		select tipo_ente
		from foliage2.flgcist_tab
	),
	miei_enti as (
		select id_ente
		from foliage2.flgprof_tab p
			join foliage2.flgenti_profilo_tab ep using (id_profilo)
		where tipo_auth = :authority
			and tipo_ambito = :tipoAmbito
			and id_utente = :idUtente
	),
	regi as (
		select *
		from foliage2.flgregi_viw fv
		where codi_istat_regione = :codRegione
	),
	prov as (
		select p.*
		from regi 
			join foliage2.flgprov_viw p using (id_regione)
	),
	comu as (
		select c.*, p.provincia 
		from prov p
			join foliage2.flgcomu_viw c using (id_provincia)
	)
(
	select id_regione::int as id, 'Regione '||regione::varchar as desc
	from regi
	where 'REGIONE' in (
			select tipo_ente
			from tipo_enti
		)
		and id_regione in (
			select id_ente
			from miei_enti
		)
)
union all(
	select id_provincia::int as id, 'Provincia di '||provincia::varchar as desc
	from prov
	where 'PROVINCIA' in (
			select tipo_ente
			from tipo_enti
		)
		and id_provincia in (
			select id_ente
			from miei_enti
		)
	order by provincia
)
union all (
	select id_comune::int as id, 'Comune di '||comune||' ('||provincia||')'::varchar as desc
	from comu
	where 'COMUNE' in (
			select tipo_ente
			from tipo_enti
		)
		and id_comune in (
			select id_ente
			from miei_enti
		)
	order by provincia, comune
)""";
			}; break;
			case "AMMI": {
				sqlQuery = """
with tipo_enti as (
		select tipo_ente
		from foliage2.flgcist_tab
	),
	regi as (
		select *
		from foliage2.flgregi_viw fv
		where codi_istat_regione = :codRegione
	),
	prov as (
		select p.*
		from regi 
			join foliage2.flgprov_viw p using (id_regione)
	),
	comu as (
		select c.*, p.provincia 
		from prov p
			join foliage2.flgcomu_viw c using (id_provincia)
	)
(
	select id_regione::int as id, 'Regione '||regione::varchar as desc
	from regi
	where 'REGIONE' in (
			select tipo_ente
			from tipo_enti
		)
)
union all(
	select id_provincia::int as id, 'Provincia di '||provincia::varchar as desc
	from prov
	where 'PROVINCIA' in (
			select tipo_ente
			from tipo_enti
		)
	order by provincia
)
union all (
	select id_comune::int as id, 'Comune di '||comune||' ('||provincia||')'::varchar as desc
	from comu
	where 'COMUNE' in (
			select tipo_ente
			from tipo_enti
		)
	order by provincia, comune
)""";
			}; break;
			default: {
				throw new FoliageException("Il tuo profilo non è abilitato per questa richiesta");
			}
		}
		return query(
				sqlQuery,
				map,
				(rs, rn) -> {
					Integer idApp= rs.getInt(1);
					if (rs.wasNull()) {
						idApp = null;
					}
					String descApp = rs.getString(2);
					final Integer idFin = idApp;

	
					return new Object() {
						public final Integer id = idFin;
						public final String desc = descApp;
					};
				}				
			);
	}

	public Object GetInfoEnte(Integer idEnte) throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select TIPO_ENTE,
	case TIPO_ENTE
		when 'COMUNE' then C.ID_PROVINCIA
		when 'PROVINCIA' then P.ID_PROVINCIA
	end as ID_PROVINCIA,
	ID_COMUNE
from FLGENTE_ROOT_TAB as ER
	join FLGENTE_TERR_TAB as ET on (ET.ID_ENTE_TERR = ER.ID_ENTE)
	left join FLGENTE_COMUNE_TAB as C on (TIPO_ENTE = 'COMUNE' and C.ID_COMUNE = ER.ID_ENTE)
	left join FLGENTE_PROVINCIA_TAB as P on (TIPO_ENTE = 'PROVINCIA' and P.ID_PROVINCIA = ER.ID_ENTE)
where ER.ID_ENTE = ?
""");
				statement.setInt(1, idEnte);
				return statement.executeQuery();
			}
		);
		if (result.next()) {
			Integer prov = result.getInt(2);
			if (result.wasNull()) {
				prov = null;
			}
			final Integer prov2 = prov;
			Integer comu = result.getInt(3);
			if (result.wasNull()) {
				comu = null;
			}
			final Integer comu2 = comu;

			return new Object() {
				public final String ambitoTerritoriale = result.getString(1);
				public final Integer provincia = prov2;
				public final Integer comune = comu2;
			};
		}
		else {
			return null;
		}


// 		String query = """
// select tipo_ente, null::int as id_provincia, null::int as id_comune, null::varchar as comune, null::varchar as provincia
// from foliage2.flgente_root_tab
// where tipo_ente = 'REGIONE'
// 	and id_ente = :idEnte
// union all
// select 'PROVINCIA' as tipo_ente, id_provincia, null::int as id_comune, null::varchar as comune, provincia
// from foliage2.flgprov_viw fv 
// where id_provincia = :idEnte
// union all
// select 'COMUNE' as tipo_ente, id_provincia, id_comune, c.comune, er.nome_ente as provincia
// from foliage2.flgcomu_viw c
// 	join foliage2.flgente_root_tab er on (er.id_ente = c.id_provincia)
// where c.id_comune = :idEnte""";

// 		HashMap<String, Object> map = new HashMap<>();
// 		map.put("idEnte", idEnte);
// 		Object res = queryForObject(
// 			query, map,
// 			(rs, rn) -> {
// 				Integer prov = rs.getInt(2);
// 				if (rs.wasNull()) {
// 					prov = null;
// 				}
// 				final Integer prov2 = prov;
// 				Integer comu = rs.getInt(3);
// 				if (rs.wasNull()) {
// 					comu = null;
// 				}
// 				final Integer comu2 = comu;

// 				return new Object() {
// 					public final String ambitoTerritoriale = rs.getString(1);
// 					public final Integer provincia = prov2;
// 					public final Integer comune = comu2;
// 					public final String nomeComune = rs.getString(4);
// 					public final String nomeProvincia = rs.getString(5);
// 				};
// 			}
// 		);
// 		return res;
	}

	public Object GetInfoComune(Integer idComune) throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select comune, provincia
from foliage2.flgcomu_viw c
	join foliage2.flgprov_viw p using (id_provincia)
where c.id_comune = ?
""");
				statement.setInt(1, idComune);
				return statement.executeQuery();
			}
		);
		if (result.next()) {

			return new Object() {
				public final String comune = result.getString(1);
				public final String provincia = result.getString(2);
			};
		}
		else {
			return null;
		}
	}

	public Object GetInfoComuniRegione(String codRegione) throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select c.id_comune, comune, provincia, id_provincia
from foliage2.flgprov_viw p
	join foliage2.flgcomu_viw c using (id_provincia)
where p.id_regione = (
		select r.id_regione
		from foliage2.flgente_regione_tab r
		where r.codi_istat = ?
	)
""");
				statement.setString(1, codRegione);
				return statement.executeQuery();
			}
		);
		return result;
	}
	public Object GetInfoComuniHost() throws SQLException {
		return GetInfoComuniRegione(codRegione);
	}


	public Object GetRegioneHost() throws SQLException {
		String sql = """
select ID_REGIONE, CODI_ISTAT_REGIONE, REGIONE
from foliage2.FLGREGI_VIW as R
where R.CODI_ISTAT_REGIONE = :codRegione""";
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("codRegione", codRegione);

		Object outVal = queryForObject(
			sql, 
			pars,
			(rs, rn) -> {
				return new Object() {
					public Integer id = rs.getInt(1);
					public String codIstat = rs.getString(2);
					public String nome = rs.getString(3);
				};
			}
		);
		return outVal;

// 		ResultSet result = this.GetResult(
// 			(conn) -> {
// 				PreparedStatement statement = conn.prepareStatement("""
// select ID_REGIONE, CODI_ISTAT_REGIONE, REGIONE
// from foliage2.FLGREGI_VIW as R
// where R.CODI_ISTAT_REGIONE = ?
// """);
// 				statement.setString(1, codRegione);
// 				return statement.executeQuery();
// 			}
// 		);
// 		if (result.next()) {
// 			return new Object() {
// 				public Integer id = result.getInt(1);
// 				public String codIstat = result.getString(2);
// 				public String nome = result.getString(3);
// 			};
// 		}
// 		else {
// 			return null;
// 		}
	}

	public ResultSet getProfili() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select ID_PROFILO as \"idProfilo\", DESCRIZIONE as \"descProfilo\", TIPO_AUTH as \"codProfilo\", TIPO_AMBITO as \"tipoAmbito\"
from FOLIAGE2.FLGPROF_TAB
""");
				return statement.executeQuery();
			}
		);
		return result;
	}


	public ResultSet getListaStatiIstanza() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select ID_STATO, COD_STATO, DESC_STATO
from FOLIAGE2.FLGSTATO_ISTANZA_TAB T
order by ID_STATO
				""");
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet getListaTipiIstanza() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select ID_CIST, NOME, DESCRIZIONE_LUNGA, TIPO_AUTH, FLAG_SENIOR, COD_TIPO_ISTANZA, TIPO_ENTE
from FOLIAGE2.FLGCIST_TAB T
order by ID_CIST
				""");
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet getListaTipiSpecificiIstanza() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select id_tipo_istanza, id_cist, cod_tipo_istanza_specifico, nome_istanza_specifico
from foliage2.flgtipo_istanza_tab
order by id_tipo_istanza		
				""");
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet getListaTipiAzienda() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
SELECT id_tazi, desc_tazi
FROM foliage2.flgtazi_tab
				""");
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet getListaTipiProprieta() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
SELECT id_tprp, desc_tprp
FROM foliage2.flgtprp_tab
				""");
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet getListaNaturaProprieta() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
SELECT id_nprp, desc_nprp
FROM foliage2.flgnprp_tab
				""");
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet getListaQualificazioniProprietario() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
SELECT id_qual, desc_qual
FROM foliage2.flgqual_tab
				""");
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet getListaIstanze() throws SQLException, Exception {

		ResultSet result = null;
		Connection conn = this.connection;
		conn.beginRequest();
		try {
			conn.setAutoCommit(false);

			PreparedStatement statement = conn.prepareStatement("""
select codi_ista, id_tipo_istanza, c.desc_cist, nome_ista, i.note, 
	s.CODICE_FISCALE, s.COGNOME, s.NOME, 
	i.data_istanza, u.codi_fisc as codi_fisc_ute
from foliage2.flgista_tab as i
	join foliage2.flgtipo_istanza_tab as t using (id_tipo_istanza)
	join foliage2.flgcist_tab c on (c.id_cist = t.id_cist)
	left join (
			select * 
			from foliage2.FLGTITOLARE_ISTANZA_TAB 
		) as s using (id_titolare)
	left join foliage2.flguten_tab u on (u.user_name = i.user_ins)
order by i.id_ista desc
""");
			result = statement.executeQuery();

		}
		finally {
			conn.endRequest();
		}
		return result;
	}


	public ResultSet getListaParticelleIstanza(String codIsta) throws SQLException, Exception {

		ResultSet result = null;
		Connection conn = this.connection;
		conn.beginRequest();
		try {
			conn.setAutoCommit(false);

//            PreparedStatement statement = conn.prepareStatement("""
//select a.*, b.supe_cata as supe,a.long as lon, a.lat as lat, c.desc_comu as comu, d.desc_prov as prov 
//        from flgista_tab i
//        	join flgispa_tab b using (id_ista)
//        	join flgpart_tab a using (id_part)
//        	join flgcomu_tab c using (codi_comu) 
//        	join flgprov_tab d using (codi_prov)
//        where i.codi_ista = ? """);

			PreparedStatement statement = conn.prepareStatement("""
select a.id_pfor, a.esposizione, a.altimetria, a.pendenza, a.giacitura, a.substrato_ped, a.profondita, a.tessitura, a.data_ins, ST_AsText(a.shape) as shape
from foliage2.flgista_tab i
	join foliage2.flgpfor_tab a using (id_ista)
where i.codi_ista = ? """);

			statement.setString(1, codIsta);
			result = statement.executeQuery();

		}
		finally {
			conn.endRequest();
		}
		return result;
	}




	public Object getInfoUtente(String username) throws SQLException, Exception {
		DatiUtente outval = null;

		String query = """
select u.id_uten, nome, cognome, user_name, codi_fisc, flag_accettazione, data_nascita,
	luogo_nascita, sesso, c.id_provincia, c.id_comune, cap, indirizzo, num_civico,
	telefono, email, pec,
	pu.id_profilo as id_profilo_default,
	(up.id_utente is not null) as test_prof
from foliage2.flguten_tab u
	left join foliage2.FLGPROFILI_UTENTE_TAB pu on (pu.id_utente = u.id_uten and pu.flag_default = true)
	left join foliage2.FLGUTE_PROFESSIONISTI_TAB up on (up.id_utente = u.id_uten)
	left join foliage2.flgente_comune_tab c using (id_comune)
where user_name = :username""";
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("username", username);


		SqlRowSet result = this.queryForRowSet(query, pars);

		if (result.next()) {
			AutocertificazioneProfessionista autocertProf = null;
			Boolean isProfessionista = null;
			final int idUtente = result.getInt("id_uten");
			isProfessionista = result.getBoolean("test_prof");
			if (result.wasNull()) {
				isProfessionista = false;
			}
			
			if (isProfessionista) {
				String sqlAutocert = """
select CATEGORIA, SOTTOCATEGORIA, COLLEGGIO, NUMERO_ISCRIZIONE, ID_PROVINCIA_ISCRIZIONE, PEC
from foliage2.FLGAUTOCERT_PROF_TAB
where ID_UTENTE = :idUtente
	and FLAG_VALIDO""";
				HashMap<String, Object> pars2 = new HashMap<>();
				pars2.put("idUtente", idUtente);
				final SqlRowSet result2 = queryForRowSet(sqlAutocert, pars2);
				
				if (result2.next()) {
					autocertProf = new AutocertificazioneProfessionista() {{
						categoria = result2.getString(1);
						sottocategoria = result2.getString(2);
						collegio = result2.getString(3);
						numeroIscrizione = result2.getString(4);
						provinciaIscrizione = result2.getInt(5);
						postaCertificata = result2.getString(6);
					}};
				}
				else {
					throw new FoliageException("Non è stata trovata un'autocertificazione valida");
				}
			}
			final AutocertificazioneProfessionista autoCertFin = autocertProf;
			final Boolean isProfessionistaFin = isProfessionista;
			java.sql.Date d = result.getDate("data_nascita");
			LocalDate dataNasc = (result.wasNull()) ? null : d.toLocalDate();

			outval = new DatiUtente() {{
				idUten = idUtente;
				nome = result.getString("nome");
				cognome = result.getString("cognome");
				userName = result.getString("user_name");
				codiceFiscale = result.getString("codi_fisc");
				flagAccettazione = result.getBoolean("flag_accettazione");
				if (result.wasNull()) {
					flagAccettazione = false;
				}
				dataDiNascita = dataNasc;
				luogoDiNascita = result.getString("luogo_nascita");
				genere = result.getString("sesso");

				provincia = result.getInt("id_provincia");
				if (result.wasNull()) {
					provincia = null;
				}

				comune = result.getInt("id_comune");
				if (result.wasNull()) {
					comune = null;
				}

				cap = result.getString("cap");
				indirizzo = result.getString("indirizzo");
				numeroCivico = result.getString("num_civico");
				// telefono = result.getString("telefono");
				// email = result.getString("email");
				// postaCertificata = result.getString("pec");
				rouloPredefinito = result.getInt("id_profilo_default");
				isProfessionistaForestale = isProfessionistaFin;
				autocertificazioneProf = autoCertFin;
			}};
		}
		return outval;
		//return result;
	}

	Base64FormioFile[] getBase64FormioFiles(Integer idFile) {
		return DbUtils.getBase64FormioFiles(this, idFile);

// 		if (idFile == null) {
// 			return null;
// 		}
// 		else {
// 			String sqlFile = """
// select FILE_NAME, ORIGINAL_FILE_NAME, FILE_SIZE, STORAGE,
// FILE_TYPE, HASH_FILE, FILE_DATA
// from FOLIAGE2.FLGBASE64_FORMIO_FILE_TAB
// where ID_FILE = :idFile
// 				""";
// 			Map<String, Object> mapFileParam = new HashMap<String, Object>();
// 			mapFileParam.put("idFile", idFile);
// 			SqlParameterSource filePars = new MapSqlParameterSource(mapFileParam);
// 			List<Base64FormioFile> outList = template.query(sqlFile, filePars, Base64FormioFile.RowMapper());
// 			Base64FormioFile[] arr = new Base64FormioFile[0];
// 			return outList.toArray(arr);
// 		}
	}

	public Object getRichiestaUtente(
		Integer idUtente,
		Integer idRichiesta, 
		Integer idUtenteExe,
		String authority,
		String authScope
	) throws SQLException, Exception {
		HashMap<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("idRichiesta", idRichiesta);
		mapParam.put("idUtente", idUtente);
		if (!idUtente.equals(idUtenteExe)) {
			if ("RESP".equals(authority)) {
				mapParam.put("idUtenteExe", idUtenteExe);
				mapParam.put("authority", authority);
				mapParam.put("authScope", authScope);
	
				String sqlPermesso = """
select exists (
	select *
	from foliage2.flgprof_tab p
		join foliage2.flgenti_profilo_tab ep using (id_profilo)
	where ep.id_ente = (
			select id_ente
			from flgrichieste_profili_tab fpt 
			where id_richiesta = :idRichiesta
				amd id_utente = :idUtente
		)
		and p.tipo_auth = :authority
		and p.tipo_ambito = :authScope
		and ep.id_utente = :idUtenteExe
)""";
				Boolean ck = queryForObject(
					sqlPermesso,
					mapParam, 
					(rs, rn) -> {
						return rs.getBoolean(1);
					}
				);
				if (!ck) {
					throw new FoliageAuthorizationException("Non autorizzato");
				}
	
				mapParam.remove("authority");
				mapParam.remove("authScope");
				mapParam.remove("idUtenteExe");
			}
			else {
				if (!"AMMI".equals(authority)) {
					throw new FoliageAuthorizationException("Non autorizzato");
				}
			}
		}

		String sql = """
select ID_PROFILO_RICHIESTO, E.TIPO_ENTE, E.NOME_ENTE, E.ID_ENTE, ESITO_APPROVAZIONE, 
	NOTE_RICHIESTA, NOTE_APPROVAZIONE,
	TIPO_NOMINA, NUMERO_PROTOCOLLO, DATA_PROTOCOLLO,
	RP.ESITO_APPROVAZIONE, RP.NOTE_RICHIESTA, RP.NOTE_APPROVAZIONE,
	RR.ID_RICHIESTA as ID_RICHIESTA_RESP,
	ID_FILE_ATTO_NOMINA, ID_FILE_DOC_IDENTITA,
	RP.DATA_APPROVAZIONE, RP.NOTE_REVOCA, RP.DATA_REVOCA
from FOLIAGE2.FLGRICHIESTE_PROFILI_TAB as RP
	join FOLIAGE2.FLGENTE_ROOT_TAB as E on (E.ID_ENTE = RP.ID_ENTE)
	left join FOLIAGE2.FLGRICHIESTE_RESPONSABILE_TAB as RR on (RR.ID_RICHIESTA = RP.ID_RICHIESTA)
where RP.ID_UTENTE  = :idUtente
	and RP.ID_RICHIESTA = :idRichiesta
	and RP.DATA_ANNULLAMENTO is null
				""";



		Object outVal = queryForObject(
				sql,
				mapParam,
				(rs, rowNum)-> {
					Integer idRichiestaResp = rs.getInt("id_richiesta_resp");
					if (rs.wasNull()) {
						idRichiestaResp = null;
					}
					Object richiestaResp = null;
					if (idRichiestaResp != null) {
						
						Base64FormioFile[] fileAttoNomina = null;
						Base64FormioFile[] fileDocIdentita = null;
						Integer idFileAttoNomina = rs.getInt("id_file_atto_nomina");
						if (rs.wasNull()) {
							idFileAttoNomina = null;
						}
						else {
							fileAttoNomina = DbUtils.getBase64FormioFiles(this, idFileAttoNomina);
						}
						Integer idFileDocIdentita = rs.getInt("id_file_doc_identita");
						if (rs.wasNull()) {
							idFileDocIdentita = null;
						}
						else {
							fileDocIdentita = DbUtils.getBase64FormioFiles(this, idFileDocIdentita);
						}
						final Base64FormioFile[] fileAttoNomina2 = fileAttoNomina;
						final Base64FormioFile[] fileDocIdentita2 = fileDocIdentita;
						richiestaResp = new Object() {
							public Integer tipoDiNomina = rs.getInt("tipo_nomina");
							public String numeroDiProtocollo = rs.getString("numero_protocollo");
							public Date dataProtocollo = rs.getDate("data_protocollo");
							public Base64FormioFile[] attoDiNomina = fileAttoNomina2;
							public Base64FormioFile[] documentoDiIdentita = fileDocIdentita2;
						};
					}
					Boolean esitoApp = rs.getBoolean("esito_approvazione");
					if (rs.wasNull()) {
						esitoApp = null;
					}
					final Boolean esitoApp2 = esitoApp;
					final Object richiestaResp2 = richiestaResp;
					final Timestamp dataVal = rs.getTimestamp("data_approvazione");
					final Timestamp dataRev = rs.getTimestamp("data_revoca");


					Object o = new Object(){
						public Object datiResponsabile = richiestaResp2;
						public Integer ruoloRichiesto = rs.getInt("id_profilo_richiesto");
						public String tipoEnte = rs.getString("tipo_ente");
						public String nomeEnte = rs.getString("nome_ente");
						public Integer idEnte = rs.getInt("id_ente");
						public String noteRichiesta = rs.getString("note_richiesta");
						public LocalDateTime dataValutazione = (dataVal == null) ? null : dataVal.toLocalDateTime();
						public LocalDateTime dataRevoca = (dataRev == null) ? null : dataRev.toLocalDateTime();
						public String noteRevoca = rs.getString("note_revoca");
						public Boolean esitoApprovazione = esitoApp2;
						public String noteApprovazione = rs.getString("note_approvazione");
					};

					return o;
				}
			);

		return outVal;
	}



	public Object getRichiesta(
		Integer idRichiesta,
		Integer idUtente,
		String authority,
		String authScope
	) throws SQLException, Exception {
		HashMap<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("idRichiesta", idRichiesta);


		if ("RESP".equals(authority)) {
			mapParam.put("idUtente", idUtente);
			mapParam.put("authority", authority);
			mapParam.put("authScope", authScope);

			String sqlPermesso = """
select exists (
	select *
	from foliage2.flgprof_tab p
		join foliage2.flgenti_profilo_tab ep using (id_profilo)
	where ep.id_ente = (
			select id_ente
			from foliage2.flgrichieste_profili_tab fpt 
			where id_richiesta = :idRichiesta
		)
		and p.tipo_auth = :authority
		and p.tipo_ambito = :authScope
		and ep.id_utente = :idUtente
)""";
			Boolean ck = queryForObject(
				sqlPermesso,
				mapParam, 
				(rs, rn) -> {
					return rs.getBoolean(1);
				}
			);
			if (!ck) {
				throw new FoliageAuthorizationException("Non autorizzato");
			}

			mapParam.remove("authority");
			mapParam.remove("authScope");
		}
		else {
			if (!"AMMI".equals(authority)) {
				throw new FoliageAuthorizationException("Non autorizzato");
			}
		}

		String sql = """
select ID_PROFILO_RICHIESTO, RP.ID_ENTE, E.TIPO_ENTE, E.NOME_ENTE, ESITO_APPROVAZIONE, 
	NOTE_RICHIESTA, NOTE_APPROVAZIONE,
	TIPO_NOMINA, NUMERO_PROTOCOLLO, DATA_PROTOCOLLO,
	RP.ESITO_APPROVAZIONE, RP.NOTE_RICHIESTA, RP.NOTE_APPROVAZIONE,
	RR.ID_RICHIESTA as ID_RICHIESTA_RESP,
	ID_FILE_ATTO_NOMINA, ID_FILE_DOC_IDENTITA, U.USER_NAME
from FOLIAGE2.FLGRICHIESTE_PROFILI_TAB as RP
	join FOLIAGE2.FLGENTE_ROOT_TAB as E on (E.ID_ENTE = RP.ID_ENTE)
	left join FOLIAGE2.FLGRICHIESTE_RESPONSABILE_TAB as RR on (RR.ID_RICHIESTA = RP.ID_RICHIESTA)
	left join FOLIAGE2.FLGUTEN_TAB as U on (U.ID_UTEN = RP.ID_UTENTE)
where RP.ID_RICHIESTA = :idRichiesta
	and RP.DATA_ANNULLAMENTO is null
				""";

		Object outVal = queryForObject(
				sql,
				mapParam,
				(rs, rowNum)-> {
					Integer idRichiestaResp = rs.getInt("id_richiesta_resp");
					if (rs.wasNull()) {
						idRichiestaResp = null;
					}
					Object richiestaResp = null;
					if (idRichiestaResp != null) {
						
						Base64FormioFile[] fileAttoNomina = null;
						Base64FormioFile[] fileDocIdentita = null;
						Integer idFileAttoNomina = rs.getInt("id_file_atto_nomina");
						if (rs.wasNull()) {
							idFileAttoNomina = null;
						}
						else {
							fileAttoNomina = DbUtils.getBase64FormioFiles(this, idFileAttoNomina);
						}
						Integer idFileDocIdentita = rs.getInt("id_file_doc_identita");
						if (rs.wasNull()) {
							idFileDocIdentita = null;
						}
						else {
							fileDocIdentita = DbUtils.getBase64FormioFiles(this, idFileDocIdentita);
						}
						final Base64FormioFile[] fileAttoNomina2 = fileAttoNomina;
						final Base64FormioFile[] fileDocIdentita2 = fileDocIdentita;
						richiestaResp = new Object() {
							public Integer tipoDiNomina = rs.getInt("tipo_nomina");
							public String numeroDiProtocollo = rs.getString("numero_protocollo");
							public Date dataProtocollo = rs.getDate("data_protocollo");
							public Base64FormioFile[] attoDiNomina = fileAttoNomina2;
							public Base64FormioFile[] documentoDiIdentita = fileDocIdentita2;
						};
					}
					Boolean esitoApp = rs.getBoolean("esito_approvazione");
					if (rs.wasNull()) {
						esitoApp = null;
					}
					final Boolean esitoApp2 = esitoApp;
					final Object richiestaResp2 = richiestaResp;
					Object o = new Object(){
						public String username = rs.getString("user_name");
						public Object datiResponsabile = richiestaResp2;
						public Integer ruoloRichiesto = rs.getInt("id_profilo_richiesto");
						public String tipoEnte = rs.getString("tipo_ente");
						public String nomeEnte = rs.getString("nome_ente");
						public Integer idEnte = rs.getInt("id_ente");
						public String noteRichiesta = rs.getString("note_richiesta");
						public Boolean esitoApprovazione = esitoApp2;
						public String noteApprovazione = rs.getString("note_approvazione");
					};

					return o;
				}
			);

		return outVal;
	}

	public Object cancelRichiestaUtente(Integer idUtente, Integer idRichiesta) throws SQLException, Exception {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			Map<String, Object> mapParam = new HashMap<String, Object>();
			mapParam.put("idUtente", idUtente);
			mapParam.put("idRichiesta", idRichiesta);
			//SqlParameterSource parameters = new MapSqlParameterSource(mapParam);

			String sql = """
update FOLIAGE2.FLGRICHIESTE_PROFILI_TAB
set DATA_ANNULLAMENTO = localtimestamp
where ID_UTENTE  = :idUtente
	and ID_RICHIESTA = :idRichiesta
	and DATA_ANNULLAMENTO is null
				""";

			int res = update(sql, mapParam);

			platformTransactionManager.commit(status);
		}catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}

		return "Ok";
	}


	public Object revocaAssociazioneRuoloEnte(Integer idUtenteExe, Integer idUtenteRevoca, Integer idProfilo, Integer idEnte, String note) throws SQLException, Exception {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			Map<String, Object> mapParam = new HashMap<String, Object>();
			mapParam.put("idUtenteRevoca", idUtenteRevoca);
			mapParam.put("idUtenteExe", idUtenteExe);
			mapParam.put("idProfilo", idProfilo);
			mapParam.put("idEnte", idEnte);
			mapParam.put("note", note);
			SqlParameterSource parameters = new MapSqlParameterSource(mapParam);

			String sqlDelAss = """
DELETE FROM foliage2.flgenti_profilo_tab
WHERE id_utente = :idUtenteRevoca
	AND id_profilo = :idProfilo
	AND id_ente = :idEnte
		""";
			int res1 = template.update(sqlDelAss, parameters);

			String sqlUpdRich = """
update foliage2.FLGRICHIESTE_PROFILI_TAB 
set ID_UTENTE_REVOCA = :idUtenteExe,
	DATA_REVOCA = localtimestamp,
	NOTE_REVOCA = :note
WHERE id_utente = :idUtenteRevoca
	AND ID_PROFILO_RICHIESTO = :idProfilo
	AND id_ente = :idEnte
	and FLAG_RICHIESTA_VALIDA
returning ID_RICHIESTA
		""";
			Integer idRichiesta = template.queryForObject(
					sqlUpdRich,
					parameters,
					(rs, rn) -> {
						Integer outVal = rs.getInt("ID_RICHIESTA");
						return outVal;
					}
			);


			String sqlDelRuol = """
DELETE FROM foliage2.flgprofili_utente_tab
WHERE id_utente = :idUtenteRevoca
	AND id_profilo = :idProfilo
	and not exists (
		select *
		FROM foliage2.flgenti_profilo_tab
		WHERE id_utente = :idUtenteRevoca
			AND id_profilo = :idProfilo
	)	
		""";
			int res2 = template.update(sqlDelRuol, parameters);

			inserisciNotificaUtente(
					idUtenteRevoca,
					"È stata revocata un'abilitazione dal tuo profilo!",
					String.format("/account/richieste/%d", idRichiesta)
			);

			platformTransactionManager.commit(status);
		}catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}

		return "Ok";
	}

	public Object revocaAssociazioneRuoloEnte(Integer idUtente, String username, Integer idProfilo, Integer idEnte, String note) throws SQLException, Exception {
		return revocaAssociazioneRuoloEnte(idUtente, getIdUtente(username), idProfilo, idEnte, note);
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

	public Integer getIdUtente(String username) throws SQLException, Exception {
		Map<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("username", username);
		SqlParameterSource pars = new MapSqlParameterSource(mapParam);

		String sql = """
select ID_UTEN
from FOLIAGE2.FLGUTEN_TAB
where USER_NAME = :username
				""";

		return template.queryForObject(
			sql,
			pars,
			(rs, rn) -> {
				return rs.getInt(1);
			}
		);
	}
	public Object getRichiestaUtente(
		String username,
		Integer idRichiesta, 
		Integer idUtente,
		String authority,
		String authScope
	) throws SQLException, Exception {
		return getRichiestaUtente(getIdUtente(username), idRichiesta, idUtente, authority, authScope);
	}

	public Object getMieiEntiPerRuolo(Integer idUtente, Integer idProfilo) throws SQLException, Exception {
		Map<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("idUtente", idUtente);
		mapParam.put("idProfilo", idProfilo);
		SqlParameterSource parameters = new MapSqlParameterSource(mapParam);

		String sql = """
select E.ID_ENTE, E.TIPO_ENTE, E.NOME_ENTE
from FOLIAGE2.FLGENTI_PROFILO_TAB as EP
	join FOLIAGE2.FLGENTE_ROOT_TAB as E on (E.ID_ENTE = EP.ID_ENTE)
where ID_UTENTE = :idUtente
	and ID_PROFILO = :idProfilo""";
		Object outVal = template.query(
				sql,
				parameters,
				(rs, rowNum)-> {
					Object o = new Object(){
						public Integer idEnte = rs.getInt("id_ente");
						public String tipo = rs.getString("tipo_ente");
						public String nome = rs.getString("nome_ente");
					};

					return o;
				}
			);

		return outVal;
	}
	public Object getEntiPerRuoloUtente(Integer idUtenteReq, String authority, String authScope, Integer idUtente, Integer idProfilo) throws SQLException, Exception {

		HashMap<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("idUtente", idUtente);
		mapParam.put("idProfilo", idProfilo);

		String sql = """
select E.ID_ENTE, E.TIPO_ENTE, E.NOME_ENTE
from FOLIAGE2.FLGENTI_PROFILO_TAB as EP
	join FOLIAGE2.FLGENTE_ROOT_TAB as E on (E.ID_ENTE = EP.ID_ENTE)
where ID_UTENTE = :idUtente
	and ID_PROFILO = :idProfilo""";

		if ("RESP".equals(authority)) {
			mapParam.put("idUtenteReq", idUtenteReq);
			mapParam.put("authScope", authScope);
			sql = """
select E.ID_ENTE, E.TIPO_ENTE, E.NOME_ENTE
from FOLIAGE2.FLGENTI_PROFILO_TAB as EP
	join FOLIAGE2.FLGENTE_ROOT_TAB as E on (E.ID_ENTE = EP.ID_ENTE)
where ID_UTENTE = :idUtente
	and ID_PROFILO = :idProfilo
	and EP.ID_ENTE in (
		select EP2.ID_ENTE
		from FOLIAGE2.FLGENTI_PROFILO_TAB as EP2
			join FOLIAGE2.FLGPROF_TAB P2 on (EP2.ID_PROFILO = P2.ID_PROFILO)
		where EP2.ID_UTENTE = :idUtenteReq
			and P2.TIPO_AUTH = 'RESP'
			and P2.TIPO_AMBITO = :authScope
	)""";
		}

		if ("DIRI".equals(authority)) {
			mapParam.put("authScope", authScope);
			sql = """
select E.ID_ENTE, E.TIPO_ENTE, E.NOME_ENTE
from FOLIAGE2.FLGENTI_PROFILO_TAB as EP
	join FOLIAGE2.FLGENTE_ROOT_TAB as E on (E.ID_ENTE = EP.ID_ENTE)
	join FOLIAGE2.FLGPROF_TAB P on (EP.ID_PROFILO = P.ID_PROFILO)
where ID_UTENTE = :idUtente
	and P.TIPO_AUTH = 'ISTR'
	and ID_PROFILO = :idProfilo
	and EP.ID_ENTE in (
		select EP2.ID_ENTE
		from FOLIAGE2.FLGENTI_PROFILO_TAB as EP2
			join FOLIAGE2.FLGPRIF_TAB P2 on (EP2.ID_PROFILO = P2.ID_PROFILO)
		where EP2.ID_ENTE = EP.ID_ENTE
			and P2.TIPO_AUTH = 'DIRI'
			and P2.TIPO_AMBITO = :authScope
	)""";
		}


		List<Object> outVal = query(
				sql,
				mapParam,
				(RowMapper<Object>)(rs, rowNum)-> {
					Object o = new Object(){
						public Integer idEnte = rs.getInt("id_ente");
						public String tipo = rs.getString("tipo_ente");
						public String nome = rs.getString("nome_ente");
					};
					return o;
				}
			);

		return outVal;
	}

	public Object getEntiPerRuoloUtente(
		Integer idUtenteReq,
		String authority,
		String authScope,
		String username,
		Integer idRichiesta
	) throws SQLException, Exception {
		return this.getEntiPerRuoloUtente(idUtenteReq, authority, authScope, getIdUtente(username), idRichiesta);
	}


	public ResultSet getNotificheUtente(Integer idUtente, Integer maxRows) throws SQLException, Exception {

		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select ID_NOTIFICA, TESTO, DATA_NOTIFICA, DATA_LETTURA, LINK
from FOLIAGE2.FLGNOTIFICHE_TAB as N
where N.ID_UTENTE = ?
order by N.FLAG_LETTA, DATA_NOTIFICA desc
limit ?
					"""
					);
				statement.setInt(1, idUtente);
				statement.setInt(2, maxRows);
				return statement.executeQuery();
			}
		);
		return result;
	}


	public int segnaLetturaNotifica(Integer idUtente, Integer idNotifica) throws SQLException, Exception {

		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			HashMap<String, Object> mapParam = new HashMap<String, Object>();
			mapParam.put("idUtente", idUtente);
			mapParam.put("idNotifica", idNotifica);

			SqlParameterSource updPars = new MapSqlParameterSource(mapParam);

			String sqlInsRich = """
update FOLIAGE2.FLGNOTIFICHE_TAB
set DATA_LETTURA = localtimestamp
where ID_UTENTE = :idUtente
	and ID_NOTIFICA = :idNotifica
				""";

			int returnValue = template.update(
					sqlInsRich,
					updPars
			);
			platformTransactionManager.commit(status);
			return returnValue;
		}catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
	}
	public ResultSet getAllRichiesteUtente(Integer idUtente) throws SQLException, Exception {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select ID_RICHIESTA, RP.DATA_RICHIESTA, U.ID_UTEN, U.USER_NAME, U.CODI_FISC, P.ID_PROFILO, P.DESCRIZIONE as PROFILO,
E.ID_ENTE, E.TIPO_ENTE, E.NOME_ENTE,
RP.DATA_APPROVAZIONE, RP.ESITO_APPROVAZIONE, RP.ID_UTENTE_APPROVAZIONE, UA.USER_NAME as USER_APPROVAZIONE, UA.CODI_FISC as COD_FISC_APPROVAZIONE,
RP.DATA_REVOCA
from FOLIAGE2.FLGRICHIESTE_PROFILI_TAB as RP
left join FOLIAGE2.FLGUTEN_TAB as U on (RP.ID_UTENTE = U.ID_UTEN)
left join FLGPROF_TAB as P on (P.ID_PROFILO = RP.ID_PROFILO_RICHIESTO)
left join FLGENTE_ROOT_TAB as E on (E.ID_ENTE = RP.ID_ENTE)
left join FLGUTEN_TAB as UA on (RP.ID_UTENTE_APPROVAZIONE = UA.ID_UTEN)
where RP.ID_UTENTE = ?
and RP.DATA_ANNULLAMENTO is null
order by RP.DATA_RICHIESTA desc, ID_RICHIESTA desc
					"""
					);
				statement.setInt(1, idUtente);
				return statement.executeQuery();
			}
		);
		return result;
	}

	public ResultSet getRichiesteUtente(Integer idUtenteReq, String authority, String authScope, Integer idUtente) throws SQLException, Exception {
		ResultSet result = null;
		switch (authority) {
			case "AMMI": {
				result = getAllRichiesteUtente(idUtente);
			}; break;
			case "RESP": {
				result = this.GetResult(
					(conn) -> {
						PreparedStatement statement = conn.prepareStatement("""
select ID_RICHIESTA, RP.DATA_RICHIESTA, U.ID_UTEN, U.USER_NAME, U.CODI_FISC, P.ID_PROFILO, P.DESCRIZIONE as PROFILO,
	E.ID_ENTE, E.TIPO_ENTE, E.NOME_ENTE,
	RP.DATA_APPROVAZIONE, RP.ESITO_APPROVAZIONE, RP.ID_UTENTE_APPROVAZIONE, UA.USER_NAME as USER_APPROVAZIONE, UA.CODI_FISC as COD_FISC_APPROVAZIONE,
	RP.DATA_REVOCA
from FOLIAGE2.FLGRICHIESTE_PROFILI_TAB as RP
	left join FOLIAGE2.FLGUTEN_TAB as U on (RP.ID_UTENTE = U.ID_UTEN)
	left join FLGPROF_TAB as P on (P.ID_PROFILO = RP.ID_PROFILO_RICHIESTO)
	left join FLGENTE_ROOT_TAB as E on (E.ID_ENTE = RP.ID_ENTE)
	left join FLGUTEN_TAB as UA on (RP.ID_UTENTE_APPROVAZIONE = UA.ID_UTEN)
where RP.ID_UTENTE = ?
	and RP.DATA_ANNULLAMENTO is null
	and E.ID_ENTE in (
			select ep2.ID_ENTE
			from FOLIAGE2.flgenti_profilo_tab ep2
				join FOLIAGE2.flgprof_tab p2 on (p2.id_profilo = ep2.id_profilo)
			where p2.tipo_auth = 'RESP'
				and p2.tipo_ambito = ?
				and ep2.ID_UTENTE = ?
		)
order by RP.DATA_RICHIESTA desc, ID_RICHIESTA desc
							"""
							);
						statement.setInt(1, idUtente);
						statement.setString(2, authScope);
						statement.setInt(3, idUtenteReq);
						return statement.executeQuery();
					}
				);
			}; break;
			default: {
				throw new FoliageAuthorizationException("Non autorizzato");
			}
		}
		return result;
	}

	public ResultSet getRichiesteUtente(Integer idUtenteReq, String authority, String authScope, String username) throws SQLException, Exception {
		return getRichiesteUtente(idUtenteReq, authority, authScope, getIdUtente(username));
	}


	public Object getListaUtenti(
		Integer idUtente,
		String profilo,
		String tipoAmbito,
		RicercaUtenti parametri
	) throws SQLException, Exception {
		//TODO: gestire i filtri in input
		HashMap<String, Object> pars = new HashMap<>();
		LinkedList<String> conditions = new LinkedList<>();
		
		String user = parametri.getUsername();
		String codFisc = parametri.getCodiceFiscale();
		//parametri.getAmbitoTerritoriale();
		Integer ente = parametri.getCodiceEnteTerritoriale();
		Integer idProfilo = parametri.getIdProfilo();
		String baseQueryPattern = """
SELECT id_uten, nome, cognome,
	user_name, codi_fisc, flag_accettazione,
	data_ins, data_upd, flag_attivo
FROM foliage2.flguten_tab as u
where id_uten > 0%s""";
		switch (profilo) {
			case "PROF":
			case "PROP": {
				if ((user == null || user.equals("")) && (codFisc == null || codFisc.equals(""))) {
					throw new FoliageAuthorizationException("Occorre definire codice fiscale o username tra i parametri di ricerca");
				}
				conditions.addLast(
					"""
exists (
		select *
		from foliage2.flgprofili_utente_tab pu
			join foliage2.flgprof_tab p on (p.id_profilo = pu.id_profilo)
		where p.tipo_auth = 'PROF'
			and pu.id_utente = u.id_uten
	)"""
				);

			}; break;
			case "DIRI": {
				conditions.addLast(
					"""
exists (
		select *
		from foliage2.flgenti_profilo_tab ep1
			join foliage2.flgprof_tab p on (p.id_profilo = ep1.id_profilo)
			join foliage2.flgenti_profilo_tab ep2 on (ep2.id_ente = ep1.id_ente)
			join foliage2.flgprof_tab p2 on (p2.id_profilo = ep2.id_profilo and p2.tipo_ambito = p.tipo_ambito)
		where p.tipo_auth = 'ISTR'
			and p2.tipo_auth = 'DIRI'
			and ep2.id_utente = :idUtente
			and ep1.id_utente = u.id_uten
	)"""
				);
				pars.put("idUtente", idUtente);
			}; break;
			case "RESP": {
				conditions.addLast(
					"""
exists (
		select *
		from foliage2.flgenti_profilo_tab ep1
			join foliage2.flgprof_tab p on (p.id_profilo = ep1.id_profilo)
			join foliage2.flgenti_profilo_tab ep2 on (ep2.id_ente = ep1.id_ente)
		where ep2.id_utente = :idUtente
			and p.tipo_ambito = :tipoAmbito
			and ep1.id_utente = u.id_uten
	)"""
				);
				pars.put("idUtente", idUtente);
				pars.put("tipoAmbito", tipoAmbito);
			}; break;
			case "AMMI": {

			}; break;
		}


		if (user != null && !user.equals("")) {
			conditions.addLast("u.user_name = :user");
			pars.put("user", user);
		}
		if (codFisc != null && !codFisc.equals("")) {
			conditions.addLast("u.codi_fisc = :codFisc");
			pars.put("codFisc", codFisc);
		}
		if (idProfilo == null) {
			if (ente != null && ente != -1) {
				conditions.addLast("""
exists (
		select *
		from foliage2.flgenti_profilo_tab ep1
		where ep1.id_utente = u.id_uten
			and ep1.id_ente = :ente
	)"""
					);
				pars.put("ente", ente);
			}
		}
		else {
			pars.put("idProfilo", idProfilo);
			if (ente == null || ente == -1) {
				conditions.addLast("""
exists (
		select *
		from foliage2.flgprofili_utente_tab pu
		where pu.id_utente = u.id_uten
			and pu.id_profilo = :idProfilo
	)"""
					);
			}
			else {
				conditions.addLast("""
exists (
		select *
		from foliage2.flgenti_profilo_tab ep1
		where ep1.id_utente = u.id_uten
			and ep1.id_ente = :ente
			and ep1.id_profilo = :idProfilo
	)"""
					);
				pars.put("ente", ente);
			}
		}



		String whereCondition = (conditions.size() > 0 )
			? String.format(
				"""

	and %s""",
				conditions.stream().map(s -> s.replaceAll("\n", "\n\t\t")).collect(Collectors.joining("\n\t\tand "))
			)
			:
				"";
		String query = String.format(baseQueryPattern, whereCondition);
		log.debug(query);
		return query(
			query, pars, 
			(rs, rn) -> {
				java.sql.Date d = rs.getDate("data_ins");
				LocalDate di = (d == null) ? null : d.toLocalDate();
				d = rs.getDate("data_upd");
				LocalDate du = (d == null) ? null : d.toLocalDate();
				return new Object() {
					public Integer id_uten = rs.getInt("id_uten");
					public String user_name = rs.getString("user_name");
					public String codi_fisc = rs.getString("codi_fisc");
					public String nome = rs.getString("nome");
					public String cognome = rs.getString("cognome");
					public Boolean flag_accettazione = rs.getBoolean("flag_accettazione");
					public LocalDate data_ins = di;
					public LocalDate data_upd = du;
				};
			}
		);
	}

	public String effettuaAccettazionePrivacy(String username) {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			HashMap<String, Object> mapParam = new HashMap<String, Object>();
			mapParam.put("username", username);
			SqlParameterSource parameters = new MapSqlParameterSource(mapParam);
			String sql = """
update foliage2.flguten_tab
set flag_accettazione = true
where user_name = :username 
				""";
			template.update(sql, parameters);
			platformTransactionManager.commit(status);
		}catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}

		return "OK";
	}


// 	private Integer caricaBase64FormioFile(Base64FormioFile file) {
				
// 		String sqlInsFile = """
// insert into FOLIAGE2.FLGBASE64_FORMIO_FILE_TAB(FILE_NAME, ORIGINAL_FILE_NAME, FILE_SIZE, STORAGE, FILE_TYPE, HASH_FILE, FILE_DATA)
// values (:fileName, :originalName, :fileSize, :storage, :type, :hash, :data) returning ID_FILE
// 				""";
		
// 		HashMap<String, Object> mapParamFile = new HashMap<String, Object>();
// 		mapParamFile.put("fileName", file.getName());
// 		mapParamFile.put("originalName", file.getOriginalName());
// 		mapParamFile.put("fileSize", file.getSize());
// 		mapParamFile.put("storage", file.getStorage());
// 		mapParamFile.put("type", file.getType());
// 		mapParamFile.put("hash", file.getHash());
// 		mapParamFile.put("data", file.getUrl().getBytes());
		
// 		return template.queryForObject(
// 			sqlInsFile,
// 			new MapSqlParameterSource(mapParamFile), 
// 			(rs, rowNum)-> {	
// 				Integer r = Integer.valueOf(rs.getInt(1));
// 				return r;
// 			}
// 		);
// 	}

	public String nuovaRichiestaProfilo(Integer idUtente, RichiestaProfilo richiesta) throws Exception {

		// TODO: transact da non mettere sui metodi che hanno solo select
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			HashMap<String, Object> mapParam = new HashMap<String, Object>();
			mapParam.put("idUtente", idUtente);
			mapParam.put("idProfilo", richiesta.getRuoloRichiesto());
			mapParam.put("idEnte", richiesta.getIdEnte());
			mapParam.put("note", richiesta.getNoteRichiesta());

			SqlParameterSource parInsRich = new MapSqlParameterSource(mapParam);

			String sqlInsRich = """
insert into FOLIAGE2.FLGRICHIESTE_PROFILI_TAB(ID_UTENTE, ID_PROFILO_RICHIESTO, ID_ENTE, DATA_RICHIESTA, NOTE_RICHIESTA)
	values (:idUtente, :idProfilo, :idEnte, LOCALTIMESTAMP, :note) RETURNING ID_RICHIESTA
					""";

			Integer idRich  = template.queryForObject(
				sqlInsRich,
				parInsRich,
				(rs, rowNum)-> {
					Integer r=  Integer.valueOf(rs.getInt(1));
					return r;
				}
			);
			DatiRichiestaResponsabile datiResp = richiesta.getDatiResponsabile();
			if (datiResp != null) {

				mapParam.remove("idProfilo");
				mapParam.remove("idEnte");

				Base64FormioFile[] attoNomina = datiResp.getAttoDiNomina();
				Base64FormioFile[] docIdentita = datiResp.getDocumentoDiIdentita();
				Integer idFileAttoNomina  = DbUtils.saveBase64FormioFiles(this, attoNomina);
				Integer idFileDocIdentita = DbUtils.saveBase64FormioFiles(this, docIdentita);


				String sqlInsResp = """
insert into FOLIAGE2.FLGRICHIESTE_RESPONSABILE_TAB(ID_RICHIESTA, TIPO_NOMINA, NUMERO_PROTOCOLLO, DATA_PROTOCOLLO, ID_FILE_ATTO_NOMINA, ID_FILE_DOC_IDENTITA)
	values (:idRichiesta, :tipoNomina, :numeroProtocollo, :dataProtocollo, :idFileAttoNomina, :idFileDocIdentita)
""";

				mapParam.put("idRichiesta", idRich);
				mapParam.put("tipoNomina", datiResp.getTipoDiNomina());
				mapParam.put("numeroProtocollo", datiResp.getNumeroDiProtocollo());
				mapParam.put("dataProtocollo", datiResp.getDataProtocollo());
				mapParam.put("idFileAttoNomina", idFileAttoNomina);
				mapParam.put("idFileDocIdentita", idFileDocIdentita);

				SqlParameterSource parInsResp = new MapSqlParameterSource(mapParam);
				template.update(sqlInsResp, parInsResp);
			}


			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}

		return "OK";
	}

	public Object valutaRichiestaProfilo(
		Integer idUtenteVal,
		String authority, 
		String authScope,
		Integer idRichiesta,
		ValutazioneRichiestaProfilo valutazione
	) throws FoliageAuthorizationException
	{
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			///TODO: vanno fatte le verifiche sul profilo e migliorati i controlli per capire se la richiesta sia già approvata o annullata
			HashMap<String, Object> mapParam = new HashMap<String, Object>();
			mapParam.put("idRichiesta", idRichiesta);
			mapParam.put("idUtenteVal", idUtenteVal);
			if ("RESP".equals(authority)) {
				mapParam.put("authority", authority);
				mapParam.put("authScope", authScope);

				String sqlPermesso = """
select exists (
		select *
		from foliage2.flgprof_tab p
			join foliage2.flgenti_profilo_tab ep using (id_profilo)
		where ep.id_ente = (
				select id_ente
				from flgrichieste_profili_tab fpt 
				where id_richiesta = :idRichiesta
			)
			and p.tipo_auth = :authority
			and p.tipo_ambito = :authScope
			and ep.id_utente = :idUtenteVal
	)""";
				Boolean ck = queryForObject(
					sqlPermesso,
					mapParam, 
					(rs, rn) -> {
						return rs.getBoolean(1);
					}
				);
				if (!ck) {
					throw new FoliageAuthorizationException("Non autorizzato");
				}

				mapParam.remove("authority");
				mapParam.remove("authScope");
			}
			else {
				if (!"AMMI".equals(authority)) {
					throw new FoliageAuthorizationException("Non autorizzato");
				}
			}
			mapParam.put("esito", valutazione.getEsito());
			mapParam.put("note", valutazione.getNote());

			String sql = """
update FOLIAGE2.FLGRICHIESTE_PROFILI_TAB
set DATA_APPROVAZIONE = localtimestamp,
	ESITO_APPROVAZIONE = :esito,
	ID_UTENTE_APPROVAZIONE = :idUtenteVal,
	NOTE_APPROVAZIONE = :note
where ID_RICHIESTA = :idRichiesta
	and DATA_ANNULLAMENTO is null
	and ESITO_APPROVAZIONE is null
	and FLAG_RICHIESTA_VALIDA
returning ID_UTENTE
			""";

			Integer idUtente = queryForObject(
				sql,
				mapParam,
				(rs, rn) -> {
					Integer v1 = rs.getInt(1);
					if (rs.wasNull()) {
						v1 = null;
					}
					return v1;
				}
			);

			sql = """
insert into FOLIAGE2.FLGPROFILI_UTENTE_TAB(ID_UTENTE, ID_PROFILO, FLAG_SENIOR)
select ID_UTENTE, ID_PROFILO_RICHIESTO, (ID_PROFILO_RICHIESTO = 8) as FLAG_SENIOR
from FOLIAGE2.FLGRICHIESTE_PROFILI_TAB as RP
where RP.ID_RICHIESTA = :idRichiesta
	and RP.ESITO_APPROVAZIONE = true
	and (ID_UTENTE, ID_PROFILO_RICHIESTO) not in (
		select PU.ID_UTENTE, PU.ID_PROFILO
		from FOLIAGE2.FLGPROFILI_UTENTE_TAB as PU
	)""";
			update(sql, mapParam);

			sql = """
					insert into FOLIAGE2.FLGENTI_PROFILO_TAB(ID_UTENTE, ID_PROFILO, ID_ENTE)
					select ID_UTENTE, ID_PROFILO_RICHIESTO, ID_ENTE
					from FOLIAGE2.FLGRICHIESTE_PROFILI_TAB as RP
					where RP.ID_RICHIESTA = :idRichiesta
						and RP.ESITO_APPROVAZIONE = true
						and RP.ID_ENTE is not null
						and (ID_UTENTE, ID_PROFILO_RICHIESTO, ID_ENTE) not in (
							select EP.ID_UTENTE, EP.ID_PROFILO, EP.ID_ENTE
							from FOLIAGE2.FLGENTI_PROFILO_TAB as EP
						)
									""";
			update(sql, mapParam);

			String mess = (valutazione.getEsito())
					? "È stata accettata una richiesta di abilitazione per il tuo profilo"
					: "Non è stata accettata una richiesta di abilitazione per il tuo profilo";

			inserisciNotificaUtente(
					idUtente,
					mess,
					String.format("/account/richieste/%d", idRichiesta)
			);

			platformTransactionManager.commit(status);
		}catch (Exception e) {
				platformTransactionManager.rollback(status);
				throw e;
			}
		finally {
				status = null;
			}

		return "";
	}

	public Object getRichiesteProfili(
		Integer idUtente, 
		String authority,
		String ambito,
		RicercaUtenti parametri
	) throws Exception {
		HashMap<String, Object> parameters = new HashMap<>();
		LinkedList<String> conditions = new LinkedList<>();
		String queryBase = """
select ID_RICHIESTA, RP.DATA_RICHIESTA, U.ID_UTEN, U.USER_NAME, U.CODI_FISC, P.ID_PROFILO, P.DESCRIZIONE as PROFILO,
	E.ID_ENTE, E.TIPO_ENTE, E.NOME_ENTE,
	RP.DATA_REVOCA,
	RP.DATA_APPROVAZIONE, RP.ESITO_APPROVAZIONE, RP.ID_UTENTE_APPROVAZIONE, UA.USER_NAME as USER_APPROVAZIONE, UA.CODI_FISC as COD_FISC_APPROVAZIONE
from FOLIAGE2.FLGRICHIESTE_PROFILI_TAB as RP
	left join FOLIAGE2.FLGUTEN_TAB as U on (RP.ID_UTENTE = U.ID_UTEN)
	left join FOLIAGE2.FLGPROF_TAB as P on (P.ID_PROFILO = RP.ID_PROFILO_RICHIESTO)
	left join FOLIAGE2.FLGENTE_ROOT_TAB as E on (E.ID_ENTE = RP.ID_ENTE)
	left join FOLIAGE2.FLGUTEN_TAB as UA on (RP.ID_UTENTE_APPROVAZIONE = UA.ID_UTEN)
where RP.DATA_ANNULLAMENTO is null%s
order by RP.DATA_RICHIESTA desc, ID_RICHIESTA desc""";

		switch (authority) {
			case "AMMI": {
			}; break;
			case "RESP": {
				parameters.put("idUtenteExe", idUtente);
				parameters.put("ambito", ambito);
				conditions.addLast(
					String.format(
						"""

	and exists (
		select *
		from foliage2.flgenti_profilo_tab ep1
			join foliage2.FLGPROF_TAB p1 using (ID_PROFILO)
		where ep1.id_ente = e.id_ente
			and ep1.id_utente = :idUtenteExe
			and p1.tipo_auth = 'RESP'
			and p1.tipo_ambito = :ambito
	)"""
					)
				);
			}; break;
			default: {
				throw new FoliageAuthorizationException("Profilo non abilitato");
			}
		}

		Integer idEnte = parametri.getCodiceEnteTerritoriale();
		String username = parametri.getUsername();
		String codFiscale = parametri.getCodiceFiscale();
		Integer idProfilo = parametri.getIdProfilo();


		if (idEnte != null && idEnte > 0) {
			parameters.put("idEnte", idEnte);
			conditions.addLast(
				String.format(
					"""

	and e.id_ente = :idEnte"""
				)
			);
		}


		if (username != null && !username.equals("")) {
			parameters.put("username", idProfilo);
			conditions.addLast(
				String.format(
					"""

	and U.USER_NAME = :username"""
				)
			);
		
		}

		if (codFiscale != null && !codFiscale.equals("")) {
			parameters.put("codFiscale", codFiscale);
			conditions.addLast(
				String.format(
					"""

	and U.CODI_FISC = :codFiscale"""
				)
			);
		
		}

		if (idProfilo != null && idProfilo > 0) {
			parameters.put("idProfilo", idProfilo);
			conditions.addLast(
				String.format(
					"""

	and p.ID_PROFILO = :idProfilo"""
				)
			);
		
		}

		String query = String.format(
			queryBase, 
			conditions.stream().collect(Collectors.joining(""))
		);

		return queryForRowSet(query, parameters);

// 		//TODO: gestire i filtri in input
// 		ResultSet result = this.GetResult(
// 			(conn) -> {
// 				PreparedStatement statement = conn.prepareStatement("""
// select ID_RICHIESTA, RP.DATA_RICHIESTA, U.ID_UTEN, U.USER_NAME, U.CODI_FISC, P.ID_PROFILO, P.DESCRIZIONE as PROFILO,
// 	E.ID_ENTE, E.TIPO_ENTE, E.NOME_ENTE,
// 	RP.DATA_REVOCA,
// 	RP.DATA_APPROVAZIONE, RP.ESITO_APPROVAZIONE, RP.ID_UTENTE_APPROVAZIONE, UA.USER_NAME as USER_APPROVAZIONE, UA.CODI_FISC as COD_FISC_APPROVAZIONE
// from FOLIAGE2.FLGRICHIESTE_PROFILI_TAB as RP
// 	left join FOLIAGE2.FLGUTEN_TAB as U on (RP.ID_UTENTE = U.ID_UTEN)
// 	left join FLGPROF_TAB as P on (P.ID_PROFILO = RP.ID_PROFILO_RICHIESTO)
// 	left join FLGENTE_ROOT_TAB as E on (E.ID_ENTE = RP.ID_ENTE)
// 	left join FLGUTEN_TAB as UA on (RP.ID_UTENTE_APPROVAZIONE = UA.ID_UTEN)
// where RP.DATA_ANNULLAMENTO is null
// order by RP.DATA_RICHIESTA desc, ID_RICHIESTA desc
// 					"""
// 					);
// 				return statement.executeQuery();
// 			}
// 		);
// 		return result;
	}


	public Object getRuoliUtente(
		String username
	) throws SQLException, Exception {
		return getRuoliUtente(getIdUtente(username));
	}
	public Object getRuoliUtente(
		Integer idUtente
	) throws SQLException, Exception {
		HashMap<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("idUtente", idUtente);
		
		String sql = """
			select PU.ID_PROFILO, DESCRIZIONE
			from FOLIAGE2.FLGPROFILI_UTENTE_TAB as PU
			left join FOLIAGE2.FLGPROF_TAB as P on (P.ID_PROFILO = PU.ID_PROFILO)
			where PU.ID_UTENTE = :idUtente
			order by PU.ID_PROFILO
				""";

		Object outVal = query(
				sql,
				mapParam,
				(rs, rowNum)-> {
					Object o = new Object(){
						public Integer idProfilo = rs.getInt(1);
						public String descrizione = rs.getString(2);
					};

					return o;
				}
			);
		return outVal;
	}

	interface JsonFunc{
		Object eval(JsonElement e);
	}

	public Object aggiornaProfiloDefault(Integer idUtente, Integer idProfilo) throws FoliageException {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			Map<String, Object> mapUteParam = new HashMap<String, Object>();
			mapUteParam.put("idUtente", idUtente);
			mapUteParam.put("idProfilo", idProfilo);
			SqlParameterSource updUtenParameters = new MapSqlParameterSource(mapUteParam);

			String sql1 = """
update foliage2.flgprofili_utente_tab
set flag_default = null
where id_utente = :idUtente
	and flag_default = true
				""";
			int nRows1 = template.update(sql1, updUtenParameters);

			String sql2 = """
update foliage2.flgprofili_utente_tab
set flag_default = true
where id_utente = :idUtente
	and id_profilo = :idProfilo
				""";
			int nRows2 = template.update(sql2, updUtenParameters);

			platformTransactionManager.commit(status);
		}catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}


		return "OK";
	}
	public Object aggiornaDatiUtente(Integer idUtente, JsonObject mods) throws FoliageException {

		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			JsonFunc stringFunc = (e) -> e.getAsString();
			JsonFunc intFunc = (e) -> e.getAsLong();
			//JsonFunc dateFunc = (e) -> LocalDate.parse(e.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			JsonFunc dateFunc2 = (e) -> LocalDate.parse(e.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);

			LinkedList<Pair<String, String>> updates = new LinkedList<>();
			LinkedList<Triplet<String, String, JsonFunc>>  uteCols = new LinkedList<>();

			uteCols.addLast(new Triplet<String, String, JsonFunc>("nome", "nome", stringFunc));
			uteCols.addLast(new Triplet<String, String, JsonFunc>("cognome", "cognome", stringFunc));
			uteCols.addLast(new Triplet<String, String, JsonFunc>("dataDiNascita", "data_nascita", dateFunc2));
			uteCols.addLast(new Triplet<String, String, JsonFunc>("luogoDiNascita", "luogo_nascita", stringFunc));
			uteCols.addLast(new Triplet<String, String, JsonFunc>("genere", "sesso", stringFunc));

			uteCols.addLast(new Triplet<String, String, JsonFunc>("comune", "id_comune", intFunc));
			uteCols.addLast(new Triplet<String, String, JsonFunc>("cap", "cap", stringFunc));
			uteCols.addLast(new Triplet<String, String, JsonFunc>("indirizzo", "indirizzo", stringFunc));
			uteCols.addLast(new Triplet<String, String, JsonFunc>("numeroCivico", "num_civico", stringFunc));
			//uteCols.addLast(new Triplet<String, String, JsonFunc>("citta", "citta", stringFunc));
			uteCols.addLast(new Triplet<String, String, JsonFunc>("telefono", "telefono", stringFunc));
			uteCols.addLast(new Triplet<String, String, JsonFunc>("email", "email", stringFunc));
			uteCols.addLast(new Triplet<String, String, JsonFunc>("postaCertificata", "pec", stringFunc));

			Map<String, Object> mapUteParam = new HashMap<String, Object>();


			int idxPars = 1;
			for (Triplet<String, String, JsonFunc> pair : uteCols) {
				JsonElement elem = mods.get(pair.getValue0());
				if (elem != null) {
					String parName = String.format("v%s", idxPars++);
					String sqlParName = String.format(":%s", parName);
					mapUteParam.put(parName, pair.getValue2().eval(elem));
					updates.add(new Pair<String,String>(pair.getValue1(), sqlParName));
				}
			}
			if (updates.size() > 0) {
				mapUteParam.put("idUtente", idUtente);
				String sqlUpdUte = String.format(
						"""
update FOLIAGE2.FLGUTEN_TAB
set %s
where ID_UTEN = :idUtente""",
						updates.stream().map((p)->String.format("%s = %s", p.getValue0(), p.getValue1())).collect(Collectors.joining(", ")).toString()
				);
				update(sqlUpdUte, mapUteParam);
			}

			JsonElement isProfMod = mods.get("isProfessionistaForestale");
			JsonElement autocertElem = mods.get("autocertificazioneProf");
			JsonObject autocertObj = (autocertElem == null) ? null : autocertElem.getAsJsonObject();
			if (isProfMod != null || autocertObj != null) {
				boolean isProfessionista = (
						(
								(isProfMod != null) && isProfMod.getAsBoolean()
						)
						|| (autocertObj != null)
				);
				// L'unico caso valido in cui si conferma professionista è quando invia i dati di autocertificazione aggiornati
				//boolean isProfessionista = (autocertObj != null);

				Map<String, Object> mapAutocParam = new HashMap<String, Object>();
				mapAutocParam.put("idUtente", idUtente);
				String annullAutocert = """
update FOLIAGE2.FLGAUTOCERT_PROF_TAB
set DATA_ANNULLAMENTO = localtimestamp
where ID_UTENTE = :idUtente
and DATA_ANNULLAMENTO is null""";
				update(annullAutocert, mapAutocParam);

				String delProf = """
delete from FOLIAGE2.FLGUTE_PROFESSIONISTI_TAB
where ID_UTENTE = :idUtente""";
				update(delProf, mapAutocParam);
				if (isProfessionista) {
					if (autocertObj == null) {
						throw new FoliageException("Dati autocertificazione professionista mancanti");
					}
					else {
						final BiFunction<JsonObject, String, String> getStr = (js, s) -> {
							JsonElement e = js.get(s);
							return (e == null) ? null: (e.isJsonNull() ? null : e.getAsString());
						};
						final BiFunction<JsonObject, String, Integer> getInt = (js, s) -> {
							JsonElement e = js.get(s);
							return (e == null) ? null: (e.isJsonNull() ? null : Integer.parseInt(e.getAsString()));
						};
						
						String categoria = getStr.apply(autocertObj, "categoria");
						String sottocategoria = getStr.apply(autocertObj, "sottocategoria");
						String collegio = getStr.apply(autocertObj, "collegio");
						String numeroIscrizione = getStr.apply(autocertObj, "numeroIscrizione");
						Integer provinciaIscrizione = getInt.apply(autocertObj, "provinciaIscrizione");
						String postaCertificata = getStr.apply(autocertObj, "postaCertificata");

						
						Boolean isSenior = (sottocategoria != null && sottocategoria.equals("senior"));

						String insAutocert = """
insert into FOLIAGE2.FLGAUTOCERT_PROF_TAB(
		ID_UTENTE,
		CATEGORIA, SOTTOCATEGORIA, COLLEGGIO, NUMERO_ISCRIZIONE,
		ID_PROVINCIA_ISCRIZIONE, PEC,
		DATA_INSERIMENTO
	)
values(
		:idUtente,
		:categoria, :sottocategoria, :collegio, :numeroIscrizione,
		:provinciaIscrizione, :postaCertificata,
		localtimestamp
	)""";
						Map<String, Object> mapDatiAutocParam = new HashMap<String, Object>();
						mapDatiAutocParam.put("idUtente", idUtente);
						mapDatiAutocParam.put("categoria", categoria);
						mapDatiAutocParam.put("sottocategoria", sottocategoria);
						mapDatiAutocParam.put("collegio", collegio);
						mapDatiAutocParam.put("numeroIscrizione", numeroIscrizione);
						mapDatiAutocParam.put("provinciaIscrizione", provinciaIscrizione);
						mapDatiAutocParam.put("postaCertificata", postaCertificata);
						update(insAutocert, mapDatiAutocParam);

						String insRuol = """
insert into FOLIAGE2.flgprofili_utente_tab(ID_UTENTE, ID_PROFILO, FLAG_SENIOR)
select :idUtente, 2, :isSenior
where (:idUtente, 2) not in (
		select ID_UTENTE, ID_PROFILO
		from FOLIAGE2.flgprofili_utente_tab
	)""";
						String insProf = """
insert into FOLIAGE2.FLGUTE_PROFESSIONISTI_TAB(ID_UTENTE, ID_PROFILO, IS_SENIOR)
values (:idUtente, 2, :isSenior)""";
						Map<String, Object> mapDatiProf = new HashMap<String, Object>();
						mapDatiProf.put("idUtente", idUtente);
						mapDatiProf.put("isSenior", isSenior);

						int verInsRuol = update(insRuol, mapDatiProf);
						if (verInsRuol == 0) {
							String updRuol = """
update FOLIAGE2.flgprofili_utente_tab
set FLAG_SENIOR = :isSenior
where ID_UTENTE = :idUtente
	and ID_PROFILO = 2""";
							update(updRuol, mapDatiProf);
						}
						update(insProf, mapDatiProf);

					}
				}
				else {
					String delRuol = """
delete from FOLIAGE2.FLGPROFILI_UTENTE_TAB
where ID_UTENTE = :idUtente
	and ID_PROFILO = 2
returning FLAG_DEFAULT""";
					Boolean isDef = queryForObject(
							delRuol,
							mapAutocParam,
							(rs, rn) -> {
								return rs.getBoolean(1);
							}
					);
					if (isDef) {
						String updDef = """
update FOLIAGE2.FLGPROFILI_UTENTE_TAB
set FLAG_DEFAULT = true
where ID_UTENTE = :idUtente
	and ID_PROFILO = (
		select min(P.ID_PROFILO)
		from FOLIAGE2.FLGPROFILI_UTENTE_TAB P
		where P.ID_UTENTE = :idUtente
	)""";
						update(
							updDef,
							mapAutocParam
						);
					}
				}
			}

			platformTransactionManager.commit(status);
		}catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return "OK";
	}
// 	public DatiTitolare getDatiTitolareFromUser(Integer idUtente) {
// 		Map<String, Object> mapUteParam = new HashMap<String, Object>();
// 		mapUteParam.put("idUtente", idUtente);
// 		SqlParameterSource updUtenParameters = new MapSqlParameterSource(mapUteParam);
// 		String sql = """
// select codi_fisc, cognome, nome, data_nascita, luogo_nascita--, email, pec
// from FOLIAGE2.FLGUTEN_TAB
// where ID_UTEN = :idUtente
// 				""";
// 		return template.queryForObject(
// 			sql,
// 			updUtenParameters,
// 			(rs, rn) -> {
// 				DatiTitolare outVal = new DatiTitolare();
// 				outVal.setCodiceFiscale(rs.getString("codi_fisc"));
// 				outVal.setCognome(rs.getString("cognome"));
// 				outVal.setNome(rs.getString("nome"));
// 				outVal.setDataDiNascita(rs.getDate("data_nascita").toLocalDate());
// 				outVal.setLuogoDiNascita(rs.getString("luogo_nascita"));
// 				// outVal.setEmail(rs.getString("email"));
// 				// outVal.setPostaCertificata(rs.getString("pec"));
// 				return outVal;
// 			}
// 		);
// 	}
	public Object GetStruttureSoprasuolo() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select id_sspr, desc_sspr
from foliage2.flgsspr_tab ft
""");
				return statement.executeQuery();
			}
		);
		return result;
	}

// 	public Object GetMacrocategorieSpecie() throws SQLException {
// 		ResultSet result = this.GetResult(
// 			(conn) -> {
// 				PreparedStatement statement = conn.prepareStatement("""
// select ID_MACROCATEGORIA, NOME_MACROCATEGORIA
// from foliage2.FLGMACROCATEGORIE_SPECIE_TAB
// """);
// 				return statement.executeQuery();
// 			}
// 		);
// 		return result;
// 	}
	
// 	public Object GetCategorieSpecie(String macrocategoria) throws SQLException {
// 		ResultSet result = this.GetResult(
// 			(conn) -> {
// 				PreparedStatement statement = conn.prepareStatement("""
// select ID_CATEGORIA, NOME_CATEGORIA
// from foliage2.FLGMACROCATEGORIE_SPECIE_TAB
// 	join foliage2.FLGCATEGORIE_SPECIE_TAB using (ID_MACROCATEGORIA)
// where NOME_MACROCATEGORIA = coalesce(?, NOME_MACROCATEGORIA)
// """);
// 				if (macrocategoria == null) {
// 					statement.setNull(1, java.sql.Types.VARCHAR);
// 				}
// 				else {
// 					statement.setString(1, macrocategoria);
// 				}
// 				return statement.executeQuery();
// 			}
// 		);
// 		return result;
// 	}

// 	public Object GetSpeciForestali(String categoria) throws SQLException {
// 		ResultSet result = this.GetResult(
// 			(conn) -> {
// 				PreparedStatement statement = conn.prepareStatement("""
// select ID_SPECIE, NOME_SPECIE, NOME_SCENTIFICO
// from foliage2.FLGCATEGORIE_SPECIE_TAB
// 	join foliage2.FLGSPECIE_TAB using (ID_CATEGORIA)
// where NOME_CATEGORIA = coalesce(?, NOME_CATEGORIA)
// """);
// 				if (categoria == null) {
// 					statement.setNull(1, java.sql.Types.VARCHAR);
// 				}
// 				else {
// 					statement.setString(1, categoria);
// 				}
// 				return statement.executeQuery();
// 			}
// 		);
// 		return result;
// 	}
	
	public Object GetFormeDiGoverno() throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select id_gove, desc_gove
from foliage2.flggove_tab ft
""");
				return statement.executeQuery();
			}
		);
		return result;
	}
	public Object GetLayerDomanda(String codIstanza) throws SQLException {
		ResultSet result = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
with lim as (
		select la.shape_vinc, la.srid
		from foliage2.flgista_tab i
			join flglimiti_amministrativi_tab la using (id_ente_terr)
		where codi_ista = ?
	)
select NOME_LAYER, CODICE, TIPO_SITO, DENOMINAZI, SRID, st_astext(ST_Transform(GEOM, srid)) as SHAPE
from (
		select 'NAT2K'as NOME_LAYER, SN.CODICE, SN.TIPO_SITO, SN.DENOMINAZI, SN.GEOM, SRID
		from lim
			join foliage_extra.siti_natura_2000_lazio_umbria sn on (ST_Intersects(sn.geom, ST_SetSRID(ST_Transform(ST_SetSRID(lim.shape_vinc , lim.srid), 6706), 6706)))
		union all
		select 'HABITAT'as NOME_LAYER, SN.HABITAT, SN.REL_CATEGO, SN.REL_DESCRI, SN.GEOM, SRID
		from lim
			join foliage_extra.nat2k_habitat_prioritari_208 sn on (ST_Intersects(sn.geom, ST_SetSRID(ST_Transform(ST_SetSRID(lim.shape_vinc , lim.srid), 4326), 4326)))
	) as T"""
					);
				statement.setString(1, codIstanza);
				return statement.executeQuery();
			}
		);
		return result;
	}

	public Object GetLayerDomanda2(String codIstanza) throws SQLException {
		HashMap<String, Object> outVal = new HashMap<>();
		

		String sqlMasterQuery = """
select COD_LAYER, NOME_GRUPPO, NOME_LAYER, SRID, DETTAGLIO_FONTE, SPIDX_COL_NAME, LABEL_EXPRESSION
from foliage2.FLGLAYERS_TAB
where FONTE_LAYER = 'TABELLA'
	and NOME_LAYER not in ('BOSCHI', 'ACQUE_PUBBLICHE_RISPETTO', 'RISPETTO_PUNTI_ARCHEOLOGICI', 'PUNTI_ARCHEOLOGICI_TIPIZZATI')""";

		return template.query(
			sqlMasterQuery,
			(rs, rn) -> {
				return new Object() {
					public String codLayer = rs.getString("cod_layer");
					public String gruppo = rs.getString("nome_gruppo");
					public String nome = rs.getString("nome_layer");
					public ResultSet dati = GetResult(
						(conn) -> {
							String layerQuery = String.format("""
with lim as (
		select la.shape_vinc, la.srid
		from foliage2.flgista_tab i
			join flglimiti_amministrativi_tab la using (id_ente_terr)
		where codi_ista = ?
	)
select T.label, st_astext(ST_Transform(GEOM, :sridGeometrie)) as geom_wkt
from (
		select srid, sn.*
		from lim
			join (
				select %s as label, %s as geom
				from foliage_extra.%s 
			) sn on (ST_Intersects(sn.geom, ST_SetSRID(ST_Transform(ST_SetSRID(lim.shape_vinc , lim.srid), %d), %d)))
	) as T""",

								rs.getString("label_expression"),
								rs.getString("spidx_col_name"),
								rs.getString("dettaglio_fonte"),
								rs.getInt("srid"),
								rs.getInt("srid")
							);
							log.debug(layerQuery);
							PreparedStatement statement = conn.prepareStatement(layerQuery);
							statement.setString(1, codIstanza);
							return statement.executeQuery();
						}
					);
				};
			}
		);
	}

	public Object GetInfoSpeciForestali() throws SQLException {

		ResultSet resultSpec = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select ID_SPECIE, NOME_SPECIE, NOME_SCENTIFICO, TIPO_SOPRASUOLO
from foliage2.FLGSPECIE_TAB
""");
				return statement.executeQuery();
			}
		);

		return new Object() {
			public ResultSet speci = resultSpec;
		};
	}

	public Object GetInterventiAmbitiNonForestali() throws SQLException {
		ResultSet resultUsoS = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select ID_USO_SUOLO, COD_USO_SUOLO, DESC_USO_SUOLO
from foliage2.FLGUSO_SUOLO_TAB
order by COD_USO_SUOLO
""");
				return statement.executeQuery();
			}
		);
		ResultSet resultTipoI = this.GetResult(
			(conn) -> {
				PreparedStatement statement = conn.prepareStatement("""
select ID_TIPO_INTERVENTO, ID_USO_SUOLO, COD_TIPO_INTERVENTO,
	NOME_TIPO_INTERVENTO, RIFERIMENTO_NORMATIVO, PARAMETRO_RICHIESTO
from foliage2.FLGTIPO_INTERVENTO_TAB
order by COD_TIPO_INTERVENTO
""");
				return statement.executeQuery();
			}
		);

		return new Object() {
			public ResultSet usiDelSuolo = resultUsoS;
			public ResultSet tipiIntervento = resultTipoI;
		};
	}

	public Object GetFormeDiTrattamento(String codIstanza) throws SQLException {
		String queryFormaGovFissa = """
select si.desc_gove
from foliage2.flgista_tab i
	left join foliage2.flgschede_intervento_limitazione_vinca_tab si using (id_scheda_intervento)
where codi_ista = :codIstanza
				""";
		HashMap<String, Object> params = new HashMap<>();
		params.put("codIstanza", codIstanza);

		String formaGovFissaFin = template.queryForObject(
				queryFormaGovFissa, params,
				(rs, rn) -> {
					return rs.getString("desc_gove");
				}
			);
		
		String queryPatt = """
				select id_forma_trattamento, desc_forma_trattamento, is_fine_turno,
					id_forma_trattamento in (
						select case
							when i.id_scheda_intervento is null
								then t.id_forma_trattamento
								else fti.id_forma_trattamento
							end as id_forma_trattamento
						from foliage2.flgista_tab i
							left join foliage2.flgforme_trattamento_intervento_tab fti using (id_scheda_intervento)
						where codi_ista = ?
					) as is_abilitato
				from foliage2.FLGFORME_TRATTAMENTO_TAB t
					join foliage2.flggove_tab g  using (id_gove)
				where g.desc_gove = '%s'""";
		ResultSet resultCeduo = null;
		ResultSet resultFustaia = null;
		if (!"Fustaia".equals(formaGovFissaFin)) {
			resultCeduo = this.GetResult(
				(conn) -> {
					String query = String.format(queryPatt, "Ceduo");
					log.debug(query);
					PreparedStatement statement = conn.prepareStatement(query);
					statement.setString(1, codIstanza);
					return statement.executeQuery();
				}
			);
		}
		if (!"Ceduo".equals(formaGovFissaFin)) {
			resultFustaia = this.GetResult(
				(conn) -> {
					String query = String.format(queryPatt, "Fustaia");
					PreparedStatement statement = conn.prepareStatement(query);
					statement.setString(1, codIstanza);
					return statement.executeQuery();
				}
			);
		}


		final ResultSet resultCeduoFin = resultCeduo;
		final ResultSet resultFustaiaFin = resultFustaia;

		return new Object() {
			public String formaGovFissa = formaGovFissaFin;
			public ResultSet listaCeduo = resultCeduoFin;
			public ResultSet listaFustaia = resultFustaiaFin;
		};
	}

// 	public Object GetFormeDiTrattamento(String tipo) throws SQLException {
// 		ResultSet result = this.GetResult(
// 			(conn) -> {
// 				PreparedStatement statement = conn.prepareStatement("""
// select ID_FORMA_TRATTAMENTO, COD_FORMA_TRATTAMENTO, DESC_FORMA_TRATTAMENTO, ID_GOVE, IS_FINE_TURNO
// from foliage2.flggove_tab ft
// 	join foliage2.FLGFORME_TRATTAMENTO_TAB using (ID_GOVE)
// where ft.desc_gove = ?
// """);
// 				statement.setString(1, tipo);
// 				return statement.executeQuery();
// 			}
// 		);
// 		return result;
// 	}

// 	public Object GetFormeDiTrattamento(String codIstanza, String tipo) throws SQLException {
// 		ResultSet result = this.GetResult(
// 			(conn) -> {
// 				PreparedStatement statement = conn.prepareStatement("""
// select ID_FORMA_TRATTAMENTO, COD_FORMA_TRATTAMENTO, DESC_FORMA_TRATTAMENTO, ID_GOVE, IS_FINE_TURNO
// from foliage2.flggove_tab ft
// 	join foliage2.FLGFORME_TRATTAMENTO_TAB using (ID_GOVE)
// where ft.desc_gove = ?
// """);
// 				statement.setString(1, tipo);
// 				return statement.executeQuery();
// 			}
// 		);
// 		return result;
// 	}
	public Object getInquadramentoVincolistica(String codIstanza) throws Exception {
		
		String queryRilievi = """
select V.GRUPPO, DESC_VINCOLO, COD_AREA, NOME_AREA, SUPERFICIE
from (
		select VI.ID_VINCOLO, COD_AREA, NOME_AREA, sum(VI.SUPERFICIE) as SUPERFICIE
		from foliage2.FLGISTA_TAB I
			join foliage2.FLGVINCOLI_ISTA_TAB VI using (ID_ISTA)
		where I.CODI_ISTA = :codIstanza
		group by VI.ID_VINCOLO, COD_AREA, NOME_AREA
	) as T
	join foliage2.FLGVINCOLI_TAB V using (ID_VINCOLO)
order by ID_VINCOLO, COD_AREA, NOME_AREA""";

		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("codIstanza", codIstanza);
		final List<Object> rilieviLayerFin = template.query(
			queryRilievi, parameters, 
			(rs, rn) -> {
				return new Object() {
					public String gruppo = rs.getString("GRUPPO");
					public String nomeVincolo = rs.getString("DESC_VINCOLO");
					public String codice = rs.getString("COD_AREA");
					public String descrizione = rs.getString("NOME_AREA");
					public Object superficie = rs.getBigDecimal("SUPERFICIE");
				};
			}
		);

		String queryMess = """
select COD_LIMITAZIONE, L.DESC_LIMITAZIONE
from foliage2.FLGLIMITAZIONI_TAB L
where L.ID_LIMITAZIONE in (
		select VTI.ID_LIMITAZIONE
		from (
				select distinct ID_TIPO_ISTANZA, VI.ID_VINCOLO, COD_AREA, NOME_AREA
				from foliage2.FLGISTA_TAB I
					join foliage2.FLGVINCOLI_ISTA_TAB VI using (ID_ISTA)
				where I.CODI_ISTA = :codIstanza
			) as T
			join foliage2.FLGVINCOLI_TAB V using (ID_VINCOLO)
			join foliage2.FLGVINCOLI_TIPO_ISTA_TAB VTI using (ID_VINCOLO, ID_TIPO_ISTANZA)
	)
order by L.ID_LIMITAZIONE""";

		boolean isVinca = false;
		final List<Pair<String, Boolean>> avvisiFin = this.query(
			queryMess, parameters, 
			(rs, rn) -> {
				return new Pair<String, Boolean>(
					rs.getString("desc_limitazione"),
					"NAT2K_SOPRA_SOGLIA_UMBRIA".equals(rs.getString("cod_limitazione"))
				);
			}
		);

		return new Object() {
			public Object sovrapposizioni = rilieviLayerFin;
			public Object avvisi = avvisiFin.stream().map(p -> p.getValue0());
			public String tipoWizard = avvisiFin.stream().anyMatch(p -> p.getValue1()) ? "wizardVinca" : "wizardBase";
		};

	}
	public AbilitazioniIstanza getAbilitazioniIstanza(Integer idUtente, String codFiscaleUtente, String codIstanza, String authority, String authScope) throws Exception {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("codIstanza", codIstanza);
		parameters.put("idUtente", idUtente);
		parameters.put("tipoAuth", authority);
		parameters.put("authScope", authScope);

		LocalDate dataAttuale = LocalDate.now();
		Function<HashMap<String, Boolean>, HashMap<String, Object>> checkFunction = null;

		
		String query = null;
		CampoSelect[] campiSelect = null;
		CondizioneEq[] condizioniWhere = new CondizioneEq[] {
			new CondizioneEq("CODI_ISTA", "codIstanza")
		};
		String[] proprietaUtilizzateQuery = null;

		
		String queryAmministratore = """
(
				select i.CODI_ISTA, s.COD_STATO
				from foliage2.flgista_tab as i
					join foliage2.flgstato_istanza_tab as s on (s.id_stato = i.stato)
			) as T""";
		switch (authority) {
			case "PROP": {
				query = """
(
				select i.CODI_ISTA, t.CODICE_FISCALE as cod_fiscale_titolare, i.id_utente_compilazione, ti.cod_tipo_istanza,
					vi.data_fine_validita, p.MESI_DURATA as mesi_proroga,
					s.COD_STATO, il.data_inizio_lavori, fl.data_fine_lavori,
					not exists (
						select *
						from FOLIAGE2.FLGSCHEDE_TIPOISTANZA_TAB STI
							left join FOLIAGE2.FLGISTA_SCHEDE_SALVATE_TAB SS on (
									SS.ID_ISTA = i.id_ista
									and SS.PROG_SCHEDA = STI.PROG_SCHEDA
							)
						where STI.id_tipo_istanza = i.id_tipo_istanza
							and SS.PROG_SCHEDA is null
							and STI.IS_OBBLIGATORIA
					) as is_completed
				from foliage2.flgista_tab as i
						join foliage2.flgstato_istanza_tab as s on (s.id_stato = i.stato)
						join foliage2.flgtipo_istanza_tab tis using (id_tipo_istanza)
						join foliage2.flgcist_tab ti on (ti.id_cist = tis.id_cist)
						left join foliage2.FLGTITOLARE_ISTANZA_TAB as t using (ID_TITOLARE)
						left join foliage2.flgvalutazione_istanza_tab as vi using (ID_ISTA)
						left join foliage2.FLGISTA_PROROGA_TAB as p using (ID_ISTA)
						left join foliage2.FLGDATE_INIZIO_LAVORI_ISTANZA_TAB as il using (ID_ISTA)
						left join foliage2.FLGDATE_FINE_LAVORI_ISTANZA_TAB as fl using (ID_ISTA)
			) as T""";
				campiSelect = new CampoSelect[] {
					new CampoSelect("cod_fiscale_titolare", DbUtils.Get),
					new CampoSelect("id_utente_compilazione", DbUtils.Get),
					new CampoSelect("cod_tipo_istanza", DbUtils.Get),
					new CampoSelect("cod_stato", DbUtils.Get),
					new CampoSelect("data_fine_validita", DbUtils.GetDate),
					new CampoSelect("mesi_proroga", DbUtils.GetInteger),
					new CampoSelect("data_inizio_lavori", DbUtils.GetDate),
					new CampoSelect("data_fine_lavori", DbUtils.GetDate),
					new CampoSelect("is_completed", DbUtils.GetBoolean)
				};
				checkFunction = (HashMap<String, Object> hm) -> {
					HashMap<String, Boolean> outVal = new HashMap<String, Boolean>();
					String codFiscaleTitolare = (String) hm.get("cod_fiscale_titolare");
					String codTipoIstanza = (String) hm.get("cod_tipo_istanza");
					Integer idGestore = (Integer) hm.get("id_utente_compilazione");
					String codStato = (String) hm.get("cod_stato");
					LocalDate dataInizioLavori = (LocalDate) hm.get("data_inizio_lavori");
					LocalDate dataFineLavori = (LocalDate) hm.get("data_fine_lavori");
					LocalDate dataFineValidita = (LocalDate) hm.get("data_fine_validita");
					Integer mesiProroga = (Integer)hm.get("mesi_proroga");
					Boolean isCompleted = (Boolean)hm.get("is_completed");
					boolean isTitolare = (codFiscaleUtente.equals(codFiscaleTitolare));
					boolean isGestore = (idGestore != null && (idUtente.equals(idGestore))) || (idGestore == null && isTitolare);
					boolean rimuoviGestore = (isTitolare || isGestore) && "SOTTO_SOGLIA".equals(codTipoIstanza) && (idGestore != null);
					Boolean read = isTitolare;
					Boolean write= isGestore && ( "COMPILAZIONE".equals(codStato));

					//Boolean upload_modulo_firmato = isGestore && ( "ISTRUTTORIA".equals(codStato) || "PRESENTATA".equals(codStato));
					Boolean allega_documenti = isGestore && ( "ISTRUTTORIA".equals(codStato));
					//boolean allegaTavole = isGestore;
					Boolean consulta_valutazione = isGestore && (("APPROVATA".equals(codStato)) || ("RESPINTA".equals(codStato)));
					Boolean invio = write && isCompleted;
					Boolean access = write || read;
					Boolean changeGestore = isTitolare;
					Boolean proroga = isGestore && dataFineLavori == null && dataFineValidita != null && dataFineValidita.isAfter(LocalDate.now().minusDays(30)) && mesiProroga == null;
					

					Boolean tellInizio = isGestore && dataFineValidita != null && dataFineValidita.isAfter(dataAttuale) && (dataInizioLavori == null);
					Boolean tellFine = isGestore && dataInizioLavori != null && !dataInizioLavori.isAfter(LocalDate.now()) && dataFineLavori == null;
					outVal.put("allega_documenti", allega_documenti);
					//outVal.put("allega_tavole", allegaTavole);
					outVal.put("access", access);
					outVal.put("consultazione", read);
					outVal.put("compilazione", write);
					outVal.put("consulta_valutazione", consulta_valutazione);
					outVal.put("invio", invio);
					outVal.put("passaggio_gestore", changeGestore);
					outVal.put("richiesta_proroga", proroga);
					outVal.put("inizio_lavori", tellInizio);
					outVal.put("fine_lavori", tellFine);
					outVal.put("rimuovi_gestore", rimuoviGestore);
					//outVal.put("upload_modulo_firmato", upload_modulo_firmato);
					
					return outVal;
				};
			}; break;
			case "PROF": {
				query = """
(
				select i.CODI_ISTA, t.CODICE_FISCALE as cod_fiscale_titolare, i.id_utente_compilazione, ti.cod_tipo_istanza,
					s.COD_STATO, il.data_inizio_lavori, fl.data_fine_lavori,
					vi.data_fine_validita, p.MESI_DURATA as mesi_proroga,
					not exists (
						select *
						from FOLIAGE2.FLGSCHEDE_TIPOISTANZA_TAB STI
							left join FOLIAGE2.FLGISTA_SCHEDE_SALVATE_TAB SS on (
									SS.ID_ISTA = i.id_ista
									and SS.PROG_SCHEDA = STI.PROG_SCHEDA
							)
						where STI.id_tipo_istanza = i.id_tipo_istanza
							and SS.PROG_SCHEDA is null
							and STI.IS_OBBLIGATORIA
					) as is_completed
				from foliage2.flgista_tab as i
					join foliage2.flgstato_istanza_tab as s on (s.id_stato = i.stato)
					join foliage2.flgtipo_istanza_tab tis using (id_tipo_istanza)
					join foliage2.flgcist_tab ti on (ti.id_cist = tis.id_cist)
					left join foliage2.FLGTITOLARE_ISTANZA_TAB as t using (ID_TITOLARE)
					left join foliage2.flgvalutazione_istanza_tab as vi using (ID_ISTA)
					left join foliage2.FLGISTA_PROROGA_TAB as p using (ID_ISTA)
					left join foliage2.FLGDATE_INIZIO_LAVORI_ISTANZA_TAB as il using (ID_ISTA)
					left join foliage2.FLGDATE_FINE_LAVORI_ISTANZA_TAB as fl using (ID_ISTA)
			) as T""";
				campiSelect = new CampoSelect[] {
					new CampoSelect("id_utente_compilazione", DbUtils.Get),
					new CampoSelect("cod_stato", DbUtils.Get),
					new CampoSelect("cod_fiscale_titolare", DbUtils.Get),
					new CampoSelect("cod_tipo_istanza", DbUtils.Get),
					new CampoSelect("data_fine_validita", DbUtils.GetDate),
					new CampoSelect("mesi_proroga", DbUtils.GetInteger),
					new CampoSelect("data_inizio_lavori", DbUtils.GetDate),
					new CampoSelect("data_fine_lavori", DbUtils.GetDate),
					new CampoSelect("is_completed", DbUtils.GetBoolean)
				};
				checkFunction = (HashMap<String, Object> hm) -> {
					HashMap<String, Boolean> outVal = new HashMap<>();
					Integer idGestore = (Integer) hm.get("id_utente_compilazione");
					String codStato = (String) hm.get("cod_stato");
					LocalDate dataInizioLavori = (LocalDate) hm.get("data_inizio_lavori");
					LocalDate dataFineLavori = (LocalDate) hm.get("data_fine_lavori");
					LocalDate dataFineValidita = (LocalDate) hm.get("data_fine_validita");
					Integer mesiProroga = (Integer)hm.get("mesi_proroga");
					//boolean isGestore = (idUtente.equals(idGestore));
					String codFiscaleTitolare = (String) hm.get("cod_fiscale_titolare");
					String codTipoIstanza = (String) hm.get("cod_tipo_istanza");
					Boolean isCompleted = (Boolean)hm.get("is_completed");
					boolean isTitolare = (codFiscaleUtente.equals(codFiscaleTitolare));
					boolean isGestore = (idGestore != null && (idUtente.equals(idGestore))) || (idGestore == null && isTitolare);
					boolean rimuoviGestore = (isTitolare || isGestore) && "SOTTO_SOGLIA".equals(codTipoIstanza) && (idGestore != null);
					//boolean allegaTavole = isGestore;

					Boolean read = isGestore || isTitolare;
					Boolean write= isGestore && ( "COMPILAZIONE".equals(codStato));
					
					//Boolean upload_modulo_firmato = isGestore && ( "ISTRUTTORIA".equals(codStato) || "PRESENTATA".equals(codStato));

					Boolean allega_documenti = isGestore && ( "ISTRUTTORIA".equals(codStato));
					Boolean consulta_valutazione = isGestore && (("APPROVATA".equals(codStato)) || ("RESPINTA".equals(codStato)));
					Boolean invio = write && isCompleted;
					Boolean access = write || read;
					Boolean changeGestore = isGestore || isTitolare;
					
					
					Boolean proroga = isGestore && dataFineLavori == null && dataFineValidita != null && dataFineValidita.isAfter(LocalDate.now().minusDays(30)) && mesiProroga == null;
					

					Boolean tellInizio = isGestore && ("APPROVATA".equals(codStato)) && (dataInizioLavori == null);
					Boolean tellFine = isGestore && dataInizioLavori != null && !dataInizioLavori.isAfter(LocalDate.now()) && dataFineLavori == null;
					outVal.put("allega_documenti", allega_documenti);
					//outVal.put("allega_tavole", allegaTavole);
					outVal.put("access", access);
					outVal.put("consultazione", read);
					outVal.put("compilazione", write);
					outVal.put("consulta_valutazione", consulta_valutazione);
					outVal.put("invio", invio);
					outVal.put("passaggio_gestore", changeGestore);
					outVal.put("richiesta_proroga", proroga);
					outVal.put("inizio_lavori", tellInizio);
					outVal.put("fine_lavori", tellFine);
					outVal.put("rimuovi_gestore", rimuoviGestore);
					//outVal.put("upload_modulo_firmato", upload_modulo_firmato);
					return outVal;
				};
				//TODO: gestire storico gestori
			}; break;
			case "ISTR": {
				query = """
(
		select i.CODI_ISTA, s.COD_STATO, id_utente_istruttore
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
		select i.CODI_ISTA, s.COD_STATO, id_utente_istruttore
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
					Boolean valutazione = (isIstruttore && "ISTRUTTORIA".equals(codStato));
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
		select i.CODI_ISTA, s.COD_STATO
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

	public Object getDettagliIterventiConsentiti(
		String codIstanza
	) throws SQLException {
		String queryRilievi = """
select DESC_VINCOLO, COD_AREA, NOME_AREA
from (
		select distinct VI.ID_VINCOLO, COD_AREA, NOME_AREA
		from foliage2.FLGISTA_TAB I
			join foliage2.FLGVINCOLI_ISTA_TAB VI using (ID_ISTA)
		where I.CODI_ISTA = :codIstanza
	) as T
	join foliage2.FLGVINCOLI_TAB V using (ID_VINCOLO)
order by ID_VINCOLO, COD_AREA, NOME_AREA""";

		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("codIstanza", codIstanza);
		final List<Object> rilieviLayerFin = template.query(
			queryRilievi, parameters, 
			(rs, rn) -> {
				return new Object() {
					public String categoria = rs.getString("DESC_VINCOLO");
					public String codice = rs.getString("COD_AREA");
					public String descrizione = rs.getString("NOME_AREA");
				};
			}
		);

		String queryMess = """
select COD_LIMITAZIONE, L.DESC_LIMITAZIONE
from foliage2.FLGLIMITAZIONI_TAB L
where L.ID_LIMITAZIONE in (
		select VTI.ID_LIMITAZIONE
		from (
				select distinct ID_TIPO_ISTANZA, VI.ID_VINCOLO, COD_AREA, NOME_AREA
				from foliage2.FLGISTA_TAB I
					join foliage2.FLGVINCOLI_ISTA_TAB VI using (ID_ISTA)
				where I.CODI_ISTA = :codIstanza
			) as T
			join foliage2.FLGVINCOLI_TAB V using (ID_VINCOLO)
			join foliage2.FLGVINCOLI_TIPO_ISTA_TAB VTI using (ID_VINCOLO, ID_TIPO_ISTANZA)
	)
order by L.ID_LIMITAZIONE""";


		final List<Object> avvisiFin = template.query(
			queryMess, parameters, 
			(rs, rn) -> {
				Object res = new Object() {
					public String code = rs.getString("cod_limitazione");
					public String value = rs.getString("desc_limitazione");
				};
				//return rs.getString("desc_limitazione");
				return res;
			}
		);

		String queryInterventi = """
select IL.ID_SCHEDA_INTERVENTO, LINK_PDF_SCHEDA, DESC_INTERVENTO, DESC_GOVE
from foliage2.FLGISTA_TAB I
	join foliage2.FLGSCHEDE_INTERVENTO_LIMITAZIONE_VINCA_TAB IL using (ID_TIPO_ISTANZA)
where I.CODI_ISTA = :codIstanza
	and exists (
		select *
		from foliage2.FLGLIMITAZIONI_TAB L
		where L.ID_LIMITAZIONE in (
				select VTI.ID_LIMITAZIONE
				from (
						select distinct ID_TIPO_ISTANZA, VI.ID_VINCOLO, COD_AREA, NOME_AREA
						from foliage2.FLGISTA_TAB I
							join foliage2.FLGVINCOLI_ISTA_TAB VI using (ID_ISTA)
						where I.CODI_ISTA = :codIstanza
					) as T
					join foliage2.FLGVINCOLI_TAB V using (ID_VINCOLO)
					join foliage2.FLGVINCOLI_TIPO_ISTA_TAB VTI using (ID_VINCOLO, ID_TIPO_ISTANZA)
			)
			and L.COD_LIMITAZIONE in ('HABITAT_VINCA_BOSCHIVO', 'HABITAT_VINCA_COMUNICAZIONE')
	)
order by DESC_INTERVENTO desc""";

		
		final List<Object> interventiConsentitiFin = template.query(
			queryInterventi, parameters, 
			(rs, rn) -> {
				Integer idSchedaFin = rs.getInt("id_scheda_intervento");
				String linkPdfFin = rs.getString("link_pdf_scheda");
				String descInterventoFin = rs.getString("desc_intervento");
				String formaGovFin = rs.getString("desc_gove");

				String queryFormeTrattamento = """
select DESC_FORMA_TRATTAMENTO
from foliage2.FLGFORME_TRATTAMENTO_INTERVENTO_TAB as fti
	join foliage2.FLGFORME_TRATTAMENTO_TAB using (ID_FORMA_TRATTAMENTO)
where fti.ID_SCHEDA_INTERVENTO = :idScheda""";
				parameters.put("idScheda", idSchedaFin);
				List<String> formeTrattanentoFin = template.query(
					queryFormeTrattamento, parameters,
					(rs1, rn1) -> rs1.getString("desc_forma_trattamento")
				);

				return new Object() {
					public Integer idSchedaIntervento = idSchedaFin;
					public String urlFilePdf = linkPdfFin;
					public String tipoIntervento = descInterventoFin;
					public String formaDiGoverno = formaGovFin;
					public List<String> formeDiTrattamentoConsentite = formeTrattanentoFin;
				};
			}
		);


		return new Object() {
			public List<Object> rilieviLayer = rilieviLayerFin;
			public List<Object> avvisi = avvisiFin;
			public List<Object> interventiConsentiti = interventiConsentitiFin;
		};
	}
	public Object getSottocategorie() throws Exception{
		String queryCat = """
select ID_CATEGORIA, NOME_CATEGORIA
from foliage2.FLGCATEGORIE_TAB
	join FOLIAGE2.FLGCATEGORIE_REGIONI_TAB using (ID_CATEGORIA)
	join FOLIAGE2.flgente_regione_tab as R using (ID_REGIONE)
where R.CODI_ISTAT = :codRegione
order by NOME_CATEGORIA""";
		HashMap<String, Object> pars1 = new HashMap<>();
		pars1.put("codRegione", codRegione);

		
		String querySubcat = """
select id_sottocategoria, nome_sottocategoria
from foliage2.FLGSOTTOCATEGORIE_TAB C
where c.id_categoria = :idCat
order by nome_sottocategoria desc""";
		HashMap<String, Object> pars2 = new HashMap<>();

		return query(
			queryCat,
			pars1,
			(rs, rn) -> {
				Integer idCat = DbUtils.GetInteger(rs, rn, "ID_CATEGORIA");
				String nomeCat = rs.getString("NOME_CATEGORIA");
				pars2.put("idCat", idCat);

				return new Object() {
					public Integer id_categoria = idCat;
					public String nome_categoria = nomeCat;
					public Object subCats = queryForRowSet(
						querySubcat,
						pars2
					);
				};
			}
		);

		/*
		return new Object() {
			public ResultSet categorie = GetResult(
				(conn) -> {
					PreparedStatement statement = conn.prepareStatement("""
select ID_CATEGORIA, NOME_CATEGORIA
from foliage2.FLGCATEGORIE_TAB
order by NOME_CATEGORIA""");
					return statement.executeQuery();
				}
			);
			public ResultSet sottocategorie = GetResult(
				(conn) -> {
					PreparedStatement statement = conn.prepareStatement("""
select ID_CATEGORIA, ID_SOTTOCATEGORIA, NOME_SOTTOCATEGORIA
from foliage2.FLGSOTTOCATEGORIE_TAB
order by NOME_SOTTOCATEGORIA""");
					return statement.executeQuery();
				}
			);
		};
		*/
	}

	public Object trasformaIstanza(String codIstanza) throws Exception{
		
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			
			String sqlInsResp = """
update FOLIAGE2.FLGISTA_TAB 
set id_tipo_istanza = (
		select T.id_tipo_istanza
		from FOLIAGE2.flgtipo_istanza_tab T
		where COD_TIPO_ISTANZA_SPECIFICO = 'IN_DEROGA'
	)
where CODI_ISTA =:codIstanza""";

			HashMap<String, Object> mapParam = new HashMap<>();
			mapParam.put("codIstanza", codIstanza);
			template.update(sqlInsResp, mapParam);

			platformTransactionManager.commit(status);
		}catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return new Object() {
			public String ok = "ok";
		};
	}
	public Base64FormioFile getModuloPdfFirmatoIstanza(String codIstanza) throws Exception {
		String sqlGetIdModulo = """
select ID_FILE_MODULO_ISTANZA_FIRMATO
from FOLIAGE2.FLGISTA_TAB I
	join FOLIAGE2.FLGISTA_INVIO_TAB using (ID_ISTA)
where CODI_ISTA = :codIstanza""";
		HashMap<String, Object> mapParam = new HashMap<>();
		mapParam.put("codIstanza", codIstanza);

		Integer idFileModulo = queryForObject(sqlGetIdModulo, mapParam, DbUtils.GetIntegerRowMapper("ID_FILE_MODULO_ISTANZA_FIRMATO"));
		if (idFileModulo == null) {
			return null;
		}
		else {
			Base64FormioFile[] fileModulo = DbUtils.getBase64FormioFiles(this, idFileModulo);
			if (fileModulo.length == 1) {
				return fileModulo[0];
			}
			else {
				return null;
			}
		}


		//outputStream.write(fileModulo[0].getArrayFromUrl());
	}
	public void getModuloPdfNonFirmatoIstanza(String codIstanza, ServletOutputStream outputStream) throws Exception {
		String sqlGetIdModulo = """
select ID_FILE_MODULO_ISTANZA
from FOLIAGE2.FLGISTA_TAB I
	join FOLIAGE2.FLGISTA_INVIO_TAB using (ID_ISTA)
where CODI_ISTA = :codIstanza""";
		HashMap<String, Object> mapParam = new HashMap<>();
		mapParam.put("codIstanza", codIstanza);

		Integer idFileModulo = queryForObject(sqlGetIdModulo, mapParam, DbUtils.GetIntegerRowMapper("ID_FILE_MODULO_ISTANZA"));

		Base64FormioFile[] fileModulo = DbUtils.getBase64FormioFiles(this, idFileModulo);


		outputStream.write(fileModulo[0].getArrayFromUrl());
	}

	public void getModuloPdfIstanza(String codIstanza, ServletOutputStream outputStream) throws Exception {
		String sqlGetIdModulo = """
select coalesce(ID_FILE_MODULO_ISTANZA_FIRMATO, ID_FILE_MODULO_ISTANZA) as ID_FILE_MODULO_ISTANZA
from FOLIAGE2.FLGISTA_TAB I
	join FOLIAGE2.FLGISTA_INVIO_TAB using (ID_ISTA)
where CODI_ISTA = :codIstanza""";
		HashMap<String, Object> mapParam = new HashMap<>();
		mapParam.put("codIstanza", codIstanza);

		Integer idFileModulo = queryForObject(sqlGetIdModulo, mapParam, DbUtils.GetIntegerRowMapper("ID_FILE_MODULO_ISTANZA"));

		Base64FormioFile[] fileModulo = DbUtils.getBase64FormioFiles(this, idFileModulo);


		outputStream.write(fileModulo[0].getArrayFromUrl());
	}

	
	public void getModuloPdfIstruttoria(String codIstanza, ServletOutputStream outputStream) throws Exception {
		String sqlGetIdModulo = """
select ID_FILE_MODULO_ISTRUTTORIA
from FOLIAGE2.FLGISTA_TAB I
	join FOLIAGE2.FLGVALUTAZIONE_ISTANZA_TAB using (ID_ISTA)
where CODI_ISTA = :codIstanza""";
		HashMap<String, Object> mapParam = new HashMap<>();
		mapParam.put("codIstanza", codIstanza);

		Integer idFileModulo = queryForObject(sqlGetIdModulo, mapParam, DbUtils.GetIntegerRowMapper("ID_FILE_MODULO_ISTRUTTORIA"));

		Base64FormioFile[] fileModulo = DbUtils.getBase64FormioFiles(this, idFileModulo);


		outputStream.write(fileModulo[0].getArrayFromUrl());
	}

// 	public Object invioModuloIstanzaFirmato(String codIstanza, Base64FormioFile[] file) throws Exception{
		
// 		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
// 		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

// 		try {

// 			Integer idFile = DbUtils.saveBase64FormioFiles(this, file);
// 			if (idFile == null) {
// 				throw new FoliageException("Si è verificato un problema nel caricamento del modulo firmato");
// 			}
// 			else {
// 				String sqlUpdFileFirma = """
// update FOLIAGE2.FLGISTA_INVIO_TAB
// set ID_FILE_MODULO_ISTANZA_FIRMATO = :idFile,
// 	DATA_FIRMA = localtimestamp
// where id_ista = (
// 		select I.ID_ISTA
// 		from FOLIAGE2.FLGISTA_TAB I
// 		where I.CODI_ISTA = :codIstanza
// 	)""";
// 				HashMap<String, Object> pars = new HashMap<>();
// 				pars.put("codIstanza", codIstanza);
// 				pars.put("idFile", idFile);
// 				int res = update(sqlUpdFileFirma, pars);
// 				if (res != 1) {
// 					throw new FoliageException("Non è stato possibile allegare questo file per l'istanza");
// 				}
// 			}


// 			platformTransactionManager.commit(status);
// 		}
// 		catch (Exception e) {
// 			platformTransactionManager.rollback(status);
// 			throw e;
// 		}
// 		finally {
// 			status = null;
// 		}
		
// 		return new Object() {
// 			public String ok = "ok";
// 		};
// 	}

	public Object invioIstanza(String codIstanza, DatiInvioIstanza datiInvio) throws Exception{

		Base64FormioFile[] ricevute = datiInvio.bolloInvio;
		Base64FormioFile[] dirittiIstruttoriaInvio = datiInvio.dirittiIstruttoria;
		boolean isModuloFirmaDigitale = datiInvio.isModuloFirmaDigitale;

		Base64FormioFile[] fileModuloFirmato = datiInvio.isModuloFirmaDigitale ? datiInvio.allegatoFirmaDigitale : datiInvio.allegatoFirmaOlografa;
		Base64FormioFile[] fileDocIdentita = datiInvio.documentoAllegato;
		
		//String note = datiInvio.note;
		
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			LocalDateTime dataInvio = LocalDateTime.now();	
			HashMap<String, Object> parsMap = new HashMap<>();
			parsMap.put("codiIsta", codIstanza);
			
			ModuloIstanza pdfModulo = queryForObject(
				ModuloIstanza.queryFromCodiIsta,
				parsMap,
				ModuloIstanza.RowMapper(this, dataInvio, codIstanza, codRegione)
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


			Integer idFileRicevute = DbUtils.saveBase64FormioFiles(this, ricevute);
			if (idFileRicevute == null) {
				throw new FoliageException("Si è verificato un problema nel caricamento della ricevuta");
			}
			
			Integer idFileDirittiIstruttoria = DbUtils.saveBase64FormioFiles(this, dirittiIstruttoriaInvio);
			if (idFileRicevute == null) {
				throw new FoliageException("Si è verificato un problema nel caricamento dei diritti di istruttoria");
			}
			
			Integer idFileModulo = DbUtils.saveBase64FormioFiles(this, modulo);
			if (idFileModulo == null) {
				throw new FoliageException("Si è verificato un problema nel caricamento del modulo");
			}


			Integer idFileModuloFirmato = DbUtils.saveBase64FormioFiles(this, fileModuloFirmato);
			if (idFileModuloFirmato == null) {
				throw new FoliageException("Si è verificato un problema nel caricamento del modulo firmato");
			}

			Integer idFileDocIdentita = null;
			if (!isModuloFirmaDigitale) {
				idFileDocIdentita = DbUtils.saveBase64FormioFiles(this, fileDocIdentita);
				if (idFileDocIdentita == null) {
					throw new FoliageException("Si è verificato un problema nel caricamento del documento di identità");
				}
			}


			String sqlInsResp = """
insert into FOLIAGE2.FLGISTA_INVIO_TAB(
		ID_ISTA, DATA_INVIO, ID_FILE_RICEVUTE, ID_FILE_DIRITTI_ISTRUTTORIA, ID_FILE_MODULO_ISTANZA, 
		ID_FILE_MODULO_ISTANZA_FIRMATO, ID_FILE_DOC_IDENTITA, IS_FIRMA_DIGITALE
	)
select ID_ISTA, localtimestamp, :idFileRicevute, :idFileDirittiIstruttoria, :idFileModulo,
	:idFileModuloFirmato, :idFileDocIdentita, :isModuloFirmaDigitale
from FOLIAGE2.FLGISTA_TAB I
where CODI_ISTA =:codIstanza
	and I.STATO = (
		select ID_STATO
		from FOLIAGE2.FLGSTATO_ISTANZA_TAB
		where COD_STATO = 'COMPILAZIONE'
	)""";

			HashMap<String, Object> mapParam = new HashMap<>();
			mapParam.put("codIstanza", codIstanza);
			mapParam.put("idFileRicevute", idFileRicevute);
			mapParam.put("idFileDirittiIstruttoria", idFileDirittiIstruttoria);
			mapParam.put("idFileModulo", idFileModulo);
			mapParam.put("idFileModuloFirmato", idFileModuloFirmato);
			mapParam.put("idFileDocIdentita", idFileDocIdentita);
			mapParam.put("isModuloFirmaDigitale", isModuloFirmaDigitale);
			
			int nRows = template.update(sqlInsResp, mapParam);

			if (nRows != 1) {
				throw new FoliageException("L'istanza da inviare non risulta tra quelle in compilazione");
			}
			String sqlStato = """
update FOLIAGE2.FLGISTA_TAB
set STATO = (
		select ID_STATO
		from FOLIAGE2.FLGSTATO_ISTANZA_TAB
		where COD_STATO = 'PRESENTATA'
	),
	note = :note
where CODI_ISTA = :codIstanza""";
			mapParam.clear();
			mapParam.put("codIstanza", codIstanza);
			mapParam.put("note", "note");
			nRows = template.update(sqlStato, mapParam);
			if (nRows != 1) {
				throw new FoliageException("Si è verificato un problema nel presentare l'istanza ");
			}

			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return new Object() {
			public String ok = "ok";
		};
	}

	public List<Integer> getUtentiTitolariIstanza(String codIstanza) {
		HashMap<String, Object> mapParam = new HashMap<>();
		mapParam.put("codIstanza", codIstanza);
		String query = """
select u.id_uten 
from foliage2.flgista_tab i
	join foliage2.flgtitolare_istanza_tab ti using (id_titolare)
	join foliage2.flguten_tab u on (u.codi_fisc = ti.codice_fiscale)
where i.codi_ista = :codIstanza""";
		return query(
			query,
			mapParam,
			(rs, rn) -> Integer.valueOf(rs.getInt("id_uten"))
		);
	}

	public Object rimuoviGestore(String codIstanza, Integer idUtenteAssegnazione) throws Exception {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			HashMap<String, Object> mapParam = new HashMap<>();
			mapParam.put("codIstanza", codIstanza);
			mapParam.put("idUtenteAssegnazione", idUtenteAssegnazione);

			String sqlInsStorico = """
insert into FOLIAGE2.FLGISTA_STORICO_GESTORI_TAB(ID_ISTA, ID_UTENTE_GESTORE_PRECEDENTE, ID_STATO_REGISTRATO, DATA_CAMBIO_GESTORE, ID_UTENTE_CAMBIO)
select id_ista, id_utente_compilazione, stato, localtimestamp, :idUtenteAssegnazione
from foliage2.flgista_tab i
where CODI_ISTA =:codIstanza""";

			int nRows = template.update(sqlInsStorico, mapParam);
			if (nRows != 1) {
				throw new FoliageException("Si è verificato un problema nel cambio di gestione");
			}

			String sqlStato = """
update FOLIAGE2.FLGISTA_TAB
set id_utente_compilazione = null
where CODI_ISTA = :codIstanza""";
			mapParam.clear();
			mapParam.put("codIstanza", codIstanza);
			nRows = template.update(sqlStato, mapParam);
			if (nRows != 1) {
				throw new FoliageException("Si è verificato un problema nel cambio di gestione");
			}

			List<Integer> utentiTitolari = getUtentiTitolariIstanza(codIstanza);
			for (Integer userId : utentiTitolari) {
				inserisciNotificaUtente(
					userId,
					"E' stato rimosso il gestore da un'istanza di cui sei il titolare",
					String.format("/istanze/%s", codIstanza)
				);
			}
			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return new Object() {
			public String ok = "ok";
		};
	}

	public Object assegnaGestore(String codIstanza, Integer idUtenteAssegnazione, Integer idUtenteNuovoGestore) throws Exception {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			HashMap<String, Object> mapParam = new HashMap<>();
			mapParam.put("codIstanza", codIstanza);
			mapParam.put("idUtenteAssegnazione", idUtenteAssegnazione);

			String sqlInsStorico = """
insert into FOLIAGE2.FLGISTA_STORICO_GESTORI_TAB(ID_ISTA, ID_UTENTE_GESTORE_PRECEDENTE, ID_STATO_REGISTRATO, DATA_CAMBIO_GESTORE, ID_UTENTE_CAMBIO)
select id_ista, id_utente_compilazione, stato, localtimestamp, :idUtenteAssegnazione
from foliage2.flgista_tab i
where CODI_ISTA =:codIstanza""";

			int nRows = template.update(sqlInsStorico, mapParam);
			if (nRows != 1) {
				throw new FoliageException("Si è verificato un problema nel cambio di gestione");
			}

			String sqlStato = """
update FOLIAGE2.FLGISTA_TAB
set id_utente_compilazione = :idUtenteNuovoGestore
where CODI_ISTA = :codIstanza""";
			mapParam.clear();
			mapParam.put("codIstanza", codIstanza);
			mapParam.put("idUtenteNuovoGestore", idUtenteNuovoGestore);
			nRows = template.update(sqlStato, mapParam);
			if (nRows != 1) {
				throw new FoliageException("Si è verificato un problema nel cambio di gestione");
			}

			inserisciNotificaUtente(
				idUtenteNuovoGestore,
				"Sei diventato il gestore di una nuova istanza",
				String.format("/istanze/%s", codIstanza)
			);
			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return new Object() {
			public String ok = "ok";
		};
	}
	public Object assegnaIstruttore(String codIstanza, Integer idUtenteAssegnazione, Integer idUtenteIstruttore) throws Exception {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			String sqlInsAssegnazione = """
insert into FOLIAGE2.flgassegnazione_istanza_tab(ID_ISTA, DATA_ASSEGNAZIONE, ID_UTENTE_ISTRUTTORE, ID_UTENTE_ASSEGNAZIONE)
select ID_ISTA, localtimestamp, :idUtenteIstruttore, :idUtenteAssegnazione
from FOLIAGE2.FLGISTA_TAB I
where CODI_ISTA =:codIstanza
	and I.STATO = (
		select ID_STATO
		from FOLIAGE2.FLGSTATO_ISTANZA_TAB
		where COD_STATO = 'PRESENTATA'
	)""";

			HashMap<String, Object> mapParam = new HashMap<>();
			mapParam.put("codIstanza", codIstanza);
			mapParam.put("idUtenteIstruttore", idUtenteIstruttore);
			mapParam.put("idUtenteAssegnazione", idUtenteAssegnazione);
			int nRows = template.update(sqlInsAssegnazione, mapParam);

			if (nRows != 1) {
				throw new FoliageException("L'istanza da assegnare non risulta tra quelle presentate");
			}


			String sqlStato = """
update FOLIAGE2.FLGISTA_TAB
set STATO = (
		select ID_STATO
		from FOLIAGE2.FLGSTATO_ISTANZA_TAB
		where COD_STATO = 'ISTRUTTORIA'
	)
where CODI_ISTA = :codIstanza
	and STATO = (
			select ID_STATO
			from FOLIAGE2.FLGSTATO_ISTANZA_TAB
			where COD_STATO = 'PRESENTATA'
		)""";
			mapParam.clear();
			mapParam.put("codIstanza", codIstanza);
			nRows = template.update(sqlStato, mapParam);
			if (nRows != 1) {
				throw new FoliageException("Si è verificato un problema nel presentare l'istanza ");
			}

			inserisciNotificaUtente(
				idUtenteIstruttore,
				"Ti è stata assegnata una nuova istanza da valutare",
				String.format("/cruscotto-pa/%s", codIstanza)
			);
			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return new Object() {
			public String ok = "ok";
		};
	}
	public void eliminaIstanza(String codIstanza) throws Exception {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			HashMap<String, Object> pars = new HashMap<>();
			pars.put("codIstanza", codIstanza);
// 			String delFile1 = """
// delete from foliage2.flgbase64_formio_file_master_tab
// where id_file in (
// 		select id
// 		from foliage2.flgista_tab
// 			join foliage2.flgfiletipo_gestione_tab a using (id_ista)
// 			LEFT JOIN LATERAL (
// 				values (a.id_file_delega_titolarita), 
// 					(a.id_file_autocertificazione_proprieta)
// 			) as t(id) on (true)
// 		where codi_ista = :codIstanza
// 			and stato = (
// 				select id_stato
// 				from foliage2.flgstato_istanza_tab s
// 				where s.cod_stato = 'COMPILAZIONE'
// 			)
// 	)""";
// 			int numRows = update(
// 				delFile1,
// 				pars
// 			);
// 			if (numRows == 0) {
// 				throw new FoliageException("L'istanza richiesta non è stata eliminata poiché non risulta tra quelle in compilazione");
// 			}
			String delFile2 = """
delete from foliage2.flgfiletipo_gestione_tab
where id_ista = (
		select i.id_ista
		from foliage2.flgista_tab i
		where codi_ista = :codIstanza
			and stato = (
				select id_stato
				from foliage2.flgstato_istanza_tab s
				where s.cod_stato = 'COMPILAZIONE'
			)
	)""";
			int numRows = update(
				delFile2,
				pars
			);
			// if (numRows == 0) {
			// 	throw new FoliageException("L'istanza richiesta non è stata eliminata poiché non risulta tra quelle in compilazione");
			// }

			String sql = """
delete from foliage2.flgista_tab
where codi_ista = :codIstanza
	and stato = (
		select id_stato
		from foliage2.flgstato_istanza_tab s
		where s.cod_stato = 'COMPILAZIONE'
	)""";
			numRows = update(
				sql,
				pars
			);
			if (numRows == 0) {
				throw new FoliageException("L'istanza richiesta non è stata eliminata poiché non risulta tra quelle in compilazione");
			}
			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
	}
	public Object revocaIstanza(Integer idUtente, String codIstanza) throws Exception {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {

			String sql = """
delete from foliage2.flgassegnazione_istanza_tab a
	using foliage2.flgista_tab i
where i.codi_ista = :codIstanza
	and i.id_ista = a.id_ista
	and i.stato = (
		select id_stato
		from foliage2.flgstato_istanza_tab s
		where s.cod_stato = 'ISTRUTTORIA'
	)""";
			HashMap<String, Object> pars = new HashMap<>();
			pars.put("codIstanza", codIstanza);
			int numRows = update(
				sql,
				pars
			);
			if (numRows == 0) {
				throw new FoliageException("L'istanza richiesta non è stata eliminata poiché non risulta tra quelle in istruttoria");
			}
			sql = """
update foliage2.flgista_tab
set stato = (
	select id_stato
	from foliage2.flgstato_istanza_tab s
	where s.cod_stato = 'PRESENTATA'
)
where codi_ista = :codIstanza
	and stato = (
		select id_stato
		from foliage2.flgstato_istanza_tab s
		where s.cod_stato = 'ISTRUTTORIA'
	)""";
			numRows = update(
				sql,
				pars
			);

			if (numRows == 0) {
				throw new FoliageException("L'istanza richiesta non è stata eliminata poiché non risulta tra quelle in istruttoria");
			}
			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return null;
	}
	public Object getDatiIstruttoria(String codIstanza) {
		Integer idIstanza = getIdIstanza(codIstanza);


		HashMap<String, Object> pars1 = new HashMap<>();
		pars1.put("codIstanza", codIstanza);
		String sqlDatiIstr = """
select ID_ISTA,
	(DI.ID_ISTA is null) as is_new,
	coalesce(oggetto, 'Parere per l''' || C.DESC_CIST || ' avente codice univoco ' || CODI_ISTA || ' e presentata in data ' ||  to_char(II.DATA_INVIO, 'DD/MM/YYYY')) as oggetto,
	ulteriori_destinatari, testo
from FOLIAGE2.FLGISTA_TAB I
	join FOLIAGE2.FLGTIPO_ISTANZA_TAB TI using (ID_TIPO_ISTANZA)
	join FOLIAGE2.FLGCIST_TAB C on (C.ID_CIST = TI.ID_CIST)
	join FOLIAGE2.FLGISTA_INVIO_TAB II using (ID_ISTA)
	left join FOLIAGE2.FLGISTA_DATI_ISTRUTTORIA_TAB DI using (ID_ISTA)
where CODI_ISTA = :codIstanza""";
		DettagliIstruttoria dati = queryForObject(
			sqlDatiIstr,
			pars1,
			(rs, rn) -> {
				DettagliIstruttoria datiI = new DettagliIstruttoria();
				datiI.isNew = DbUtils.GetBoolean(rs, 0, "IS_NEW");
				datiI.idIstanza = DbUtils.GetInteger(rs, 0, "ID_ISTA");
				datiI.oggetto = rs.getString("oggetto");
				datiI.ulterioriDestinatari = rs.getString("ulteriori_destinatari");
				datiI.testo = rs.getString("testo");
				return datiI;
			}
		);
		pars1.clear();
		pars1 = null;

		HashMap<String, Object> pars = new HashMap<>();
		pars.put("idIstanza", dati.idIstanza);

		String queryValutazione = """
select ESITO_VALUTAZIONE, NOTE_VALUTAZIONE
from foliage2.flgvalutazione_istanza_tab v
where id_ista = :idIstanza""";

		List<ValutazioneIstanza> listValutazione = query(
			queryValutazione,
			pars,
			(rs, rn) -> {
				Boolean esito = DbUtils.GetBoolean(rs, 0, "esito_valutazione");
				String note = rs.getString("note_valutazione");
				ValutazioneIstanza val = new ValutazioneIstanza(esito, note);
				return val;
			}
		);
		Optional<ValutazioneIstanza> val = listValutazione.stream().findFirst();


		String queryFile = """
select ID_RICHIESTE_ISTUTTORIA_ISTANZA, CATEGORIA, TIPO_DOCUMENTO, ID_FILE, NOTE_ISTRUTTORE, NOTE_GESTORE
from foliage2.FLGRICHIESTE_ISTUTTORIA_ISTANZA_TAB R
	left join foliage2.FLGDOCUMENTI_ISTUTTORIA_ISTANZA_TAB D using (ID_RICHIESTE_ISTUTTORIA_ISTANZA)
where ID_ISTA = :idIstanza""";
		List<Pair<ElementoValutazioneIstanza, ElementoValutazioneIstanza>> list = query(
			queryFile, pars,
			(rs, rn) -> {
				Integer idFile = rs.getInt("id_file");
				if (rs.wasNull()) {
					idFile = null;
				}
				final Base64FormioFile[] file = (idFile == null) ? null : DbUtils.getBase64FormioFiles(this, idFile);
				if (file == null) {
					return new Pair<ElementoValutazioneIstanza, ElementoValutazioneIstanza>(
						ElementoValutazioneIstanza.creaRichiesto(
							rs.getInt("id_richieste_istuttoria_istanza"),
							rs.getString("categoria"),
							rs.getString("tipo_documento"),
							rs.getString("note_istruttore")
						),
						null
					);
				}
				else {
					return new Pair<ElementoValutazioneIstanza, ElementoValutazioneIstanza>(
						null,
						ElementoValutazioneIstanza.creaConsegnato(
							rs.getInt("id_richieste_istuttoria_istanza"),
							rs.getString("categoria"),
							rs.getString("tipo_documento"),
							rs.getString("note_istruttore"),
							rs.getString("note_gestore"),
							file
						)
					);
				}
			}
		);
		LinkedList<ElementoValutazioneIstanza> docRichiesti = new LinkedList<>();
		LinkedList<ElementoValutazioneIstanza> docConsegnati = new LinkedList<>();
		if (list != null) {
			for (Pair<ElementoValutazioneIstanza, ElementoValutazioneIstanza> pair : list) {
				ElementoValutazioneIstanza richiesto = pair.getValue0();
				ElementoValutazioneIstanza consegnato = pair.getValue1();
				if (richiesto != null) {
					docRichiesti.addLast(richiesto);
				}
				if (consegnato != null) {
					docConsegnati.addLast(consegnato);
				}
			}
		}

		return new Object() {
			public FileValutazioneIstanza file = new FileValutazioneIstanza(docRichiesti, docConsegnati);
			public ValutazioneIstanza valutazione = val.isPresent() ? val.get() : null;
			public DettagliIstruttoria datiIstr = dati;
		};
	}
	public Object setDatiIstruttoria(
		String codIstanza,
		Integer idUtente,
		String authority,
		DatiIstruttoria dati
	) throws Exception
	{
		FileValutazioneIstanza file = dati.file;
		LinkedList<ElementoValutazioneIstanza> richiesti = file.richiesti;
		LinkedList<ElementoValutazioneIstanza> consegnati = file.consegnati;

		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );
		Object outval = null;
		try {


			HashMap<String, Object> pars = new HashMap<>();
			pars.put("codIstanza", codIstanza);
			String sqlGetId = """
select ID_ISTA, ID_UTENTE_COMPILAZIONE, ID_UTENTE_ISTRUTTORE
from FOLIAGE2.FLGISTA_TAB
	join FOLIAGE2.flgassegnazione_istanza_tab using (ID_ISTA)
where CODI_ISTA = :codIstanza""";
			Triplet<Integer, Integer, Integer> valId = template.queryForObject(
				sqlGetId, pars,
				(rs, rn) -> {
					Integer id = rs.getInt("ID_ISTA");
					if (rs.wasNull()) {
						id =  null;
					}
					Integer idUteComp = rs.getInt("ID_UTENTE_COMPILAZIONE");
					if (rs.wasNull()) {
						idUteComp =  null;
					}
					Integer idUteIstr = rs.getInt("ID_UTENTE_ISTRUTTORE");
					if (rs.wasNull()) {
						idUteIstr =  null;
					}
					return new Triplet<Integer, Integer, Integer>(id, idUteComp, idUteIstr);
				}
			);
			Integer idIstanza = valId.getValue0();
			Integer idCompilatore = valId.getValue1();
			Integer idIstruttore = valId.getValue2();

			if (idIstanza == null) {
				throw new FoliageException("Istanza non trovata");
			}
			pars.clear();
			pars.put("idIstanza", idIstanza);

			if (authority.equals("ISTR") || authority.equals("DIRI")) {
				DettagliIstruttoria dettIstr = dati.dettagli;
				String oggetto = dettIstr.oggetto;
				String testo = dettIstr.testo;
				String uleterioriDest = dettIstr.ulterioriDestinatari;
	
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
				
				pars.remove("oggetto");
				pars.remove("testo");
				pars.remove("uleterioriDest");

				String sqlDel = """
delete from foliage2.FLGRICHIESTE_ISTUTTORIA_ISTANZA_TAB
where id_ista = :idIstanza
	and id_richieste_istuttoria_istanza = :idFile""";
				if (file.richiestiEliminati != null) {
					for (int idFile : file.richiestiEliminati) {
						pars.put("idFile", idFile);
						nRows = update(sqlDel, pars);
						if (nRows != 1) {
							throw new FoliageException("Problema nell'eliminazione di un allegato richiesto");
						}
					}
				}
				if (file.consegnatiEliminati != null) {
					for (int idFile : file.consegnatiEliminati) {
						pars.put("idFile", idFile);
						nRows = update(sqlDel, pars);
						if (nRows != 1) {
							throw new FoliageException("Problema nell'eliminazione di un allegato consegnato");
						}
					}
				}
				pars.remove("idFile");
				pars.put("idUtente", idUtente);
				String sqlInsRichiesta = """
insert into foliage2.FLGRICHIESTE_ISTUTTORIA_ISTANZA_TAB(
		ID_ISTA, ID_UTENTE_ISTRUTTORE, 
		CATEGORIA, TIPO_DOCUMENTO, NOTE_ISTRUTTORE, DATA_RICHIESTA
	)
	values (
		:idIstanza, :idUtente,
		:categoria, :tipoDocumento, :noteIstruttore, localtimestamp
	)""";
				for (ElementoValutazioneIstanza elem : richiesti) {
					pars.put("categoria", elem.categoria);
					pars.put("tipoDocumento", elem.tipoDocumento);
					pars.put("noteIstruttore", elem.noteIstruttore);
					nRows = update(sqlInsRichiesta, pars);
					if (nRows != 1) {
						throw new FoliageException("Problema nel caricamento di un allegato richiesto");
					}
				}
				inserisciNotificaUtente(
					idCompilatore,
					"Occorre caricare dei nuovi documenti per una domanda che hai in carico",
					String.format("/istanze/%s", codIstanza)
				);
			}
			else {
				pars.put("idUtente", idUtente);
				String sqlInsConsegnato = """
insert into foliage2.FLGDOCUMENTI_ISTUTTORIA_ISTANZA_TAB (
		ID_RICHIESTE_ISTUTTORIA_ISTANZA, ID_UTENTE_GESTORE,
		ID_FILE, NOTE_GESTORE, DATA_CONSEGNA
	)
select ID_RICHIESTE_ISTUTTORIA_ISTANZA, :idUtente,  :idFile, :noteGestore, localtimestamp
from foliage2.FLGRICHIESTE_ISTUTTORIA_ISTANZA_TAB
where ID_RICHIESTE_ISTUTTORIA_ISTANZA = :idRichiesta
	and ID_ISTA = :idIstanza""";
				for (ElementoValutazioneIstanza elem : richiesti) {
					Integer idFile = DbUtils.saveBase64FormioFiles(this, elem.allegato);
					pars.put("idRichiesta", elem.idRichiesta);
					pars.put("idFile", idFile);
					pars.put("noteGestore", elem.note);
					int nRows = update(sqlInsConsegnato, pars);
					if (nRows != 1) {
						throw new FoliageException("Problema nel caricamento di un allegato consegnato");
					}
				}
				inserisciNotificaUtente(
					idIstruttore,
					"I documenti che avevi richiesto per un'istanza sono stati caricati",
					String.format("/cruscotto-pa/%s", codIstanza)
				);
			}
			outval = getDatiIstruttoria(codIstanza);
			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return outval;
	}
	public Object valutazioneIstanza(
		String codIstanza,
		Integer idUtente,
		String authority,
		ValutazioneIstanza valutazione
	) throws Exception
	{
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );
		Object outval = null;
		try {
			HashMap<String, Object> pars = new HashMap<>();
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


			pars.put("idUtente", idUtente);
			pars.put("esito", valutazione.esito);
			pars.put("noteValutazione", valutazione.noteValutazione);
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
	and STATO = (
			select id_stato
			from foliage2.flgstato_istanza_tab s
			where s.cod_stato = 'ISTRUTTORIA'
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

			pars.put("codStato", valutazione.esito ? "APPROVATA" : "RESPINTA");
			String sqlAggStato = """
update foliage2.flgista_tab
set stato = (
		select id_stato
		from foliage2.flgstato_istanza_tab s
		where s.cod_stato = :codStato
	)
where codi_ista = :codIstanza
	and stato = (
			select id_stato
			from foliage2.flgstato_istanza_tab s
			where s.cod_stato = 'ISTRUTTORIA'
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
				String.format("Un'istanza che avevi presentato è stata %s", (valutazione.esito ? "approvata" : "respinta")),
				String.format("istanze/%s/consulta-valutazione", codIstanza)
			);

			outval = getDatiIstruttoria(codIstanza);
			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return outval;
	}
	public Object comunicaInizio(String codIstanza, Integer idUtente, LocalDate dataInizio) throws Exception{
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );
		Object outval = null;
		try {
			HashMap<String, Object> pars = new HashMap<>();

			pars.put("idUtente", idUtente);
			pars.put("codIstanza", codIstanza);
			pars.put("dataInizio", dataInizio);

			String sqlInsValutazione = """
insert into foliage2.flgdate_inizio_lavori_istanza_tab (
		ID_ISTA, DATA_INIZIO_LAVORI, DATA_COMUNICAZIONE_INIZIO_LAVORI, ID_UTENTE_COMUNICAZIONE_INIZIO_LAVORI
	)
select ID_ISTA, :dataInizio, localtimestamp, :idUtente
from foliage2.FLGISTA_TAB i
	join foliage2.flgvalutazione_istanza_tab using (ID_ISTA)
where CODI_ISTA = :codIstanza
	and DATA_FINE_VALIDITA >= current_date
	and DATA_FINE_VALIDITA >= :dataInizio""";
			int numRows = update(sqlInsValutazione, pars);
			if (numRows != 1) {
				throw new FoliageException("L'istanza indicata non risultata tra quelle valide");
			}

			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return outval;

	}

	
	public Object comunicaFine(String codIstanza, Integer idUtente, LocalDate dataFine) throws Exception{
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );
		Object outval = null;
		try {
			HashMap<String, Object> pars = new HashMap<>();

			pars.put("idUtente", idUtente);
			pars.put("codIstanza", codIstanza);
			pars.put("dataFine", dataFine);

			String sqlInsValutazione = """
insert into foliage2.flgdate_fine_lavori_istanza_tab (
		ID_ISTA, DATA_FINE_LAVORI, DATA_COMUNICAZIONE_FINE_LAVORI, ID_UTENTE_COMUNICAZIONE_FINE_LAVORI
	)
select ID_ISTA, :dataFine, localtimestamp, :idUtente
from foliage2.FLGISTA_TAB i
	join foliage2.flgvalutazione_istanza_tab using (ID_ISTA)
	join foliage2.flgdate_inizio_lavori_istanza_tab using (ID_ISTA)
where CODI_ISTA = :codIstanza
	and DATA_FINE_VALIDITA >= current_date
	and :dataFine >= DATA_INIZIO_LAVORI
	and DATA_FINE_VALIDITA >= :dataFine""";
			int numRows = update(sqlInsValutazione, pars);
			if (numRows != 1) {
				throw new FoliageException("L'istanza indicata non risultata tra quelle valide");
			}

			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return outval;
	}
	public Object prorogaIstanza(String codIstanza, Integer idUtente, ProrogaIstanza body) throws Exception {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );
		Object outval = null;
		try {
			HashMap<String, Object> pars = new HashMap<>();

			Integer durata = body.durataProroga;
			
			Integer idFile = DbUtils.saveBase64FormioFiles(this, body.bollo);

			pars.put("idUtente", idUtente);
			pars.put("codIstanza", codIstanza);
			pars.put("durata", durata);
			pars.put("idFile", idFile);
			pars.put("motivazione", body.motivazione);


			String sqlInsProroga = """
insert into foliage2.FLGISTA_PROROGA_TAB (
		ID_ISTA, MESI_DURATA, ID_FILE_PAGAMENTO, MOTIVAZIONE, DATA_PROROGA, UTENTE_PROROGA
	)
select ID_ISTA, :durata, :idFile, :motivazione, localtimestamp, :idUtente
from foliage2.FLGISTA_TAB
where codi_ista = :codIstanza""";
			int numRows = update(sqlInsProroga, pars);
			if (numRows != 1) {
				throw new FoliageException("L'istanza indicata non risultata tra quelle valide");
			}

			platformTransactionManager.commit(status);
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return outval;
	}

	interface CellProcedure extends BiProcedure<String, Cell> {

	}
	class CellProcedureRun {
		public CellProcedureRun(String s, CellProcedure p) {
			this.name = s;
			this.proc = p;
		}
		public String name;
		public CellProcedure proc;
	}
	public Workbook GetReportP4(Integer idUtente, String authority, String authScope/* , Integer idEnte*/) throws Exception {
		if ("AMMI".equals(authority)) {

		}
		else {
			if ("RESP".equals(authority)) {
				if ("TERRIRORIALE".equals(authScope)) {
					// if (verificaRuoloInEnte(idUtente, authority, authScope, idEnte)) {
					// }
					// else {
					// 	throw new FoliageAuthorizationException("Il profilo utilizzato non è autorizzato ad effettuare questa richiesta");
					// }
					throw new FoliageAuthorizationException("Il profilo utilizzato non è autorizzato ad effettuare questa richiesta");
				}
				else {
					throw new FoliageAuthorizationException("Il profilo utilizzato non è autorizzato ad effettuare questa richiesta");
				}
			}
			else {
				// if ("DIRI".equals(authority)) {
				// 	if ("TERRIRORIALE".equals(authScope)) {
				// 		if (verificaRuoloInEnte(idUtente, authority, authScope, idEnte)) {
				// 		}
				// 		else {
				// 			throw new FoliageAuthorizationException("Il profilo utilizzato non è autorizzato ad effettuare questa richiesta");
				// 		}
				// 	}
				// 	else {
				// 		throw new FoliageAuthorizationException("Il profilo utilizzato non è autorizzato ad effettuare questa richiesta");
				// 	}
				// }
				// else {
				// 	throw new FoliageAuthorizationException("Il profilo utilizzato non è autorizzato ad effettuare questa richiesta");
				// }
				throw new FoliageAuthorizationException("Il profilo utilizzato non è autorizzato ad effettuare questa richiesta");
			}
		}
		String modelPath = "reportModels/P4.xlsx";
		ClassLoader classLoader = WebController.class.getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(modelPath);
		
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		XSSFSheet sheet = workbook.getSheetAt(0);


		String sqlQuery = """
select INDICATORE,
	count(*) as numero_istanze,
	sum(case when esito then 1 end) as numero_istanze_autorizzate,
	sum(case when not esito then 1 end) as numero_istanze_non_autorizzate,
	sum(case when esito then supe end) as supe_istanze_autorizzate,
	sum(supe_privata) as supe_privata,
	sum(supe_pubblica) as supe_pubblica,
	sum(supe_uso_civico) as supe_uso_civico,
	sum(supe_altro) as supe_altro,
	sum(supe_ceduo) as supe_ceduo,
	sum(supe_fustaia) as supe_fustaia,
	sum(supe_misto) as supe_misto
from (
		select i.id_ente_terr,
			case when cod_tipo_istanza_specifico in ('SOPRA_SOGLIA', 'IN_DEROGA') then 'A' else 'B' end as INDICATORE,
			coalesce(v.esito_valutazione, false) as esito,
			--pf.superficie_pfor as supe,
			supe_uo as supe,
			case when n.desc_nprp = 'P.Privata' then supe_uo end as supe_privata,
			case when n.desc_nprp = 'P.Pubblica' then supe_uo end as supe_pubblica,
			case when 1 = 0 then supe_uo end as supe_uso_civico,
			case when coalesce(n.desc_nprp, '') not in ('P.Privata', 'P.Pubblica') then supe_uo end as supe_altro,
			supe_ceduo, supe_fustaia, supe_misto
		from foliage2.flgista_tab i
			join foliage2.flgtipo_istanza_tab using (id_tipo_istanza)
			join foliage2.flgnprp_tab n using (id_nprp)
			left join foliage2.flgvalutazione_istanza_tab v using (id_ista)
			--left join (
			--	select id_ista, sum(superficie) as superficie_pfor
			--	from flgparticella_forestale_shape_tab
			--	group by id_ista
			--) pf using (id_ista)
			left join (
				select id_ista,
					sum(round((superficie_utile/10000), 2)) as supe_uo,
					coalesce(sum(case when desc_gove = 'Ceduo' then round((superficie_utile/10000), 2) end), 0) as supe_ceduo,
					coalesce(sum(case when desc_gove = 'Fustaia' then round((superficie_utile/10000), 2) end), 0) as supe_fustaia,
					coalesce(sum(case when coalesce(desc_gove, 'Misto') = 'Misto' then round((superficie_utile/10000), 2) end), 0) as supe_misto
				from foliage2.flgunita_omogenee_tab fot
				group by id_ista
				union all	
				select id_ista, superficie_intervento as supe_uo,
					coalesce(case when desc_gove = 'Ceduo' then superficie_intervento end, 0) as supe_ceduo,
					coalesce(case when desc_gove = 'Fustaia' then superficie_intervento end, 0) as supe_fustaia,
					coalesce(case when coalesce(desc_gove, 'Misto') = 'Misto' then superficie_intervento end, 0) as supe_misto
				from foliage2.flgista_taglio_boschivo_tab ftbt
			) uo using (id_ista)
		--where id_ente_terr = :idEnte
		where cod_tipo_istanza_specifico in ('SOPRA_SOGLIA', 'IN_DEROGA', 'TAGLIO_BOSCHIVO', 'ATTUAZIONE_PIANI')
	) as T
group by INDICATORE""";
		HashMap<String, Object> parsMap = new HashMap<>();
		//parsMap.put("idEnte", idEnte);
		SqlRowSet rs = queryForRowSet(sqlQuery, parsMap);
		
		CellProcedure setInt = (name, sheetCell) -> {
			Integer value = rs.getInt(name);
			if (rs.wasNull()) {
				value = null;
			}
			else {
				sheetCell.setCellValue(value);
			}
		};
		CellProcedure setDecimal = (name, sheetCell) -> {
			Double value = rs.getDouble(name);
			if (rs.wasNull()) {
				value = null;
			}
			else {
				sheetCell.setCellValue(value);
			}
		};

		CellProcedureRun[] settersArr = new CellProcedureRun[] {
			new CellProcedureRun(
				"numero_istanze",
				setInt
			),
			new CellProcedureRun(
				"numero_istanze_autorizzate",
				setInt
			),
			new CellProcedureRun(
				"numero_istanze_non_autorizzate",
				setInt
			),
			new CellProcedureRun(
				"supe_istanze_autorizzate",
				setDecimal
			),
			new CellProcedureRun(
				"supe_privata",
				setDecimal
			),
			new CellProcedureRun(
				"supe_pubblica",
				setDecimal
			),
			new CellProcedureRun(
				"supe_uso_civico",
				setDecimal
			),
			new CellProcedureRun(
				"supe_altro",
				setDecimal
			),
			new CellProcedureRun(
				"supe_ceduo",
				setDecimal
			),
			new CellProcedureRun(
				"supe_fustaia",
				setDecimal
			),
			new CellProcedureRun(
				"supe_misto",
				setDecimal
			)
		};

		while (rs.next()) {
			String indicatore = rs.getString("indicatore");
			int idxRow = ("A".equals(indicatore)) ? 3 : 4;
			Row r = sheet.getRow(idxRow);
			Cell cell = r.getCell(1);
			cell.setCellValue(codRegione);
			cell = r.getCell(2);
			cell.setCellValue(2023);

			int idxCol = 4;
			for (CellProcedureRun cpr : settersArr) {
				cpr.proc.eval(cpr.name, r.getCell(idxCol++));
			}
		}
		
		// sheet.autoSizeColumn(1, true);
		// sheet.autoSizeColumn(2, true);
		// int idxCol = 4;
		// for (CellProcedureRun cpr : settersArr) {
		// 	//cpr.proc.eval(cpr.name, r.getCell(idxCol));
		// 	// sheet.getCo
		// 	sheet.autoSizeColumn(idxCol++, true);
		// }
		return workbook;
	}

	public Integer getIdIstanza(String codIstanza) {
		String sql = """
select id_ista
from foliage2.flgista_tab
where codi_ista = :codIstanza""";
		HashMap<String, Object> map = new HashMap<>();
		map.put("codIstanza", codIstanza);
		return queryForObject(
			sql, map,
			(rs, rn) -> {
				Integer value = rs.getInt("id_ista");
				return (rs.wasNull()) ? null : value;
			}
		);
	}


	public <T> List<T> getFileIstanza(Integer idIsta, RowMapper<T> rm) {
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
select 'Allegati del titolare' as tipo, 'Delega al Professionista' as categoria, null as desc, id_file_delega as id_file
from foliage2.flgista_tab 
	join foliage2.flgtitolare_istanza_tab using (id_titolare)
where id_ista = :idIsta
	and id_file_delega is not null
union all
select 'Allegato Gestione' as tipo, categoria, null as desc, id_file
from foliage2.flgfiletipo_gestione_tab a
	LEFT JOIN LATERAL (
			values ('Autocertificazione Proprietà', a.id_file_autocertificazione_proprieta),
				--('Delega presentazione', a.id_file_delega_presentazione),
				('Delega titolarità dai comproprietari', a.id_file_delega_titolarita),
				('Atto di nomina come rappresentante legale', a.id_file_atto_nomina_rappresentante_legale),
				('Provvedimento finale di sostituzione e conferimento boschi silenti', a.id_file_provvedimento_boschi_silenti),
				('Autocertificazione come ditta forestale', a.id_file_autocertificazione_ditta_forestale),
				('Documenti di identità', a.id_file_documenti_identita)
		) as t(categoria, id_file) on (true)
where id_ista = :idIsta
	and id_file is not null
union all
select 'Elaborato VIncA' as tipo, null, null as desc, id_file_vinca as id_file
from foliage2.flgista_elaborato_vinca_tab
where id_ista = :idIsta
union all
select 'Documento d''identità per verifica firma olografa' as tipo, 'Invio istanza' as categoria, null, ID_FILE_DOC_IDENTITA
from foliage2.FLGISTA_INVIO_TAB
where id_ista = :idIsta
	and ID_FILE_DOC_IDENTITA is not null
union all
select 'Bollo Invio' as tipo, 'Invio istanza', null as desc, id_file_ricevute as id_file
from foliage2.flgista_invio_tab
where id_ista = :idIsta
union all
select 'Diritti di Istruttoria' as tipo, 'Invio istanza', null as desc, id_file_diritti_istruttoria as id_file
from foliage2.flgista_invio_tab
where id_ista = :idIsta
union all
select 'Bollo Proroga' as tipo, 'Richiesta Proroga', null as desc, id_file_pagamento as id_file
from foliage2.flgista_proroga_tab
where id_ista = :idIsta""";
		HashMap<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("idIsta", idIsta);
		return query(query, mapParam, rm);
	}

	public List<FileIstanzaApp> getFileIstanzaApp(Integer idIsta) {
		return getFileIstanza(idIsta, FileIstanzaApp.RowMapper(this));
	}

	
	public List<FileIstanzaWeb> getFileIstanzaWeb(Integer idIsta) {
		return getFileIstanza(idIsta, FileIstanzaWeb.RowMapper(this));
	}

	public Object getModulisticaIstanza(String codIstanza) {
		HashMap<String, Object> mapMain = new HashMap<String, Object>();
		mapMain.put("codIstanza", codIstanza);
		String queryMain = """
select id_ista, c.cod_tipo_istanza = 'SOTTO_SOGLIA' as is_sottosoglia, i.note, s.cod_stato = 'COMPILAZIONE' as is_bozza
from foliage2.flgista_tab i
	join foliage2.flgtipo_istanza_tab t using (id_tipo_istanza)
	join foliage2.flgcist_tab c on (c.id_cist = t.id_cist)
	join foliage2.flgstato_istanza_tab s on (s.id_stato = i.stato)
where codi_ista = :codIstanza""";

		Quartet<Integer, Boolean, Boolean, String> res = template.queryForObject(
			queryMain, mapMain,
			(rs, n) -> {
				Integer id = rs.getInt("id_ista");
				if (rs.wasNull()) {
					id = null;
				}
				Boolean isSotto = rs.getBoolean("is_sottosoglia");
				if (rs.wasNull()) {
					isSotto = null;
				}
				Boolean isBozza = DbUtils.GetBoolean(rs, 0, "is_bozza");

				String note = rs.getString("note");
				Quartet<Integer, Boolean, Boolean, String> outVal = new Quartet<Integer, Boolean, Boolean, String>(
					id, isSotto, isBozza, note
				);
				return outVal;
			}
		);
		Integer idIsta = res.getValue0();
		Boolean isSotto = res.getValue1();
		Boolean isBozza1 = res.getValue2();
		String noteStr = res.getValue3();

		String queryUOG = """
select nome_uog as nome, ST_AsText(shape) as geometria, superficie
from foliage2.flgunita_omogenee_tab
where id_ista = :idIsta""";

		String queryAST = """
select nome_strato as nome, ST_AsText(shape) as geometria, superficie_strato as superficie
from foliage2.FLGSTRATI_ISTA_TAB
where id_ista = :idIsta
	and is_area_saggio_tradizionale""";
	
		String queryAD = """
select nome_strato as nome, ST_AsText(shape) as geometria, superficie_strato as superficie
from foliage2.FLGSTRATI_ISTA_TAB
where id_ista = :idIsta
	and is_area_dimostrativa""";
		
		String queryImp = """
select nome_strato as nome, ST_AsText(shape) as geometria, superficie_strato as superficie
from foliage2.FLGSTRATI_ISTA_TAB
where id_ista = :idIsta
	and is_imposto""";	

		String queryPF = """
select '' as nome, ST_AsText(ST_UNION(P.SHAPE)) as geometria, sum(superficie) as superficie
from foliage2.flgparticella_forestale_shape_tab P
where id_ista = :idIsta
having count(*) > 0""";
	
		String queryViab = """
select cod_tipo_viabilita as nome, ST_AsText(SHAPE) as geometria, null as superficie
from foliage2.flgviabilita_ista_tab
where id_ista = :idIsta""";
		
		String queryTavole = """
select prog_tavola, id_file_tavola
from foliage2.flgtavole_istanza_tab
where id_ista = :idIsta""";
		HashMap<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("idIsta", idIsta);
		if (isSotto) {
			return new Object() {
				public List<FileIstanzaWeb> files = getFileIstanzaWeb(idIsta);
				public Boolean isSottosoglia = isSotto;
				public Boolean isBozza = isBozza1;
				public String note = noteStr;
			};
		}
		else {
			List<Pair<Integer, Integer>> idFileTavoleList = this.query(queryTavole, mapParam,
				DbUtils.GetIntegerPairRowMapper("prog_tavola", "id_file_tavola")
			);
			HashMap<Integer, Base64FormioFile[]> tavoleOut = new HashMap<>();
			for (Pair<Integer, Integer> pair : idFileTavoleList) {
				if (pair.getValue0() != null && pair.getValue1() != null) {
					tavoleOut.put(pair.getValue0(), DbUtils.getBase64FormioFiles(this, pair.getValue1()));
				}
			}
			
			return new Object() {
				public List<FileIstanzaWeb> files = getFileIstanzaWeb(idIsta);
				public Boolean isSottosoglia = isSotto;
				public Boolean isBozza = isBozza1;
				public String note = noteStr;
				public HashMap<Integer, Base64FormioFile[]> tavole = tavoleOut;
				public Object pfor = new Object() {
					public String shapeSrid = String.format("EPSG:%d", sridGeometrie);
					public String shapeField = "geometria";
					public String labelField = null;
					public List<EntitaGeometrica> array = query(queryPF, mapParam, EntitaGeometrica.RowMapper());
				};
	
				public Object uo = new Object() {
					public String shapeSrid = String.format("EPSG:%d", sridGeometrie);
					public String shapeField = "geometria";
					public String labelField = "nome";
					public List<EntitaGeometrica> array = query(queryUOG, mapParam, EntitaGeometrica.RowMapper());
				};
				
				public Object ast = new Object() {
					public String shapeSrid = String.format("EPSG:%d", sridGeometrie);
					public String shapeField = "geometria";
					public String labelField = "nome";
					public List<EntitaGeometrica> array = query(queryAST, mapParam, EntitaGeometrica.RowMapper());
				};
				
				public Object ad = new Object() {
					public String shapeSrid = String.format("EPSG:%d", sridGeometrie);
					public String shapeField = "geometria";
					public String labelField = "nome";
					public List<EntitaGeometrica> array = query(queryAD, mapParam, EntitaGeometrica.RowMapper());
				};
	
				public Object imp = new Object() {
					public String shapeSrid = String.format("EPSG:%d", sridGeometrie);
					public String shapeField = "geometria";
					public String labelField = "nome";
					public List<EntitaGeometrica> array = query(queryImp, mapParam, EntitaGeometrica.RowMapper());
				};
				
				public Object viab = new Object() {
					public String shapeSrid = String.format("EPSG:%d", sridGeometrie);
					public String shapeField = "geometria";
					public String labelField = null;
					public List<EntitaGeometrica> array = query(queryViab, mapParam, EntitaGeometrica.RowMapper());
				};
			};
		}
	}
	public void creaModuloPdfIstruttoria(String codIstanza, OutputStream outputStream) throws Exception {
		HashMap<String, Object> parsMap = new HashMap<>();
			parsMap.put("codIstanza", codIstanza);

		ModuloIstruttoria m = queryForObject(
			ModuloIstruttoria.queryFromCodiIsta,
			parsMap,
			ModuloIstruttoria.RowMapper(this, codIstanza)
		);
		m.creaPdf(outputStream);
	}
	public void creaModuloPdfIstanza(String codIstanza, OutputStream outputStream, Boolean isBozza) throws Exception
	{
		HashMap<String, Object> parsMap = new HashMap<>();
			parsMap.put("codiIsta", codIstanza);

		ModuloIstanza m = queryForObject(
			ModuloIstanza.queryFromCodiIsta,
			parsMap,
			ModuloIstanza.RowMapper(this, codIstanza, codRegione)
		);
		m.creaPdf(outputStream, isBozza);
	}
	public void creaModuloPdfIstanza(String codIstanza, OutputStream outputStream) throws Exception {
		creaModuloPdfIstanza(codIstanza, outputStream, null);
	}
	public Object getProvinciaComuneAsSet(Integer idComune) throws Exception {
		HashMap<String, Object> parsMap = new HashMap<>();
			parsMap.put("idComune", idComune);
		String query = """
select id_provincia as id_prov, provincia as desc_prov
from foliage2.flgprov_viw p
where p.id_provincia = (
		select id_provincia
		from foliage2.flgente_comune_tab  c
		where id_comune = :idComune
	)
order by desc_prov""";
		return queryForRowSet(query, parsMap);
	}
	public Object getProvinciaAsSet(Integer idProvincia) throws Exception {
		HashMap<String, Object> parsMap = new HashMap<>();
			parsMap.put("idProvincia", idProvincia);
		String query = """
select id_provincia as id_prov, provincia as desc_prov
from foliage2.flgprov_viw p
where p.id_provincia = :idProvincia
order by desc_prov""";
		return queryForRowSet(query, parsMap);
	}
	public Object getComuneAsSet(Integer idComune) throws Exception {
		HashMap<String, Object> parsMap = new HashMap<>();
			parsMap.put("idComune", idComune);
		String query = """
select ID_COMUNE as ID_COMU, COMUNE as DESC_COMU
from foliage2.flgcomu_viw c
where c.id_comune = :idComune""";
		return queryForRowSet(query, parsMap);
	}
	
	public Pair<String, Integer> getInfoEnteIstanza(String codIstanza) throws Exception {
		HashMap<String, Object> parsMap = new HashMap<>();
			parsMap.put("codIstanza", codIstanza);
		String queryTipo = """
select er.id_ente, er.tipo_ente
from foliage2.flgista_tab i
	join foliage2.flgente_root_tab er on (er.id_ente = i.id_ente_terr)
where i.codi_ista = :codIstanza""";
		Pair<String, Integer> res1 = queryForObject(
			queryTipo, parsMap,
			(rs, rn) -> {
				Integer id = rs.getInt("id_ente");
				if (rs.wasNull()) {
					id = null;
				}
				final Integer idFin = id;
				return new Pair<String, Integer>(
					rs.getString("tipo_ente"),
					idFin
				);
			}
		);
		return res1;
	}
	public Object getProvincieIstanza(String codIstanza) throws Exception {
		Pair<String, Integer> res1 = getInfoEnteIstanza(codIstanza);
		Object outVal = null;
		switch (res1.getValue0()) {
			case "REGIONE": {
				outVal = this.GetProvincieRegioneHost();
			}; break;
			case "PROVINCIA": {
				outVal = this.getProvinciaAsSet(res1.getValue1());
			}; break;
			case "COMUNE": {
				outVal = this.getProvinciaComuneAsSet(res1.getValue1());
			}
		}
		return outVal;
		// String query = """
				
		// 		""";
		// return queryForRowSet(query, parsMap);
	}
	
	public Object getComuniIstanza(String codIstanza, Integer idProvincia) throws Exception {
		Pair<String, Integer> res1 = getInfoEnteIstanza(codIstanza);
		Object outVal = null;
		switch (res1.getValue0()) {
			case "REGIONE": {
				outVal = this.GetComuniProvincia(idProvincia);
			}; break;
			case "PROVINCIA": {
				if ( idProvincia != null && idProvincia.equals(res1.getValue1()) ) {
					outVal = this.GetComuniProvincia(idProvincia);
				}
				else {
					throw new FoliageException("La provincia indicata non è compatibile con l'istanza");
				}
			}; break;
			case "COMUNE": {
				outVal = this.getComuneAsSet(res1.getValue1());
			}
		}
		return outVal;
	}
	public Integer salvaTavolaIstanza(String codIstanza, Integer idxTavola, Base64FormioFile[] fileTavola, Integer idUtente) throws Exception {
		if (idxTavola > 3) {
			throw new FoliageException(String.format("Non è possibile specificare la tavola %d", idxTavola +1));
		}
		Integer outVal = null;
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			Integer idFileTavola = DbUtils.saveBase64FormioFiles(this, fileTavola);
			if (idFileTavola == null) {
				throw new FoliageException("Si è verificato un problema nel caricamento del file");
			}
			HashMap<String, Object> parsMap = new HashMap<>(){{
				put("codIstanza", codIstanza);
				put("idxTavola", idxTavola);
				put("idFileTavola", idFileTavola);
				put("idUtente", idUtente);

			}};
			String queryTipo = """
insert into foliage2.flgtavole_istanza_tab(id_ista, prog_tavola, id_file_tavola, id_utente, data_caricamento)
select i.id_ista, :idxTavola, :idFileTavola, :idUtente, localtimestamp
from foliage2.flgista_tab i
where i.codi_ista = :codIstanza
returning id_tavola_ista as id_tavola_ista""";
			
			outVal = queryForObject(
				queryTipo, parsMap,
				DbUtils.GetIntegerRowMapper("id_tavola_ista")
			);

			platformTransactionManager.commit(status);
		}catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
		return outVal;
	}
	public int deleteBase64FormIoFile(Integer idFile) throws Exception {
		String query = """
delete from foliage2.flgbase64_formio_file_master_tab
where id_file = :idFile""";
		Map<String, Object> pars = new HashMap<>() {{
			put("idFile", idFile);
		}};
		return this.update(query, pars);
	}
	public void eliminaTavolaIstanza(String codIstanza, Integer idxTavola) throws Exception {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			String deleteCmd = """
delete from foliage2.flgtavole_istanza_tab
where id_ista = (
		select i.id_ista
		from foliage2.flgista_tab i
		where i.codi_ista = :codIstanza
	)
	and prog_tavola = :idxTavola
returning id_file_tavola as id_file_tavola""";
			HashMap<String, Object> delPars = new HashMap<>() {{
				put("codIstanza", codIstanza);
				put("idxTavola", idxTavola);
			}};
			Integer idFile = this.queryForObject(deleteCmd, delPars, DbUtils.GetIntegerRowMapper("id_file_tavola"));
			if (idFile == null) {
				throw new FoliageException("La tavola richiesta non è stata trovata");
			}
			else {
				deleteBase64FormIoFile(idFile);
			}
			platformTransactionManager.commit(status);
		}catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
	}

	public List<FoliageReport> getReportDisponibili(String authority, String authScope) {
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("authority", authority);
		pars.put("authScope", authScope);
		return this.query(FoliageReport.queryForReportDisponibili, pars, FoliageReport.getRowMapperWithEsecuzioni(this));
	}

	// interface RowSetFetch extends it.almaviva.foliage.function.BiFunction<Object, SqlRowSet, String> {
	// }
	// public static Object getSqlRowsetValue(SqlRowSet rs, int rn, ) {
	// 	DbUtils
	// }


	// class RowSetFieldFetch {
	// 	public TriFunction fetcher;
	// 	public String columnName;
	// 	public String Header;
	// }


	// class RowSetFetch {
	// 	public Integer startRow;
	// 	public HashMap<Integer;
	// 	public RowSetFieldFetch[] fields;
	// }
	public void buildReport(
		String codReport, String formato, LocalDate data,
		Integer idUtente, String authority, String authScope,
		OutputStream outputStream
	) throws Exception {
		boolean isAmmi = "AMMI".equals(authority);
		boolean isResp = !isAmmi && ("RESP".equals(authority) && "TERRITORIALE".equals(authScope));
		if (isAmmi || isResp) {
			
			LinkedList<ReportBuiler> builders = new LinkedList<>();
			HashMap<String, Object> pars = new HashMap<>();
			pars.put("dataRife", data);
			if (isResp) {
				pars.put("idUtente", idUtente);
				pars.put("authority", authority);
				pars.put("authScope", authScope);
			}
			switch (codReport) {
				case "AUTO_ACCETTAZIONE": {
					ReportBuiler builder = isAmmi ? ReportBuiler.ReportAutoaccettazioneAmmi : ReportBuiler.ReportAutoaccettazioneResp;
					builders.add(builder);
				}; break;
				case "P1_M": {
					if ("csv".equals(formato)) {
						ReportBuiler builder = isAmmi ? ReportBuiler.ReportP1Ammi : ReportBuiler.ReportP1Resp;
						builders.add(builder);
					}
					else {
						ReportBuiler builder1 = isAmmi ? ReportBuiler.ReportP1Ammi : ReportBuiler.ReportP1Resp;
						builders.add(builder1);
						ReportBuiler builder = isAmmi ? ReportBuiler.ReportP1AmmiAgg : ReportBuiler.ReportP1RespPdf;
						builders.add(builder);
					}
					PeriodDuration pd = PeriodDuration.of(Period.ofMonths(1));
					pars.put("durata", DbUtils.GetPgInterval(pd));
				}; break;
				case "P1_A": {
					if ("csv".equals(formato)) {
						ReportBuiler builder = isAmmi ? ReportBuiler.ReportP1Ammi : ReportBuiler.ReportP1Resp;
						builders.add(builder);
					}
					else {
						ReportBuiler builder1 = isAmmi ? ReportBuiler.ReportP1Ammi : ReportBuiler.ReportP1Resp;
						builders.add(builder1);
						ReportBuiler builder = isAmmi ? ReportBuiler.ReportP1AmmiAgg : ReportBuiler.ReportP1RespPdf;
						builders.add(builder);
					}
					PeriodDuration pd = PeriodDuration.of(Period.ofYears(1));
					pars.put("durata", DbUtils.GetPgInterval(pd));
				}; break;
				case "P2_M": {
					ReportBuiler builder = isAmmi ? ReportBuiler.ReportP2Ammi : ReportBuiler.ReportP2Resp;
					builders.add(builder);
					PeriodDuration pd = PeriodDuration.of(Period.ofMonths(1));
					pars.put("durata", DbUtils.GetPgInterval(pd));
					
				}; break;
				case "P2_A": {
					ReportBuiler builder = isAmmi ? ReportBuiler.ReportP2Ammi : ReportBuiler.ReportP2Resp;
					builders.add(builder);
					PeriodDuration pd = PeriodDuration.of(Period.ofYears(1));
					pars.put("durata", DbUtils.GetPgInterval(pd));
				}; break;
				case "P3_NAT1": {
					if (isAmmi)  {
						pars.put("sridGeometrie", sridGeometrie);
						if ("GeoJSON".equals(formato)) {
							ReportBuiler builder = ReportBuiler.ReportP3Nat1GeoJson;
							builders.add(builder);
						}
						else {
							ReportBuiler builder = ReportBuiler.ReportP3Nat1;
							builders.add(builder);
						}
					}
					else {
						throw new FoliageAuthorizationException("Il profilo utilizzato non è autorizzato ad effettuare questa richiesta");
					}
				}; break;
				case "P3_NAT2": {
					if (isAmmi) {
						pars.put("sridGeometrie", sridGeometrie);
						if ("GeoJSON".equals(formato)) {
							ReportBuiler builder = ReportBuiler.ReportP3Nat2GeoJson;
							builders.add(builder);
						}
						else {
							ReportBuiler builder = ReportBuiler.ReportP3Nat2;
							builders.add(builder);
						}
					}
					else {
						throw new FoliageAuthorizationException("Il profilo utilizzato non è autorizzato ad effettuare questa richiesta");
					}
				}; break;
				case "P4": {
					ReportBuiler builder = ReportBuiler.GetReportP4(this, data.getYear());
					builders.add(builder);
					//rs = GetReportP4(data, idUtente, authority, authScope);
					// xlsxTemplate = "reportModels/P4.xlsx";
					// shapeColumn = "shape";
				}; break;
				default: {
					throw new FoliageException("Tipologia di report non gestita");
				}
			}
			XSSFWorkbook workbook = null;
			
			PdfWriter writer = new PdfWriter(outputStream);
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc, PageSize.A4.rotate());
			if ("pdf".equals(formato)) {
				writer = new PdfWriter(outputStream);
				pdfDoc = new PdfDocument(writer);
				document = new Document(pdfDoc, PageSize.A4.rotate());
				
				PdfFont helveticaFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				PdfFont helveticaBoldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				document.setMargins(72, 36, 72, 36);
				document.setFont(helveticaFont);
			}
			boolean isFirst = true;
			for (ReportBuiler builder : builders) {

				builder.prepareRowSet(this, pars);	
				switch (formato) {
					case "csv": {
						builder.writeToCsv(outputStream);
					}; break;
					case "GeoJSON": {
						builder.writeToGeoJSON(outputStream);
					}; break;
					case "xlsx": {
						workbook = builder.getXlsxReport(workbook);
					}; break;
					case "pdf": {
						//builder.writeToPdf(outputStream);
						if (isFirst) {
							isFirst = false;
						}
						else {
							document.add(new AreaBreak());
						}
						builder.writeToPdf(document);
					}; break;
	
					default: {
						// XSSFWorkbook wb = builder.getXlsxReport();
						// switch (formato) {
						// 	case "pdf": {
						// 		ReportBuiler.addXlsxWorkbookToPdf(wb, outputStream);
						// 	}; break;
						// 	case "xlsx": {
						// 		wb.write(outputStream);
						// 		wb.close();
						// 	}; break;
	
						// }
						// if ("pdf".equals(formato)) {
	
						// }
					}; break;
				}
			}

			if (workbook != null && "xlsx".equals(formato)) {
				workbook.write(outputStream);
				workbook.close();
			}
			
			if ("pdf".equals(formato)) {
				document.close();
				pdfDoc.close();
			}
		}
		else {
			throw new FoliageAuthorizationException("Il profilo utilizzato non è autorizzato ad effettuare questa richiesta");
		}
	}


	public SqlRowSet getTipoElaborazioniGovernance() {
		String sql = """
select b.id_batch, b.cod_batch, b.desc_batch
from foliage2.flgconf_batch_tab b
where b.id_batch = any (
		select r.id_batch 
		from foliage2.flgconf_batch_report_tab r
	)""";
		HashMap<String, Object> pars = new HashMap<>();
		SqlRowSet rs = this.queryForRowSet(sql, pars);

		
		// while(rs.next()) {
		// 	System.out.println("Riga");
		// }
		// rs.beforeFirst();

		return rs;
	}

	public SqlRowSet getElaborazioniGovernance() {
		String sql = """
select bd.id_batch_ondemand, b.cod_batch, data_rife,
	bd.data_avvio as data_di_esecuzione_richiesta, 
	u.codi_fisc||': '||u.cognome||' '||u.nome as utente_sottomissione,
	data_inserimento, parametri, id_exec_batch, data_submission, eb.data_avvio, data_termine, cnt_err
from foliage2.flgbatch_ondemand_tab bd
	join foliage2.flguten_tab u on (u.id_uten = bd.id_utente)
	join foliage2.flgconf_batch_tab b using (id_batch)
	left join foliage2.flgexecuted_batch_tab eb using (id_batch, data_rife)
	left join lateral (
		select count(*) as cnt_err
		from foliage2.flgerror_batch_tab erb
		where erb.id_batch = bd.id_batch
			and erb.data_rife = bd.data_rife
	) as err on (true)
where b.id_batch = any (
		select r.id_batch 
		from foliage2.flgconf_batch_report_tab r
	)""";
		HashMap<String, Object> pars = new HashMap<>();
		SqlRowSet rs = this.queryForRowSet(sql, pars);

		
		// while(rs.next()) {
		// 	System.out.println("Riga");
		// }
		// rs.beforeFirst();

		return rs;
	}

	public SqlRowSet getElaborazioniMonitoraggio() {
		String sql = """
select bd.id_batch_ondemand, data_rife,
	bd.data_avvio as data_di_esecuzione_richiesta, 
	u.codi_fisc||': '||u.cognome||' '||u.nome as utente_sottomissione,
	data_inserimento, parametri, id_exec_batch, data_submission, eb.data_avvio, data_termine, cnt_err
from foliage2.flgbatch_ondemand_tab bd
	join foliage2.flguten_tab u on (u.id_uten = bd.id_utente)
	join foliage2.flgconf_batch_tab b using (id_batch)
	left join foliage2.flgexecuted_batch_tab eb using (id_batch, data_rife)
	left join lateral (
		select count(*) as cnt_err
		from foliage2.flgerror_batch_tab erb
		where erb.id_batch = bd.id_batch
			and erb.data_rife = bd.data_rife
	) as err on (true)
where b.cod_batch = 'MONITORAGGIO_SAT'""";
		HashMap<String, Object> pars = new HashMap<>();
		SqlRowSet rs = this.queryForRowSet(sql, pars);

		
		// while(rs.next()) {
		// 	System.out.println("Riga");
		// }
		// rs.beforeFirst();

		return rs;
	}
	
	public void salvaElaborazioneMonitoraggio(ElaborazioneMonitoraggio elaborazione, Integer idUtente) {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );
		try {
			elaborazione.salva(this, idUtente);
			platformTransactionManager.commit(status);
		}catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
	}
	public ElaborazioneMonitoraggio getElaborazioneMonitoraggio(Integer idRichiesta) {
		return ElaborazioneMonitoraggio.carica(this, idRichiesta);
	}
	public void eliminaElaborazioneMonitoraggio(Integer idRichiesta) {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			ElaborazioneMonitoraggio.elimina(this, idRichiesta);
			platformTransactionManager.commit(status);
		}catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
	}

	
	public void salvaElaborazioneGovernance(ElaborazioneGovernance elaborazione, Integer idUtente) {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			elaborazione.salva(this, idUtente);
			platformTransactionManager.commit(status);
		}catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
	}
	public ElaborazioneGovernance getElaborazioneGovernance(Integer idRichiesta) {
		return ElaborazioneGovernance.carica(this, idRichiesta);
	}
	public void eliminaElaborazioneGovernance(Integer idRichiesta) {
		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			ElaborazioneGovernance.elimina(this, idRichiesta);
			platformTransactionManager.commit(status);
		}catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
	}
}
