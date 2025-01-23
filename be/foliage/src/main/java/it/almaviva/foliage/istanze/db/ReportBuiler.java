package it.almaviva.foliage.istanze.db;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.poi.sl.draw.geom.GuideIf.Op;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetDimension;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.controllers.WebController;
import it.almaviva.foliage.function.Function;
import it.almaviva.foliage.services.AbstractDal;
import it.almaviva.foliage.services.WebDal;

public class ReportBuiler {
	private SqlRowSet rowSet;
	private ReportBuiler(
		String templatePath,
		RowSetFieldFetch[] fields,
		Integer startRow,
		String query
	) {
		this.templatePath = templatePath;
		this.fields = fields;
		this.startRow = startRow;
		this.query = query;
	}
	private ReportBuiler(
		String templatePath,
		RowSetFieldFetch[] fields,
		RowSetXlsxIdxRowMapper idxRowMapper,
		String query
	) {
		this.templatePath = templatePath;
		this.fields = fields;
		this.idxRowMapper = idxRowMapper;
		this.query = query;
	}

	private ReportBuiler(
		String query,
		String geoJsonColumn
	) {
		this.query = query;
		this.geoJsonColumn = geoJsonColumn;
	}

	private ReportBuiler(
		RowSetFieldFetch[] fields,
		String query
	) {
		this.fields = fields;
		this.query = query;
	}

	private String templatePath;
	private RowSetFieldFetch[] fields;
	private Integer startRow;
	private RowSetXlsxIdxRowMapper idxRowMapper;
	private String query;
	private String geoJsonColumn;
	private float pdfFontSize = 5;
	private Function<Integer, Object[]> sheetChooser;
	private Integer availableSheets = null;

	public ReportBuiler withPdfFontSize(float pdfFontSize) {
		this.pdfFontSize = pdfFontSize;
		return this;
	}

	public ReportBuiler withAvaliableSheets(Integer availableSheets) {
		this.availableSheets = availableSheets;
		return this;
	}

	public ReportBuiler withSheetChooser(Function<Integer, Object[]> sheetChooser) {
		this.sheetChooser = sheetChooser;
		return this;
	}

	public void prepareRowSet(AbstractDal dal, HashMap<String, Object> pars) {
		rowSet = dal.queryForRowSet(query, pars);
	}
	public void writeToPdf(Document document) throws Exception {
		
		// PdfWriter writer = new PdfWriter(outputStream);
		// PdfDocument pdfDoc = new PdfDocument(writer);
		// Document document = new Document(pdfDoc, PageSize.A4.rotate());
		
		
		{
			int nColumns = this.fields.length;
			Table table = new Table(UnitValue.createPercentArray(nColumns)).useAllAvailableWidth().setFontSize(pdfFontSize);
			for (int i = 0; i < nColumns; i++) {
				table.addCell(fields[i].header);
			}
			int rn = 0;
			while (rowSet.next()) {
				for (int i = 0; i < nColumns; i++) {
					RowSetFieldFetch field = this.fields[i];
					Object value = field.fetcher.get(rowSet, rn, field.columnName);
					if (value != null) {
						String strValue = (field.stringConverter == null) ? value.toString() : field.stringConverter.get(value);
						table.addCell(strValue);
					}
					else {
						table.addCell("");
					}
				}
				rn++;
			}
			document.add(table);
		}
		// document.close();
		// pdfDoc.close();
	}

