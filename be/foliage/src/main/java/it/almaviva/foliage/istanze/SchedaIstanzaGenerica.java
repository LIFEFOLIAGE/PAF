package it.almaviva.foliage.istanze;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.function.ContextTest;
import it.almaviva.foliage.function.OperazioneContesto;
import it.almaviva.foliage.istanze.db.IOperazioneDb;
import it.almaviva.foliage.services.WebDal;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SchedaIstanzaGenerica implements ISchedaIstanzaWithContext{

	//public HashMap<String, Object> contesto;
	public String[] contextUpdateProps;

	public IOperazioneDb[] operazioniSalvataggio;
	public IOperazioneDb[] operazioniCaricamento;
	public IOperazioneDb[] operazioniCancellazione;
	public OperazioneContesto[] presalvataggio;
	public ContextTest validityTest;
	public String name;
	public CorrispondenzaJson corrispondenzeJson;
	public Integer[] dependencies = {};


	public SchedaIstanzaGenerica(
		String name,
		CorrispondenzaJson corrispondenzeAcquisizione,
		IOperazioneDb[] operazioniCaricamento,
		IOperazioneDb[] operazioniSalvataggio,
		IOperazioneDb[] operazioniCancellazione,
		String[] contextUpdateProps
	) {
		this.name = name;
		this.corrispondenzeJson = corrispondenzeAcquisizione;
		this.operazioniSalvataggio = operazioniSalvataggio;
		this.operazioniCaricamento = operazioniCaricamento;
		this.operazioniCancellazione = operazioniCancellazione;
		this.contextUpdateProps = contextUpdateProps;
	}
	
	
	public SchedaIstanzaGenerica(
		String name,
		CorrispondenzaJson corrispondenzeAcquisizione,
		IOperazioneDb[] operazioniCaricamento,
		IOperazioneDb[] operazioniSalvataggio,
		IOperazioneDb[] operazioniCancellazione
	) {
		this(name, corrispondenzeAcquisizione, operazioniCaricamento, operazioniSalvataggio, operazioniCancellazione, null);
	}
	
	public SchedaIstanzaGenerica(
		String name,
		CorrispondenzaJson corrispondenzeAcquisizione,
		IOperazioneDb[] operazioniCaricamento,
		String[] contextUpdateProps
	) {
		this(name, corrispondenzeAcquisizione, operazioniCaricamento, null, null, contextUpdateProps);
	}

	public SchedaIstanzaGenerica(
		String name,
		CorrispondenzaJson corrispondenzeAcquisizione,
		IOperazioneDb[] operazioniCaricamento
	) {
		this(name, corrispondenzeAcquisizione, operazioniCaricamento, null, null, null);
	}

	public SchedaIstanzaGenerica(
		String name,
		CorrispondenzaJson corrispondenzeAcquisizione
	) {
		this(name, corrispondenzeAcquisizione, null, null, null, null);
	}

	
	@Override
	public List<Integer> getDependencies() {
		return Arrays.asList(dependencies);
	}

	@Override
	public void salvaScheda(WebDal dal, HashMap<String, Object> contestoIniziale, JsonElement datiScheda) throws Exception{
		log.debug(String.format("Inizio salvataggio scheda %s", name));
		HashMap<String, Object> contestoEliminazione = new HashMap<>(contestoIniziale);
		if (presalvataggio != null) {
			for (OperazioneContesto oper : presalvataggio) {
				oper.exec(contestoIniziale);
			}
		}

		cancellaScheda(dal, contestoEliminazione);
		JsonObject jsonObj = (JsonObject)datiScheda;
		if (jsonObj != null) {
			HashMap<String, Object> contesto = new HashMap<>(contestoIniziale);
			corrispondenzeJson.caricaContesto(jsonObj, contesto);
			for (IOperazioneDb oper : operazioniSalvataggio) {
				oper.applica(dal, contesto);
			}
			if (contextUpdateProps != null) {
				for (String k : contextUpdateProps) {
					//contestoIniziale.put(prop, contesto.get(prop));

					Object v = contesto.get(k);
					if (v == null) {
						log.debug(String.format("Recupero proprietà %s = NULL", k));
					}
					else {
						log.debug(String.format("Recupero proprietà %s = '%s' (%s)", k, v.toString(), v.getClass()));
					}
					contestoIniziale.put(k, v);
				}
			}
		}
		else {
			throw new FoliageException("Elemento Json non supportato");
		}
		log.debug(String.format("Fine salvataggio scheda %s", name));
	}

	@Override
	public void cancellaScheda(WebDal dal, HashMap<String, Object> contestoIniziale) throws Exception {
		log.debug(String.format("Inizio cancellazione scheda %s", name));

		HashMap<String, Object> contesto = new HashMap<>(contestoIniziale);
		if (operazioniCancellazione != null) {
			for (IOperazioneDb oper : operazioniCancellazione) {
				oper.applica(dal, contesto);
			}
		}
		log.debug(String.format("Fine cancellazione scheda %s", name));
	}


	@Override
	public boolean isValid(HashMap<String, Object> contesto) throws Exception {
		if (validityTest != null) {
			return validityTest.eval(contesto);
		}
		return true;
	}

	@Override
	public String[] getContextUpdateProps() {
		return contextUpdateProps;
	}

	@Override
	public JsonElement caricaScheda(HashMap<String, Object> contesto) throws Exception {
		
		JsonElement mainElement = carica(contesto);
		return mainElement;
	}

	@Override
	public void analizzaScheda(WebDal dal, HashMap<String, Object> contesto) throws Exception {
		log.debug(String.format("Inizio analisi scheda %s", name));
		HashMap<String, Object> newContext = new HashMap<>();
		newContext.putAll(contesto);
		analizza(dal, newContext);
		if (contextUpdateProps != null) {
			for (String k : contextUpdateProps) {
				//contesto.put(prop, newContext.get(prop));

				Object v = newContext.get(k);
				if (v == null) {
					log.debug(String.format("Recupero proprietà %s = NULL", k));
				}
				else {
					log.debug(String.format("Recupero proprietà %s = '%s' (%s)", k, v.toString(), v.getClass()));
				}
				contesto.put(k, v);
			}
		}
		log.debug(String.format("Fine analisi scheda %s", name));
	}

	private void analizza(WebDal dal, HashMap<String, Object> contesto) throws Exception {
		if (operazioniCaricamento != null) {
			for (IOperazioneDb oper : operazioniCaricamento) {
				oper.applica(dal, contesto);
			}
		}
	}
	private JsonObject carica(HashMap<String, Object> contesto) throws Exception {
		return corrispondenzeJson.getObjectFromContext(contesto);
	}

	// @Override
	// public void analisiVeloce(Dal dal, HashMap<String, Object> contesto) throws Exception {
	// 	if (contextUpdateProps != null && contextUpdateProps.length > 0) {
	// 		if (operazioniCaricamento != null) {
	// 			for (int i = operazioniCaricamento.length-1; i >= 0; i--) {
	// 				IOperazioneDb oper = operazioniCaricamento[i];
	// 			}

	// 			for (IOperazioneDb oper : operazioniCaricamento) {
	
	// 				oper.getProprietaUtilizzate()
	// 				if () {
	// 					oper.applica(dal, contesto);
	// 				}
	// 			}
	// 		}
	// 	}
	// }
	
	@Override
	public JsonElement analizzaCaricaScheda(WebDal dal, HashMap<String, Object> contesto) throws Exception {
		log.debug(String.format("Inizio acquisizione scheda %s", name));

		HashMap<String, Object> newContext = new HashMap<>();
		newContext.putAll(contesto);
		analizza(dal, newContext);
		JsonObject outVal = carica(newContext);


		if (contextUpdateProps != null) {
			for (String k : contextUpdateProps) {
				Object v = newContext.get(k);
				if (v == null) {
					log.debug(String.format("Recupero proprietà %s = NULL", k));
				}
				else {
					log.debug(String.format("Recupero proprietà %s = '%s' (%s)", k, v.toString(), v.getClass()));
				}
				contesto.put(k, v);
			}
		}
		log.debug(String.format("Fine acquisizione scheda %s", name));
		return outVal;
	}
}
