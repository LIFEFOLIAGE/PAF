import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { ComponentDataType, ComponentType } from "../ComponentInterface";
import { SimpleObjectChange } from "../../../shared/editor-scheda/editor-scheda.component";
import { IstanzaComponentInterface } from "../../../interfaces/istanza-component-interface";
import { ExportValue } from 'src/app/components/istanze/editor-istanza/editor-istanza.component';

@Component({
	selector: 'app-riepilogo-finale',
	templateUrl: './riepilogo-finale.component.html',
	styleUrls: ['./riepilogo-finale.component.css']
})
export class RiepilogoFinaleComponent implements ComponentType<SimpleObjectChange>, OnInit, OnChanges {
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

	supportiFinanziari = [
		{
			key: 1,
			label: "Fondi del Piano di Sviluppo Rurale",
		}, {
			key: 2,
			label: "Fondi di progetti LIFE",
		}, {
			key: 3,
			label: "Fondi Nazionali",
		}, {
			key: 4,
			label: "Fondi Regionali",
		}, {
			key: 5,
			label: "Altri fondi Europei (eg. horizon 2020) ",
		}, {
			key: 6,
			label: "Altri finanziamenti",
		}, {
			key: 7,
			label: "Nessun finanziamento",
		},
	];
	get showDenominazioneFondo(): boolean {
		// Nel caso l’utente selezioni una alternativa tra i numeri n. 2, 3, 4, 5, 6
		// questi deve inoltre indicare in un campo stringa libero la “denominazione del fondo”.
		return [2, 3, 4, 5, 6].includes(this.datiEffettivi['supportoFinanziario']);
	}

	datiEffettivi: any = {};
	scostamento?: any;
	errori: Record<string, (string | boolean)> = {};

	set codTipoIstanz(value: string) {
		this.isAttuazionePiani = value === 'ATTUAZIONE_PIANI';
		//this.isAttuazionePiani = value === 'SOPRA_SOGLIA'; //TODO: delete me
	}

	modifiche: SimpleObjectChange = {};

	isAttuazionePiani = false;

	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case "context": {
					this.codTipoIstanz = currValue.codTipoIstanza;
				};break;
				case "dati": {
					// const DATI_DI_PROVA = { //TODO: delete me
					// 	superficieTotale: 55,
					// 	superficieUtile: 45,
					// 	superficieImproduttiva: 10,

					// 	nome_pgf: "Nome nome_pgf",
					// 	nome_comp: "Nome nome_comp",
					// 	oggetto: "Oggetto",

					// 	supportoFinanziario: 2,
					// 	denominazioneFondo: "descrzione denom fondo",
					// };

					this.datiEffettivi = {
						//...DATI_DI_PROVA,
						...currValue
					};

					this.modifiche = {};
				};break;
			}
		}
		
		this.scostamento = (
			(
				(
					(this.datiEffettivi.superficieGeometrica??0) - (this.context.superficieTotIntervento??0)
				)
				/ (this.context.superficieTotIntervento??0)
			)
			* 100
		).toFixed(2);
		//this.componentInit.emit({ getValidity: this.getValidity.bind(this) });
	}

	ngOnInit(): void {
		this.componentInit.emit({ getValidity: this.getValidity.bind(this) });
	}

	onChangeProperty(propertyName: string, newVal: any) {
		this.errori = {};

		const currChange = Object.fromEntries([[propertyName, newVal]]);
		this.datiEffettivi = { ...this.datiEffettivi, ...currChange };

		this.modifiche = {
			...this.datiEffettivi,
			...this.modifiche,
			...currChange
		};

		if (!this.isReadOnly) {
			if (this.isAttuazionePiani) {
				const nome_pgf = this.datiEffettivi['nome_pgf'] ?? "";
				if (nome_pgf.length == 0) {
					this.errori["nome_pgf"] = "Il nome del piano di gestione forestale non puo essere vuoto";
				}
				else {
					delete this.errori["nome_pgf"];
				}

				const nome_comp = this.datiEffettivi['nome_comp'] ?? "";
				if (nome_comp.length == 0) {
					this.errori["nome_comp"] = "Il nome della compresa forestale non puo essere vuoto";
				}
				else {
					delete this.errori["nome_comp"];
				}

				const oggetto = this.datiEffettivi['oggetto'] ?? "";
				if (oggetto.length == 0) {
					this.errori["oggetto"] = "L'oggetto non puo essere vuoto";
				}
				else {
					delete this.errori["oggetto"];
				}
			}

			const supportoFinanziario = this.datiEffettivi['supportoFinanziario'];
			if (supportoFinanziario == undefined) {
				this.errori["supportoFinanziario"] = "Devi selezionare un tipo di finanziamento";

			}
			else {
				delete this.errori["supportoFinanziario"];
			}

			if (this.showDenominazioneFondo) {
				const denominazioneFondo = this.datiEffettivi['denominazioneFondo'];
				if (denominazioneFondo == undefined || denominazioneFondo.length == 0) {
					this.errori["denominazioneFondo"] = "Devi inserire la denominazione del fondo";
				}
				else {
					delete this.errori["denominazioneFondo"];
				}
			}
			else {
				delete this.errori["denominazioneFondo"];
				delete this.datiEffettivi["denominazioneFondo"];
				delete this.modifiche["denominazioneFondo"];
			}
		}

		this.dataChanged.emit(this.modifiche);
	}

	onChangeSupportoFinanziarioById($event: any) {
		this.onChangeProperty('supportoFinanziario', $event);

		this.onChangeProperty('denominazioneFondo', "");
	}

	onChangeDenominazioneFondo($event: any) {
		this.onChangeProperty('denominazioneFondo', $event);
	}

	getValidity: () => boolean = () => {
		return Object.keys(this.errori).length == 0;
	};
}
