
create trigger post_upd_id_file_tavola after
update
    on
    foliage2.flgtavole_istanza_tab referencing old table as old for each row
    when ((old.id_file_tavola is distinct
from
    new.id_file_tavola)) execute function foliage2.flgdelete_base64_formio_file('id_file_tavola');
create trigger post_del_id_file_tavola after
delete
    on
    foliage2.flgtavole_istanza_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_tavola');



create trigger post_upd_id_file_atto_nomina after
update
    on
    foliage2.flgrichieste_responsabile_tab referencing old table as old for each row
    when ((old.id_file_atto_nomina is distinct
from
    new.id_file_atto_nomina)) execute function foliage2.flgdelete_base64_formio_file('id_file_atto_nomina');
create trigger post_upd_id_file_doc_identita after
update
    on
    foliage2.flgrichieste_responsabile_tab referencing old table as old for each row
    when ((old.id_file_doc_identita is distinct
from
    new.id_file_doc_identita)) execute function foliage2.flgdelete_base64_formio_file('id_file_doc_identita');
create trigger post_del_id_file_atto_nomina after
delete
    on
    foliage2.flgrichieste_responsabile_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_atto_nomina');
create trigger post_del_id_file_doc_identita after
delete
    on
    foliage2.flgrichieste_responsabile_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_doc_identita');



create trigger post_upd_id_file_delega after
update
    on
    foliage2.flgtitolare_istanza_tab referencing old table as old for each row
    when ((old.id_file_delega is distinct
from
    new.id_file_delega)) execute function foliage2.flgdelete_base64_formio_file('id_file_delega');
create trigger post_del_id_file_delega after
delete
    on
    foliage2.flgtitolare_istanza_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_delega');


create trigger post_upd_id_file_allegato after
update
    on
    foliage2.flgallegati_ista_tab referencing old table as old for each row
    when ((old.id_file_allegato is distinct
from
    new.id_file_allegato)) execute function foliage2.flgdelete_base64_formio_file('id_file_allegato');
create trigger post_del_id_file_allegato after
delete
    on
    foliage2.flgallegati_ista_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_allegato');


create trigger post_upd_id_file after
update
    on
    foliage2.flgdocumenti_istuttoria_istanza_tab referencing old table as old for each row
    when ((old.id_file is distinct
from
    new.id_file)) execute function foliage2.flgdelete_base64_formio_file('id_file');
create trigger post_del_id_file after
delete
    on
    foliage2.flgdocumenti_istuttoria_istanza_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file');



create trigger post_upd_id_file_atto_nomina_rappresentante_legale after
update
    on
    foliage2.flgfiletipo_gestione_tab referencing old table as old for each row
    when ((old.id_file_atto_nomina_rappresentante_legale is distinct
from
    new.id_file_atto_nomina_rappresentante_legale)) execute function foliage2.flgdelete_base64_formio_file('id_file_atto_nomina_rappresentante_legale');
create trigger post_upd_id_file_autocertificazione_ditta_forestale after
update
    on
    foliage2.flgfiletipo_gestione_tab referencing old table as old for each row
    when ((old.id_file_autocertificazione_ditta_forestale is distinct
from
    new.id_file_autocertificazione_ditta_forestale)) execute function foliage2.flgdelete_base64_formio_file('id_file_autocertificazione_ditta_forestale');
create trigger post_upd_id_file_autocertificazione_proprieta after
update
    on
    foliage2.flgfiletipo_gestione_tab referencing old table as old for each row
    when ((old.id_file_autocertificazione_proprieta is distinct
from
    new.id_file_autocertificazione_proprieta)) execute function foliage2.flgdelete_base64_formio_file('id_file_autocertificazione_proprieta');
create trigger post_upd_id_file_delega_presentazione after
update
    on
    foliage2.flgfiletipo_gestione_tab referencing old table as old for each row
    when ((old.id_file_delega_presentazione is distinct
from
    new.id_file_delega_presentazione)) execute function foliage2.flgdelete_base64_formio_file('id_file_delega_presentazione');
create trigger post_upd_id_file_delega_titolarita after
update
    on
    foliage2.flgfiletipo_gestione_tab referencing old table as old for each row
    when ((old.id_file_delega_titolarita is distinct
from
    new.id_file_delega_titolarita)) execute function foliage2.flgdelete_base64_formio_file('id_file_delega_titolarita');
