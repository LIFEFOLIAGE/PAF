import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { confIstanze } from './conf/schede';
import { BaseAuthService, CsrsToken } from '../../../services/auth.service';
import { TipoDatiScheda } from '../../shared/editor-scheda/editor-scheda.component';
import { SessionManagerService } from 'src/app/services/session-manager.service';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { DateTimeFormatter, LocalDateTime } from '@js-joda/core';
import { RequestService } from 'src/app/services/request.service';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';


export type ExportValue = {
	value: any,
	opts: any,
	idxSezione: number;
	idxScheda: number
};


@Component({
	selector: 'app-editor-istanza',
	templateUrl: './editor-istanza.component.html',
	styleUrls: ['./editor-istanza.component.css']
})
export class EditorIstanzaComponent implements OnChanges, OnInit{
	static getSrcDati(datiPratica: any) : string {
		return JSON.stringify(datiPratica);
	}
	static getInitModifichePraticaFromDati(datiPratica: any) : any {
		if (datiPratica) {
			return Object.keys(datiPratica).map(
				x => Object.keys(x).map(
					y => ({})
				)
			);
		}
		else {
			return [];
		}
	}
	static getInitModifichePraticaFromConf(sezioniPratica: any) : any {
		if (sezioniPratica) {
			return sezioniPratica.map(
				(sez: any) => sez.schede.map(
					(scheda: any) => {
						switch (scheda.tipoDati) {
							case TipoDatiScheda.Object: {
								return ({});
							}; break;
							case TipoDatiScheda.Array: {
								return ([]);
							}; break;
							default: return undefined;
						}
					}
				)
			);
		}
		else {
			return [];
		}
	}

	static getInitValiditaPraticaFromConf(sezioniPratica: any) : boolean[][] {
		if (sezioniPratica) {
			return sezioniPratica.map(
				(sez: any) => sez.schede.map(
					(scheda: any) => {
						return true;
					}
				)
			);
		}
		else {
			return [];
		}
	}

	static getInitValiditaPraticaFromDati(datiPratica: any) : boolean[][] {
		if (datiPratica) {
			return Object.keys(datiPratica).map(
				x => Object.keys(x).map(
					y => true
				)
			);
		}
		else {
			return [];
		}
	}




	resources: any;

	//sezioniPratica: any[] = istanzaSopraSoglia;
	sezioniPratica: any[] = [];
	//sezioniPratica: any[] = istanzaSopraSoglia;
	datiIstanza: any[] = [];
	statoCompilazione: Record<number, any> = {};


	contesto: any = {};
	rilevamenti: any = {};

	public idxUltimaSezione: number = 4;
	public idxUltimaScheda: number = 0;


	public idxSezioneSelezionata: number = -1;
	public idxSchedaSelezionata: number = -1;
	dictionariesData?: Record<string, any>;
	dictionariesRilevamenti?: Record<string, any>;
	schedaRilevamenti: any;
	private hasChanges: boolean = false;
	ready: boolean = false;
	onRilevamenti: boolean = false;
	importedValue: any;
	titoloCorto?: string;

	private srcDati : string = EditorIstanzaComponent.getSrcDati(this.datiIstanza);
	private modificheIstanza: any[] = EditorIstanzaComponent.getInitModifichePraticaFromConf(this.sezioniPratica);
	private validitaPratica: boolean[][] = EditorIstanzaComponent.getInitValiditaPraticaFromConf(this.sezioniPratica);

	tipoIstanza!: number;
	
	private _schedaSelezinata: any = {};
	public get schedaSelezionata(): any {
		if (this.idxSezioneSelezionata >= 0 && this.idxSchedaSelezionata >= 0) {
			return (this.sezioniPratica[this.idxSezioneSelezionata]?.schede[this.idxSchedaSelezionata])??{};
		}
		else {
			return {};
		}
	}
	public get modificheSchedaSelezionata(): any {
		if (this.idxSezioneSelezionata >= 0 && this.idxSchedaSelezionata >= 0) {
			const modificheSezione = this.modificheIstanza[this.idxSezioneSelezionata];
			if (modificheSezione) {
				return (modificheSezione[this.idxSchedaSelezionata])??{};
			}
			else {
				return {}
			}
		}
		else {
			return {};
		}
	}
	public get datiSchedaSelezionata(): any {
		// if (this.idxSezioneSelezionata == 2 && this.idxSchedaSelezionata == 6) {
		// 	return testProspettiRiepilogativi;
		// }
		if (this.datiIstanza && this.datiIstanza.length > 0 && this.idxSezioneSelezionata >= 0 && this.idxSchedaSelezionata >= 0) {
			return this.datiIstanza[this.idxSezioneSelezionata][this.idxSchedaSelezionata];
		}
		else {
			return undefined;
		}
	}
	public get nomeSchedaSelezionata(): string {
		return (this.schedaSelezionata) ? this.schedaSelezionata.nome : "";
	}

