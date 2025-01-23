package it.almaviva.foliage.legacy.bean;

import lombok.Getter;
import lombok.Setter;

public class Profilo {
	@Getter
	@Setter
	private int idProfilo;

	@Getter
	@Setter
	private String descrizione;

	@Getter
	@Setter
	private int flagRegionale;
	
	@Getter
	@Setter
	private int idComu;
	
	@Getter
	@Setter
	private String descComu;
	
	@Getter
	@Setter
	private int idProv;
	
	@Getter
	@Setter
	private String descProv;
	
	@Getter
	@Setter
	private int idUtenteProfilo;
	
	@Getter
	@Setter
	private String flagDefault;
}
