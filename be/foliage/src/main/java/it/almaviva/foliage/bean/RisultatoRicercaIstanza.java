package it.almaviva.foliage.bean;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

import it.almaviva.foliage.enums.TipoAuthScope;
import it.almaviva.foliage.enums.TipoAuthority;
import lombok.Getter;
import lombok.Setter;

public class RisultatoRicercaIstanza {
/*
i.id_ista, i.codi_ista, i.nome_ista, i.note,
	tit.id_titolare, tit.codice_fiscale, tit.cognome, tit.cognome,
	t.id_cist, t.id_tipo_istanza, t.cod_tipo_istanza_specifico, t.nome_istanza_specifico,
	e.id_ente, e.tipo_ente, e.nome_ente, 
	s.id_stato, s.cod_stato, s.desc_stato,
*/
	public RisultatoRicercaIstanza() {
	}

	public Integer idIstanza;
	public String codIstanza;
	public String nomeIstanza;
	public String codFiscaleTitolare;
	public String cognomeTitolare;
	public String nomeTitolare;
	public String codTipoIstanza;
	public String codTipoIstanzaSpecifico;
	public String tipoEnte;
	public String nomeEnte;
	public String descStato;
	public LocalDate dataIstanza;
	public String codFiscaleGestore;
	public Integer idUtenteGestore;
	public String authority;
	public String authScope;


	public static final RowMapper<RisultatoRicercaIstanza> RowMapper(String authority, String authScope) {
		int suffix = TipoAuthority.valueOf(authority).toInt()*10 + TipoAuthScope.valueOf(authScope).toInt();
		return  (ResultSet rs, int rn) -> {
			RisultatoRicercaIstanza outVal = new RisultatoRicercaIstanza();
			outVal.idIstanza = rs.getInt("id_ista");
			outVal.idIstanza *= 100;
			outVal.idIstanza += suffix;
			outVal.authority = authority;
			outVal.authScope = authScope;
			if ( rs.wasNull() ) {
				outVal.idIstanza = null;
			}
			outVal.codIstanza = rs.getString("codi_ista");
			outVal.nomeIstanza = rs.getString("nome_ista");
			outVal.codFiscaleTitolare = rs.getString("codice_fiscale");
			outVal.cognomeTitolare = rs.getString("cognome");
			outVal.nomeTitolare = rs.getString("nome");
			outVal.codTipoIstanza = rs.getString("desc_cist");
			outVal.codTipoIstanzaSpecifico = rs.getString("cod_tipo_istanza_specifico");
			outVal.tipoEnte = rs.getString("tipo_ente");
			outVal.nomeEnte = rs.getString("nome_ente");
			outVal.descStato = rs.getString("desc_stato");
			outVal.idUtenteGestore = rs.getInt("id_ute_gestore");
			if (rs.wasNull()) {
				outVal.idUtenteGestore = null;
			}
			Date date = rs.getDate("data_istanza");
			if (date != null) {
				outVal.dataIstanza = date.toLocalDate();
			}
			outVal.codFiscaleGestore = rs.getString("cod_fisc_gestore");
			
			// outVal.nomeTipoIstanzaSpecifico = rs.getString("nome_istanza_specifico");
			// outVal.idIstanza = rs.getString("id_ista");
			// outVal.idIstanza = rs.getString("id_ista");
	
			return outVal;
		};
	}

/*
	uc.id_uten as id_ute_gestore, uc.user_name as username_gestore, uc.codi_fisc as cod_fisc_gestore, uc.cognome as cognome_gestore, uc.nome as nome_gestore,
	ai.data_assegnazione,
	ui.id_uten as id_ute_istruttore, ui.user_name as username_istruttore, ui.codi_fisc as cod_fisc_istruttore, ui.cognome as cognome_istruttore, ui.nome as nome_istruttore,
	ua.id_uten as id_ute_dirigente, ua.user_name as username_dirigente, ua.codi_fisc as cod_fisc_dirigente, ua.cognome as cognome_dirigente, ua.nome as nome_dirigente,
	v.data_valutazione, v.note_valutazione,
	di.data_inizio_lavori, di.data_comunicazione_inizio_lavori,
	df.data_fine_lavori, df.data_comunicazione_fine_lavori
*/


	
}