	public XSSFWorkbook getXlsxReport(XSSFWorkbook startWorkbook) throws Exception {
		if (rowSet == null) {
			throw new FoliageException("Report non pronto");
		}
		else {
			XSSFWorkbook wb = null;
			XSSFSheet sheet = null;
			int nColumns = this.fields.length;
			if (this.templatePath == null) {
				wb = (startWorkbook == null) ? new XSSFWorkbook() : startWorkbook;
				
				if (availableSheets == null) {
					sheet = wb.createSheet();
					Row r = sheet.createRow(0);
					for (int i = 0; i < nColumns; i++) {
						Cell cell = r.createCell(i);
						cell.setCellValue(fields[i].header);
					}
				}
				else {
					for (int i = 0; i < availableSheets; i++) {
						sheet = wb.createSheet();
						Row r = sheet.createRow(0);
						for (int j = 0; j < nColumns; j++) {
							Cell cell = r.createCell(j);
							cell.setCellValue(fields[j].header);
						}
					}
				}
				startRow = 1;
			}
			else {
				String modelPath = this.templatePath;
				ClassLoader classLoader = WebController.class.getClassLoader();
				InputStream inputStream = classLoader.getResourceAsStream(modelPath);
				
				wb = new XSSFWorkbook(inputStream);
				sheet = wb.getSheetAt(0);
			}

			int rn = 0;

			XlsxCellSetter[] xlsxSetters = new XlsxCellSetter[nColumns];
			Integer[] idxColumns = new Integer[nColumns];
			for (int i = 0; i < nColumns; i++) {
				xlsxSetters[i] = fields[i].cellSetter;
				idxColumns[i] = fields[i].idxColumn;
			}

			Object[] rowVals = new Object[nColumns];
			
			Integer[] currRows = (availableSheets == null) ? new Integer[] { 0 } : new Integer[availableSheets];
			while (rowSet.next()) {
				for (int i = 0; i < nColumns; i++) {
					rowVals[i] = fields[i].fetcher.get(rowSet, rn, fields[i].columnName);
				}
				Integer currSheetIdx = (sheetChooser == null) ? null : sheetChooser.get(rowVals);
				XSSFSheet currSheet = (currSheetIdx == null) ? sheet : wb.getSheetAt(currSheetIdx);
				if (this.idxRowMapper == null) {
					Integer currRowIdx = null;
					if (currSheetIdx == null) {
						if (currRows[0] == null) {
							currRows[0] = startRow;
						}
						else {
							currRows[0]++;
						}
						currRowIdx = currRows[0];
					}
					else {
						if (currRows[currSheetIdx] == null) {
							currRows[currSheetIdx] = startRow;
						}
						else {
							currRows[currSheetIdx]++;
						}
						currRowIdx = currRows[currSheetIdx];
					}



					//int idxRow = (startRow == null) ? rn : startRow + rn;
					int idxRow = currRowIdx;
					Row r = currSheet.createRow(idxRow);
					for (int i = 0; i < nColumns; i++) {
						if (rowVals[i] != null) {
							Cell cell = r.createCell(i);
							if (xlsxSetters[i] == null) {
								cell.setCellValue(rowVals[i].toString());
							}
							else {
								xlsxSetters[i].eval(rowVals[i], cell);
							}
						}
					}
				}
				else {
					idxRowMapper.fillSheetWithRowMapper(rowVals, xlsxSetters, idxColumns, currSheet);
				}
				rn++;
			}
			return wb;
		}
	}

	public static void concatWorkbooks(XSSFWorkbook[] workbooks) {
		XSSFWorkbook finalWorkbook = new XSSFWorkbook();
		for (XSSFWorkbook workbook : workbooks) {
			int n = workbook.getNumberOfSheets();
			for (int i = 0; i < n; i++) {
				XSSFSheet sheetFrom = workbook.getSheetAt(i);
				XSSFSheet sheetTo = finalWorkbook.createSheet();

			}
		}
		
	}

	// public static void addXlsxWorkbookToPdf(XSSFWorkbook workbook, OutputStream outputStream) throws Exception {
		
	// 	PdfWriter writer = new PdfWriter(outputStream);
	// 	PdfDocument pdfDoc = new PdfDocument(writer);
	// 	Document document = new Document(pdfDoc, PageSize.A4.rotate());
		
		
	// 	PdfFont helveticaFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	// 	PdfFont helveticaBoldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
	// 	document.setMargins(72, 36, 72, 36);
	// 	document.setFont(helveticaFont);

	// 	int nScheets = workbook.getNumberOfSheets();
	// 	for (int i = 0; i < nScheets; i++) {
	// 		XSSFSheet sheet = workbook.getSheetAt(i);
	// 		Iterator<Row> rowIterator = sheet.iterator();
	// 		int nCols = 0;
	// 		while (rowIterator.hasNext()) {
	// 			Row row = rowIterator.next();
	// 			int nColsRow = row.getLastCellNum() + 1;
	// 			if (nCols < nColsRow) {
	// 				nCols = nColsRow;
	// 			} 
	// 		}

	// 		List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
	// 		CellRangeAddress ra = mergedRegions.get(0);
	// 		ra.getFirstColumn();
	// 		ra.getFirstRow();
	// 		ra.getLastRow();
	// 		ra.getLastColumn();


	// 		Table table = new Table(UnitValue.createPercentArray(nCols)).useAllAvailableWidth().setFontSize(2);
			
	// 		rowIterator = sheet.iterator();
	// 		while (rowIterator.hasNext()) {
	// 			Row row = rowIterator.next();
	// 			Iterator<Cell> cellIterator = row.iterator();
	// 			while (cellIterator.hasNext()) {
	// 				Cell c = cellIterator.next();
	// 				int idxRow = c.getRowIndex();
	// 				int idxCol = c.getColumnIndex();

