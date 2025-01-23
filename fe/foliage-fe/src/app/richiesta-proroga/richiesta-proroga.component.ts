import { Component, OnInit } from '@angular/core';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import formProroga from './richiesta-proroga-form';
import { ActivatedRoute, Router } from "@angular/router";
import { BaseAuthService } from "../services/auth.service";
import { SessionManagerService } from '../services/session-manager.service';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-richiesta-proroga',
	templateUrl: './richiesta-proroga.component.html',
	styleUrls: ['./richiesta-proroga.component.css']
})
export class RichiestaProrogaComponent implements OnInit {
	codIstanza: string = '';
	ambito: string = '';

	formProroga: any = formProroga;
	datiForm: any = {};
	datiFormIniziale: any = {};
	canInvio: boolean = false;

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private sessionManager: SessionManagerService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}

	ngOnInit(): void {
		const routeParams = this.route.snapshot.params;
		this.codIstanza = routeParams['codIstanza'];
		this.ambito = this.route.snapshot.data["ambito"];
		const root = (this.ambito == 'pubblico' ? 'istanze' : 'cruscotto-pa');
		const rootLabel = (this.ambito == 'pubblico' ? 'Istanze' : 'Cruscotto P.A.');
		const breadcrumbModel = new BreadcrumbModel(
			[
				{
					icon: 'bi bi-house',
					url: ['/']
				},
				{
					label: rootLabel,
					url: [root]
				},
				{
					label: `Gestione ${this.codIstanza}`,
					url: [root, `${(this.codIstanza)}`]
				}
			],
			"Richiesta di proroga"
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = `Istanza ${this.codIstanza} - Richiesta di proroga`;
	}

	onDataFormChanged(changes: any) {
		this.datiForm = { ...changes };

		const durataProroga = this.datiForm.durataProroga as number
		const motivazione = this.datiForm.motivazione as string
		const bollo = this.datiForm.bollo as any[]
		this.canInvio = durataProroga != undefined && (durataProroga > 0 && durataProroga <= 12)
			&& motivazione != undefined && motivazione.length > 0
			&& bollo != undefined && bollo.length == 1
	}

	inviaProroga() {
		if (this.codIstanza && this.canInvio) {
			let sendData: any = {
				//codiceIstanza: this.codIstanza,
				...this.datiForm
			};

			const sendDataBody = JSON.stringify(sendData);

			this.sessionManager.profileCsrsFetch(
				`/istanze/${this.codIstanza}/proroga`,
				{
					method: 'POST',
					body: sendDataBody,
					headers: {
						"Content-Type": "application/json",
					}
				}
			).then(
				(res: any) => {
					alert(`La proroga ${this.codIstanza} è stata presentata`);
					this.router.navigate(
						[`/istanze/${(this.codIstanza)}`]
					);
				}
			);
		} else {
			alert("Non è stato possibile inviare la richiesta");
		}
	}
}
