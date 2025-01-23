// package it.almaviva.foliage.controllers;

// import it.almaviva.foliage.authentication.JwtAuthentication;
// import it.almaviva.foliage.bean.RichiestaProfilo;
// import it.almaviva.foliage.services.UtenzaDal;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.ws.rs.core.MediaType;

// import java.nio.charset.StandardCharsets;
// import java.sql.SQLException;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.google.gson.Gson;
// import com.google.gson.JsonObject;

// import io.micrometer.core.instrument.util.IOUtils;


// @RestController
// @RequestMapping("/api/corrente")
// public class UtenzaCorrenteController {
	
//     @Autowired
//     private UtenzaDal dal;

//     public UtenzaCorrenteController() throws SQLException {
//     }
	
//     @GetMapping("/")
//     public Object readCurrent() throws Exception {
//         Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;
//         return this.dal.getInfoUtente(jwtAuth.getUsername());
//     }
	
//     // @GetMapping("/profili")
//     // public Object getRuoliUtenteCorrente() throws Exception {
//     //     Authentication a = SecurityContextHolder.getContext().getAuthentication();
//     //     JwtAuthentication jwtAuth = (JwtAuthentication)a;
//     //     return new ResponseEntity<>(dal.getProfiliUtente(jwtAuth.getAccessToken().getIdUtente()), HttpStatus.OK);
//     // }
    
    
//     @GetMapping("/richieste")
//     public Object getRichiesteUtenteCorrente() throws Exception {
//         Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;
//         //return new ResponseEntity<>(dal.getRichiesteUtente(jwtAuth.getAccessToken().getIdUtente()), HttpStatus.OK);
//         return new ResponseEntity<>(dal.getRichiesteUtente(jwtAuth.getAccessToken().getIdUtente()), HttpStatus.OK);
//     }

//     @GetMapping("/richieste/{idRichiesta}")
//     public Object getRichiestaUtenteCorrente(@PathVariable Integer idRichiesta) throws Exception {
//         Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;
//         //return new ResponseEntity<>(dal.getRichiesteUtente(jwtAuth.getAccessToken().getIdUtente()), HttpStatus.OK);
//         return new ResponseEntity<>(dal.getRichiestaUtente(jwtAuth.getAccessToken().getIdUtente(), idRichiesta), HttpStatus.OK);
//     }
	
//     @DeleteMapping("/richieste/{idRichiesta}")
//     public Object cancelRichiestaUtenteCorrente(@PathVariable Integer idRichiesta) throws Exception {
//         Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;
//         //return new ResponseEntity<>(dal.getRichiesteUtente(jwtAuth.getAccessToken().getIdUtente()), HttpStatus.OK);
//         return new ResponseEntity<>(dal.cancelRichiestaUtente(jwtAuth.getAccessToken().getIdUtente(), idRichiesta), HttpStatus.OK);
//     }
 	
//     @PostMapping(path = "/accettazione-privacy")
//     public Object accettazionePrivacy() throws Exception {
//         Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;
//         return new ResponseEntity<>(dal.effettuaAccettazionePrivacy(jwtAuth.getUsername()), HttpStatus.OK);
//     }
    
//     @PostMapping(path = "/aggiorna-dati")
//     public Object aggiornaDatiUtente(HttpServletRequest request) throws Exception {
//         final String json = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
//         JsonObject mods = new Gson().fromJson(json, JsonObject.class);
//         Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;
//         return new ResponseEntity<>(dal.aggiornaDatiUtente(jwtAuth.getAccessToken().getIdUtente(), mods), HttpStatus.OK);
//     }

//     @PostMapping(path = "/nuova-richiesta-profilo", consumes = MediaType.APPLICATION_JSON)
//     public Object nuovaRichiestaProfilo(@RequestBody RichiestaProfilo richiesta) throws Exception {
//         Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;

//         return new ResponseEntity<>(dal.nuovaRichiestaProfilo(jwtAuth.getAccessToken().getIdUtente(), richiesta), HttpStatus.OK);
//     }

//     @GetMapping("/profili-selezionabili")
//     public Object getProfiliSelezionabili() throws Exception {
//         Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;

//         return new ResponseEntity<>(jwtAuth.getAccessToken().getAuthorities(), HttpStatus.OK);
//     }

// 	@GetMapping("/ruolo/{idProfilo}/enti")
//     public Object getEntiPerProfilo(@PathVariable Integer idProfilo) throws Exception {
//         Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;
// 		return new ResponseEntity<>(this.dal.getEntiPerRuoloUtente(jwtAuth.getAccessToken().getIdUtente(), idProfilo), HttpStatus.OK);
//     }


//     @GetMapping("/profilo-default")
//     public Object getProfiloDefault() throws Exception {
//         Authentication a = SecurityContextHolder.getContext().getAuthentication();
//         JwtAuthentication jwtAuth = (JwtAuthentication)a;

//         return new ResponseEntity<>(jwtAuth.getAccessToken().getProfiloDefault(), HttpStatus.OK);
//     }
// }
