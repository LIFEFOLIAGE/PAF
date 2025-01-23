package it.almaviva.foliage.function;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface JsonGetFunction {
	Object exec(JsonObject json, String name);
}