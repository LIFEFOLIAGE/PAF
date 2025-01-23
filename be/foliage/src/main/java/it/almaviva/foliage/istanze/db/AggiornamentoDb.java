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
public class AggiornamentoDb implements IOperazioneDb {
		
	public String tabella;
	public LinkedList<CampoSet> campiAggiornati;
	public LinkedList<CondizioneEq> condizioniWhere;
	public LinkedList<CampoSelect> campiReturning;

	public AggiornamentoDb(
		String tabella,
		CampoSet[] campiAggiornati,
		CondizioneEq[] condizioniWhere,
		CampoSelect[] campiReturning
	) {
		this.tabella = tabella;
		if (campiAggiornati != null) {
			this.campiAggiornati = new LinkedList<>(Arrays.asList(campiAggiornati));
		}
		if (condizioniWhere != null) {
			this.condizioniWhere = new LinkedList<>(Arrays.asList(condizioniWhere));
		}
		if (campiReturning != null) {
			this.campiReturning = new LinkedList<>(Arrays.asList(campiReturning));
		}
	}
	public AggiornamentoDb(
		String tabella,
		CampoSet[] campiAggiornati,
		CondizioneEq[] condizioniWhere
	) {
		this(tabella, campiAggiornati, condizioniWhere, null);
	}

	private Triplet<String, HashMap<String, Object>, RowMapper<LinkedList<Pair<String, Object>>>> preApplica(HashMap<String, Object> contesto) {
		String setCampi = CampoSet.join(campiAggiornati);
		
		String returningClause = "";
		HashSet<String> proprietaUtilizzate = new HashSet<>();
		String condizioneWhere =  CondizioneEq.evalCondizioneWhere(condizioniWhere, proprietaUtilizzate);
		RowMapper<LinkedList<Pair<String, Object>>> returningRowMapper = null;
		proprietaUtilizzate.addAll(
			CampoSet.getAllProprietaUtilizzate(campiAggiornati)
		);
		
		Pair<String, RowMapper<LinkedList<Pair<String, Object>>>> returningPair = CampoSelect.GetReturningPair(campiReturning);
		if (returningPair != null) {
			returningClause = returningPair.getValue0();
			returningRowMapper = returningPair.getValue1();
		}

		String sqlCmd = String.format("""
update %s
set %s%s%s
				""",
				tabella,
				setCampi,
				condizioneWhere,
				returningClause
		);
		HashMap<String, Object> parSource = DbUtils.getParSource(contesto, proprietaUtilizzate);

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
			log.debug(String.format("Aggiornati %d record", numRows));
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
