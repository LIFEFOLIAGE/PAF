// /*
//  * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
//  * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
//  */
// package it.almaviva.foliage.services;

// import java.sql.Connection;
// import java.sql.Date;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.util.HashMap;
// import java.util.LinkedList;
// import java.util.Map;
// import java.util.function.BiFunction;
// import java.util.function.Function;
// import java.util.stream.Collectors;

// import org.javatuples.Pair;
// import org.javatuples.Triplet;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
// import org.springframework.jdbc.core.namedparam.SqlParameterSource;
// import org.springframework.jdbc.datasource.DriverManagerDataSource;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.TransactionStatus;
// import org.springframework.transaction.support.DefaultTransactionDefinition;

// import com.google.gson.JsonElement;
// import com.google.gson.JsonObject;

// import it.almaviva.foliage.FoliageException;
// import it.almaviva.foliage.bean.Base64FormioFile;
// import it.almaviva.foliage.bean.DatiRichiestaResponsabile;
// import it.almaviva.foliage.bean.RichiestaProfilo;
// import it.almaviva.foliage.bean.ValutazioneRichiestaProfilo;
// import it.almaviva.foliage.legacy.bean.RicercaUtenti;

// /**
//  *
//  * @author A.Rossi
//  */
// @Component
// public class UtenzaDal extends Dal {
	
	

// 	// private Connection connection;
	
// 	// public UtenzaDal() throws SQLException {
// 	//     log.debug("UtenzaDal");
// 	//     DriverManagerDataSource dataSource = new DriverManagerDataSource();
// 	//     //dataSource.setDriverClassName("org.postgresql.Driver");
// 	//     dataSource.setUrl("jdbc:postgresql://unioneeuropea-foliage-svil.cs5b2t1vzg63.eu-west-1.rds.amazonaws.com/foliage");
// 	//     //dataSource.setUrl("jdbc:postgresql://127.0.0.1/foliage");
// 	//     dataSource.setUsername("foliage");
// 	//     dataSource.setPassword("foliage.01");
// 	//     this.connection = dataSource.getConnection();
// 	// }
	
// 	@Autowired
// 	public UtenzaDal(JdbcTemplate jdbcTemplate) throws SQLException {
// 		super(jdbcTemplate,"UtenzaDal");
// 	}
	
// 	public Object getInfoUtente(String username) throws SQLException, Exception {
// 		Object outval = null;
// 		Connection conn = this.connection;
// 		conn.beginRequest();
// 		try {
			
// 			conn.setAutoCommit(false);

			
// 			PreparedStatement statement = conn.prepareStatement("""
// select u.id_uten, nome, cognome, user_name, codi_fisc, flag_accettazione, data_nascita,
// 	luogo_nascita, sesso, indirizzo, citta, cap, telefono, email, 
// 	pu.id_profilo as id_profilo_default, IS_SENIOR
// from foliage2.flguten_tab u
// 	left join foliage2.FLGPROFILI_UTENTE_TAB pu on (pu.id_utente = u.id_uten and pu.flag_default = true)
// 	left join foliage2.FLGUTE_PROFESSIONISTI_TAB up on (up.id_utente = u.id_uten)
// where user_name = ?""");
			
// 			statement.setString(1, username);
// 			final ResultSet result = statement.executeQuery();
			
// 			if (result.next()) {
// 				final long idUtente = result.getLong(1);
// 				Boolean senior = result.getBoolean(16);
// 				Object autocertProf = null;
// 				Boolean isProfessionista = null;
// 				if (result.wasNull()) {
// 					senior = null;
// 					isProfessionista = false;
// 				}
// 				else {
// 					isProfessionista = true;
// 					String sqlAutocert = """
// select CATEGORIA, SOTTOCATEGORIA, COLLEGGIO, NUMERO_ISCRIZIONE, ID_PROVINCIA_ISCRIZIONE
// from foliage2.FLGAUTOCERT_PROF_TAB
// where ID_UTENTE = ?
// 	and FLAG_VALIDO
// 					""";
// 					statement = conn.prepareStatement(sqlAutocert);
// 					statement.setLong(1, idUtente);
// 					final ResultSet result2 = statement.executeQuery();
// 					if (result2.next()) {
// 						autocertProf = new Object() {
// 							public final String categoria = result2.getString(1);
// 							public final String sottocategoria = result2.getString(2);
// 							public final String collegio = result2.getString(3);
// 							public final String numeroIscrizione = result2.getString(4);
// 							public final String provinciaIscrizione = result2.getString(5);
// 						};
// 					}
// 					else {
// 						throw new FoliageException("Non è stata trovata un'autocertificazione valida");
// 					}
// 				}
// 				final Object autoCertFin = autocertProf;
// 				final Boolean seniorFin = senior;
// 				final Boolean isProfessionistaFin = isProfessionista;

// 				outval = new Object() {
// 					public long idUten = idUtente;
// 					public String nome = result.getString(2);
// 					public String cognome = result.getString(3);
// 					public String userName = result.getString(4);
// 					public String cf = result.getString(5);
// 					public boolean flagAccettazione = result.getBoolean(6);
// 					public Date dataNascita = result.getDate(7);
// 					public String luogoNascita = result.getString(8);
// 					public String sesso = result.getString(9);
// 					public String indirizzo = result.getString(10);
// 					public String citta = result.getString(11);
// 					public String cap = result.getString(12);
// 					public String telefono = result.getString(13);
// 					public String email = result.getString(14);
// 					public int rouloPredefinito = result.getInt(15);
// 					public Boolean isSenior = ((seniorFin != null) && seniorFin);
// 					public boolean isProfessionistaForestale = isProfessionistaFin;
// 					public Object autocertificazioneProf = autoCertFin;
// 				};
// 			}
// 		}
// 		finally {
// 			conn.endRequest();
// 		}
// 		return outval;
// 		//return result;
// 	}

