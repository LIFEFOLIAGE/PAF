package it.almaviva.foliage.bean;

public class ElementoValutazioneIstanza {
	public String categoria;
	public String noteIstruttore;
	public String tipoDocumento;
	public Integer idRichiesta;
	public String note;
	public Base64FormioFile[] allegato;
	public static ElementoValutazioneIstanza creaRichiesto(
		Integer idRichiesta,
		String categoria,
		String tipoDocumento,
		String noteIstruttore
	) {
		ElementoValutazioneIstanza outVal = new ElementoValutazioneIstanza();
		outVal.idRichiesta = idRichiesta;
		outVal.categoria = categoria;
		outVal.tipoDocumento = tipoDocumento;
		outVal.noteIstruttore = noteIstruttore;
		return outVal;
	}
	public static ElementoValutazioneIstanza creaConsegnato(
		Integer idRichiesta,
		String categoria,
		String tipoDocumento,
		String noteIstruttore,
		String note,
		Base64FormioFile[] allegato
	) {
		ElementoValutazioneIstanza outVal = creaRichiesto(idRichiesta, categoria, tipoDocumento, noteIstruttore);
		outVal.note = note;
		outVal.allegato = allegato;
		return outVal;
	}
}
