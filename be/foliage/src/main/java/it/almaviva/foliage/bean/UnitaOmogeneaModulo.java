package it.almaviva.foliage.bean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import it.almaviva.foliage.services.WebDal;

public class UnitaOmogeneaModulo extends SoprasuoloForestaleModulo{

	public BigDecimal superficie;
	public String nomeUo;
	public String metodoDiCubatura;
	public String descMetodoDiCubatura;
	public List<DescrizioneIterventoModulo> descrizioneInterventi;

	
	public static org.springframework.jdbc.core.RowMapper<UnitaOmogeneaModulo> RowMapperUO(WebDal dal) {
		return (rs, rowNum)-> {
			SoprasuoloForestaleModulo sfm = SoprasuoloForestaleModulo.RowMapper.mapRow(rs, rowNum);

			UnitaOmogeneaModulo uom = new UnitaOmogeneaModulo();
			uom.nomeUo = rs.getString("nome_uog");
			uom.superficieUtile = sfm.superficieUtile;
			uom.formaDiGoverno = sfm.formaDiGoverno;
			uom.speciePrevalente = sfm.speciePrevalente;
			uom.strutturaSoprasuolo = sfm.strutturaSoprasuolo;
			uom.formaTrattamentoPrecedente = sfm.formaTrattamentoPrecedente;
			uom.formaTrattamentoProposta = sfm.formaTrattamentoProposta;
			uom.tipoSoprasuolo = sfm.tipoSoprasuolo;
			uom.etaMedia = sfm.etaMedia;

			uom.superficie = rs.getBigDecimal("superficie");
			uom.metodoDiCubatura = rs.getString("metodo_cubatura");
			uom.descMetodoDiCubatura = rs.getString("desc_metodo_cubatura");
			

			int idIsta = rs.getInt("id_ista");
			int progUog = rs.getInt("prog_uog");
			HashMap<String, Object> parMap = new HashMap<>();
			parMap.put("idIsta", idIsta);
			parMap.put("progUog", progUog);

			uom.destinazioniUso = dal.query(
				DestinazioneUsoModulo.queryFromIdIstaProgUog,
				parMap,
				DestinazioneUsoModulo.RowMapper
			);
			uom.descrizioneInterventi = dal.query(
				DescrizioneIterventoModulo.queryFromIdIstaProgUog,
				parMap,
				DescrizioneIterventoModulo.RowMapper
			);
			return uom;
		};
	}

	public static final String queryFromIdIsta = """
select id_ista, prog_uog, nome_uog, superficie, superficie_utile/10000 as superficie_utile, desc_gove, 
	desc_sspr as strut_soprasuolo, eta_media, coalesce(tipo_soprasuolo, 'Misto') as tipo_soprasuolo,
	(
		select nome_specie
		from foliage2.FLGSPECI_UOG_TAB su
			join foliage2.flgspecie_tab s using (id_specie)
		where id_ista = fot.id_ista
			and prog_uog = fot.prog_uog
		order by su.percentuale_intervento, prog_specie_uog desc
		fetch first 1 row only
	) as specie_prevalente,
	metodo_cubatura, desc_metodo_cubatura,
	trattamento_ceduo_prec, trattamento_fustaia_prec,
	trattamento_ceduo_prop, trattamento_fustaia_prop
--select *
from foliage2.flgunita_omogenee_tab fot
	left join foliage2.flgsspr_tab s using (id_sspr)
	left join foliage2.flgunita_omogenee_cubatura_tab c using (id_ista, prog_uog)
	left join (
		values ('A', 'Tavole di cubatura locale a 1 entrata'),
			('B', 'Tavole di cubatura locale a 2 entrate'),
			('C', 'Sistema di tariffe'),
			('D', 'Tavole di cubature a doppia entrata o equazioni allometriche dell''IFNI 1985'),
			('E', 'Tavole di cubature a doppia entrata o equazioni allometriche dell''INFC 2005 e 2015 (Tabacchi et al. 2011)'),
			('F', 'Albero modello'),
			('G', 'Altro metodo')
	) mc(cod_metodo_cubatura, metodo_cubatura) using (cod_metodo_cubatura)
	left join (
		select id_ista, prog_uog,
			max(case when clas = 'PREC' and desc_gove = 'Ceduo' then desc_forma_trattamento end) as trattamento_ceduo_prec,
			max(case when clas = 'PREC' and desc_gove = 'Fustaia' then desc_forma_trattamento end) as trattamento_fustaia_prec,
			max(case when clas = 'PROP' and desc_gove = 'Ceduo' then desc_forma_trattamento end) as trattamento_ceduo_prop,
			max(case when clas = 'PROP' and desc_gove = 'Fustaia' then desc_forma_trattamento end) as trattamento_fustaia_prop
		from (
				select id_ista, prog_uog, id_gove, clas, t.id_forma_trattamento
				from foliage2.flgunita_omogenee_trattamento_tab fott
					cross join lateral(
						values ('PREC', id_forma_trattamento_prec),
							('PROP', id_forma_trattamento)
					) as t(clas, id_forma_trattamento)
			) as t
			join foliage2.flgforme_trattamento_tab using (id_forma_trattamento, id_gove)
			join foliage2.flggove_tab using (id_gove)
		group by id_ista, prog_uog	
	) as ft using (id_ista, prog_uog)
where fot.id_ista = :idIsta""";

}