// 	public Object getRichiestaUtente(Integer idUtente, Integer idRichiesta) throws SQLException, Exception {
// 		Map<String, Object> mapParam = new HashMap<String, Object>();
// 		mapParam.put("idUtente", idUtente);
// 		mapParam.put("idRichiesta", idRichiesta);
// 		SqlParameterSource parameters = new MapSqlParameterSource(mapParam);

// 		String sql = """
// select ID_PROFILO_RICHIESTO, E.TIPO_ENTE, E.NOME_ENTE, E.ID_ENTE, ESITO_APPROVAZIONE, 
// 	NOTE_RICHIESTA, NOTE_APPROVAZIONE,
// 	TIPO_NOMINA, NUMERO_PROTOCOLLO, DATA_PROTOCOLLO,
// 	RP.ESITO_APPROVAZIONE, RP.NOTE_RICHIESTA, RP.NOTE_APPROVAZIONE,
// 	RR.ID_RICHIESTA as ID_RICHIESTA_RESP,
// 	ID_FILE_ATTO_NOMINA, ID_FILE_DOC_IDENTITA
// from FOLIAGE2.FLGRICHIESTE_PROFILI_TAB as RP
// 	join FOLIAGE2.FLGENTE_ROOT_TAB as E on (E.ID_ENTE = RP.ID_ENTE)
// 	left join FOLIAGE2.FLGRICHIESTE_RESPONSABILE_TAB as RR on (RR.ID_RICHIESTA = RP.ID_RICHIESTA)
// where RP.ID_UTENTE  = :idUtente
// 	and RP.ID_RICHIESTA = :idRichiesta
// 	and RP.DATA_ANNULLAMENTO is null
// 				""";

		

// 		Object outVal = template.queryForObject(
// 				sql,
// 				parameters, 
// 				(rs, rowNum)-> {
// 					Integer idRichiestaResp = rs.getInt("id_richiesta_resp");
// 					if (rs.wasNull()) {
// 						idRichiestaResp = null;
// 					}
// 					Object richiestaResp = null;
// 					if (idRichiestaResp != null) {
// 						String sqlFile = """
// select FILE_NAME, ORIGINAL_FILE_NAME, FILE_SIZE, STORAGE,
// 	FILE_TYPE, HASH_FILE, FILE_DATA
// from FOLIAGE2.FLGBASE64_FORMIO_FILE_TAB
// where ID_FILE = :idFile
// 							""";
// 						Map<String, Object> mapFileParam = new HashMap<String, Object>();
						
// 						Base64FormioFile fileAttoNomina = null;
// 						Base64FormioFile fileDocIdentita = null;
// 						Integer idFileAttoNomina = rs.getInt("id_file_atto_nomina");
// 						if (rs.wasNull()) {
// 							idFileAttoNomina = null;
// 						}
// 						else {
// 							mapFileParam.put("idFile", idFileAttoNomina);
// 							SqlParameterSource filePars = new MapSqlParameterSource(mapFileParam);
// 							fileAttoNomina = template.queryForObject(sqlFile, filePars, Base64FormioFile.RowMapper());
// 						}
// 						Integer idFileDocIdentita = rs.getInt("id_file_doc_identita");
// 						if (rs.wasNull()) {
// 							idFileDocIdentita = null;
// 						}
// 						else {
// 							mapFileParam.put("idFile", idFileDocIdentita);
// 							SqlParameterSource filePars = new MapSqlParameterSource(mapFileParam);
// 							fileDocIdentita = template.queryForObject(sqlFile, filePars, Base64FormioFile.RowMapper());
// 						}
// 						final Base64FormioFile fileAttoNomina2 = fileAttoNomina;
// 						final Base64FormioFile fileDocIdentita2 = fileDocIdentita;
// 						richiestaResp = new Object() {
// 							public Integer tipoDiNomina = rs.getInt("tipo_nomina");
// 							public String numeroDiProtocollo = rs.getString("numero_protocollo");
// 							public Date dataProtocollo = rs.getDate("data_protocollo");
// 							public Base64FormioFile[] attoDiNomina = new Base64FormioFile[] {fileAttoNomina2};
// 							public Base64FormioFile[] documentoDiIdentita = new Base64FormioFile[] {fileDocIdentita2};
// 						};
// 					}
// 					Boolean esitoApp = rs.getBoolean("esito_approvazione");
// 					if (rs.wasNull()) {
// 						esitoApp = null;
// 					}
// 					final Boolean esitoApp2 = esitoApp;
// 					final Object richiestaResp2 = richiestaResp;
// 					Object o = new Object(){
// 						public Object datiResponsabile = richiestaResp2;
// 						public Integer ruoloRichiesto = rs.getInt("id_profilo_richiesto");
// 						public String tipoEnte = rs.getString("tipo_ente");
// 						public String nomeEnte = rs.getString("nome_ente");
// 						public Integer idEnte = rs.getInt("id_ente");
// 						public String noteRichiesta = rs.getString("note_richiesta");
// 						public Boolean esitoApprovazione = esitoApp2;
// 						public String noteApprovazione = rs.getString("note_approvazione");
// 					};

// 					return o;
// 				}
// 			);

// 		return outVal;	
// 	}


	
// 	public Object getRichiesta(Integer idRichiesta) throws SQLException, Exception {
// 		Map<String, Object> mapParam = new HashMap<String, Object>();
// 		mapParam.put("idRichiesta", idRichiesta);
// 		SqlParameterSource parameters = new MapSqlParameterSource(mapParam);

