package it.almaviva.foliage.bean;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.istanze.db.DbUtils;

public class FoliageTaskExecution<T extends FoliageTask> {

	public boolean isPending = false;
	public Integer pendingId;
	public Integer idExec;
	public Integer numRecordElaborati;
	public T task;
	public LocalDateTime batchTime;
	public LocalDateTime rifeTime;
	public LocalDateTime submissionTime;
	public LocalDateTime startTime;
	public LocalDateTime endTime;
	public Exception error;

	public LocalDateTime evalRifeTime() {
		if (rifeTime == null) {
			if (batchTime == null) {
				throw new FoliageException("Problema nel recupero della data di riferimento");
			}
			return batchTime;
		}
		else {
			return rifeTime;
		}
	}

	public static String QueryFromIdBatchForReport = """
select eb.data_rife, eb.data_avvio, eb.data_termine, eb.num_record_elaborati,
	bd.id_batch_ondemand , bd.data_inserimento
from foliage2.flgexecuted_batch_tab eb
	left join foliage2.flgbatch_ondemand_tab bd using (id_batch, data_rife)
where eb.id_batch = :idBatch
order by eb.data_rife desc""";

	public static org.springframework.jdbc.core.RowMapper<FoliageTaskExecution<FoliageTask>> ReportRowMapper = (ResultSet rs, int rn) -> {
		FoliageTaskExecution<FoliageTask> outVal = new FoliageTaskExecution<FoliageTask>();
		outVal.rifeTime = DbUtils.GetLocalDateTime(rs, rn, "data_rife");
		outVal.startTime = DbUtils.GetLocalDateTime(rs, rn, "data_avvio");
		outVal.endTime = DbUtils.GetLocalDateTime(rs, rn, "data_termine");
		outVal.numRecordElaborati = DbUtils.GetInteger(rs, rn, "num_record_elaborati");
		outVal.submissionTime = DbUtils.GetLocalDateTime(rs, rn, "data_inserimento");
		outVal.idExec = DbUtils.GetInteger(rs, rn, "id_batch_ondemand");
		return outVal;
	};
	public static org.springframework.jdbc.core.RowMapper<FoliageTaskExecution<FoliageTask>> getPendingRowMapper(HashMap<Integer, FoliageTask> tasksMap) {
		return (ResultSet rs, int rn) -> {
			
			Integer idTask = DbUtils.GetInteger(rs, rn, "id_batch");
			FoliageTaskExecution<FoliageTask> outVal = new FoliageTaskExecution<FoliageTask>();
			outVal.task = tasksMap.get(idTask);
			Timestamp t = rs.getTimestamp("data_batch");
			if (t != null) {
				outVal.batchTime = t.toLocalDateTime();
			}
			return outVal;
		};
	}
}
