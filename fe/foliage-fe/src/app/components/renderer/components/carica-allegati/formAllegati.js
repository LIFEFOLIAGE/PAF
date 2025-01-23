
const tipoAllegati = "application/pdf, image/*, .p7m";

export const formAllegati = {
	"components": [
		{
			"label": "Documenti allegati",
			"reorder": false,
			"addAnotherPosition": "bottom",
			"layoutFixed": false,
			"enableRowGroups": false,
			"initEmpty": true,
			"tableView": false,
			"defaultValue": [
				{
					"categoria": "",
					"tipoDocumento": "",
					"categoriaAllegato": "",
					"fileAllegato": []
				}
			],
			"key": "documentiAllegati",
			"type": "datagrid",
			"input": true,
			"components": [
				{
					"label": "Categoria",
					"columns": [
						{
							"components": [
								{
									"label": "Categoria",
									"widget": "html5",
									"tableView": true,
									"data": {
										"values": [
											{
												"label": "Parere",
												"value": "parere"
											},
											{
												"label": "Nulla-osta",
												"value": "nullaOsta"
											},
											{
												"label": "Autorizzazione",
												"value": "autorizzazione"
											},
											{
												"label": "Piedilista di martellata",
												"value": "piedilistaDiMartellata"
											},
											{
												"label": "Visura catastale",
												"value": "visuraCatastale"
											},
											{
												"label": "Autorizzazioni ricevute da enti terzi",
												"value": "autorizzazioniRicevuteDaEntiTerzi"
											},
											{
												"label": "Schede specifiche di Pre-screening VIncA",
												"value": "schedeSpecificheDiPreScreeningVIncA"
											},
											{
												"label": "Progetto di taglio boschivo",
												"value": "progettoTaglioBoschivo"
											},
											{
												"label": "Allegati cartografici",
												"value": "allegatiCartografici"
											}/*,
											{
												"label": "Altra documentazione",
												"value": "altraDocumentazione"
											}*/
										]
									},
									"searchEnabled": false,
									"readOnlyValue": true,
									"validate": {
										"required": true
									},
									"key": "categoriaAllegato",
									"type": "select",
									"input": true
								}/*,
								{
									"label": "Nome del documento",
									"applyMaskOn": "change",
									"tableView": true,
									"validate": {
										"required": true,
										"unique": true
									},
									"unique": true,
									"key": "nomeDocumento",
									"conditional": {
										"show": true,
										"when": "categoriaAllegato",
										"eq": "altraDocumentazione"
									},
									"type": "textfield",
									"input": true
								}*/
							],
							"width": 10,
							"offset": 0,
							"push": 0,
							"pull": 0,
							"size": "md",
							"currentWidth": 12
						}
					],
					"hideLabel": true,
					"key": "columns",
					"type": "columns",
					"input": false,
					"tableView": false
				},
				{
					"label": "File",
					"hideLabel": true,
					"tableView": false,
					"storage": "base64",
					"webcam": false,
					"capture": false,
					"filePattern": tipoAllegati,
					"validate": {
						"required": true
					},
					"key": "fileAllegato",
					"type": "file",
					"input": true
				}
			]
		}
	]
};
