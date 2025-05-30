import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from "@angular/core";
import { DateTimeFormatter, LocalDate, LocalDateTime, Period } from "@js-joda/core";
import { Locale } from "@js-joda/locale_it";
import { BaseAuthService } from "src/app/services/auth.service";
import { HtmlService } from "src/app/services/html.service";
import { PeriodDuration } from "src/app/utils/date-ext";



@Component({
	selector: 'app-dati-schedulazione-governance',
	templateUrl: './dati-schedulazione-governance.component.html'
})
export class DatiSchedulazioneGovernanceComponent implements OnChanges {
	errori: any = {};
	tipiElab: any[] = [];
	idxTipiElab: any = {};
	tipoElaborazione: any;
	dataAvvioRichiesta?: LocalDateTime;
	periodoElaborazione: any;
	getTipi: Promise<any>;
	

	@Input() datiSchedulazione: any;
	@Input() isReadOnly: boolean = false;

	@Output() datiSchedulazioneChange: EventEmitter<any> = new EventEmitter<any>();

	elaborazioni: any[] = [];
	idxElaborazioni: any = {};
	promElaborazione: Promise<void> = Promise.resolve();
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
				);
			}
		);
	}
	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case "datiSchedulazione": {
					this.onChangeDataAvvio(this.htmlService.toLocalDateTime(currValue.dataAvvioRichiesta));
					if (currValue.idBatch != undefined) {
						this.getTipi.then(
							() => {
								const dataRife = currValue.dataRife;
								this.onChangeTipoElaborazione(this.idxTipiElab[currValue.idBatch]);
								
								this.promElaborazione.then(
									() => {
										this.onChangePeriodoElaborazione(this.idxElaborazioni[dataRife]);
									}
								);
							}
						);
					}
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

			let elab = this.tipoElaborazione;
			if (elab.cod_batch == "MONITORAGGIO_SAT") {
				elab = {
					...elab,
					data_partenza: "2024-01-01T00:00:00.000+00:00",
					intervallo_frequenza: "0001-00-00/00:00:00.000",
					intervallo_offset: "0000-05-09/00:00:00.000"
				};
				let dataElaborazioneCorrente: LocalDateTime = LocalDateTime.parse(elab.data_partenza, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
				const pdOffset: (PeriodDuration|undefined) = (elab.intervallo_offset) ? PeriodDuration.parse(elab.intervallo_offset) : undefined;
				if (pdOffset != undefined) {
					dataElaborazioneCorrente = dataElaborazioneCorrente.plus(pdOffset.period).plus(pdOffset.duration);
				}
				const periodo: PeriodDuration = PeriodDuration.parse(elab.intervallo_frequenza);
				const dataScadenzaSuccessiva = this.dataAvvioRichiesta.plus(periodo.period).plus(periodo.duration);
				
				const elabs: any[] = [];
				const pattern: string = "yyyy";
				const formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern).withLocale(Locale.ITALIAN);
				const formatOffset: Period = Period.of(0, 0, 1);
				while (dataElaborazioneCorrente.isBefore(dataScadenzaSuccessiva)) {
					const dataRife: LocalDateTime = (pdOffset == undefined) ? dataElaborazioneCorrente : dataElaborazioneCorrente.minus(pdOffset.period).minus(pdOffset.duration)
					elabs.push(
						{
							dataRife,
							descData: dataRife.minus(formatOffset).format(formatter)
						}
					);
					dataElaborazioneCorrente = dataElaborazioneCorrente.plus(periodo.period).plus(periodo.duration);
				}
				this.elaborazioni = elabs.reverse();
				this.idxElaborazioni = Object.fromEntries(
					this.elaborazioni.map(
						(v: any) => ([v.dataRife.format(DateTimeFormatter.ISO_LOCAL_DATE) , v])
					)
				);
				this.onChangePeriodoElaborazione(this.idxElaborazioni[this.datiSchedulazione.dataRife]);
			}
			else {

				let currdate: LocalDateTime = LocalDateTime.parse(elab.data_partenza, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
				const pdOffset: (PeriodDuration|undefined) = (elab.intervallo_offset) ? PeriodDuration.parse(elab.intervallo_offset) : undefined;
				if (pdOffset != undefined) {
					currdate = currdate.plus(pdOffset.period).plus(pdOffset.duration);
				}
				const pdFreq: PeriodDuration = PeriodDuration.parse(elab.intervallo_frequenza);
				const endData = this.dataAvvioRichiesta.plus(pdFreq.period).plus(pdFreq.duration);
				
				const elabs: any[] = [];
				const pattern: string = ["_A", "_P3", "_P4"].findIndex(x => (elab.cod_batch).toString().endsWith(x)) >= 0 ? "yyyy"
				: (
					(elab.cod_batch).toString().endsWith("_M") ? "LLLL yyyy"
						: "dd/MM/yyyy"
				);
				const formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern).withLocale(Locale.ITALIAN);
				const formatOffset: Period = Period.of(0, 0, 1);
				while (currdate.isBefore(endData)) {
					const dataRife: LocalDateTime = (pdOffset == undefined) ? currdate : currdate.minus(pdOffset.period).minus(pdOffset.duration)
					elabs.push(
						{
							dataRife,
							descData: dataRife.minus(formatOffset).format(formatter)
						}
					);
					currdate = currdate.plus(pdFreq.period).plus(pdFreq.duration);
				}
				this.elaborazioni = elabs.reverse();
				this.idxElaborazioni = Object.fromEntries(
					this.elaborazioni.map(
						(v: any) => ([v.dataRife.format(DateTimeFormatter.ISO_LOCAL_DATE) , v])
					)
				);
				this.onChangePeriodoElaborazione(this.idxElaborazioni[this.datiSchedulazione.dataRife]);
				// console.log(this.elaborazioni);
			}

		}
	}
	onChangeTipoElaborazione(valore: any) {
		this.tipoElaborazione = valore;
		this.onChangeDatiSchedulazione('idBatch', valore?.id_batch);
		this.willEvalElaborazioni()
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

	}
	onChangeDatiSchedulazione(campo: string, valore: any) {
		this.datiSchedulazione[campo] = valore;
		this.datiSchedulazioneChange.emit(this.datiSchedulazione);
	}
}