// 		String sql = """
// select ID_PROFILO_RICHIESTO, RP.ID_ENTE, E.TIPO_ENTE, E.NOME_ENTE, ESITO_APPROVAZIONE, 
// 	NOTE_RICHIESTA, NOTE_APPROVAZIONE,
// 	TIPO_NOMINA, NUMERO_PROTOCOLLO, DATA_PROTOCOLLO,
// 	RP.ESITO_APPROVAZIONE, RP.NOTE_RICHIESTA, RP.NOTE_APPROVAZIONE,
// 	RR.ID_RICHIESTA as ID_RICHIESTA_RESP,
// 	ID_FILE_ATTO_NOMINA, ID_FILE_DOC_IDENTITA, U.USER_NAME
// from FOLIAGE2.FLGRICHIESTE_PROFILI_TAB as RP
// 	join FOLIAGE2.FLGENTE_ROOT_TAB as E on (E.ID_ENTE = RP.ID_ENTE)
// 	left join FOLIAGE2.FLGRICHIESTE_RESPONSABILE_TAB as RR on (RR.ID_RICHIESTA = RP.ID_RICHIESTA)
// 	left join FOLIAGE2.FLGUTEN_TAB as U on (U.ID_UTEN = RP.ID_UTENTE)
// where RP.ID_RICHIESTA = :idRichiesta
// 	and RP.DATA_ANNULLAMENTO is null
// 				""";

		

// 		Object outVal = template.queryForObject(
// 				sql,
// 				parameters, 
// 				(rs, rowNum)-> {
// 					Integer idRichiestaResp = rs.getInt("id_richiesta_resp");
// 					if (rs.wasNull()) {
// 						idRichiestaResp = null;
// 					}
// 					Object richiestaResp = null;
// 					if (idRichiestaResp != null) {
// 						String sqlFile = """
// select FILE_NAME, ORIGINAL_FILE_NAME, FILE_SIZE, STORAGE,
// 	FILE_TYPE, HASH_FILE, FILE_DATA
// from FOLIAGE2.FLGBASE64_FORMIO_FILE_TAB
// where ID_FILE = :idFile
// 							""";
// 						Map<String, Object> mapFileParam = new HashMap<String, Object>();
						
// 						Base64FormioFile fileAttoNomina = null;
// 						Base64FormioFile fileDocIdentita = null;
// 						Integer idFileAttoNomina = rs.getInt("id_file_atto_nomina");
// 						if (rs.wasNull()) {
// 							idFileAttoNomina = null;
// 						}
// 						else {
// 							mapFileParam.put("idFile", idFileAttoNomina);
// 							SqlParameterSource filePars = new MapSqlParameterSource(mapFileParam);
// 							fileAttoNomina = template.queryForObject(sqlFile, filePars, Base64FormioFile.RowMapper());
// 						}
// 						Integer idFileDocIdentita = rs.getInt("id_file_doc_identita");
// 						if (rs.wasNull()) {
// 							idFileDocIdentita = null;
// 						}
// 						else {
// 							mapFileParam.put("idFile", idFileDocIdentita);
// 							SqlParameterSource filePars = new MapSqlParameterSource(mapFileParam);
// 							fileDocIdentita = template.queryForObject(sqlFile, filePars, Base64FormioFile.RowMapper());
// 						}
// 						final Base64FormioFile fileAttoNomina2 = fileAttoNomina;
// 						final Base64FormioFile fileDocIdentita2 = fileDocIdentita;
// 						richiestaResp = new Object() {
// 							public Integer tipoDiNomina = rs.getInt("tipo_nomina");
// 							public String numeroDiProtocollo = rs.getString("numero_protocollo");
// 							public Date dataProtocollo = rs.getDate("data_protocollo");
// 							public Base64FormioFile[] attoDiNomina = new Base64FormioFile[] {fileAttoNomina2};
// 							public Base64FormioFile[] documentoDiIdentita = new Base64FormioFile[] {fileDocIdentita2};
// 						};
// 					}
// 					Boolean esitoApp = rs.getBoolean("esito_approvazione");
// 					if (rs.wasNull()) {
// 						esitoApp = null;
// 					}
// 					final Boolean esitoApp2 = esitoApp;
// 					final Object richiestaResp2 = richiestaResp;
// 					Object o = new Object(){
// 						public String username = rs.getString("user_name");
// 						public Object datiResponsabile = richiestaResp2;
// 						public Integer ruoloRichiesto = rs.getInt("id_profilo_richiesto");
// 						public String tipoEnte = rs.getString("tipo_ente");
// 						public String nomeEnte = rs.getString("nome_ente");
// 						public Integer idEnte = rs.getInt("id_ente");
// 						public String noteRichiesta = rs.getString("note_richiesta");
// 						public Boolean esitoApprovazione = esitoApp2;
// 						public String noteApprovazione = rs.getString("note_approvazione");
// 					};

// 					return o;
// 				}
// 			);

// 		return outVal;	
// 	}

// 	public Object cancelRichiestaUtente(Integer idUtente, Integer idRichiesta) throws SQLException, Exception {
// 		Map<String, Object> mapParam = new HashMap<String, Object>();
// 		mapParam.put("idUtente", idUtente);
// 		mapParam.put("idRichiesta", idRichiesta);
// 		SqlParameterSource parameters = new MapSqlParameterSource(mapParam);

// 		String sql = """
// update FOLIAGE2.FLGRICHIESTE_PROFILI_TAB
// set DATA_ANNULLAMENTO = localtimestamp
// where ID_UTENTE  = :idUtente
// 	and ID_RICHIESTA = :idRichiesta
// 	and DATA_ANNULLAMENTO is null
// 				""";

// 		int res = template.update(sql, parameters);
// 		return "Ok";
// 	}

// 	public Object revocaAssociazioneRuoloEnte(Integer idUtenteExe, Integer idUtenteRevoca, Integer idProfilo, Integer idEnte, String note) throws SQLException, Exception {

