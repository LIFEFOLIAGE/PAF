package it.almaviva.foliage.istanze.db;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import it.almaviva.foliage.services.WebDal;

public interface IOperazioneDb {
	void applica(WebDal dal, HashMap<String, Object> contesto) throws Exception;
	List<LinkedList<Pair<String, Object>>> applicaArray(WebDal dal, HashMap<String, Object> contesto) throws Exception;
}