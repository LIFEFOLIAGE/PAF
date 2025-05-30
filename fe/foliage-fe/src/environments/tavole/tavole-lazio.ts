export const tavoleLazio = [
	{
		titolo: 'Cartografia Tecnica',
		layers: [
			'crt', //'orto', 'osm',
			'pfor', 'viab', 'uo', 'ad', 'ast', 'imp'
		]
	},
	{
		titolo: 'Planimetria Catastale',
		layers: [
			'crt',// 'orto', 'osm',
			'catasto', 'pfor'
		]
	},
	{
		titolo: 'Aree protette e rete natura 2000',
		layers: [
			'crt',
			'NAT2K', 'AREE_PROTETTE',
			'pfor'
		]
	},
	{
		titolo: 'Altri vincoli territoriali',
		layers: [
			'crt',
			 //PAI
			'PAI_RISCHIO_ALLUVIONE', 'PAI_RISCHIO_FRANA', 'PAI_RISCHIO_VALANGA',
			//PPTR Vincoli Paesaggistici
			"PUNTI_ARCHEOLOGICI_TIPIZZATI", "USI_CIVICI", "BOSCHI", "RISPETTO_GEOMORFOLOGIA", "ACQUE_PUBBLICHE", "LINEE_ARCHEOLOGICHE",
			"RISPETTO_LINEE_ARCHEOLOGICHE", "ALTIMETRIA_1200", "ACQUE_PUBBLICHE_RISPETTO", "RISPETTO_LINEE_ARCHEOLOGICHE_TIPIZZATE",
			"RISPETTO_PUNTI_ARCHEOLOGICI", "ZONE_UMIDE", "DECRETI_ARCHEOLOGICI", "GEOMORFOLOGICI_TIPIZZATI", "EX_1497_AB", "EX_1497_CD",
			"COSTA_MARE", "PUNTI_ARCHEOLOGICI", "COSTA_LAGHI",
			'pfor'
		] 
	}
];