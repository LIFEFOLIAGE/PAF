package it.almaviva.foliage.istanze.db;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import it.almaviva.foliage.services.WebDal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EliminazioneDb implements IOperazioneDb {

	public String tabella;
	public String usingQuery;
	public LinkedList<CondizioneEq> condizioniWhere;

	public EliminazioneDb(
		String tabella,
		String usingQuery,
		CondizioneEq[] condizioniWhere
	) {
		this.tabella = tabella;
		this.usingQuery = usingQuery;
		this.condizioniWhere = new LinkedList<>(Arrays.asList(condizioniWhere));
	}

	public EliminazioneDb(
		String tabella,
		CondizioneEq[] condizioniWhere
	) {
		this(tabella, null, condizioniWhere);
	}
	@Override
	public void applica(WebDal dal, HashMap<String, Object> contesto) {
		HashSet<String> proprietaUtilizzate = new HashSet<>();
		String usingClause = (usingQuery == null) ? "" : String.format("""
 using (
		%s
	) as T""",
			usingQuery.replaceAll("\n", "\n\t\t")
		);
		String condizioneWhere =  CondizioneEq.evalCondizioneWhere(condizioniWhere, proprietaUtilizzate);
		
		String sqlCmd = String.format("""
delete%s
from %s%s
				""",
				usingClause,
				tabella,
				condizioneWhere
		);
		HashMap<String, Object> parSource = DbUtils.getParSource(contesto, proprietaUtilizzate);
		//log.debug(sqlCmd);
		
		int numRows = dal.update(sqlCmd, parSource);
		log.debug(String.format("Eliminati %d record", numRows));
	}
	
	@Override
	public List<LinkedList<Pair<String, Object>>> applicaArray(WebDal dal, HashMap<String, Object> contesto) {
		applica(dal, contesto);
		return null;
	}
}
