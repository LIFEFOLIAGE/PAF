CREATE SEQUENCE foliage2.flgfoto_seq INCREMENT BY 1 START 1 CACHE 1 NO CYCLE;

delete
from foliage2.flgconf_batch_report_tab
where cod_report = 'AUTO_ACCETTAZIONE';

update foliage2.FLGSCHEDE_INTERVENTO_LIMITAZIONE_VINCA_TAB
set LINK_PDF_SCHEDA = replace(LINK_PDF_SCHEDA, '/documenti/', '');

