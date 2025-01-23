package it.almaviva.foliage.istanze.db;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class CondizioneEq implements ISqlCommandPart {
	//public String aliasTabella;
	public String espressione;
	public String nomeProprietaContesto;
	public String espressione2;

	public CondizioneEq(
		String espressione,
		String espressione2,
		String nomeProprietaContesto
	) {
		this.espressione = espressione;
		this.espressione2 = espressione2;
		this.nomeProprietaContesto = nomeProprietaContesto;
	}
	
	public CondizioneEq(
		String espressione,
		String nomeProprietaContesto
	) {
		this(espressione, String.format(":%s", nomeProprietaContesto), nomeProprietaContesto);
	}
	
	public HashSet<String> getProprietaUtilizzate() {
		HashSet<String> outVal = new HashSet<String>();
		if (nomeProprietaContesto != null) {
			outVal.add(nomeProprietaContesto);
		}
		return outVal;
	}
	
	public String getCommandPartString() {
		return String.format("%s = %s", espressione, espressione2);
	}
	
	public static List<String> getAllProprietaUtilizzate(Collection<CondizioneEq> condizioniColl) {
		return condizioniColl.stream().flatMap(x -> x.getProprietaUtilizzate().stream()).toList();
	}
	public static String evalCondizioneWhere(Collection<CondizioneEq> condizioniColl, HashSet<String> proprietaUtilizzate) {
		String condizioneWhere = null;
		if (condizioniColl != null && condizioniColl.size() > 0) {
			String strCondizioni = condizioniColl.stream().map(x -> x.getCommandPartString()).collect(Collectors.joining(" and "));
			condizioneWhere = String.format(
				"""

where %s"""
				,
				strCondizioni
			);
			proprietaUtilizzate.addAll(
				CondizioneEq.getAllProprietaUtilizzate(condizioniColl)
			);
		}
		else {
			condizioneWhere = "";
		}
		return condizioneWhere;
	}
}

