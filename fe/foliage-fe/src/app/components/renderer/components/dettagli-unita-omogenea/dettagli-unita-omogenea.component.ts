import { Component, EventEmitter, Input, OnChanges, OnInit, Output, Directive, SimpleChanges, ElementRef } from '@angular/core';
import { ComponentDataType, ComponentRecordType, ComponentType } from "../ComponentInterface";
import { SimpleObjectChange } from "../../../shared/editor-scheda/editor-scheda.component";
import { IstanzaComponentInterface } from "../../../interfaces/istanza-component-interface";
import { ExportValue } from 'src/app/components/istanze/editor-istanza/editor-istanza.component';


@Component({
	selector: 'app-dettagli-unita-omogenea',
	templateUrl: './dettagli-unita-omogenea.component.html',
	styleUrls: ['./dettagli-unita-omogenea.component.css']
})
export class DettagliUnitaOmogeneaComponent implements ComponentType<SimpleObjectChange>, OnInit, OnChanges {
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
	datiEffettivi: any = {};
	errori: Record<string, string> = {};
	modifiche: SimpleObjectChange = {};
	//superficiEttari = ["areeImproduttive"];
	hideSoprasuolo = true;
	datiSoprasuolo: any = {};
	categorie: any[] = [];
	categoria: any;
	sottocategorie: any[] = [];
	private _soprasuoloBoschivoInterface?: IstanzaComponentInterface;

