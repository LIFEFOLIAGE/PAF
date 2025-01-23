package it.almaviva.foliage.istanze;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.RowMapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.almaviva.foliage.function.BiFunction;
import it.almaviva.foliage.function.BiProcedure;
import it.almaviva.foliage.function.JsonIO;
import it.almaviva.foliage.istanze.db.AggiornamentoDb;
import it.almaviva.foliage.istanze.db.CampoSelect;
import it.almaviva.foliage.istanze.db.CampoSet;
import it.almaviva.foliage.istanze.db.CaricamentoArray;
import it.almaviva.foliage.istanze.db.CaricamentoRecord;
import it.almaviva.foliage.istanze.db.CaricamentoFileBase64;
import it.almaviva.foliage.istanze.db.CaricamentoQuery;
import it.almaviva.foliage.istanze.db.CondizioneEq;
import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.istanze.db.EliminazioneDb;
import it.almaviva.foliage.istanze.db.IOperazioneDb;
import it.almaviva.foliage.istanze.db.RecuperoArray;
import it.almaviva.foliage.istanze.db.RecuperoDb;
import it.almaviva.foliage.istanze.db.RecuperoFileBase64;
import it.almaviva.foliage.services.WebDal;
import it.almaviva.foliage.bean.Base64FormioFile;

