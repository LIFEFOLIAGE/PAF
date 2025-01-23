insert into foliage2.flgcist_tab (
		id_cist, desc_cist, note, desc_regi_ammi, flag_valido,
		user_ins, data_ins, user_upd, data_upd, data_ini_vali, data_fine_vali,
		nome,
		descrizione_lunga,
		tipo_auth, flag_senior, cod_tipo_istanza,
		tipo_ente, mesi_validita, durata_timer_autoaccettazione
	) values (
		3, 'Istanza sopra soglia', NULL, 'Regime di comunicazione (autorizzazione silente)', 1,
		'admin', '2021-11-04', NULL, NULL, '2021-11-04', NULL,
		'Istanza sopra soglia',
		'Richiesta di taglio boschivo che richiede elaborati tecnico-professionali e viene presentata all''ente competente in regime di autorizzazione con silenzio-assenso dopo un termine di tempo stabilito dalle norme vigenti',
		'PROF', false, 'SOPRA_SOGLIA',
		'REGIONE', 18, '90 days'::interval
	)/*, (
		4, 'Istanza di progetti in attuazione dei piano di gestione forestali', NULL, 'Regime di comunicazione (autorizzazione silente)', 1,
		'admin', '2021-11-04', NULL, NULL, '2021-11-04', NULL,
		'Istanza di attuazione dei piani di gestione forestale (PGF) e strumenti equivalenti','Richiesta di taglio di un soprassuolo forestale per il quale esiste un PGF, o strumento equivalente, approvato e in corso di validità',
		'PROF', true, 'ATTUAZIONE_PIANI',
		'REGIONE', 18, null
	)*/, (
		2, 'Istanza sotto soglia', NULL, 'Regime di comunicazione (autorizzazione silente)',
		1, 'admin', '2021-11-04', NULL, NULL, '2021-11-04', NULL,
		'Istanza sotto soglia e altri interventi a comunicazione','Richiesta semplificata di taglio boschivo o di altro intervento previsto a semplice comunicazione che non richiede elaborati tecnico-professionali',
		NULL, false, 'SOTTO_SOGLIA',
		'REGIONE', 18, '15 days'::interval
	), (
		5, 'Istanza di progetti in deroga', NULL, 'Regime autorizzativo (autorizzazione palese)',
		1, 'admin', '2021-11-04', NULL, NULL, '2021-11-04', NULL,
		'Istanza in deroga','Richiesta di taglio boschivo che richiede elaborati tecnico-professionali e viene presentata all''ente competente in regime di richiesta di autorizzazione',
		'PROF', true, 'IN_DEROGA',
		'REGIONE', 24, null
	);