// 		Map<String, Object> mapParam = new HashMap<String, Object>();
// 		mapParam.put("idUtenteRevoca", idUtenteRevoca);
// 		mapParam.put("idUtenteExe", idUtenteExe);
// 		mapParam.put("idProfilo", idProfilo);
// 		mapParam.put("idEnte", idEnte);
// 		mapParam.put("note", note);
// 		SqlParameterSource parameters = new MapSqlParameterSource(mapParam);

// 		String sqlInsRevoca = """
// INSERT INTO foliage2.flgrevoca_profili_tab (
// 		id_utente, id_profilo_revocato, id_ente,
// 		id_utente_revoca, data_revoca, note_revoca
// 	)
// VALUES(
// 		:idUtenteRevoca, :idProfilo, :idEnte,
// 		:idUtenteExe, localtimestamp, :note
// 	)
// 		""";
// 		int res = template.update(sqlInsRevoca, parameters);

// 		String sqlDelAss = """
// 			DELETE FROM foliage2.flgenti_profilo_tab
// 			WHERE id_utente = :idUtenteRevoca
// 				AND id_profilo = :idProfilo
// 				AND id_ente = :idEnte
// 		""";
// 		int res2 = template.update(sqlDelAss, parameters);

// 		String sqlDelRuol = """
// DELETE FROM foliage2.flgprofili_utente_tab
// WHERE id_utente = :idUtenteRevoca
// 	AND id_profilo = :idProfilo
// 	and not exists (
// 		select *
// 		FROM foliage2.flgenti_profilo_tab
// 		WHERE id_utente = :idUtenteRevoca
// 			AND id_profilo = :idProfilo
// 	)	
// 		""";
// 		int res3 = template.update(sqlDelRuol, parameters);

// 		return "Ok";
// 	}

// 	public Object revocaAssociazioneRuoloEnte(Integer idUtente, String username, Integer idProfilo, Integer idEnte, String note) throws SQLException, Exception {
// 		return revocaAssociazioneRuoloEnte(idUtente, getIdUtente(username), idProfilo, idEnte, note);
// 	}
	
// 	public Integer getIdUtente(String username) throws SQLException, Exception {
// 		Map<String, Object> mapParam = new HashMap<String, Object>();
// 		mapParam.put("username", username);
// 		SqlParameterSource pars = new MapSqlParameterSource(mapParam);

// 		String sql = """
// select ID_UTEN
// from FOLIAGE2.FLGUTEN_TAB
// where USER_NAME = :username
// 				""";
		
// 		return template.queryForObject(
// 			sql,
// 			pars,
// 			(rs, rn) -> {
// 				return rs.getInt(1);
// 			}
// 		);
// 	}
// 	public Object getRichiestaUtente(String username, Integer idRichiesta) throws SQLException, Exception {
// 		return getRichiestaUtente(getIdUtente(username), idRichiesta);
// 	}
	
// 	public Object getEntiPerRuoloUtente(Integer idUtente, Integer idProfilo) throws SQLException, Exception {

// 		Map<String, Object> mapParam = new HashMap<String, Object>();
// 		mapParam.put("idUtente", idUtente);
// 		mapParam.put("idProfilo", idProfilo);
// 		SqlParameterSource parameters = new MapSqlParameterSource(mapParam);

// 		String sql = """
// select E.ID_ENTE, E.TIPO_ENTE, E.NOME_ENTE
// from FOLIAGE2.FLGENTI_PROFILO_TAB as EP
// 	left join FOLIAGE2.FLGENTE_ROOT_TAB as E on (E.ID_ENTE = EP.ID_ENTE)
// where ID_UTENTE = :idUtente
// 	and ID_PROFILO = :idProfilo
// 				""";

		

// 		Object outVal = template.query(
// 				sql,
// 				parameters, 
// 				(rs, rowNum)-> {
// 					Object o = new Object(){
// 						public Integer idEnte = rs.getInt("id_ente");
// 						public String tipo = rs.getString("tipo_ente");
// 						public String nome = rs.getString("nome_ente");
// 					};

// 					return o;
// 				}
// 			);

// 		return outVal;	
// 	}

// 	public Object getEntiPerRuoloUtente(String username, Integer idRichiesta) throws SQLException, Exception {
// 		return this.getEntiPerRuoloUtente(getIdUtente(username), idRichiesta);
// 	}

// 	public ResultSet getRichiesteUtente(Integer idUtente) throws SQLException, Exception {
		
// 		ResultSet result = this.GetResult(
// 			(conn) -> {
// 				PreparedStatement statement = conn.prepareStatement("""
// select ID_RICHIESTA, RP.DATA_RICHIESTA, U.ID_UTEN, U.USER_NAME, U.CODI_FISC, P.ID_PROFILO, P.DESCRIZIONE as PROFILO,
// 	E.ID_ENTE, E.TIPO_ENTE, E.NOME_ENTE,
// 	RP.DATA_APPROVAZIONE, RP.ESITO_APPROVAZIONE, RP.ID_UTENTE_APPROVAZIONE, UA.USER_NAME as USER_APPROVAZIONE, UA.CODI_FISC as COD_FISC_APPROVAZIONE
// from FOLIAGE2.FLGRICHIESTE_PROFILI_TAB as RP
// 	left join FOLIAGE2.FLGUTEN_TAB as U on (RP.ID_UTENTE = U.ID_UTEN)
// 	left join FLGPROF_TAB as P on (P.ID_PROFILO = RP.ID_PROFILO_RICHIESTO)
// 	left join FLGENTE_ROOT_TAB as E on (E.ID_ENTE = RP.ID_ENTE)
// 	left join FLGUTEN_TAB as UA on (RP.ID_UTENTE_APPROVAZIONE = UA.ID_UTEN)
// where RP.ID_UTENTE = ?
// 	and RP.DATA_ANNULLAMENTO is null
// order by RP.DATA_RICHIESTA desc, ID_RICHIESTA desc
// 					"""
// 					);
// 				statement.setInt(1, idUtente);
// 				return statement.executeQuery();
// 			}
// 		);
// 		return result;
// 	}

