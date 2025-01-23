-- noinspection SqlNoDataSourceInspectionForFile

---------
-- Operatività profili:
--	Creazione istanze a 
---		Proprietario, Professionista, Sportello
--	Modifica istanze a 
---		Proprietario, Professionista, Sportello, (Istruttore--NO-NO)
--	Consultazione istanze a 
---		Proprietario, Professionista, Sportello, Dirigente, Istruttore
--	Variazione Compilatore
---		Proprietario
--	Assegnazione istruttore a
---		Dirigente
--	Approvazione istanze a
---		Istruttore, Dirigente
--	Creazione Responsabile di servizio per ciascun ente a
---		Amministratore
--	Assegnazione profili Dirigente, Istruttore di un ente a
---		Responsabile di servizio
--	Visualizzazione proprio account a
---		tutti
--	Visualizzazione delle utenze a
---		Responsabile di servizio, Amministratore
---------

--0) Login e accettazione
--1) Visualizzazione proprio account: dettagli anagrafici, recapiti, richieste profili per enti territoriali, notifiche
--2) Visualizzazione delle utenze: dettagli anagraficie, recapiti, notifiche, richieste profili per enti territoriali (con possibilità di approvazione, ma solo un amministratore può approvare un responsabile di servizio )
--3) Assegnazione profili Dirigente, Istruttore tramite richieste utenti
--4) Consultazione istanze: ricerca istanze con visibilità coerente con il profilo. Possibilità di:
--		- apertura in sola lettura
--		- creazione nuove istanza (solo per proprietari e professionisti)
--		- apertura in lettura/scrittura delle istanze che si hanno in compilazione
--		- variazione dell'utente assegnato alla compilazione (per il proprietario)
--		- assegnazione istruttore (per il dirigente dell'ente)
--		- valutazione (per l'istruttore assegnato)
--		- richiesta integrazioni (per l'istruttore assegnato)
--		- compilazione (per l'utente assegnato alla compilazione)
--		- visualizzare il profilo del professionista che ha presentato una domanda (per l'istruttore assegnato)



--- Ripristino di tutte le regioni
insert into flgregi_tab(id_regi, codi_regi, desc_regi, flag_valido, user_ins, data_ini_vali)
select nextval('flgregi_seq'), codi, upper(nome), 1, 'admin', date'2021-11-04'
from (
	values
		(1,'01','Piemonte'),
		(2,'02','Valle D''Aosta'),
		(3,'03','Lombardia'),
		(4,'04','Trentino Alto Adige'),
		(5,'05','Veneto'),
		(6,'06','Friuli Venezia Giulia'),
		(7,'07','Liguria'),
		(8,'08','Emilia Romagna'),
		(9,'09','Toscana'),
		(10,'10','Umbria'),
		(12,'12','Lazio'),
		(13,'13','Abruzzo'),
		(14,'14','Molise'),
		(15,'15','Campania'),
		(16,'16','Puglia'),
		(17,'17','Basilicata'),
		(18,'18','Calabria'),
		(19,'19','Sicilia'),
		(20,'20','Sardegna'),
		(11,'11','Marche')
	) as T(id, codi, nome)
where codi not in (
	select codi_regi
	from flgregi_tab
);


-- ridenominazione con Regione dei comuni con nome duplicato
update FLGCOMU_TAB c
set desc_comu = desc_comu||' ('||(
		select desc_regi
		from flgprov_tab as p
			join (
				values
					(1,'01','Piemonte'),
					(2,'02','Valle D''Aosta'),
					(3,'03','Lombardia'),
					(4,'04','Trentino Alto Adige'),
					(5,'05','Veneto'),
					(6,'06','Friuli Venezia Giulia'),
					(7,'07','Liguria'),
					(8,'08','Emilia Romagna'),
					(9,'09','Toscana'),
					(10,'10','Umbria'),
					(12,'12','Lazio'),
					(13,'13','Abruzzo'),
					(14,'14','Molise'),
					(15,'15','Campania'),
					(16,'16','Puglia'),
					(17,'17','Basilicata'),
					(18,'18','Calabria'),
					(19,'19','Sicilia'),
					(20,'20','Sardegna'),
					(11,'11','Marche'),
					(24,'99','Nuova')
			) as r(id, codi_regi, desc_regi) on (r.codi_regi = p.codi_regi)
		where p.id_prov  = c.id_prov
	)||')'
where DESC_COMU in (
		select DESC_COMU
		from FLGCOMU_TAB
		group by DESC_COMU
		having count(*) > 1
	);


-- elenco dei tutti gli enti (di ogni tipo)
create table FLGENTE_ROOT_TAB(
	ID_ENTE int not null GENERATED ALWAYS AS IDENTITY, 
	TIPO_ENTE varchar not null,
	NOME_ENTE varchar not null,
	DATA_INIZ_VALI date default date'1900-01-01' not null,
	DATA_FINE_VALI date default date'9999-12-31' not null,
	constraint FLGENTE_ROOT_PK
		primary key (ID_ENTE),
	constraint FLGENTE_ROOT_UNQ
		unique (TIPO_ENTE, NOME_ENTE),
	constraint FLGENTE_ROOT_CK_TIPO
		check (TIPO_ENTE in ('REGIONE', 'PROVINCIA', 'COMUNE', 'CASERMA', 'PARCO'))
);

insert into FLGENTE_ROOT_TAB(TIPO_ENTE, NOME_ENTE)
	values ('CASERMA', 'Corpo dei carabinieri');

insert into FLGENTE_ROOT_TAB(TIPO_ENTE, NOME_ENTE)
	values ('PARCO', 'Parco nazionale');

insert into FLGENTE_ROOT_TAB(TIPO_ENTE, NOME_ENTE)
select 'REGIONE', DESC_REGI
from FLGREGI_TAB
union all
select 'PROVINCIA', DESC_PROV
from FLGPROV_TAB
union all
select 'COMUNE', DESC_COMU
from FLGCOMU_TAB;

-- elenco dei tutti gli enti territoriali
create table FLGENTE_TERR_TAB(
	ID_ENTE_TERR int not null,
	constraint FLGENTE_TERR_PK
		primary key (ID_ENTE_TERR),
	constraint FLGENTE_TERR_FK_ENTE
		foreign key (ID_ENTE_TERR)
		references FLGENTE_ROOT_TAB	
);

insert into FLGENTE_TERR_TAB(ID_ENTE_TERR)
select ID_ENTE
from FLGENTE_ROOT_TAB
where TIPO_ENTE in ('REGIONE', 'PROVINCIA', 'COMUNE');


-- elenco delle regioni
create table FLGENTE_REGIONE_TAB (
	ID_REGIONE int not null,
	CODI_ISTAT varchar not null,
	constraint FLGENTE_REGIONE_PK
		primary key (ID_REGIONE),
	constraint FLGENTE_REGIONE_UNQ_ISTAT
		unique (CODI_ISTAT),
	constraint FLGENTE_REGIONE_FK_ENTE
		foreign key (ID_REGIONE)
		references FLGENTE_TERR_TAB
);

insert into FLGENTE_REGIONE_TAB(ID_REGIONE, CODI_ISTAT)
select ER.ID_ENTE, R.CODI_REGI
from FLGREGI_TAB as R
	join FLGENTE_ROOT_TAB as ER on (ER.NOME_ENTE = R.DESC_REGI and ER.TIPO_ENTE = 'REGIONE');


-- elenco delle provincie
create table FLGENTE_PROVINCIA_TAB (
	ID_PROVINCIA int not null,
	ID_REGIONE int not null,
	CODI_ISTAT varchar not null,
	constraint FLGENTE_PROVINCIA_PK
		primary key (ID_PROVINCIA),
	constraint FLGENTE_PROVINCIA_UNQ_ISTAT
		unique (CODI_ISTAT),
	constraint FLGENTE_PROVINCIA_FK_ENTE
		foreign key (ID_PROVINCIA)
		references FLGENTE_TERR_TAB,
	constraint FLGENTE_PROVINCIA_FK_REGIONE
		foreign key (ID_REGIONE)
		references FLGENTE_REGIONE_TAB
);

insert into FLGENTE_PROVINCIA_TAB(ID_PROVINCIA, ID_REGIONE, CODI_ISTAT)
select ER.ID_ENTE, R.ID_REGIONE, P.CODI_PROV
from FLGPROV_TAB as P
	join FLGENTE_ROOT_TAB as ER on (ER.NOME_ENTE = P.DESC_PROV and ER.TIPO_ENTE = 'PROVINCIA')
	join FLGENTE_REGIONE_TAB as R on(R.CODI_ISTAT = P.CODI_REGI);

create index FLGENTE_PROVINCIA_IDX_REGIONE on FLGENTE_PROVINCIA_TAB(ID_REGIONE);

-- elenco dei comuni
create table FLGENTE_COMUNE_TAB (
	ID_COMUNE int not null,
	ID_PROVINCIA int not null,
	CODI_ISTAT varchar not null,
	constraint FLGENTE_COMUNE_PK
		primary key (ID_COMUNE),
	constraint FLGENTE_COMUNE_UNQ_ISTAT
		unique (CODI_ISTAT),
	constraint FLGENTE_COMUNE_FK_ENTE
		foreign key (ID_COMUNE)
		references FLGENTE_TERR_TAB,
	constraint FLGENTE_COMUNE_FK_PROV
		foreign key (ID_PROVINCIA)
		references FLGENTE_PROVINCIA_TAB
);

insert into FLGENTE_COMUNE_TAB(ID_COMUNE, ID_PROVINCIA, CODI_ISTAT)
select ER.ID_ENTE, EP.ID_PROVINCIA, C.CODI_COMU
from FLGCOMU_TAB as C
	join FLGPROV_TAB as P on (P.ID_PROV = C.ID_PROV)
	join FLGENTE_ROOT_TAB as ER on (ER.NOME_ENTE = C.DESC_COMU and ER.TIPO_ENTE = 'COMUNE')
	join FLGENTE_PROVINCIA_TAB as EP on(EP.CODI_ISTAT = P.CODI_PROV);


create index FLGENTE_COMUNE_IDX_PROVINCIA on FLGENTE_COMUNE_TAB(ID_PROVINCIA);


create view FLGCOMU_VIW as
select ID_COMUNE, CODI_ISTAT as CODI_ISTAT_COMUNE, NOME_ENTE as COMUNE, ID_PROVINCIA, DATA_INIZ_VALI, DATA_FINE_VALI 
from FLGENTE_COMUNE_TAB
	join FLGENTE_ROOT_TAB on (ID_ENTE = ID_COMUNE);


create view FLGPROV_VIW as
select ID_PROVINCIA, CODI_ISTAT as CODI_ISTAT_PROVINCIA, NOME_ENTE as PROVINCIA, ID_REGIONE, DATA_INIZ_VALI, DATA_FINE_VALI 
from FLGENTE_PROVINCIA_TAB
	join FLGENTE_ROOT_TAB on (ID_ENTE = ID_PROVINCIA);


create view FLGREGI_VIW as
select ID_REGIONE, CODI_ISTAT as CODI_ISTAT_REGIONE, NOME_ENTE as REGIONE, DATA_INIZ_VALI, DATA_FINE_VALI 
from FLGENTE_REGIONE_TAB
	join FLGENTE_ROOT_TAB on (ID_ENTE = ID_REGIONE);




------- 
--- In FLGPROF_TAB andrebbe mantenuto soltanto un profilo da professionista (poi indicazioni junior/senior ed iscrizioni all'albo verrebbero gestite a parte)
--  Anche perché si dovrebbe impedire che un utente sia contemporaneamente junior e senior
-------

delete from FLGPROF_TAB where ID_PROFILO in (3, 4);

update FLGPROF_TAB
set DESCRIZIONE = 'Professionista forestale'
where ID_PROFILO = 2;

update FLGPROF_TAB
set DESCRIZIONE = 'Operatore di sportello'
where ID_PROFILO = 8;

alter table FLGPROF_TAB add column TIPO_AMBITO varchar;


alter table FLGPROF_TAB add column TIPO_AUTH varchar;

alter table FLGPROF_TAB
	add constraint FLGPROF_CK_TIPO_AUTH
	check (
		TIPO_AUTH in (
			'PROP',
			'PROF',
			'ISTR',
			'DIRI',
			'SORV',
			'RESP',
			'AMMI'
		)
	);

alter table FLGPROF_TAB
	add constraint FLGPROF_UNQ_AUTH_AMB
	unique(TIPO_AUTH, TIPO_AMBITO);

update FLGPROF_TAB
set FLAG_REGIONALE = 0;

alter table FLGPROF_TAB drop column FLAG_REGIONALE;


update FLGPROF_TAB
set TIPO_AMBITO = 'TERRITORIALE'
where ID_PROFILO in (
		6, 7, 8, 11
	);

update FLGPROF_TAB
set TIPO_AMBITO = 'CASERMA'
where ID_PROFILO = 9;

update FLGPROF_TAB
set TIPO_AMBITO = 'PARCO'
where ID_PROFILO = 10;

update FLGPROF_TAB
set TIPO_AMBITO = 'GENERICO'
where TIPO_AMBITO is null;


ALTER TABLE FLGPROF_TAB ADD
	CONSTRAINT FLGPROF_CK_TIPO_AMBITO CHECK (
		TIPO_AMBITO in (
			'CASERMA', 'PARCO', 'TERRITORIALE', 'GENERICO'
		)
	);

alter table FLGPROF_TAB alter column TIPO_AMBITO set NOT NULL;


update FLGPROF_TAB
set TIPO_AUTH = case ID_PROFILO
	when 1 then 'PROP'
	when 2 then 'PROF'
	when 6 then 'ISTR'
	when 7 then 'DIRI'
	when 8 then 'PROF'
	when 9 then 'SORV'
	when 10 then 'SORV'
	when 11 then 'RESP'
	when 12 then 'AMMI'
end;

alter table FLGPROF_TAB alter column TIPO_AUTH set NOT NULL;

alter table FLGPROF_TAB ADD
	constraint FLGPROF_UNQ_AUTH_AMBITO 
	unique (TIPO_AUTH, TIPO_AMBITO);


INSERT INTO foliage2.flgprof_tab (
	id_profilo, descrizione, tipo_auth,
	TIPO_AMBITO
)
VALUES(
	13, 'Responsabile Carabinieri', 'RESP',
	'CASERMA', true
);
INSERT INTO foliage2.flgprof_tab (
	id_profilo, descrizione, tipo_auth,
	TIPO_AMBITO
)
VALUES(
	14, 'Responsabile Parco', 'RESP',
	'PARCO', true
);
-- aggiunta colonna pec alla tabella degli utenti
alter table FLGUTEN_TAB add pec varchar;

-- profili degli utenti
create table FLGPROFILI_UTENTE_TAB(
	ID_UTENTE int not null,
	ID_PROFILO int not null,
	FLAG_DEFAULT boolean,
	FLAG_SENIOR boolean default false not null,
	constraint FLGPROFILI_UTENTE_PK
		primary key (ID_UTENTE, ID_PROFILO),
	constraint FLGPROFILI_UTENTE_FK_UTENTE
		foreign key (ID_UTENTE)
		references FLGUTEN_TAB,
	constraint FLGPROFILI_UTENTE_FK_PROFILO
		foreign key (ID_PROFILO)
		references FLGPROF_TAB,
	constraint FLGPROFILI_UTENTE_CK_DEF
		check (FLAG_DEFAULT is null or FLAG_DEFAULT = true),
	constraint FLGPROFILI_UTENTE_UNQ_DEF
		unique(ID_UTENTE, FLAG_DEFAULT)
);

insert into FLGPROFILI_UTENTE_TAB(ID_UTENTE, ID_PROFILO, FLAG_DEFAULT)
select distinct ID_UTEN, ID_PROFILO, nullif(FLAG_DEFAULT, 0)::boolean, false
from FLGUTRU_TAB;

ALTER TABLE foliage2.flguten_tab ALTER COLUMN data_ini_vali DROP NOT NULL;


-- tabella delle autocertificazioni da professionista forestale
create table FLGAUTOCERT_PROF_TAB (
	ID_UTENTE int not null,
	CATEGORIA varchar not null,
	SOTTOCATEGORIA varchar,
	COLLEGGIO varchar,
	NUMERO_ISCRIZIONE varchar,
	ID_PROVINCIA_ISCRIZIONE int,
	DATA_INSERIMENTO timestamp without time zone not null,
	DATA_ANNULLAMENTO timestamp without time zone,
	FLAG_VALIDO boolean GENERATED ALWAYS AS (case when DATA_ANNULLAMENTO is null then true else null end) STORED,
	constraint FLGAUTOCERT_PROF_UNQ_VAL
		unique (ID_UTENTE, FLAG_VALIDO),
	constraint FLGAUTOCERT_PROF_FK_PROV
		foreign key(ID_PROVINCIA_ISCRIZIONE)
		references FLGENTE_PROVINCIA_TAB,
	constraint FLGAUTOCERT_PROF_FK_UTE
		foreign key(ID_UTENTE)
		references FLGUTEN_TAB,
	constraint FLGAUTOCERT_PROF_CK_SOTTOC_COLL
		check (
			(CATEGORIA = 'ordineProfessionale' and SOTTOCATEGORIA in ('junior', 'senior') )
			or (CATEGORIA = 'collegio' and COLLEGGIO in ('laureati', 'nonLaureati'))
		)
);

create index FLGAUTOCERT_PROF_IDX_UTE on FLGAUTOCERT_PROF_TAB (ID_UTENTE);
create index FLGAUTOCERT_PROF_IDX_PROV on FLGAUTOCERT_PROF_TAB (ID_PROVINCIA_ISCRIZIONE);

-- tabella che gestise i dettagli degli utenti con profilo professionita
create table FLGUTE_PROFESSIONISTI_TAB(
	ID_UTENTE int not null,
	ID_PROFILO int not null,
	IS_SENIOR boolean not null,
	constraint FLGUTE_PROFESSIONISTI_PK
		primary key (ID_UTENTE, ID_PROFILO),
	constraint FLGUTE_PROFESSIONISTI_CK_PROFILO
		check (ID_PROFILO = 2),
	constraint FLGUTE_PROFESSIONISTI_FK_UTE_PROF
		foreign key (ID_UTENTE, ID_PROFILO)
		references FLGPROFILI_UTENTE_TAB
);

-- tabella delle notifiche
create table FLGNOTIFICHE_TAB (
	ID_NOTIFICA int not null GENERATED ALWAYS AS IDENTITY,
	ID_UTENTE int not null,
	TESTO varchar not null,
	LINK varchar not null,
	DATA_NOTIFICA timestamp without time zone not null,
	DATA_LETTURA timestamp without time zone,
	FLAG_LETTA boolean GENERATED ALWAYS AS (DATA_LETTURA is not null) STORED,
	constraint FLGNOTIFICHE_PK
		primary key(ID_NOTIFICA)
);

create index FLGNOTIFICHE_IDX_ORD on FLGNOTIFICHE_TAB(ID_UTENTE, FLAG_LETTA desc, DATA_NOTIFICA);


-- tabella per l'abilitazione alla creazione delle istanze a seconda del profilo
create table FLGPROFILI_CIST_TAB (
	ID_PROFILO int not null,
	ID_CIST int not null,
	FLAG_JUNIOR_SENIOR int,
	constraint FLGPROFILI_CIST_PK
		primary key(ID_PROFILO, ID_CIST),
	constraint FLGPROFILI_CIST_FK_PROFILO
		foreign key (ID_PROFILO)
		references FLGPROF_TAB
	constraint FLGPROFILI_CIST_FK_CIST
		foreign key (ID_CIST)
		references FLGCIST_TAB
);


---- eliminazione differenziazione dei tipi di istanza per regione
-- variazione FK verso quelle del Lazio
-- eliminazione record Umbria
-- eliminazione colonna CODI_REGI

update FLGISTA_TAB TT
set ID_CIST = (
		select ID_TO
		from (
				select ID_CIST as ID_FROM, CI.DESC_CIST
				from FLGCIST_TAB CI
				where CODI_REGI = '10'
			) as T
			join (
				select ID_CIST as ID_TO, CI.DESC_CIST
				from FLGCIST_TAB CI
				where CODI_REGI = '12'
			) as T1 using (DESC_CIST)
		where T.ID_FROM = TT.ID_CIST
	)
where ID_CIST in (
		select ID_CIST
		from FLGCIST_TAB CI
		where CODI_REGI = '10'
	);


delete from FLGCIST_TAB
where CODI_REGI = '10';

alter table FLGCIST_TAB drop column CODI_REGI;


----
-- Aggiunta abilitazione necessaria oltre a nome e descrizione da visualizzare sul front end per i tipi istanza

alter table FLGCIST_TAB add column NOME varchar;
alter table FLGCIST_TAB add column DESCRIZIONE_LUNGA varchar;

alter table FLGCIST_TAB add constraint FLGCISTA_UNQ_NOME unique (NOME);


create table FLGABILITAZIONI_TAB (
	TIPO_AUTH varchar not null,
	constraint FLGABILITAZIONI_PK
		primary key(TIPO_AUTH)
);

insert into FLGABILITAZIONI_TAB
values('PROP'),
	('PROF'),
	('ISTR'),
	('DIRI'),
	('RESP'),
	('SORV'),
	('AMMI');

alter table FLGCIST_TAB add
	column TIPO_AUTH varchar;
alter table FLGCIST_TAB add
	constraint FLGCIST_FK_TIPO_AUTH
		foreign key(TIPO_AUTH)
		references FLGABILITAZIONI_TAB;
alter table FLGCIST_TAB add
	column FLAG_SENIOR boolean default false not null;

alter table FLGCIST_TAB add
	column COD_TIPO_ISTANZA varchar;

alter table FLGCIST_TAB add
	column TIPO_ENTE varchar check (TIPO_ENTE in ('COMUNE', 'REGIONE', 'PROVINCIA'));

update FLGCIST_TAB
set COD_TIPO_ISTANZA = 'SOTTO_SOGLIA',
	TIPO_ENTE = 'COMUNE',
	NOME = 'Istanza sotto soglia e altri interventi a comunicazione',
	TIPO_AUTH = null,
	DESCRIZIONE_LUNGA = 'Richiesta semplificata di taglio boschivo o di altro intervento previsto a semplice comunicazione che non richiede elaborati tecnico-professionali'
where DESC_CIST = 'Istanze sotto soglia';

update FLGCIST_TAB
set COD_TIPO_ISTANZA = 'SOPRA_SOGLIA',
	TIPO_ENTE = 'PROVINCIA',
	NOME = 'Istanza sopra soglia',
	TIPO_AUTH = 'PROF',
	DESCRIZIONE_LUNGA = 'Richiesta di taglio boschivo che richiede elaborati tecnico-professionali e viene presentata all''ente competente in regime di autorizzazione con silenzio-assenso dopo un termine di tempo stabilito dalle norme vigenti'
where DESC_CIST = 'Istanze sopra soglia';

update FLGCIST_TAB
set COD_TIPO_ISTANZA = 'ATTUAZIONE_PIANI',
	TIPO_ENTE = 'PROVINCIA',
	NOME = 'Istanza di attuazione dei piani di gestione forestale (PGF) e strumenti equivalenti',
	TIPO_AUTH = 'PROF',
	FLAG_SENIOR = true,
	DESCRIZIONE_LUNGA = 'Richiesta di taglio di un soprassuolo forestale per il quale esiste un PGF, o strumento equivalente, approvato e in corso di validità'
where DESC_CIST = 'Istanze di progetti in attuazione dei piano di gestione forestali';

update FLGCIST_TAB
set COD_TIPO_ISTANZA = 'IN_DEROGA',
	TIPO_ENTE = 'PROVINCIA',
	NOME = 'Istanza in deroga',
	TIPO_AUTH = 'PROF',
	FLAG_SENIOR = true,
	DESCRIZIONE_LUNGA = 'Richiesta di taglio boschivo che richiede elaborati tecnico-professionali e viene presentata all''ente competente in regime di richiesta di autorizzazione'
where DESC_CIST = 'Istanze di progetti in deroga';


alter table FLGCIST_TAB alter
	column COD_TIPO_ISTANZA set not null;

alter table FLGCIST_TAB alter
	column TIPO_ENTE set not null;


alter table FLGCIST_TAB ALTER column NOME set not null;
alter table FLGCIST_TAB ALTER column DESCRIZIONE_LUNGA set not null;


alter table FLGISTA_TAB add column ID_UTENTE_COMPILAZIONE int;
alter table FLGISTA_TAB add column ID_ENTE_TERR int;
alter table FLGISTA_TAB add constraint FLGISTA_FK_USER_COMPILAZIONE
	foreign key (ID_UTENTE_COMPILAZIONE)
	references FLGUTEN_TAB;
alter table FLGISTA_TAB add constraint FLGISTA_FK_ENTE_TERR
	foreign key (ID_ENTE_TERR)
	references FLGENTE_TERR_TAB;


/*
	FLAG_VALIDO boolean GENERATED ALWAYS AS (case when DATA_ANNULLAMENTO is null then true else null end) STORED,
	constraint FLGAUTOCERT_PROF_UNQ_VAL
		unique (ID_UTENTE, FLAG_VALIDO),

*/



-- tabella con le richieste per Istruttori, Dirigenti e Responsabili degli enti territoriali
create table FLGRICHIESTE_PROFILI_TAB (
	ID_RICHIESTA int not null GENERATED ALWAYS AS IDENTITY,
	ID_UTENTE int not null,
	ID_PROFILO_RICHIESTO int not null,
	ID_ENTE int not null,
	DATA_RICHIESTA timestamp without time zone not null,
	ESITO_APPROVAZIONE boolean,
	ID_UTENTE_APPROVAZIONE int,
	ID_UTENTE_REVOCA int,
	DATA_ANNULLAMENTO timestamp without time zone,
	DATA_APPROVAZIONE timestamp without time zone,
	DATA_REVOCA timestamp without time zone,
	NOTE_RICHIESTA varchar,
	NOTE_APPROVAZIONE varchar,
	NOTE_REVOCA varchar,
	FLAG_RICHIESTA_VALIDA boolean GENERATED ALWAYS AS (
		case 
			when DATA_ANNULLAMENTO is null 
					and DATA_REVOCA is null
					and (ESITO_APPROVAZIONE is null or ESITO_APPROVAZIONE) 
				then true 
			else null 
		end) STORED,
	constraint FLGRICHIESTE_PROFILI_PK
		primary  key (ID_RICHIESTA),
	constraint FLGRICHIESTE_PROFILI_UNQ_RIC
		unique (ID_UTENTE, ID_PROFILO_RICHIESTO, ID_ENTE, FLAG_RICHIESTA_VALIDA),
	constraint FLGRICHIESTE_PROFILI_FK_UTE
		foreign key (ID_UTENTE)
		references FLGUTEN_TAB,
	constraint FLGRICHIESTE_PROFILI_FK_PROF
		foreign key (ID_PROFILO_RICHIESTO)
		references FLGPROF_TAB,
	constraint FLGRICHIESTE_PROFILI_FK_ENTE
		foreign key (ID_ENTE)
		references FLGENTE_ROOT_TAB,
	constraint FLGRICHIESTE_PROFILI_FK_UTE_APPROV
		foreign key (ID_UTENTE_APPROVAZIONE)
		references FLGUTEN_TAB,
	constraint FLGRICHIESTE_PROFILI_CK_ANNUL
		check(
			(DATA_ANNULLAMENTO is null or DATA_APPROVAZIONE is not null) --puo' esserci un annullamento solo prima dell'approvazione
		),
	constraint FLGRICHIESTE_PROFILI_CK_REVOCA
		check(
			(DATA_REVOCA is null or DATA_APPROVAZIONE < DATA_REVOCA) --puo' esserci una revoca solo dopo l'approvazione
		)
);

create index FLGRICHIESTE_PROFILI_IDX_UTE on FLGRICHIESTE_PROFILI_TAB(ID_UTENTE);
create index FLGRICHIESTE_PROFILI_IDX_PROF on FLGRICHIESTE_PROFILI_TAB(ID_PROFILO_RICHIESTO);
create index FLGRICHIESTE_PROFILI_IDX_ENTE on FLGRICHIESTE_PROFILI_TAB(ID_ENTE);
create index FLGRICHIESTE_PROFILI_IDX_UTE_APPR on FLGRICHIESTE_PROFILI_TAB(ID_UTENTE_APPROVAZIONE);



-- abilitazioni per i profili degli utenti
-- qui andrebbero le associazioni di istruttori/dirigenti/responsabili agli enti per cui lavorano
create table FLGENTI_PROFILO_TAB (
	ID_UTENTE int not null,
	ID_PROFILO int not null,
	ID_ENTE int not null,
	FLAG_RICHIESTA_VALIDA boolean GENERATED ALWAYS AS (true) STORED,
	constraint FLGENTI_PROFILO_PK
		primary key (ID_UTENTE, ID_PROFILO, ID_ENTE),
	constraint FLGENTI_PROFILO_FK_RICH_VAL
		foreign key(ID_UTENTE, ID_PROFILO, ID_ENTE, FLAG_RICHIESTA_VALIDA)
		references FLGRICHIESTE_PROFILI_TAB(ID_UTENTE, ID_PROFILO_RICHIESTO, ID_ENTE, FLAG_RICHIESTA_VALIDA)
);

create index FLGENTI_PROFILO_IDX_ENTE on FLGENTI_PROFILO_TAB(ID_ENTE);


-- tabella con i file caricati da formio in base 64
create table FLGBASE64_FORMIO_FILE_MASTER_TAB (
	ID_FILE int not null GENERATED ALWAYS AS IDENTITY,
	DATA_CARICAMENTO timestamp without time zone not null,
	constraint FLGBASE64_FORMIO_FILE_MASTER_PK
		primary key (ID_FILE)
);

create table FLGBASE64_FORMIO_FILE_TAB (
	ID_FILE int not null,
	PROG_FILE int not null,
	FILE_NAME varchar not null,
	ORIGINAL_FILE_NAME varchar not null,
	FILE_SIZE int not null,
	STORAGE varchar not null,
	FILE_TYPE varchar not null,
	HASH_FILE varchar not null,
	FILE_DATA bytea,
	constraint FLGBASE64_FORMIO_FILE_PK
		primary key (ID_FILE, PROG_FILE),
	constraint FLGBASE64_FORMIO_FILE_FK_MASTER
		foreign key (ID_FILE)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB
		on delete cascade
);

-- tabella i documenti necessari delle richieste per Responsabili degli enti territoriali
create table FLGRICHIESTE_RESPONSABILE_TAB (
	ID_RICHIESTA int not null,
	TIPO_NOMINA varchar not null,
	NUMERO_PROTOCOLLO varchar not null,
	DATA_PROTOCOLLO date not null,
	ID_FILE_ATTO_NOMINA int not null,
	ID_FILE_DOC_IDENTITA int not null,
	constraint FLGRICHIESTE_RESPONSABILE_PK
		primary key(ID_RICHIESTA),
	constraint FLGRICHIESTE_RESPONSABILE_FK_RICH
		foreign key (ID_RICHIESTA)
		references FLGRICHIESTE_PROFILI_TAB,
	constraint FLGRICHIESTE_RESPONSABILE_FK_ATTO
		foreign key (ID_FILE_ATTO_NOMINA)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB,
	constraint FLGRICHIESTE_RESPONSABILE_FK_DOC
		foreign key (ID_FILE_DOC_IDENTITA)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB
);

-- sostituisce la tabella e la relazione dei soggetti alle istanze
create table FLGTITOLARE_ISTANZA_TAB (
	ID_TITOLARE int not null GENERATED ALWAYS AS IDENTITY,
	CODICE_FISCALE varchar not null,
	COGNOME varchar not null,
	NOME varchar not null,
	DATA_NASCITA date not null,
	LUOGO_NASCITA varchar not null,
	--GENERE varchar not null check (GENERE in ('M', 'F')),
	EMAIL varchar not null,
	PEC varchar not null,
	ID_FILE_DELEGA int,
	constraint FLGTITOLARE_ISTANZA_PK
		primary key (ID_TITOLARE),
	constraint FLGTITOLARE_ISTANZA_FK_FILE_DELEGA
		foreign key(ID_FILE_DELEGA)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB
);

create index FLGTITOLARE_ISTANZA_IDX_CODI_FISC on FLGTITOLARE_ISTANZA_TAB(CODICE_FISCALE);

alter table FLGISTA_TAB alter column CODI_REGI drop not null;
alter table FLGISTA_TAB add ID_TITOLARE int;
alter table FLGISTA_TAB add
	constraint FLGISTA_FK
		foreign key(ID_TITOLARE)
		references FLGTITOLARE_ISTANZA_TAB;
	
create index FLGISTA_IDX_TITO on FLGISTA_TAB(ID_TITOLARE);


-- popolamento tabella titolare dalla tabella soggetto
do
$$
declare
	ss record;
begin
	for ss in (
		select isg.id_ista, s.codi_fisc, s.cognome, s.nome,
			s.data_nascita, s.luogo_nascita,
			s.email, s.pec
		from flgisso_tab isg
			join flgsogg_tab s on (s.id_sogg = isg.id_sogg)
		where tipo_sogg = 1
			and exists (
				select *
				from flgista_tab i
				where i.id_ista = isg.id_ista 
			)
	) loop
		declare
			idIsta int := ss.id_ista;
			codiFisc varchar := ss.codi_fisc;
			cognome varchar := ss.cognome;
			nome varchar := ss.nome;
			dataNascita date := ss.data_nascita;
			luogoNascita varchar := ss.luogo_nascita;
			email varchar := ss.email;
			pec varchar := ss.pec;
			idTitolare int;
		begin
			if (dataNascita is null) then
				dataNascita := date'1900-01-01';
			end if;
			if (luogoNascita is null) then
				luogoNascita := 'N.D.';
			end if;
			if (email is null) then
				email := 'email@prova.it';
			end if;
			if (pec is null) then
				pec := 'email@prova.it';
			end if;
			insert into FLGTITOLARE_ISTANZA_TAB(
				CODICE_FISCALE, COGNOME, NOME,
				DATA_NASCITA, LUOGO_NASCITA, --GENERE,
				EMAIL, PEC
			) 
			values (
				codiFisc, cognome, nome,
				dataNascita, luogoNascita, --'M',
				email, pec
			)
			returning ID_TITOLARE into idTitolare;
		
			update FLGISTA_TAB
			set ID_TITOLARE = idTitolare
			where ID_ISTA = idIsta;
		end;
	end loop;
end;
$$	



create table FLGTIPO_ISTANZA_TAB (
	ID_TIPO_ISTANZA int not null GENERATED ALWAYS AS IDENTITY,
	ID_CIST int not null,
	COD_TIPO_ISTANZA_SPECIFICO varchar not null,
	NOME_ISTANZA_SPECIFICO varchar not null,
	constraint FLGTIPO_ISTANZA_PK
		primary key (ID_TIPO_ISTANZA),
	constraint FLGTIPO_ISTANZA_UNQ
		unique (COD_TIPO_ISTANZA_SPECIFICO),
	constraint FLGTIPO_ISTANZA_FK_TIPO
		foreign key (ID_CIST)
		references FLGCIST_TAB
);
create index FLGTIPO_ISTANZA_IDX_CIST on FLGTIPO_ISTANZA_TAB(ID_CIST);

insert into FLGTIPO_ISTANZA_TAB(ID_CIST, COD_TIPO_ISTANZA_SPECIFICO, NOME_ISTANZA_SPECIFICO)
select ID_CIST, COD_TIPO_ISTANZA, NOME
from FLGCIST_TAB
where COD_TIPO_ISTANZA != 'SOTTO_SOGLIA'
union all
select ID_CIST, T.NOME, T.DESCRIZIONE
from FLGCIST_TAB
	cross join (
		values('TAGLIO_BOSCHIVO', 'Istanza di taglio boschivo'),
			('INTERVENTO_A_COMUNICAZIONE', 'Intervento a comunicazione diverso da istanza di taglio boschivo (arboricoltura da legno, tartufaie, castagneti da frutto, interventi accessori, etc. previsti dalla norma regionale)')
	) as T(NOME, DESCRIZIONE)
where COD_TIPO_ISTANZA = 'SOTTO_SOGLIA';

alter table FLGISTA_TAB add ID_TIPO_ISTANZA int;
alter table FLGISTA_TAB add
	constraint FLGISTA_FK_TIPO 
		foreign key (ID_TIPO_ISTANZA)
		references FLGTIPO_ISTANZA_TAB;

update FLGISTA_TAB I
set ID_TIPO_ISTANZA =  (
		select min(TI.ID_TIPO_ISTANZA)
		from FLGTIPO_ISTANZA_TAB  TI
		where TI.ID_CIST = I.ID_CIST
	);
alter table FLGISTA_TAB alter ID_TIPO_ISTANZA set not null;
alter table FLGISTA_TAB alter ID_CIST drop not null;



---------------------------
-- eliminazione dati altre regioni


delete
from flgpian_tab
where id_asag in (
		select a.id_asag
		from flgasag_tab a
		where id_uogt in (
				select u.id_uogt
				from flguogt_tab u
				where id_pfor in (
						select p.id_pfor
						from flgpfor_tab p
						where id_ista in (
								select ft.id_ista
								from flgista_tab ft
								where codi_regi != '12'
							)
					)
			)
	);

delete
from flgpoll_tab
where id_asag in (
		select a.id_asag
		from flgasag_tab a
		where id_uogt in (
				select u.id_uogt
				from flguogt_tab u
				where id_pfor in (
						select p.id_pfor
						from flgpfor_tab p
						where id_ista in (
								select ft.id_ista
								from flgista_tab ft
								where codi_regi != '12'
							)
					)
			)
	);

delete
from flgmatr_tab
where id_asag in (
		select a.id_asag
		from flgasag_tab a
		where id_uogt in (
				select u.id_uogt
				from flguogt_tab u
				where id_pfor in (
						select p.id_pfor
						from flgpfor_tab p
						where id_ista in (
								select ft.id_ista
								from flgista_tab ft
								where codi_regi != '12'
							)
					)
			)
	);

delete
from flgassp_tab
where id_asag in (
		select a.id_asag
		from flgasag_tab a
		where id_uogt in (
				select u.id_uogt
				from flguogt_tab u
				where id_pfor in (
						select p.id_pfor
						from flgpfor_tab p
						where id_ista in (
								select ft.id_ista
								from flgista_tab ft
								where codi_regi != '12'
							)
					)
			)
	);

delete
from flgasag_tab
where id_uogt in (
		select u.id_uogt
		from flguogt_tab u
		where id_pfor in (
				select p.id_pfor
				from flgpfor_tab p
				where id_ista in (
						select ft.id_ista
						from flgista_tab ft
						where codi_regi != '12'
					)
			)
	);

delete
from flguosp_tab
where id_uogt in (
		select u.id_uogt
		from flguogt_tab u
		where id_pfor in (
				select p.id_pfor
				from flgpfor_tab p
				where id_ista in (
						select ft.id_ista
						from flgista_tab ft
						where codi_regi != '12'
					)
			)
	);

delete
from flguogt_tab
where id_pfor in (
		select p.id_pfor
		from flgpfor_tab p
		where id_ista in (
				select ft.id_ista
				from flgista_tab ft
				where codi_regi != '12'
			)
	);

delete
from flgpfpc_tab
where id_pfor in (
		select p.id_pfor
		from flgpfor_tab p
		where id_ista in (
				select ft.id_ista
				from flgista_tab ft
				where codi_regi != '12'
			)
	);

delete
from flgpfor_tab
where id_ista in (
		select ft.id_ista
		from flgista_tab ft
		where codi_regi != '12'
	);

delete
from flgisso_tab
where id_ista in (
		select ft.id_ista
		from flgista_tab ft
		where codi_regi != '12'
	);

delete
from flgispa_tab
where id_ista in (
		select ft.id_ista
		from flgista_tab ft
		where codi_regi != '12'
	);

delete 
from flgisdo_tab
where id_ista in (
		select ft.id_ista
		from flgista_tab ft
		where codi_regi != '12'
	);

delete
from flgispr_tab
where id_ista in (
		select ft.id_ista
		from flgista_tab ft
		where codi_regi != '12'
	);

delete
from flgissi_tab
where id_ista in (
		select ft.id_ista
		from flgista_tab ft
		where codi_regi != '12'
	);

delete from flgista_tab ft 
where codi_regi != '12';

delete from flgtprp_tab
where codi_regi != '12';

delete from flgnprp_tab
where codi_regi != '12';

delete from flgmint_tab
where codi_regi != '12';

delete from flgprod_tab
where codi_regi != '12';

delete
from flgqall_tab
where id_qual in (
		select q.id_qual
		from flgqual_tab q
		where codi_regi != '12'
	);

delete 
from flgisdo_tab
where id_ista in (
		select i.id_ista
		from flgista_tab i
		where id_qual in (
				select q.id_qual
				from flgqual_tab q
				where q.codi_regi != '12'
			)
	);

delete 
from flgispa_tab
where id_ista in (
		select i.id_ista
		from flgista_tab i
		where id_qual in (
				select q.id_qual
				from flgqual_tab q
				where q.codi_regi != '12'
			)
	);


delete 
from flgisso_tab
where id_ista in (
		select i.id_ista
		from flgista_tab i
		where id_qual in (
				select q.id_qual
				from flgqual_tab q
				where q.codi_regi != '12'
			)
	);

delete
from flgista_tab
where id_qual in (
		select q.id_qual
		from flgqual_tab q
		where q.codi_regi != '12'
	);

delete from flgqual_tab
where codi_regi != '12';

delete
from flgassp_tab
where id_spec in (
		select s.id_spec
		from flgspec_tab s
		where codi_regi != '12'
	);

delete
from flgmatr_tab
where id_spec in (
		select s.id_spec
		from flgspec_tab s
		where codi_regi != '12'
	);

delete
from flgpoll_tab
where id_spec in (
		select s.id_spec
		from flgspec_tab s
		where codi_regi != '12'
	);

delete
from flguosp_tab
where id_spec in (
		select s.id_spec
		from flgspec_tab s
		where codi_regi != '12'
	);

delete
from flgpian_tab
where id_spec in (
		select s.id_spec
		from flgspec_tab s
		where codi_regi != '12'
	);

delete from flgspec_tab
where codi_regi != '12';

delete from flgsspr_tab
where codi_regi != '12';

delete from flgtazi_tab
where codi_regi != '12';

delete
from flggotr_tab
where id_trat in (
		select t.id_trat
		from flgtrat_tab t
		where codi_regi != '12'
	);

delete from flgtrat_tab
where codi_regi != '12';

delete from flgtspr_tab
where codi_regi != '12';

delete from flgmatr_tab;
delete from flgpian_tab;
delete from flgpoll_tab;
delete from flgassp_tab;
delete from flgasag_tab;
delete from flguosp_tab;
delete from flguogt_tab;
delete from flggest_tab;
delete
from FLGGOVE_TAB
where desc_gove = 'Misto';



create table FLGPART_CATASTALI_TAB (
	ID_PART_CATA int not null GENERATED ALWAYS AS IDENTITY,
	ID_ISTA int not null,
	ID_COMUNE int not null,
	SEZIONE varchar not null,
	FOGLIO int not null,
	PARTICELLA varchar not null,
	SUB varchar not null,
	SUPERFICIE int not null check (SUPERFICIE >= 0),
	constraint FLGPART_CATASTALI_PK
		primary key(ID_PART_CATA),
	constraint FLGPART_CATASTALI_FK_ISTA
		foreign key(ID_ISTA)
		references FLGISTA_TAB
		on delete cascade,
	constraint FLGPART_CATASTALI_FK_COMUNE
		foreign key(ID_COMUNE)
		references FLGENTE_COMUNE_TAB
);

create index FLGPART_CATASTALI_IDX_ISTA on FLGPART_CATASTALI_TAB(ID_ISTA);
create index FLGPART_CATASTALI_IDX_COMUNE on FLGPART_CATASTALI_TAB(ID_COMUNE);


insert into FLGPART_CATASTALI_TAB(
	ID_ISTA, ID_COMUNE,
	SEZIONE, FOGLIO, PARTICELLA, SUB,
	SUPERFICIE
)
select ft.id_ista , ft3.id_comune, 
	ft2.codi_sezi, ft2.foglio, ft2.particella, '',
	coalesce(ft2.supe_cata, 0) as supe_cata
from foliage2.flgispa_tab ft 
	join foliage2.flgpart_tab ft2 using (id_part)
	join flgcomu_viw ft3 on (ft3.codi_istat_comune = ft2.codi_comu);



create table FLGLIMITI_AMMINISTRATIVI_TAB (
	ID_ENTE_TERR int not null,
	SHAPE_VINC geometry not null,
	SRID int not null,
	SHAPE_ENVELOPE_VINC geometry not null,
	constraint FLGLIMITI_AMMINISTRATIVI_PK
		primary key (ID_ENTE_TERR),
	constraint FLGLIMITI_AMMINISTRATIVI_FK_ENTE
		foreign key (ID_ENTE_TERR)
		references FLGENTE_TERR_TAB
);


create table FLGSTATO_ISTANZA_TAB (
	ID_STATO int not null,
	COD_STATO varchar not null,
	DESC_STATO varchar not null,
	constraint FLGSTATO_ISTANZA_PK
		primary key(ID_STATO),
	constraint FLGSTATO_ISTANZA_UNQ_COD
		unique(COD_STATO)
);
insert into FLGSTATO_ISTANZA_TAB(ID_STATO, COD_STATO, DESC_STATO)
	values (1, 'COMPILAZIONE', 'In Compilazione'), -- dopo la creazione
		(2, 'PRESENTATA', 'Presentata'), -- dopo l'invio
		(3, 'ISTRUTTORIA', 'In Valutazione'), -- dopo l'assegnazione dell'istruttore
		(4, 'APPROVATA', 'Approvata'), -- dopo la valutazione di approvazione dell'istruttore
		(5, 'RESPINTA', 'Respinta');  -- dopo la valutazione di respingimento dell'istruttore

update flgista_tab set stato = 1 where stato = 0;

alter table flgista_tab add
	constraint flgista_fk_stato
		foreign key(stato)
		references FLGSTATO_ISTANZA_TAB;


create table FLGDATE_INIZIO_LAVORI_ISTANZA_TAB (
	ID_ISTA int not null,
	DATA_INIZIO_LAVORI date not null,
	DATA_COMUNICAZIONE_INIZIO_LAVORI timestamp without time zone not null,
	ID_UTENTE_COMUNICAZIONE_INIZIO_LAVORI int not null,
	constraint FLGDATE_INIZIO_LAVORI_ISTANZA_PK
		primary key(ID_ISTA),
	constraint FLGDATE_INIZIO_LAVORI_ISTANZA_FK_ISTA
		foreign key(ID_ISTA)
		references FLGISTA_TAB
);

create table FLGDATE_FINE_LAVORI_ISTANZA_TAB (
	ID_ISTA int not null,
	DATA_FINE_LAVORI date not null,
	DATA_COMUNICAZIONE_FINE_LAVORI timestamp without time zone not null,
	ID_UTENTE_COMUNICAZIONE_FINE_LAVORI int not null,
	constraint FLGDATE_FINE_LAVORI_ISTANZA_PK
		primary key(ID_ISTA),
	constraint FLGDATE_FINE_LAVORI_ISTANZA_FK_INIZIO
		foreign key(ID_ISTA)
		references FLGDATE_INIZIO_LAVORI_ISTANZA_TAB
);


create table FLGASSEGNAZIONE_ISTANZA_TAB (
	ID_ISTA int not null,
	ID_UTENTE_ISTRUTTORE int not null,
	ID_UTENTE_ASSEGNAZIONE int not null,
	DATA_ASSEGNAZIONE timestamp without time zone not null,
	constraint FLGASSEGNAZIONE_ISTANZA_PK
		primary key (ID_ISTA),
	constraint FLGASSEGNAZIONE_ISTANZA_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB
);

create table FLGVALUTAZIONE_ISTANZA_TAB (
	ID_ISTA int not null,
	ESITO_VALUTAZIONE boolean not null,
	NOTE_VALUTAZIONE varchar not null,
	DATA_VALUTAZIONE timestamp without time zone not null,
	constraint FLGVALUTAZIONE_ISTANZA_PK
		primary key (ID_ISTA),
	constraint FLGVALUTAZIONE_ISTANZA_FK_ISTA
		foreign key (ID_ISTA)
		references FLGASSEGNAZIONE_ISTANZA_TAB
);


create table FLGFILETIPO_GESTIONE_TAB (
	ID_ISTA int not null,
	ID_FILE_AUTOCERTIFICAZIONE_PROPRIETA int not null,
	ID_FILE_DELEGA_TITOLARITA int,
	constraint FLGFILETIPO_GESTIONE_PK
		primary key (ID_ISTA),
	constraint FLGFILETIPO_GESTIONE_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB,
	constraint FLGFILETIPO_GESTIONE_FK_AUTOCERT
		foreign key (ID_FILE_AUTOCERTIFICAZIONE_PROPRIETA)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB
		on delete cascade,
	constraint FLGFILETIPO_GESTIONE_FK_DELEGA
		foreign key (ID_FILE_DELEGA_TITOLARITA)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB
		on delete cascade
);


create table FLGPARTICELLA_FORESTALE_TAB (
	ID_ISTA int not null,
	ALTIMETRIA_MIN real,
	ALTIMETRIA_MAX real,
	ALTIMETRIA_AVG real,
	PENDENZA_MIN real,
	PENDENZA_MAX real,
	PENDENZA_AVG real,
	SUPERFICIE_PFOR real,
	constraint FLGPARTICELLA_FORESTALE_PK
		primary key (ID_ISTA),
	constraint FLGPARTICELLA_FORESTALE_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB
);

create table FLGPARTICELLA_FORESTALE_SHAPE_TAB (
	ID_ISTA int not null,
	PROG_GEOM int not null,
	SUPERFICIE real,
	SHAPE geometry not null,
	constraint FLGPARTICELLA_FORESTALE_SHAPE_PK
		primary key (ID_ISTA, PROG_GEOM),
	constraint FLGPARTICELLA_FORESTALE_SHAPE_FK_PFOR
		foreign key (ID_ISTA)
		references FLGISTA_TAB
);


insert into FLGPARTICELLA_FORESTALE_SHAPE_TAB(ID_ISTA, PROG_GEOM, SUPERFICIE, SHAPE)
select id_ista, row_number() over(partition by id_ista order by id_pfor) , ST_area(shape) as superficie, shape   
from flgpfor_tab ft
order by id_ista;



delete from flggove_tab ft
where codi_regi != '12';

alter table flggove_tab add column is_fustaia boolean;
alter table flggove_tab add column is_ceduo boolean;
update flggove_tab
set is_fustaia = (lower(desc_gove) in ('misto', 'fustaia') ),
	is_ceduo = (lower(desc_gove) in ('misto', 'ceduo') );
alter table flggove_tab alter column is_fustaia set not null;
alter table flggove_tab alter column is_ceduo set not null;


-- Elenco dei vincoli
create table FLGVINCOLI_TAB (
	ID_VINCOLO int not null GENERATED ALWAYS AS IDENTITY,
	COD_VINCOLO varchar not null,
	DESC_VINCOLO varchar,
	constraint FLGVINCOLI_PK
		primary key (ID_VINCOLO),
	constraint FLGVINCOLI_UNQ_COD
		unique (COD_VINCOLO)
);


insert into foliage2.FLGVINCOLI_TAB(COD_VINCOLO, DESC_VINCOLO)
	values (
		'NAT2K', 'Natura 2000'
	),
	(
		'HABITAT_PRIOR', 'Habitat Prioritari'
	),
	(
		'PAI_FRANE', 'PAI Rischio Idrogeologico - Frane'
	),
	(
		'PAI_VALANGHE', 'PAI Rischio Idrogeologico - Valanghe'
	),
	(
		'PAI_ALLUVIONI', 'PAI Rischio Idrogeologico - Alluvioni'
	),
	(
		'AREE_PROTETTE', 'Aree Protette'
	);

create table FLGLIMITAZIONI_TAB (
	ID_LIMITAZIONE int not null GENERATED ALWAYS AS IDENTITY,
	COD_LIMITAZIONE varchar not null,
	DESC_LIMITAZIONE varchar,
	constraint FLGLIMITAZIONI_PK
		primary key (ID_LIMITAZIONE),
	constraint FLGLIMITAZIONI_UNQ_COD
		unique (COD_LIMITAZIONE)
);

insert into FLGLIMITAZIONI_TAB(COD_LIMITAZIONE, DESC_LIMITAZIONE)
	values (
		'NAT2K_4000MQ',
		'Avvisi il taglio boschivo indicato ricade nei limiti del Sito Natura 2000. gli interventi di fine turno sono ammessi per una superficie'
		|| ' non superiore a 4.000 m2 (art. 53, c. 2, lettera f)'
	),
	(
		'HABITAT_VINCA_BOSCHIVO',
		'L''area di intervento ricade nel/negli Habitat Prioritario/i. Deve essere selezionata una tipologia di intervento tra quelle espresse dal DGR Umbria'
		|| ' n. 1093 del 10/11/2021, e devono essere rispettate le specifiche condizioni descritte dalla relativa scheda di Pre-screening VIncA.'
		|| ' L''istanza selezionata è soggetta a limitazioni specifiche derivanti dal pre-screening VIncA previsto dalla normativa regionale vigente.'
		|| ' Le superfici autorizzate per l''intervento richiesto potrebbero essere obbligatoriamente ridotte rispetto a quanto indicato dall''utente'
		|| ' a seconda dell''habitat interessato. Devono essere rispettate le condizioni per l'' Habitat Prioritario/i'
	),
	(
		'HABITAT_VINCA_COMUNICAZIONE',
		'L''area di intervento ricade nel/negli Habitat Prioritario/i. Deve essere selezionata una tipologia di intervento tra quelle espresse dal DGR Umbria'
		|| ' n. 1093 del 10/11/2021, e devono essere rispettate le specifiche condizioni descritte dalla relativa scheda di Pre-screening VIncA.'
		|| ' L''istanza selezionata è soggetta a limitazioni specifiche derivanti dal pre-screening VIncA previsto dalla normativa regionale vigente.'
		|| ' Le superfici autorizzate per l''intervento richiesto potrebbero essere obbligatoriamente ridotte rispetto a quanto indicato dall''utente'
		|| ' a seconda dell''habitat interessato. Devono essere rispettate le condizioni per l'' Habitat Prioritario/i'
	);


create table FLGSCHEDE_INTERVENTO_LIMITAZIONE_VINCA_TAB (
	ID_SCHEDA_INTERVENTO int not null GENERATED ALWAYS AS IDENTITY,
	ID_TIPO_ISTANZA int,
	DESC_INTERVENTO varchar,
	LINK_PDF_SCHEDA varchar,
	constraint FLGSCHEDE_INTERVENTO_LIMITAZIONE_PK
		primary key(ID_SCHEDA_INTERVENTO),
	constraint FLGSCHEDE_INTERVENTO_LIMITAZIONE_FK_TIPO_ISTA
		foreign key (ID_TIPO_ISTANZA)
		references FLGTIPO_ISTANZA_TAB
);


with schede_boschive as (
		select *
		from (
			values (1, 'sfollo e diradamento nei boschi ceduo'),
				(2, 'sfollo e diradamento nelle fustaie coetanee di età inferiore ai cinquanta anni'),
				(3, 'sfollo e diradamento nelle fustaie coetanee di età superiore ai cinquanta anni'),
				(4, 'Taglio di avviamento all''alto fusto'),
				(5, 'Interventi con matricinatura a gruppi'),
				(6, 'Tagli di utilizzazione dei boschi cedui')
			) as T(ID_SCHEDA, TIPO_INTERVENTO)
	), shede_a_comunicazione as (
		select *
		from (
			values (7, 'interventi collegati al mantenimento delle attività tradizionali (usi civici)'),
				(8, 'tagli di utilizzazione tradizionale dei boschi cedui'),
				(9, 'tagli di utilizzazione dei boschi cedui di castagno'),
				(10, 'interventi nei castagneti da frutto'),
				(11, 'operazioni colturali negli impianti di arboricoltura da legno'),
				(12, 'operazioni di potatura e spalcatura nei popolamenti a prevalenza di conifere')
		) as T(ID_SCHEDA, TIPO_INTERVENTO)
	), link_pdf as (
		select *
		from (
			values (1, 'format-screening-prevalutazioni-sfollo-e-diradamento-cedui-1'),
				(2, 'format-screening-prevalutazioni-sfollo-e-diradamento-fustaie-1-1'),
				(3, 'format-screening-prevalutazioni-sfollo-e-diradamento-fustaie-2-1'),
				(4, 'format-screening-prevalutazioni-avviamento-alto-fusto-dgr-1'),
				(5, 'format-screening-prevalutazioni-matricinatura-a-gruppi-dgr-1'),
				(6, 'format-screening-prevalutazioni-boschi-cedui-dgr-1'),
				(7, 'format-screening-prevalutazioni-tradizionale-cedui-2-dgr-1'),
				(8, 'format-screening-prevalutazioni-tradizionale-cedui-1-dgr-1.pdf'),
				(9, 'format-screening-prevalutazioni-cedui-castagno-dgr-1'),
				(10, 'format-screening-prevalutazioni-castagneti-da-frutto-dgr-1'),
				(11, 'format-screening-prevalutazioni-altri-interventi-forestali-dgr-1'),
				(12, 'format-screening-prevalutazioni-potatura-spalcatura-conifere-dgr-1')
		) as T(ID_SCHEDA, FILE_PDF)
	)
insert into FLGSCHEDE_INTERVENTO_LIMITAZIONE_VINCA_TAB(ID_TIPO_ISTANZA, DESC_INTERVENTO, LINK_PDF_SCHEDA)
select ID_TIPO_ISTANZA, TIPO_INTERVENTO, FILE_PDF
from (
		select 'TAGLIO_BOSCHIVO' as COD_TIPO_ISTANZA_SPECIFICO, t.*
		from schede_boschive t
		union all
		select 'INTERVENTO_A_COMUNICAZIONE' as COD_TIPO_ISTANZA_SPECIFICO, t.*
		from shede_a_comunicazione t
	) a
	left join link_pdf p using (ID_SCHEDA)
	left join flgtipo_istanza_tab using (COD_TIPO_ISTANZA_SPECIFICO);


create table FLGFORME_TRATTAMENTO_TAB (
	ID_FORMA_TRATTAMENTO int not null GENERATED ALWAYS AS IDENTITY,
	COD_FORMA_TRATTAMENTO varchar not null,
	DESC_FORMA_TRATTAMENTO varchar not null,
	ID_GOVE int not null,
	IS_FINE_TURNO boolean not null,
	constraint FLGFORME_TRATTAMENTO_PK
		primary key (ID_FORMA_TRATTAMENTO),
	constraint FLGFORME_TRATTAMENTO_UNQ_COD
		unique (COD_FORMA_TRATTAMENTO),
	constraint FLGFORME_TRATTAMENTO_FK_GOVE
		foreign key (ID_GOVE)
		references FLGGOVE_TAB
);

alter table FLGISTA_TAB add column ID_SCHEDA_INTERVENTO int;
alter table FLGISTA_TAB add constraint FLGISTA_FK_SCHEDA_INTE
	foreign key(ID_SCHEDA_INTERVENTO)
	references FLGSCHEDE_INTERVENTO_LIMITAZIONE_VINCA_TAB;


with forme_tratt as (
		select id_trat, cod_trat, desc_trat,desc_gove, flag_fine_turno,
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
	)
insert into FLGFORME_TRATTAMENTO_TAB(COD_FORMA_TRATTAMENTO, DESC_FORMA_TRATTAMENTO, ID_GOVE, IS_FINE_TURNO)
select cod_trat, desc_trat, ID_GOVE, is_fine_turno
from forme_tratt
	join flggove_tab ft using(desc_gove);


create table FLGFORME_TRATTAMENTO_INTERVENTO_TAB (
	ID_SCHEDA_INTERVENTO int not null,
	ID_FORMA_TRATTAMENTO int not null,
	constraint FLGFORME_TRATTAMENTO_INTERVENTO_PK
		primary key (ID_SCHEDA_INTERVENTO, ID_FORMA_TRATTAMENTO),
	constraint FLGFORME_TRATTAMENTO_INTERVENTO_FK_FORMA
		foreign key (ID_FORMA_TRATTAMENTO)
		references FLGFORME_TRATTAMENTO_TAB,
	constraint FLGFORME_TRATTAMENTO_INTERVENTO_FK_SCHEDA
		foreign key (ID_SCHEDA_INTERVENTO)
		references FLGSCHEDE_INTERVENTO_LIMITAZIONE_VINCA_TAB
);


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







select *
from (
	values (1, 'sfollo e diradamento nei boschi ceduo'),
		(2, 'sfollo e diradamento nelle fustaie coetanee di età inferiore ai cinquanta anni'),
		(3, 'sfollo e diradamento nelle fustaie coetanee di età superiore ai cinquanta anni'),
		(4, 'Taglio di avviamento all''alto fusto'),
		(5, 'Interventi con matricinatura a gruppi'),
		(6, 'Tagli di utilizzazione dei boschi cedui')
	) as T(ID_SCHEDA, TIPO_INTERVENTO);

select *
from (
	values (7, 'interventi collegati al mantenimento delle attività tradizionali (usi civici)'),
		(8, 'tagli di utilizzazione tradizionale dei boschi cedui'),
		(9, 'tagli di utilizzazione dei boschi cedui di castagno'),
		(10, 'interventi nei castagneti da frutto'),
		(11, 'operazioni colturali negli impianti di arboricoltura da legno'),
		(12, 'operazioni di potatura e spalcatura nei popolamenti a prevalenza di conifere')
) as T(ID_SCHEDA, TIPO_INTERVENTO);


select *
from (
	values (1,'{ 12 }'),
		(2,'{ 8 }'),
		(3,'{ 8 }'),
		(4,'{ 11 }'),
		(5,'{ 10 }'),
		(6,'{ 9, 10, 11, 12 }')
	) as T(ID_SCHEDA, TRATTAMENTI)






select *
from (
	values (1, 'format-screening-prevalutazioni-sfollo-e-diradamento-cedui-1'),
		(2, 'format-screening-prevalutazioni-sfollo-e-diradamento-fustaie-1-1'),
		(3, 'format-screening-prevalutazioni-sfollo-e-diradamento-fustaie-2-1'),
		(4, 'format-screening-prevalutazioni-avviamento-alto-fusto-dgr-1'),
		(5, 'format-screening-prevalutazioni-matricinatura-a-gruppi-dgr-1'),
		(6, 'format-screening-prevalutazioni-boschi-cedui-dgr-1'),
		(7, 'format-screening-prevalutazioni-tradizionale-cedui-2-dgr-1'),
		(8, 'format-screening-prevalutazioni-tradizionale-cedui-1-dgr-1.pdf'),
		(9, 'format-screening-prevalutazioni-cedui-castagno-dgr-1'),
		(10, 'format-screening-prevalutazioni-castagneti-da-frutto-dgr-1'),
		(11, 'format-screening-prevalutazioni-altri-interventi-forestali-dgr-1'),
		(12, 'format-screening-prevalutazioni-potatura-spalcatura-conifere-dgr-1')
) as T(ID_SCHEDA, FILE_PDF)







create table FLGVINCOLI_TIPO_ISTA_TAB (
	ID_VINCOLO int not null,
	ID_TIPO_ISTANZA int not null,
	ID_LIMITAZIONE int,
	constraint FLGVINCOLI_TIPO_ISTA_PK
		primary key (ID_VINCOLO, ID_TIPO_ISTANZA),
	constraint FLGVINCOLI_TIPO_ISTA_FK_VINC
		foreign key (ID_VINCOLO)
		references FLGVINCOLI_TAB,
	constraint FLGVINCOLI_TIPO_ISTA_FK_TIPO
		foreign key (ID_TIPO_ISTANZA)
		references FLGTIPO_ISTANZA_TAB,
	constraint FLGVINCOLI_TIPO_ISTA_FK_LIMIT
		foreign key (ID_LIMITAZIONE)
		references FLGLIMITAZIONI_TAB
);

create index FLGVINCOLI_TIPO_ISTA_IDX_TIPO on FLGVINCOLI_TIPO_ISTA_TAB(ID_TIPO_ISTANZA);

insert into FLGVINCOLI_TIPO_ISTA_TAB (ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE)
select ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE
from FLGTIPO_ISTANZA_TAB
	cross join FLGVINCOLI_TAB
	cross join FLGLIMITAZIONI_TAB
where COD_TIPO_ISTANZA_SPECIFICO = 'TAGLIO_BOSCHIVO'
	and COD_VINCOLO = 'NAT2K'
	and COD_LIMITAZIONE = 'NAT2K_4000MQ';

insert into FLGVINCOLI_TIPO_ISTA_TAB (ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE)
select ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE
from FLGTIPO_ISTANZA_TAB
	cross join FLGVINCOLI_TAB
	cross join FLGLIMITAZIONI_TAB
where COD_TIPO_ISTANZA_SPECIFICO = 'TAGLIO_BOSCHIVO'
	and COD_VINCOLO = 'HABITAT_PRIOR'
	and COD_LIMITAZIONE = 'HABITAT_VINCA_BOSCHIVO';

insert into FLGVINCOLI_TIPO_ISTA_TAB (ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE)
select ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE
from FLGTIPO_ISTANZA_TAB
	cross join FLGVINCOLI_TAB
	cross join FLGLIMITAZIONI_TAB
where COD_TIPO_ISTANZA_SPECIFICO = 'INTERVENTO_A_COMUNICAZIONE'
	and COD_VINCOLO = 'HABITAT_PRIOR'
	and COD_LIMITAZIONE = 'HABITAT_VINCA_COMUNICAZIONE';


create table FLGVINCOLI_ISTA_TAB (
	ID_VINCOLO int not null,
	ID_ISTA int not null,
	PROG int not null,
	COD_AREA varchar,
	NOME_AREA varchar,
	SHAPE geometry,
	SUPERFICIE numeric check(SUPERFICIE >= 0),
	constraint FLGVINCOLI_ISTA_PK
		primary key (ID_VINCOLO, ID_ISTA, PROG),
	constraint FLGVINCOLI_ISTA_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB,
	constraint FLGVINCOLI_ISTA_FK_VINC
		foreign key (ID_VINCOLO)
		references FLGVINCOLI_TAB
);

create index FLGVINCOLI_ISTA_IDX_ISTA on FLGVINCOLI_ISTA_TAB(ID_ISTA);




alter table FLGSCHEDE_INTERVENTO_LIMITAZIONE_VINCA_TAB add desc_gove varchar;


update FLGSCHEDE_INTERVENTO_LIMITAZIONE_VINCA_TAB si
set desc_gove = (
		select g.desc_gove
		from (
				select distinct id_gove
				from FLGFORME_TRATTAMENTO_INTERVENTO_TAB fti
					join flgforme_trattamento_tab ft using (id_forma_trattamento)
				where fti.id_scheda_intervento  = si.id_scheda_intervento 
			) as t
			join flggove_tab g using (id_gove)
	);

create table FLGISTA_TAGLIO_BOSCHIVO_TAB (
	ID_ISTA int not null,
	SUPERFICIE_INTERVENTO numeric not null check(SUPERFICIE_INTERVENTO >= 0),
	DESC_GOVE varchar,
	ID_SSPR int not null,
	ETA_MEDIA int check(ETA_MEDIA >= 0),
	TIPO_SOPRASUOLO varchar,
	constraint FLGISTA_TAGLIO_BOSCHIVO_PK
		primary key (ID_ISTA),
	constraint FLGISTA_TAGLIO_BOSCHIVO_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB,
	constraint FLGISTA_TAGLIO_BOSCHIVO_FK_TIPO
		foreign key (TIPO_SOPRASUOLO)
		references FLGMACROCATEGORIE_SPECIE_TAB(NOME_MACROCATEGORIA)
);

create table FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_TAB (
	ID_ISTA int not null,
	ID_GOVE int not null,
	ID_FORMA_TRATTAMENTO_PREC int not null,
	ID_FORMA_TRATTAMENTO int not null,
	constraint FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_PK
		primary key (ID_ISTA, ID_GOVE),
	constraint FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAGLIO_BOSCHIVO_TAB
		on delete cascade
);


create table FLGMACROCATEGORIE_SPECIE_TAB (
	ID_MACROCATEGORIA int not null GENERATED ALWAYS AS IDENTITY,
	NOME_MACROCATEGORIA varchar not null,
	constraint FLGMACROCATEGORIE_SPECIE_PK
		primary key (ID_MACROCATEGORIA),
	constraint FLGMACROCATEGORIE_SPECIE_UNQ_NOME
		unique (NOME_MACROCATEGORIA)
);


with specie as (
		select *
		from (
			values
			 (1,'Acero campestre','Acer campestre','Latifoglia','Altri boschi caducifogli'),
			 (2,'Acero di monte','Acer pseudoplatanus','Latifoglia','Altri boschi caducifogli'),
			 (3,'Acero d''Ungheria','Acer opalus obtusatum','Latifoglia','Altri boschi caducifogli'),
			 (4,'Betulla','Betula pendula','Latifoglia','Altri boschi caducifogli'),
			 (5,'Carpino bianco','Carpinus betulus','Latifoglia','Ostrieti, carpineti'),
			 (6,'Carpino nero','Ostrya carpinifolia','Latifoglia','Altri boschi caducifogli'),
			 (7,'Castagno','Castanea sativa','Latifoglia','Castagneti'),
			 (8,'Cerro','Quercus cerris','Latifoglia','Boschi di cerro, farnetto, fragno,vallonea'),
			 (9,'Ciliegio selvatico','Prunus avium','Latifoglia','Altri boschi caducifogli'),
			 (10,'Cipresso','Cupressus spp','Conifera','Altri boschi di conifere'),
			 (11,'Corbezzolo','Arbustus unedo','Latifoglia','Altri boschi di latifoglie sempreverdi'),
			 (12,'Eucalipto spp','Eucalyptus spp','Latifoglia','Piantagioni di altre latifoglie'),
			 (13,'Faggio','Fagus sylvatica','Latifoglia','Faggete'),
			 (14,'Farnetto','Quercus frainetto','Latifoglia','Boschi di cerro, farnetto, fragno, vallonea'),
			 (15,'Farnia','Quercus robur','Latifoglia','Boschi di cerro, farnetto, fragno, vallonea'),
			 (16,'Fragno','Quercus trojana','Latifoglia','Boschi di cerro, farnetto, fragno, vallonea'),
			 (17,'Frassino maggiore','Fraxinus excelsior','Latifoglia','Altri boschi caducifogli'),
			 (18,'Ilatro comune','Phyllirea latifolia','Latifoglia','Macchia, arbusteti mediterranei'),
			 (19,'Leccio','Quercus ilex','Latifoglia','Leccete'),
			 (20,'Nocciolo','Corylus avellana','Latifoglia','Boschi igrofili'),
			 (21,'Olmo comune','Ulmus minor','Latifoglia','Boschi igrofili'),
			 (22,'Ontano napoletano','Alnus cordata','Latifoglia','Boschi igrofili'),
			 (23,'Ontano nero','Alnus glutinosa','Latifoglia','Boschi igrofili'),
			 (24,'Orniello','Fraxinus ornus','Latifoglia','Ostrieti, carpineti'),
			 (25,'Pino domestico','Pinus pinea','Conifera','Pinete di pini mediterranei'),
			 (26,'Pino d''Aleppo','Pinus Halepensis','Conifera','Pinete di pini mediterranei'),
			 (27,'Pino laricio','Pinus nigra laricio','Conifera','Pinete di pino nero, pino laricio e pino loricato'),
			 (28,'Pino marittimo','Pinus pinaster','Conifera','Pinete di pini mediterranei'),
			 (29,'Pino nero','Pinus nigra','Conifera','Pinete di pino nero, pino laricio e pino loricato'),
			 (30,'Pioppo nero','Populus nigra','Latifoglia','Boschi igrofili'),
			 (31,'Pioppo tremulo','Populus tremula','Latifoglia','Altri boschi caducifogli'),
			 (32,'Robinia','Robinia pseudoacacia','Latifoglia','Altri boschi caducifogli'),
			 (33,'Roverella','Quercus pubescens','Latifoglia','Boschi di rovere, roverella e farnia'),
			 (34,'Rovere','Quercus petraea','Latifoglia','Boschi di rovere, roverella e farnia'),
			 (35,'Salicone','Salix caprea','Latifoglia','Altri boschi caducifogli'),
			 (36,'Tiglio selvatico','Tilia cordata','Latifoglia','Altri boschi caducifogli')
			)as t(id_specie,nome_comune,nome_scientifico,macrocategoria,categoria)
	)
insert into FLGMACROCATEGORIE_SPECIE_TAB(NOME_MACROCATEGORIA)
select distinct macrocategoria
from specie;



create table FLGCATEGORIE_SPECIE_TAB (
	ID_CATEGORIA int not null GENERATED ALWAYS AS IDENTITY,
	NOME_CATEGORIA varchar not null,
	ID_MACROCATEGORIA int not null,
	constraint FLGMCATEGORIE_SPECIE_PK
		primary key (ID_CATEGORIA),
	constraint FLGCATEGORIE_SPECIE_UNQ_NOME
		unique (NOME_CATEGORIA),
	constraint FLGMCATEGORIE_SPECIE_FK_MACRO
		foreign key (ID_MACROCATEGORIA)
		references FLGMACROCATEGORIE_SPECIE_TAB
);


with specie as (
		select *
		from (
			values
			 (1,'Acero campestre','Acer campestre','Latifoglia','Altri boschi caducifogli'),
			 (2,'Acero di monte','Acer pseudoplatanus','Latifoglia','Altri boschi caducifogli'),
			 (3,'Acero d''Ungheria','Acer opalus obtusatum','Latifoglia','Altri boschi caducifogli'),
			 (4,'Betulla','Betula pendula','Latifoglia','Altri boschi caducifogli'),
			 (5,'Carpino bianco','Carpinus betulus','Latifoglia','Ostrieti, carpineti'),
			 (6,'Carpino nero','Ostrya carpinifolia','Latifoglia','Altri boschi caducifogli'),
			 (7,'Castagno','Castanea sativa','Latifoglia','Castagneti'),
			 (8,'Cerro','Quercus cerris','Latifoglia','Boschi di cerro, farnetto, fragno,vallonea'),
			 (9,'Ciliegio selvatico','Prunus avium','Latifoglia','Altri boschi caducifogli'),
			 (10,'Cipresso','Cupressus spp','Conifera','Altri boschi di conifere'),
			 (11,'Corbezzolo','Arbustus unedo','Latifoglia','Altri boschi di latifoglie sempreverdi'),
			 (12,'Eucalipto spp','Eucalyptus spp','Latifoglia','Piantagioni di altre latifoglie'),
			 (13,'Faggio','Fagus sylvatica','Latifoglia','Faggete'),
			 (14,'Farnetto','Quercus frainetto','Latifoglia','Boschi di cerro, farnetto, fragno, vallonea'),
			 (15,'Farnia','Quercus robur','Latifoglia','Boschi di cerro, farnetto, fragno, vallonea'),
			 (16,'Fragno','Quercus trojana','Latifoglia','Boschi di cerro, farnetto, fragno, vallonea'),
			 (17,'Frassino maggiore','Fraxinus excelsior','Latifoglia','Altri boschi caducifogli'),
			 (18,'Ilatro comune','Phyllirea latifolia','Latifoglia','Macchia, arbusteti mediterranei'),
			 (19,'Leccio','Quercus ilex','Latifoglia','Leccete'),
			 (20,'Nocciolo','Corylus avellana','Latifoglia','Boschi igrofili'),
			 (21,'Olmo comune','Ulmus minor','Latifoglia','Boschi igrofili'),
			 (22,'Ontano napoletano','Alnus cordata','Latifoglia','Boschi igrofili'),
			 (23,'Ontano nero','Alnus glutinosa','Latifoglia','Boschi igrofili'),
			 (24,'Orniello','Fraxinus ornus','Latifoglia','Ostrieti, carpineti'),
			 (25,'Pino domestico','Pinus pinea','Conifera','Pinete di pini mediterranei'),
			 (26,'Pino d''Aleppo','Pinus Halepensis','Conifera','Pinete di pini mediterranei'),
			 (27,'Pino laricio','Pinus nigra laricio','Conifera','Pinete di pino nero, pino laricio e pino loricato'),
			 (28,'Pino marittimo','Pinus pinaster','Conifera','Pinete di pini mediterranei'),
			 (29,'Pino nero','Pinus nigra','Conifera','Pinete di pino nero, pino laricio e pino loricato'),
			 (30,'Pioppo nero','Populus nigra','Latifoglia','Boschi igrofili'),
			 (31,'Pioppo tremulo','Populus tremula','Latifoglia','Altri boschi caducifogli'),
			 (32,'Robinia','Robinia pseudoacacia','Latifoglia','Altri boschi caducifogli'),
			 (33,'Roverella','Quercus pubescens','Latifoglia','Boschi di rovere, roverella e farnia'),
			 (34,'Rovere','Quercus petraea','Latifoglia','Boschi di rovere, roverella e farnia'),
			 (35,'Salicone','Salix caprea','Latifoglia','Altri boschi caducifogli'),
			 (36,'Tiglio selvatico','Tilia cordata','Latifoglia','Altri boschi caducifogli')
			)as t(id_specie,nome_comune,nome_scientifico,macrocategoria,categoria)
	)
insert into FLGCATEGORIE_SPECIE_TAB(ID_MACROCATEGORIA, NOME_CATEGORIA)
select distinct mc.ID_MACROCATEGORIA, s.categoria
from specie s
	join FLGMACROCATEGORIE_SPECIE_TAB mc on (mc.NOME_MACROCATEGORIA = s.macrocategoria);



create table FLGSPECIE_TAB (
	ID_SPECIE int not null GENERATED ALWAYS AS IDENTITY,
	NOME_SPECIE varchar not null,
	NOME_SCENTIFICO varchar not null,
	ID_CATEGORIA int not null,
	constraint FLGSPECIE_PK
		primary key (ID_SPECIE),
	constraint FLGSPECIE_UNQ_NOME_SPECIE
		unique (NOME_SPECIE),
	constraint FLGSPECIE_UNQ_NOME_SCENTIFICO
		unique (NOME_SCENTIFICO),
	constraint FLGSPECIE_FK_CAT
		foreign key (ID_CATEGORIA)
		references FLGCATEGORIE_SPECIE_TAB
);


with specie as (
		select *
		from (
			values
			 (1,'Acero campestre','Acer campestre','Latifoglia','Altri boschi caducifogli'),
			 (2,'Acero di monte','Acer pseudoplatanus','Latifoglia','Altri boschi caducifogli'),
			 (3,'Acero d''Ungheria','Acer opalus obtusatum','Latifoglia','Altri boschi caducifogli'),
			 (4,'Betulla','Betula pendula','Latifoglia','Altri boschi caducifogli'),
			 (5,'Carpino bianco','Carpinus betulus','Latifoglia','Ostrieti, carpineti'),
			 (6,'Carpino nero','Ostrya carpinifolia','Latifoglia','Altri boschi caducifogli'),
			 (7,'Castagno','Castanea sativa','Latifoglia','Castagneti'),
			 (8,'Cerro','Quercus cerris','Latifoglia','Boschi di cerro, farnetto, fragno,vallonea'),
			 (9,'Ciliegio selvatico','Prunus avium','Latifoglia','Altri boschi caducifogli'),
			 (10,'Cipresso','Cupressus spp','Conifera','Altri boschi di conifere'),
			 (11,'Corbezzolo','Arbustus unedo','Latifoglia','Altri boschi di latifoglie sempreverdi'),
			 (12,'Eucalipto spp','Eucalyptus spp','Latifoglia','Piantagioni di altre latifoglie'),
			 (13,'Faggio','Fagus sylvatica','Latifoglia','Faggete'),
			 (14,'Farnetto','Quercus frainetto','Latifoglia','Boschi di cerro, farnetto, fragno, vallonea'),
			 (15,'Farnia','Quercus robur','Latifoglia','Boschi di cerro, farnetto, fragno, vallonea'),
			 (16,'Fragno','Quercus trojana','Latifoglia','Boschi di cerro, farnetto, fragno, vallonea'),
			 (17,'Frassino maggiore','Fraxinus excelsior','Latifoglia','Altri boschi caducifogli'),
			 (18,'Ilatro comune','Phyllirea latifolia','Latifoglia','Macchia, arbusteti mediterranei'),
			 (19,'Leccio','Quercus ilex','Latifoglia','Leccete'),
			 (20,'Nocciolo','Corylus avellana','Latifoglia','Boschi igrofili'),
			 (21,'Olmo comune','Ulmus minor','Latifoglia','Boschi igrofili'),
			 (22,'Ontano napoletano','Alnus cordata','Latifoglia','Boschi igrofili'),
			 (23,'Ontano nero','Alnus glutinosa','Latifoglia','Boschi igrofili'),
			 (24,'Orniello','Fraxinus ornus','Latifoglia','Ostrieti, carpineti'),
			 (25,'Pino domestico','Pinus pinea','Conifera','Pinete di pini mediterranei'),
			 (26,'Pino d''Aleppo','Pinus Halepensis','Conifera','Pinete di pini mediterranei'),
			 (27,'Pino laricio','Pinus nigra laricio','Conifera','Pinete di pino nero, pino laricio e pino loricato'),
			 (28,'Pino marittimo','Pinus pinaster','Conifera','Pinete di pini mediterranei'),
			 (29,'Pino nero','Pinus nigra','Conifera','Pinete di pino nero, pino laricio e pino loricato'),
			 (30,'Pioppo nero','Populus nigra','Latifoglia','Boschi igrofili'),
			 (31,'Pioppo tremulo','Populus tremula','Latifoglia','Altri boschi caducifogli'),
			 (32,'Robinia','Robinia pseudoacacia','Latifoglia','Altri boschi caducifogli'),
			 (33,'Roverella','Quercus pubescens','Latifoglia','Boschi di rovere, roverella e farnia'),
			 (34,'Rovere','Quercus petraea','Latifoglia','Boschi di rovere, roverella e farnia'),
			 (35,'Salicone','Salix caprea','Latifoglia','Altri boschi caducifogli'),
			 (36,'Tiglio selvatico','Tilia cordata','Latifoglia','Altri boschi caducifogli')
			)as t(id_specie,nome_comune,nome_scientifico,macrocategoria,categoria)
	)
insert into FLGSPECIE_TAB (NOME_SPECIE, NOME_SCENTIFICO, ID_CATEGORIA)
select nome_comune, nome_scientifico, ID_CATEGORIA
from specie s
	join FLGCATEGORIE_SPECIE_TAB mc on (mc.NOME_CATEGORIA = s.categoria);

create table FLGSPECI_ISTA_TAB(
	ID_ISTA int not null,
	PROG int not null,
	ID_SPECIE int not null,
	PERCENTUALE_INTERVENTO float not null check(PERCENTUALE_INTERVENTO >= 0 and PERCENTUALE_INTERVENTO <= 100),
	constraint FLGSPECI_ISTA_PK
		primary key(ID_ISTA, PROG),
	constraint  FLGSPECI_ISTA_UNQ_SPEC
		unique (ID_SPECIE, ID_ISTA),
	constraint FLGSPECI_ISTA_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB,
	constraint FLGSPECI_ISTA_FK_SPECIE
		foreign key (ID_SPECIE)
		references FLGSPECIE_TAB
);

create table FLGASSORTIMENTO_TAB (
	ID_ASSORTIMENTO int not null GENERATED ALWAYS AS IDENTITY,
	NOME_ASSORTIMENTO varchar not null,
	DESTINAZIONE_USO varchar not null,
	DESC_ASSORTIMENTO varchar not null,
	constraint FLGASSORTIMENTO_PK
		primary key (ID_ASSORTIMENTO),
	constraint FLGASSORTIMENTO_UNQ
		unique (NOME_ASSORTIMENTO)
);
insert into FLGASSORTIMENTO_TAB(DESTINAZIONE_USO, DESC_ASSORTIMENTO, NOME_ASSORTIMENTO)
values ('A fini energetici','Legna da ardere e carbone', 'LEGNA'),
	('A fini energetici','Cippato per combustibile (biomasse)', 'COMBUSTIBILE'),
	('Per lavorazione indrustiale','Tronchi (travame, trancia da sega, trancia da sfoglia)', 'TRONCHI'),
	('Per lavorazione indrustiale','Cippato per cellulosa', 'CELLULOSA'),
	('Per lavorazione indrustiale','Altro', 'ALTRO');


create table FLGASS_SPECI_ISTA_TAB (
	ID_ISTA int not null,
	ID_SPECIE int not null,
	ID_ASSORTIMENTO int not null,
	IS_AUTOCONSUMO boolean not null,
	PERCENTUALE_ASS float not null check(PERCENTUALE_ASS >= 0 and PERCENTUALE_ASS <= 100),
	constraint FLGASS_SPECI_ISTA_PK
		primary key (ID_ISTA, ID_SPECIE, ID_ASSORTIMENTO, IS_AUTOCONSUMO),
	constraint FLGASS_SPECI_ISTA_FK_SPECI_ISTA
		foreign key (ID_ISTA, ID_SPECIE)
		references FLGSPECI_ISTA_TAB(ID_ISTA, ID_SPECIE),
	constraint FLGASS_SPECI_ISTA_FK_ASSORT
		foreign key (ID_ASSORTIMENTO)
		references FLGASSORTIMENTO_TAB
);


create table FLGUSO_SUOLO_TAB (
	ID_USO_SUOLO int not null GENERATED ALWAYS AS IDENTITY,
	COD_USO_SUOLO varchar not null,
	DESC_USO_SUOLO varchar not null,
	constraint FLGUSO_SUOLO_PK
		primary key (ID_USO_SUOLO),
	constraint FLGUSO_SUOLO_UNQ_COD
		unique (COD_USO_SUOLO)
);

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

create table FLGTIPO_INTERVENTO_TAB (
	ID_TIPO_INTERVENTO int not null GENERATED ALWAYS AS IDENTITY,
	ID_USO_SUOLO int not null,
	COD_TIPO_INTERVENTO varchar not null,
	NOME_TIPO_INTERVENTO varchar not null,
	RIFERIMENTO_NORMATIVO varchar not null,
	PARAMETRO_RICHIESTO varchar not null,
	constraint FLGTIPO_INTERVENTO_PK
		primary key (ID_TIPO_INTERVENTO),
	constraint FLGFLGTIPO_INTERVENTO_UNQ_COD
		unique (ID_USO_SUOLO, COD_TIPO_INTERVENTO),
	constraint FLGFLGTIPO_INTERVENTO_FK_USO
		foreign key (ID_USO_SUOLO)
		references FLGUSO_SUOLO_TAB
);

insert into FLGTIPO_INTERVENTO_TAB (
		ID_USO_SUOLO, COD_TIPO_INTERVENTO,
		NOME_TIPO_INTERVENTO, RIFERIMENTO_NORMATIVO, PARAMETRO_RICHIESTO
	)
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
select ID_USO_SUOLO, substr(cod_intervento, 3) as cod_intervento,
	tipo_intervento, normativo, parametro
from flginte_no_forest_tab
	join flguso_suolo_tab s on (s.cod_uso_suolo = id_tipo::varchar);




create table FLGISTA_INTERVENTO_COMUNICAZIONE_TAB  (
	ID_ISTA int not null,
	ID_TIPO_INTERVENTO int not null,
	VALORE_DICHIATATO float not null,
	DESC_INTERVENTO varchar,
	constraint FLGISTA_INTERVENTO_COMUNICAZIONE_PK
		primary key(ID_ISTA),
	constraint FLGISTA_INTERVENTO_COMUNICAZIONE_FK_ISTA
		foreign key(ID_ISTA)
		references FLGISTA_TAB,
	constraint FLGISTA_INTERVENTO_COMUNICAZIONE_FK_INTE
		foreign key(ID_TIPO_INTERVENTO)
		references FLGTIPO_INTERVENTO_TAB
);

create table FLGISTA_STORICO_GESTORI_TAB (
	ID_ISTA int not null,
	ID_UTENTE_GESTORE_PRECEDENTE int not null,
	ID_STATO_REGISTRATO int not null,
	DATA_CAMBIO_GESTORE timestamp without time zone not null,
	ID_UTENTE_CAMBIO int not null,
	constraint FLGISTA_STORICO_GESTORI_PK
		primary key (ID_ISTA, DATA_CAMBIO_GESTORE),
	constraint FLGISTA_STORICO_GESTORI_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB,
	constraint FLGISTA_STORICO_GESTORI_FK_GEST
		foreign key (ID_UTENTE_GESTORE_PRECEDENTE)
		references FLGUTEN_TAB,
	constraint FLGISTA_STORICO_GESTORI_FK_CAMBIO
		foreign key (ID_UTENTE_CAMBIO)
		references FLGUTEN_TAB,
	constraint FLGISTA_STORICO_GESTORI_FK_STATO
		foreign key (ID_STATO_REGISTRATO)
		references FLGSTATO_ISTANZA_TAB
);



create table FLGISTA_INVIO_TAB (
	ID_ISTA int not null,
	DATA_INVIO timestamp without time zone not null,
	ID_FILE_RICEVUTE int not null,
	constraint FLGISTA_INVIO_PK
		primary key(ID_ISTA),
	constraint FLGISTA_INVIO_FK_ISTA
		foreign key(ID_ISTA)
		references FLGISTA_TAB
		on delete cascade,
	constraint FLGISTA_INVIO_FK_FILE
		foreign key(ID_FILE_RICEVUTE)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB
);

create index FLGISTA_INVIO_IDX_FILE on FLGISTA_INVIO_TAB(ID_FILE_RICEVUTE);



insert into FLGLIMITAZIONI_TAB(COD_LIMITAZIONE, DESC_LIMITAZIONE)
	values (
		'NAT2K_SOPRA_SOGLIA_UMBRIA',
		'la particella forestale ricade in un''area delle Rete Natura 2000. L''istanza di intervento  selezionata è soggetta a limitazioni '
		|| 'specifiche derivanti dalle condizioni e contenuti specifici del piano di gestione del sito interessato, come  previsto dalla '
		|| 'normativa regionale vigente. Gli interventi di taglio previsti devono rispettare le condizioni di obbligo generale e i vincoli'
		|| ' specifici indicati nell''allegato 1 e 2 della DGR Umbria n. 1093 del 10/11/2021 e le superfici limite   indicate a seconda '
		|| 'dell''habitat interessato. Devono essere rispettate le condizioni per l''Habitat Prioritario/i'
	),
	(
		'GENERICA_SOPRA_SOGLIA',
		'La particella forestale ricade in aree vincolate. E'' responsabilità del professionista individuare il tipo di istanza corretto '
		|| 'tra istanza sopra soglia e istanza in deroga.'
	);


insert into FLGVINCOLI_TIPO_ISTA_TAB (ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE)
select ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE
from FLGTIPO_ISTANZA_TAB
	join FLGCIST_TAB using (ID_CIST)
	cross join FLGVINCOLI_TAB
	cross join FLGLIMITAZIONI_TAB
where cod_tipo_istanza != 'SOTTO_SOGLIA'
	and COD_VINCOLO = 'NAT2K'
	and COD_LIMITAZIONE = 'NAT2K_SOPRA_SOGLIA_UMBRIA';



insert into FLGVINCOLI_TIPO_ISTA_TAB (ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE)
select ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE
from FLGTIPO_ISTANZA_TAB
	join FLGCIST_TAB using (ID_CIST)
	cross join FLGVINCOLI_TAB
	cross join FLGLIMITAZIONI_TAB
where cod_tipo_istanza != 'SOTTO_SOGLIA'
	and COD_VINCOLO in ('PAI_FRANE', 'PAI_VALANGHE', 'PAI_ALLUVIONI', 'AREE_PROTETTE')
	and COD_LIMITAZIONE = 'GENERICA_SOPRA_SOGLIA';





--  Solo per regione umbria
insert into FLGLIMITAZIONI_TAB(COD_LIMITAZIONE, DESC_LIMITAZIONE)
	values (
		'NAT2K_SOTTO_UMBRIA',
		'Le istanze sotto soglia sono soggette alle limitazioni esplicitate dalle condizioni d''obbligo per gli interventi forestali e dal relativo allegato'
	);
insert into FLGVINCOLI_TIPO_ISTA_TAB (ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE)
select ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE
from FLGTIPO_ISTANZA_TAB
	cross join FLGVINCOLI_TAB
	cross join FLGLIMITAZIONI_TAB
where COD_TIPO_ISTANZA_SPECIFICO = 'TAGLIO_BOSCHIVO'
	and COD_VINCOLO = 'NAT2K'
	and COD_LIMITAZIONE = 'NAT2K_SOTTO_UMBRIA';

insert into FLGVINCOLI_TIPO_ISTA_TAB (ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE)
select ID_VINCOLO, ID_TIPO_ISTANZA, ID_LIMITAZIONE
from FLGTIPO_ISTANZA_TAB
	cross join FLGVINCOLI_TAB
	cross join FLGLIMITAZIONI_TAB
where COD_TIPO_ISTANZA_SPECIFICO = 'INTERVENTO_A_COMUNICAZIONE'
	and COD_VINCOLO = 'NAT2K'
	and COD_LIMITAZIONE = 'NAT2K_SOTTO_UMBRIA';


create table FLGRISPOSTE_WIZARD_VINCOLISTICA_TAB (
	ID_ISTA int not null,
	PROG int not null,
	RISPOSTA varchar not null,
	constraint FLGRISPOSTE_WIZARD_VINCOLISTICA_PK
		primary key (ID_ISTA, PROG),
	constraint FLGRISPOSTE_WIZARD_VINCOLISTICA_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB
		on delete cascade
);

create table FLGISTA_ELABORATO_VINCA_TAB (
	ID_ISTA int not null,
	ID_FILE_VINCA int not null,
	constraint FLGISTA_ELABORATO_VINCA_PK
		primary key (ID_ISTA),
	constraint FLGISTA_ELABORATO_VINCA_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB
		on delete cascade,
	constraint FLGISTA_ELABORATO_VINCA_FK_FILE
		foreign key (ID_FILE_VINCA)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB
);
CREATE INDEX flgista_elaborato_vinca_idx_file ON foliage2.flgista_elaborato_vinca USING (id_file_vinca);

alter table flgvincoli_tab add column
	gruppo varchar;

update flgvincoli_tab
set gruppo = 'PAI - Rischio Idrogeologico'
where COD_VINCOLO in ('PAI_FRANE', 'PAI_VALANGHE', 'PAI_ALLUVIONI');

update flgvincoli_tab
set gruppo = 'Aree Protette'
where COD_VINCOLO in ('AREE_PROTETTE', 'NAT2K');



--create table FLGLAYERS_TAB (
--	ID_LAYER int not null GENERATED ALWAYS AS identity,
--	COD_LAYER varchar not null,
--	NOME_GRUPPO varchar not null,
--	NOME_LAYER varchar not null,
--	SRID int not null,
--	FONTE_LAYER varchar not null check (FONTE_LAYER in ('TABELLA', 'WFS', 'WMS')),
--	DETTAGLIO_FONTE varchar not null,
--	SPIDX_COL_NAME varchar,
--	LABEL_EXPRESSION varchar,
--	constraint FLGLAYERS_PK
--		primary key (ID_LAYER),
--	constraint FLGLAYERS_UNQ_COD
--		unique (COD_LAYER)
--);
--
--insert into flglayers_tab (cod_layer,nome_gruppo,nome_layer,srid,fonte_layer,dettaglio_fonte,spidx_col_name,label_expression) values
--	 ('101a','PAI - Rischio Idrogeologico','PAI_RISCHIO_ALLUVIONE',4326,'TABELLA','pai_rischio_alluvione_101','geom','rischio'),
--	 ('101f','PAI - Rischio Idrogeologico','PAI_RISCHIO_FRANA',4326,'TABELLA','pai_rischio_frana_101','geom','rischio'),
--	 ('101v','PAI - Rischio Idrogeologico','PAI_RISCHIO_VALANGA',4326,'TABELLA','pai_rischio_valanga_101','geom','rischio'),
--	 ('105','Aree Protette','NAT2K',4326,'TABELLA','sitiprotetti_natura_2000','geom','denominazi'),
--	 ('106','Aree Protette','AREE_PROTETTE',4326,'TABELLA','aree_protette_106','geom','nome_gazze'),
--
--	 ('208','Aree Protette','HABITAT',6706,'TABELLA','siti_natura_2000_lazio_umbria','geom','denominazi'), --solo Umbria
--	 ('215','Vincoli paesaggistici','AREE_PAESAGGISTICHE',6706,'TABELLA','aree_paesaggistiche_215','geom','comune||' - '||localita'), --solo Umbria
--
--	 ('501','PTPR B','COSTA_MARE',25833,'TABELLA','costa_mare_501','geom','"COMUNE"'), --solo Lazio
--	 ('502','PTPR B','COSTA_LAGHI',25833,'TABELLA','costa_laghi_502','geom','"NOME_CTR"||''(''||"COMUNE_"||'')'''), --solo Lazio
--	 ('503','PTPR B','ACQUE_PUBBLICHE',25833,'TABELLA','acque_pubbliche_503','geom','id'), --solo Lazio
--	 ('504','PTPR B','ACQUE_PUBBLICHE_RISPETTO',25833,'TABELLA','acque_pubbliche_rispetto_504','geom','nome_gu'), --solo Lazio
--	 ('505','PTPR B','ALTIMETRIA_1200',25833,'TABELLA','altimetria_1200_505','geom','id'), --solo Lazio
--	 ('506','PTPR B','USI_CIVICI',25833,'TABELLA','usi_civici_506','geom','id'), --solo Lazio
--	 ('507','PTPR B','ZONE_UMIDE',25833,'TABELLA','zone_umide_507','geom','"NOME_ZONA"'), --solo Lazio
--	 ('508','PTPR B','LINEE_ARCHEOLOGICHE',25833,'TABELLA','linee_archeologiche_508','geom','id'), --solo Lazio
--	 ('509','PTPR B','RISPETTO_LINEE_ARCHEOLOGICHE',25833,'TABELLA','rispetto_linee_archeologiche_509','geom','id'), --solo Lazio
--	 ('510','PTPR B','RISPETTO_LINEE_ARCHEOLOGICHE_TIPIZZATE',25833,'TABELLA','rispetto_linee_archeologiche_tipizzate_510','geom','id'), --solo Lazio
--	 ('511','PTPR B','PUNTI_ARCHEOLOGICI',25833,'TABELLA','punti_archeologici_511','geom','id||'': ''||"NOME"'), --solo Lazio
--	 ('512','PTPR B','RISPETTO_PUNTI_ARCHEOLOGICI',25833,'TABELLA','rispetto_punti_archeologici_512','geom','id'), --solo Lazio
--	 ('513','PTPR B','PUNTI_ARCHEOLOGICI_TIPIZZATI',25833,'TABELLA','punti_archeologici_tipizzati_513','geom','nome'), --solo Lazio
--	 ('514','PTPR B','DECRETI_ARCHEOLOGICI',25833,'TABELLA','decreti_archeologici_514','geom','"NOME"'), --solo Lazio
--	 ('515','PTPR B','GEOMORFOLOGICI_TIPIZZATI',25833,'TABELLA','geomorfologici_tipizzati_515','geom','"NOME"'), --solo Lazio
--	 ('516','PTPR B','RISPETTO_GEOMORFOLOGIA',25833,'TABELLA','rispetto_geomorfologia_516','geom','id'), --solo Lazio
--	 ('517','PTPR B','BOSCHI',25833,'TABELLA','boschi_517','geom','id'), --solo Lazio
--	 ('518','PTPR B','EX_1497_AB',25833,'TABELLA','ex_1497_ab_518','geom','"NOME"'), --solo Lazio
--	 ('519','PTPR B','EX_1497_CD',25833,'TABELLA','ex_1497_cd_519','geom','"NOME"') --solo Lazio
--	 ;



create table FLGUNITA_OMOGENEE_TAB (
	ID_ISTA int not null,
	PROG_UOG int not null,
	NOME_UOG varchar not null,
	SUPERFICIE_UTILE numeric not null check(SUPERFICIE_UTILE >= 0),
	SUPERFICIE_AREE_IMPRODUTTIVE numeric not null check(SUPERFICIE_AREE_IMPRODUTTIVE >= 0),
	SUPERFICIE_CHIARE_RADURE numeric not null check(SUPERFICIE_CHIARE_RADURE >= 0),
	SUPERFICIE_AREE_INTERDETTE numeric not null check(SUPERFICIE_AREE_INTERDETTE >= 0),
	SUPERFICIE numeric not null check(SUPERFICIE >= 0),
	DESC_GOVE varchar,
	ID_SSPR int,
	ETA_MEDIA int check(ETA_MEDIA >= 0),
	TIPO_SOPRASUOLO varchar,
	SHAPE geometry,
	constraint FLGUNITA_OMOGENEE_PK
		primary key (ID_ISTA, PROG_UOG),
	constraint FLGUNITA_OMOGENEE_UNQ_NOME
		unique (ID_ISTA, NOME_UOG),
	constraint FLGUNITA_OMOGENEE_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB
		on delete cascade,
	constraint FLGUNITA_OMOGENEE_FK_TIPO
		foreign key (TIPO_SOPRASUOLO)
		references FLGMACROCATEGORIE_SPECIE_TAB(NOME_MACROCATEGORIA)
);

create table FLGUNITA_OMOGENEE_TRATTAMENTO_TAB (
	ID_ISTA int not null,
	PROG_UOG int not null,
	ID_GOVE int not null,
	ID_FORMA_TRATTAMENTO_PREC int not null,
	ID_FORMA_TRATTAMENTO int not null,
	constraint FLGUNITA_OMOGENEE_TRATTAMENTO_PK
		primary key (ID_ISTA, PROG_UOG, ID_GOVE),
	constraint FLGUNITA_OMOGENEE_TRATTAMENTO_FK_ISTA
		foreign key (ID_ISTA, PROG_UOG)
		references FLGUNITA_OMOGENEE_TAB
		on delete cascade
);


alter table FLGPART_CATASTALI_TAB drop constraint FLGPART_CATASTALI_FK_ISTA;
alter table FLGPART_CATASTALI_TAB add
	constraint FLGPART_CATASTALI_FK_ISTA
		foreign key(ID_ISTA)
		references FLGISTA_TAB
		on delete cascade;

alter table FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_TAB drop constraint FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_FK_ISTA;
alter table FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_TAB add
	constraint FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAGLIO_BOSCHIVO_TAB
		on delete cascade;



drop view foliage2.flgispa_view;
drop table foliage2.flgispa_tab;
drop table foliage2.flgpfpc_tab;
drop table foliage2.flgpart_tab;
drop table foliage2.flgisso_tab;
drop table foliage2.flgsogg_tab;
drop table foliage2.flgmatr_tab;
drop table foliage2.flgassp_tab;
drop table foliage2.flgpoll_tab;
drop table foliage2.flgpian_tab;
drop table foliage2.flgasag_tab;
drop table foliage2.flguosp_tab;
drop table foliage2.flguogt_tab;
drop table foliage2.flgpfor_tab;



create table FLGSTRATI_ISTA_TAB (
	ID_ISTA int not null,
	PROG_STRATO int not null,
	PROG_UOG int,
	NOME_STRATO varchar not null,
	IS_AREA_SAGGIO_TRADIZIONALE boolean not null,
	IS_AREA_DIMOSTRATIVA boolean not null,
	IS_AREA_SAGGIO_RELASCOPICA boolean not null,
	IS_IMPOSTO boolean not null,
	SUPERFICIE_STRATO numeric,
	PERCENTUALE_RAPPRESENTATIVITA numeric,
	SHAPE geometry not null,
	constraint FLGSTRATI_ISTA_PK
		primary key (ID_ISTA, PROG_STRATO),
	constraint FLGSTRATI_ISTA_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB
		on delete cascade,
	constraint FLGSTRATI_ISTA_FK_UO
		foreign key (ID_ISTA, PROG_UOG)
		references FLGUNITA_OMOGENEE_TAB
		on delete cascade
);

create table FLGTIPO_VIABILITA_TAB (
	ID_TIPO_VIABILITA int not null GENERATED ALWAYS AS identity,
	COD_TIPO_VIABILITA varchar not null,
	NOME_TIPO_VIABILITA varchar not null,
	DESC_TIPO_VIABILITA varchar not null,
	constraint FLGTIPO_VIABILITA_PK
		primary key (ID_TIPO_VIABILITA),
	constraint FLGTIPO_VIABILITA_UNQ_COD
		unique (COD_TIPO_VIABILITA)
);

create table FLGVIABILITA_ISTA_TAB (
	ID_ISTA int not null,
	PROG_VIABILITA int not null,
	COD_TIPO_VIABILITA varchar not null,
	shape geometry not null,
	constraint FLGVIABILITA_ISTA_PK
		primary key (ID_ISTA, PROG_VIABILITA),
	constraint FLGVIABILITA_ISTA_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB
		on delete cascade,
	constraint FLGVIABILITA_ISTA_FK_TIPO
		foreign key (COD_TIPO_VIABILITA)
		references FLGTIPO_VIABILITA_TAB(COD_TIPO_VIABILITA)
);


create table FLGSPECI_UOG_TAB(
	ID_ISTA int not null,
	PROG_UOG int not null,
	PROG_SPECIE_UOG int not null,
	ID_SPECIE int not null,
	PERCENTUALE_INTERVENTO float not null check(PERCENTUALE_INTERVENTO >= 0 and PERCENTUALE_INTERVENTO <= 100),
	constraint FLGSPECI_UOG_PK
		primary key(ID_ISTA, PROG_UOG, PROG_SPECIE_UOG),
	constraint  FLGSPECI_UOG_UNQ_SPEC
		unique (ID_SPECIE, ID_ISTA, PROG_UOG),
	constraint FLGSPECI_UOG_FK_UOG
		foreign key (ID_ISTA, PROG_UOG)
		references FLGUNITA_OMOGENEE_TAB
		on delete cascade,
	constraint FLGSPECI_UOG_FK_SPECIE
		foreign key (ID_SPECIE)
		references FLGSPECIE_TAB
);


create table FLGASS_SPECI_UOG_TAB (
	ID_ISTA int not null,
	PROG_UOG int not null,
	ID_SPECIE int not null,
	ID_ASSORTIMENTO int not null,
	IS_AUTOCONSUMO boolean not null,
	PERCENTUALE_ASS float not null check(PERCENTUALE_ASS >= 0 and PERCENTUALE_ASS <= 100),
	constraint FLGASS_SPECI_UOG_PK
		primary key (ID_ISTA, PROG_UOG, ID_SPECIE, ID_ASSORTIMENTO, IS_AUTOCONSUMO),
	constraint FLGASS_SPECI_UOG_FK_SPECI_UOG
		foreign key (ID_ISTA, PROG_UOG, ID_SPECIE)
		references FLGSPECI_UOG_TAB(ID_ISTA, PROG_UOG, ID_SPECIE)
		on delete cascade,
	constraint FLGASS_SPECI_UOG_FK_ASSORT
		foreign key (ID_ASSORTIMENTO)
		references FLGASSORTIMENTO_TAB
);

create table FLGALLEGATI_ISTA_TAB (
	ID_ALLEGATO_ISTA int not null GENERATED ALWAYS AS identity,
	ID_ISTA int not null,
	COD_TIPO_ALLEGATO varchar not null,
	DESC_ALTRO_ALLEGATO varchar,
	ID_FILE_ALLEGATO int not null,
	constraint FLGALLEGATI_ISTA_PK
		primary key (ID_ALLEGATO_ISTA),
	constraint FLGALLEGATI_ISTA_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB
		on delete cascade,
	constraint FLGALLEGATI_ISTA_FK_FILE
		foreign key (ID_FILE_ALLEGATO)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB 
);




create table FLGCATEGORIE_TAB (
	ID_CATEGORIA int not null GENERATED ALWAYS AS identity,
	NOME_CATEGORIA varchar not null,
	constraint FLGCATEGORIE_PK
		primary key(ID_CATEGORIA),
	constraint FLGCATEGORIE_UNQ
		unique(NOME_CATEGORIA)
);
with master as (        
		select *
		from (
		values ('BOSCHI DI ABETE BIANCO', 'Abetina a Campanula'),
			('BOSCHI DI ABETE BIANCO', 'Abetina a Cardamine'),
			('BOSCHI DI ABETE BIANCO', 'Abetina e abeti-faggeta a Vaccinium e Maianthemum'),
			('ALTRI BOSCHI CADUCIFOGLI', 'Acereti appenninici'),
			('ALTRI BOSCHI CADUCIFOGLI', 'Acero-tilieti di monte e boschi di frassino ecc'),
			('ALTRI BOSCHI DI CONIFERE PURI E MISTI', 'Altre formazioni a conifere'),
			('ARBUSTETI A CLIMA TEMPERATO', 'Altre formazioni a ginestre'),
			('PINETE DI PINO NERO, PINO LARICIO E PINO LORICATO', 'Altre formazioni a pino nero e pino loricato'),
			('PINETE DI PINO SILVESTRE E PINO MONTANO', 'Altre formazioni a pino silvestre e pino montano'),
			('ALTRI BOSCHI CADUCIFOGLI', 'Altre formazioni caducifoglie'),
			('BOSCHI DI ABETE ROSSO', 'Altre formazioni con prevalenza di peccio'),
			('BOSCHI DI ABETE BIANCO', 'Altre formazioni di abete bianco'),
			('BOSCHI DI CERRO, FARNETTO, FRAGNO, VALLONEA', 'Altre formazioni di cerro, farnetto, fragno o vallonea'),
			('FAGGETE', 'Altre formazioni di faggio'),
			('BOSCHI DI LARICE E PINO CEMBRO ', 'Altre formazioni di larice e cembro'),
			('BOSCHI DI ROVERE, ROVERELLA E FARNIA', 'Altre formazioni di rovere, roverella e farnia'),
			('BOSCHI IGROFILI', 'Altre formazioni forestali in ambienti umidi'),
			('PIANTAGIONI DI CONIFERE ', 'Altre piantagioni di conifere esotiche'),
			('ARBUSTETI A CLIMA TEMPERATO', 'Altri arbusteti di clima temperato'),
			('MACCHIA, ARBUSTETI MEDITERRANEI ', 'Altri arbusteti sempreverdi'),
			('ARBUSTETI SUBALPINI', 'Altri arbusteti subalpini di aghifoglie'),
			('ARBUSTETI A CLIMA TEMPERATO', 'Arbusteti a ginepro'),
			('ARBUSTETI A CLIMA TEMPERATO', 'Arbusteti a ginestra (Spartium junceum) '),
			('ARBUSTETI A CLIMA TEMPERATO', 'Arbusteti a ginestra dell''Etna (Genista aetnensis)'),
			('ALTRI BOSCHI CADUCIFOGLI', 'Betuleti, boschi montani pionieri '),
			('OSTRIETI, CARPINETI ', 'Boscaglia a carpino orientale'),
			('LECCETE ', 'Boscaglia di leccio'),
			('ALTRI BOSCHI CADUCIFOGLI', 'Boscaglie di Cercis'),
			('ALTRI BOSCHI DI LATIFOGLIE SEMPREVERDI', 'Boscaglie termo-mediterranee'),
			('BOSCHI IGROFILI', 'Boschi a frassino ossifillo e olmo'),
			('BOSCHI IGROFILI', 'Boschi a ontano bianco'),
			('BOSCHI IGROFILI', 'Boschi a ontano nero'),
			('OSTRIETI, CARPINETI ', 'Boschi di carpino bianco'),
			('OSTRIETI, CARPINETI ', 'Boschi di carpino nero e orniello '),
			('BOSCHI DI CERRO, FARNETTO, FRAGNO, VALLONEA', 'Boschi di farnetto'),
			('BOSCHI DI ROVERE, ROVERELLA E FARNIA', 'Boschi di farnia'),
			('BOSCHI DI CERRO, FARNETTO, FRAGNO, VALLONEA', 'Boschi di fragno e nuclei di vallonea'),
			('ALTRI BOSCHI CADUCIFOGLI', 'Boschi di ontano napoletano'),
			('BOSCHI DI ROVERE, ROVERELLA E FARNIA', 'Boschi di rovere'),
			('BOSCHI DI ROVERE, ROVERELLA E FARNIA', 'Boschi di roverella'),
			('ALTRI BOSCHI DI LATIFOGLIE SEMPREVERDI', 'Boschi sempreverdi di ambienti umidi'),
			('LECCETE ', 'Bosco misto di leccio e orniello'),
			('ARBUSTETI SUBALPINI', 'Brughiera subalpina '),
			('CASTAGNETI', 'Castagneti da frutto, selve castanili'),
			('CASTAGNETI', 'Castagneti da legno'),
			('BOSCHI DI CERRO, FARNETTO, FRAGNO, VALLONEA', 'Cerrete collinari e montane'),
			('BOSCHI DI CERRO, FARNETTO, FRAGNO, VALLONEA', 'Cerrete di pianura'),
			('MACCHIA, ARBUSTETI MEDITERRANEI ', 'Cisteti'),
			('FAGGETE', 'Faggete a agrifoglio, felci e campanula'),
			('FAGGETE', 'Faggete acidofile a Luzula'),
			('FAGGETE', 'Faggete mesofile'),
			('FAGGETE', 'Faggete termofile a Cephalanthera'),
			('ARBUSTETI SUBALPINI', 'Formazione ad ontano verde'),
			('ALTRI BOSCHI DI CONIFERE PURI E MISTI', 'Formazioni a cipresso'),
			('MACCHIA, ARBUSTETI MEDITERRANEI ', 'Formazioni a ginepri sul litorale '),
			('BOSCHI DI LARICE E PINO CEMBRO ', 'Lariceto in fustaia chiusa'),
			('BOSCHI DI LARICE E PINO CEMBRO ', 'Larici isolati nella brughiera subalpina'),
			('BOSCHI DI LARICE E PINO CEMBRO ', 'Larici-cembreto'),
			('LECCETE ', 'Lecceta rupicola '),
			('LECCETE ', 'Lecceta termofila costiera '),
			('MACCHIA, ARBUSTETI MEDITERRANEI ', 'Macchia a lentisco'),
			('MACCHIA, ARBUSTETI MEDITERRANEI ', 'Macchia litorale'),
			('ARBUSTETI SUBALPINI', 'Mughete'),
			('SUGHERETE ', 'Pascolo arborato di sughera'),
			('BOSCHI DI ABETE ROSSO', 'Pecceta montana'),
			('BOSCHI DI ABETE ROSSO', 'Pecceta subalpina'),
			('PIANTAGIONI DI CONIFERE ', 'Piantagioni di conifere indigene '),
			('PIANTAGIONI DI ALTRE LATIFOGLIE', 'Piantagioni di eucalipti'),
			('PIANTAGIONI DI ALTRE LATIFOGLIE', 'Piantagioni di latifoglie'),
			('PINETE DI PINO SILVESTRE E PINO MONTANO', 'Pineta (pino silvestre) a carice oppure astragali'),
			('PINETE DI PINO SILVESTRE E PINO MONTANO', 'Pineta (pino silvestre) a erica'),
			('PINETE DI PINO SILVESTRE E PINO MONTANO', 'Pineta (pino silvestre) a farnia e molinia'),
			('PINETE DI PINO SILVESTRE E PINO MONTANO', 'Pineta (pino silvestre) a roverella e citiso a foglie sessili'),
			('PINETE DI PINO NERO, PINO LARICIO E PINO LORICATO', 'Pineta a pino nero a citiso e ginestra'),
			('PINETE DI PINO NERO, PINO LARICIO E PINO LORICATO', 'Pineta a pino nero a erica e orniello'),
			('PINETE DI PINO NERO, PINO LARICIO E PINO LORICATO', 'Pineta a pino nero a pino laricio (Pinus laricio)'),
			('PINETE DI PINO NERO, PINO LARICIO E PINO LORICATO', 'Pineta a pino nero a pino loricato (Pinus leucodermis)'),
			('PINETE DI PINO SILVESTRE E PINO MONTANO', 'Pineta di pino montano'),
			('PINETE DI PINI MEDITERRANEI', 'Pinete a Pinus halepensis'),
			('PINETE DI PINI MEDITERRANEI', 'Pinete a Pinus pinaster'),
			('PINETE DI PINI MEDITERRANEI', 'Pinete a Pinus pinea'),
			('PIANTAGIONI DI CONIFERE ', 'Pinus radiata'),
			('PIOPPETI ARTIFICIALI', 'Pioppeti artificiali'),
			('BOSCHI IGROFILI', 'Pioppeti naturali'),
			('BOSCHI IGROFILI', 'Plataneto'),
			('ARBUSTETI A CLIMA TEMPERATO', 'Pruneti e corileti'),
			('PIANTAGIONI DI CONIFERE ', 'Pseudotsuga menziesii'),
			('ALTRI BOSCHI CADUCIFOGLI', 'Robineti e ailanteti'),
			('ARBUSTETI SUBALPINI', 'Saliceti alpini'),
			('BOSCHI IGROFILI', 'Saliceti ripariali'),
			('SUGHERETE ', 'Sugherete mediterranee ')
		) as t(categoria, sottocategoria)
	)
insert into FLGCATEGORIE_TAB (NOME_CATEGORIA)
select distinct categoria
from master;


create table FLGSOTTOCATEGORIE_TAB (
	ID_SOTTOCATEGORIA int not null GENERATED ALWAYS AS identity,
	ID_CATEGORIA int not null,
	NOME_SOTTOCATEGORIA varchar not null,
	constraint FLGSOTTOCATEGORIE_PK
		primary key(ID_SOTTOCATEGORIA),
	constraint FLGSOTTOCATEGORIE_UNQ
		unique(NOME_SOTTOCATEGORIA),
	constraint FLGSOTTOCATEGORIE_FK_CAT
		foreign key (ID_CATEGORIA)
		references FLGCATEGORIE_TAB
);

with master as (        
		select *
		from (
		values ('BOSCHI DI ABETE BIANCO', 'Abetina a Campanula'),
			('BOSCHI DI ABETE BIANCO', 'Abetina a Cardamine'),
			('BOSCHI DI ABETE BIANCO', 'Abetina e abeti-faggeta a Vaccinium e Maianthemum'),
			('ALTRI BOSCHI CADUCIFOGLI', 'Acereti appenninici'),
			('ALTRI BOSCHI CADUCIFOGLI', 'Acero-tilieti di monte e boschi di frassino ecc'),
			('ALTRI BOSCHI DI CONIFERE PURI E MISTI', 'Altre formazioni a conifere'),
			('ARBUSTETI A CLIMA TEMPERATO', 'Altre formazioni a ginestre'),
			('PINETE DI PINO NERO, PINO LARICIO E PINO LORICATO', 'Altre formazioni a pino nero e pino loricato'),
			('PINETE DI PINO SILVESTRE E PINO MONTANO', 'Altre formazioni a pino silvestre e pino montano'),
			('ALTRI BOSCHI CADUCIFOGLI', 'Altre formazioni caducifoglie'),
			('BOSCHI DI ABETE ROSSO', 'Altre formazioni con prevalenza di peccio'),
			('BOSCHI DI ABETE BIANCO', 'Altre formazioni di abete bianco'),
			('BOSCHI DI CERRO, FARNETTO, FRAGNO, VALLONEA', 'Altre formazioni di cerro, farnetto, fragno o vallonea'),
			('FAGGETE', 'Altre formazioni di faggio'),
			('BOSCHI DI LARICE E PINO CEMBRO ', 'Altre formazioni di larice e cembro'),
			('BOSCHI DI ROVERE, ROVERELLA E FARNIA', 'Altre formazioni di rovere, roverella e farnia'),
			('BOSCHI IGROFILI', 'Altre formazioni forestali in ambienti umidi'),
			('PIANTAGIONI DI CONIFERE ', 'Altre piantagioni di conifere esotiche'),
			('ARBUSTETI A CLIMA TEMPERATO', 'Altri arbusteti di clima temperato'),
			('MACCHIA, ARBUSTETI MEDITERRANEI ', 'Altri arbusteti sempreverdi'),
			('ARBUSTETI SUBALPINI', 'Altri arbusteti subalpini di aghifoglie'),
			('ARBUSTETI A CLIMA TEMPERATO', 'Arbusteti a ginepro'),
			('ARBUSTETI A CLIMA TEMPERATO', 'Arbusteti a ginestra (Spartium junceum) '),
			('ARBUSTETI A CLIMA TEMPERATO', 'Arbusteti a ginestra dell''Etna (Genista aetnensis)'),
			('ALTRI BOSCHI CADUCIFOGLI', 'Betuleti, boschi montani pionieri '),
			('OSTRIETI, CARPINETI ', 'Boscaglia a carpino orientale'),
			('LECCETE ', 'Boscaglia di leccio'),
			('ALTRI BOSCHI CADUCIFOGLI', 'Boscaglie di Cercis'),
			('ALTRI BOSCHI DI LATIFOGLIE SEMPREVERDI', 'Boscaglie termo-mediterranee'),
			('BOSCHI IGROFILI', 'Boschi a frassino ossifillo e olmo'),
			('BOSCHI IGROFILI', 'Boschi a ontano bianco'),
			('BOSCHI IGROFILI', 'Boschi a ontano nero'),
			('OSTRIETI, CARPINETI ', 'Boschi di carpino bianco'),
			('OSTRIETI, CARPINETI ', 'Boschi di carpino nero e orniello '),
			('BOSCHI DI CERRO, FARNETTO, FRAGNO, VALLONEA', 'Boschi di farnetto'),
			('BOSCHI DI ROVERE, ROVERELLA E FARNIA', 'Boschi di farnia'),
			('BOSCHI DI CERRO, FARNETTO, FRAGNO, VALLONEA', 'Boschi di fragno e nuclei di vallonea'),
			('ALTRI BOSCHI CADUCIFOGLI', 'Boschi di ontano napoletano'),
			('BOSCHI DI ROVERE, ROVERELLA E FARNIA', 'Boschi di rovere'),
			('BOSCHI DI ROVERE, ROVERELLA E FARNIA', 'Boschi di roverella'),
			('ALTRI BOSCHI DI LATIFOGLIE SEMPREVERDI', 'Boschi sempreverdi di ambienti umidi'),
			('LECCETE ', 'Bosco misto di leccio e orniello'),
			('ARBUSTETI SUBALPINI', 'Brughiera subalpina '),
			('CASTAGNETI', 'Castagneti da frutto, selve castanili'),
			('CASTAGNETI', 'Castagneti da legno'),
			('BOSCHI DI CERRO, FARNETTO, FRAGNO, VALLONEA', 'Cerrete collinari e montane'),
			('BOSCHI DI CERRO, FARNETTO, FRAGNO, VALLONEA', 'Cerrete di pianura'),
			('MACCHIA, ARBUSTETI MEDITERRANEI ', 'Cisteti'),
			('FAGGETE', 'Faggete a agrifoglio, felci e campanula'),
			('FAGGETE', 'Faggete acidofile a Luzula'),
			('FAGGETE', 'Faggete mesofile'),
			('FAGGETE', 'Faggete termofile a Cephalanthera'),
			('ARBUSTETI SUBALPINI', 'Formazione ad ontano verde'),
			('ALTRI BOSCHI DI CONIFERE PURI E MISTI', 'Formazioni a cipresso'),
			('MACCHIA, ARBUSTETI MEDITERRANEI ', 'Formazioni a ginepri sul litorale '),
			('BOSCHI DI LARICE E PINO CEMBRO ', 'Lariceto in fustaia chiusa'),
			('BOSCHI DI LARICE E PINO CEMBRO ', 'Larici isolati nella brughiera subalpina'),
			('BOSCHI DI LARICE E PINO CEMBRO ', 'Larici-cembreto'),
			('LECCETE ', 'Lecceta rupicola '),
			('LECCETE ', 'Lecceta termofila costiera '),
			('MACCHIA, ARBUSTETI MEDITERRANEI ', 'Macchia a lentisco'),
			('MACCHIA, ARBUSTETI MEDITERRANEI ', 'Macchia litorale'),
			('ARBUSTETI SUBALPINI', 'Mughete'),
			('SUGHERETE ', 'Pascolo arborato di sughera'),
			('BOSCHI DI ABETE ROSSO', 'Pecceta montana'),
			('BOSCHI DI ABETE ROSSO', 'Pecceta subalpina'),
			('PIANTAGIONI DI CONIFERE ', 'Piantagioni di conifere indigene '),
			('PIANTAGIONI DI ALTRE LATIFOGLIE', 'Piantagioni di eucalipti'),
			('PIANTAGIONI DI ALTRE LATIFOGLIE', 'Piantagioni di latifoglie'),
			('PINETE DI PINO SILVESTRE E PINO MONTANO', 'Pineta (pino silvestre) a carice oppure astragali'),
			('PINETE DI PINO SILVESTRE E PINO MONTANO', 'Pineta (pino silvestre) a erica'),
			('PINETE DI PINO SILVESTRE E PINO MONTANO', 'Pineta (pino silvestre) a farnia e molinia'),
			('PINETE DI PINO SILVESTRE E PINO MONTANO', 'Pineta (pino silvestre) a roverella e citiso a foglie sessili'),
			('PINETE DI PINO NERO, PINO LARICIO E PINO LORICATO', 'Pineta a pino nero a citiso e ginestra'),
			('PINETE DI PINO NERO, PINO LARICIO E PINO LORICATO', 'Pineta a pino nero a erica e orniello'),
			('PINETE DI PINO NERO, PINO LARICIO E PINO LORICATO', 'Pineta a pino nero a pino laricio (Pinus laricio)'),
			('PINETE DI PINO NERO, PINO LARICIO E PINO LORICATO', 'Pineta a pino nero a pino loricato (Pinus leucodermis)'),
			('PINETE DI PINO SILVESTRE E PINO MONTANO', 'Pineta di pino montano'),
			('PINETE DI PINI MEDITERRANEI', 'Pinete a Pinus halepensis'),
			('PINETE DI PINI MEDITERRANEI', 'Pinete a Pinus pinaster'),
			('PINETE DI PINI MEDITERRANEI', 'Pinete a Pinus pinea'),
			('PIANTAGIONI DI CONIFERE ', 'Pinus radiata'),
			('PIOPPETI ARTIFICIALI', 'Pioppeti artificiali'),
			('BOSCHI IGROFILI', 'Pioppeti naturali'),
			('BOSCHI IGROFILI', 'Plataneto'),
			('ARBUSTETI A CLIMA TEMPERATO', 'Pruneti e corileti'),
			('PIANTAGIONI DI CONIFERE ', 'Pseudotsuga menziesii'),
			('ALTRI BOSCHI CADUCIFOGLI', 'Robineti e ailanteti'),
			('ARBUSTETI SUBALPINI', 'Saliceti alpini'),
			('BOSCHI IGROFILI', 'Saliceti ripariali'),
			('SUGHERETE ', 'Sugherete mediterranee ')
		) as t(categoria, sottocategoria)
	)
insert into FLGSOTTOCATEGORIE_TAB (ID_CATEGORIA, NOME_SOTTOCATEGORIA)
select ID_CATEGORIA, sottocategoria
from master
	join FLGCATEGORIE_TAB on (NOME_CATEGORIA = categoria);



create table FLGUNITA_OMOGENEE_CUBATURA_TAB (
	ID_ISTA int not null,
	PROG_UOG int not null,
	COD_METODO_CUBATURA varchar,
	DESC_METODO_CUBATURA varchar,
	ID_SOTTOCATEGORIA int,
	constraint FLGUNITA_OMOGENEE_CUBATURA_PK
		primary key(ID_ISTA, PROG_UOG),
	constraint FLGUNITA_OMOGENEE_CUBATURA_FK_UO
		foreign key(ID_ISTA, PROG_UOG)
		references FLGUNITA_OMOGENEE_TAB
		on delete cascade,
	constraint FLGUNITA_OMOGENEE_CUBATURA_FK_SOTTOCAT
		foreign key (ID_SOTTOCATEGORIA)
		references FLGSOTTOCATEGORIE_TAB
);



create table FLGGRUPPI_CUBATURA_TAB (
	ID_GRUPPO_CUBATURA int not null GENERATED ALWAYS as IDENTITY,
	COD_GRUPPO_CUBATURA varchar not null,
	DESC_GRUPPO_CUBATURA varchar not null,
	constraint FLGGRUPPI_CUBATURA_PK
		primary key (ID_GRUPPO_CUBATURA),
	constraint FLGGRUPPI_CUBATURA_UNQ_COD
		unique (COD_GRUPPO_CUBATURA)
);
insert into FLGGRUPPI_CUBATURA_TAB(COD_GRUPPO_CUBATURA, DESC_GRUPPO_CUBATURA)
values ('PRES', 'Presenti'),
	('RILASCIA', 'Da Rilasciare al Taglio'),
	('TAGLIA', 'Da Tagliare');



create table FLGUNITA_OMOGENEE_VAL_CUBATURA_TAB(
	ID_ISTA int not null,
	PROG_UOG int not null,
	CAT_CUBATURA varchar not null,
	COD_GRUPPO_CUBATURA varchar not null,
	VALORE_NUM_HA numeric,
	VALORE_MQ_HA numeric,
	constraint FLGUNITA_OMOGENEE_VAL_CUBATURA_PK
		primary key (ID_ISTA, PROG_UOG, CAT_CUBATURA, COD_GRUPPO_CUBATURA),
	constraint FLGUNITA_OMOGENEE_VAL_CUBATURA_FK_UO_CUB
		foreign key (ID_ISTA, PROG_UOG)
		references FLGUNITA_OMOGENEE_CUBATURA_TAB
		on delete cascade
);


alter table FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_TAB drop constraint FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_PK;
alter table FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_TAB add
	constraint FLGISTA_TAGLIO_BOSCHIVO_TRATTAMENTO_PK
		primary key (ID_ISTA, ID_GOVE);


create table FLGSUPPORTO_FINANZIARIO_TAB (
	ID_TIPO_FINANZIAMENTO int not null GENERATED ALWAYS as IDENTITY,
	COD_TIPO_FINANZIAMENTO int not null,
	DESC_TIPO_FINANZIAMENTO varchar not null,
	constraint FLGSUPPORTO_FINANZIARIO_PK
		primary key(ID_TIPO_FINANZIAMENTO),
	constraint FLGSUPPORTO_FINANZIARIO_UNQ_COD
		unique (COD_TIPO_FINANZIAMENTO)
);

create table FLGSUPPORTO_FINANZIARIO_ISTA_TAB (
	ID_ISTA int not null,
	COD_TIPO_FINANZIAMENTO int not null,
	DENOM_FONDO varchar,
	constraint FLGSUPPORTO_FINANZIARIO_ISTA_PK
		primary key (ID_ISTA),
	constraint FLGSUPPORTO_FINANZIARIO_ISTA_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB
		on delete cascade,
	constraint FLGSUPPORTO_FINANZIARIO_ISTA_FK_TIPO
		foreign key (COD_TIPO_FINANZIAMENTO)
		references FLGSUPPORTO_FINANZIARIO_TAB(COD_TIPO_FINANZIAMENTO)
);

insert into FLGSUPPORTO_FINANZIARIO_TAB (COD_TIPO_FINANZIAMENTO, DESC_TIPO_FINANZIAMENTO)
	values (1, 'Fondi del Piano di Sviluppo Rurale'),
		(2, 'Fondi di progetti LIFE'),
		(3, 'Fondi Nazionali'),
		(4, 'Fondi Regionali'),
		(5, 'Altri fondi Europei (eg. horizon 2020) '),
		(6, 'Altri finanziamenti'),
		(7, 'Nessun finanziamento');

create table FLGATTUAZIONE_PIANI_ISTA_TAB (
	ID_ISTA int not null,
	NOME_PGF varchar not null,
	NOME_COMPRESA_FORESTALE varchar not null,
	OGGETTO varchar not null,
	constraint FLGATTUAZIONE_PIANI_ISTA_PK
		primary key (ID_ISTA),
	constraint FLGATTUAZIONE_PIANI_ISTA_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB
		on delete cascade
);

alter table flgpart_catastali_tab drop
	constraint flgpart_catastali_unq_part;

alter table flgpart_catastali_tab add
	constraint flgpart_catastali_unq_part
		unique(id_ista, id_comune, sezione, foglio, particella, sub);


create table FLGRICHIESTE_ISTUTTORIA_ISTANZA_TAB (
	ID_RICHIESTE_ISTUTTORIA_ISTANZA int not null GENERATED ALWAYS AS identity,
	ID_ISTA int not null,
	ID_UTENTE_ISTRUTTORE int not null,
	CATEGORIA varchar,
	TIPO_DOCUMENTO varchar,
	NOTE_ISTRUTTORE varchar,
	DATA_RICHIESTA timestamp without time zone not null,
	constraint FLGRICHIESTE_ISTUTTORIA_ISTANZA_PK
		primary key (ID_RICHIESTE_ISTUTTORIA_ISTANZA),
	constraint FLGRICHIESTE_ISTUTTORIA_ISTANZA_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB,
	constraint FLGRICHIESTE_ISTUTTORIA_ISTANZA_FK_ISTR
		foreign key (ID_UTENTE_ISTRUTTORE)
		references FLGUTEN_TAB
);

create index FLGRICHIESTE_ISTUTTORIA_ISTANZA_IDX_ISTA on FLGRICHIESTE_ISTUTTORIA_ISTANZA_TAB(ID_ISTA);

create table FLGDOCUMENTI_ISTUTTORIA_ISTANZA_TAB (
	ID_RICHIESTE_ISTUTTORIA_ISTANZA int not null,
	ID_UTENTE_GESTORE int not null,
	ID_FILE int not null,
	NOTE_GESTORE varchar,
	DATA_CONSEGNA timestamp without time zone,
	constraint FLGDOCUMENTI_ISTUTTORIA_ISTANZA_PK
		primary key (ID_RICHIESTE_ISTUTTORIA_ISTANZA),
	constraint FLGDOCUMENTI_ISTUTTORIA_ISTANZA_FK_ISTA
		foreign key (ID_RICHIESTE_ISTUTTORIA_ISTANZA)
		references FLGRICHIESTE_ISTUTTORIA_ISTANZA_TAB
		on delete cascade,
	constraint FLGDOCUMENTI_ISTUTTORIA_ISTANZA_FK_FILE
		foreign key (ID_FILE)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB,
	constraint FLGDOCUMENTI_ISTUTTORIA_ISTANZA_FK_GEST
		foreign key (ID_UTENTE_GESTORE)
		references FLGUTEN_TAB
);

create index FLGDOCUMENTI_ISTUTTORIA_ISTANZA_IDX_ISTA on FLGDOCUMENTI_ISTUTTORIA_ISTANZA_TAB(ID_RICHIESTE_ISTUTTORIA_ISTANZA);
create index FLGDOCUMENTI_ISTUTTORIA_ISTANZA_IDX_FILE on FLGDOCUMENTI_ISTUTTORIA_ISTANZA_TAB(ID_FILE);


alter table flgvalutazione_istanza_tab add
	column id_utente_istruttore int not null;
alter table flgvalutazione_istanza_tab add
	constraint flgvalutazione_istanza_fk_istr
	foreign key (id_utente_istruttore)
	references flguten_tab;


alter table flgcist_tab add column mesi_validita int;

-- Per Umbria tutto a 24 mesi
update flgcist_tab
set mesi_validita = durata
from (
	values('SOTTO_SOGLIA', 18),
		('SOPRA_SOGLIA', 18),
		('IN_DEROGA', 24),
		('ATTUAZIONE_PIANI', 18)
) as T(tipo, durata)
where t.tipo = cod_tipo_istanza

alter table flgcist_tab alter column mesi_validita set not null;

create table FLGISTA_PROROGA_TAB (
	ID_ISTA int not null,
	MESI_DURATA int not null,
	ID_FILE_PAGAMENTO int not null,
	MOTIVAZIONE varchar,
	DATA_PROROGA timestamp without time zone,
	UTENTE_PROROGA int not null,
	constraint FLGISTA_PROROGA_PK
		primary key (ID_ISTA),
	constraint FLGISTA_PROROGA_FK_ISTA
		foreign key (ID_ISTA)
		references FLGISTA_TAB,
	constraint FLGISTA_PROROGA_FK_FILE
		foreign key (ID_FILE_PAGAMENTO)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB,
	constraint FLGISTA_PROROGA_FK_UTE
		foreign key (UTENTE_PROROGA)
		references FLGUTEN_TAB
);

alter table flgvalutazione_istanza_tab add
	column data_fine_validita date;

ALTER TABLE foliage2.flgrile_tab DROP CONSTRAINT flgrile_tab_flgista_tab_fk;
ALTER TABLE foliage2.flgrile_tab ADD CONSTRAINT flgrile_tab_flgista_tab_fk FOREIGN KEY (id_ista) REFERENCES foliage2.flgista_tab(id_ista) ON DELETE CASCADE;


CREATE TABLE foliage2.flgfoto_tab (
	id_foto int4 NOT NULL,
	nome varchar NULL,
	file bytea NULL,
	id_rile numeric(20) NULL,
	flag_valido int2 NULL,
	user_ins varchar NULL,
	data_ins date NULL,
	user_upd varchar NULL,
	data_upd date NULL,
	data_ini_vali date NOT NULL,
	data_fine_vali date NULL,
	CONSTRAINT flgfoto_tab_pkey PRIMARY KEY (id_foto),
	CONSTRAINT flgfoto_rile_tab_fk1 FOREIGN KEY (id_rile) REFERENCES foliage2.flgrile_tab(id_rile) on delete cascade
);

alter table flgqual_tab drop column note;
alter table flgqual_tab drop column flag_valido;
alter table flgqual_tab drop column user_ins;
alter table flgqual_tab drop column data_ins;
alter table flgqual_tab drop column user_upd;
alter table flgqual_tab drop column data_upd;
alter table flgqual_tab drop column data_ini_vali;
alter table flgqual_tab drop column data_fine_vali;
alter table flgqual_tab drop column codi_regi;
alter table flgqual_tab add column codi_qual varchar null;


with new_data as (
		select *,
			row_number() OVER () as rownum
		from (
				values ('PROPRIETARIO', 'Proprietario'),
					('DELEGATO', 'Delegato del proprietario'),
					('COMPROPRIETARIO', 'Comproprietario'),
					('AFFITTUARIO', 'Affittuario'),
					('RAPPRESENTANTE', 'Rappresentante legale'),
					('COMODATO', 'Titolare di comodato d''uso'),
					('ALTRO', 'Altro titolo di possesso del soprasuolo')
			) as T(codi_qual, desc_qual)
	), curr_data as (
		select *,
			row_number() OVER (order by id_qual) as rownum
		from foliage2.flgqual_tab
	)
update foliage2.flgqual_tab t
set (codi_qual, desc_qual) = (
		select n.codi_qual, n.desc_qual
		from curr_data as c
			join new_data as n using (rownum)
		where c.id_qual = t.id_qual
	);


with new_data as (
		select *,
			row_number() OVER () as rownum
		from (
				values ('PROPRIETARIO', 'Proprietario'),
					('DELEGATO', 'Delegato del proprietario'),
					('COMPROPRIETARIO', 'Comproprietario'),
					('AFFITTUARIO', 'Affittuario'),
					('RAPPRESENTANTE', 'Rappresentante legale'),
					('COMODATO', 'Titolare di comodato d''uso'),
					('ALTRO', 'Altro titolo di possesso del soprasuolo')
			) as T(codi_qual, desc_qual)
	)
insert into foliage2.flgqual_tab(id_qual, codi_qual, desc_qual)
select nextval('foliage2.flgqual_seq') as id_qual,
	codi_qual, desc_qual
from new_data as n
where n.codi_qual not in (
		select c.codi_qual
		from foliage2.flgqual_tab as c
	);


alter table FLGISTA_STORICO_GESTORI_TAB alter column id_utente_gestore_precedente DROP NOT NULL;

alter table flgfiletipo_gestione_tab add column id_file_atto_nomina_rappresentante_legale int;
alter table flgfiletipo_gestione_tab add column id_file_delega_presentazione int;
alter table flgfiletipo_gestione_tab add column id_file_provvedimento_boschi_silenti int;
alter table flgfiletipo_gestione_tab add column id_file_autocertificazione_ditta_forestale int;
alter table flgfiletipo_gestione_tab add column id_file_documenti_identita int;

alter table flgfiletipo_gestione_tab add
	constraint flgfiletipo_gestione_fk_atto_nomina_rappresentante_legale
		foreign key (id_file_atto_nomina_rappresentante_legale)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB;
alter table flgfiletipo_gestione_tab add
	constraint flgfiletipo_gestione_fk_delega_presentazione
		foreign key (id_file_delega_presentazione)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB;
alter table flgfiletipo_gestione_tab add
	constraint flgfiletipo_gestione_fk_provvedimento_boschi_silenti
		foreign key (id_file_provvedimento_boschi_silenti)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB;
alter table flgfiletipo_gestione_tab add
	constraint flgfiletipo_gestione_fk_autocertificazione_ditta_forestale
		foreign key (id_file_autocertificazione_ditta_forestale)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB;
alter table flgfiletipo_gestione_tab add
	constraint flgfiletipo_gestione_fk_documenti_identita
		foreign key (id_file_documenti_identita)
		references FLGBASE64_FORMIO_FILE_MASTER_TAB;


alter table flguten_tab add column id_comune int;
alter table flguten_tab add
	constraint flguten_fk_comune
		foreign key(id_comune)
		references flgente_comune_tab;
alter table flguten_tab add column num_civico varchar;

update flguten_tab u
set id_comune = (
		select id_comune
		from flgcomu_viw c
		where upper(c.comune) = upper(u.citta)
	);

alter table flgtitolare_istanza_tab add column id_comune int;
alter table flgtitolare_istanza_tab add column cap varchar;
alter table flgtitolare_istanza_tab add column indirizzo varchar;
alter table flgtitolare_istanza_tab add column num_civico varchar;
alter table flgtitolare_istanza_tab add column telefono varchar;
alter table flgtitolare_istanza_tab add column genere varchar;

create table flgtavole_istanza_tab (
	id_tavola_ista int not null GENERATED ALWAYS AS identity,
	id_ista int not null,
	prog_tavola int not null,
	id_file_tavola int not null,
	id_utente int not null, 
	data_caricamento date not null,
	constraint flgtavole_istanza_pk
		primary key (id_tavola_ista),
	constraint flgtavole_istanza_unq
		unique (id_ista, prog_tavola),
	constraint flgtavole_istanza_fk_file
		foreign key (id_file_tavola)
		references flgbase64_formio_file_master_tab
);

alter table foliage2.flguten_tab alter column user_name set not null


ALTER TABLE foliage2.flgrile_tab ALTER COLUMN data_ini_vali DROP NOT NULL;
ALTER TABLE foliage2.flgfoto_tab ALTER COLUMN data_ini_vali DROP NOT NULL;


alter table flgrile_tab add column id_utente int;
alter table flgrile_tab add column tipo_auth varchar;
alter table flgrile_tab add column tipo_ambito varchar;
alter table flgrile_tab add
	constraint flgrile_fk_utente
		foreign key(id_utente)
		references flguten_tab
		on delete cascade;
alter table flgrile_tab add
	constraint flgrile_fk_profilo
		foreign key(tipo_auth, tipo_ambito)
		references flgprof_tab(tipo_auth, tipo_ambito);

update flgrile_tab
set id_utente = (
		select id_uten
		from flguten_tab ft 
		where user_name = 'mcampelli'
	),
	(tipo_auth, tipo_ambito) = (
		select tipo_auth, tipo_ambito
		from flgprof_tab
		where descrizione = 'Professionista forestale'
	);

alter table flgrile_tab alter column id_utente set not null;
alter table flgrile_tab alter column tipo_auth set not null;
alter table flgrile_tab alter column tipo_ambito set not null;

alter table FLGPART_CATASTALI_TAB add column SUPERFICIE_INTERVENTO int default 0 not null;


alter table FOLIAGE2.FLGISTA_INVIO_TAB add ID_FILE_DIRITTI_ISTRUTTORIA int;
update FOLIAGE2.FLGISTA_INVIO_TAB
set ID_FILE_DIRITTI_ISTRUTTORIA = ID_FILE_RICEVUTE;
alter table FOLIAGE2.FLGISTA_INVIO_TAB alter ID_FILE_DIRITTI_ISTRUTTORIA set not null;


create table FOLIAGE2.FLGISTA_PROGRESSIVI_TAB (
	ANNO int not null,
	PROG_SUCCESSIVO int not null,
	constraint FLGISTA_PROGRESSIVI_PK
		primary key (ANNO)
);

create table FOLIAGE2.FLGSCHEDE_TIPOISTANZA_TAB (
	ID_TIPO_ISTANZA int not null,
	PROG_SCHEDA int not null,
	COD_SCHEDA varchar not null,
	IS_OBBLIGATORIA boolean not null,
	constraint FLGSCHEDE_ISTANZA_PK
		primary key (ID_TIPO_ISTANZA, PROG_SCHEDA)
);


create table FOLIAGE2.FLGISTA_SCHEDE_SALVATE_TAB (
	ID_ISTA int not null,
	PROG_SCHEDA int not null,
	DATA_ULTIMO_SALVATAGGIO timestamp without time zone not null default localtimestamp,
	ID_UTENTE_SALVATAGGIO int not null,
	constraint FLGISTA_SCHEDE_SALVATE_PK
		primary key (ID_ISTA, PROG_SCHEDA)
);

alter table FOLIAGE2.FLGISTA_TAB add column HAS_VISIONE_VINCOLI bool;

insert into FLGSCHEDE_TIPOISTANZA_TAB(ID_TIPO_ISTANZA, PROG_SCHEDA, COD_SCHEDA, IS_OBBLIGATORIA)
select TI.ID_TIPO_ISTANZA, T.PROG_SCHEDA, T.COD_SCHEDA, T.IS_OBBLIGATORIA
from ( values
		--('TIPOLOGIA', 0, null, true),
		--('TITOLARE', 1, null, true),
		('TIPO_GESTIONE', 2, null, true),
		('PARTICELLE', 3, null, true),
		('INTERVENTO', 4, null, true),
		--sotto
		('NATURA_2K', 5, ('{4, 5}')::int[], true),
		--taglio boschivo
		('SOPRASSUOLO', 6, ('{4}')::int[], true),
		('ASSORTIMENTI', 7, ('{4}')::int[], true),
		--int comunicazione
		('AMBITI_NON_FORESTALI', 6, ('{5}')::int[], true),
		--values ('CARATTERIZZAZIONE', 5, ('{1, 2, 3}')::int[], true),
		('VINCOLISTICA', 6, ('{1, 2, 3}')::int[], true),
		('UNITA_OMOGENEE', 7, ('{1, 2, 3}')::int[], true),
		('ALTRI_STRATI', 8, ('{1, 2, 3}')::int[], false),
		('VIABILITA', 9, ('{1, 2, 3}')::int[], false),
		('PROSPETTI', 10, ('{1, 2, 3}')::int[], true),
		('ALLEGATI', 11, ('{1, 2, 3}')::int[], false),
		('RIEPILOGO', 12, ('{1, 2, 3}')::int[], true)
	) as T(COD_SCHEDA, PROG_SCHEDA, TIPI_ISTANZA, IS_OBBLIGATORIA)
	join flgtipo_istanza_tab ti on (T.TIPI_ISTANZA is null or ti.id_tipo_istanza = any(T.TIPI_ISTANZA));


alter table FLGPART_CATASTALI_TAB alter column SUPERFICIE_INTERVENTO drop not null;

alter table FOLIAGE2.FLGISTA_INVIO_TAB add column ID_FILE_MODULO_ISTANZA int;

ALTER TABLE foliage2.flgista_invio_tab drop CONSTRAINT flgista_invio_fk_file;

ALTER TABLE foliage2.flgista_invio_tab ADD CONSTRAINT flgista_invio_fk_file_ricevute FOREIGN KEY (id_file_ricevute) REFERENCES foliage2.flgbase64_formio_file_master_tab(id_file);
ALTER TABLE foliage2.flgista_invio_tab ADD CONSTRAINT flgista_invio_fk_file_diritti FOREIGN KEY (id_file_diritti_istruttoria) REFERENCES foliage2.flgbase64_formio_file_master_tab(id_file);
ALTER TABLE foliage2.flgista_invio_tab ADD CONSTRAINT flgista_invio_fk_file_modulo FOREIGN KEY (id_file_modulo_istanza) REFERENCES foliage2.flgbase64_formio_file_master_tab(id_file);



create table FOLIAGE2.FLGISTA_DATI_ISTRUTTORIA_TAB(
	ID_ISTA int not null,
	OGGETTO varchar not null,
	ULTERIORI_DESTINATARI varchar,
	TESTO varchar,
	constraint FLGISTA_DATI_ISTRUTTORIA_FK_ISTA
		foreign key (ID_ISTA)
		references FOLIAGE2.FLGISTA_TAB
);

ALTER TABLE foliage2.flgista_dati_istruttoria_tab ADD CONSTRAINT flgista_dati_istruttoria_pk PRIMARY KEY (id_ista);

alter table FOLIAGE2.FLGVALUTAZIONE_ISTANZA_TAB add column ID_FILE_MODULO_ISTRUTTORIA int;
alter table FOLIAGE2.FLGVALUTAZIONE_ISTANZA_TAB add constraint FLGVALUTAZIONE_ISTANZA_FK_FILE_MODULO foreign key (ID_FILE_MODULO_ISTRUTTORIA) references FOLIAGE2.FLGBASE64_FORMIO_FILE_MASTER_TAB(ID_FILE);


--------------------------------
-- qui sotto ancora da eseguire

----------------------------------

alter table foliage2.flgbase64_formio_file_master_tab drop column id_file_old;


CREATE FUNCTION foliage2.flgdelete_base64_formio_file() RETURNS trigger AS $$
declare
	nome_campo varchar;
	old_record json;
	valore_str varchar;
	valore_int int;
begin
	nome_campo = TG_ARGV[0];
	RAISE NOTICE 'nome_campo: %', nome_campo;
	old_record = to_json(old);
	RAISE NOTICE 'old_record: %', old_record;
	valore_str = old_record->>nome_campo;
	RAISE NOTICE 'valore_str: %', valore_str;
	if (valore_str is not null) then
		valore_int = valore_str::int;
		RAISE NOTICE 'valore_int: %', valore_int;
		delete from foliage2.flgbase64_formio_file_master_tab f
		where f.id_file = valore_int;
	end if;
	return old;
end;
$$ LANGUAGE plpgsql;


-- with file_fk as (
-- 		select *
-- 		from (
-- 				select kcu.*
-- 				from information_schema.table_constraints tc
-- 					join information_schema.key_column_usage kcu using (constraint_catalog, constraint_schema, constraint_name)
-- 				where tc.constraint_schema = 'foliage2'
-- 					and constraint_name in (
-- 						select rc.constraint_name
-- 						from information_schema.referential_constraints rc
-- 						where constraint_schema = 'foliage2'
-- 							and unique_constraint_name = 'flgbase64_formio_file_master_pk'
-- 					)
-- 				order by table_name, column_name
-- 			) t1
-- 		where not exists (
-- 				select *
-- 				from information_schema.triggers t2 
-- 				--order by event_object_table, trigger_name
-- 				where t2.event_object_table = t1.table_name
-- 					and t2.trigger_name = 'post_upd_'||t1.column_name
-- 			)
-- 	)
-- select table_name, column_name,
-- 'CREATE TRIGGER POST_UPD_'||column_name||'
--    AFTER UPDATE ON foliage2.'||table_name||'
--    REFERENCING OLD TABLE AS old
--    FOR EACH row
--    WHEN (OLD.'||column_name||' IS DISTINCT FROM NEW.'||column_name||')
--    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('''||column_name||''');' as trigger_upd,
-- 'CREATE TRIGGER POST_DEL_'||column_name||'
--    AFTER DELETE ON foliage2.'||table_name||'
--    REFERENCING OLD TABLE AS oldtab
--    FOR EACH row
--    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('''||column_name||''');' as trigger_del
-- from (
-- 		select table_name, column_name
-- 		from file_fk
-- 	) as t
-- order by table_name, column_name;




CREATE TRIGGER POST_UPD_id_file_allegato
    AFTER UPDATE ON foliage2.flgallegati_ista_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_allegato IS DISTINCT FROM NEW.id_file_allegato)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_allegato');
CREATE TRIGGER POST_UPD_id_file
    AFTER UPDATE ON foliage2.flgbase64_formio_file_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file IS DISTINCT FROM NEW.id_file)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file');
CREATE TRIGGER POST_UPD_id_file
    AFTER UPDATE ON foliage2.flgdocumenti_istuttoria_istanza_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file IS DISTINCT FROM NEW.id_file)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file');
CREATE TRIGGER POST_UPD_id_file_atto_nomina_rappresentante_legale
    AFTER UPDATE ON foliage2.flgfiletipo_gestione_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_atto_nomina_rappresentante_legale IS DISTINCT FROM NEW.id_file_atto_nomina_rappresentante_legale)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_atto_nomina_rappresentante_legale');
CREATE TRIGGER POST_UPD_id_file_autocertificazione_ditta_forestale
    AFTER UPDATE ON foliage2.flgfiletipo_gestione_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_autocertificazione_ditta_forestale IS DISTINCT FROM NEW.id_file_autocertificazione_ditta_forestale)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_autocertificazione_ditta_forestale');
CREATE TRIGGER POST_UPD_id_file_autocertificazione_proprieta
    AFTER UPDATE ON foliage2.flgfiletipo_gestione_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_autocertificazione_proprieta IS DISTINCT FROM NEW.id_file_autocertificazione_proprieta)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_autocertificazione_proprieta');
