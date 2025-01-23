package it.almaviva.foliage.bean;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.services.AbstractDal;

public class FoliageReport {
	public Integer idBatch;
	public String codice;
	public String descrizione;
	public String nomeFile;
	public List<String> formatoFiles;
	public String formatoDataFile;
	public String formatoDataDesc;
	public List<FoliagePeriodicTaskExecution> listaEsecuzioni;
	public static String queryForReportDisponibili = """
select id_batch, cod_report, desc_report, report_name, formato_files, formato_data_file, formato_data_desc
from foliage2.flgconf_batch_report_tab r
where not exists (
		select *
		from foliage2.flgprofili_report_tab pr
		where pr.id_report = r.id_report 
	)
	or exists (
		select *
		from foliage2.flgprofili_report_tab pr
			join foliage2.flgprof_tab p using (id_profilo)
		where pr.id_report = r.id_report
			and p.tipo_auth = :authority
			and p.tipo_ambito = :authScope
	)
order by cod_report""";


	public static String queryForReportFromIdBatch = """
select id_batch, cod_report, desc_report, report_name, formato_files, formato_data_file, formato_data_desc
from foliage2.flgconf_batch_report_tab r
where r.id_batch = :idBatch
order by cod_report""";
	public static RowMapper<FoliageReport> RowMapper = (rs, rn) -> {
		FoliageReport outVal = new FoliageReport();
		outVal.idBatch = DbUtils.GetInteger(rs, rn, "id_batch");
		outVal.codice = rs.getString("cod_report");
		outVal.descrizione = rs.getString("desc_report");
		outVal.nomeFile = rs.getString("report_name");
		outVal.formatoDataFile = rs.getString("formato_data_file");
		outVal.formatoDataDesc = rs.getString("formato_data_desc");
		
		{
			Array formatoFile = rs.getArray("formato_files");
			ResultSet rs1 = formatoFile.getResultSet();
			outVal.formatoFiles = new LinkedList<String>();
			while (rs1.next()) {
				outVal.formatoFiles.add(rs1.getString(2));
			}	
		}

		return outVal;
	};

	public static RowMapper<FoliageReport> getRowMapperWithEsecuzioni(AbstractDal dal)  {
		return (ResultSet rs, int rn) -> {
			// FoliageReport outVal = new FoliageReport();
			// outVal.idBatch = DbUtils.GetInteger(rs, rn, "id_batch");
			// outVal.codice = rs.getString("cod_report");
			// outVal.descrizione = rs.getString("desc_report");
			// outVal.nomeFile = rs.getString("report_name");
			// outVal.formatoDataFile = rs.getString("formato_data_file");
			// outVal.formatoDataDesc = rs.getString("formato_data_desc");
			FoliageReport outVal = FoliageReport.RowMapper.mapRow(rs, rn);

			HashMap<String, Object> pars = new HashMap<>();
			pars.put("idBatch", outVal.idBatch);
			outVal.listaEsecuzioni = dal.query(
				FoliagePeriodicTaskExecution.QueryFromIdBatchForReport, 
				pars, 
				FoliagePeriodicTaskExecution.ReportRowMapper
			);
			return outVal;
		};
	}
}