// 	public ResultSet getRichiesteUtente(String username) throws SQLException, Exception {
// 		return getRichiesteUtente(getIdUtente(username));
// 	}

	
// 	public ResultSet getListaUtenti(RicercaUtenti parametri) throws SQLException, Exception {
// 		//TODO: gestire i filtri in input
// 		ResultSet result = null;
// 		Connection conn = this.connection;
// 		conn.beginRequest();
// 		try {
// 			conn.setAutoCommit(false);

// 			PreparedStatement statement = conn.prepareStatement("""
// SELECT id_uten, nome, cognome,
// 	user_name, codi_fisc, flag_accettazione,
// 	data_ins, data_upd, flag_attivo
// FROM foliage2.flguten_tab"""
// 			);
// 			result = statement.executeQuery();
// 		}
// 		finally {
// 			conn.endRequest();
// 		}
// 		return result;
// 	}
	
	
// 	public ResultSet getRichiesteResponsabile(String username) throws SQLException, Exception {
		
// 		ResultSet result = null;
// 		Connection conn = this.connection;
// 		conn.beginRequest();
// 		try {
// 			conn.setAutoCommit(false);

// 			PreparedStatement statement = conn.prepareStatement("""
// select /*u.*,'----' as sep,
// 	p.*,'----' as sep2, 
// 	x.*,
// 	ur.*/
// 	x.id_richiesta,                                                                                                                                
// 	p.id_comu, p.id_prov,
// 	u.user_name, ur.nome, ur.cognome, ur.codi_fisc,
// 	x.nome as ente, x.data_ins
// from foliage2.flguten_tab u
// 	left join foliage2.flgutru_tab p on (p.id_uten = u.id_uten)
// 	join lateral(
// 		select rp.*, 'Provincia di '|| pr.desc_prov as nome
// 		from foliage2.flgrichiestaresp_tab rp
// 			join foliage2.flgprov_tab pr ON (pr.id_prov = rp.id_prov)
// 		where rp.id_prov = p.id_prov
// 			and p.id_comu is null
// 		union all
// 		select rc.*, 
// 			'Comune di ' || case when pr.desc_prov = com.desc_comu then
// 					com.desc_comu
// 				else
// 					com.desc_comu||' ('||desc_prov||')'
// 			end as nome
// 		from foliage2.flgrichiestaresp_tab rc
// 			join foliage2.flgcomu_tab com on (com.id_comu = rc.id_comu)
// 			join foliage2.flgprov_tab pr ON (pr.id_prov = rc.id_prov)
// 		where rc.id_comu = p.id_comu
// 			and p.id_prov is null
// 		union all
// 		select rnpc.*,
// 			case when rnpc.id_comu is not null then 
// 				(
// 					select 'Comune di ' || case when prov.desc_prov = comu.desc_comu then
// 								comu.desc_comu
// 							else
// 								comu.desc_comu||' ('||desc_prov||')'
// 						end as nome
// 					from foliage2.flgcomu_tab comu
// 						join foliage2.flgprov_tab prov ON (prov.id_prov = comu.id_prov)
// 					where comu.id_comu = rnpc.id_comu
// 				)
// 				when rnpc.id_prov is not null then 
// 				(
// 					select 'Provincia di '|| prov.desc_prov as nome
// 					from foliage2.flgprov_tab prov
// 					where prov.id_prov = rnpc.id_prov
// 				)
// 				else 'Regione'
// 			end as nome
// 		from foliage2.flgrichiestaresp_tab rnpc
// 		where p.id_comu is null
// 			and p.id_prov is null
// 	) x on (true)
// 	left join foliage2.flguten_tab ur on (ur.id_uten = x.id_utente)
// where p.id_profilo in (11, 12)
// 	and x.stato = 0
// 	and u.user_name = ?"""
// 			);
// 			statement.setString(1, username);
// 			result = statement.executeQuery();
// 		}
// 		finally {
// 			conn.endRequest();
// 		}
// 		return result;
// 	}

// 	public String effettuaAccettazionePrivacy(String username) {
// 		HashMap<String, Object> mapParam = new HashMap<String, Object>();
// 		mapParam.put("username", username);
// 		SqlParameterSource parameters = new MapSqlParameterSource(mapParam);
// 		String sql = """
// update foliage2.flguten_tab
// set flag_accettazione = true
// where user_name = :username 
// 				""";
// 		template.update(sql, parameters);

// 		return "OK";
// 	}

// 	public String nuovaRichiestaProfilo(Integer idUtente, RichiestaProfilo richiesta) {
		
// 		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
// 		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );
		
// 		try {
// 			HashMap<String, Object> mapParam = new HashMap<String, Object>();
// 			mapParam.put("idUtente", idUtente);
// 			mapParam.put("idProfilo", richiesta.getRuoloRichiesto());
// 			mapParam.put("idEnte", richiesta.getIdEnte());
// 			mapParam.put("note", richiesta.getNoteRichiesta());

// 			SqlParameterSource parInsRich = new MapSqlParameterSource(mapParam);

// 			String sqlInsRich = """
// insert into FOLIAGE2.FLGRICHIESTE_PROFILI_TAB(ID_UTENTE, ID_PROFILO_RICHIESTO, ID_ENTE, DATA_RICHIESTA, NOTE_RICHIESTA)
// 	values (:idUtente, :idProfilo, :idEnte, LOCALTIMESTAMP, :note) RETURNING ID_RICHIESTA
// 					""";
			
// 			Integer idRich  = template.queryForObject(
// 				sqlInsRich,
// 				parInsRich, 
// 				(rs, rowNum)-> {	
// 					Integer r=  Integer.valueOf(rs.getInt(1));
// 					return r;
// 				}
// 			);
// 			DatiRichiestaResponsabile datiResp = richiesta.getDatiResponsabile();
// 			if (datiResp != null) {

