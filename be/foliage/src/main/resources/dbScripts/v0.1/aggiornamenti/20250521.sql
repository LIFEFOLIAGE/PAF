
update foliage2.flgconf_batch_report_tab
set desc_report = replace(desc_report, 'SRID: EPSG:3857', 'SRID: EPSG:3035');

update foliage2.flgconf_batch_report_tab
set desc_report = 'Report di monitoraggio per i tagli boschivi potenzialmente illegali (SRID: EPSG:3035)'
where cod_report = 'ALERT';

create view foliage2.flginspire_1_viw as
select i.id_ista * 1000 + t.prog_uog as id, cti.desc_cist as tipo_di_istanza,
	v.data_valutazione as data_approvazione,
	t.superficie as superficie_totale,
	t.supe_uo as superficie_autorizzata,
	s.nome_sottocategoria as sottocategoria_forestale,
	t.nome_specie1 as specie_forestale_1,
	t.nome_specie2 as specie_forestale_2,
	t.desc_gove as forma_di_governo,
	st_setsrid(shape, 3035) as geom
from foliage2.flgista_tab i
	join foliage2.flgtipo_istanza_tab ti on (ti.id_tipo_istanza = i.id_tipo_istanza)
	join foliage2.flgcist_tab cti on (cti.id_cist = ti.id_cist)
	join foliage2.flgvalutazione_istanza_tab v using (id_ista)
	cross join lateral (
		select uo.prog_uog, round(uo.superficie/10000, 4) as superficie,
			round((superficie_utile/10000), 4) as supe_uo, uo.id_sottocategoria,
			s1.nome_specie1, s1.nome_specie2, coalesce(uo.desc_gove, 'Misto') as desc_gove,
			uo.shape
		from foliage2.flgunita_omogenee_tab uo
			cross join lateral (
				select max(case when suo.prog_specie_uog = 0 then s.nome_specie end) as nome_specie1,
					max(case when suo.prog_specie_uog = 1 then s.nome_specie end) as nome_specie2
				from foliage2.flgspeci_uog_tab suo
					join foliage2.flgspecie_tab s using (id_specie)
				where suo.id_ista = uo.id_ista
					and suo.prog_uog = uo.prog_uog
			) s1
		where uo.id_ista = i.id_ista
		union all
		select 0 as prog_uog, round((st_area(pf.shape)/10000)::numeric, 2) as superficie,
			round((itb.superficie_intervento/10000), 2) as supe_uo, null::int as id_sottocategoria,
			s2.nome_specie1, s2.nome_specie2, coalesce(itb.desc_gove, 'Misto') as desc_gove,
			pf.shape
		from foliage2.flgista_taglio_boschivo_tab itb
			cross join lateral (
				select st_union(pf.shape) as shape
				from foliage2.flgparticella_forestale_shape_tab pf
				where id_ista = itb.id_ista
			) as pf
			cross join lateral (
				select max(case when si.prog = 0 then s.nome_specie end) as nome_specie1,
					max(case when si.prog = 1 then s.nome_specie end) as nome_specie2
				from foliage2.flgspeci_ista_tab si
					join foliage2.flgspecie_tab s using (id_specie)
				where si.id_ista = itb.id_ista
			) s2
		where itb.id_ista = i.id_ista
	) t
	left join foliage2.flgsottocategorie_tab s using (id_sottocategoria)
where v.esito_valutazione;
