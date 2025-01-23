import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { DateTimeFormatter, LocalDateTime } from '@js-joda/core';
import { Ambito } from '../selettore-ambito/selettore-ambito.component';
import { BaseAuthService } from '../services/auth.service';
import { SessionManagerService } from '../services/session-manager.service';
import formRichiesta from './richiesta-profilo-form';
import { IstanzaComponentInterface } from "../components/interfaces/istanza-component-interface";
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
		{
			label: "Le mie richieste",
			url: ["account", "richieste"]
		}
	],
	""
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
		{
			label: "Richieste",
			url: ["amministrazione", "richieste"]
		}
	],
	""
);



function ammiUserBreadcrumb(username: string, idRichiesta: number) {
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
			},
			{
				label: "Richieste",
				url: ["amministrazione", "utenti", username, "richieste"]
			}
		],
		`${idRichiesta}`
	);
}



@Component({
	selector: 'app-form-richiesta',
	templateUrl: './form-richiesta.component.html',
	styleUrls: ['./form-richiesta.component.css']
})
export class FormRichiestaComponent implements OnInit {
	usernameRef?: string;
	username?: string;
	idRichiesta?: number;
	canApprove: boolean = false;
	noteApprovazione: string = "";
	noteRichiesta: string = "";

	isHandled: boolean = false;
	currProfilo?: string;
	urlReq?: string;
	backUrl: string[] = [];

	ruoliDict: Record<string, any> = {};

	listaCaserme: any[] = [];
	listaParchi: any[] = [];

	ruoliRichiesta: any[] = [];
	ruoloRichiesto?: number;
	public tipoAmbitoTerr?: string;
	public idEnte?: number;
	public datiRichiestaSave: any = undefined;
	//showAmbito: boolean = false;
	tipoAmbito: string = "";
	datiRichiesta: any = {};
	datiResponsabileIniziali: any = {};
	datiResponsabile: any = {};
	formRichiesta: any = formRichiesta;
	// formController: any = undefined;
	ready: boolean = false;
	hasProf: boolean = false;
	hasId: boolean = false;

	private _istanzaComponentInterface?: IstanzaComponentInterface;