// 				mapParam.remove("idProfilo");
// 				mapParam.remove("idEnte");

// 				Base64FormioFile attoNomina = datiResp.getAttoDiNomina()[0];
// 				Base64FormioFile docIdentita = datiResp.getDocumentoDiIdentita()[0];

// 				String sqlInsFile = """
// insert into FOLIAGE2.FLGBASE64_FORMIO_FILE_TAB(FILE_NAME, ORIGINAL_FILE_NAME, FILE_SIZE, STORAGE, FILE_TYPE, HASH_FILE, FILE_DATA)
// 	values (:fileName, :originalName, :fileSize, :storage, :type, :hash, :data) returning ID_FILE
// 						""";
				
// 				HashMap<String, Object> mapParamFile = new HashMap<String, Object>();
// 				mapParamFile.put("fileName", attoNomina.getName());
// 				mapParamFile.put("originalName", attoNomina.getOriginalName());
// 				mapParamFile.put("fileSize", attoNomina.getSize());
// 				mapParamFile.put("storage", attoNomina.getStorage());
// 				mapParamFile.put("type", attoNomina.getType());
// 				mapParamFile.put("hash", attoNomina.getHash());
// 				mapParamFile.put("data", attoNomina.getUrl().getBytes());

// 				Integer idFileAttoNomina  = template.queryForObject(
// 					sqlInsFile,
// 					new MapSqlParameterSource(mapParamFile), 
// 					(rs, rowNum)-> {	
// 						Integer r=  Integer.valueOf(rs.getInt(1));
// 						return r;
// 					}
// 				);

// 				mapParamFile.clear();
// 				mapParamFile.put("fileName", docIdentita.getName());
// 				mapParamFile.put("originalName", docIdentita.getOriginalName());
// 				mapParamFile.put("fileSize", docIdentita.getSize());
// 				mapParamFile.put("storage", docIdentita.getStorage());
// 				mapParamFile.put("type", docIdentita.getType());
// 				mapParamFile.put("hash", docIdentita.getHash());
// 				mapParamFile.put("data", docIdentita.getUrl().getBytes());

// 				Integer idFileDocIdentita = template.queryForObject(
// 					sqlInsFile,
// 					new MapSqlParameterSource(mapParamFile), 
// 					(rs, rowNum)-> {	
// 						Integer r=  Integer.valueOf(rs.getInt(1));
// 						return r;
// 					}
// 				);


// 				String sqlInsResp = """
// insert into FOLIAGE2.FLGRICHIESTE_RESPONSABILE_TAB(ID_RICHIESTA, TIPO_NOMINA, NUMERO_PROTOCOLLO, DATA_PROTOCOLLO, ID_FILE_ATTO_NOMINA, ID_FILE_DOC_IDENTITA)
// 	values (:idRichiesta, :tipoNomina, :numeroProtocollo, :dataProtocollo, :idFileAttoNomina, :idFileDocIdentita)
// """;

// 				mapParam.put("idRichiesta", idRich);
// 				mapParam.put("tipoNomina", datiResp.getTipoDiNomina());
// 				mapParam.put("numeroProtocollo", datiResp.getNumeroDiProtocollo());
// 				mapParam.put("dataProtocollo", datiResp.getDataProtocollo());
// 				mapParam.put("idFileAttoNomina", idFileAttoNomina);
// 				mapParam.put("idFileDocIdentita", idFileDocIdentita);

// 				SqlParameterSource parInsResp = new MapSqlParameterSource(mapParam);
// 				template.update(sqlInsResp, parInsResp);
// 			}

			
// 			platformTransactionManager.commit(status);
// 		}
// 		catch (Exception e) {
// 			platformTransactionManager.rollback(status);
// 			throw e;
// 		}
// 		finally {
// 			status = null;
// 		}

// 		return "OK";
// 	}

// 	public Object valutaRichiestaProfilo(Integer idUtenteVal, Integer idRichiesta, ValutazioneRichiestaProfilo valutazione) {

// 		///TODO: vanno fatte le verifiche sul profilo e migliorati i controlli per capire se la richiesta sia già approvata o annullata
// 		HashMap<String, Object> mapParam = new HashMap<String, Object>();
// 		mapParam.put("idRichiesta", idRichiesta);
// 		mapParam.put("idUtenteVal", idUtenteVal);
// 		mapParam.put("esito", valutazione.getEsito());
// 		mapParam.put("note", valutazione.getNote());

// 		SqlParameterSource parameters = new MapSqlParameterSource(mapParam);
// 		String sql = """
// update FOLIAGE2.FLGRICHIESTE_PROFILI_TAB
// set DATA_APPROVAZIONE = localtimestamp,
// 	ESITO_APPROVAZIONE = :esito,
// 	ID_UTENTE_APPROVAZIONE = :idUtenteVal,
// 	NOTE_APPROVAZIONE = :note
// where ID_RICHIESTA = :idRichiesta
// 	and DATA_ANNULLAMENTO is null
// 	and ESITO_APPROVAZIONE is null
// 				""";

// 		template.update(sql, parameters);

// 		sql = """
// insert into FOLIAGE2.FLGPROFILI_UTENTE_TAB(ID_UTENTE, ID_PROFILO)
// select ID_UTENTE, ID_PROFILO_RICHIESTO
// from FOLIAGE2.FLGRICHIESTE_PROFILI_TAB as RP
// where RP.ID_RICHIESTA = :idRichiesta
// 	and RP.ESITO_APPROVAZIONE = true
// 	and (ID_UTENTE, ID_PROFILO_RICHIESTO) not in (
// 		select PU.ID_UTENTE, PU.ID_PROFILO
// 		from FOLIAGE2.FLGPROFILI_UTENTE_TAB as PU
// 	)
// 				""";
// 		template.update(sql, parameters);

