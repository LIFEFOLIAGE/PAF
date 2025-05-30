package it.almaviva.foliage.services;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.authentication.AccessToken;
import it.almaviva.foliage.authentication.JwtAuthentication;
import it.almaviva.foliage.function.BiProcedure;
import lombok.extern.slf4j.Slf4j;


import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import it.almaviva.foliage.istanze.db.DbUtils;


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
	private static final HashMap<String, Object> emptyHashMap = new HashMap<>();
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

		// DriverManagerDataSource dataSource = new DriverManagerDataSource();
		// //dataSource.setDriverClassName("org.postgresql.Driver");
		// dataSource.setUrl("jdbc:postgresql://unioneeuropea-foliage-svil.cs5b2t1vzg63.eu-west-1.rds.amazonaws.com/foliage");
		// //dataSource.setUrl("jdbc:postgresql://127.0.0.1/foliage");
		// dataSource.setUsername("foliage");
		// dataSource.setPassword("foliage.01");
		//this.connection = dataSource.getConnection();
		
		this.connection = jdbcTemplate.getDataSource().getConnection();
		String databaseUrl = this.connection.getMetaData().getURL();
		log.debug(String.format("connesso a: %s", databaseUrl));
		this.update("set search_path to foliage2, public", emptyHashMap);
		
		// PreparedStatement statement = connection.prepareStatement("set search_path to foliage2, public");
		// log.debug("set search_path to foliage2, public");
		// statement.execute();

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

	public void checkAccettazionePrivacy(AccessToken jwtToken) {
		if (jwtToken == null || jwtToken.getFlagAccettazione() == null || jwtToken.getFlagAccettazione() == false) {
			throw new FoliageException("L'utente non ha accettato l'informativa sulla privacy");
		}
	}

	public void checkAccettazionePrivacy() {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();
		checkAccettazionePrivacy(jwtToken);
	}

	// public <T> List<T> query(String sql, MapSqlParameterSource map, RowMapper<T> mapper) {
	// 	log.debug(String.format("Executing:\n", sql));
	// 	for (String name : map.getParameterNames()) {
	// 		Object value = map.getValue(name);
	// 		if (value == null) {
	// 			log.debug(String.format("Parameter %s null", name));
	// 		}
	// 		else {
	// 			String strValue = value.toString();
	// 			int len = strValue.length();
	// 			if (len <= 1000) {
	// 				log.debug(String.format("Parameter %s = %s", name, strValue));
	// 			}
	// 			else {
	// 				log.debug(String.format("Parameter %s = %s", name, strValue.substring(0, 1000)));
	// 			}
	// 		}
	// 	}
	// 	return template.query(sql, map, mapper);
	// }
	
	public <T> T queryForObject(String sql, Map<String, Object> map, RowMapper<T> mapper) {
		log.debug(String.format("Executing:\n%s", sql));
		for (String name : map.keySet()) {
			Object value = map.get(name);
			if (value == null) {
				log.debug(String.format("Parameter %s null", name));
			}
			else {
				String strValue = value.toString();
				int len = strValue.length();
				if (len <= 1000) {
					log.debug(String.format("Parameter %s = %s", name, strValue));
				}
				else {
					log.debug(String.format("Parameter %s = %s", name, strValue.substring(0, 1000)));
				}
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
				String strValue = value.toString();
				int len = strValue.length();
				if (len <= 1000) {
					log.debug(String.format("Parameter %s = %s", name, strValue));
				}
				else {
					log.debug(String.format("Parameter %s = %s", name, strValue.substring(0, 1000)));
				}
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
				String strValue = value.toString();
				int len = strValue.length();
				if (len <= 1000) {
					log.debug(String.format("Parameter %s = %s", name, strValue));
				}
				else {
					log.debug(String.format("Parameter %s = %s", name, strValue.substring(0, 1000)));
				}
			}
		}
		return template.query(sql, pars, mapper);
	}

	// public int update(String sql, MapSqlParameterSource map) {
	// 	log.debug(String.format("Executing:\n", sql));
	// 	for (String name : map.getParameterNames()) {
	// 		Object value = map.getValue(name);
	// 		if (value == null) {
	// 			log.debug(String.format("Parameter %s null", name));
	// 		}
	// 		else {
	// 			String strValue = value.toString();
	// 			int len = strValue.length();
	// 			if (len <= 1000) {
	// 				log.debug(String.format("Parameter %s = %s", name, strValue));
	// 			}
	// 			else {
	// 				log.debug(String.format("Parameter %s = %s", name, strValue.substring(0, 1000)));
	// 			}
	// 		}
	// 	}
	// 	int retVal = template.update(sql, map);
	// 	log.debug(String.format("%d record aggiornati", retVal));
	// 	return retVal;
	// }
	
	public int update(String sql, Map<String, Object> pars) {
		log.debug(String.format("Executing:\n%s", sql));
		for (String name : pars.keySet()) {
			Object value = pars.get(name);
			if (value == null) {
				log.debug(String.format("Parameter %s null", name));
			}
			else {
				String strValue = value.toString();
				int len = strValue.length();
				if (len <= 1000) {
					log.debug(String.format("Parameter %s = %s", name, strValue));
				}
				else {
					log.debug(String.format("Parameter %s = %s", name, strValue.substring(0, 1000)));
				}
			}
		}
		int retVal = template.update(sql, pars);
		log.debug(String.format("%d record aggiornati", retVal));
		return retVal;
	}
	public static void defaultDataIntegrityViolationHandler(DataIntegrityViolationException e, Map<String, String> errMessages) {
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
	public static BiProcedure<DataIntegrityViolationException, Map<String, String>> defaultDataIntegrityViolationHandler = AbstractDal::defaultDataIntegrityViolationHandler;
	//public static Consumer<DataIntegrityViolationException, Map<String, String> > defaultDataIntegrityViolationHandler = AbstractDal::defaultDataIntegrityViolationHandler;

	public int update(String sql, Map<String, Object> pars, Map<String, String> errMessages) {
		int retVal = -1;
		try {
			retVal = this.update(sql, pars);
		}
		catch (DataIntegrityViolationException e) {
			defaultDataIntegrityViolationHandler(e, errMessages);
			// Throwable ce = e.getCause();
			// if (ce == null) {
			// 	ce = e;
			// }
			// String message = ce.getMessage();
			// FoliageException fe = null;
			// if (errMessages != null) {
			// 	Set<Map.Entry<String, String>> set = errMessages.entrySet();
			// 	Iterator<Map.Entry<String, String>> i = set.iterator();
			// 	while (fe == null && i.hasNext()) {
			// 		Map.Entry<String, String> entry = i.next();
			// 		String key = entry.getKey();
			// 		if (message.contains(key)) {
			// 			fe = new FoliageException(entry.getValue(), e);
			// 		}
			// 	}
			// }
			// if (fe == null) {
			// 	throw e;
			// }
			// else {
			// 	throw fe;
			// }
		}
		return retVal;
	}

	public ResultSet GetResult(StatementResultBuilder builder) throws SQLException {
		ResultSet result = null;
		Connection conn = this.connection;
		result = builder.getExecution(conn);
		return result;
	}
}
