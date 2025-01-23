package it.almaviva.foliage.istanze.db;

import java.math.BigDecimal;
import java.net.URLConnection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.javatuples.Pair;
import org.postgresql.util.PGInterval;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.threeten.extra.PeriodDuration;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.bean.Base64FormioFile;
import it.almaviva.foliage.function.GetFromResultSet;
import it.almaviva.foliage.services.AbstractDal;
import it.almaviva.foliage.services.WebDal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DbUtils {
	public static HashMap<String, Object> getParSource(HashMap<String, Object> contesto, HashSet<String> proprietaUtilizzate) {
		log.debug("\n\nElenco Proprietà(contesto):");
		HashMap<String, Object> mapParam = new HashMap<String, Object>();
		for (String prop : proprietaUtilizzate) {
			String k = prop;
			Object v = contesto.get(prop);
			
			if (v == null) {
				log.debug(String.format("%s = null", k));
			}
			else {
				log.debug(String.format("%s = '%s' (%s)", k, v, v.getClass().getName()));
			}
			mapParam.put(k, v);
		}
		//SqlParameterSource parSource = new MapSqlParameterSource(mapParam);
		return mapParam;
	}
	// public static final GetFromResultSet GetString = (java.sql.ResultSet rs, Integer rn, String colName) -> {
	// 	return rs.getString(colName);
	// };
	
	public static final org.springframework.jdbc.core.RowMapper<String> GetStringRowMapper(String colName){
		return (java.sql.ResultSet rs, int rn) -> {
			String value = rs.getString(colName);
			return value;
		};
	} 

	public static final org.springframework.jdbc.core.RowMapper<Integer> GetIntegerRowMapper(String colName){
		return (java.sql.ResultSet rs, int rn) -> {
			Integer value = rs.getInt(colName);
			return (rs.wasNull()) ? null : value;
		};
	} 

	public static final org.springframework.jdbc.core.RowMapper<Boolean> GetBooleanRowMapper(String colName){
		return (java.sql.ResultSet rs, int rn) -> {
			Boolean value = rs.getBoolean(colName);
			return (rs.wasNull()) ? null : value;
		};
	}

	public static final org.springframework.jdbc.core.RowMapper<LocalDate> GetLocalDateRowMapper(String colName){
		return (java.sql.ResultSet rs, int rn) -> {
			Date value = rs.getDate(colName);
			return (rs.wasNull()) ? (LocalDate)null : value.toLocalDate();
		};
	}
	public static final org.springframework.jdbc.core.RowMapper<LocalDateTime> GetLocalDateTimeRowMapper(String colName){
		return (java.sql.ResultSet rs, int rn) -> {
			Timestamp value = rs.getTimestamp(colName);
			return (rs.wasNull()) ? (LocalDateTime)null : value.toLocalDateTime();
		};
	} 


	public static final org.springframework.jdbc.core.RowMapper<Pair<Integer, Integer>> GetIntegerPairRowMapper(String col1Name, String col2Name) {
		return (java.sql.ResultSet rs, int rn) -> {
			Integer prog = rs.getInt(col1Name);
			if (rs.wasNull()) {
				prog = null;
			}
			Integer idFile = rs.getInt(col2Name);
			if (rs.wasNull()) {
				idFile = null;
			}
			Pair<Integer, Integer> vOut = new Pair<Integer,Integer>(prog, idFile);
			return vOut;
		};
	}
	
	public static LocalDateTime GetLocalDateTime(
		java.sql.ResultSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		Timestamp value = rs.getTimestamp(colName);
		return (rs.wasNull()) ? ((LocalDateTime)null) : value.toLocalDateTime();
	}

	public static LocalDateTime GetLocalDateTime(
		SqlRowSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		Timestamp value = rs.getTimestamp(colName);
		return (rs.wasNull()) ? ((LocalDateTime)null) : value.toLocalDateTime();
	}

	public static LocalDate GetLocalDate(
		java.sql.ResultSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		Date value = rs.getDate(colName);
		return (rs.wasNull()) ? ((LocalDate)null) : value.toLocalDate();
	}

	public static LocalDate GetLocalDate(
		SqlRowSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		Date value = rs.getDate(colName);
		return (rs.wasNull()) ? ((LocalDate)null) : value.toLocalDate();
	}

	public static Boolean GetBoolean(
		java.sql.ResultSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		Boolean value = rs.getBoolean(colName);
		return (rs.wasNull()) ? ((Boolean)null) : value;
	}

	public static Boolean GetBoolean(
		SqlRowSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		Boolean value = rs.getBoolean(colName);
		return (rs.wasNull()) ? ((Boolean)null) : value;
	}

	public static Object GetObject(
		java.sql.ResultSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		return rs.getObject(colName);
	}

	public static Object GetObject(
		SqlRowSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		return rs.getObject(colName);
	}

	public static PGInterval GetPgInterval(PeriodDuration pd) {
		Duration d = pd.getDuration();
		Period p = pd.getPeriod();
		PGInterval outVal = new PGInterval(
			p.getYears(), p.getMonths(), p.getDays(), 
			d.toHoursPart(), d.toMinutesPart(), d.toSecondsPart()
		);
		return outVal;
	}


	public static PGInterval GetPgInterval(Duration d) {
		PGInterval outVal = new PGInterval(
			0, 0, 0, 
			d.toHoursPart(), d.toMinutesPart(), d.toSecondsPart()
		);
		return outVal;
	}

	public static PeriodDuration GetInterval(
		java.sql.ResultSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		final PGInterval pgi = (PGInterval) rs.getObject(colName);
		if (rs.wasNull()) {
			return null;
		}
		else {
			final int years = pgi.getYears();
			final int months = pgi.getMonths();
			final int days = pgi.getDays();
			final int hours = pgi.getHours();
			final int mins = pgi.getMinutes();
			final int secs = (int)Math.floor(pgi.getSeconds());
			Period p = Period.of(years, months, days);
			Duration d = Duration.ofSeconds(
				secs
				+ (mins * 60)
				+ (hours * 60 * 60)
			);

			return PeriodDuration.of(p, d);
		}
	}

	public static Integer GetInteger(
		java.sql.ResultSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		Integer value = rs.getInt(colName);
		return (rs.wasNull()) ? ((Integer)null) : value;
	}

	public static Integer GetInteger(
		SqlRowSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		Integer value = rs.getInt(colName);
		return (rs.wasNull()) ? ((Integer)null) : value;
	}

	public static Long GetLong(
		java.sql.ResultSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		Long value = rs.getLong(colName);
		return (rs.wasNull()) ? ((Long)null) : value;
	}
	
	public static Long GetLong(
		SqlRowSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		Long value = rs.getLong(colName);
		return (rs.wasNull()) ? ((Long)null) : value;
	}

	public static BigDecimal GetDecimal(
		java.sql.ResultSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		BigDecimal value = rs.getBigDecimal(colName);
		return (rs.wasNull()) ? ((BigDecimal)null) : value;
	}

	public static BigDecimal GetDecimal(
		SqlRowSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		BigDecimal value = rs.getBigDecimal(colName);
		return (rs.wasNull()) ? ((BigDecimal)null) : value;
	}

	public static JsonElement GetJsonElement(
		java.sql.ResultSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		String jsonString = rs.getString(colName);
		if (jsonString == null) {
			return null;
		}
		else {
			return JsonParser.parseString(jsonString);
		}
	}
	public static JsonElement GetJsonElement(
		SqlRowSet rs,
		Integer rn,
		String colName
	) throws SQLException {
		String jsonString = rs.getString(colName);
		if (jsonString == null) {
			return null;
		}
		else {
			return JsonParser.parseString(jsonString);
		}
	}

	public static final GetFromResultSet GetInteger = (java.sql.ResultSet rs, Integer rn, String colName) -> {
		// Integer value = rs.getInt(colName);
		// return (rs.wasNull()) ? null : value;
		return GetInteger(rs, rn, colName);
	};
	public static final GetFromResultSet GetDate = (java.sql.ResultSet rs, Integer rn, String colName) -> {
		java.sql.Date value = rs.getDate(colName);
		return (rs.wasNull()) ? null : value.toLocalDate();
	};
	public static final GetFromResultSet GetDateTime = (java.sql.ResultSet rs, Integer rn, String colName) -> {
		java.sql.Timestamp value = rs.getTimestamp(colName);
		return (rs.wasNull()) ? null : value.toLocalDateTime();
	};
	public static final GetFromResultSet GetBoolean = (java.sql.ResultSet rs, Integer rn, String colName) -> {
		Boolean value = rs.getBoolean(colName);
		return (rs.wasNull()) ? null : value;
	};
	public static final GetFromResultSet GetFloat = (java.sql.ResultSet rs, Integer rn, String colName) -> {
		Float value = rs.getFloat(colName);
		return (rs.wasNull()) ? null : value;
	};
	public static final GetFromResultSet GetDecimal = (java.sql.ResultSet rs, Integer rn, String colName) -> {
		BigDecimal value = rs.getBigDecimal(colName);
		return (rs.wasNull()) ? null : value;
	};
	public static final GetFromResultSet Get = (java.sql.ResultSet rs, Integer rn, String colName) -> {
		Object value = rs.getObject(colName);
		return (rs.wasNull()) ? null : value;
	};
	
	public static Base64FormioFile[] getBase64FormioFiles(AbstractDal dal, Integer idFile) {
		if (idFile == null) {
			return null;
		}
		else {
			String sqlFile = """
select FILE_NAME, ORIGINAL_FILE_NAME, FILE_SIZE, STORAGE,
FILE_TYPE, HASH_FILE, FILE_DATA
from FOLIAGE2.FLGBASE64_FORMIO_FILE_TAB
where ID_FILE = :idFile
				""";
			Map<String, Object> mapFileParam = new HashMap<String, Object>();
			mapFileParam.put("idFile", idFile);
			//SqlParameterSource filePars = new MapSqlParameterSource(mapFileParam);
			List<Base64FormioFile> outList = dal.query(sqlFile, mapFileParam, Base64FormioFile.RowMapper());
			Base64FormioFile[] arr = new Base64FormioFile[0];
			Base64FormioFile[] arrOut = outList.toArray(arr);
			if (arrOut.length == 0) {
				return null;
			}
			else {
				return arrOut;
			}
		}
	}

	public static Integer saveBase64FormioFiles(AbstractDal dal, Base64FormioFile[] files) throws Exception {
		if (files != null && files.length > 0) {
			Optional<Base64FormioFile> fileKo = Arrays.stream(files).filter(
					f -> {
						String fName = f.getOriginalName();
						//String mime = mimeMap.getContentType(fName);
						String mime = URLConnection.guessContentTypeFromName(fName);
						return !(mime.startsWith("image") || mime.endsWith("pdf"));
					}
				).findFirst();
			if (fileKo.isPresent()) {
				throw new FoliageException(String.format("Tipo di file non ammesso: %s ", fileKo.get().getOriginalName()));
			}
			String sqlInsMasterFile = """
insert into FOLIAGE2.FLGBASE64_FORMIO_FILE_MASTER_TAB(DATA_CARICAMENTO)
	values (localtimestamp) returning ID_FILE
			""";
			HashMap<String, Object> mapParamMaster = new HashMap<String, Object>();
			//log.debug(sqlInsMasterFile);
			Integer idFile = dal.queryForObject(
				sqlInsMasterFile,
				mapParamMaster,
				DbUtils.GetIntegerRowMapper("ID_FILE")
			);
			if (idFile == null) {
				throw new FoliageException("Missing idFile");
			}
			String sqlInsFile = """
insert into FOLIAGE2.FLGBASE64_FORMIO_FILE_TAB(ID_FILE, PROG_FILE, FILE_NAME, ORIGINAL_FILE_NAME, FILE_SIZE, STORAGE, FILE_TYPE, HASH_FILE, FILE_DATA)
	values (:idFile, :prog, :fileName, :originalName, :fileSize, :storage, :type, :hash, :data)
				""";
			for (int i = 0; i < files.length; i++) {
				Base64FormioFile file = files[i];
				HashMap<String, Object> mapParamFile = new HashMap<String, Object>();
				mapParamFile.put("idFile", idFile);
				mapParamFile.put("prog", Integer.valueOf(i));
				mapParamFile.put("fileName", file.getName());
				mapParamFile.put("originalName", file.getOriginalName());
				mapParamFile.put("fileSize", file.getSize());
				mapParamFile.put("storage", file.getStorage());
				mapParamFile.put("type", file.getType());
				mapParamFile.put("hash", file.getHash());
				mapParamFile.put("data", file.getUrl().getBytes());
				//log.debug(sqlInsFile);
				dal.update(
					sqlInsFile,
					new MapSqlParameterSource(mapParamFile)
				);
			}
			return idFile;
			
		}
		else {
			return null;
		}
	}
}