// 		sql = """
// insert into FOLIAGE2.FLGENTI_PROFILO_TAB(ID_UTENTE, ID_PROFILO, ID_ENTE)
// select ID_UTENTE, ID_PROFILO_RICHIESTO, ID_ENTE
// from FOLIAGE2.FLGRICHIESTE_PROFILI_TAB as RP
// where RP.ID_RICHIESTA = :idRichiesta
// 	and RP.ESITO_APPROVAZIONE = true
// 	and RP.ID_ENTE is not null
// 	and (ID_UTENTE, ID_PROFILO_RICHIESTO, ID_ENTE) not in (
// 		select EP.ID_UTENTE, EP.ID_PROFILO, EP.ID_ENTE
// 		from FOLIAGE2.FLGENTI_PROFILO_TAB as EP
// 	)
// 				""";
// 		template.update(sql, parameters);

// 		return "";
// 	}

// 	public Object getRichieste(RicercaUtenti parametri) throws SQLException {
// 		//TODO: gestire i filtri in input
// 		ResultSet result = this.GetResult(
// 			(conn) -> {
// 				PreparedStatement statement = conn.prepareStatement("""
// select ID_RICHIESTA, RP.DATA_RICHIESTA, U.ID_UTEN, U.USER_NAME, U.CODI_FISC, P.ID_PROFILO, P.DESCRIZIONE as PROFILO,
// 	E.ID_ENTE, E.TIPO_ENTE, E.NOME_ENTE,
// 	RP.DATA_APPROVAZIONE, RP.ESITO_APPROVAZIONE, RP.ID_UTENTE_APPROVAZIONE, UA.USER_NAME as USER_APPROVAZIONE, UA.CODI_FISC as COD_FISC_APPROVAZIONE
// from FOLIAGE2.FLGRICHIESTE_PROFILI_TAB as RP
// 	left join FOLIAGE2.FLGUTEN_TAB as U on (RP.ID_UTENTE = U.ID_UTEN)
// 	left join FLGPROF_TAB as P on (P.ID_PROFILO = RP.ID_PROFILO_RICHIESTO)
// 	left join FLGENTE_ROOT_TAB as E on (E.ID_ENTE = RP.ID_ENTE)
// 	left join FLGUTEN_TAB as UA on (RP.ID_UTENTE_APPROVAZIONE = UA.ID_UTEN)
// where RP.DATA_ANNULLAMENTO is null
// order by RP.DATA_RICHIESTA desc, ID_RICHIESTA desc
// 					"""
// 					);
// 				return statement.executeQuery();
// 			}
// 		);
// 		return result;
// 	}


// 	public Object getRuoliUtente(String username) throws SQLException, Exception {
// 		return getRuoliUtente(getIdUtente(username));
// 	}
// 	public Object getRuoliUtente(Integer idUtente) throws SQLException, Exception {

// 		Map<String, Object> mapParam = new HashMap<String, Object>();
// 		mapParam.put("idUtente", idUtente);
// 		SqlParameterSource parameters = new MapSqlParameterSource(mapParam);

// 		String sql = """
// 			select PU.ID_PROFILO, DESCRIZIONE
// 			from FOLIAGE2.FLGPROFILI_UTENTE_TAB as PU
// 			left join FOLIAGE2.FLGPROF_TAB as P on (P.ID_PROFILO = PU.ID_PROFILO)
// 			where PU.ID_UTENTE = :idUtente
// 			order by PU.ID_PROFILO
// 				""";

// 		Object outVal = template.query(
// 				sql,
// 				parameters, 
// 				(rs, rowNum)-> {
// 					Object o = new Object(){
// 						public Integer idProfilo = rs.getInt(1);
// 						public String descrizione = rs.getString(2);
// 					};

// 					return o;
// 				}
// 			);


// 		return outVal;
// 	}

// 	interface JsonFunc{
// 		Object eval(JsonElement e);
// 	}
	
// 	public Object aggiornaDatiUtente(Integer idUtente, JsonObject mods) throws FoliageException {
// 		JsonFunc stringFunc = (e) -> e.getAsString();
// 		JsonFunc dateFunc = (e) -> LocalDate.parse(e.getAsString(), DateTimeFormatter.ofPattern("YYYY-MM-DD"));

// 		LinkedList<Pair<String, String>> updates = new LinkedList<>();
// 		LinkedList<Triplet<String, String, JsonFunc>>  uteCols = new LinkedList<>();

// 		uteCols.addLast(new Triplet<String, String, JsonFunc>("nome", "nome", stringFunc));
// 		uteCols.addLast(new Triplet<String, String, JsonFunc>("cognome", "cognome", stringFunc));
// 		uteCols.addLast(new Triplet<String, String, JsonFunc>("dataNascita", "data_nascita", dateFunc));
// 		uteCols.addLast(new Triplet<String, String, JsonFunc>("luogoNascita", "luogo_nascita", stringFunc));
// 		uteCols.addLast(new Triplet<String, String, JsonFunc>("sesso", "sesso", stringFunc));
// 		uteCols.addLast(new Triplet<String, String, JsonFunc>("indirizzo", "indirizzo", stringFunc));
// 		uteCols.addLast(new Triplet<String, String, JsonFunc>("citta", "citta", stringFunc));
// 		uteCols.addLast(new Triplet<String, String, JsonFunc>("cap", "cap", stringFunc));
// 		uteCols.addLast(new Triplet<String, String, JsonFunc>("telefono", "telefono", stringFunc));
// 		uteCols.addLast(new Triplet<String, String, JsonFunc>("email", "email", stringFunc));

// 		Map<String, Object> mapUteParam = new HashMap<String, Object>();
		

