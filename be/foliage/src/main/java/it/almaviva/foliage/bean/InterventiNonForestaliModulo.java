package it.almaviva.foliage.bean;

import java.math.BigDecimal;

public class InterventiNonForestaliModulo {
	public String usoDelSuolo;
	public String interventoPrevisto;
	public String unitaMisutaIntervento;
	public BigDecimal valoreIntervento;
	public String descrizioneIntervento;

	public static String queryFromIdIsta = """
select us.desc_uso_suolo, tic.nome_tipo_intervento, ic.valore_dichiatato, tic.parametro_richiesto, ic.desc_intervento
from foliage2.FLGISTA_INTERVENTO_COMUNICAZIONE_TAB ic
	join foliage2.flgtipo_intervento_tab tic  using (id_tipo_intervento)
	join foliage2.flguso_suolo_tab us using (id_uso_suolo)
where id_ista = :idIsta""";

	public static org.springframework.jdbc.core.RowMapper<InterventiNonForestaliModulo> RowMapper = (rs, rowNum)-> {
		InterventiNonForestaliModulo outVal = new InterventiNonForestaliModulo();
		outVal.usoDelSuolo = rs.getString("desc_uso_suolo");
		outVal.interventoPrevisto = rs.getString("nome_tipo_intervento");
		outVal.valoreIntervento = rs.getBigDecimal("valore_dichiatato");
		if (rs.wasNull()) {
			outVal.valoreIntervento = null;
		}
		outVal.unitaMisutaIntervento = rs.getString("parametro_richiesto");
		outVal.descrizioneIntervento = rs.getString("desc_intervento");

		return outVal;
	};
}
