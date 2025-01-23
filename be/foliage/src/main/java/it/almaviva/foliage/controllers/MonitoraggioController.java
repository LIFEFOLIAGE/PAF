package it.almaviva.foliage.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import it.almaviva.foliage.bean.AttivitaMonitoraggioBean;
import it.almaviva.foliage.bean.RisultatiMonitoraggioBean;
import it.almaviva.foliage.services.MonitoraggioDal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("${foliage.base-path}api/monitoraggio")
public class MonitoraggioController {

	@Autowired
	@Qualifier("monitoraggioDal")
	private MonitoraggioDal dal;

	public MonitoraggioController() throws Exception {
		log.debug(
			String.format(
				"Timezone is %s",
				TimeZone.getDefault().toString()
			)
		);
	}

	@PostMapping(path="/attivita/{clientId}")
	public Object recuperoAttita(
		@PathVariable String clientId,
		HttpServletRequest request
	) {
		return new ResponseEntity<>(
			dal.recuperoAttivita(request, clientId),
			HttpStatus.OK
		);		
	}

	@GetMapping(path="/dati-preelaborazione/{idRichiesta}")
	public Object getDatiPreelaborazione(@PathVariable int idRichiesta) {
		return new ResponseEntity<>(
			dal.getGeoJsonDatiPreelaborazione(idRichiesta),
			HttpStatus.OK
		);		
	}
	
	@PutMapping(path="/attivita/{clientId}")
	public Object salvataggioAttita(
		@PathVariable String clientId,
		//@Valid @RequestBody RisultatiMonitoraggioBean attivita
		HttpServletRequest request
	) throws IOException {
		InputStream stream = request.getInputStream();
		
		String s = new String(stream.readAllBytes());

		log.debug(s);
		stream.close();
		Gson gson =  new Gson();
		RisultatiMonitoraggioBean attivita = gson.fromJson(JsonParser.parseString(s), RisultatiMonitoraggioBean.class);
		dal.salvataggioAttivita(clientId, attivita);
		return new ResponseEntity<>(
			"OK",
			HttpStatus.OK
		);		
	}
}
