import {Component, OnInit} from '@angular/core';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import {ActivatedRoute, Router} from "@angular/router";
import {BaseAuthService} from "../services/auth.service";
import {SessionManagerService} from "../services/session-manager.service";
import {Ambito} from "../selettore-ambito/selettore-ambito.component";
import {DataFormat} from '../modules/table/table.component';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';
// import {
//   MatSnackBar,
//   MatSnackBarHorizontalPosition,
//   MatSnackBarVerticalPosition,
// } from '@angular/material/snack-bar';
//import {take} from "rxjs";

@Component({
	selector: 'app-gestione-assegna-istruttore',
	templateUrl: './gestione-assegna-istruttore.component.html',
	styleUrls: ['./gestione-assegna-istruttore.component.css']
})
export class GestioneAssegnaIstruttoreComponent implements OnInit {
	codIstanza: string = '';
	ambito: string = '';

	// snackConfiguration = {
	//   horizontalPosition: 'center' as MatSnackBarHorizontalPosition,
	//   verticalPosition: 'top' as MatSnackBarVerticalPosition,
	//   duration: 2000,
	// };


	DataFormat = DataFormat;
	elencoUtenti: any[] = [];

	profiloSelezionato: number = -1;
	elencoProfili: any[] = [];

	codiceFiscale: string = "";
	username: string = "";

	tipoAmbitoTerr: string = "";
	idAmbitoTerr: number = -1;

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
		this.codIstanza = routeParams.get('codIstanza')??'';
		this.idAmbitoTerr = +(routeParams.get('enteDomanda') ?? -1);

		this.authService.authFetch('/utenze/profili').then(
			(profili: any[]) => {
				// TODO: prendiamo  i profili da BE e filtriamo solo l'istruttore.
				// Non lo scriviamo a mano perche id e descrizione potrebbero cmabiare
				this.elencoProfili = profili.filter(value => {
					return value.codProfilo == 'ISTR'
				});

				// Preimpostiamo il profilo selezionato a istruttore.. [0] non dovrebbe andare in nullpointer
				this.profiloSelezionato = this.elencoProfili[0].idProfilo
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
					label: this.codIstanza,
					url: [root, `${(this.codIstanza)}`]
				}
			],
			"Assegna istruttore"
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = `Istanza ${this.codIstanza} - Assegnazione istruttore`;

	}

	onChangeAmbito(event: Ambito) {
		//console.log({event});
		this.tipoAmbitoTerr = event.tipo;
		this.idAmbitoTerr = event.value;
	}

	assegnaIstruttore(rowData: any) {
		this.sessionManager.profileCsrsFetch(`/istanze/${this.codIstanza}/assegnaA/${rowData.id_uten}`).then(
			() => {
				alert("Assegnazione avvenuta con successo");
				this.router.navigate(['/cruscotto-pa', this.codIstanza]);
			}
		);
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

		console.log(reqBody)

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
