set search_path=foliage2, public;


insert into foliage2.flgvincoli_tab (cod_vincolo,desc_vincolo,gruppo) values
	 ('AREE_PAESAGGISTICHE','Vincoli beni paesaggistici','Vincoli');

insert into FLGVINCOLI_TIPO_ISTA_TAB (ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE)
select ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE
from FLGTIPO_ISTANZA_TAB
	join FLGCIST_TAB using (ID_CIST)
	cross join FLGVINCOLI_TAB
	cross join FLGLIMITAZIONI_TAB
where cod_tipo_istanza != 'SOTTO_SOGLIA'
	and COD_VINCOLO = 'NAT2K'
	and COD_LIMITAZIONE = 'NAT2K_SOPRA_SOGLIA_UMBRIA';
	
insert into FLGVINCOLI_TIPO_ISTA_TAB (ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE)
select ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE
from FLGTIPO_ISTANZA_TAB
	join FLGCIST_TAB using (ID_CIST)
	cross join FLGVINCOLI_TAB
	cross join FLGLIMITAZIONI_TAB
where cod_tipo_istanza != 'SOTTO_SOGLIA'
	and COD_VINCOLO in ('AREE_PROTETTE', 'AREE_PAESAGGISTICHE')
	and COD_LIMITAZIONE = 'GENERICA_SOPRA_SOGLIA';