	constructor(
		public router : Router,
		private route: ActivatedRoute,
		private authService: BaseAuthService,
		private sessionManager: SessionManagerService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}
	ngOnInit(): void {

		this.authService.authFetch('/utenze/lista-caserme').then(
			(res) => {
				this.listaCaserme = res;
			}
		);
		this.authService.authFetch('/utenze/lista-parchi').then(
			(res) => {
				this.listaParchi = res;
			}
		);

		this.authService.authFetch('/utenze/profili-richiesta').then(
			(res) => {
				this.ruoliRichiesta = res;
				this.ruoliDict = Object.fromEntries(res.map((x: any) => ([x.id_profilo, x])));

				let sub1: any = undefined;
				let sub2: any = undefined;

				sub1 = this.route.params.subscribe(
					{
						next: (par) => {
							this.ready = false;

							this.idRichiesta = par["idRichiesta"];
							this.username = par["username"];

							this.hasId = true;




							this.currProfilo = (this.route.data as any).value["profilo"];
							this.canApprove = (this.currProfilo == "amministratore");

							this.hasProf = true;


							this.apriRichiesta();
							// resolve(undefined);
						},
						error: (error) => {
							// reject(error);
							console.error(error);
						}
					}
				);

				// sub2 = this.route.data.subscribe(
				// 	{
				// 		next: (data) => {
				// 			this.ready = false;

				// 			this.currProfilo = data["profilo"];
				// 			this.canApprove = (this.currProfilo == "amministratore");

				// 			this.hasProf = true;
				// 			this.apriRichiesta();
				// 			// resolve(undefined);
				// 		},
				// 		error: (error) => {
				// 			// reject(error);
				// 			console.error(error);
				// 		}
				// 	}

				// );

				// const p1 = new Promise(
				// 	(resolve, reject) => {
				// 	}
				// );

				// const p2 = new Promise(
				// 	(resolve, reject) => {
				// 	}
				// );

				// Promise.all([p1, p2]).then(
				// 	(res: any[]) => {


				// 		this.apriRichiesta();


				// 	}
				// );

			}
		);


		this.sessionManager.getCurrProfilo().then(
			(data: any) => {
				console.log(data);
			}
		);
	}
	onChangeRuolo(ruolo: any) {
		console.log(ruolo);
		if (ruolo != undefined && ruolo != "" && ruolo != "undefined") {
			if (this.tipoAmbito != this.ruoliDict[ruolo].tipo_ambito) {
				this.idEnte = undefined;
			}
			this.tipoAmbito = this.ruoliDict[ruolo].tipo_ambito;

		}
		else {
			this.idEnte = undefined;
			this.tipoAmbito = "";
		}
	}
	apriRichiesta() {
		if (this.hasProf && this.hasId) {
			if (this.username == undefined) {
				if (this.currProfilo) {
					if (this.idRichiesta) {
						this.backUrl = ['amministrazione', 'richieste'];
						const breadcrumbModel = ammiBreadcrumb;
						breadcrumbModel.label = `${this.idRichiesta}`;
						
						this.breadcrumbService.breadcrumb = breadcrumbModel;
						this.titleService.title = `Richiesta ${this.idRichiesta}`;

						this.urlReq = `/utenze/richieste/${this.idRichiesta}`;
					}
					else {
						throw new Error('Nuova richiesta da amministratore');
					}
				}
				else {
					const breadcrumbModel = myBreadcrumb;
					if (this.idRichiesta) {
						breadcrumbModel.label = `${this.idRichiesta}`;
						this.urlReq = `/corrente/richieste/${this.idRichiesta}`;
						this.titleService.title = `Richiesta ${this.idRichiesta}`;
					}
					else {
						breadcrumbModel.label = 'Nuova Richiesta';
						this.titleService.title = 'Nuova Richiesta di abilitazione';
					}
					this.breadcrumbService.breadcrumb = breadcrumbModel;
				}
			}
			else {
				if (this.currProfilo) {
					if (this.idRichiesta) {
						if (this.username) {
							this.backUrl = ['amministrazione', 'utenti', this.username, 'richieste'];
							const breadcrumbModel = ammiUserBreadcrumb(this.username, this.idRichiesta);
							this.urlReq = `/utenze/utente/${this.username}/richieste/${this.idRichiesta}`;
							
							this.breadcrumbService.breadcrumb = breadcrumbModel;
							this.titleService.title = `Richiesta di abilitazione ${this.idRichiesta} per ${this.username}`;
						}
					}
					else {
						throw new Error('Nuova richiesta da amministratore');
					}
				}
			}


			if (this.urlReq) {
				this.sessionManager.profileFetch(
					this.urlReq
				).
				then(
					(res: any) => {
						this.datiRichiesta = res;
						this.datiResponsabile = res.datiResponsabile;
						this.datiResponsabileIniziali = res.datiResponsabile;
						this.ruoloRichiesto = res.ruoloRichiesto;
						this.usernameRef = res.username;
						this.noteApprovazione = res.noteApprovazione;
						this.noteRichiesta = res.noteRichiesta;
						this.idEnte = res.idEnte;
						this.isHandled = this.datiRichiesta.esitoApprovazione != undefined;
						switch (res.tipoEnte) {
							case "PARCO":
							case "CASERMA":
							{
								this.tipoAmbito = res.tipoAmbito;
							}; break;
							default: {
								this.tipoAmbito = "TERRITORIALE";
							}
						}
						this.ready = true;

					}
				);
			}
			this.hasProf = false;
			this.hasId = false;
			this.ready = true;
		}
	}
	// ngOnChanges(changes: SimpleChanges): void {
	// 	for (let propName in changes) {
	// 		const currValue = changes[propName].currentValue;
	// 		switch (propName) {
	// 			case "idRichiesta": {
	// 				this.apriRichiesta(currValue);
	// 			}; break;
	// 		}
	// 	}
	// }