	// 				CellStyle cellStyle = c.getCellStyle();
	// 				short bgColorIndex = cellStyle.getFillForegroundColor();
	// 				XSSFColor bgColor = null;
	// 				BaseColor backgroundColor = null;

	// 				if (bgColorIndex != IndexedColors.AUTOMATIC.getIndex()) {
	// 					bgColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
	// 					if (bgColor != null) {
	// 						byte[] rgb = bgColor.getRGB();
	// 						if (rgb != null && rgb.length == 3) {
	// 							cellPdf.setBackgroundColor(new BaseColor(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF));
	// 						}
	// 					}
	// 				}
	// 				org.apache.poi.ss.usermodel.Font cellFont = workbook.getFontAt(cellStyle.getFontIndex());
	// 				short fontSize = cellFont.getFontHeightInPoints();
					
			  
					

	// 				String value = null;
	// 				CellType ct = c.getCellType();
	// 				if (ct == CellType.FORMULA) {
	// 					ct = c.getCachedFormulaResultType();
	// 				}

	// 				switch (ct) {
	// 					case BLANK: {
	// 					}; break;
	// 					case BOOLEAN: {
	// 						value = c.getBooleanCellValue() ? "X" : "O";
	// 					}; break;
	// 					case ERROR: {
	// 						value = String.format("Errore %s", Byte.valueOf(c.getErrorCellValue()).toString());
	// 					}; break;
	// 					// case FORMULA: {
	// 					// 	;
	// 					// }; break;
	// 					case NUMERIC: {
	// 						value = String.valueOf(BigDecimal.valueOf(c.getNumericCellValue()));
	// 					}; break;
	// 					case STRING: {
	// 						value = c.getStringCellValue();
	// 					}; break;
	// 					default: {
	// 						value = null;
	// 					}; break;
	// 				}
					
					

	// 				Optional<CellRangeAddress> omr = mergedRegions.stream().filter((r) -> r.isInRange(idxRow, idxCol)).findAny();
	// 				if (omr.isPresent()) {
	// 					CellRangeAddress mr = omr.get();
	// 					int firstMergedRow = mr.getFirstRow();
	// 					int firstMergedCol = mr.getFirstColumn();
	// 					if (idxRow == firstMergedRow && idxCol == firstMergedCol ) {
							
	// 					}
	// 				}
	// 				else {

	// 				}

	// 				com.itextpdf.layout.element.Cell pdfCell = new com.itextpdf.layout.element.Cell();
	// 				pdfCell.set
	// 				//pdfCell.setFont(helveticaBoldFont)
	// 			}
	// 		}

			
	// 	}


	// 	outputStream.flush();
	// }
	public void writeToCsv(OutputStream outputStream) throws Exception {
		if (rowSet == null) {
			throw new FoliageException("Report non pronto");
		}
		else {
			PrintStream ps = new PrintStream(outputStream);
			for(int i = 0; i < this.fields.length; i++) {
				if (i > 0) {
					ps.print(",");
				}
				RowSetFieldFetch field = this.fields[i];
				ps.print(field.header);
			}
			ps.println();
			int rn = 0;
			while (rowSet.next()) {
				rn++;
				for(int i = 0; i < this.fields.length; i++) {
					if (i > 0) {
						ps.print(",");
					}
					RowSetFieldFetch field = this.fields[i];
					Object value = field.fetcher.get(rowSet, rn, field.columnName);
					String strValue = (field.stringConverter == null) ? value.toString() : field.stringConverter.get(value);
					ps.print(strValue);
				}
				ps.println();
			}
			ps.close();
		}
	}
	 
	public void writeToGeoJSON(OutputStream outputStream) {
		if (rowSet == null) {
			throw new FoliageException("Report non pronto");
		}
		else {
			PrintStream ps = new PrintStream(outputStream);
			while (rowSet.next()) {
				String val = rowSet.getString(geoJsonColumn);
				ps.println(val);	
			}
			ps.close();
		}
	}


	public static final String queryReportAutoaccettazioneResp = """
select i.codi_ista, ii.data_invio,
	e.codi_istat_regione as regione, e.codi_istat_provincia as provincia, e.codi_istat_comune as comune,
	t.codice_fiscale as codi_fisc_tito,
	a.data_assegnazione, ua.codi_fisc as codi_fisc_ass, ui.codi_fisc as codi_fisc_istr
from foliage2.flgreport_autoaccettazione_istanze_tab r
	join foliage2.flgista_tab i using (id_ista, id_ente_terr)
	join foliage2.flgtitolare_istanza_tab t using (id_titolare)
	left join foliage2.flgista_invio_tab ii using (id_ista)
	left join foliage2.flgassegnazione_istanza_tab a using (id_ista)
	left join foliage2.flguten_tab ua on (ua.id_uten = a.id_utente_assegnazione)
	left join foliage2.flguten_tab ui on (ui.id_uten = a.id_utente_istruttore)
	join foliage2.flgcodi_istat_enti_mvw e using (id_ente_terr)
where r.data_rife = :dataRife
	and id_ente_terr = any (
			select id_ente
			from foliage2.flgprof_tab p
				join foliage2.flgenti_profilo_tab ep using (id_profilo)
			where p.tipo_auth = :authority
				and p.tipo_ambito = :authScope
				and ep.id_utente = :idUtente
		)""";


