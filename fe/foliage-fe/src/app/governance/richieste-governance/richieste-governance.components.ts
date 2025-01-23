import {Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BaseAuthService } from 'src/app/services/auth.service';
import { SessionManagerService } from 'src/app/services/session-manager.service';
import { DataFormat } from "../../modules/table/table.component";
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-richieste-governance',
	templateUrl: './richieste-governance.components.html'
  })
export class RichiesteGovernanceComponent implements OnInit {
	DataFormat = DataFormat;
	elenco: any[] = [];
	
	constructor(
		private sessionManager: SessionManagerService,
		private router: Router,
		private authService: BaseAuthService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}
	ngOnInit(): void {
		this.refresh();
		const breadcrumbModel = new BreadcrumbModel(
			[
				{
					icon: 'bi bi-house',
					url: ['/']
				},
				{
					label: "Governance",
					url: ["governance"]
				}
			],
			"Richieste"
		);
		
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = "Richieste Elaborazioni Governance";
	}
	refresh() {
		this.sessionManager.profileFetch('/governance').then(
			(res?: any[]) => {
				this.elenco = res??[];
			}
		);
	}
	openRichiesta(id: number) {
		console.log({id});
		this.router.navigate(['governance', 'richieste', id]);
	}
	nuovaRichiesta() {
		this.router.navigate(['governance', 'richieste', 'nuova']);
	}
}