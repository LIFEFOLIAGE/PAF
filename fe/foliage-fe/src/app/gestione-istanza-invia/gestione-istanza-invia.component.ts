import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from "@angular/router";
import { BaseAuthService } from "../services/auth.service";
import { SessionManagerService } from "../services/session-manager.service";
import formInvio from './istanza-invia-form';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';


@Component({
	selector: 'app-gestione-istanza-invia',
	templateUrl: './gestione-istanza-invia.component.html',
	styleUrls: ['./gestione-istanza-invia.component.css']
})
export class GestioneIstanzaInviaComponent implements OnInit {
	codIstanza?: string;
	ambito: string = '';

	formInvio: any = formInvio;
	datiForm: any = {};
	note?: string = undefined;
	allegatoFirmaDigitale: any;
	allegatoFirmaOlografa: any;
	documentoAllegato: any;
	isModuloFirmaDigitale?: boolean = undefined;
	//canInvio: boolean = false;

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
					label: this.codIstanza,
					url: [root, `${(this.codIstanza)}`]
				}
			],
			"Invio"
		);

		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = `Istanza ${this.codIstanza} - Invio`;
	}

	// onDataFormChanged(changes: any) {
	// 	console.log("changes", { datiForm: changes });
	// 	this.datiForm = { ...this.datiForm, ...changes };
	// 	console.log("Full form", this.datiForm);
	// 	//this.canInvio = this.datiForm.bolloInvio && this.datiForm.dirittiIstruttoria;
	// }

	inviaRichiesta() {
		if (!this.datiForm.bolloInvio) {
			alert("È necessario caricare il bollo");
		}
		else {
			if (!this.datiForm.dirittiIstruttoria) {
				alert("È necessario caricare i diritti di istruttoria");
			}
			else {
				if (this.isModuloFirmaDigitale == undefined) {
					alert("È necessario indicare la modalità di firma");
				}
				else {
					let ckOk: boolean = true;
					if (this.isModuloFirmaDigitale) {
						if (!this.allegatoFirmaDigitale) {
							alert("È necessario caricare il modulo con firma digitale");
							ckOk = false;
						}
					}
					else {
						if (!this.allegatoFirmaOlografa) {
							alert("È necessario caricare il modulo con firma olografa");
							ckOk = false;
						}
						else {
							if (!this.documentoAllegato) {
								alert("È necessario caricare il documento di identità");
								ckOk = false;
							}
						}
					}
	
					if (ckOk) {
	
						let sendData: any = {
							//codiceIstanza: this.codIstanza,
							bolloInvio: [this.datiForm.bolloInvio],
							dirittiIstruttoria: [this.datiForm.dirittiIstruttoria],
							isModuloFirmaDigitale: this.isModuloFirmaDigitale,
							allegatoFirmaDigitale: this.isModuloFirmaDigitale ? [this.allegatoFirmaDigitale] : undefined,
							allegatoFirmaOlografa: this.isModuloFirmaDigitale ? undefined :[this.allegatoFirmaOlografa],
							documentoAllegato: this.isModuloFirmaDigitale ? undefined :[this.documentoAllegato]
						};
			
						const sendDataBody = JSON.stringify(sendData);
			
						console.log("call be: ", sendDataBody)
			
						this.sessionManager.profileCsrsFetch(
							`/istanze/${this.codIstanza}/invio`,
							{
								method: 'POST',
								body: sendDataBody,
								headers: {
									"Content-Type": "application/json",
								}
							}
						).then(
							(res: any) => {
								alert(`L'istanza ${this.codIstanza} è stata presentata`);
								this.router.navigate([`/istanze/${(this.codIstanza)}`]);
							},
							(err: any) => {
								alert("Si è verificato un problema nell'invio della richiesta");
							}
						);
					}
				}
			}
		}
		// if (this.codIstanza && this.datiForm.ricevute && this.datiForm.ricevute.length > 0) {
		// } else {
		// 	alert("Non è stato possibile caricare le ricevute");
		// }
	}

	canInviare(): boolean {
		return (this.isModuloFirmaDigitale)
			? (this.allegatoFirmaDigitale && this.datiForm.bolloInvio && this.datiForm.dirittiIstruttoria)
			: (this.allegatoFirmaOlografa && this.documentoAllegato && this.datiForm.bolloInvio && this.datiForm.dirittiIstruttoria)

	}
	downloadPdf() {
		this.sessionManager.profileFetch(`/istanze/${this.codIstanza}/modulo-istanza-on-fly`).then(
			(blob) => {
				if (blob != null) {
					var url = window.URL.createObjectURL(blob);
					var a = document.createElement('a');
					a.href = url;
					a.download = `${this.codIstanza}-istanza.pdf`;
					document.body.appendChild(a);
					a.click();
					a.remove();
				}
			}
		)
	}

	// invioPdfFirmato() {
	// 	const sendDataBody = JSON.stringify([this.allegato]);

	// 	this.sessionManager.profileCsrsFetch(
	// 		`/istanze/${this.codIstanza}/modulo-istanza-firmato`,
	// 		{
	// 			method: 'PUT',
	// 			body: sendDataBody,
	// 			headers: {
	// 				"Content-Type": "application/json",
	// 			}
	// 		}
	// 	).then(
	// 		(res: any) => {
	// 			alert(`Il moduolo firmato per l'istanza ${this.codIstanza} è stato inviato`);
	// 			this.router.navigate([`/istanze/${(this.codIstanza)}`]);
	// 		}/*,
	// 		(err: any) => {
	// 			alert("Si è verificato un problema nell'invio della richiesta");
	// 		}*/
	// 	);
	// }
}
