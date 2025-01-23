package it.almaviva.foliage.document;

import java.io.OutputStream;
import java.math.BigDecimal;
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
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.RowMapper;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.bean.FileIstanzaWeb;
import it.almaviva.foliage.function.Function;
import it.almaviva.foliage.istanze.db.DbUtils;
import it.almaviva.foliage.services.AbstractDal;
import it.almaviva.foliage.services.WebDal;

public class ModuloIstruttoria {
	private static float paragraphTopMargin = 60;
	private static float paragraphTopMargin2 = 30;
	private static BigDecimal zeroDecimal = BigDecimal.valueOf(0);
	public boolean isSottoSoglia = false;
	public boolean isBozza = true;
	public String tipoIstanza;
	public String codIstanza;
	public String codIstruttoria;
	public LocalDate dataValutazione;

	public String nome;
	public String cognome;
	public String pec;
	public String ulterioriDestinatari;
	public String oggetto;
	public String testo;
	public Collection<FileIstanzaWeb> allegati;

	
	public static org.springframework.jdbc.core.RowMapper<ModuloIstruttoria> RowMapper(AbstractDal dal, String codIstanza) {
		return RowMapper(dal, null, codIstanza);
	}
	public static org.springframework.jdbc.core.RowMapper<ModuloIstruttoria> RowMapper(AbstractDal dal, LocalDateTime oraValutazione, String codIstanza) {
		return (rs, rn) -> {
			Integer idIsta = DbUtils.GetInteger(rs, 0, "ID_ISTA");
			
			HashMap<String, Object> parsMap = new HashMap<>();
			parsMap.put("idIsta", idIsta);

			ModuloIstruttoria m = new ModuloIstruttoria();
			m.codIstanza = codIstanza;
			m.codIstruttoria = String.format("ISTR_%s", codIstanza);
			m.nome = rs.getString("NOME");
			m.cognome = rs.getString("COGNOME");
			m.tipoIstanza = rs.getString("DESC_CIST");

			m.oggetto = rs.getString("OGGETTO");
			m.ulterioriDestinatari = rs.getString("ULTERIORI_DESTINATARI");
			m.testo = rs.getString("TESTO");

			m.isSottoSoglia = rs.getBoolean("IS_SOTTOSOGLIA");
			if (!m.isSottoSoglia) {
				m.pec = rs.getString("PEC");
			}

			Date d = rs.getDate("DATA_VALUTAZIONE");
			if (rs.wasNull()) {
				if (oraValutazione == null) {
					m.dataValutazione = LocalDate.now();
					m.isBozza = true;
				}
				else {
					m.dataValutazione = oraValutazione.toLocalDate();
					m.isBozza = false;
				}
			}
			else {
				if (oraValutazione == null) {
					m.dataValutazione = d.toLocalDate();
					m.isBozza = false;
				}
				else {
					throw new FoliageException("L'istruttoria per cui si sta generando il modulo è già stata valutata");
				}
			}
			m.allegati = getFileIstanzaWeb(idIsta, dal);

			return m;
		};
	}
	private static String getString(String s) {
		return (s == null ? "N.D." : s);
	}


	public static Collection<FileIstanzaWeb> getFileIstanzaWeb(Integer idIsta, AbstractDal dal) {
		String query = """
select 'Allegato Istanza' as tipo, cod_tipo_allegato as categoria, desc_altro_allegato as descrizione, id_file_allegato as id_file
from foliage2.flgallegati_ista_tab
where id_ista = :idIsta
union all
select 'Richiesta Istruttoria' as tipo, categoria, tipo_documento, id_file
from foliage2.FLGRICHIESTE_ISTUTTORIA_ISTANZA_TAB
	join foliage2.flgdocumenti_istuttoria_istanza_tab using (ID_RICHIESTE_ISTUTTORIA_ISTANZA)
where id_ista = :idIsta
union all
select 'Allegati del titolare' as tipo, 'Delega al Professionista' as categoria, null as desc, id_file_delega as id_file
from foliage2.flgista_tab 
	join foliage2.flgtitolare_istanza_tab using (id_titolare)
where id_ista = :idIsta
	and id_file_delega is not null
union all
select 'Allegato Gestione' as tipo, categoria, null as desc, id_file
from foliage2.flgfiletipo_gestione_tab a
	LEFT JOIN LATERAL (
			values ('Autocertificazione Proprietà', a.id_file_autocertificazione_proprieta),
				--('Delega presentazione', a.id_file_delega_presentazione),
				('Delega titolarità dai comproprietari', a.id_file_delega_titolarita),
				('Atto di nomina come rappresentante legale', a.id_file_atto_nomina_rappresentante_legale),
				('Provvedimento finale di sostituzione e conferimento boschi silenti', a.id_file_provvedimento_boschi_silenti),
				('Autocertificazione come ditta forestale', a.id_file_autocertificazione_ditta_forestale),
				('Documenti di identità', a.id_file_documenti_identita)
		) as t(categoria, id_file) on (true)
where id_ista = :idIsta
	and id_file is not null
union all
select 'Elaborato VIncA' as tipo, null, null as desc, id_file_vinca as id_file
from foliage2.flgista_elaborato_vinca_tab
where id_ista = :idIsta
union all
select 'Bollo Invio' as tipo, 'Bolli', null as desc, id_file_ricevute as id_file
from foliage2.flgista_invio_tab
where id_ista = :idIsta
union all
select 'Diritti di Istruttoria' as tipo, 'Bolli', null as desc, id_file_diritti_istruttoria as id_file
from foliage2.flgista_invio_tab
where id_ista = :idIsta
union all
select 'Bollo Proroga' as tipo, 'Bolli', null as desc, id_file_pagamento as id_file
from foliage2.flgista_proroga_tab
where id_ista = :idIsta""";
		HashMap<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("idIsta", idIsta);
		return dal.query(query, mapParam, FileIstanzaWeb.RowMapper(dal));
	}
	