CREATE TRIGGER POST_UPD_id_file_delega_presentazione
    AFTER UPDATE ON foliage2.flgfiletipo_gestione_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_delega_presentazione IS DISTINCT FROM NEW.id_file_delega_presentazione)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_delega_presentazione');
CREATE TRIGGER POST_UPD_id_file_delega_titolarita
    AFTER UPDATE ON foliage2.flgfiletipo_gestione_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_delega_titolarita IS DISTINCT FROM NEW.id_file_delega_titolarita)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_delega_titolarita');
CREATE TRIGGER POST_UPD_id_file_documenti_identita
    AFTER UPDATE ON foliage2.flgfiletipo_gestione_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_documenti_identita IS DISTINCT FROM NEW.id_file_documenti_identita)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_documenti_identita');
CREATE TRIGGER POST_UPD_id_file_provvedimento_boschi_silenti
    AFTER UPDATE ON foliage2.flgfiletipo_gestione_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_provvedimento_boschi_silenti IS DISTINCT FROM NEW.id_file_provvedimento_boschi_silenti)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_provvedimento_boschi_silenti');
CREATE TRIGGER POST_UPD_id_file_vinca
    AFTER UPDATE ON foliage2.flgista_elaborato_vinca_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_vinca IS DISTINCT FROM NEW.id_file_vinca)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_vinca');
