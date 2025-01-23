import { environment } from "src/environments/environment";

const apiUrl = `${environment.apiOrigin??window.origin}${environment.apiServerPath}`;

export default {
	"display": "form",
	"components": [
		{
			"label": "Columns",
			"columns": [
				{
					"components": [
						{
							"label": "Nome Utente",
							"applyMaskOn": "change",
							"disabled": true,
							"tableView": true,
							"key": "userName",
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
							"label": "Id Utente",
							"applyMaskOn": "change",
							"disabled": true,
							"tableView": true,
							"key": "idUten",
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
							"label": "Codice Fiscale",
							"applyMaskOn": "change",
							"disabled": true,
							"tableView": true,
							"key": "codiceFiscale",
							"type": "textfield",
							"input": true
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
			"key": "columns2",
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
							"label": "Nome",
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
							"label": "Cognome",
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
							"label": "Data Di Nascita",
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
							"label": "Luogo Di Nascita",
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
							"label": "Genere",
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
		},
		{
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
									"label": "Provincia",
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
									"label": "Comune",
									"widget": "html5",
									"tableView": true,
									"dataSrc": "url",
									"data": {
										"url": `${apiUrl}/comuni/{{data.provincia}}`,
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
									"label": "CAP",
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
									"label": "Indirizzo",
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
									"label": "Numero Civico",
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
		},
		// {
		// 	"title": "Contatti",
		// 	"collapsible": false,
		// 	"key": "contatti",
		// 	"type": "panel",
		// 	"label": "Panel",
		// 	"input": false,
		// 	"tableView": false,
		// 	"components": [
		// 		{
		// 			"label": "Columns",
		// 			"columns": [
		// 				{
		// 					"components": [
		// 						{
		// 							"label": "Telefono",
		// 							"applyMaskOn": "change",
		// 							"tableView": true,
		// 							"validate": {
		// 								"pattern": "^(\\+)?[0-9]*$",
		// 								"customMessage": "Sono ammessi soltanto caratteri numerici eventualmente preceduti da (+)"
		// 							},
		// 							"key": "telefono",
		// 							"type": "textfield",
		// 							"input": true
		// 						}
		// 					],
		// 					"width": 4,
		// 					"offset": 0,
		// 					"push": 0,
		// 					"pull": 0,
		// 					"size": "md",
		// 					"currentWidth": 4
		// 				},
		// 				{
		// 					"components": [
		// 						{
		// 							"label": "Email",
		// 							"applyMaskOn": "change",
		// 							"tableView": true,
		// 							"key": "email",
		// 							"type": "email",
		// 							"input": true
		// 						}
		// 					],
		// 					"width": 4,
		// 					"offset": 0,
		// 					"push": 0,
		// 					"pull": 0,
		// 					"size": "md",
		// 					"currentWidth": 4
		// 				},
		// 				{
		// 					"components": [
		// 						{
		// 							"label": "Pec",
		// 							"applyMaskOn": "change",
		// 							"tableView": true,
		// 							"key": "postaCertificata",
		// 							"type": "email",
		// 							"input": true
		// 						}
		// 					],
		// 					"width": 4,
		// 					"offset": 0,
		// 					"push": 0,
		// 					"pull": 0,
		// 					"size": "md",
		// 					"currentWidth": 4
		// 				}
		// 			],
		// 			"key": "columns4",
		// 			"type": "columns",
		// 			"input": false,
		// 			"tableView": false
		// 		}
		// 	]
		// },
		{
			"label": "Professionista Forestale",
			"tableView": false,
			"defaultValue": false,
			"key": "isProfessionistaForestale",
			"type": "checkbox",
			"input": true
		},
		{
			"title": "Dettagli Professionista Forestale",
			"collapsible": false,
			"key": "autocertificazioneProfessionistaForestale",
			"conditional": {
				"show": true,
				"when": "isProfessionistaForestale",
				"eq": "true"
			},
			"type": "panel",
			"label": "Panel",
			"input": false,
			"tableView": false,
			"components": [
				{
					"label": "Container",
					"tableView": false,
					"key": "autocertificazioneProf",
					"type": "container",
					"input": true,
					"components": [
						{
							"label": "Columns",
							"columns": [
								{
									"components": [
										{
											"label": "Categoria",
											"widget": "html5",
											"placeholder": "Categoria",
											"tableView": true,
											"data": {
												"values": [
													{
														"label": "Ordine Professionale",
														"value": "ordineProfessionale"
													},
													{
														"label": "Collegio Agrotecnici",
														"value": "collegio"
													}
												]
											},
											"validate": {
												"required": true
											},
											"key": "categoria",
											"type": "select",
											"input": true,
											"row": "0-0"
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
											"label": "Sottocategoria",
											"widget": "html5",
											"placeholder": "Sottocategoria",
											"tableView": true,
											"data": {
												"values": [
													{
														"label": "Junior",
														"value": "junior"
													},
													{
														"label": "Senior",
														"value": "senior"
													}
												]
											},
											"idPath": "value",
											"validate": {
												"required": true
											},
											"key": "sottocategoria",
											"conditional": {
												"show": true,
												"when": "autocertificazioneProf.categoria",
												"eq": "ordineProfessionale"
											},
											"type": "select",
											"input": true,
											"row": "0-0"
										},
										{
											"label": "Collegio",
											"widget": "html5",
											"placeholder": "Collegio",
											"tableView": true,
											"data": {
												"values": [
													{
														"label": "Laureati",
														"value": "laureati"
													},
													{
														"label": "Non Laureati",
														"value": "nonLaureati"
													}
												]
											},
											"idPath": "value",
											"validate": {
												"required": true
											},
											"key": "collegio",
											"conditional": {
												"show": true,
												"when": "autocertificazioneProf.categoria",
												"eq": "collegio"
											},
											"type": "select",
											"input": true,
											"row": "0-0"
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
							"key": "columns5",
							"type": "columns",
							"input": false,
							"tableView": false
						},
						{
							"label": "Numero Iscrizione",
							"tableView": true,
							"key": "numeroIscrizione",
							"type": "textfield",
							"input": true,
							"validate": {
								"required": true
							},
							"row": "0-1"
						},
						{
							"label": "Provincia Iscrizione",
							"widget": "html5",
							"tableView": true,
							"dataSrc": "url",
							"data": {
								"url": `${apiUrl}/provincie`,
								"headers": [
									{
										"key": "authorization",
										"value": "{{submission.metadata.getAccessToken()}}"
									}
								]
							},
							"idPath": "id_prov",
							"valueProperty": "id_prov",
							"template": "<span>{{ item.desc_prov }}</span>",
							"searchEnabled": false,
							"validate": {
								"required": true
							},
							"key": "provinciaIscrizione",
							"type": "select",
							"disableLimit": false,
							"input": true,
							"row": "0-2"
						},
						{
							"label": "Pec",
							"applyMaskOn": "change",
							"tableView": true,
							"key": "postaCertificata",
							"type": "email",
							"validate": {
								"required": true,
								"customMessage": "È necessario inserire una PEC valida"
							},
							"input": true
						}
					]
				}
			]
		}
	]
}