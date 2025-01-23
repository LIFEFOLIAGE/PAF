import {
		Component, ElementRef, Input,
		OnChanges, SimpleChanges,
		Output, EventEmitter,
		Directive, ViewChild, QueryList
	} from '@angular/core';
import { RowData } from '../modules/table/table.component';

//import { BaseMap } from './base-map';
import { InteractiveMap } from './interactive-map';
import { LayerBE } from "../gis-table/models/layer-be";
import { ExportValue } from '../components/istanze/editor-istanza/editor-istanza.component';

//import { Scheda } from '../components/shared/editor-scheda/editor-scheda.component';

@Directive({
	selector: '[olMap]'
})
export class OlMapDirective {
	constructor(public elem: ElementRef) {
		console.log(elem);
	}
}

@Directive({
	selector: '[olPopUp]'
})
export class OlPopupDirective {
	constructor(public elem: ElementRef) {
		console.log(elem);
	}
}



@Component({
	selector: 'app-map',
	templateUrl: './map.component.html',
	styleUrls: ['./map.component.css'],
})
export class MapComponent implements OnChanges {
	map!: InteractiveMap;
	
	@Input() isReadOnly: boolean = false;
	@Input() dati : any = [];
	@Input() conf : any = {};
	@Input() context: any;
	@Input() schedaInfo : any = undefined;
	@Input() inserting : any = undefined;
	@Input() resources: any = undefined;

	@Input() dictionariesData?: Record<string, any>;
	_importedValue: any;
	@Input() set importedValue (value: any) {
		this._importedValue = value;
	}

	mapElement?: ElementRef;
	@Output() mapCreated = new EventEmitter<InteractiveMap>();
	@Output() dataChanged = new EventEmitter<any>();
	@Output() cancelChanges = new EventEmitter<any>();
	@Output() selection = new EventEmitter<(RowData|undefined)>();
	@Output() highlight = new EventEmitter<(RowData|undefined)>();
	
	@Output() export = new EventEmitter<ExportValue>();
	onExport(value: ExportValue) {
		console.log(value);
		this.export.emit(value);
	}

	@Output() schedaElementCreated: EventEmitter<QueryList<ElementRef>> = new EventEmitter<QueryList<ElementRef>>();
	
	constructor() {
		this.map = new InteractiveMap(this.onMapSelection.bind(this), this.onMapHighlight.bind(this));
	}
	
	ngOnChanges(changes: SimpleChanges): void {
		console.log(changes);
		let conf: any = undefined;
		let dati: any = undefined;
		let dictionaries: any = undefined;


		for (let propName in changes) {
			const v = changes[propName].currentValue;
			switch (propName) {
				case "dati": {
					dati = v;
					// if (this.map) {
					// 	this.map.updateData(this.conf, v);
					// 	this.map.updateConfLayers(this.context, this.conf, this.dictionariesData);
					// 	this.map.mostraOverlay(undefined);
					// }
				}; break;
				case "conf": {
					conf = v;
					// if (this.map) {
					// 	this.drawMap();
					// 	this.map.updateConfLayers(this.context, this.conf, this.dictionariesData);
					// }
				}; break;
				case "dictionariesData": {
					dictionaries = v;

					// if (this.map) {
					// 	this.map.updateConfLayers(this.context, this.conf, this.dictionariesData);
					// }
				}
			}
		}
		if (this.map) {
			if (conf == undefined) {
				if (dati != undefined) {
					this.map.updateData(this.conf, dati);
					this.map.mostraOverlay(undefined);
				}
				if (dictionaries != undefined) {
					this.map.updateConfLayers(this.context, this.conf.mappa, dictionaries);
				}
			}
			else {
				if (dati != undefined) {
					this.drawMap(dati, dictionaries??this.dictionariesData);
				}
				else {
					this.drawMap(this.dati, dictionaries??this.dictionariesData);
				}
			}
		}
	}

	indexOfRow(v: any) : (number | undefined) {
		return v ? this.dati.indexOf(v) : undefined;
	}
	selectedRowIdx?: number;

