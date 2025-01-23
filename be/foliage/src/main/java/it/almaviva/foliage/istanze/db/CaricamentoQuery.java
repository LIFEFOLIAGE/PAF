package it.almaviva.foliage.istanze.db;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.jdbc.core.RowMapper;

import it.almaviva.foliage.services.WebDal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CaricamentoQuery implements IOperazioneDb {
	public String tabella;
	public String query;
	public LinkedList<CampoSet> campiInseriti;
	public LinkedList<CampoSelect> campiReturning;
	public String[] propritaUtilizzate;


	public CaricamentoQuery(
		String tabella,
		String query,
		CampoSet[] campiInseriti,
		CampoSelect[] campiReturning,
		String[] propritaUtilizzate
	) {
		this.tabella = tabella;
		this.query = query;
		if (campiInseriti != null) {
			this.campiInseriti = new LinkedList<>(Arrays.asList(campiInseriti));
		}
		if (campiReturning != null) {
			this.campiReturning = new LinkedList<>(Arrays.asList(campiReturning));
		}
		this.propritaUtilizzate = propritaUtilizzate;
	}

	
	private Triplet<String, HashMap<String, Object>, RowMapper<LinkedList<Pair<String, Object>>>> preApplica(HashMap<String, Object> contesto) {
		String insCampi = CampoSet.joinFields(campiInseriti);
		String selExpressions = CampoSet.joinExpressions(campiInseriti);
			
		String returningClause = "";
		HashSet<String> proprieta = new HashSet<>();
		if (propritaUtilizzate != null)  {
			proprieta.addAll(Arrays.asList(propritaUtilizzate));
		}
		//String condizioneWhere =  CondizioneEq.evalCondizioneWhere(condizioniWhere, proprietaUtilizzate);
		RowMapper<LinkedList<Pair<String, Object>>> returningRowMapper = null;
		proprieta.addAll(
			CampoSet.getAllProprietaUtilizzate(campiInseriti)
		);
		
		Pair<String, RowMapper<LinkedList<Pair<String, Object>>>> returningPair = CampoSelect.GetReturningPair(campiReturning);
		if (returningPair != null) {
			returningClause = returningPair.getValue0();
			returningRowMapper = returningPair.getValue1();
		}

		String sqlCmd = String.format("""
insert into %s(%s)
select %s
from (
		%s
	) as TXXXX%s""",
				tabella,
				insCampi,
				selExpressions,
				query.replaceAll("\n", "\n\t\t"),
				returningClause
		);
		HashMap<String, Object> parSource = DbUtils.getParSource(contesto, proprieta);

		return new Triplet<String,HashMap<String, Object>,RowMapper<LinkedList<Pair<String,Object>>>>(sqlCmd, parSource, returningRowMapper);
	}

	@Override
	public void applica(WebDal dal, HashMap<String, Object> contesto) {
		Triplet<String,HashMap<String, Object>,RowMapper<LinkedList<Pair<String,Object>>>> pars = preApplica(contesto);
		String sqlCmd = pars.getValue0();
		HashMap<String, Object> parSource = pars.getValue1();
		RowMapper<LinkedList<Pair<String, Object>>> returningRowMapper = pars.getValue2();
		//log.debug(sqlCmd);
		if (returningRowMapper == null) {
			int numRows = dal.update(sqlCmd, parSource);
			log.debug(String.format("Caricati %d record", numRows));
		}
		else {
			LinkedList<Pair<String, Object>> vals = dal.queryForObject(sqlCmd, parSource, returningRowMapper);
			CampoSelect.updateContestoFromPairList(contesto, vals);
		}
	}
	@Override
	public List<LinkedList<Pair<String, Object>>> applicaArray(WebDal dal, HashMap<String, Object> contesto) {
		Triplet<String,HashMap<String, Object>,RowMapper<LinkedList<Pair<String,Object>>>> pars = preApplica(contesto);
		String sqlCmd = pars.getValue0();
		HashMap<String, Object> parSource = pars.getValue1();
		RowMapper<LinkedList<Pair<String, Object>>> returningRowMapper = pars.getValue2();

		//log.debug(sqlCmd);
		if (returningRowMapper == null) {
			dal.update(sqlCmd, parSource);
			return null;
		}
		else {
			List<LinkedList<Pair<String, Object>>> vals = dal.query(sqlCmd, parSource, returningRowMapper);
			return vals;
		}
	}
	
}
