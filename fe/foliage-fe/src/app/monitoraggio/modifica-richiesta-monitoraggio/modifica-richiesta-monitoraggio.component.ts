import { Component, OnInit } from "@angular/core";
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { ActivatedRoute, Router } from "@angular/router";
import { BaseAuthService } from "../../services/auth.service";
import { SessionManagerService } from "../../services/session-manager.service";
import { ChronoUnit, LocalDateTime } from "@js-joda/core";
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-modifica-richiesta-monitoraggio',
	templateUrl: './modifica-richiesta-monitoraggio.component.html'
})
export class ModificaRichiestaMonitoraggioComponent implements OnInit {
	idRichiesta?: number;
	titolo: string = "";

	datiSchedulazione: any = {};
	errori: any = {}
	constructor(
		private router: Router,
		private authService: BaseAuthService,
		private route: ActivatedRoute,
		private sessionManager: SessionManagerService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}
	ngOnInit(): void {
		this.idRichiesta = this.route.snapshot.params['idRichiesta'];
		
		const root = {
			label: "Monitoraggio Satellitare",
			url: ["monitoraggio", "richieste"]
		};
		if (this.idRichiesta) {
			this.loadRichiesta();
			
			this.titolo = `Modifica Richiesta ${this.idRichiesta}`;
			const breadcrumbModel = new BreadcrumbModel(
				[
					{
						icon: 'bi bi-house',
						url: ['/']
					},
					root,
					{
						label: `Richiesta ${this.idRichiesta.toString()}`,
						url: ["monitoraggio", "richieste", this.idRichiesta.toString()]
					}
				],
				"Modifica"
			);
			this.breadcrumbService.breadcrumb = breadcrumbModel;
			this.titleService.title = `Modifica Richiesta Monitoraggio ${this.idRichiesta}`;
		}
		else {
			this.titolo = "Nuova Richiesta Monitoraggio";
			const breadcrumbModel = new BreadcrumbModel(
				[
					{
						icon: 'bi bi-house',
						url: ['/']
					},
					root
				],
				"Nuova Richiesta"
			);
			this.breadcrumbService.breadcrumb = breadcrumbModel;
			this.titleService.title = `Nuova Richiesta Monitoraggio`;

			this.datiSchedulazione.dataAvvioRichiesta = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
		}
	}

	vaiAlleRichieste() {
		this.router.navigate(["monitoraggio", "richieste"]);
	}
	elimina() {
		if (confirm("Vuoi eliminare questa richiesta?")) {
			this.sessionManager.profileCsrsFetch(
				`/monitoraggio/${this.idRichiesta}`,
				{
					method: 'DELETE'
				}
			).then(
				(res: any) => {
					if (res) {
						alert("Richiesta eliminata");
						this.vaiAlleRichieste();
					}
					else {
						alert("Non è stato possibile rimuovere questa richiesta!")
					}
				}
			);
		}
	}
	// modifica() {
	// 	this.canEdit = true;
	// 	this.router.navigate(
	// 		["modifica"],
	// 		{
	// 			relativeTo: this.route
	// 		}
	// 	);
	// }
	salva() {
		const body = JSON.stringify(
			{
				idRichiesta: this.idRichiesta,
				datiSchedulazione: this.datiSchedulazione
			}
		);

		this.sessionManager.profileCsrsFetch(
			`/monitoraggio`,
			{
				method: 'PUT',
				headers: {
					"Content-Type": "application/json",
				},
				body: body
			}
		).then(
			(res: any) => {
				if (res) {
					alert("Richiesta salvata");
					this.vaiAlleRichieste();
				}
				else {
					alert("Non è stato possibile rimuovere questa richiesta!")
				}
			}
		);
	}
	loadRichiesta() {
		if (this.idRichiesta) {
			this.sessionManager.profileFetch(`/monitoraggio/${this.idRichiesta}`).then(
				(res: any) => {
					this.datiSchedulazione = res.datiSchedulazione;
				},
				(e: any) => {
					this.vaiAlleRichieste();
				}
			);
		}
		else {
			alert();
		}
	}
}