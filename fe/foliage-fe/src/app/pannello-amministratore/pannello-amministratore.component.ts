import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { SessionManagerService } from '../services/session-manager.service';
import { BreadcrumbService } from '../services/breadcrumb.service';
import { TitleService } from '../services/title.service';

const myBreadcrumb = new BreadcrumbModel(
	[
		{
			icon: 'bi bi-house',
			url: ['/']
		}
	],
	"Amministrazione"
);

@Component({
	selector: 'app-pannello-amministratore',
	templateUrl: './pannello-amministratore.component.html',
	styleUrls: ['./pannello-amministratore.component.css']
})
export class PannelloAmministratoreComponent implements OnInit {
	constructor(
		private router : Router,
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
			"Amministrazione"
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService._title = "Pannello Amministratore";
		this.sessionManager.getCurrProfilo().then(
			(prof: any) => {
				console.log(prof);
				switch(prof.authority) {
					case "AMMI":
					case "RESP": {
						this.collegamenti = [
							{
								nome: "Utenti"
							},
							{
								nome: "Richieste"
							}
						];
					}; break;
					case "DIRI": {
						this.collegamenti = [
							{
								nome: "Utenti"
							}
						];
					}; break;
					default: {
						alert("Profilo non abilitato");
					};
				}
			}
		);
	}
	collegamenti: any[] = [];
	openCollegamento(coll: any) {
		this.router.navigate(["amministrazione", coll.nome.toLowerCase()]);
	}
}
