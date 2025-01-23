import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { IstanzaComponentInterface } from '../../../../components/interfaces/istanza-component-interface';
import { SimpleObjectChange } from '../../../../components/shared/editor-scheda/editor-scheda.component';
import { ComponentDataType, ComponentType } from '../ComponentInterface';
import { ExportValue } from '../../../../components/istanze/editor-istanza/editor-istanza.component';
//import { getSpeciForestali } from 'src/app/components/istanze/editor-istanza/schede/conf';
import { getSpeciForestali } from '../../../../components/istanze/editor-istanza/conf/schede';
import { BaseAuthService } from '../../../../services/auth.service';


@Component({
	selector: 'app-assortimenti-ritraibili',
	templateUrl: './assortimenti-ritraibili.component.html'
})
export class AssortimentiRitraibili implements ComponentType<SimpleObjectChange>, OnInit, OnChanges {
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

	codIstanza!: string;
	tipoDiSoprasuolo?: string;
	arrSpeci: any[] = ['0', '1'];

	datiEffettivi: any = {};
	modifiche: SimpleObjectChange = {};


	idxTipoSopr: Record<(string|number), any> = {};
	idxSpeci: Record<(string|number), any> = {};
	speci: any[] = [];
	errori: Record<string, (string|boolean)> = {};
	totPercElenco: Record<string, number> = Object.fromEntries(
		this.arrSpeci.map(s => [s, 0])
	);
	totElenchi: number = 0;

	percRichieste = [
		{
			destinazione: "A fini energetici",
			assortimento: "Legna da ardere e carbone",
			chiave: "LEGNA"
		},
		{
			destinazione: "A fini energetici",
			assortimento: "Cippato per combustibile (biomasse)",
			chiave: "COMBUSTIBILE"
		},
		{
			destinazione: "Per lavorazione industriale",
			assortimento: "Tronchi (travame, trancia da sega, trancia da sfoglia)",
			chiave: "TRONCHI"
		},
		{
			destinazione: "Per lavorazione industriale",
			assortimento: "Cippato per cellulosa",
			chiave: "CELLULOSA"
		},
		{
			destinazione: "Per lavorazione industriale",
			assortimento: "Altro",
			chiave: "ALTRO"
		}
	];