	getTitle() {
		if (this.idRichiesta) {
			if (this.username) {
				return `Richiesta ${this.idRichiesta} di ${this.username}`;
			}
			else {
				return `Richiesta ${this.idRichiesta}`;
			}
		}
		else {
			return "Nuova Richiesta Abilitazione Ruolo";
		}
	}
	goTo(dest: string[]) {
		this.router.navigate(dest);
	}
	onChangeAmbito(event: Ambito) {
		console.log({ambito: event});
		this.tipoAmbitoTerr = event.tipo;
		this.idEnte = event.value;
	}

	onDataRichiestaChanged(changes: any) {
		console.log({richiesta: changes});

		this.datiResponsabile = { ...this.datiResponsabile, ...changes};

		// if (changes.ruoloRichiesto) {
		// 	this.showAmbito = changes.ruoloRichiesto.flag_territoriale;
		// }


		// this.objChanges = changes;
		// this.dataChanged.emit(changes);
	}
	onFormRichiestaInit($event: any) {
		this.datiRichiestaSave = this.datiRichiesta;
		// this.formController = formController;
		this._istanzaComponentInterface= $event;
	}

	inviaRichiesta() {
		if (!this.idRichiesta) {
			if (this.ruoloRichiesto) {
				const needEnte =  ['TERRITORIALE', 'CASERMA', 'PARCO'].includes(this.tipoAmbito);
				if (this.idEnte != -1 && needEnte) {

					let sendData : any = {
						ruoloRichiesto: this.ruoloRichiesto,
						noteRichiesta: this.noteRichiesta
					};
					if (needEnte) {
						sendData.idEnte = this.idEnte;
					}

					if (this.ruoliDict[this.ruoloRichiesto].tipo_auth == 'RESP') {
						sendData.datiResponsabile = this.datiResponsabile;
					}

					//sendData.ruoloRichiesto = sendData.ruoloRichiesto.id_profilo;

					const sendDataBody = JSON.stringify(sendData);


					this.authService.csrsFetch(
						'/corrente/nuova-richiesta-profilo',
						{
							method: 'POST',
							body: sendDataBody,
							headers: {
								"Content-Type": "application/json",
								}
						}
					).then(
						(res: any) => {
							alert("La richiesta è stata inviata");
							this.router.navigate(["account", "richieste"]);
						},
						(err: any) => {
							alert("Si è verificato un problema nell'invio della richiesta");
						}
					);
				}
				else {
					alert("Occorre specificare l'ente");
				}
			}
			else {
				alert("Occorre specificare il ruolo richiesto");
			}
		}
	}

	valutaRichiesta(modo: boolean) {
		if (this.currProfilo && this.idRichiesta) {
			if (!modo && (this.noteApprovazione==undefined || this.noteApprovazione == "")) {
				alert("Occorre specificare una motivazione per il respingimento!");
			}
			else {
				const sendData = {
					esito: modo,
					note: this.noteApprovazione
				};
				const sendDataBody = JSON.stringify(sendData);

				this.sessionManager.profileCsrsFetch(
					`/utenze/richieste/${this.idRichiesta}`,
					{
						method: 'POST',
						body: sendDataBody,
						headers: {
							"Content-Type": "application/json",
							}
					}
				).then(
					(res: any) => {
						alert(`La richiesta ${this.idRichiesta} è stata ${(modo ? "approvata": "respinta")}`);
						this.router.navigate(this.backUrl);
					},
					(err: any) => {
						alert("Si è verificato un problema durante la gestione della richiesta");
					}
				);
			}
		}
	}
	formatDateTime(value: string) {
		if (value == undefined) {
			return undefined;
		}
		else {
			let dt : (LocalDateTime | undefined) = undefined;
			dt = LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			if (dt) {
				return dt.format(DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm:ss'));;
			}
			else {
				return undefined;
			}
		}
	}
}
