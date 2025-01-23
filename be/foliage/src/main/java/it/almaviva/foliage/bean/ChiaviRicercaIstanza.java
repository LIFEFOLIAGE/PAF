package it.almaviva.foliage.bean;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

public class ChiaviRicercaIstanza {
	@Getter
	@Setter
	private Integer tipoIstanza;
	
	@Getter
	@Setter
	private Integer statoIstanza;
	
	@Getter
	@Setter
	private Integer statoAvanzamento;
	
	@Getter
	@Setter
	private String testo;
	
	@Getter
	@Setter
	private Integer idEnte;
	
	@Getter
	@Setter
	private String codFiscaleTitolare;

	@Getter
	@Setter
	private String codFiscaleIstruttore;

	@Getter
	@Setter
	private String usernameIstruttore;

	@Getter
	@Setter
	private LocalDate validitaDa;

	@Getter
	@Setter
	private LocalDate validitaA;

	@Getter
	@Setter
	private LocalDate approvazioneDa;

	@Getter
	@Setter
	private LocalDate approvazioneA;
}
