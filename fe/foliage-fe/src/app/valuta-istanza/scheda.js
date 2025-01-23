const tipoAllegati = "application/pdf, image/*, .p7m";

const selectCategoria = {
	"label": "Categoria",
	"widget": "html5",
	"tableView": true,
	"data": {
		"values": [
			{
				"label": "Convocazione",
				"value": "convocazione"
			},
			{
				"label": "Verbale",
				"value": "verbale"
			},
			{
				"label": "Parere Ente Terzo",
				"value": "parereEnteTerzo"
			},
			{
				"label": "Altro",
				"value": "altro"
			}
		]
	},
	"searchEnabled": false,
	"readOnlyValue": true,
	"validate": {
		"required": true
	},
	"key": "categoria",
	"type": "select",
	"input": true
};

const textTipoDoc = {
	"label": "Tipo Documento",
	"applyMaskOn": "change",
	"tableView": true,
	"validate": {
		"required": true
	},
	"unique": true,
	"key": "tipoDocumento",
	"type": "textfield",
	"input": true
};
const textNoteIstruttore = {
	"label": "Note Istruttore",
	"applyMaskOn": "change",
	"autoExpand": false,
	"tableView": true,
	"key": "noteIstruttore",
	"type": "textarea",
	"input": true
};
const textNote = {
	"label": "Note",
	"applyMaskOn": "change",
	"autoExpand": false,
	"tableView": true,
	"key": "note",
	"type": "textarea",
	"input": true
};
const allegato = {
	"label": "Allegato",
	"tableView": false,
	"storage": "base64",
	"webcam": false,
	"capture": false,
	"filePattern": tipoAllegati,
	"key": "allegato",
	"type": "file",
	"input": true,
	"validate": {
		"required": true
	}
};


const selectCategoriaReadOnly = {
	...selectCategoria,
	"disabled": true
};

const textTipoDocReadOnly = {
	...textTipoDoc,
	"disabled": true
};
const textNoteIstruttoreReadOnly = {
	...textNoteIstruttore,
	"disabled": true
};
export const schedaCaricatiPubblico = {
	"components": [
		{
			"label": "Richiesta Documenti",
			"reorder": false,
			"addAnotherPosition": "bottom",
			"layoutFixed": false,
			"enableRowGroups": false,
			"initEmpty": true,
			"tableView": false,
			"defaultValue": [
				{}
			],
			"key": "caricati",
			"type": "datagrid",
			"disableAddingRemovingRows": true,
			"input": true,
			"components": [
				{
					"label": "Documento",
					"columns": [
						{
							"components": [
								selectCategoria,
								textTipoDoc,
								allegato
							],
							"width": 12,
							"offset": 0,
							"push": 0,
							"pull": 0,
							"size": "md",
							"currentWidth": 6
						}/*,
						{
							"components": [
								textNoteIstruttore,
								textNote
							],
							"width": 6,
							"offset": 0,
							"push": 0,
							"pull": 0,
							"size": "md",
							"currentWidth": 6
						}*/
					],
					"hideLabel": true,
					"key": "columns",
					"type": "columns",
					"input": false,
					"tableView": false
				}
			]
		}
	]
};



const textNoteReadOnly = {
	...textNote,
	"disabled": true
};

const allegatoReadOnly = {
	...allegato,
	"disabled": true
};

export const schedaCaricatiIstruttore = {
	"components": [
		{
			"label": "Richiesta Documenti",
			"conditionalAddButton": "show = false",
			"reorder": false,
			"addAnotherPosition": "bottom",
			"layoutFixed": false,
			"enableRowGroups": false,
			"initEmpty": true,
			"tableView": false,
			"defaultValue": [
				{}
			],
			"key": "caricati",
			"type": "datagrid",
			"disableAddingRemovingRows": true,
			"input": true,
			"components": [
				{
					"label": "Documento",
					"columns": [
						{
							"components": [
								selectCategoriaReadOnly,
								textTipoDocReadOnly,
								allegatoReadOnly
							],
							"width": 12,
							"offset": 0,
							"push": 0,
							"pull": 0,
							"size": "md",
							"currentWidth": 6
						}/*,
						{
							"components": [
								textNoteIstruttoreReadOnly,
								textNoteReadOnly
							],
							"width": 6,
							"offset": 0,
							"push": 0,
							"pull": 0,
							"size": "md",
							"currentWidth": 6
						}*/
					],
					"hideLabel": true,
					"key": "columns",
					"type": "columns",
					"input": false,
					"tableView": false
				}
			]
		}
	]
};

export const schedaRichiestaIstruttore = {
	"components": [
		{
			"label": "Richiesta Documenti",
			"reorder": false,
			"addAnotherPosition": "bottom",
			"layoutFixed": false,
			"enableRowGroups": false,
			"initEmpty": true,
			"tableView": false,
			"defaultValue": [
				{}
			],
			"key": "richieste",
			"type": "datagrid",
			"input": true,
			"components": [
				selectCategoria,
				textTipoDoc/*,
				textNoteIstruttore*/
			]
		}
	]
};


export const schedaRichiestaPubblico = {
	"components": [
		{
			"label": "Richiesta Documenti",
			"disableAddingRemovingRows": true,
			"reorder": false,
			"addAnotherPosition": "bottom",
			"layoutFixed": false,
			"enableRowGroups": false,
			"initEmpty": true,
			"tableView": false,
			"defaultValue": [
				{}
			],
			"key": "richieste",
			"type": "datagrid",
			"input": true,
			"components": [
				{
					"label": "Documento",
					"columns": [
						{
							"components": [
								selectCategoriaReadOnly,
								textTipoDocReadOnly,
								allegato
							],
							"width": 12,
							"offset": 0,
							"push": 0,
							"pull": 0,
							"size": "md",
							"currentWidth": 6
						}
					],
					"hideLabel": true,
					"key": "columns",
					"type": "columns",
					"input": false,
					"tableView": false
				}
			]
		}
	]
};

