package it.almaviva.foliage.istanze.db;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import it.almaviva.foliage.function.Function;

public class RowSetXlsxIdxRowMapper {
	public int startColumn;
	public int[] mappingFields;
	public Function<Integer, Object[]> idxRowMapper;

	public RowSetXlsxIdxRowMapper(
		int startColumn,
		int[] mappingFields,
		Function<Integer, Object[]> idxRowMapper
	) {
		this.startColumn = startColumn;
		this.mappingFields = mappingFields;
		this.idxRowMapper = idxRowMapper;
	}
	public void fillSheetWithRowMapper(Object[] vals, XlsxCellSetter[] procs, Integer[] idxColumns, XSSFSheet sheet) throws Exception {
		int n = mappingFields.length;
		Object[] arrKeys = new Object[n];
		for (int i = 0; i < mappingFields.length; i++) {
			int idx = mappingFields[i];
			arrKeys[i] = vals[idx];
		}
		//Arrays.asList(mappingFields).stream().map((int idx) -> vals[idx]).toArray();
		int idxRow = idxRowMapper.get(arrKeys);
		Row r = sheet.getRow(idxRow);
		n = vals.length;
		int idxColumn = 0;
		for (int i = 0; i < n; i++) {
			if (vals[i] != null) {
				Integer idxCol = (idxColumns[i] == null) ? startColumn + idxColumn++ : idxColumns[i];
				if (idxCol != -1) {
					Cell cell = r.getCell(idxCol);
					if (procs[i] == null) {
						cell.setCellValue(vals[i].toString());
					}
					else {
						procs[i].eval(vals[i], cell);
					}
				}
			}
		}
	}
}
