package it.almaviva.foliage.legacy.bean;

import lombok.Getter;
import lombok.Setter;

public class RicercaUtenti {

	@Getter
	@Setter
	private String ambitoTerritoriale;
	
	@Getter
	@Setter
	private Integer codiceEnteTerritoriale;
	
	@Getter
	@Setter
	private Integer idProfilo;
		
	@Getter
	@Setter
	private String codiceFiscale;
	
	@Getter
	@Setter
	private String username;

}