	constructor(
		private authService: BaseAuthService
	) {
	}
	ngOnInit(): void {
		this.initNomiSpeci();

		this.componentInit.emit({getValidity: this.getValidity.bind(this)});
	}

	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case "context": {
					if (currValue != undefined) {
						this.codIstanza = currValue.codIstanza;
						this.tipoDiSoprasuolo = currValue.tipoDiSoprasuolo??this.tipoDiSoprasuolo;
					}
				}; break;
				case "dati": {
					this.datiEffettivi = {...currValue};
					this.errori = {};
					this.modifiche = {};
					this.tipoDiSoprasuolo = currValue.tipoDiSoprasuolo??this.tipoDiSoprasuolo;
					this.initNomiSpeci();
				}; break;
				case "resources": {
					getSpeciForestali(this, currValue).then(
						() => {
							const speciForestali = currValue.speciForestali;
							if (speciForestali) {
								// this.macrocategorie = speciForestali.macrocategorie;
								// this.idxMacrocategorie = speciForestali.idxMacrocategorie;
								// this.idxCategorie = speciForestali.idxCategorie;
								this.speci = speciForestali.speci;
								this.idxSpeci = speciForestali.idxSpeci;
								this.idxTipoSopr = speciForestali.idxTipoSopr;
							}
						}
					);
				}
			}
		}
		this.checkErrori();
	}
	initNomiSpeci() {
		[0, 1].forEach(
			i => {
				const s = this.datiEffettivi[`specie${i}`];
				if (s != undefined) {
					const specie = this.idxSpeci[s];
					if (specie) {
						//this.onChangePropertyAndNotNotify('nomeSpecie' + i, specie.nome_specie);
						this.onChangeProperty('nomeSpecie' + i, specie.nome_specie);
					}
				}
			}
		);
	}
	onChangePropertyAndNotNotify(propertyName: string, newVal: any) {
		if (newVal == "") {
			newVal = undefined;
		}
		const currChange = Object.fromEntries([[propertyName, newVal]]);
		if (newVal == undefined) {
			delete this.datiEffettivi[propertyName];
			this.datiEffettivi = {...this.datiEffettivi};

		}
		else {
			this.datiEffettivi = {...this.datiEffettivi, ...currChange};
		}
		this.checkErrori();
		return currChange;
	}
	onChangeProperty(propertyName: string, newVal: any) {
		const currChange = this.onChangePropertyAndNotNotify(propertyName, newVal);

		this.modifiche =  {
			...this.modifiche,
			...currChange
		}
		this.dataChanged.emit(this.modifiche);
	}
	onChangeSpecie(numElenco: string , newVal: number) {
		this.onChangeProperty('specie' + numElenco, newVal);
		let nomeSpecie: string = "";
		if (newVal != undefined) {
			const specie = this.idxSpeci[newVal];
			if (specie) {
				nomeSpecie = specie.nome_specie;
			}
		}
		this.onChangeProperty('nomeSpecie' + numElenco, nomeSpecie);
	}

	checkErrori() {
		let totCoperSpeci = 0;

		const arrNomi = ['specie', 'percCopertura'];
		this.arrSpeci.forEach(
			idxSpecie => {
				const keyTotali = `totali${idxSpecie}`;
				delete this.errori[keyTotali];
				if (idxSpecie == '0' || this.isSecondaVisible()) {
					arrNomi.forEach(
						nome => {
							const key = nome + idxSpecie;
							const val = this.datiEffettivi[key];
							if (val) {
								delete this.errori[key];
								switch (nome) {
									case 'percCopertura': {
										const numVal = Number(val);
										if (Number.isNaN(numVal)) {
											this.errori[key] = "Deve essere un numero positivo non superiore a 100";
										}
										else {
											if (numVal < 0 || numVal > 100) {
												this.errori[key] = "Deve essere un numero positivo non superiore a 100";
											}
											totCoperSpeci += numVal;								
											if (totCoperSpeci >= 80 && idxSpecie == '0') {
												arrNomi.forEach(
													nome2 => {
														delete this.errori[nome2 + '1'];
														delete this.errori['totali1'];
													}
												);
											}
											if (totCoperSpeci > 100 && idxSpecie == '1') {
												this.errori[key] = `La somma delle percentuali di copertura dell'intervento tra prima e seconda specie forestale (${totCoperSpeci}) non può superare 100`;
											}
										}
									}; break;
									case 'specie': {
										if (idxSpecie == '1' && val == this.datiEffettivi.specie0) {
											this.errori[key] = `Prima e seconda specie forestale devono essere diverse`;
										}
									}; break;
								}
							}
							else {
								this.errori[key] = "Valore richiesto";
							}
						}
					);
					let totDestinazioni = 0;
					this.percRichieste.forEach(
						perc => {
							['PercAutoc', 'PercVendita'].forEach(
								dest => {
									const key = perc.chiave + dest + idxSpecie;
									const val = this.datiEffettivi[key];
									delete this.errori[key];
									
									const numVal = Number.parseFloat(val??0);
									if (Number.isNaN(numVal)) {
										this.errori[key] = "Deve essere un numero positivo non superiore a 100";
									}
									else {
										if (numVal < 0 || numVal > 100) {
											this.errori[key] = "Deve essere un numero positivo non superiore a 100";
										}
										totDestinazioni += numVal; 
									}
								}
							)
						}
					);
					if (totDestinazioni != 100) {
						this.errori[keyTotali] = `La somma delle percentuali tra i vari assortimenti (${totDestinazioni}) deve essere 100`;
					}
				}
			}
		);
	}
	// checkErroriPercInterventi() {
	// 	let totCoperturaSpecie = 0;
	// 	this.arrSpeci.forEach(
	// 		specie => {
	// 			const currSpecie = this.datiEffettivi[`specie${specie}`];
	// 			if (currSpecie) {
	// 				this.percRichieste.forEach(
	// 					(percRich) => {
	// 						['PercAutoc', 'PercVendita']
	// 					}
	// 				);
	// 				totCoperturaSpecie += this.datiEffettivi[`percCopertura${specie}`];
	// 				delete this.errori[`specie${specie}`];
	// 			}
	// 			else {
	// 				this.errori[`specie${specie}`] = 'Campo Richiesto'
	// 			}
	// 		}
	// 	)
	// }
	// checkErroriPercIntervento(propertyName: string) {
	// 	const newValue: string = this.datiEffettivi[propertyName];
	// 	if (newValue != undefined || newValue != "") {
	// 		const numVal = Number(newValue);
	// 		if (Number.isNaN(numVal) || numVal < 0 || numVal > 100) {
	// 			this.errori[propertyName] = "Deve essere un numero positivo non superiore a 100";
	// 		}
	// 		else {
	// 			this.onChangeProperty(propertyName, numVal);
	// 		}
	// 	}
	// }

	onChangePercIntervento(elenco: string, newValue: string) {
		const propertyName = `percCopertura${elenco}`;
		delete this.errori[propertyName];
		if (newValue != undefined || newValue != "") {
			const numVal = Number(newValue);
			if (Number.isNaN(numVal) || numVal < 0 || numVal > 100) {
				this.errori[propertyName] = "Deve essere un numero positivo non superiore a 100";
			}
			else {
				this.onChangeProperty(propertyName, numVal);
			}
		}
		this.totElenchi = this.arrSpeci.reduce(
			(pv, cv) => (
				pv + this.datiEffettivi[`percCopertura${cv}`]
			),
			0
		);
		delete this.errori['totali'];
		if (this.totElenchi > 100) {
			this.errori['totali'] = `La somma delle percentuali di copertura dell'intervento tra prima e seconda specie forestale (${this.totElenchi}) non può superare 100`;
		}
	}
	onChangePerc(chiave: string, colonna: string, elenco: string, newValue: string) {
		const propertyName = `${chiave}${colonna}${elenco}`;
		delete this.errori[propertyName];
		if (newValue != undefined || newValue != "") {
			const numVal = Number(newValue);
			if (Number.isNaN(numVal) || numVal < 0 || numVal > 100) {
				this.errori[propertyName] = "Deve essere un numero positivo non superiore a 100";
			}
			else {
				this.onChangeProperty(propertyName, numVal);
			}
		}
		this.totPercElenco[elenco] = this.percRichieste.reduce(
			(pv, cv) => (
				pv
				+ ['PercVendita', 'PercAutoc'].reduce(
					(pvv, cvv) => (
						pvv
						+ ((this.datiEffettivi[cv.chiave + cvv  + elenco] > 0) ? this.datiEffettivi[cv.chiave + cvv  + elenco] : 0)
					),
					0
				)
			),
			0
		);

		delete this.errori[`totali${elenco}`];
		if (this.totPercElenco[elenco] != 100) {
			this.errori[`totali${elenco}`] = `La somma delle percentuali tra i vari assortimenti (${this.totPercElenco[elenco]}) deve essere 100`;
		}
	};


	isSecondaVisible() {
		const prima = this.datiEffettivi['percCopertura0'];
		if (prima != undefined) {
			const percCopertura = Number(prima);
			if (percCopertura < 80) {
				return true;
			}
		}
		return false;
	}
	showErrPercentuali(numElenco: string) {
		return this.totPercElenco[numElenco] != 100;
	}
	getListaSpeci() {
		if (this.tipoDiSoprasuolo) {
			return this.idxTipoSopr[this.tipoDiSoprasuolo];
		}
		else {
			return this.speci;
		}
	}

    getValidity: () => boolean = () => {
		return Object.entries(this.errori).filter(([k, v]) => v != undefined).length == 0;
    }
}
