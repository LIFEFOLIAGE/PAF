package it.almaviva.foliage.bean;

import java.math.BigDecimal;

public class VincoloModulo {
	public String gruppo;
	public String nome;
	public String codice;
	public String descrizione;
	public BigDecimal superficie;
	
	public final static org.springframework.jdbc.core.RowMapper<VincoloModulo> RowMapper =  (rs, rn) -> {
		VincoloModulo vm = new VincoloModulo();
		vm.gruppo = rs.getString("gruppo");
		vm.nome = rs.getString("desc_vincolo");
		vm.codice = rs.getString("cod_area");
		vm.descrizione = rs.getString("nome_area");
		vm.superficie = rs.getBigDecimal("superficie");
		
		if (vm.gruppo == null) {
			vm.gruppo = "";
		}
		if (vm.nome == null) {
			vm.nome = "";
		}
		if (vm.codice == null) {
			vm.codice = "";
		}
		if (vm.descrizione == null) {
			vm.descrizione = "";
		}
		return vm;
	};
	
	public static final String queryFromIdIsta = """
select gruppo, desc_vincolo, cod_area, nome_area, superficie
from foliage2.FLGVINCOLI_ISTA_TAB vi
	join foliage2.flgvincoli_tab v using (id_vincolo)
where vi.id_ista = :idIsta""";

}
