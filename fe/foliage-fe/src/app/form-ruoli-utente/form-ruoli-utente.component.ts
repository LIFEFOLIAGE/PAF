import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RowData } from '../modules/table/table.component';
import { BaseAuthService } from '../services/auth.service';
import { SessionManagerService } from '../services/session-manager.service';
import { ngReloadComponent } from '../services/utils';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

const myBreadcrumb = new BreadcrumbModel(
	[
		{
			icon: 'bi bi-house',
			url: ['/']
		},
		{
			label: "Account",
			url: ["account"]
		}
	],
	"I miei Ruoli"
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
			},
			{
				label: username,
				url: ["amministrazione", "utenti", username]
			}
		],
		`Ruoli`
	);
}

@Component({
	selector: 'app-form-ruoli-utente',
	templateUrl: './form-ruoli-utente.component.html',
	styleUrls: ['./form-ruoli-utente.component.css']
})
export class FormRuoliUtenteComponent implements OnInit {
	constructor(
		private router : Router,
		private route: ActivatedRoute,
		private authService: BaseAuthService,
		private sessionManager: SessionManagerService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}
	username?: string;
	currProfilo?: string;
	idRouloPredefinito?: number = 6;
	ruoloSelezionato: any = undefined;
	nomeRuoloSelezionato?: string = undefined;
	elencoEnti?: any[] = undefined;
	elencoRuoli: any[] = [];
	idEnteInDisassociazione?: number = undefined;
	noteRevoca: string = "";
	ngOnInit(): void {
		let sub1: any = undefined;
		let sub2: any = undefined;
		
		const p1 = new Promise(
			(resolve, reject) => {
				sub1 = this.route.params.subscribe(
					{
						next: (par) => {
							this.username = par["username"];
							
							resolve(undefined);
						},
						error: (error) => {
							reject(error);
						}
					}
				);
			}
		);

		const p2 = new Promise(
			(resolve, reject) => {
				sub2 = this.route.data.subscribe(
					{
						next: (data) => {
							
							this.currProfilo = data["profilo"];
							console.log(this.currProfilo);
							resolve(undefined);
						},
						error: (error) => {
							reject(error);
						}
					}
					
				);
			}
		);


		
		Promise.all([p1, p2]).then(
			(res: any[]) => {

				if (this.username == undefined) {
					this.breadcrumbService.breadcrumb = myBreadcrumb;
					this.titleService.title = `I miei Ruoli`;
				}
				else {
					this.breadcrumbService.breadcrumb = ammiUserBreadcrumb(this.username);
					this.titleService.title = `Ruoli di ${this.username}`;
				}
				this.apriRuoli();
			}
		);
	}
	
	apriRuoli() {
		this.elencoRuoli = []; 
		this.elencoEnti = [];
		this.ruoloSelezionato = undefined;
		if (this.username == undefined) {
			this.sessionManager.logout();
			//TODO: va sostituito con il profilo predefinito del DB non quello attuale
			this.authService.getUserData().then(
				(res: any) => {
					this.idRouloPredefinito = res.rouloPredefinito;
				}
			);
			this.sessionManager.getProfiliUtente().then(
				(res: any) => {
					this.elencoRuoli = res;
				}
			);
		}
		else {
			this.sessionManager.profileFetch(`/utenze/utente/${this.username}/ruoli`).then(
				(res: any) => {
					this.elencoRuoli = res;
				}
			);
			this.sessionManager.profileFetch(`/utenze/utente/${this.username}`).then(
				(res: any) => {
					this.idRouloPredefinito = res.rouloPredefinito;
				}
			);
		}
	}
	setPredefinito(idPredefinito: number) {
		this.idRouloPredefinito = idPredefinito;
	}
	onRoleSelection(newSel: (RowData|undefined)) {
		console.log(newSel);
		if (newSel) {
			this.elencoEnti = undefined;
			this.ruoloSelezionato = newSel.data;
			this.nomeRuoloSelezionato = newSel.data.descrizione;

			const req = (this.username == undefined)
				? this.authService.authFetch(`/corrente/ruolo/${newSel.data.idProfilo}/enti`)
				: this.sessionManager.profileFetch(`/utenze/utente/${this.username}/ruolo/${newSel.data.idProfilo}/enti`);



			req.then(
				(res: any[]) => {
					this.elencoEnti = res;
				}
			);
		}
		else {
			this.elencoEnti = undefined;
			this.nomeRuoloSelezionato = undefined;
		}

	}
	eliminaEnte(idEnte: number) {
		this.noteRevoca = "";
		this.idEnteInDisassociazione = idEnte;
	}
	annullaRevoca() {
		this.idEnteInDisassociazione = undefined;
	}
	confermaRevoca() {
		if (this.noteRevoca != "") {
			this.sessionManager.profileFetch(
					`/utenze/utente/${this.username}/ruolo/${this.ruoloSelezionato.idProfilo}/${this.idEnteInDisassociazione}`,
					{
						method: 'DELETE',
						body: this.noteRevoca
					}
				)
				.then(
					(x) => {
						alert("Revoca effettuata");
						this.idEnteInDisassociazione = undefined;
						this.apriRuoli();
					},
					(e) => {
						console.error(e);
					}
				);
		}
		else {
			alert("Occorre specificare una nota");
		}
	}
	setRuoloPredefinito(ev : Event, ruolo: any) {
		console.log(ev);
		if (confirm(`Stai cambiando il ruolo predefinito in "${ruolo.descrizione}"`)) {
			console.log(ruolo.idProfilo);
			this.authService.csrsFetch(
					`/corrente/default/${ruolo.idProfilo}`,
					{
						method: 'POST'
					}
				).then(
					() => {
						//window.location.reload();
						ngReloadComponent(this.router);
					}
				);
		}
		ev.stopPropagation();
	}
}
