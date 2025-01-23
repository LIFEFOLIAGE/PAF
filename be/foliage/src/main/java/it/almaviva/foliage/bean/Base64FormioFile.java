package it.almaviva.foliage.bean;

import java.io.OutputStream;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

public class Base64FormioFile {
	private static Encoder Base64enc = Base64.getEncoder();
	private static Decoder Base64dec = Base64.getDecoder();

	@Getter
	@Setter
	private String hash;
	
	@Getter
	@Setter
	private String name;
	
	@Getter
	@Setter
	private String originalName;
	
	@Getter
	@Setter
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Integer size;
	
	@Getter
	@Setter
	private String storage;
	
	@Getter
	@Setter
	private String type;
	
	@Getter
	@Setter
	private String url;
	

	public static org.springframework.jdbc.core.RowMapper<Base64FormioFile> RowMapper() {
		return (rs, rowNum)-> {
			Base64FormioFile r=new Base64FormioFile();
			r.setHash(rs.getString("hash_file"));
			r.setName(rs.getString("file_name"));
			r.setOriginalName(rs.getString("original_file_name"));
			r.setSize(rs.getInt("file_size"));
			r.setStorage(rs.getString("storage"));
			r.setType(rs.getString("file_type"));
			r.setUrl(new String(rs.getBytes("file_data")));
			return r;
		};
	}

	public void loadUrlFromArray(byte[] arr, String mimeType) {
		String base64 = Base64enc.encodeToString(arr);
		this.url = String.format(
			"data:%s;base64,%s", 
			mimeType,
			base64
		);
	}
	public byte[] getArrayFromUrl() {
		int startDecPos = this.url.indexOf(",", 0);
		String encString = (startDecPos > 0) ? this.url.substring(startDecPos + 1) : this.url;
		byte[] outVal = Base64dec.decode(encString);
		return outVal;
	}
}
