package it.almaviva.foliage.function;

import java.util.HashMap;

@FunctionalInterface
public interface OperazioneContesto {
	void exec(HashMap<String, Object> contesto) throws Exception;
}