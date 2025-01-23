package it.almaviva.foliage.bean;

import java.math.BigDecimal;

public class DescrizioneIterventoModulo {
	public String formaDiGoverno;
	public String varieta;
	public BigDecimal presentiNumHa;
	public BigDecimal presentiMcHa;

	public BigDecimal daRilasciareNumHa;
	public BigDecimal daRilasciareMcHa;
	
	public BigDecimal daTagliareNumHa;
	public BigDecimal daTagliareMcHa;

	public static final String queryFromIdIstaProgUog = """
select case 
		when cat_cubatura in ('specie0', 'specie1') then 'Fustaia'
		else 'Ceduo'
	end as forma_di_governo,
	case cat_cubatura
		when 'POLL' then 'Polloni'
		when 'ALL' then 'Allievi'
		when 'MATR2' then 'Matricine di 2° turno'
		when 'MATR3' then 'Matricine di 3° turno'
		when 'specie0' then (
				select nome_specie
				from foliage2.FLGSPECI_UOG_TAB suo
					join foliage2.flgspecie_tab s using (id_specie)
				where suo.id_ista = uovc.id_ista
					and suo.prog_uog  = uovc.prog_uog
					and suo.prog_specie_uog = 0
			)
		when 'specie1' then (
				select nome_specie
				from foliage2.FLGSPECI_UOG_TAB suo
					join foliage2.flgspecie_tab s using (id_specie)
				where suo.id_ista = uovc.id_ista
					and suo.prog_uog  = uovc.prog_uog
					and suo.prog_specie_uog = 1
			)
	end as varieta,
	presenti_num_ha, presenti_mc_ha, tagliare_num_ha, tagliare_mc_ha, rilasciare_num_ha, rilasciare_mc_ha
from (
		select id_ista, prog_uog,
			cat_cubatura,
			max(case when cod_gruppo_cubatura = 'PRES' then valore_num_ha end) as presenti_num_ha,
			max(case when cod_gruppo_cubatura = 'PRES' then valore_mq_ha end) as presenti_mc_ha, 
			max(case when cod_gruppo_cubatura = 'RILASCIA' then valore_num_ha end) as tagliare_num_ha,
			max(case when cod_gruppo_cubatura = 'RILASCIA' then valore_mq_ha end) as tagliare_mc_ha, 
			max(case when cod_gruppo_cubatura = 'TAGLIA' then valore_num_ha end) as rilasciare_num_ha,
			max(case when cod_gruppo_cubatura = 'TAGLIA' then valore_mq_ha end) as rilasciare_mc_ha
		from foliage2.FLGUNITA_OMOGENEE_VAL_CUBATURA_TAB
		where id_ista = :idIsta
			and prog_uog = :progUog
			and cat_cubatura in ('POLL', 'ALL', 'MATR2', 'MATR3', 'specie0', 'specie1')
		group by id_ista, prog_uog, cat_cubatura
	) uovc""";
	public static final org.springframework.jdbc.core.RowMapper<DescrizioneIterventoModulo> RowMapper = (rs, rowNum)-> {
		DescrizioneIterventoModulo dim = new DescrizioneIterventoModulo();
		dim.formaDiGoverno = rs.getString("forma_di_governo");
		dim.varieta = rs.getString("varieta");

		dim.presentiNumHa = rs.getBigDecimal("presenti_num_ha");
		if (rs.wasNull()) {
			dim.presentiNumHa = null;
		}
		dim.presentiMcHa = rs.getBigDecimal("presenti_mc_ha");
		if (rs.wasNull()) {
			dim.presentiMcHa = null;
		}

		dim.daTagliareNumHa = rs.getBigDecimal("tagliare_num_ha");
		if (rs.wasNull()) {
			dim.daTagliareNumHa = null;
		}
		dim.daTagliareMcHa = rs.getBigDecimal("tagliare_mc_ha");
		if (rs.wasNull()) {
			dim.daTagliareMcHa = null;
		}

		dim.daRilasciareNumHa = rs.getBigDecimal("rilasciare_num_ha");
		if (rs.wasNull()) {
			dim.daRilasciareNumHa = null;
		}
		dim.daRilasciareMcHa = rs.getBigDecimal("rilasciare_mc_ha");
		if (rs.wasNull()) {
			dim.daRilasciareMcHa = null;
		}
		return dim;
	};
}