		public static final String queryReportAutoaccettazioneAmmi = """
select i.codi_ista, ii.data_invio,
	e.codi_istat_regione as regione, e.codi_istat_provincia as provincia, e.codi_istat_comune as comune,
	t.codice_fiscale as codi_fisc_tito,
	a.data_assegnazione, ua.codi_fisc as codi_fisc_ass, ui.codi_fisc as codi_fisc_istr
from foliage2.flgreport_autoaccettazione_istanze_tab r
	join foliage2.flgista_tab i using (id_ista, id_ente_terr)
	join foliage2.flgtitolare_istanza_tab t using (id_titolare)
	left join foliage2.flgista_invio_tab ii using (id_ista)
	left join foliage2.flgassegnazione_istanza_tab a using (id_ista)
	left join foliage2.flguten_tab ua on (ua.id_uten = a.id_utente_assegnazione)
	left join foliage2.flguten_tab ui on (ui.id_uten = a.id_utente_istruttore)
	join foliage2.flgcodi_istat_enti_mvw e using (id_ente_terr)
where r.data_rife = :dataRife""";
	public static final RowSetFieldFetch[] campiReportAutoaccettazione = new RowSetFieldFetch[] {
		new RowSetFieldFetch(
			"codi_ista", "Id",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"data_invio", "Data Presentazione",
			DbUtils::GetLocalDate
		),
		new RowSetFieldFetch(
			"regione", "Regione",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"provincia", "Provincia",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"comune", "Comune",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"codi_fisc_tito", "Id Prop.",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"data_assegnazione", "Data Assegnazione",
			DbUtils::GetLocalDate
		),
		new RowSetFieldFetch(
			"codi_fisc_ass", "Id Assegnazione",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"codi_fisc_istr", "Id Istruttore",
			DbUtils::GetObject
		)
	};
	public static final ReportBuiler ReportAutoaccettazioneResp = new ReportBuiler(
		campiReportAutoaccettazione,
		queryReportAutoaccettazioneResp
	);
	public static ReportBuiler ReportAutoaccettazioneAmmi = new ReportBuiler(
		campiReportAutoaccettazione,
		queryReportAutoaccettazioneAmmi
	);

	public static final Function<Integer, Object[]> ReportP1SheetChooser = (Object[] vals) -> {
		Integer outVal = null;
		String val = vals[1].toString();
		switch (val) {
			case "Istanza sopra soglia": {
				outVal = 1;
			}; break;
			case "Istanza di progetti in attuazione dei piano di gestione forestali": {
				outVal = 2;
			}; break;
			case "Istanza sotto soglia": {
				outVal = 0;
			}; break;
			case "Istanza di progetti in deroga": {
				outVal = 3;
			}; break;
			default: {
				
			}; break;
		}
		return outVal;
	};

	public static final RowSetFieldFetch[] campiReportP1 = new RowSetFieldFetch[] {
		new RowSetFieldFetch(
			"id", "Id",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"tipologia", "Tipologia",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"data", "Data",
			DbUtils::GetLocalDate
		),
		new RowSetFieldFetch(
			"regione", "Regione",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"provincia", "Provincia",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"comune", "Comune",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"stato", "Stato",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"id_prop", "Id Prop.",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"id_prof", "Id Prof.",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"tratt_uo", "Tratt. uo.",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"supe_uo", "Sup. uo.",
			DbUtils::GetDecimal
		),
		new RowSetFieldFetch(
			"vol_uo", "Vol. uo.",
			DbUtils::GetDecimal
		)
	};

	public static String queryReportP1Ammi = """
select r.id, r.tipologia, data,
	e.codi_istat_regione as regione, e.codi_istat_provincia as provincia, e.codi_istat_comune as comune,
	stato, id_prop, id_prof, tratt_uo, supe_uo, vol_uo
from foliage2.flgreport_p1_2_tab r
	join foliage2.flgcodi_istat_enti_mvw e using (id_ente_terr)
where r.data_rife = :dataRife
	and r.durata = :durata""";

