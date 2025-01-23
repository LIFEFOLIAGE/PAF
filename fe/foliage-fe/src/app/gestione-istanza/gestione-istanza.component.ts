import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {BaseAuthService} from "../services/auth.service";
import {SessionManagerService} from "../services/session-manager.service";
import {InfoIstanzaModel} from "./models/info-istanza-model";
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { environment } from 'src/environments/environment';
import { LocalDate, nativeJs, convert } from '@js-joda/core';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';


type UpdatedDateType = "inizio" | "fine" | undefined;


@Component({
	selector: 'app-gestione-istanza',
	templateUrl: './gestione-istanza.component.html',
	styleUrls: ['./gestione-istanza.component.css']
})
export class GestioneIstanzaComponent implements OnInit {
	//protected readonly undefined = undefined;

	ambito?: string = undefined;

	currProfilo: any = undefined;
	userData: any = undefined;

	codIstanza: string | undefined;
	infoIstanza: InfoIstanzaModel | undefined = undefined;

	// codFiscaleNuovoGestore: string = "";

	updatedDateType: UpdatedDateType = undefined;
	updatedDate?: LocalDate = undefined;

	// getMinDate(): Date {
	// 	if (this.infoIstanza) {
	// 		if (this.infoIstanza.dataInizioLavori) {
	// 			// se settata data inizio valori la usiamo come data minima (per la data di fine)
	// 			return new Date(this.infoIstanza.dataInizioLavori)
	// 		}
	// 	}