CREATE TRIGGER POST_UPD_id_file_diritti_istruttoria
    AFTER UPDATE ON foliage2.flgista_invio_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_diritti_istruttoria IS DISTINCT FROM NEW.id_file_diritti_istruttoria)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_diritti_istruttoria');
CREATE TRIGGER POST_UPD_id_file_modulo_istanza
    AFTER UPDATE ON foliage2.flgista_invio_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_modulo_istanza IS DISTINCT FROM NEW.id_file_modulo_istanza)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_modulo_istanza');
CREATE TRIGGER POST_UPD_id_file_ricevute
    AFTER UPDATE ON foliage2.flgista_invio_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_ricevute IS DISTINCT FROM NEW.id_file_ricevute)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_ricevute');
CREATE TRIGGER POST_UPD_id_file_pagamento
    AFTER UPDATE ON foliage2.flgista_proroga_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_pagamento IS DISTINCT FROM NEW.id_file_pagamento)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_pagamento');
CREATE TRIGGER POST_UPD_id_file_atto_nomina
    AFTER UPDATE ON foliage2.flgrichieste_responsabile_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_atto_nomina IS DISTINCT FROM NEW.id_file_atto_nomina)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_atto_nomina');
CREATE TRIGGER POST_UPD_id_file_doc_identita
    AFTER UPDATE ON foliage2.flgrichieste_responsabile_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_doc_identita IS DISTINCT FROM NEW.id_file_doc_identita)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_doc_identita');
