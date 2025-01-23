import { Component, EventEmitter, Inject, OnDestroy, OnInit, Output } from '@angular/core';
import { NavigationEnd, Event as Event_1, Router, RouterEvent } from '@angular/router';
import { SessionManagerService } from 'src/app/services/session-manager.service';
import { BaseAuthService } from '../../../services/auth.service';
import { LocalDateTime, DateTimeFormatter } from '@js-joda/core';
import { DOCUMENT } from '@angular/common';
import { filter } from 'rxjs';
import { ngReloadComponent } from 'src/app/services/utils';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';


@Component({
	selector: 'app-header',
	templateUrl: './header.component.html',
	styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {
	goToAccount() {
		this.router.navigate(['account']);
	}
	user: any = undefined;
	isAuthenticated: boolean = false;
	notificheLette: number = 0;
	notificheNonLette: number = 0;
	numNotifiche: number = 0;
	userLogin: any = undefined;

	docClick = this.documentClick.bind(this);
	static longDf = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm:ss');
	
	@Output() loginLogout: EventEmitter<boolean> = new EventEmitter<boolean>();

	onNotifiche(event: Event) {
		if (this.showNotifiche) {
			this.closeNotifiche();
		}
		else {
			this.openNotifiche();
		}
		event.stopPropagation();
	}
	openNotifiche() {
		this.showNotifiche = true;
		this.document.addEventListener("click", this.docClick);
	}
	closeNotifiche() {
		this.showNotifiche = false;
		this.document.removeEventListener("click", this.docClick);
	}
	documentClick(): void {
		console.log("HostListener");
		this.closeNotifiche();
	}

	showNotifiche: boolean = false;
	notifiche: any[] = [];
	constructor(
		private authService: BaseAuthService,
		private sessionManager: SessionManagerService,
		private router: Router,
		public breadcrumbService: BreadcrumbService,
		//public titleService: TitleService,
		@Inject(DOCUMENT) private document: Document
	) {
	}
	ngOnDestroy(): void {
		this.document.removeEventListener("click", this.docClick);
	}
	ngOnInit(): void {
		//console.log(this.authService.userName);
		this.router.events.pipe(
			filter((e: Event_1 | RouterEvent): e is RouterEvent => e instanceof NavigationEnd)
		).subscribe((e: RouterEvent) => {
			this.isAuthenticated = this.authService.isAuthenticated();
			if (this.isAuthenticated) {
				this.sessionManager.updateProfiliUtente().then(
					(res: any[]) => {
						this.listaProfili = res;
					},
					() => {
						this.reload();
					}
				);
				this.leggiNotifiche();
			}
		});
		this.reload();
	}
	reload(): Promise<any> {
		this.isAuthenticated = this.authService.isAuthenticated();
		if (this.isAuthenticated) {
			const prom1 = this.authService.getUserData().then(
				res => {
					console.log({getUserData: res});
					this.authService.onDisconnection().then(
						() => {
							this.userLogin = undefined;
						}
					);
					this.userLogin = {
						cf: res.cf,
						username: res.userName,
						nome: res.nome,
						cognome: res.cognome
					};
					//console.log({userLogin: this.userLogin});
					this.sessionManager.getCurrProfilo().then(
						(res: any) => {
							this.profiloSelezionato = res;
						}
					);
					
				}
			);
			const prom2 = this.sessionManager.updateProfiliUtente().then(
				(res: any[]) => {
					this.listaProfili = res;
				},
				(e) => {
					this.reload();
				}
			);
			return Promise.all([prom1, prom2]);
		}
		else {
			return Promise.reject();
		}
	}
	leggiNotifiche() {
		if (this.isAuthenticated) {
			this.authService.authFetch('/corrente/notifiche').then(
				(res: any[]) => {
					this.notifiche = res;
					this.numNotifiche = res.length;
					this.notificheLette = this.notifiche.reduce(
						(pRes, cv) => {
							if (cv.data_lettura != undefined) {
								return pRes + 1;
							}
							else {
								return pRes;
							}
						},
						0
					);
					this.notificheNonLette = res.length - this.notificheLette;
				}
			);
		}
	}

	opzioniProfilo: any[] = [
		{
			nome: "Profilo Utente",
			icona: "assets/images/svg/wd-sprite.svg#wd-login-header",
			call: () => {
				this.goToAccount();
			}
		},
		{
			nome: "Logout",
			icona: "assets/images/svg/wd-sprite.svg#wd-logout",
			call: () => {
				this.logout();
			}
		}
	]

	listaProfili: any[] = [];


	profiloSelezionato: any = undefined;


	login(): void {
		this.sessionManager.logout();
		this.authService.login();
		Promise.resolve().then(
			() => {
				this.reload();
				this.loginLogout.emit(true);
			}
		);
	}
	logout(): void {
		// this.userLogin = undefined;
		// this.profiloSelezionato = undefined;
		alert("Ti sei appena disconnesso dal servizio");
		this.sessionManager.logout();
		this.authService.logout();
		this.loginLogout.emit(false);

		//this.reload();
	}
	selezionaProfilo(profilo: any): void {
		this.sessionManager.setCurrProfilo(profilo);
		this.profiloSelezionato = profilo;
		//this.router.navigate([profilo.urlDashboard]);
		this.router.navigate(['/dashboard'], { replaceUrl: false });
	}
	showDate(x: string) {
		const d = LocalDateTime.parse(x, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		return d.format(HeaderComponent.longDf)
	}
	visitaNotifica(notifica: any) {
		const action = () => {
			this.showNotifiche = false;
			this.router.navigate([notifica.link]);
		};
		if (notifica.data_lettura != undefined) {
			action();
		}
		else {
			this.authService.csrsFetch(
				`/corrente/notifica/${notifica.id_notifica}`,
				{
					method: 'POST'
				}
			).then(
				() => {
					this.leggiNotifiche();
					action();
				}
			);
		}
	}
}
