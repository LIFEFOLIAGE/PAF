package it.almaviva.foliage.istanze;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.almaviva.foliage.function.JsonGetFunction;
import it.almaviva.foliage.function.JsonPutProcedure;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CorrispondenzaJson {

	public CorrispondenzaJsonDiretta[] corrispondenzeDirette;
	public SottoCorrispondenzaJson[] sottoCorrispondenze;

	public CorrispondenzaJson(
		CorrispondenzaJsonDiretta[] corrispondenzeDirette,
		SottoCorrispondenzaJson[] sottoCorrispondenze
	) {
		this.corrispondenzeDirette = corrispondenzeDirette;
		this.sottoCorrispondenze = sottoCorrispondenze;
	}
	
	public CorrispondenzaJson(
		CorrispondenzaJsonDiretta[] corrispondenzeDirette
	)
	{
		this(corrispondenzeDirette, null);
	}
	public void caricaContesto(JsonObject json, HashMap<String, Object> contesto) throws Exception{
		if (corrispondenzeDirette != null) {
			for (CorrispondenzaJsonDiretta corr : corrispondenzeDirette) {
				String propertyName = corr.nomeProprieta;
				String contextName = corr.nomeContesto;
				JsonGetFunction fun = corr.jsonIo.loader;

				Object value = fun.exec(json, propertyName);
				
				contesto.put(contextName, value);
			}
		}
		if (sottoCorrispondenze != null) {
			for (SottoCorrispondenzaJson pair : sottoCorrispondenze) {
				String propertyName = pair.nomeProprieta;
				JsonElement elem = json.get(propertyName);
				if (elem != null) {
					CorrispondenzaJson corr = pair.sottoCorrispondenza;
					JsonObject obj = elem.getAsJsonObject();
					corr.caricaContesto(obj, contesto);
				}
			}
		}
	}

	
	public JsonObject getObjectFromContext(HashMap<String, Object> contesto) throws Exception{
		JsonObject jsonObj = new JsonObject();
		if (corrispondenzeDirette != null) {
			for (CorrispondenzaJsonDiretta corr : corrispondenzeDirette) {
				String propertyName = corr.nomeProprieta;
				String contextName = corr.nomeContesto;

				Object value = contesto.get(contextName);

				JsonPutProcedure proc = corr.jsonIo.saver;
				try {
					proc.exec(jsonObj, propertyName, value);	
				} catch (Exception e) {
					log.error(String.format("Errore durante il salvataggio della proprietà '%s' dal campo '%s' ", propertyName, contextName));
					throw e;
				}
			}
		}
		if (sottoCorrispondenze != null) {
			for (SottoCorrispondenzaJson pair : sottoCorrispondenze) {
				String propertyName = pair.nomeProprieta;
				CorrispondenzaJson corr = pair.sottoCorrispondenza;
				JsonObject subObject = corr.getObjectFromContext(contesto);
				jsonObj.add(
					propertyName, 
					subObject
				);
			}
		}
		return jsonObj;
	}
}
