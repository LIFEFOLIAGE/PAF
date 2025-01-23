package it.almaviva.foliage.bean;

import lombok.Getter;
import lombok.Setter;

public class RichiestaProfilo {
	@Getter
	@Setter
	private Integer idEnte;

	// @Getter
	// @Setter
	// private String tipoEnte;
	
	// @Getter
	// @Setter
	// private String nomeEnte;


	@Getter
	@Setter
	private Integer ruoloRichiesto;

	@Getter
	@Setter
	private String noteRichiesta;

	@Getter
	@Setter
	private DatiRichiestaResponsabile datiResponsabile;
}
