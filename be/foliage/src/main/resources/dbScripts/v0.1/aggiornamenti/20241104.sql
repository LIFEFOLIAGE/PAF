create table FOLIAGE2.FLGESECUZIONI_MONITORAGGIO_TAB (
	id_batch_ondemand int not null,
	data_acquisizione timestamp without time zone not null,
	id_client varchar not null,
	hostname varchar not null,
	ip_addres varchar not null,
	constraint FLGESECUZIONI_MONITORAGGIO_PK
		primary key(id_batch_ondemand),
	constraint FLGESECUZIONI_MONITORAGGIO_FK_BATCH_ONDEMAND
		foreign key(id_batch_ondemand)
		references FOLIAGE2.flgbatch_ondemand_tab
		on delete cascade
);

create table FOLIAGE2.FLGDATI_PRE_MONITORAGGIO_TAB (
	id_batch_ondemand int not null,
	codi_ista varchar not null,
	nome_uog varchar not null,
	cod_forma_trattamento_ceduo varchar,
	cod_forma_trattamento_fustaia varchar,
	superficie_utile numeric not null,
	data_inizio_autorizzazione date not null,
	data_fine_autorizzazione date not null,
	shape geometry not null,
	constraint FLGDATI_PRE_MONITORAGGIO_PK
		primary key(id_batch_ondemand, codi_ista, nome_uog),
	constraint FLGDATI_PRE_MONITORAGGIO_FK_BATCH_ONDEMAND
		foreign key(id_batch_ondemand)
		references FOLIAGE2.FLGESECUZIONI_MONITORAGGIO_TAB
		on delete cascade
);

--- GIA' PRESENTE
--INSERT INTO foliage2.flgconf_batch_tab (
--		cod_batch, desc_batch
--	)
--	values (
--			'MONITORAGGIO_SAT', 'Batch per l''elaborazione del monitoraggio satellitare'
--		);


alter table foliage2.flgserver_requests_tab add column headers varchar;

