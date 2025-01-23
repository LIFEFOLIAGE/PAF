import { Component, OnInit } from "@angular/core";
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { ActivatedRoute, Router } from "@angular/router";
import { BaseAuthService } from "../../services/auth.service";
import { SessionManagerService } from "../../services/session-manager.service";
import { ChronoUnit, DateTimeFormatter, Duration, LocalDateTime } from "@js-joda/core";
import { Locale } from "@js-joda/locale_it";
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-gestione-richiesta-monitoraggio',
	templateUrl: './gestione-richiesta-monitoraggio.component.html'
})
export class GestioneRichiestaMonitoraggioComponent implements OnInit {
	idRichiesta?: number;
	//titolo: string = "";

	datiSchedulazione: any = {};
	datiEsecuzione: any = undefined;
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
			
			//this.titolo = `Richiesta Monitoraggio ${this.idRichiesta}`;
			const breadcrumbModel = new BreadcrumbModel(
				[
					{
						icon: 'bi bi-house',
						url: ['/']
					},
					root
				],
				`Richiesta ${this.idRichiesta}`
			);
			this.breadcrumbService.breadcrumb = breadcrumbModel;
			this.titleService.title = `Gestione Richiesta Monitoraggio ${this.idRichiesta}`;
		}
		else {
			alert("Nessuna richiesta specificata");
			this.vaiAlleRichieste();
		}
	}

	vaiAlleRichieste() {
		this.router.navigate(["monitoraggio", "richieste"]);
	}
	modifica() {
		this.router.navigate(
			["modifica"],
			{
				relativeTo: this.route
			}
		);
	}
	elimina() {
		if (confirm("Vuoi eliminare questa richiesta ed i dati prodotti dall'elaborazione?")) {
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
	loadRichiesta() {
		if (this.idRichiesta) {
			this.sessionManager.profileFetch(`/monitoraggio/${this.idRichiesta}`).then(
				(res: any) => {
					this.datiSchedulazione = res.datiSchedulazione;
					
					
					
					this.datiEsecuzione = {
						dataAvvio: LocalDateTime.parse(res.datiEsecuzione.dataInizio),
						dataFine: LocalDateTime.parse(res.datiEsecuzione.dataFine)
					};
					
					this.datiEsecuzione.strDataAvvio = this.datiEsecuzione.dataAvvio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withLocale(Locale.ITALY));
					this.datiEsecuzione.durata = Duration.between(this.datiEsecuzione.dataAvvio, this.datiEsecuzione.dataFine);
					this.datiEsecuzione.strDurata = this.datiEsecuzione.durata.toString();
					this.datiEsecuzione.milliDurata = Math.trunc(this.datiEsecuzione.durata.get(ChronoUnit.NANOS)/1000000);
					this.datiEsecuzione.secDurata = this.datiEsecuzione.durata.get(ChronoUnit.SECONDS);
					
					this.datiEsecuzione.minDurata = Math.trunc(this.datiEsecuzione.durata.get(ChronoUnit.SECONDS)/60);
					this.datiEsecuzione.secDurata = this.datiEsecuzione.secDurata  % 60;

					this.datiEsecuzione.oreDurata = Math.trunc(this.datiEsecuzione.minDurata/24);
					this.datiEsecuzione.minDurata = this.datiEsecuzione.minDurata % 60;

					this.datiEsecuzione.strDurata = `${this.datiEsecuzione.oreDurata.toString().padStart(2, '0')}:${this.datiEsecuzione.minDurata.toString().padStart(2, '0')}:${this.datiEsecuzione.secDurata.toString().padStart(2, '0')}.${this.datiEsecuzione.milliDurata.toString().padStart(3, '0')}`;
				},
				(e: any) => {
					this.vaiAlleRichieste();
				}
			);
		}
		else {
			alert(`La richiesta ${this.idRichiesta} non è stata trovata`);
			this.vaiAlleRichieste();
		}
	}
}