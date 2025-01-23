// /*
//  * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
//  * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
//  */
// package it.almaviva.foliage.controllers;

// import it.almaviva.foliage.authentication.JwtAuthentication;
// import it.almaviva.foliage.bean.RichiestaProfilo;
// import it.almaviva.foliage.bean.ValutazioneRichiestaProfilo;
// import it.almaviva.foliage.legacy.bean.RicercaUtenti;
// import it.almaviva.foliage.services.UtenzaDal;
// import jakarta.ws.rs.core.MediaType;

// import java.sql.SQLException;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// /**
//  *
//  * @author A.Rossi
//  */
// @RestController
// @RequestMapping("/api/utenze")
// public class UtenzaController {

//     @Autowired
//     private UtenzaDal dal;

//     public UtenzaController() throws SQLException {
//     }
    
//     // @GetMapping("/utenti")
//     // public Object getListaUtenti() throws Exception {
//     //     return new ResponseEntity<>(dal.getListaUtenti(null), HttpStatus.OK);
//     // }

//     @PostMapping("/utenti")
//     public Object getRicercaUtenti(@RequestBody RicercaUtenti parametri) throws Exception {
//         return new ResponseEntity<>(dal.getListaUtenti(parametri), HttpStatus.OK);
//     }


//     @DeleteMapping("/utente/{username}/ruolo/{idProfilo}/{idEnte}")
//     public Object revocaAssociazioneRuoloEnte(@PathVariable String username, @PathVariable Integer idProfilo, @PathVariable Integer idEnte, @RequestBody String note) throws Exception {
//         Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;
//         //return new ResponseEntity<>(dal.getRichiesteUtente(jwtAuth.getAccessToken().getIdUtente()), HttpStatus.OK);
//         return new ResponseEntity<>(dal.revocaAssociazioneRuoloEnte(jwtAuth.getAccessToken().getIdUtente(), username, idProfilo, idEnte, note), HttpStatus.OK);
//     }

//     @GetMapping("/utente/{username}")
//     public Object read(@PathVariable String username) throws Exception {
//         return this.dal.getInfoUtente(username);
//     }

    
//     @GetMapping("/utente/{username}/richieste")
//     public Object getRichiesteUtente(@PathVariable String username) throws Exception {
//         return this.dal.getRichiesteUtente(username);
//     }

//     @GetMapping("/utente/{username}/ruolo/{idProfilo}/enti")
//     public Object getEntiPerProfiloUtente(@PathVariable String username, @PathVariable Integer idProfilo) throws Exception {
//         return this.dal.getEntiPerRuoloUtente(username, idProfilo);
//     }

//     @GetMapping("/utente/{username}/ruoli")
//     public Object getRuoliUtente(@PathVariable String username) throws Exception {
//         return new ResponseEntity<>(dal.getRuoliUtente(username), HttpStatus.OK);
//     }

//     @GetMapping("/utente/{username}/richieste/{idRichiesta}")
//     public Object getRichiestaUtente(@PathVariable String username, @PathVariable Integer idRichiesta) throws Exception {
//         return this.dal.getRichiestaUtente(username, idRichiesta);
//     }
//     @GetMapping("/richieste/{idRichiesta}")
//     public Object getRichiesta(@PathVariable Integer idRichiesta) throws Exception {
//         return this.dal.getRichiesta(idRichiesta);
//     }
    
//     // @GetMapping("/corrente")
//     // public Object readCurrent() throws Exception {
//     //     Authentication a = SecurityContextHolder.getContext().getAuthentication();
//     //     JwtAuthentication jwtAuth = (JwtAuthentication)a;
//     //     return this.dal.getInfoUtente(jwtAuth.getUsername());
//     // }
    
//     // @GetMapping("/corrente/profili")
//     // public Object getRuoliUtenteCorrente() throws Exception {
//     //     Authentication a = SecurityContextHolder.getContext().getAuthentication();
//     //     JwtAuthentication jwtAuth = (JwtAuthentication)a;
//     //     return new ResponseEntity<>(dal.getRuoliUtente(jwtAuth.getUsername()), HttpStatus.OK);
//     // }
    
    
//     // @GetMapping("/corrente/richieste")
//     // public Object getRichiesteUtenteCorrente() throws Exception {
//     //     Authentication a = SecurityContextHolder.getContext().getAuthentication();
//     //     JwtAuthentication jwtAuth = (JwtAuthentication)a;
//     //     //return new ResponseEntity<>(dal.getRichiesteUtente(jwtAuth.getAccessToken().getIdUtente()), HttpStatus.OK);
//     //     return new ResponseEntity<>(dal.getRichiesteUtente(jwtAuth.getAccessToken().getIdUtente()), HttpStatus.OK);
//     // }
    
//     // @PostMapping(path = "/corrente/accettazione-privacy")
//     // public Object accettazionePrivacy() throws Exception {
//     //     Authentication a = SecurityContextHolder.getContext().getAuthentication();
//     //     JwtAuthentication jwtAuth = (JwtAuthentication)a;
//     //     return new ResponseEntity<>(dal.effettuaAccettazionePrivacy(jwtAuth.getUsername()), HttpStatus.OK);
//     // }

