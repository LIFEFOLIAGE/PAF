
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BaseAuthService } from '../services/auth.service';
import { LoggingService } from '../services/logging.service';
import { DataFormat } from 'src/app/modules/table/table.component';
import { SessionManagerService } from '../services/session-manager.service';
import { Ambito } from '../selettore-ambito/selettore-ambito.component';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { environment } from 'src/environments/environment';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';



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
		"Istanze"
	);
}

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
	"Le mie istanze"
);



@Component({
	selector: 'app-ricerca-istanze',
	templateUrl: './ricerca-istanze.component.html',
	styleUrls: ['./ricerca-istanze.component.css']
})
export class RicercaIstanzeComponent implements OnInit, OnDestroy {
	DataFormat = DataFormat;
	data: any[] = [];
	elencoStatoLavori: any[] = [];
	elencoStatiIstanza: any[] = [];
	elencoProvincie: any[] = [];
	tipiIstanza: any[] = [];
	ambito: string = '';
	userData: any = {};

	searchText: string = "";
	cfTitolare: string = "";
	cfIstruttore: string = "";
	userIstruttore: string = "";
	tipoIstanza: any = undefined;
	statoIstanza?: number = undefined;
	statoAvanzamento?: number = undefined;

	tipoAmbitoTerr: string = "";
	idAmbitoTerr: number = -1;
	ambitiTerr: string[] = [];

	selectedUserName?: string;

	constructor(
		private router: Router,
		private logger: LoggingService,
		private authService: BaseAuthService,
		private route: ActivatedRoute,
		private sessionManager: SessionManagerService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}

	ngOnDestroy(): void {
		if (this.desProf != undefined) {
			this.sessionManager.removeListener('changeProfilo', this.desProf);
		}
	}

	currProfilo: any = undefined;
	private desProf?: number = undefined;

	ngOnInit(): void {
		console.log(this.route);
		this.ambito = this.route.snapshot.data['ambito'] ?? '';

		const pCurrentProfilo: Promise<void> = this.sessionManager.getCurrProfilo().then(
			(profilo: any) => {
				this.currProfilo = profilo;
			}
		);

		this.desProf = this.sessionManager.addListener(
			'changeProfilo',
			(newProfilo: any) => {
				this.currProfilo = newProfilo;
			}
		);

		const pUserData: Promise<void> = this.authService.getUserData().then(
			(ud: any) => {
				this.userData = ud;
			}
		);

		const pListaStati: Promise<void> = this.authService.authFetch(
			'/istanze/lista-stati-istanza'
		).then(
			(results: any) => {
				this.elencoStatiIstanza = results;
			}
		);

		const pListaTipi: Promise<void> = this.authService.authFetch(
			'/istanze/lista-tipi-istanza'
		).then(
			(results: any) => {
				this.tipiIstanza = results;
			}
		);

		const pListaLavori: Promise<void> = this.authService.authFetch(
			'/istanze/lista-stato-lavori'
		).then(
			(results: any) => {
				this.elencoStatoLavori = results;
			}
		);

		const pProvince: Promise<void> = this.authService.authFetch(
			'/provincie-host'
		).then(
			(results: any) => {
				this.elencoProvincie = results;
			}
		);


		// le promise effettivamente necessarie sono solo pUserData pListaStati
		Promise.all([pCurrentProfilo, pUserData, pListaStati, pListaTipi, pListaLavori, pProvince])
			.then(() => {
					let breadcrumbModel : any;
					let titolo: any;
					switch (this.ambito) {
						case 'pubblico': {
							// necessario aspettare pUserData
							breadcrumbModel = new BreadcrumbModel(
								[
									{
										icon: 'bi bi-house',
										url: ['/']
									}
								],
								"Istanze"
							);
							titolo = 'Cruscotto istanze di taglio boschivo';
						}; break;
						case 'cruscotto-pa': {
							// necessario aspettare pUserData
							breadcrumbModel = new BreadcrumbModel(
								[
									{
										icon: 'bi bi-house',
										url: ['/']
									}
								],
								"Cruscotto P.A."
							);
							titolo = 'Cruscotto Pubblica Amministrazione';
						}; break;
						case 'amministrazione': {
							this.selectedUserName = this.route.snapshot.params['username'];
							if (this.selectedUserName) {
								breadcrumbModel = ammiUserBreadcrumb(this.selectedUserName);
							}
							titolo = 'Cruscotto amministrazione istanze';
						}; break;
						case 'account': {
							// necessario aspettare pUserData
							this.selectedUserName = this.userData.userName;
							breadcrumbModel = myBreadcrumb;
							titolo = 'Le mie istanze da valutare';
						}; break;
					}
					if (this.ambito == 'amministrazione') {
					}
					else {
						if (this.ambito == 'account') {
						}
						else {
							if (this.ambito == 'cruscotto-pa') {
							}
						}
					}
					this.breadcrumbService.breadcrumb = breadcrumbModel;
					this.titleService.title = titolo;

					if (this.selectedUserName) {
						// necessario aspettare pListaStati
						const statiIstanzaAssegnata = this.elencoStatiIstanza.filter(value => {
							return value.cod_stato == 'ISTRUTTORIA';
						})[0];

						this.statoIstanza = statiIstanzaAssegnata.id_stato;

						this.userIstruttore = this.selectedUserName;
						this.esecuzioneRicerca();
					}
				}
			);
	}