create trigger post_upd_id_file_documenti_identita after
update
    on
    foliage2.flgfiletipo_gestione_tab referencing old table as old for each row
    when ((old.id_file_documenti_identita is distinct
from
    new.id_file_documenti_identita)) execute function foliage2.flgdelete_base64_formio_file('id_file_documenti_identita');
create trigger post_upd_id_file_provvedimento_boschi_silenti after
update
    on
    foliage2.flgfiletipo_gestione_tab referencing old table as old for each row
    when ((old.id_file_provvedimento_boschi_silenti is distinct
from
    new.id_file_provvedimento_boschi_silenti)) execute function foliage2.flgdelete_base64_formio_file('id_file_provvedimento_boschi_silenti');
create trigger post_del_id_file_atto_nomina_rappresentante_legale after
delete
    on
    foliage2.flgfiletipo_gestione_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_atto_nomina_rappresentante_legale');
create trigger post_del_id_file_autocertificazione_ditta_forestale after
delete
    on
    foliage2.flgfiletipo_gestione_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_autocertificazione_ditta_forestale');
create trigger post_del_id_file_autocertificazione_proprieta after
delete
    on
    foliage2.flgfiletipo_gestione_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_autocertificazione_proprieta');
create trigger post_del_id_file_delega_presentazione after
delete
    on
    foliage2.flgfiletipo_gestione_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_delega_presentazione');
create trigger post_del_id_file_delega_titolarita after
delete
    on
    foliage2.flgfiletipo_gestione_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_delega_titolarita');
create trigger post_del_id_file_documenti_identita after
delete
    on
    foliage2.flgfiletipo_gestione_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_documenti_identita');
create trigger post_del_id_file_provvedimento_boschi_silenti after
delete
    on
    foliage2.flgfiletipo_gestione_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_provvedimento_boschi_silenti');


create trigger post_upd_id_file_vinca after
update
    on
    foliage2.flgista_elaborato_vinca_tab referencing old table as old for each row
    when ((old.id_file_vinca is distinct
from
    new.id_file_vinca)) execute function foliage2.flgdelete_base64_formio_file('id_file_vinca');
create trigger post_del_id_file_vinca after
delete
    on
    foliage2.flgista_elaborato_vinca_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_vinca');



create trigger post_upd_id_file_diritti_istruttoria after
update
    on
    foliage2.flgista_invio_tab referencing old table as old for each row
    when ((old.id_file_diritti_istruttoria is distinct
from
    new.id_file_diritti_istruttoria)) execute function foliage2.flgdelete_base64_formio_file('id_file_diritti_istruttoria');
create trigger post_upd_id_file_modulo_istanza after
update
    on
    foliage2.flgista_invio_tab referencing old table as old for each row
    when ((old.id_file_modulo_istanza is distinct
from
    new.id_file_modulo_istanza)) execute function foliage2.flgdelete_base64_formio_file('id_file_modulo_istanza');
create trigger post_upd_id_file_ricevute after
update
    on
    foliage2.flgista_invio_tab referencing old table as old for each row
    when ((old.id_file_ricevute is distinct
from
    new.id_file_ricevute)) execute function foliage2.flgdelete_base64_formio_file('id_file_ricevute');
create trigger post_del_id_file_diritti_istruttoria after
delete
    on
    foliage2.flgista_invio_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_diritti_istruttoria');
create trigger post_del_id_file_modulo_istanza after
delete
    on
    foliage2.flgista_invio_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_modulo_istanza');
create trigger post_del_id_file_ricevute after
delete
    on
    foliage2.flgista_invio_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_ricevute');


create trigger post_upd_id_file_pagamento after
update
    on
    foliage2.flgista_proroga_tab referencing old table as old for each row
    when ((old.id_file_pagamento is distinct
from
    new.id_file_pagamento)) execute function foliage2.flgdelete_base64_formio_file('id_file_pagamento');
create trigger post_del_id_file_pagamento after
delete
    on
    foliage2.flgista_proroga_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_pagamento');

create trigger post_upd_id_file_modulo_istruttoria after
update
    on
    foliage2.flgvalutazione_istanza_tab referencing old table as old for each row
    when ((old.id_file_modulo_istruttoria is distinct
from
    new.id_file_modulo_istruttoria)) execute function foliage2.flgdelete_base64_formio_file('id_file_modulo_istruttoria');
create trigger post_del_id_file_modulo_istruttoria after
delete
    on
    foliage2.flgvalutazione_istanza_tab referencing old table as oldtab for each row execute function foliage2.flgdelete_base64_formio_file('id_file_modulo_istruttoria');
