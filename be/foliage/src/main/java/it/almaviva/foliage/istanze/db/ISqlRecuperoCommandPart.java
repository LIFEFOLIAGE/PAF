package it.almaviva.foliage.istanze.db;

import org.javatuples.Pair;
import org.springframework.jdbc.core.RowMapper;

public interface ISqlRecuperoCommandPart extends ISqlCommandPart {
	public RowMapper<Pair<String, Object>> rowMapper();
}