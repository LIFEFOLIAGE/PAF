const tipoAllegati = "application/pdf, image/*, .p7m";

export default {
	"components": [
		{
			"title": "Dati per Responsabile di Servizio",
			"collapsible": false,
			"key": "datiPerResponsabileDiServizio",
			"type": "panel",
			"label": "Panel",
			"input": false,
			"tableView": false,
			"components": [
				{
					"label": "Dettagli",
					"columns": [
						{
							"components": [
								{
									"label": "Tipo Di Nomina",
									"widget": "html5",
									"tableView": true,
									"data": {
										"values": [
											{
												"label": "Responsabile del Servizio PAF",
												"value": "1"
											},
											{
												"label": "Rappresentante Legale",
												"value": "2"
											}
										]
									},
									"validate": {
										"required": true
									},
									"key": "tipoDiNomina",
									"type": "select",
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
									"label": "Numero di Protocollo",
									"applyMaskOn": "change",
									"tableView": true,
									"validate": {
										"required": true
									},
									"key": "numeroDiProtocollo",
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
									"label": "Data Protocollo",
									"format": "dd/MM/yyyy",
									"tableView": false,
									"datePicker": {
										"disableWeekends": false,
										"disableWeekdays": false
									},
									"enableTime": false,
									"timePicker": {
										"showMeridian": false
									},
									"enableMinDateInput": false,
									"enableMaxDateInput": false,
									"key": "dataProtocollo",
									"type": "datetime",
									"input": true,
									"widget": {
										"type": "calendar",
										"displayInTimezone": "utc",
										"locale": "it",
										"useLocaleSettings": true,
										"allowInput": true,
										"mode": "single",
										"enableTime": false,
										"noCalendar": false,
										"format": "dd/MM/yyyy",
										"hourIncrement": 1,
										"minuteIncrement": 1,
										"time_24hr": true,
										"minDate": null,
										"disableWeekends": false,
										"disableWeekdays": false,
										"maxDate": null
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
					"key": "dettagli",
					"type": "columns",
					"input": false,
					"tableView": false
				},
				{
					"label": "Atto di Nomina",
					"tableView": false,
					"storage": "base64",
					"webcam": false,
					"capture": false,
					"filePattern": tipoAllegati,
					"key": "attoDiNomina",
					"type": "file",
					"input": true,
					"validate": {
						"required": true
					}
				},
				{
					"label": "Documento di Identità",
					"tableView": false,
					"storage": "base64",
					"webcam": false,
					"capture": false,
					"filePattern": tipoAllegati,
					"key": "documentoDiIdentita",
					"type": "file",
					"input": true,
					"validate": {
						"required": true
					}
				}
			]
		}
	]
};