package it.almaviva.foliage.authentication;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
//import java.util.LinkedList;
import java.util.HashMap;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.services.AbstractDal;

@Component
public class JwtAuthenticationDal extends AbstractDal {
	@Autowired
	public JwtAuthenticationDal(
		JdbcTemplate jdbcTemplate,
		TransactionTemplate transactionTemplate,
		PlatformTransactionManager platformTransactionManager
	) throws Exception {
		super(jdbcTemplate, transactionTemplate, platformTransactionManager, "JwtAuthentication");
	}

    @Value("${foliage.security.user-block-mode:}")
    private String userBlockMode;

	public synchronized void censimentoUtente(AccessToken token) throws SQLException {
		Collection<GrantedAuthority> authorities = token.getAuthorities();
		//SimpleGrantedAuthority
		boolean isBlack = "black".equals(userBlockMode);
		boolean isWhite = !isBlack && "white".equals(userBlockMode);

		if (isBlack || isWhite) {
			try {
				String userName = token.getUsername();
				
				String findCfSql = String.format("""
select exists (
		select *
		from foliage2.flgutenti_%s_list_tab u
		where u.user_name = :userName
	) as res""",
					(isBlack ? "black" : "white")
				);
				HashMap<String, Object> parMap = new HashMap<>();
				parMap.put("userName", userName);
				Boolean res = this.queryForObject(
					findCfSql,
					parMap,
					DbUtils.GetBooleanRowMapper("res")
				);
				if (
					(!res && isWhite)
					|| (res && isBlack)
				) {
					throw new FoliageException("Utenza non autorizzata ad accedere alla piattaforma");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				if (e.getClass() == FoliageException.class) {
					throw e;
				}
			}
		}



		DefaultTransactionDefinition paramTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = platformTransactionManager.getTransaction(paramTransactionDefinition );

		try {
			ResultSet result = this.GetResult(
				(conn) -> {
					PreparedStatement statement = conn.prepareStatement("""
SELECT a.id_uten,
	data_ins, user_name, 
	nome, cognome, codi_fisc, 
	data_nascita, luogo_nascita, sesso, 
	indirizzo, citta, cap
FROM foliage2.flguten_tab a
WHERE a.user_name= ?"""
						);
					statement.setString(1, token.getUsername());
					return statement.executeQuery();
				}
			);
			if (result.next()) {
				int idUten = result.getInt(1);
				String nome = result.getString(4);
				String cognome = result.getString(5);
				String codiFisc = result.getString(6);
				Date dataNascitaS = result.getDate(7);
				LocalDate dataNascita = (dataNascitaS == null) ? null : dataNascitaS.toLocalDate();
				String luogoNascita = result.getString(8);
				String genere = result.getString(9);
				String indirizzo = result.getString(10);
				String citta = result.getString(11);
				String cap = result.getString(12);
				// String telefono = result.getString(13);
				// String email = result.getString(14);
				// String pec = result.getString(15);

				result.close();
				token.setIdUtente(idUten);
				token.setName(nome);
				token.setSurname(cognome);
				token.setCodiceFiscale(codiFisc);
				token.setBirthDate(dataNascita);
				token.setBirthPlace(luogoNascita);
				token.setGender(genere);
				token.setAddress(indirizzo);
				token.setCity(citta);
				token.setCap(cap);
				// token.setPhoneNumber(telefono);
				// token.setEmail(email);
				// token.setPec(pec);

				ResultSet result3 = this.GetResult(
					(conn) -> {
						PreparedStatement statement = conn.prepareStatement("""
select id_profilo, id_ente
from FOLIAGE2.flgenti_profilo_tab fpt
where id_utente = ?"""
						);
						statement.setInt(1, idUten);
						return statement.executeQuery();
					}
				);

				HashMap<Integer, LinkedList<Integer>> entiProfiloMap = new HashMap<>();
				while(result3.next()) {
					Integer idProf = result3.getInt(1);
					Integer idEnte = result3.getInt(2);
					if (entiProfiloMap.containsKey(idProf)) {
						entiProfiloMap.get(idProf).addLast(idEnte);
					}
					else {
						LinkedList<Integer> list = new LinkedList<>();
						list.addLast(idEnte);
						//  (new Integer[]{idEnte})
						entiProfiloMap.put(
							idProf,
							list
						);
					}
				}
			

				ResultSet result2 = this.GetResult(
					(conn) -> {
						PreparedStatement statement = conn.prepareStatement("""
select PU.ID_PROFILO, DESCRIZIONE, PU.FLAG_DEFAULT, TIPO_AUTH, P.TIPO_AMBITO, PU.FLAG_SENIOR
from FOLIAGE2.FLGPROFILI_UTENTE_TAB as PU
	left join FOLIAGE2.FLGPROF_TAB as P on (P.ID_PROFILO = PU.ID_PROFILO)
where PU.ID_UTENTE = ?
order by PU.ID_PROFILO
							"""
							);
						statement.setInt(1, idUten);
						return statement.executeQuery();
					}
				);
					
				while(result2.next()) {
					int id = result2.getInt(1);
					String desc = result2.getString(2);
					Boolean def = result2.getBoolean(3);
					String tipoAuth = result2.getString(4);
					String ambito = result2.getString(5);
					Boolean isSenior = result2.getBoolean(6);
					if (result2.wasNull()) {
						isSenior = false;
					}
					final Boolean isSeniorFin = isSenior;
				
					LinkedList<Integer> listaProfili = null;
					if (entiProfiloMap.containsKey(id)) {
						listaProfili = entiProfiloMap.get(id);
					}

					FoliageGrantedAuthority fg = new FoliageGrantedAuthority(
						id,
						desc,
						tipoAuth,
						ambito,
						isSenior,
						listaProfili
					);
					GrantedAuthority g = fg;
					if (def) {
						token.setProfiloDefault(fg);
					}
					authorities.add(g);
				}
				result2.close();
			}
			else {
				Integer idUtente = null;
				HashMap<String, Object>  mapUteParams = new HashMap<String, Object>();
				mapUteParams.put("username", token.getUsername());
				mapUteParams.put("name", token.getName());
				mapUteParams.put("surname", token.getSurname());
				mapUteParams.put("address", token.getAddress());
				mapUteParams.put("city", token.getCity());
				mapUteParams.put("cap", token.getCap());
				mapUteParams.put("codiceFiscale", token.getCodiceFiscale());
				mapUteParams.put("birthDate", token.getBirthDate());
				mapUteParams.put("birthPlace", token.getBirthPlace());
				//mapUteParams.put("phoneNumber", token.getPhoneNumber());
				mapUteParams.put("gender", token.getGender());
				//mapUteParams.put("email", token.getEmail());

				SqlParameterSource sqlUteParams = new MapSqlParameterSource(mapUteParams);
				
				String sqlInsUte = """
INSERT INTO foliage2.flguten_tab (
	data_ins, user_name, 
	nome, cognome, codi_fisc, 
	data_nascita, luogo_nascita, sesso, 
	indirizzo, citta, cap
)
VALUES (
	localtimestamp(0), :username,
	:name, :surname, :codiceFiscale,
	:birthDate, :birthPlace, :gender,
	:address, :city, :cap
) returning id_uten""";

				idUtente = template.queryForObject(
					sqlInsUte,
					mapUteParams,
					(rs, rn) -> rs.getInt(1)
				);

				String sqlInsDefProf = """
INSERT INTO foliage2.flgprofili_utente_tab (
		id_utente, id_profilo, flag_default
	)
values(
		:idUtente, 1, true
	)""";
				HashMap<String, Object>  mapProfParams = new HashMap<String, Object>();
				mapProfParams.put("idUtente", idUtente);
				//SqlParameterSource sqlProfParams = new MapSqlParameterSource(mapProfParams);
				//template.
				this.update(sqlInsDefProf, mapProfParams);


				FoliageGrantedAuthority fg = new FoliageGrantedAuthority(
					1,
					"Proprietario e gestore forestale",
					"PROP",
					"GENERICO",
					false,
					null
				);
				GrantedAuthority g = fg;
				token.setProfiloDefault(fg);
				authorities.add(g);
			}

			platformTransactionManager.commit(status);
	
		}
		catch (Exception e) {
			platformTransactionManager.rollback(status);
			throw e;
		}
		finally {
			status = null;
		}
	}
}
