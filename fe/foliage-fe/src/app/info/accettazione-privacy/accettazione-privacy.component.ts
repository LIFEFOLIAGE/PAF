import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { BaseAuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-accettazione-privacy',
	templateUrl: './accettazione-privacy.component.html',
	styleUrls: ['./accettazione-privacy.component.css']
})
export class AccettazionePrivacyComponent implements OnInit {
	showButtons: boolean = false;
	@Output() onAccetta: EventEmitter<void> = new EventEmitter<void>();


	listaDocumenti: any[] = [
		// {
		// 	icon: 'assets/images/icona-link.png',
		// 	descrizione: "documento1",
		// 	link: "assets/images/icona-link.png"

		// },
		// {
		// 	icon: 'assets/images/icona-pdf.png',
		// 	descrizione: "Note legali",
		// 	link: "assets/images/icona-link.png",
		// 	download: true
		// },
		{
			icon: 'assets/images/icona-pdf.png',
			descrizione: "Informativa sulla privacy",
			link: "assets/images/icona-link.png",
			download: true
		},
		// {
		// 	icon: 'assets/images/icona-pdf.png',
		// 	descrizione: "Credits",
		// 	link: "assets/images/icona-link.png",
		// 	download: true
		// },
		// {
		// 	icon: 'assets/images/icona-pdf.png',
		// 	descrizione: "Cookie Policy",
		// 	link: "assets/images/icona-link.png",
		// 	download: true
		// },
		// {
		// 	icon: 'assets/images/icona-pdf.png',
		// 	descrizione: "Contatti",
		// 	link: "assets/images/icona-link.png",
		// 	download: true
		// },
		// {
		// 	icon: 'assets/images/icona-pdf.png',
		// 	descrizione: "Dichiarazione di accessibilità",
		// 	link: "info/dichiarazione-accessibilita",
		// 	download: false
		// }
	];
	logout() {
		this.authService.logout();
	}
	setFlagAccettazione() {
		this.authService.csrsFetch(
			'/corrente/accettazione-privacy',
			{
				method: 'POST'
			}
		).then(
			() => {
				//location.reload();
				this.authService.resetUserData();
				//ngReloadComponent(this.router);
				this.router.navigate(['/']);
				//this.onAccetta.emit();
			}
		);

	}
	constructor(
		private authService: BaseAuthService,
		private router: Router,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
		
	}
	ngOnInit(): void {
		this.titleService.title = "Informativa sulla Privacy"
		this.authService.cercaAccettazionePrivacy().then(
			p => {
				this.showButtons = !p;
				if (this.showButtons) {
					this.breadcrumbService.breadcrumb = undefined;
				}
				else {
					const breadcrumbModel = new BreadcrumbModel(
						[
							{
								icon: 'bi bi-house',
								url: ['/']
							}
						],
						"Informativa sulla Privacy"
					);
					this.breadcrumbService.breadcrumb = breadcrumbModel;
				}
			}
		);
	}
}

