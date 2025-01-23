package it.almaviva.foliage.bean;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

public class DatiTitolare extends DatiAnagrafici {

	// @Getter
	// @Setter
	// private String codiceFiscale;

	// @Getter
	// @Setter
	// private String cognome;

	// @Getter
	// @Setter
	// private String nome;

	// @Getter
	// @Setter
	// private LocalDate dataDiNascita;

	// @Getter
	// @Setter
	// private String luogoDiNascita;

	// @Getter
	// @Setter
	// private Integer provincia;
	
	// @Getter
	// @Setter
	// private Integer comune;
	
	// @Getter
	// @Setter
	// private String cap;

	// @Getter
	// @Setter
	// private String indirizzo;

	// @Getter
	// @Setter
	// private Integer numeroCivico;

	// @Getter
	// @Setter
	// private String telefono;

	// @Getter
	// @Setter
	// private String email;

	// @Getter
	// @Setter
	// private String postaCertificata;

	@Getter
	@Setter
	protected Base64FormioFile[] fileDelegaProfesssionista;

	// @Getter
	// @Setter
	// private Base64FormioFile[] file2;
	public DatiTitolare(){
	}
	public DatiTitolare(	
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
		Base64FormioFile[] fileDelegaProfesssionista
	){
		super(
			codiceFiscale, cognome, nome, dataDiNascita, luogoDiNascita, genere, provincia, comune, cap, indirizzo, numeroCivico
			//, email, telefono, postaCertificata
		);
		this.fileDelegaProfesssionista = fileDelegaProfesssionista;
	}

	// public static DatiTitolare datiFromToken(AccessToken token) {
	// 	DatiTitolare outVal = new DatiTitolare();
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
