import '@js-joda/timezone';
import {Component, OnInit} from '@angular/core';
import { BaseAuthService } from '../../services/auth.service';
import {SessionManagerService} from "../../services/session-manager.service";
import { DateTimeFormatter, LocalDateTime } from '@js-joda/core';
import { Locale } from '@js-joda/locale_it';
import { Router } from '@angular/router';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
  selector: 'app-dashboard-governance',
  templateUrl: './dashboard-governance.component.html'
})
export class DashboardGovernanceComponent implements OnInit {
	reports?: any[] = undefined;
	constructor(
		private sessionManager: SessionManagerService,
		private router: Router,
		private authService: BaseAuthService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}
	ngOnInit(): void {
		const breadcrumbModel = new BreadcrumbModel(
			[
				{
					icon: 'bi bi-house',
					url: ['/']
				},
			],
			"Governance"
		);

		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = "Cruscotto di Supporto alla Governance";
		this.sessionManager.profileFetch('/report-disponibili').then(
			(res: any[]) => {
				this.reports = res.map(
					(rep: any) => {
						const outVal: any = {...rep};
						const anni: any = {}
						rep.listaEsecuzioni.forEach(
							(ex: any) => {
								const dataRife = LocalDateTime.parse(ex.rifeTime);
								const anno = dataRife.year();
								let dataReq: any = undefined;
								if (ex.submissionTime != undefined) {
									const data = LocalDateTime.parse(ex.submissionTime);
									dataReq = data.format(DateTimeFormatter.ofPattern('d/M/yyyy')) + ' alle ' + data.format(DateTimeFormatter.ofPattern('HH:mm:ss'));
								}

								let startTime: any = undefined;
								{
									const data = LocalDateTime.parse(ex.startTime);
									startTime = data.format(DateTimeFormatter.ofPattern('d/M/yyyy')) + ' alle ' + data.format(DateTimeFormatter.ofPattern('HH:mm:ss'));
								}

								const elem = {
									dataReport: dataRife,
									dataText: dataRife.format(DateTimeFormatter.ISO_LOCAL_DATE),
									dataDesc: dataRife.format(DateTimeFormatter.ofPattern(rep.formatoDataDesc).withLocale(Locale.ITALY)),
									nomeFileConData: `${rep.nomeFile}_${dataRife.format(DateTimeFormatter.ofPattern(rep.formatoDataFile))}`,
									idExec: ex.idExec,
									dataReq,
									startTime
								}
								if (anni[anno] == undefined) {
									anni[anno] = {
										anno,
										date: [
											elem
										]
									};
								}
								else {
									anni[anno].date.push(elem)
								}
							}
						);
						outVal.anni = Object.values(anni).sort((a: any, b: any) => b.anno - a.anno);
						return outVal;
					}
				);
			}
		);
	}
	downloadReport(report: any, data: any, formato: string): void {
		console.log({report, data, formato});
		this.sessionManager.profileFetch(`/report/${report.codice}/${formato}/${data.dataText}`)
			.then(
				blob => {
					const url = window.URL.createObjectURL(blob);
					const a = document.createElement('a');
					a.style.display = 'none';
					a.href = url;
					a.download = `${data.nomeFileConData}.${formato}`;
					document.body.appendChild(a);
					a.click();
					window.URL.revokeObjectURL(url);
					a.remove();
				}
			);
	}
	
	nuovaRichiesta() {
		this.router.navigate(['governance', 'richieste', 'nuova']);
	}
	goToRichieste() {
		this.router.navigate(['governance', 'richieste']);
	}
}