	public static final String queryReportP1Resp = """
select r.id, r.tipologia, data,
	e.codi_istat_regione as regione, e.codi_istat_provincia as provincia, e.codi_istat_comune as comune,
	stato, id_prop, id_prof, tratt_uo, supe_uo, vol_uo
from foliage2.flgreport_p1_2_tab r
	join foliage2.flgcodi_istat_enti_mvw e using (id_ente_terr)
where r.data_rife = :dataRife
	and r.durata = :durata
	and id_ente_terr = any (
			select id_ente
			from foliage2.flgprof_tab p
				join foliage2.flgenti_profilo_tab ep using (id_profilo)
			where p.tipo_auth = :authority
				and p.tipo_ambito = :authScope
				and ep.id_utente = :idUtente
		)""";
	public static final ReportBuiler ReportP1Resp = new ReportBuiler(
		campiReportP1,
		queryReportP1Resp
	).withSheetChooser(ReportP1SheetChooser).withAvaliableSheets(4);
	
	public static ReportBuiler ReportP1Ammi = new ReportBuiler(
		campiReportP1,
		queryReportP1Ammi
	).withSheetChooser(ReportP1SheetChooser).withAvaliableSheets(4);


	public static final RowSetFieldFetch[] campiReportP1Agg = new RowSetFieldFetch[] {
		new RowSetFieldFetch(
			"tipologia", "Tipologia",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"regione", "Regione",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"provincia", "Provincia",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"comune", "Comune",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"tratt_uo", "Tratt. uo.",
			DbUtils::GetObject
		),
		new RowSetFieldFetch(
			"num_ista", "Numero totale istanze",
			DbUtils::GetInteger
		),
		new RowSetFieldFetch(
			"num_uo", "Numero totale UO",
			DbUtils::GetInteger
		),
		new RowSetFieldFetch(
			"num_uo_supe", "Numero totale UO con dato di superficie",
			DbUtils::GetInteger
		),
		new RowSetFieldFetch(
			"num_uo_vol", "Numero totale UO con dato di volume totale ",
			DbUtils::GetInteger
		),
		new RowSetFieldFetch(
			"supe_uo", "Somma della superficie (ha) di intervento",
			DbUtils::GetDecimal
		),
		new RowSetFieldFetch(
			"vol_uo", "Somma del volume  (m3) da tagliare",
			DbUtils::GetDecimal
		)
	};

	public static String queryReportP1RespAgg = """
select tipologia, regione, provincia, comune, tratt_uo,
	count(distinct id) as num_ista,
	sum(case when prog_uog > 0 then 1 else 0 end) as num_uo,
	sum(case when prog_uog > 0 and supe_uo > 0 then 1 else 0 end) as num_uo_supe,
	sum(case when prog_uog > 0 and vol_uo > 0 then 1 else 0 end) as num_uo_vol,
	sum(supe_uo) as supe_uo,
	sum(vol_uo) as vol_uo
from (
		select r.id, r.prog_uog, r.tipologia, data,
			e.codi_istat_regione as regione, e.codi_istat_provincia as provincia, e.codi_istat_comune as comune,
			stato, id_prop, id_prof, tratt_uo, supe_uo, vol_uo
		from foliage2.flgreport_p1_2_tab r
			join foliage2.flgcodi_istat_enti_mvw e using (id_ente_terr)
		where r.data_rife = :dataRife
			and r.durata = :durata
			and id_ente_terr = any (
					select id_ente
					from foliage2.flgprof_tab p
						join foliage2.flgenti_profilo_tab ep using (id_profilo)
					where p.tipo_auth = :authority
						and p.tipo_ambito = :authScope
						and ep.id_utente = :idUtente
				)
	) as t
group by tipologia, regione, provincia, comune, tratt_uo""";

	public static final ReportBuiler ReportP1RespPdf = new ReportBuiler(
		campiReportP1Agg,
		queryReportP1RespAgg
	);

	public static String queryReportP1AmmiAgg = """
select tipologia, regione, provincia, comune, tratt_uo,
	count(distinct id) as num_ista,
	sum(case when prog_uog > 0 then 1 else 0 end) as num_uo,
	sum(case when prog_uog > 0 and supe_uo > 0 then 1 else 0 end) as num_uo_supe,
	sum(case when prog_uog > 0 and vol_uo > 0 then 1 else 0 end) as num_uo_vol,
	sum(supe_uo) as supe_uo,
	sum(vol_uo) as vol_uo
from (
		select r.id, r.prog_uog, r.tipologia, data,
			e.codi_istat_regione as regione, e.codi_istat_provincia as provincia, e.codi_istat_comune as comune,
			stato, id_prop, id_prof, tratt_uo, supe_uo, vol_uo
		from foliage2.flgreport_p1_2_tab r
			join foliage2.flgcodi_istat_enti_mvw e using (id_ente_terr)
		where r.data_rife = :dataRife
			and r.durata = :durata
	) as t
group by tipologia, regione, provincia, comune, tratt_uo""";

