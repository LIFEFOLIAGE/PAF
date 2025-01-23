import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import {
	ComponentDataType,
	ComponentListType, ComponentRecordType,
	ComponentType
} from "../ComponentInterface";
import { InterventoConsentito, Rilievo, VerificaLayerVincolatiAPIModel } from "./models/VerificaLayerVincolatiAPIModel";
import { DomSanitizer, SafeHtml } from "@angular/platform-browser";
import { SimpleObjectChange } from 'src/app/components/shared/editor-scheda/editor-scheda.component';
import { SessionManagerService } from 'src/app/services/session-manager.service';
import { IstanzaComponentInterface } from "../../../interfaces/istanza-component-interface";
import { ExportValue } from 'src/app/components/istanze/editor-istanza/editor-istanza.component';

const linkAvvisi = {
	NAT2K_SOTTO_UMBRIA: [
		// {
		// 	nome: "documento",
		// 	path: ""
		// },
		// {
		// 	nome: "allegato",
		// 	path: ""
		// }
	]
}

@Component({
	selector: 'app-verifica-layer-vincolati',
	templateUrl: './verifica-layer-vincolati.component.html',
	styleUrls: ['./verifica-layer-vincolati.component.css']
})
export class VerificaLayerVincolatiComponent implements ComponentType<SimpleObjectChange>, OnInit, OnChanges {
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
	idSchedaInterventoSelezionata?: number = undefined;
	existsLayers?: boolean = undefined;
	linkAvvisi: Record<string, {nome: string, path:string}[]> = linkAvvisi;
	rilieviArray: Rilievo[] = [];
	avvisiArray: {code: string, value: string}[] = [];
	interventiConsentitiArray: InterventoConsentito[] = [];
	hasVisioneVincoli?: boolean;
	errore?: string;
	//messaggioFinale: SafeHtml | undefined = undefined;

	codIstanza!: string;
	
	constructor(
		private sanitizer: DomSanitizer,
		private sessionManager: SessionManagerService
	) {
	}

	ngOnChanges(changes: SimpleChanges): void {
		this.initialData = { ...this.dati };
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case 'dati': {
					this.initialData = {...currValue};
					this.idSchedaInterventoSelezionata = currValue['idSchedaInterventoSelezionata'];
					this.hasVisioneVincoli = currValue['hasVisioneVincoli'];
				}; break;
				case 'idSchedaInterventoSelezionata': {
					this.idSchedaInterventoSelezionata = currValue;

				}; break;
			}
		};
		this.checkErrori();
		//this.componentInit.emit({getValidity: this.getValidity.bind(this)});
	}
	ngOnInit(): void {
		this.codIstanza = this.context.codIstanza;
		//this.changeEdit.emit(true);

		this.sessionManager.profileFetch(`/istanze/${this.codIstanza}/interventi-consentiti`).then(
			(res) => {
				this.rilieviArray  = res.rilieviLayer;
				this.avvisiArray = res.avvisi;
				this.interventiConsentitiArray  = res.interventiConsentiti;
				this.existsLayers = this.rilieviArray != undefined && this.rilieviArray.length > 0;
				this.checkErrori();
				//this.changeEdit.emit(!this.existsLayers);
			}
		);

		this.componentInit.emit({getValidity: this.getValidity.bind(this)})
	}

	setTipoInterventoSelezionato(intervento: InterventoConsentito) {
		// TODO: NOTA changeedit serve ai componenti tipo mappa per nascondere
		//  i bottoni conferma e annulla. in questo non serve
		// if (this.idSchedaInterventoSelezionata != intervento.idSchedaIntervento) {
		// 	this.changeEdit.emit(true);
		// }
		// else {
		// 	this.changeEdit.emit(false);
		// }
		this.idSchedaInterventoSelezionata = intervento.idSchedaIntervento;
		this.hasVisioneVincoli = undefined;
		// const changes = {
		// 	from: {
		// 		idSchedaInterventoSelezionata: this.idSchedaInterventoSelezionata
		// 	} as Record<string, number>,
		// 	to: {
		// 		idSchedaInterventoSelezionata: intervento.idSchedaIntervento
		// 	} as Record<string, number>
		// };
		const changes : SimpleObjectChange = {
			idSchedaInterventoSelezionata: intervento.idSchedaIntervento,
			hasVisioneVincoli: this.hasVisioneVincoli
		};
		this.checkErrori();
		this.dataChanged.emit(changes);
	}

	setHasVisioneVincoli(value: boolean) {
		this.idSchedaInterventoSelezionata = undefined;
		this.hasVisioneVincoli = value;
		const changes : SimpleObjectChange = {
			idSchedaInterventoSelezionata: this.idSchedaInterventoSelezionata,
			hasVisioneVincoli: this.hasVisioneVincoli
		};

		this.checkErrori();
		this.dataChanged.emit(changes);
	}

	checkErrori() {
		if(this.existsLayers && this.interventiConsentitiArray.length > 0) {
			this.errore = (this.idSchedaInterventoSelezionata != undefined) ? undefined : "Occorre indicare un intervento";
		}
		else {
			this.errore = (this.hasVisioneVincoli==true) ? undefined : "Occorre indicare di aver preso visione";
		}
	}
	getValidity: () => boolean = () => {
		this.checkErrori();
		return (this.errore == undefined);
	}
}
