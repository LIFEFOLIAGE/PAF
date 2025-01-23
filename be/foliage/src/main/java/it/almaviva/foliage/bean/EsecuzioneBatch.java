package it.almaviva.foliage.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.services.AbstractDal;

public class EsecuzioneBatch {
	public Integer idEsecuzione;
	public LocalDateTime dataSottomissione;
	public LocalDateTime dataInizio;
	public LocalDateTime dataFine;

	public static RowMapper<EsecuzioneBatch> RowMapper = (rs, rn) -> {
		EsecuzioneBatch outVal = new EsecuzioneBatch();
		outVal.idEsecuzione = DbUtils.GetInteger(rs, rn, "id_exec_batch");
		outVal.dataSottomissione = DbUtils.GetLocalDateTime(rs, rn, "data_submission");
		outVal.dataInizio = DbUtils.GetLocalDateTime(rs, rn, "data_avvio");
		outVal.dataFine = DbUtils.GetLocalDateTime(rs, rn, "data_termine");
		return outVal;
	};
}
