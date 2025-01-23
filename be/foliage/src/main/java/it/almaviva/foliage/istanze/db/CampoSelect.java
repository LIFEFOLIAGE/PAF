package it.almaviva.foliage.istanze.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.jdbc.core.RowMapper;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.function.GetFromResultSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CampoSelect implements ISqlRecuperoCommandPart {
	private final static Pattern patternTogliAliasTabella = Pattern.compile("[^.]*$");
	public String alias;
	public String espressione;
	public String nomeProprietaContesto;
	public GetFromResultSet getValue;

	public String aliasEff;
	
	public CampoSelect(
		String alias,
		String espressione,
		String nomeProprietaContesto,
		GetFromResultSet getValue
	) throws Exception {
		this.alias = alias;
		this.espressione = espressione;
		this.nomeProprietaContesto = nomeProprietaContesto;
		this.getValue = getValue;
		
		if (this.alias == null) {
			Matcher matcher = patternTogliAliasTabella.matcher(this.espressione);
			if (matcher.find()) {
				this.aliasEff = matcher.group();
			}
			else {
				throw new FoliageException("Alias mancante per un'espressione complessa");
			}
		}
		else {
			this.aliasEff = this.alias;
		}


		//this.aliasEff = (this.alias == null) ? this.espressione : this.alias;
	}
	
	public CampoSelect(
		String espressione,
		String nomeProprietaContesto,
		GetFromResultSet getValue
	) throws Exception {
		this(
			null,
			espressione,
			nomeProprietaContesto,
			getValue
		);
	}
	
	public CampoSelect(
		String espressione,
		GetFromResultSet getValue
	) throws Exception {
		this(
			null,
			espressione,
			espressione,
			getValue
		);
	}

	public HashSet<String> getProprietaUtilizzate() {
		HashSet<String> outVal = new HashSet<String>();
		outVal.add(nomeProprietaContesto);
		return outVal;
	}
	
	public String getCommandPartString() {
		if (alias == null) {
			return String.format("%s", espressione);
		}
		else {
			return String.format("%s as %s", espressione, alias);
		}
	}
	@Override
	public RowMapper<Pair<String, Object>> rowMapper() {	
		return (rs, rowNum)-> {
			Object value = null;
			try {
				value = this.getValue.get(rs, rowNum, this.aliasEff);
				if (rs.wasNull()) {
					value = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new Pair<String, Object>(nomeProprietaContesto, value);
		};
	}
	
	public static String join(Collection<CampoSelect> campiColl) {
		return campiColl.stream().map(x -> x.getCommandPartString()).collect(Collectors.joining(", "));
	}
	
	public static Pair<String, RowMapper<LinkedList<Pair<String, Object>>>> GetReturningPair(Collection<CampoSelect> campiReturning) {
		if (campiReturning != null && campiReturning.size() > 0) {
			String returningClause = "";
			String strReturning = CampoSelect.join(campiReturning);
			//campiReturning.stream().map(x -> x.getCommandPartString()).collect(Collectors.joining(", "));
			returningClause = String.format(
				"""

returning %s"""
				,
				strReturning
			);
			RowMapper<LinkedList<Pair<String, Object>>> returningRowMapper = CampoSelect.getJointRowMapper(campiReturning);
			return new Pair<String, RowMapper<LinkedList<Pair<String, Object>>>>(returningClause, returningRowMapper);
		}
		else {
			return null;
		}
	}
	
	public static RowMapper<LinkedList<Pair<String, Object>>> getJointRowMapper(Collection<CampoSelect> collCampi) {
		return (rs, rn) -> {
			LinkedList<Pair<String, Object>> outVal = new LinkedList<>();
			outVal.addAll(collCampi.stream().map(
				cr -> {
					Pair<String, Object> outVal1 = null;
					try {
						return cr.rowMapper().mapRow(rs, rn);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return outVal1;
				}
			).toList());
			return outVal;
		};
	}

	public static void updateContestoFromPairList(HashMap<String, Object> contesto, LinkedList<Pair<String, Object>> vals) {
		log.debug("\n\nElenco Proprietà ottenute(list):");
		for (Pair<String,Object> pair : vals) {
			String k = pair.getValue0();
			Object v = pair.getValue1();
			contesto.put(k, v);
			if (v == null) {
				log.debug(String.format("%s = null", k));
			}
			else {
				String strValue = v.toString();
				if (strValue.length() > 100) {
					strValue = strValue.substring(0, 100);
				}
				log.debug(String.format("%s = '%s' (%s)", k, strValue, v.getClass().getName()));
			}
		}
		log.debug("\n\n");
	}
}