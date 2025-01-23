package it.almaviva.foliage.bean;

import java.math.BigDecimal;

public class ParticellaCatastaleModulo {
	public String provincia;
	public String comune;
	public String sezione;
	public Integer foglio;
	public String particella;
	public String sub;
	public BigDecimal superficie;
	public BigDecimal superficieIntervento;

	public static String queryFromIdIsta = """
select comune, provincia, sezione, comune, foglio, particella, sub, superficie, superficie_intervento
from foliage2.flgpart_catastali_tab pc
	join foliage2.flgcomu_viw c using (id_comune)
	join foliage2.flgprov_viw p using (id_provincia)
where id_ista = :idIsta""";

	public static final org.springframework.jdbc.core.RowMapper<ParticellaCatastaleModulo> RowMapper = (rs, rowNum)-> {
		ParticellaCatastaleModulo p = new ParticellaCatastaleModulo();
		p.provincia = rs.getString("provincia");
		p.comune = rs.getString("comune");
		p.sezione = rs.getString("sezione");
		p.foglio = rs.getInt("foglio");
		if (rs.wasNull()) {
			p.foglio = null;
		}
		p.particella = rs.getString("particella");
		p.sub = rs.getString("sub");
		p.superficie = rs.getBigDecimal("superficie");
		p.superficieIntervento = rs.getBigDecimal("superficie_intervento");
		if (rs.wasNull()) {
			p.superficieIntervento = null;
		}

		return p;
	};
}
