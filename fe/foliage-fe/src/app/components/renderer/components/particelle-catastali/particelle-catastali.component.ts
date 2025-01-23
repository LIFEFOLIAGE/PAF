import { Component, EventEmitter, Input, OnChanges, OnInit, Output, Directive, SimpleChanges, ElementRef } from '@angular/core';
import { ComponentDataType, ComponentRecordType, ComponentType } from "../ComponentInterface";
import { SimpleObjectChange } from "../../../shared/editor-scheda/editor-scheda.component";
import { IstanzaComponentInterface } from '../../../interfaces/istanza-component-interface';
import { ExportValue } from '../../../istanze/editor-istanza/editor-istanza.component';
import { environment } from '../../../../../environments/environment';

const apiUrl = `${environment.apiOrigin??window.origin}${environment.apiServerPath}`;
const schedaFormioParticelleCatastali = {
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
		// {
		// 	"label": "Columns",
		// 	"columns": [
		// 		{
		// 			"components": [
		// 				{
		// 					"label": "Superficie catastale (mq)",
		// 					"applyMaskOn": "change",
		// 					"mask": false,
		// 					"tableView": false,
		// 					"delimiter": false,
		// 					"requireDecimal": false,
		// 					"inputFormat": "plain",
		// 					"truncateMultipleSpaces": false,
		// 					"validate": {
		// 						"required": true,
		// 						"min": 0
		// 					},
		// 					"key": "superficie",
		// 					"type": "number",
		// 					"input": true,
		// 					"decimalLimit": 0
		// 				}
		// 			],
		// 			"width": 6,
		// 			"offset": 0,
		// 			"push": 0,
		// 			"pull": 0,
		// 			"size": "md",
		// 			"currentWidth": 6
		// 		},
		// 		{
		// 			"components": [
		// 				{
		// 					"label": "Superficie di intervento (mq)",
		// 					"applyMaskOn": "change",
		// 					"mask": false,
		// 					"tableView": false,
		// 					"delimiter": false,
		// 					"requireDecimal": false,
		// 					"inputFormat": "plain",
		// 					"truncateMultipleSpaces": false,
		// 					"validate": {
		// 						"required": true,
		// 						"custom": "valid = (input <= data.superficie) ? true : 'La superficie dell&quot;intervento non può superare la superficie catastale';",
		// 						//"custom": "if (input <= data.superficie) {valid = true;} else {valid = 'La superficie dell&quot;intervento non può superare la superficie catastale';}",
		// 						"min": 0
		// 					},
		// 					"key": "superficieInterventoPart",
		// 					"type": "number",
		// 					"input": true,
		// 					"decimalLimit": 0
		// 				}
		// 			],
		// 			"width": 6,
		// 			"offset": 0,
		// 			"push": 0,
		// 			"pull": 0,
		// 			"size": "md",
		// 			"currentWidth": 6
		// 		}
		// 	],
		// 	"key": "columns2",
		// 	"type": "columns",
		// 	"input": false,
		// 	"tableView": false
		// }
	]
};

@Component({
	selector: 'app-particelle-catastali',
	templateUrl: './particelle-catastali.component.html'
})
export class ParticellaCatastaleComponent implements ComponentType<SimpleObjectChange>, OnInit, OnChanges {
	@Input() dati: ComponentDataType = {};
	@Input() isReadOnly: boolean = false;
	@Input() context: any;
	@Input() resources: any;
	@Input() componentOptions: any = {};
	@Input() dictionariesData?: Record<string, any>;

	@Output() changeEdit: EventEmitter<boolean> = new EventEmitter<boolean>();
	@Output() dataChanged: EventEmitter<SimpleObjectChange> = new EventEmitter<SimpleObjectChange>();
	@Output() componentInit: EventEmitter<IstanzaComponentInterface> = new EventEmitter<IstanzaComponentInterface>();
	@Output() export: EventEmitter<ExportValue> = new EventEmitter<ExportValue>();


