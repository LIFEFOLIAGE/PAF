import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from "@angular/core";
import { DateTimeFormatter, LocalDate, LocalDateTime, Period } from "@js-joda/core";
import { Locale } from "@js-joda/locale_it";
import { BaseAuthService } from "src/app/services/auth.service";
import { HtmlService } from "src/app/services/html.service";
import { PeriodDuration } from "src/app/utils/date-ext";


const tipoElab: any = {
	"data_partenza": "2024-01-01T00:00:00.000+00:00",
	"intervallo_frequenza": "0001-00-00/00:00:00.000",
	"intervallo_offset": "0000-08-00/00:00:00.000"
}
@Component({
	selector: 'app-dati-schedulazione-monitoraggio',
	templateUrl: './dati-schedulazione-monitoraggio.component.html'
})
export class DatiSchedulazioneMonitoraggioComponent implements OnChanges {
	errori: any = {};
	tipoElaborazione = tipoElab;
	dataAvvioRichiesta?: LocalDateTime;
	periodoElaborazione: any;

	@Input() datiSchedulazione: any;
	@Input() isReadOnly: boolean = false;

	@Output() datiSchedulazioneChange: EventEmitter<any> = new EventEmitter<any>();
	
	elaborazioni: any[] = [];
	idxElaborazioni: any = {};
	promElaborazione: Promise<void> = Promise.resolve();
	constructor(
		private authService: BaseAuthService,
		public htmlService: HtmlService
	) {
	}
	
	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case "datiSchedulazione": {
					this.onChangeDataAvvio(this.htmlService.toLocalDateTime(currValue.dataAvvioRichiesta));


					const dataRife = currValue.dataRife;
					
					this.promElaborazione.then(
						() => {
							this.onChangePeriodoElaborazione(this.idxElaborazioni[dataRife]);
						}
					);
				}
			}
		}
	}
	lastEval = 0;
	willEvalElaborazioni() {
		const currEval = ++this.lastEval;
		this.promElaborazione = new Promise(
			(resolve, reject) => {
				setTimeout(
					() => {
						if (currEval == this.lastEval) {
							this.evalElaborazioni();
						}
						resolve();
					},
					500
				);
			}
		);
	}
	evalElaborazioni() {
		if (this.dataAvvioRichiesta != undefined && this.tipoElaborazione != undefined) {

			const elab = this.tipoElaborazione;

			let firstDate: LocalDateTime = LocalDateTime.parse(elab.data_partenza, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
			// const pdOffset: (PeriodDuration|undefined) = (elab.intervallo_offset) ? PeriodDuration.parse(elab.intervallo_offset) : undefined;
			// if (pdOffset != undefined) {
			// 	firstDate = firstDate.plus(pdOffset.period).plus(pdOffset.duration);
			// }
			//const pdFreq: PeriodDuration = PeriodDuration.parse(elab.intervallo_frequenza);
			const theDay: LocalDateTime = this.dataAvvioRichiesta;
			const theDaySMonth: number = theDay.monthValue();
			const theDaySYear: number = theDay.year();
			const endYear: number = theDaySYear - ((theDaySMonth > 8) ?  1 : 2);
			const startYear: number = firstDate.minusDays(1).year();
			
			
			const pattern: string = "yyyy";
			const formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern).withLocale(Locale.ITALIAN);
			const formatOffset: Period = Period.of(0, 0, 1);

			const elabs: any[] = [];

			let elabYear: number = startYear;
			while (elabYear <= endYear) {
				const dataRife: LocalDateTime = LocalDateTime.of(elabYear+1, 1, 1);
				elabs.push(
					{
						dataRife,
						descData: dataRife.minus(formatOffset).format(formatter)
					}
				);
				elabYear = elabYear + 1;
			}


			
			// let currDate = startDate;
			// while (currDate.isBefore(endData)) {
			// 	const dataRife: LocalDateTime = (pdOffset == undefined) ? currDate : currDate.minus(pdOffset.period).minus(pdOffset.duration)
			// 	elabs.push(
			// 		{
			// 			dataRife,
			// 			descData: dataRife.minus(formatOffset).format(formatter)
			// 		}
			// 	);
			// 	currDate = currDate.plus(pdFreq.period).plus(pdFreq.duration);
			// }
			this.elaborazioni = elabs.reverse();
			this.idxElaborazioni = Object.fromEntries(
				this.elaborazioni.map(
					(v: any) => ([v.dataRife.format(DateTimeFormatter.ISO_LOCAL_DATE) , v])
				)
			);
			// console.log(this.elaborazioni);
			// console.log(this.idxElaborazioni);
		}
	}
	
	onChangeDataAvvio(valore?: LocalDateTime) {
		this.dataAvvioRichiesta = valore;
		this.onChangeDatiSchedulazione('dataAvvioRichiesta', valore);
		this.willEvalElaborazioni()
	}

	onChangePeriodoElaborazione(valore: any) {
		this.periodoElaborazione = valore;
		const dataRife: (LocalDate | undefined) = valore?.dataRife;
		this.onChangeDatiSchedulazione('dataRife', dataRife);
		if (dataRife != undefined) {
			const dataFine = dataRife.minusDays(1);
			const dataInizio = dataRife.minusYears(1);
			this.onChangeDatiSchedulazione('dataInizio', dataInizio);
			this.onChangeDatiSchedulazione('dataFine', dataFine);
		}
		else {
			this.onChangeDatiSchedulazione('dataInizio', undefined);
			this.onChangeDatiSchedulazione('dataFine', undefined);
		}
	}

	onChangeDatiSchedulazione(campo: string, valore: any) {
		this.datiSchedulazione[campo] = valore;
		this.datiSchedulazioneChange.emit(this.datiSchedulazione);
	}
}