	public static ReportBuiler ReportP1AmmiAgg = new ReportBuiler(
		campiReportP1Agg,
		queryReportP1AmmiAgg
	);
	
	public static String queryReportP2Ammi = """
select json_build_object(
		'type', 'FeatureCollection',
		'features', json_agg(ST_AsGeoJSON(t.*)::json)
	) as res
from (
		select r.id, r.tipologia, r.data,
			e.codi_istat_regione as regione, e.codi_istat_provincia as provincia, e.codi_istat_comune as comune,
			r.stato, r.id_prop, r.id_prof, r.tratt_uo, r.supe_uo, r.vol_uo,
			shape
		from foliage2.flgreport_p1_2_tab r
			join foliage2.flgcodi_istat_enti_mvw e using (id_ente_terr)
		where r.data_rife = :dataRife
			and r.durata = :durata
	) as t""";

	public static String queryReportP2Resp = """
select json_build_object(
		'type', 'FeatureCollection',
		'features', json_agg(ST_AsGeoJSON(t.*)::json)
	) as res
from (
		select r.id, r.tipologia, r.data,
			e.codi_istat_regione as regione, e.codi_istat_provincia as provincia, e.codi_istat_comune as comune,
			r.stato, r.id_prop, r.id_prof, r.tratt_uo, r.supe_uo, r.vol_uo,
			shape
		from foliage2.flgreport_p1_2_tab r
			join foliage2.flgcodi_istat_enti_mvw e using (id_ente_terr)
		where r.data_rife = :dataRife
			and r.durata = :durata
			and id_ente_terr = any (
					select id_ente
					from foliage2.flgprof_tab p
						join foliage2.flgenti_profilo_tab ep using (id_profilo)
					where p.tipo_auth = :authority
						and p.tipo_ambito = :authScope
						and ep.id_utente = :idUtente
				)
	) as t""";
	
	public static ReportBuiler ReportP2Ammi = new ReportBuiler(
		queryReportP2Ammi,
		"res"
	);
	public static ReportBuiler ReportP2Resp = new ReportBuiler(
		queryReportP2Resp,
		"res"
	);

	
	public static String queryReportP3Nat1GeoJson = """
select json_build_object(
		'type', 'FeatureCollection',
		'features', json_agg(ST_AsGeoJSON(t.*)::json)
	) as res --srid3035
from (
		select codice as id_sito, sp.denominazi as nome_sito,
			r3.shape_vinc, r3.superficie_vinc as superficie_disturbo, r3.superficie_pf as superficie_intervento, r3.superficie_utile, r3.massa
		from foliage2.flgreport_p3_tab as r3
			join foliage_extra.sitiprotetti_natura_2000 as sp using (codice)
		where r3.data_rife = :dataRife
	) as t""";
	
	public static ReportBuiler ReportP3Nat1GeoJson = new ReportBuiler(
		queryReportP3Nat1GeoJson,
		"res"
	);	

	public static String queryReportP3Nat1 = """
select codice as id_sito, sp.denominazi as nome_sito,
	r3.superficie_vinc, r3.superficie_pf, r3.superficie_utile, r3.massa
from foliage2.flgreport_p3_tab as r3
	join foliage_extra.sitiprotetti_natura_2000 as sp using (codice)
where r3.data_rife = :dataRife""";

	public static ReportBuiler ReportP3Nat1 = new ReportBuiler(
		new RowSetFieldFetch[] {
			new RowSetFieldFetch(
				"id_sito", "Id Sito",
				DbUtils::GetObject
			),
			new RowSetFieldFetch(
				"nome_sito", "Nome Sito",
				DbUtils::GetObject
			),
			new RowSetFieldFetch(
				"superficie_vinc", "Superficie Disturbo (ha)",
				DbUtils::GetDecimal
			),
			new RowSetFieldFetch(
				"superficie_pf", "Superficie Intervento (ha)",
				DbUtils::GetDecimal
			),
			new RowSetFieldFetch(
				"superficie_utile", "Superficie Utile (ha)",
				DbUtils::GetDecimal
			),
			new RowSetFieldFetch(
				"massa", "Massa (m³)",
				DbUtils::GetDecimal
			)
		},
		queryReportP3Nat1
	);