	// readonly soprasuoloConfig = { embeddedInMapPopup: true };

	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case "context": {
					this.codIstanza = currValue?.codIstanza;
				};break;
				case "dati": {
					currValue['superficie'] = (currValue['superficie'] == undefined)
						? 0
						: Number.parseFloat(currValue['superficie']).toFixed(2);

					this.datiEffettivi = { ...currValue };
					this.datiSoprasuolo = { ...currValue };
					this.evalSuperfici();
					this.errori = {};

					this.categoria = (this.categorie) ? this.categorie.find(c => c.id_categoria == currValue.idCategoria) : [];
					this.sottocategorie = (this.categoria) ? this.categoria.subCats : [];
					this.checkErrors();
					// this.modifiche = {};
				}; break;
				case "resources": {
					currValue.categorie.then(
						(categorie: any) => {
							this.categorie = categorie;
							this.categoria = (this.categorie) ? this.categorie.find(c => c.id_categoria == this.datiEffettivi["idCategoria"]) : [];
							this.sottocategorie = (this.categoria) ? this.categoria.subCats : [];
							this.checkErrors();
						}
					)
				}; break;
			}
		}
	}

	ngOnInit(): void {
		console.log("init");
	}
	evalSuperfici() {
		// this.superficiEttari.forEach(
		// 	propertyName => {
		// 		if (this.datiEffettivi[propertyName] != undefined) {
		// 			this.onChangeProperty(`${propertyName}Ettari`, this.datiEffettivi[propertyName]/10000);
		// 		}
		// 	}
		// );
		const supeGeom = this.datiEffettivi['superficie'];
		const supeAreeImp = Number.parseFloat(this.datiEffettivi['areeImproduttive']??0);
		const supeChiareRadure = Number.parseFloat(this.datiEffettivi['chiarieRadure']??0);
		const supeAreeInt = Number.parseFloat(this.datiEffettivi['areeInterdette']??0);
		const totSupeImpr = supeAreeImp + supeChiareRadure + supeAreeInt;

		delete this.errori['areeImproduttive'];
		delete this.errori['chiarieRadure'];
		delete this.errori['areeInterdette'];

		if (supeAreeImp > supeGeom) {
			this.errori['areeImproduttive'] = "Valore superiore alla superficie dell'intervento";
		}
		if (supeChiareRadure > supeGeom) {
			this.errori['chiarieRadure'] = "Valore superiore alla superficie dell'intervento";
		}
		if (supeAreeInt > supeGeom) {
			this.errori['areeInterdette'] = "Valore superiore alla superficie dell'intervento";
		}
		if (!(this.errori['areeImproduttive'] || this.errori['chiarieRadure'] || this.errori['areeInterdette'])) {
			if (totSupeImpr > supeGeom) {
				const mess = "La superficie di tutte le aree improduttive supera quella dell'intervento";
				if (supeAreeImp) {
					this.errori['areeImproduttive'] = mess;
				}
				if (supeChiareRadure) {
					this.errori['chiarieRadure'] = mess;
				}
				if (supeAreeInt) {
					this.errori['areeInterdette'] = mess;
				}
			}
		}
		this.datiEffettivi['totaleAreeImproduttive'] = totSupeImpr;

		let superficieUtile = this.datiEffettivi['superficie'] - this.datiEffettivi['totaleAreeImproduttive'];
		superficieUtile = Number.parseFloat(superficieUtile.toFixed(2));

		this.datiEffettivi['superficieUtile'] = superficieUtile;
		this.modifiche['superficieUtile'] = superficieUtile;
	}

	onChangeInputSuperficie(propertyName: string, newVal?: number) {
		delete this.errori[propertyName];
		if (newVal != undefined) {
			this.onChangeSuperficie(propertyName, newVal);
		}
		else {
			this.errori[propertyName] = "Inserire un numero";
		}
	}

	onChangeSuperficie(propertyName: string, newVal?: number) {
		this.onChangeProperty(propertyName, newVal);
	}



	onChangeProperty(propertyName: string, newVal: any) {
		if (!this.isReadOnly) {
			const currChange = Object.fromEntries([[propertyName, newVal]]);
			this.datiEffettivi = { ...this.datiEffettivi, ...currChange };
			//this.checkDati();

			this.modifiche = {
				...this.modifiche,
				...currChange
			};

			this.dataChanged.emit(this.modifiche);
		}
		this.checkErrors();
	}


	checkErrors() {
		this.evalSuperfici();
		const nomeUO = this.datiEffettivi.nomeUO;
		if (nomeUO == undefined || nomeUO.length == 0) {
			this.errori["nomeUO"] = "Il nome della unità omogenea non puo essere vuoto";
		}
		else {
			delete this.errori["nomeUO"];
		}

		if (this.categoria == undefined) {
			this.errori["idCategoria"] = "Valore richiesto";
		}
		else {
			delete this.errori["idCategoria"];
		}
	}
	onChangeEditSoprasuoloBoschivo($event: boolean) {
		this.changeEdit.emit($event);
	}

	onDataChangeSoprasuoloBoschivo($event: SimpleObjectChange) {
		// for (let propName in $event) {
		// 	// const currValue = $event[propName].currentValue; //TODO currentValue è undefined
		// 	switch (propName) {
		// 		case "dati": {
		// 			this.datiEffettivi = { ...$event };
		// 			// this.errori = {};
		// 			// this.modifiche = {};
		// 		}
		// 			;
		// 			break;
		// 		case "superficie": {
		// 			this.datiEffettivi = { ...$event };
		// 			// this.errori = {};
		// 			// this.modifiche = {};
		// 		}
		// 			;
		// 			break;
		// 	}
		// }

		this.datiEffettivi = { ...this.datiEffettivi, ...$event};
		this.modifiche = { ...this.modifiche, ...$event }

		// this.modifiche = {
		// 	...this.datiEffettivi,
		// 	...this.modifiche,
		// 	...$event
		// };

		this.dataChanged.emit(this.modifiche);
		//this.dataChanged.emit(this.datiEffettivi);
	}

	onComponentInitSoprasuoloBoschivo($event: IstanzaComponentInterface) {
		this._soprasuoloBoschivoInterface = $event;
		this.componentInit.emit({ getValidity: this.getValidity.bind(this) });
	}

	getValidity: () => (boolean) = () => {
		this.checkErrors();

		const soprasuoloBoschivoValid = this._soprasuoloBoschivoInterface?.getValidity() ?? false;

		// const dettagliValidi = this.errori["sommaError"] == undefined;
		// isValid = isValid && dettagliValidi;

		const dettagliValidi = Object.keys(this.errori).length == 0;
		const isValid = soprasuoloBoschivoValid && dettagliValidi;

		// return true;
		return isValid;
	};

	onChangeCategoria(categoria: any) {
		this.categoria = categoria;
		this.sottocategorie = categoria.subCats;
		this.onChangeProperty("idCategoria", categoria.id_categoria);
		this.onChangeProperty("idSottocategoria", undefined);
	}
}
