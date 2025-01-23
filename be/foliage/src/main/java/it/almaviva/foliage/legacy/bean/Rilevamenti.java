package it.almaviva.foliage.legacy.bean;

import org.springframework.jdbc.core.RowMapper;

import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


public class Rilevamenti {
	
	
	@Schema(description = "Id del rilevamento, in inserimento è null" )
	@Getter
	@Setter
	private Long idRile;
	
	@Schema(description = "Id istanza a cui è legato il rilevamento" , required=true)
	@Getter
	@Setter
	private Integer idIsta;
	
	@Schema(description = "tipologia del rilevamento", required=true, example = "0=point,1=shape")
	@Getter
	@Setter
	private int tipoRile;
	
	@Schema(description = "Nome rilevamento", required=true)
	@Getter
	@Setter
	private String nome;
	
	@Schema(description = "Note rilevamento")
	@Getter
	@Setter
	private String note;
	
	@Schema(description = "id tipologia del rilevamento", required=true)
	@Getter
	@Setter
	private Integer idClay;
	
	@Schema(description = "Geometria del rilevamento", required=true)
	@Getter
	@Setter
	private String geometry;
	
	@Schema(description = "Foto del rilevamento")
	@Getter
	@Setter
	private List<FotoRilevamento> foto;
	
	@Getter
	@Setter
	private int flagVali;

	@Schema(description = "Username dell'utente", required=true)
	@Getter
	@Setter
	private String userIns;

	@Getter
	@Setter
	private String userUpd;

	
	public static RowMapper<Rilevamenti> RowMapper() {
		return (rs, rowNum)-> {
			Rilevamenti r=new Rilevamenti();
			r.setIdRile(rs.getLong("id_rile"));
			r.setNome(rs.getString("nome"));
			r.setNote(rs.getString("note"));
			r.setTipoRile(rs.getInt("tipo_rilevamento"));
			r.setIdClay(rs.getInt("id_clay"));
			r.setGeometry(rs.getString("geom"));
			r.setIdIsta(rs.getInt("id_ista"));
				
			return r;
		};
	}
	
}
