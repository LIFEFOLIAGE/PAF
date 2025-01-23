package it.almaviva.foliage.bean;

import java.math.BigDecimal;

public class DestinazioneUsoModulo {
	// public String macrocategoria;
	// public String categoria;
	public String tipoSoprasuolo;
	public String specie;
	public BigDecimal percCoperturaSpecie;
	public String destinazioneUso;
	public String assortimento;
	public BigDecimal percAutoconsumo;
	public BigDecimal percVendita;

	public static String queryFromIdIsta = """
select s.tipo_soprasuolo,
	nome_specie, su.percentuale_intervento,
	destinazione_uso, desc_assortimento, perc_autoconsumo, perc_vendita
from (
		select id_ista, id_specie,
			id_assortimento,
			max(case when a.is_autoconsumo then a.percentuale_ass end) as perc_autoconsumo,
			max(case when not a.is_autoconsumo then a.percentuale_ass end) as perc_vendita
		from foliage2.FLGASS_SPECI_ISTA_TAB a
			join foliage2.flgspeci_ista_tab fit using (ID_ISTA, ID_SPECIE)
			join foliage2.flgassortimento_tab ft  using (ID_ASSORTIMENTO)
		where id_ista = :idIsta
		group by id_ista, id_specie, id_assortimento
	) as d
	join foliage2.flgspeci_ista_tab su using (id_ista, id_specie)
	join foliage2.flgassortimento_tab a using (ID_ASSORTIMENTO)
	join foliage2.flgspecie_tab s using (id_specie)""";

	public static String queryFromIdIstaProgUog = """
select s.tipo_soprasuolo,
	nome_specie, su.percentuale_intervento,
	destinazione_uso, desc_assortimento, perc_autoconsumo, perc_vendita
from (
		select id_ista, prog_uog, id_specie,
			id_assortimento,
			max(case when a.is_autoconsumo then a.percentuale_ass end) as perc_autoconsumo,
			max(case when not a.is_autoconsumo then a.percentuale_ass end) as perc_vendita
		from foliage2.FLGASS_SPECI_UOG_TAB a
			join foliage2.FLGSPECI_UOG_TAB fit using (ID_ISTA, PROG_UOG, ID_SPECIE)    
		where ID_ISTA = :idIsta
				and PROG_UOG = :progUog
		group by id_ista, prog_uog, id_specie, id_assortimento
	) as d
	join foliage2.FLGSPECI_UOG_TAB su using (id_ista, prog_uog, id_specie)
	join foliage2.flgassortimento_tab a using (ID_ASSORTIMENTO)
	join foliage2.flgspecie_tab s using (id_specie)""";


	public static final org.springframework.jdbc.core.RowMapper<DestinazioneUsoModulo> RowMapper = (rs, rowNum)-> {
		DestinazioneUsoModulo dum = new DestinazioneUsoModulo();
		// dum.macrocategoria = rs.getString("nome_macrocategoria");
		// dum.categoria = rs.getString("nome_categoria");
		dum.tipoSoprasuolo = rs.getString("tipo_soprasuolo");
		dum.specie = rs.getString("nome_specie");
		dum.percCoperturaSpecie = rs.getBigDecimal("percentuale_intervento");
		dum.destinazioneUso = rs.getString("destinazione_uso");
		dum.assortimento = rs.getString("desc_assortimento");
		dum.percAutoconsumo = rs.getBigDecimal("perc_autoconsumo");
		dum.percVendita = rs.getBigDecimal("perc_vendita");

		return dum;
	};
}


