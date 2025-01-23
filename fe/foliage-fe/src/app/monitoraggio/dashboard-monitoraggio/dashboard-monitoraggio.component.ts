import { Component, OnInit } from "@angular/core";
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { Router } from "@angular/router";
import { BaseAuthService } from "../../services/auth.service";
import { SessionManagerService } from "../../services/session-manager.service";
import { DataFormat } from "../../modules/table/table.component";
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-dashboard-monitoraggio',
	templateUrl: './dashboard-monitoraggio.component.html'
})
export class MonitoraggioComponent implements OnInit {
	DataFormat = DataFormat;
	elenco: any[] = [];
	
	constructor(
		private router: Router,
		private authService: BaseAuthService,
		private sessionManager: SessionManagerService,
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
				}
			],
			"Monitoraggio Satellitare"
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = `Gestione Richieste per Monitoraggio Satellitare`;

		this.refresh();
	}
	refresh() {
		this.sessionManager.profileFetch('/monitoraggio').then(
			(res?: any[]) => {
				this.elenco = res??[];
			}
		);
	}
	openRichiesta(id: number) {
		console.log({id});
		this.router.navigate(['monitoraggio', 'richieste', id]);
	}
	nuovaRichiesta() {
		this.router.navigate(['monitoraggio', 'richieste', 'nuova']);
	}
}
