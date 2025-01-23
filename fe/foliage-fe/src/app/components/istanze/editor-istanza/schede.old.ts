import { MapLayerType, MapStdAction } from "src/app/gis-table/gis-table.component";
import { TableMenuStdAction, TableRowMenuStdAction } from "src/app/modules/table/table.component";
import { environment } from "src/environments/environment";
import { TipoDatiScheda } from "../../shared/editor-scheda/editor-scheda.component";

const tipologiaPratica = {
	nome: "Tipologia Pratica",
	tipo: "formio",
	tipoDati: TipoDatiScheda.Object,
	readOnly: true,
	conf: {
		"display": "form",
		"settings": {
			"pdf": {
				"id": "1ec0f8ee-6685-5d98-a847-26f67b67d6f0",
				"src": "https://files.form.io/pdf/5692b91fd1028f01000407e3/file/1ec0f8ee-6685-5d98-a847-26f67b67d6f0"
			}
		},
		"components": [
			{
				"label": "Columns",
				"columns": [
					{
						"components": [
							{
								"label": "Codice Istanza",
								"applyMaskOn": "change",
								"disabled": true,
								"tableView": true,
								"key": "codice",
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
								"label": "Ente competente",
								"applyMaskOn": "change",
								"disabled": true,
								"tableView": true,
								"key": "enteCompetente",
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
				"label": "Tipologia Istanza",
				"applyMaskOn": "change",
				"disabled": true,
				"tableView": true,
				"key": "tipo",
				"type": "textfield",
				"input": true
			},
			{
				"label": "Tipologia Specifica",
				"applyMaskOn": "change",
				"disabled": true,
				"tableView": true,
				"key": "tipoSpecifico",
				"conditional": {
					"show": true,
					"when": "textField"
				},
				"customConditional": "show = !(data.tipo == data.tipoSpecifico)",
				"type": "textfield",
				"input": true
			},
			{
				"label": "Nome Istanza",
				"applyMaskOn": "change",
				"tableView": true,
				"validate": {
					"required": true
				},
				"key": "nome",
				"type": "textfield",
				"input": true
			},
			{
				"label": "Descrizione",
				"applyMaskOn": "change",
				"autoExpand": false,
				"tableView": true,
				"key": "note",
				"type": "textarea",
				"input": true,
				"validate": {
					"required": true,
					"customMessage": "Definire una descrizione"
				}
			}
		]
	}
}
const sezioneAnagraficaPratica = {
	nome: "Anagrafica",
	schede: [
		tipologiaPratica
	]
};

const pannelloAnagrafico = {
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
};

const pannelloRecapiti = {
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
};

const fileUpload = {
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
	//"customConditional": "show = submission.metadata.contesto.cfTitolare != data.codiceFiscale",
	"type": "file",
	"url": `${environment.apiServerPath}/istanze/upload/{{submission.metadata.context.codIstanza}}/delegaProfessionista`,
	//"options": "{\n  \"headers\": {\n    \"Authorization\": \"Bearer eyJ4NXQiOiJNell4TW1Ga09HWXdNV0kwWldObU5EY3hOR1l3WW1NNFpUQTNNV0kyTkRBelpHUXpOR00wWkdSbE5qSmtPREZrWkRSaU9URmtNV0ZoTXpVMlpHVmxOZyIsImtpZCI6Ik16WXhNbUZrT0dZd01XSTBaV05tTkRjeE5HWXdZbU00WlRBM01XSTJOREF6WkdRek5HTTBaR1JsTmpKa09ERmtaRFJpT1RGa01XRmhNelUyWkdWbE5nX1JTMjU2IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJDTVBNUkM3Mk0xN0g1MDFOIiwiYXV0IjoiQVBQTElDQVRJT05fVVNFUiIsImNvdW50cnkiOiJSb21hIiwiYmlydGhkYXRlIjoiMTBcLzEwXC8xOTgwIiwiZ2VuZGVyIjoiTSIsImlzcyI6Imh0dHBzOlwvXC9sb2NhbGhvc3Q6OTQ0M1wvb2F1dGgyXC90b2tlbiIsImdyb3VwcyI6WyJJbnRlcm5hbFwvYWRtaW4iLCJBcHBsaWNhdGlvblwvRm9saWFnZSIsIkludGVybmFsXC9ldmVyeW9uZSIsImFkbWluIl0sImdpdmVuX25hbWUiOiJDTVBNUkM3Mk0xN0g1MDFOIiwiYXVkIjoiTTNKQUFuQzVLRFBJS2ltVWJ6UEtRNjFpSjRFYSIsInVwbiI6ImNhbXBlbGxpIiwibmJmIjoxNjg4NzQxNDY3LCJ1cGRhdGVkX2F0IjoxNjgwMTc4NTk3MDAwLCJhenAiOiJNM0pBQW5DNUtEUElLaW1VYnpQS1E2MWlKNEVhIiwic2NvcGUiOiJvcGVuaWQiLCJvcmdhbml6YXRpb24iOiJNQ0FNUEVMTEkiLCJuaWNrbmFtZSI6Im1hcmNvIiwibmFtZSI6ImNhbXBlbGxpIiwiZXhwIjoxNjg4NzQ1MDY3LCJpYXQiOjE2ODg3NDE0NjcsImZhbWlseV9uYW1lIjoiY2FtcGVsbGkiLCJqdGkiOiIyZjY2ZTk0MS1jMzM4LTQ0ZjMtYjc2YS0wZGM2YjM5NDg4OTIiLCJlbWFpbCI6ImFhQGFhLml0In0.YKUWkC_-EnwLLDizdupH5-qMYFaOdrMmMFESphSyQ4hIRYyojTIQImXL_P5EtalSyVXWXz759gkj8C7HflmeSKeU21YDmQB7lajjdRAUz3xxPLPD6RF1nuHnkhzlPX9ES0Y5uI0HmMrMNPFJxe8YG2hwkqNqOToqqVA4JBGAe5G0mw-obmZi2aG5loeKMF36M92mbLHW8FWU8U8BxAbaZYxtvWm9RCSGXOnieBIKhqAA6QOGo6E4vD6_6dbBQiM37AJlw9pKLf4rc9f6iAfhISgnLKnpo37R6X8MDg6wJyOMiykgPJY_VytP8tuXSSOeNqDGOgNS15b3rz0VKjLMQA\"\n  }\n}",
	//"options": "{\n  \"headers\": {\n    \"Authorization\": \"{{submission.getAccessToken()}}\"  }\n}",
	// "options": {
	// 	"headers": {
	// 		"Authorization": "{{submission.accessToken()}}"
	// 	}
	// },
	//"headers": {'Authorization': '{{submission.getAccessToken()}}'},
	"input": true
};

const fileUploadBase64 = {
	"label": "Delega ricevuta dal titolare",
	"tableView": false,
	"storage": "base64",
	"webcam": false,
	"capture": false,
	"fileTypes": [
		{
			"label": "",
			"value": ""
		}
	],
	"key": "fileDelegaProfesssionista",
	"conditional": {
		"show": true
	},
	"fileMaxSize": "5MB",
	//"customConditional": "show = submission.metadata?.context?.cfUtente != data.codiceFiscale",
	"type": "file",
	"input": true
};

const schedaTitolare = {
	"display": "form",
	"components": [
		pannelloAnagrafico,
		pannelloRecapiti,
		fileUploadBase64/*,
		fileUpload*/
	]
};


const tabSchedaParticelle = {
	schedaInfo: {
		"components": [
			{
				"label": "Columns",
				"columns": [
					{
						"components": [
							{
								"label": "Provincia",
								"widget": "html5",
								"tableView": true,
								"dataSrc": "url",
								"data": {
									"url": `${environment.apiServerPath}/provincie-host`,
									"headers": [
										{
											"key": "Authorization",
											"value": "{{submission.metadata.getAccessToken()}}"
										}
									]
								},
								"idPath": "id_prov",
								"valueProperty": "id_prov",
								"template": "<span>{{ item.desc_prov }}</span>",
								"key": "provincia",
								"type": "select",
								"input": true,
								"disableLimit": false,
								"noRefreshOnScroll": false
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
								"label": "Comune",
								"widget": "html5",
								"tableView": true,
								"dataSrc": "url",
								"data": {
									"url": `${environment.apiServerPath}/istanze/comuni/{{data.provincia}}`,
									"headers": [
										{
											"key": "Authorization",
											"value": "{{submission.metadata.getAccessToken()}}"
										}
									]
								},
								"idPath": "id_comu",
								"valueProperty": "id_comu",
								"template": "<span>{{ item.desc_comu }}</span>",
								"refreshOn": "provincia",
								"key": "comune",
								"type": "select",
								"disableLimit": false,
								"noRefreshOnScroll": false,
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
			  "label": "Columns",
			  "columns": [
				{
				  "components": [
					{
					  "label": "Sezione",
					  "applyMaskOn": "change",
					  "tableView": true,
					  "key": "sezione",
					  "type": "textfield",
					  "input": true
					}
				  ],
				  "width": 3,
				  "offset": 0,
				  "push": 0,
				  "pull": 0,
				  "size": "md",
				  "currentWidth": 3
				},
				{
				  "components": [
					{
					  "label": "Foglio",
					  "applyMaskOn": "change",
					  "tableView": true,
					  "key": "foglio",
					  "type": "textfield",
					  "input": true
					}
				  ],
				  "width": 3,
				  "offset": 0,
				  "push": 0,
				  "pull": 0,
				  "size": "md",
				  "currentWidth": 3
				},
				{
				  "components": [
					{
					  "label": "Particella",
					  "applyMaskOn": "change",
					  "tableView": true,
					  "key": "particella",
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
				},
				{
				  "components": [
					{
					  "label": "Subalterno",
					  "applyMaskOn": "change",
					  "tableView": true,
					  "key": "sub",
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
			  "key": "columns1",
			  "type": "columns",
			  "input": false,
			  "tableView": false
			}
		]
	},
	triggers: {
		delete: (caller: any, oldRow: any) => Promise.resolve(undefined),
		update: (caller: any, oldRow: any, newRow: any) => Promise.resolve(undefined),
		insert: (caller: any, newRow: any) => Promise.resolve(undefined),
		init: (caller: any, resources: any) => {
			if (!resources.comuni) {
				return caller.authService.authFetch(
						`/info-comuni-host`
					).then(
						(res: any[]) => {
							resources.comuni = Object.fromEntries(
								res.map(
									(v: any) => ([v.id_comune, v])
								)
							);
						}
					);
			}
		},
		dataLoadCb: (data: any, caller: any, resources: any) => {
			const outVal = {...data, provincia: resources.comuni[data.comune].id_provincia };
			return Promise.resolve(outVal);
		}
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
			header: "Provincia",
			cellValue: (rowData: any, resources: any) => {
				return (rowData.comune && resources.comuni) ? resources.comuni[rowData.comune].provincia : undefined;
			}
		},
		{
			header: "Comune",
			cellValue: (rowData: any, resources: any) => {
				return (rowData.comune && resources.comuni) ? resources.comuni[rowData.comune].comune : undefined;
			}
		},
		{
			header: "Sezione",
			dataField: "sezione"
		},
		{
			header: "Foglio",
			dataField: "foglio"
		},
		{
			header: "Particella",
			dataField: "particella"
		},
		{
			header: "Subalterno",
			dataField: "sub"
		}
	]
};

const schedaParticellaForestale = {
	mappa: {
		shape: {
			label: "id",
			attribute: "shape",
			srid: "EPSG:3857"
		},
		view: {
			srid: "EPSG:3857"
		}
	},
	trigger: {
		delete: (caller: any, oldRow: any) => Promise.resolve(undefined),
		update: (caller: any, oldRow: any, newRow: any) => Promise.resolve(undefined),
		insert: (caller: any, newRow: any) => {
			newRow["id"] = caller.getId();
			return Promise.resolve(undefined);
		},
		split: (caller: any, pars: { oldRow: any, newRows: any[] }) => (
			pars.newRows.map(
				newRow => {
					newRow["id"] = caller.getId();
					return Promise.resolve(undefined);
				}
			)
		)
	},
	geometries: ['Poligoni'],
	interactions: {
		element: [
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
	}
};

const schedaParticellaForestaleSotto = {
	...schedaParticellaForestale, geometries: ['Poligoni', 'Linee', 'Punti']
}

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
		split: (caller: any, pars: { oldRow: any, newRows: any[] }) => (
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
	dictionaries: {
		parts: [3, 0]
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
			header: "Cod. P.for.2",
			dataField: "partForestale"
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



const tabSchedaParticelleTest = {
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
`<button type="button" class="pulsante btn btn-primary" (click)="resources.log(row.data.profondita)">
	Dettagli
</button>`
		}
	]
};


const schedaTipoGestione = {
	"components": [
		{
			"label": "Tipo Azienda",
			"widget": "html5",
			"tableView": true,
			"dataSrc": "url",
			"data": {
				"url": `${environment.apiServerPath}/istanze/lista-tipi-azienda`,
				"headers": [
					{
						"key": "Authorization",
						"value": "{{submission.metadata.getAccessToken()}}"
					}
				]
			},
			"idPath": "id_tazi",
			"valueProperty": "id_tazi",
			"template": "<span>{{ item.desc_tazi }}</span>",
			"key": "tipoAzienda",
			"type": "select",
			"input": true,
			"disableLimit": false,
			"noRefreshOnScroll": false
		},
		{
			"label": "Natura Proprietà",
			"widget": "html5",
			"tableView": true,
			"dataSrc": "url",
			"data": {
				"url": `${environment.apiServerPath}/istanze/lista-natura-proprieta`,
				"headers": [
					{
						"key": "Authorization",
						"value": "{{submission.metadata.getAccessToken()}}"
					}
				]
			},
			"idPath": "id_nprp",
			"valueProperty": "id_nprp",
			"template": "<span>{{ item.desc_nprp }}</span>",
			"key": "naturaProprieta",
			"type": "select",
			"input": true,
			"disableLimit": false,
			"noRefreshOnScroll": false
		},
		{
			"label": "Tipo Proprietà",
			"widget": "html5",
			"tableView": true,
			"dataSrc": "url",
			"data": {
				"url": `${environment.apiServerPath}/istanze/lista-tipi-proprieta`,
				"headers": [
					{
						"key": "Authorization",
						"value": "{{submission.metadata.getAccessToken()}}"
					}
				]
			},
			"idPath": "id_tprp",
			"valueProperty": "id_tprp",
			"template": "<span>{{ item.desc_tprp }}</span>",
			"key": "tipoProprieta",
			"type": "select",
			"input": true,
			"disableLimit": false,
			"noRefreshOnScroll": false
		},
		{
			"label": "Qualificazione titolare istanza",
			"widget": "html5",
			"tableView": true,
			"dataSrc": "url",
			"data": {
				"url": `${environment.apiServerPath}/istanze/lista-qualificazioni-proprietario`,
				"headers": [
					{
						"key": "Authorization",
						"value": "{{submission.metadata.getAccessToken()}}"
					}
				]
			},
			"idPath": "id_qual",
			"valueProperty": "id_qual",
			"template": "<span>{{ item.desc_qual }}</span>",
			"key": "qualificaTitolare",
			"type": "select",
			"input": true,
			"disableLimit": false,
			"noRefreshOnScroll": false
		},
		{
			"label": "Autocertificazione della proprietà o altro titolo di possesso",
			"tableView": false,
			"storage": "base64",
			"webcam": false,
			"capture": false,
			"fileTypes": [
				{
					"label": "",
					"value": ""
				}
			],
			"multiple": true,
			"validate": {
				"required": true
			},
			"key": "autocertificazioneDellaProprietaOAltroTitoloDiPossesso",
			"type": "file",
			"input": true
		},
		{
			"label": "Delega alla titolarità delle proprietà multiple",
			"tableView": false,
			"storage": "base64",
			"webcam": false,
			"capture": false,
			"fileTypes": [
				{
					"label": "",
					"value": ""
				}
			],
			"multiple": true,
			"validate": {
				"required": true
			},
			"key": "delegaAllaTitolaritaDellistanzaDelleProprietaMultiple",
			"conditional": {
				"show": true,
				"when": "qualificaTitolare",
				"eq": "8"
			},
			"type": "file",
			"input": true
		},
		{
			"label": "Atto di nomina come rappresentante legale",
			"tableView": false,
			"storage": "base64",
			"webcam": false,
			"capture": false,
			"fileTypes": [
				{
					"label": "",
					"value": ""
				}
			],
			"validate": {
				"required": true
			},
			"key": "attoDiNominaComeRappresentanteLegale",
			"conditional": {
				"show": true,
				"when": "qualificaTitolare",
				"eq": "11"
			},
			"type": "file",
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
			conf: schedaTitolare,
			readOnly: true
		},
		{
			nome: "Tipo Gestione",
			tipo: "formio",
			tipoDati: TipoDatiScheda.Object,
			conf: schedaTipoGestione
		},
		{
			nome: "Particelle Catastali",
			tipo: "tabella",
			tipoDati: TipoDatiScheda.Array,
			conf: tabSchedaParticelle
		}
	]
};


const sezioneInquadramentoSottoSoglia =  {
	nome: "Inquadramento",
	schede: [
		{
			nome: "Particella Forestale",
			tipo: "tabellaGis",
			tipoDati: TipoDatiScheda.Array,
			conf: schedaParticellaForestaleSotto
		},
		{
			nome: "Caratterizzazione Stazione Forestale",
			tipo: "formio",
			tipoDati: TipoDatiScheda.Object,
			conf: {},
			readOnly: true
		},
		{
			nome: "Inquadramento Vincolistica",
			tipo: "formio",
			tipoDati: TipoDatiScheda.Object,
			conf: {},
			readOnly: true
		}
	]
};

const sezioneInquadramentoSopraSoglia =  {
	nome: "Inquadramento",
	schede: [
		{
			nome: "Particella Forestale",
			tipo: "tabellaGis",
			tipoDati: TipoDatiScheda.Array,
			conf: schedaParticellaForestale
		},
		{
			nome: "Caratterizzazione Stazione Forestale",
			tipo: "formio",
			tipoDati: TipoDatiScheda.Object,
			conf: {},
			readOnly: true
		},
		{
			nome: "Inquadramento Vincolistica",
			tipo: "formio",
			tipoDati: TipoDatiScheda.Object,
			conf: {},
			readOnly: true
		}
	]
};


export const istanzaDiEsempio = [
	sezioneAnagraficaPratica,
	sezioneProponenti,
	sezioneInquadramentoSottoSoglia,
	{
		nome: "Schede di prova",
		schede: [
			{
				nome: "Particelle",
				tipo: "tabella",
				tipoDati: TipoDatiScheda.Array,
				conf: tabSchedaParticelleTest
			},
			{
				nome: "Unità Omogenee",
				tipo: "tabellaGis",
				tipoDati: TipoDatiScheda.Array,
				conf: schedaUnitaOmogenee
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



export const istanzaSopraSoglia = [
	sezioneAnagraficaPratica,
	sezioneProponenti,
	sezioneInquadramentoSopraSoglia
];