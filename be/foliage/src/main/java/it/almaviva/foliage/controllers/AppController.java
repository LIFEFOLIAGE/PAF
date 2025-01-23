package it.almaviva.foliage.controllers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.javatuples.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.io.util.IntHashtable.Entry;

import org.springframework.beans.factory.annotation.Value;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.authentication.AccessToken;
import it.almaviva.foliage.authentication.JwtAuthentication;
import it.almaviva.foliage.bean.AbilitazioniIstanza;
import it.almaviva.foliage.bean.ChiaviRicercaIstanza;
import it.almaviva.foliage.bean.DatiIstanza;
import it.almaviva.foliage.bean.FileIstanzaApp;
import it.almaviva.foliage.bean.RisultatoRicercaIstanza;
import it.almaviva.foliage.enums.TipoAuthScope;
import it.almaviva.foliage.enums.TipoAuthority;
import it.almaviva.foliage.legacy.LegacyDal;
import it.almaviva.foliage.legacy.bean.CatalogoLayer;
import it.almaviva.foliage.legacy.bean.File;
import it.almaviva.foliage.legacy.bean.Istanza;
import it.almaviva.foliage.legacy.bean.PartForestale;
import it.almaviva.foliage.legacy.bean.Regione;
import it.almaviva.foliage.legacy.bean.Rilevamenti;
import it.almaviva.foliage.legacy.bean.Utente;
// import it.almaviva.foliage.bean.AreaDiSaggio;
// import it.almaviva.foliage.bean.CatalogoLayer;
// import it.almaviva.foliage.bean.File;
// import it.almaviva.foliage.bean.Istanza;
// import it.almaviva.foliage.bean.Login;
// import it.almaviva.foliage.bean.PartForestale;
// import it.almaviva.foliage.bean.Regione;
// import it.almaviva.foliage.bean.Rilevamenti;
// import it.almaviva.foliage.bean.Uog;
// import it.almaviva.foliage.bean.Utente;
// import it.almaviva.foliage.service.AppService;
//import it.almaviva.foliage.services.Dal;
import jakarta.ws.rs.QueryParam;

@RestController
@RequestMapping(value = "${foliage.base-path}api/app")
@Tag(name = "App", description = "Operazioni dell'app mobile")
public class AppController {


	@Autowired
    private LegacyDal dal;
	
