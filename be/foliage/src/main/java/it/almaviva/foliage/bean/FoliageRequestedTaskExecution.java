package it.almaviva.foliage.bean;

import java.sql.ResultSet;
import java.util.HashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.almaviva.foliage.istanze.db.DbUtils;

public class FoliageRequestedTaskExecution extends FoliageTaskExecution<FoliageTask> {
	public Integer idRequest;
	public Integer idUtente;
	public JsonObject parametri;

	
	// public void initRifeTime() {
	// 	rifeTime = batchTime;
	// }

	public static org.springframework.jdbc.core.RowMapper<FoliageRequestedTaskExecution> getRequestedPendingRowMapper(HashMap<Integer, FoliageTask> tasksMap) {
		return (ResultSet rs, int rn) -> {
			
			Integer idTask = DbUtils.GetInteger(rs, rn, "id_batch");
			FoliageRequestedTaskExecution outVal = new FoliageRequestedTaskExecution();
			outVal.task = tasksMap.get(idTask);
			outVal.idRequest = DbUtils.GetInteger(rs, rn, "id_batch_ondemand");
			outVal.idUtente = DbUtils.GetInteger(rs, rn, "id_utente");
			JsonElement parElement = DbUtils.GetJsonElement(rs, rn, "parametri");
			if (parElement != null) {
				outVal.parametri = parElement.getAsJsonObject();
			}
			// Timestamp t = rs.getTimestamp("data_batch");
			// if (t != null) {
			// 	outVal.batchTime = t.toLocalDateTime();
			// }

			outVal.rifeTime = DbUtils.GetLocalDateTime(rs, rn, "data_rife");
			outVal.batchTime = DbUtils.GetLocalDateTime(rs, rn, "data_avvio");

			

			return outVal;
		};
	}
}
