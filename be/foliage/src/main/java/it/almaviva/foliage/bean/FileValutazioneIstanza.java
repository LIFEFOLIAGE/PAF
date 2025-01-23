package it.almaviva.foliage.bean;

import java.util.LinkedList;

public class FileValutazioneIstanza {
	public LinkedList<ElementoValutazioneIstanza> richiesti;
	public LinkedList<ElementoValutazioneIstanza> consegnati;

	public LinkedList<Integer> consegnatiEliminati;
	public LinkedList<Integer> richiestiEliminati;

	

	
	public FileValutazioneIstanza(
		LinkedList<ElementoValutazioneIstanza> richiesti,
		LinkedList<ElementoValutazioneIstanza> consegnati
	) {
		this.richiesti = richiesti;
		this.consegnati = consegnati;
	}
}
