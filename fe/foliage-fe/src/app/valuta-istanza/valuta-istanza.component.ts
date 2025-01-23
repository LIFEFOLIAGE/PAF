import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { defaultStrokeStyle } from 'ol/render/canvas';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BaseAuthService } from '../services/auth.service';
import { SessionManagerService } from '../services/session-manager.service';
import { deepClone } from '../services/utils';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';
import {
	schedaCaricatiPubblico,
	schedaCaricatiIstruttore,
	schedaRichiestaIstruttore,
	schedaRichiestaPubblico
} from './scheda';


@Component({
	selector: 'app-valuta-istanza',
	templateUrl: './valuta-istanza.component.html'
})
export class ValutaIstanzaComponent implements OnInit {

	ambito?: string;
	codIstanza: string = '';
	formConsegnati: any = {};
	datiFormConsegnati: any = {};
	formRichiesti: any;
	datiFormRichiesti: any;
	arrIdRichiestiIniziale: any;
	arrIdConsegnatiIniziale: any;
	datiFormConsegnatiIniziale: any;
	datiFormRichiestiIniziale: any;
	isConsegnatiReadOnly: boolean = true;
	showUlterioriDestinatari: boolean = false;
	isReadOnly: boolean = true;
	errori: any = {};
	isSalvato: boolean = false;

	openValutazioneDialog: boolean = false;
	noteValutazione: string = "";
	isSalvaEnabled: boolean = false;
	isValutazioneEnabled: boolean = false;
	valutazione?: {esito: boolean, noteValutazione: string} = undefined;
	datiIstr: any = {};

	constructor(
		private route: ActivatedRoute,
		private authService: BaseAuthService,
		private sessionManager: SessionManagerService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}

	ngOnInit(): void {
		this.ambito = this.route.snapshot.data["ambito"];
		this.isReadOnly = this.route.snapshot.data["isReadOnly"];
		this.codIstanza = this.route.snapshot.params['codIstanza'];

		let root: string | undefined = undefined;
		let rootLabel: string | undefined = undefined;

		switch (this.ambito) {
			case "pubblico": {
				this.formRichiesti = schedaRichiestaPubblico;
				this.formConsegnati = schedaCaricatiPubblico;
				this.isConsegnatiReadOnly = true;
				root = 'istanze';
				rootLabel = 'Istanze';
			}; break;
			case "cruscotto-pa": {
				this.formRichiesti = schedaRichiestaIstruttore;
				this.formConsegnati = schedaCaricatiIstruttore;
				this.isConsegnatiReadOnly = false;
				root = 'cruscotto-pa';
				rootLabel = 'Cruscotto P.A.';
			}; break;
			case "vigilanza":{
				this.formRichiesti = schedaRichiestaIstruttore;
				this.formConsegnati = schedaCaricatiIstruttore;
				this.isConsegnatiReadOnly = true;
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
		
		
		const prefix = this.isReadOnly ? "Consulta" : "Compila"
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
					label: `Gestione ${this.codIstanza}`,
					url: [root, `${(this.codIstanza)}`]
				}
			],
			`${prefix} Valutazione`
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = `Istanza ${this.codIstanza} - ${prefix} Valutazione`;

		this.sessionManager.profileFetch(`/istanze/${this.codIstanza}/dati-istruttoria`).then(
			(res) => {
				this.caricaDati(res);
			}
		);
	}

	caricaDati(dati: any) {
		const file = dati.file;
		const valutazione = dati.valutazione;
		if (valutazione != undefined) {
			this.isReadOnly = true;
			this.valutazione = valutazione;
		}
		const datiIstr = dati.datiIstr;
		if (datiIstr != undefined) {
			this.datiIstr = datiIstr;
			this.isSalvato = (!datiIstr.isNew)??false;
			this.showUlterioriDestinatari = datiIstr.ulterioriDestinatari != undefined;
		}
		this.datiFormRichiesti = {richieste: file.richiesti}
		this.datiFormRichiestiIniziale = {...this.datiFormRichiesti};

		this.datiFormConsegnati = {caricati: file.consegnati};
		this.datiFormConsegnatiIniziale = {...this.datiFormConsegnati};


		this.arrIdRichiestiIniziale = file.richiesti.map((x:any) => x.idRichiesta);
		this.arrIdConsegnatiIniziale = file.consegnati.map((x:any) => x.idRichiesta);
		
		this.refreshEnabledButtons();
	}

