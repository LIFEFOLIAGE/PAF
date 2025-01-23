package it.almaviva.foliage.bean;

import java.util.HashMap;

public class AbilitazioniIstanza {
	public boolean access;
	public boolean consultazione;
	public boolean compilazione;
	public boolean allega_documenti;
	//public boolean allega_tavole;
	public boolean invio;
	public boolean assegna_istruttore;
	public boolean revoca_istruttore;
	public boolean valutazione;
	public boolean consulta_valutazione;
	//public boolean cambia_gestore;
	public boolean passaggio_gestore;
	public boolean rimuovi_gestore;
	public boolean richiesta_proroga;
	public boolean inizio_lavori;
	public boolean fine_lavori;

	//public boolean upload_modulo_firmato;

	public AbilitazioniIstanza() {}
	public AbilitazioniIstanza(HashMap<String, Boolean> confMap) {
		access = (confMap.containsKey("access") && confMap.get("access") == true);
		allega_documenti = (confMap.containsKey("allega_documenti") && confMap.get("allega_documenti") == true);
		//allega_tavole = (confMap.containsKey("allega_tavole") && confMap.get("allega_tavole") == true);
		consultazione = (confMap.containsKey("consultazione") && confMap.get("consultazione") == true);
		compilazione = (confMap.containsKey("compilazione") && confMap.get("compilazione") == true);
		invio = (confMap.containsKey("invio") && confMap.get("invio") == true);
		consulta_valutazione = (confMap.containsKey("consulta_valutazione") && confMap.get("consulta_valutazione") == true);
		revoca_istruttore = (confMap.containsKey("revoca_istruttore") && confMap.get("revoca_istruttore") == true);
		assegna_istruttore = (confMap.containsKey("assegna_istruttore") && confMap.get("assegna_istruttore") == true);
		valutazione = (confMap.containsKey("valutazione") && confMap.get("valutazione") == true);
		//cambia_gestore = (confMap.containsKey("passaggio_gestore") && confMap.get("passaggio_gestore") == true);
		passaggio_gestore = (confMap.containsKey("passaggio_gestore") && confMap.get("passaggio_gestore") == true);
		rimuovi_gestore = (confMap.containsKey("rimuovi_gestore") && confMap.get("rimuovi_gestore") == true);
		richiesta_proroga = (confMap.containsKey("richiesta_proroga") && confMap.get("richiesta_proroga") == true);
		inizio_lavori = (confMap.containsKey("inizio_lavori") && confMap.get("inizio_lavori") == true);
		fine_lavori = (confMap.containsKey("fine_lavori") && confMap.get("fine_lavori") == true);
		//upload_modulo_firmato = (confMap.containsKey("upload_modulo_firmato") && confMap.get("upload_modulo_firmato") == true);;
	}
}
