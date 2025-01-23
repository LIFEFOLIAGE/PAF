package it.almaviva.foliage.istanze.db;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import it.almaviva.foliage.services.WebDal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecuperoDb implements IOperazioneDb {
	public LinkedList<CampoSelect> campiSelect;
	public String query;
	public LinkedList<CondizioneEq> condizioniWhere;
	public String[] proprietaUtilizzateQuery;

	public RecuperoDb(
		String query,
		CampoSelect[] campiSelect,
		CondizioneEq[] condizioniWhere,
		String[] proprietaUtilizzateQuery
	) {
		this.campiSelect = new LinkedList<>(Arrays.asList(campiSelect));
		this.query = query;
		if (condizioniWhere != null) {
			this.condizioniWhere = new LinkedList<>(Arrays.asList(condizioniWhere));
		}
		this.proprietaUtilizzateQuery = proprietaUtilizzateQuery;
	}

	public RecuperoDb(
		String query,
		CampoSelect[] campiSelect,
		CondizioneEq[] condizioniWhere
	) {
		this(query, campiSelect, condizioniWhere, null);
	}

	public EliminazioneDb toEliminazione() {
		return new EliminazioneDb(query, condizioniWhere.toArray(new CondizioneEq[0]));
	}

	private Triplet<String, HashMap<String, Object>, RowMapper<LinkedList<Pair<String, Object>>>> preApplica(HashMap<String, Object> contesto) {
		HashSet<String> proprietaUtilizzate = new HashSet<>();
		if (proprietaUtilizzateQuery != null) {
			for (String prop : proprietaUtilizzateQuery) {
				proprietaUtilizzate.add(prop);
			}
		}
		String condizioneWhere =  CondizioneEq.evalCondizioneWhere(condizioniWhere, proprietaUtilizzate);
		
		String sqlCmd = String.format("""
select %s
from %s%s""",
				CampoSelect.join(campiSelect),
				query,
				condizioneWhere
		);
		HashMap<String, Object> parSource = DbUtils.getParSource(contesto, proprietaUtilizzate);
		RowMapper<LinkedList<Pair<String, Object>>> rowMapper = CampoSelect.getJointRowMapper(campiSelect);
		//log.debug(String.format("\n%s", sqlCmd));

		return new Triplet<String,HashMap<String, Object>,RowMapper<LinkedList<Pair<String,Object>>>>(sqlCmd, parSource, rowMapper);
	}

	@Override
	public void applica(WebDal dal, HashMap<String, Object> contesto) {
// 		HashSet<String> proprietaUtilizzate = new HashSet<>();
// 		String condizioneWhere =  CondizioneEq.evalCondizioneWhere(condizioniWhere, proprietaUtilizzate);
		
// 		String sqlCmd = String.format("""
// select %s
// from %s%s""",
// 				CampoSelect.join(campiSelect),
// 				query,
// 				condizioneWhere
// 		);
// 		log.debug(sqlCmd);
// 		SqlParameterSource parSource = DbUtils.getParSource(contesto, proprietaUtilizzate);
// 		RowMapper<LinkedList<Pair<String, Object>>> rowMapper = CampoSelect.getJointRowMapper(campiSelect);
		Triplet<String,HashMap<String, Object>,RowMapper<LinkedList<Pair<String,Object>>>> pars = preApplica(contesto);
		String sqlCmd = pars.getValue0();
		HashMap<String, Object> parSource = pars.getValue1();
		RowMapper<LinkedList<Pair<String, Object>>> rowMapper = pars.getValue2();

		
		try {
			LinkedList<Pair<String, Object>> vals = dal.queryForObject(sqlCmd, parSource, rowMapper);
			CampoSelect.updateContestoFromPairList(contesto, vals);
		}
		catch (org.springframework.dao.EmptyResultDataAccessException e) {
			log.warn("Nessun record caricato...");
		}
	}

	@Override
	public List<LinkedList<Pair<String, Object>>> applicaArray(WebDal dal, HashMap<String, Object> contesto) {
		Triplet<String,HashMap<String, Object>,RowMapper<LinkedList<Pair<String,Object>>>> pars = preApplica(contesto);
		String sqlCmd = pars.getValue0();
		HashMap<String, Object> parSource = pars.getValue1();
		RowMapper<LinkedList<Pair<String, Object>>> rowMapper = pars.getValue2();

		if (rowMapper == null) {
			return null;
		}
		else {
			List<LinkedList<Pair<String, Object>>> vals = dal.query(sqlCmd, parSource, rowMapper);
			return vals;
		}
	}
}
