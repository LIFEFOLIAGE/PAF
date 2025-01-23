package it.almaviva.foliage.services;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.security.KeyStore.Entry;
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
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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
import lombok.extern.slf4j.Slf4j;


import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.bean.AbilitazioniIstanza;
import it.almaviva.foliage.bean.AutocertificazioneProfessionista;
import it.almaviva.foliage.bean.Base64FormioFile;
import it.almaviva.foliage.bean.DatiRichiestaResponsabile;
import it.almaviva.foliage.bean.DatiTitolare;
import it.almaviva.foliage.bean.DatiUtente;
import it.almaviva.foliage.bean.ElementoValutazioneIstanza;
import it.almaviva.foliage.bean.EntitaGeometrica;
import it.almaviva.foliage.bean.EsecuzioneBatchManuale;
import it.almaviva.foliage.bean.FileIstanzaApp;
import it.almaviva.foliage.bean.FileIstanzaWeb;
import it.almaviva.foliage.bean.FileValutazioneIstanza;
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
import it.almaviva.foliage.function.ResultFunction;
import it.almaviva.foliage.function.TriFunction;
import it.almaviva.foliage.istanze.CaricatoreIstanza;
import it.almaviva.foliage.istanze.FlussoSchede;
import it.almaviva.foliage.istanze.SchedaIstanza;
import it.almaviva.foliage.istanze.db.CampoSelect;
import it.almaviva.foliage.istanze.db.CondizioneEq;
import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.istanze.db.RecuperoDb;
import it.almaviva.foliage.legacy.bean.RicercaUtenti;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.ws.rs.core.MediaType;
import javassist.bytecode.ByteArray;


@Slf4j
public abstract class AbstractDal {
	
	protected Connection connection;
	protected JdbcTemplate jdbcTemplate;
	protected NamedParameterJdbcTemplate template;
	protected TransactionTemplate transactionTemplate;
	protected PlatformTransactionManager platformTransactionManager;
	
	public NamedParameterJdbcTemplate getNamedTemplate() {
		return template;
	}

	public AbstractDal(
		JdbcTemplate jdbcTemplate,
		TransactionTemplate transactionTemplate,
		PlatformTransactionManager platformTransactionManager,
		String name
	) throws Exception {
		log.debug(name);
		this.jdbcTemplate = jdbcTemplate;
		this.template = new NamedParameterJdbcTemplate(jdbcTemplate);
		this.transactionTemplate = transactionTemplate;
		this.platformTransactionManager = platformTransactionManager;

		this.connection = jdbcTemplate.getDataSource().getConnection();

		
		PreparedStatement statement = connection.prepareStatement("set search_path to foliage2, public");
		log.debug("set search_path to foliage2, public");
		statement.execute();

		printTime();
	}

	public void printTime() {
		String sql = "select localtimestamp as time";
		HashMap<String, Object> pars = new HashMap<>();
		LocalDateTime time = queryForObject(sql, pars, DbUtils.GetLocalDateTimeRowMapper("time"));
		log.info(
			String.format(
				"Current database time is %s",
				time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
			)
		);
	}


	public <T> List<T> query(String sql, MapSqlParameterSource map, RowMapper<T> mapper) {
		log.debug(String.format("Executing:\n", sql));
		for (String name : map.getParameterNames()) {
			Object value = map.getValue(name);
			if (value == null) {
				log.debug(String.format("Parameter %s null", name));
			}
			else {
				value = value.toString();
				log.debug(String.format("Parameter %s = %s", name, value));
			}
		}
		return template.query(sql, map, mapper);
	}
	
	public <T> T queryForObject(String sql, Map<String, Object> map, RowMapper<T> mapper) {
		log.debug(String.format("Executing:\n%s", sql));
		for (String name : map.keySet()) {
			Object value = map.get(name);
			if (value == null) {
				log.debug(String.format("Parameter %s null", name));
			}
			else {
				value = value.toString();
				log.debug(String.format("Parameter %s = %s", name, value));
			}
		}
		return template.queryForObject(sql, map, mapper);
	}

	public SqlRowSet queryForRowSet(String sql, Map<String, Object> pars) {
		log.debug(String.format("Executing:\n%s", sql));
		for (String name : pars.keySet()) {
			Object value = pars.get(name);
			if (value == null) {
				log.debug(String.format("Parameter %s null", name));
			}
			else {
				value = value.toString();
				log.debug(String.format("Parameter %s = %s", name, value));
			}
		}
		return template.queryForRowSet(sql, pars);
	}

	public <T> List<T> query(String sql, Map<String, Object> pars, RowMapper<T> mapper) {
		log.debug(String.format("Executing:\n%s", sql));
		for (String name : pars.keySet()) {
			Object value = pars.get(name);
			if (value == null) {
				log.debug(String.format("Parameter %s null", name));
			}
			else {
				value = value.toString();
				log.debug(String.format("Parameter %s = %s", name, value));
			}
		}
		return template.query(sql, pars, mapper);
	}

	public int update(String sql, MapSqlParameterSource map) {
		log.debug(String.format("Executing:\n", sql));
		for (String name : map.getParameterNames()) {
			Object value = map.getValue(name);
			if (value == null) {
				log.debug(String.format("Parameter %s null", name));
			}
			else {
				value = value.toString();
				log.debug(String.format("Parameter %s = %s", name, value));
			}
		}
		int retVal = template.update(sql, map);
		log.debug(String.format("%d record aggiornati", retVal));
		return retVal;
	}
	
	public int update(String sql, Map<String, Object> pars) {
		log.debug(String.format("Executing:\n%s", sql));
		for (String name : pars.keySet()) {
			Object value = pars.get(name);
			if (value == null) {
				log.debug(String.format("Parameter %s null", name));
			}
			else {
				value = value.toString();
				log.debug(String.format("Parameter %s = %s", name, value));
			}
		}
		int retVal = template.update(sql, pars);
		log.debug(String.format("%d record aggiornati", retVal));
		return retVal;
	}

	public int update(String sql, Map<String, Object> pars, Map<String, String> errMessages) {

		int retVal = 0;
		try {
			this.update(sql, pars);
		}
		catch (DataIntegrityViolationException e) {
			Throwable ce = e.getCause();
			if (ce == null) {
				ce = e;
			}
			String message = ce.getMessage();
			FoliageException fe = null;
			if (errMessages != null) {
				Set<Map.Entry<String, String>> set = errMessages.entrySet();
				Iterator<Map.Entry<String, String>> i = set.iterator();
				while (fe == null && i.hasNext()) {
					Map.Entry<String, String> entry = i.next();
					String key = entry.getKey();
					if (message.contains(key)) {
						fe = new FoliageException(entry.getValue(), e);
					}
				}
			}
			if (fe == null) {
				throw e;
			}
			else {
				throw fe;
			}
		}
		log.debug(String.format("%d record aggiornati", retVal));
		return retVal;
	}

	public ResultSet GetResult(StatementResultBuilder builder) throws SQLException {
		ResultSet result = null;
		Connection conn = this.connection;
		result = builder.getExecution(conn);
		return result;
	}
}
