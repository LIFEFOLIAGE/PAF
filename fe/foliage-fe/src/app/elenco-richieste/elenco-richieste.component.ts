import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataFormat } from '../modules/table/table.component';
import { Ambito } from '../selettore-ambito/selettore-ambito.component';
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
		},
	],
	"Le mie richieste"
);

const ammiBreadcrumb = new BreadcrumbModel(
	[
		{
			icon: 'bi bi-house',
			url: ['/']
		},
		{
			label: "Amministrazione",
			url: ["amministrazione"]
		},
	],
	"Richieste"
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
		"Richieste"
	);
}

@Component({
	selector: 'app-elenco-richieste',
	templateUrl: './elenco-richieste.component.html',
	styleUrls: ['./elenco-richieste.component.css']
})
export class ElencoRichiesteComponent implements OnInit{
	constructor(
		private router : Router,
		private route: ActivatedRoute,
		private authService: BaseAuthService,
		private sessionManager: SessionManagerService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}

	
	profiloSelezionato: number = -1;
	elencoProfili: any[] = [];
	codiceFiscale: string = "";
	username: string = "";
	
	tipoAmbitoTerr: string = "";
	idAmbitoTerr: number = -1;






	fixedUser?: string;
	currProfilo?: string;
	canCreateCancel: boolean = false;
	richieste: any[] = [];
	

	//backendCall: string = "";
	urlPrefix: string[] = [];
	readonly DataFormat = DataFormat;

	ngOnInit(): void {
		this.authService.authFetch('/utenze/profili-richiesta').then(
			(profili: any[]) => {
				this.elencoProfili = profili;
			}
		);
		let sub1: any = undefined;
		let sub2: any = undefined;
		const p1 = new Promise(
			(resolve, reject) => {
				sub1 = this.route.params.subscribe(
					{
						next: (par) => {
							this.fixedUser = par['username'];
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
				if (this.fixedUser == undefined) {
					if (this.currProfilo) {
						this.breadcrumbService.breadcrumb = ammiBreadcrumb;
						this.titleService.title = "Richieste Di Abilitazione";

						this.urlPrefix = ["amministrazione", "richieste"];
						//this.backendCall = `/utenze/richieste`;
						// this.authService.authFetch(this.backendCall).then(
						// 	(res: any[]) => {
						// 		this.richieste = res;
						// 	}
						// );
					}
					else {
						
						this.breadcrumbService.breadcrumb = myBreadcrumb;
						this.titleService.title = "Le Mie Richieste Di Abilitazione";
						this.urlPrefix = ["account", "richieste"];
						this.canCreateCancel = true;
						this.authService.authFetch('/corrente/richieste').then(
							(res: any[]) => {
								this.richieste = res;
							}
						);
					}
				}
				else {
					if (this.currProfilo) {
						this.breadcrumbService.breadcrumb = ammiUserBreadcrumb(this.fixedUser);
						this.titleService.title = `Richieste Di Abilitazione per ${this.fixedUser}`;

						this.urlPrefix = ["amministrazione", "utenti", this.fixedUser, "richieste"];
						this.sessionManager.profileFetch(`/utenze/utente/${this.fixedUser}/richieste`).then(
							(res: any[]) => {
								this.richieste = res;
							}
						);
					}
				}
				sub1.unsubscribe();
				sub2.unsubscribe();
			}
		);
	}
	
	onChangeAmbito(event: Ambito) {
		//console.log({event});
		this.tipoAmbitoTerr = event.tipo;
		this.idAmbitoTerr = event.value;
	}

	nuovaRichiesta() {
		if (this.currProfilo == undefined) {
			this.router.navigate(['account', 'richiesta-profilo']);
		}
	};
	
	openRichiesta(idRichiesta: number) {
		console.log(idRichiesta);
		this.router.navigate([...this.urlPrefix, idRichiesta]);
		//this.router.navigate(['amministrazione', 'utenti','richieste', idRichiesta]);
	}


	eliminaRichiesta(idRichiesta: number) {
		if (this.currProfilo == undefined) {
			if (confirm(`Vuoi eliminare la richiesta numero ${idRichiesta}`)) {
				this.authService.csrsFetch(
						`/corrente/richieste/${idRichiesta}`,
						{
							method: 'DELETE'
						}
					).then(
						() => {

							alert(`La richiesta ${idRichiesta} è stata annullata`);
							//location.reload();
							ngReloadComponent(this.router);
						},
						() => {
							alert("È stato rilevato un problema durante l'annullamento");
							//location.reload();
							ngReloadComponent(this.router);
						}
					);
			}
		}
	}
	
	ricerca() {
		const idProfilo = (this.profiloSelezionato == -1) ? undefined : this.profiloSelezionato;
		//const ambitoTerritoriale = this.
		const reqBody = {
			ambitoTerritoriale: this.tipoAmbitoTerr,
			codiceEnteTerritoriale: this.idAmbitoTerr,
			idProfilo,
			codiceFiscale: this.codiceFiscale,
			username: this.username
		};
		this.sessionManager.profileCsrsFetch(
			`/utenze/richieste`,
			{
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify(reqBody)
			}
		).then(
			res => {
				this.richieste = res;
			}
		);
	}

}
