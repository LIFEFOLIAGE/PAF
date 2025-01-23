package it.almaviva.foliage.bean;

import org.springframework.jdbc.core.RowMapper;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import it.almaviva.foliage.istanze.db.DbUtils;
import org.springframework.http.MediaType;

public class Foto {
	public static final String prefix = String.format( "data:%s;base64,", MediaType.IMAGE_PNG);// "data:image/png;base64,";

	private static Encoder Base64enc = Base64.getEncoder();
	private static Decoder Base64dec = Base64.getDecoder();


	public Integer idFoto;
	public String nomeFoto;
	public String base64encFoto;


	public static final String queryForIdRile = """
select id_foto, nome, file
from foliage2.flgfoto_tab
where id_rile = :idRile
""";
	public static final RowMapper<Foto> RowMapper = (rs, rn) -> {
		Foto outVal = new Foto();
		outVal.idFoto = DbUtils.GetInteger(rs, rn, "id_foto");
		outVal.nomeFoto = rs.getString("nome");
		byte[] arrContenuto = rs.getBytes("file");
		String base64 = Base64enc.encodeToString(arrContenuto);
		outVal.base64encFoto = String.format(
			"%s%s", 
			prefix,
			base64
		);
		return outVal;
	};
}
