package it.almaviva.foliage.function;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import it.almaviva.foliage.bean.Base64FormioFile;

public class JsonIO {
	public static final Gson gson = new Gson();
	public final JsonPutProcedure saver;
	public final JsonGetFunction loader;
	public JsonIO(JsonPutProcedure saver, JsonGetFunction loader) {
		this.saver = saver;
		this.loader = loader;
	}

	public static final JsonIO StringIo = new JsonIO(
		(JsonObject json, String name, Object value) -> {
			json.addProperty(name, (String)value);
		},
		(JsonObject object, String propName) -> {
			if (object != null) {
				JsonElement elem = object.get(propName);
				if (elem == null) {
					return null;
				}
				else {
					if (elem.isJsonNull())
					{
						return null;
					}
					else {
						return elem.getAsString();
					}
				}
			}
			return null;
		}
	);
	
	public static final JsonIO NumberIo = new JsonIO(
		(JsonObject json, String name, Object value) -> {
			if (value == null) {
				json.add(name, JsonNull.INSTANCE);
			}
			else {
				json.addProperty(name, (Number)value);
				// if (value != null) {
				// 	//json.addProperty(name, value.toString());
				// }
			}
		},
		(JsonObject object, String propName) -> {
			if (object != null) {
				JsonElement elem = object.get(propName);
				if (elem == null) {
					return null;
				}
				else {
					if (elem.isJsonNull())
					{
						return null;
					}
					else {
						return elem.getAsNumber();
					}
				}
			}
			return null;
		}
	);

	public static final JsonIO BooleanIo = new JsonIO(
		(JsonObject json, String name, Object value) -> {
			if (value == null) {
				json.add(name, JsonNull.INSTANCE);
			}
			else {
				json.addProperty(name, (Boolean)value);
				// if (value != null) {
				// 	//json.addProperty(name, value.toString());
				// }
			}
		},
		(JsonObject object, String propName) -> {
			if (object != null) {
				JsonElement elem = object.get(propName);
				if (elem == null) {
					return null;
				}
				else {
					if (elem.isJsonNull())
					{
						return null;
					}
					else {
						return elem.getAsBoolean();
					}
				}
			}
			return null;
		}
	);

	public static final JsonIO DecimalIo = new JsonIO(
		(JsonObject json, String name, Object value) -> {
			if (value == null) {
				json.add(name, JsonNull.INSTANCE);
			}
			else {
				json.addProperty(name, (BigDecimal)value);
				// if (value != null) {
				// 	//json.addProperty(name, value.toString());
				// }
			}
		},
		(JsonObject object, String propName) -> {
			if (object != null) {
				JsonElement elem = object.get(propName);
				if (elem == null) {
					return null;
				}
				else {
					if (elem.isJsonNull())
					{
						return null;
					}
					else {
						Number n = elem.getAsNumber();
						String sn = n.toString();
						BigDecimal big = new BigDecimal(sn);
						return big;
					}
				}
			}
			return null;
		}
	);
	// public static final JsonIO IntegerIo = new JsonIO(
	// 	(JsonObject json, String name, Object value) -> {
	// 		if (value == null) {
	// 			json.add(name, JsonNull.INSTANCE);
	// 		}
	// 		else {
	// 			json.addProperty(name, (Integer)value);
	// 		}
	// 	},
	// 	(JsonObject object, String propName) -> {
	// 		if (object != null) {
	// 			return (Integer)object.get(propName).getAsNumber();
	// 		}
	// 		return null;
	// 	}
	// );
	public static final JsonIO DateIo = new JsonIO(
		(JsonObject json, String name, Object value) -> {
			if (value == null) {
				json.add(name, JsonNull.INSTANCE);
				
			}
			else {
				LocalDate dateVal = (LocalDate)value;
				json.addProperty(name, dateVal.format(DateTimeFormatter.ISO_LOCAL_DATE));
			}
		},
		(JsonObject object, String propName) -> {
			if (object != null) {
				JsonElement elem = object.get(propName);
				if (elem.isJsonNull())
				{
					return null;
				}
				else {
					String strVal = elem.getAsString();
					return LocalDate.parse(strVal, DateTimeFormatter.ISO_LOCAL_DATE);
				}
			}
			return null;
		}
	);
	public static final JsonIO DateTimeIo = new JsonIO(
		(JsonObject json, String name, Object value) -> {
			if (value == null) {
				json.add(name, JsonNull.INSTANCE);
			}
			else {
				if (value != null) {
					LocalDateTime dateVal = (LocalDateTime)value;
					json.addProperty(name, dateVal.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
				}
			}
		},
		(JsonObject object, String propName) -> {
			if (object != null) {
				JsonElement elem = object.get(propName);
				if (elem.isJsonNull())
				{
					return null;
				}
				else {
					String strVal = elem.getAsString();
					return LocalDate.parse(strVal, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				}
			}
			return null;
		}
	);
	public static final JsonIO ObjectIo = new JsonIO(
		(JsonObject json, String name, Object value) -> {
			if (value == null) {
				json.add(name, JsonNull.INSTANCE);
			}
			else {
				JsonElement obj = JsonIO.gson.toJsonTree(value);
				json.add(name, obj);
			}
		},
		(JsonObject object, String propName) -> {
			if (object != null) {
				JsonElement elem = object.get(propName);
				if (elem == null) {
					return null;
				}
				else {
					if (elem.isJsonNull())
					{
						return null;
					}
					else {
						JsonObject jsonObj = elem.getAsJsonObject();
						return JsonIO.gson.fromJson(jsonObj, Object.class);
					}
				}
			}
			return null;
		}
	);
	public static final JsonIO ArrayIo = new JsonIO(
		(JsonObject json, String name, Object value) -> {
			if (value == null) {
				json.add(name, JsonNull.INSTANCE);
			}
			else {
				JsonElement obj = JsonIO.gson.toJsonTree(value);
				json.add(name, obj);
			}
		},
		(JsonObject object, String propName) -> {
			if (object != null) {
				JsonElement elem = object.get(propName);
				if (elem == null) {
					return null;
				}
				else {
					if (elem.isJsonNull())
					{
						return null;
					}
					else {
						JsonArray jsonObj = elem.getAsJsonArray();
						LinkedList<JsonElement> outVal = new LinkedList<>();
						jsonObj.forEach(e-> {
							outVal.addLast(e);
						});
						return outVal;
					}
				}
			}
			return null;
		}
	);


	
	public static final JsonIO Base64FormioFileIo = new JsonIO(
		(JsonObject json, String name, Object value) -> {
			if (value == null) {
				// Quando non ci sono file da inviare, occorre non indicare nulla per evitare 
				// di far apparire messaggi di validazione appena caricati i dati sul front end

				//JsonArray arr = new JsonArray();
				//arr.add(new JsonObject());
				//json.add(name, arr);
				//return JsonIO.gson.fromJson(jsonObj, Base64FormioFile[].class);
			}
			else {
				JsonElement obj = JsonIO.gson.toJsonTree(value);
				json.add(name, obj);
			}
		},
		(JsonObject object, String propName) -> {
			if (object != null) {
				JsonElement jsonElem = object.get(propName);
				if (jsonElem == null) {
					return null;
				}
				else {
					JsonArray jsonObj = jsonElem.getAsJsonArray();
					Object outVal = JsonIO.gson.fromJson(jsonObj, Base64FormioFile[].class);
					return outVal;
				}
			}
			else {
				return null;
			}
		}
	);
}
