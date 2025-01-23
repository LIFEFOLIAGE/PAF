import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { ComponentRecordType, ComponentType } from "../ComponentInterface";
import { SimpleObjectChange } from "../../../shared/editor-scheda/editor-scheda.component";
import { TipologiaSuoloModel } from "./models/TipologiaSuoloModel";
//import { CampoDaGestire, TipologiaInterventoModel } from "./models/TipologiaInterventoModel";
import { TipologiaInterventoModel } from "./models/TipologiaInterventoModel";
import { BaseAuthService } from "../../../../services/auth.service";
import { ActivatedRoute } from "@angular/router";
import { IstanzaComponentInterface } from "../../../interfaces/istanza-component-interface";
import { ExportValue } from 'src/app/components/istanze/editor-istanza/editor-istanza.component';

@Component({
	selector: 'app-interventi-in-ambiti-non-forestali',
	templateUrl: './interventi-in-ambiti-non-forestali.component.html',
	styleUrls: ['./interventi-in-ambiti-non-forestali.component.css']
})
export class InterventiInAmbitiNonForestaliComponent implements ComponentType<SimpleObjectChange>, OnChanges, OnInit {

	@Input() dati: ComponentRecordType = {};
	@Input() context: any;
	@Input() resources: any;
	@Input() isReadOnly: boolean = true;
	@Input() componentOptions: any;
	@Input() dictionariesData?: Record<string, any>;

    @Output() changeEdit: EventEmitter<boolean> = new EventEmitter<boolean>();
    @Output() dataChanged: EventEmitter<SimpleObjectChange> = new EventEmitter<SimpleObjectChange>();
    @Output() componentInit: EventEmitter<IstanzaComponentInterface> = new EventEmitter<IstanzaComponentInterface>();
	@Output() export: EventEmitter<ExportValue> = new EventEmitter<ExportValue>();

	codIstanza?: string
	initialData: ComponentRecordType = {};
	datiEffettivi: ComponentRecordType = {};
	errori: Record<string, (string|undefined)> = {};

	tipologieSuolo: TipologiaSuoloModel[] = [];
	idxSuoli: Record<number, any> = {};
	idxInterventi: Record<number, any> = {};
	tipologieIntervento?: TipologiaInterventoModel[] = undefined;

	interventoSelezionato?: TipologiaInterventoModel = undefined;
	campoDaGestire?: string = undefined;
	valoreCampoDaGestire?: number;
	modifiche: SimpleObjectChange = {};
	loadProm?: Promise<any> = undefined;

	constructor(
		private route: ActivatedRoute,
		private authService: BaseAuthService,
	) {
	}
	ngOnInit(): void {
		this.componentInit.emit({ getValidity: this.getValidity.bind(this) });
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (this.loadProm == undefined) {
			this.loadProm = this.authService.authFetch('/interventi-ambiti-non-forestali').then(
				(res) => {
					console.log("INIZIO: aggiorno info");
					this.tipologieSuolo = res.usiDelSuolo;
					//this.tipologieIntervento = res.tipologieIntervento;
					this.idxSuoli = Object.fromEntries(
						res.usiDelSuolo.map(
							(v: any) => [v.id_uso_suolo, v]
						)
					);
					if (res.tipiIntervento != undefined) {
						this.idxInterventi = Object.fromEntries(
							res.tipiIntervento.map(
								(v: any) => [v.id_tipo_intervento, v]
							)
						);
						res.tipiIntervento.forEach(
							(s: any) => {
								const catPadre = this.idxSuoli[s.id_uso_suolo];
								if (catPadre != undefined) {
									let listPadre = catPadre.tipiInterventi;
									if (listPadre == undefined) {
										catPadre.tipiInterventi = listPadre = [];
									}
									listPadre.push(s);
								}
							}
						);
					}
					console.log("FINE: aggiorno info");
				}
			);
		}
		this.loadProm.then(
			() => {
				for (let propName in changes) {
					const currValue = changes[propName].currentValue;
					switch (propName) {
						case "context": {
							this.codIstanza = currValue.codIstanza;
						}; break;
						case "dati": {
							console.log("INIZIO: aggiorno dati");
							this.datiEffettivi = {...currValue};
							//this.tipologieIntervento = this.datiEffettivi['tipologieIntervento'];
							const tipoIntId = this.dati["idIntervento"];
							if (tipoIntId != undefined) {
								const tipoInt = this.idxInterventi[tipoIntId];
								if (tipoInt != undefined)  {
									this.onChangeTipologiaSuoloById(tipoInt.id_uso_suolo, true);
									this.onChangeIntervento(tipoInt, true);
								}
							}
							else {
								this.onChangeTipologiaSuolo(undefined, true);
							}
							const newValue = this.dati["valoreIntervento"];
							if (newValue != undefined) {
								this.onChangeValoreIntervento(newValue, true);
							}
							this.modifiche = {};
							this.errori = {};
							console.log("FINE: aggiorno dati");
						}; break;
					}
				}
				this.checkErrori();
				//this.componentInit.emit({getValidity: this.getValidity.bind(this)});
			}
		);
	}