	public static String queryReportP3Nat2 = """
select codice as id_sito, denominazi as nome_sito,
	--shape,
	(area_disturbo/area_tot)*100 as perc_dist,
	(area_disturbo_ok/area_tot)*100 as perc_tagl
from (
		select n2.codice, n2.denominazi,
			--n2.geom as shape,
			st_area(ST_Transform(n2.geom, :sridGeometrie)) as area_tot,
			st_area(ST_UnaryUnion(ST_Collect(r3_ok.shape_vinc, r3_ko.shape_vinc))) as area_disturbo,
			st_area(r3_ok.shape_vinc) as area_disturbo_ok
		from foliage_extra.sitiprotetti_natura_2000 as n2
			left join lateral (
				select st_union(r3.shape_vinc) as shape_vinc
				from foliage2.flgreport_p3_tab as r3
				where r3.codice = n2.codice
					and r3.data_rife = :dataRife
					and r3.esito_valutazione
			) as r3_ok on (true)
			left join lateral (
				select st_union(r3.shape_vinc) as shape_vinc
				from foliage2.flgreport_p3_tab as r3
				where r3.codice = n2.codice
					and r3.data_rife = :dataRife
					and (r3.esito_valutazione = false or r3.esito_valutazione is null)
			) as r3_ko on (true)
	) as t
where area_disturbo > 0""";

	public static ReportBuiler ReportP3Nat2 = new ReportBuiler(
		new RowSetFieldFetch[] {
			new RowSetFieldFetch(
				"id_sito", "Id Sito",
				DbUtils::GetObject
			),
			new RowSetFieldFetch(
				"nome_sito", "Nome Sito",
				DbUtils::GetObject
			),
			new RowSetFieldFetch(
				"perc_dist", "perc_dist",
				DbUtils::GetDecimal
			),
			new RowSetFieldFetch(
				"perc_tagl", "perc_tagli",
				DbUtils::GetDecimal
			)
		},
		queryReportP3Nat2
	);



	public static String queryReportP3Nat2GeoJson = """
select json_build_object(
		'type', 'FeatureCollection',
		'features', json_agg(ST_AsGeoJSON(t.*)::json)
	) as res --srid3035
from (
		select codice as id_sito, denominazi as nome_sito,
			shape,
			(area_disturbo/area_tot)*100 as perc_dist,
			(area_disturbo_ok/area_tot)*100 as perc_tagl
		from (
				select n2.codice, n2.denominazi,
					ST_Transform(n2.geom, :sridGeometrie) as shape,
					st_area(ST_Transform(n2.geom, :sridGeometrie)) as area_tot,
					st_area(ST_UnaryUnion(ST_Collect(r3_ok.shape_vinc, r3_ko.shape_vinc))) as area_disturbo,
					st_area(r3_ok.shape_vinc) as area_disturbo_ok
				from foliage_extra.sitiprotetti_natura_2000 as n2
					left join lateral (
						select st_union(r3.shape_vinc) as shape_vinc
						from foliage2.flgreport_p3_tab as r3
						where r3.codice = n2.codice
							and r3.data_rife = :dataRife
							and r3.esito_valutazione
					) as r3_ok on (true)
					left join lateral (
						select st_union(r3.shape_vinc) as shape_vinc
						from foliage2.flgreport_p3_tab as r3
						where r3.codice = n2.codice
							and r3.data_rife = :dataRife
							and (r3.esito_valutazione = false or r3.esito_valutazione is null)
					) as r3_ko on (true)
			) as t
		where area_disturbo > 0
	) as t""";

	public static ReportBuiler ReportP3Nat2GeoJson = new ReportBuiler(
		queryReportP3Nat2GeoJson,
		"res"
	);

