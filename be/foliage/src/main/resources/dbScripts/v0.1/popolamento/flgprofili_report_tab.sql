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
