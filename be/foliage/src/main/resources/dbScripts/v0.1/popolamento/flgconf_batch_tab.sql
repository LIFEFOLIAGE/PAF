INSERT INTO foliage2.flgconf_batch_tab (
		cod_batch, desc_batch/*,
		data_partenza, 
		intervallo_frequenza, intervallo_offset, has_recupero_esecuzioni_mancanti*/
	)
	values (
			'AUTO_ACCETTAZIONE', 'Batch giornaliero di autoaccettazione istanze presentate da troppo tempo e non valutate'/*,
			date'2023-01-01',
			'1 days'::interval, null, false*/
		),(
			'REPORT_P1_2_M', 'Batch di generazione report Prodotto 1/2 mensile'/*,
			date'2023-01-01',
			'1 months'::interval, '1 days'::interval, true*/
		),(
			'REPORT_P1_2_A', 'Batch di generazione report Prodotto 1/2 annuale'/*,
			date'2023-01-01',
			'1 years'::interval, '5 months 9 days'::interval, true*/
		),(
			'REPORT_P3', 'Batch di generazione report Prodotto 3'/*,
			date'2023-01-01',
			'1 years'::interval, '0 months 9 days'::interval, true*/
		),(
			'REPORT_P4', 'Batch di generazione report Prodotto 4'/*,
			date'2023-01-01',
			'1 years'::interval, '5 months 9 days'::interval, true*/
		),(
			'MONITORAGGIO_SAT', 'Batch per l''elaborazione del monitoraggio satellitare'
		);
