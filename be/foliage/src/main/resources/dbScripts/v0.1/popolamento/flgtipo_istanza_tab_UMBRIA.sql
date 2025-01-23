insert into foliage2.FLGTIPO_ISTANZA_TAB(ID_CIST, COD_TIPO_ISTANZA_SPECIFICO, NOME_ISTANZA_SPECIFICO)
select ID_CIST, COD_TIPO_ISTANZA, NOME
from foliage2.FLGCIST_TAB
where COD_TIPO_ISTANZA != 'SOTTO_SOGLIA'
union all
select ID_CIST, T.NOME, T.DESCRIZIONE
from foliage2.FLGCIST_TAB
	cross join (
		values('TAGLIO_BOSCHIVO', 'Istanza di taglio boschivo'),
			('INTERVENTO_A_COMUNICAZIONE', 'Intervento a comunicazione diverso da istanza di taglio boschivo (arboricoltura da legno, tartufaie, castagneti da frutto, interventi accessori, etc. previsti dalla norma regionale)')
	) as T(NOME, DESCRIZIONE)
where COD_TIPO_ISTANZA = 'SOTTO_SOGLIA';
