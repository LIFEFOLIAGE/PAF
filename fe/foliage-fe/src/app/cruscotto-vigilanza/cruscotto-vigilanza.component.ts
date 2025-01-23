import { Component, OnDestroy, OnInit } from '@angular/core';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { ActivatedRoute, Router } from "@angular/router";
import { LoggingService } from "../services/logging.service";
import { BaseAuthService } from "../services/auth.service";
import { SessionManagerService } from "../services/session-manager.service";
import { Ambito } from "../selettore-ambito/selettore-ambito.component";
import { DataFormat } from 'src/app/modules/table/table.component';
import { DateTimeFormatter, LocalDate } from "@js-joda/core";
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-cruscotto-vigilanza',
	templateUrl: './cruscotto-vigilanza.component.html',
	styleUrls: ['./cruscotto-vigilanza.component.css']
})
export class CruscottoVigilanzaComponent implements OnInit, OnDestroy {
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

	dataApprovazioneInizio?: string;
	maxDataApprovazioneInizio?: string;

	dataApprovazioneFine?: string;
	minDataApprovazioneFine?: string;

	terminePeriodoAttivitaInizio?: string;
	maxTerminePeriodoAttivitaInizio?: string;

	terminePeriodoAttivitaFine?: string;
	minTerminePeriodoAttivitaFine?: string;



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
		const breadcrumbModel = new BreadcrumbModel(
			[
				{
					icon: 'bi bi-house',
					url: ['/']
				}
			],
			"Vigilanza"
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = "Cruscotto di vigilanza";

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

	onChangeDate($event: any, name: string) {
		switch (name) {
			case "dataApprovazioneInizio": {
				this.minDataApprovazioneFine = this.dataApprovazioneInizio;
			}
				break;
			case "dataApprovazioneFine": {
				this.maxDataApprovazioneInizio = this.dataApprovazioneFine;
			}
				break;
			case "terminePeriodoAttivitaInizio": {
				this.minTerminePeriodoAttivitaFine = this.terminePeriodoAttivitaInizio;
			}
				break;
			case "terminePeriodoAttivitaFine": {
				this.maxTerminePeriodoAttivitaInizio = this.terminePeriodoAttivitaFine;
			}
				break;
		}
	}

	formatDateForBE(date: string): string {
		return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
			.format(DateTimeFormatter.ISO_LOCAL_DATE);
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

		if (this.dataApprovazioneInizio) {
			parametriRicerca.approvazioneDa = this.formatDateForBE(this.dataApprovazioneInizio);
		}
		if (this.dataApprovazioneFine) {
			parametriRicerca.approvazioneA = this.formatDateForBE(this.dataApprovazioneFine);
		}
		if (this.terminePeriodoAttivitaInizio) {
			parametriRicerca.validitaDa = this.formatDateForBE(this.terminePeriodoAttivitaInizio);
		}
		if (this.terminePeriodoAttivitaFine) {
			parametriRicerca.validitaA = this.formatDateForBE(this.terminePeriodoAttivitaFine);
		}

		const reqBody = JSON.stringify(parametriRicerca);

		const searchReq = this.sessionManager.profileCsrsFetch(
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
