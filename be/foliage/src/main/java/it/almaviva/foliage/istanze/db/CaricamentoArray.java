package it.almaviva.foliage.istanze.db;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import it.almaviva.foliage.function.BiFunction;
import it.almaviva.foliage.function.BiProcedure;
import it.almaviva.foliage.services.WebDal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CaricamentoArray<T> implements IOperazioneDb {
	public String insertSql;
	public String proprietaProg;
	public String proprietaElemento;
	public String destContextName;
	public HashSet<String> proprietaUtilizzate;
	public BiProcedure<HashMap<String, Object>, T> rowSetter;
	public BiFunction<BiProcedure<HashMap<String, Object>, T>, WebDal, HashMap<String, Object>> funRowSetter;
	public CaricamentoArray(
		String insertSql,
		String proprietaProg,
		String proprietaElemento,
		String destContextName,
		BiProcedure<HashMap<String, Object>, T> rowSetter,
		String[] proprietaUtilizzate
	) {
		this.insertSql = insertSql;
		this.proprietaProg = proprietaProg;
		this.proprietaElemento = proprietaElemento;
		this.destContextName = destContextName;
		this.proprietaUtilizzate = new HashSet<>(Arrays.asList(proprietaUtilizzate));
		this.proprietaUtilizzate.add(this.proprietaProg);
		this.rowSetter = rowSetter;
	}
	
	public CaricamentoArray(
		String insertSql,
		String proprietaProg,
		String proprietaElemento,
		String destContextName,
		BiFunction<BiProcedure<HashMap<String, Object>, T>, WebDal, HashMap<String, Object>> funRowSetter,
		String[] proprietaUtilizzate
	) {
		this.insertSql = insertSql;
		this.proprietaProg = proprietaProg;
		this.proprietaElemento = proprietaElemento;
		this.destContextName = destContextName;
		this.proprietaUtilizzate = new HashSet<>(Arrays.asList(proprietaUtilizzate));
		this.proprietaUtilizzate.add(this.proprietaProg);
		this.funRowSetter = funRowSetter;
	}

	@Override
	public void applica(WebDal dal, HashMap<String, Object> contesto) throws Exception {

		
		Collection<T> arr = (Collection<T>)contesto.get(destContextName);
		int idx = 0;
		for (T t : arr) {
			HashMap<String, Object> subContesto = new HashMap<>(contesto);
			subContesto.put(proprietaProg, Integer.valueOf(idx));
			subContesto.put(proprietaElemento, t);
			BiProcedure<HashMap<String, Object>, T> rs = (funRowSetter == null) ? rowSetter : funRowSetter.get(dal, subContesto);	

			rs.eval(subContesto, t);

			HashMap<String, Object> psource = DbUtils.getParSource(subContesto, proprietaUtilizzate);
			log.debug(insertSql);
			dal.update(insertSql, psource);
			idx++;
		}
		
	}

	@Override
	public List<LinkedList<Pair<String, Object>>> applicaArray(WebDal dal, HashMap<String, Object> contesto)  throws Exception{
		applica(dal, contesto);
		return null;
	}
	
}
