package it.almaviva.foliage.legacy.bean;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Utente {
	
	
	@Getter
	@Setter
	private Long idUten;
	
	@Getter
	@Setter
	private String nome;
	
	@Getter
	@Setter
	private String cognome;
	
	@Getter
	@Setter	
	private String cf;
	
	@Getter
	@Setter	
	private String userName;
	
	@Getter
	@Setter	
	private Date dataNascita;
	
	@Getter
	@Setter	
	private String luogoNascita;
	
	@Getter
	@Setter	
	private String sesso;
	
	@Getter
	@Setter	
	private String dataInizVali;
	
	@Getter
	@Setter	
	private String dataFineVali;
	
	@Getter
	@Setter	
	private String codiRegi;
	
	@Getter
	@Setter	
	private String descRegi;
	
	@Getter
	@Setter	
	private String stato;
	
	@Getter
	@Setter	
	private String dataIns;
	
	@Getter
	@Setter	
	private String dataUpd;
	
	@Getter
	@Setter	
	private List<Profilo> ruoli;
	
	@Getter
	@Setter
	private String token;
	
	@Getter
	@Setter	
	private boolean flagAccettazione;
	
	@Getter
	@Setter	
	private String cap;
	
	// @Getter
	// @Setter	
	// private String email;
	
	// @Getter
	// @Setter	
	// private String telefono;
	
	@Getter
	@Setter	
	private String indirizzo;
	
	@Getter
	@Setter	
	private String citta;
	
	@Getter
	@Setter	
	private int flagAttivo;

	public static RowMapper<Utente> RowMapper = (ResultSet rs, int rowNum) -> {		
		Utente utente=new Utente();
		
		utente.setIdUten(rs.getLong("id_uten"));
		utente.setNome(rs.getString("nome"));
		utente.setCognome(rs.getString("cognome"));
		utente.setUserName(rs.getString("user_name"));
		utente.setCf(rs.getString("codi_fisc"));
		utente.setFlagAccettazione(rs.getBoolean("flag_accettazione"));
	
		return utente;
	};
}