	public static String queryFromCodiIsta = """
select I.ID_ISTA, T.NOME, T.COGNOME, T.PEC, C.DESC_CIST,
	DI.OGGETTO, DI.ULTERIORI_DESTINATARI, DI.TESTO,
	VI.DATA_VALUTAZIONE,
	COD_TIPO_ISTANZA = 'SOTTO_SOGLIA' AS IS_SOTTOSOGLIA
from FOLIAGE2.FLGISTA_TAB I
	join FOLIAGE2.FLGTIPO_ISTANZA_TAB TI using (ID_TIPO_ISTANZA)
	join FOLIAGE2.FLGCIST_TAB C on (C.ID_CIST = TI.ID_CIST)
	join FOLIAGE2.FLGTITOLARE_ISTANZA_TAB T using (ID_TITOLARE)
	left join FOLIAGE2.FLGISTA_DATI_ISTRUTTORIA_TAB DI using (ID_ISTA)
	left join FOLIAGE2.FLGVALUTAZIONE_ISTANZA_TAB VI using (ID_ISTA)
where I.CODI_ISTA = :codIstanza""";

	private ModuloIstruttoria() {
	}
	
	public PdfDocument getDocument(PdfWriter writer, int numPages) throws Exception {
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
				"%s: Parere per %s %s - Valutata il: %s\n",
				codIstruttoria,
				tipoIstanza,
				codIstanza,
				dataValutazione.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
			);
			p.add(s);
			Cell cell = new Cell();
			cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
			cell.add(p);
			headTable.addCell(cell);
			pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new HeaderHandler(document, headTable, numPages, this.isBozza));
		}


		{
			Paragraph p = new Paragraph()
				.setFont(helveticaFont)
				.setTextAlignment(TextAlignment.RIGHT);
			String s = String.format("%s %s", nome, cognome);
			p.add(s);
			document.add(p);
		}

		if (pec != null) {
			Paragraph p = new Paragraph()
				.setFont(helveticaFont)
				.setTextAlignment(TextAlignment.RIGHT);
			String s = String.format("PEC %s", pec);
			p.add(s);
			document.add(p);
		}

		if (ulterioriDestinatari != null && !ulterioriDestinatari.equals("")) {
			Paragraph p = new Paragraph()
				.setFont(helveticaFont)
				.setTextAlignment(TextAlignment.RIGHT);
			p.add(ulterioriDestinatari);
			document.add(p);
		}

		{
			Paragraph p = new Paragraph()
				.setFont(helveticaFont)
				.setBold()
				.setTextAlignment(TextAlignment.LEFT);
			String s = String.format("Oggetto: %s", oggetto);
			p.add(s);
			document.add(p);
		}


		{
			Paragraph p = new Paragraph()
				.setFont(helveticaFont)
				.setTextAlignment(TextAlignment.JUSTIFIED);
			p.add(testo);
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

		if (allegati != null && allegati.size() > 0) {
				// Paragraph p = new Paragraph()
				// 	.setFont(helveticaFont)
				// 	.setMarginTop(paragraphTopMargin)
				// 	.setTextAlignment(TextAlignment.LEFT);
				// p.add("Allegati:\n");
				// document.add(p);
				// List list = new List()
				// 	.setSymbolIndent(12)
				// 	.setListSymbol("\u2022");
				// for (String all : allegati) {
				// 	list.add(all);
				// }
				// document.add(list);

				
				Paragraph p = new Paragraph()
					.setFont(helveticaFont)
					.setMarginTop(paragraphTopMargin)
					.setTextAlignment(TextAlignment.LEFT);
				p.add("Allegati:\n");
				document.add(p);
				List list = new List()
					.setSymbolIndent(12)
					.setListSymbol("\u2022");
				for (FileIstanzaWeb fm : allegati) {
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
		else {
			Paragraph p = new Paragraph()
				.setFont(helveticaFont)
				.setMarginTop(paragraphTopMargin)
				.setTextAlignment(TextAlignment.LEFT);
			p.add("Nessun Allegato\n");
			document.add(p);
		}


		return pdfDoc;
	}
	
	private int getNumPages() throws Exception {
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		PdfWriter writer = new PdfWriter(pdfOutputStream);
		PdfDocument pdfDoc = getDocument(writer, 0);
		int numPages = pdfDoc.getNumberOfPages();
		pdfDoc.close();
		return numPages;
	}
	
	public void creaPdf(OutputStream outputStream) throws Exception {
		int numPages = getNumPages();

		PdfWriter writer = new PdfWriter(outputStream);
		PdfDocument pdfDoc = getDocument(writer, numPages);
		pdfDoc.close();
	}
}