	ambito: string = "";
	idxPrimaSchedaObblNonSalvata?: number;
	idxUltimaSchedaSalvata?: number;

	@Input() isReadOnly: boolean = false;
	@Input() routeUscita!: string;
	@Input() codIstanza?: string;

	constructor(
		private router : Router,
		private route: ActivatedRoute,
		private authService: BaseAuthService,
		private sessionManager: SessionManagerService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}

	ngOnInit(): void {
		this.ambito = this.route.snapshot.data["ambito"];
		this.codIstanza = this.route.snapshot.params['codIstanza'];

		let root: string|undefined = undefined;
		let rootLabel: string|undefined = undefined;
		//const root = (this.ambito == 'pubblico' ? 'istanze' : 'cruscotto-pa');
		//const rootLabel = (this.ambito == 'pubblico' ? 'Istanze' : 'Cruscotto P.A.');

		switch (this.ambito) {
			case "pubblico": {
				root = 'istanze';
				rootLabel = 'Istanze';
			}; break;
			case "cruscotto-pa": {
				root = 'cruscotto-pa';
				rootLabel = 'Cruscotto P.A.';
				this.isReadOnly = true;
			}; break;
			case "vigilanza":{
				this.isReadOnly = true;
				root = 'vigilanza';
				rootLabel = 'Vigilanza';
			}; break;
			default: {
				const errMsg = "Ambito non gestito";
				alert(errMsg);
				throw new Error(errMsg);
			}
		}

		const breadcrumbModel = new BreadcrumbModel(
			[
				{
					icon: 'bi bi-house',
					url: ['/']
				},
				{
					label: rootLabel,
					url: [root]
				},
				{
					label: this.codIstanza??'',
					url: [root, this.codIstanza??""]
				}
			],
			this.isReadOnly ? "Consulta" : "Compila"
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titoloCorto = this.getTitle();
		this.titleService.setAll(this.titoloCorto, true);
	}
	getTitle() {
		const prefix = (this.isReadOnly) ? "Consulta" : "Compila";
		const schedaSelezionata = this.schedaSelezionata;
		if (schedaSelezionata && schedaSelezionata.nome) {
			return `${prefix} Istanza ${this.codIstanza} - ${schedaSelezionata.nome}`;
		}
		else {
			return `${prefix} Istanza ${this.codIstanza}`;
		}
	}
	ngOnChanges(changes: SimpleChanges) {
		console.log(changes);
		for (let propName in changes) {
			const v = changes[propName].currentValue;
			switch (propName) {
				case "codIstanza": {
					this.apriIstanza(v);
				}; break;
			}
		}
	}

	isSchedaAttiva(idxSezione: number, idxScheda: number, idxSalvataggio: number) {
		const datiSezione = this.datiIstanza[idxSezione];
		return (
			datiSezione != undefined 
			&& datiSezione[idxScheda] != undefined
			// && (
			// 	this.isReadOnly ?
			// 	(
			// 		this.idxUltimaSchedaSalvata == undefined 
			// 		|| idxSalvataggio <= this.idxUltimaSchedaSalvata
			// 	)
			// 		: (
			// 			this.idxPrimaSchedaObblNonSalvata == undefined 
			// 			|| idxSalvataggio <= this.idxPrimaSchedaObblNonSalvata
			// 		)
			// )
			&& (
				this.idxPrimaSchedaObblNonSalvata == undefined
				|| (
					this.isReadOnly ?
						idxSalvataggio < this.idxPrimaSchedaObblNonSalvata :
						idxSalvataggio <= this.idxPrimaSchedaObblNonSalvata
				)

			)
		);

		//return (idxSezione < this.idxUltimaSezione) || (idxSezione == this.idxUltimaSezione && idxScheda <= this.idxUltimaScheda);
	}
	caricaDatiIstanza(schede: any[], idxStart: number = 0) {
		let idx = 0;
		if (schede != undefined) {
			//this.datiIstanza = [];
			this.sezioniPratica.forEach(
				(sez: any, idxSez: number) => {
					sez.schede.forEach(
						(sched: any, idxSched: number) => {
							const sezDest = this.datiIstanza[idxSez];
							if (sezDest != undefined) {
								if (idxStart <= idx) {
									const datiInp = schede[idx-idxStart];
									if (datiInp == undefined)
									{
										switch (sched.tipoDati) {
											case TipoDatiScheda.Object: {
												sezDest[idxSched] = {};
											}; break;
											case TipoDatiScheda.Array: {
												sezDest[idxSched] = [];
											}; break;
											default: {
												sezDest[idxSched] = undefined;
											}
										}
									}
									else {
										sezDest[idxSched] = datiInp;
									}
									sched.idxSalvataggio = idx;
								}
							}
							idx++;
						}
					)
				}
			);
		}
	}

	initStatoCompilazione(statoCompilazione: any[]) {
		const longDf = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm:ss');
		const LocDateTime = LocalDateTime;
		let idxPrimaSchedaObblNonSalvata: number|undefined = undefined;
		let idxUltimaSchedaSalvata: number|undefined = undefined;
		this.statoCompilazione = Object.fromEntries(
			statoCompilazione.map(
				(v: any) => {
					const prog: number = v.prog;
					const dataSalvataggio: (string|undefined) = v.dataSalvataggio ? LocDateTime.parse(v.dataSalvataggio).format(longDf) : undefined;
					if (dataSalvataggio == undefined) {
						if (v.isObbligatoria) {
							if (idxPrimaSchedaObblNonSalvata) {
								if (idxPrimaSchedaObblNonSalvata > prog) {
									idxPrimaSchedaObblNonSalvata = prog;
								}
							}
							else {
								idxPrimaSchedaObblNonSalvata = prog;
							}
						}
					}
					else {
						if (idxUltimaSchedaSalvata) {
							if (idxUltimaSchedaSalvata < prog) {
								idxUltimaSchedaSalvata = prog;
							}
						}
						else {
							idxUltimaSchedaSalvata = prog;
						}
					}
					return [
						prog,
						{
							...v,
							dataSalvataggio
						}
					];
				}
			)
		);
		this.idxPrimaSchedaObblNonSalvata = idxPrimaSchedaObblNonSalvata;
		this.idxUltimaSchedaSalvata = idxUltimaSchedaSalvata;

	}

	caricamento(results: any) {
		const contesto = results.contesto;
		if (contesto) {
			this.contesto = contesto;
			if (contesto.codTipoIstanzaSpecifico && confIstanze[contesto.codTipoIstanzaSpecifico] != undefined) {
				const confIstanza = confIstanze[contesto.codTipoIstanzaSpecifico];
				this.sezioniPratica = confIstanza.schede;
				
				this.datiIstanza = EditorIstanzaComponent.getInitModifichePraticaFromConf(this.sezioniPratica);
				this.caricaDatiIstanza(results.schede);
				this.hasChanges = false;

				this.initStatoCompilazione(results.statoCompilazione);

				const rilevamenti = results.rilevamenti;
				if (rilevamenti) {
					this.rilevamenti = rilevamenti;
					if (confIstanza.schedaRilevamenti) {
						this.schedaRilevamenti = confIstanza.schedaRilevamenti;
						this.dictionariesRilevamenti = this.getSchedaDictionary(confIstanza.schedaRilevamenti);
					}
				}
			}
		}
	}
	caricamentoBound = this.caricamento.bind(this);
	apriIstanza(codIstanza: string) {
		this.ready = false;
		this.contesto = {
			shared: {},
			codIstanza: codIstanza
		};
		this.rilevamenti = {}
		this.resources = {alert: alert, log: console.log, shared: {}, isReadOnly: this.isReadOnly};

		this.dictionariesRilevamenti = undefined;
		this.schedaRilevamenti = undefined;
		const p1: Promise<void> = this.sessionManager.profileFetch(`/istanze/${codIstanza}`).then(
			this.caricamentoBound
			// (results: any) => {
			// 	const contesto = results.contesto;
			// 	if (contesto) {
			// 		this.contesto = contesto;
			// 		if (contesto.codTipoIstanzaSpecifico && confIstanze[contesto.codTipoIstanzaSpecifico] != undefined) {
			// 			const confIstanza = confIstanze[contesto.codTipoIstanzaSpecifico];
			// 			this.sezioniPratica = confIstanza.schede;
						
			// 			this.datiIstanza = EditorIstanzaComponent.getInitModifichePraticaFromConf(this.sezioniPratica);
			// 			this.caricaDatiIstanza(results.schede);

			// 			this.initStatoCompilazione(results.statoCompilazione);

			// 			const rilevamenti = results.rilevamenti;
			// 			if (rilevamenti) {
			// 				this.rilevamenti = rilevamenti;
			// 				if (confIstanza.schedaRilevamenti) {
			// 					this.schedaRilevamenti = confIstanza.schedaRilevamenti;
			// 					this.dictionariesRilevamenti = this.getSchedaDictionary(confIstanza.schedaRilevamenti);
			// 				}
			// 			}
			// 		}
			// 	}
			// }
		);

		const p2: Promise<void> = this.authService.getUserData().then(
				(res) => {
					this.contesto.cfUtente = res.cf;
				}
			);

		const p3: Promise<void> = this.authService.getCsrsToken().then(
				(tok: CsrsToken) => {
					this.contesto.csrsToken = tok;
				}
			);

		Promise.all([
			p1,
			p2,
			p3
		]).then(
			() => {
				this.modificheIstanza = EditorIstanzaComponent.getInitModifichePraticaFromConf(this.sezioniPratica);
				
				if (this.sezioniPratica[0] && this.sezioniPratica[0].schede && this.sezioniPratica[0].schede[0]) {
					this.selezioneScheda(0, 0);
				}
				else {
					this.idxSezioneSelezionata = -1;
					this.idxSchedaSelezionata = -1;
				}
				this.ready = true;
			}
		);
	}



	public isSchedaModificata(idxSezione: number, idxScheda: number) : boolean {
		if (this.modificheIstanza) {
			const modSezione = this.modificheIstanza[idxSezione];
			if (modSezione) {
				const modScheda = modSezione[idxScheda];
				if (modScheda) {
					return Object.keys(modScheda).length > 0
				}
			}
		}
		return false;
	}
	public isSchedaInvalida(idxSezione: number, idxScheda: number) : boolean {
		return this.validitaPratica[idxSezione][idxScheda] == false;
	}
	static getDatiPratica(src: any, keys: (number|string)[]): any {
		if (keys.length == 0) {
			return src;
		}
		else {
			return EditorIstanzaComponent.getDatiPratica(
				src[keys[0]],
				keys.slice(1)
			);
		}
	}
	getDatiPratica(keys?: (number|string)[]) {
		if (keys) {
			return EditorIstanzaComponent.getDatiPratica(this.datiIstanza, keys);
		}
		else {
			return this.rilevamenti;
		}
	}
	getSchedaDictionary(scheda: any) {
		const dic: Record<string, (number|string)[]> = scheda?.dictionaries;
		if (dic) {
			console.log(dic);
			return Object.fromEntries(
				Object.entries(dic).map(
					([k, v]) => [
						k,
						this.getDatiPratica(v)
					]
				)
			);
		}
		else {
			return undefined;
		}
	}
	apriRilevamenti() {
		if (this.hasChanges) {
			alert("Prima di poter passare ai rilevamenti è necessario confermare o annullare le modifiche");
		}
		else {
			this.onRilevamenti = true;
			this.idxSezioneSelezionata = -1;
		}
	}
	currSchedaKey: string = "";
	getSchedaKey(idxSezione: number, idxScheda: number) {
		if (idxSezione == undefined) {
			return "";
		}
		else {
			if (idxScheda == undefined) {
				return "";
			}
			else {
				return idxSezione.toString() + '_' + idxScheda.toString();
			}
		}
	}
	selezioneScheda(idxSezione: number, idxScheda: number, importedValue?: any) {
		if (this.hasChanges) {
			alert("Prima di poter cambiare scheda è necessario confermare o annullare le modifiche");
		}
		else {
			this.onRilevamenti = false;
			this._schedaSelezinata = (this.sezioniPratica[idxSezione]?.schede[idxScheda])??{};
			this.dictionariesData = this.getSchedaDictionary(this._schedaSelezinata);
			if (this._schedaSelezinata.tipoDati == TipoDatiScheda.Array && this._schedaSelezinata.conf) {
				this._schedaSelezinata.conf.dictionariesData = this.dictionariesData;
			}
			this.importedValue = importedValue;

			this.idxSezioneSelezionata = idxSezione;
			this.idxSchedaSelezionata = idxScheda;
			this.currSchedaKey = this.getSchedaKey(idxSezione, idxScheda);
			this.titleService.setAll(this.getTitle(), true);
		}
	}
	onDataConfirmedBuond = this.onDataConfirmed.bind(this);
	onDataConfirmed(evento: any) {
		let modifiche = evento.modifiche;
		let dati = evento.dati;
		console.log(evento);
		//let oldDati = this.datiSchedaSelezionata;
		//this.datiIstanza[this.idxSezioneSelezionata][this.idxSchedaSelezionata] = dati;
		this.isReadOnly = true;
		return this.sessionManager.profileCsrsFetch(
				`/istanze/${this.contesto.codIstanza}/${this.schedaSelezionata.idxSalvataggio}`,
				{
					method: "PUT",
					body: JSON.stringify(evento.dati),
					credentials: 'include'
				}
			).then(
				res => {
					console.log({postSalvataggio: res});
					this.caricamento(res);
					//this.selezioneScheda(this.idxSezioneSelezionata, this.idxSchedaSelezionata);
					console.log(this.dictionariesData);
					this.dictionariesData = this.getSchedaDictionary(this._schedaSelezinata);
					console.log(this.dictionariesData);

					// if (res.schede != undefined) {
					// 	//this.caricaDatiIstanza(res.schede, this.schedaSelezionata.idxSalvataggio);
					// 	this.caricaDatiIstanza(res.schede);
					// }
					// if (res.context != undefined) {
					// 	this.contesto = res.context;
					// }
					// if (res.statoCompilazione) {

					// 	this.initStatoCompilazione(res.statoCompilazione);

					// 	// const longDf = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm:ss');
					// 	// const LocDateTime = LocalDateTime;
					// 	// this.statoCompilazione = Object.fromEntries(
					// 	// 	res.statoCompilazione.map(
					// 	// 		(v: any) => {
					// 	// 			const prog = v.prog;
					// 	// 			const dataSalvataggio = v.dataSalvataggio ? LocDateTime.parse(v.dataSalvataggio).format(longDf) : undefined;
					// 	// 			return [
					// 	// 				prog,
					// 	// 				{
					// 	// 					...v,
					// 	// 					dataSalvataggio
					// 	// 				}
					// 	// 			];
					// 	// 		}
					// 	// 	)
					// 	// );
					// }

					console.log(this);
					alert("Salvataggio avvenuto correttamente");
					return Promise.resolve();
				},
				(err) => {
					//this.datiIstanza[this.idxSezioneSelezionata][this.idxSchedaSelezionata] = oldDati;
					//alert("Si è verificato un problema durante il salvataggio");
					return Promise.reject(err);
				}
			).finally(
				() => {
					this.isReadOnly = false;
				}
			);
		// this.datiIstanza[this.idxSezioneSelezionata][this.idxSchedaSelezionata] = dati;
		// this.modificheIstanza[this.idxSezioneSelezionata][this.idxSchedaSelezionata] = modifiche;

		// console.log(this.modificheIstanza);

	}
	onDataChangedBuond = this.onDataChanged.bind(this);
	onDataChanged(evento: any) {
		//console.log(evento);
		this.hasChanges = evento && (Object.keys(evento).length > 0);
		return Promise.resolve();
	}
	onExportRilevamento(value: ExportValue) {
		console.log(value);
		this.selezioneScheda(value.idxSezione, value.idxScheda, value);
	}
	trackBySezione(index: number, sezione: any) {
		return index;
	}
	trackByScheda(index: number, scheda: any) {
		return index;
	}
}
