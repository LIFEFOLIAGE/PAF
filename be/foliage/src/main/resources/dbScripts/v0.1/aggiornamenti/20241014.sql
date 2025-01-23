

alter table FOLIAGE2.FLGISTA_INVIO_TAB add column ID_FILE_MODULO_ISTANZA_FIRMATO int;
ALTER TABLE foliage2.flgista_invio_tab ADD CONSTRAINT flgista_invio_fk_file_modulo_firmato FOREIGN KEY (ID_FILE_MODULO_ISTANZA_FIRMATO) REFERENCES foliage2.flgbase64_formio_file_master_tab(id_file);
alter table FOLIAGE2.FLGISTA_INVIO_TAB add column DATA_FIRMA timestamp without time zone;


CREATE TRIGGER POST_UPD_id_file_modulo_istanza_firmato
   AFTER UPDATE ON foliage2.flgista_invio_tab
   REFERENCING OLD TABLE AS old
   FOR EACH row
   WHEN (OLD.id_file_modulo_istanza_firmato IS DISTINCT FROM NEW.id_file_modulo_istanza_firmato)
   EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_modulo_istanza_firmato');

CREATE TRIGGER POST_DEL_id_file_modulo_istanza_firmato
   AFTER DELETE ON foliage2.flgista_invio_tab
   REFERENCING OLD TABLE AS oldtab
   FOR EACH row
   EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_modulo_istanza_firmato');



alter table FOLIAGE2.FLGISTA_INVIO_TAB add column is_firma_digitale boolean;
update FOLIAGE2.FLGISTA_INVIO_TAB
set is_firma_digitale = false;
alter table FOLIAGE2.FLGISTA_INVIO_TAB alter column is_firma_digitale set not null;

alter table FOLIAGE2.FLGISTA_INVIO_TAB add column ID_FILE_DOC_IDENTITA int;
ALTER TABLE foliage2.flgista_invio_tab ADD CONSTRAINT flgista_invio_fk_FILE_DOC_IDENTITA FOREIGN KEY (ID_FILE_DOC_IDENTITA) REFERENCES foliage2.flgbase64_formio_file_master_tab(id_file);

--trigger FOLIAGE2.FLGISTA_INVIO_TAB.ID_FILE_DOC_IDENTITA per cancellazione file

CREATE TRIGGER POST_UPD_id_file_doc_identita
   AFTER UPDATE ON foliage2.flgista_invio_tab
   REFERENCING OLD TABLE AS old
   FOR EACH row
   WHEN (OLD.id_file_doc_identita IS DISTINCT FROM NEW.id_file_doc_identita)
   EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_doc_identita');

CREATE TRIGGER POST_DEL_id_file_doc_identita
   AFTER DELETE ON foliage2.flgista_invio_tab
   REFERENCING OLD TABLE AS oldtab
   FOR EACH row
   EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_doc_identita');