// 		int idxPars = 1;
// 		for (Triplet<String, String, JsonFunc> pair : uteCols) {
// 			JsonElement elem = mods.get(pair.getValue0());
// 			if (elem != null) {
// 				String parName = String.format("v%s", idxPars++);
// 				String sqlParName = String.format(":%s", parName);
// 				mapUteParam.put(parName, pair.getValue2().eval(elem));
// 				updates.add(new Pair<String,String>(pair.getValue1(), sqlParName));
// 			}
// 		}
// 		if (updates.size() > 0) {
// 			mapUteParam.put("idUtente", idUtente);
// 			String sqlUpdUte = String.format(
// 				"""
// update FOLIAGE2.FLGUTEN_TAB
// set %s
// where ID_UTEN = :idUtente
// 				""",
// 				updates.stream().map((p)->String.format("%s = %s", p.getValue0(), p.getValue1())).collect(Collectors.joining(", ")).toString()
// 			);
// 			SqlParameterSource updUtenParameters = new MapSqlParameterSource(mapUteParam);
// 			template.update(sqlUpdUte, updUtenParameters);
// 		}

// 		JsonElement isProfMod = mods.get("isProfessionistaForestale");
// 		JsonElement autocertElem = mods.get("autocertificazioneProf");
// 		JsonObject autocertObj = (autocertElem == null) ? null : autocertElem.getAsJsonObject();
// 		if (isProfMod != null || autocertObj != null) {
// 			boolean v = (isProfMod != null) && isProfMod.getAsBoolean();
			
// 			Map<String, Object> mapAutocParam = new HashMap<String, Object>();
// 			mapAutocParam.put("idUtente", idUtente);
// 			SqlParameterSource updAutocParameters = new MapSqlParameterSource(mapAutocParam);
// 			String annullAutocert = """
// update FOLIAGE2.FLGAUTOCERT_PROF_TAB
// set DATA_ANNULLAMENTO = localtimestamp
// where ID_UTENTE = :idUtente
// and DATA_ANNULLAMENTO is null
// 			""";
// 			template.update(annullAutocert, updAutocParameters);

// 			String delProf = """
// delete from FOLIAGE2.FLGUTE_PROFESSIONISTI_TAB
// where ID_UTENTE = :idUtente
// 			""";
// 			template.update(delProf, updAutocParameters);
// 			if (v) {
// 				if (autocertObj == null) {
// 					throw new FoliageException("Dati autocertificazione professionista mancanti");
// 				}
// 				else {
// 					final BiFunction<JsonObject, String, String> getStr = (js, s) -> {
// 						JsonElement e = js.get(s);
// 						return (e == null) ? null:e.getAsString();
// 					};
// 					final BiFunction<JsonObject, String, Integer> getInt = (js, s) -> {
// 						JsonElement e = js.get(s);
// 						return (e == null) ? null: Integer.parseInt(e.getAsString());
// 					};
// 					String categoria = getStr.apply(autocertObj, "categoria");
// 					String sottocategoria = getStr.apply(autocertObj, "sottocategoria");
// 					String collegio = getStr.apply(autocertObj, "collegio");
// 					String numeroIscrizione = getStr.apply(autocertObj, "numeroIscrizione");
// 					Integer provinciaIscrizione = getInt.apply(autocertObj, "provinciaIscrizione");
// 					Boolean isSenior = (sottocategoria.equals("senior"));

// 					String insAutocert = """
// insert into FOLIAGE2.FLGAUTOCERT_PROF_TAB(ID_UTENTE, CATEGORIA, SOTTOCATEGORIA, COLLEGGIO, NUMERO_ISCRIZIONE, ID_PROVINCIA_ISCRIZIONE, DATA_INSERIMENTO)
// values(:idUtente, :categoria, :sottocategoria, :collegio, :numeroIscrizione, :provinciaIscrizione, localtimestamp)
// 					""";
// 					Map<String, Object> mapDatiAutocParam = new HashMap<String, Object>();
// 					mapDatiAutocParam.put("idUtente", idUtente);
// 					mapDatiAutocParam.put("categoria", categoria);
// 					mapDatiAutocParam.put("sottocategoria", sottocategoria);
// 					mapDatiAutocParam.put("collegio", collegio);
// 					mapDatiAutocParam.put("numeroIscrizione", numeroIscrizione);
// 					mapDatiAutocParam.put("provinciaIscrizione", provinciaIscrizione);
// 					SqlParameterSource insDatiAutocParam = new MapSqlParameterSource(mapDatiAutocParam);
// 					template.update(insAutocert, insDatiAutocParam);

// 					String insRuol = """
// insert into FOLIAGE2.flgprofili_utente_tab(ID_UTENTE, ID_PROFILO)
// select :idUtente, 2
// where (:idUtente, 2) not in (
// 		select ID_UTENTE, ID_PROFILO
// 		from FOLIAGE2.flgprofili_utente_tab
// 	)
// 					""";
// 					String insProf = """
// insert into FOLIAGE2.FLGUTE_PROFESSIONISTI_TAB(ID_UTENTE, ID_PROFILO, IS_SENIOR)
// values (:idUtente, 2, :isSenior)
// 					""";
// 					Map<String, Object> mapDatiProf = new HashMap<String, Object>();
// 					mapDatiProf.put("idUtente", idUtente);
// 					mapDatiProf.put("isSenior", isSenior);
// 					SqlParameterSource insDatiProf = new MapSqlParameterSource(mapDatiProf);

// 					template.update(insRuol, insDatiProf);
// 					template.update(insProf, insDatiProf);
					
// 				}
// 			}
// 			else {
// 				String delRuol = """
// delete from FOLIAGE2.FLGPROFILI_UTENTE_TAB
// where ID_UTENTE = :idUtente
// 	and ID_PROFILO = 2
// 						""";
// 				template.update(delRuol, updAutocParameters);
// 			}
// 		}
		
	
// 		return "OK";
// 	}
// }
