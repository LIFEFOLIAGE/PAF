/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.almaviva.foliage.services;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import it.almaviva.foliage.function.Converters;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;
import org.threeten.extra.PeriodDuration;

/**
 *
 * @author A.Rossi
 */
@JsonComponent
public class PeriodDurationSerializer extends StdSerializer<PeriodDuration>{

	public static class ResultSetSerializerException extends JsonProcessingException{
		public ResultSetSerializerException(Throwable cause){
			super(cause);
		}
	}

	@Override
	public Class<PeriodDuration> handledType() {
		return PeriodDuration.class;
	}
	
	public PeriodDurationSerializer() {
		this(null);
	}
	
	public PeriodDurationSerializer(Class<PeriodDuration> t) {
		super(t);
	}
	
	@Override
	public void serialize(PeriodDuration pd, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		String strValue = Converters.IntervalToString(pd);
		provider.defaultSerializeValue(strValue, jgen);
	}
}
