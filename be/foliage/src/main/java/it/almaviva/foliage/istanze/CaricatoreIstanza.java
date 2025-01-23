package it.almaviva.foliage.istanze;


import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.javatuples.Pair;

import com.google.gson.JsonElement;

import it.almaviva.foliage.function.Procedure;
import it.almaviva.foliage.istanze.db.CampoSelect;
import it.almaviva.foliage.istanze.db.CondizioneEq;
import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.istanze.db.IOperazioneDb;
import it.almaviva.foliage.istanze.db.RecuperoDb;
import it.almaviva.foliage.services.WebDal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CaricatoreIstanza {
	public ISchedaIstanzaWithContext[] schede;


	public RecuperoDb[] preparaContesto;
	
	public CaricatoreIstanza(ISchedaIstanzaWithContext[] schede) {
		this.schede = schede;
	}

	public static Pair<Collection<JsonElement>, HashMap<String, Object>> salvaScheda(
		//NamedParameterJdbcTemplate template, String codRegione,
		WebDal dal, String codRegione,
		String codIstanza, JsonElement contenuto, int idxScheda, Integer idUtente, Integer sridGeometrie
	) throws Exception {

		log.debug(
			String.format("\n\nInizio CaricatoreIstanza.salvaScheda per scheda %d per istanza %s", idxScheda, codIstanza)
		);

		HashMap<String, Object> contesto = new HashMap<>();
		contesto.put("codIstanza", codIstanza);
		contesto.put("codRegione", codRegione);
		contesto.put("sridGeometrie", sridGeometrie);
		contesto.put("idUtente", idUtente);
		contesto.put("currentDateTime", LocalDateTime.now());
		HashMap<String, Object> loadContext = new HashMap<>(contesto);

		if (InizializzazioneContesto != null) {
			log.debug(
				String.format("\n\nInizio carica contesto 1 per CaricatoreIstanza.salvaScheda per scheda %d per istanza %s", idxScheda, codIstanza)
			);

			for (IOperazioneDb iOperazioneDb : InizializzazioneContesto) {
				iOperazioneDb.applica(dal, contesto);
			}
			
			log.debug(
				String.format("Fine carica contesto 1 per CaricatoreIstanza.salvaScheda per scheda %d per istanza %s\n\n", idxScheda, codIstanza)
			);
		}

		log.debug(
			String.format("\n\nInizio salva flusso per CaricatoreIstanza.salvaScheda per scheda %d per istanza %s", idxScheda, codIstanza)
		);
		Flusso.salvaScheda(dal, contesto, contenuto, idxScheda);
		log.debug(
			String.format("Fine salva flusso per CaricatoreIstanza.salvaScheda per scheda %d per istanza %s\n\n", idxScheda, codIstanza)
		);
		
		
		if (InizializzazioneContesto != null) {
			log.debug(
				String.format("\n\nInizio carica contesto 2 per CaricatoreIstanza.salvaScheda per scheda %d per istanza %s", idxScheda, codIstanza)
			);

			for (IOperazioneDb iOperazioneDb : InizializzazioneContesto) {
				iOperazioneDb.applica(dal, loadContext);
			}
			log.debug(
				String.format("Fine carica contesto 2 per CaricatoreIstanza.salvaScheda per scheda %d per istanza %s\n\n", idxScheda, codIstanza)
			);
		}
		//return Flusso.carica(template, loadContext, idxScheda);
		Pair<Collection<JsonElement>, HashMap<String, Object>>  outVal = Flusso.carica(dal, loadContext);
		
		log.debug(
			String.format("Fine CaricatoreIstanza.salvaScheda per scheda %d per istanza %s\n\n", idxScheda, codIstanza)
		);
	
		return outVal;

	}

	public static Pair<Collection<JsonElement>, HashMap<String, Object>> load(WebDal dal, String codRegione, String codIstanza, Integer sridGeometrie) throws Exception{
		HashMap<String, Object> contesto = new HashMap<>();
		contesto.put("codIstanza", codIstanza);
		contesto.put("codRegione", codRegione);
		contesto.put("sridGeometrie", sridGeometrie);

		if (InizializzazioneContesto != null) {
			for (IOperazioneDb iOperazioneDb : InizializzazioneContesto) {
				iOperazioneDb.applica(dal, contesto);
			}
		}
		return Flusso.carica(dal, contesto, 0);
	}

	public static IOperazioneDb[] InizializzazioneContesto;
	public static void Inizializza() throws Exception {
		InizializzazioneContesto = new IOperazioneDb[] {
			new RecuperoDb(
				"""
foliage2.flgista_tab i
	join foliage2.flgtipo_istanza_tab t on (t.id_tipo_istanza = i.id_tipo_istanza)
	join foliage2.flgcist_tab c on (c.id_cist = t.id_cist)
	left join foliage2.flgente_root_tab e on (e.id_ente = i.id_ente_terr)
	left join foliage2.flglimiti_amministrativi_tab la on (la.id_ente_terr = i.id_ente_terr)""",
				new CampoSelect[] {
					new CampoSelect(
						null,
						"id_ista", "idIstanza",
						DbUtils.GetInteger
					),
					new CampoSelect(
						null,
						"id_titolare", "idTitolareIstanza",
						DbUtils.GetInteger
					),
					new CampoSelect(
						null,
						"cod_tipo_istanza_specifico", "codTipoIstanzaSpecifico",
						DbUtils.Get
					),
					new CampoSelect(
						null,
						"cod_tipo_istanza", "codTipoIstanza",
						DbUtils.Get
					),
					new CampoSelect(
						null,
						"desc_cist", "descTipoIstanza",
						DbUtils.Get
					),
					new CampoSelect(
						null,
						"nome_istanza_specifico", "descTipoIstanzaSpecifico",
						DbUtils.Get
					),
					new CampoSelect(
						null,
						"id_qual", "idQualificaTitolare",
						DbUtils.GetInteger
					),
					new CampoSelect(
						null,
						"id_tprp", "idTipoProprieta",
						DbUtils.GetInteger
					),
					new CampoSelect(
						null,
						"id_nprp", "idNaturaProprieta",
						DbUtils.GetInteger
					),
					new CampoSelect(
						null,
						"id_tazi", "idTipoAzienda",
						DbUtils.GetInteger
					),
					new CampoSelect(
						null,
						"nome_ista", "nomeIstanza",
						DbUtils.Get
					),
					new CampoSelect(
						null,
						"e.tipo_ente", "tipoEnte",
						DbUtils.Get
					),
					new CampoSelect(
						null,
						"nome_ente", "nomeEnte",
						DbUtils.Get
					),
					new CampoSelect(
						"desc_ente",
						"e.tipo_ente||' '||e.nome_ente", "descEnteCompetente",
						DbUtils.Get
					),
					new CampoSelect(
						null,
						"i.note", "noteIstanza",
						DbUtils.Get
					),
					new CampoSelect(
						"shape_ente",
						"ST_AsText(la.shape_vinc)", "shapeEnte",
						DbUtils.Get
					),
					new CampoSelect(
						"shape_envel_ente",
						"ST_AsText(la.shape_envelope_vinc)", "boxInquadrEnte",
						DbUtils.Get
					),
					new CampoSelect(
						null,
						"la.srid", "sridShapeEnte",
						DbUtils.Get
					),
					new CampoSelect(
						null,
						"i.id_scheda_intervento", "idSchedaInterventoSelezionata",
						DbUtils.Get
					)
				},
				new CondizioneEq[] {
					new CondizioneEq("i.codi_ista", "codIstanza")
				}
			),
			new RecuperoDb(
				"FOLIAGE2.FLGPART_CATASTALI_TAB",
				new CampoSelect[] {
					new CampoSelect(
						"SUPERFICIE_TOTALE",
						"coalesce(sum(SUPERFICIE), 0)", "superficieTotCatastale",
						DbUtils.GetInteger
					),
					new CampoSelect(
						"SUPERFICIE_INTERVENTO",
						"coalesce(sum(SUPERFICIE_INTERVENTO), 0)", "superficieTotIntervento",
						DbUtils.GetInteger
					)
				},
				new CondizioneEq[] {
					new CondizioneEq("ID_ISTA", "idIstanza")
				}
			)
		};
	}
	public static final String [] ContestoGenerico = new  String [] {
		"idIstanza", "codIstanza", "idTitolareIstanza", "descTipoIstanza", "descTipoIstanzaSpecifico", "nomeIstanza",
		"descEnteCompetente", "shapeEnte", "boxInquadrEnte", "sridShapeEnte", "codiceFiscaleTitolare", "nomeTitolare",
		"cognomeTitolare", "codTipoIstanza", "codTipoIstanzaSpecifico", "superficieTotCatastale", "superficieTotIntervento"//, "idIstanza", "idIstanza", "idIstanza"
	};
	public static final String [] ContestoTaglioBoschivo = new  String [] {
		"tipoDiSoprasuolo"
	};
	public static FlussoSchede Flusso = new FlussoSchede(
		"FlussoGenerico",
		ContestoGenerico,
		new ISchedaIstanzaWithContext[] {
			SchedaIstanza.TipologiaIstanza,
			SchedaIstanza.Titolare,
			SchedaIstanza.TipoGestione,
			SchedaIstanza.ParticelleCatastali
		},
		"codTipoIstanza",
		Map.ofEntries(
			new AbstractMap.SimpleEntry<String,FlussoSchede>(
				"SOTTO_SOGLIA",
				new FlussoSchede(
					"Flusso Sotto Soglia",
					new ISchedaIstanzaWithContext[] {
						SchedaIstanza.ParticellaForestaleSottoSoglia,
						SchedaIstanza.VerificaNat2kSottosoglia
					},
					"codTipoIstanzaSpecifico",
					Map.ofEntries(
						new AbstractMap.SimpleEntry<String,FlussoSchede>(
							"TAGLIO_BOSCHIVO",
							new FlussoSchede(
								"Flusso Taglio Boschivo",
								ContestoTaglioBoschivo,
								new ISchedaIstanzaWithContext[] {
									SchedaIstanza.SoprasuoloBoschivo,
									SchedaIstanza.DestinazioniUsoBoschivo
								}
							)
						),
						new AbstractMap.SimpleEntry<String,FlussoSchede>(
							"INTERVENTO_A_COMUNICAZIONE",
							new FlussoSchede(
								"Flusso Intervento Comunicazione",
								null,
								new ISchedaIstanzaWithContext[] {
									SchedaIstanza.DettagliInterventoComunicazione
								}
							)
						)
					)
				)
			)
		),
		new FlussoSchede(
			"Flusso Non Sotto Soglia",
			null,
			new ISchedaIstanzaWithContext[] {
				SchedaIstanza.ParticellaForestaleSopraSoglia,
				SchedaIstanza.StazioneForestale,
				SchedaIstanza.VincolisticaSopraSoglia,
				SchedaIstanza.UnitaOmogenee,
				SchedaIstanza.AltriStratiInformativi,
				SchedaIstanza.ViabilitaForestale,
				SchedaIstanza.DestinazioniUsoEProspettiRiepilogativi,
				SchedaIstanza.Allegati,
				SchedaIstanza.Riepilogo
			}
		)
	);
}
