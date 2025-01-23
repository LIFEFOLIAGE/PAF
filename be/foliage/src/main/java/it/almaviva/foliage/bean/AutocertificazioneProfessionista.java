package it.almaviva.foliage.bean;

import lombok.Getter;
import lombok.Setter;

public class AutocertificazioneProfessionista {
	public String categoria;
	public String sottocategoria;
	public String collegio;
	public String numeroIscrizione;
	public Integer provinciaIscrizione;
	public String postaCertificata;

	public AutocertificazioneProfessionista() {}
	public AutocertificazioneProfessionista(
		String categoria,
		String sottocategoria,
		String collegio,
		String numeroIscrizione,
		Integer provinciaIscrizione,
		String postaCertificata
	) {
		this.categoria = categoria;
		this.sottocategoria = sottocategoria;
		this.collegio = collegio;
		this.numeroIscrizione = numeroIscrizione;
		this.provinciaIscrizione = provinciaIscrizione;
		this.postaCertificata = postaCertificata;
	}
}
