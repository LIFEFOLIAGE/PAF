package it.almaviva.foliage.controllers;

import it.almaviva.foliage.bean.*;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import it.almaviva.foliage.services.WebDal;
import io.micrometer.core.instrument.util.IOUtils;
import it.almaviva.foliage.FoliageAuthorizationException;
import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.authentication.AccessToken;
import it.almaviva.foliage.authentication.FoliageGrantedAuthority;
import it.almaviva.foliage.authentication.JwtAuthentication;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.almaviva.foliage.legacy.bean.RicercaUtenti;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;




@RestController
@Slf4j
@RequestMapping("${foliage.base-path}api/web")
@Tag(name = "Web", description = "Operazioni dell'app web")
public class WebController {

	@Autowired
	@Qualifier("webDal")
	private WebDal dal;

	public WebController() throws Exception {
		log.debug(
			String.format(
				"Timezone is %s",
				TimeZone.getDefault().toString()
			)
		);
	}


	@GetMapping("/istanze/{codIstanza}")
	public Object apriIstanza(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable String codIstanza
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken tok = jwtAuth.getAccessToken();
		Integer idUtente = tok.getIdUtente();
		String codFiscale = tok.getCodiceFiscale();
		AbilitazioniIstanza abil = dal.getAbilitazioniIstanza(idUtente, codFiscale, codIstanza, authority, authScope);

		if (abil.access) {
			return new ResponseEntity<>(dal.apriIstanza(codIstanza, idUtente, authority, authScope), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping("/istanze/{codIstanza}/info")
	public Object getInfoIstanza(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable String codIstanza
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AbilitazioniIstanza abil = dal.getAbilitazioniIstanza(jwtAuth.getAccessToken().getIdUtente(), jwtAuth.getAccessToken().getCodiceFiscale(), codIstanza, authority, authScope);
		if (abil.access) {
			return new ResponseEntity<>(dal.getInfoIstanza(codIstanza), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}
	
	@PostMapping("/istanze/{codIstanza}/rimuovi-gestore")
	public Object rimuoviGestoreIstanza(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable String codIstanza
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		AbilitazioniIstanza abil = dal.getAbilitazioniIstanza(idUtente, jwtAuth.getAccessToken().getCodiceFiscale(), codIstanza, authority, authScope);
		
		if ("PROP".equals(authority) || "PROF".equals(authority)) {
			if (abil.rimuovi_gestore) {
				return new ResponseEntity<>(dal.rimuoviGestore(codIstanza, idUtente), HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
			}
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}


	@GetMapping("/istanze/{codIstanza}/assegnaA/{idIstruttore}")
	public Object assegnaIstanza(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable String codIstanza,
		@PathVariable Integer idIstruttore
	) throws Exception {
		
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		AbilitazioniIstanza abil = dal.getAbilitazioniIstanza(idUtente, jwtAuth.getAccessToken().getCodiceFiscale(), codIstanza, authority, authScope);
		if ("DIRI".equals(authority)) {
			if (abil.assegna_istruttore) {
				return new ResponseEntity<>(dal.assegnaIstruttore(codIstanza, idUtente, idIstruttore), HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
			}
		}
		else {
			if ("PROP".equals(authority) || "PROF".equals(authority)) {
				if (abil.passaggio_gestore) {
					return new ResponseEntity<>(dal.assegnaGestore(codIstanza, idUtente, idIstruttore), HttpStatus.OK);
				}
				else {
					return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
				}
			}
			else {
				return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
			}
		}
	}


	// @GetMapping("/istanze/{codIstanza}/listaParticelle")
	// public Object getListaParticelleIstanza(@PathVariable String codIstanza) throws Exception {
	// 	return new ResponseEntity<>(dal.getListaParticelleIstanza(codIstanza), HttpStatus.OK);

	// 	//return ResultSetSerializer.ToObjectNode(dal.getListaIstanze());
	// 	//return service.find(id);
	// }


	// @PutMapping("/istanze/{codIstanza}")
	// public String salvaIstanza(
	// 	@PathVariable String codIstanza,
	// 	HttpServletRequest request
	// ) throws Exception {
	// 	//TODO: verificare condizioni salvataggio a seconda del profilo
	// 	final String json = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
	// 	log.debug(json);
	// 	return json;
	// }

	
	@DeleteMapping("/istanze/{codIstanza}")
	public Object eliminaIstanza(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable String codIstanza
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();

		String codFiscaleUtente = jwtToken.getCodiceFiscale();
		Integer idUtente = jwtToken.getIdUtente();

		AbilitazioniIstanza abil = dal.getAbilitazioniIstanza(idUtente, codFiscaleUtente, codIstanza, authority, authScope);
		
		if (abil.compilazione) {
			dal.eliminaIstanza(codIstanza);
			return new ResponseEntity<>(
				"Ok",
				HttpStatus.OK
			);
		}
		else {
			return new ResponseEntity<>(
				"Accesso Negato",
				HttpStatus.FORBIDDEN
			);

		}

	}

	@PutMapping("/istanze/{codIstanza}/{idxScheda}")
	public Object salvaSchedaIstanza(
		@PathVariable String codIstanza,
		@PathVariable Integer idxScheda,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		HttpServletRequest request
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();

		String codFiscaleUtente = jwtToken.getCodiceFiscale();
		Integer idUtente = jwtToken.getIdUtente();

		AbilitazioniIstanza abil = dal.getAbilitazioniIstanza(idUtente, codFiscaleUtente, codIstanza, authority, authScope);
		
		if (abil.compilazione) {
			try {
				Object outVal = dal.salvaSchedaIstanza(
					codIstanza, idxScheda,
					codFiscaleUtente, idUtente,
					request.getInputStream()
				);
				return new ResponseEntity<>(
					outVal,
					HttpStatus.OK
				);
			}
			catch(FoliageAuthorizationException ae) {
				return new ResponseEntity<>(
					ae.toString(),
					HttpStatus.FORBIDDEN
				);
			}
		}
		else {
			return new ResponseEntity<>(
				"Accesso Negato",
				HttpStatus.FORBIDDEN
			);

		}


		// //TODO: verificare condizioni salvataggio a seconda del profilo
		// final String json = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
		// log.debug(json);
		// return json;
	}



	// @PutMapping("/istanze/{tipoIStanza}/{codIstanza}/{sezione}/{scheda}")
	// public String salvaSchedaIstanza(
	// 	@PathVariable String tipoIStanza,
	// 	@PathVariable String codIstanza,
	// 	@PathVariable String sezione,
	// 	@PathVariable String scheda,
	// 	HttpServletRequest request
	// ) throws Exception {
	// 	//TODO: verificare condizioni salvataggio a seconda del profilo
	// 	final String json = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
	// 	log.debug(json);
	// 	return json;
	// }

	// @GetMapping("/istanze")
	// public Object listaIstanze() throws Exception {
	// 	return new ResponseEntity<>(dal.getListaIstanze(), HttpStatus.OK);

	// 	//return ResultSetSerializer.ToObjectNode(dal.getListaIstanze());
	// 	//return service.find(id);
	// }

	@PostMapping("/istanze")
	public Object getIstanze(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@RequestBody ChiaviRicercaIstanza parametri
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;


		String codFiscaleUtente = jwtAuth.getAccessToken().getCodiceFiscale();
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		return new ResponseEntity<>(
			dal.ricercaInstanze(
				authority, authScope,
				idUtente, codFiscaleUtente,
				parametri
			),
			HttpStatus.OK
		);
	}

	@GetMapping("/istanze/lista-stati-istanza")
	public Object getListaStatiIstanza() throws SQLException {
		return new ResponseEntity<>(dal.getListaStatiIstanza(), HttpStatus.OK);
	}

	@GetMapping("/istanze/lista-tipi-istanza")
	public Object getListaTipiIstanza() throws SQLException {
		return new ResponseEntity<>(dal.getListaTipiIstanza(), HttpStatus.OK);
	}

	@GetMapping("/istanze/lista-tipi-istanza-specifici")
	public Object getListaTipiSpecificiIstanza() throws SQLException {
		return new ResponseEntity<>(dal.getListaTipiSpecificiIstanza(), HttpStatus.OK);
	}

	@GetMapping("/istanze/lista-tipi-azienda")
	public Object getListaTipiAzienda() throws SQLException {
		return new ResponseEntity<>(dal.getListaTipiAzienda(), HttpStatus.OK);
	}
	@GetMapping("/istanze/lista-tipi-proprieta")
	public Object getListaTipiProprieta() throws SQLException {
		return new ResponseEntity<>(dal.getListaTipiProprieta(), HttpStatus.OK);
	}
	@GetMapping("/istanze/lista-natura-proprieta")
	public Object getListaNaturaProprieta() throws SQLException {
		return new ResponseEntity<>(dal.getListaNaturaProprieta(), HttpStatus.OK);
	}
	@GetMapping("/istanze/lista-qualificazioni-proprietario")
	public Object getListaQualificazioniProprietario() throws SQLException {
		return new ResponseEntity<>(dal.getListaQualificazioniProprietario(), HttpStatus.OK);

		// return new Object[] {
		// 	new Object() {
		// 		public int idQualificazione = 0;
		// 		public String codQualificazione = "PROPRIETARIO";
		// 		public String descQualificazione = "Proprietario";
		// 	},
		// 	new Object() {
		// 		public int idQualificazione = 1;
		// 		public String codQualificazione = "COMPROPRIETARIO";
		// 		public String descQualificazione = "Comproprietario";
		// 	},
		// 	new Object() {
		// 		public int idQualificazione = 2;
		// 		public String codQualificazione = "AFFITTUARIO";
		// 		public String descQualificazione = "Affittuario";
		// 	},
		// 	new Object() {
		// 		public int idQualificazione = 3;
		// 		public String codQualificazione = "RAPPRESENTANTE";
		// 		public String descQualificazione = "Rappresentante legale";
		// 	},
		// 	new Object() {
		// 		public int idQualificazione = 4;
		// 		public String codQualificazione = "COMODATO";
		// 		public String descQualificazione = "Titolare di comodato d'uso";
		// 	},
		// 	new Object() {
		// 		public int idQualificazione = 5;
		// 		public String codQualificazione = "ALTRO";
		// 		public String descQualificazione = "Altro titolo di possesso del soprasuolo";
		// 	},
		// 	new Object() {
		// 		public int idQualificazione = 6;
		// 		public String codQualificazione = "DELEGATO";
		// 		public String descQualificazione = "Delegato del proprietario";
		// 	}
		// };
	}


	@GetMapping("/istanze/lista-stato-lavori")
	public Object getListaStatoLavori() {
		return new Object[] {
			new Object() {
				public int idStato = 0;
				public String codStato = "ASSENTI";
				public String descStato = "Non Pianificati";
			}, // dopo l'approvazione
			new Object() {
				public int idStato = 1;
				public String codStato = "PIANIFICATI";
				public String descStato = "Pianificati";
			}, // dopo l'indicazione della data di inizio
			new Object() {
				public int idStato = 2;
				public String codStato = "INIZIATI";
				public String descStato = "Iniziati";
			}, // dopo il superamento della data di inizio
			new Object() {
				public int idStato = 3;
				public String codStato = "CON_SCADENZA";
				public String descStato = "Con scadenza";
			}, // dopo l'indicazione della data di fine
			new Object() {
				public int idStato = 4;
				public String codStato = "TERMINATI";
				public String descStato = "Terminati";
			} // dopo il superamento della data di fine
		};
	}

	@GetMapping("/csrs")
	public Object getCsrsToken(HttpServletRequest request) {
		Object tok = request.getAttribute(CsrfToken.class.getName());
		if (tok == null) {
			return new Object() {
				public String headerName = null;
				public String token = null;
			};
		}
		else {
			return tok;
		}
	}


	@PutMapping("/istanza")
	public Object creaIstanza(
		@RequestBody CreazioneIstanza parametri,
		@QueryParam("authority") String authority
	) throws SQLException, FoliageException, Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		//username = jwtAuth.getUsername();

		//TODO: gestire creazione diversa per proprietario e professionista

		if (!"PROF".equals(authority)) {
			if ("PROP".equals(authority)) {
				if (!"TAGLIO_BOSCHIVO".equals(parametri.getTipoInsta()) && !"INTERVENTO_A_COMUNICAZIONE".equals(parametri.getTipoInsta())) {
					throw new FoliageException("Con il profilo corrente non è possibile creare istanze di questo tipo");
				}
			}
			else {
				throw new FoliageException("Con il profilo corrente non è possibile creare istanze");
			}
		}

		if (parametri.getDatiTitolare() != null) {
			if ("PROP".equals(authority)) {
				throw new FoliageException("Con il profilo corrente non è possibile creare istanze per altri soggetti");
			}
		}
		// else {
		// 	//parametri.setDatiTitolare(DatiTitolare.datiFromToken(token));
		// 	parametri.setDatiTitolare(dal.getDatiTitolareFromUser(token.getIdUtente()));
		// }

		return new ResponseEntity<>(
			dal.creaIstanza(
					token.getIdUtente(),
					token.getUsername(),
					parametri
				),
				HttpStatus.OK
			);
	}

	@GetMapping("/provincie")
	public Object GetAllProvincie() throws SQLException, Exception {
		return new ResponseEntity<>(dal.GetAllProvincie(), HttpStatus.OK);
	}

	@GetMapping("/regione-host") // TODO: cambio path togliendo istanze
	public Object GetRegioneHost() throws SQLException, Exception {
		return new ResponseEntity<>(dal.GetRegioneHost(), HttpStatus.OK);
	}

	@GetMapping("/provincie-host") // TODO: cambio path togliendo istanze
	public Object GetProvincieRegioneHost() throws SQLException, Exception {
		return new ResponseEntity<>(dal.GetProvincieRegioneHost(), HttpStatus.OK);
	}

	@GetMapping("/comuni/{idProvincia}") // TODO: cambio path togliendo istanze
	public Object GetComuni(@PathVariable Integer idProvincia) throws SQLException, Exception {
		return new ResponseEntity<>(dal.GetComuniProvincia(idProvincia), HttpStatus.OK);
	}

	@GetMapping("/info-ente/{idEnte}") // TODO: cambio path togliendo istanze
	public Object GetInfoEnte(@PathVariable Integer idEnte) throws SQLException, Exception {
		return new ResponseEntity<>(dal.GetInfoEnte(idEnte), HttpStatus.OK);
	}
	@GetMapping("/info-comune/{idComune}")
	public Object GetInfoComune(@PathVariable Integer idComune) throws SQLException, Exception {
		return new ResponseEntity<>(dal.GetInfoComune(idComune), HttpStatus.OK);
	}

	@GetMapping("/info-comuni-host")
	public Object GetInfoComuniHost() throws SQLException, Exception {
		return new ResponseEntity<>(dal.GetInfoComuniHost(), HttpStatus.OK);
	}
	@GetMapping("/info-comuni-regione/{codRegione}")
	public Object GetInfoComuniRegione(String codRegione) throws SQLException, Exception {
		return new ResponseEntity<>(dal.GetInfoComuniRegione(codRegione), HttpStatus.OK);
	}


	@GetMapping("/strutture-soprasuolo")
	public Object GetStruttureSoprasuolo() throws SQLException, Exception {
		return new ResponseEntity<>(dal.GetStruttureSoprasuolo(), HttpStatus.OK);
	}

	@GetMapping("/forme-di-governo")
	public Object GetFormeDiGoverno() throws SQLException, Exception {
		return new ResponseEntity<>(dal.GetFormeDiGoverno(), HttpStatus.OK);
	}
	
	// @GetMapping("/macrocategorie-specie")
	// public Object GetMacrocategorieSpecie() throws SQLException, Exception {
	// 	return new ResponseEntity<>(dal.GetMacrocategorieSpecie(), HttpStatus.OK);
	// }
	@GetMapping("/info-speci-forestali")
	public Object GetInfoSpeciForestali() throws SQLException, Exception {
		return new ResponseEntity<>(dal.GetInfoSpeciForestali(), HttpStatus.OK);
	}

	// @GetMapping("/categorie-specie/{macrocategoria}")
	// public Object GetCategorieSpecie(
	// 	@PathVariable String macrocategoria
	// ) throws SQLException, Exception {
	// 	return new ResponseEntity<>(dal.GetCategorieSpecie(macrocategoria), HttpStatus.OK);
	// }
	// @GetMapping("/speci-forestali/{categoria}")
	// public Object GetSpeciForestali(
	// 	@PathVariable String categoria
	// ) throws SQLException, Exception {
	// 	return new ResponseEntity<>(dal.GetSpeciForestali(categoria), HttpStatus.OK);
	// }

	@GetMapping("/istanze/{codIstanza}/info-soprasuolo")
	public Object GetInfoSoprasuolo(
		@PathVariable String codIstanza
	) throws SQLException, Exception {
		return new ResponseEntity<>(dal.GetFormeDiTrattamento(codIstanza), HttpStatus.OK);
	}

	@GetMapping("/interventi-ambiti-non-forestali")
	public Object GetInterventiAmbitiNonForestali() throws SQLException, Exception {
		return new ResponseEntity<>(dal.GetInterventiAmbitiNonForestali(), HttpStatus.OK);
	}

	// @GetMapping("/istanze/{codIstanza}/forme-di-trattamento/{tipo}")
	// public Object GetFormeDiTrattamento(
	// 	@PathVariable String codIstanza,
	// 	@PathVariable String tipo
	// ) throws SQLException, Exception {
	// 	return new ResponseEntity<>(dal.GetFormeDiTrattamento(codIstanza, tipo), HttpStatus.OK);
	// }

	// @GetMapping("/forme-di-trattamento/{tipo}")
	// public Object GetFormeDiTrattamento(
	// 	@PathVariable String tipo
	// ) throws SQLException, Exception {
	// 	return new ResponseEntity<>(dal.GetFormeDiTrattamento(tipo), HttpStatus.OK);
	// }

	@GetMapping("/istanze/{codIstanza}/layer")
	public Object GetLayerDomanda(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable String codIstanza
	) throws Exception {
		
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();

		String codFiscaleUtente = jwtToken.getCodiceFiscale();
		Integer idUtente = jwtToken.getIdUtente();

		AbilitazioniIstanza abil = dal.getAbilitazioniIstanza(idUtente, codFiscaleUtente, codIstanza, authority, authScope);
		if (abil.compilazione || abil.consultazione) {
			return new ResponseEntity<>(dal.GetLayerDomanda2(codIstanza), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso Negato", HttpStatus.FORBIDDEN);
		}
	}

	// @PutMapping("/istanze/{codIstanza}/upload/{tipoFile}")
	// public Object upload(
	// 	@PathVariable String codIstanza,
	// 	@PathVariable String tipoFile,
	// 	@RequestParam("file") MultipartFile file
	// ) throws IOException {
	//     byte[] bytes = null;
	//     if (!file.isEmpty()) {
	//          bytes = file.getBytes();
	//         //store file in storage
	//     }

	//     log.debug(String.format("receive %s come %s dell'istanza %s", file.getOriginalFilename(), tipoFile, codIstanza));
	// 	return new ResponseEntity<>(null, HttpStatus.OK);
	// }

	/////////////////////////////////////////////////////////




	@GetMapping("/corrente")
	public Object readCurrent() throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		return this.dal.getInfoUtente(jwtAuth.getUsername());
	}

	// @GetMapping("/profili")
	// public Object getRuoliUtenteCorrente() throws Exception {
	//     Authentication a = SecurityContextHolder.getContext().getAuthentication();
	//     JwtAuthentication jwtAuth = (JwtAuthentication)a;
	//     return new ResponseEntity<>(dal.getProfiliUtente(jwtAuth.getAccessToken().getIdUtente()), HttpStatus.OK);
	// }
	@GetMapping("/corrente/notifiche")
	public Object getNotificheUtenteCorrente() throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		return new ResponseEntity<>(dal.getNotificheUtente(jwtAuth.getAccessToken().getIdUtente(), 20), HttpStatus.OK);
	}

	@PostMapping(path = "/corrente/notifica/{idNotifica}")
	public Object segnaLetturaNotifica(@PathVariable Integer idNotifica) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		return new ResponseEntity<>(dal.segnaLetturaNotifica(jwtAuth.getAccessToken().getIdUtente(), idNotifica), HttpStatus.OK);
	}

	@GetMapping("/corrente/richieste")
	public Object getRichiesteUtenteCorrente() throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		return new ResponseEntity<>(dal.getAllRichiesteUtente(jwtAuth.getAccessToken().getIdUtente()), HttpStatus.OK);
	}

	@GetMapping("/corrente/richieste/{idRichiesta}")
	public Object getRichiestaUtenteCorrente(
		@PathVariable Integer idRichiesta,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		return new ResponseEntity<>(dal.getRichiestaUtente(idUtente, idRichiesta, idUtente, authority, authScope), HttpStatus.OK);
	}

	@DeleteMapping("/corrente/richieste/{idRichiesta}")
	public Object cancelRichiestaUtenteCorrente(@PathVariable Integer idRichiesta) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		return new ResponseEntity<>(dal.cancelRichiestaUtente(jwtAuth.getAccessToken().getIdUtente(), idRichiesta), HttpStatus.OK);
	}

	@PostMapping(path = "/corrente/accettazione-privacy")
	public Object accettazionePrivacy() throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		return new ResponseEntity<>(dal.effettuaAccettazionePrivacy(jwtAuth.getUsername()), HttpStatus.OK);
	}

	@PutMapping(path = "/corrente")
	public Object aggiornaDatiUtente(HttpServletRequest request) throws Exception {
		final String json = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
		JsonObject mods = new Gson().fromJson(json, JsonObject.class);
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		return new ResponseEntity<>(dal.aggiornaDatiUtente(jwtAuth.getAccessToken().getIdUtente(), mods), HttpStatus.OK);
	}

	@PostMapping(path = "/corrente/default/{idProfilo}")
	public Object aggiornaProfiloDefault(@PathVariable Integer idProfilo) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		return new ResponseEntity<>(dal.aggiornaProfiloDefault(jwtAuth.getAccessToken().getIdUtente(), idProfilo), HttpStatus.OK);
	}


	@PostMapping(path = "/corrente/nuova-richiesta-profilo", consumes = MediaType.APPLICATION_JSON)
	public Object nuovaRichiestaProfilo(@RequestBody RichiestaProfilo richiesta) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;

		return new ResponseEntity<>(dal.nuovaRichiestaProfilo(jwtAuth.getAccessToken().getIdUtente(), richiesta), HttpStatus.OK);
	}

