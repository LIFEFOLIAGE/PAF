with schede_boschive as (
		select *
		from (
			values (1, 'sfollo e diradamento nei boschi ceduo'),
				(2, 'sfollo e diradamento nelle fustaie coetanee di età inferiore ai cinquanta anni'),
				(3, 'sfollo e diradamento nelle fustaie coetanee di età superiore ai cinquanta anni'),
				(4, 'Taglio di avviamento all''alto fusto'),
				(5, 'Interventi con matricinatura a gruppi'),
				(6, 'Tagli di utilizzazione dei boschi cedui')
			) as T(ID_SCHEDA, DESC_INTERVENTO)
	),
	forme_tratt as (
		select id_trat, cod_trat as COD_FORMA_TRATTAMENTO, desc_trat,desc_gove, flag_fine_turno,
			(flag_fine_turno = 'Si') as  is_fine_turno
		from (
			values
			 (1, 'RASO', 'Taglio a raso','Fustaia','Si'),
			 (2, 'AVVIAMENTO', 'Taglio di avviamento','Fustaia','No'),
			 (3, 'PREPARAZIONE', 'Taglio di preparazione','Fustaia','No'),
			 (4, 'SEMENTAZIONE', 'Taglio di sementazione','Fustaia','No'),
			 (5, 'SGOMBERO', 'Taglio di sgombero','Fustaia','Si'),
			 (6, 'A_BUCHE', 'Taglio a buche','Fustaia','Si'),
			 (7, 'SALTUARIO', 'Taglio saltuario','Fustaia','No'),
			 (8, 'SFOLLO_FUSTAIA', 'Sfollo e diradamento','Fustaia','No'),
			 (9, 'RASO_SEMPLICE', 'Taglio a raso semplice (senza rilascio di matricine)','Ceduo','Si'),
			 (10, 'RASO_CON_MATRICINE', 'Taglio a raso con rilascio di matricine (matricinato, intensamente matricinato, composto)','Ceduo','Si'),
			 (11, 'STERZO', 'Taglio a sterzo','Ceduo','No'),
			 (12, 'SFOLLO_CEDUO', 'Sfollo e diradamento','Ceduo','No'),
			 (13, 'FITOSANITARIO_FUSTAIA', 'Taglio fitosanitario','Fustaia','No'),
			 (14, 'FITOSANITARIO_CEDUO', 'Taglio fitosanitario','Ceduo','No')
			)as t(id_trat, cod_trat, desc_trat,desc_gove,flag_fine_turno)
	),
	forme_schede as (
		select *
		from (
			values (1,ARRAY[ 12 ]),
				(2,ARRAY[ 8 ]),
				(3,ARRAY[ 8 ]),
				(4,ARRAY[ 11 ]),
				(5,ARRAY[ 10 ]),
				(6,ARRAY[ 9, 10, 11, 12 ])
			) as T(ID_SCHEDA, TRATTAMENTI)
	)
insert into FLGFORME_TRATTAMENTO_INTERVENTO_TAB (ID_SCHEDA_INTERVENTO, ID_FORMA_TRATTAMENTO)
select ID_SCHEDA_INTERVENTO, ID_FORMA_TRATTAMENTO
from schede_boschive
	join forme_schede as f using (ID_SCHEDA)
	cross join unnest(f.trattamenti) as T(id_trat)
	join forme_tratt  using (id_trat)
	join FLGFORME_TRATTAMENTO_TAB using (COD_FORMA_TRATTAMENTO)
	join FLGSCHEDE_INTERVENTO_LIMITAZIONE_VINCA_TAB using (DESC_INTERVENTO);