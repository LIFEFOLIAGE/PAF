package it.almaviva.foliage.authentication;

import java.util.LinkedList;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;

public class FoliageGrantedAuthority implements GrantedAuthority {

	private static final long serialVersionUID = 610L;
  
	// public static String GetTipoProfilo(int idProfilo) {
	// 	String tipo = null;
		
	// 	switch (idProfilo) {
	// 		case 1: {
	// 			tipo = "PROPRIETARIO";
	// 		}; break;
	// 		case 2: {
	// 			tipo = "PROF_SENIOR";
	// 		}; break;
	// 		case 3: {
	// 			tipo = "PROF_JUNIOR";
	// 		}; break;
	// 		case 4: {
	// 			tipo = "PROF_SENIOR"; //"PROF_COLLEGGIO";
	// 		}; break;
	// 		case 6: {
	// 			tipo = "ISTRUTTORE";
	// 		}; break;
	// 		case 7: {
	// 			tipo = "DIRIGENTE";
	// 		}; break;
	// 		case 8: {
	// 			tipo = "PROF_SENIOR"; //"SPORTELLO";
	// 		}; break;
	// 		case 9: {
	// 			tipo = "CARABINIERE";
	// 		}; break;
	// 		case 10: {
	// 			tipo = "CARABINIERE"; // "GUARDIA_PARCO";
	// 		}; break;
	// 		case 11: {
	// 			tipo = "RESPONSABILE";
	// 		}; break;
	// 		case 12: {
	// 			tipo = "AMMINISTRATORE";
	// 		}; break;
	// 		default: {
	// 			tipo = null;
	// 		}
	// 	}
	// 	return tipo;
	// }
	
	public FoliageGrantedAuthority(
		int id,
		String descrizione,
		String tipoAuth,
		String ambito,
		Boolean isSenior,
		LinkedList<Integer> idEntiAssociati
	) {
		this.role = tipoAuth;
		this.idProfilo = id;
		this.descrizione = descrizione;
		this.tipo = tipoAuth;
		this.ambito = ambito;
		this.isSenior = isSenior;
		this.idEntiAssociati = idEntiAssociati;
	}
	
	public java.lang.String getAuthority() {
		return this.role;
	}

	private final String role;

	@Getter
	private final int idProfilo;
	
	@Getter
	private final String descrizione;
	
	@Getter
	private final String tipo;
	
	@Getter
	private final Boolean isSenior;
	
	@Getter
	private final String ambito;

	@Getter
	private final LinkedList<Integer> idEntiAssociati;

}
