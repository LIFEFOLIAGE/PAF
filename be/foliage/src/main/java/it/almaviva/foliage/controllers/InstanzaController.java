// /*
//  * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
//  * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
//  */
// package it.almaviva.foliage.controllers;

// //import com.fasterxml.jackson.databind.node.ObjectNode;
// import io.micrometer.core.instrument.util.IOUtils;
// import it.almaviva.foliage.FoliageException;
// import it.almaviva.foliage.authentication.AccessToken;
// import it.almaviva.foliage.authentication.FoliageGrantedAuthority;
// import it.almaviva.foliage.authentication.JwtAuthentication;
// import it.almaviva.foliage.bean.ChiaviRicercaIstanza;
// import it.almaviva.foliage.bean.CreazioneIstanza;
// import it.almaviva.foliage.services.IstanzaDal;
// import jakarta.servlet.http.HttpServletRequest;
// //import it.almaviva.foliage.services.ResultSetSerializer;
// import jakarta.ws.rs.Produces;

// import java.io.IOException;
// import java.lang.reflect.Array;
// import java.nio.charset.StandardCharsets;
// import java.sql.SQLException;
// import java.util.Collection;
// import java.util.Collections;
// import java.util.Enumeration;
// import java.util.HashMap;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;

// import org.springframework.beans.factory.annotation.Autowired;
// //import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// //import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.multipart.MultipartFile;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.web.csrf.CsrfToken;

// /**
//  *
//  * @author A.Rossi
//  */
// @RestController
// @RequestMapping("/api/istanze")
// public class InstanzaController {
	
// 	@Autowired
// 	private IstanzaDal dal;

// 	public InstanzaController() throws SQLException {
// 	}
	
// 	@GetMapping("/{codIstanza}")
// 	public Object apriIstanza(@PathVariable String codIstanza) throws SQLException, FoliageException {
// 		return new ResponseEntity<>(dal.apriIstanza(codIstanza), HttpStatus.OK);
// 	}

// 	@GetMapping("/listaIstanze")
// 	public Object listaIstanze() throws Exception {
// 		return new ResponseEntity<>(dal.getListaIstanze(), HttpStatus.OK);
		
// 		//return ResultSetSerializer.ToObjectNode(dal.getListaIstanze());
// 		//return service.find(id);
// 	}
	
// 	@GetMapping("/{codIstanza}/listaParticelle")
// 	public Object getListaParticelleIstanza(@PathVariable String codIstanza) throws Exception {
// 		return new ResponseEntity<>(dal.getListaParticelleIstanza(codIstanza), HttpStatus.OK);
		
// 		//return ResultSetSerializer.ToObjectNode(dal.getListaIstanze());
// 		//return service.find(id);
// 	}
	
	
// 	@PostMapping("/{codIstanza}/salvaIstanza")
// 	public String salvaIstanza(@PathVariable String codIstanza, HttpServletRequest request) throws Exception {
// 		final String json = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
// 		log.debug(json);
// 		return json;
// 	}

// 	@PostMapping("/profilo/{tipoProfilo}")
// 	public Object getIstanze(@PathVariable String tipoProfilo, @RequestBody ChiaviRicercaIstanza parametri) throws SQLException, FoliageException {
// 		Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;

// 		String codFiscaleUtente = jwtAuth.getAccessToken().getCodiceFiscale();
// 		String username = jwtAuth.getAccessToken().getUsername();
// 		Collection<GrantedAuthority> authorities = jwtAuth.getAccessToken().getAuthorities();
		
// 		Stream<String> stream = authorities.stream().map(authority -> authority.getAuthority());
// 		String res = stream.collect(Collectors.joining(","));

// 		GrantedAuthority userProf = authorities.stream()
//   			.filter(authority -> tipoProfilo.equals(authority.getAuthority()))
// 			.findAny()
//   			.orElse(null);

// 		if (userProf != null) {
// 			return new ResponseEntity<>(
// 				dal.ricercaInstanze(
// 					tipoProfilo,
// 					username, codFiscaleUtente,
// 					parametri
// 				),
// 				HttpStatus.OK
// 			);
// 		}
// 		else {	
// 			return new ResponseEntity<>(
// 				null,
// 				HttpStatus.FORBIDDEN
// 			);
// 		}
// 	}

// 	@GetMapping("/lista-stati-istanza")
// 	public Object getListaStatiIstanza(HttpServletRequest request) {
// 		return new Object[] {
// 			new Object() {
// 				public int idStato = 0;
// 				public String codStato = "COMPILAZIONE";
// 				public String descStato = "In Compilazione";
// 			},
// 			new Object() {
// 				public int idStato = 1;
// 				public String codStato = "PRESENTATA";
// 				public String descStato = "Presentata";
// 			},
// 			new Object() {
// 				public int idStato = 2;
// 				public String codStato = "ASSEGNATA";
// 				public String descStato = "Assegnata";
// 			},
// 			new Object() {
// 				public int idStato = 2;
// 				public String codStato = "ISTRUTTORIA";
// 				public String descStato = "In Approvazione";
// 			},
// 			new Object() {
// 				public int idStato = 3;
// 				public String codStato = "APPROVATA";
// 				public String descStato = "Approvata";
// 			},
// 			new Object() {
// 				public int idStato = 4;
// 				public String codStato = "RESPINTA";
// 				public String descStato = "Respinta";
// 			}
// 		};
// 	}

