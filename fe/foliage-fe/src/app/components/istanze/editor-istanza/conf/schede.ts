import { MapLayerType, MapStdAction } from "../../../../gis-table/gis-table.component";
import { DataFormat, TableMenuStdAction, TableRowMenuStdAction } from "../../../../modules/table/table.component";
import { environment } from "../../../../../environments/environment";
import { TipoDatiScheda } from "../../../shared/editor-scheda/editor-scheda.component";
import {
	VerificaLayerVincolatiComponent
} from "../../../renderer/components/verifica-layer-vincolati/verifica-layer-vincolati.component";
import {
	InterventiInAmbitiNonForestaliComponent
} from "../../../renderer/components/interventi-in-ambiti-non-forestali/interventi-in-ambiti-non-forestali.component";
import { SoprasuoloBoschivoComponent } from "../../../renderer/components/soprasuolo-boschivo/soprasuolo-boschivo.component";
import { AssortimentiRitraibili } from "../../../renderer/components/assortimenti-ritraibili/assortimenti-ritraibili.component";
import { StazioneForestaleComponent } from "../../../renderer/components/stazione-forestale/stazione-forestale.component";
import {
	ContiguitaTagliBoschiviComponent
} from "../../../renderer/components/contiguita-tagli-boschivi/contiguita-tagli-boschivi.component";
import {
	InquadramentoVincolisticaComponent
} from "../../../renderer/components/inquadramento-vincolistica/inquadramento-vincolistica.component";
import {
	DettagliUnitaOmogeneaComponent
} from "../../../renderer/components/dettagli-unita-omogenea/dettagli-unita-omogenea.component";
import {
	ViabilitaForestaleComponent
} from "../../../renderer/components/viabilita-forestale/viabilita-forestale.component";
import { CaricaAllegatiComponent } from "../../../renderer/components/carica-allegati/carica-allegati.component";
import { RiepilogoFinaleComponent } from "../../../renderer/components/riepilogo-finale/riepilogo-finale.component";
import { ProspettiRiepilogativiComponent } from "../../../renderer/components/prospetti-riepilogativi/prospetti-riepilogativi-component";
import { SchedaAltriStratiComponent } from "../../../renderer/components/scheda-altri-stati/scheda-altri-stati.component";
import { RilevamentoComponent } from "../../../renderer/components/rilevamento/rilevamento.component";
import { ParticellaCatastaleComponent } from "../../../renderer/components/particelle-catastali/particelle-catastali.component";


const tipoAllegati: string = "application/pdf, image/*, .p7m";

const tipologiaPratica = {
	nome: "Tipologia Pratica",
	tipo: "formio",
	tipoDati: TipoDatiScheda.Object,
	readOnly: true,
	conf: {
		"display": "form",
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
};
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
							"label": "Provincia",
							"applyMaskOn": "change",
							"tableView": true,
							"key": "nomeProvincia",
							"type": "textfield",
							"validate": {
							  "required": true
							},
							"input": true
						}
						// {
						// 	"label": "Provincia",
						// 	"widget": "html5",
						// 	"tableView": true,
						// 	"dataSrc": "url",
						// 	"data": {
						// 		"url": `${apiUrl}/provincie`,
						// 		"headers": [
						// 			{
						// 				"key": "Authorization",
						// 				"value": "{{submission.metadata.getAccessToken()}}"
						// 			}
						// 		]
						// 	},
						// 	"idPath": "id_prov",
						// 	"valueProperty": "id_prov",
						// 	"template": "<span>{{ item.desc_prov }}</span>",
						// 	"key": "provincia",
						// 	"type": "select",
						// 	"validate": {
						// 	  "required": true
						// 	},
						// 	"input": true,
						// 	"disableLimit": false,
						// 	"noRefreshOnScroll": false
						// }
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
							"applyMaskOn": "change",
							"tableView": true,
							"key": "nomeComune",
							"type": "textfield",
							"validate": {
							  "required": true
							},
							"input": true
						}
						// {
						// 	"label": "Comune",
						// 	"widget": "html5",
						// 	"tableView": true,
						// 	"dataSrc": "url",
						// 	"data": {
						// 		"url": `${apiUrl}/comuni/{{data.provincia}}`,
						// 		"headers": [
						// 			{
						// 				"key": "Authorization",
						// 				"value": "{{submission.metadata.getAccessToken()}}"
						// 			}
						// 		]
						// 	},
						// 	"idPath": "id_comu",
						// 	"valueProperty": "id_comu",
						// 	"template": "<span>{{ item.desc_comu }}</span>",
						// 	"refreshOn": "provincia",
						// 	"key": "comune",
						// 	"validate": {
						// 	  "required": true
						// 	},
						// 	"type": "select",
						// 	"disableLimit": false,
						// 	"noRefreshOnScroll": false,
						// 	"input": true
						// }
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
							"label": "Telefono",
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
							"label": "Email",
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
							"label": "Pec",
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
	"label": "Delega ricevuta dal titolare",
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
		pannelloAnagrafico,
		pannelloDomicilio,
		//pannelloContatti,
		fileUploadBase64
	]
};

