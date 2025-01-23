package it.almaviva.foliage.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

public class InfoIstanza extends RisultatoRicercaIstanza {
	
	private String noteIstanza;
	private Integer idTitolare;
	private Integer idTipoIstanza;
	private Integer idTipoIstanzaSpecifico;
	public String nomeTipoIstanzaSpecifico;
	private Integer idEnte;
	private Integer stato;
	private String codStato;

	private Integer usernameGestore;
	private Integer cognomeGestore;
	private Integer nomeGestore;

	private Integer idUtenteIstruttore;
	private Integer usernameIstruttore;
	private Integer codFiscaleIstruttore;
	private Integer cognomeIstruttore;
	private Integer nomeIstruttore;

	private LocalDateTime dataAssegnazione;

	private Integer idUtenteAssegnazione;
	private Integer usernameAssegnazione;
	private Integer codFiscaleAssegnazione;
	private Integer cognomeAssegnazione;
	private Integer nomeAssegnazione;

	private LocalDateTime dataValutazione;
	private String noteValutazione;

	private LocalDate dataInizioLavori;
	private LocalDate dataFineLavori;

	private LocalDateTime dataComunicazioneInizioLavori;
	private LocalDateTime dataComunicazioneFineLavori;
}
