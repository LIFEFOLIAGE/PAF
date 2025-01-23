package it.almaviva.foliage.bean;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

public class DatiUtente extends DatiAnagrafici {
	
	@Getter
	@Setter
	protected Integer idUten;
	
	@Getter
	@Setter
	protected String userName;

	@Getter
	@Setter
	protected Boolean flagAccettazione;
	
	@Getter
	@Setter
	protected Integer rouloPredefinito;

	@Getter
	@Setter
	protected Boolean isProfessionistaForestale;

	@Getter
	@Setter
	protected AutocertificazioneProfessionista autocertificazioneProf;

	public DatiUtente(){
	}
	public DatiUtente(
		String codiceFiscale,
		String cognome,
		String nome,
		LocalDate dataDiNascita,
		String luogoDiNascita,
		String genere,

		Integer provincia,
		Integer comune,
		String cap,
		String indirizzo,
		String numeroCivico,

		// String telefono,
		// String email,
		// String postaCertificata,
		
		Integer idUten,
		String userName,
		Boolean flagAccettazione,
		Integer rouloPredefinito,
		Boolean isProfessionistaForestale,
		AutocertificazioneProfessionista autocertificazioneProf
	){
		super(
			codiceFiscale, cognome, nome, dataDiNascita, luogoDiNascita, genere, provincia, comune, cap, indirizzo, numeroCivico
			//, email, telefono, postaCertificata
		);
		this.idUten = idUten;
		this.userName = userName;
		this.flagAccettazione = flagAccettazione;
		this.rouloPredefinito = rouloPredefinito;
		this.isProfessionistaForestale = isProfessionistaForestale;
		this.autocertificazioneProf = autocertificazioneProf;
	}
}
