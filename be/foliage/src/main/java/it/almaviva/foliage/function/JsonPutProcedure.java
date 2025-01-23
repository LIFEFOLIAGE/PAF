package it.almaviva.foliage.function;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface JsonPutProcedure {
	void exec(JsonObject json, String name, Object value);	
}
