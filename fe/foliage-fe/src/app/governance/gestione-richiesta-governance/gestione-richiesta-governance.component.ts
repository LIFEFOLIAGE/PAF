import { Component, OnInit } from "@angular/core";
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { ActivatedRoute, Router } from "@angular/router";
import { BaseAuthService } from "../../services/auth.service";
import { SessionManagerService } from "../../services/session-manager.service";
import { ChronoUnit, DateTimeFormatter, Duration, LocalDate, LocalDateTime, convert } from "@js-joda/core";
import { Locale } from "@js-joda/locale_it";
import { HtmlService } from "src/app/services/html.service";
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-gestione-richiesta-governance',
	templateUrl: './gestione-richiesta-governance.component.html'
})
export class GestioneRichiestaGovernanceComponent implements OnInit {
	idRichiesta?: number;
	titolo: string = "";

	datiSchedulazione: any = {};
	datiEsecuzione: any;
	reportGenerati?: any[];
	errori: any = {};
	dataRife!: LocalDate;
	dataText!: string;

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
			
			this.titolo = `Richiesta Governance ${this.idRichiesta}`;
			const breadcrumbModel = new BreadcrumbModel(
				[
					{
						icon: 'bi bi-house',
						url: ['/']
					},
					root, lev1
				],
				this.idRichiesta.toString()
			);
			this.breadcrumbService.breadcrumb = breadcrumbModel;
			this.titleService.title = `Gestione Richiesta Governance ${this.idRichiesta.toString()}`;
		}
		else {
			alert("Nessuna richiesta specificata");
			this.vaiAlleRichieste();
		}
	}

	vaiAlleRichieste() {
		this.router.navigate(["governance", "richieste"]);
	}
	modifica() {
		this.router.navigate(
			["modifica"],
			{
				relativeTo: this.route
			}
		);
	}

	// salva() {
	// 	const body = JSON.stringify(
	// 		{
	// 			idRichiesta: this.idRichiesta,
	// 			datiSchedulazione: this.datiSchedulazione
	// 		}
	// 	);

	// 	this.sessionManager.profileCsrsFetch(
	// 		`/governance`,
	// 		{
	// 			method: 'PUT',
	// 			headers: {
	// 				"Content-Type": "application/json",
	// 			},
	// 			body: body
	// 		}
	// 	).then(
	// 		(res: any) => {
	// 			if (res) {
	// 				alert("Richiesta salvata");
	// 				this.vaiAlleRichieste();
	// 			}
	// 			else {
	// 				alert("Non è stato possibile rimuovere questa richiesta!")
	// 			}
	// 		}
	// 	);
	// }
	loadRichiesta() {
		if (this.idRichiesta) {
			this.sessionManager.profileFetch(`/governance/${this.idRichiesta}`).then(
				(res: any) => {
					this.datiSchedulazione = res.datiSchedulazione;
					this.dataRife = LocalDate.parse(res.datiSchedulazione.dataRife);
					this.dataText = this.dataRife.format(DateTimeFormatter.ISO_LOCAL_DATE);

					this.reportGenerati = res.reportGenerati.map(
						(rep: any) => ({
							...rep,
							dataDesc: this.dataRife.format(DateTimeFormatter.ofPattern(rep.formatoDataDesc).withLocale(Locale.ITALY)),
							nomeFileConData: `${rep.nomeFile}_${this.dataRife.format(DateTimeFormatter.ofPattern(rep.formatoDataFile))}`
						})
					);
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
					//.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withLocale(Locale.ITALY));

					console.log(this.datiEsecuzione);
					
					
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
	
	elimina() {
		if (confirm("Vuoi eliminare questa richiesta ed i dati prodotti dall'elaborazione?")) {
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

	downloadReport(report: any, data: any, formato: string): void {
		console.log({report, data, formato});
		this.sessionManager.profileFetch(`/report/${report.codice}/${formato}/${this.dataText}`)
			.then(
				blob => {
					const url = window.URL.createObjectURL(blob);
					const a = document.createElement('a');
					a.style.display = 'none';
					a.href = url;
					a.download = `${report.nomeFileConData}.${formato}`;
					document.body.appendChild(a);
					a.click();
					window.URL.revokeObjectURL(url);
					a.remove();
				}
			);
	}
}