import { Component, EventEmitter, Input, OnInit, OnChanges, Output, SimpleChanges } from '@angular/core';
import { ComponentDataType, ComponentType } from "../ComponentInterface";
import { SimpleObjectChange } from "../../../shared/editor-scheda/editor-scheda.component";
import { IstanzaComponentInterface } from "../../../interfaces/istanza-component-interface";
import { ExportValue } from 'src/app/components/istanze/editor-istanza/editor-istanza.component';

@Component({
	selector: 'app-scheda-altri-stati',
	templateUrl: './scheda-altri-stati.component.html'
})
export class SchedaAltriStratiComponent implements ComponentType<SimpleObjectChange>, OnInit, OnChanges {
	@Input() dati: ComponentDataType = {};
	@Input() isReadOnly: boolean = false;
	@Input() context: any;
	@Input() resources: any;
	@Input() componentOptions: any;
	@Input() dictionariesData?: Record<string, any>;

	@Output() changeEdit: EventEmitter<boolean> = new EventEmitter<boolean>();
	@Output() dataChanged: EventEmitter<SimpleObjectChange> = new EventEmitter<SimpleObjectChange>();
	@Output() componentInit: EventEmitter<IstanzaComponentInterface> = new EventEmitter<IstanzaComponentInterface>();
	@Output() export: EventEmitter<ExportValue> = new EventEmitter<ExportValue>();


	datiEffettivi: any = {};
	modifiche: SimpleObjectChange = {};
	errori: Record<string, any> = {};
	errString: string = '';

	elencoAlternative: any[] = [
		{
			nome: "isAreaTradizionale",
			label: "Area di Saggio Tradizionale"
		},
		{
			nome: "isAreaDimostrativa",
			label: "Area Dimostrativa"
		},
		{
			nome: "isImposto",
			label: "Imposto"
		},
		{
			nome: "isAreaRelascopica",
			label: "Area di Saggio Relascopica"
		}
	];

	unitaOmogenee: any[] = [];

	ngOnInit(): void {
		this.validaErrori();
		this.componentInit.emit({getValidity: this.getErrori.bind(this)});
	}

	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case "dictionariesData": {
					this.unitaOmogenee = currValue.uo;
				}; break;
				case "dati": {
					this.datiEffettivi = {...currValue};
					this.modifiche = {};
				}
			}
		}
		//this.componentInit.emit({getValidity: this.validaErrori.bind(this)});
	}

	validaErrori() {
		this.errori["alternative"] = this.elencoAlternative.reduce(
			(pv: boolean, cv: any) => (pv || this.datiEffettivi[cv.nome]),
			false
		) ?  undefined : "Scegliere almeno una delle tipologie disponibili";
		this.errori["nomeArea"] = (this.datiEffettivi.nomeArea??'' == '') ? undefined : "Valore richiesto";
		console.log(this.errori);
		this.errString = JSON.stringify(this.errori);
	}
	getErrori() {
		return Object.values(this.errori).filter((x: any) => x).length == 0;
	}
	changeProprieta(nome: string, value: any) {
		this.modifiche = {...this.modifiche}
		if (value == undefined) {
			delete this.modifiche[nome];
		}
		else {
			this.modifiche[nome] = value;
		}
		this.datiEffettivi[nome] = value;
		this.dataChanged.emit(this.modifiche);
		this.validaErrori();
	}
	changeAlternativa(nome: string) {
		const val = (this.datiEffettivi[nome]);
		const boolVal = val && val == true;
		this.changeProprieta(nome, !boolVal);
	}
}