//     // @PostMapping(path = "/corrente/nuova-richiesta-profilo", consumes = MediaType.APPLICATION_JSON)
//     // public Object nuovaRichiestaProfilo(@RequestBody RichiestaProfilo richiesta) throws Exception {
//     //     Authentication a = SecurityContextHolder.getContext().getAuthentication();
//     //     JwtAuthentication jwtAuth = (JwtAuthentication)a;

//     //     return new ResponseEntity<>(dal.nuovaRichiestaProfilo(jwtAuth.getAccessToken().getIdUtente(), richiesta), HttpStatus.OK);
//     // }

//     @GetMapping("/profili-territoriali")
//     public Object getProfiliTerritoriali() throws Exception {
//         return new ResponseEntity<>(dal.getProfiliTerritoriali(), HttpStatus.OK);
//     }

//     @GetMapping("/profili-richiesta")
//     public Object getProfiliRichiesta() throws Exception {
//         return new ResponseEntity<>(dal.getProfiliRichiesta(), HttpStatus.OK);
//     }
 
//     @GetMapping("/lista-caserme")
//     public Object getCaserme() throws Exception {
//         return new ResponseEntity<>(dal.getCaserme(), HttpStatus.OK);
//     }
    
//     @GetMapping("/lista-parchi")
//     public Object getParchi() throws Exception {
//         return new ResponseEntity<>(dal.getParchi(), HttpStatus.OK);
//     }
       


//     // @GetMapping("/corrente/profili-selezionabili")
//     // public Object getProfiliSelezionabili() throws Exception {
//     //     Authentication a = SecurityContextHolder.getContext().getAuthentication();
//     //     JwtAuthentication jwtAuth = (JwtAuthentication)a;

//     //     return new ResponseEntity<>(jwtAuth.getAccessToken().getAuthorities(), HttpStatus.OK);
//     // }

//     // @GetMapping("/corrente/profilo-default")
//     // public Object getProfiloDefault() throws Exception {
//     //     Authentication a = SecurityContextHolder.getContext().getAuthentication();
//     //     JwtAuthentication jwtAuth = (JwtAuthentication)a;

//     //     return new ResponseEntity<>(jwtAuth.getAccessToken().getProfiloDefault(), HttpStatus.OK);
//     // }


//     @GetMapping("/profili")
//     public Object getProfili() throws Exception {
        
// 		Object[] arr = new Object[] {
// 			new Object() {
// 				public int idProfilo = 1;
// 				public String codProfilo = "PROPRIETARIO";
// 				public String descProfilo = "Proprietario e gestore forestale";
// 			},
// 			new Object() {
// 				public int idProfilo = 2;
// 				public String codProfilo = "PROFESSIONISTA";
// 				public String descProfilo = "Professionista";
// 			},
// 			new Object() {
// 				public int idProfilo = 6;
// 				public String codProfilo = "ISTRUTTORE";
// 				public String descProfilo = "Istruttore";
// 			},
// 			new Object() {
// 				public int idProfilo = 7;
// 				public String codProfilo = "DIRIGENTE";
// 				public String descProfilo = "Dirigente";
// 			},
// 			new Object() {
// 				public int idProfilo = 8;
// 				public String codProfilo = "SPORTELLO";
// 				public String descProfilo = "Funzionario di sportello";
// 			},
// 			new Object() {
// 				public int idProfilo = 9;
// 				public String codProfilo = "CARABINIERE";
// 				public String descProfilo = "Carabiniere forestale, guardia parco";
// 			},
// 			new Object() {
// 				public int idProfilo = 12;
// 				public String codProfilo = "AMMINISTRATORE";
// 				public String descProfilo = "Amministratore di sistema";
// 			},
// 			new Object() {
// 				public int idProfilo = 11;
// 				public String codProfilo = "RESPONSABILE";
// 				public String descProfilo = "Responsabile del servizio";
// 			}
// 		};

//         //return new ResponseEntity<>(dal.getProfili(), HttpStatus.OK);
//         return new ResponseEntity<>(arr, HttpStatus.OK);
//     }

//     @PostMapping("/richieste")
//     public Object getRichieste(@RequestBody RicercaUtenti parametri) throws Exception {
//         Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;
//         //TODO: gestire autorizzazione
//         return new ResponseEntity<>(dal.getRichieste(parametri), HttpStatus.OK);
//     }
    
    
//     @GetMapping("/richieste/responsabile")
//     public Object getRichiesteResponsabile() throws Exception {
//         Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;
//          return new ResponseEntity<>(dal.getRichiesteResponsabile(jwtAuth.getUsername()), HttpStatus.OK);
//     }
    
//     @PostMapping(path = "/richieste/{idRichiesta}", consumes = MediaType.APPLICATION_JSON)
//     public Object valutaRichiestaProfilo(@PathVariable Integer idRichiesta, @RequestBody ValutazioneRichiestaProfilo valutazione) throws Exception {
//         Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;

//         return new ResponseEntity<>(dal.valutaRichiestaProfilo(jwtAuth.getAccessToken().getIdUtente(), idRichiesta, valutazione), HttpStatus.OK);
//     }
    

// }
