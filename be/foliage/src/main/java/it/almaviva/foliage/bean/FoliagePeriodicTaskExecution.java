package it.almaviva.foliage.bean;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.threeten.extra.PeriodDuration;

import it.almaviva.foliage.istanze.db.DbUtils;

public class FoliagePeriodicTaskExecution extends FoliageTaskExecution<FoliagePeriodicTask> {
	// public boolean isPending = false;
	// public Integer pendingId;
	// public Integer idExec;
	// public Integer numRecordElaborati;
	// public FoliagePeriodicTask task;
	// public LocalDateTime batchTime;
	// public LocalDateTime rifeTime;
	// public LocalDateTime submissionTime;
	// public LocalDateTime startTime;
	// public LocalDateTime endTime;
	// public Exception error;

// 	public static String QueryFromIdBatchForReport = """
// select e.data_rife, e.data_termine, e.num_record_elaborati
// from foliage2.flgexecuted_batch_tab e
// where e.id_batch = :idBatch
// order by e.data_rife desc""";

	@Override
	public LocalDateTime evalRifeTime() {
		LocalDateTime time = super.evalRifeTime();
		PeriodDuration timeOffset = task.timeOffset;

		if (timeOffset != null) {
			time = time.minus(timeOffset.getPeriod()).minus(timeOffset.getDuration());
		}
		return time;
		
	}

	public static org.springframework.jdbc.core.RowMapper<FoliagePeriodicTaskExecution> ReportRowMapper = (ResultSet rs, int rn) -> {
		FoliagePeriodicTaskExecution outVal = new FoliagePeriodicTaskExecution();
		outVal.rifeTime = DbUtils.GetLocalDateTime(rs, rn, "data_rife");
		outVal.startTime = DbUtils.GetLocalDateTime(rs, rn, "data_avvio");
		outVal.endTime = DbUtils.GetLocalDateTime(rs, rn, "data_termine");
		outVal.numRecordElaborati = DbUtils.GetInteger(rs, rn, "num_record_elaborati");
		outVal.submissionTime = DbUtils.GetLocalDateTime(rs, rn, "data_inserimento");
		outVal.idExec = DbUtils.GetInteger(rs, rn, "id_batch_ondemand");
		return outVal;
	};
	public static org.springframework.jdbc.core.RowMapper<FoliagePeriodicTaskExecution> getPeriodicPendingRowMapper(HashMap<Integer, FoliagePeriodicTask> tasksMap) {
		return (ResultSet rs, int rn) -> {
			
			Integer idTask = DbUtils.GetInteger(rs, rn, "id_batch");
			FoliagePeriodicTaskExecution outVal = new FoliagePeriodicTaskExecution();
			outVal.task = tasksMap.get(idTask);
			// Timestamp t = rs.getTimestamp("data_batch");
			// if (t != null) {
			// 	outVal.batchTime = t.toLocalDateTime();
			// }
			outVal.batchTime = DbUtils.GetLocalDateTime(rs, rn, "data_batch");

			

			return outVal;
		};
	}
}