	onChangeProperty(propertyName: string, newVal: any, silent: boolean = false) {
		if (!this.isReadOnly) {
			if (newVal == "") {
				newVal = undefined;
			}
			const currChange = Object.fromEntries([[propertyName, newVal]]);
			this.datiEffettivi = {...this.datiEffettivi, ...currChange};

			this.checkErrori();

			if (!silent) {
				if (this.dati[propertyName] == newVal) {
					delete this.modifiche[propertyName];
					this.modifiche = {
						...this.modifiche
					};
				}
				else {
					this.modifiche =  {
						...this.modifiche,
						...currChange
					};
				}
				this.dataChanged.emit(this.modifiche);
			}
		}
	}

	onChangeIntervento(newIntervento?: TipologiaInterventoModel, silent: boolean = false) {
		this.interventoSelezionato = newIntervento;
		let newIdIntervento = undefined;
		if (newIntervento != undefined) {
			newIdIntervento = newIntervento.id_tipo_intervento;
			if (this.campoDaGestire != newIntervento.parametro_richiesto) {
				this.campoDaGestire = newIntervento.parametro_richiesto;
				this.onChangeProperty("valoreIntervento", undefined, silent);
			}

		}
		this.onChangeProperty("idIntervento", newIdIntervento, silent);
	}
	onChangeTipologiaSuolo(newSuolo: any, silent: boolean = false) {
		//this.usoSelezionato = newSuolo
		if (newSuolo != undefined) {
			this.tipologieIntervento = newSuolo.tipiInterventi;
			this.onChangeProperty("idUsoSuolo", newSuolo.id_uso_suolo, silent);
		}
		else {
			this.tipologieIntervento = undefined;
			this.onChangeProperty("idUsoSuolo", undefined, silent);
		}
		this.interventoSelezionato = undefined;
		this.onChangeIntervento(undefined, silent);
	}
	onChangeTipologiaSuoloById(newIdSuolo: any, silent: boolean = false){
		let newSuolo = undefined;
		if (newIdSuolo != undefined) {
			newSuolo = this.idxSuoli[newIdSuolo]
		}
		this.onChangeTipologiaSuolo(newSuolo, silent);
	}


	onChangeDescrizioneIntervento(descrizioneIntervento: string) {
		this.modifiche = {
			...this.modifiche,
			descrizioneIntervento
		}
		this.dataChanged.emit(this.modifiche);
	}

	checkErrori() {
		delete this.errori['idUsoSuolo'];
		delete this.errori['interventoSelezionato'];
		delete this.errori['valoreIntervento'];
		delete this.errori['descrizioneIntervento'];
		
		if (this.datiEffettivi['idUsoSuolo'] == undefined) {
			this.errori['idUsoSuolo'] = "Campo richiesto";
		}
		else {
			if (this.interventoSelezionato == undefined) {
				this.errori['interventoSelezionato'] = "Occorre selezionare la tipologia di intervento";
			}
			else {
				if (this.campoDaGestire != undefined) {
					const value = this.datiEffettivi['valoreIntervento'];
					let errore = null;
					switch (this.campoDaGestire) {
						case "numero siti": {
							errore = this.checkNumeroIntero(value);
						}; break;
						case "estensione intervento (metri lineari) intero senza decimali": {
							errore = this.checkNumeroIntero(value);
						}; break;
						case "numero esemplari oggetto di esbosco": {
							errore = this.checkNumeroIntero(value);
						}; break;
						case "superficie (Ha) 2 decimali": {
							errore = this.checkNumero2Decimali(value);
						}; break;
					}
					if (errore) {
						this.errori['valoreIntervento'] = errore;
					}
				}
				if (this.datiEffettivi['descrizioneIntervento'] == undefined) {
					this.errori['descrizioneIntervento'] = "La descrizione è richiesta";
				}
			}
		}
	}


	checkNumeroIntero(value: number) {
		if (value) {
			const fixedVal = Number.parseFloat(value.toFixed(0));
			if (value == fixedVal) {
				return undefined;
			}
			else {
				return "È richiesto un valore intero";
			}
		}
		else {
			return "Valore richiesto";
		}
	}
	checkNumero2Decimali(value?: string) {
		if (value == undefined) {
			return "Valore richiesto";
		}
		else {
			const numberVal = Number(value); 
			if (isNaN(numberVal)) {
				return "È richiesto un valore numerico";
			}
			else {
				const fixedVal = Number.parseFloat(numberVal.toFixed(2));
				if (numberVal == fixedVal) {
					return undefined;
				}
				else {
					return "È richiesto un valore con al massimo 2 decimali";
				}
			}
		}
	}
	onChangeValoreIntervento(value: number, silent: boolean = false) {
		if (this.campoDaGestire != undefined) {
			this.onChangeProperty("valoreIntervento", value, silent);
		}
	}


    getValidity: (() => boolean) = () => {
		this.checkErrori();

		return Object.entries(this.errori).filter(([k, v]) => v != undefined).length == 0;

        // let errorMessages = []

        // if (this.modifiche['codUso'] == undefined) {
        //     errorMessages.push('Tipologia suolo non selezionata')
        // }
        // if (this.modifiche['codIntervento'] == undefined) {
        //     errorMessages.push('Tipologia intervento non selezionata')
        // }
        // let elems: NodeListOf<any> = this.elRef.nativeElement.querySelectorAll(".invalid-feedback.d-block")
        // elems.forEach((elem) => {
        //     errorMessages.push(elem.innerText)
        // })

        // return errorMessages.length == 0 ? true : errorMessages.join("\n")
    }
}
