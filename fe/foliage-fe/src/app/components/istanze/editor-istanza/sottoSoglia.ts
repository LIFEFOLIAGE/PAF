import { MapLayerType, MapStdAction } from "src/app/gis-table/gis-table.component";
import { TableMenuStdAction, TableRowMenuStdAction } from "src/app/modules/table/table.component";
import { environment } from "src/environments/environment";
import { TipoDatiScheda } from "../../shared/editor-scheda/editor-scheda.component";


const schedaTitolare = {
	"display": "form",
	"components": [
		{
			"title": "Anagrafica",
			"collapsible": false,
			"key": "anagrafica",
			"type": "panel",
			"label": "Panel",
			"input": false,
			"tableView": false,
			"components": [
				{
					"label": "Columns",
					"columns": [
						{
							"components": [
								{
									"label": "Codice Fiscale",
									"applyMaskOn": "change",
									"tableView": true,
									"key": "codiceFiscale",
									"type": "textfield",
									"input": true
								}
							],
							"width": 2,
							"offset": 0,
							"push": 0,
							"pull": 0,
							"size": "md",
							"currentWidth": 2
						},
						{
							"components": [
								{
									"label": "Cognome",
									"applyMaskOn": "change",
									"tableView": true,
									"key": "cognome",
									"type": "textfield",
									"input": true
								}
							],
							"width": 2,
							"offset": 0,
							"push": 0,
							"pull": 0,
							"size": "md",
							"currentWidth": 2
						},
						{
							"components": [
								{
									"label": "Nome",
									"applyMaskOn": "change",
									"tableView": true,
									"key": "nome",
									"type": "textfield",
									"input": true
								}
							],
							"size": "md",
							"width": 2,
							"offset": 0,
							"push": 0,
							"pull": 0,
							"currentWidth": 2
						},
						{
							"components": [
								{
									"label": "Data di Nascita",
									"format": "dd/MM/yyyy",
									"tableView": false,
									"datePicker": {
										"disableWeekends": false,
										"disableWeekdays": false
									},
									"enableTime": false,
									"enableMinDateInput": false,
									"enableMaxDateInput": false,
									"key": "dataDiNascita",
									"type": "datetime",
									"input": true,
									"widget": {
										"type": "calendar",
										"displayInTimezone": "viewer",
										"locale": "it",
										"useLocaleSettings": false,
										"allowInput": true,
										"mode": "single",
										"enableTime": false,
										"noCalendar": false,
										"format": "dd/MM/yyyy",
										"hourIncrement": 1,
										"minuteIncrement": 1,
										"time_24hr": false,
										"minDate": null,
										"disableWeekends": false,
										"disableWeekdays": false,
										"maxDate": null
									}
								}
							],
							"size": "md",
							"width": 3,
							"offset": 0,
							"push": 0,
							"pull": 0,
							"currentWidth": 3
						},
						{
							"components": [
								{
									"label": "Luogo Di Nascita",
									"applyMaskOn": "change",
									"tableView": true,
									"key": "luogoDiNascita",
									"type": "textfield",
									"input": true
								}
							],
							"size": "md",
							"width": 3,
							"offset": 0,
							"push": 0,
							"pull": 0,
							"currentWidth": 3
						}
					],
					"key": "columns",
					"type": "columns",
					"input": false,
					"tableView": false
				}
			]
		},
		{
			"title": "Recapiti",
			"collapsible": false,
			"key": "recapiti",
			"type": "panel",
			"label": "Panel",
			"input": false,
			"tableView": false,
			"components": [
				{
					"label": "Columns",
					"columns": [
						{
							"components": [
								{
									"label": "Email",
									"applyMaskOn": "change",
									"tableView": true,
									"key": "email",
									"type": "textfield",
									"input": true
								}
							],
							"width": 6,
							"offset": 0,
							"push": 0,
							"pull": 0,
							"size": "md",
							"currentWidth": 6
						},
						{
							"components": [
								{
									"label": "Posta Certificata",
									"applyMaskOn": "change",
									"tableView": true,
									"key": "postaCertificata",
									"type": "textfield",
									"input": true
								}
							],
							"width": 6,
							"offset": 0,
							"push": 0,
							"pull": 0,
							"size": "md",
							"currentWidth": 6
						}
					],
					"key": "columns1",
					"type": "columns",
					"input": false,
					"tableView": false
				}
			]
		},
		{
			"label": "Delega ricevuta dal titolare",
			"tableView": false,
			"storage": "base64",
			"webcam": false,
			"filePattern": "application/pdf, image/*",
			"key": "file",
			"conditional": {
				"show": true
			},
			"fileMaxSize": "5MB",
			//"customConditional": "show = submission.isProfessionista",
			"type": "file",
			"input": true
		},
		{
			"label": "Delega ricevuta dal titolare",
			"tableView": false,
			"storage": "url",
			"webcam": false,
			"fileTypes": [
				{
					"label": "",
					"value": ""
				}
			],
			"key": "file2",
			"type": "file",
			"url": `${environment.apiServerPath}/istanze/upload/{{submission.metadata.context.codIstanza}}/delegaProfessionista`,
			"input": true
		}
	]
};