CREATE TRIGGER POST_UPD_id_file_tavola
    AFTER UPDATE ON foliage2.flgtavole_istanza_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_tavola IS DISTINCT FROM NEW.id_file_tavola)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_tavola');
CREATE TRIGGER POST_UPD_id_file_delega
    AFTER UPDATE ON foliage2.flgtitolare_istanza_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_delega IS DISTINCT FROM NEW.id_file_delega)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_delega');
CREATE TRIGGER POST_UPD_id_file_modulo_istruttoria
    AFTER UPDATE ON foliage2.flgvalutazione_istanza_tab
    REFERENCING OLD TABLE AS old
    FOR EACH row
    WHEN (OLD.id_file_modulo_istruttoria IS DISTINCT FROM NEW.id_file_modulo_istruttoria)
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_modulo_istruttoria');

CREATE TRIGGER POST_DEL_id_file_allegato
    AFTER DELETE ON foliage2.flgallegati_ista_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_allegato');
CREATE TRIGGER POST_DEL_id_file
    AFTER DELETE ON foliage2.flgbase64_formio_file_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file');
CREATE TRIGGER POST_DEL_id_file
    AFTER DELETE ON foliage2.flgdocumenti_istuttoria_istanza_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file');
CREATE TRIGGER POST_DEL_id_file_atto_nomina_rappresentante_legale
    AFTER DELETE ON foliage2.flgfiletipo_gestione_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_atto_nomina_rappresentante_legale');
