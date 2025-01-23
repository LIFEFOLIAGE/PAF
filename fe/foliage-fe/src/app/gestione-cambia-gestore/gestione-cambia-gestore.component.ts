import { Component, OnInit } from '@angular/core';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { ActivatedRoute, Router } from "@angular/router";
import { BaseAuthService } from "../services/auth.service";
import { SessionManagerService } from "../services/session-manager.service";
import { DataFormat } from '../modules/table/table.component';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-gestione-cambia-gestore',
	templateUrl: './gestione-cambia-gestore.component.html',
	styleUrls: ['./gestione-cambia-gestore.component.css']
})
export class GestioneCambiaGestoreComponent implements OnInit {
	codIstanza: string = '';
	ambito: string = '';

	breadcrumbModel?: BreadcrumbModel = undefined;
	DataFormat = DataFormat;
	elencoUtenti: any[] = [];

	profiloSelezionato: number = -1;
	elencoProfili: any[] = [];

	codiceFiscale: string = "";
	username: string = "";

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
		this.ambito = this.route.snapshot.data["ambito"];
		this.codIstanza = routeParams.get('codIstanza') ?? '';

		this.authService.authFetch('/utenze/profili').then(
			(profili: any[]) => {
				// TODO: prendiamo  i profili da BE e filtriamo solo quelli che ci servono
				// Non lo scriviamo a mano perche id e descrizione potrebbero cambiare
				this.elencoProfili = profili.filter(value => {
					return value.codProfilo == 'PROF';
				});
			}
		);
		const root = (this.ambito == 'pubblico' ? 'istanze' : 'cruscotto-pa');
		const breadcrumbModel = new BreadcrumbModel(
			[
				{
					icon: 'bi bi-house',
					url: ['/']
				},
				{
					label: "Istanze",
					url: [root]
				},
				{
					label: `Gestione ${this.codIstanza}`,
					url: [root, `${(this.codIstanza)}`]
				}
			],
			"Assegna gestore"
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = `Istanza ${this.codIstanza} - Assegnazione Gestore`;

	}

	cambiaGestore(rowData: any) {
		this.sessionManager.profileCsrsFetch(`/istanze/${this.codIstanza}/assegnaA/${rowData.id_uten}`).then(
			() => {
				alert("Cambio gestore avvenuto con successo");
				this.router.navigate(
					['..'],
					{
						relativeTo: this.route
					}
				);
			}
		);
	}


	ricerca() {
		if (this.username == "" && this.codiceFiscale == "") {
			alert("Per effettuare la ricerca devi inserire lo Username oppure il Codice fiscale");
			return;
		}

		const idProfilo = (this.profiloSelezionato == -1) ? undefined : this.profiloSelezionato;
		const reqBody = {
			idProfilo,
			codiceFiscale: this.codiceFiscale,
			username: this.username
		};

		console.log(reqBody);

		this.sessionManager.profileCsrsFetch(
			'/utenze/utenti',
			{
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify(reqBody)
			}
		).then(
			res => {
				this.elencoUtenti = res;
			}
		);
	}
}
