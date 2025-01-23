package it.almaviva.foliage.function;

@FunctionalInterface
public interface GetFromResultSet {
	Object get(java.sql.ResultSet rs, Integer rn, String colName) throws Exception;
}
