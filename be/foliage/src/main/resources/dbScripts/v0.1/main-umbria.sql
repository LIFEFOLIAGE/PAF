/*
psql -U postgres 

CREATE DATABASE "foliageTest";

\set ECHO all
\set ON_ERROR_STOP on
\c "foliageTest";
CREATE EXTENSION postgis WITH SCHEMA public;
CREATE EXTENSION postgis_raster WITH SCHEMA public;

drop schema foliage2 cascade;
drop schema foliage_extra cascade;

CREATE SCHEMA foliage2 AUTHORIZATION foliage;
CREATE SCHEMA foliage_extra AUTHORIZATION foliage;

psql -U foliage 
*/

SET search_path TO foliage2,foliage_extra,public;

\i 01-ddl-tabelle.sql
\i 02-ddl-view.sql
\i 03-mat-view.sql
\i 04-ddl-procedure.sql
\i 05-ddl-trigger.sql

/*
delete from foliage2.flguten_tab;
delete from foliage2.flgprofili_report_tab;
delete from foliage2.flgconf_batch_report_tab;
delete from foliage2.flgbatch_scheduling_tab;
delete from foliage2.flgconf_batch_tab;



delete from foliage2.flgclay_tab;
delete from foliage2.flgtipo_intervento_tab;
delete from foliage2.flguso_suolo_tab;
delete from foliage2.flgtprp_tab;
delete from foliage2.flgtipo_viabilita_tab;
delete from foliage2.flgtazi_tab;
delete from foliage2.flgsupporto_finanziario_tab;
delete from foliage2.flgsspr_tab;
delete from foliage2.flgspecie_tab;
delete from foliage2.flgforme_trattamento_intervento_tab;
delete from foliage2.flgforme_trattamento_tab;
delete from foliage2.flgschede_intervento_limitazione_vinca_tab;
delete from foliage2.flgvincoli_tipo_ista_tab;
delete from foliage2.flgvincoli_tab;
delete from foliage2.flglimitazioni_tab;
delete from foliage2.flgqual_tab;
delete from foliage2.flgnprp_tab;
delete from foliage2.flggruppi_cubatura_tab;
delete from foliage2.flggove_tab;
delete from foliage2.flgsottocategorie_tab;
delete from foliage2.flgcategorie_regioni_tab;
delete from foliage2.flgcategorie_tab;
delete from foliage2.flgassortimento_tab;



delete from foliage2.flgtipo_istanza_tab;
delete from foliage2.flgprofili_cist_tab;
delete from foliage2.flgcist_tab;
delete from foliage2.flgschede_tipoistanza_tab;
delete from foliage2.flgstato_istanza_tab;

delete from foliage2.flgprof_tab;
delete from foliage2.flgabilitazioni_tab;

delete from foliage2.flglimiti_amministrativi_tab;
delete from foliage2.flgente_comune_tab;
delete from foliage2.flgente_provincia_tab;
delete from foliage2.flgente_regione_tab;
delete from foliage2.flgente_terr_tab;
delete from foliage2.flgente_root_tab;
*/


\i popolamento/flgente_root_tab.sql
\i popolamento/flgente_terr_tab.sql
\i popolamento/flgente_regione_tab.sql
\i popolamento/flgente_provincia_tab.sql
\i popolamento/flgente_comune_tab.sql
\i popolamento/flglimiti_amministrativi_tab.sql


\i popolamento/flgabilitazioni_tab.sql
\i popolamento/flgprof_tab.sql


\i popolamento/flgstato_istanza_tab.sql
--\i popolamento/flgcist_tab_LAZIO.sql
\i popolamento/flgcist_tab_UMBRIA.sql
\i popolamento/flgprofili_cist_tab.sql
--\i popolamento/flgtipo_istanza_tab_LAZIO.sql
\i popolamento/flgtipo_istanza_tab_UMBRIA.sql
\i popolamento/flgschede_tipoistanza_tab.sql


\i popolamento/flgassortimento_tab.sql
\i popolamento/flgcategorie_tab.sql
\i popolamento/flgcategorie_regioni_tab.sql
\i popolamento/flgsottocategorie_tab.sql
\i popolamento/flggove_tab.sql
\i popolamento/flggruppi_cubatura_tab.sql
\i popolamento/flgnprp_tab.sql
\i popolamento/flgqual_tab.sql
--\i popolamento/flglimitazioni_tab_LAZIO.sql
\i popolamento/flglimitazioni_tab_UMBRIA.sql
\i popolamento/flgvincoli_tab.sql
\i popolamento/flgvincoli_tipo_ista_tab.sql
\i popolamento/flgschede_intervento_limitazione_vinca_tab.sql
\i popolamento/flgforme_trattamento_tab.sql
\i popolamento/flgforme_trattamento_intervento_tab.sql
\i popolamento/flgspecie_tab.sql
\i popolamento/flgsspr_tab.sql
\i popolamento/flgsupporto_finanziario_tab.sql
\i popolamento/flgtazi_tab.sql
\i popolamento/flgtipo_viabilita_tab.sql
\i popolamento/flgtprp_tab.sql
\i popolamento/flguso_suolo_tab.sql
\i popolamento/flgtipo_intervento_tab.sql
\i popolamento/flgclay_tab.sql

\i popolamento/flgconf_batch_tab.sql
\i popolamento/flgbatch_scheduling_tab.sql
\i popolamento/flgconf_batch_report_tab.sql
\i popolamento/flgprofili_report_tab.sql
\i popolamento/flguten_tab.sql

--\i foliage_extra_lazio.dmp.sql
--\i foliage_extra_umbria.dmp.sql


