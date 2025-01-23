package it.almaviva.foliage.bean;

import java.sql.ResultSet;

import org.threeten.extra.PeriodDuration;

import it.almaviva.foliage.istanze.db.DbUtils;

public class FoliagePeriodicTask extends FoliageTask {
	public PeriodDuration timeOffset;
	public PeriodDuration periodInterval;

	

	public static org.springframework.jdbc.core.RowMapper<FoliagePeriodicTask> RowMapper = (ResultSet rs, int rn) -> {
		FoliagePeriodicTask outVal = new FoliagePeriodicTask();
		outVal.id = DbUtils.GetInteger(rs, rn, "id_batch");
		outVal.codice = rs.getString("cod_batch");
		outVal.timeOffset = DbUtils.GetInterval(rs, rn, "intervallo_offset");
		outVal.periodInterval = DbUtils.GetInterval(rs, rn, "intervallo_frequenza");
		return outVal;
	};
}
