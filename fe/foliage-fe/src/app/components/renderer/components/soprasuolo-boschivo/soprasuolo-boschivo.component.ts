import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { SimpleObjectChange } from 'src/app/components/shared/editor-scheda/editor-scheda.component';
import { BaseAuthService } from 'src/app/services/auth.service';
import { ComponentDataType, ComponentType } from '../ComponentInterface';
import { IstanzaComponentInterface } from "../../../interfaces/istanza-component-interface";
import { ExportValue } from 'src/app/components/istanze/editor-istanza/editor-istanza.component';

const labelEtaStd = "Età media : (anni)";

@Component({
	selector: 'app-soprasuolo-boschivo',
	templateUrl: './soprasuolo-boschivo.component.html'
})
export class SoprasuoloBoschivoComponent implements ComponentType<SimpleObjectChange>, OnInit, OnChanges {
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

	liste: Record<string, any[]> = {

	};
	formaGovFissa?: string;
	formeGoverno: any[] = [];
	struttureSoprasuolo: any[] = [];
	datiEffettivi: any = {};
	modifiche: SimpleObjectChange = {};
	errori: Record<string, (string|boolean)> = {};

	labelEta: string = labelEtaStd;
	showEta: boolean = true;
	showTrattamento: Record<string, boolean> = {};
	ready: boolean = false;

	//formaTrattamentoFissa?: {tipo: string, valore: number} = undefined;
	constructor(
		private authService: BaseAuthService
	) {
		this.authService.authFetch('/forme-di-governo').then(
			res => {
				if (this.formaGovFissa == undefined) {
					this.formeGoverno = [...res, {desc_gove: 'Misto'}];
				}
				else {
					this.formeGoverno = res;
				}
			}
		);
		this.authService.authFetch('/strutture-soprasuolo').then(
			res => {
				this.struttureSoprasuolo = res;
			}
		);
	}

	ngOnInit(): void {
		this.componentInit.emit({ getValidity: this.getValidity.bind(this) });
	}

