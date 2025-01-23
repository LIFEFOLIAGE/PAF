package it.almaviva.foliage.istanze;

import java.util.List;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.google.gson.JsonElement;

import it.almaviva.foliage.services.WebDal;

public interface ISchedaIstanza<T, V> {
	JsonElement caricaScheda(T riferimenti) throws Exception;
	JsonElement analizzaCaricaScheda(WebDal dal, T riferimenti) throws Exception;
	void analizzaScheda(WebDal dal, T riferimenti) throws Exception;
	void cancellaScheda(WebDal dal, T riferimenti) throws Exception;
	void salvaScheda(WebDal dal, V riferimenti, JsonElement datiScheda) throws Exception;
	boolean isValid(V riferimenti) throws Exception;
	List<Integer> getDependencies();
}