	@GetMapping("/corrente/profili-selezionabili")
	public Object getProfiliSelezionabili() throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;

		return new ResponseEntity<>(jwtAuth.getAccessToken().getAuthorities(), HttpStatus.OK);
	}

	@GetMapping("/corrente/ruolo/{idProfilo}/enti")
	public Object getEntiPerProfilo(@PathVariable Integer idProfilo) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		return new ResponseEntity<>(this.dal.getMieiEntiPerRuolo(jwtAuth.getAccessToken().getIdUtente(), idProfilo), HttpStatus.OK);
	}


	@GetMapping("/corrente/profilo-default")
	public Object getProfiloDefault() throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		FoliageGrantedAuthority outVal = jwtAuth.getAccessToken().getProfiloDefault();
		return new ResponseEntity<>(outVal, HttpStatus.OK);
	}



	/////////////////////////////////////////////////////////////////////////////////////




	@PostMapping("/utenze/utenti")
	public Object getRicercaUtenti(
		@RequestBody RicercaUtenti parametri,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;

		if (authority.equals("AMMI") || authority.equals("RESP") || authority.equals("DIRI") || authority.equals("PROP") || authority.equals("PROF")) {
			return new ResponseEntity<>(dal.getListaUtenti(jwtAuth.getAccessToken().getIdUtente(), authority, authScope, parametri), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
	}



	@DeleteMapping("/utenze/utente/{username}/ruolo/{idProfilo}/{idEnte}")
	public Object revocaAssociazioneRuoloEnte(
		@PathVariable String username,
		@PathVariable Integer idProfilo,
		@PathVariable Integer idEnte,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@RequestBody String note
	) throws Exception {
		
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		boolean isOk = false;

		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		switch (authority) {
			case "RESP": {
				isOk = dal.verificaRuoloInEnte(idUtente, authority, authScope, idEnte);
			}; break;
			case "AMMI": {
				isOk = true;
			}; break;
		}
		if (isOk) {
			return new ResponseEntity<>(dal.revocaAssociazioneRuoloEnte(idUtente, username, idProfilo, idEnte, note), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}



	@GetMapping("/utenze/utente/{username}")
	public Object getInfoUtente(
		@PathVariable String username,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;

		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		if (dal.verificaAccessoAUtente(idUtente, authority, authScope, username)) {
			return new ResponseEntity<>(dal.getInfoUtente(username), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}


	@GetMapping("/utenze/utente/{username}/richieste")
	public Object getRichiesteUtente(
		@PathVariable String username,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		try {
			return new ResponseEntity<>(dal.getRichiesteUtente(idUtente, authority, authScope, username), HttpStatus.OK);
		}
		catch (FoliageAuthorizationException e) {
			return new ResponseEntity<>("Accesso Negato", HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping("/utenze/utente/{username}/ruolo/{idProfilo}/enti")
	public Object getEntiPerProfiloUtente(
		@PathVariable String username,
		@PathVariable Integer idProfilo,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		if (dal.verificaAccessoAUtente(idUtente, authority, authScope, username)) {
			return this.dal.getEntiPerRuoloUtente(idUtente, authority, authScope, username, idProfilo);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping("/utenze/utente/{username}/ruoli")
	public Object getRuoliUtente(
		@PathVariable String username,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		if (authority.equals("RESP") || authority.equals("AMMI")) {
			return new ResponseEntity<>(dal.getRuoliUtente(username), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping("/utenze/utente/{username}/richieste/{idRichiesta}")
	public Object getRichiestaUtente(
		@PathVariable String username,
		@PathVariable Integer idRichiesta,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		if (authority.equals("RESP") || authority.equals("AMMI")) {
			return this.dal.getRichiestaUtente(username, idRichiesta, idUtente, authority, authScope);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
	}
	@GetMapping("/utenze/richieste/{idRichiesta}")
	public Object getRichiesta(
		@PathVariable Integer idRichiesta,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		if (authority.equals("RESP") || authority.equals("AMMI")) {
			try {
				return this.dal.getRichiesta(idRichiesta, idUtente, authority, authScope);
			}
			catch (FoliageAuthorizationException e) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);	
			}
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping("/utenze/profili-territoriali")
	public Object getProfiliTerritoriali() throws Exception {
		return new ResponseEntity<>(dal.getProfiliTerritoriali(), HttpStatus.OK);
	}

	@GetMapping("/utenze/profili-richiesta")
	public Object getProfiliRichiesta() throws Exception {
		return new ResponseEntity<>(dal.getProfiliRichiesta(), HttpStatus.OK);
	}

	@GetMapping("/utenze/lista-caserme")
	public Object getCaserme() throws Exception {
		return new ResponseEntity<>(dal.getCaserme(), HttpStatus.OK);
	}

	@GetMapping("/utenze/lista-parchi")
	public Object getParchi() throws Exception {
		return new ResponseEntity<>(dal.getParchi(), HttpStatus.OK);
	}


	@GetMapping("/utenze/profili")
	public Object getProfili() throws Exception {
		
		// Object[] arr = new Object[] {
		// 	new Object() {
		// 		public int idProfilo = 1;
		// 		public String codProfilo = "PROPRIETARIO";
		// 		public String descProfilo = "Proprietario e gestore forestale";
		// 	},
		// 	new Object() {
		// 		public int idProfilo = 2;
		// 		public String codProfilo = "PROFESSIONISTA";
		// 		public String descProfilo = "Professionista";
		// 	},
		// 	new Object() {
		// 		public int idProfilo = 6;
		// 		public String codProfilo = "ISTRUTTORE";
		// 		public String descProfilo = "Istruttore";
		// 	},
		// 	new Object() {
		// 		public int idProfilo = 7;
		// 		public String codProfilo = "DIRIGENTE";
		// 		public String descProfilo = "Dirigente";
		// 	},
		// 	new Object() {
		// 		public int idProfilo = 8;
		// 		public String codProfilo = "SPORTELLO";
		// 		public String descProfilo = "Funzionario di sportello";
		// 	},
		// 	new Object() {
		// 		public int idProfilo = 9;
		// 		public String codProfilo = "CARABINIERE";
		// 		public String descProfilo = "Carabiniere forestale, guardia parco";
		// 	},
		// 	new Object() {
		// 		public int idProfilo = 12;
		// 		public String codProfilo = "AMMINISTRATORE";
		// 		public String descProfilo = "Amministratore di sistema";
		// 	},
		// 	new Object() {
		// 		public int idProfilo = 11;
		// 		public String codProfilo = "RESPONSABILE";
		// 		public String descProfilo = "Responsabile del servizio";
		// 	}
		// };

		return new ResponseEntity<>(dal.getProfili(), HttpStatus.OK);
		//return new ResponseEntity<>(arr, HttpStatus.OK);
	}

	@PostMapping("/utenze/richieste")
	public Object getRichieste(
		@RequestBody RicercaUtenti parametri,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		return new ResponseEntity<>(dal.getRichiesteProfili(idUtente, authority, authScope, parametri), HttpStatus.OK);
	}


	// @GetMapping("/utenze/richieste/responsabile")
	// public Object getRichiesteResponsabile() throws Exception {
	//     Authentication a = SecurityContextHolder.getContext().getAuthentication();
	//     JwtAuthentication jwtAuth = (JwtAuthentication)a;
	//      return new ResponseEntity<>(dal.getRichiesteResponsabile(jwtAuth.getUsername()), HttpStatus.OK);
	// }

	@PostMapping(path = "/utenze/richieste/{idRichiesta}", consumes = MediaType.APPLICATION_JSON)
	public Object valutaRichiestaProfilo(
		@PathVariable Integer idRichiesta,
		@RequestBody ValutazioneRichiestaProfilo valutazione,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;

		try {
			return new ResponseEntity<>(dal.valutaRichiestaProfilo(jwtAuth.getAccessToken().getIdUtente(), authority, authScope, idRichiesta, valutazione), HttpStatus.OK);
		}
		catch (FoliageAuthorizationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);	
		}
	}

	////////////////////////////////////

	

	@GetMapping("/legacy/getRegioni")
	public Object getRegioni() throws Exception {
		return new ResponseEntity<>(dal.getRegioni(), HttpStatus.OK);
	}
	

	@GetMapping("/legacy/getProvince")
	public Object getProvince(@RequestParam String codiRegi) throws SQLException, Exception {
		return new ResponseEntity<>(dal.getProvincie(codiRegi), HttpStatus.OK);
		//return null;
	}
	@GetMapping("/legacy/getProvincie")
	public Object getProvincie(@RequestParam String codiRegi) throws SQLException, Exception {
		return new ResponseEntity<>(dal.getProvincie(codiRegi), HttpStatus.OK);
		//return null;
	}

	@GetMapping("/istanze/{codIstanza}/provincie-istanza")
	public Object getProvincieIstanza(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws SQLException, Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		String codFiscale = jwtAuth.getAccessToken().getCodiceFiscale();
		AbilitazioniIstanza abil = dal.getAbilitazioniIstanza(idUtente, codFiscale, codIstanza, authority, authScope);
		if (abil.access || abil.compilazione || abil.consultazione) {
			return new ResponseEntity<>(dal.getProvincieIstanza(codIstanza), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping("/istanze/{codIstanza}/comuni-istanza/{idProvincia}")
	public Object getComuniIstanza(
		@PathVariable String codIstanza,
		@PathVariable Integer idProvincia,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws SQLException, Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		String codFiscale = jwtAuth.getAccessToken().getCodiceFiscale();
		AbilitazioniIstanza abil = dal.getAbilitazioniIstanza(idUtente, codFiscale, codIstanza, authority, authScope);
		if (abil.access || abil.compilazione || abil.consultazione) {
			return new ResponseEntity<>(dal.getComuniIstanza(codIstanza, idProvincia), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping("/istanze/{codIstanza}/interventi-consentiti")
	public Object getDettagliIterventiConsentiti(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws SQLException, Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		String codFiscale = jwtAuth.getAccessToken().getCodiceFiscale();
		AbilitazioniIstanza abil = dal.getAbilitazioniIstanza(idUtente, codFiscale, codIstanza, authority, authScope);
		if (abil.access || abil.compilazione || abil.consultazione) {
			return new ResponseEntity<>(dal.getDettagliIterventiConsentiti(codIstanza), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
	}


	// @GetMapping("/istanze/{codIstanza}/tipo-intervento/{codUso}")
	// public Object getTipoInterventoTest(
	// 	@PathVariable String codIstanza,
	// 	@PathVariable String codUso
	// ) throws SQLException, Exception {
	// 	Object[] arr = new Object[]{
	// 			new Object() {
	// 				public String codIntervento = codUso + "_1";
	// 				public String tipoIntervento = "Tipo int 1";
	// 				public String rifNorma = "rif norma 1";
	// 				public String campoDaGestire = "superficie-ha";
	// 			},

	// 			new Object() {
	// 				public String codIntervento = codUso + "_2";
	// 				public String tipoIntervento = "Tipo int 2";
	// 				public String rifNorma = "rif norma 2";
	// 				public String campoDaGestire = "numero-esemplari";
	// 			},

	// 			new Object() {
	// 				public String codIntervento = codUso + "_3";
	// 				public String tipoIntervento = "Tipo int 3";
	// 				public String rifNorma = "rif norma 3";
	// 				public String campoDaGestire = "numero-siti";
	// 			},

	// 			new Object() {
	// 				public String codIntervento = codUso + "_4";
	// 				public String tipoIntervento = "Tipo int 4";
	// 				public String rifNorma = "rif norma 4";
	// 				public String campoDaGestire = "superficie-mq";
	// 			}
	// 	};

	// 	return new ResponseEntity<>(arr, HttpStatus.OK);
	// }

	@GetMapping(path = "/istanze/{codIstanza}/actions")
	public Object getAbilitazioniIstanza(
			@PathVariable String codIstanza,
			@QueryParam("authority") String authority,
			@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;

		// Object mockedResponse = new Object() {
		// 	public boolean consultazione = true;
		// 	public boolean compilazione = true;
		// 	public boolean invio = true;
		// 	public boolean assegna_istruttore = true;
		// 	public boolean valutazione = true;
		// 	public boolean cambia_gestore = true;
		// 	public boolean passaggio_gestore = true;
		// 	public boolean richiesta_proroga = true;
		// 	public boolean inizio_lavori = true;
		// 	public boolean fine_lavori = true;
		// };

		// return new ResponseEntity<>(mockedResponse, HttpStatus.OK);
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(jwtAuth.getAccessToken().getIdUtente(), jwtAuth.getAccessToken().getCodiceFiscale(), codIstanza, authority, authScope);
		if (outVal.access) {
			return new ResponseEntity<>(outVal, HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}
	
	@PostMapping(path = "/istanze/{codIstanza}/revoca")
	public Object revocaIstanza(
			@PathVariable String codIstanza,
			@QueryParam("authority") String authority,
			@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		if (outVal.revoca_istruttore) {
			return new ResponseEntity<>(dal.revocaIstanza(idUtente, codIstanza), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
		
	}

	@PostMapping(path = "/istanze/{codIstanza}/trasforma")
	public Object trasformaIstanza(
			@PathVariable String codIstanza,
			@QueryParam("authority") String authority,
			@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		if (outVal.compilazione) {
			return new ResponseEntity<>(dal.trasformaIstanza(codIstanza), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	@PostMapping(path = "/istanze/{codIstanza}/invio")
	public Object invioIstanza(
			@PathVariable String codIstanza,
			@RequestBody DatiInvioIstanza datiInvio,
			@QueryParam("authority") String authority,
			@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		if (outVal.compilazione) {
			return new ResponseEntity<>(dal.invioIstanza(codIstanza, datiInvio), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}
	

	// @PutMapping(path="/istanze/{codIstanza}/modulo-istanza-firmato")
	// public Object invioModuloIstanzaFirmato(
	// 	@PathVariable String codIstanza,
	// 	@RequestBody Base64FormioFile[] file,
	// 	@QueryParam("authority") String authority,
	// 	@QueryParam("authScope") String authScope
	// )  throws Exception {
	// 	Authentication a = SecurityContextHolder.getContext().getAuthentication();
	// 	JwtAuthentication jwtAuth = (JwtAuthentication)a;
	// 	AccessToken token = jwtAuth.getAccessToken();
	// 	Integer idUtente = token.getIdUtente();
	// 	AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
	// 	if (outVal.upload_modulo_firmato ) {
	// 		return new ResponseEntity<>(dal.invioModuloIstanzaFirmato(codIstanza, file), HttpStatus.OK);
	// 	}
	// 	else {
	// 		return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
	// 	}
	// }
	

	@GetMapping(path = "/istanze/{codIstanza}/stazione-forestale")
	public Object getStazioniPerIstanza(
			@PathVariable String codIstanza
	) throws Exception {
		Object[] arr = new Object[]{
				new Object() {
					public String nome = "Altimetria (m s.l.m.)";
					public double minimo = 0.0;
					public double massimo = 0.0;
					public double media = 0.0;
				},
				new Object() {
					public String nome = "Pendenza  (%)";
					public double minimo = 0.0;
					public double massimo = 0.0;
					public double media = 0.0;
				},
		};

		return new ResponseEntity<>(arr, HttpStatus.OK);
	}

	@GetMapping(path = "/istanze/{codIstanza}/contiguita-tagli-boschivi")
	public Object getContiguitaTagli(
			@PathVariable String codIstanza
	) throws Exception {
		Object[] arr = new Object[]{
				new Object() {
					public String codIstanza = "cod 1";
					public double superficie = 9;
					public double sovrapposizione = 4;
				},
				new Object() {
					public String codIstanza = "cod 2";
					public double superficie = 44;
					public double sovrapposizione = 11;
				},
				new Object() {
					public String codIstanza = "cod 3";
					public double superficie = 50;
					public double sovrapposizione = 5;
				},
				new Object() {
					public String codIstanza = "cod 4";
					public double superficie = 10;
					public double sovrapposizione = 2;
				},
				new Object() {
					public String codIstanza = "cod 5";
					public double superficie = 40;
					public double sovrapposizione = 3;
				},
				new Object() {
					public String codIstanza = "cod 6";
					public double superficie = 50;
					public double sovrapposizione = 8;
				},
		};

		return new ResponseEntity<>(arr, HttpStatus.OK);
	}

	@GetMapping(path = "/istanze/{codIstanza}/inquadramento-vincolistica")
	public Object getInquadramentoVincolistica(
			@PathVariable String codIstanza,
			@QueryParam("authority") String authority,
			@QueryParam("authScope") String authScope
	) throws Exception {
		
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		if (outVal.compilazione || outVal.consultazione) {
			return new ResponseEntity<>(dal.getInquadramentoVincolistica(codIstanza), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping(path = "/istanze/sottocategorie")
	public Object getSottocategorie() throws Exception {
		return new ResponseEntity<>(dal.getSottocategorie(), HttpStatus.OK);
	}

	@GetMapping(path = "/istanze/{codIstanza}/dati-istruttoria")
	public Object getDatiIstruttoria(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception
	{
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		if (outVal.allega_documenti || outVal.consulta_valutazione || outVal.valutazione) {
			return new ResponseEntity<>(dal.getDatiIstruttoria(codIstanza), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	@PutMapping(path = "/istanze/{codIstanza}/dati-istruttoria")
	public Object setDatiIstruttoria(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@RequestBody DatiIstruttoria dati
		//@RequestBody FileValutazioneIstanza file
	) throws Exception
	{
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		if (outVal.allega_documenti || outVal.valutazione) {
			return new ResponseEntity<>(dal.setDatiIstruttoria(codIstanza, idUtente, authority, dati), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}
	@PutMapping(path = "/istanze/{codIstanza}/valuta")
	public Object valutazioneIstanza(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@RequestBody ValutazioneIstanza valutazione
	) throws Exception
	{
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		if (outVal.valutazione) {
			return new ResponseEntity<>(dal.valutazioneIstanza(codIstanza, idUtente, authority, valutazione), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	
	@PostMapping(path = "/istanze/{codIstanza}/comunica-inizio")
	public Object comunicaInizioLavori(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@RequestBody LocalDate dataInizio
	) throws Exception
	{
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		if (outVal.inizio_lavori) {
			return new ResponseEntity<>(dal.comunicaInizio(codIstanza, idUtente, dataInizio), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}
	
	@PostMapping(path = "/istanze/{codIstanza}/comunica-fine")
	public Object comunicaFineLavori(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@RequestBody LocalDate dataFine
	) throws Exception
	{
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		if (outVal.fine_lavori) {
			return new ResponseEntity<>(dal.comunicaFine(codIstanza, idUtente, dataFine), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}
	
	@PostMapping(path = "/istanze/{codIstanza}/proroga")
	public Object prorogaIstanza(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@RequestBody ProrogaIstanza body
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		if (outVal.richiesta_proroga) {
			return new ResponseEntity<>(dal.prorogaIstanza(codIstanza, idUtente, body), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping(path = "/ambiti-report-p4")
	public Object GetAmbitiReportP4(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		return dal.GetAmbitiReportP4(idUtente, authority, authScope);
	}

	//@GetMapping(path = "/report-p4/{idEnte}")
	@GetMapping(path = "/report-p4")
	public void GetReportP4(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		/*@PathVariable Integer idEnte,*/
		HttpServletResponse response
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		Workbook workbook = dal.GetReportP4(idUtente, authority, authScope/*, idEnte */);

		ServletOutputStream outputStream = response.getOutputStream();
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
	}

	@GetMapping(path = "/istanze/{codIstanza}/dati-modulistica")
	public Object getModulisticaIstanza(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		if (outVal.compilazione || outVal.consultazione) {
			return new ResponseEntity<>(dal.getModulisticaIstanza(codIstanza), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping(path = "/istanze/{codIstanza}/modulo-istruttoria")
	public void getModuloPdfIstruttoria(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		/*@PathVariable Integer idEnte,*/
		HttpServletResponse response
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		
		
		if (outVal.compilazione || outVal.consultazione) {
			//Object pdfStream = 
			
			ServletOutputStream outputStream = response.getOutputStream();
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			
			//dal.creaModuloPdfIstanza(codIstanza, outputStream);
			dal.getModuloPdfIstruttoria(codIstanza, outputStream);

			// pdfStream.write(outputStream);
			// pdfStream.flush();
			// pdfStream.close();
			outputStream.flush();
			outputStream.close();

		}
		else {
			response.setStatus(403);
		}
	}
	
	@GetMapping(path="/istanze/{codIstanza}/modulo-istanza-firmato")
	public Object getModuloPdfFirmatoIstanza(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		
		
		if (outVal.compilazione || outVal.consultazione) {
			return dal.getModuloPdfFirmatoIstanza(codIstanza);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping(path = "/istanze/{codIstanza}/modulo-istanza-non-firmato")
	public void getModuloPdfNonFirmatoIstanza(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		/*@PathVariable Integer idEnte,*/
		HttpServletResponse response
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		
		
		if (outVal.compilazione || outVal.consultazione) {
			//Object pdfStream = 
			
			ServletOutputStream outputStream = response.getOutputStream();
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			
			//dal.creaModuloPdfIstanza(codIstanza, outputStream);
			dal.getModuloPdfNonFirmatoIstanza(codIstanza, outputStream);

			// pdfStream.write(outputStream);
			// pdfStream.flush();
			// pdfStream.close();
			outputStream.flush();
			outputStream.close();

		}
		else {
			response.setStatus(403);
		}
	}

	@GetMapping(path = "/istanze/{codIstanza}/modulo-istanza")
	public void getModuloPdfIstanza(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		/*@PathVariable Integer idEnte,*/
		HttpServletResponse response
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		
		
		if (outVal.compilazione || outVal.consultazione) {
			//Object pdfStream = 
			
			ServletOutputStream outputStream = response.getOutputStream();
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			
			//dal.creaModuloPdfIstanza(codIstanza, outputStream);
			dal.getModuloPdfIstanza(codIstanza, outputStream);

			// pdfStream.write(outputStream);
			// pdfStream.flush();
			// pdfStream.close();
			outputStream.flush();
			outputStream.close();

		}
		else {
			response.setStatus(403);
		}
	}

	@GetMapping(path = "/istanze/{codIstanza}/bozza-modulo-istanza")
	public void getBozzaModuloPdfIstanza(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		/*@PathVariable Integer idEnte,*/
		HttpServletResponse response
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		
		
		if (outVal.compilazione || outVal.consultazione) {
			//Object pdfStream = 
			
			ServletOutputStream outputStream = response.getOutputStream();
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			
			dal.creaModuloPdfIstanza(codIstanza, outputStream);
			//dal.getModuloPdfIstanza(codIstanza, outputStream);

			// pdfStream.write(outputStream);
			// pdfStream.flush();
			// pdfStream.close();
			outputStream.flush();
			outputStream.close();

		}
		else {
			response.setStatus(403);
		}
	}

	
	@GetMapping(path = "/istanze/{codIstanza}/modulo-istanza-on-fly")
	public void getBozzaModuloPdfIstanzaOnFly(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		/*@PathVariable Integer idEnte,*/
		HttpServletResponse response
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		
		
		if (outVal.compilazione || outVal.consultazione) {
			//Object pdfStream = 
			
			ServletOutputStream outputStream = response.getOutputStream();
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			
			dal.creaModuloPdfIstanza(codIstanza, outputStream, false);
			//dal.getModuloPdfIstanza(codIstanza, outputStream);

			// pdfStream.write(outputStream);
			// pdfStream.flush();
			// pdfStream.close();
			outputStream.flush();
			outputStream.close();

		}
		else {
			response.setStatus(403);
		}
	}

	
	@GetMapping(path = "/istanze/{codIstanza}/bozza-modulo-istruttoria")
	public void getBozzaModuloPdfIstruttoria(
		@PathVariable String codIstanza,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		/*@PathVariable Integer idEnte,*/
		HttpServletResponse response
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		
		
		if (outVal.valutazione) {
			//Object pdfStream = 
			
			ServletOutputStream outputStream = response.getOutputStream();
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			
			dal.creaModuloPdfIstruttoria(codIstanza, outputStream);
			//dal.getModuloPdfIstanza(codIstanza, outputStream);

			// pdfStream.write(outputStream);
			// pdfStream.flush();
			// pdfStream.close();
			outputStream.flush();
			outputStream.close();

		}
		else {
			response.setStatus(403);
		}
	}

	@GetMapping(path = "/istanze/{codIstanza}/tavole/idxTavola")
	public void getTavolaIstanza(
		@PathVariable String codIstanza,
		@PathVariable Integer idxTavola,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		HttpServletResponse response
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		
		
		if (outVal.consultazione) {
			//Object pdfStream = 
			
			// ServletOutputStream outputStream = response.getOutputStream();
			// response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			// dal.creaModuloPdfIstanza(codIstanza, outputStream);
			// // pdfStream.write(outputStream);
			// // pdfStream.flush();
			// // pdfStream.close();
			// outputStream.flush();
			// outputStream.close();

		}
		else {
			response.setStatus(403);
		}
	}
	
	@PutMapping(path = "/istanze/{codIstanza}/tavole/{idxTavola}")
	public Object salvaTavolaIstanza(
		@PathVariable String codIstanza,
		@PathVariable Integer idxTavola,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@RequestBody Base64FormioFile[] file
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		if (outVal.compilazione) {
			return new ResponseEntity<>(dal.salvaTavolaIstanza(codIstanza, idxTavola, file, idUtente), HttpStatus.OK);
			//return new ResponseEntity<>("Ok", HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}
	
	@DeleteMapping(path = "/istanze/{codIstanza}/tavole/{idxTavola}")
	public Object deleteTavolaIstanza(
		@PathVariable String codIstanza,
		@PathVariable Integer idxTavola,
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		AbilitazioniIstanza outVal = dal.getAbilitazioniIstanza(idUtente, token.getCodiceFiscale(), codIstanza, authority, authScope);
		if (outVal.compilazione) {
			dal.eliminaTavolaIstanza(codIstanza, idxTavola);
			return new ResponseEntity<>("ok", HttpStatus.OK);
			//return new ResponseEntity<>("Ok", HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping(path = "/report-disponibili")
	public Object getReportDisponibili(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();
		
		if ("AMMI".equals(authority) || !"GENERICO".equals(authScope)) {
			return new ResponseEntity<>(
				dal.getReportDisponibili(authority, authScope),
				HttpStatus.OK
			);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping(path = "/monitoraggio")
	public Object getElaborazioniMonitoraggio(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		if ("AMMI".equals(authority)) {
			return new ResponseEntity<>(
				dal.getElaborazioniMonitoraggio(),
				HttpStatus.OK
			);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	
	@GetMapping(path = "/tipo-elaborazioni-governance")
	public Object getTipoElaborazioniGovernance() throws Exception {
		return new ResponseEntity<>(
			dal.getTipoElaborazioniGovernance(),
			HttpStatus.OK
		);
	}

	@GetMapping(path = "/governance")
	public Object getElaborazioniGovernance(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception {
		if ("AMMI".equals(authority)) {
			return new ResponseEntity<>(
				dal.getElaborazioniGovernance(),
				HttpStatus.OK
			);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	@PutMapping(path = "/governance")
	public Object salvaElaborazioneGovernance(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@RequestBody ElaborazioneGovernance elaborazione
	) throws Exception {
		if ("AMMI".equals(authority)) {
			Authentication a = SecurityContextHolder.getContext().getAuthentication();
			JwtAuthentication jwtAuth = (JwtAuthentication)a;
			AccessToken token = jwtAuth.getAccessToken();
			Integer idUtente = token.getIdUtente();
			dal.salvaElaborazioneGovernance(elaborazione, idUtente);
			return new ResponseEntity<>(
				"OK",
				HttpStatus.OK
			);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}


	@PutMapping(path = "/monitoraggio")
	public Object salvaElaborazioneMonitoraggio(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@RequestBody ElaborazioneMonitoraggio elaborazione
	) throws Exception {
		if ("AMMI".equals(authority)) {
			Authentication a = SecurityContextHolder.getContext().getAuthentication();
			JwtAuthentication jwtAuth = (JwtAuthentication)a;
			AccessToken token = jwtAuth.getAccessToken();
			Integer idUtente = token.getIdUtente();
			dal.salvaElaborazioneMonitoraggio(elaborazione, idUtente);
			return new ResponseEntity<>(
				"OK",
				HttpStatus.OK
			);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping(path = "/monitoraggio/{idRichiesta}")
	public Object getElaborazioneMonitoraggio(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable Integer idRichiesta
	) throws Exception {
		if ("AMMI".equals(authority)) {
			return new ResponseEntity<>(
				dal.getElaborazioneMonitoraggio(idRichiesta),
				HttpStatus.OK
			);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping(path = "/governance/{idRichiesta}")
	public Object getElaborazioneGovernance(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable Integer idRichiesta
	) throws Exception {
		if ("AMMI".equals(authority)) {
			return new ResponseEntity<>(
				dal.getElaborazioneGovernance(idRichiesta),
				HttpStatus.OK
			);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}
	@DeleteMapping(path = "/governance/{idRichiesta}")
	public Object eliminaElaborazioneGovernance(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable Integer idRichiesta
	) throws Exception {
		if ("AMMI".equals(authority)) {
			dal.eliminaElaborazioneGovernance(idRichiesta);
			return new ResponseEntity<>(
				"OK",
				HttpStatus.OK
			);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	@DeleteMapping(path = "/monitoraggio/{idRichiesta}")
	public Object eliminaElaborazioneMonitoraggio(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable Integer idRichiesta
	) throws Exception {
		if ("AMMI".equals(authority)) {
			dal.eliminaElaborazioneMonitoraggio(idRichiesta);
			return new ResponseEntity<>(
				"OK",
				HttpStatus.OK
			);
		}
		else {
			return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		}
	}

	//@GetMapping(path = "/report-p4/{idEnte}")
	@GetMapping(path = "/report/{codReport}/{formato}/{data}")
	public void GetReport(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable String codReport,
		@PathVariable String formato,
		@PathVariable LocalDate data,
		HttpServletResponse response
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken token = jwtAuth.getAccessToken();
		Integer idUtente = token.getIdUtente();

		response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		ServletOutputStream outputStream = response.getOutputStream();
		dal.buildReport(codReport, formato, data, idUtente, authority, authScope, outputStream);
		outputStream.close();

		// if ("AMMI".equals(authority) || !"GENERICO".equals(authScope)) {
			
		// }
		// else {
		// 	return new ResponseEntity<>("Accesso negato", HttpStatus.FORBIDDEN);
		// }


	}

	
}