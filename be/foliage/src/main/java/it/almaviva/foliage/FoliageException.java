package it.almaviva.foliage;

import java.io.PrintWriter;
import java.io.StringWriter;

public class FoliageException extends RuntimeException {
	public FoliageException(String message) {
		super(message);
	}
	public FoliageException(String message, Throwable cause) {
		super(message, cause);
	}
	public static String GetExceptionStackTrace(Throwable trowable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		trowable.printStackTrace(pw);
		String strErrore = sw.toString();
		return strErrore;
	}
}
