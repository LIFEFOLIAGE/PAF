package it.almaviva.foliage.bean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import it.almaviva.foliage.services.WebDal;

public class SoprasuoloForestaleModulo {
	public String formaDiGoverno;
	public String strutturaSoprasuolo;
	public String tipoSoprasuolo;
	public Integer etaMedia;
	public String speciePrevalente;
	public String[] formaTrattamentoProposta;
	public String[] formaTrattamentoPrecedente;
	public BigDecimal superficieUtile;
	
	public List<DestinazioneUsoModulo> destinazioniUso;
	public static org.springframework.jdbc.core.RowMapper<SoprasuoloForestaleModulo> RowMapper = (rs, rowNum)-> {
		SoprasuoloForestaleModulo uom = new SoprasuoloForestaleModulo();
		uom.superficieUtile = rs.getBigDecimal("superficie_utile");
		uom.formaDiGoverno = rs.getString("desc_gove");
		uom.speciePrevalente = rs.getString("specie_prevalente");
		uom.strutturaSoprasuolo = rs.getString("strut_soprasuolo");
		uom.tipoSoprasuolo = rs.getString("tipo_soprasuolo");
		uom.etaMedia = rs.getInt("eta_media");
		
		if (rs.wasNull()) {
			uom.etaMedia = null;
		}

		switch (uom.formaDiGoverno) {
			case "Misto": {
				uom.formaTrattamentoPrecedente = new String[] {
					String.format("Fustaia: %s", rs.getString("trattamento_fustaia_prec")),
					String.format("Ceduo: %s", rs.getString("trattamento_ceduo_prec"))
				};
				uom.formaTrattamentoProposta = new String[] {
					String.format("Fustaia: %s", rs.getString("trattamento_fustaia_prop")),
					String.format("Ceduo: %s", rs.getString("trattamento_ceduo_prop"))
				};

			}; break;
			case "Fustaia": {
				uom.formaTrattamentoPrecedente = new String[] {
					rs.getString("trattamento_fustaia_prec")
				};
				uom.formaTrattamentoProposta = new String[] {
					rs.getString("trattamento_fustaia_prop")
				};
			}; break;
			case "Ceduo": {
				uom.formaTrattamentoPrecedente = new String[] {
					rs.getString("trattamento_ceduo_prec")
				};
				uom.formaTrattamentoProposta = new String[] {
					rs.getString("trattamento_ceduo_prop")
				};
			}; break;
		}

		// uom.nomeUo = rs.getString("nome_uog");
		// uom.metodoDiCubatura = rs.getString("metodo_cubatura");
		// uom.descMetodoDiCubatura = rs.getString("desc_metodo_cubatura");
		// int idIsta = rs.getInt("id_ista");
		// int progUog = rs.getInt("prog_uog");
		
		// HashMap<String, Object> parMap = new HashMap<>();
		// parMap.put("idIsta", idIsta);
		// parMap.put("progUog", progUog);

		// uom.destinazioniUso = dal.query(
		// 	DestinazioneUsoModulo.queryFromIdIstaProgUog,
		// 	parMap,
		// 	DestinazioneUsoModulo.RowMapper
		// );
		// uom.descrizioneInterventi = dal.query(
		// 	DescrizioneIterventoModulo.queryFromIdIstaProgUog,
		// 	parMap,
		// 	DescrizioneIterventoModulo.RowMapper
		// );
		return uom;
	};

	public static org.springframework.jdbc.core.RowMapper<SoprasuoloForestaleModulo> RowMapperSF(WebDal dal) {
		return (rs, rowNum)-> {
			SoprasuoloForestaleModulo uom = RowMapper.mapRow(rs, rowNum);
			int idIsta = rs.getInt("id_ista");
			HashMap<String, Object> parMap = new HashMap<>();
			parMap.put("idIsta", idIsta);

			uom.destinazioniUso = dal.query(
				DestinazioneUsoModulo.queryFromIdIsta,
				parMap,
				DestinazioneUsoModulo.RowMapper
			);
			// uom.descrizioneInterventi = dal.query(
			// 	DescrizioneIterventoModulo.queryFromIdIsta,
			// 	parMap,
			// 	DescrizioneIterventoModulo.RowMapper
			// );
			return uom;
		};
	}

	public static final String queryFromIdIsta = """
select id_ista, superficie_utile, desc_gove, 
		desc_sspr as strut_soprasuolo, eta_media, coalesce(tipo_soprasuolo, 'Misto') as tipo_soprasuolo,
		(
			select nome_specie
			from foliage2.flgspeci_ista_tab si
				join foliage2.flgspecie_tab s using (id_specie)
			where id_ista = fot.id_ista
			order by si.percentuale_intervento, si.prog desc
			fetch first 1 row only
		) as specie_prevalente,
		trattamento_ceduo_prec, trattamento_fustaia_prec,
		trattamento_ceduo_prop, trattamento_fustaia_prop
	--select *
	from (
			select id_ista, superficie_intervento/10000 as superficie_utile, desc_gove, eta_media, tipo_soprasuolo, desc_sspr
			from foliage2.FLGISTA_TAGLIO_BOSCHIVO_TAB
				left join foliage2.flgsspr_tab s using (id_sspr)
			where id_ista = :idIsta
		) as fot
		left join (
			select id_ista, 
				max(case when clas = 'PREC' and desc_gove = 'Ceduo' then desc_forma_trattamento end) as trattamento_ceduo_prec,
				max(case when clas = 'PREC' and desc_gove = 'Fustaia' then desc_forma_trattamento end) as trattamento_fustaia_prec,
				max(case when clas = 'PROP' and desc_gove = 'Ceduo' then desc_forma_trattamento end) as trattamento_ceduo_prop,
				max(case when clas = 'PROP' and desc_gove = 'Fustaia' then desc_forma_trattamento end) as trattamento_fustaia_prop
			from (
					select id_ista, id_gove, clas, t.id_forma_trattamento
					from foliage2.FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_TAB
						cross join lateral(
							values ('PREC', id_forma_trattamento_prec),
								('PROP', id_forma_trattamento)
						) as t(clas, id_forma_trattamento)
					where id_ista = :idIsta
				) as t3
				join foliage2.flgforme_trattamento_tab using (id_forma_trattamento, id_gove)
				join foliage2.flggove_tab using (id_gove)
			group by id_ista
		) as t2 using (id_ista)""";
}
