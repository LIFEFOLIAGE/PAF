import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from "@angular/core";
import { BaseAuthService } from "src/app/services/auth.service";
import { HtmlService } from "src/app/services/html.service";


@Component({
	selector: 'app-dati-schedulazione-governance',
	templateUrl: './dati-schedulazione-governance.component.html'
})
export class DatiSchedulazioneGovernanceComponent implements OnInit, OnChanges {
	errori: any = {};
	tipiElab: any[] = [];
	idxTipiElab: any = {};
	tipoElaborazione: any;
	getTipi: Promise<any>;

	@Input() datiSchedulazione: any;
	@Input() isReadOnly: boolean = false;

	@Output() datiSchedulazioneChange: EventEmitter<any> = new EventEmitter<any>();

	constructor(
		public htmlService: HtmlService,
		private authService: BaseAuthService
	) {
		
		this.getTipi = this.authService.authFetch('/tipo-elaborazioni-governance').then(
			(res: any[]) => {
				this.tipiElab = res;
				this.idxTipiElab = Object.fromEntries(
					this.tipiElab.map(
						(v: any) => ([v.id_batch, v])
					)
				)
			}
		);
	}
	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case "datiSchedulazione": {
					if (currValue.idBatch != undefined) {
						this.getTipi.then(
							() => {
								this.tipoElaborazione = this.idxTipiElab[currValue.idBatch];
							}
						);
					}
				}
			}
		}
	}
	ngOnInit(): void {
	}

	onChangeTipoElaborazione(valore: any) {
		console.log(valore);
		this.tipoElaborazione = valore;
		this.onChangeDatiSchedulazione('idBatch', valore.id_batch);
	}
	onChangeDatiSchedulazione(campo: string, valore: any) {
		this.datiSchedulazione[campo] = valore;
		this.datiSchedulazioneChange.emit(this.datiSchedulazione);
	}
}