	@Schema(description = "Login", implementation = Utente.class)
	@PostMapping("/login")
	public ResponseEntity<Utente> login() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthentication jwtAuth = (JwtAuthentication)a;
        return new ResponseEntity<>(
			dal.getLoginUtente(
				jwtAuth.getUsername()
			),
			HttpStatus.OK
		);
	}
	
		
	@Schema(description = "Restituisce la lista delle istanze per l'utente loggato", implementation = Istanza.class)
	@GetMapping("/istanze")
	public ResponseEntity<List<RisultatoRicercaIstanza>> ricercaIstanze(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope
	) throws Exception
	{
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;


		String codFiscaleUtente = jwtAuth.getAccessToken().getCodiceFiscale();
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		return new ResponseEntity<>(
			dal.ricercaInstanze(
				authority, authScope,
				idUtente, codFiscaleUtente,
				new ChiaviRicercaIstanza()
			),
			HttpStatus.OK
		);
	}

	@Schema(description = "Restituisce i dati di un'istanza", implementation = Istanza.class)
	@GetMapping("/istanze/{idIsta}")
	public ResponseEntity<DatiIstanza> getDatiIstanza(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable Integer idIsta
	) throws Exception
	{
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();

		String codFiscaleUtente = jwtToken.getCodiceFiscale();
		Integer idUtente = jwtToken.getIdUtente();
		
		int realId = idIsta/100;
		AbilitazioniIstanza abil = dal.getAbilitazioniIdIstanza(idUtente, codFiscaleUtente, realId, authority, authScope);

		if (abil.compilazione || abil.consultazione) {
			return new ResponseEntity<>(dal.getDatiIstanza(realId, authority, authScope), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(
				null,
				HttpStatus.FORBIDDEN
			);

		}
	}

	
	@Schema(description = "Restituisce la lista dei documenti dell'istanza", implementation = FileIstanzaApp.class)
	@GetMapping("/istanze/{idIsta}/files")
	public ResponseEntity<List<FileIstanzaApp>> getFileIstanza(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable Integer idIsta
	) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();

		String codFiscaleUtente = jwtToken.getCodiceFiscale();
		Integer idUtente = jwtToken.getIdUtente();
		
		int realId = idIsta/100;
		AbilitazioniIstanza abil = dal.getAbilitazioniIdIstanza(idUtente, codFiscaleUtente, realId, authority, authScope);

		if (abil.compilazione || abil.consultazione) {
			return new ResponseEntity<>(dal.getFileIstanzaApp(realId), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(
				null,
				HttpStatus.FORBIDDEN
			);
		}
	}

	
	@Schema(description = "Restituisce i rilevamenti di un'istanza", implementation = Rilevamenti.class)
	@GetMapping("/istanze/{idIsta}/rilevamenti")
	public ResponseEntity<List<Rilevamenti>> ricercaRilevamentiIstanza(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable Integer idIsta
	) throws Exception{
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();

		String codFiscaleUtente = jwtToken.getCodiceFiscale();
		Integer idUtente = jwtToken.getIdUtente();
		
		int realId = idIsta/100;
		AbilitazioniIstanza abil = dal.getAbilitazioniIdIstanza(idUtente, codFiscaleUtente, realId, authority, authScope);

		if (abil.compilazione || abil.consultazione) {
			List<Rilevamenti> lista = dal.getRilevamentiForIdIsta(realId, idUtente, authority, authScope);
			return new ResponseEntity<List<Rilevamenti>>(lista, HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(
				null,
				HttpStatus.FORBIDDEN
			);
		}
	}

	@Schema(description = "Inserimento di un nuovo rilevamento", implementation = String.class)
	@PutMapping("/istanze/{idIsta}/rilevamenti")
	public ResponseEntity<String> inserisciRilevamentiIstanzaNew(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable Integer idIsta,
		@RequestBody List<Rilevamenti> rlev
	) throws Exception{
		ResponseEntity<String> outVal = null;

		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();

		String codFiscaleUtente = jwtToken.getCodiceFiscale();
		Integer idUtente = jwtToken.getIdUtente();
		
		int realId = idIsta/100;
		AbilitazioniIstanza abil = dal.getAbilitazioniIdIstanza(idUtente, codFiscaleUtente, realId, authority, authScope);

		if (abil.compilazione || abil.consultazione) {
			
			String message = null;
			HttpStatusCode code = HttpStatus.OK;
			try {
				dal.inserisciRilevamenti(realId, rlev, idUtente, authority, authScope);
			}
			catch (Exception e) {
				e.printStackTrace();
				code = HttpStatus.INTERNAL_SERVER_ERROR;
				message = e.toString();
			}
			finally {
				outVal = new ResponseEntity<String>(message,  code);
			}
		}
		else {
			return new ResponseEntity<>(
				null,
				HttpStatus.FORBIDDEN
			);
		}

		return outVal;
	}
	@Schema(description = "Inserimento dei rilevamenti", implementation = String.class)
	@PutMapping("/rilevamenti")
	public ResponseEntity<String> inserisciRilevamentiNew(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@RequestBody List<Rilevamenti> rilev
	) throws Exception{
		ResponseEntity<String> outVal = null;

		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();

		String codFiscaleUtente = jwtToken.getCodiceFiscale();
		Integer idUtente = jwtToken.getIdUtente();
		// List<Integer> ids = rilev.stream().map(
		// 	(Rilevamenti r) -> r.getIdIsta()
		// ).distinct().toList();
		Map<Integer, List<Rilevamenti>> groups = rilev.stream().collect(Collectors.groupingBy(Rilevamenti::getIdIsta));
		
		boolean ab = groups.keySet().stream().map(
				(Integer id) -> {
					try {
						int realId = id/100;
						int suff = id%100;
						int idAuthority = suff/10;
						int idScope = suff%10;
						TipoAuthority auth = TipoAuthority.fromInt(idAuthority);
						TipoAuthScope scope = TipoAuthScope.fromInt(idScope);
						//return dal.getAbilitazioniIdIstanza(idUtente, codFiscaleUtente, id, authority, authScope);
						return dal.getAbilitazioniIdIstanza(idUtente, codFiscaleUtente, realId, auth.name(), scope.name());
					}
					catch (Exception e){
						return new AbilitazioniIstanza();
					}
				}
			).allMatch(
				(AbilitazioniIstanza abil) -> abil.compilazione || abil.consultazione
			);
		
		if (ab) {
			Unit<Exception> opt = new Unit<>(null);
			String message = null;
			HttpStatusCode code = HttpStatus.OK;

			groups.entrySet().stream().forEach(
				(Map.Entry<Integer, List<Rilevamenti>> e) -> {
					if (opt.getValue0() == null) {
						Integer id = e.getKey();
						int realId = id/100;
						int suff = id%100;
						int idAuthority = suff/10;
						int idScope = suff%10;
						TipoAuthority auth = TipoAuthority.fromInt(idAuthority);
						TipoAuthScope scope = TipoAuthScope.fromInt(idScope);
						try {
							dal.inserisciRilevamenti(realId, e.getValue(), idUtente, auth.name(), scope.name());
						} catch (Exception e1) {
							opt.setAt0(e1);
						}
					}
				}
			);
			Exception e = opt.getValue0();
			if (e == null) {
				outVal = new ResponseEntity<String>(message,  code);
			}
			else {
				throw new FoliageException("Si è verificato un problema durante il caricamento dei rilevamenti", e);
			}
		}
		else {
			return new ResponseEntity<>(
				"Non hai accesso a tutte le istanze indicate",
				HttpStatus.FORBIDDEN
			);
		}
		return outVal;
	}
	@Schema(description = "Elimina un rilevamento", implementation = Rilevamenti.class)
	@DeleteMapping("/istanze/{idIsta}/rilevamenti/{idRile}")
	public ResponseEntity<String> eliminaRilevamentoNew(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable Integer idIsta,
		@PathVariable Long idRile
	) throws Exception{
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();

		String codFiscaleUtente = jwtToken.getCodiceFiscale();
		Integer idUtente = jwtToken.getIdUtente();
		
		int realId = idIsta/100;
		AbilitazioniIstanza abil = dal.getAbilitazioniIdIstanza(idUtente, codFiscaleUtente, realId, authority, authScope);

		if (abil.compilazione || abil.consultazione) {	
			dal.eliminaRilevamentoNew(realId, idRile, idUtente, authority, authScope);
			return new ResponseEntity<String>(HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(
				null,
				HttpStatus.FORBIDDEN
			);
		}
	}

	@Schema(description = "Restituisce i dettagli dei rilevamento selezionato", implementation = Rilevamenti.class)
	@GetMapping("/istanze/{idIsta}/rilevamenti/{idRile}")
	public ResponseEntity<Rilevamenti> getDettRilevamentoNew(
		@QueryParam("authority") String authority,
		@QueryParam("authScope") String authScope,
		@PathVariable Integer idIsta,
		@PathVariable Integer idRile
	) throws Exception{
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();

		String codFiscaleUtente = jwtToken.getCodiceFiscale();
		Integer idUtente = jwtToken.getIdUtente();
		
		int realId = idIsta/100;
		AbilitazioniIstanza abil = dal.getAbilitazioniIdIstanza(idUtente, codFiscaleUtente, realId, authority, authScope);

		if (abil.compilazione || abil.consultazione) {	
			Rilevamenti ril = dal.getDettRilevamento(realId, idRile, idUtente, authority, authScope);
			return new ResponseEntity<Rilevamenti>(ril, HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(
				null,
				HttpStatus.FORBIDDEN
			);
		}
	}
	
	@Schema(description = "Restituisce la lista delle tipologie di layer", implementation = Regione.class)
	@GetMapping("/tipologie-rilevamento")
	public ResponseEntity<List<CatalogoLayer>> getTipiRilevamento(){
		List<CatalogoLayer> lista=dal.getTipolRilev();
		return new ResponseEntity<List<CatalogoLayer>>(lista, HttpStatus.OK);
	}

	@Schema(description = "Restituisce la lista delle tipologie di layer", implementation = Regione.class)
	@GetMapping("/getTipolRilevamento")
	@Deprecated
	public ResponseEntity<List<CatalogoLayer>> getTipolRilevamento(){
		List<CatalogoLayer> lista=dal.getTipolRilev();
		return new ResponseEntity<List<CatalogoLayer>>(lista, HttpStatus.OK);
	}

	@GetMapping("/profili-utente-corrente")
	public ResponseEntity<Collection<GrantedAuthority>> getProfiliUtente() {

        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthentication jwtAuth = (JwtAuthentication)a;
        return new ResponseEntity<>(
			jwtAuth.getAuthorities(),
			HttpStatus.OK
		);
	}
	
	@Schema(description = "Restituisce i dettagli dei rilevamento selezionato", implementation = Rilevamenti.class)
	@GetMapping("/getDettRilevamento")
	@Deprecated
	public ResponseEntity<Rilevamenti> getDettRilevamento(@RequestParam Long idRile) throws Exception{
		
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();
		Integer idUtente = jwtToken.getIdUtente();
		
		Rilevamenti ril = dal.getDettRilevamento(idRile, idUtente, "PROP", "GENERICO");
		return new ResponseEntity<Rilevamenti>(ril, HttpStatus.OK);
	}
	
	@Schema(description = "Modifica rilevamento", implementation = String.class)
	@PostMapping("/updateRilevamento")
	@Deprecated
	public ResponseEntity<String> updateRilevamento(@RequestBody Rilevamenti ril) throws Exception{
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();
		Integer idUtente = jwtToken.getIdUtente();
		
		dal.updateRilevamento(ril, idUtente, "PROP", "GENERICO");
		return new ResponseEntity<String>(HttpStatus.OK);
	}




	
	@Schema(description = "Restituisce la lista delle regioni disponibili", implementation = Regione.class)
	@GetMapping("/getRegioni")
	@Deprecated
	public ResponseEntity<List<Regione>> getRegioni(){
		List<Regione> lista=dal.getRegioniLegacy();
		return new ResponseEntity<List<Regione>>(lista, HttpStatus.OK);
	}

	@Schema(description = "Restituisce la lista delle istanze per l'utente loggato", implementation = Istanza.class)
	@GetMapping("/listaIstanze")
	@Deprecated
	public ResponseEntity<List<Istanza>> listaIstanze(
		@RequestParam String cf
	) throws Exception
	{
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();
		Integer idUtente = jwtToken.getIdUtente();
		String codFiscale = jwtAuth.getAccessToken().getCodiceFiscale();
		List<Istanza> lista = dal.ricercaInstanze(
			"PROP", "GENERICO",
			idUtente, codFiscale, 
			new ChiaviRicercaIstanza(),
			Istanza.RowMapper()
		);
		return new ResponseEntity<List<Istanza>>(lista, HttpStatus.OK);
	}

	@Schema(description = "Restituisce la lista delle particelle forestali", implementation = PartForestale.class)
	@GetMapping("/getPartForestali")
	@Deprecated
	public ResponseEntity<List<PartForestale>> getPartForestali(@RequestParam int idIsta){
		List<PartForestale> ls=dal.getPartForestaliForApp(idIsta);
		return new ResponseEntity<List<PartForestale>>(ls, HttpStatus.OK);
	}
	
	
	@Schema(description = "Restituisce la lista dei documenti dell'istanza", implementation = File.class)
	@GetMapping("/getFilesIstanza")
	@Deprecated
	public List<File> getFilesIstanza(@RequestParam int idIsta) throws Exception {
		return dal.getFilesIstanza(idIsta);
	}

	@Schema(description = "Inserimento di un nuovo rilevamento", implementation = String.class)
	@PostMapping("/inserisciRilevamento")
	@Deprecated
	public ResponseEntity<String> inserisciRilevamento(@RequestBody List<Rilevamenti> rlev) throws Exception{
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuth = (JwtAuthentication)a;
		AccessToken jwtToken = jwtAuth.getAccessToken();
		Integer idUtente = jwtToken.getIdUtente();

		String message = null;
		HttpStatusCode code = HttpStatus.OK;
		ResponseEntity<String> outVal = null;
		try {
			dal.inserisciRilevamenti(rlev, idUtente, "PROP", "GENERICO");
		}
		catch (Exception e) {
			e.printStackTrace();
			code = HttpStatus.INTERNAL_SERVER_ERROR;
			message = e.toString();
		}
		finally {
			outVal = new ResponseEntity<String>(message,  code);
		}
		return outVal;
	}


	@Schema(description = "Restituisce i rilevamenti di un'istanza", implementation = Rilevamenti.class)
	@GetMapping("/getRilevamenti")
	@Deprecated
	public ResponseEntity<List<Rilevamenti>> getRilevamento(@RequestParam Integer idIsta) throws Exception{
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthentication jwtAuth = (JwtAuthentication)a;
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		
		List<Rilevamenti> lista = dal.getRilevamentiForIdIsta(idIsta, idUtente, "PROP", "GENERICO");
		return new ResponseEntity<List<Rilevamenti>>(lista, HttpStatus.OK);
	}
	
	@Schema(description = "Elimina un rilevamento", implementation = Rilevamenti.class)
	@DeleteMapping("/eliminaRilevamento")
	@Deprecated
	public ResponseEntity<String> eliminaRilevamento(@RequestParam Long idRile) throws Exception{
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthentication jwtAuth = (JwtAuthentication)a;
		String user = jwtAuth.getUsername();
		String codFiscale = jwtAuth.getAccessToken().getCodiceFiscale();
		Integer idUtente = jwtAuth.getAccessToken().getIdUtente();
		dal.eliminaRilevamento(idRile, user, codFiscale, idUtente, "PROP", "GENERICO");
		return new ResponseEntity<String>(HttpStatus.OK);
	}
		

}
