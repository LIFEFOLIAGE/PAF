import { Component, OnInit } from "@angular/core";
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { ActivatedRoute, Router } from "@angular/router";
import { BaseAuthService } from "../../services/auth.service";
import { SessionManagerService } from "../../services/session-manager.service";
import { ChronoUnit, LocalDateTime } from "@js-joda/core";
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-modifica-richiesta-governance',
	templateUrl: './modifica-richiesta-governance.component.html'
})
export class ModificaRichiestaGovernanceComponent implements OnInit {
	idRichiesta?: number;
	//titolo: string = "";

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
			label: "Governance",
			url: ["governance"]
		};
		const lev1 = {
			label: "Richieste",
			url: ["governance", "richieste"]
		}
		
		if (this.idRichiesta) {
			this.loadRichiesta();
			
			//this.titolo = `Modifica Richiesta Elaborazione Governance ${this.idRichiesta}`;
			const breadcrumbModel = new BreadcrumbModel(
				[
					{
						icon: 'bi bi-house',
						url: ['/']
					},
					root, lev1,
					{
						label: this.idRichiesta.toString(),
						url: ["governance", "richieste", this.idRichiesta.toString()]
					}
				],
				"Modifica"
			);
			
			this.breadcrumbService.breadcrumb = breadcrumbModel;
			this.titleService.title = `Modifica Richiesta Elaborazione Governance ${this.idRichiesta.toString()}`;
		}
		else {
			const breadcrumbModel = new BreadcrumbModel(
				[
					{
						icon: 'bi bi-house',
						url: ['/']
					},
					root, lev1
				],
				"Nuova Richiesta"
			);	
			this.breadcrumbService.breadcrumb = breadcrumbModel;
			this.titleService.title = "Nuova Richiesta Elaborazione Governance";

			this.datiSchedulazione.dataAvvioRichiesta = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
		}
	}

	vaiAlleRichieste() {
		this.router.navigate(["governance", "richieste"]);
	}
	elimina() {
		if (confirm("Vuoi eliminare questa richiesta?")) {
			this.sessionManager.profileCsrsFetch(
				`/governance/${this.idRichiesta}`,
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
			`/governance`,
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
			this.sessionManager.profileFetch(`/governance/${this.idRichiesta}`).then(
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