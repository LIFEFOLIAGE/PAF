import { Component, OnInit, OnChanges, SimpleChanges, Input, Output, EventEmitter } from '@angular/core';
import { BaseAuthService } from '../services/auth.service';

export type Ambito = {
	tipo: string,
	value: number
}


const ambitiBase = [
	{
		idAmbito: 0,
		descAmbito: "Comune"
	},
	{
		idAmbito: 1,
		descAmbito: "Provincia"
	}
];
const ambitiConRegione = [
	{
		idAmbito: 2,
		descAmbito: "Regione"
	},
	...ambitiBase
];

const ambiti: Record<string, object> = {
	COMUNE: {
		idAmbito: 0,
		descAmbito: "Comune"
	},
	PROVINCIA: {
		idAmbito: 1,
		descAmbito: "Provincia"
	},
	REGIONE: {
		idAmbito: 2,
		descAmbito: "Regione"
	}
}

@Component({
	selector: 'app-selettore-ambito',
	templateUrl: './selettore-ambito.component.html',
	styleUrls: ['./selettore-ambito.component.css']
})
export class SelettoreAmbitoComponent implements OnInit, OnChanges {
	@Input() isRequired: boolean = false;
	//@Input() showRegione: boolean = false;
	@Input() ambitiTerritoriali: string[] = ['COMUNE', 'PROVINCIA', 'REGIONE'];
	@Input() isReadOnly: boolean = false;
	@Input() idAmbito?: number;
	@Input() emitInitialValue?: boolean = false;


	@Output() readonly changeAmbito = new EventEmitter<Ambito>();

	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case "showRegione": {
					if (currValue) {
						this.elencoAmbitiTerritoriali = ambitiConRegione;
					}
					else {
						this.elencoAmbitiTerritoriali = ambitiBase;
					}
				}; break;
				case "ambitiTerritoriali": {
					this.elencoAmbitiTerritoriali = currValue.map((a: string) => ambiti[a]).filter((x: object) => x != undefined);
					if (this.elencoAmbitiTerritoriali.length == 0) {
						this.elencoAmbitiTerritoriali = Object.values(ambiti);
					}
					if (this.elencoAmbitiTerritoriali.findIndex((x: any) => (x.descAmbito == this.ambitoTerritoriale)) == -1) {
						this.ambitoTerritoriale = "";
					}
					this.elencoAmbitiTerritoriali.sort((a: any, b: any) => a.idAmbito - b.idAmbito);
				}; break;
				case "idAmbito": {
					if (this.idAmbito) {
						if (currValue > 0) {
							this.authService.authFetch(
								`/info-ente/${currValue}`
							).
							then(
								(res: any) => {
									if (res.ambitoTerritoriale == "REGIONE") {
										this.ambitoTerritoriale = "Regione";
									}
									else {
										if (res.ambitoTerritoriale == "PROVINCIA") {
											this.ambitoTerritoriale = "Provincia";
											this.provincia = res.provincia;
										}
										else {
											if (res.ambitoTerritoriale == "COMUNE") {
												this.ambitoTerritoriale = "Comune";
												this.provincia = res.provincia;
												this.comune = res.comune;
											}
											else {
												throw new Error("Ambito non valido");
											}
										}
									}
									if (this.emitInitialValue) {
										const tipo = this.ambitoTerritoriale;
										let value: number = -1;
										switch (tipo) {
											case "Comune": {
												value = this.comune;
											}; break;
											case "Provincia": {
												value = this.provincia;
											}; break;
											case "Regione": {
												value = this.infoRegione.id;
											}; break;
										}
										const change: Ambito = {tipo, value};
										this.changeAmbito.emit(change);
									}
								}
							);
						}
					}
				}
			}
		}
	}
	onChange() {
		const tipo = this.ambitoTerritoriale;

		let value: number = -1;
		switch (tipo) {
			case "Comune": {
				value = this.comune;
			}; break;
			case "Provincia": {
				value = this.provincia;
			}; break;
			case "Regione": {
				value = this.infoRegione.id;
			}; break;
		}

		const change : Ambito = {tipo, value};
		//console.log({change});
		this.changeAmbito.emit(change);
	}
	infoRegione: any = {};
	elencoProvincie: any[] = [];
	elencoComuni: any[] = [];
	elencoAmbitiTerritoriali: any[] = ambitiConRegione;

	ambitoTerritoriale: string = "";
	_provincia: number = -1;
	get provincia(): number {
		return this._provincia;
	}
	set provincia(value: number) {
		// if (this._provincia != value) {
		// 	this.comune = '';
		// }
		this._provincia = value;
		if (value == -1) {
			this.elencoComuni = [];
		}
		else {
			this.authService.authFetch(
				`/comuni/${value}`
			).
			then(
				(results: any) => {
					this.elencoComuni = results;
				}
			);
		}
	}

	comune: number = -1;

	constructor(
		private authService: BaseAuthService
	) {
	}

	ngOnInit(): void {
		this.authService.authFetch(
			'/regione-host'
			).
			then(
				(res: any) => {
					this.infoRegione = res;
				}
			);
		this.authService.authFetch(
				'/provincie-host'
			).
			then(
				(results: any) => {
					this.elencoProvincie = results;
				}
			);
	}
}