	onDataFormRichiestiChanged(changes: any) {
		console.log("changes", { datiFormRichiesti: changes });

		this.datiFormRichiesti = { ...this.datiFormRichiesti, ...changes };
		this.isSalvato = false;
		this.refreshEnabledButtons();
	}

	onDataFormConsegnatiChanged(changes: any) {
		console.log("changes", { datiFormConsegnati: changes });
		this.datiFormConsegnati = { ...this.datiFormConsegnati, ...changes };
		this.isSalvato = false;
		this.refreshEnabledButtons();
	}

	isPubblicaAmministrazione(): boolean {
		return this.ambito == 'cruscotto-pa';
	}

	cambiaDatiIstruttoria(campo: string, valore: any) {
		this.datiIstr[campo] = valore;
		this.isSalvato = false;
		this.refreshEnabledButtons();
	}

	changeUlterioriDestinatari(newValue: boolean) {
		this.showUlterioriDestinatari = newValue;
		if (!newValue) {
			delete this.datiIstr["ulterioriDestinatari"];
		}
		this.checkErroriDatiIstr();
	}

	checkErroriDatiIstr() {
		this.errori = {};
		["oggetto", "testo"].forEach(
			s => {
				const val = this.datiIstr[s];
				if (!val) {
					this.errori[s] = "Valore richiesto";
				}
			}
		);
		
		if (this.showUlterioriDestinatari) {
			const ult = this.datiIstr["ulterioriDestinatari"];
			if (!ult || ult.length == 0) {
				this.errori["ulterioriDestinatari"] = "Valore richiesto";
			}
			else {
				delete this.errori["ulterioriDestinatari"];
			}
		}
		else {
			delete this.errori["ulterioriDestinatari"];
		}
	}
	doSalva() {
		this.checkErroriDatiIstr();
		const hasErrors = Object.entries(this.errori).length != 0;
		if (hasErrors) {
			alert("Impossibile proseguire con il salvataggio: i dati di compilazione del documento non sono validi!");
		}
		else {
			const allRichiesti = this.datiFormRichiesti.richieste;
			const allConsegnati = this.datiFormConsegnati.caricati;
			const richiestiEliminati = this.arrIdRichiestiIniziale.filter(
				(idA: any) => !allRichiesti.map((x: any) => x.idRichiesta).includes(idA)
			);
			const consegnatiEliminati = this.arrIdConsegnatiIniziale.filter(
				(idA: any) => !allConsegnati.map((x: any) => x.idRichiesta).includes(idA)
			);
			const richiesti = (this.ambito == 'pubblico') ? allRichiesti : allRichiesti.filter((x: any) => x.idRichiesta == undefined);
	
	
	
			const dataToSend = {
				file: {
					richiestiEliminati, consegnatiEliminati,
					richiesti
				},
				dettagli: this.datiIstr
			};
	
			console.log("salva: ", dataToSend);
	
			this.sessionManager.profileCsrsFetch(
				`/istanze/${this.codIstanza}/dati-istruttoria`,
				{
					method: "PUT",
					headers: {
						"Content-Type": "application/json",
					},
					body: JSON.stringify(dataToSend),
					// credentials: 'include' //TODO: serve ?
				}
			).then(
				(results) => {
					console.log(results);
					alert("Salvataggio effettuato");
					this.caricaDati(results);
				}
			);

		}
	}

	
	downloadBozzaPdf() {
		this.sessionManager.profileFetch(`/istanze/${this.codIstanza}/bozza-modulo-istruttoria`).then(
			(blob) => {
				if (blob != null) {
					var url = window.URL.createObjectURL(blob);
					var a = document.createElement('a');
					a.href = url;
					a.download = `${this.codIstanza}-bozza-valutazione.pdf`;
					document.body.appendChild(a);
					a.click();
					a.remove();
				}
			}
		)
	}

