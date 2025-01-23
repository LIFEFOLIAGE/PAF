/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.almaviva.foliage.services;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.javatuples.Pair;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

/**
 *
 * @author A.Rossi
 */
@JsonComponent
public class SqlRowSetSerializer extends StdSerializer<SqlRowSet>{

	public static class SqlRowSetSerializerException extends JsonProcessingException{
		public SqlRowSetSerializerException(Throwable cause){
			super(cause);
		}
	}

	@Override
	public Class<SqlRowSet> handledType() {
		return SqlRowSet.class;
	}
	
	public SqlRowSetSerializer() {
		this(null);
	}
	
	public SqlRowSetSerializer(Class<SqlRowSet> t) {
		super(t);
	}
	
	@Override
	public void serialize(SqlRowSet rs, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		
		try {
			SqlRowSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();
			String[] columnNames = new String[numColumns];
			int[] columnTypes = new int[numColumns];

			for (int i = 0; i < columnNames.length; i++) {
				columnNames[i] = rsmd.getColumnLabel(i + 1);
				columnTypes[i] = rsmd.getColumnType(i + 1);
			}

			jgen.writeStartArray();

			while (rs.next()) {
				serializeRow(rs, columnNames, columnTypes, jgen, provider);
			}

			jgen.writeEndArray();

		} catch (SQLException e) {
			throw new SqlRowSetSerializerException(e);
		}
	}
	public static Pair<String[], int[]> intitSerializer(SqlRowSet rs) throws SQLException {
		SqlRowSetMetaData rsmd = rs.getMetaData();
		int numColumns = rsmd.getColumnCount();
		String[] columnNames = new String[numColumns];
		int[] columnTypes = new int[numColumns];
		for (int i = 0; i < columnNames.length; i++) {
			columnNames[i] = rsmd.getColumnLabel(i + 1);
			columnTypes[i] = rsmd.getColumnType(i + 1);
		}

		return new Pair<>(columnNames, columnTypes);
	}
	
	public static void serializeRow(SqlRowSet rs, String[] columnNames, int[] columnTypes, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException, SQLException {
		jgen.writeStartObject();

		boolean b;
		long l;
		double d;

		for (int i = 0; i < columnNames.length; i++) {

			jgen.writeFieldName(columnNames[i]);
			switch (columnTypes[i]) {

			case Types.INTEGER:
				l = rs.getInt(i + 1);
				if (rs.wasNull()) {
					jgen.writeNull();
				} else {
					jgen.writeNumber(l);
				}
				break;

			case Types.BIGINT:
				l = rs.getLong(i + 1);
				if (rs.wasNull()) {
					jgen.writeNull();
				} else {
					jgen.writeNumber(l);
				}
				break;

			case Types.DECIMAL:
			case Types.NUMERIC:
				jgen.writeNumber(rs.getBigDecimal(i + 1));
				break;

			case Types.FLOAT:
			case Types.REAL:
			case Types.DOUBLE:
				d = rs.getDouble(i + 1);
				if (rs.wasNull()) {
					jgen.writeNull();
				} else {
					jgen.writeNumber(d);
				}
				break;

			case Types.NVARCHAR:
			case Types.VARCHAR:
			case Types.LONGNVARCHAR:
			case Types.LONGVARCHAR:
				jgen.writeString(rs.getString(i + 1));
				break;

			case Types.BOOLEAN:
			case Types.BIT:
				b = rs.getBoolean(i + 1);
				if (rs.wasNull()) {
					jgen.writeNull();
				} else {
					jgen.writeBoolean(b);
				}
				break;

			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				throw new RuntimeException("SqlRowSetSerializer not yet implemented for SQL type ARRAY");

			case Types.TINYINT:
			case Types.SMALLINT:
				l = rs.getShort(i + 1);
				if (rs.wasNull()) {
					jgen.writeNull();
				} else {
					jgen.writeNumber(l);
				}
				break;

			case Types.DATE:
				java.sql.Date date = rs.getDate(i + 1);
				if (rs.wasNull()) {
					jgen.writeNull();
				}
				else {
					jgen.writeString(date.toLocalDate().toString());
					//provider.defaultSerializeDateValue(date.toLocalDate(), jgen);   
				}
				break;

			case Types.TIMESTAMP:
				java.sql.Timestamp timeStamp = rs.getTimestamp(i + 1);
				if (rs.wasNull()) {
					jgen.writeNull();
				}
				else {
					LocalDateTime dt = timeStamp.toLocalDateTime();
					LocalDate dat = dt.toLocalDate();
					LocalTime tim = dt.toLocalTime();
					int hour = tim.getHour();
					int min = tim.getMinute();
					int sec = tim.getSecond();
					int nanos = timeStamp.getNanos();
					int millis = nanos/1000000;
					String s = String.format("%sT%02d:%02d:%02d.%03d+00:00", dat.toString(), hour, min, sec, millis);
					jgen.writeString(s);
					//jgen.writeString(timeStamp.toString());
					//provider.defaultSerializeDateValue(timeStamp, jgen);   
				}
				break;

			case Types.BLOB:
				throw new RuntimeException("SqlRowSetSerializer not yet implemented for SQL type ARRAY");

			case Types.CLOB:
				throw new RuntimeException("SqlRowSetSerializer not yet implemented for SQL type ARRAY");

			case Types.ARRAY:
				throw new RuntimeException("SqlRowSetSerializer not yet implemented for SQL type ARRAY");

			case Types.STRUCT:
				throw new RuntimeException("SqlRowSetSerializer not yet implemented for SQL type STRUCT");

			case Types.DISTINCT:
				throw new RuntimeException("SqlRowSetSerializer not yet implemented for SQL type DISTINCT");

			case Types.REF:
				throw new RuntimeException("SqlRowSetSerializer not yet implemented for SQL type REF");

			case Types.JAVA_OBJECT:
			default:
				provider.defaultSerializeValue(rs.getObject(i + 1), jgen);
				break;
			}
		}
		jgen.writeEndObject();
	}

	public static ObjectMapper resultSetMapper() {
		SimpleModule module = new SimpleModule();
		module.addSerializer(new SqlRowSetSerializer());

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(module);
		return objectMapper;
	}
	
	public static ObjectNode ToObjectNode(SqlRowSet resultset) {
		// Use the DataBind Api here
		ObjectNode objectNode = resultSetMapper().createObjectNode();

		// put the resultset in a containing structure
		objectNode.putPOJO("results", resultset);

		// generate all
		return objectNode;
	}
}
