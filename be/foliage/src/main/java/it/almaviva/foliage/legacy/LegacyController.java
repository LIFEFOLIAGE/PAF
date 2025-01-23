package it.almaviva.foliage.legacy;

//import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micrometer.core.instrument.util.IOUtils;
import it.almaviva.foliage.services.WebDal;
//import it.almaviva.foliage.services.IstanzaDal;
import jakarta.servlet.http.HttpServletRequest;
//import it.almaviva.foliage.services.ResultSetSerializer;
import jakarta.ws.rs.Produces;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import it.almaviva.foliage.authentication.JwtAuthentication;
import it.almaviva.foliage.legacy.bean.Login;
import it.almaviva.foliage.legacy.bean.Utente;

@RestController
@RequestMapping("${foliage.base-path}api/legacy")
public class LegacyController {

	@Autowired
	private LegacyDal dal;
	private void checkAmministratore() {
		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthentication jwtAuth = (JwtAuthentication)auth;
		jwtAuth.getUsername();

	}
	
    public LegacyController() throws SQLException {
    }

    @GetMapping("/getRegioni")
    public Object getRegioni() throws Exception {
        return new ResponseEntity<>(dal.getRegioni(), HttpStatus.OK);
    }

	
	//@Schema(description = "Login", response = Utente.class)
	@PostMapping("/login")
	public ResponseEntity<Utente> login( @RequestBody Login login) {

		//Utente utente= dal.login();
		Utente utente= new Utente();
		return new ResponseEntity<Utente>(utente, HttpStatus.OK);
	}

	@GetMapping("/listaIstanze")
	public Object listaIstanze(@RequestParam String cf, @RequestParam String codiRegi) {
        //return new ResponseEntity<>(dal.listaIstanze(cf, codiRegi), HttpStatus.OK);
		return null;
	}
	

	@GetMapping("/getListaEnti")
	public Object getListaEnti(@RequestParam String codiRegi){
        //return new ResponseEntity<>(dal.getListaEnti(codiRegi), HttpStatus.OK);
		return null;
	}

	@GetMapping("/getSoggetto")
	public Object getSoggetto(@RequestParam String cf) {
        //return new ResponseEntity<>(dal.getSoggetto(cf), HttpStatus.OK);
		return null;
	}
	
	@GetMapping("/getSoggettoProp")
	public Object getSoggettoPropietario(@RequestParam String idIsta) {
        //return new ResponseEntity<>(dal.getSoggettoPropietario(idIsta), HttpStatus.OK);
		return null;
	}


	@GetMapping("/getProvince")
	public Object getProvince(@RequestParam String codiRegi) throws SQLException, Exception {
		return new ResponseEntity<>(dal.getProvincie(codiRegi), HttpStatus.OK);
		//return null;
	}
	@GetMapping("/getProvincie")
	public Object getProvincie(@RequestParam String codiRegi) throws SQLException, Exception {
		return new ResponseEntity<>(dal.getProvincie(codiRegi), HttpStatus.OK);
		//return null;
	}



	@PostMapping("/eliminaIstanza")
	public Object eliminaIstanza(@RequestParam int idIsta, @RequestParam int stato) {
		checkAmministratore();
		//dal.eliminaIstanza(idIsta);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	// @PostMapping("/creaIstanza")
	// public Object creaIstanza(@RequestBody InserimentoIstanza i) {
		
	// 	return null;
	// }
}
