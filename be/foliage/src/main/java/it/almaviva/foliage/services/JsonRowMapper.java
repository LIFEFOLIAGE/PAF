// package it.almaviva.foliage.services;

// import java.io.IOException;
// import java.io.InputStream;
// import java.io.Reader;
// import java.io.StringWriter;
// import java.nio.charset.StandardCharsets;
// import java.sql.Blob;
// import java.sql.Clob;
// import java.sql.ResultSet;
// import java.sql.ResultSetMetaData;
// import java.sql.SQLException;
// import java.sql.Types;
// import java.util.HashMap;

// import org.springframework.jdbc.core.RowMapper;
// import com.google.gson.JsonObject;

// import lombok.Getter;

// public class JsonRowMapper implements RowMapper<JsonObject>{
// 	@Getter
// 	private HashMap<String, String> redefinitions;

// 	private ResultSetMetaData rsmd;
// 	private int numColumns;
// 	String[] columnNames;
// 	int[] columnTypes;

// 	public JsonRowMapper(HashMap<String, String> redefs) {
// 		this.redefinitions = redefs;
// 	}
// 	public JsonRowMapper() {
// 		this(new HashMap<>());
// 	}

// 	@Override
// 	public JsonObject mapRow(ResultSet rs, int rowNum) throws SQLException {
// 		if (rsmd == null) {
// 			rsmd = rs.getMetaData();
//             numColumns = rsmd.getColumnCount();
//             columnNames = new String[numColumns];
//             columnTypes = new int[numColumns];

//             for (int i = 0; i < columnNames.length; i++) {
//                 columnNames[i] = rsmd.getColumnLabel(i + 1);
//                 columnTypes[i] = rsmd.getColumnType(i + 1);
//             }
// 		}
// 		JsonObject retVal = new JsonObject();
		
// 		boolean b;
// 		long l;
// 		int i;
// 		double d;
// 		short s;
		



// 		for (int idx = 0; idx < columnNames.length; idx++) {
// 			String name = columnNames[idx];
// 			if (this.redefinitions.containsKey(name)) {
// 				name = this.redefinitions.get(name);
// 			}
// 			switch (columnTypes[idx]) {

// 				case Types.INTEGER: {
// 					i = rs.getInt(idx + 1);
// 					if (rs.wasNull()) {
// 						//value = null;

// 					} else {
// 						retVal.addProperty(name, Integer.valueOf(i));
// 					}
// 				}; break;

// 				case Types.BIGINT: {
// 					l = rs.getLong(idx + 1);
// 					if (rs.wasNull()) {
// 						//value = null;
// 					} else {
// 						retVal.addProperty(name, Long.valueOf(l));
// 					}
// 				}; break;

// 				case Types.DECIMAL:
// 				case Types.NUMERIC: {
// 					retVal.addProperty(name, rs.getBigDecimal(idx + 1));
// 				}; break;

// 				case Types.FLOAT:
// 				case Types.REAL:
// 				case Types.DOUBLE: {
// 					d = rs.getDouble(idx + 1);
// 					if (rs.wasNull()) {
// 						//value = null;
// 					}
// 					else {
// 						retVal.addProperty(name, Double.valueOf(d));
// 					}
// 				}; break;

// 				case Types.NVARCHAR:
// 				case Types.VARCHAR:
// 				case Types.LONGNVARCHAR:
// 				case Types.LONGVARCHAR: {
// 					retVal.addProperty(name, rs.getString(idx + 1));
// 				}; break;

// 				case Types.BOOLEAN:
// 				case Types.BIT: {
// 					b = rs.getBoolean(idx + 1);
// 					if (rs.wasNull()) {
// 						//value = null;
// 					} else {
// 						retVal.addProperty(name, Boolean.valueOf(b));
// 					}
// 				}; break;

// 				case Types.TINYINT:
// 				case Types.SMALLINT: {
// 					s = rs.getShort(idx + 1);
// 					if (rs.wasNull()) {
// 						//value = null;
// 					} else {
// 						retVal.addProperty(name, Short.valueOf(s));
// 					}
// 				}; break;

// 				case Types.DATE: {
// 					java.sql.Date date = rs.getDate(idx + 1);
// 					if (rs.wasNull()) {
// 						//value = null;
// 					}
// 					else {
// 						retVal.addProperty(name, date.toLocalDate().toString());
// 					}
// 				}; break;

// 				case Types.TIMESTAMP: {
// 					java.sql.Timestamp timeStamp = rs.getTimestamp(idx + 1);
// 					if (rs.wasNull()) {
// 						//value = null;
// 					}
// 					else {
// 						retVal.addProperty(name, timeStamp.toLocalDateTime().toString());
// 					}
// 				}; break;

// 				case Types.BLOB: {
// 					Blob blob = rs.getBlob(idx);
// 					InputStream is = blob.getBinaryStream();
// 					String str = null;
					
// 					try {
// 						str = new String(is.readAllBytes(), StandardCharsets.UTF_8);
// 						is.close();
// 					} 
// 					catch (Exception e) {}
//     				retVal.addProperty(name, str);
// 					blob.free();
// 				}; break;

// 				case Types.CLOB:
// 					Clob clob = rs.getClob(idx);
// 					Reader reader = clob.getCharacterStream();
// 					StringWriter writer = new StringWriter();
// 					int intValueOfChar;
// 					try {
// 						while ((intValueOfChar = reader.read()) != -1) {
// 							writer.append((char) intValueOfChar);
// 						}
// 					}
// 					catch (Exception e){

// 					}
// 					retVal.addProperty(name, writer.toString());
// 					clob.free();
// 					try {
// 						writer.close();
// 					} 
// 					catch (Exception e) {}
// 					break;


// 				case Types.BINARY:
// 				case Types.VARBINARY:
// 				case Types.LONGVARBINARY: {
// 					// TODO: codifica base 64???
// 					throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type BINARY");
// 					//retVal.addProperty(name, rs.getBytes(idx + 1));
// 				}

// 				case Types.ARRAY:
// 					throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type ARRAY");

// 				case Types.STRUCT:
// 					throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type STRUCT");

// 				case Types.DISTINCT:
// 					throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type DISTINCT");

// 				case Types.REF:
// 					throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type REF");

// 				case Types.JAVA_OBJECT:
// 				default: {
// 					throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type REF");
// 					//provider.defaultSerializeValue(rs.getObject(idx + 1), jgen);
// 				}
// 			}
// 		}

// 		return retVal;



// 	}
	
// }
