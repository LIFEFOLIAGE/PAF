import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Ambito } from 'src/app/selettore-ambito/selettore-ambito.component';
import { DataFormat } from '../modules/table/table.component';
import { BaseAuthService } from '../services/auth.service';
import { SessionManagerService } from '../services/session-manager.service';
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
				label: "Amministrazione",
				url: ["amministrazione"]
			}
		],
		"Utenti"
	);

@Component({
	selector: 'app-elenco-utenti',
	templateUrl: './elenco-utenti.component.html',
	styleUrls: ['./elenco-utenti.component.css']
})
export class ElencoUtentiComponent implements OnInit{
	constructor(
		private router : Router,
		private route: ActivatedRoute,
		private authService: BaseAuthService,
		private sessionManager: SessionManagerService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}

	DataFormat = DataFormat;
	elencoUtenti: any[] = [];
	
	profiloSelezionato: number = -1;
	elencoProfili: any[] = [];
	codiceFiscale: string = "";
	username: string = "";
	
	tipoAmbitoTerr: string = "";
	idAmbitoTerr: number = -1;

	ngOnInit(): void {
		this.authService.authFetch('/utenze/profili').then(
			(profili: any[]) => {
				this.elencoProfili = profili;
			}
		);

		this.breadcrumbService.breadcrumb = myBreadcrumb;
		this.titleService.title = "Utenti";
	}


	onChangeAmbito(event: Ambito) {
		//console.log({event});
		this.tipoAmbitoTerr = event.tipo;
		this.idAmbitoTerr = event.value;
	}
	openUtente(username: string) {
		this.router.navigate(['amministrazione', 'utenti', username]);
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
