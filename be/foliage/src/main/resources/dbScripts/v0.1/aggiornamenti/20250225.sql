alter table FOLIAGE2.FLGDATI_PRE_MONITORAGGIO_TAB add column cod_tipo_istanza varchar;

update FOLIAGE2.FLGDATI_PRE_MONITORAGGIO_TAB m
set cod_tipo_istanza = (
		select c.cod_tipo_istanza
		from foliage2.flgista_tab i
			join foliage2.flgtipo_istanza_tab t using (id_tipo_istanza)
			join foliage2.flgcist_tab c using (id_cist)
		where i.codi_ista = m.codi_ista
	);

alter table FOLIAGE2.FLGDATI_PRE_MONITORAGGIO_TAB alter column cod_tipo_istanza set not null;


create table FOLIAGE2.FLGALERT_MONITORAGGIO_TAB (
	data_rife date not null,
	id numeric not null,
	id_eop numeric not null,
	area_eop numeric not null,
	id_fmp varchar,
	area_tot_declared numeric not null,
	area_tot_intersect numeric not null,
	area_tot_requested numeric not null,
	amm_type varchar not null,
	alert varchar not null,
	shape geometry not null,
	constraint FLGALERT_MONITORAGGIO_PK
		primary key (data_rife, id)
);

create table FOLIAGE2.FLGNAT2000_MONITORAGGIO_TAB (
	data_rife date not null,
	id numeric not null,
	id_sito varchar not null,
	nome_sito varchar not null,
	sup_sito numeric not null,
	sup_boschiva numeric not null,
	perc_dist numeric,
	perc_tagli numeric,
	indice_biodiv numeric,
	shape geometry not null,
	constraint FLGNAT2000_MONITORAGGIO_PK
		primary key (data_rife, id)
);

create table FOLIAGE2.FLGPRIVACY_POLICY_TAB (
	id_privacy_policy int not null GENERATED ALWAYS AS identity,
	cod_privacy_policy varchar not null,
	data_rilascio date not null,
	testo varchar,
	is_current boolean,
	constraint FLGPRIVACY_POLICY_PK
		primary key(id_privacy_policy),
	constraint FLGPRIVACY_POLICY_UNQ_COD
		unique(cod_privacy_policy),
	constraint FLGPRIVACY_POLICY_UNQ_CURRENT
		unique (is_current),
	constraint FLGPRIVACY_POLICY_CK_CURRENT
		check (is_current is null or is_current)
);

create table FOLIAGE2.FLGUTEN_PRIVACY_POLICY_TAB (
	id_utente int not null,
	id_privacy_policy int not null,
	data_visione timestamp without time zone not null,
	constraint FLGUTEN_PRIVACY_POLICY_PK
		primary key (id_utente, id_privacy_policy),
	constraint FLGUTEN_PRIVACY_POLICY_FK_POLICY
		foreign key (id_privacy_policy)
		references FOLIAGE2.FLGPRIVACY_POLICY_TAB,
	constraint FLGUTEN_PRIVACY_POLICY_FK_UTENTE
		foreign key (id_utente)
		references FOLIAGE2.FLGUTEN_TAB
);


insert into FOLIAGE2.FLGPRIVACY_POLICY_TAB(cod_privacy_policy, data_rilascio)
	values ('V1', date'2024-11-01');

update FOLIAGE2.FLGPRIVACY_POLICY_TAB
set is_current = true
where cod_privacy_policy = 'V1';

insert into FOLIAGE2.FLGUTEN_PRIVACY_POLICY_TAB(id_utente, id_privacy_policy, data_visione)
select ID_UTEN, id_privacy_policy, localtimestamp
from FOLIAGE2.flguten_tab u
	cross join FOLIAGE2.FLGPRIVACY_POLICY_TAB p
where u.flag_accettazione
	AND p.is_current;


insert into foliage2.flgprofili_report_tab(id_report, id_profilo)
select id_report, id_profilo
from (
		values (
				'P3_NAT1', '{Carabiniere forestale}'::varchar[]
			), (
				'P3_NAT2', '{Carabiniere forestale}'::varchar[]
			)
	) as t1(cod_report, arr_profili)
	join foliage2.flgconf_batch_report_tab using (cod_report)
	cross join lateral (
		select desc_profilo
		from unnest(t1.arr_profili) as t(desc_profilo)
	) as t2(desc_profilo)
	join foliage2.flgprof_tab p on (p.descrizione = t2.desc_profilo);


INSERT INTO foliage2.flgconf_batch_report_tab(
		id_batch, cod_report, desc_report,
		report_name, formato_data_desc, formato_files,
		formato_data_file
	)
select id_batch, cod_report, desc_report,
	report_name, formato_data_desc, formato_files,
	formato_data_file
from (
		values(
				'MONITORAGGIO_SAT', 'ALERT', 'Report di monitoraggio per i tagli boschivi potenzialmente illegali',
				'AlertMonitoraggio', 'yyyy', '{ GeoJSON }'::varchar[], 'yyyy'
			)
	) as t(cod_batch, cod_report, desc_report, report_name, formato_data_desc, formato_files, formato_data_file)
	join foliage2.flgconf_batch_tab b using (cod_batch);


insert into foliage2.flgprofili_report_tab(id_report, id_profilo)
select id_report, id_profilo
from (
		values (
				'ALERT', '{Carabiniere forestale, Responsabile del Servizio}'::varchar[]
			)
	) as t1(cod_report, arr_profili)
	join foliage2.flgconf_batch_report_tab using (cod_report)
	cross join lateral (
		select desc_profilo
		from unnest(t1.arr_profili) as t(desc_profilo)
	) as t2(desc_profilo)
	join foliage2.flgprof_tab p on (p.descrizione = t2.desc_profilo);


update foliage2.flgconf_batch_report_tab
set id_batch = (
		select x.id_batch
		from foliage2.flgconf_batch_tab as x
		where x.cod_batch = 'MONITORAGGIO_SAT'
	)
where cod_report = 'P3_NAT2';

alter table foliage2.flgbatch_ondemand_tab rename column data_avvio to data_avvio_pianificata;
