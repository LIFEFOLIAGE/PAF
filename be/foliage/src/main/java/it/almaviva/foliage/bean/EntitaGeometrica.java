package it.almaviva.foliage.bean;

import java.math.BigDecimal;

public class EntitaGeometrica {
	public String nome;
	public String geometria;
	public BigDecimal superficie;
	
	public static org.springframework.jdbc.core.RowMapper<EntitaGeometrica> RowMapper() {
		return (rs, rowNum)-> {
			EntitaGeometrica r = new EntitaGeometrica();
			r.nome = rs.getString("nome");
			r.geometria = rs.getString("geometria");
			r.superficie = rs.getBigDecimal("superficie");
			
			return r;
		};
	}
}
