package it.almaviva.foliage.bean;

import java.util.List;

import it.almaviva.foliage.enums.TipoGeometria;
//import it.almaviva.foliage.enums.TipoRilevamento;
import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.services.WebDal;

import org.springframework.jdbc.core.RowMapper;


public class Rilevamento {
	public Long id;
	public TipoGeometria tipoGeometria;
	public String nome;
	public String note;
	//public TipoRilevamento tipoRilevamento;
	public String tipoRilevamento;
	public String wktGeometria;
	public List<Foto> listaFoto;
	
	public static final String queryForCodiIstaIdUtenteProf = """
select r.id_rile, r.tipo_rilevamento, r.nome, r.note, c.desc_clay, St_asText(r.shape) as wkt
from foliage2.flgista_tab i
	join foliage2.flgrile_tab r using (id_ista)
	left join foliage2.flgclay_tab c using (id_clay)
where i.codi_ista = :codIstanza
	and r.id_utente = :idUtente
	and r.tipo_auth = :authority
	and r.tipo_ambito = :authScope
""";
	public static final RowMapper<Rilevamento> RowMapper = (rs, rn) -> {
		Rilevamento outVal = new Rilevamento();
		outVal.id = DbUtils.GetLong(rs, rn, "id_rile");
		outVal.tipoGeometria = TipoGeometria.fromInt(DbUtils.GetInteger(rs, rn, "tipo_rilevamento"));
		//outVal.tipoRilevamento = TipoRilevamento.fromInt(DbUtils.GetInteger(rs, rn, "desc_clay"));
		outVal.tipoRilevamento = rs.getString("desc_clay");
		outVal.nome = rs.getString("nome");
		outVal.note = rs.getString("note");
		outVal.wktGeometria = rs.getString("wkt");

		return outVal;
	};
	public static final RowMapper<Rilevamento> RowMapper(WebDal dal) {
		return (rs, rowNum)-> {
			Rilevamento outVal = RowMapper.mapRow(rs, rowNum);
			outVal.listaFoto = dal.getFotoRilevamento(outVal.id);
			return outVal;
		};
	}
}
