package it.almaviva.foliage.istanze.db;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import it.almaviva.foliage.function.BiFunction;
import it.almaviva.foliage.services.WebDal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecuperoArray<T> implements IOperazioneDb{
	public RowMapper<T> rowMapper;
	BiFunction<RowMapper<T>, WebDal, HashMap<String, Object>> funRowMapper;
	public String query;
	public String destContextName;
	public HashSet<String> proprietaUtilizzate;
	public IOperazioneDb[] recuperiAssociati;
	public RecuperoArray(
		BiFunction<RowMapper<T>, WebDal, HashMap<String, Object>> funRowMapper,
		String query,
		String destContextName,
		String[] proprietaUtilizzate
	){
		this.funRowMapper = funRowMapper;
		this.query = query;
		this.destContextName = destContextName;
		this.proprietaUtilizzate = new HashSet<>(Arrays.asList(proprietaUtilizzate)) ;
	}

	public RecuperoArray(
		RowMapper<T> rowMapper,
		String query,
		String destContextName,
		String[] proprietaUtilizzate
	){
		this.rowMapper = rowMapper;
		this.query = query;
		this.destContextName = destContextName;
		this.proprietaUtilizzate = new HashSet<>(Arrays.asList(proprietaUtilizzate)) ;
	}

	@Override
	public void applica(WebDal dal, HashMap<String, Object> contesto) throws Exception {
		HashMap<String, Object> parSource = DbUtils.getParSource(contesto, proprietaUtilizzate);
		log.debug(query);

		RowMapper<T> rm = (this.funRowMapper == null) ? rowMapper : this.funRowMapper.get(dal, contesto);

		List<T> outVal = dal.query(query, parSource, rm);
		
		contesto.put(destContextName, outVal);
	}

	@Override
	public List<LinkedList<Pair<String, Object>>> applicaArray(WebDal dal, HashMap<String, Object> contesto) throws Exception {
		applica(dal, contesto);
		return null;
	}
	
}