CREATE TRIGGER POST_DEL_id_file_autocertificazione_ditta_forestale
    AFTER DELETE ON foliage2.flgfiletipo_gestione_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_autocertificazione_ditta_forestale');
CREATE TRIGGER POST_DEL_id_file_autocertificazione_proprieta
    AFTER DELETE ON foliage2.flgfiletipo_gestione_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_autocertificazione_proprieta');
CREATE TRIGGER POST_DEL_id_file_delega_presentazione
    AFTER DELETE ON foliage2.flgfiletipo_gestione_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_delega_presentazione');
CREATE TRIGGER POST_DEL_id_file_delega_titolarita
    AFTER DELETE ON foliage2.flgfiletipo_gestione_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_delega_titolarita');
CREATE TRIGGER POST_DEL_id_file_documenti_identita
    AFTER DELETE ON foliage2.flgfiletipo_gestione_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_documenti_identita');
CREATE TRIGGER POST_DEL_id_file_provvedimento_boschi_silenti
    AFTER DELETE ON foliage2.flgfiletipo_gestione_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_provvedimento_boschi_silenti');
CREATE TRIGGER POST_DEL_id_file_vinca
    AFTER DELETE ON foliage2.flgista_elaborato_vinca_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_vinca');
CREATE TRIGGER POST_DEL_id_file_diritti_istruttoria
    AFTER DELETE ON foliage2.flgista_invio_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_diritti_istruttoria');
