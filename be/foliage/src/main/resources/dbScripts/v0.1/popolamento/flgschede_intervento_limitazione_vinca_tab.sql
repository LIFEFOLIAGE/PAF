with schede_boschive as (
		select *
		from (
			values (1, 'sfollo e diradamento nei boschi ceduo', 'Ceduo'),
				(2, 'sfollo e diradamento nelle fustaie coetanee di età inferiore ai cinquanta anni', 'Fustaia'),
				(3, 'sfollo e diradamento nelle fustaie coetanee di età superiore ai cinquanta anni', 'Fustaia'),
				(4, 'Taglio di avviamento all''alto fusto', 'Ceduo'),
				(5, 'Interventi con matricinatura a gruppi', 'Ceduo'),
				(6, 'Tagli di utilizzazione dei boschi cedui', 'Ceduo')
			) as T(ID_SCHEDA, TIPO_INTERVENTO, DESC_GOVE)
	), shede_a_comunicazione as (
		select *, null::varchar DESC_GOVE
		from (
			values (7, 'interventi collegati al mantenimento delle attività tradizionali (usi civici)'),
				(8, 'tagli di utilizzazione tradizionale dei boschi cedui'),
				(9, 'tagli di utilizzazione dei boschi cedui di castagno'),
				(10, 'interventi nei castagneti da frutto'),
				(11, 'operazioni colturali negli impianti di arboricoltura da legno'),
				(12, 'operazioni di potatura e spalcatura nei popolamenti a prevalenza di conifere')
		) as T(ID_SCHEDA, TIPO_INTERVENTO)
	), link_pdf as (
		select *
		from (
			values (1, '/documenti/format-screening-prevalutazioni-sfollo-e-diradamento-cedui-1'),
				(2, '/documenti/format-screening-prevalutazioni-sfollo-e-diradamento-fustaie-1-1'),
				(3, '/documenti/format-screening-prevalutazioni-sfollo-e-diradamento-fustaie-2-1'),
				(4, '/documenti/format-screening-prevalutazioni-avviamento-alto-fusto-dgr-1'),
				(5, '/documenti/format-screening-prevalutazioni-matricinatura-a-gruppi-dgr-1'),
				(6, '/documenti/format-screening-prevalutazioni-boschi-cedui-dgr-1'),
				(7, '/documenti/format-screening-prevalutazioni-tradizionale-cedui-2-dgr-1'),
				(8, '/documenti/format-screening-prevalutazioni-tradizionale-cedui-1-dgr-1.pdf'),
				(9, '/documenti/format-screening-prevalutazioni-cedui-castagno-dgr-1'),
				(10, '/documenti/format-screening-prevalutazioni-castagneti-da-frutto-dgr-1'),
				(11, '/documenti/format-screening-prevalutazioni-altri-interventi-forestali-dgr-1'),
				(12, '/documenti/format-screening-prevalutazioni-potatura-spalcatura-conifere-dgr-1')
		) as T(ID_SCHEDA, FILE_PDF)
	)
insert into FLGSCHEDE_INTERVENTO_LIMITAZIONE_VINCA_TAB(ID_TIPO_ISTANZA, DESC_INTERVENTO, LINK_PDF_SCHEDA, DESC_GOVE)
select ID_TIPO_ISTANZA, TIPO_INTERVENTO, FILE_PDF, DESC_GOVE
from (
		select 'TAGLIO_BOSCHIVO' as COD_TIPO_ISTANZA_SPECIFICO, t.*
		from schede_boschive t
		union all
		select 'INTERVENTO_A_COMUNICAZIONE' as COD_TIPO_ISTANZA_SPECIFICO, t.*
		from shede_a_comunicazione t
	) a
	left join link_pdf p using (ID_SCHEDA)
	left join flgtipo_istanza_tab using (COD_TIPO_ISTANZA_SPECIFICO);