	// 	// altrimenti mandiamo oggi (per data inizio)
	// 	return new Date()
	// }
	revocaButtonVisible: boolean = false;
	consultazioneButtonVisible: boolean = false;
	allegaButtonVisible: boolean = false;
	compilazioneButtonVisible: boolean = false;
	invioButtonVisible: boolean = false;
	assegnaIstruttoreButtonVisible: boolean = false;
	valutazioneButtonVisible: boolean = false;
	consultaIstruttoriaButtonVisible: boolean = false;
	modulisticaButtonVisible: boolean = false;
	passaggioGestoreButtonVisible: boolean = false;
	rimuoviGestoreButtonVisible: boolean = false;
	//allegaFirmatoButtonVisible: boolean = false;
	richiestaProrogaButtonVisible: boolean = false;
	inizioLavoriButtonVisible: boolean = false;
	fineLavoriButtonVisible: boolean = false;
	now: LocalDate = LocalDate.now();
	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private authService: BaseAuthService,
		private sessionManager: SessionManagerService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService

	) {
	}

	ngOnInit(): void {
		const routeParams = this.route.snapshot.paramMap;
		this.codIstanza = routeParams.get('codIstanza')??undefined;
		this.ambito = this.route.snapshot.data["ambito"];

		let root = "";
        let rootLabel = "";
        if (this.ambito == 'pubblico') {
            root = 'istanze';
            rootLabel = 'Istanze';
        }
        else if (this.ambito == 'cruscotto-pa') {
            root = "cruscotto-pa";
            rootLabel = "Cruscotto P.A.";
        }
        else if (this.ambito == 'vigilanza') {
            root = "vigilanza";
            rootLabel = "Vigilanza";
        }
        // const root = (this.ambito == 'pubblico' ? 'istanze' : 'cruscotto-pa');
        // const rootLabel = (this.ambito == 'pubblico' ? 'Istanze' : 'Cruscotto P.A.');
		
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
			],
			this.codIstanza
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = `Gestione istanza ${this.codIstanza}`;


		this.sessionManager.getCurrProfilo().then(
			(res: any) => {
				console.log(res);
				this.currProfilo = res;
				this.authService.getUserData().then(
					(userData: any) => {
						this.userData = userData;
						this.reload();
					}
				)
			}
		);
	}
	reload() {
		this.sessionManager.profileFetch(
			`/istanze/${this.codIstanza}/info`
		).then(
			(results: any) => {
				this.infoIstanza = results;
				console.log("recuperato info istanza", this.infoIstanza);
				
				this.consultazioneButtonVisible = false;
				this.compilazioneButtonVisible = false;
				this.invioButtonVisible = false;
				this.revocaButtonVisible = false;
				this.assegnaIstruttoreButtonVisible = false;
				this.valutazioneButtonVisible = false;
				this.modulisticaButtonVisible = false;
				// this.cambiaGestoreButtonVisible = false;
				this.passaggioGestoreButtonVisible = false;
				this.richiestaProrogaButtonVisible = false;
				this.inizioLavoriButtonVisible = false;
				this.fineLavoriButtonVisible = false;
				this.sessionManager.profileFetch(
					`/istanze/${this.codIstanza}/actions`
				).then(
					(results: any) => {
						console.log("recuperato bottoni istanza", JSON.stringify(results));
						this.allegaButtonVisible = results.allega_documenti ?? false;
						this.consultazioneButtonVisible = results.consultazione ?? false;
						this.compilazioneButtonVisible = results.compilazione ?? false;
						this.invioButtonVisible = results.invio ?? false;
						this.revocaButtonVisible = (results.revoca_istruttore ?? false);
						this.assegnaIstruttoreButtonVisible = (results.assegna_istruttore ?? false);
						this.valutazioneButtonVisible = (results.valutazione ?? false);
						this.consultaIstruttoriaButtonVisible = (results.consulta_valutazione ?? false);
						this.modulisticaButtonVisible = (this.consultaIstruttoriaButtonVisible || this.compilazioneButtonVisible);
						// this.cambiaGestoreButtonVisible = (results.cambia_gestore ?? false);
						this.passaggioGestoreButtonVisible = (results.passaggio_gestore ?? false);
						this.rimuoviGestoreButtonVisible = (results.rimuovi_gestore ?? false);
						//this.allegaFirmatoButtonVisible = (results.upload_modulo_firmato ?? false);
						this.richiestaProrogaButtonVisible = (results.richiesta_proroga ?? false);
						this.inizioLavoriButtonVisible = (results.inizio_lavori ?? false);
						this.fineLavoriButtonVisible = (results.fine_lavori ?? false);
					},
					(err) => {
						console.log("errore recuperato bottoni istanza", err)
					}
				);
			},
			(err) => {
				console.log("errore recuperato info istanza", this.infoIstanza)
			}
		);
	}
	prendiInCarico() {
		if (confirm("Voui prendere in carico la valutazione dell'istanza?")) {
			this.sessionManager.profileCsrsFetch(`/istanze/${this.codIstanza}/assegnaA/${this.userData.idUten}`).then(
				() => {
					alert("Hai preso in carico la valutazione dell'istanza");
				}
			).finally(
				() => {
					this.reload();
				}
			);
		}
	}
	rimuoviGestore() {
		if (confirm("Voui togliere il gestore dell'istanza?")) {
			this.sessionManager.profileCsrsFetch(
				`/istanze/${this.codIstanza}/rimuovi-gestore`,
				{
					method: 'POST'
				}
			).then(
				(v: any) => {
					alert("Hai rimosso il gestore dall'istanza");
				}
			).finally(
				() => {
					this.reload();
				}
			);
		}
	}
	revocaIstruttore() {
		if (confirm("Voui togliere la valutazione dall'istruttore?")) {
			this.sessionManager.profileCsrsFetch(
				`/istanze/${this.codIstanza}/revoca`,
				{
					method: 'POST'
				}
			).then(
				(v: any) => {
					alert('Assegnazione istanza revocata');
				}
			).finally(
				() => {
					this.reload();
				}
			);
		}
	}
	// openAllegaFirmato() {
	// 	console.log("openAllegaFirmato", this.codIstanza);
	// 	//this.router.navigate([`consulta/${(this.codIstanza)}`]);
	// 	this.router.navigate(
	// 		[`modulo-firmato`],
	// 		{
	// 			relativeTo: this.route
	// 		}
	// 	);
	// }
	openConsultazione() {
		console.log("openConsultazione", this.codIstanza);
		//this.router.navigate([`consulta/${(this.codIstanza)}`]);
		this.router.navigate(
			[`consulta`],
			{
				relativeTo: this.route
			}
		);
	}
	openPdf() {
		console.log("openConsultaModulistiva", this.codIstanza);
		//this.router.navigate([`consulta/${(this.codIstanza)}`]);
		this.router.navigate(
			['consulta-modulistica'],
			{
				relativeTo: this.route
			}
		);
	}
	openPdfCompilazione() {
		console.log("openPreparaModulistiva", this.codIstanza);
		//this.router.navigate([`compila/${(this.codIstanza)}`]);
		this.router.navigate(
			[`prepara-modulistica`],
			{
				relativeTo: this.route
			}
		);
	}

	openCompilazione() {
		// (proprietario o professionista - solo quando è l'utente gestore della domanda)
		//  link per apertura istanza in compilazione (ora non preoccuparti di quale sia il link preciso)
		console.log("openCompilazione", this.codIstanza);
		//this.router.navigate([`compila/${(this.codIstanza)}`]);
		this.router.navigate(
			[`compila`],
			{
				relativeTo: this.route
			}
		);
	}

	openInvio() {
		// invio istanza e caricamento in allegato dei file con i bolli pagati necessari
		// (proprietario e professionista - se in stato "compilazione"):
		// link a una pagina specifica con un form per inserire i dati necessari all’invio
		console.log("openInvio", this.codIstanza);
		// this.router.navigate([`/istanze/compilazione/${(this.codIstanza)}`]);
		this.router.navigate(
			[`invio`],
			{
				relativeTo: this.route
			}
		);
	}

	openAssegnaIstruttore() {
		//assegnazione istruttore (dirigente se istruttoria non assegnata e presentata):
		// link che porterebbe alla pagina di gestore utenze per assegnamento di uno
		// degli istruttori dell'ente all'istanza
		console.log("openAssegnaIstruttore");
		this.router.navigate(
			[`assegnazioneIstruttore`, this.infoIstanza?.idEnteTerritoriale],
			{
				relativeTo: this.route
			}
		);
	}

	openValutazione() {
		console.log("openValutazione");
		this.router.navigate(
			[`valutazione`],
			{
				relativeTo: this.route
			}
		);
	}
	consultaValutazione() {
		console.log("openValutazione");
		this.router.navigate(
			[`consulta-valutazione`],
			{
				relativeTo: this.route
			}
		);
	}

	openRichiestaDiProroga() {
		console.log("openRichiestaDiProroga");
		this.router.navigate(
			[`proroga`],
			{
				relativeTo: this.route
			}
		);
	}

	openPassaggioGestore() {
		console.log("openPassaggioGestore");
		this.router.navigate(
			[`assegna-gestore`],
			{
				relativeTo: this.route
			}
		);
	}

	onDateChanged(event: string, tipo: UpdatedDateType) {
		console.log("Date changed", event, tipo);
		this.updatedDateType = tipo
		try {
			this.updatedDate = LocalDate.parse(event);
		}
		catch(e) {
			this.updatedDate = undefined;
		}
	}
	
	dropIstanza() {
		if (confirm(`Stai per eliminare l'istanza ${this.codIstanza}!`)) {
			this.sessionManager.profileCsrsFetch(
				`/istanze/${this.codIstanza}`,
				{
					method: 'DELETE'
				}
			).then(
				(v: any) => {
					alert('Istanza eliminata');
					this.router.navigate(
						['..'],
						{
							relativeTo: this.route
						}
					);
				}
			);
		}
	}

	// confirmDateChange() {
	// 	console.log("chiamata be aggiornare data", this.updatedDateType, this.updatedDate)
	// 	const fineVali = this.infoIstanza?.dataFineValidita
		
		
	// 	if (fineVali != undefined) {
	// 		let servizio: string = "";
	// 		let dataIndicata: any = undefined;
			
	// 		//const currentDate: LocalDate = LocalDate.now();
	// 		//const timeIndicato = this.updatedDate.getTime();

	// 		switch (this.updatedDateType) {
	// 			case "inizio": {
	// 				const dataFineVali = LocalDate.parse(fineVali);
	// 				servizio = 'comunica-inizio';
	// 				if (this.updatedDate == undefined) {
	// 					alert("La data di inizio lavori deve essere specificata");
	// 					return;
	// 				}
	// 				else {
	// 					if (
	// 						dataIndicata.isBefore(this.now)
	// 						//timeIndicato < currentDate.getTime()
	// 						|| //timeIndicato > convert(LocalDate.parse(dataFineVali)).toDate().getTime()
	// 						this.updatedDate.isAfter(dataFineVali)
	// 					) {
	// 						alert("La data di inizio lavori deve essere odierna o successiva (entro il periodo di validità)");
	// 						return;
	// 					}
	// 					else {
	// 						dataIndicata = this.updatedDate;
	// 					}
	// 				}
	// 			}; break;
	// 			case "fine": {
	// 				servizio = 'comunica-fine';
	// 				//const timeInizio = convert(LocalDate.parse(this.infoIstanza?.dataInizioLavori??"")).toDate().getTime();
	// 				// const dataInizio = LocalDate.parse(this.infoIstanza?.dataInizioLavori??"");
	// 				// if (
	// 				// 	//timeIndicato < currentDate.getTime()
	// 				// 	this.updatedDate.isBefore(currentDate)
	// 				// 	//|| timeIndicato > convert(LocalDate.parse(dataFineVali)).toDate().getTime()
	// 				// 	|| this.updatedDate.isAfter(dataFineVali)
	// 				// 	//|| timeIndicato < timeInizio
	// 				// 	|| this.updatedDate.isBefore(dataInizio)
	// 				// ) {
	// 				// 	alert("La di fine lavori data deve essere odierna o successiva (entro il periodo di validità) e successiva alla data di inizio lavori");
	// 				// }
	// 				dataIndicata = this.now;
	// 			}; break;
	// 		}
			
	// 	}
	// }

	comunicaLavori(servizio: string, dataIndicata: LocalDate) {
		this.sessionManager.profileCsrsFetch(
			`/istanze/${this.codIstanza}/${servizio}`,
			{
				method: 'POST',
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify(dataIndicata)
			}
		).then(
			(v: any) => {
				alert('Comunicazione Lavori effettuata');
			}
		).finally(
			() => {
				this.reload();
			}
		);
	}

	comunicaInizio() {
		const fineVali = this.infoIstanza?.dataFineValidita;
		if (fineVali != undefined) {
			const dataFineVali = LocalDate.parse(fineVali);
			if (this.updatedDate == undefined) {
				alert("La data di inizio lavori deve essere specificata");
				return;
			}
			else {
				if (
					this.updatedDate.isBefore(this.now)
					//timeIndicato < currentDate.getTime()
					|| //timeIndicato > convert(LocalDate.parse(dataFineVali)).toDate().getTime()
					this.updatedDate.isAfter(dataFineVali)
				) {
					alert("La data di inizio lavori deve essere odierna o successiva (entro il periodo di validità)");
					return;
				}
			}
			this.comunicaLavori('comunica-inizio', this.updatedDate);
		}

	}
	comunicaFine() {
		this.comunicaLavori('comunica-fine', this.now);
	}
}
