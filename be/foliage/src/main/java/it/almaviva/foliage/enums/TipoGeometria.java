package it.almaviva.foliage.enums;

import java.util.HashMap;

public enum TipoGeometria {
	PUNTO,
	POLIGONO,
	LINEA;
	
	private static Object[][] arrCorr = {
		new Object[] {TipoGeometria.PUNTO, 0},
		new Object[] {TipoGeometria.POLIGONO, 1},
		new Object[] {TipoGeometria.LINEA, 2}
	};
	private static HashMap<Integer, TipoGeometria> intMap = new HashMap<>(){{
		for(Object[] arr1 : arrCorr) {
			put((Integer)arr1[1], (TipoGeometria)arr1[0]);
		}
	}};
	private static HashMap<TipoGeometria, Integer> enumMap = new HashMap<>(){{
		for(Object[] arr1 : arrCorr) {
			put((TipoGeometria)arr1[0], (Integer)arr1[1]);
		}
	}};
	public static TipoGeometria fromInt(int val) {
		return intMap.get(val);
	}
	public int toInt() {
		return enumMap.get(this);
	}
}
