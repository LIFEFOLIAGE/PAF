package it.almaviva.foliage.bean;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

public class DatiRichiestaResponsabile {
	@Getter
	@Setter
	private Integer tipoDiNomina;

	@Getter
	@Setter
	private Base64FormioFile[] attoDiNomina;
	
	@Getter
	@Setter
	private Base64FormioFile[] documentoDiIdentita;

	@Getter
	@Setter
	private Date dataProtocollo;

	@Getter
	@Setter
	private String numeroDiProtocollo;

}
