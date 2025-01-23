insert into foliage2.flgente_regione_tab (id_regione,codi_istat)
select ER.ID_ENTE, R.CODI_REGI
from (
		values ('01', 'PIEMONTE'),
			('02', 'VALLE D''AOSTA'),
			('03', 'LOMBARDIA'),
			('04', 'TRENTINO ALTO ADIGE'),
			('05', 'VENETO'),
			('06', 'FRIULI VENEZIA GIULIA'),
			('07', 'LIGURIA'),
			('08', 'EMILIA ROMAGNA'),
			('09', 'TOSCANA'),
			('10', 'UMBRIA'),
			('11', 'MARCHE'),
			('12', 'LAZIO'),
			('13', 'ABRUZZO'),
			('14', 'MOLISE'),
			('15', 'CAMPANIA'),
			('16', 'PUGLIA'),
			('17', 'BASILICATA'),
			('18', 'CALABRIA'),
			('19', 'SICILIA'),
			('20', 'SARDEGNA')
	) as R(CODI_REGI, DESC_REGI)
	join FLGENTE_ROOT_TAB as ER on (ER.NOME_ENTE = R.DESC_REGI and ER.TIPO_ENTE = 'REGIONE');