	downloadPdf() {
		this.sessionManager.profileFetch(`/istanze/${this.codIstanza}/modulo-istruttoria`).then(
			(blob) => {
				if (blob != null) {
					var url = window.URL.createObjectURL(blob);
					var a = document.createElement('a');
					a.href = url;
					a.download = `${this.codIstanza}-valutazione.pdf`;
					document.body.appendChild(a);
					a.click();
					a.remove();
				}
			}
		)
	}

	openEffettuaValutazione() {
		this.noteValutazione = "";
		this.openValutazioneDialog = true;
	}

	confermaEffettuaValutazione(approvata: boolean) {
		// if (!approvata && this.noteValutazione.length == 0) {
		// 	alert("Per respingere un'istanza è necessario inserire una nota");
		// 	return;
		// }
		const dataToSend = {
			esito: approvata,
			noteValutazione: this.noteValutazione
		};


		console.log("Invia valutazione", dataToSend);

		this.sessionManager.profileCsrsFetch(
			`/istanze/${this.codIstanza}/valuta`,
			{
				method: "PUT",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify(dataToSend),
				// credentials: 'include' //TODO: serve ?
			}
		).then(
			(results) => {
				console.log(results);
				alert(`L'istanza ${this.codIstanza} è stata ${approvata ? 'approvata' : 'respinta'}`);
				this.openValutazioneDialog = false;
				this.caricaDati(results);
			}
		);
	}

	annullaValutazione() {
		this.openValutazioneDialog = false;
	}

	private refreshEnabledButtons() {
		this.checkErroriDatiIstr();

		const delConsegnati = this.arrIdConsegnatiIniziale.find(
			(x: any) => !this.datiFormConsegnati.caricati.map((c:any) => c.idRichiesta).includes(x)
		);
		const newConsegnati = this.datiFormConsegnati.caricati.find(
			(x: any) => !this.arrIdConsegnatiIniziale.includes(x.idRichiesta)
		);
		const delRichiesti = this.arrIdRichiestiIniziale.find(
			(x: any) => !this.datiFormRichiesti.richieste.map((c:any) => c.idRichiesta).includes(x)
		);
		const newRichiesti = this.datiFormRichiesti.richieste.find((x: any) => x.idRichiesta == undefined);

		const newRichiestiCaricati = this.datiFormRichiesti.richieste.find((x: any) => (x.allegato != undefined && x.allegato.length > 0)) != undefined;

		let areAllFieldsOK = false;
		if (this.isPubblicaAmministrazione()) {
			const emptyTipoDocumento = this.datiFormRichiesti.richieste.filter((richiesta: { tipoDocumento: string; categoria: string; }) => {
				return richiesta.tipoDocumento.length == 0 || richiesta.categoria.length == 0;
			});
			areAllFieldsOK = emptyTipoDocumento.length == 0;
		}
		else {
			const missingAllegati = this.datiFormRichiesti.richieste.filter((richiesta: { allegato: any[]; }) => {
				return richiesta.allegato == undefined || richiesta.allegato.length == 0;
			});
			areAllFieldsOK = missingAllegati.length == 0;
		}

		this.isSalvaEnabled = !this.isSalvato
			Object.entries(this.errori).length == 0
			&& areAllFieldsOK // sono stati caricati tutti gli allegati
			&& (delConsegnati || newConsegnati || delRichiesti || newRichiesti || newRichiestiCaricati);  // non sono cambiati ne i documenti richiesti, ne quelli consegnati

		this.isValutazioneEnabled = this.isSalvato
			&& areAllFieldsOK // i tipi documento sono tutti compilati
			&& !(delConsegnati || newConsegnati || delRichiesti || newRichiesti)
			&& this.datiFormRichiesti.richieste.length == 0; // non ci sono documenti richiesti in pending
	}
}
