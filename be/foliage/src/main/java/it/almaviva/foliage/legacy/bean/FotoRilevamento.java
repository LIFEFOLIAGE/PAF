package it.almaviva.foliage.legacy.bean;

import org.springframework.jdbc.core.RowMapper;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

public class FotoRilevamento {

	@Schema(description = "Id foto rilevamento")
	@Getter
	@Setter
	private Integer idFotoRile;
	
	@Schema(description = "Id rilevamento")
	@Getter
	@Setter
	private Long idRile;
		
	@Schema(description = "Nome foto")
	@Getter
	@Setter
	private String nome;
	
	@Schema(description = "Array di byte")
	@Getter
	@Setter
	private byte[] file;
	
	
	@Getter
	@Setter
	private int flagVali;

	
	@Getter
	@Setter
	private String userIns;
	
	@Getter
	@Setter
	private String userUpd;
	
	public static RowMapper<FotoRilevamento> RowMapper() {
		return (rs, rowNum)-> {	
			FotoRilevamento r=new FotoRilevamento();
			r.setIdRile(rs.getLong("id_rile"));
			r.setNome(rs.getString("nome"));
			r.setFile(rs.getBytes("file"));
				
			return r;
		};
	}
	
}
