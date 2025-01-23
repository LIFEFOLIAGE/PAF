import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { ComponentDataType, ComponentType } from "../ComponentInterface";
import { SimpleObjectChange } from "../../../shared/editor-scheda/editor-scheda.component";
import { IstanzaComponentInterface } from "../../../interfaces/istanza-component-interface";
import { ExportValue } from 'src/app/components/istanze/editor-istanza/editor-istanza.component';

@Component({
	selector: 'app-rilevamento',
	templateUrl: './rilevamento.component.html',
	styleUrl: './rilevamento.component.css'
})
export class RilevamentoComponent implements ComponentType<SimpleObjectChange> {
	@Input() set dati(value: ComponentDataType) {
		this.datiEffettivi = value;
	}
	@Input() isReadOnly: boolean = false;
	@Input() context: any;
	@Input() set resources(value: any) {
		this.canExport = !(value.isReadOnly??true);
	}
	canExport: boolean = false;
	@Input() set componentOptions(value: any) {
		this.exports = {};
		if (value.exportPaths) {
			value.exportPaths.forEach(
				(ep: any) => {
					ep.tipoGeom.forEach(
						(tg: string) => {
							const elem = {
								nome: ep.nome,
								path: ep.path
							};
							const exp = this.exports[tg];
							if (exp) {
								exp.push(elem);
							}
							else {
								this.exports[tg] = [elem];
							}
						}
					)
				}
			);
		}
		this.srcSrid = value.srcSrid;
	}
	exports: Record<string, any[]> = {};
	srcSrid?: string;
	@Input() dictionariesData?: Record<string, any>;

	@Output() changeEdit: EventEmitter<boolean> = new EventEmitter<boolean>();
	@Output() dataChanged: EventEmitter<SimpleObjectChange> = new EventEmitter<SimpleObjectChange>();
	@Output() componentInit: EventEmitter<IstanzaComponentInterface> = new EventEmitter<IstanzaComponentInterface>();
	@Output() export: EventEmitter<ExportValue> = new EventEmitter<ExportValue>();
	datiEffettivi: any = undefined;
	currFotoIdx: number = 0;
	setFotoIdx(index: any) {
		this.currFotoIdx = index;
	}

	esporta(path: number[]) {
		const value: ExportValue = {
			value: this.datiEffettivi.wktGeometria,
			opts: {
				dataProjection: this.srcSrid
			},
			idxSezione: path[0],
			idxScheda: path[1]
		};
		console.log(value);
		this.export.emit(value);
	}
}