	public static ReportBuiler GetReportP4(WebDal dal, Integer anno) {
		return  new ReportBuiler(
			"reportModels/P4.xlsx",
			new RowSetFieldFetch[] {
				new RowSetFieldFetch(
					"cod_indicatore", "Indicatore SINFOR",
					DbUtils::GetObject
				).withIdxColumn(-1),
				new RowSetFieldFetch(
					null, "Regione",
					(rs, rn, val) -> {
						return dal.getCodRegione();
					}
				).withIdxColumn(1),
				new RowSetFieldFetch(
					null, "Anno",
					(rs, rn, val) -> {
						return anno;
					}
				).withIdxColumn(2),
				new RowSetFieldFetch(
					"numero_istanze", "Numero totale istanze",
					DbUtils::GetInteger
				),
				new RowSetFieldFetch(
					"numero_istanze_autorizzate", "Numero istanze autorizzate",
					DbUtils::GetInteger
				),
				new RowSetFieldFetch(
					"numero_istanze_non_autorizzate", "Numero istanze non autorizzate",
					DbUtils::GetInteger
				),
				new RowSetFieldFetch(
					"supe_istanze_autorizzate", "Superficie delle istanze autorizzate (ha)",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_privata", "di cui privato",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_pubblica", "di cui pubblico",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_uso_civico", "di cui uso civco",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_altro", "di cui altro",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_fustaia", "di cui fustaia",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_ceduo", "di cui ceduo",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_misto", "altro",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"vol_totale", "Totale volume ritraibile",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"vol_ardere_conifere", "1.1.C. di cui Conifere",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"vol_ardere_nonconifere", "1.1.NC. Di cui Non conifere",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"vol_legname_conifere", "1.2.C. di cui  Conifere",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"vol_legname_nonconifere", "1.2.NC. Di cui Non conifere",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"vol_impiallaccitura_conifere", "1.2.1.C. di cui Conifere",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"vol_impiallaccitura_nonconifere", "1.2.1.NC. Di cui Non conifere",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"vol_paste_conifere", "1.2.2.C. di cui Conifere",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"vol_paste_nonconifere", "1.2.2.NC. Di cui Non conifere",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"vol_altro_conifere", "1.2.3.C. di cui Conifere",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"vol_altro_nonconifere", "1.2.3.NC. Di cui Non conifere",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_1", "BOSCHI DI LARICE E PINO CEMBRO",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_2", "BOSCHI DI ABETE ROSSO",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_3", "BOSCHI DI ABETE BIANCO",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_4", "PINETE DI PINO SILVESTRE E PINO MONTANO",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_5", "PINETE DI PINO NERO, PINO LARICIO E PINO LORICATO",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_6", "PINETE DI PINI MEDITERRANEI",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_7", "ALTRI BOSCHI DI CONIFERE PURE O MISTE",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_8", "FAGGETE",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_9", "QUERCETI A ROVERE, ROVERELLA E FARNIA",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_10", "CERRETE, BOSCHI DI FARNETTO, FRAGNO E VALLONEA",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_11", "CASTAGNETI",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_12", "OSTRIETI, CARPINETI",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_13", "BOSCHI IGROFILI",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_14", "ALTRI BOSCHI CADUCIFOGLI",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_15", "LECCETE",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_16", "SUGHERETE",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_17", "ALTRI BOSCHI DI LATIFOGLIE SEMPREVERDI",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_18", "PIOPPETI ARTIFICIALI",
					DbUtils::GetDecimal
				),
				new RowSetFieldFetch(
					"supe_cat_19", "PIANTAGIONI DI ALTRE LATIFOGLIE",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_20", "PIANTAGIONI DI CONIFERE",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_21", "ARBUSTETI SUBALPINI",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_22", "ARBUSTETI A CLIMA TEMPERATO",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter),
				new RowSetFieldFetch(
					"supe_cat_23", "MACCHIA, ARBUSTETI MEDITERRANEI",
					DbUtils::GetDecimal
				).withCellSetter(RowSetFieldFetch.DecimalSetter)
			},
			new RowSetXlsxIdxRowMapper(
				4,
				new int[] {0},
				(Object[] vals) -> {
					String indicatore = (String)vals[0];
					int idxRow = ("A".equals(indicatore)) ? 3 : 4;
					return idxRow;
				}
			),
			"""
SELECT cod_indicatore, numero_istanze, numero_istanze_autorizzate, numero_istanze_non_autorizzate,
	supe_istanze_autorizzate, supe_privata, supe_pubblica, supe_uso_civico, supe_altro, supe_ceduo, supe_fustaia, supe_misto,
	vol_totale, vol_ardere_conifere, vol_ardere_nonconifere, vol_legname_conifere, vol_legname_nonconifere, vol_impiallaccitura_conifere,
	vol_impiallaccitura_nonconifere, vol_paste_conifere, vol_paste_nonconifere, vol_altro_conifere, vol_altro_nonconifere,
	supe_cat_1, supe_cat_2, supe_cat_3, supe_cat_4, supe_cat_5, supe_cat_6, supe_cat_7, supe_cat_8, supe_cat_9, supe_cat_10,
	supe_cat_11, supe_cat_12, supe_cat_13, supe_cat_14, supe_cat_15, supe_cat_16, supe_cat_17, supe_cat_18, supe_cat_19, supe_cat_20,
	supe_cat_21, supe_cat_22, supe_cat_23
FROM foliage2.flgreport_p4_tab
where data_rife = :dataRife"""
		).withPdfFontSize(2);
	}
}
