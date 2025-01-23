package it.almaviva.foliage.bean;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

public class FormioFile {
	public FormioFile() {
	}
	
	public FormioFile(Base64FormioFile fileBase64) throws UnsupportedEncodingException {
		hash = fileBase64.getHash();
		name = fileBase64.getName();
		originalName = fileBase64.getOriginalName();
		size = fileBase64.getSize();
		storage = fileBase64.getStorage();
		type = fileBase64.getType();
		String base64 = fileBase64.getUrl();

		final int dataStartIndex = base64.indexOf(",") + 1;
		final String data = base64.substring(dataStartIndex);
		byte[] decoded = java.util.Base64.getDecoder().decode(data);
		content = decoded;
	}

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
	private byte[] content;
	

	public static org.springframework.jdbc.core.RowMapper<FormioFile> RowMapper() {
		return (rs, rowNum)-> {
			FormioFile r = new FormioFile();
			r.setHash(rs.getString("hash_file"));
			r.setName(rs.getString("file_name"));
			r.setOriginalName(rs.getString("original_file_name"));
			r.setSize(rs.getInt("file_size"));
			r.setStorage(rs.getString("storage"));
			r.setType(rs.getString("file_type"));
			r.setContent(rs.getBytes("file_data"));
			return r;
		};
	}
}
