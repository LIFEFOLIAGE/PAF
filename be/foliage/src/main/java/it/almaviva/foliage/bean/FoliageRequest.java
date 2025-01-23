package it.almaviva.foliage.bean;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.postgresql.util.PGInterval;
import org.postgresql.util.PGobject;

import it.almaviva.foliage.istanze.db.DbUtils;
import lombok.val;

public class FoliageRequest {
	// public String threadName;
	// public String ipAddress;
	// public String username;
	// public String method;
	// public String host;
	// public String path;
	// public String queryString;
	// public LocalDateTime oraInizio;
	// public LocalDateTime oraFine;
	// public String requestUrl;
	// public Integer status;

	private HashMap<String, Object> parsMap = new HashMap<>();
	private LocalDateTime oraInizio;
	private Duration durata;

	public HashMap<String, Object> getParsMap() {
		return parsMap;
	}

	public String getThreadName() {
		return (String) parsMap.get("threadName");
	}

	public void setThreadName(String value) {
		parsMap.put("threadName", value);
	}

	public String getIpAddress() {
		return (String) parsMap.get("ipAddress");
	}

	public void setIpAddress(String value) {
		parsMap.put("ipAddress", value);
	}

	public String getUsername() {
		return (String) parsMap.get("username");
	}

	public void setUsername(String value) {
		parsMap.put("username", value);
	}

	public String getMethod() {
		return (String) parsMap.get("method");
	}

	public void setMethod(String value) {
		parsMap.put("method", value);
	}

	public String getHost() {
		return (String) parsMap.get("host");
	}

	public void setHost(String value) {
		parsMap.put("host", value);
	}

	public String getPath() {
		return (String) parsMap.get("path");
	}

	public void setPath(String value) {
		parsMap.put("path", value);
	}

	public String getQueryString() {
		return (String) parsMap.get("queryString");
	}

	public void setQueryString(String value) {
		parsMap.put("queryString", value);
	}

	public LocalDateTime getOraInizio() {
		//return (LocalDateTime) parsMap.get("oraInizio");
		return oraInizio;
	}

	public void setOraInizio(LocalDateTime value) {
		oraInizio = value;
		parsMap.put("oraInizio", value);
	}

	public void setHeaders(String value) {
		parsMap.put("headers", value);
	}

	public Duration getDurata() {
		return durata;
		//return (Duration) parsMap.get("durata");
	}

	public void setDurata(Duration value) throws SQLException {
		durata = value;
		if (value == null) {
			parsMap.put("durata", null);
		}
		else {
			String strVal = String.format( "%d microseconds", durata.getNano()/1000);
			PGobject intObj = new PGobject();
			intObj.setType("interval");
			intObj.setValue(strVal);
			
			parsMap.put("durata", intObj);
		}
	}

	public Integer getStatus() {
		return (Integer) parsMap.get("status");
	}

	public void setStatus(Integer value) {
		parsMap.put("status", value);
	}

	public String getErrore() {
		return (String) parsMap.get("errore");
	}

	public void setErrore(String value) {
		parsMap.put("errore", value);
	}

	// public \1 get\2() {return (\1)parsMap.get("\2");}
	// public void set\2(\1 value) { parsMap.put("\2", value);}
}
