package it.almaviva.foliage.bean;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;


import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.services.WebDal;
import lombok.Getter;
import lombok.Setter;

public class FileIstanzaApp {
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
	private FormioFile[] files;

	
	public static org.springframework.jdbc.core.RowMapper<FileIstanzaApp> RowMapper(WebDal dal) {
		return (rs, rowNum)-> {
			FileIstanzaApp f = new FileIstanzaApp();
			f.setTipo(rs.getString("tipo"));
			f.setCategoria(rs.getString("categoria"));
			f.setDescrizione(rs.getString("descrizione"));
			Integer idFile = rs.getInt("id_file");
			Base64FormioFile[] base64Files = DbUtils.getBase64FormioFiles(dal, idFile);
			FormioFile[] fileArr = Arrays.asList(base64Files).stream().map(
				x -> {
					FormioFile outVal = null;
					try {
						outVal = new FormioFile(x);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return outVal;
				}
			).toArray(size -> new FormioFile[size]);
			f.setFiles(fileArr);
			return f;
		};
	}
}