const sezioneProponenti = {
	nome: "Proponenti",
	schede: [
		{
			nome: "Titolare",
			tipo: "formio",
			tipoDati: TipoDatiScheda.Object,
			conf: schedaTitolare
		},
		{
			nome: "Professionista",
			tipo: "formio",
			tipoDati: TipoDatiScheda.Object,
			conf: schedaTitolare
		}
	]
};

const tabSchedaParticelle = {
	dictionaries: {
		titolare: [1, 0]
	},
	schedaInfo: {
		"display": "form",
		"components": [
			{
				"label": "Data Caricamento",
				"format": "dd/MM/yyyy",
				"placeholder": "Data Caricamento",
				"tableView": false,
				"datePicker": {
					"disableWeekends": false,
					"disableWeekdays": false
				},
				"enableTime": false,
				"enableMinDateInput": false,
				"enableMaxDateInput": false,
				"key": "data_ins",
				"type": "datetime",
				"input": true,
				"widget": {
					"type": "calendar",
					"displayInTimezone": "utc",
					"locale": "it",
					"useLocaleSettings": false,
					"allowInput": true,
					"mode": "single",
					"enableTime": false,
					"noCalendar": false,
					"format": "dd/MM/yyyy",
					"hourIncrement": 1,
					"minuteIncrement": 1,
					"time_24hr": false,
					"readOnly": false,
					"minDate": null,
					"disableWeekends": false,
					"disableWeekdays": false,
					"maxDate": null
				}
			},
			{
				"label": "Altimetria",
				"placeholder": "Altimetria",
				"applyMaskOn": "change",
				"mask": false,
				"tableView": false,
				"delimiter": false,
				"requireDecimal": false,
				"inputFormat": "plain",
				"truncateMultipleSpaces": false,
				"key": "altimetria",
				"type": "number",
				"input": true
			},
			{
				"label": "Pendenza",
				"placeholder": "Pendenza",
				"applyMaskOn": "change",
				"mask": false,
				"tableView": false,
				"delimiter": false,
				"requireDecimal": false,
				"inputFormat": "plain",
				"truncateMultipleSpaces": false,
				"key": "pendenza",
				"type": "number",
				"input": true
			}
		]
	},
	trigger: {
		delete: (caller: any, oldRow: any) => Promise.resolve(undefined),
		update: (caller: any, oldRow: any, newRow: any) => Promise.resolve(undefined),
		insert: (caller: any, newRow: any) => Promise.resolve(undefined)
	},
	menuOptions: {
		element: [
			{
				label: "Visualizza",
				action: TableRowMenuStdAction.View
			},
			{
				label: "Modifica",
				action: TableRowMenuStdAction.Edit
			},
			{
				label: "Elimina",
				action: TableRowMenuStdAction.Remove
			}
		],
		general: [
			{
				label: "Nuovo",
				action: TableMenuStdAction.New
			}
		]
	},
	columns: [
		{
			header: "Id",
			dataField: "id_pfor"
		},
		{
			header: "Esposizione Prevalente",
			dataField: "esposizione"
		},
		{
			header: "Altimetria",
			dataField: "altimetria"
		},
		{
			header: "Pendenza",
			dataField: "pendenza"
		},
		{
			header: "Giacitura",
			dataField: "giacitura"
		},
		{
			header: "Substrato Pedologico",
			dataField: "substrato_ped"
		},
		{
			header: "Profonfità",
			dataField: "profondita"
		},
		{
			header: "Tessitura",
			dataField: "tessitura"
		},
		{
			header: "Data Caricamento",
			dataField: "data_ins",
			dataType: "date",
			dataFormat: "ShortDate"
		},
		{
			header: "Valore Calcolato",
			cellValue: (rowData: any) => {
				return `${rowData.profondita}-${rowData.tessitura}`;
			}
		},
		{
			header: "Template",
			template: 
`<button type="button" class="pulsante btn btn-primary" (click)="alert(profondita)">
	Dettagli
</button>`
		}
	]
};