	ngOnChanges(changes: SimpleChanges): void {
		let waits: Promise<any>[] = [];
		this.ready = false;
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			console.log(`Aggiornamento ${propName}`);
			switch (propName) {
				case "context": {
					if (currValue != undefined) {
						this.codIstanza = currValue.codIstanza;
						const p = this.authService.authFetch(`/istanze/${this.codIstanza}/info-soprasuolo`).then(
							res => {
								if (res.formaGovFissa != undefined) {
									this.formaGovFissa = res.formaGovFissa;
									this.formeGoverno = this.formeGoverno.filter(x => x.desc_gove == res.formaGovFissa);
								}
								this.liste = {
									Ceduo: res.listaCeduo,
									Fustaia: res.listaFustaia
								};
							}
						);
						waits.push(p);
					}
				}; break;
				case "dati": {
					this.datiEffettivi = {...currValue};
					this.modifiche = {};
					this.errori = {};
				}; break;
			}
		}
		Promise.all(waits).then(
			arr => {
				const formaDiGoverno = this.datiEffettivi["formaDiGoverno"];
				const superficie = this.datiEffettivi["superficie"];
				const eta = this.datiEffettivi["etaMediaDelSoprassuoloAnni"];
				this.checkShowTrattamento(formaDiGoverno);
				this.buildLabelEta(formaDiGoverno)
				this.checkSuperficie(superficie);
				this.checkEta(eta);
				this.checkErrors();
				this.ready = true;
				console.log('Pronto');
			}
		);
	}


	trackFormaGov(index: number, formaGov: any) {
        //console.log(hero);
        return formaGov == undefined ? undefined: formaGov.desc_gove;
    }

	checkShowTrattamento(newFormaGov?: string) {
		if (newFormaGov == undefined) {
			this.showTrattamento = {};
		}
		else {
			this.showTrattamento["Ceduo"] = newFormaGov != "Fustaia";
			this.showTrattamento["Fustaia"] = newFormaGov != "Ceduo";
		}
	}

	buildLabelEta(newFormaGov: string) {
		if (newFormaGov == "Ceduo") {
			this.labelEta = "Età media dei polloni : (anni)";
		}
		else {
			this.labelEta = labelEtaStd;
		}
	}

	checkSuperficie(supe: any) {
		delete this.errori["superficie"];
		if (supe == undefined) {
			this.errori["superficie"] = "La superficie deve essere indicata";
		}
		else {
			const numVal = Number(supe);
			if (Number.isNaN(numVal) || numVal < 0) {
				this.errori["superficie"] = "La superficie deve essere un numero positivo";
			}
			else {
				const fixedVal = Number.parseFloat(numVal.toFixed(2));
				if (numVal == fixedVal) {
					if (
						(
							(this.datiEffettivi.formaPropostaCeduo != undefined && this.datiEffettivi.formaPropostaCeduo.isFineTurno)
							|| (this.datiEffettivi.formaPropostaFustaia != undefined && this.datiEffettivi.formaPropostaFustaia.isFineTurno)
						)
						&& this.context.limitazioniSuperficieFineTurno
						&& numVal > this.context.limitazioniSuperficieFineTurno
					) {
						this.errori["superficie"] = "Superficie eccessiva per i vicoli sulle forme di trattamento a fine turno";
					}
				}
				else {
					this.errori["superficie"] = "La superficie può avere al massimo 2 decimali";
				}
			}
		}
	}
	checkEta(eta: any) {
		delete this.errori["eta"];
		this.showEta = !(['Disetaneo', 'Irregolare'].includes( this.struttureSoprasuolo.find(x => x.id_sspr == this.datiEffettivi.strutturaDelSoprasuolo)?.desc_sspr));
		
		if (this.showEta) {
			if (eta != undefined) {
				const numVal = Number(eta);
				if (Number.isNaN(numVal) || numVal < 0) {
					this.errori["eta"] = "L'età media deve essere un numero positivo";
				}
			} else {
				this.errori["eta"] = "L'età deve essere indicata";
			}
		}
	}

	checkErrors() {
		this.checkSuperficie(this.datiEffettivi['superficie']);
		this.checkEta(this.datiEffettivi['etaMediaDelSoprassuoloAnni']);

		delete this.errori["formaDiGoverno"];
		delete this.errori["strutturaDelSoprasuolo"];
		delete this.errori["tipoDiSoprasuolo"];

		
		delete this.errori["formaTrattamentoFustaia"];

		const formaDiGoverno = this.datiEffettivi.formaDiGoverno;
		if (formaDiGoverno == undefined) {
			this.errori["formaDiGoverno"] = "Forma di governo non puo essere vuoto";
		}
		else {
			delete this.errori["formaDiGoverno"];
		}

		const strutturaDelSoprasuolo = this.datiEffettivi.strutturaDelSoprasuolo;
		if (strutturaDelSoprasuolo == undefined) {
			this.errori["strutturaDelSoprasuolo"] = " Struttura del soprassuolo non puo essere vuoto";
		}
		else {
			delete this.errori["strutturaDelSoprasuolo"];
		}
		['Ceduo', 'Fustaia'].forEach(
			(governo) => {
				const refErr = "formaTrattamento" + governo;
				delete this.errori[refErr];
				if (
					this.showTrattamento[governo]
					&& 
					!(
						this.datiEffettivi['trattamentoPrecedente' + governo]
						&&
						this.datiEffettivi['trattamento' + governo]
					)
				) {
					this.errori[refErr] = 'La forma di trattamento proposta e quella applicata in precedenza sono obbligatorie';
				}
			}
		)

		// const tipoDiSoprasuolo = this.datiEffettivi.tipoDiSoprasuolo;
		// if (tipoDiSoprasuolo == undefined) {
		// 	this.errori["tipoDiSoprasuolo"] = "Non puo essere vuoto";
		// }
		// else {
		// 	delete this.errori["tipoDiSoprasuolo"];
		// }
	}
	onChangeProperty(propertyName: string, newVal: any) {
		if (!this.isReadOnly) {
			if (newVal == "") {
				newVal = undefined;
			}
			switch (propertyName) {
				case "superficie": {
					this.checkSuperficie(newVal);
				}; break;
				case "etaMediaDelSoprassuoloAnni": {
					this.checkEta(newVal);
				}; break;
				case "formaDiGoverno": {
					this.buildLabelEta(newVal);
					if (newVal == "") {
						newVal = undefined;
					}
					this.checkShowTrattamento(newVal);
				}; break;
				case "tipoDiSoprasuolo": {
					if (newVal == "") {
						newVal = undefined;
					}
				}; break;
				// case "strutturaDelSoprasuolo": {
				// 	this.showEta = !(['Disetaneo', 'Irregolare'].includes( this.struttureSoprasuolo.find(x => x.id_sspr == newVal)?.desc_sspr));
				// }; break;
			}
			const currChange = Object.fromEntries([[propertyName, newVal]]);
			this.datiEffettivi = {...this.datiEffettivi, ...currChange};

			this.checkErrors();

			this.modifiche =  {
				...this.modifiche,
				...currChange
			}
			this.dataChanged.emit(this.modifiche);
		}
	}


	onChangeTrattamentoAttuale(propertyName: string, newVal: any) {
		if (newVal.is_abilitato == true) {
			this.onChangeProperty(propertyName, newVal.id_forma_trattamento);
		}
	}

	getValidity: () => boolean = () => {
		let errorMessages: string[] = []
		let isValid = true

		this.checkErrors();

		// Object.values(this.errori).forEach(value => {
		// 	if (typeof value === 'string') {
		// 		errorMessages.push(value)
		// 	} else {
		// 		isValid = isValid && value
		// 	}
		// })
		// if (errorMessages.length > 0) {
		// 	//return errorMessages.join("\n")
		// 	return true;
		// }

		// return isValid
		return Object.keys(this.errori).length == 0;
	}
}
