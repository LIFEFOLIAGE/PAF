package it.almaviva.foliage.bean;

import java.sql.ResultSet;

import it.almaviva.foliage.istanze.db.DbUtils;

public class FoliageTask {
	public Integer id;
	public String codice;

	public static org.springframework.jdbc.core.RowMapper<FoliageTask> RowMapper = (ResultSet rs, int rn) -> {
		FoliageTask outVal = new FoliageTask();
		outVal.id = DbUtils.GetInteger(rs, rn, "id_batch");
		outVal.codice = rs.getString("cod_batch");
		return outVal;
	};
}
