package it.almaviva.foliage.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.JsonParser;

import it.almaviva.foliage.function.JsonIO;
import it.almaviva.foliage.istanze.db.DbUtils;

public class AttivitaMonitoraggioBean {
	public Integer idRichiesta;
	public Object parametri;
	public LocalDateTime dataAvvioPianificata;
	public LocalDate dataRiferimento;
	//public List<RecordPreelaborazioneMonitoraggio> datiPreelaborazione;


	public static org.springframework.jdbc.core.RowMapper<AttivitaMonitoraggioBean> RowMapper = (rs, rn)-> {
			AttivitaMonitoraggioBean r = new AttivitaMonitoraggioBean();
			r.idRichiesta = DbUtils.GetInteger(rs, rn, "id_batch_ondemand");
			LocalDateTime dataAvvioPianificata = DbUtils.GetLocalDateTime(rs, rn, "data_avvio_pianificata");
			LocalDate dataRiferimento = DbUtils.GetLocalDate(rs, rn, "data_rife");
			r.dataAvvioPianificata = dataAvvioPianificata;
			r.dataRiferimento = dataRiferimento;
			//rs.getString("data_avvio_pianificata");
			String strParametri = rs.getString("parametri");
			
			r.parametri = JsonIO.gson.fromJson(JsonParser.parseString(strParametri), Object.class);
			return r;
		};
}
