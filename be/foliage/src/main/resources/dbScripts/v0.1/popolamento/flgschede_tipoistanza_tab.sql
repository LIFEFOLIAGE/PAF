insert into foliage2.FLGSCHEDE_TIPOISTANZA_TAB(ID_TIPO_ISTANZA, PROG_SCHEDA, COD_SCHEDA, IS_OBBLIGATORIA)
select TI.ID_TIPO_ISTANZA, T.PROG_SCHEDA, T.COD_SCHEDA, T.IS_OBBLIGATORIA
from ( values
		--('TIPOLOGIA', 0, null, true),
		--('TITOLARE', 1, null, true),
		('TIPO_GESTIONE', 2, null, true),
		('PARTICELLE', 3, null, true),
		('INTERVENTO', 4, null, true),
		--sotto
		('NATURA_2K', 5, ('{TAGLIO_BOSCHIVO, INTERVENTO_A_COMUNICAZIONE}')::varchar[], true),
		--taglio boschivo
		('SOPRASSUOLO', 6, ('{TAGLIO_BOSCHIVO}')::varchar[], true),
		('ASSORTIMENTI', 7, ('{TAGLIO_BOSCHIVO}')::varchar[], true),
		--int comunicazione
		('AMBITI_NON_FORESTALI', 6, ('{5}')::varchar[], true),
		('VINCOLISTICA', 6, ('{SOPRA_SOGLIA, ATTUAZIONE_PIANI, IN_DEROGA}')::varchar[], true),
		('UNITA_OMOGENEE', 7, ('{SOPRA_SOGLIA, ATTUAZIONE_PIANI, IN_DEROGA}')::varchar[], true),
		('ALTRI_STRATI', 8, ('{SOPRA_SOGLIA, ATTUAZIONE_PIANI, IN_DEROGA}')::varchar[], false),
		('VIABILITA', 9, ('{SOPRA_SOGLIA, ATTUAZIONE_PIANI, IN_DEROGA}')::varchar[], false),
		('PROSPETTI', 10, ('{SOPRA_SOGLIA, ATTUAZIONE_PIANI, IN_DEROGA}')::varchar[], true),
		('ALLEGATI', 11, ('{SOPRA_SOGLIA, ATTUAZIONE_PIANI, IN_DEROGA}')::varchar[], false),
		('RIEPILOGO', 12, ('{SOPRA_SOGLIA, ATTUAZIONE_PIANI, IN_DEROGA}')::varchar[], true)
	) as T(COD_SCHEDA, PROG_SCHEDA, TIPI_ISTANZA, IS_OBBLIGATORIA)
	join foliage2.flgtipo_istanza_tab ti on (T.TIPI_ISTANZA is null or ti.cod_tipo_istanza_specifico = any(T.TIPI_ISTANZA));

