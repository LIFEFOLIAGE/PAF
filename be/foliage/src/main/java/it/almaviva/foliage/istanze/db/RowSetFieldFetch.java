package it.almaviva.foliage.istanze.db;

import java.math.BigDecimal;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import it.almaviva.foliage.function.Function;
import it.almaviva.foliage.function.TriFunction;

public class RowSetFieldFetch {
		public TriFunction<Object, SqlRowSet, Integer, String> fetcher;
		public String columnName;
		public String header;
		public Function<String, Object> stringConverter;
		public XlsxCellSetter cellSetter;
		public Integer idxColumn = null;

		public RowSetFieldFetch(
			String columnName,
			String header,
			TriFunction<Object, SqlRowSet, Integer, String> fetcher,
			Function<String, Object> stringConverter
		) {
			this.fetcher = fetcher;
			this.columnName = columnName;
			this.header = header;
			this.stringConverter = stringConverter;
		}
		
		public RowSetFieldFetch(
			String columnName,
			String header,
			TriFunction<Object, SqlRowSet, Integer, String> fetcher
		) {
			this(columnName, header, fetcher, null);
		}

		public RowSetFieldFetch withIdxColumn(Integer idxColumn) {
			this.idxColumn = idxColumn;
			return this;
		}

		public RowSetFieldFetch withCellSetter(XlsxCellSetter cellSetter) {
			this.cellSetter = cellSetter;
			return this;
		}

		public static final XlsxCellSetter DecimalSetter = (val, cell) -> {
			BigDecimal d = (BigDecimal)val;
			cell.setCellValue(d.doubleValue());
		};
}