CREATE TRIGGER POST_DEL_id_file_modulo_istanza
    AFTER DELETE ON foliage2.flgista_invio_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_modulo_istanza');
CREATE TRIGGER POST_DEL_id_file_ricevute
    AFTER DELETE ON foliage2.flgista_invio_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_ricevute');
CREATE TRIGGER POST_DEL_id_file_pagamento
    AFTER DELETE ON foliage2.flgista_proroga_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_pagamento');
CREATE TRIGGER POST_DEL_id_file_atto_nomina
    AFTER DELETE ON foliage2.flgrichieste_responsabile_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_atto_nomina');
CREATE TRIGGER POST_DEL_id_file_doc_identita
    AFTER DELETE ON foliage2.flgrichieste_responsabile_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_doc_identita');
CREATE TRIGGER POST_DEL_id_file_tavola
    AFTER DELETE ON foliage2.flgtavole_istanza_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_tavola');
CREATE TRIGGER POST_DEL_id_file_delega
    AFTER DELETE ON foliage2.flgtitolare_istanza_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_delega');
CREATE TRIGGER POST_DEL_id_file_modulo_istruttoria
    AFTER DELETE ON foliage2.flgvalutazione_istanza_tab
    REFERENCING OLD TABLE AS oldtab
    FOR EACH row
    EXECUTE FUNCTION foliage2.flgdelete_base64_formio_file('id_file_modulo_istruttoria');


