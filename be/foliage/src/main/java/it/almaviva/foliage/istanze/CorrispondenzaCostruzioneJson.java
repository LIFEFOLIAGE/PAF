// package it.almaviva.foliage.istanze;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.HashMap;

// import org.javatuples.Pair;
// import org.javatuples.Triplet;

// import com.google.gson.JsonObject;

// import it.almaviva.foliage.function.TriProcedure;

// public class CorrispondenzaCostruzioneJson {
// 	public static TriProcedure<Object, JsonObject, String> setString = (Object value, JsonObject destJson, String propName) -> {
// 		destJson.addProperty(propName, (String)value);
// 	};
// 	public static TriProcedure<Object, JsonObject, String> setNumber = (Object value, JsonObject destJson, String propName) -> {
// 		destJson.addProperty(propName, (Number)value);
// 	};
// 	public static TriProcedure<Object, JsonObject, String> setDate = (Object value, JsonObject destJson, String propName) -> {
// 		LocalDate dateVal = (LocalDate)value;
// 		destJson.addProperty(propName, dateVal.format(DateTimeFormatter.ISO_LOCAL_DATE));
// 	};
// 	public static TriProcedure<Object, JsonObject, String> setDateTime = (Object value, JsonObject destJson, String propName) -> {
// 		LocalDateTime dateVal = (LocalDateTime)value;
// 		destJson.addProperty(propName, dateVal.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
// 	};

// 	public class SottoCorrispondenzaCostruzioneJson {
// 		String nomeProprieta; 
// 		CorrispondenzaCostruzioneJson corrispondenza;

// 	}

// 	public Triplet<String, String, TriProcedure<Object, JsonObject, String>>[] corrispondenzeDirette;
// 	public Pair<String, CorrispondenzaCostruzioneJson>[] sottoCorrispondenze;
	
// 	public CorrispondenzaCostruzioneJson(
// 		Triplet<String, String, TriProcedure<Object, JsonObject, String>>[] corrispondenzeDirette,
// 		Pair<String, CorrispondenzaCostruzioneJson>[] sottoCorrispondenze
// 	){
// 		this.corrispondenzeDirette = corrispondenzeDirette;
// 		this.sottoCorrispondenze = sottoCorrispondenze;
// 	}
// 	public JsonObject caricaDaContesto(HashMap<String, Object> contesto) throws Exception{
// 		JsonObject json = new JsonObject();
// 		if (corrispondenzeDirette != null) {
// 			for (Triplet<String, String, TriProcedure<Object, JsonObject, String>> corr : corrispondenzeDirette) {
// 				String propertyName = corr.getValue0();
// 				String contextName = corr.getValue1();

// 				Object value = contesto.get(contextName);

// 				TriProcedure<Object, JsonObject, String> fun = corr.getValue2();

// 				fun.eval(value, json, propertyName);
				
// 				contesto.put(contextName, value);
// 			}
// 		}
// 		if (sottoCorrispondenze != null) {
// 			for (Pair<String, CorrispondenzaCostruzioneJson> pair : sottoCorrispondenze) {
// 				String propertyName = pair.getValue0();
// 				CorrispondenzaCostruzioneJson corr = pair.getValue1();
// 				JsonObject subObject = corr.caricaDaContesto(contesto);
// 				json.add(
// 					propertyName, 
// 					subObject
// 				);
				
// 			}
// 		}
// 		return json;
// 	}
// }