	//selInteraction: MoveInteraction = new MoveInteraction(this);
	onMapSelection(selected: any, deselected: any) {
		//console.log({selected, deselected});
		const sel = selected[0];
		if (sel) {
			const idx = sel.get('idx');
			const dati = sel.get('dati');

			this.schedaInfo = {...this.schedaInfo };
			this.schedaInfo.dati = dati;
			this.selectedRowIdx = idx;
			this.selection.emit({data: dati, idx});
		}
		else {
			this.selectedRowIdx = undefined;
			if (this.schedaInfo) {
				this.schedaInfo.dati = undefined;
			}
			this.selection.emit(undefined);
		}
	}
	onMapHighlight(selected: any, deselected: any) {
		//console.log({selected, deselected});
		// console.log(this.selectedRowIdx);
		// console.log(selected.length);
		
		const sel = ((this.selectedRowIdx) ? selected.filter((f: any) => f.get('idx') != this.selectedRowIdx) : selected)[0];
		if (sel) {
			const idx = sel.get('idx');
			const dati = sel.get('dati');
			this.highlight.emit({data: dati, idx});
		}
		else {
			this.highlight.emit(undefined);
		}
	}

	@ViewChild(OlMapDirective)
	set olMap(directive: OlMapDirective) {
		if (directive) {
			console.log({olMap: directive});
			this.mapElement = directive.elem;
			this.drawMap(this.dati, this.dictionariesData);
		}
	};

	popupElement!: ElementRef;
	@ViewChild(OlPopupDirective)
	set olPopup(directive: OlPopupDirective) {
		if (directive) {
			console.log({olPopup: directive});
			this.popupElement = directive.elem;
			this.drawMap(this.dati, this.dictionariesData);
		}
	};

	unhighlightOnLeave: (() => void) = (() => {
		if (this.map != undefined) {
			this.map.evidenzia(undefined, undefined);
		}
	}).bind(this);
	drawMap(dati: any, dictionaries: any) {
		if (this.mapElement && this.popupElement){
			this.mapElement.nativeElement.removeEventListener(
				'mouseleave',
				this.unhighlightOnLeave
			);
			
			for (const node of this.mapElement.nativeElement.children) {
				this.mapElement.nativeElement.removeChild(node)
			}
			console.log("draw map");
			this.map.drawMap(
				this.mapElement.nativeElement, this.popupElement.nativeElement,
				this.conf, dati,
				this.context,
				dictionaries,
				() => {
					this.mapCreated.emit(this.map);
				}
			);
			this.mapElement.nativeElement.addEventListener(
				'mouseleave',
				this.unhighlightOnLeave
			);
		}
	}

	// @Input() selectedRow? : any;
	// @Input() highlightRow? : any;
	onCancelChangesBound = this.onCancelChanges.bind(this);
	onCancelChanges(data: any) {
		this.map.mostraOverlayAtPoint(undefined);

		this.cancelChanges.emit(data);
		return Promise.resolve(undefined);
	}

	onDataConfBound = this.onDataConfirmed.bind(this);
	onDataConfirmed(evento: any) {
		console.log({dataConfirmed: evento});
		const selIdx = this.map.selectedRowIdx;
		this.dataChanged.emit(
			{
				pos: selIdx,
				value: evento.dati,
				isValid: evento.isValid
			}
		);

		//this.map.mostraOverlayAtPoint(undefined);
		return Promise.resolve(undefined);

		//this.dataChanged.emit(evento);

		// let modifiche = evento.modifiche;
		// let dati = evento.dati;
		// let isValid = evento.isValid;
		// if (this.map.selectedRowIdx) {
		// 	this.dati[this.map.selectedRowIdx] = dati;
		// }

		// this.datiPratica[this.idxSezioneSelezionata][this.idxSchedaSelezionata] = dati;
		// this.modifichePratica[this.idxSezioneSelezionata][this.idxSchedaSelezionata] = modifiche;
		// this.validitaPratica[this.idxSezioneSelezionata][this.idxSchedaSelezionata] = isValid;

		// console.log(this.modifichePratica);
	}
	onDataChanged(evento: any) {
		console.log({onMapDataChanged: evento});
		// this.hasChanges = evento && (Object.keys(evento).length > 0);
	}

	getSelectedRow() : any {
		if (this.dati && this.map.selectedRowIdx != undefined) {
			return this.dati[this.map.selectedRowIdx]
		}
		else {
			return this.inserting;
		}
	}
	onSchedaElementCreated(element: QueryList<ElementRef>) {
		this.schedaElementCreated.emit(element);
	}
}
