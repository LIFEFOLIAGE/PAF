package it.almaviva.foliage.istanze;

public class SottoCorrispondenzaJson {
	public String nomeProprieta;
	CorrispondenzaJson sottoCorrispondenza;
	public SottoCorrispondenzaJson(
		String nomeProprieta,
		CorrispondenzaJson sottoCorrispondenza
	) {
		this.nomeProprieta = nomeProprieta;
		this.sottoCorrispondenza = sottoCorrispondenza;
	}
}
