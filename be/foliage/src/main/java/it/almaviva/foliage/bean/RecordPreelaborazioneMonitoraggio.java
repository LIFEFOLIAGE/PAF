package it.almaviva.foliage.bean;

import java.math.BigDecimal;
import java.time.LocalDate;

import it.almaviva.foliage.istanze.db.DbUtils;

public class RecordPreelaborazioneMonitoraggio {
	public String codIstanza;
	public String nomeUo;
	public String formaTrattamentoFustaia;
	public String formaTrattamentoCeduo;
	public BigDecimal superficie;
	public LocalDate dataInizioAutorizzazione;
	public LocalDate dataFineAutorizzazione;
	public String shape;
	
	
	public static org.springframework.jdbc.core.RowMapper<RecordPreelaborazioneMonitoraggio> RowMapper = (rs, rn)-> {
		RecordPreelaborazioneMonitoraggio r = new RecordPreelaborazioneMonitoraggio();
			r.codIstanza = rs.getString("codi_ista");
			r.nomeUo = rs.getString("nome_uog");
			r.formaTrattamentoFustaia = rs.getString("cod_forma_trattamento_fustaia");
			r.formaTrattamentoCeduo = rs.getString("cod_forma_trattamento_ceduo");
			r.superficie = DbUtils.GetDecimal(rs, rn, "superficie_utile");
			r.dataInizioAutorizzazione = DbUtils.GetLocalDate(rs, rn, "data_inizio_autorizzazione");
			r.dataFineAutorizzazione = DbUtils.GetLocalDate(rs, rn, "data_fine_autorizzazione");
			r.shape = rs.getString("shape");

			return r;
		};
}