// 	@GetMapping("/lista-stato-lavori")
// 	public Object getListaStatoLavori(HttpServletRequest request) {
// 		return new Object[] {
// 			new Object() {
// 				public int idStato = 0;
// 				public String codStato = "ASSENTI";
// 				public String descStato = "Non Pianificati";
// 			},
// 			new Object() {
// 				public int idStato = 1;
// 				public String codStato = "PIANIFICATI";
// 				public String descStato = "Pianificati";
// 			},
// 			new Object() {
// 				public int idStato = 2;
// 				public String codStato = "INIZIATI";
// 				public String descStato = "Iniziati";
// 			},
// 			new Object() {
// 				public int idStato = 3;
// 				public String codStato = "TERMINATI";
// 				public String descStato = "Terminati";
// 			},
// 			new Object() {
// 				public int idStato = 4;
// 				public String codStato = "RESPINTA";
// 				public String descStato = "Respinta";
// 			}
// 		};
// 	}

// 	@GetMapping("/lista-qualificazioni-proprietario")
// 	public Object getListaQualificazioniProprietario(HttpServletRequest request) {
// 		return new Object[] {
// 			new Object() {
// 				public int idQualificazione = 0;
// 				public String codQualificazione = "PROPRIETARIO";
// 				public String descQualificazione = "Proprietario";
// 			},
// 			new Object() {
// 				public int idQualificazione = 1;
// 				public String codQualificazione = "COMPROPRIETARIO";
// 				public String descQualificazione = "Comproprietario";
// 			},
// 			new Object() {
// 				public int idQualificazione = 2;
// 				public String codQualificazione = "AFFITTUARIO";
// 				public String descQualificazione = "Affittuario";
// 			},
// 			new Object() {
// 				public int idQualificazione = 3;
// 				public String codQualificazione = "RAPPRESENTANTE";
// 				public String descQualificazione = "Rappresentante legale";
// 			},
// 			new Object() {
// 				public int idQualificazione = 4;
// 				public String codQualificazione = "COMODATO";
// 				public String descQualificazione = "Titolare di comodato d'uso";
// 			},
// 			new Object() {
// 				public int idQualificazione = 5;
// 				public String codQualificazione = "ALTRO";
// 				public String descQualificazione = "Altro titolo di possesso del soprasuolo";
// 			},
// 			new Object() {
// 				public int idQualificazione = 6;
// 				public String codQualificazione = "DELEGATO";
// 				public String descQualificazione = "Delegato del proprietario";
// 			}
// 		};
// 	}

// 	@GetMapping("/csrs")
// 	public Object getCsrsToken(HttpServletRequest request) {
// 		Object tok = request.getAttribute(CsrfToken.class.getName());
// 		if (tok == null) {
// 			return new Object() {
// 				public String headerName = null;
// 				public String token = null;
// 			};
// 		}
// 		else {
// 			return tok;
// 		}
// 	}


// 	@PostMapping("/nuova-istanza-proprietario")
// 	public Object nuovaIstanzaProprietario(@RequestBody CreazioneIstanza parametri) throws SQLException {
// 		Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;
// 		AccessToken token = jwtAuth.getAccessToken();
// 		//username = jwtAuth.getUsername();

// 		return new ResponseEntity<>(
// 			dal.creaIstanzaProprietario( 
// 					parametri.getTipoInsta(), parametri.getSottotipoInsta(),
// 					parametri.getTipoProprieta(), parametri.getTipoNaturaProprieta(),
// 					parametri.getNomeIsta(),
// 					token.getUsername(), token.getSurname(), token.getName(), token.getGender(),
// 					token.getBirthDate(), token.getBirthPlace()
// 				), 
// 				HttpStatus.OK
// 			);
// 	}

	
	
// 	@GetMapping("/regione-host")
// 	public Object GetRegioneHost() throws SQLException, Exception {
// 		return new ResponseEntity<>(dal.GetRegioneHost(), HttpStatus.OK);
// 	}

// 	@GetMapping("/provincie-host")
// 	public Object GetProvincieRegioneHost() throws SQLException, Exception {
// 		return new ResponseEntity<>(dal.GetProvincieRegioneHost(), HttpStatus.OK);
// 	}
	
// 	@GetMapping("/comuni/{idProvincia}")
// 	public Object GetComuni(@PathVariable Integer idProvincia) throws SQLException, Exception {
// 		return new ResponseEntity<>(dal.GetComuniProvincia(idProvincia), HttpStatus.OK);
// 	}

// 	@GetMapping("/info-ente/{idEnte}") 
// 	public Object GetInfoEnte(@PathVariable Integer idEnte) throws SQLException, Exception {
// 		return new ResponseEntity<>(dal.GetInfoEnte(idEnte), HttpStatus.OK);
// 	}

//     @PostMapping("/upload/{codIstanza}/{tipoFile}")
//     public Object upload(
// 		@PathVariable String codIstanza,
// 		@PathVariable String tipoFile,
// 		@RequestParam("file") MultipartFile file
// 	) throws IOException {
//         byte[] bytes;

//         if (!file.isEmpty()) {
//              bytes = file.getBytes();
//             //store file in storage
//         }

//         log.debug(String.format("receive %s come %s dell'istanza %s", file.getOriginalFilename(), tipoFile, codIstanza));
// 		return new ResponseEntity<>(null, HttpStatus.OK);
//     }
// }
