import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { ComponentDataType, ComponentType } from "../ComponentInterface";
import { SimpleObjectChange } from "../../../shared/editor-scheda/editor-scheda.component";
import { IstanzaComponentInterface } from "../../../interfaces/istanza-component-interface";
import { formAllegati } from "./formAllegati";
import { deepClone } from "../../../../services/utils";
import { ExportValue } from 'src/app/components/istanze/editor-istanza/editor-istanza.component';

@Component({
	selector: 'app-carica-allegati',
	templateUrl: './carica-allegati.component.html',
	styleUrls: ['./carica-allegati.component.css']
})
export class CaricaAllegatiComponent implements ComponentType<SimpleObjectChange>, OnInit, OnChanges {
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

	private _istanzaComponentInterface?: IstanzaComponentInterface;

	formAllegati: any = formAllegati;
	datiFormAllegati: any;
	datiFormAllegatiIniziale: any;
	modifiche: SimpleObjectChange = {};


	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case "context": {
				}
					;
					break;
				case "dati": {
					// const datiProva = {
					// 	"documentiAllegati": [
					// 		{
					// 			"categoria": "",
					// 			"tipoDocumento": "",
					// 			"containerCategoria": {
					// 				"categoriaAllegato": "parere"
					// 			},
					// 			"fileAllegato": [
					// 				{
					// 					"storage": "base64",
					// 					"name": "Info-59204c8b-676b-46ae-947b-ca8ed0c33ced.txt",
					// 					"url": "data:text/plain;base64,QXBwbGljYXppb25pIHNjYXJpY2F0ZSBkYWdsaSBzdG9yZXMgbmVsIHBlcmlvZG8gZGVsbG8gc3ZpbHVwcG8u",
					// 					"size": 63,
					// 					"type": "text/plain",
					// 					"originalName": "Info.txt",
					// 					"hash": "6d2839d0d8f18a7e66a302cae99f865f"
					// 				}
					// 			]
					// 		},
					// 		{
					// 			"containerCategoria": {
					// 				"categoriaAllegato": "altraDocumentazione",
					// 				"nomeDocumento": "fffffffffffffffffff"
					// 			},
					// 			"fileAllegato": [
					// 				{
					// 					"storage": "base64",
					// 					"name": "Info-855fef03-370d-4eae-b629-2aa60d06ba28.txt",
					// 					"url": "data:text/plain;base64,QXBwbGljYXppb25pIHNjYXJpY2F0ZSBkYWdsaSBzdG9yZXMgbmVsIHBlcmlvZG8gZGVsbG8gc3ZpbHVwcG8u",
					// 					"size": 63,
					// 					"type": "text/plain",
					// 					"originalName": "Info.txt",
					// 					"hash": "6d2839d0d8f18a7e66a302cae99f865f"
					// 				}
					// 			]
					// 		}
					// 	]
					// };
					// this.caricaDatiForm(datiProva);

					this.caricaDatiForm(currValue);
				}
					;
					break;
			}
		}
	}

	ngOnInit(): void {
		// this.onComponentInit.emit({ getValidity: this.getValidity.bind(this) });
	}

	private caricaDatiForm(dati: any) {
		if (dati.documentiAllegati == undefined) {
			this.datiFormAllegatiIniziale = {};
		}
		else {
			this.datiFormAllegatiIniziale = {
				documentiAllegati: dati.documentiAllegati
			};
		}
		this.datiFormAllegati = deepClone(this.datiFormAllegatiIniziale);

	}

	onDataFormAllegatiChanged(changes: any) {
		this.datiFormAllegati = { ...this.datiFormAllegati, ...changes };

		this.modifiche = {
			...this.datiFormAllegati,
			...this.modifiche,
			...changes
		};

		this.dataChanged.emit(this.modifiche);
	}

	onFormInit($event: IstanzaComponentInterface) {
		this._istanzaComponentInterface = $event;
		this.componentInit.emit({ getValidity: this.getValidity.bind(this) });
	}

	getValidity: () => boolean = () => {
		return this._istanzaComponentInterface?.getValidity() ?? false;
	};
}
