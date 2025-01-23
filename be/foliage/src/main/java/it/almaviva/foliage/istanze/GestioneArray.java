package it.almaviva.foliage.istanze;

public class GestioneArray {
	//public CorrispondenzaJson corrispondenzaCampo;
	public String nomeArray;
	public SchedaIstanzaArray schedaGestione;

	public GestioneArray(
		String nomeArray,
		SchedaIstanzaArray schedaGestione
	) {
		this.nomeArray = nomeArray;
		this.schedaGestione = schedaGestione;
	}
}
