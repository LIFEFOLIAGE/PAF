import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DateTimeFormatter, LocalDate } from '@js-joda/core';
import { TipoDatiScheda } from '../components/shared/editor-scheda/editor-scheda.component';
import { BaseAuthService } from '../services/auth.service';
import { SessionManagerService } from '../services/session-manager.service';
import form from './formUtente.js';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

const myBreadcrumb = new BreadcrumbModel(
	[
		{
			icon: 'bi bi-house',
			url: ['/']
		}
	],
	"Account"
);


function ammiUserBreadcrumb(username: string) {
	return new BreadcrumbModel(
		[
			{
				icon: 'bi bi-house',
				url: ['/']
			},
			{
				label: "Amministrazione",
				url: ["amministrazione"]
			},
			{
				label: "Utenti",
				url: ["amministrazione", "utenti"]
			}
		],
		username
	);
}

@Component({
	selector: 'app-form-utente',
	templateUrl: './form-utente.component.html',
	styleUrls: ['./form-utente.component.css']
})
export class FormUtenteComponent implements OnInit {
	constructor(
		private router : Router,
		private route: ActivatedRoute,
		private authService: BaseAuthService,
		private sessionManager: SessionManagerService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}
	isReadOnly: boolean = true;
	username?: string;
	currProfilo?: string;
	userData: any;
	form: any = form;
	tipoDatiScheda = TipoDatiScheda.Object;
	currLoc?: string[];
	formDict: Record<string, any> = {};

	stringRuoli = "Ruoli";
	stringRichieste = "Richieste";
	stringIstanze = "Istanze";
	sezioni: any[] = [];
	assegnazione(data: any) {
		this.userData = data;
		if (!this.userData.autocertificazioneProf) {
			this.userData.autocertificazioneProf = {};
		}
	}
	ngOnInit(): void {
		let sub1: any = undefined;
		let sub2: any = undefined;
		
		sub1 = this.route.params.subscribe(
			{
				next: (par) => {
					this.username = par["username"];
					// resolve(undefined);
					this.apriProfilo();
				},
				error: (error) => {
					// reject(error);
					console.error(error);
				}
			}
		);
		
		sub2 = this.route.data.subscribe(
			{
				next: (data) => {
					this.currProfilo = data["profilo"];
					// resolve(undefined);
					this.apriProfilo();
				},
				error: (error) => {
					// reject(error);
					console.error(error);
				}
			}
		);
	}
	onDataProfiloConfirmedBound = this.onDataProfiloConfirmed.bind(this);
	onDataProfiloConfirmed(evento: any) {
		let modifiche = {...evento.modifiche};
		if (modifiche.dataNascita != undefined) {

			modifiche.dataNascita = LocalDate.parse(modifiche.dataNascita, DateTimeFormatter.ISO_ZONED_DATE_TIME);
		}
		let dati = evento.dati;
		let isValid = evento.isValid;
		console.log(evento);

		

		let confirmedChanges = JSON.stringify(modifiche);
		return  this.authService.csrsFetch(
			`/corrente`,
			{
				method: "PUT",
				body: confirmedChanges,
				credentials: 'include'
			}
		).then(
			() => {
				this.authService.clearUserData();
				this.sessionManager.logout();
				this.sessionManager.updateProfiliUtente().then(
					(x) => {
						this.apriProfilo();
						alert("Profilo aggiornato");
					}
				)
			}
		).catch(
			(e) => {
				alert(`Errore: ${e.message}`);
				return Promise.reject();
			}
		);
	}
	onDataProfiloChangedBound = this.onDataProfiloChanged.bind(this);
	onDataProfiloChanged(evento: any) {
		//console.log(evento);
		//this.hasChanges = evento && (Object.keys(evento).length > 0);
		return Promise.resolve();
	}
	apriProfilo() {
		if (this.username == undefined) {
			if (this.currProfilo) {
				throw new Error('Proprio profilo sotto amministratore');
			}
			else {
				const breadcrumbModel = myBreadcrumb;
				this.breadcrumbService.breadcrumb = breadcrumbModel;
				this.titleService.title = `Il Mio Profilo`;
				this.currLoc = ['account'];
				this.isReadOnly = false;
				//this.authService.loadUserData(this.assegnazione.bind(this));
				this.authService.getUserData().then(this.assegnazione.bind(this));
				this.sessionManager.getCurrProfilo().then(
					(p) => {
						console.log("Il mio profilo");
						console.log(p);
						if (["DIRI", "ISTR"].includes(p.tipo)) {
							this.sezioni = [
								{
									nome: this.stringRuoli
								},
								{
									nome: this.stringRichieste
								},
								{
									nome: this.stringIstanze
								}
							];
						}
						else {
							this.sezioni = [
								{
									nome: this.stringRuoli
								},
								{
									nome: this.stringRichieste
								}
							];
						}
					}
				);
			}
		}
		else {
			if (this.currProfilo) {
				if (this.username) {
					const breadcrumbModel = ammiUserBreadcrumb(this.username);
					
					this.breadcrumbService.breadcrumb = breadcrumbModel;
					this.titleService.title = `Profilo di ${this.username}`;
					this.currLoc = ['amministrazione', 'utenti', this.username];
					this.sessionManager.profileFetch(`/utenze/utente/${this.username}`).then(this.assegnazione.bind(this));
					this.sessionManager.getCurrProfilo().then(
						(profData: any) => {
							switch (profData.authority) {
								case "AMMI":
								case "RESP": {
									this.sezioni = [
										{
											nome: this.stringRuoli
										},
										{
											nome: this.stringRichieste
										},
										{
											nome: this.stringIstanze
										}
									];
								}; break;
								case "DIRI": {
									this.sezioni = [
										{
											nome: this.stringIstanze
										}
									];
								}; break;
							}
						}
					);
				}
				else {
					throw new Error("Solo l'amministratore può controllare gli utenti");
				}
			}
		}
	}
	goTo(sezione: string) {
		if (this.currLoc) {
			const next = sezione.toLowerCase();
			//this.router.navigate([...this.currLoc, next]);
			this.router.navigate(
				[next],
				{
					relativeTo: this.route
				}
			);
		}
	}
}
