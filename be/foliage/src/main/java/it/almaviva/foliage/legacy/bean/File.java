package it.almaviva.foliage.legacy.bean;
import org.springframework.jdbc.core.RowMapper;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

public class File {
	

	@Schema(description = "Nome File")
	@Getter
	@Setter
	private String nomeFile;
	
	@Schema(description = "Tipo dell'allegato")
	@Getter
	@Setter
	private String tipoFile;
	
	@Getter
	@Setter
	private int idAlle;
	
	@Schema(description = "Array di byte")
	@Getter
	@Setter
	private byte[] file;
	
	@Getter
	@Setter
	private int idDocu;
	
	public static RowMapper<File> RowMapper() {
		return (rs, rowNum)-> {
			File f=new File();
			f.setFile(rs.getBytes("file"));
			f.setNomeFile(rs.getString("nome"));
			f.setIdDocu(rs.getInt("id_docu"));
			f.setTipoFile(rs.getString("tipoFile"));
			return f;
		};
	}
}