	onChangeAmbito(event: Ambito) {
		this.tipoAmbitoTerr = event.tipo;
		this.idAmbitoTerr = event.value;
	}

	onChangeTipoIstanza(event: any) {
		console.log(event);
		if (event) {
			this.ambitiTerr = [event.tipo_ente];
		}
		else {
			this.ambitiTerr = [];
		}
	}

	//readonly undef: undefined = undefined;
	esecuzioneRicerca() {
		const parametriRicerca: any = {
			tipoIstanza: this.tipoIstanza?.id_cist,
			statoIstanza: this.statoIstanza,
			statoAvanzamento: this.statoIstanza == 4 ? this.statoAvanzamento : undefined,
			testo: this.searchText,
			codFiscaleTitolare: this.cfTitolare,
			codFiscaleIstruttore: this.cfIstruttore,
			usernameIstruttore: this.userIstruttore
		};
		if (this.tipoAmbitoTerr) {
			parametriRicerca.idEnte = this.idAmbitoTerr;
		}

		const reqBody = JSON.stringify(parametriRicerca);

		const searchReq = this.sessionManager.profileCsrsFetch(
			//`/istanze/profilo/${this.currProfilo.tipo}`,
			`/istanze`,
			{
				method: 'POST',
				headers: {
					"Content-Type": "application/json",
				},
				body: reqBody
			}
		);
		searchReq.then(
			(value: any) => {
				//console.log(value);
				this.data = value;
			},
			(reason) => {
				console.error(reason);
				alert("Rilevato un errore nella ricerca");
			}
		);
	}

	dropIstanza(codIsta: string) {
		if (confirm(`Stai per eliminare l'istanza ${codIsta}!`)) {
			this.sessionManager.profileCsrsFetch(
				`/istanze/${codIsta}`,
				{
					method: 'DELETE'
				}
			).then(
				(v: any) => {
					alert('Istanza eliminata');
					this.esecuzioneRicerca();
				}
			);
		}
	}

	openIstanza(codIsta: string) {
		console.log({ openIstanza: codIsta });
		this.router.navigate(
			[codIsta, 'compila'], {
				relativeTo: this.route
			}
		);
		//this.router.navigate([`/proprietario/pratica/${codIsta}`]);
		//this.router.navigate([`/proprietario/pratica/${codIsta}`]);
	}

	nuovaIstanza() {
		this.router.navigate(['istanze', 'nuova']);
	}

	openGestioneIstanza(codIsta: string) {
		//console.log({ openGestioneIstanza: codIsta });
		if (this.ambito == "amministrazione") {
			this.router.navigate(['cruscotto-pa', codIsta]);
		}
		else {
			this.router.navigate(
				[codIsta], {
					relativeTo: this.route
				}
			);
		}
	}

	userIstruttoreChanged() {
		this.cfIstruttore = '';
	}
}
