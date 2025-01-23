import { Component, EventEmitter, Input, Output } from "@angular/core";
import { HtmlService } from "src/app/services/html.service";


@Component({
	selector: 'app-dati-schedulazione-monitoraggio',
	templateUrl: './dati-schedulazione-monitoraggio.component.html'
})
export class DatiSchedulazioneMonitoraggioComponent {
	errori: any = {};

	@Input() datiSchedulazione: any;
	@Input() isReadOnly: boolean = false;

	@Output() datiSchedulazioneChange: EventEmitter<any> = new EventEmitter<any>();

	constructor(
		public htmlService: HtmlService
	) {
	}
	onChangeDatiSchedulazione(campo: string, valore: any) {
		this.datiSchedulazione[campo] = valore;
		this.datiSchedulazioneChange.emit(this.datiSchedulazione);
	}
}