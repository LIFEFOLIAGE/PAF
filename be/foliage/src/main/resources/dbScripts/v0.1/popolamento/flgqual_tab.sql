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
