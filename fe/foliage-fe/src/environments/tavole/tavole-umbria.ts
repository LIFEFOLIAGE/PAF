export const tavoleUmbria = [
	{
		titolo: 'Cartografia Tecnica',
		layers: [
			'crt', 
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
			//Vincoli paesaggistici
			"AREE_PAESAGGISTICHE",
			'pfor'
		] 
	}
];