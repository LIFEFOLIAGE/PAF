package it.almaviva.foliage.enums;

import java.util.HashMap;

public enum TipoAuthScope {
	CASERMA,
	GENERICO,
	PARCO,
	TERRITORIALE;
	
	private static Object[][] arrCorr = {
		new Object[] {TipoAuthScope.GENERICO, 0},
		new Object[] {TipoAuthScope.TERRITORIALE, 1},
		new Object[] {TipoAuthScope.CASERMA, 2},
		new Object[] {TipoAuthScope.PARCO, 3}
	};
	private static HashMap<Integer, TipoAuthScope> intMap = new HashMap<>(){{
		for(Object[] arr1 : arrCorr) {
			put((Integer)arr1[1], (TipoAuthScope)arr1[0]);
		}
	}};
	private static HashMap<TipoAuthScope, Integer> enumMap = new HashMap<>(){{
		for(Object[] arr1 : arrCorr) {
			put((TipoAuthScope)arr1[0], (Integer)arr1[1]);
		}
	}};
	public static TipoAuthScope fromInt(int val) {
		return intMap.get(val);
	}
	public int toInt() {
		return enumMap.get(this);
	}
}
