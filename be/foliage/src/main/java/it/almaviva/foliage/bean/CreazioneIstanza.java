package it.almaviva.foliage.bean;

import lombok.Getter;
import lombok.Setter;

public class CreazioneIstanza {
	@Getter
	@Setter
	private String tipoInsta;
	
	@Getter
	@Setter
	private Integer idEnte;
	
	// @Getter
	// @Setter
	// private Integer sottotipoInsta;
	
	// @Getter
	// @Setter
	// private Integer tipoProprieta;
	
	// @Getter
	// @Setter
	// private Integer tipoNaturaProprieta;
	
	@Getter
	@Setter
	private String nomeIsta;

	@Getter
	@Setter
	private String noteIsta;

	@Getter
	@Setter
	private DatiTitolare datiTitolare;
}
