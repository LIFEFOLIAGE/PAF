package it.almaviva.foliage.enums;

import java.util.HashMap;

public enum TipoAuthority {
	PROP,
	PROF,
	ISTR,
	DIRI,
	SORV,
	RESP,
	AMMI;
	
	private static Object[][] arrCorr = {
		new Object[] {TipoAuthority.PROP, 0},
		new Object[] {TipoAuthority.PROF, 1},
		new Object[] {TipoAuthority.ISTR, 2},
		new Object[] {TipoAuthority.DIRI, 3},
		new Object[] {TipoAuthority.SORV, 4},
		new Object[] {TipoAuthority.RESP, 5},
		new Object[] {TipoAuthority.AMMI, 6}
	};
	private static HashMap<Integer, TipoAuthority> intMap = new HashMap<>(){{
		for(Object[] arr1 : arrCorr) {
			put((Integer)arr1[1], (TipoAuthority)arr1[0]);
		}
	}};
	private static HashMap<TipoAuthority, Integer> enumMap = new HashMap<>(){{
		for(Object[] arr1 : arrCorr) {
			put((TipoAuthority)arr1[0], (Integer)arr1[1]);
		}
	}};
	public static TipoAuthority fromInt(int val) {
		return intMap.get(val);
	}
	public int toInt() {
		return enumMap.get(this);
	}
}