function postSalvaParticella(newPart: any) {
	if (newPart.sezione == undefined || newPart.sezione == '') {
		newPart.sezione = ' ';
	}
	if (newPart.sub == undefined || newPart.sub == '') {
		newPart.sub = ' ';
	}
}
const apiUrl = `${environment.apiOrigin??window.origin}${environment.apiServerPath}`;
const confSchedaParticelleSotto = {
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
								"url": `${apiUrl}/istanze/{{submission.metadata.context.codIstanza}}/provincie-istanza?authority={{submission.metadata.authority}}&authScope={{submission.metadata.authScope}}`,
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
							"noRefreshOnScroll": false,
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
							"label": "Comune",
							"widget": "html5",
							"tableView": true,
							"dataSrc": "url",
							"data": {
								//"url": `${apiUrl}/istanze/comuni/{{data.provincia}}`,
								"url": `${apiUrl}/istanze/{{submission.metadata.context.codIstanza}}/comuni-istanza/{{submission.data.provincia}}?authority={{submission.metadata.authority}}&authScope={{submission.metadata.authScope}}`,
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
							"input": true,
							"validate": {
								"required": true
							}
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
							"input": true,
							"validate": {
								"required": true
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
		},
		{
			"label": "Columns",
			"columns": [
				{
					"components": [
						{
							"label": "Superficie catastale (mq)",
							"applyMaskOn": "change",
							"mask": false,
							"tableView": false,
							"delimiter": false,
							"requireDecimal": false,
							"inputFormat": "plain",
							"truncateMultipleSpaces": false,
							"validate": {
								"required": true,
								"min": 0
							},
							"key": "superficie",
							"type": "number",
							"input": true,
							"decimalLimit": 0
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
					],
					"width": 6,
					"offset": 0,
					"push": 0,
					"pull": 0,
					"size": "md",
					"currentWidth": 6
				}
			],
			"key": "columns2",
			"type": "columns",
			"input": false,
			"tableView": false
		}
	]
};

const confSchedaParticelleSopra = {
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
								"url": `${apiUrl}/istanze/{{submission.metadata.context.codIstanza}}/provincie-istanza?authority={{submission.metadata.authority}}&authScope={{submission.metadata.authScope}}`,
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
							"noRefreshOnScroll": false,
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
							"label": "Comune",
							"widget": "html5",
							"tableView": true,
							"dataSrc": "url",
							"data": {
								//"url": `${apiUrl}/istanze/comuni/{{data.provincia}}`,
								"url": `${apiUrl}/istanze/{{submission.metadata.context.codIstanza}}/comuni-istanza/{{submission.data.provincia}}?authority={{submission.metadata.authority}}&authScope={{submission.metadata.authScope}}`,
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
							"input": true,
							"validate": {
								"required": true
							}
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
							"input": true,
							"validate": {
								"required": true
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
		},
		{
			"label": "Columns",
			"columns": [
				{
					"components": [
						{
							"label": "Superficie catastale (mq)",
							"applyMaskOn": "change",
							"mask": false,
							"tableView": false,
							"delimiter": false,
							"requireDecimal": false,
							"inputFormat": "plain",
							"truncateMultipleSpaces": false,
							"validate": {
								"required": true,
								"min": 0
							},
							"key": "superficie",
							"type": "number",
							"input": true,
							"decimalLimit": 0
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
							"label": "Superficie di intervento (mq)",
							"applyMaskOn": "change",
							"mask": false,
							"tableView": false,
							"delimiter": false,
							"requireDecimal": false,
							"inputFormat": "plain",
							"truncateMultipleSpaces": false,
							"validate": {
								"required": true,
								"custom": "valid = (input <= data.superficie) ? true : 'La superficie dell&quot;intervento non può superare la superficie catastale';",
								//"custom": "if (input <= data.superficie) {valid = true;} else {valid = 'La superficie dell&quot;intervento non può superare la superficie catastale';}",
								"min": 0
							},
							"key": "superficieInterventoPart",
							"type": "number",
							"input": true,
							"decimalLimit": 0
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
			"key": "columns2",
			"type": "columns",
			"input": false,
			"tableView": false
		}
	]
};

const tabSchedaParticelleSotto = {
	schedaInfo: {
		// nome: "Dettagli particella",
		// tipo: "formio",
		// tipoDati: TipoDatiScheda.Object,
		// conf: confSchedaParticelleSotto,
		// readOnly: false
		nome: "Dettagli particella",
		tipo: "component",
		tipoDati: TipoDatiScheda.Object,
		conf: {
			component: ParticellaCatastaleComponent,
			options: {
				isIstanzaSopraSoglia: false //permette di nascondere "Superficie di intervento"
			}
		},
		readOnly: false
	},
	triggers: {
		delete: (caller: any, oldRow: any) => Promise.resolve(undefined),
		update: (caller: any, oldRow: any, newRow: any) => {
			postSalvaParticella(newRow);
			return Promise.resolve(undefined)
		},
		insert: (caller: any, newRow: any) => {
			postSalvaParticella(newRow);
			return Promise.resolve(undefined)
		},
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
			const outVal = { ...data, provincia: resources.comuni[data.comune]?.id_provincia };
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
		},
		{
			header: "Superficie catastale(ha)",
			dataField: "superficie",
			dataFormat: DataFormat.Ettari
		}
	],
	constraints: [
		{
			messaggio: "Presenza di campi obbligatori non impostati",
			notNull: ["comune", "sezione", "foglio", "particella", "sub", "superficie"]
		},
		{
			messaggio: "Non si può definire la stessa particella più volte",
			unique: ["comune", "sezione", "foglio", "particella", "sub"]
		}
	],
	totals: [
		{
			label: "Numero di particelle inserite",
			startVal: 0,
			formula: (prevValue: any, currValue: {data: any, idx: number}) => (prevValue + 1),
			alignment: "right"
		},
		{
			label: "Totale superficie catastale (ha)",
			startVal: 0,
			formula: (prevValue: any, currValue: {data: any, idx: number}) => (prevValue + (currValue.data["superficie"]/10000)),
			alignment: "right"
		}
	]
};

const tabSchedaParticelleSopra = {
	...tabSchedaParticelleSotto,
	schedaInfo: {
		// nome: "Dettagli particella",
		// tipo: "formio",
		// tipoDati: TipoDatiScheda.Object,
		// conf: confSchedaParticelleSopra,
		// readOnly: false
		nome: "Dettagli particella",
		tipo: "component",
		tipoDati: TipoDatiScheda.Object,
		conf: {
			component: ParticellaCatastaleComponent,
			options: {
				isIstanzaSopraSoglia: true
			}
		},
		readOnly: false
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
		},
		{
			header: "Superficie catastale(ha)",
			dataField: "superficie",
			dataFormat: DataFormat.Ettari
		},
		{
			header: "Superficie di intervento(ha)",
			dataField: "superficieInterventoPart",
			dataFormat: DataFormat.Ettari
		}
		
	],
	constraints: [
		{
			messaggio: "Presenza di campi obbligatori non impostati",
			notNull: ["comune", "sezione", "foglio", "particella", "sub", "superficie", "superficieInterventoPart"]
		},
		{
			messaggio: "Non si può definire la stessa particella più volte",
			unique: ["comune", "sezione", "foglio", "particella", "sub"]
		}
	],
	totals: [
		{
			label: "Numero di particelle inserite",
			startVal: 0,
			formula: (prevValue: any, currValue: {data: any, idx: number}) => (prevValue + 1),
			alignment: "right"
		},
		{
			label: "Totale superficie di intervento (ha)",
			startVal: 0,
			formula: (prevValue: any, currValue: {data: any, idx: number}) => (prevValue + (currValue.data["superficieInterventoPart"]/10000)),
			alignment: "right"
		},
		{
			label: "Totale superficie catastale (ha)",
			startVal: 0,
			formula: (prevValue: any, currValue: {data: any, idx: number}) => (prevValue + (currValue.data["superficie"]/10000)),
			alignment: "right"
		}
	]
}

const schedaParticellaForestaleSopra = {
	mappa: {
		shape: {
			//label: "id",
			attribute: "shape",
			srid: "EPSG:3035"
		},
		view: {
			srid: "EPSG:3035",
			maxZoom: 20
		},
		suffissoFileExport: 'ParticellaForestale',
		layers: {
			rilevamenti: {
				name: "Rilievi in Campo",
				type: MapLayerType.DictionaryLayer,
				styleName: 'rilevamenti',
				conf: {
					src: 'rilevamenti',
					shapeField: 'wktGeometria',
					labelField: 'nome',
					shapeSrid: "EPSG:4326"
				}
			},
			uo: {
				name: "Unità Omogenee",
				type: MapLayerType.DictionaryLayer,
				color: 'rgba(255, 0, 0, 1.0)',
				conf: {
					//src: 'unitaOmogenee',
					labelField: "nomeUO",
					shapeField: "shape",
					shapeSrid: "EPSG:3035"
				}
			},
			viab: {
				name: "Viabilità Forestale",
				type: MapLayerType.DictionaryLayer,
				color: 'rgba(255, 0, 0, 1.0)',
				conf: {
					shapeField: "shape",
					shapeSrid: "EPSG:3035"
				}
			},
			altr: {
				name: "Altri Strati Informativi",
				type: MapLayerType.DictionaryLayer,
				color: 'rgba(255, 0, 0, 1.0)',
				conf: {
					shapeField: "shape",
					shapeSrid: "EPSG:3035"
				}
			}
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
	},
	minSize: {
		value: 1,
		errorMessage: "La particella forestale deve contenere almeno una geometria"
	},
	areaConstraint: {
		unit: 'ha',
		contextValue: 'superficieTotIntervento',
		constrainedAreaText: 'Totale superficie di intervento (ha)',
		totalEditingAreaText: 'Superficie della particella forestale (ha)',
		valueDescription: 'Scostamento con superficie di intervento (%)',
		minAcceptedValue: -3,
		maxAcceptedValue: 3,
		displayedDecimals: 4,
		getValue: (areaValue: number, constraintValue: number) => (((areaValue - constraintValue) / constraintValue) * 100),
		wrongValueErrorMessage: "Lo scostamento con la superficie di intervento non è compreso tra -3% e +3%",
		missingValueErrorMessage: "Il totale della superficie di intevervento non è stato dichiarato"
	},
	geometryConstraints: {
		contains: {
			reverse: true,
			opposite: true,
			errMessage: "La zona di intervento non può uscire dai confini dell'ente competente per l'istanza"
		}
	}
};

const schedaParticellaForestaleIntBoschivo = {
	...schedaParticellaForestaleSopra,
	geometries: ['Poligoni', 'Punti'],
	singleGeometries: true,
	areaConstraint: undefined,
	maxSize: {
		value: 1,
		errorMessage: "Si può definire soltanto una geometria in questa mappa"
	}
};

const schedaParticellaForestaleIntComunicazione = {
	...schedaParticellaForestaleSopra,
	geometries: ['Linee'],
	singleGeometries: true,
	areaConstraint: undefined,
	maxSize: {
		value: 1,
		errorMessage: "Si può definire soltanto una geometria in questa mappa"
	}
};



const schedaSoprasuolo = {
	nome: "Soprassuolo",
	tipo: "component",
	tipoDati: TipoDatiScheda.Object,
	conf: {
		component: SoprasuoloBoschivoComponent,
		options:  {
			embedSoprasuoloInMapPopup: false//permette di nascondere "Superficie dell'intervento" quando usato nell'unità omogenea
		}
	},
	readOnly: false
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
			label: "nomeUO",
			attribute: "shape",
			srid: "EPSG:3035",
			areaAttribute: "superficie"
		},
		view: {
			srid: "EPSG:3035",
			maxZoom: 20
		},
		suffissoFileExport: 'UnitaOmogenee',
		layers: {
			rilevamenti: {
				name: "Rilievi in Campo",
				type: MapLayerType.DictionaryLayer,
				styleName: 'rilevamenti',
				conf: {
					src: 'rilevamenti',
					shapeField: 'wktGeometria',
					labelField: 'nome',
					shapeSrid: "EPSG:4326"
				}
			},
			pfor: {
				name: "Particella Forestale",
				type: MapLayerType.DictionaryLayer,
				styleName: 'pfor',
				conf: {
					src: 'pfor',
					shapeField: 'shape',
					labelField: '',
					shapeSrid: "EPSG:3035"
				}
			},
			viab: {
				name: "Viabilità Forestale",
				type: MapLayerType.DictionaryLayer,
				color: 'rgba(255, 0, 0, 1.0)',
				conf: {
					shapeField: "shape",
					shapeSrid: "EPSG:3035"
				}
			},
			altr: {
				name: "Altri Strati Informativi",
				type: MapLayerType.DictionaryLayer,
				color: 'rgba(255, 0, 0, 1.0)',
				conf: {
					shapeField: "shape",
					shapeSrid: "EPSG:3035"
				}
			}
		}
	},
	trigger: {
		delete: (caller: any, oldRow: any) => Promise.resolve(undefined),
		update: (caller: any, oldRow: any, newRow: any) => Promise.resolve(undefined),
		insert: (caller: any, newRow: any) => {
			return Promise.resolve(undefined);
		},
		split: (caller: any, pars: { oldRow: any, newRows: any[] }) => (
			pars.newRows.map(
				newRow => {
					newRow["nomeUO"] = undefined;
					newRow["superficieUtile"] = newRow["superficie"];
					["areeInterdette", "chiarieRadure", "areeImproduttive"].forEach(
						(p) => {
							newRow[p] = 0;
						}
					)
					return Promise.resolve(undefined);
				}
			)
		),
		init: (caller: any, resources: any) => {
			getSottoCategorie(caller, resources);
		}
	},
	interactions: {
		element: [
			{
				label: "Modifica Valori",
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
		// general: [
		// 	{
		// 		label: "Nuovo",
		// 		action: MapStdAction.New,
		// 		icon: "bi bi-plus"
		// 	}
		// ]
	},
	schedaInfo: {
		nome: "Dettagli unità omogenea",
		tipo: "component",
		tipoDati: TipoDatiScheda.Object,
		conf: {
			component: DettagliUnitaOmogeneaComponent
		},
		readOnly: false/*,
		ignoredProps: ['superficieUtile']*/
	},
	geometries: ['Poligoni'],
	columns: [
		{
			header: "Nome U.O.",
			dataField: "nomeUO"
		},
		{
			header: "Forma di governo",
			dataField: "formaDiGoverno",
		},
		{
			header: "Superficie totale (ha)",
			dataField: "superficie",
			dataFormat: DataFormat.Ettari

		},
		{
			header: "Superficie utile (ha)",
			dataField: "superficieUtile",
			dataFormat: DataFormat.Ettari
		}
	],
	minSize: {
		value: 1,
		errorMessage: "Deve esserci almeno un'unità omogenea nell'istanza"
	},
	constraints: [
		{
			messaggio: "Il nome è obbligatorio",
			notNull: ["nomeUO"]
		},
		{
			messaggio: "La forma di governo è obbligatoria",
			notNull: ["formaDiGoverno"]
		},
		{
			messaggio: "Non si può definire lo stesso nome più volte",
			unique: ["nomeUO"]
		},
		{
			messaggio: "Indicare la categoria forestale",
			notNull: ["idCategoria"]
		}
	],
	geometryConstraints: {
		contains: {
			layer: 'pfor',
			isSingle: false,
			errMessage: "L'unità omogenea non può uscire dai confini della zona di intervento indicata",
			reverse: true,
			opposite: true
		}
	}
};

const schedaAltriStratiInformativi = {
	mappa: {
		shape: {
			label: "nomeArea",
			attribute: "shape",
			srid: "EPSG:3035",
			areaAttribute: "superficie"
		},
		view: {
			srid: "EPSG:3035",
			maxZoom: 20
		},
		suffissoFileExport: 'AltriStratiInformativi',
		layers: {
			rilevamenti: {
				name: "Rilievi in Campo",
				type: MapLayerType.DictionaryLayer,
				styleName: 'rilevamenti',
				conf: {
					src: 'rilevamenti',
					shapeField: 'wktGeometria',
					labelField: 'nome',
					shapeSrid: "EPSG:4326"
				}
			},
			pfor: {
				name: "Particella Forestale",
				type: MapLayerType.DictionaryLayer,
				color: 'rgba(0, 255, 0, 1.0)',
				conf: {
					src: 'partForestale'
				}
			},
			uo: {
				name: "Unità Omogenee",
				type: MapLayerType.DictionaryLayer,
				color: 'rgba(255, 0, 0, 1.0)',
				conf: {
					//src: 'unitaOmogenee',
					labelField: "nomeUO",
					shapeField: "shape",
					shapeSrid: "EPSG:3035"
				}
			},
			viab: {
				name: "Viabilità Forestale",
				type: MapLayerType.DictionaryLayer,
				color: 'rgba(255, 0, 0, 1.0)',
				conf: {
					shapeField: "shape",
					shapeSrid: "EPSG:3035"
				}
			}
		}
	},
	trigger: {
		delete: (caller: any, oldRow: any) => Promise.resolve(undefined),
		update: (caller: any, oldRow: any, newRow: any) => Promise.resolve(undefined),
		insert: (caller: any, newRow: any) => {
			return Promise.resolve(undefined);
		},
	},
	singleGeometries: true,
	geometries: ['Punti'],
	interactions: {
		element: [
			{
				label: "Modifica Attributi",
				action: MapStdAction.ModifyAttributes,
				icon: "bi bi-card-text"
			},
			{
				label: "Elimina",
				action: MapStdAction.Remove,
				icon: "bi bi-trash"
			},
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
		nome: "Altri Strati",
		tipo: "component",
		tipoDati: TipoDatiScheda.Object,
		conf: {
			component: SchedaAltriStratiComponent
		}
	},
	columns: [
		{
			header: "Nome",
			dataField: "nomeArea"
		},
		{
			header: "Superficie",
			dataField: "superficieArea",
			dataFormat: DataFormat.Decimal
		}
	],
	constraints: [
		{
			messaggio: "Il nome è obbligatorio",
			notNull: ["nomeArea"]
		}
	],
	geometryConstraints: {
		coveredBy: {
			layer: "uo",
			single: true,
			predicate: (currFeature: any, layerFeature: any) => {
				
			},
			errMessage: "L'area di saggio deve essere all'interno dell'unità omogenea selezionata"
		}
	}
};

const schedaViabilitaForestale = {
	mappa: {
		shape: {
			label: "codiceUog",
			attribute: "shape",
			srid: "EPSG:3035",
			areaAttribute: "superficie"
		},
		view: {
			srid: "EPSG:3035",
			maxZoom: 20
		},
		suffissoFileExport: 'ViabilitaForestale',
		layers: {
			rilevamenti: {
				name: "Rilievi in Campo",
				type: MapLayerType.DictionaryLayer,
				styleName: 'rilevamenti',
				conf: {
					src: 'rilevamenti',
					shapeField: 'wktGeometria',
					labelField: 'nome',
					shapeSrid: "EPSG:4326"
				}
			},
			pfor: {
				name: "Particella Forestale",
				color: 'rgba(0, 255, 0, 1.0)',
				type: MapLayerType.DictionaryLayer,
				conf: {
					src: 'pfor',
					shapeField: "shape",
					shapeSrid: "EPSG:3035"
				}
			},
			uo: {
				name: "Unità Omogenee",
				type: MapLayerType.DictionaryLayer,
				color: 'rgba(255, 0, 0, 1.0)',
				conf: {
					src: 'uo',
					labelField: "nomeUO",
					shapeField: "shape",
					shapeSrid: "EPSG:3035"
				}
			},
			altr: {
				name: "Altri Strati Informativi",
				type: MapLayerType.DictionaryLayer,
				color: 'rgba(255, 0, 0, 1.0)',
				conf: {
					shapeField: "shape",
					shapeSrid: "EPSG:3035"
				}
			}
		}
	},
	trigger: {
		delete: (caller: any, oldRow: any) => Promise.resolve(undefined),
		update: (caller: any, oldRow: any, newRow: any) => Promise.resolve(undefined),
		insert: (caller: any, newRow: any) => {
			newRow["codiceUog"] = caller.getId();
			return Promise.resolve(undefined);
		},
	},
	singleGeometries: true,
	geometries: ['Linee'],
	interactions: {
		element: [
			{
				label: "Modifica Attributi",
				action: MapStdAction.ModifyAttributes,
				icon: "bi bi-card-text"
			},
			{
				label: "Elimina",
				action: MapStdAction.Remove,
				icon: "bi bi-trash"
			},
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
		nome: "Viabilità forestale",
		tipo: "component",
		tipoDati: TipoDatiScheda.Object,
		conf: {
			component: ViabilitaForestaleComponent,
		},
		readOnly: false
	},
	columns: [
		{
			header: "Cod. Tipo",
			dataField: "codTipoViabilita"
		},
		{
			header: "Tipo",
			dataField: "nomeTipoViabilita"
		}
	],
	constraints: [
		{
			messaggio: "Il tipo di viabilità è obbligatorio",
			notNull: ["codTipoViabilita"]
		}
	]
};

const schedaAllegati = {
	nome: "Allegati",
	tipo: "component",
	tipoDati: TipoDatiScheda.Object,
	conf: {
		component: CaricaAllegatiComponent,
		options: undefined
	},
	readOnly: false
}

const schedaRiepilogoFinale = {
	nome: "Riepilogo",
	tipo: "component",
	tipoDati: TipoDatiScheda.Object,
	conf: {
		component: RiepilogoFinaleComponent,
		options: undefined
	},
	readOnly: false

}


const schedaTipoGestione = {
	"components": [
		{
			"label": "Tipo Azienda",
			"widget": "html5",
			"tableView": true,
			"dataSrc": "url",
			"data": {
				"url": `${apiUrl}/istanze/lista-tipi-azienda`,
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
			"noRefreshOnScroll": false,
			"validate": {
				"required": true
			}
		},
		{
			"label": "Natura Proprietà",
			"widget": "html5",
			"tableView": true,
			"dataSrc": "url",
			"data": {
				"url": `${apiUrl}/istanze/lista-natura-proprieta`,
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
			"noRefreshOnScroll": false,
			"validate": {
				"required": true
			}
		},
		{
			"label": "Tipo Proprietà",
			"widget": "html5",
			"tableView": true,
			"dataSrc": "url",
			"data": {
				"url": `${apiUrl}/istanze/lista-tipi-proprieta`,
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
			"noRefreshOnScroll": false,
			"validate": {
				"required": true
			}
		},
		{
			"label": "Qualificazione titolare istanza",
			"widget": "html5",
			"tableView": true,
			"dataSrc": "url",
			"data": {
				"url": `${apiUrl}/istanze/lista-qualificazioni-proprietario`,
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
			"noRefreshOnScroll": false,
			"validate": {
				"required": true
			}
		},
		{
			"label": "Autocertificazione del titolo di possesso del soprassuolo forestale da parte dell'avente diritto",
			"tableView": false,
			"storage": "base64",
			"webcam": false,
			"capture": false,
			"filePattern": tipoAllegati,
			"multiple": true,
			"validate": {
				"required": true
			},
			"key": "autocertificazioneProprieta",
			"type": "file",
			"input": true
		},
		// {
		// 	"label": "Delega da parte degli altri comproprietari per la presentazione dell'istanza di taglio boschivo",
		// 	"tableView": false,
		// 	"storage": "base64",
		// 	"webcam": false,
		// 	"capture": false,
		// 	"filePattern": "application/pdf, image/*",
		// 	"multiple": true,
		// 	"validate": {
		// 		"required": true
		// 	},
		// 	"key": "delegaAllaTitolarita",
		// 	"conditional": {
		// 		"show": true,
		// 		"when": "qualificaTitolare",
		// 		"eq": "9"
		// 	},
		// 	"type": "file",
		// 	"input": true
		// },



		
		{
			"label": "Istanza presentata da una persona giuridica",
			"tableView": false,
			"validateWhenHidden": false,
			"key": "isPersonaGiuridica",
			"type": "checkbox",
			"input": true,
			"defaultValue": false
		},
		{
			"label": "Atto di nomina come rappresentante legale",
			"tableView": false,
			"storage": "base64",
			"webcam": false,
			"capture": false,
			"filePattern": tipoAllegati,
			"key": "attoNominaRappresentanteLegale",
			"validate": {
			  "required": true
			},
			"conditional": {
				"show": true,
				"when": "isPersonaGiuridica",
				"eq": "true"
			},
			"validateWhenHidden": false,
			"type": "file",
			"input": true
		},

		{
			"label": "Istanza presentata per un bosco silente",
			"tableView": false,
			"validateWhenHidden": false,
			"key": "isBoscoSilente",
			"type": "checkbox",
			"input": true,
			"defaultValue": false
		},
		{
			"label": "Provvedimento finale di sostituzione e conferimento boschi silenti",
			"tableView": false,
			"storage": "base64",
			"webcam": false,
			"capture": false,
			"filePattern": tipoAllegati,
			"multiple": false,
			"key": "provvedimentoBoschiSilenti",
			"type": "file",
			"validate": {
			  "required": true
			},
			"conditional": {
				"show": true,
				"when": "isBoscoSilente",
				"eq": "true"
			},
			"validateWhenHidden": false,
			"input": true
		},



		{
			"label": "Istanza presentata da una ditta forestale",
			"tableView": false,
			"validateWhenHidden": false,
			"key": "isDittaForestale",
			"type": "checkbox",
			"input": true,
			"defaultValue": false
		},
		{
			"label": "Autocertificazione come ditta forestale",
			"tableView": false,
			"storage": "base64",
			"webcam": false,
			"capture": false,
			"filePattern": tipoAllegati,
			"multiple": false,
			"key": "autocertificazioneDittaForestale",
			"type": "file",
			"validate": {
			  "required": true
			},
			"conditional": {
				"show": true,
				"when": "isDittaForestale",
				"eq": "true"
			},
			"validateWhenHidden": false,
			"input": true
		},
		{
			"label": "Documenti di identità",
			"tableView": false,
			"storage": "base64",
			"webcam": false,
			"capture": false,
			"filePattern": tipoAllegati,
			"multiple": false,
			"key": "documentiIdentita",
			"type": "file",
			"input": true,
			"validate": {
				"required": true
			}
		}
	]
};


const sezioneProponentiSotto = {
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
			conf: tabSchedaParticelleSotto
		}
	]
};

const sezioneProponentiSopra = {
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
			conf: tabSchedaParticelleSopra
		}
	]
};

const sezioneInquadramentoIntBoschivo = {
	nome: "Inquadramento",
	schede: [
		{
			nome: "Mappa Interventi Forestali e Cartografie",
			tipo: "tabellaGis",
			tipoDati: TipoDatiScheda.Array,
			conf: schedaParticellaForestaleIntBoschivo,
			dictionaries: {
				rilevamenti: undefined
			}
		},
		{
			nome: "Inquadramento Natura 2000",
			tipo: "component",
			tipoDati: TipoDatiScheda.Object,
			conf: {
				component: VerificaLayerVincolatiComponent,
				options: undefined
			},
			readOnly: false
		}
	]
};

const sezioneInquadramentoIntComunicazione = {
	nome: "Inquadramento",
	schede: [
		{
			nome: "Mappa Interventi Forestali e Cartografie",
			tipo: "tabellaGis",
			tipoDati: TipoDatiScheda.Array,
			conf: schedaParticellaForestaleIntComunicazione,
			dictionaries: {
				rilevamenti: undefined
			}
		},
		{
			nome: "Inquadramento Natura 2000",
			tipo: "component",
			tipoDati: TipoDatiScheda.Object,
			conf: {
				component: VerificaLayerVincolatiComponent,
				options: undefined
			},
			readOnly: false
		}
	]
};


const sezioneInterventoComunicazione = {
	nome: "Interventi",
	schede: [
		{
			nome: "Interventi in ambiti non forestali",
			tipo: "component",
			tipoDati: TipoDatiScheda.Object,
			conf: {
				component: InterventiInAmbitiNonForestaliComponent,
				options: undefined
			},
			readOnly: false
		}
	]
};

function getSottoCategorie(caller: any, resources: any) {
	if (!resources.categorie) {
		resources.categorie = caller.authService.authFetch('/istanze/sottocategorie').then(
			(res: any) => {
				const categorie : any = res;
				return categorie;
			}
		);
	}
}

export function getSpeciForestali(caller: any, resources: any): Promise<any> {
	if (!resources) {
		throw new Error("Undefinded resources");
	}
	if (!resources.speciForestali) {
		return caller.authService.authFetch(
			`/info-speci-forestali`
		).then(
			(res: any) => {
				const speciForestali : any = resources.speciForestali = {};
				//let idxCategorie: Record<(string|number), any> = {};

				if (res.speci) {
					speciForestali.speci = res.speci;
					speciForestali.idxSpeci = Object.fromEntries(
						res.speci.map((x:any) => [x.id_specie, x])
					);
					speciForestali.idxTipoSopr = (Object as any).groupBy(res.speci, (s:any) => s.tipo_soprasuolo);
				}
				else {
					speciForestali.idxSpeci = {};
					speciForestali.idxTipoSopr = {};

					speciForestali.speci = [];
				}
				return speciForestali;
			}
		);
	}
	else {
		return Promise.resolve(resources.speciForestali);
	}
}

const tabProspettiRiepilogativi = {
	schedaInfo: {
		nome: "Prospetti Riepilogativi",
		tipo: "component",
		tipoDati: TipoDatiScheda.Object,
		conf: {
			component: ProspettiRiepilogativiComponent,
			options: undefined
		}
	},
	triggers: {
		delete: (caller: any, oldRow: any) => Promise.resolve(undefined),
		update: (caller: any, oldRow: any, newRow: any) => Promise.resolve(undefined),
		insert: (caller: any, newRow: any) => Promise.resolve(undefined),
		init: (caller: any, resources: any) => {
			getSpeciForestali(caller, resources);
			//getSottoCategorie(caller, resources);
			// if (!resources.speciForestali) {
			// 	return caller.authService.authFetch(
			// 		`/info-speci-forestali`
			// 	).then(
			// 		(res: any) => {
			// 			const speciForestali : any = resources.speciForestali = {};
			// 			//let idxCategorie: Record<(string|number), any> = {};
			// 			if (res.macrocategorie) {
			// 				speciForestali.macrocategorie = res.macrocategorie;
			// 				speciForestali.idxMacrocategorie = Object.fromEntries(
			// 					res.macrocategorie.map((x:any) => [x.id_macrocategoria, x])
			// 				);
			// 			}
			// 			else {
			// 				speciForestali.macrocategorie = [];
			// 				speciForestali.idxMacrocategorie = {};
			// 			}
			// 			if (res.categorie) {
			// 				//this.categorie = res.categorie;
			// 				speciForestali.idxCategorie = Object.fromEntries(
			// 					res.categorie.map((x:any) => [x.id_categoria, x])
			// 				);
			// 				res.categorie.forEach(
			// 					(c: any) => {
			// 						const macroPadre = speciForestali.idxMacrocategorie[c.id_macrocategoria];
			// 						if (macroPadre != undefined) {
			// 							let listPadre = macroPadre.categorie;
			// 							if (listPadre == undefined ) {
			// 								macroPadre.categorie = listPadre = [];
			// 							}
			// 							listPadre.push(c);
			// 						}
			// 						else {
			// 							console.error(`Macrocategoria ${c.id_macrocategoria}`);
			// 						}
			// 					}
			// 				);
			// 			}
			// 			else {
			// 				speciForestali.idxCategorie = {};
			// 			}
			// 			if (res.specie) {
			// 				speciForestali.idxSpeci = Object.fromEntries(
			// 					res.specie.map((x:any) => [x.id_specie, x])
			// 				);
			// 				res.specie.forEach(
			// 					(s: any) => {
			// 						const catPadre = speciForestali.idxCategorie[s.id_categoria];
			// 						if (catPadre != undefined) {
			// 							let listPadre = catPadre.speci;
			// 							if (listPadre == undefined) {
			// 								catPadre.speci = listPadre = [];
			// 							}
			// 							listPadre.push(s);
			// 						}
			// 					}
			// 				);
			// 			}
			// 			else {
			// 				speciForestali.idxSpeci = {};
			// 			}
			// 		}
			// 	);
			// }
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
			}
		]
	},
	columns: [
		{
			header: "Nome UO",
			dataField: "nomeUo"
		},
		{
			header: "Forma di Governo",
			dataField: "formaDiGoverno"
		},
		{
			header: "Tipologia di Soprasuolo",
			cellValue: (rowData: any, resources: any) => {
				return rowData.tipoDiSoprasuolo??"Misto";
			}
		},

		{
			header: "Prima Specie F.",
			cellValue: (rowData: any, resources: any) => {
				return (rowData.specie0 && resources.speciForestali && resources.speciForestali.idxSpeci) ? resources.speciForestali.idxSpeci[rowData.specie0].nome_specie : undefined;
			}
		},
		{
			header: "Seconda Specie F.",
			cellValue: (rowData: any, resources: any) => {
				return (rowData.specie1 && resources.speciForestali && resources.speciForestali) ? resources.speciForestali.idxSpeci[rowData.specie1].nome_specie : undefined;
			}
		},


		// {
		// 	header: "Prima Specie F.",
		// 	dataField: "nomePrimaSpecie"
		// },
		// {
		// 	header: "Seconda Specie F.",
		// 	dataField: "nomeSecondaSpecie"
		// },
		{
			header: "Metodo di Cubatura",
			dataField: "metodoDiCubatura"
		},
		{
			header: "Superficie Utile (ha)",
			dataField: "superficieUtile",
			dataFormat: DataFormat.Ettari

		}
	],
	constraints: [
		{
			customRow: (data: any) => {
				const arrCateg = ['LEGNA', 'COMBUSTIBILE', 'TRONCHI', 'CELLULOSA', 'ALTRO'];
				const arrValori = ['PercAutoc', 'PercVendita'];
				for(let i = 0; i <= 2; i++) {
					let idxSpecie = i.toString();
					const specie = data['specie'+idxSpecie];
					if (idxSpecie == '0' && specie == undefined) {
						return "Indicare la specie";
					}
					else {
						if (specie) {
							let totPerc = 0;
							for(let j = 0; j < arrCateg.length; j++) {
								const categ = arrCateg[j];
								for(let k = 0; k < arrValori.length; k++) {
									const nomeVal = arrValori[k];
									const val = Number.parseFloat(data[categ + nomeVal + idxSpecie]??'0');
									totPerc += val;
								}
							}
							if (totPerc != 100) {
								return "I dati degli assortimenti non sono validi";
							}
						}
					}
				}
				return undefined;
			}
		},
		{
			messaggio: "Indicare il metodo di cubatura",
			notNull: ["metodoDiCubatura"]
		}
	]
};

const schedaProspettiRiepilogativi = {
	nome: "Prospetti Riepilogativi",
	tipo: "tabella",
	tipoDati: TipoDatiScheda.Array,
	conf: tabProspettiRiepilogativi
}

const schedaVincolistica = {
	nome: "Inquadramento Vincolistica",
	tipo: "component",
	tipoDati: TipoDatiScheda.Object,
	conf: {
		component: InquadramentoVincolisticaComponent,
		options: {
			wizards: {
				wizardVinca: {
					1: {
						desc: "Gli interventi di taglio previsti devono rispettare le condizioni di obbligo generale e i vincoli specifici indicati nell’allegato 1 e 2 della DGR Umbria n. 1093 del 10/11/2021 e le superfici limite indicate a seconda dell'habitat interessato. Devono essere rispettate le condizioni per l’Habitat Prioritario/i”.\nScegliendo sì, ci si impegnerà nel rispetto delle condizioni indicate\n",
						scelte: {
							si: 5,
							no: 2
						}
					},
					2: {
						desc: "E' possibile proseguire con l’istanza sopra soglia unicamente se si allega l’esito del parere di VIncA altrimenti è necessario cambiare il tipo di istanza a istanza in deroga (richiesta soggetta ad autorizzazione).\nVuoi caricare l'elaborato di Vinca?\n",
						scelte: {
							si: 3,
							no: 4
						}
					},
					3: {
						desc: "Inserisci elaborato di Vinca",
						link: "upload-vinca"
					},
					4: {
						desc: "Varia tipo di richiesta a istanza in deroga",
						link: "link-deroga"
					},
					5: {
						desc: "Continua",
						link: "resta"
					}
				},
				wizardBase: {
					1: {
						desc: "Si desidera cambiare la tipologia in Istanza in Deroga?\n",
						scelte: {
							si: 4,
							no: 5
						}
					},
					4: {
						desc: "Varia tipo di richiesta a istanza in deroga",
						link: "link-deroga"
					},
					5: {
						desc: "Continua",
						link: "resta"
					}
				}
			}
		}
	},
	readOnly: false
};

const sezioneInquadramentoSopraSoglia = {
	nome: "Inquadramento",
	schede: [
		{
			nome: "Mappa Interventi Forestali e Cartografie",
			tipo: "tabellaGis",
			tipoDati: TipoDatiScheda.Array,
			conf: schedaParticellaForestaleSopra,
			dictionaries: {
				rilevamenti: undefined,
				uo: [2, 3],
				altr: [2, 4],
				viab: [2, 5]
			}
		},
		{
			nome: "Caratterizzazione Stazione Forestale",
			tipo: "component",
			tipoDati: TipoDatiScheda.Object,
			conf: {
				component: StazioneForestaleComponent,
				options: undefined
			},
			readOnly: true
		},
		// {
		// 	nome: "Contiguità tagli boschivi",
		// 	tipo: "component",
		// 	tipoDati: TipoDatiScheda.Object,
		// 	conf: {
		// 		component: ContiguitaTagliBoschiviComponent,
		// 		options: undefined
		// 	},
		// 	readOnly: false
		// },
		schedaVincolistica,
		{
			nome: "Unità Omogenee",
			tipo: "tabellaGis",
			tipoDati: TipoDatiScheda.Array,
			conf: schedaUnitaOmogenee,
			dictionaries: {
				pfor: [2, 0],
				rilevamenti: undefined,
				altr: [2, 4],
				viab: [2, 5]
			}
		},
		{
			nome: "Altri strati informativi",
			tipo: "tabellaGis",
			tipoDati: TipoDatiScheda.Array,
			conf: schedaAltriStratiInformativi,
			dictionaries: {
				pfor: [2, 0],
				uo: [2, 3],
				viab: [2, 5],
				rilevamenti: undefined
			}
		},
		{
			nome: "Viabilità forestale",
			tipo: "tabellaGis",
			tipoDati: TipoDatiScheda.Array,
			conf: schedaViabilitaForestale,
			dictionaries: {
				pfor: [2, 0],
				uo: [2, 3],
				altr: [2, 4],
				rilevamenti: undefined
			}
		},
		schedaProspettiRiepilogativi,
		schedaAllegati,
		schedaRiepilogoFinale
	]
};


const schedaAssortimentiRitraibili2 = {
	nome: "Assortimenti Ritraibili",
	tipo: "component",
	tipoDati: TipoDatiScheda.Object,
	ignoredProps: ['nomeSpecie0', 'nomeSpecie1'],
	conf: {
		component: AssortimentiRitraibili,
		options: undefined
	}
};


const sezioneSoprasuoloBoschivo = {
	nome: "Soprasuolo",
	schede: [
		schedaSoprasuolo,
		schedaAssortimentiRitraibili2
	]
};
export const schedeIstanzaTaglioBoschivo = [
	sezioneAnagraficaPratica,
	sezioneProponentiSotto,
	sezioneInquadramentoIntBoschivo,
	sezioneSoprasuoloBoschivo
];

export const schedeIstanzaInterventoComunicazione = [
	sezioneAnagraficaPratica,
	sezioneProponentiSotto,
	sezioneInquadramentoIntComunicazione,
	sezioneInterventoComunicazione
];

const schedaRilevamentiTaglioBoschivo = {
	conf: {
		mappa: {
			shape: {
				label: "nome",
				attribute: "wktGeometria",
				srid: "EPSG:4326"
			},
			view: {
				srid: "EPSG:3857",
				maxZoom: 20
			},
			layers: {
				pfor: {
					name: "Particella Forestale",
					color: 'rgba(0, 255, 0, 1.0)',
					type: MapLayerType.DictionaryLayer,
					conf: {
						src: 'pfor',
						shapeField: "shape",
						shapeSrid: "EPSG:3035"
					}
				}
			}
		},
		columns: [
			{
				header: "Nome Rilev",
				dataField: "nome"
			},
			{
				header: "Note",
				dataField: "note",
			},
			{
				header: "Tipo Rilevamento",
				dataField: "tipoRilevamento"
			},
			{
				header: "Tipo Geometria",
				dataField: "tipoGeometria"
			}
		],
		interactions: {
			element: [
				{
					label: "Modifica Valori",
					action: MapStdAction.ModifyAttributes,
					icon: "bi bi-card-text"
				}
			]
		},
		schedaInfo: {
			nome: "Rilievi in Campo",
			tipo: "component",
			tipoDati: TipoDatiScheda.Object,
			conf: {
				component: RilevamentoComponent,
				options: {
					srcSrid: "EPSG:4326",
					exportPaths: [
						{
							nome: 'Particella Forestale',
							path: [2, 0],
							tipoGeom: ['POLIGONO', 'PUNTO']
						}
					]
				}
			},
			readOnly: false
		}
	},
	dictionaries: {
		pfor: [2, 0]
	}
};
const schedaRilevamentiInterventoComunicazione = {
	conf: {
		mappa: {
			shape: {
				label: "nome",
				attribute: "wktGeometria",
				srid: "EPSG:4326"
			},
			view: {
				srid: "EPSG:3857",
				maxZoom: 20
			},
			layers: {
				pfor: {
					name: "Particella Forestale",
					color: 'rgba(0, 255, 0, 1.0)',
					type: MapLayerType.DictionaryLayer,
					conf: {
						src: 'pfor',
						shapeField: "shape",
						shapeSrid: "EPSG:3035"
					}
				}
			}
		},
		columns: [
			{
				header: "Nome Rilev",
				dataField: "nome"
			},
			{
				header: "Note",
				dataField: "note",
			},
			{
				header: "Tipo Rilevamento",
				dataField: "tipoRilevamento"
			},
			{
				header: "Tipo Geometria",
				dataField: "tipoGeometria"
			}
		],
		interactions: {
			element: [
				{
					label: "Modifica Valori",
					action: MapStdAction.ModifyAttributes,
					icon: "bi bi-card-text"
				}
			]
		},
		schedaInfo: {
			nome: "Rilievi in Campo",
			tipo: "component",
			tipoDati: TipoDatiScheda.Object,
			conf: {
				component: RilevamentoComponent,
				options: {
					srcSrid: "EPSG:4326",
					exportPaths: [
						{
							nome: 'Particella Forestale',
							path: [2, 0],
							tipoGeom: ['POLINEAIGONO']
						}
					]
				}
			},
			readOnly: false
		}
	},
	dictionaries: {
		pfor: [2, 0]
	}
};
const schedaRilevamentiSopra = {
	conf: {
		mappa: {
			shape: {
				label: "nome",
				attribute: "wktGeometria",
				srid: "EPSG:4326"
			},
			view: {
				srid: "EPSG:3857",
				maxZoom: 20
			},
			layers: {
				pfor: {
					name: "Particella Forestale",
					color: 'rgba(0, 255, 0, 1.0)',
					type: MapLayerType.DictionaryLayer,
					conf: {
						src: 'pfor',
						shapeField: "shape",
						shapeSrid: "EPSG:3035"
					}
				},
				uo: {
					name: "Unità Omogenee",
					type: MapLayerType.DictionaryLayer,
					color: 'rgba(255, 0, 0, 1.0)',
					conf: {
						src: 'uo',
						labelField: "nomeUO",
						shapeField: "shape",
						shapeSrid: "EPSG:3035"
					}
				},
				altr: {
					name: "Altri Strati Informativi",
					type: MapLayerType.DictionaryLayer,
					color: 'rgba(255, 0, 0, 1.0)',
					conf: {
						shapeField: "shape",
						shapeSrid: "EPSG:3035"
					}
				},
				viab: {
					name: "Viabilità Forestale",
					type: MapLayerType.DictionaryLayer,
					color: 'rgba(255, 0, 0, 1.0)',
					conf: {
						shapeField: "shape",
						shapeSrid: "EPSG:3035"
					}
				}
			}
		},
		columns: [
			{
				header: "Nome Rilev",
				dataField: "nome"
			},
			{
				header: "Note",
				dataField: "note",
			},
			{
				header: "Tipo Rilevamento",
				dataField: "tipoRilevamento"
			},
			{
				header: "Tipo Geometria",
				dataField: "tipoGeometria"
			}
		],
		interactions: {
			element: [
				{
					label: "Modifica Valori",
					action: MapStdAction.ModifyAttributes,
					icon: "bi bi-card-text"
				}
			]
		},
		schedaInfo: {
			nome: "Rilievi in Campo",
			tipo: "component",
			tipoDati: TipoDatiScheda.Object,
			conf: {
				component: RilevamentoComponent,
				options: {
					srcSrid: "EPSG:4326",
					exportPaths: [
						{
							nome: 'Intervento forestale',
							path: [2, 0],
							tipoGeom: ['POLIGONO']
						},
						// {
						// 	nome: 'Unità Omogenee',
						// 	path: [2, 3],
						// 	tipoGeom: ['POLIGONO']
						// },
						{
							nome: 'Altri Strati Informativi',
							path: [2, 4],
							tipoGeom: ['PUNTO']
						},
						{
							nome: 'Viabilità Forestale',
							path: [2, 5],
							tipoGeom: ['LINEA']
						}
					]
				}
			},
			readOnly: false
		}
	},
	dictionaries: {
		pfor: [2, 0],
		uo: [2, 3],
		altr: [2, 4],
		viab: [2, 5]
	}
};

export const schedeIstanzaSopraSoglia = [
	sezioneAnagraficaPratica,
	sezioneProponentiSopra,
	sezioneInquadramentoSopraSoglia
];
export const istanzaSopraSoglia = {
	schede: schedeIstanzaSopraSoglia,
	schedaRilevamenti: schedaRilevamentiSopra
};


//const schedaVincolisticaInDeroga = {...schedaVincolistica, readOnly: true};
const schedaVincolisticaInDeroga = {...schedaVincolistica };
const sezioneInquadramentoInDeroga = {...sezioneInquadramentoSopraSoglia };
sezioneInquadramentoInDeroga.schede = [...sezioneInquadramentoInDeroga.schede];
sezioneInquadramentoInDeroga.schede[sezioneInquadramentoInDeroga.schede.findIndex(x => x.nome == "Inquadramento Vincolistica")] = schedaVincolisticaInDeroga;


const schedeIstanzaInDeroga = [...schedeIstanzaSopraSoglia]
schedeIstanzaInDeroga[schedeIstanzaInDeroga.findIndex(x => x.nome == "Inquadramento")] = sezioneInquadramentoInDeroga;
const istanzaInDeroga = {
	schede: schedeIstanzaInDeroga,
	schedaRilevamenti: schedaRilevamentiSopra
};

const istanzaTaglioBoschivo = {
	schede: schedeIstanzaTaglioBoschivo,
	schedaRilevamenti: schedaRilevamentiTaglioBoschivo
};

const istanzaInterventoComunicazione = {
	schede: schedeIstanzaInterventoComunicazione,
	schedaRilevamenti: schedaRilevamentiInterventoComunicazione
}
export const confIstanze: Record<string, any> = {
	"SOPRA_SOGLIA": istanzaSopraSoglia,
	"ATTUAZIONE_PIANI": istanzaInDeroga,
	"IN_DEROGA": istanzaInDeroga,
	"TAGLIO_BOSCHIVO": istanzaTaglioBoschivo,
	"INTERVENTO_A_COMUNICAZIONE": istanzaInterventoComunicazione
};
