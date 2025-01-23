package it.almaviva.foliage.istanze.db;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class CampoSet implements ISqlCommandPart {
	public String nomeCampoTabella;
	public String nomeProprietaContesto;
	public String espressioneValore;

	public CampoSet(
		String nomeCampoTabella,
		String nomeProprietaContesto,
		String espressioneValore
	) {
		this.nomeCampoTabella = nomeCampoTabella;
		this.nomeProprietaContesto = nomeProprietaContesto;
		this.espressioneValore = espressioneValore;
		if (espressioneValore == null) {
			this.espressioneValore = String.format(":%s", nomeProprietaContesto);
		}
	}

	public CampoSet(
		String nomeCampoTabella,
		String nomeProprietaContesto
	) {
		this(nomeCampoTabella, nomeProprietaContesto, null);
	}


	public HashSet<String> getProprietaUtilizzate() {
		HashSet<String> outVal = new HashSet<String>();
		if (nomeProprietaContesto != null) {
			outVal.add(nomeProprietaContesto);
		}
		return outVal;
	}
	public String getCommandPartString() {
		return String.format("%s = %s", nomeCampoTabella, espressioneValore);
	}

	public static String join(Collection<CampoSet> campiColl) {
		return campiColl.stream().map(x -> x.getCommandPartString()).collect(Collectors.joining(", "));
	}
	public static String joinExpressions(Collection<CampoSet> campiColl) {
		return campiColl.stream().map(x -> x.espressioneValore).collect(Collectors.joining(", "));
	}
	public static String joinFields(Collection<CampoSet> campiColl) {
		return campiColl.stream().map(x -> x.nomeCampoTabella).collect(Collectors.joining(", "));
	}
	public static List<String> getAllProprietaUtilizzate(Collection<CampoSet> campiColl) {
		return campiColl.stream().flatMap(x -> x.getProprietaUtilizzate().stream()).toList();
	}
}