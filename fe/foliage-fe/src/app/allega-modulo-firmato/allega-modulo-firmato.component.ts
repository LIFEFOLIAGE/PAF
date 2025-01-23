// import { Component, Input } from '@angular/core';
// import { ActivatedRoute, Router } from '@angular/router';
// import { BaseAuthService } from '../services/auth.service';
// import { SessionManagerService } from '../services/session-manager.service';
// import { BreadcrumbModel } from 'src/app/models/breadcrumb';
// import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
// import { TitleService } from 'src/app/services/title.service';

// @Component({
// 	selector: 'app-allega-modulo-firmato',
// 	templateUrl: './allega-modulo-firmato.component.html'
// })
// export class AllegaModuloFirmato {
// 	codIstanza: string = "";
// 	allegato: any;
// 	isReady: boolean = false;
// 	constructor(
// 		private router: Router,
// 		private route: ActivatedRoute,
// 		private authService: BaseAuthService,
// 		private sessionManager: SessionManagerService,
// 		private breadcrumbService: BreadcrumbService,
// 		private titleService: TitleService
// 	) {
// 		this.codIstanza = this.route.snapshot.params['codIstanza'];
		
// 		const breadcrumbModel = new BreadcrumbModel(
// 			[
// 				{
// 					icon: 'bi bi-house',
// 					url: ['/']
// 				},
// 				{
// 					label: 'Istanze',
// 					url: ['istanze']
// 				},
// 				{
// 					label: this.codIstanza,
// 					url: ['istanze', `${(this.codIstanza)}`]
// 				}
// 			],
// 			"Firma"
// 		);
// 		this.breadcrumbService.breadcrumb = breadcrumbModel;
// 		this.titleService.title = `Istanza ${this.codIstanza} - Firma modulo`;

// 	}
// 	ngOnInit(): void {
// 		this.sessionManager.profileFetch(`/istanze/${this.codIstanza}/modulo-istanza-firmato`).then(
// 			(res: any) => {
// 				this.allegato = res;
// 			}
// 		).finally(
// 			() => {
// 				this.isReady = true;
// 			}
// 		);
// 	}
// 	downloadPdf() {
// 		this.sessionManager.profileFetch(`/istanze/${this.codIstanza}/modulo-istanza-non-firmato`).then(
// 			(blob) => {
// 				if (blob != null) {
// 					var url = window.URL.createObjectURL(blob);
// 					var a = document.createElement('a');
// 					a.href = url;
// 					a.download = `pdfIstanza.pdf`;
// 					document.body.appendChild(a);
// 					a.click();
// 					a.remove();
// 				}
// 			}
// 		)
// 	}

// 	invioPdfFirmato() {
// 		const sendDataBody = JSON.stringify([this.allegato]);

// 		this.sessionManager.profileCsrsFetch(
// 			`/istanze/${this.codIstanza}/modulo-istanza-firmato`,
// 			{
// 				method: 'PUT',
// 				body: sendDataBody,
// 				headers: {
// 					"Content-Type": "application/json",
// 				}
// 			}
// 		).then(
// 			(res: any) => {
// 				alert(`Il moduolo firmato per l'istanza ${this.codIstanza} è stato inviato`);
// 				this.router.navigate([`/istanze/${(this.codIstanza)}`]);
// 			}/*,
// 			(err: any) => {
// 				alert("Si è verificato un problema nell'invio della richiesta");
// 			}*/
// 		);
// 	}
// }