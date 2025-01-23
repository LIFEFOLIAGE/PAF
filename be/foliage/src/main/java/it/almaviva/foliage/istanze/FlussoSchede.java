package it.almaviva.foliage.istanze;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.javatuples.Pair;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import io.swagger.v3.oas.models.PathItem;
import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.function.MonoProcedure;
import it.almaviva.foliage.function.Procedure;
import it.almaviva.foliage.services.WebDal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FlussoSchede {
	public String[] contextUpdateProps;
	public String switchString;

	public ISchedaIstanzaWithContext[] initialSteps;

	public Map<String, FlussoSchede> flowSwitch;
	public FlussoSchede defaultFlow;
	public String nomeFlusso;
	
	public FlussoSchede(
		String nomeFlusso,
		String[] contextUpdateProps,
		ISchedaIstanzaWithContext[] initialSteps,
		String switchString,
		Map<String, FlussoSchede> flowSwitch,
		FlussoSchede defaultFlow
	) {
		this.nomeFlusso = nomeFlusso;
		this.contextUpdateProps = contextUpdateProps;
		this.initialSteps = initialSteps;
		this.switchString = switchString;
		this.flowSwitch = flowSwitch;
		this.defaultFlow = defaultFlow;
	}
	
	public FlussoSchede(
		String nomeFlusso,
		ISchedaIstanzaWithContext[] initialSteps,
		String switchString,
		Map<String, FlussoSchede> flowSwitch,
		FlussoSchede defaultFlow
	) {
		this(nomeFlusso, null, initialSteps, switchString, flowSwitch, defaultFlow);
	}
	public FlussoSchede(
		String nomeFlusso,
		ISchedaIstanzaWithContext[] initialSteps,
		String switchString,
		Map<String, FlussoSchede> flowSwitch
	) {
		this(nomeFlusso, null, initialSteps, switchString, flowSwitch, null);
	}
	public FlussoSchede(
		String nomeFlusso,
		String[] contextUpdateProps,
		ISchedaIstanzaWithContext[] initialSteps
	) {
		this(nomeFlusso, contextUpdateProps, initialSteps, null, null, null);
	}

	public Pair<Collection<JsonElement>, HashMap<String, Object>> carica(WebDal dal, HashMap<String, Object> contesto) throws Exception {
		log.debug(String.format("Inizio caricamento flusso %s", nomeFlusso));

		LinkedList<JsonElement> elementsList = new LinkedList<>();
		HashMap<String, Object> outContesto = new HashMap<>();
		if (initialSteps != null) {
			Pair<Exception, Object> exception = new Pair<Exception,Object>(null, null);
			elementsList.addAll(
				Arrays.asList(initialSteps).stream().map(
					s -> {
						if (exception.getValue0() == null) {
							if (s == null) {
								JsonElement outVal = JsonNull.INSTANCE;
								return outVal;
							}
							else {
								try {
									HashMap<String, Object> newContext = new HashMap<>(contesto);
									JsonElement outVal = s.analizzaCaricaScheda(dal, newContext);
									
									String[] props = s.getContextUpdateProps();
									if (props != null) {
										for (String prop : props) {
											contesto.put(prop, newContext.get(prop));
										}
									}
									return outVal;
								}
								catch (Exception e) {
									exception.setAt0(e);
									e.printStackTrace();
									return null;
								}
							}
						}
						else {
							return null;
						}
					}
				).toList()
			);
			if (exception.getValue0() != null) {
				throw exception.getValue0();
			}
		}


		if (flowSwitch != null && switchString != null) {
			String switchVal = contesto.get(switchString).toString();
			if (flowSwitch.containsKey(switchVal)) {
				FlussoSchede subFlow = flowSwitch.get(switchVal);

				HashMap<String, Object> newContext = new HashMap<>(contesto);
				Pair<Collection<JsonElement>, HashMap<String, Object>> res = subFlow.carica(dal, newContext);
				HashMap<String, Object> resContesto = res.getValue1();
				outContesto.putAll(resContesto);
				elementsList.addAll(res.getValue0());

				//contesto.putAll(resContesto);
				if (subFlow.contextUpdateProps != null) {
					for (String k : subFlow.contextUpdateProps) {
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
			}
			else {
				if (defaultFlow != null) {

					FlussoSchede subFlow = defaultFlow;

					HashMap<String, Object> newContext = new HashMap<>(contesto);
					Pair<Collection<JsonElement>, HashMap<String, Object>> res = subFlow.carica(dal, newContext);
					HashMap<String, Object> resContesto = res.getValue1();
					outContesto.putAll(resContesto);
					elementsList.addAll(res.getValue0());
	
					//contesto.putAll(resContesto);

					// HashMap<String, Object> newContext = new HashMap<>(contesto);
					// elementsList.addAll(subFlow.carica(dal, newContext).getValue0());
					// if (subFlow.contextUpdateProps != null) {
					// 	for (String prop : subFlow.contextUpdateProps) {
					// 		contesto.put(prop, newContext.get(prop));
					// 	}
					// }
					if (subFlow.contextUpdateProps != null) {
						for (String k : subFlow.contextUpdateProps) {
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
				}
			}
		}
		if (this.contextUpdateProps != null) {
			List<String> propList = Arrays.asList(this.contextUpdateProps);
			Stream<String> s1 = propList.stream().filter(s -> contesto.containsKey(s) && contesto.get(s) != null);
			Map<String, Object> map1 = s1.collect(Collectors.toMap((s) -> s, s -> contesto.get(s)));
			outContesto.putAll(map1);
		}
		log.debug(String.format("Fine caricamento flusso %s", nomeFlusso));
		return new Pair<Collection<JsonElement>, HashMap<String, Object>>(elementsList, outContesto);
	}

	
	public Pair<Collection<JsonElement>, HashMap<String, Object>> carica(
		WebDal dal, HashMap<String, Object> contesto,
		int idxScheda
	) throws Exception {
		HashMap<String, Object> newContext = new HashMap<>(contesto);

		Collection<JsonElement> list = carica(
			dal, newContext,
			idxScheda, 0
		);
		
		return new Pair<Collection<JsonElement>,HashMap<String,Object>>(list, newContext);
	}
	private Collection<JsonElement> carica(
		WebDal dal, HashMap<String, Object> contesto,
		int idxScheda, int idxCurr
	) throws Exception {
		log.debug(String.format("Inizio caricamento flusso %s (%d/%d)", nomeFlusso, idxScheda, idxCurr));

		LinkedList<JsonElement> elementsList = new LinkedList<>();
		HashMap<String, Object> outContesto = new HashMap<>();

		MonoProcedure<ISchedaIstanzaWithContext> analisi = s -> {
			HashMap<String, Object> newContext = new HashMap<>(contesto);
			s.analizzaScheda(dal, newContext);
			
			String[] props = s.getContextUpdateProps();
			if (props != null) {
				for (String prop : props) {
					contesto.put(prop, newContext.get(prop));
				}
			}
		};

		MonoProcedure<ISchedaIstanzaWithContext> carica = s -> {
			HashMap<String, Object> newContext = new HashMap<>(contesto);
			JsonElement contenutoScheda = s.analizzaCaricaScheda(dal, newContext);
			elementsList.addLast(contenutoScheda);
			
			String[] props = s.getContextUpdateProps();
			if (props != null) {
				for (String prop : props) {
					contesto.put(prop, newContext.get(prop));
				}
			}
		};

		HashMap<String, Object> appContext = new HashMap<>(contesto);
		int nextIdxCurr = idxCurr;
		if (initialSteps != null) {
			int initLen = initialSteps.length;
			for (int i = 0; i < initLen; i++) {
				log.debug(String.format("Flusso %s - analisi passi iniziali %d/%d", nomeFlusso, i, initLen));
				ISchedaIstanzaWithContext scheda = initialSteps[i];
				if (scheda == null) {
					//throw new FoliageException("Scheda non configurata");
					elementsList.addLast(JsonNull.INSTANCE);
				}
				else {
					if (nextIdxCurr >= idxScheda) {
						carica.eval(scheda);
					}
					else {
						analisi.eval(scheda);
					}
				}
				nextIdxCurr++;
			}
		}
		if (flowSwitch != null && switchString != null) {
			String switchVal = contesto.get(switchString).toString();
			if (flowSwitch.containsKey(switchVal)) {
				FlussoSchede subFlow = flowSwitch.get(switchVal);
				Collection<JsonElement> list = subFlow.carica(dal, contesto, idxScheda - nextIdxCurr, 0);
				elementsList.addAll(list);
			}
			else {
				if (defaultFlow != null) {
					FlussoSchede subFlow = defaultFlow;
					Collection<JsonElement> list = subFlow.carica(dal, contesto, idxScheda - nextIdxCurr, 0);
					elementsList.addAll(list);
				}
			}
		}

		log.debug(String.format("Fine caricamento flusso %s (%d/%d)", nomeFlusso, idxScheda, idxCurr));
		return elementsList;
	}

	public Pair<Collection<JsonElement>, HashMap<String, Object>> salvaScheda(
		WebDal dal, HashMap<String, Object> contesto, JsonElement datiScheda,
		int idxScheda
	) throws Exception {
		// ISchedaIstanzaWithContext scheda = findScheda(dal, appContext, idxScheda, 0);
		// scheda.salvaScheda(dal, appContext, datiScheda);
		HashMap<String, Object> appContext = new HashMap<>(contesto);
		// HashMap<String, Object> loadContext = new HashMap<>(contesto);
		cancella(dal, contesto, idxScheda);
		Collection<JsonElement> outSchede =  salvaScheda(dal, appContext, datiScheda, idxScheda, 0);
		
		//return carica(dal, loadContext, idxScheda);

		Pair<Collection<JsonElement>, HashMap<String, Object>> outPair = new Pair<Collection<JsonElement>,HashMap<String,Object>>(outSchede, appContext);
		registraSalvataggioScheda(dal, contesto, idxScheda);
		return outPair;
	}
	public void cancella(WebDal dal, HashMap<String, Object> contesto) throws Exception {
		cancella(
			dal, contesto,
			0, 0
		);
	}
	public void cancella(WebDal dal, HashMap<String, Object> contesto, int idxSchedaStart) throws Exception {
		HashMap<String, Object> newContext = new HashMap<>(contesto);
		cancella(
			dal, newContext,
			idxSchedaStart, 0
		);
	}
	private void cancella(
		WebDal dal, HashMap<String, Object> contesto,
		int idxScheda, int idxCurr
	) throws Exception {
		log.debug(String.format("Inizio cancellazione flusso %s (%d/%d)", nomeFlusso, idxScheda, idxCurr));
		MonoProcedure<ISchedaIstanzaWithContext> analisi = s -> {
			HashMap<String, Object> newContext = new HashMap<>(contesto);
			s.analizzaScheda(dal, newContext);
			
			String[] props = s.getContextUpdateProps();
			if (props != null) {
				for (String prop : props) {
					contesto.put(prop, newContext.get(prop));
				}
			}
		};

		HashMap<String, Object> appContext = new HashMap<>(contesto);
		int nextIdxCurr = idxCurr;
		if (initialSteps != null) {
			int initLen = initialSteps.length;
			for (int i = 0; i < initLen; i++) {
				log.debug(String.format("Flusso %s - analisi passi iniziali %d/%d", nomeFlusso, i, initLen));
				ISchedaIstanzaWithContext scheda = initialSteps[i];
				if (scheda == null) {
					//throw new FoliageException("Scheda non configurata");
					log.debug("Scheda non configurata");
				}
				else {
					analisi.eval(scheda);
				}
				nextIdxCurr++;
			}
		}
		if (flowSwitch != null && switchString != null) {
			String switchVal = contesto.get(switchString).toString();
			if (flowSwitch.containsKey(switchVal)) {
				FlussoSchede subFlow = flowSwitch.get(switchVal);
				subFlow.cancella(dal, contesto, idxScheda, nextIdxCurr);
			}
			else {
				if (defaultFlow != null) {
					FlussoSchede subFlow = defaultFlow;
					subFlow.cancella(dal, contesto, idxScheda, nextIdxCurr);
				}
			}
		}


		if (initialSteps != null) {
			int initLen = initialSteps.length;
			for (int i = initLen - 1; i >= 0; i--) {
				if (idxCurr + i >= idxScheda) {
					log.debug(String.format("Flusso %s - cancellazione passi iniziali %d", nomeFlusso, i));
					ISchedaIstanzaWithContext scheda = initialSteps[i];
					if (scheda == null) {
						//throw new FoliageException("Scheda non configurata");
						log.debug("Scheda non configurata");
					}
					else {
						registraCancellazioneScheda(dal, appContext, idxCurr + i);
						scheda.cancellaScheda(dal, appContext);
					}
				}
			}
		}
		log.debug(String.format("Fine cancellazione flusso %s (%d/%d)", nomeFlusso, idxScheda, idxCurr));
	}

	private void registraCancellazioneScheda(
		WebDal dal, HashMap<String, Object> contesto, Integer idxScheda) {
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("idIstanza", contesto.get("idIstanza"));
		pars.put("idxScheda", idxScheda);
		
		String delSql = """
delete from FOLIAGE2.FLGISTA_SCHEDE_SALVATE_TAB
where ID_ISTA = :idIstanza
	and PROG_SCHEDA = :idxScheda""";

		int nRows = dal.update(delSql, pars);
		
		log.debug(String.format("Eliminati %d record di traccia salvataggio per la scheda %d", nRows, idxScheda));
	}

	private void registraSalvataggioScheda(WebDal dal, HashMap<String, Object> contesto, Integer idxScheda) {
		HashMap<String, Object> pars = new HashMap<>();
		pars.put("idIstanza", contesto.get("idIstanza"));
		pars.put("idUtente", contesto.get("idUtente"));
		pars.put("currentDateTime", contesto.get("currentDateTime"));
		pars.put("idxScheda", idxScheda);

		String insSql = """
insert into FOLIAGE2.FLGISTA_SCHEDE_SALVATE_TAB(ID_ISTA, PROG_SCHEDA, DATA_ULTIMO_SALVATAGGIO, ID_UTENTE_SALVATAGGIO)
	values (:idIstanza, :idxScheda, :currentDateTime, :idUtente)""";

		int nRows = dal.update(insSql, pars);
			
		log.debug(String.format("Caricati %d record di traccia salvataggio per la scheda %d", nRows, idxScheda));
	}

	private Collection<JsonElement> salvaScheda(
		WebDal dal, HashMap<String, Object> contesto, JsonElement datiScheda,
		int idxScheda, int idxCurr
	) throws Exception {
		log.debug(String.format("Inizio salvataggio flusso %s", nomeFlusso));

		LinkedList<JsonElement> outVal = new LinkedList<>();
		MonoProcedure<ISchedaIstanzaWithContext> analisi = s -> {
			HashMap<String, Object> newContext = new HashMap<>(contesto);
			s.analizzaScheda(dal, newContext);
			
			String[] props = s.getContextUpdateProps();
			if (props != null) {
				for (String prop : props) {
					contesto.put(prop, newContext.get(prop));
				}
			}
		};

		MonoProcedure<ISchedaIstanzaWithContext> carica = s -> {
			HashMap<String, Object> newContext = new HashMap<>(contesto);
			JsonElement contenutoScheda = s.analizzaCaricaScheda(dal, newContext);
			outVal.addLast(contenutoScheda);
			
			String[] props = s.getContextUpdateProps();
			if (props != null) {
				for (String prop : props) {
					contesto.put(prop, newContext.get(prop));
				}
			}
		};
		if (initialSteps != null) {
			int initLen = initialSteps.length;
			int pos = idxScheda - idxCurr;
			if (pos >= 0) {
				if (pos < initLen) {
					for (int i = 0; i < initLen; i++) {
						ISchedaIstanzaWithContext scheda = initialSteps[i];
						if (scheda == null) {
							if (i == pos) {
								throw new FoliageException("Scheda non configurata");
							}
						}
						else {
							if (i < pos) {
								analisi.eval(scheda);
								//nextIdx++;
							}
							else {
								if (i == pos) {
									HashMap<String, Object> newContext = new HashMap<>();
									newContext.putAll(contesto);
									scheda.salvaScheda(dal, newContext, datiScheda);
									String[] props = scheda.getContextUpdateProps();
									if (props != null) {
										for (String k : props) {
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

									newContext.clear();
									newContext.putAll(contesto);
									JsonElement contenutoNuovaScheda = scheda.analizzaCaricaScheda(dal, newContext);
									if (props != null) {
										for (String k : props) {
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

									outVal.addLast(contenutoNuovaScheda);
		
								}
								else {
									carica.eval(scheda);
								}
							}
						}
					}
					if (flowSwitch != null && switchString != null) {
						String switchVal = contesto.get(switchString).toString();
						if (flowSwitch.containsKey(switchVal)) {
							FlussoSchede subFlow = flowSwitch.get(switchVal);
							Pair<Collection<JsonElement>, HashMap<String, Object>> pair = subFlow.carica(dal, contesto);
							outVal.addAll(pair.getValue0());
						}
						else {
							if (defaultFlow != null) {
								FlussoSchede subFlow = defaultFlow;
								Pair<Collection<JsonElement>, HashMap<String, Object>> pair = subFlow.carica(dal, contesto);
								outVal.addAll(pair.getValue0());
							}
						}
					}
				}
			}
			idxScheda -= initLen;
		}

		if (flowSwitch != null && switchString != null) {
			String switchVal = contesto.get(switchString).toString();
			if (flowSwitch.containsKey(switchVal)) {
				FlussoSchede subFlow = flowSwitch.get(switchVal);

				HashMap<String, Object> newContext = new HashMap<>(contesto);
				Collection<JsonElement> outVal1 = subFlow.salvaScheda(dal, newContext, datiScheda,  idxScheda, 0);
				if (subFlow.contextUpdateProps != null) {
					for (String k : subFlow.contextUpdateProps) {
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
				outVal.addAll(outVal1);
			}
			else {
				if (defaultFlow != null) {
					FlussoSchede subFlow = defaultFlow;

					HashMap<String, Object> newContext = new HashMap<>(contesto);
					Collection<JsonElement> outVal1 = subFlow.salvaScheda(dal, newContext, datiScheda, idxScheda, 0);
					if (subFlow.contextUpdateProps != null) {
						for (String k : subFlow.contextUpdateProps) {
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
					outVal.addAll(outVal1);
				}
			}
		}
		log.debug(String.format("Fine salvataggio flusso %s", nomeFlusso));
		return outVal;
	}
}