delete
from flgbase64_formio_file_master_tab fffmt 
where id_file not in (
		select id_file_allegato
		from flgallegati_ista_tab
		where id_file_allegato is not null
		union all
		select id_file
		from flgbase64_formio_file_tab
		where id_file is not null
		union all
		select id_file
		from flgdocumenti_istuttoria_istanza_tab
		where id_file is not null
		union all
		select id_file_atto_nomina_rappresentante_legale
		from flgfiletipo_gestione_tab
		where id_file_atto_nomina_rappresentante_legale is not null
		union all
		select id_file_autocertificazione_ditta_forestale
		from flgfiletipo_gestione_tab
		where id_file_autocertificazione_ditta_forestale is not null
		union all
		select id_file_autocertificazione_proprieta
		from flgfiletipo_gestione_tab
		where id_file_autocertificazione_proprieta is not null
		union all
		select id_file_delega_presentazione
		from flgfiletipo_gestione_tab
		where id_file_delega_presentazione is not null
		union all
		select id_file_delega_titolarita
		from flgfiletipo_gestione_tab
		where id_file_delega_titolarita is not null
		union all
		select id_file_documenti_identita
		from flgfiletipo_gestione_tab
		where id_file_documenti_identita is not null
		union all
		select id_file_provvedimento_boschi_silenti
		from flgfiletipo_gestione_tab
		where id_file_provvedimento_boschi_silenti is not null
		union all
		select id_file_vinca
		from flgista_elaborato_vinca_tab
		where id_file_vinca is not null
		union all
		select id_file_diritti_istruttoria
		from flgista_invio_tab
		where id_file_diritti_istruttoria is not null
		union all
		select id_file_modulo_istanza
		from flgista_invio_tab
		where id_file_modulo_istanza is not null
		union all
		select id_file_ricevute
		from flgista_invio_tab
		where id_file_ricevute is not null
		union all
		select id_file_pagamento
		from flgista_proroga_tab
		where id_file_pagamento is not null
		union all
		select id_file_atto_nomina
		from flgrichieste_responsabile_tab
		where id_file_atto_nomina is not null
		union all
		select id_file_doc_identita
		from flgrichieste_responsabile_tab
		where id_file_doc_identita is not null
		union all
		select id_file_tavola
		from flgtavole_istanza_tab
		where id_file_tavola is not null
		union all
		select id_file_delega
		from flgtitolare_istanza_tab
		where id_file_delega is not null
		union all
		select id_file_modulo_istruttoria
		from flgvalutazione_istanza_tab
		where id_file_modulo_istruttoria is not null
	);

alter table foliage2.FLGRICHIESTE_PROFILI_TAB drop constraint FLGRICHIESTE_PROFILI_CK_ANNUL;



delete from foliage2.FLGSPECI_UOG_TAB;
delete from foliage2.flgass_speci_ista_tab;
delete from foliage2.FLGSPECI_ISTA_TAB;
alter table FOLIAGE2.FLGSPECIE_TAB drop constraint flgspecie_fk_cat;
alter table FOLIAGE2.FLGSPECIE_TAB drop column id_categoria;
alter table FOLIAGE2.FLGSPECIE_TAB add column TIPO_SOPRASUOLO varchar;
alter table foliage2.FLGUNITA_OMOGENEE_CUBATURA_TAB drop column id_sottocategoria;


delete from FOLIAGE2.FLGSPECIE_TAB;
insert into FOLIAGE2.FLGSPECIE_TAB(TIPO_SOPRASUOLO, NOME_SPECIE, NOME_SCENTIFICO)
select --SPLIT_PART(T.line, '	', 1)::int as id_specie,
	SPLIT_PART(T.line, '	', 2) as tipo_soprasuolo,
	SPLIT_PART(T.line, '	', 3) as nome,
	SPLIT_PART(T.line, '	', 4) as nome_scentifico
from unnest(
		STRING_TO_ARRAY(
'1	Latifoglia	Acero campestre	Acer campestre
2	Latifoglia	Acero di monte	Acer pseudoplatanus
3	Latifoglia	Acero d''Ungheria	Acer opalus obtusatum
4	Latifoglia	Betulla	Betula pendula
5	Latifoglia	Carpino bianco	Carpinus betulus
6	Latifoglia	Carpino nero	Ostrya carpinifolia
7	Latifoglia	Castagno	Castanea sativa
8	Latifoglia	Cerro	Quercus cerris
9	Latifoglia	Ciliegio selvatico	Prunus avium
10	Conifera	Cipresso	Cupressus spp
11	Latifoglia	Corbezzolo	Arbutus unedo
12	Latifoglia	Eucalipto spp	Eucalyptus spp
13	Latifoglia	Faggio	Fagus sylvatica
14	Latifoglia	Farnetto	Quercus frainetto
15	Latifoglia	Farnia	Quercus robur
16	Latifoglia	Fragno	Quercus trojana
17	Latifoglia	Frassino maggiore	Fraxinus excelsior
18	Latifoglia	Ilatro comune	Phyllirea latifolia
19	Latifoglia	Leccio	Quercus ilex
20	Latifoglia	Nocciolo	Corylus avellana
21	Latifoglia	Olmo comune	Ulmus minor
22	Latifoglia	Ontano napoletano	Alnus cordata
23	Latifoglia	Ontano nero	Alnus glutinosa
24	Latifoglia	Orniello	Fraxinus ornus
25	Conifera	Pino domestico	Pinus pinea
26	Conifera	Pino d''Aleppo	Pinus halepensis
27	Conifera	Pino laricio	Pinus nigra laricio
28	Conifera	Pino marittimo	Pinus pinaster
29	Conifera	Pino nero	Pinus nigra
30	Latifoglia	Pioppo nero	Populus nigra
31	Latifoglia	Pioppo tremulo	Populus tremula
32	Latifoglia	Robinia	Robinia pseudoacacia
33	Latifoglia	Roverella	Quercus pubescens
34	Latifoglia	Rovere	Quercus petraea
35	Latifoglia	Salicone	Salix caprea
36	Latifoglia	Tiglio selvatico	Tilia cordata
37	Latifoglia	Sughera	Quercus suber',
'
'
		)
	) as T(line);

drop table FOLIAGE2.flgcategorie_specie_tab;
alter table FOLIAGE2.flgista_taglio_boschivo_tab drop constraint flgista_taglio_boschivo_fk_tipo;
alter table FOLIAGE2.flgunita_omogenee_tab  drop constraint flgunita_omogenee_fk_tipo;
drop table FOLIAGE2.flgmacrocategorie_specie_tab;

delete from FOLIAGE2.FLGSOTTOCATEGORIE_TAB;
delete from FOLIAGE2.FLGCATEGORIE_TAB;

alter table FOLIAGE2.FLGCATEGORIE_TAB add cod_categoria int;


insert into FOLIAGE2.FLGCATEGORIE_TAB(cod_categoria, nome_categoria)
select SPLIT_PART(T.line, '	', 1)::int as cod_categoria,
	SPLIT_PART(T.line, '	', 2) as nome/*,
	SPLIT_PART(T.line, '	', 3)='TRUE' as is_lazio,
	SPLIT_PART(T.line, '	', 4)='TRUE' as is_umbria*/
from unnest(
		STRING_TO_ARRAY(
'1	BOSCHI DI LARICE E PINO CEMBRO	FALSE	FALSE
2	BOSCHI DI ABETE ROSSO	FALSE	FALSE
3	BOSCHI DI ABETE BIANCO	FALSE	FALSE
4	PINETE DI PINO SILVESTRE E PINO MONTANO	FALSE	FALSE
5	PINETE DI PINO NERO, PINO LARICIO E PINO LORICATO	TRUE	TRUE
6	PINETE DI PINI MEDITERRANEI	TRUE	TRUE
7	ALTRI BOSCHI DI CONIFERE PURE O MISTE	TRUE	TRUE
8	FAGGETE	TRUE	TRUE
9	QUERCETI A ROVERE, ROVERELLA E FARNIA	TRUE	TRUE
10	CERRETE, BOSCHI DI FARNETTO, FRAGNO E VALLONEA	TRUE	TRUE
11	CASTAGNETI	TRUE	TRUE
12	OSTRIETI, CARPINETI	TRUE	TRUE
13	BOSCHI IGROFILI	TRUE	TRUE
14	ALTRI BOSCHI CADUCIFOGLI	TRUE	TRUE
15	LECCETE	TRUE	TRUE
16	SUGHERETE	TRUE	TRUE
17	ALTRI BOSCHI DI LATIFOGLIE SEMPREVERDI	TRUE	TRUE
18	PIOPPETI ARTIFICIALI	FALSE	FALSE
19	PIANTAGIONI DI ALTRE LATIFOGLIE	FALSE	FALSE
20	PIANTAGIONI DI CONIFERE	FALSE	FALSE
21	ARBUSTETI SUBALPINI	FALSE	FALSE
22	ARBUSTETI A CLIMA TEMPERATO	FALSE	FALSE
23	MACCHIA, ARBUSTETI MEDITERRANEI	FALSE	FALSE',
'
'
		)
	) as T(line);


create table FOLIAGE2.FLGCATEGORIE_REGIONI_TAB(
	ID_CATEGORIA int not null,
	ID_REGIONE int not null,
	constraint FLGCATEGORIE_REGIONI_PK
		primary key (ID_CATEGORIA, ID_REGIONE),
	constraint FLGCATEGORIE_REGIONI_FK_CATEGORIA
		foreign key (ID_CATEGORIA)
		references FOLIAGE2.FLGCATEGORIE_TAB,
	constraint FLGCATEGORIE_REGIONI_FK_REGIONE
		foreign key (ID_REGIONE)
		references FOLIAGE2.flgente_regione_tab
);

insert into FOLIAGE2.FLGCATEGORIE_REGIONI_TAB(ID_CATEGORIA, ID_REGIONE)
select id_categoria, id_regione
from FOLIAGE2.flgregi_viw fv
	cross join (
		select SPLIT_PART(T.line, '	', 1)::int as cod_categoria,
			SPLIT_PART(T.line, '	', 2) as nome,
			SPLIT_PART(T.line, '	', 3)='TRUE' as is_lazio,
			SPLIT_PART(T.line, '	', 4)='TRUE' as is_umbria
		from unnest(
				STRING_TO_ARRAY(
'1	BOSCHI DI LARICE E PINO CEMBRO	FALSE	FALSE
2	BOSCHI DI ABETE ROSSO	FALSE	FALSE
3	BOSCHI DI ABETE BIANCO	FALSE	FALSE
4	PINETE DI PINO SILVESTRE E PINO MONTANO	FALSE	FALSE
5	PINETE DI PINO NERO, PINO LARICIO E PINO LORICATO	TRUE	TRUE
6	PINETE DI PINI MEDITERRANEI	TRUE	TRUE
7	ALTRI BOSCHI DI CONIFERE PURE O MISTE	TRUE	TRUE
8	FAGGETE	TRUE	TRUE
9	QUERCETI A ROVERE, ROVERELLA E FARNIA	TRUE	TRUE
10	CERRETE, BOSCHI DI FARNETTO, FRAGNO E VALLONEA	TRUE	TRUE
11	CASTAGNETI	TRUE	TRUE
12	OSTRIETI, CARPINETI	TRUE	TRUE
13	BOSCHI IGROFILI	TRUE	TRUE
14	ALTRI BOSCHI CADUCIFOGLI	TRUE	TRUE
15	LECCETE	TRUE	TRUE
16	SUGHERETE	TRUE	TRUE
17	ALTRI BOSCHI DI LATIFOGLIE SEMPREVERDI	TRUE	TRUE
18	PIOPPETI ARTIFICIALI	FALSE	FALSE
19	PIANTAGIONI DI ALTRE LATIFOGLIE	FALSE	FALSE
20	PIANTAGIONI DI CONIFERE	FALSE	FALSE
21	ARBUSTETI SUBALPINI	FALSE	FALSE
22	ARBUSTETI A CLIMA TEMPERATO	FALSE	FALSE
23	MACCHIA, ARBUSTETI MEDITERRANEI	FALSE	FALSE',
'
'
				)
			) as T(line)
	) as T
	join FOLIAGE2.FLGCATEGORIE_TAB C using (cod_categoria)
where (regione = 'LAZIO' and is_lazio)
	or (regione = 'UMBRIA' and is_umbria);