const schedaUnitaOmogenee = {
	mappa: {
		// dataLayers: [
		// 	{

		// 		label: "codiceUog",
		// 		attribute: "shape",
		// 		srid: "EPSG:3857"
		// 	},
		// ],
		shape: {
			label: "codiceUog",
			attribute: "shape",
			srid: "EPSG:3857"
		},
		view: {
			srid: "EPSG:3857"
		},
		layers: [
			{
				name: "Rete Natura 2000",
				type: MapLayerType.Wfs,
				conf: {
					url: function (extent: any) {
						return (
							'https://ahocevar.com/geoserver/wfs?service=WFS&' +
							'version=1.1.0&request=GetFeature&typename=osm:water_areas&' +
							'outputFormat=application/json&srsname=EPSG:3857&' +
							'bbox=' +
							extent.join(',') +
							',EPSG:3857'
						);
					  },
					strategy: "bbox"
				}
			}
		]
	},
	trigger: {
		delete: (caller: any, oldRow: any) => Promise.resolve(undefined),
		update: (caller: any, oldRow: any, newRow: any) => Promise.resolve(undefined),
		insert: (caller: any, newRow: any) => {
			newRow["codiceUog"] = caller.getId();
			return Promise.resolve(undefined);
		},
		split: (caller: any, pars : {oldRow: any, newRows: any[]}) => (
			pars.newRows.map(
				newRow => {
					newRow["codiceUog"] = caller.getId();
					return Promise.resolve(undefined);
				}
			)
		)
	},
	interactions: {
		element: [
			{
				label: "Modify Attibutes",
				action: MapStdAction.ModifyAttributes,
				icon: "bi bi-card-text"
			},
			{
				label: "Modifica Geometria",
				action: MapStdAction.ModifyGeometry,
				icon: "bi bi-bounding-box-circles"
			},
			{
				label: "Elimina",
				action: MapStdAction.Remove,
				icon: "bi bi-trash"
			},
			{
				label: "Taglia",
				action: MapStdAction.Cut,
				icon: "bi bi-pie-chart"
			},
			{
				label: "Scorpora geometria composta",
				action: MapStdAction.Split,
				icon: "bi bi-grid-1x2"
			},
			{
				label: "Assimila Altre Geometrie",
				action: MapStdAction.Join,
				icon: "bi bi-bandaid"
			}
		],
		general: [
			{
				label: "Nuovo",
				action: MapStdAction.New,
				icon: "bi bi-plus"
			}
		]
	},
	schedaInfo: {
		"display": "form",
		"components": [
			{
				"label": "Columns",
				"columns": [
					{
						"components": [
							{
								"label": "Codice UOG",
								"disabled": true,
								"tableView": true,
								"key": "codiceUog",
								"type": "textfield",
								"input": true
							}
						],
						"width": 6,
						"offset": 0,
						"push": 0,
						"pull": 0,
						"size": "md",
						"currentWidth": 6
					},
					{
						"components": [
							{
								"label": "Codice Particella Forestale",
								"disabled": true,
								"tableView": true,
								"key": "codicePartForestale",
								"type": "textfield",
								"input": true
							}
						],
						"width": 6,
						"offset": 0,
						"push": 0,
						"pull": 0,
						"size": "md",
						"currentWidth": 6
					}
				],
				"key": "columns",
				"type": "columns",
				"input": false,
				"tableView": false
			},
			{
				"label": "Tipo Di Governo",
				"widget": "html5",
				"placeholder": "Seleziona",
				"tableView": true,
				"data": {
					"values": [
						{
							"label": "Ceduo",
							"value": "ceduo"
						},
						{
							"label": "Non Ceduo",
							"value": "nonCeduo"
						}
					]
				},
				"key": "tipoDiGoverno",
				"type": "select",
				"input": true
			},
			{
				"label": "Particella Forestale",
				"widget": "html5",
				"placeholder": "Seleziona",
				"tableView": true,
				"dataSrc": "custom",
				"data": {
					"resource": "",
					"url": "",
					"json": "",
					"custom": 'values = (submission.metadata.dictionariesData ? submission.metadata.dictionariesData.parts: []).filter(x => x != null).map(x => (x.id_pfor));'
					//"custom": 'values = [{label: 1, value: {a:1}}];'
					//"custom": 'values = (submission.dictionariesData ? submission.dictionariesData.parts: []).filter(x => x != null).map(x => ({label: x.id_pfor, value: x.id_pfor}));'
				},
				"key": "partForestale",
				"type": "select",
				//"disabled": true,
				"input": true,
				validate: {
					required: true,
					customMessage: "Occorre selezionare la particella"
				}
			}
		]
	},
	columns: [
		{
			header: "Cod. UOG",
			dataField: "codiceUog"
		},
		{
			header: "Cod. P.for.",
			dataField: "codicePartForestale"
		},
		{
			header: "Tipo Di Governo",
			dataField: "tipoDiGoverno",
			cellValue: (data: string) => {
				return {
					ceduo: "Ceduo",
					nonCeduo: "Non Ceduo"
				}[data];
			}
		}
	]
};
export const sezioniSottoSoglia = [
	sezioneProponenti,
	{
		nome: "Inquadramento",
		schede: [
			{
				nome: "Tipo Gestione"
			},
			{
				nome: "Particelle Catastali",
				tipo: "tabella",
				tipoDati: TipoDatiScheda.Array,
				conf: tabSchedaParticelle
			},
			{
				nome: "Unità Omogenee",
				tipo: "tabellaGis",
				tipoDati: TipoDatiScheda.Array,
				conf: schedaUnitaOmogenee,
				dictionaries: {
					parts: [3, 1]
				}
			}
		]
	},
	{
		nome: "Proposta",
		schede: [
			{
				nome: "Aree di saggio"
			},
			{
				nome: "Polloni"
			},
			{
				nome: "Matricine"
			}
		]
	}
];