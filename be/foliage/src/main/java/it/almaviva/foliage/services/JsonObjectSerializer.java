package it.almaviva.foliage.services;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import lombok.val;

public class JsonObjectSerializer extends StdSerializer<JsonElement> {
    public static class JsonObjectSerializerException extends JsonProcessingException{
        public JsonObjectSerializerException(Throwable cause){
            super(cause);
        }
    }

	
    @Override
    public Class<JsonElement> handledType() {
        return JsonElement.class;
    }
    
    public JsonObjectSerializer() {
        this(null);
    }
    
    public JsonObjectSerializer(Class<JsonElement> t) {
        super(t);
    }


	@Override
	public void serialize(JsonElement value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		// TODO Auto-generated method stub

		if (value.isJsonNull()) {
			gen.writeNull();
		}
		else {
			if (value.isJsonPrimitive()) {
				JsonPrimitive jp = value.getAsJsonPrimitive();
				if (jp.isNumber()) {

					Number n = jp.getAsNumber();
					//serialize(n, gen, provider);
				}
				
			}
			else {
				if (value.isJsonArray()) {

				}
				else {
					if (value.isJsonObject()) {

					}
					else {
						throw new JsonObjectSerializerException(new Exception("Invalid Json State"));
					}
				}
			}
		}



		throw new UnsupportedOperationException("Unimplemented method 'serialize'");
	}
	
}
