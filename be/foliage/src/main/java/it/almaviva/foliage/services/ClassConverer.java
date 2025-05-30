package it.almaviva.foliage.services;

import java.time.Duration;
import java.time.Period;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.postgresql.util.PGInterval;
import org.threeten.extra.PeriodDuration;

import it.almaviva.foliage.function.Converters;
import it.almaviva.foliage.istanze.db.DbUtils;

public abstract class ClassConverer {
	Class<?> convertingClass;
	private ClassConverer(Class<?> c) {
		this.convertingClass = c;
	}
	public Class<?> getConvertingClass() {
		return this.convertingClass;
	}
	public Object getConvertedValue(Object obj) {
		if (obj == null) {
			return null;
		}
		else {
			if (this.convertingClass.isInstance(obj)) {
				return this.convert(obj);
			}
			else {
				return null;
			}
		}
		
		
	}
	protected abstract Object convert(Object obj);
	public static Object tryConvert(List<ClassConverer> convs, Object obj) {
		Optional<ClassConverer> converter = ClassConverer.SqlConversions.stream().filter(
				(ClassConverer cc) -> {
					return cc.getConvertingClass().isInstance(obj);
				}
			).findFirst();
		if (converter.isPresent()) {
			return converter.get().convert(obj);
		}
		else {
			return obj;
		}
	}
	
	public static ClassConverer PGIntervalConverter = new ClassConverer(PGInterval.class) {
		protected Object convert(Object obj) {
			PGInterval pgInterval = (PGInterval) obj;
			PeriodDuration pd = DbUtils.GetInterval(pgInterval);
			
			return Converters.IntervalToString(pd);
		}
	};
	public static LinkedList<ClassConverer> SqlConversions = new LinkedList<ClassConverer>(
		Arrays.asList(
			new ClassConverer[] {
				PGIntervalConverter
			}
		)
	);
}