	codIstanza!: string;
	datiEffettivi: any = {};
	errori: Record<string, string> = {};
	modifiche: SimpleObjectChange = {};

	datiRiferimentiParticella: any = {};
	private _formioIoRenderer?: IstanzaComponentInterface;
	readonly schedaFormioParticelleCatastali = schedaFormioParticelleCatastali;
	
	checks: string[][] = [];
	ngOnInit(): void {
		console.log("init");

		this.checks = [['superficie', 'superficie catastale']]

		if (this.componentOptions.isIstanzaSopraSoglia) {
			this.checks.push(['superficieInterventoPart', 'superficie di intervento']);
		}
	}
	
	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case "context": {
					this.codIstanza = currValue?.codIstanza;
				};break;
				case "dati": {
					currValue['superficie'] = (currValue['superficie'] == undefined)
						? 0
						: Number.parseFloat(currValue['superficie']).toFixed(2);

					this.datiEffettivi = { ...currValue };
					this.datiRiferimentiParticella = { ...currValue };
					this.errori = {};

					this.checkErrors();
					// this.modifiche = {};
				}; break;
			}
		}
	}
	
	onRiferiementiPartocelleDataChanged($event: SimpleObjectChange) {
		this.datiEffettivi = { ...this.datiEffettivi, ...$event};
		this.modifiche = { ...this.modifiche, ...$event }
		this.dataChanged.emit(this.modifiche);
	}

	onParticelleCatastaliComponentInit($event: IstanzaComponentInterface) {
		this._formioIoRenderer = $event;
		this.componentInit.emit({ getValidity: this.getValidity.bind(this) });
	}

	checkErrors() {

		

		this.checks.forEach(
			(s: string[]) => {
				const key = s[0];
				const nome = s[1];
				const v = this.datiEffettivi[key];


				if (v == undefined) {
					this.errori[key] = `La ${nome} è un valore richiesto`;
				}
				else {
					if (isNaN(v)) {
						this.errori[key] = "Inserire un numero";
		
					}
					else {
						if (v <= 0) {
							this.errori[key] = `La ${nome} deve essere maggiore di 0`;
						}
						else {
							delete this.errori[key];
						}
					}
				}


				// if (v == undefined || isNaN(v) ) {
				// 	this.errori[key] = `${nome} è un valore richiesto`;
				// }
				// else {
				// 	if (v <= 0) {

				// 	}
				// 	else {
				// 		delete this.errori[key];
				// 	}
				// }

			}
		);
		if (this.componentOptions.isIstanzaSopraSoglia) {
			if (this.datiEffettivi['superficie'] < this.datiEffettivi['superficieInterventoPart']) {
				this.errori['superficieInterventoPart'] =  "La superficie dell'intervento non può superare la superficie catastale"
			}
			else {
				delete this.errori['superficieInterventoPart'];
			}
		}
	}

	getValidity: () => (boolean) = () => {
		this.checkErrors();

		const soprasuoloBoschivoValid = this._formioIoRenderer?.getValidity() ?? false;

		// const dettagliValidi = this.errori["sommaError"] == undefined;
		// isValid = isValid && dettagliValidi;

		const dettagliValidi = Object.keys(this.errori).length == 0;
		const isValid = soprasuoloBoschivoValid && dettagliValidi;

		// return true;
		return isValid;
	};
	
	onChangeProperty(propertyName: string, newVal: any) {
		if (!this.isReadOnly) {
			const currChange = Object.fromEntries([[propertyName, newVal]]);
			this.datiEffettivi = { ...this.datiEffettivi, ...currChange };

			this.modifiche = {
				...this.modifiche,
				...currChange
			};

			this.dataChanged.emit(this.modifiche);
		}
		this.checkErrors();
	}

	onChangeSuperficie(propertyName: string, newVal?: number) {
		this.onChangeProperty(propertyName, newVal);
	}

	onChangeInputSuperficie(propertyName: string, newVal?: number) {
		
		this.onChangeSuperficie(propertyName, newVal);
	}
}