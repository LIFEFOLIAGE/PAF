CREATE OR REPLACE VIEW foliage2.flgcomu_viw
AS SELECT flgente_comune_tab.id_comune,
    flgente_comune_tab.codi_istat AS codi_istat_comune,
    flgente_root_tab.nome_ente AS comune,
    flgente_comune_tab.id_provincia,
    flgente_root_tab.data_iniz_vali,
    flgente_root_tab.data_fine_vali
   FROM foliage2.flgente_comune_tab
     JOIN foliage2.flgente_root_tab ON flgente_root_tab.id_ente = flgente_comune_tab.id_comune;



CREATE OR REPLACE VIEW foliage2.flgprov_viw
AS SELECT flgente_provincia_tab.id_provincia,
    flgente_provincia_tab.codi_istat AS codi_istat_provincia,
    flgente_root_tab.nome_ente AS provincia,
    flgente_provincia_tab.id_regione,
    flgente_root_tab.data_iniz_vali,
    flgente_root_tab.data_fine_vali
   FROM foliage2.flgente_provincia_tab
     JOIN foliage2.flgente_root_tab ON flgente_root_tab.id_ente = flgente_provincia_tab.id_provincia;


CREATE OR REPLACE VIEW foliage2.flgregi_viw
AS SELECT flgente_regione_tab.id_regione,
    flgente_regione_tab.codi_istat AS codi_istat_regione,
    flgente_root_tab.nome_ente AS regione,
    flgente_root_tab.data_iniz_vali,
    flgente_root_tab.data_fine_vali
   FROM foliage2.flgente_regione_tab
     JOIN foliage2.flgente_root_tab ON flgente_root_tab.id_ente = flgente_regione_tab.id_regione;
	 
	 
	 