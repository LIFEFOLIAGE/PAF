import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { BaseAuthService } from "../../../../services/auth.service";
import { ComponentDataType, ComponentType } from "../ComponentInterface";
import { SimpleObjectChange } from "../../../shared/editor-scheda/editor-scheda.component";
import { IstanzaComponentInterface } from "../../../interfaces/istanza-component-interface";
import { SessionManagerService } from "../../../../services/session-manager.service";
import { ExportValue } from 'src/app/components/istanze/editor-istanza/editor-istanza.component';

@Component({
	selector: 'app-stazione-forestale',
	templateUrl: './stazione-forestale.component.html',
	styleUrls: ['./stazione-forestale.component.css']
})
export class StazioneForestaleComponent implements ComponentType<SimpleObjectChange>, OnInit {
	datiEffettivi: any = {};
	//@Input() dati: ComponentDataType = {};
	@Input() set dati(value: ComponentDataType) {
		this.datiEffettivi = value;
	}
	@Input() isReadOnly: boolean = false;
	@Input() context: any;
	@Input() resources: any;
	@Input() componentOptions: any;
	@Input() dictionariesData?: Record<string, any>;

	@Output() changeEdit: EventEmitter<boolean> = new EventEmitter<boolean>();
	@Output() dataChanged: EventEmitter<SimpleObjectChange> = new EventEmitter<SimpleObjectChange>();
	@Output() componentInit: EventEmitter<IstanzaComponentInterface> = new EventEmitter<IstanzaComponentInterface>();
	@Output() export: EventEmitter<ExportValue> = new EventEmitter<ExportValue>();

	codIstanza!: string;
	datiSezioneForestale: any;

	constructor(
	) {
	}

	ngOnInit(): void {
		this.changeEdit.emit(true);
	}

	// ngOnChanges(changes: SimpleChanges): void {
	// 	for (let propName in changes) {
	// 		const currValue = changes[propName].currentValue;
	// 		switch (propName) {
	// 			case "context": {
	// 				this.codIstanza = currValue.codIstanza;
	// 				if (this.codIstanza) {
	// 					this.sessionManager.profileFetch(
	// 						`/istanze/${this.codIstanza}/stazione-forestale`
	// 					).then(
	// 						(results: any) => {
	// 							this.datiSezioneForestale = results
	// 						},
	// 						(err) => {
	// 							console.log("errore recupero stazione forestale istanza", err)
	// 						}
	// 					);
	// 				}
	// 			}; break;
	// 		}
	// 	}
	// }
}