insert into FOLIAGE2.FLGSOTTOCATEGORIE_TAB(id_categoria, nome_sottocategoria)
select id_categoria, nome_sottocategoria
from (
		select SPLIT_PART(T.line, '	', 1)::int as cod_categoria,
			SPLIT_PART(T.line, '	', 2)::int as id_sottocat,
			SPLIT_PART(T.line, '	', 3) as nome_sottocategoria
		from unnest(
				STRING_TO_ARRAY(
'1	1	Altre formazioni di larice e cembro
1	2	Lariceto in fustaia chiusa
1	3	Larici isolati nella brughiera subalpina
1	4	Larici-cembreto
2	5	Altre formazioni con prevalenza di peccio
2	6	Pecceta montana
2	7	Pecceta subalpina
3	8	Abetina a Campanula
3	9	Abetina a Cardamine
3	10	Abetina e abeti-faggeta a Vaccinium e Maianthemum
3	11	Altre formazioni di abete bianco
4	12	Altre formazioni a pino silvestre e pino montano
4	13	Pineta (pino silveste) a carice oppure astragali
4	14	Pineta (pino silveste) a erica
4	15	Pineta (pino silveste) a farnia e molinia
4	16	Pineta (pino silveste) a roverella e citiso a foglie sessili
4	17	Pineta di pino montano
5	18	Altre formazioni a pino nero e pino loricato
5	19	Pineta a pino nero a citiso e ginestra
5	20	Pineta a pino nero a erica e orniello
5	21	Pineta a pino nero a pino laricio (Pinus laricio)
5	22	Pineta a pino nero a pino loricato (Pinus leucodermis)
6	23	Pinete a Pinus halepensis
6	24	Pinete a Pinus pinaster
6	25	Pinete a Pinus pinea
7	26	Altre formazioni a conifere
7	27	Formazioni a cipresso
8	28	Altre formazioni di faggio
8	29	Faggete a agrifoglio, felci e campanula
8	30	Faggete acidofile a Luzula
8	31	Faggete mesofile
8	32	Faggete termofile a Cephalanthera
9	33	Altre formazioni di rovere, roverella e farnia
9	34	Boschi di farnia
9	35	Boschi di rovere
9	36	Boschi di roverella
10	37	Altre formazioni di cerro, farnetto, fragno o vallonea
10	38	Boschi di farnetto
10	39	Boschi di fragno e nuclei di vallonea
10	40	Cerrete collinari e montane
10	41	Cerrete di pianura
11	42	Castagneti da frutto, selve castanili
11	43	Castagneti da legno
12	44	Boscaglia a carpino orientale
12	45	Boschi di carpino bianco
12	46	Boschi di carpino nero e orniello
13	47	Altre formazioni forestali in ambienti umidi
13	48	Boschi a frassino ossifillo e olmo
13	49	Boschi a ontano bianco
13	50	Boschi a ontano nero
13	51	Pioppeti naturali
13	52	Plataneto
13	53	Saliceti ripariali
14	54	Acereti appenninici
14	55	Acero-tilieti di monte e boschi di frassino ecc.
14	56	Altre formazioni caducifoglie
14	57	Betuleti, boschi montani pioneri
14	58	Boscaglie di Cercis
14	59	Boschi di ontano napoletano
14	60	Robineti e ailanteti
15	61	Boscaglia di leccio
15	62	Bosco misto di leccio e orniello
15	63	Lecceta rupicola
15	64	Lecceta termofila costiera
16	65	Pascolo arborato di sughera
16	66	Sugherete mediterranee
17	67	Boscaglie termo-mediterranee
17	68	Boschi sempreverdi di ambienti umidi
18	69	Pioppeti artificiali
19	70	Piantagioni di eucalipti
19	71	Piantagioni di latifoglie
20	72	Altre piantagioni di conifere esotiche
20	73	Piantagioni di conifere indigene
20	74	Pinus radiata
20	75	Pseudotsuga menziesii
21	76	Altri arbusteti subalpini di aghifoglie
21	77	Brughiera subalpina
21	78	Formazione ad ontano verde
21	79	Mughete
21	80	Saliceti alpini
22	81	Altre formazioni a ginestre
22	82	Altri arbusteti di clima temperato
22	83	Arbusteti a ginepro
22	84	Arbusteti a ginestra (Spartium junceum)
22	85	Arbusteti a ginestra dell''Etna (Genista aetnensis)
22	86	Pruneti e corileti
23	87	Altri arbusteti sempreverdi
23	88	Cisteti
23	89	Formazioni a ginepri sul litorale
23	90	Macchia a lentisco
23	91	Macchia litorale',
'
'
				)
			) as T(line)
	) as T
	join FOLIAGE2.FLGCATEGORIE_TAB using (cod_categoria);


alter table FOLIAGE2.FLGUNITA_OMOGENEE_TAB add column id_categoria int;
alter table FOLIAGE2.FLGUNITA_OMOGENEE_TAB add
	constraint FLGUNITA_OMOGENEE_FK_CATEGORIA
		foreign key(id_categoria)
		references FOLIAGE2.FLGCATEGORIE_TAB;
update FOLIAGE2.FLGUNITA_OMOGENEE_TAB
set id_categoria = (
		select min(s.id_categoria)
		from FOLIAGE2.FLGCATEGORIE_TAB s
	);
alter table FOLIAGE2.FLGUNITA_OMOGENEE_TAB alter column id_categoria set not null;



alter table FOLIAGE2.FLGUNITA_OMOGENEE_TAB add column id_sottocategoria int;
alter table FOLIAGE2.FLGUNITA_OMOGENEE_TAB add
	constraint FLGUNITA_OMOGENEE_FK_SOTTOCATEGORIA
		foreign key(id_sottocategoria)
		references FOLIAGE2.FLGSOTTOCATEGORIE_TAB;



delete from foliage2.flgvincoli_ista_tab;
delete from foliage2.flgviabilita_ista_tab;
delete from foliage2.flgunita_omogenee_trattamento_tab;
delete from foliage2.flgunita_omogenee_val_cubatura_tab;
delete from foliage2.flgunita_omogenee_cubatura_tab;
delete from foliage2.flgstrati_ista_tab;
delete from foliage2.flgass_speci_uog_tab;
delete from foliage2.flgspeci_uog_tab;
delete from foliage2.flgunita_omogenee_tab;
delete from foliage2.flgsupporto_finanziario_ista_tab;
delete from foliage2.flgstrati_ista_tab;
delete from foliage2.flgass_speci_ista_tab;
delete from foliage2.flgspeci_ista_tab;
delete from foliage2.flgrisposte_wizard_vincolistica_tab;
delete from foliage2.flgfoto_tab;
delete from foliage2.flgrile_tab;
delete from foliage2.flgdocumenti_istuttoria_istanza_tab;
delete from foliage2.flgrichieste_istuttoria_istanza_tab;
delete from foliage2.flgparticella_forestale_tab;
delete from foliage2.flgparticella_forestale_shape_tab;
delete from foliage2.flgpart_catastali_tab;
delete from foliage2.flgista_taglio_boschivo_trattamento_tab;
delete from foliage2.flgista_taglio_boschivo_tab;
delete from foliage2.flgista_storico_gestori_tab;
delete from foliage2.flgista_proroga_tab;
delete from foliage2.flgista_invio_tab;
delete from foliage2.flgista_intervento_comunicazione_tab;
delete from foliage2.flgista_elaborato_vinca_tab;
delete from foliage2.flgista_dati_istruttoria_tab;
delete from foliage2.flgissi_tab;
delete from foliage2.flgispr_tab;
delete from foliage2.flgfiletipo_gestione_tab;
delete from foliage2.flgdate_fine_lavori_istanza_tab;
delete from foliage2.flgdate_inizio_lavori_istanza_tab;
delete from foliage2.flgattuazione_piani_ista_tab;
delete from foliage2.flgvalutazione_istanza_tab;
delete from foliage2.flgassegnazione_istanza_tab;
delete from foliage2.flgallegati_ista_tab;
delete from foliage2.flgista_tab;
delete from foliage2.flgtitolare_istanza_tab;


alter table foliage2.flgparticella_forestale_tab add column slope_raster raster;
alter table foliage2.flgparticella_forestale_tab add column dem_raster raster;


----------------
--- Ancora da eseguire in lazio

alter table foliage2.flgista_tab drop column id_cist;

alter table foliage2.flgcist_tab add column durata_timer_autoaccettazione interval;
---umbria
update foliage2.flgcist_tab
set durata_timer_autoaccettazione = (
		select t.durata
		from (
				values ('SOPRA_SOGLIA', '90 days'::interval),
					--('ATTUAZIONE_PIANI', '90 days'::interval),
					--('IN_DEROGA', '90 days'::interval),
					('SOTTO_SOGLIA', '15 days'::interval)
			) as t(Cod_tipo_istanza, durata)
		where t.Cod_tipo_istanza = foliage2.flgcist_tab.Cod_tipo_istanza
	)
where Cod_tipo_istanza not in ('IN_DEROGA', 'ATTUAZIONE_PIANI');

---lazio
update foliage2.flgcist_tab
set durata_timer_autoaccettazione = '2 months'::interval
where Cod_tipo_istanza not in ('IN_DEROGA', 'ATTUAZIONE_PIANI');


drop table foliage2.flgprofili_report_tab;
drop table foliage2.flgerror_batch_tab;
drop table foliage2.flgexecuted_batch_tab;
drop table foliage2.flgpending_batch_tab;
drop table foliage2.flgconf_batch_report_tab;
drop table foliage2.flgconf_batch_tab;


create table foliage2.flgconf_batch_tab (
	id_batch int not null GENERATED ALWAYS AS IDENTITY,
	cod_batch varchar not null,
	desc_batch varchar,
	data_partenza timestamp without time zone,
	intervallo_frequenza interval not null,
	intervallo_offset interval,
	has_recupero_esecuzioni_mancanti boolean not null,
	constraint flgconf_batch_pk
		primary key (id_batch),
	constraint flgconf_batch_unq
		unique (cod_batch)
);


create table foliage2.flgconf_batch_report_tab (
	id_report int not null GENERATED ALWAYS AS IDENTITY,
	id_batch int not null,
	desc_report varchar not null,
	cod_report varchar not null,
	report_name varchar not null,
	formato_files varchar[] not null,
	formato_data_desc varchar not null,
	formato_data_file varchar not null,
	constraint flgconf_batch_report_pk
		primary key (id_report),
	constraint flgconf_batch_report_unq
		unique (cod_report),
	constraint flgconf_batch_report_fk_batch
		foreign key (id_batch)
		references foliage2.flgconf_batch_tab
);

INSERT INTO foliage2.flgconf_batch_tab (
		cod_batch, desc_batch,
		data_partenza, 
		intervallo_frequenza, intervallo_offset, has_recupero_esecuzioni_mancanti
	)
	values (
			'AUTO_ACCETTAZIONE', 'Batch giornaliero di autoaccettazione istanze presentate da troppo tempo e non valutate',
			date'2023-01-01',
			'1 days'::interval, null, false
		),(
			'REPORT_P1_2_M', 'Batch di generazione report Prodotto 1/2 mensile',
			date'2023-01-01',
			'1 months'::interval, '1 days'::interval, true
		),(
			'REPORT_P1_2_A', 'Batch di generazione report Prodotto 1/2 annuale',
			date'2023-01-01',
			'1 years'::interval, '5 months 9 days'::interval, true
		),(
			'REPORT_P3', 'Batch di generazione report Prodotto 3',
			date'2023-01-01',
			'1 years'::interval, '0 months 9 days'::interval, true
		),(
			'REPORT_P4', 'Batch di generazione report Prodotto 4',
			date'2023-01-01',
			'1 years'::interval, '5 months 9 days'::interval, true
		);


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
				'AUTO_ACCETTAZIONE', 'AUTO_ACCETTAZIONE', 'Report delle istanze accettate automaticamente dopo lo scadere dei temini previsti dalla presentazione',
				'IstanzeAutoAccettazione', 'EEEE d MMMM yyyy', '{ xlsx, pdf, csv }'::varchar[],'yyyyMM'
			),(
				'REPORT_P1_2_M', 'P1_M', 'Report Prodotto 1 mensile: Elenco di tutte le istanze presentate',
				'ReportP1mese', 'MMMM yyyy', '{ xlsx, pdf, csv }'::varchar[], 'yyyyMM'
			),(
				'REPORT_P1_2_A', 'P1_A', 'Report Prodotto 1 annuale: Elenco di tutte le istanze presentate',
				'ReportP1anno', 'yyyy', '{ xlsx, pdf, csv }'::varchar[], 'yyyy'
			),(
				'REPORT_P1_2_M', 'P2_M', 'Report Prodotto 2 mensile: Cartografia recante le poligonazioni o i centroidi delle aree oggetto d''intervento selvicolturale (SRID: EPSG:3857)',
				'ReportP2mese', 'MMMM yyyy', '{ GeoJSON }'::varchar[], 'yyyyMM'
			),(
				'REPORT_P1_2_A', 'P2_A', 'Report Prodotto 2 annuale: Cartografia recante le poligonazioni o i centroidi delle aree oggetto d''intervento selvicolturale (SRID: EPSG:3857)',
				'ReportP2anno', 'yyyy', '{ GeoJSON }'::varchar[], 'yyyy'
			),(
				'REPORT_P3', 'P3_NAT1', 'Report Prodotto 3 nat1: Natura2000 (Stakeholder MITE) - localizzazione dei tagli boschivi autorizzati (SRID: EPSG:3857)',
				'ReportP3Nat1', 'yyyy', '{ GeoJSON, xlsx, pdf, csv }'::varchar[], 'yyyy'
			),(
				'REPORT_P3', 'P3_NAT2', 'Report Prodotto 3 nat2: Natura2000 (Stakeholder MITE) - disturbi agli ecosistemi forestali dei siti ReteNatura2000 (SRID: EPSG:3857)',
				'ReportP3Nat2', 'yyyy', '{ GeoJSON, xlsx, pdf, csv }'::varchar[], 'yyyy'
			),(
				'REPORT_P4', 'P4', 'Report Prodotto 4: Prodotto di sintesi delle statistiche forestali fruibile per il RaF (Stakeholder MASAF, ISTAT, ISPRA)',
				'ReportP4', 'yyyy', '{ xlsx, pdf, csv }'::varchar[], 'yyyy'
			)
	) as t(cod_batch, cod_report, desc_report, report_name, formato_data_desc, formato_files, formato_data_file)
	join foliage2.flgconf_batch_tab b using (cod_batch);

create table foliage2.flgprofili_report_tab (
	id_report int not null,
	id_profilo int not null,
	constraint flgprofili_report_pk
		primary key (id_report, id_profilo)
);

insert into foliage2.flgprofili_report_tab(id_report, id_profilo)
select id_report, id_profilo
from (
		values (
				'P1_M', '{Responsabile di Sede, Responsabile del Servizio}'::varchar[]
			), (
				'P1_A', '{Responsabile di Sede, Responsabile del Servizio}'::varchar[]
			), (
				'P2_M', '{Responsabile di Sede, Responsabile del Servizio}'::varchar[]
			), (
				'P2_A', '{Responsabile di Sede, Responsabile del Servizio}'::varchar[]
			), (
				'P3_NAT1', '{Responsabile del Servizio}'::varchar[]
			), (
				'P3_NAT2', '{Responsabile del Servizio}'::varchar[]
			), (
				'P4', '{Responsabile del Servizio}'::varchar[]
			), (
				'AUTO_ACCETTAZIONE', '{Responsabile di Sede, Responsabile del Servizio}'::varchar[]
			)
	) as t1(cod_report, arr_profili)
	join foliage2.flgconf_batch_report_tab using (cod_report)
	cross join lateral (
		select desc_profilo
		from unnest(t1.arr_profili) as t(desc_profilo)
	) as t2(desc_profilo)
	join foliage2.flgprof_tab p on (p.descrizione = t2.desc_profilo);

create table foliage2.flgpending_batch_tab (
	id_pend_batch int not null GENERATED ALWAYS AS IDENTITY,
	id_batch int not null,
	data_batch timestamp without time zone not null,
	data_rife timestamp without time zone not null,
	data_submission timestamp without time zone not null,
	constraint flgpending_batch_pk
		primary key (id_pend_batch),
	constraint flgpending_batch_fk_batch
		foreign key (id_batch)
		references foliage2.flgconf_batch_tab,
	constraint flgpending_batch_unq
		unique (id_batch, data_rife)
);

create table foliage2.flgexecuted_batch_tab (
	id_exec_batch int not null GENERATED ALWAYS AS IDENTITY,
	id_batch int not null,
	data_batch timestamp without time zone not null,
	data_rife timestamp without time zone not null,
	data_submission timestamp without time zone not null,
	data_avvio timestamp without time zone not null,
	data_termine timestamp without time zone not null,
	num_record_elaborati int not null,
	constraint flgexecuted_batch_pk
		primary key (id_exec_batch),
	constraint flgexecuted_batch_fk_batch
		foreign key (id_batch)
		references foliage2.flgconf_batch_tab,
	constraint flgexecuted_batch_unq
		unique (id_batch, data_rife)
);

create table foliage2.flgerror_batch_tab (
	id_err_batch int not null GENERATED ALWAYS AS IDENTITY,
	id_batch int not null,
	data_batch timestamp without time zone not null,
	data_rife timestamp without time zone not null,
	data_submission timestamp without time zone not null,
	data_avvio timestamp without time zone not null,
	data_termine timestamp without time zone not null,
	dett_errore varchar not null,
	constraint flgerror_batch_pk
		 primary key (id_err_batch),
	constraint flgerror_batch_fk_batch
		foreign key (id_batch)
		references foliage2.flgconf_batch_tab
);

insert into foliage2.flguten_tab(id_uten, user_name, flag_accettazione)
	OVERRIDING SYSTEM VALUE values(-1, 'UTENTE_AUTOACCETTAZIONE', false);

create table foliage2.flgreport_autoaccettazione_istanze_tab (
	data_rife timestamp without time zone not null,
	id_ista int not null,
	id_ente_terr int not null,
	constraint flgreport_autoaccettazione_istanze_pk
		primary key (id_ista),
	constraint flgreport_autoaccettazione_istanze_fk_ista
		foreign key (id_ista)
		references foliage2.flgista_tab
);

create index flgreport_autoaccettazione_istanze_idx_data on foliage2.flgreport_autoaccettazione_istanze_tab(data_rife);

create table foliage2.flgreport_p4_tab (
	data_rife timestamp without time zone not null,
	cod_indicatore varchar not null,
	numero_istanze int not null,
	numero_istanze_autorizzate int not null,
	numero_istanze_non_autorizzate int not null,
	supe_istanze_autorizzate numeric not null,
	supe_privata numeric not null,
	supe_pubblica numeric not null,
	supe_uso_civico numeric not null,
	supe_altro numeric not null,
	supe_ceduo numeric not null,
	supe_fustaia numeric not null,
	supe_misto numeric not null,
	vol_totale numeric not null,
	vol_ardere_conifere numeric not null,
	vol_ardere_nonconifere numeric not null,
	vol_legname_conifere numeric not null,
	vol_legname_nonconifere numeric not null,
	vol_impiallaccitura_conifere numeric not null,
	vol_impiallaccitura_nonconifere numeric not null,
	vol_paste_conifere numeric not null,
	vol_paste_nonconifere numeric not null,
	vol_altro_conifere numeric not null,
	vol_altro_nonconifere numeric not null,
	supe_cat_1  numeric not null,
	supe_cat_2  numeric not null,
	supe_cat_3  numeric not null,
	supe_cat_4  numeric not null,
	supe_cat_5  numeric not null,
	supe_cat_6  numeric not null,
	supe_cat_7  numeric not null,
	supe_cat_8  numeric not null,
	supe_cat_9  numeric not null,
	supe_cat_10  numeric not null,
	supe_cat_11  numeric not null,
	supe_cat_12  numeric not null,
	supe_cat_13  numeric not null,
	supe_cat_14  numeric not null,
	supe_cat_15  numeric not null,
	supe_cat_16  numeric not null,
	supe_cat_17  numeric not null,
	supe_cat_18  numeric not null,
	supe_cat_19  numeric not null,
	supe_cat_20  numeric not null,
	supe_cat_21  numeric not null,
	supe_cat_22  numeric not null,
	supe_cat_23  numeric not null,
	constraint flgreport_p4_pk
		primary key(data_rife, cod_indicatore),
	constraint flgreport_p4_ck_cod_indicatore
		check (cod_indicatore in ('A', 'B'))
);


create materialized view foliage2.flgcodi_istat_enti_mvw as
select id_comune as id_ente_terr, 
	r.codi_istat as codi_istat_regione,
	p.codi_istat as codi_istat_provincia,
	c.codi_istat as codi_istat_comune
from foliage2.flgente_comune_tab c
	join foliage2.flgente_provincia_tab p on (p.id_provincia = c.id_provincia)
	join foliage2.flgente_regione_tab r on (r.id_regione = p.id_regione)
union all
select id_provincia as id_ente_terr, 
	r.codi_istat as codi_istat_regione,
	p.codi_istat as codi_istat_provincia,
	'' as codi_istat_comune
from foliage2.flgente_provincia_tab p
	join foliage2.flgente_regione_tab r on (r.id_regione = p.id_regione)
union all
select id_regione as id_ente_terr,
	codi_istat as codi_istat_regione,
	'' as codi_istat_provincia,
	'' as codi_istat_comune
from foliage2.flgente_regione_tab r;



create index flgcodi_istat_enti_pk on foliage2.flgcodi_istat_enti_mvw(id_ente_terr);

create table foliage2.flgreport_p1_2_tab (
	data_rife date not null,
	durata interval not null,
	id varchar not null,
	prog_uog int not null,
	tipologia varchar not null, 
	data date not null,
	id_ente_terr int not null,
	stato varchar not null,
	id_prop varchar not null, 
	id_prof varchar not null,
	tratt_uo varchar not null,
	supe_uo numeric not null,
	vol_uo numeric not null,
	shape geometry not null,
	constraint flgreport_p1m_unq
		unique (data_rife, durata, id, prog_uog, tratt_uo)
);

alter table foliage_extra.sitiprotetti_natura_2000 add
	constraint sitiprotetti_natura_2000_unq
		unique (codice);

create table foliage2.flgreport_p3_tab (
	data_rife date not null,
	codice varchar not null,
	id_ista int not null,
	esito_valutazione boolean not null,
	shape_vinc geometry not null,
	superficie_vinc numeric not null,
	superficie_pf numeric not null,
	superficie_utile numeric not null,
	massa numeric not null,
	perc_dist numeric not null,
	constraint flgreport_p3_unq
		unique (data_rife, codice, id_ista)
);



create table foliage2.flgutenti_ok_tab (
	user_name varchar not null,
	constraint flgutenti_ok_pk
		primary key(user_name)
);


----------------
--- Qui sotto da eseguire in lazio


create table foliage2.flgbatch_scheduling_tab (
	id_batch int not null,
	data_partenza timestamp without time zone,
	intervallo_frequenza interval not null,
	intervallo_offset interval,
	has_recupero_esecuzioni_mancanti boolean not null,
	constraint flgbatch_scheduling_pk
		primary key (id_batch),
	constraint flgbatch_scheduling_fk_batch
		foreign key (id_batch)
		references foliage2.flgconf_batch_tab
);

insert into foliage2.flgbatch_scheduling_tab(
		id_batch, data_partenza, intervallo_frequenza, intervallo_offset, has_recupero_esecuzioni_mancanti
	)
select id_batch, data_partenza, intervallo_frequenza, intervallo_offset, has_recupero_esecuzioni_mancanti
from foliage2.flgconf_batch_tab;

alter table foliage2.flgconf_batch_tab drop column data_partenza;
alter table foliage2.flgconf_batch_tab drop column intervallo_frequenza;
alter table foliage2.flgconf_batch_tab drop column intervallo_offset;
alter table foliage2.flgconf_batch_tab drop column has_recupero_esecuzioni_mancanti;

create table foliage2.flgbatch_ondemand_tab (
	id_batch_ondemand int not null GENERATED ALWAYS AS identity,
	id_batch int not null,
	data_inserimento timestamp without time zone not null,
	data_rife timestamp without time zone not null, 
	parametri json,
	id_utente int not null,
	constraint flgbatch_ondemand_pk
		primary key (id_batch_ondemand),
	constraint flgbatch_ondemand_unq
		unique (id_batch, data_rife),
	constraint flgbatch_ondemand_fk_batch
		foreign key (id_batch)
		references foliage2.flgconf_batch_tab,
	constraint flgbatch_ondemand_fk_utente
		foreign key (id_utente)
		references foliage2.flguten_tab
);

ALTER TABLE foliage2.flgtitolare_istanza_tab ALTER COLUMN genere SET NOT NULL;
ALTER TABLE foliage2.flgtitolare_istanza_tab ALTER COLUMN num_civico SET NOT NULL;
ALTER TABLE foliage2.flgtitolare_istanza_tab ALTER COLUMN pec DROP NOT NULL;
ALTER TABLE foliage2.flgtitolare_istanza_tab ALTER COLUMN email DROP NOT NULL;

ALTER TABLE foliage2.flgautocert_prof_tab add column pec varchar;
ALTER TABLE foliage2.flgallegati_ista_tab ALTER COLUMN cod_tipo_allegato DROP NOT NULL;



alter table foliage2.flgutenti_ok_tab rename to flgutenti_white_list_tab;
alter table foliage2.flgutenti_white_list_tab rename constraint flgutenti_ok_pk to flgutenti_white_list_pk;
alter table foliage2.flgutenti_white_list_tab add column data_ins timestamp without time zone default localtimestamp;

create table foliage2.flgutenti_black_list_tab (
	user_name varchar not null,
	data_ins timestamp without time zone default localtimestamp,
	constraint flgutenti_black_list_pk
		primary key(user_name)
);

create table foliage2.flgserver_requests_tab (
	thread_name varchar not null,
	ip_addres varchar not null,
	hostname varchar not null,
	username varchar,
	http_method varchar not null,
	requested_path varchar not null,
	request_query varchar,
	ora_inizio timestamp without time zone not null,
	durata interval,
	http_status_code int not null,
	errore varchar
);

alter table foliage2.flgbatch_ondemand_tab add data_avvio timestamp without time zone;

update foliage2.flgbatch_ondemand_tab 
set data_avvio = data_rife;


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

--- eseguire da qui in produzione

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

INSERT INTO foliage2.flgconf_batch_tab (
		cod_batch, desc_batch
	)
	values (
			'MONITORAGGIO_SAT', 'Batch per l''elaborazione del monitoraggio satellitare'
		);


alter table foliage2.flgserver_requests_tab add column headers varchar;

--- eseguire da qui in locale
--- eseguire da qui in test

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
