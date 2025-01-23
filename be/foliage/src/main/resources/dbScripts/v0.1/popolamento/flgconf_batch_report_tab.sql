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
