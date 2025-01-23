package it.almaviva.foliage.bean;

import it.almaviva.foliage.istanze.db.DbUtils;

public class IstanzaAutoaccettazione {
	public String codIstanza;
	public Integer idIstanza;
	public Integer idEnte;

	public static final org.springframework.jdbc.core.RowMapper<IstanzaAutoaccettazione> RowMapper = (rs, rn) -> {
		IstanzaAutoaccettazione outVal = new IstanzaAutoaccettazione();
		outVal.idEnte = DbUtils.GetInteger(rs, rn, "id_ente_terr");
		outVal.idIstanza = DbUtils.GetInteger(rs, rn, "id_ista");
		outVal.codIstanza = rs.getString("codi_ista");
		return outVal;
	};
}
