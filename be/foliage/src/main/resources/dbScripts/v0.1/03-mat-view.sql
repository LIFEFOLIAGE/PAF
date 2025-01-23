CREATE MATERIALIZED VIEW foliage2.flgcodi_istat_enti_mvw TABLESPACE pg_default AS
SELECT c.id_comune AS id_ente_terr,
    r.codi_istat AS codi_istat_regione,
    p.codi_istat AS codi_istat_provincia,
    c.codi_istat AS codi_istat_comune
   FROM foliage2.flgente_comune_tab c
     JOIN foliage2.flgente_provincia_tab p ON p.id_provincia = c.id_provincia
     JOIN foliage2.flgente_regione_tab r ON r.id_regione = p.id_regione
UNION ALL
SELECT p.id_provincia AS id_ente_terr,
    r.codi_istat AS codi_istat_regione,
    p.codi_istat AS codi_istat_provincia,
    ''::character varying AS codi_istat_comune
   FROM foliage2.flgente_provincia_tab p
     JOIN foliage2.flgente_regione_tab r ON r.id_regione = p.id_regione
UNION ALL
SELECT r.id_regione AS id_ente_terr,
    r.codi_istat AS codi_istat_regione,
    ''::character varying AS codi_istat_provincia,
    ''::character varying AS codi_istat_comune
   FROM foliage2.flgente_regione_tab r
WITH DATA;

-- View indexes:
CREATE INDEX flgcodi_istat_enti_pk ON foliage2.flgcodi_istat_enti_mvw USING btree (id_ente_terr);