package it.almaviva.foliage.bean;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.services.AbstractDal;
import it.almaviva.foliage.services.WebDal;
import lombok.Getter;
import lombok.Setter;

public class FileIstanzaWeb {
	@Getter
	@Setter
	private String tipo;

	@Getter
	@Setter
	private String categoria;

	@Getter
	@Setter
	private String descrizione;

	@Getter
	@Setter
	private Base64FormioFile[] files;

	
	public static org.springframework.jdbc.core.RowMapper<FileIstanzaWeb> RowMapper(AbstractDal dal) {
		return (rs, rowNum)-> {
			FileIstanzaWeb f = new FileIstanzaWeb();
			f.setTipo(rs.getString("tipo"));
			f.setCategoria(rs.getString("categoria"));
			f.setDescrizione(rs.getString("descrizione"));
			Integer idFile = rs.getInt("id_file");
			Base64FormioFile[] base64Files = DbUtils.getBase64FormioFiles(dal, idFile);
			
			f.setFiles(base64Files);
			return f;
		};
	}
}
