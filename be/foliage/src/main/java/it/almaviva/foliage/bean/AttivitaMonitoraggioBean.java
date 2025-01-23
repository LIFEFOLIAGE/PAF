package it.almaviva.foliage.bean;

import java.util.List;

import com.google.gson.JsonParser;

import it.almaviva.foliage.function.JsonIO;
import it.almaviva.foliage.istanze.db.DbUtils;

public class AttivitaMonitoraggioBean {
	public Integer idRichiesta;
	public Object parametri;
	//public List<RecordPreelaborazioneMonitoraggio> datiPreelaborazione;


	public static org.springframework.jdbc.core.RowMapper<AttivitaMonitoraggioBean> RowMapper = (rs, rn)-> {
			AttivitaMonitoraggioBean r = new AttivitaMonitoraggioBean();
			r.idRichiesta = DbUtils.GetInteger(rs, rn, "id_batch_ondemand");
			String strParametri = rs.getString("parametri");
			
			r.parametri = JsonIO.gson.fromJson(JsonParser.parseString(strParametri), Object.class);
			return r;
		};
}