public abstract class SchedaIstanza {
	// protected NamedParameterJdbcTemplate  dal;
	// public SchedaIstanza(NamedParameterJdbcTemplate  dal) {
	// 	this.dal = dal;
	// }
	public static void initSchedaIstanza(String regionName) throws Exception {
		TipologiaIstanza = GetSchedaTipologiaIstanza();
		Titolare = GetSchedaTitolare();
		TipoGestione = GetSchedaTipoGestione();
		ParticelleCatastali = GetParticelleCatastali();
		ParticellaForestaleSottoSoglia = GetParticellaForestaleSottoSoglia();
		VerificaNat2kSottosoglia = GetVerificaNat2kSottosoglia();
		SoprasuoloBoschivo = GetSoprasuoloBoschivo();
		DestinazioniUsoBoschivo = GetDestinazioniUsoBoschivo();
		DettagliInterventoComunicazione = GetDettagliInterventoComunicazione();
		ParticellaForestaleSopraSoglia = GetParticellaForestaleSopraSoglia(regionName);
		StazioneForestale = GetStazioneForestale();
		VincolisticaSopraSoglia = GetVincolisticaSopraSoglia();
		SingolaUnitaOmogenea = GetSingolaUnitaOmogenea();
		UnitaOmogenee = GetUnitaOmogenee();
		AltriStratiInformativi = GetAltriStratiInformativi();
		ViabilitaForestale = GetViabilitaForestale();
		DestinazioniUsoEProspettiRiepilogativi = GetDestinazioniUsoEProspettiRiepilogativi();
		Allegati = GetAllegati();
		Riepilogo = GetRiepilogo();
	}
	private static final SchedaIstanzaGenerica GetRiepilogo() throws Exception {

		return new SchedaIstanzaGenerica(
			"Riepilogo",
			new CorrispondenzaJson(
				new CorrispondenzaJsonDiretta[] {
					new CorrispondenzaJsonDiretta("nome_pgf", JsonIO.StringIo),
					new CorrispondenzaJsonDiretta("nome_comp", JsonIO.StringIo),
					new CorrispondenzaJsonDiretta("oggetto", JsonIO.StringIo),
					new CorrispondenzaJsonDiretta("supportoFinanziario", JsonIO.NumberIo),
					new CorrispondenzaJsonDiretta("denominazioneFondo", JsonIO.StringIo),
					new CorrispondenzaJsonDiretta("superficieTotale", JsonIO.DecimalIo),
					new CorrispondenzaJsonDiretta("superficieUtile", JsonIO.DecimalIo),
					new CorrispondenzaJsonDiretta("superficieImproduttiva", JsonIO.DecimalIo),
					new CorrispondenzaJsonDiretta("superficieGeometrica", JsonIO.DecimalIo)
				}
			),
			new IOperazioneDb[]{
				new RecuperoDb(
					"""
(
		select superficie, superficie_utile, superficie - superficie_utile as superficie_improduttiva
		from (
				select sum(superficie_utile) as superficie_utile,
					sum(superficie) as superficie
				from foliage2.flgunita_omogenee_tab fot
				where ID_ISTA = :idIstanza
			) as T1
	) as T""",
					new CampoSelect[] {
						new CampoSelect("superficie", "superficieTotale", DbUtils.GetDecimal ),
						new CampoSelect("superficie_utile", "superficieUtile", DbUtils.GetDecimal ),
						new CampoSelect("superficie_improduttiva", "superficieImproduttiva", DbUtils.GetDecimal )
					},
					null,
					new String[] {"idIstanza"}
				),
				new RecuperoDb(
					"FOLIAGE2.FLGPARTICELLA_FORESTALE_SHAPE_TAB",
					new CampoSelect[] {
						new CampoSelect(
							"SUPERFICIE_TOTALE",
							"coalesce(sum(SUPERFICIE), 0)", "superficieGeometrica",
							DbUtils.GetDecimal
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("ID_ISTA", "idIstanza")
					}
				),
				new RecuperoDb(
					"foliage2.FLGSUPPORTO_FINANZIARIO_ISTA_TAB",
					new CampoSelect[] {
						new CampoSelect("COD_TIPO_FINANZIAMENTO", "supportoFinanziario", DbUtils.GetInteger ),
						new CampoSelect("DENOM_FONDO", "denominazioneFondo", DbUtils.Get )
					},
					new CondizioneEq[] {
						new CondizioneEq("ID_ISTA", "idIstanza")
					}
				),
				new RecuperoDb(
					"foliage2.FLGATTUAZIONE_PIANI_ISTA_TAB",
					new CampoSelect[] {
						new CampoSelect("NOME_PGF", "nome_pgf", DbUtils.Get ),
						new CampoSelect("NOME_COMPRESA_FORESTALE", "nome_comp", DbUtils.Get ),
						new CampoSelect("OGGETTO", "oggetto", DbUtils.Get )
					},
					new CondizioneEq[] {
						new CondizioneEq("ID_ISTA", "idIstanza")
					}
				)
			},// operazioniCaricamento,
			new IOperazioneDb[]{
				new CaricamentoQuery(
					"foliage2.FLGATTUAZIONE_PIANI_ISTA_TAB",
					"""
select *
from (
		values(:nome_comp, :nome_pgf, :oggetto)
	) as T(NOME_PGF, NOME_COMPRESA_FORESTALE, OGGETTO)
where NOME_PGF is not null
	and NOME_COMPRESA_FORESTALE is not null
	and OGGETTO is not null""",
					new CampoSet[] {
						new CampoSet("ID_ISTA", "idIstanza"),
						new CampoSet("NOME_PGF", null, "NOME_PGF"),
						new CampoSet("NOME_COMPRESA_FORESTALE", null, "NOME_COMPRESA_FORESTALE"),
						new CampoSet("OGGETTO", null, "OGGETTO")
					},
					null,
					new String[] {"nome_comp", "nome_pgf", "oggetto"}
				),
				new CaricamentoRecord(
					"foliage2.FLGSUPPORTO_FINANZIARIO_ISTA_TAB",
					new CampoSet[] {
						new CampoSet("ID_ISTA", "idIstanza"),
						new CampoSet("COD_TIPO_FINANZIAMENTO", "supportoFinanziario"),
						new CampoSet("DENOM_FONDO", "denominazioneFondo")
					}
				)
			},//operazioniSalvataggio,
			new IOperazioneDb[]{
				new EliminazioneDb(
					"foliage2.FLGATTUAZIONE_PIANI_ISTA_TAB",
					new CondizioneEq[] {
						new CondizioneEq("ID_ISTA", "idIstanza")
					}
				),
				new EliminazioneDb(
					"foliage2.FLGSUPPORTO_FINANZIARIO_ISTA_TAB",
					new CondizioneEq[] {
						new CondizioneEq("ID_ISTA", "idIstanza")
					}
				)
			},//operazioniCancellazione,
			null// contextUpdateProps
		);
	}
	private static final SchedaIstanzaGenerica GetAllegati() throws Exception {
		BiFunction<RowMapper<Object>, WebDal, HashMap<String, Object>> funRowMapper = 
			(WebDal dal, HashMap<String, Object> contesto) -> {
				return (ResultSet rs, int rn) -> {
					final Integer idFile = rs.getInt("id_file_allegato");

					HashMap<String, Object> outVal = new HashMap<>();
					outVal.put("fileAllegato", DbUtils.getBase64FormioFiles(dal, idFile));
					outVal.put("categoriaAllegato", rs.getString("cod_tipo_allegato"));
					outVal.put("nomeDocumento", rs.getString("desc_altro_allegato"));
					return outVal;
				};
			};

		BiFunction<BiProcedure<HashMap<String, Object>, Object>, WebDal, HashMap<String, Object>> funRowSetter = 
			(WebDal dal, HashMap<String, Object> contesto1) -> {
				JsonObject elem = (JsonObject)contesto1.get("elem");
				String categoria = elem.get("categoriaAllegato").getAsString();
				JsonElement ee = elem.get("nomeDocumento");
				String nomeDocumento = null;
				if (ee != null && !ee.isJsonNull()) {
					nomeDocumento = ee.getAsString();
				}
				final String nomeDocumentoFin = nomeDocumento;

				Base64FormioFile[] files = (Base64FormioFile[])JsonIO.Base64FormioFileIo.loader.exec(elem, "fileAllegato");

				final Integer idFile = DbUtils.saveBase64FormioFiles(dal, files);
				return (HashMap<String, Object> contesto2, Object s) -> {
					//HashMap<String, Object> x = (HashMap<String, Object>)s;
					//contesto2.putAll(x);
					contesto2.put("idFileAllegato", idFile);
					contesto2.put("codTipoAllegato", categoria);
					contesto2.put("descAltroAllegato", nomeDocumentoFin);
				};
			};
		return new SchedaIstanzaGenerica(
			"allegati",
			new CorrispondenzaJson(
				new CorrispondenzaJsonDiretta[] {
					new CorrispondenzaJsonDiretta("documentiAllegati", JsonIO.ArrayIo)
				}
			),
			new IOperazioneDb[] {
				new RecuperoArray<Object>(
					funRowMapper,
					"""
select COD_TIPO_ALLEGATO, DESC_ALTRO_ALLEGATO, ID_FILE_ALLEGATO
from foliage2.FLGALLEGATI_ISTA_TAB
where ID_ISTA = :idIstanza
order by ID_ALLEGATO_ISTA""",
					"documentiAllegati", 
					new String[] {"idIstanza"}
				)
			},//IOperazioneDb[] operazioniCaricamento,
			new IOperazioneDb[] {
				new CaricamentoArray<>(
					"""
insert into foliage2.FLGALLEGATI_ISTA_TAB(ID_ISTA, COD_TIPO_ALLEGATO, DESC_ALTRO_ALLEGATO, ID_FILE_ALLEGATO)
	values (:idIstanza, :codTipoAllegato, :descAltroAllegato, :idFileAllegato)""",
					"progIdx",
					"elem",
					"documentiAllegati",
					funRowSetter,
					new String[] {"documentiAllegati", "idIstanza", "codTipoAllegato", "descAltroAllegato", "idFileAllegato"}
				)
			},//IOperazioneDb[] operazioniSalvataggio,
			new IOperazioneDb[]{
				new EliminazioneDb(
					"foliage2.FLGALLEGATI_ISTA_TAB",
					new CondizioneEq[] {
						new CondizioneEq("ID_ISTA", "idIstanza")
					}
				)
			},//IOperazioneDb[] operazioniCancellazione,
			null//String[] contextUpdateProps
		);
	}
	private static final SchedaIstanzaGenerica GetDestinazioniUsoUnitaOmogeneaSingola() throws Exception {
		//new CorrispondenzaJsonDiretta[]

		String[] elenchi = new String[] {"0", "1"};
		String[] assortimenti = new String[] {"LEGNA", "COMBUSTIBILE", "TRONCHI", "CELLULOSA", "ALTRO"};
		String[] dest = new String[] {"PercAutoc", "PercVendita"};

		String[] categCubatura = new String[] { "volTOTALE", "volFUSTAIA", "volCEDUO", "totFUSTAIA", "totCEDUO", "POLL", "ALL", "MATR2", "MATR3", "specie0", "specie1"};
		String[] gruppiCubatura = new String[] {"PRES", "RILASCIA", "TAGLIA"};
		String[] valoriCubatura = new String[] {"N", "Mc"};


		LinkedList<String> assCubatura = new LinkedList<>();
		

		LinkedList<String> valuesCubatura = new LinkedList<>();
		LinkedList<String> pivotCubatura = new LinkedList<>();
		LinkedList<CampoSelect> campiSelectRecuperoCubatura = new LinkedList<>();
		for (String cat : categCubatura) {
			for (String gruppo : gruppiCubatura) {
				String[] nomePar = new String[2];
				int i = 0;
				for (String val : valoriCubatura) {
					String parName = String.format("%s%s%s", cat, gruppo, val);
					nomePar[i++] = parName;
					assCubatura.addLast(parName);
					String colonna = (val.equals("N")) ? "VALORE_NUM_HA" : "VALORE_MQ_HA";
					pivotCubatura.addLast(
						String.format(
							"max(case when vc.CAT_CUBATURA = '%s' and vc.COD_GRUPPO_CUBATURA = '%s'  then %s end) as %s",
							cat,
							gruppo,
							colonna,
							parName
						)
					);
					
					campiSelectRecuperoCubatura.addLast(
						new CampoSelect(
							parName,
							parName,
							DbUtils.GetDecimal
						)
					);
				}
				
				valuesCubatura.addLast(String.format("('%s', '%s', :%s::numeric, :%s::numeric)", cat, gruppo, nomePar[0], nomePar[1]));
				
			}
		}

		String querySalvataggioCubatura = String.format(
			"""
select CAT_CUBATURA, COD_GRUPPO_CUBATURA, VALORE_NUM_HA, VALORE_MQ_HA
from (
		values%s
	) T (CAT_CUBATURA, COD_GRUPPO_CUBATURA, VALORE_NUM_HA, VALORE_MQ_HA)
where VALORE_NUM_HA is not null 
	or VALORE_MQ_HA is not null""", 
					valuesCubatura.stream().collect(Collectors.joining("""
,
		"""
				)
			)
		);

		String[] arrPropCubatura = assCubatura.toArray(new String[0]);
		CaricamentoQuery salvataggioCubatura = new CaricamentoQuery("foliage2.FLGUNITA_OMOGENEE_VAL_CUBATURA_TAB", 
			querySalvataggioCubatura,
			new CampoSet[] {
				new CampoSet("ID_ISTA", "idIstanza"),
				new CampoSet("PROG_UOG", "progIdx"),
				new CampoSet("CAT_CUBATURA", null, "CAT_CUBATURA"),
				new CampoSet("COD_GRUPPO_CUBATURA", null, "COD_GRUPPO_CUBATURA"),
				new CampoSet("VALORE_NUM_HA", null, "VALORE_NUM_HA"),
				new CampoSet("VALORE_MQ_HA", null, "VALORE_MQ_HA")
			},
			null,
			arrPropCubatura
		);


		String queryAcquisizioneCubatura = String.format(
			"""
(
		select %s
		--select *
		from foliage2.FLGUNITA_OMOGENEE_VAL_CUBATURA_TAB vc
		where ID_ISTA = :idIstanza
			and PROG_UOG = :progUo
	) as T""", 
					pivotCubatura.stream().collect(Collectors.joining("""
,
			"""
				)
			)
		);

		RecuperoDb aquisizioneCubatura = new RecuperoDb(
			queryAcquisizioneCubatura, 
			campiSelectRecuperoCubatura.toArray(new CampoSelect[0]), 
			null,
			new String[] {"idIstanza", "progUo"}
		);

		LinkedList<String> assAssortimenti = new LinkedList<>();


		LinkedList<String> valuesAssortimenti = new LinkedList<>();
		LinkedList<String> pivotAssortimenti = new LinkedList<>();
		LinkedList<CampoSelect> campiSelectSecondoRecuperoAssortimenti = new LinkedList<>();
		for (String e : elenchi) {
			for (String a : assortimenti) {
				for (String d : dest) {
					String assEntry = String.format("%s%s%s", a, d, e);
					valuesAssortimenti.addLast(String.format("('%s', '%s', :specie%s::int, :%s::float)", a, d, e, assEntry));
					pivotAssortimenti.addLast(
						String.format(
							//max(case when ft.nome_assortimento = 'LEGNA' and a.is_autoconsumo = true and PROG = 0 then percentuale_ass end) as LEGNAPercAutoc0
							"max(case when ft.nome_assortimento = '%s' and a.is_autoconsumo = %s and PROG_SPECIE_UOG = %s then percentuale_ass end) as %s",
							a,
							(d.equals("PercAutoc") ? "true" : "false"),
							e,
							assEntry
						)
					);
					//max(case when ft.nome_assortimento = 'LEGNA' and a.is_autoconsumo = true and PROG = 0 then percentuale_ass end) as LEGNAPercAutoc0
					assAssortimenti.addLast(assEntry);
					campiSelectSecondoRecuperoAssortimenti.addLast(
						new CampoSelect(
							assEntry,
							assEntry,
							DbUtils.GetDecimal
						)
					);
				}
			}
		}
		//assAssortimenti.addAll(Arrays.asList("specie0", "specie1", "categoria0", "categoria1", "macrocategoria0", "macrocategoria1"));
		assAssortimenti.addAll(Arrays.asList("specie0", "specie1"));
		assAssortimenti.addAll(Arrays.asList("percCopertura0", "percCopertura1"));
		String[] arrPropAssortimenti = assAssortimenti.toArray(new String[0]);


		LinkedList<String> props = new LinkedList<>();
		props.addAll(assAssortimenti);
		props.addAll(assCubatura);

		props.add("idIstanza");
		props.add("progUog");
		props.add("metodoDiCubatura");
		props.add("descrizioneMetodoCubatura");

		String[] arrProps = props.toArray(new String[0]);


		String querySecondoCaricamento = String.format(
			"""
select ID_SPECIE, ID_ASSORTIMENTO,
	dest = 'PercAutoc' as IS_AUTOCONSUMO,
	val as PERCENTUALE_ASS
from (
		values%s
	) as T(NOME_ASSORTIMENTO, dest, ID_SPECIE, val)
	join foliage2.FLGASSORTIMENTO_TAB A using (NOME_ASSORTIMENTO)
where ID_SPECIE is not null
	and val is not null""",
			valuesAssortimenti.stream().collect(Collectors.joining("""
,
		"""
				)
			)
		);




		String querySecondaAcquisizione = String.format(
			"""
(
		select %s
		--select *
		from foliage2.FLGASS_SPECI_UOG_TAB a
			join foliage2.FLGSPECI_UOG_TAB fit using (ID_ISTA, PROG_UOG, ID_SPECIE)
			join foliage2.flgassortimento_tab ft  using (ID_ASSORTIMENTO)
		where ID_ISTA = :idIstanza
			and PROG_UOG = :progUo
	) as T""", 
			pivotAssortimenti.stream().collect(Collectors.joining("""
,
			"""
				)
			)
		);


		LinkedList<CorrispondenzaJsonDiretta> corr =  new LinkedList<>();
		corr.addAll(
			assAssortimenti.stream().map(
				s -> new CorrispondenzaJsonDiretta(s, JsonIO.NumberIo)
			).toList()
		);
		corr.addAll(
			assCubatura.stream().map(
				s -> new CorrispondenzaJsonDiretta(s, JsonIO.NumberIo)
			).toList()
		);
		corr.add(new CorrispondenzaJsonDiretta(
				"progUo", JsonIO.NumberIo
			)
		);
		corr.add(new CorrispondenzaJsonDiretta(
				"formaDiGoverno", JsonIO.StringIo
			)
		);
		corr.add(new CorrispondenzaJsonDiretta(
				"tipoDiSoprasuolo", JsonIO.StringIo
			)
		);
		corr.add(new CorrispondenzaJsonDiretta(
				"nomeUo", JsonIO.StringIo
			)
		);
		corr.add(new CorrispondenzaJsonDiretta(
				"superficieUtile", JsonIO.DecimalIo
			)
		);
		corr.add(new CorrispondenzaJsonDiretta(
				"metodoDiCubatura", JsonIO.StringIo
			)
		);
		corr.add(new CorrispondenzaJsonDiretta(
				"descrizioneMetodoCubatura", JsonIO.StringIo
			)
		);
		


		CorrispondenzaJsonDiretta[] assArr =  corr.toArray(new CorrispondenzaJsonDiretta[0]);

		return new SchedaIstanzaGenerica(
			"DestinazioniUsoBoschivo",
			new CorrispondenzaJson(assArr),
			new IOperazioneDb[] {
				new RecuperoDb(
					"foliage2.FLGUNITA_OMOGENEE_CUBATURA_TAB", 
					new CampoSelect[] {
						new CampoSelect("COD_METODO_CUBATURA", "metodoDiCubatura", DbUtils.Get),
						new CampoSelect("DESC_METODO_CUBATURA", "descrizioneMetodoCubatura", DbUtils.Get)
					},
					new CondizioneEq[] {
						new CondizioneEq("ID_ISTA", "idIstanza"),
						new CondizioneEq("PROG_UOG", "progUo")
					}
				),
				new RecuperoDb(
					"""
(
		select max(case when PROG_SPECIE_UOG = 0 then ID_SPECIE end) as specie0,
			max(case when PROG_SPECIE_UOG = 1 then ID_SPECIE end) as specie1,
			max(case when PROG_SPECIE_UOG = 0 then percentuale_intervento end) as percCopertura0,
			max(case when PROG_SPECIE_UOG = 1 then percentuale_intervento end) as percCopertura1
		from foliage2.FLGSPECI_UOG_TAB
			join foliage2.flgspecie_tab using (id_specie)
		where ID_ISTA = :idIstanza
			and PROG_UOG = :progUo
	) as T
					""", 
					new CampoSelect[] {
						new CampoSelect("specie0", "specie0", DbUtils.GetInteger),
						new CampoSelect("specie1", "specie1", DbUtils.GetInteger),
						new CampoSelect("percCopertura0", "percCopertura0", DbUtils.GetInteger),
						new CampoSelect("percCopertura1", "percCopertura1", DbUtils.GetInteger)
					}, 
					null,
					new String[] {"idIstanza", "progUo"}
				),
				new RecuperoDb(
					querySecondaAcquisizione, 
					campiSelectSecondoRecuperoAssortimenti.toArray(new CampoSelect[0]), 
					null,
					new String[] {"idIstanza", "progUo"}
				),
				aquisizioneCubatura
			},
			new IOperazioneDb[]{
				new CaricamentoRecord(
					"foliage2.FLGUNITA_OMOGENEE_CUBATURA_TAB", 
					new CampoSet[] {
						new CampoSet("ID_ISTA", "idIstanza"),
						new CampoSet("PROG_UOG", "progIdx"),
						new CampoSet("COD_METODO_CUBATURA", "metodoDiCubatura"),
						new CampoSet("DESC_METODO_CUBATURA", "descrizioneMetodoCubatura")
					}
				),
				new CaricamentoQuery("foliage2.FLGSPECI_UOG_TAB", 
					"""
select ID_ISTA, PROG_UOG, PROG_SPECIE_UOG, ID_SPECIE, PERCENTUALE_INTERVENTO
from (
		values
			(:idIstanza, :progIdx, 0, :specie0::int, :percCopertura0::numeric),
			(:idIstanza, :progIdx, 1, :specie1::int, :percCopertura1::numeric)
	) as T(ID_ISTA, PROG_UOG, PROG_SPECIE_UOG, ID_SPECIE, PERCENTUALE_INTERVENTO)
where ID_SPECIE is not null
	and PERCENTUALE_INTERVENTO is not null""",
					new CampoSet[] {
						new CampoSet("ID_ISTA", null, "ID_ISTA"),
						new CampoSet("PROG_UOG", "progIdx"),
						new CampoSet("PROG_SPECIE_UOG", null, "PROG_SPECIE_UOG"),
						new CampoSet("ID_SPECIE", null, "ID_SPECIE"),
						new CampoSet("PERCENTUALE_INTERVENTO", null, "PERCENTUALE_INTERVENTO")
					},
					null,
					new String[] {
						"idIstanza", "progIdx", "specie0", "percCopertura0", "specie1", "percCopertura1"
					}
				),
				new CaricamentoQuery("foliage2.FLGASS_SPECI_UOG_TAB", 
					querySecondoCaricamento,
					new CampoSet[] {
						new CampoSet("ID_ISTA", "idIstanza"),
						new CampoSet("PROG_UOG", "progIdx"),
						new CampoSet("ID_SPECIE", null, "ID_SPECIE"),
						new CampoSet("ID_ASSORTIMENTO", null, "ID_ASSORTIMENTO"),
						new CampoSet("IS_AUTOCONSUMO", null, "IS_AUTOCONSUMO"),
						new CampoSet("PERCENTUALE_ASS", null, "PERCENTUALE_ASS")
					},
					null,
					arrPropAssortimenti
				),
				salvataggioCubatura
			},
			null,
			null
		);
	}

	private static final ISchedaIstanzaWithContext GetDestinazioniUsoEProspettiRiepilogativi() throws Exception {
		SchedaIstanzaGenerica schedaSingola = GetDestinazioniUsoUnitaOmogeneaSingola();
		// new SchedaIstanzaGenerica(
		// 	"Destinazioni Uso e Prospetto Riepilogativo per Unità Omogenea",
		// 	new CorrispondenzaJson(
		// 		new CorrispondenzaJsonDiretta[] {
		// 			new CorrispondenzaJsonDiretta(
		// 				"progUo", JsonIO.NumberIo
		// 			),
		// 			new CorrispondenzaJsonDiretta(
		// 				"nomeUo", JsonIO.StringIo
		// 			),
		// 			new CorrispondenzaJsonDiretta(
		// 				"superficieUtile", JsonIO.DecimalIo
		// 			),
		// 			new CorrispondenzaJsonDiretta(
		// 				"formaDiGoverno", JsonIO.StringIo
		// 			)
		// 		}
		// 	),
		// 	null,//operazioniCaricamento,
		// 	null,//IOperazioneDb[] operazioniSalvataggio,
		// 	null, //IOperazioneDb[] operazioniCancellazione,
		// 	null //String[] contextUpdateProps
		// );

		return new SchedaIstanzaArray(
			"arrayUog2",
			new RecuperoDb(
				"foliage2.FLGUNITA_OMOGENEE_TAB",
				new CampoSelect[] {
					new CampoSelect(
						"PROG_UOG", "progUo",
						DbUtils.GetInteger
					),
					new CampoSelect(
						"NOME_UOG", "nomeUo",
						DbUtils.Get
					),
					new CampoSelect(
						"SUPERFICIE_UTILE", "superficieUtile",
						DbUtils.GetDecimal
					),
					new CampoSelect(
						"DESC_GOVE", "formaDiGoverno",
						DbUtils.Get
					),
					new CampoSelect(
						"TIPO_SOPRASUOLO", "tipoDiSoprasuolo",
						DbUtils.Get
					)
				},
				new CondizioneEq[] {
					new CondizioneEq("id_ista", "idIstanza")
				}
			),//aquisizione
			new IOperazioneDb[] {
				new EliminazioneDb(
					"foliage2.FLGSPECI_UOG_TAB",
					new CondizioneEq[] {
						new CondizioneEq("ID_ISTA", "idIstanza")
					}
				),
				new EliminazioneDb(
					"foliage2.FLGUNITA_OMOGENEE_CUBATURA_TAB",
					new CondizioneEq[] {
						new CondizioneEq("ID_ISTA", "idIstanza")
					}
				),
				new EliminazioneDb(
					"foliage2.FLGASS_SPECI_UOG_TAB",
					new CondizioneEq[] {
						new CondizioneEq("ID_ISTA", "idIstanza")
					}
				)
			},//cancellazione
			schedaSingola,
			"progIdx",
			null,//presalvataggio
			null//postsalvataggio
		);
	}
	private static final ISchedaIstanzaWithContext GetViabilitaForestale() throws Exception {		
		SchedaIstanzaGenerica schedaElemento = new SchedaIstanzaGenerica(
			"Viabilità Forestale",
			new CorrispondenzaJson(
				new CorrispondenzaJsonDiretta[] {
					new CorrispondenzaJsonDiretta(
						"progViabilita", JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"codTipoViabilita", JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"nomeTipoViabilita", JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"shape", JsonIO.StringIo
					)
				}
			),
			new IOperazioneDb[]{

			},//operazioniCaricamento,
			new IOperazioneDb[]{
				new CaricamentoRecord(
					"foliage2.FLGVIABILITA_ISTA_TAB", 
					new CampoSet[] {
						new CampoSet("ID_ISTA", "idIstanza"),
						new CampoSet("PROG_VIABILITA", "progIdx"),
						new CampoSet("COD_TIPO_VIABILITA", "codTipoViabilita"),
						new CampoSet("SHAPE", "shape", "st_geometryfromtext(:shape)"),
					}
				)
			},//operazioniSalvataggio,
			new IOperazioneDb[]{

			},//operazioniCancellazione,
			null
		);
		return new SchedaIstanzaArray(
			"arrayViabilitaForestale",
			new RecuperoDb(
				"foliage2.FLGVIABILITA_ISTA_TAB join foliage2.FLGTIPO_VIABILITA_TAB using (COD_TIPO_VIABILITA)",
				new CampoSelect[] {
					new CampoSelect(
						"PROG_VIABILITA", "progViabilita",
						DbUtils.GetInteger
					),
					new CampoSelect(
						"COD_TIPO_VIABILITA", "codTipoViabilita",
						DbUtils.Get
					),
					new CampoSelect(
						"NOME_TIPO_VIABILITA", "nomeTipoViabilita",
						DbUtils.Get
					),
					new CampoSelect(
						"shape", 
						"ST_AsText(shape)", "shape",
						DbUtils.Get
					)
				},
				new CondizioneEq[] {
					new CondizioneEq("id_ista", "idIstanza")
				}
			),//aquisizione
			new IOperazioneDb[]{
				new EliminazioneDb(
					"foliage2.FLGVIABILITA_ISTA_TAB tb",
					new CondizioneEq[] {
						new CondizioneEq("id_ista","idIstanza")
					}
				)
			},//cancellazione
			schedaElemento,
			"progIdx",
			null,//presalvataggio
			null//postsalvataggio
		);
	}
	private static final ISchedaIstanzaWithContext GetAltriStratiInformativi() throws Exception {
		SchedaIstanzaGenerica schedaElemento = new SchedaIstanzaGenerica(
			"Altri Strati",
			new CorrispondenzaJson(
				new CorrispondenzaJsonDiretta[] {
					new CorrispondenzaJsonDiretta(
						"progStrato", JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"progUo", JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"progUnitaOmogeneaAssociata", JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"isAreaTradizionale", JsonIO.BooleanIo
					),
					new CorrispondenzaJsonDiretta(
						"isAreaDimostrativa", JsonIO.BooleanIo
					),
					new CorrispondenzaJsonDiretta(
						"isImposto", JsonIO.BooleanIo
					),
					new CorrispondenzaJsonDiretta(
						"isAreaRelascopica", JsonIO.BooleanIo
					),
					new CorrispondenzaJsonDiretta(
						"nomeArea", JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"superficieArea", JsonIO.DecimalIo
					),
					new CorrispondenzaJsonDiretta(
						"percentualeRappresentativita", JsonIO.DecimalIo
					),
					new CorrispondenzaJsonDiretta(
						"shape", JsonIO.StringIo
					)
				}
			),
			new IOperazioneDb[]{

			},//operazioniCaricamento,
			new IOperazioneDb[]{
				new CaricamentoRecord(
					"foliage2.FLGSTRATI_ISTA_TAB", 
					new CampoSet[] {
						new CampoSet("ID_ISTA", "idIstanza"),
						new CampoSet("PROG_STRATO", "progIdx"),
						new CampoSet("PROG_UOG", "progUo"),
						new CampoSet("NOME_STRATO", "nomeArea"),
						new CampoSet("IS_AREA_SAGGIO_TRADIZIONALE", "isAreaTradizionale", ":isAreaTradizionale::boolean is not null and :isAreaTradizionale::boolean"),
						new CampoSet("IS_AREA_DIMOSTRATIVA", "isAreaDimostrativa", ":isAreaDimostrativa::boolean is not null and :isAreaDimostrativa::boolean"),
						new CampoSet("IS_AREA_SAGGIO_RELASCOPICA", "isAreaRelascopica", ":isAreaRelascopica::boolean is not null and :isAreaRelascopica::boolean"),
						new CampoSet("IS_IMPOSTO", "isImposto", ":isImposto::boolean is not null and :isImposto::boolean"),
						new CampoSet("SUPERFICIE_STRATO", "superficieArea"),
						new CampoSet("PERCENTUALE_RAPPRESENTATIVITA", "percentualeRappresentativita"),
						new CampoSet("SHAPE", "shape", "st_geometryfromtext(:shape)"),
					}
				)
			},//operazioniSalvataggio,
			new IOperazioneDb[]{

			},//operazioniCancellazione,
			null
		);
		return new SchedaIstanzaArray(
			"arrayAltriStrati",
			new RecuperoDb(
				"foliage2.FLGSTRATI_ISTA_TAB",
				new CampoSelect[] {
					new CampoSelect(
						"PROG_STRATO", "progStrato",
						DbUtils.GetInteger
					),
					new CampoSelect(
						"PROG_UOG", "progUo",
						DbUtils.GetInteger
					),
					new CampoSelect(
						"NOME_STRATO", "nomeArea",
						DbUtils.Get
					),
					new CampoSelect(
						"IS_AREA_SAGGIO_TRADIZIONALE", "isAreaTradizionale",
						DbUtils.GetBoolean
					),
					new CampoSelect(
						"IS_AREA_DIMOSTRATIVA", "isAreaDimostrativa",
						DbUtils.GetBoolean
					),
					new CampoSelect(
						"IS_AREA_SAGGIO_RELASCOPICA", "isAreaRelascopica",
						DbUtils.GetBoolean
					),
					new CampoSelect(
						"IS_IMPOSTO", "isImposto",
						DbUtils.GetBoolean
					),
					new CampoSelect(
						"SUPERFICIE_STRATO", "superficieArea",
						DbUtils.GetDecimal
					),
					new CampoSelect(
						"PERCENTUALE_RAPPRESENTATIVITA", "percentualeRappresentativita",
						DbUtils.GetDecimal
					),
					new CampoSelect(
						"shape", 
						"ST_AsText(shape)", "shape",
						DbUtils.Get
					)
				},
				new CondizioneEq[] {
					new CondizioneEq("id_ista", "idIstanza")
				}
			),//aquisizione
			new IOperazioneDb[]{
				new EliminazioneDb(
					"foliage2.FLGSTRATI_ISTA_TAB tb",
					new CondizioneEq[] {
						new CondizioneEq("id_ista","idIstanza")
					}
				)
			},//cancellazione
			schedaElemento,
			"progIdx",
			null,//presalvataggio
			null//postsalvataggio
		);
	}
	private static final SchedaIstanzaGenerica GetSingolaUnitaOmogenea() throws Exception {
		return new SchedaIstanzaGenerica(
			"SingolaUnitaOmogenea",
			new CorrispondenzaJson(
				new CorrispondenzaJsonDiretta[] {
					new CorrispondenzaJsonDiretta(
						"nomeUO",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"shape",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"superficie",
						JsonIO.DecimalIo
					),
					new CorrispondenzaJsonDiretta(
						"formaDiGoverno",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"strutturaDelSoprasuolo",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"etaMediaDelSoprassuoloAnni",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"trattamentoPrecedenteCeduo",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"trattamentoCeduo",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"trattamentoPrecedenteFustaia",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"trattamentoFustaia",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"tipoDiSoprasuolo",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"superficieUtile",
						JsonIO.DecimalIo
					),
					new CorrispondenzaJsonDiretta(
						"areeImproduttive",
						JsonIO.DecimalIo
					),
					new CorrispondenzaJsonDiretta(
						"chiarieRadure",
						JsonIO.DecimalIo
					),
					new CorrispondenzaJsonDiretta(
						"areeInterdette",
						JsonIO.DecimalIo
					),
					new CorrispondenzaJsonDiretta(
						"prog",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"idCategoria",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"idSottocategoria",
						JsonIO.NumberIo
					)
				}
			),
			new IOperazioneDb[] {
				new RecuperoDb(
					"foliage2.FLGUNITA_OMOGENEE_TAB",
					new CampoSelect[] {
						new CampoSelect(
							"PROG_UOG", "prog",
							DbUtils.Get
						),
						new CampoSelect(
							"NOME_UOG", "nomeUO",
							DbUtils.Get
						),
						new CampoSelect(
							"SUPERFICIE_UTILE", "superficieUtile",
							DbUtils.GetDecimal
						),
						new CampoSelect(
							"SUPERFICIE_AREE_IMPRODUTTIVE", "areeImproduttive",
							DbUtils.GetDecimal
						),
						new CampoSelect(
							"SUPERFICIE_CHIARE_RADURE", "chiarieRadure",
							DbUtils.GetDecimal
						),
						new CampoSelect(
							"SUPERFICIE_AREE_INTERDETTE", "areeInterdette",
							DbUtils.GetDecimal
						),
						new CampoSelect(
							"SUPERFICIE", "superficie",
							DbUtils.GetDecimal
						),
						new CampoSelect(
							"shape", "ST_AsText(SHAPE)", "shape",
							DbUtils.Get
						),
						new CampoSelect(
							"DESC_GOVE", "formaDiGoverno",
							DbUtils.Get
						),
						new CampoSelect(
							"ID_SSPR", "strutturaDelSoprasuolo",
							DbUtils.GetInteger
						),
						new CampoSelect(
							"ETA_MEDIA", "etaMediaDelSoprassuoloAnni",
							DbUtils.GetInteger
						),
						new CampoSelect(
							"TIPO_SOPRASUOLO",
//							"coalesce(TIPO_SOPRASUOLO, '')",
							"tipoDiSoprasuolo",
							DbUtils.Get
						),
						new CampoSelect(
							"ID_CATEGORIA", "idCategoria",
							DbUtils.GetInteger
						),
						new CampoSelect(
							"ID_SOTTOCATEGORIA", "idSottocategoria",
							DbUtils.GetInteger
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("ID_ISTA", "idIstanza"),
						new CondizioneEq("PROG_UOG", "progIdx")
					}
				),
				new RecuperoDb(
					"""
(
		select *
		from foliage2.FLGUNITA_OMOGENEE_TRATTAMENTO_TAB tb
			join foliage2.flggove_tab g on (g.id_gove = tb.id_gove)
		where g.desc_gove = 'Ceduo'
	) t""",
					new CampoSelect[] {
						new CampoSelect(
							"ID_FORMA_TRATTAMENTO_PREC", "trattamentoPrecedenteCeduo",
							DbUtils.GetInteger
						),
						new CampoSelect(
							"ID_FORMA_TRATTAMENTO", "trattamentoCeduo",
							DbUtils.GetInteger
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza"),
						new CondizioneEq("PROG_UOG", "progIdx")
					}
				),
				new RecuperoDb(
					"""
(
		select *
		from foliage2.FLGUNITA_OMOGENEE_TRATTAMENTO_TAB tb
			join foliage2.flggove_tab g on (g.id_gove = tb.id_gove)
		where g.desc_gove = 'Fustaia'
	) t""",
					new CampoSelect[] {
						new CampoSelect(
							"ID_FORMA_TRATTAMENTO_PREC", "trattamentoPrecedenteFustaia",
							DbUtils.GetInteger
						),
						new CampoSelect(
							"ID_FORMA_TRATTAMENTO", "trattamentoFustaia",
							DbUtils.GetInteger
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza"),
						new CondizioneEq("PROG_UOG", "progIdx")
					}
				)
			},//lettura
			new IOperazioneDb[] {
				new CaricamentoRecord(
					"foliage2.FLGUNITA_OMOGENEE_TAB", 
					new CampoSet[]{
						new CampoSet(
							"ID_ISTA", "idIstanza"
						),
						new CampoSet(
							"PROG_UOG", "progIdx"
						),
						new CampoSet(
							"NOME_UOG", "nomeUO"
						),
						new CampoSet(
							"SUPERFICIE", "superficie", "coalesce(:superficie, 0)"
						),
						new CampoSet(
							"SUPERFICIE_UTILE", "superficieUtile", "coalesce(:superficieUtile, 0)"
						),
						new CampoSet(
							"SUPERFICIE_AREE_IMPRODUTTIVE", "areeImproduttive", "coalesce(:areeImproduttive, 0)"
						),
						new CampoSet(
							"SUPERFICIE_CHIARE_RADURE", "chiarieRadure", "coalesce(:chiarieRadure, 0)"
						),
						new CampoSet(
							"SUPERFICIE_AREE_INTERDETTE", "areeInterdette", "coalesce(:areeInterdette, 0)"
						),
						new CampoSet(
							"SHAPE", "shape", "st_geometryfromtext(:shape)"
						),
						new CampoSet(
							"DESC_GOVE", "formaDiGoverno"
						),
						new CampoSet(
							"ID_SSPR", "strutturaDelSoprasuolo"
						),
						new CampoSet(
							"TIPO_SOPRASUOLO", "tipoDiSoprasuolo", "nullif(:tipoDiSoprasuolo, '')"
						),
						new CampoSet(
							"ETA_MEDIA", "etaMediaDelSoprassuoloAnni", ":etaMediaDelSoprassuoloAnni"
						),
						new CampoSet(
							"ID_CATEGORIA", "idCategoria"
						),
						new CampoSet(
							"ID_SOTTOCATEGORIA", "idSottocategoria"
						)
					}
				),
				new CaricamentoQuery(
					"foliage2.FLGUNITA_OMOGENEE_TRATTAMENTO_TAB", 
					"""
select ID_GOVE, ID_FORMA_TRATTAMENTO_PREC, ID_FORMA_TRATTAMENTO
from (
		values('Ceduo', :trattamentoPrecedenteCeduo::int, :trattamentoCeduo::int),
		('Fustaia', :trattamentoPrecedenteFustaia::int, :trattamentoFustaia::int)
	) as FORME_TRAT(DESC_GOVE, ID_FORMA_TRATTAMENTO_PREC, ID_FORMA_TRATTAMENTO)
	join foliage2.flggove_tab using (DESC_GOVE)
where (
		ID_FORMA_TRATTAMENTO is not null
		or ID_FORMA_TRATTAMENTO_PREC is not null
	)
	and DESC_GOVE = coalesce(nullif(:formaDiGoverno, 'Misto'), DESC_GOVE) """,
					new CampoSet[] {
						new CampoSet("ID_ISTA", "idIstanza"),
						new CampoSet("PROG_UOG", "progIdx"),
						new CampoSet("ID_GOVE", null, "ID_GOVE"),
						new CampoSet("ID_FORMA_TRATTAMENTO_PREC", null, "ID_FORMA_TRATTAMENTO_PREC"),
						new CampoSet("ID_FORMA_TRATTAMENTO", null, "ID_FORMA_TRATTAMENTO")
					},
					null,
					new String[] {
						"trattamentoPrecedenteCeduo", "trattamentoCeduo", "trattamentoPrecedenteFustaia", "trattamentoFustaia", "formaDiGoverno"
					}
				)
			},//scrittura
			null,//cancellazione
			new String[] { "idSchedaInterventoSelezionata", "tipoDiSoprasuolo" }
		);
	}
	private static final ISchedaIstanzaWithContext GetUnitaOmogenee() throws Exception {
		return new SchedaIstanzaArray(
			"arrayUog",
			new RecuperoDb(
				"""
(
	select coalesce(PROG_UOG, 0) as PROG_UOG, coalesce(U.SHAPE, T.SHAPE) as SHAPE
	from (
			select ID_ISTA, ST_UNION(P.SHAPE) as SHAPE
			from foliage2.flgparticella_forestale_shape_tab P
			where ID_ISTA = :idIstanza
			group by ID_ISTA
		) as T
		left join foliage2.FLGUNITA_OMOGENEE_TAB U using (ID_ISTA)
) as TT""",
				new CampoSelect[] {
					new CampoSelect(
						"PROG_UOG", "progIdx",
						DbUtils.Get
					),
					new CampoSelect(
						"shape", 
						"ST_AsText(shape)", "shape",
						DbUtils.Get
					),
					new CampoSelect(
						"SUPERFICIE", 
						"ST_Area(shape)", "superficie",
						DbUtils.GetDecimal
					)
					
				},
				null,
				new String[] {"idIstanza"}
			),//aquisizione
			new IOperazioneDb[]{
				new EliminazioneDb(
					"foliage2.FLGUNITA_OMOGENEE_TAB tb",
					new CondizioneEq[] {
						new CondizioneEq("id_ista","idIstanza")
					}
				),
				new EliminazioneDb(
					"foliage2.FLGUNITA_OMOGENEE_TRATTAMENTO_TAB tb",
					new CondizioneEq[] {
						new CondizioneEq("id_ista","idIstanza")
					}
				)
			},//cancellazione
			SingolaUnitaOmogenea,
			"progIdx",
			null,//presalvataggio
			null//postsalvataggio
		);
	}
	private static final ISchedaIstanzaWithContext GetStazioneForestale() throws Exception {
		return new SchedaIstanzaGenerica(
			"StazioneForestale",
			new CorrispondenzaJson(
				new CorrispondenzaJsonDiretta[] {
					new CorrispondenzaJsonDiretta(
						"minAltimetria",
						JsonIO.DecimalIo
					),
					new CorrispondenzaJsonDiretta(
						"maxAltimetria",
						JsonIO.DecimalIo
					),
					new CorrispondenzaJsonDiretta(
						"avgAltimetria",
						JsonIO.DecimalIo
					),
					new CorrispondenzaJsonDiretta(
						"minPendenza",
						JsonIO.DecimalIo
					),
					new CorrispondenzaJsonDiretta(
						"maxPendenza",
						JsonIO.DecimalIo
					),
					new CorrispondenzaJsonDiretta(
						"avgPendenza",
						JsonIO.DecimalIo
					)
				}
			),
			new IOperazioneDb[] {
				new RecuperoDb(
					"foliage2.flgparticella_forestale_tab", 
					new CampoSelect[] {
						new CampoSelect(
							"altimetria_min", "minAltimetria", 
							DbUtils.GetDecimal
						),
						new CampoSelect(
							"altimetria_max", "maxAltimetria", 
							DbUtils.GetDecimal
						),
						new CampoSelect(
							"altimetria_avg", "avgAltimetria", 
							DbUtils.GetDecimal
						),
						new CampoSelect(
							"pendenza_min", "minPendenza", 
							DbUtils.GetDecimal
						),
						new CampoSelect(
							"pendenza_max", "maxPendenza", 
							DbUtils.GetDecimal
						),
						new CampoSelect(
							"pendenza_avg", "avgPendenza", 
							DbUtils.GetDecimal
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				)
			}
		);
	}
	private static final ISchedaIstanzaWithContext GetVincolisticaSopraSoglia() throws Exception {
		return new SchedaIstanzaGenerica(
			"VincolisticaSopraSoglia",
			new CorrispondenzaJson(
				new CorrispondenzaJsonDiretta[] {
					new CorrispondenzaJsonDiretta(
						"hasVisioneVincoli",
						JsonIO.BooleanIo
					),
					new CorrispondenzaJsonDiretta(
						"fileVinca",
						JsonIO.Base64FormioFileIo
					),
					new CorrispondenzaJsonDiretta(
						"scelteUtente",
						JsonIO.ArrayIo
					)
				}
			),
			new IOperazioneDb[] {
				new RecuperoDb(
					"foliage2.FLGISTA_TAB", 
					new CampoSelect[] {
						new CampoSelect(
							"HAS_VISIONE_VINCOLI", "hasVisioneVincoli", 
							DbUtils.GetBoolean
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				),
				new RecuperoDb(
					"foliage2.FLGISTA_ELABORATO_VINCA_TAB", 
					new CampoSelect[] {
						new CampoSelect(
							"ID_FILE_VINCA", "idFileVinca", 
							DbUtils.GetInteger
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				),
				new RecuperoFileBase64("idFileVinca", "fileVinca"),
				new RecuperoArray<Object>(
					(ResultSet rs, int rn) -> {
						return rs.getString("risposta");
					},
					"""
select RISPOSTA
from foliage2.FLGRISPOSTE_WIZARD_VINCOLISTICA_TAB
where ID_ISTA = :idIstanza
order by PROG""",
					"scelteUtente", 
					new String[] {"idIstanza"}
				)
			},//select
			new IOperazioneDb[] {//inserimento
				new AggiornamentoDb(
					"foliage2.FLGISTA_TAB",
					new CampoSet[] {
						new CampoSet("HAS_VISIONE_VINCOLI", "hasVisioneVincoli")
					}, 
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				),
				new CaricamentoArray<Object>(
					"""
insert into foliage2.FLGRISPOSTE_WIZARD_VINCOLISTICA_TAB(ID_ISTA, PROG, RISPOSTA)
	values(:idIstanza, :prog, :risposta)""",
					"prog",
					"risposta",
					"scelteUtente",
					(HashMap<String, Object> contesto, Object s) -> {
						JsonElement x = (JsonElement)s;
						String val = x.getAsString();
						contesto.put("risposta", val);
					},
					new String [] {"risposta", "idIstanza"}
				),
				new CaricamentoFileBase64("idFileVinca", "fileVinca"),
				new CaricamentoQuery(
					"foliage2.FLGISTA_ELABORATO_VINCA_TAB", 
					"""
select ID_ISTA, ID_FILE_VINCA
from (
		values(:idIstanza::int, :idFileVinca::int)
	) as T(ID_ISTA, ID_FILE_VINCA)
where T.ID_FILE_VINCA is not null""",
					new CampoSet[] {
						new CampoSet("ID_ISTA", null, "ID_ISTA"),
						new CampoSet("ID_FILE_VINCA", null, "ID_FILE_VINCA")
					},
					null,
					new String[] {"idIstanza", "idFileVinca"}
				)
			},
			new IOperazioneDb[] {
				new EliminazioneDb(
					"foliage2.FLGRISPOSTE_WIZARD_VINCOLISTICA_TAB",
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				),
				new EliminazioneDb(
					"foliage2.FLGISTA_ELABORATO_VINCA_TAB",
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				),
				new AggiornamentoDb(
					"foliage2.FLGISTA_TAB",
					new CampoSet[] {
						new CampoSet("HAS_VISIONE_VINCOLI", null, "null")
					}, 
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				)
			},//delete
			null
		);
	}
	private static final ISchedaIstanzaWithContext GetParticellaForestaleSopraSoglia(String regionName) throws Exception {
		return new SchedaIstanzaArray(
			"shapeParticellaForestale",
			new RecuperoDb(
				"foliage2.FLGPARTICELLA_FORESTALE_SHAPE_TAB",
				new CampoSelect[] {
					new CampoSelect(
						"PROG_GEOM", "progShape",
						DbUtils.Get
					),
					new CampoSelect(
						"wkt_shape", 
						"ST_AsText(shape)", "shape",
						DbUtils.Get
					)
				},
				new CondizioneEq[] {
					new CondizioneEq("id_ista", "idIstanza")
				}
			),//acquisizione
			new IOperazioneDb[] {
				new EliminazioneDb(
					"foliage2.FLGUNITA_OMOGENEE_TAB",
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				),
				new EliminazioneDb(
					"foliage2.FLGVINCOLI_ISTA_TAB", 
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				),
				new EliminazioneDb(
					"foliage2.flgparticella_forestale_tab", 
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				),
				new EliminazioneDb(
					"foliage2.FLGPARTICELLA_FORESTALE_SHAPE_TAB",
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				)
			},//cancellazione
			new SchedaIstanzaGenerica(
				"ParticellaForestale",
				new CorrispondenzaJson(
					new CorrispondenzaJsonDiretta[] {
						new CorrispondenzaJsonDiretta("id", "progShape", JsonIO.NumberIo),
						new CorrispondenzaJsonDiretta("shape", JsonIO.StringIo)
					}
				),
				null,
				new IOperazioneDb[] {
					new CaricamentoRecord(
						"foliage2.flgparticella_forestale_shape_tab",
						new CampoSet[] {
							new CampoSet(
								"id_ista",
								"idIstanza"
							),
							new CampoSet(
								"prog_geom",
								"idxGeom"
							),
							new CampoSet(
								"shape",
								"shape",
								"st_geomfromtext(:shape)"
							),
							new CampoSet(
								"superficie",
								"shape",
								"st_area(st_geomfromtext(:shape))"
							)
						}
					)
				},
				null
			),//scheda
			"idxGeom",
			null,//presalvataggio
			new IOperazioneDb[] {
				new CaricamentoQuery(
					"foliage2.FLGVINCOLI_ISTA_TAB", 
					"""
select *
from (
		select T1.*, st_area(SHAPE_INTE) as SUPE_INTE
		from (
				with PFOR as (
					select ID_ISTA, ID_VINCOLO, SHAPE
					from (
							select ID_ISTA, ST_SetSRID(ST_UNION(shape), :sridGeometrie) as shape
							from foliage2.flgparticella_forestale_shape_tab
							where ID_ISTA = :idIstanza
							group by ID_ISTA
						) as T
						join foliage2.FLGISTA_TAB using (ID_ISTA)
						join foliage2.flgvincoli_tipo_ista_tab using (ID_TIPO_ISTANZA)
				)
				select ID_VINCOLO, ID_ISTA, row_number() over() as PROG, HABITAT::varchar as CODICE, REL_DESCRI::varchar as DENOMINAZI,
					ST_Transform(st_intersection(pfor.shape, hab.geom),:sridGeometrie) as SHAPE_INTE
				from (
						select ID_ISTA, ID_VINCOLO, ST_SetSRID(ST_Transform(shape, 6706), 6706) as shape
						from PFOR
					) as pfor
					join foliage2.FLGVINCOLI_TAB V using (ID_VINCOLO)
					join foliage_extra.nat2k_habitat_prioritari_208 hab on (ST_Intersects(pfor.shape, hab.geom))
				where V.cod_vincolo = 'HABITAT_PRIOR'
				union all
				select ID_VINCOLO, ID_ISTA, row_number() over() as PROG, CODICE, DENOMINAZI,
					ST_Transform(st_intersection(pfor.shape, hab.geom), :sridGeometrie) as SHAPE_INTE
				from (
						select ID_ISTA, ID_VINCOLO, ST_SetSRID(ST_Transform(shape, 4326), 4326) as shape
						from PFOR
					) as pfor
					join foliage2.FLGVINCOLI_TAB V using (ID_VINCOLO)
					join foliage_extra.sitiprotetti_natura_2000 hab on (ST_Intersects(pfor.shape, hab.geom))
				where V.cod_vincolo = 'NAT2K'
				union all
				select ID_VINCOLO, ID_ISTA, row_number() over() as PROG, RISCHIO, TIPOLOGIA,
					ST_Transform(st_intersection(pfor.shape, hab.geom), :sridGeometrie) as SHAPE_INTE
				from (
						select ID_ISTA, ID_VINCOLO, st_setsrid(ST_Transform(shape, 4326), 4326) as shape
						from PFOR
					) as pfor
					join foliage2.FLGVINCOLI_TAB V using (ID_VINCOLO)
					join foliage_extra.pai_rischio_frana_101 hab on (ST_Intersects(pfor.shape, hab.geom))
				where V.cod_vincolo = 'PAI_FRANE'
				union all
				select ID_VINCOLO, ID_ISTA, row_number() over() as PROG, RISCHIO, TIPOLOGIA,
					ST_Transform(st_intersection(pfor.shape, hab.geom), :sridGeometrie) as SHAPE_INTE
				from (
						select ID_ISTA, ID_VINCOLO, st_setsrid(ST_Transform(shape, 4326), 4326) as shape
						from PFOR
					) as pfor
					join foliage2.FLGVINCOLI_TAB V using (ID_VINCOLO)
					join foliage_extra.pai_rischio_alluvione_101 hab on (ST_Intersects(pfor.shape, hab.geom))
				where V.cod_vincolo = 'PAI_ALLUVIONI'
				union all
				select ID_VINCOLO, ID_ISTA, row_number() over() as PROG, RISCHIO, TIPOLOGIA,
					ST_Transform(st_intersection(pfor.shape, hab.geom), :sridGeometrie) as SHAPE_INTE
				from (
						select ID_ISTA, ID_VINCOLO, st_setsrid(ST_Transform(shape, 4326), 4326) as shape
						from PFOR
					) as pfor
					join foliage2.FLGVINCOLI_TAB V using (ID_VINCOLO)
					join foliage_extra.pai_rischio_valanga_101 hab on (ST_Intersects(pfor.shape, hab.geom))
				where V.cod_vincolo = 'PAI_VALANGHE'
				union all
				select ID_VINCOLO, ID_ISTA, row_number() over() as PROG, CODICE_ARE, NOME_GAZZE,
					ST_Transform(st_intersection(pfor.shape, hab.geom), :sridGeometrie) as SHAPE_INTE
				from (
						select ID_ISTA, ID_VINCOLO, st_setsrid(ST_Transform(shape, 4326), 4326) as shape
						from PFOR
					) as pfor
					join foliage2.FLGVINCOLI_TAB V using (ID_VINCOLO)
					join foliage_extra.aree_protette_106 hab on (ST_Intersects(pfor.shape, hab.geom))
				where V.cod_vincolo = 'AREE_PROTETTE'
			) as T1
	) as T2""", 
					new CampoSet[] {
						new CampoSet(
							"ID_VINCOLO", null,
							"ID_VINCOLO"
						),
						new CampoSet(
							"ID_ISTA", null,
							"ID_ISTA"
						),
						new CampoSet(
							"PROG",
							null,
							"PROG"
						),
						new CampoSet(
							"COD_AREA",
							null,
							"CODICE"
						),
						new CampoSet(
							"NOME_AREA",
							null,
							"DENOMINAZI"
						),
						new CampoSet(
							"SHAPE",
							null,
							"SHAPE_INTE"
						),
						new CampoSet(
							"SUPERFICIE",
							null,
							"SUPE_INTE"
						)
					}, 
					null,
					new String[] {"idIstanza", "sridGeometrie"}
				),
				new CaricamentoQuery(
					"foliage2.flgparticella_forestale_tab",
					String.format(
						"""
with part as (
		select ST_SetSRID(ST_Transform(ST_SetSRID(ST_union(shape), :sridGeometrie), 4326), 4326) as shape
		from foliage2.flgparticella_forestale_shape_tab pf
		where id_ista = :idIstanza
	), rast_int as (
		select ST_area(pf.shape) as superficie,
			d1.rast_dem_int,
			s1.rast_slope_int
		from part pf
		cross join lateral (
			select st_union(st_intersection(ST_SetSRID(st_clip(d.rast, pf.shape), 4326), d.rast)) as rast_dem_int
			from foliage_extra.%s_dem as d
			where ST_Intersects(d.rast, pf.shape)
		) as d1
		cross join lateral (
			select st_union(st_intersection(ST_SetSRID(st_clip(s.rast, pf.shape), 4326), s.rast)) as rast_slope_int
			from foliage_extra.%s_slope as s
			where ST_Intersects(s.rast, pf.shape)
		) as s1
	), vals as (
		select superficie, rast_dem_int, rast_slope_int,
			ST_SummaryStats(rast_dem_int) as vals_dem,
			ST_SummaryStats(rast_slope_int) as vals_slope
		from rast_int
	)
select (v.vals_dem).min as dem_min, (v.vals_dem).max as dem_max, (v.vals_dem).mean as dem_avg,
	(v.vals_slope).min as slope_min, (v.vals_slope).max as slope_max, (v.vals_slope).mean as slope_avg,
	v.superficie, rast_dem_int, rast_slope_int
from vals as v""",
						regionName, regionName
					),
					new CampoSet[] {
						new CampoSet(
							"ID_ISTA", null,
							":idIstanza"
						),
						new CampoSet(
							"SUPERFICIE_PFOR", null,
							"superficie"
						),
						new CampoSet(
							"ALTIMETRIA_MIN", null,
							"dem_min"
						),
						new CampoSet(
							"ALTIMETRIA_MAX", null,
							"dem_max"
						),
						new CampoSet(
							"ALTIMETRIA_AVG", null,
							"dem_avg"
						),
						new CampoSet(
							"PENDENZA_MIN", null,
							"slope_min"
						),
						new CampoSet(
							"PENDENZA_MAX", null,
							"slope_max"
						),
						new CampoSet(
							"PENDENZA_AVG", null,
							"slope_avg"
						),
						new CampoSet(
							"DEM_RASTER",
							null,
							"RAST_DEM_INT"
						),
						new CampoSet(
							"SLOPE_RASTER",
							null,
							"RAST_SLOPE_INT"
						)
					},
					null,
					new String[] {"idIstanza", "sridGeometrie"}
				)
			}//post-salvataggio
		);
	}
	private static final ISchedaIstanzaWithContext GetDettagliInterventoComunicazione() throws Exception {
		return new SchedaIstanzaGenerica(
			"DettagliInterventoComunicazione",
			new CorrispondenzaJson(
				new CorrispondenzaJsonDiretta[] {
					new CorrispondenzaJsonDiretta("idIntervento", JsonIO.NumberIo),
					new CorrispondenzaJsonDiretta("valoreIntervento", JsonIO.DecimalIo),
					new CorrispondenzaJsonDiretta("descrizioneIntervento", JsonIO.StringIo)
				}
			),
			new IOperazioneDb[]{
				new RecuperoDb(
					"foliage2.FLGISTA_INTERVENTO_COMUNICAZIONE_TAB",
					new CampoSelect[] {
						new CampoSelect(
							"ID_TIPO_INTERVENTO", "idIntervento",
							DbUtils.GetInteger
						),
						new CampoSelect(
							"VALORE_DICHIATATO", "valoreIntervento",
							DbUtils.GetDecimal
						),
						new CampoSelect(
							"DESC_INTERVENTO", "descrizioneIntervento",
							DbUtils.Get
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("ID_ISTA", "idIstanza")
					}
				)
			},//lettura
			new IOperazioneDb[]{
				new CaricamentoRecord(
					"foliage2.FLGISTA_INTERVENTO_COMUNICAZIONE_TAB", 
					new CampoSet[] {
						new CampoSet("ID_ISTA", "idIstanza"),
						new CampoSet("DESC_INTERVENTO", "descrizioneIntervento"),
						new CampoSet("VALORE_DICHIATATO", "valoreIntervento"),
						new CampoSet("ID_TIPO_INTERVENTO", "idIntervento")
					}
				)
			},//scrittura
			new IOperazioneDb[]{
				new EliminazioneDb(
					"foliage2.FLGISTA_INTERVENTO_COMUNICAZIONE_TAB", 
					new CondizioneEq[] {
						new CondizioneEq("ID_ISTA", "idIstanza")
					}
				)
			},//cancellazione
			(String[])null
		);
	}
	private static final ISchedaIstanzaWithContext GetDestinazioniUsoBoschivo() throws Exception {
		//new CorrispondenzaJsonDiretta[]

		String[] elenchi = new String[] {"0", "1"};
		String[] assortimenti = new String[] {"LEGNA", "COMBUSTIBILE", "TRONCHI", "CELLULOSA", "ALTRO"};
		String[] dest = new String[] {"PercAutoc", "PercVendita"};

		//LEGNAPercVenditaSeconda


		// List<String> ass = assortimentiList.stream().flatMap(
		// 	a -> destList.stream().map(
		// 		d -> elenchiList.stream().map(
		// 			e -> String.format("%s%s%s", a, d, e)
		// 		)
		// 	)
		// ).toList();


		LinkedList<String> values = new LinkedList<>();
		LinkedList<String> ass = new LinkedList<>();
		LinkedList<String> props = new LinkedList<>();
		LinkedList<String> pivot = new LinkedList<>();
		LinkedList<CampoSelect> campiSelectSecondoRecupero = new LinkedList<>();
		for (String e : elenchi) {
			for (String a : assortimenti) {
				for (String d : dest) {
					String assEntry = String.format("%s%s%s", a, d, e);
					values.addLast(String.format("('%s', '%s', :specie%s, :%s::float)", a, d, e, assEntry));
					pivot.addLast(
						String.format(
							//max(case when ft.nome_assortimento = 'LEGNA' and a.is_autoconsumo = true and PROG = 0 then percentuale_ass end) as LEGNAPercAutoc0
							"max(case when ft.nome_assortimento = '%s' and a.is_autoconsumo = %s and PROG = %s then percentuale_ass end) as %s",
							a,
							(d.equals("PercAutoc") ? "true" : "false"),
							e,
							assEntry
						)
					);
					//max(case when ft.nome_assortimento = 'LEGNA' and a.is_autoconsumo = true and PROG = 0 then percentuale_ass end) as LEGNAPercAutoc0
					ass.addLast(assEntry);
					campiSelectSecondoRecupero.addLast(
						new CampoSelect(
							assEntry,
							assEntry,
							DbUtils.GetDecimal
						)
					);
				}
			}
		}
		//ass.addAll(Arrays.asList("specie0", "specie1", "categoria0", "categoria1", "macrocategoria0", "macrocategoria1"));
		ass.addAll(Arrays.asList("specie0", "specie1"));
		props.addAll(ass);
		ass.addAll(Arrays.asList("percCopertura0", "percCopertura1"));
		props.add("idIstanza");

		String[] arrProps = props.toArray(new String[0]);


		String querySecondoCaricamento = String.format(
			"""
select ID_SPECIE, ID_ASSORTIMENTO,
	dest = 'PercAutoc' as IS_AUTOCONSUMO,
	val as PERCENTUALE_ASS
from (
	values%s
	) as T(NOME_ASSORTIMENTO, dest, ID_SPECIE, val)
	join foliage2.FLGASSORTIMENTO_TAB A using (NOME_ASSORTIMENTO)
where ID_SPECIE is not null
	and val is not null""",
			values.stream().collect(Collectors.joining("""
,
		"""
				)
			)
		);

		String querySecondaAcquisizione = String.format(
			"""
(
		select 
			%s
		--select *
		from foliage2.FLGASS_SPECI_ISTA_TAB a
			join foliage2.flgspeci_ista_tab fit using (ID_ISTA, ID_SPECIE)
			join foliage2.flgassortimento_tab ft  using (ID_ASSORTIMENTO)
		where ID_ISTA = :idIstanza
	) as T""", 
			pivot.stream().collect(Collectors.joining("""
,
	"""
				)
			)
		);


		LinkedList<CorrispondenzaJsonDiretta> corr =  new LinkedList<>();
		corr.addAll(
			ass.stream().map(
				s -> new CorrispondenzaJsonDiretta(s, JsonIO.NumberIo)
			).toList()
		);


		CorrispondenzaJsonDiretta[] assArr =  corr.toArray(new CorrispondenzaJsonDiretta[0]);

		return new SchedaIstanzaGenerica(
			"DestinazioniUsoBoschivo",
			new CorrispondenzaJson(assArr),
			new IOperazioneDb[] {
				new RecuperoDb(
					"""
(
		select max(case when PROG = 0 then ID_SPECIE end) as specie0,
			max(case when PROG = 1 then ID_SPECIE end) as specie1,
			max(case when PROG = 0 then percentuale_intervento end) as percCopertura0,
			max(case when PROG = 1 then percentuale_intervento end) as percCopertura1
		from foliage2.flgspeci_ista_tab
			join foliage2.flgspecie_tab using (id_specie)
		where ID_ISTA = :idIstanza
	) as T
					""", 
					new CampoSelect[] {
						new CampoSelect("specie0", "specie0", DbUtils.GetInteger),
						new CampoSelect("specie1", "specie1", DbUtils.GetInteger),
						new CampoSelect("percCopertura0", "percCopertura0", DbUtils.GetInteger),
						new CampoSelect("percCopertura1", "percCopertura1", DbUtils.GetInteger)
					}, 
					null,
					new String[] {"idIstanza"}
				),
				new RecuperoDb(
					querySecondaAcquisizione, 
					campiSelectSecondoRecupero.toArray(new CampoSelect[0]), 
					null,
					new String[] {"idIstanza"}
				)
			},
			new IOperazioneDb[]{
				new CaricamentoQuery("foliage2.FLGSPECI_ISTA_TAB", 
					"""
select ID_ISTA, PROG, ID_SPECIE, PERCENTUALE_INTERVENTO
from (
		values
			(:idIstanza, 0, :specie0, :percCopertura0),
			(:idIstanza, 1, :specie1, :percCopertura1)
	) as T(ID_ISTA, PROG, ID_SPECIE, PERCENTUALE_INTERVENTO)
where ID_SPECIE is not null
	and PERCENTUALE_INTERVENTO is not null""",
					new CampoSet[] {
						new CampoSet("ID_ISTA", null, "ID_ISTA"),
						new CampoSet("PROG", null, "PROG"),
						new CampoSet("ID_SPECIE", null, "ID_SPECIE"),
						new CampoSet("PERCENTUALE_INTERVENTO", null, "PERCENTUALE_INTERVENTO")
					},
					null,
					new String[] {
						"idIstanza", "specie0", "percCopertura0", "specie1", "percCopertura1"
					}
				),
				new CaricamentoQuery("foliage2.FLGASS_SPECI_ISTA_TAB", 
					querySecondoCaricamento,
					new CampoSet[] {
						new CampoSet("ID_ISTA", "idIstanza"),
						new CampoSet("ID_SPECIE", null, "ID_SPECIE"),
						new CampoSet("ID_ASSORTIMENTO", null, "ID_ASSORTIMENTO"),
						new CampoSet("IS_AUTOCONSUMO", null, "IS_AUTOCONSUMO"),
						new CampoSet("PERCENTUALE_ASS", null, "PERCENTUALE_ASS")
					},
					null,
					arrProps
				)
			},
			new IOperazioneDb[] {
				new EliminazioneDb("foliage2.FLGASS_SPECI_ISTA_TAB",
					new CondizioneEq[] {
						new CondizioneEq(
							"ID_ISTA", "idIstanza"
						)
					}
				),
				new EliminazioneDb("foliage2.FLGSPECI_ISTA_TAB",
					new CondizioneEq[] {
						new CondizioneEq(
							"ID_ISTA", "idIstanza"
						)
					}
				)
			},
			null
		);
	}
	private static final ISchedaIstanzaWithContext GetSoprasuoloBoschivo() throws Exception {
		return new SchedaIstanzaGenerica(
			"SoprasuoloBoschivo",
			new CorrispondenzaJson(
				new CorrispondenzaJsonDiretta[] {
					new CorrispondenzaJsonDiretta(
						"superficie",
						JsonIO.DecimalIo
					),
					new CorrispondenzaJsonDiretta(
						"formaDiGoverno",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"strutturaDelSoprasuolo",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"etaMediaDelSoprassuoloAnni",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"trattamentoPrecedenteCeduo",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"trattamentoCeduo",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"trattamentoPrecedenteFustaia",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"trattamentoFustaia",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"tipoDiSoprasuolo",
						JsonIO.StringIo
					)
				}
			),
			new IOperazioneDb[] {
				new RecuperoDb(
					"foliage2.flgschede_intervento_limitazione_vinca_tab si",
					new CampoSelect[] {
						new CampoSelect(
							"DESC_GOVE", "formaDiGoverno",
							DbUtils.Get
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("id_scheda_intervento", "idSchedaInterventoSelezionata")
					}
				),
				new RecuperoDb(
					"foliage2.FLGISTA_TAGLIO_BOSCHIVO_TAB",
					new CampoSelect[] {
						new CampoSelect(
							"SUPERFICIE_INTERVENTO", "superficie",
							DbUtils.GetDecimal
						),
						new CampoSelect(
							"DESC_GOVE", "formaDiGoverno",
							DbUtils.Get
						),
						new CampoSelect(
							"ID_SSPR", "strutturaDelSoprasuolo",
							DbUtils.GetInteger
						),
						new CampoSelect(
							"ETA_MEDIA", "etaMediaDelSoprassuoloAnni",
							DbUtils.GetInteger
						),
						new CampoSelect(
							"TIPO_SOPRASUOLO",
//							"coalesce(TIPO_SOPRASUOLO, '')",
							"tipoDiSoprasuolo",
							DbUtils.Get
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("ID_ISTA", "idIstanza")
					}
				),
				new RecuperoDb(
					"""
(
		select *
		from foliage2.FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_TAB tb
			join foliage2.flggove_tab g on (g.id_gove = tb.id_gove)
		where g.desc_gove = 'Ceduo'
	) t""",
					new CampoSelect[] {
						new CampoSelect(
							"ID_FORMA_TRATTAMENTO_PREC", "trattamentoPrecedenteCeduo",
							DbUtils.GetInteger
						),
						new CampoSelect(
							"ID_FORMA_TRATTAMENTO", "trattamentoCeduo",
							DbUtils.GetInteger
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				),
				new RecuperoDb(
					"""
(
		select *
		from foliage2.FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_TAB tb
			join foliage2.flggove_tab g on (g.id_gove = tb.id_gove)
		where g.desc_gove = 'Fustaia'
	) t""",
					new CampoSelect[] {
						new CampoSelect(
							"ID_FORMA_TRATTAMENTO_PREC", "trattamentoPrecedenteFustaia",
							DbUtils.GetInteger
						),
						new CampoSelect(
							"ID_FORMA_TRATTAMENTO", "trattamentoFustaia",
							DbUtils.GetInteger
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				)
			},//lettura
			new IOperazioneDb[] {
				new CaricamentoRecord(
					"foliage2.FLGISTA_TAGLIO_BOSCHIVO_TAB", 
					new CampoSet[]{
						new CampoSet(
							"ID_ISTA", "idIstanza"
						),
						new CampoSet(
							"SUPERFICIE_INTERVENTO", "superficie"
						),
						new CampoSet(
							"DESC_GOVE", "formaDiGoverno"
						),
						new CampoSet(
							"ID_SSPR", "strutturaDelSoprasuolo"
						),
						new CampoSet(
							"TIPO_SOPRASUOLO", "tipoDiSoprasuolo", "nullif(:tipoDiSoprasuolo, '')"
						),
						new CampoSet(
							"ETA_MEDIA", "etaMediaDelSoprassuoloAnni", ":etaMediaDelSoprassuoloAnni"
						)
					}
				),
				new CaricamentoQuery(
					"foliage2.FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_TAB", 
					"""
select ID_GOVE, ID_FORMA_TRATTAMENTO_PREC, ID_FORMA_TRATTAMENTO
from (
		values('Ceduo', :trattamentoPrecedenteCeduo::int, :trattamentoCeduo::int),
		('Fustaia', :trattamentoPrecedenteFustaia::int, :trattamentoFustaia::int)
	) as FORME_TRAT(DESC_GOVE, ID_FORMA_TRATTAMENTO_PREC, ID_FORMA_TRATTAMENTO)
	join foliage2.flggove_tab using (DESC_GOVE)
where (
		ID_FORMA_TRATTAMENTO is not null
		or ID_FORMA_TRATTAMENTO_PREC is not null
	)
	and DESC_GOVE = coalesce(nullif(:formaDiGoverno, 'Misto'), DESC_GOVE) """,
					new CampoSet[] {
						new CampoSet("ID_ISTA", "idIstanza"),
						new CampoSet("ID_GOVE", null, "ID_GOVE"),
						new CampoSet("ID_FORMA_TRATTAMENTO_PREC", null, "ID_FORMA_TRATTAMENTO_PREC"),
						new CampoSet("ID_FORMA_TRATTAMENTO", null, "ID_FORMA_TRATTAMENTO")
					},
					null,
					new String[] {
						"trattamentoPrecedenteCeduo", "trattamentoCeduo", "trattamentoPrecedenteFustaia", "trattamentoFustaia", "formaDiGoverno"
					}
				)
			},//scrittura
			new IOperazioneDb[]{
				new EliminazioneDb(
					"foliage2.FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_TAB tb",
					new CondizioneEq[] {
						new CondizioneEq("id_ista","idIstanza")
					}
				),
				new EliminazioneDb(
					"foliage2.FLGISTA_TAGLIO_BOSCHIVO_TAB tb",
					new CondizioneEq[] {
						new CondizioneEq("id_ista","idIstanza")
					}
				)
			},//cancellazione
			new String[] { "idSchedaInterventoSelezionata", "tipoDiSoprasuolo" }
		);
	}
	private static final ISchedaIstanzaWithContext GetVerificaNat2kSottosoglia() throws Exception {
		return new SchedaIstanzaGenerica(
			"VerificaNat2kBoschivo",
			new CorrispondenzaJson(
				new CorrispondenzaJsonDiretta[] {
					new CorrispondenzaJsonDiretta(
						"hasVisioneVincoli",
						JsonIO.BooleanIo
					),
					new CorrispondenzaJsonDiretta(
						"idSchedaInterventoSelezionata",
						JsonIO.NumberIo
					)
				}
			),
			new IOperazioneDb[] {
				new RecuperoDb(
					"foliage2.FLGISTA_TAB", 
					new CampoSelect[] {
						new CampoSelect(
							"HAS_VISIONE_VINCOLI", "hasVisioneVincoli", 
							DbUtils.GetBoolean
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				),
				new RecuperoDb(
					"foliage2.FLGSCHEDE_INTERVENTO_LIMITAZIONE_VINCA_TAB", 
					new CampoSelect[] {
						new CampoSelect(
							"desc_gove", "formaDiGoverno", 
							DbUtils.Get
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("id_scheda_intervento", "idSchedaInterventoSelezionata")
					}
				)
			},//select
			new IOperazioneDb[] {//inserimento
				new AggiornamentoDb(
					"foliage2.flgista_tab", 
					new CampoSet[]{
						new CampoSet(
							"id_scheda_intervento", "idSchedaInterventoSelezionata"
						),
						new CampoSet(
							"HAS_VISIONE_VINCOLI", "hasVisioneVincoli"
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				)
			},
			new IOperazioneDb[] {
				new AggiornamentoDb(
					"foliage2.flgista_tab",
					new CampoSet[] {
						new CampoSet(
							"id_scheda_intervento", null, "null"
						),
						new CampoSet(
							"HAS_VISIONE_VINCOLI", null, "null"
						)
					},
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				)
			},//delete
			new String[] { "formaDiGoverno", "id_scheda_intervento", "hasVisioneVincoli" }
		);
	}
	private static final ISchedaIstanzaWithContext GetParticelleCatastali() throws Exception {
		return new SchedaIstanzaArray(
			"particelleCatastali",
			new RecuperoDb(
"""
foliage2.FLGPART_CATASTALI_TAB
	join foliage2.flgcomu_viw using (ID_COMUNE)
		""",
				new CampoSelect[] {
					new CampoSelect( 
						"ID_PROVINCIA", "provincia",
						DbUtils.GetInteger
					),
					new CampoSelect( 
						"ID_COMUNE", "comune",
						DbUtils.GetInteger
					),
					new CampoSelect( 
						"SEZIONE", "sezione",
						DbUtils.Get
					),
					new CampoSelect( 
						"FOGLIO", "foglio",
						DbUtils.GetInteger
					),
					new CampoSelect( 
						"PARTICELLA", "particella",
						DbUtils.Get
					),
					new CampoSelect( 
						"SUB", "sub",
						DbUtils.Get
					),
					new CampoSelect( 
						"SUPERFICIE", "superficie",
						DbUtils.Get
					),
					new CampoSelect( 
						"SUPERFICIE_INTERVENTO", "superficieInterventoPart",
						DbUtils.Get
					)
				},
				new CondizioneEq[] {
					new CondizioneEq("id_ista", "idIstanza")
				}
			),
			new EliminazioneDb[] {
				new EliminazioneDb(
					"foliage2.FLGPART_CATASTALI_TAB",
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				)
			},
			new SchedaIstanzaGenerica(
				"ParticelleCatastali",
				new CorrispondenzaJson(
					new CorrispondenzaJsonDiretta[] {
						new CorrispondenzaJsonDiretta(
							"provincia",
							JsonIO.NumberIo
						),
						new CorrispondenzaJsonDiretta(
							"comune",
							JsonIO.NumberIo
						),
						new CorrispondenzaJsonDiretta(
							"sezione",
							JsonIO.StringIo
						),
						new CorrispondenzaJsonDiretta(
							"foglio",
							JsonIO.NumberIo
						),
						new CorrispondenzaJsonDiretta(
							"particella",
							JsonIO.StringIo
						),
						new CorrispondenzaJsonDiretta(
							"sub",
							JsonIO.StringIo
						),
						new CorrispondenzaJsonDiretta(
							"superficie",
							JsonIO.NumberIo
						),
						new CorrispondenzaJsonDiretta(
							"superficieInterventoPart",
							JsonIO.NumberIo
						)
					}
				),
				(IOperazioneDb[]) null,
				new IOperazioneDb[] {
					new CaricamentoRecord(
						"foliage2.FLGPART_CATASTALI_TAB",
						new CampoSet[] {
							new CampoSet("ID_ISTA", "idIstanza"),
							new CampoSet("ID_COMUNE", "comune"),
							new CampoSet("SEZIONE", "sezione"),
							new CampoSet("FOGLIO", "foglio"),
							new CampoSet("PARTICELLA", "particella"),
							new CampoSet("SUB", "sub"),
							new CampoSet("SUPERFICIE", "superficie"),
							new CampoSet("SUPERFICIE_INTERVENTO", "superficieInterventoPart")
						}
					)

				},
				(IOperazioneDb[]) null
			),
			"idxPart"
		);
	}
	private static final ISchedaIstanzaWithContext GetParticellaForestaleSottoSoglia() throws Exception {
		return new SchedaIstanzaArray(
			"shapeParticellaForestale",
			new RecuperoDb(
				"foliage2.FLGPARTICELLA_FORESTALE_SHAPE_TAB",
				new CampoSelect[] {
					new CampoSelect(
						"PROG_GEOM", "progShape",
						DbUtils.Get
					),
					new CampoSelect(
						"wkt_shape", 
						"ST_AsText(shape)", "shape",
						DbUtils.Get
					)
				},
				new CondizioneEq[] {
					new CondizioneEq("id_ista", "idIstanza")
				}
			),	
			new IOperazioneDb[] {
				new AggiornamentoDb(
					"foliage2.FLGISTA_TAB", 
					new CampoSet[]{
						new CampoSet(
							"id_scheda_intervento", null, "null"
						)
					}, 
					new CondizioneEq[] {
						new CondizioneEq(
							"id_ista", "idIstanza"
						)
					}
				),
				new EliminazioneDb(
					"foliage2.FLGVINCOLI_ISTA_TAB", 
					new CondizioneEq[] {
						new CondizioneEq(
							"id_ista", "idIstanza"
						)
					}
				),
				new EliminazioneDb(
					"foliage2.FLGPARTICELLA_FORESTALE_SHAPE_TAB",
					new CondizioneEq[] {
						new CondizioneEq("id_ista", "idIstanza")
					}
				)
			},
			new SchedaIstanzaGenerica(
				"ParticellaForestale",
				new CorrispondenzaJson(
					new CorrispondenzaJsonDiretta[] {
						new CorrispondenzaJsonDiretta("id", "progShape", JsonIO.NumberIo),
						new CorrispondenzaJsonDiretta("shape", JsonIO.StringIo)
					}
				),
				null,
				new IOperazioneDb[] {
					new CaricamentoRecord(
						"foliage2.flgparticella_forestale_shape_tab",
						new CampoSet[] {
							new CampoSet(
								"id_ista",
								"idIstanza"
							),
							new CampoSet(
								"prog_geom",
								"idxGeom"
							),
							new CampoSet(
								"shape",
								"shape",
								"st_geomfromtext(:shape)"
							),
							new CampoSet(
								"superficie",
								"shape",
								"st_area(st_geomfromtext(:shape))"
							)
						}
					)
				},
				null
			),
			"idxGeom",
			null,
			new IOperazioneDb[] {
				new CaricamentoQuery(
					"foliage2.FLGVINCOLI_ISTA_TAB", 
					"""
select *
from (
		select T1.*, st_area(SHAPE_INTE) as SUPE_INTE
		from (
				select ID_VINCOLO, ID_ISTA, row_number() over() as PROG, HABITAT::varchar as CODICE, REL_DESCRI::varchar as DENOMINAZI,
					ST_Transform(st_intersection(pfor.shape, hab.geom), :sridGeometrie) as SHAPE_INTE
				from (
						select ID_ISTA, ST_SetSRID(ST_Transform(ST_SetSRID(shape, :sridGeometrie), 6706), 6706) as shape
						from foliage2.flgparticella_forestale_shape_tab
						where ID_ISTA = :idIstanza
					) as pfor
					join foliage_extra.nat2k_habitat_prioritari_208 hab on (ST_Intersects(pfor.shape, hab.geom))
					cross join foliage2.FLGVINCOLI_TAB V
				where V.cod_vincolo = 'HABITAT_PRIOR'
				union all
				select ID_VINCOLO, ID_ISTA, row_number() over() as PROG, CODICE, DENOMINAZI,
					ST_Transform(st_intersection(pfor.shape, hab.geom), :sridGeometrie) as SHAPE_INTE
				from (
						select ID_ISTA, ST_SetSRID(ST_Transform(ST_SetSRID(shape, :sridGeometrie), 4326), 4326) as shape
						from foliage2.flgparticella_forestale_shape_tab 
						where ID_ISTA = :idIstanza
					) as pfor
					join foliage_extra.sitiprotetti_natura_2000 hab on (ST_Intersects(pfor.shape, hab.geom))
					cross join foliage2.FLGVINCOLI_TAB V
				where V.cod_vincolo = 'NAT2K'
			) as T1
	) as T2
--where T2.SUPE_INTE > 0
					""", 
					new CampoSet[] {
						new CampoSet(
							"ID_VINCOLO", null,
							"ID_VINCOLO"
						),
						new CampoSet(
							"ID_ISTA", null,
							"ID_ISTA"
						),
						new CampoSet(
							"PROG",
							null,
							"PROG"
						),
						new CampoSet(
							"COD_AREA",
							null,
							"CODICE"
						),
						new CampoSet(
							"NOME_AREA",
							null,
							"DENOMINAZI"
						),
						new CampoSet(
							"SHAPE",
							null,
							"SHAPE_INTE"
						),
						new CampoSet(
							"SUPERFICIE",
							null,
							"SUPE_INTE"
						)
					}, 
					null,
					new String[] {"idIstanza", "sridGeometrie"}
				)
			}
		);
	}
	private static final ISchedaIstanzaWithContext GetSchedaTipoGestione() throws Exception {
		RecuperoDb recuperoFileGesitone = new RecuperoDb(
			"foliage2.flgfiletipo_gestione_tab",
			new CampoSelect[] {
				new CampoSelect(
					"id_file_autocertificazione_proprieta", "idFileAutocertificazioneProprieta",
					DbUtils.GetInteger
				),
				new CampoSelect(
					"id_file_delega_presentazione", "idFileDelegaPresentazione",
					DbUtils.GetInteger
				),
				new CampoSelect(
					"id_file_delega_titolarita", "idFileDelegaTitolarita",
					DbUtils.GetInteger
				),
				new CampoSelect(
					"is_pesona_giuridica", "id_file_atto_nomina_rappresentante_legale is not null", "isPersonaGiuridica",
					DbUtils.GetBoolean
				),
				new CampoSelect(
					"id_file_atto_nomina_rappresentante_legale", "idFileAttoNominaRappresentanteLegale",
					DbUtils.GetInteger
				),
				new CampoSelect(
					"is_bosco_silente", "id_file_provvedimento_boschi_silenti is not null", "isBoscoSilente",
					DbUtils.GetBoolean
				),
				new CampoSelect(
					"id_file_provvedimento_boschi_silenti", "idFileProvvedimentoBoschiSilenti",
					DbUtils.GetInteger
				),
				new CampoSelect(
					"is_ditta_forestale", "id_file_autocertificazione_ditta_forestale is not null", "isDittaForestale",
					DbUtils.GetBoolean
				),
				new CampoSelect(
					"id_file_autocertificazione_ditta_forestale", "idFileAutocertificazioneDittaForestale",
					DbUtils.GetInteger
				),
				new CampoSelect(
					"id_file_documenti_identita", "idFileDocumentiIdentita",
					DbUtils.GetInteger
				)
			},
			new CondizioneEq[] {
				new CondizioneEq(
					"id_ista",
					"idIstanza"
				)
			}
		);
		EliminazioneDb eliminazioneFileGestione = recuperoFileGesitone.toEliminazione();

		return new SchedaIstanzaGenerica(
			"TipoGestione",
			new CorrispondenzaJson(
				new CorrispondenzaJsonDiretta[] {
					new CorrispondenzaJsonDiretta(
						"qualificaTitolare", "idQualificaTitolare",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"tipoProprieta", "idTipoProprieta",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"naturaProprieta", "idNaturaProprieta",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"tipoAzienda", "idTipoAzienda",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"autocertificazioneProprieta", "fileAutocertificazioneProprieta",
						JsonIO.Base64FormioFileIo
					),
					new CorrispondenzaJsonDiretta(
						"delegaPresentazione", "fileDelegaPresentazione",
						JsonIO.Base64FormioFileIo
					),
					new CorrispondenzaJsonDiretta(
						"delegaAllaTitolarita", "fileDelegaTitolarita",
						JsonIO.Base64FormioFileIo
					),
					new CorrispondenzaJsonDiretta(
						"isPersonaGiuridica", "isPersonaGiuridica",
						JsonIO.BooleanIo
					),
					new CorrispondenzaJsonDiretta(
						"attoNominaRappresentanteLegale", "fileAttoDiNominaRappresentanteLegale",
						JsonIO.Base64FormioFileIo
					),
					new CorrispondenzaJsonDiretta(
						"isBoscoSilente", "isBoscoSilente",
						JsonIO.BooleanIo
					),
					new CorrispondenzaJsonDiretta(
						"provvedimentoBoschiSilenti", "fileProvvedimentoBoschiSilenti",
						JsonIO.Base64FormioFileIo
					),
					new CorrispondenzaJsonDiretta(
						"isDittaForestale", "isDittaForestale",
						JsonIO.BooleanIo
					),
					new CorrispondenzaJsonDiretta(
						"autocertificazioneDittaForestale", "fileAutocertificazioneDittaForestale",
						JsonIO.Base64FormioFileIo
					),
					new CorrispondenzaJsonDiretta(
						"documentiIdentita", "fileDocumentiIdentita",
						JsonIO.Base64FormioFileIo
					)
				}
			),
			new IOperazioneDb[] {
				recuperoFileGesitone,
				new RecuperoFileBase64("idFileAutocertificazioneProprieta", "fileAutocertificazioneProprieta"),
				new RecuperoFileBase64("idFileDelegaPresentazione", "fileDelegaPresentazione"),
				new RecuperoFileBase64("idFileDelegaTitolarita", "fileDelegaTitolarita"),
				new RecuperoFileBase64("idFileAttoNominaRappresentanteLegale", "fileAttoDiNominaRappresentanteLegale"),
				new RecuperoFileBase64("idFileProvvedimentoBoschiSilenti", "fileProvvedimentoBoschiSilenti"),
				new RecuperoFileBase64("idFileAutocertificazioneDittaForestale", "fileAutocertificazioneDittaForestale"),
				new RecuperoFileBase64("idFileDocumentiIdentita", "fileDocumentiIdentita")
			},
			new IOperazioneDb[] {
				new AggiornamentoDb(
					"foliage2.flgista_tab",
					new CampoSet[] {
						new CampoSet(
							"id_qual", "idQualificaTitolare"
						),
						new CampoSet(
							"id_tprp", "idTipoProprieta"
						),
						new CampoSet(
							"id_nprp", "idNaturaProprieta"
						),
						new CampoSet(
							"id_tazi", "idTipoAzienda"
						)
					},
					new CondizioneEq[] {
						new CondizioneEq(
							"id_ista", "idIstanza"
						)
					}
				),
				new CaricamentoFileBase64("idFileAutocertificazioneProprieta", "fileAutocertificazioneProprieta"),
				new CaricamentoFileBase64("idFileDelegaPresentazione", "fileDelegaPresentazione"),
				new CaricamentoFileBase64("idFileDelegaTitolarita", "fileDelegaTitolarita"),
				new CaricamentoFileBase64("idFileAttoNominaRappresentanteLegale", "fileAttoDiNominaRappresentanteLegale"),
				new CaricamentoFileBase64("idFileProvvedimentoBoschiSilenti", "fileProvvedimentoBoschiSilenti"),
				new CaricamentoFileBase64("idFileAutocertificazioneDittaForestale", "fileAutocertificazioneDittaForestale"),
				new CaricamentoFileBase64("idFileDocumentiIdentita", "fileDocumentiIdentita"),
				new CaricamentoRecord(
					"foliage2.flgfiletipo_gestione_tab",
					new CampoSet[] {
						new CampoSet(
							"id_ista", "idIstanza"
						),
						new CampoSet(
							"id_file_autocertificazione_proprieta", "idFileAutocertificazioneProprieta"
						),
						new CampoSet(
							"id_file_delega_presentazione", "idFileDelegaPresentazione"
						),
						new CampoSet(
							"id_file_delega_titolarita", "idFileDelegaTitolarita"
						),
						new CampoSet(
							"id_file_atto_nomina_rappresentante_legale", "idFileAttoNominaRappresentanteLegale"
						),
						new CampoSet(
							"id_file_provvedimento_boschi_silenti", "idFileProvvedimentoBoschiSilenti"
						),
						new CampoSet(
							"id_file_autocertificazione_ditta_forestale", "idFileAutocertificazioneDittaForestale"
						),
						new CampoSet(
							"id_file_documenti_identita", "idFileDocumentiIdentita"
						)
					}
				)
			},
			new IOperazioneDb[] {
				eliminazioneFileGestione
			}
		);
	}

	private static final ISchedaIstanzaWithContext GetSchedaTitolare() throws Exception {
		return new SchedaIstanzaGenerica(
			"Titolare",
			new CorrispondenzaJson(
				new CorrispondenzaJsonDiretta[] {
					new CorrispondenzaJsonDiretta(
						"codiceFiscale", "codiceFiscaleTitolare",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"nome", "nomeTitolare",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"cognome", "cognomeTitolare",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"dataDiNascita", "dataDiNascitaTitolare",
						JsonIO.DateIo
					),
					new CorrispondenzaJsonDiretta(
						"luogoDiNascita", "luogoDiNascitaTitolare",
						JsonIO.StringIo
					),
					// new CorrispondenzaJsonDiretta(
					// 	"email", "emailTitolare",
					// 	JsonIO.StringIo
					// ),
					// new CorrispondenzaJsonDiretta(
					// 	"postaCertificata", "postaCertificataTitolare",
					// 	JsonIO.StringIo
					// ),
					// new CorrispondenzaJsonDiretta(
					// 	"telefono", "telefonoTitolare",
					// 	JsonIO.StringIo
					// ),
					new CorrispondenzaJsonDiretta(
						"fileDelegaProfesssionista", "fileDelegaProfesssionista",
						JsonIO.Base64FormioFileIo
					),
					new CorrispondenzaJsonDiretta(
						"provincia", "idProvinciaTitolare",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"comune", "idComuneTitolare",
						JsonIO.NumberIo
					),
					new CorrispondenzaJsonDiretta(
						"nomeComune", "nomeComuneTitolare",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"nomeProvincia", "nomeProvinciaTitolare",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"genere", "genereTitolare",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"cap", "capTitolare",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"indirizzo", "indirizzoTitolare",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"numeroCivico", "numeroCivicoTitolare",
						JsonIO.StringIo
					)
				}
			),
			new IOperazioneDb[] {
				new RecuperoDb(
					"foliage2.flgtitolare_istanza_tab",
					new CampoSelect[] {
						new CampoSelect(
							"codice_fiscale", "codiceFiscaleTitolare",
							DbUtils.Get
						),
						new CampoSelect(
							"nome", "nomeTitolare",
							DbUtils.Get
						),
						new CampoSelect(
							"cognome", "cognomeTitolare",
							DbUtils.Get
						),
						new CampoSelect(
							"data_nascita", "dataDiNascitaTitolare",
							DbUtils.GetDate
						),
						new CampoSelect(
							"luogo_nascita", "luogoDiNascitaTitolare",
							DbUtils.Get
						),
						// new CampoSelect(
						// 	"telefono", "telefonoTitolare",
						// 	DbUtils.Get
						// ),
						// new CampoSelect(
						// 	"email", "emailTitolare",
						// 	DbUtils.Get
						// ),
						// new CampoSelect(
						// 	"pec", "postaCertificataTitolare",
						// 	DbUtils.Get
						// ),
						new CampoSelect(
							"id_file_delega", "idFileDelegaProfesssionista",
							DbUtils.GetInteger
						),
						new CampoSelect(
							"id_comune", "idComuneTitolare",
							DbUtils.GetInteger
						),
						new CampoSelect(
							"cap", "capTitolare",
							DbUtils.Get
						),
						new CampoSelect(
							"indirizzo", "indirizzoTitolare",
							DbUtils.Get
						),
						new CampoSelect(
							"num_civico", "numeroCivicoTitolare",
							DbUtils.Get
						)
					},
					new CondizioneEq[]{
						new CondizioneEq("id_titolare", "idTitolareIstanza")
					}
				),
				new RecuperoDb(
					"foliage2.flgente_comune_tab",
					new CampoSelect[] {
						new CampoSelect(
							"id_provincia", "idProvinciaTitolare",
							DbUtils.GetInteger
						)
					},
					new CondizioneEq[]{
						new CondizioneEq("id_comune", "idComuneTitolare")
					}
				),
				new RecuperoFileBase64("idFileDelegaProfesssionista", "fileDelegaProfesssionista"),
				new RecuperoDb(
					"foliage2.flgente_root_tab",
					new CampoSelect[] {
						new CampoSelect(
							"nome_ente", "nomeComuneTitolare",
							DbUtils.Get
						)
					},
					new CondizioneEq[]{
						new CondizioneEq("id_ente", "idComuneTitolare")
					}
				),
				new RecuperoDb(
					"foliage2.flgente_root_tab",
					new CampoSelect[] {
						new CampoSelect(
							"nome_ente", "nomeProvinciaTitolare",
							DbUtils.Get
						)
					},
					new CondizioneEq[]{
						new CondizioneEq("id_ente", "idProvinciaTitolare")
					}
				)
			},
			new String [] {
				"codiceFiscaleTitolare", "nomeTitolare", "cognomeTitolare"
			}
		);
	}


	private static final ISchedaIstanzaWithContext GetSchedaTipologiaIstanza() throws Exception {
		return new SchedaIstanzaGenerica(
			"TipologiaIstanza",
			new CorrispondenzaJson(
				new CorrispondenzaJsonDiretta[] {
					new CorrispondenzaJsonDiretta(
						"codice", "codIstanza",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"enteCompetente", "descEnteCompetente",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"tipo", "descTipoIstanza",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"tipoSpecifico", "descTipoIstanzaSpecifico",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"nome", "nomeIstanza",
						JsonIO.StringIo
					),
					new CorrispondenzaJsonDiretta(
						"note", "noteIstanza",
						JsonIO.StringIo
					)
				}
			)
		);
	}
	public static ISchedaIstanzaWithContext TipologiaIstanza;
	public static ISchedaIstanzaWithContext Titolare;
	public static ISchedaIstanzaWithContext TipoGestione;
	public static ISchedaIstanzaWithContext ParticelleCatastali;
	public static ISchedaIstanzaWithContext ParticellaForestaleSottoSoglia;
	public static ISchedaIstanzaWithContext VerificaNat2kSottosoglia;
	public static ISchedaIstanzaWithContext DettagliInterventoComunicazione;
	public static ISchedaIstanzaWithContext SoprasuoloBoschivo;
	public static ISchedaIstanzaWithContext DestinazioniUsoBoschivo;
	public static ISchedaIstanzaWithContext ParticellaForestaleSopraSoglia;
	public static ISchedaIstanzaWithContext StazioneForestale;
	public static ISchedaIstanzaWithContext VincolisticaSopraSoglia;
	public static ISchedaIstanzaWithContext UnitaOmogenee;
	public static SchedaIstanzaGenerica SingolaUnitaOmogenea;
	public static ISchedaIstanzaWithContext AltriStratiInformativi;
	public static ISchedaIstanzaWithContext ViabilitaForestale;
	public static ISchedaIstanzaWithContext DestinazioniUsoEProspettiRiepilogativi;
	public static ISchedaIstanzaWithContext Allegati;
	public static ISchedaIstanzaWithContext Riepilogo;
}
