package it.almaviva.foliage.bean;

import it.almaviva.foliage.authentication.AccessToken;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

public class DatiAnagrafici {
	
	@Getter
	@Setter
	protected String codiceFiscale;

	@Getter
	@Setter
	protected String cognome;

	@Getter
	@Setter
	protected String nome;

	@Getter
	@Setter
	protected LocalDate dataDiNascita;

	@Getter
	@Setter
	protected String luogoDiNascita;
	
	@Getter
	@Setter
	protected String genere;

	@Getter
	@Setter
	protected Integer provincia;
	
	@Getter
	@Setter
	protected Integer comune;
	
	@Getter
	@Setter
	protected String cap;

	@Getter
	@Setter
	protected String indirizzo;

	@Getter
	@Setter
	protected String numeroCivico;

	// @Getter
	// @Setter
	// protected String telefono;

	// @Getter
	// @Setter
	// protected String email;

	// @Getter
	// @Setter
	// protected String postaCertificata;
	public DatiAnagrafici(){
	}
	public DatiAnagrafici(
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
		String numeroCivico
		
		// ,

		// String telefono,
		// String email,
		// String postaCertificata
	){
		this.codiceFiscale = codiceFiscale;
		this.cognome = cognome;
		this.nome = nome;
		this.dataDiNascita = dataDiNascita;
		this.luogoDiNascita = luogoDiNascita;
		this.genere = genere;

		this.provincia = provincia;
		this.comune = comune;
		this.cap = cap;
		this.indirizzo = indirizzo;
		this.numeroCivico = numeroCivico;

		// this.telefono = telefono;
		// this.email = email;
		// this.postaCertificata = postaCertificata;
	}

	// public static DatiAnagrafici datiFromToken(AccessToken token) {
	// 	DatiAnagrafici outVal = new DatiAnagrafici();
	// 	outVal.codiceFiscale = token.getCodiceFiscale();
	// 	outVal.cognome = token.getSurname();
	// 	outVal.nome = token.getName();
	// 	outVal.dataDiNascita = token.getBirthDate();
	// 	outVal.luogoDiNascita = token.getBirthPlace();
	// 	outVal.email = token.getEmail();
	// 	outVal.postaCertificata = token.getPec();
	// 	return outVal;
	// }
}
