// package it.almaviva.foliage.enums;

// import java.util.HashMap;

// public enum TipoRilevamento {

// 	ACQUE,
// 	STRADE,
// 	FABBRICATI;
	
// 	private static Object[][] arrCorr = {
// 		new Object[] {TipoRilevamento.ACQUE, 1},
// 		new Object[] {TipoRilevamento.STRADE, 2},
// 		new Object[] {TipoRilevamento.FABBRICATI, 3}
// 	};
// 	private static HashMap<Integer, TipoRilevamento> intMap = new HashMap<>(){{
// 		for(Object[] arr1 : arrCorr) {
// 			put((Integer)arr1[1], (TipoRilevamento)arr1[0]);
// 		}
// 	}};
// 	private static HashMap<TipoRilevamento, Integer> enumMap = new HashMap<>(){{
// 		for(Object[] arr1 : arrCorr) {
// 			put((TipoRilevamento)arr1[0], (Integer)arr1[1]);
// 		}
// 	}};
// 	public static TipoRilevamento fromInt(int val) {
// 		return intMap.get(val);
// 	}
// 	public int toInt() {
// 		return enumMap.get(this);
// 	}
// }
