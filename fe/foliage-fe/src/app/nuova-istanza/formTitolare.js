import { environment } from "src/environments/environment";

const tipoAllegati = "application/pdf, image/*, .p7m";
const apiUrl = `${environment.apiOrigin??window.origin}${environment.apiServerPath}`;

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
							"label": "Codice Fiscale:",
							"tableView": true,
							"key": "codiceFiscale",
							"type": "textfield",
							"applyMaskOn": "change",
							"input": true,
							"validate": {
							  "required": true
							}
						}
					],
					"width": 4,
					"offset": 0,
					"push": 0,
					"pull": 0,
					"size": "md",
					"currentWidth": 4
				},
				{
					"components": [
						{
							"label": "Nome:",
							"tableView": true,
							"key": "nome",
							"type": "textfield",
							"applyMaskOn": "change",
							"input": true,
							"validate": {
							  "required": true
							}
						}
					],
					"width": 4,
					"offset": 0,
					"push": 0,
					"pull": 0,
					"size": "md",
					"currentWidth": 4
				},
				{
					"components": [
						{
							"label": "Cognome:",
							"applyMaskOn": "change",
							"tableView": true,
							"key": "cognome",
							"type": "textfield",
							"input": true,
							"validate": {
							  "required": true
							}
						}
					],
					"width": 4,
					"offset": 0,
					"push": 0,
					"pull": 0,
					"size": "md",
					"currentWidth": 4
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
							"label": "Data Di Nascita:",
							"format": "dd/MM/yyyy",
							"tableView": false,
							"datePicker": {
								"disableWeekends": false,
								"disableWeekdays": false
							},
							"enableMinDateInput": false,
							"enableMaxDateInput": false,
							"key": "dataDiNascita",
							"type": "datetime",
							"input": true,
							"enableTime": false,
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
							},
							"validate": {
							  "required": true
							}
						}
					],
					"width": 4,
					"offset": 0,
					"push": 0,
					"pull": 0,
					"size": "md",
					"currentWidth": 4
				},
				{
					"components": [
						{
							"label": "Luogo Di Nascita:",
							"applyMaskOn": "change",
							"tableView": true,
							"key": "luogoDiNascita",
							"type": "textfield",
							"input": true,
							"validate": {
							  "required": true
							}
						}
					],
					"width": 4,
					"offset": 0,
					"push": 0,
					"pull": 0,
					"size": "md",
					"currentWidth": 4
				},
				{
					"components": [
						{
							"label": "Genere:",
							"widget": "html5",
							"tableView": true,
							"data": {
								"values": [
									{
										"label": "Maschio",
										"value": "M"
									},
									{
										"label": "Femmina",
										"value": "F"
									}
								]
							},
							"key": "genere",
							"type": "select",
							"input": true,
							"validate": {
							  "required": true
							}
						}
					],
					"size": "md",
					"width": 4,
					"offset": 0,
					"push": 0,
					"pull": 0,
					"currentWidth": 4
				}
			],
			"key": "columns1",
			"type": "columns",
			"input": false,
			"tableView": false
		}
	]
};

const pannelloDomicilio = {
	"title": "Domicilio",
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
							"label": "Provincia:",
							"widget": "html5",
							"tableView": true,
							"dataSrc": "url",
							"data": {
								"url": `${apiUrl}/provincie`,
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
							"validate": {
							  "required": true
							},
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
							"label": "Comune:",
							"widget": "html5",
							"tableView": true,
							"dataSrc": "url",
							"data": {
								"url": `${environment.apiServerPath}/comuni/{{data.provincia}}`,
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
							"validate": {
							  "required": true
							},
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
			"key": "columns1",
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
							"label": "CAP:",
							"applyMaskOn": "change",
							"tableView": true,
							"key": "cap",
							"type": "textfield",
							"validate": {
							  "required": true
							},
							"input": true
						}
					],
					"width": 4,
					"offset": 0,
					"push": 0,
					"pull": 0,
					"size": "md",
					"currentWidth": 4
				},
				{
					"components": [
						{
							"label": "Indirizzo:",
							"applyMaskOn": "change",
							"tableView": true,
							"key": "indirizzo",
							"type": "textfield",
							"validate": {
							  "required": true
							},
							"input": true
						}
					],
					"width": 4,
					"offset": 0,
					"push": 0,
					"pull": 0,
					"size": "md",
					"currentWidth": 4
				},
				{
					"components": [
						{
							"label": "Numero Civico:",
							"applyMaskOn": "change",
							"tableView": true,
							"key": "numeroCivico",
							"type": "textfield",
							"validate": {
							  "required": true
							},
							"input": true
						}
					],
					"width": 4,
					"offset": 0,
					"push": 0,
					"pull": 0,
					"size": "md",
					"currentWidth": 4
				}
			],
			"key": "columns1",
			"type": "columns",
			"input": false,
			"tableView": false
		}
	]
};

const pannelloContatti = {
	"title": "Contatti",
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
							"label": "Telefono:",
							"applyMaskOn": "change",
							"tableView": true,
							"validate": {
								"pattern": "^(\\+)?[0-9]*$",
								"customMessage": "Sono ammessi soltanto caratteri numerici eventualmente preceduti da (+)"
							},
							"key": "telefono",
							"type": "textfield",
							"input": true
						}
					],
					"width": 4,
					"offset": 0,
					"push": 0,
					"pull": 0,
					"size": "md",
					"currentWidth": 4
				},
				{
					"components": [
						{
							"label": "Email:",
							"applyMaskOn": "change",
							"tableView": true,
							"key": "email",
							"type": "email",
							"input": true
						}
					],
					"width": 4,
					"offset": 0,
					"push": 0,
					"pull": 0,
					"size": "md",
					"currentWidth": 4
				},
				{
					"components": [
						{
							"label": "Pec:",
							"applyMaskOn": "change",
							"tableView": true,
							"key": "postaCertificata",
							"type": "email",
							"input": true
						}
					],
					"width": 4,
					"offset": 0,
					"push": 0,
					"pull": 0,
					"size": "md",
					"currentWidth": 4
				}
			],
			"key": "columns4",
			"type": "columns",
			"input": false,
			"tableView": false
		}
	]
};

const fileUploadBase64 = {
	"label": "Delega ricevuta dal titolare:",
	"tableView": false,
	"storage": "base64",
	"webcam": false,
	"filePattern": tipoAllegati,
	"validate": {
	  "required": true
	},
	"key": "fileDelegaProfesssionista",
	"conditional": {
		"show": true
	},
	"fileMaxSize": "5MB",
	//"customConditional": "show = submission.isProfessionista",
	"type": "file",
	"input": true
};

const schedaTitolare = {
	"display": "form",
	"components": [
		{
			"title": "Dati del Titolare",
			"collapsible": false,
			"key": "datiDelTitolare",
			"type": "panel",
			"label": "Panel",
			"input": false,
			"tableView": false,
			"components": [
				pannelloAnagrafico,
				pannelloDomicilio,
				//pannelloContatti,
				fileUploadBase64
			]
		}
	]
};


export default schedaTitolare;