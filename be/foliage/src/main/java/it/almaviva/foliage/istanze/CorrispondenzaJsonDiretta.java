package it.almaviva.foliage.istanze;

import org.apache.catalina.valves.rewrite.InternalRewriteMap.Escape;

import com.google.gson.JsonObject;

import it.almaviva.foliage.function.JsonGetFunction;
import it.almaviva.foliage.function.JsonIO;

public class CorrispondenzaJsonDiretta {
	public String nomeProprieta;
	public String nomeContesto;
	public JsonIO jsonIo;

	public CorrispondenzaJsonDiretta(
		String nomeProprieta,
		String nomeContesto,
		JsonIO jsonIo
	) {
		this.nomeProprieta = nomeProprieta;
		this.nomeContesto = nomeContesto;
		this.jsonIo = jsonIo;
	}
	
	public CorrispondenzaJsonDiretta(
		String nomeProprieta,
		JsonIO jsonIo
	) {
		this(nomeProprieta, nomeProprieta, jsonIo);
	}

	public static JsonGetFunction GetString = (JsonObject object, String propName) -> {
		if (object != null) {
			return object.get(propName).getAsString();
		}
		return null;
	};
}