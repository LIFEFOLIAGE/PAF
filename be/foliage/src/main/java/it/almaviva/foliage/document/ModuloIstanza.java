package it.almaviva.foliage.document;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.bean.DescrizioneIterventoModulo;
import it.almaviva.foliage.bean.DestinazioneUsoModulo;
import it.almaviva.foliage.bean.FileIstanzaWeb;
import it.almaviva.foliage.bean.InterventiNonForestaliModulo;
import it.almaviva.foliage.bean.ParticellaCatastaleModulo;
import it.almaviva.foliage.bean.SoprasuoloForestaleModulo;
import it.almaviva.foliage.bean.UnitaOmogeneaModulo;
import it.almaviva.foliage.bean.VincoloModulo;
import it.almaviva.foliage.function.Function;
import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.services.WebDal;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;


public class ModuloIstanza {
	private static float paragraphTopMargin = 60;
	private static float paragraphTopMargin2 = 30;
	private static BigDecimal zeroDecimal = BigDecimal.valueOf(0);

	private static DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ITALIAN);
	private static DecimalFormat haFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.ITALIAN);
	private static DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
	private static DecimalFormatSymbols haSymbols = haFormatter.getDecimalFormatSymbols();

	static {
		symbols.setGroupingSeparator('.');
		symbols.setDecimalSeparator(',');
		formatter.setDecimalFormatSymbols(symbols);
		formatter.setMaximumFractionDigits(2);
		formatter.setGroupingSize(3);

		
		haSymbols.setGroupingSeparator('.');
		haSymbols.setDecimalSeparator(',');
		haFormatter.setDecimalFormatSymbols(symbols);
		haFormatter.setMaximumFractionDigits(4);
		haFormatter.setGroupingSize(3);
	}

	private static String covertDecimalToString(BigDecimal v) {
		if (v != null ) {
			return formatter.format(v);
		}
		else {
			return "0.00";
		}
	}
	
	private static String covertDecimalToHa(BigDecimal v) {
		
		if (v != null ) {
			return haFormatter.format(v);
		}
		else {
			return "0.00";
		}
	}

	private static Function<String, BigDecimal> decToString = ModuloIstanza::covertDecimalToString;
	
	public String nomeEnte;
	public String codIstanza;
	public String codFiscaleTitolare;
	public String nomeTitolare;
	public String cognomeTitolare;
	public String luogoNascTitolare;
	public String dataNascTitolare;
	public String qualificaTitolare;

	public String comuneTitolare;
	public String indirizzoTitolare;
	public String numCivicoTitolare;
	public String telefonoTitolare;


	public String tipoIstanza;
	public boolean isDeroga = false;
	public boolean isSottoSoglia = false;
	public boolean isBozza = true;
	public String codRegione;
	public String naturaProprieta;
	public String tipoProprieta;
	public LocalDate dataInvio;
	public String note;
	public String comune;
	public String provincia;
	public String schedaIntervento;
	public String linkPdfIntervento;
	public Collection<ParticellaCatastaleModulo> infoPart;
	public Collection<UnitaOmogeneaModulo> infoUO;
	public SoprasuoloForestaleModulo sezioneA;
	public InterventiNonForestaliModulo sezioneB;
	public Collection<VincoloModulo> infoVincoli;
	public Collection<FileIstanzaWeb> infoFile;
	public BigDecimal supeficieGeometrica;

	public Collection<Pair<String, String>> vincoliPerTipoIstanza;
	public HashMap<String, java.util.List<Pair<String, String>>> vincoliIstanza;
	public LinkedList<String> limitazioni;
	public LinkedList<String> vincoliEsenti;

	public static String[] LIMITAZIONI = { "NAT2K_4000MQ", "NAT2K_SOTTO_UMBRIA", "HABITAT_VINCA_BOSCHIVO" };
	public static HashMap<String, String> LIMITAZIONI_MAP = new HashMap<>(){{
		put("NAT2K_4000MQ", "che la superficie dell'intervento è inferiore a 4.000 mq");
		put("NAT2K_SOTTO_UMBRIA", "l'intervento rispetta le condizioni di obbligo generale per gli interventi nei siti Natura 2000 (DGR Umbria n. 1093 del 10/11/2021)");
		put("HABITAT_VINCA_BOSCHIVO", "l'intervento rispetta le condizioni d'obbligo specifiche negli Habitat Prioritari espresse dalle schede pre-screening intervento: %s");
	}};

	public static String[] VINCOLI = {"NAT2K", "HABITAT_PRIOR"};
	public static HashMap<String, String> VINCOLI_MAP = new HashMap<>(){{
		put("NAT2K", "Z.P.S., S.I.C. o Z.S.C");
	}};
	public static HashMap<String, String> VINCOLI_MSG_MAP = new HashMap<>(){{
		put("NAT2K", "che l'intervento ricade nei siti Natura2000:");
		put("HABITAT_PRIOR", "che l'intervento ricade negli Habitat:");
	}};


	public static org.springframework.jdbc.core.RowMapper<ModuloIstanza> RowMapper(WebDal dal, String codIstanza, String codRegione) {
		return RowMapper(dal, null, codIstanza, codRegione);
	}
	public static org.springframework.jdbc.core.RowMapper<ModuloIstanza> RowMapper(WebDal dal, LocalDateTime oraInvio, String codIstanza, String codRegione) {
		return (rs, rn) -> {
			Integer idIsta = rs.getInt("id_ista");
			if (rs.wasNull()) {
				idIsta = null;
			}

			Integer idTipoIsta = rs.getInt("id_tipo_istanza");
			if (rs.wasNull()) {
				idTipoIsta = null;
			}

			HashMap<String, Object> parsMap = new HashMap<>();
			parsMap.put("idIsta", idIsta);
			ModuloIstanza m = new ModuloIstanza();
			m.codIstanza = codIstanza;
			m.codRegione = codRegione;			
			String tipoEnte = rs.getString("tipo_ente");
			m.nomeEnte = String.format(
				"%s %s%s",
				tipoEnte,
				"REGIONE".equals(tipoEnte) ? "" : "di ",
				rs.getString("nome_ente")
			);
			m.tipoIstanza = rs.getString("desc_cist");
			m.naturaProprieta = rs.getString("desc_nprp");
			m.tipoProprieta = rs.getString("desc_tprp");
			m.qualificaTitolare = rs.getString("desc_qual");
			m.isDeroga = rs.getBoolean("is_deroga");
			m.isSottoSoglia = rs.getBoolean("is_sottosoglia");
			m.codFiscaleTitolare = rs.getString("codice_fiscale");
			m.cognomeTitolare = rs.getString("cognome");
			m.nomeTitolare = rs.getString("nome");
			m.luogoNascTitolare = rs.getString("luogo_nascita");
			m.comuneTitolare = rs.getString("comune_tit");
			m.indirizzoTitolare = rs.getString("indirizzo");
			m.numCivicoTitolare = rs.getString("num_civico");
			m.telefonoTitolare = rs.getString("telefono");

			m.supeficieGeometrica = rs.getBigDecimal("supe_geom");
			

			m.comune = rs.getString("comune");
			m.provincia = rs.getString("provincia");
			m.schedaIntervento = rs.getString("desc_intervento");
			if (m.schedaIntervento == null) {
				m.schedaIntervento = "";
			}
			m.linkPdfIntervento = rs.getString("link_pdf_scheda");
			if (m.linkPdfIntervento != null)  {
				m.linkPdfIntervento = m.linkPdfIntervento.replaceAll("^.*([^/]+)", "");
			}
			Date d = rs.getDate("data_nascita");
			if (rs.wasNull()) {
				m.dataNascTitolare = "N. D.";	
			}
			else {
				LocalDate ld = d.toLocalDate();
				m.dataNascTitolare = ld.format(DateTimeFormatter.ofPattern("dd/M/yyyy"));
			}

			
			m.note = rs.getString("note");

			d = rs.getDate("data_invio");
			if (rs.wasNull()) {
				if (oraInvio == null) {
					m.dataInvio = LocalDate.now();
					m.isBozza = true;
				}
				else {
					m.dataInvio = oraInvio.toLocalDate();
					m.isBozza = false;
				}
			}
			else {
				if (oraInvio == null) {
					m.dataInvio = d.toLocalDate();
					m.isBozza = false;
				}
				else {
					throw new FoliageException("L'istanza per cui si sta generando il modulo è già stata inviata");
				}
			}

			m.infoPart = dal.query(
				ParticellaCatastaleModulo.queryFromIdIsta,
				parsMap,
				ParticellaCatastaleModulo.RowMapper
			);
			m.infoUO = dal.query(
				UnitaOmogeneaModulo.queryFromIdIsta,
				parsMap,
				UnitaOmogeneaModulo.RowMapperUO(dal)
			);
			try {
				m.sezioneA = dal.queryForObject(
					SoprasuoloForestaleModulo.queryFromIdIsta,
					parsMap,
					SoprasuoloForestaleModulo.RowMapperSF(dal)
				);
			}
			catch(org.springframework.dao.EmptyResultDataAccessException e) {
				m.sezioneA = null;
			}

			try {
				m.sezioneB = dal.queryForObject(
					InterventiNonForestaliModulo.queryFromIdIsta,
					parsMap,
					InterventiNonForestaliModulo.RowMapper
				);
			}
			catch(org.springframework.dao.EmptyResultDataAccessException e) {
				m.sezioneB = null;
			}
			m.infoVincoli = dal.query(
				VincoloModulo.queryFromIdIsta,
				parsMap,
				VincoloModulo.RowMapper
			);
			m.infoFile = dal.getFileIstanzaWeb(idIsta);

			
			if (m.isSottoSoglia) {
				m.limitazioni = new LinkedList<String>();
				m.vincoliEsenti = new LinkedList<String>();
				m.vincoliIstanza = new HashMap<>();
				final Integer idTipoIstaFin = idTipoIsta;
				HashMap<String, Object> map = new HashMap<>(){{
					put("idTipoIsta", idTipoIstaFin);
				}};
				//id_tipo_istanza

				String query = """
select v.cod_vincolo, l.cod_limitazione
from foliage2.flgvincoli_tipo_ista_tab vi
	join foliage2.flgvincoli_tab v using (id_vincolo)
	join foliage2.flglimitazioni_tab l  using (id_limitazione)
where vi.id_tipo_istanza = :idTipoIsta""";
				m.vincoliPerTipoIstanza = dal.query(
					query,
					map,
					(rs1, rn1) -> {
						return new Pair<String, String>(
							rs1.getString("cod_vincolo"),
							rs1.getString("cod_limitazione")
						);
					}
				);
				Integer idIstaFin = idIsta;
				String queryVinc = """
select cod_area, nome_area
from foliage2.flgvincoli_tab 
	join foliage2.flgvincoli_ista_tab fit using (id_vincolo)
where id_ista = :idIsta
	--and 1 = 0
	and cod_vincolo = :codVincolo""";
				HashMap<String, Object> vincMap = new HashMap<>(){{
					put("idIsta", idIstaFin);
				}};
				org.springframework.jdbc.core.RowMapper<Pair<String, String>> rmVinc = (rs1, rn1) -> {
					return new Pair<String, String>(
						rs1.getString("cod_area"),
						rs1.getString("nome_area")
					);
				};
				for (Pair<String, String> v : m.vincoliPerTipoIstanza) {
					vincMap.put("codVincolo", v.getValue0());
					java.util.List<Pair<String, String>> res = dal.query(
						queryVinc,
						vincMap,
						rmVinc
					);
					if (res.stream().anyMatch(x -> true)) {
						m.vincoliIstanza.put(
							v.getValue0(),
							res
						);
						m.limitazioni.addLast(v.getValue1());
					}
					else {
						m.vincoliEsenti.addLast(v.getValue0());
					}
				}
			}
			return m;
		};
	}

	public static String queryFromCodiIsta = """
select id_ista, desc_ista, er.tipo_ente, nome_ente, desc_nprp, desc_qual, desc_tprp, desc_cist,
	i.id_tipo_istanza,
	cod_tipo_istanza_specifico = 'IN_DEROGA' as is_deroga,
	cod_tipo_istanza = 'SOTTO_SOGLIA' as is_sottosoglia,
	tit.codice_fiscale, tit.cognome, tit.nome, tit.data_nascita, tit.luogo_nascita, tit.indirizzo, tit.num_civico, tit.telefono, ct.comune as comune_tit,
	inv.data_invio, i.note,
	c.provincia, c.comune,
	si.desc_intervento, si.link_pdf_scheda,
	coalesce(
		(
			select sum(superficie)
			from foliage2.flgparticella_forestale_shape_tab pf
			where pf.id_ista = i.id_ista
		),
		0
	) as supe_geom
--select *
from foliage2.flgista_tab i
	join foliage2.flgtipo_istanza_tab ts using (id_tipo_istanza)
	join foliage2.flgcist_tab ti on (ti.id_cist = ts.id_cist)
	join foliage2.flgtitolare_istanza_tab tit using (id_titolare)
	join foliage2.flgente_root_tab er on (er.id_ente = i.id_ente_terr)
	left join foliage2.flgcomu_viw ct on (ct.id_comune = tit.id_comune)
	left join foliage2.flgqual_tab q using (id_qual)
	left join foliage2.flgtprp_tab tp using (id_tprp)
	left join foliage2.flgnprp_tab n using (id_nprp)
	left join foliage2.FLGISTA_INVIO_TAB inv using (id_ista)
	left join foliage2.FLGSCHEDE_INTERVENTO_LIMITAZIONE_VINCA_TAB as si using (id_scheda_intervento)
	left join (
		select id_comune, provincia, comune
		from foliage2.flgcomu_viw
			join foliage2.flgprov_viw using (id_provincia)
	) c on (c.id_comune = i.id_ente_terr)
where codi_ista = :codiIsta""";
	
	private ModuloIstanza() {
	}
	// public ModuloIstanza(Collection<ParticellaCatastaleModulo> pcmo, Collection<UnitaOmogeneaModulo> umo) {
	// 	this.infoPart = pcmo;
	// 	this.infoUO = umo;
	// }
	private static String getString(String s) {
		return (s == null ? "N.D." : s);
	}
	private static BigDecimal zeroDec = new BigDecimal(0);
	private static BigDecimal dieciMilaDec = new BigDecimal(10000);
	public PdfDocument getDocument(PdfWriter writer, int numPages, Boolean isBozza) throws Exception {
		PdfDocument pdfDoc = new PdfDocument(writer);
		Document document = new Document(pdfDoc, PageSize.A4);
		AreaBreak pageBreak = new AreaBreak();
		PdfFont helveticaFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		PdfFont helveticaBoldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		document.setMargins(72, 36, 72, 36);
		
		if (numPages > 0) {
			Table headTable = new Table(1).useAllAvailableWidth();

			Paragraph p = new Paragraph()
				.setFont(helveticaFont)
				.setFontSize(10)
				.setTextAlignment(TextAlignment.LEFT);
			String s = String.format(
				"%s: %s - Presentata il: %s\n",
				tipoIstanza,
				codIstanza,
				dataInvio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
			);
			p.add(s);
			Cell cell = new Cell();
			cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
			cell.add(p);
			headTable.addCell(cell);
			pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new HeaderHandler(document, headTable, numPages,  (isBozza == null) ? this.isBozza : isBozza.booleanValue()));
		}

		{
			Paragraph p = new Paragraph()
				.setFont(helveticaFont)
				.setTextAlignment(TextAlignment.RIGHT);
			String s = String.format("Spett.le %s", nomeEnte);
			p.add(s);
			document.add(p);
		}

		
		{
			Paragraph p = new Paragraph()
				.setMarginTop(paragraphTopMargin2)
				.setFont(helveticaFont)
				.setTextAlignment(TextAlignment.LEFT);
			Text t1 = new Text("oggetto: ")
				.setFont(helveticaBoldFont);
			p.add(t1);
			String s = String.format("istanza di taglio boschivo %s", codIstanza);
			p.add(s);
			document.add(p);
		}

		{
			Paragraph p = new Paragraph()
				.setMarginTop(paragraphTopMargin)
				.setFont(helveticaFont)
				.setTextAlignment(TextAlignment.JUSTIFIED);
			String s = String.format("""
Il sottoscritto %s %s, Nato a %s il %s, C.F %s, residente in %s, %s %s tel. %s, in qualità di: %s""",
				nomeTitolare,
				cognomeTitolare,
				luogoNascTitolare,
				dataNascTitolare,
				codFiscaleTitolare,
				comuneTitolare,
				indirizzoTitolare,
				numCivicoTitolare,
				telefonoTitolare,
				getString(qualificaTitolare)
			);
			p.add(s);
			document.add(p);
		}

		{
			Paragraph p = new Paragraph()
				.setMarginTop(paragraphTopMargin2)
				.setFont(helveticaFont)
				.setTextAlignment(TextAlignment.CENTER);
			String s = isDeroga ? "RICHIEDE" : "COMUNICA";
			p.add(s);
			document.add(p);
		}

		{
			Paragraph p = new Paragraph()
				.setMarginTop(paragraphTopMargin2)
				.setFont(helveticaFont)
				.setTextAlignment(TextAlignment.LEFT);
			String s = ("12".equals(codRegione)) ?
"""
di procedere all'utilizzazione di soprassuoli forestali come di seguito dichiarato.
A tal fine, ai sensi della L.R. n.39/02 e del relativo Regolamento attuativo n. 7/05"""
				: """
di procedere all'utilizzazione di soprassuoli forestali come di seguito dichiarato.
A tal fine, sotto la propria responsabilità e consapevole di quanto disposto dall'art. 76 del D.P.R. 28.12.2000, n. 445 e delle conseguenze di natura penale in caso di dichiarazioni mendaci""";
			p.add(s);
			document.add(p);
		}

		{
			Paragraph p = new Paragraph()
				.setMarginTop(paragraphTopMargin2)
				.setFont(helveticaFont)
				.setTextAlignment(TextAlignment.CENTER);
			String s = "DICHIARA";
			p.add(s);
			document.add(p);
		}

		{
			Paragraph p = new Paragraph()
				.setMarginTop(paragraphTopMargin)
				.setFont(helveticaFont)
				.setTextAlignment(TextAlignment.LEFT);
			String s = """
che la proprietà interessata dall'intervento di gestione è la seguente:
""";
			p.add(s);
			document.add(p);
		}
		{
			List list = new List()
				.setSymbolIndent(12)
				.setListSymbol("\u2022");
			Paragraph p = new Paragraph()
				.setFont(helveticaFont)
				.setTextAlignment(TextAlignment.LEFT);


			if (isSottoSoglia) {
				list.add(
					String.format(
						"Provincia: %s",
						getString(provincia)
					)
				);
				list.add(
					String.format(
						"Comune: %s",
						getString(comune)
					)
				);
			}
		
			BigDecimal supeCata = new BigDecimal(0);
			BigDecimal totSupeInteDich = new BigDecimal(0);

			BigDecimal supeInte = (this.isSottoSoglia) 
				? (
					(this.sezioneA == null) 
						? null
						: this.sezioneA.superficieUtile
						//.multiply(dieciMilaDec)
				)
				//: this.infoPart.stream().map(part -> part.superficieIntervento).reduce(new BigDecimal(0), BigDecimal::add);
				: new BigDecimal(0);
			
			if (infoPart != null && infoPart.stream().anyMatch(x -> true)) {
				p.add("Particelle catastali:\n");
	
				ListItem subItem = new ListItem();
				List listP = new List();
				for (ParticellaCatastaleModulo pc : infoPart) {
					supeCata = supeCata.add(pc.superficie);

					if (isSottoSoglia) {
						listP.add(
							String.format(
								"%s (%s)%s foglio %d particella %s%s (superficie catastale di %s ha)\n",
								pc.comune,
								pc.provincia,
								(pc.sezione != null && !"".equals(pc.sezione.replaceAll(" ", "")))
									? String.format(" sezione %s", pc.sezione)
									: "",
								pc.foglio,
								pc.particella,
								(pc.sub != null && !"".equals(pc.sub.replaceAll(" ", "")))
									? String.format(" subalterno %s", pc.sub)
									: "",
								covertDecimalToHa(pc.superficie.divide(dieciMilaDec, 4, RoundingMode.HALF_UP))//pc.superficie
							)
						);
					}
					else {
						totSupeInteDich = totSupeInteDich.add(pc.superficieIntervento);
						//supeInte = supeInte.add(pc.superficieIntervento);
		
						listP.add(
							String.format(
								"%s (%s)%s foglio %d particella %s%s (superficie catastale %s ha) con superficie di intervento %s ha\n",
								pc.comune,
								pc.provincia,
								(pc.sezione != null && !"".equals(pc.sezione.replaceAll(" ", "")))
									? String.format(" sezione %s", pc.sezione)
									: "",
								pc.foglio,
								pc.particella,
								(pc.sub != null && !"".equals(pc.sub.replaceAll(" ", "")))
									? String.format(" subalterno %s", pc.sub)
									: "",
								covertDecimalToHa(pc.superficie.divide(dieciMilaDec, 4, RoundingMode.HALF_UP)),// pc.superficie,
								covertDecimalToHa(pc.superficieIntervento.divide(dieciMilaDec, 4, RoundingMode.HALF_UP))//pc.superficieIntervento
							)
						);
					}
				}
				p.add(listP);
				subItem.add(p);
				list.add(subItem);
			}
			else {
				list.add("particelle catastali: N.D.\n");
			}

			list.add(
				String.format(
					"superficie totale delle particelle catastali: %s ha\n",
					//supeCata.divide(dieciMilaDec, 4, RoundingMode.HALF_UP)
					//supeCata
					covertDecimalToHa(supeCata.divide(dieciMilaDec, 4, RoundingMode.HALF_UP))
				)
			);
			if (isSottoSoglia) {
				// nelle sottosoglia la superficie viene inserita in ha nella scheda soprasuolo
				list.add(
					String.format(
						"superficie oggetto di gestione forestale: %s ha\n",
						covertDecimalToHa(supeInte)
					)
				);
			}
			else {
				list.add(
					String.format(
						"totale superficie geometrica di intervento: %s ha\n",
						covertDecimalToHa(supeficieGeometrica.divide(dieciMilaDec, 4, RoundingMode.HALF_UP))
					)
				);
				list.add(
					String.format(
						"totale superficie di intervento dichiarata: %s ha\n",
						covertDecimalToString(totSupeInteDich.divide(dieciMilaDec, 4, RoundingMode.HALF_UP))
					)
				);
				if (totSupeInteDich.compareTo(zeroDec) == 0) {
					list.add("scostamento con valore superficie di intervento dichiarata: N.D.\n");
				}
				else {
					list.add(
						String.format(
							"scostamento con valore superficie di intervento dichiarata: %s %%\n",
							//supeficieGeometrica.subtract(supeInte).divide(supeInte, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100))
							covertDecimalToString(supeficieGeometrica.subtract(totSupeInteDich).divide(totSupeInteDich, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)))
						)
					);
				}
			}


			// if (supeInte == null) {
			// 	list.add("superficie oggetto di gestione forestale: N.D.\n");
			// }
			// else {
			// 	if (isSottoSoglia) {
			// 		// nelle sottosoglia la superficie viene inserita in ha nella scheda soprasuolo
			// 		list.add(
			// 			String.format(
			// 				"superficie oggetto di gestione forestale: %s ha\n",
			// 				covertDecimalToHa(supeInte)
			// 			)
			// 		);
			// 	}
			// 	else {
			// 		// nelle sottosoglia la superficie viene inserita in mq insieme a quella delle particelle
			// 		list.add(
			// 			String.format(
			// 				"superficie oggetto di gestione forestale: %s mq\n",
			// 				covertDecimalToString(supeInte)
			// 			)
			// 		);
			// 	}
			// }

			// if (isSottoSoglia) {
			// 	// nelle sottosoglia la superficie viene inserita in ha nella scheda soprasuolo
			// 	list.add(
			// 		String.format(
			// 			"superficie oggetto di gestione forestale: %s ha\n",
			// 			covertDecimalToHa(supeInte)
			// 		)
			// 	);
			// }

			list.add(
				String.format(
					"natura della proprietà: %s\n",
					getString(naturaProprieta)
				)
			);
			list.add(
				String.format(
					"tipo di proprietà: %s\n",
					getString(tipoProprieta)
				)
			);
			document.add(list);

		}
		document.add(pageBreak);
		
		if (infoUO != null && infoUO.stream().anyMatch((x) -> true)) {
		
			{
				Paragraph p = new Paragraph()
					.setMarginTop(paragraphTopMargin)
					.setFont(helveticaFont)
					.setTextAlignment(TextAlignment.LEFT);
				String s = """
	che l'intervento è stato articolato nelle seguenti Unità Omogenee (UO):
	""";
				p.add(s);
				document.add(p);
			}
	
			{
	
				List list = new List()
					.setSymbolIndent(12)
					.setListSymbol("\u2022");
				for (UnitaOmogeneaModulo uo : infoUO) {
					list.add(String.format(
							"Unità omogenea %s dalla superificie utile di %s ha\n",
							uo.nomeUo,
							covertDecimalToHa(uo.superficieUtile)
						)
					);
				}
				document.add(list);
			}
			document.add(pageBreak);
	
			
			for (UnitaOmogeneaModulo uo : infoUO) {
				{	
					Paragraph p = new Paragraph()
						.setMarginTop(paragraphTopMargin2)
						.setFont(helveticaFont)
						.setTextAlignment(TextAlignment.LEFT);
					p.add(
						String.format(
							"che il soprassuolo forestale dell'UO %s presenta le seguenti caratteristiche:\n",
							uo.nomeUo
						)
					);
					document.add(p);
				}
				{
					List list = new List()
						.setSymbolIndent(12)
						.setListSymbol("\u2022");
					list.add(
						String.format(
							"forma di governo: %s\n",
							getString(uo.formaDiGoverno)
						)
					);
					list.add(
						String.format(
							"struttura del soprassuolo: %s\n",
							getString(uo.strutturaSoprasuolo)
						)
					);
					if (uo.etaMedia != null && uo.etaMedia > 0) {
						list.add(
							String.format(
								" età media del soprassuolo: %d anni\n",
								uo.etaMedia
							)
						);
					}
	
					if ("Misto".equals(uo.formaDiGoverno)) {
						ListItem subItem = new ListItem();
						Paragraph p = new Paragraph()
							.setFont(helveticaFont)
							.setTextAlignment(TextAlignment.LEFT);
						p.add("forma di trattamento applicata in passato:\n");
						List nestList = new List();
						for (String ft : uo.formaTrattamentoPrecedente) {
							nestList.add(ft);
						}
						p.add(nestList);
						subItem.add(p);
						list.add(subItem);
	
					}
					else {
						list.add(
							String.format(
								"forma di trattamento applicata in passato: %s\n",
								getString(uo.formaTrattamentoPrecedente[0])
							)
						);
					}
					list.add(
						String.format(
							"tipologia di soprassuolo: %s\n",
							getString(uo.tipoSoprasuolo)
						)
					);
					
					list.add(
						String.format(
							"specie forestale prevalente: %s\n",
							getString(uo.speciePrevalente)
						)
					);
					document.add(list);
				}
	
				{
					Paragraph p = new Paragraph()
						.setFont(helveticaFont)
						.setTextAlignment(TextAlignment.LEFT);
					p.add("che l'intervento proposto è articolato come segue.\n");
					document.add(p);
				}
				
				//TODO
				{
					List list = new List()
						.setSymbolIndent(12)
						.setListSymbol("\u2022");
					
					if ("Misto".equals(uo.formaDiGoverno)) {
						ListItem subItem = new ListItem();
						Paragraph p = new Paragraph()
							.setFont(helveticaFont)
							.setTextAlignment(TextAlignment.LEFT);
						p.add("forma di trattamento proposta:\n");
						List nestList = new List();
						for (String ft : uo.formaTrattamentoProposta) {
							nestList.add(ft);
						}
						p.add(nestList);
						subItem.add(p);
						list.add(subItem);
					}
					else {
						list.add(
							String.format(
								"forma di trattamento proposta: %s\n",
								getString(uo.formaTrattamentoProposta[0])
							)
						);
					}
					document.add(list);
				}
	
				{
					Paragraph p = new Paragraph()
						.setFont(helveticaFont)
						.setTextAlignment(TextAlignment.LEFT);
					p.add("che la destinazione d'uso del legname e gli assortimenti ritraibili sono i seguenti:\n");
					document.add(p);
				}
	
				///TODO: tabella assortimenti retraibili
				{
					Table table = new Table(UnitValue.createPercentArray(7)).useAllAvailableWidth().setFontSize(10);
					table.addCell(
						"Tipo di Soprasuolo"
					);
					table.addCell(
						"Specie forestale"
					);
					table.addCell(
						"Copertura specie (%)"
					);
					table.addCell(
						"Destinazione di uso"
					);
					table.addCell(
						"Assortimento"
					);

					{
						Paragraph p = new Paragraph()
							.setFont(helveticaFont)
							.setTextAlignment(TextAlignment.CENTER);
						p.add("Autoconsumo (%)");
						table.addCell(p);
					}
					{
						Paragraph p = new Paragraph()
							.setFont(helveticaFont)
							.setTextAlignment(TextAlignment.CENTER);
						p.add("Vendita sul mercato (%)");
						table.addCell(p);
					}
					
					for (DestinazioneUsoModulo du : uo.destinazioniUso) {
						table.addCell(du.tipoSoprasuolo);
						table.addCell(du.specie);
						table.addCell(du.percCoperturaSpecie.toString());
						table.addCell(du.destinazioneUso);
						table.addCell(du.assortimento);
						
	
						{
							Paragraph p = new Paragraph()
								.setFont(helveticaFont)
								.setTextAlignment(TextAlignment.RIGHT);
							p.add(decToString.get(du.percAutoconsumo));
							table.addCell(p);
						}
						
						{
							Paragraph p = new Paragraph()
								.setFont(helveticaFont)
								.setTextAlignment(TextAlignment.RIGHT);
							p.add(decToString.get(du.percVendita));
							table.addCell(p);
						}
					}
					document.add(table);
				}
	
	
				// {
				// 	Paragraph p = new Paragraph()
				// 		.setFont(helveticaFont)
				// 		.setTextAlignment(TextAlignment.LEFT);
				// 	p.add("che la destinazione d'uso del legname è il seguente:");
				// 	document.add(p);
				// }
	
				{
					Paragraph p = new Paragraph()
						.setFont(helveticaFont)
						.setTextAlignment(TextAlignment.LEFT);
					p.add(
						String.format(
							"che la cubatura è stata effettuata dell'UO è stata effettuata con il metodo %s\n",
							getString(uo.metodoDiCubatura)
						)
					);
					document.add(p);
				}
	
				{
					Paragraph p = new Paragraph()
						.setFont(helveticaFont)
						.setTextAlignment(TextAlignment.LEFT);
					p.add("che l'intervento sarà articolato come indicato dal seguente prospetto:\n");
					document.add(p);
				}
	
				//Tabella cubatura
				{
					Table table = new Table(UnitValue.createPercentArray(8)).useAllAvailableWidth().setFontSize(10);
					{
						Cell c1 = new Cell(2, 1);
						c1.add(new Paragraph("Forma di Governo"));
						table.addCell(c1);
					}
					{
						Cell c1 = new Cell(2, 1);
						c1.add(new Paragraph("Varietà/Specie"));
						table.addCell(c1);
					}
					
					{
						Cell c1 = new Cell(1, 2);
						c1.add(new Paragraph("Presenti").setFont(helveticaFont).setTextAlignment(TextAlignment.CENTER));
						table.addCell(c1);
					}
					
					{
						Cell c1 = new Cell(1, 2);
						c1.add(new Paragraph("Da Rilasciare al Taglio").setFont(helveticaFont).setTextAlignment(TextAlignment.CENTER));
						table.addCell(c1);
					}
					
					{
						Cell c1 = new Cell(1, 2);
						c1.add(new Paragraph("Da Tagliare").setFont(helveticaFont).setTextAlignment(TextAlignment.CENTER));
						table.addCell(c1);
					}
	
					for (int i = 0; i < 3; i++) {
						{
							Paragraph p = new Paragraph()
								.setFont(helveticaFont)
								.setTextAlignment(TextAlignment.CENTER);
							p.add("n/ha");
							table.addCell(p);
						}
						{
							Paragraph p = new Paragraph()
								.setFont(helveticaFont)
								.setTextAlignment(TextAlignment.CENTER);
							p.add("mq/ha");
							table.addCell(p);
						}
					}
	
					for (DescrizioneIterventoModulo di : uo.descrizioneInterventi) {
						table.addCell(di.formaDiGoverno);
						table.addCell(di.varieta);
						
						
						{
							Paragraph p = new Paragraph()
								.setFont(helveticaFont)
								.setTextAlignment(TextAlignment.RIGHT);
							p.add(decToString.get(di.presentiNumHa));
							table.addCell(p);
						}
						
						{
							Paragraph p = new Paragraph()
								.setFont(helveticaFont)
								.setTextAlignment(TextAlignment.RIGHT);
							p.add(decToString.get(di.presentiMcHa));
							table.addCell(p);
						}
						
						{
							Paragraph p = new Paragraph()
								.setFont(helveticaFont)
								.setTextAlignment(TextAlignment.RIGHT);
							p.add(decToString.get(di.daRilasciareNumHa));
							table.addCell(p);
						}
						{
							Paragraph p = new Paragraph()
								.setFont(helveticaFont)
								.setTextAlignment(TextAlignment.RIGHT);
							p.add(decToString.get(di.daRilasciareMcHa));
							table.addCell(p);
						}
						
						{
							Paragraph p = new Paragraph()
								.setFont(helveticaFont)
								.setTextAlignment(TextAlignment.RIGHT);
							p.add(decToString.get(di.daTagliareNumHa));
							table.addCell(p);
						}
						
						{
							Paragraph p = new Paragraph()
								.setFont(helveticaFont)
								.setTextAlignment(TextAlignment.RIGHT);
							p.add(decToString.get(di.daTagliareMcHa));
							table.addCell(p);
						}
					}
					document.add(table);
				}
	
				document.add(pageBreak);
			}
		}
		
		if (isSottoSoglia) {
			if (sezioneA != null)
			{
				SoprasuoloForestaleModulo uo = sezioneA;
				{	
					Paragraph p = new Paragraph()
						.setMarginTop(paragraphTopMargin2)
						.setFont(helveticaFont)
						.setTextAlignment(TextAlignment.LEFT);
					p.add("che il soprassuolo forestale presenta le seguenti caratteristiche:\n");
					document.add(p);
				}
				{
					List list = new List()
						.setSymbolIndent(12)
						.setListSymbol("\u2022");
					list.add(
						String.format(
							"forma di governo: %s\n",
							getString(uo.formaDiGoverno)
						)
					);
					list.add(
						String.format(
							"struttura del soprassuolo: %s\n",
							getString(uo.strutturaSoprasuolo)
						)
					);
					if (uo.etaMedia != null && uo.etaMedia > 0) {
						list.add(
							String.format(
								" età media del soprassuolo: %d anni\n",
								uo.etaMedia
							)
						);
					}
					
					{
							
						if ("Misto".equals(uo.formaDiGoverno)) {
							ListItem subItem = new ListItem();
							Paragraph p = new Paragraph()
								.setFont(helveticaFont)
								.setTextAlignment(TextAlignment.LEFT);
							p.add("forma di trattamento applicata in passato:\n");
							List nestList = new List();
							for (String ft : uo.formaTrattamentoPrecedente) {
								nestList.add(ft);
							}
							p.add(nestList);
							subItem.add(p);
							list.add(subItem);
		
						}
						else {
							list.add(
								String.format(
									"forma di trattamento applicata in passato: %s\n",
									getString(uo.formaTrattamentoPrecedente[0])
								)
							);
						}
					}

					list.add(
						String.format(
							"tipologia di soprassuolo: %s\n",
							getString(uo.tipoSoprasuolo)
						)
					);
					
					list.add(
						String.format(
							"specie forestale prevalente: %s\n",
							getString(uo.speciePrevalente)
						)
					);
					document.add(list);
				}
	
				{
					Paragraph p = new Paragraph()
						.setFont(helveticaFont)
						.setTextAlignment(TextAlignment.LEFT);
					p.add("che l'intervento proposto è articolato come segue.\n");
					document.add(p);
				}
				
				
				{
					List list = new List()
						.setSymbolIndent(12)
						.setListSymbol("\u2022");
					
					if ("Misto".equals(uo.formaDiGoverno)) {
						ListItem subItem = new ListItem();
						Paragraph p = new Paragraph()
							.setFont(helveticaFont)
							.setTextAlignment(TextAlignment.LEFT);
						p.add("forma di trattamento proposta:\n");
						List nestList = new List();
						for (String ft : uo.formaTrattamentoProposta) {
							nestList.add(ft);
						}
						p.add(nestList);
						subItem.add(p);
						list.add(subItem);
					}
					else {
						list.add(
							String.format(
								"forma di trattamento proposta: %s\n",
								getString(uo.formaTrattamentoProposta[0])
							)
						);
					}
					document.add(list);
				}
	
				document.add(pageBreak);
				{
					Paragraph p = new Paragraph()
						.setFont(helveticaFont)
						.setTextAlignment(TextAlignment.LEFT);
					p.add("che la destinazione d'uso del legname e gli assortimenti ritraibili sono i seguenti:\n");
					document.add(p);
				}
	
				///tabella assortimenti retraibili
				{
					Table table = new Table(UnitValue.createPercentArray(7)).useAllAvailableWidth().setFontSize(10);
					table.addCell(
						"Tipo di Soprasuolo"
					);
					table.addCell(
						"Specie forestale"
					);
					table.addCell(
						"Copertura specie (%)"
					);
					table.addCell(
						"Destinazione di uso"
					);
					table.addCell(
						"Assortimento"
					);
					
					{
						Paragraph p = new Paragraph()
							.setFont(helveticaFont)
							.setTextAlignment(TextAlignment.CENTER);
						p.add("Autoconsumo (%)");
						table.addCell(p);
					}
					{
						Paragraph p = new Paragraph()
							.setFont(helveticaFont)
							.setTextAlignment(TextAlignment.CENTER);
						p.add("Vendita sul mercato (%)");
						table.addCell(p);
					}
					
					for (DestinazioneUsoModulo du : uo.destinazioniUso) {
						table.addCell(du.tipoSoprasuolo);
						table.addCell(du.specie);
						table.addCell(du.percCoperturaSpecie.toString());
						table.addCell(du.destinazioneUso);
						table.addCell(du.assortimento);
						
						{
							Paragraph p = new Paragraph()
								.setFont(helveticaFont)
								.setTextAlignment(TextAlignment.RIGHT);
							p.add(decToString.get(du.percAutoconsumo));
							table.addCell(p);
						}
						{
							Paragraph p = new Paragraph()
								.setFont(helveticaFont)
								.setTextAlignment(TextAlignment.RIGHT);
							p.add(decToString.get(du.percVendita));
							table.addCell(p);
						}
					}
					document.add(table);
				}
	
				document.add(pageBreak);
			}

			if (sezioneB != null) {
				InterventiNonForestaliModulo  inf = sezioneB;
				
				{	
					Paragraph p = new Paragraph()
						.setMarginTop(paragraphTopMargin2)
						.setFont(helveticaFont)
						.setTextAlignment(TextAlignment.LEFT);
					p.add(String.format(
							"che l'uso del suolo nell'ambito di intervento è il seguente %s presenta le seguenti caratteristiche.\n",
							inf.usoDelSuolo
						));
					document.add(p);
				}
				
				{	
					Paragraph p = new Paragraph()
						.setMarginTop(paragraphTopMargin2)
						.setFont(helveticaFont)
						.setTextAlignment(TextAlignment.LEFT);
					p.add("che l'intervento proposto è articolato come segue.\n");

					{
						List list = new List()
							.setSymbolIndent(12)
							.setListSymbol("\u2022");
						list.add(
							String.format("intervento previsto: %s\n", inf.interventoPrevisto)
						);
						list.add(
							String.format("parametri dell'intervento  : %s %s\n", inf.valoreIntervento, inf.unitaMisutaIntervento)
						);
						list.add(
							String.format("descrizione breve dell'intervento  : %s\n", inf.descrizioneIntervento)
						);
						
						
						p.add(list);
					}
					document.add(p);
				}

			}

			{
				Paragraph p = new Paragraph()
					.setFont(helveticaFont)
					.setMarginTop(paragraphTopMargin2)
					.setTextAlignment(TextAlignment.LEFT);
				p.add("che l'area di intervento non ricade in:\n");
				document.add(p);
				
				{
					List list = new List()
						.setSymbolIndent(12)
						.setListSymbol("\u2022");
					list.add("aree naturali protette\n");
					
					for( String v : vincoliEsenti) {
						String msg = VINCOLI_MAP.get(v);
						if (msg != null) {
							list.add(msg);
						}
					}
					if ("12".equals(codRegione)) {
						list.add("aree dichiarate a rischio molto elevato (R4) oppure elevato (R3) dal P.A.I.\n");
					}
					document.add(list);
				}
			}

			for (String vincolo : VINCOLI) {
				java.util.List<Pair<String, String>> vincoli = vincoliIstanza.get(vincolo);
				if (vincoli != null) {
					String msgPattern = VINCOLI_MSG_MAP.get(vincolo);
					Paragraph p1 = new Paragraph()
						.setFont(helveticaFont)
						.setMarginTop(paragraphTopMargin2)
						.setTextAlignment(TextAlignment.LEFT);
					p1.add(msgPattern);
					document.add(p1);
					List listVinc = new List()
						.setSymbolIndent(12)
						.setListSymbol("\u2022");
					for(Pair<String, String> p : vincoli) {
						listVinc.add(
							String.format(
								"%s %s\n",
								p.getValue0(),
								p.getValue1()
							)
						);
					}
					document.add(listVinc);
				}
			}
			for (String lim : limitazioni) {
				{
					String pattern = LIMITAZIONI_MAP.get(lim);
					if (pattern != null) {
						String msg = String.format(pattern, schedaIntervento);
						Paragraph p = new Paragraph()
							.setFont(helveticaFont)
							.setMarginTop(paragraphTopMargin2)
							.setTextAlignment(TextAlignment.LEFT);
						p.add(msg);
						document.add(p);
					}

				}
			}
		}
		else {
			{
				Paragraph p = new Paragraph()
					.setFont(helveticaFont)
					.setMarginTop(paragraphTopMargin2)
					.setTextAlignment(TextAlignment.LEFT);
				p.add("che i vincoli presenti all'interno della particella forestale sono i seguenti:\n");
				document.add(p);
			}
			
			///Tabella vincolistica
			{
				Table table = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth().setFontSize(10);
				table.addCell(
					"Gruppo"
				);
				table.addCell(
					"Nome Vincolo"
				);
				table.addCell(
					"Codice"
				);
				table.addCell(
					"Descrizione"
				);
				
				{
					Paragraph p = new Paragraph()
						.setFont(helveticaFont)
						.setTextAlignment(TextAlignment.CENTER);
					p.add("Superficie (mq)");
					table.addCell(p);
				}
				
				for (VincoloModulo vm : infoVincoli) {
					table.addCell(vm.gruppo);
					table.addCell(vm.nome);
					table.addCell(vm.codice);
					table.addCell(vm.descrizione);
					{
						Paragraph p = new Paragraph()
							.setFont(helveticaFont)
							.setTextAlignment(TextAlignment.RIGHT);
						p.add(decToString.get(vm.superficie));
						table.addCell(p);
					}
				}
				document.add(table);
			}
		}
		

		// {
		// 	Paragraph p = new Paragraph()
		// 		.setFont(helveticaFont)
		// 		.setMarginTop(paragraphTopMargin)
		// 		.setTextAlignment(TextAlignment.LEFT);
		// 	p.add(
		// 		String.format(
		// 			"Note: %s\n",
		// 			getString(note)
		// 		)
		// 	);
		// 	document.add(p);
		// }
		document.add(pageBreak);

		{
			Paragraph p = new Paragraph()
				.setFont(helveticaFont)
				.setUnderline()
				.setMarginTop(paragraphTopMargin)
				.setTextAlignment(TextAlignment.LEFT);
			p.add("Data:                                           ");
			document.add(p);
		}
		
		{
			Paragraph p = new Paragraph()
				.setFont(helveticaFont)
				.setUnderline()
				.setTextAlignment(TextAlignment.RIGHT);
			p.add("Firma:                                           ");
			document.add(p);
		}
		
		
		{

			if (infoFile == null || infoFile.isEmpty()) {
				Paragraph p = new Paragraph()
					.setFont(helveticaFont)
					.setMarginTop(paragraphTopMargin)
					.setTextAlignment(TextAlignment.LEFT);
				p.add("Nessun Allegato\n");
				document.add(p);
			}
			else {
				Paragraph p = new Paragraph()
					.setFont(helveticaFont)
					.setMarginTop(paragraphTopMargin)
					.setTextAlignment(TextAlignment.LEFT);
				p.add("Allegati:\n");
				document.add(p);
				List list = new List()
					.setSymbolIndent(12)
					.setListSymbol("\u2022");
				for (FileIstanzaWeb fm : infoFile) {
					String descrizione = fm.getDescrizione();
					if (descrizione == null) {
						descrizione = "N.D.";
					}
					list.add(
						String.format(
							"Tipo: %s - Categoria: %s - Descrizione: %s\n\t%s\n",
							fm.getTipo(),
							fm.getCategoria(),
							descrizione,
							Arrays.asList(fm.getFiles()).stream().map(f -> f.getOriginalName()).collect(Collectors.joining(", "))
						)
					);
				}
				document.add(list);
			}

		}

		if (!isSottoSoglia){
			{
				Paragraph p = new Paragraph()
					.setFont(helveticaFont)
					.setMarginTop(paragraphTopMargin)
					.setTextAlignment(TextAlignment.LEFT);
				p.add("Tavole:");
				document.add(p);
			}
	
			
			{
				List list = new List()
					.setSymbolIndent(12)
					.setListSymbol("\u2022");
				list.add("Cartografica tecnica - Carta tecnica\n");
				list.add("Cartografica tecnica - Planimetria catastale\n");
				list.add("Vincoli territoriali - Aree protette e rete natura 2000\n");
				list.add("Vincoli territoriali - Altri vincoli territoriali\n");
				document.add(list);
			}
		}

		//document.close();

		return pdfDoc;
	}

	public PdfDocument getDocument(PdfWriter writer, int numPages) throws Exception {
		return getDocument(writer, numPages, null);
	}
	private int getNumPages() throws Exception {
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		PdfWriter writer = new PdfWriter(pdfOutputStream);
		PdfDocument pdfDoc = getDocument(writer, 0);
		int numPages = pdfDoc.getNumberOfPages();
		pdfDoc.close();
		return numPages;
	}
	
	public void creaPdf(OutputStream outputStream,  Boolean isBozza) throws Exception {
		int numPages = getNumPages();

		PdfWriter writer = new PdfWriter(outputStream);
		PdfDocument pdfDoc = getDocument(writer, numPages, isBozza);
		pdfDoc.close();
	}
	public void creaPdf(OutputStream outputStream) throws Exception {
		creaPdf(outputStream,  null);
	}
}
