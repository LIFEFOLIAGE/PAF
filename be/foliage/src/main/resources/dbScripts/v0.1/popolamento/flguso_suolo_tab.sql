insert into FLGUSO_SUOLO_TAB(COD_USO_SUOLO, DESC_USO_SUOLO)
with flginte_no_forest_tab as (
		select *
		from (
			values
				 (1,'Arboricoltura da Legno','1_1','cure colturali','(R.R. 7/2002 s.m.i. art. 71 comma 7)','Superficie (Ha) 2 decimali'),
				 (1,'Arboricoltura da Legno','1_2','Taglio di utilizzazione in impianti di arboricoltura da legno ','(R.R. 7/2002 s.m.i. art. 71 comma 7)','Superficie (Ha) 2 decimali'),
				 (2,'Tartufaie','2_1','Gestione Piante Simbionti','','Superficie (Ha) 2 decimali'),
				 (2,'Tartufaie','2_2','Potature di piante simbionti ','(R.R.7/2002 s.m.i. art. 20 ter comma 2)','Superficie (Ha) 2 decimali'),
				 (3,'Castagneto da frutto','3_1','a) formazione e  ripristino di ripiani di altezza superiore a mezzo metro ','Interventi in castagneti da frutto (R.R. 7/2002 s.m.i. art. 51 comma 4 ','Superficie (Ha) 2 decimali'),
				 (3,'Castagneto da frutto','3_2','b) sostituzione di piante di castagno morte o non più produttive','Interventi in castagneti da frutto (R.R. 7/2002 s.m.i. art. 51 comma 4','Superficie (Ha) 2 decimali'),
				 (3,'Castagneto da frutto','3_3','c) taglio delle piante arboree la cui chioma è distante meno di due metri dalla chioma dei castagni da frutto','Interventi in castagneti da frutto (R.R. 7/2002 s.m.i. art. 51 comma 4','Superficie (Ha) 2 decimali'),
				 (4,'Bosco (interventi diversi o accessori alla gestione forestale)','4_1','Esbosco dell''albero di maggiori dimensioni o di maggiore età, morto o caduto ','(R.R. 7/2002 s.m.i. art. 10 comma 2)','numero esemplari oggetto di esbosco'),
				 (4,'Bosco (interventi diversi o accessori alla gestione forestale)','4_2','Interventi di sottopiantagione finalizzati all''arricchimento specifico',' (R.R. 7/2002 s.m.i. art. 15 comma 3) ','Superficie (Ha) 2 decimali'),
				 (4,'Bosco (interventi diversi o accessori alla gestione forestale)','4_3','Interventi di sottopiantagione finalizzati al rinfoltimento del bosco ','(R.R. 7/2002 s.m.i. art. 15 comma 3) ','Superficie (Ha) 2 decimali'),
				 (4,'Bosco (interventi diversi o accessori alla gestione forestale)','4_4','Ripuliture nei boschi per favorire l''insediamento della rinnovazione naturale ','(R.R. 7/2002 s.m.i. art. 16 comma 5)','Superficie (Ha) 2 decimali'),
				 (4,'Bosco (interventi diversi o accessori alla gestione forestale)','4_5','Ripristino dei boschi danneggiati o distrutti ','(R.R. 7/2002 s.m.i. art. 19 comma 3)','Superficie (Ha) 2 decimali'),
				 (4,'Bosco (interventi diversi o accessori alla gestione forestale)','4_6','Pascolo in bosco ','(R.R. 7/2002 s.m.i. art. 20 comma 3)','Superficie (Ha) 2 decimali'),
				 (4,'Bosco (interventi diversi o accessori alla gestione forestale)','4_7','Carbonizzazione ','(R.R. 7/2002 s.m.i. art. 32 comma 1)','numero siti'),
				 (4,'Bosco (interventi diversi o accessori alla gestione forestale)','4_8','Realizzazione di imboschimenti e rimboschimenti',' (R.R. 7/2002 s.m.i. art. 72 comma 5)','Superficie (Ha) 2 decimali'),
				 (4,'Bosco (interventi diversi o accessori alla gestione forestale)','4_9','Manutenzione ordinaria di strade o piste principali  ','(R.R. 7/2002 s.m.i. art. 77 comma 3)','estensione intervento (metri lineari) intero senza decimali'),
				 (4,'Bosco (interventi diversi o accessori alla gestione forestale)','4_10','Manutenzione ordinaria di aree di pertinenza di elettrodotti aerei ','(R.R. 7/2002 s.m.i. art.85 comma 2)','Superficie (Ha) 2 decimali'),
				 (4,'Bosco (interventi diversi o accessori alla gestione forestale)','4_11','Progetti di ricerca','(R.R. 7/2002 s.m.i. art. 89 comma 1)','Superficie (Ha) 2 decimali'),
				 (5,'Seminativi e altri usi del suolo ','5_1','Realizzazione e coltivazione di impianti destinati alla produzione di ‘alberi di Natale'' ','(R.R. 7/2002 s.m.i. art. 73 comma 2)','Superficie (Ha) 2 decimali')
			) as T(id_tipo,desc_tipo,cod_intervento,tipo_intervento,normativo,parametro)
	)
select distinct id_tipo::varchar as cod_tipo, desc_tipo
from flginte_no_forest_tab;
