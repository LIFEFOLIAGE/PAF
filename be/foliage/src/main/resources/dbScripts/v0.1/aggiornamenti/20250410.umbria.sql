set search_path=foliage2, public;

insert into FOLIAGE2.FLGPRIVACY_POLICY_TAB(cod_privacy_policy, data_rilascio)
	values ('V2', date'2025-04-10');

update FOLIAGE2.FLGPRIVACY_POLICY_TAB
set is_current = null;

update FOLIAGE2.FLGPRIVACY_POLICY_TAB
set is_current = true
where cod_privacy_policy = 'V2';
