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
public class CaricamentoRecord implements IOperazioneDb {
	
	public String tabella;
	public LinkedList<CampoSet> campiInseriti;
	public LinkedList<CampoSelect> campiReturning;
	public CaricamentoRecord(
		String tabella,
		CampoSet[] campiInseriti,
		CampoSelect[] campiReturning
	) {
		this.tabella = tabella;
		this.campiInseriti = (campiInseriti == null) ? null : new LinkedList<>(Arrays.asList(campiInseriti));
		this.campiReturning = (campiReturning == null) ? null : new LinkedList<>(Arrays.asList(campiReturning));
	}

	public CaricamentoRecord(
		String tabella,
		CampoSet[] campiInseriti
	) {
		this(tabella, campiInseriti, null);
	}
	private Triplet<String, HashMap<String, Object>, RowMapper<LinkedList<Pair<String, Object>>>> preApplica(HashMap<String, Object> contesto) {
		HashSet<String> proprietaUtilizzate = new HashSet<>();
		String returningClause = "";
		RowMapper<LinkedList<Pair<String, Object>>> returningRowMapper = null;
		Pair<String, RowMapper<LinkedList<Pair<String, Object>>>> returningPair = CampoSelect.GetReturningPair(campiReturning);
		if (returningPair != null) {
			returningClause = returningPair.getValue0();
			returningRowMapper = returningPair.getValue1();
		}

		proprietaUtilizzate.addAll(
			CampoSet.getAllProprietaUtilizzate(campiInseriti)
		);

		String sqlCmd = String.format("""
insert into %s(%s)
values(%s)%s

			""",
			tabella,
			CampoSet.joinFields(campiInseriti),
			CampoSet.joinExpressions(campiInseriti),
			returningClause
		);

		HashMap<String, Object> parSource = DbUtils.getParSource(contesto, proprietaUtilizzate);

		return new Triplet<String,HashMap<String, Object>,RowMapper<LinkedList<Pair<String,Object>>>>(sqlCmd, parSource, returningRowMapper);
	}

	@Override
	public void applica(WebDal dal, HashMap<String, Object> contesto) {
// 		HashSet<String> proprietaUtilizzate = new HashSet<>();
// 		String returningClause = null;
// 		RowMapper<LinkedList<Pair<String, Object>>> returningRowMapper = null;
// 		Pair<String, RowMapper<LinkedList<Pair<String, Object>>>> returningPair = CampoSelect.GetReturningPair(campiReturning);
// 		if (returningPair != null) {
// 			returningClause = returningPair.getValue0();
// 			returningRowMapper = returningPair.getValue1();
// 		}

// 		proprietaUtilizzate.addAll(
// 			CampoSet.getAllProprietaUtilizzate(campiInseriti)
// 		);

// 		String sqlCmd = String.format("""
// insert into %s(%)
// values(%s)%s

// 			""",
// 			tabella,
// 			CampoSet.join(campiInseriti),
// 			CampoSet.joinValues(campiInseriti),
// 			returningClause
// 		);

// 		SqlParameterSource parSource = DbUtils.getParSource(contesto, proprietaUtilizzate);
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
			int numRows = dal.update(sqlCmd, parSource);
			log.debug(String.format("Caricati %d record", numRows));
			return null;
		}
		else {
			List<LinkedList<Pair<String, Object>>> vals = dal.query(sqlCmd, parSource, returningRowMapper);
			return vals;
		}
	}
}
