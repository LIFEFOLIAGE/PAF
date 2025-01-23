package it.almaviva.foliage.istanze;

import java.util.HashMap;

public interface ISchedaIstanzaWithContext extends ISchedaIstanza<HashMap<String, Object>, HashMap<String, Object>>{
	public String[] getContextUpdateProps();
}
