package it.almaviva.foliage.function;

import java.time.Duration;
import java.time.Period;

import org.apache.commons.lang3.StringUtils;
import org.threeten.extra.PeriodDuration;

import it.almaviva.foliage.FoliageException;

public class Converters {
	public static String IntervalToString(PeriodDuration pd) {
		if (pd == null) {
			return null;
		}
		else {
			Period p = pd.getPeriod();
			Duration d = pd.getDuration();

			long seconds = d.getSeconds();
			int nanos = d.getNano();

			long pSeconds = seconds % 60;
			long minutes = seconds / 60;
			long pMinutes = minutes % 60;
			long hours = minutes / 60;
			int millis = nanos / 1000000;

			String perString = (p == null) ? "0000-00-00" : String.format("%04d-%02d-%02d", p.getYears(), p.getMonths(), p.getDays());
			String durString = (d == null) ? "00:00:00.000" : String.format("%02d:%02d:%02d.%03d", hours, pMinutes, pSeconds, millis);
			
			String pdString = String.format("%s/%s", perString, durString);
			return pdString;
		}
	}
	public static PeriodDuration StringToInterval(String value) {
		if (value == null) {
			return null;
		}
		else {
			String[] parts = value.split("/");
			if (parts.length == 2) {
				Period p = null;
				String perString = parts[0];
				String[] perParts = perString.split("-");
				if (perParts.length == 3) {
					int years = Integer.parseInt(perParts[0]);
					int months = Integer.parseInt(perParts[1]);
					int days = Integer.parseInt(perParts[2]);
					p = Period.of(years, months, days);
				}
				else {
					throw new FoliageException("Formato dell'intervallo non riconosciuto (1)");
				}
				Duration d = null;
				String durString = parts[1];
				String[] durParts = durString.split(":");
				if (durParts.length == 3) {
					int hours = Integer.parseInt(durParts[0]);
					int minutes = Integer.parseInt(durParts[1]);

					String secString = durParts[2];
					String[] secParts = secString.split(".");
					int secPartsLen = secParts.length;
					if (secPartsLen > 2) {
						throw new FoliageException("Formato dell'intervallo non riconosciuto (3)");
					}

					int seconds = Integer.parseInt(secParts[0]);
					int millis = (secPartsLen == 2) ? Integer.parseInt(StringUtils.rightPad(secParts[1], 3, "0") ) : 0;

					d = Duration.ofMillis(
						millis
						+ (
							1000 * (
								seconds
								+ (
									60 * (
										minutes + (
											60 * hours
										)
									)
								)
							)
						)
					);
				}
				else {
					throw new FoliageException("Formato dell'intervallo non riconosciuto (2)");	
				}

				return PeriodDuration.of(p, d);
			}
			else {
				throw new FoliageException("Formato dell'intervallo non riconosciuto (0)");
			}
		}

	}
}
