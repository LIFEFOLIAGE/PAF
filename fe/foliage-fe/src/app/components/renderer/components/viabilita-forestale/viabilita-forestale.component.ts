import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { ComponentRecordType, ComponentType } from "../ComponentInterface";
import { SimpleObjectChange } from "../../../shared/editor-scheda/editor-scheda.component";
import { IstanzaComponentInterface } from "../../../interfaces/istanza-component-interface";
import viabilitaTableData from './viabilita-forestale-data';
import { ExportValue } from 'src/app/components/istanze/editor-istanza/editor-istanza.component';

@Component({
	selector: 'app-viabilita-forestale',
	templateUrl: './viabilita-forestale.component.html',
	styleUrls: ['./viabilita-forestale.component.css']
})
export class ViabilitaForestaleComponent implements ComponentType<SimpleObjectChange>, OnInit, OnChanges {
	@Input() dati: ComponentRecordType = {};
	@Input() context: any;
	@Input() resources: any;
	@Input() isReadOnly: boolean = false;
	@Input() componentOptions: any;
	@Input() dictionariesData?: Record<string, any>;

	@Output() changeEdit: EventEmitter<boolean> = new EventEmitter<boolean>();
	@Output() dataChanged: EventEmitter<SimpleObjectChange> = new EventEmitter<SimpleObjectChange>();
	@Output() componentInit: EventEmitter<IstanzaComponentInterface> = new EventEmitter<IstanzaComponentInterface>();
	@Output() export: EventEmitter<ExportValue> = new EventEmitter<ExportValue>();

	initialData: ComponentRecordType = {};
	codViabilitaSelezionata?: number = undefined;

	readonly tipiDiViabilita = viabilitaTableData;

	ngOnInit(): void {
		this.componentInit.emit({ getValidity: this.getValidity.bind(this) });
	}

	ngOnChanges(changes: SimpleChanges): void {
		this.initialData = { ...this.dati };

		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case 'dati': {
					this.initialData = {...currValue};
					this.codViabilitaSelezionata = currValue['codTipoViabilita'];
				}; break;
				case 'codTipoViabilita': {
					this.codViabilitaSelezionata = currValue;
				}; break;
			}
		};
		//this.componentInit.emit({ getValidity: this.getValidity.bind(this) });
	}

	setTipoViabilita(viabilita: {
		descTipoViabilita: string;
		codTipoViabilita: number;
		nomeTipoViabilita: string
	}) {
		this.codViabilitaSelezionata = viabilita.codTipoViabilita;

		const changes: SimpleObjectChange = {
			codTipoViabilita: viabilita.codTipoViabilita,
			nomeTipoViabilita: viabilita.nomeTipoViabilita
		};

		this.dataChanged.emit(changes);
	}

	getValidity: () => boolean = () => {
		return this.codViabilitaSelezionata != undefined;
	};
}
