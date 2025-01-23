package it.almaviva.foliage.istanze;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.almaviva.foliage.function.ContextTest;
import it.almaviva.foliage.function.OperazioneContesto;
import it.almaviva.foliage.istanze.db.IOperazioneDb;
import it.almaviva.foliage.services.WebDal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedaIstanzaArray implements ISchedaIstanzaWithContext{

	public IOperazioneDb operazioneCaricamento;
	public IOperazioneDb[] operazioniCancellazione;
	public IOperazioneDb[] operazioniPreSalvataggioScheda;
	public IOperazioneDb[] operazioniPostSalvataggioScheda;
	public SchedaIstanzaGenerica schedaElementi;
	public OperazioneContesto[] presalvataggio;
	public String arrayContextName;
	public String arrayIndexContextName;
	public Integer[] dependencies = {};


	public ContextTest validityTest;
	

	public SchedaIstanzaArray(
		String arrayContextName,
		IOperazioneDb operazioneCaricamento,
		IOperazioneDb[] operazioniCancellazione,
		SchedaIstanzaGenerica schedaElementi,
		String arrayIndexContextName,
		IOperazioneDb[] operazioniPreSalvataggioScheda,
		IOperazioneDb[] operazioniPostSalvataggioScheda
	) {
		this.arrayContextName = arrayContextName;
		this.operazioneCaricamento = operazioneCaricamento;
		this.schedaElementi = schedaElementi;
		this.operazioniCancellazione = operazioniCancellazione;
		this.arrayIndexContextName = arrayIndexContextName;
		this.operazioniPreSalvataggioScheda = operazioniPreSalvataggioScheda;
		this.operazioniPostSalvataggioScheda = operazioniPostSalvataggioScheda;
	}

	public SchedaIstanzaArray(
		String arrayContextName,
		IOperazioneDb operazioneCaricamento,
		IOperazioneDb[] operazioniCancellazione,
		SchedaIstanzaGenerica schedaElementi,
		String arrayIndexContextName
	) {
		this(arrayContextName, operazioneCaricamento, operazioniCancellazione, schedaElementi, arrayIndexContextName, null, null);
	}

	@Override
	public List<Integer> getDependencies() {
		return Arrays.asList(dependencies);
	}
	
	@Override
	public void salvaScheda(
		WebDal dal, HashMap<String, Object> contestoIniziale,
		JsonElement datiScheda
	) throws Exception {
		JsonArray jsonArr = (JsonArray)datiScheda;
		log.debug(String.format("Inizio salvataggio scheda array %s", arrayContextName));
		HashMap<String, Object> contestoEliminazione = new HashMap<>(contestoIniziale);
		if (presalvataggio != null) {
			for (OperazioneContesto oper : presalvataggio) {
				oper.exec(contestoIniziale);
			}
		}
		cancellaScheda(dal, contestoEliminazione);

		if (operazioniPreSalvataggioScheda != null) {
			for (IOperazioneDb oper : operazioniPreSalvataggioScheda) {
				oper.applica(dal, contestoIniziale);	
			}
		}

		int idx = 1;
		for (JsonElement jsonElement : jsonArr) {
			if (!jsonElement.isJsonNull()) {
				JsonObject jsonObj = (JsonObject)jsonElement;
				HashMap<String, Object> contestoSalvataggio = new HashMap<>(contestoIniziale);
				if (arrayIndexContextName != null) {
					//jsonObj.remove(arrayContextName);
					contestoSalvataggio.put(arrayIndexContextName, Integer.valueOf(idx));
					idx++;
				}
				schedaElementi.salvaScheda(dal, contestoSalvataggio, jsonObj);
			}
		}
		
		if (operazioniPostSalvataggioScheda != null) {
			for (IOperazioneDb oper : operazioniPostSalvataggioScheda) {
				oper.applica(dal, contestoIniziale);	
			}
		}
		log.debug(String.format("Fine salvataggio scheda array %s", arrayContextName));
	}

	@Override
	public void cancellaScheda(WebDal dal, HashMap<String, Object> contestoIniziale) throws Exception {
		log.debug(String.format("Inizio cancellazione scheda array %s", arrayContextName));
		if (operazioniCancellazione != null) {
			int idx = 0;
			HashMap<String, Object> contesto = new HashMap<>(contestoIniziale);
			for (IOperazioneDb oper : operazioniCancellazione) {
				if (arrayIndexContextName != null) {
	
					contesto.put(arrayIndexContextName, Integer.valueOf(idx));
					//schedaElementi.cancellaScheda(dal, contesto);
				}
				oper.applica(dal, contesto);
			}
		}
		else {
			log.warn(String.format("Scheda array senza operazioni di cancellazione %s", arrayContextName));
		}
		log.debug(String.format("Fine cancellazione scheda array %s", arrayContextName));
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
		return null;
	}

	private JsonElement carica(HashMap<String, Object> contesto) throws Exception {
		List<LinkedList<Pair<String, Object>>> vals = (List<LinkedList<Pair<String, Object>>>)contesto.get(arrayContextName);
		JsonArray jsonArr = null;
		for (LinkedList<Pair<String,Object>> pairsList : vals) {

			HashMap<String, Object> contestoCaricamento = new HashMap<>(contesto);
			contestoCaricamento.putAll(
				pairsList.stream().collect(Collectors.toMap(Pair<String, Object>::getValue0, Pair<String, Object>::getValue1))
			);
			JsonObject jsonElem = schedaElementi.corrispondenzeJson.getObjectFromContext(contestoCaricamento);
			if (jsonArr == null) {
				jsonArr = new JsonArray();
			}
			jsonArr.add(jsonElem);
		}
		return jsonArr;
	}
	private void analizza(WebDal dal, HashMap<String, Object> contesto) throws Exception {
		if (operazioneCaricamento != null) {
			List<LinkedList<Pair<String, Object>>> vals = operazioneCaricamento.applicaArray(dal, contesto);
			int idx = 0;
			for (LinkedList<Pair<String,Object>> val : vals) {
				HashMap<String, Object> sottoContesto = new HashMap<>(contesto);
				for (Pair<String,Object> pair : val) {
					sottoContesto.put(pair.getValue0(), pair.getValue1());
				}
				if (arrayIndexContextName != null) {
					sottoContesto.put(arrayIndexContextName, Integer.valueOf(idx++));
				}
				schedaElementi.analizzaScheda(dal, sottoContesto);
				// for (String pName : schedaElementi.contextUpdateProps) {
				// 	Object addVal = sottoContesto.get(pName);
				// 	val.addLast(new Pair<String,Object>(pName, addVal));
				// }
			}
			contesto.put(arrayContextName, vals);
		}
	}
	
	private JsonElement privCarica(WebDal dal, HashMap<String, Object> contesto) throws Exception {
		JsonArray array = null;
		if (operazioneCaricamento != null) {
			List<LinkedList<Pair<String, Object>>> vals = operazioneCaricamento.applicaArray(dal, contesto);
			int idx = 0;
			for (LinkedList<Pair<String,Object>> val : vals) {
				HashMap<String, Object> sottoContesto = new HashMap<>(contesto);
				for (Pair<String,Object> pair : val) {
					sottoContesto.put(pair.getValue0(), pair.getValue1());
				}
				// if (arrayIndexContextName != null) {
				// 	sottoContesto.put(arrayIndexContextName, Integer.valueOf(idx++));
				// }
				JsonElement e = schedaElementi.analizzaCaricaScheda(dal, sottoContesto);
				if (array == null) {
					array = new JsonArray();
				}
				array.add(e);
				// for (String pName : schedaElementi.contextUpdateProps) {
				// 	Object addVal = sottoContesto.get(pName);
				// 	val.addLast(new Pair<String,Object>(pName, addVal));
				// }
			}
			contesto.put(arrayContextName, vals);
		}
		return array;
	}
	@Override
	public JsonElement caricaScheda(HashMap<String, Object> contesto) throws Exception {
		return carica(contesto);
	}


	@Override
	public void analizzaScheda(WebDal dal, HashMap<String, Object> contesto) throws Exception {
		log.debug(String.format("Inizio analisi semplice scheda array %s", arrayContextName));
		analizza(dal, contesto);
		log.debug(String.format("Fine analisi semplice scheda array %s", arrayContextName));
	}
	
	@Override
	public JsonElement analizzaCaricaScheda(WebDal dal, HashMap<String, Object> contesto) throws Exception {
		log.debug(String.format("Inizio analisi scheda array %s", arrayContextName));
		//analizza(dal, contesto);

		JsonElement res = privCarica(dal, contesto);
		log.debug(String.format("Fine analisi scheda array %s", arrayContextName));
		return res;
	}
}
