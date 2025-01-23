import { inject, NgModule } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterModule, RouterStateSnapshot, Routes, UrlTree } from '@angular/router';
//import { DashboardUtenteComponent } from './components/inutili/dashboard-utente/dashboard-utente.component';
//import { ViewIstanzaComponent } from './components/inutili/view-istanza/view-istanza.component';
import { UnauthorizedComponent } from './components/shared/unauthorized/unauthorized.component';
import { BaseAuthService } from './services/auth.service';
import { RicercaIstanzeComponent } from './ricerca-istanze/ricerca-istanze.component';
import { NuovaIstanzaComponent } from './nuova-istanza/nuova-istanza.component';
import { CompilaIstanzaComponent } from './compila-istanza/compila-istanza.component';
import { AccettazionePrivacyComponent } from './info/accettazione-privacy/accettazione-privacy.component';
//import { RichiestaProfiloComponent } from './richiesta-profilo/richiesta-profilo.component';
import { FormRichiestaComponent } from './form-richiesta/form-richiesta.component';
import { ElencoRichiesteComponent } from './elenco-richieste/elenco-richieste.component';
import { FormUtenteComponent } from './form-utente/form-utente.component';
import { ElencoUtentiComponent } from './elenco-utenti/elenco-utenti.component';
import { PannelloAmministratoreComponent } from './pannello-amministratore/pannello-amministratore.component';
import { FormRuoliUtenteComponent } from './form-ruoli-utente/form-ruoli-utente.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { GestioneIstanzaComponent } from "./gestione-istanza/gestione-istanza.component";
import {
	GestioneAssegnaIstruttoreComponent
} from "./gestione-assegna-istruttore/gestione-assegna-istruttore.component";
import { GestioneIstanzaInviaComponent } from "./gestione-istanza-invia/gestione-istanza-invia.component";
import { ValutaIstanzaComponent } from './valuta-istanza/valuta-istanza.component';
import { GestioneCambiaGestoreComponent } from "./gestione-cambia-gestore/gestione-cambia-gestore.component";
import { RichiestaProrogaComponent } from "./richiesta-proroga/richiesta-proroga.component";
import { CruscottoVigilanzaComponent } from "./cruscotto-vigilanza/cruscotto-vigilanza.component";
import { DashboardGovernanceComponent } from './governance/dashboard-governance/dashboard-governance.component';
import { DocumentazioneIstanzaComponent } from './components/istanze/documentazione-istanza/documentazione-istanza.component';
import { DichiarazioneAccessibilitaComponent } from './info/dichiarazione-accessibilita/dichiarazione-accessibilita.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { SessionManagerService } from './services/session-manager.service';
import { MonitoraggioComponent } from './monitoraggio/dashboard-monitoraggio/dashboard-monitoraggio.component';
import { ModificaRichiestaMonitoraggioComponent } from './monitoraggio/modifica-richiesta-monitoraggio/modifica-richiesta-monitoraggio.component';
import { GestioneRichiestaMonitoraggioComponent } from './monitoraggio/gestione-richiesta-monitoraggio/gestione-richiesta-monitoraggio.component';
import { NoteLegaliComponent } from './info/note-legali/note-legali.component';
import { CreditsComponent } from './info/credits/credits.component';
import { ContactsComponent } from './info/contacts/contacts.component';
import { RichiesteGovernanceComponent } from './governance/richieste-governance/richieste-governance.components';
import { ModificaRichiestaGovernanceComponent } from './governance/modifica-richiesta-governance/modifica-richiesta-governance.component';
import { GestioneRichiestaGovernanceComponent } from './governance/gestione-richiesta-governance/gestione-richiesta-governance.component';
//import { AllegaModuloFirmato } from './allega-modulo-firmato/allega-modulo-firmato.component';
//import { authGuardCittadino } from './guard/auth.guard';

export const authGuardProprietario = () => {
	// const authService = inject(BaseAuthService);
	// const router = inject(Router);

	// if (authService.isLoggedIn) {
	//   return true;
	// }

	// // Redirect to the login page
	// return router.parseUrl('/login');

	return true;
};

export const authGuardProfessionista = () => {
	// const authService = inject(BaseAuthService);
	// const router = inject(Router);

	// if (authService.isProfessionista) {
	//   return true;
	// }

	// // Redirect to the login page
	// return router.parseUrl('/unauthorized');

	return true;
};

export const authGuardIstruttore = () => {
	// const authService = inject(BaseAuthService);
	// const router = inject(Router);

	// if (authService.isIstruttore) {
	//   return true;
	// }

	// // Redirect to the login page
	// return router.parseUrl('/unauthorized');

	return true;
};


export const authGuardBase = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
	const authService = inject(BaseAuthService);
	const router = inject(Router);

	const dstUrl =
		(route as any)._routerState.url
		//route.pathFromRoot.map(v => v.url.map(segment => segment.toString()).join('/')).join('/')
	;
	console.log({accesso_a: dstUrl});

	if (authService.isAuthenticated()) {
		return authService.cercaAccettazionePrivacy().then(
			(p: boolean) => {
				if (p) {
					return true;
				}
				else {
					authService.postLoginRedirection = dstUrl;
					router.navigate(['/info/privacy']);
					return false;
				}
			},
			(e: any) => {
				return false;
			}
		);
	}
	else {
		const dstUrl =
			(route as any)._routerState.url
			//route.pathFromRoot.map(v => v.url.map(segment => segment.toString()).join('/')).join('/')
		;
		console.log({accesso_a: dstUrl});
		authService.postLoginRedirection = dstUrl;
		router.navigate(['login']);
		return false;
	}
};


const profiliIstanze = ['PROP', 'PROF'];
const authGuardIstanze = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
	const sessionManager: SessionManagerService = inject(SessionManagerService);
	const authService = inject(BaseAuthService);
	const router = inject(Router);

	const dstUrl =
		(route as any)._routerState.url
	;
	console.log({accesso_a: dstUrl});

	if (authService.isAuthenticated()) {
		return authService.cercaAccettazionePrivacy().then(
			(p: boolean) => {
				if (p) {
					//return true;
					return sessionManager.getCurrProfilo().then(
						p => {
							const res = profiliIstanze.includes(p.tipo);
							if (!res) {
								alert('Con il profilo attuale non è possibile accedere al cruscotto delle istanze!');
								router.navigate(['/dashboard']);
							}
							return res;
						},
						e => false
					);
				}
				else {
					authService.postLoginRedirection = dstUrl;
					router.navigate(['/info/privacy']);
					return false;
				}
			},
			(e: any) => {
				return false;
			}
		);
	}
	else {
		const dstUrl =
			(route as any)._routerState.url
			//route.pathFromRoot.map(v => v.url.map(segment => segment.toString()).join('/')).join('/')
		;
		console.log({accesso_a: dstUrl});
		authService.postLoginRedirection = dstUrl;
		router.navigate(['login']);
		return false;
	}
};

const profiliCruscottoPA = ['DIRI', 'ISTR'];
const authGuardCruscottoPA = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
	const sessionManager: SessionManagerService = inject(SessionManagerService);
	const authService = inject(BaseAuthService);
	const router = inject(Router);

	const dstUrl =
		(route as any)._routerState.url
	;
	console.log({accesso_a: dstUrl});

	if (authService.isAuthenticated()) {
		return authService.cercaAccettazionePrivacy().then(
			(p: boolean) => {
				if (p) {
					//return true;
					return sessionManager.getCurrProfilo().then(
						p => {
							const res = profiliCruscottoPA.includes(p.tipo);
							if (!res) {
								alert('Con il profilo attuale non è possibile accedere al cruscotto PA!');
								router.navigate(['/dashboard']);
							}
							return res;
						},
						e => false
					);
				}
				else {
					authService.postLoginRedirection = dstUrl;
					router.navigate(['/info/privacy']);
					return false;
				}
			},
			(e: any) => {
				return false;
			}
		);
	}
	else {
		const dstUrl =
			(route as any)._routerState.url
			//route.pathFromRoot.map(v => v.url.map(segment => segment.toString()).join('/')).join('/')
		;
		console.log({accesso_a: dstUrl});
		authService.postLoginRedirection = dstUrl;
		router.navigate(['login']);
		return false;
	}
};


const profiliVigilanza = ['AMMI', 'RESP', 'SORV'];
const authGuardVigilanza = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
	const sessionManager: SessionManagerService = inject(SessionManagerService);
	const authService = inject(BaseAuthService);
	const router = inject(Router);

	const dstUrl =
		(route as any)._routerState.url
	;
	console.log({accesso_a: dstUrl});

	if (authService.isAuthenticated()) {
		return authService.cercaAccettazionePrivacy().then(
			(p: boolean) => {
				if (p) {
					//return true;
					return sessionManager.getCurrProfilo().then(
						p => {
							const res = profiliVigilanza.includes(p.tipo);
							if (!res) {
								alert('Con il profilo attuale non è possibile accedere al cruscotto di vigilanza!');
								router.navigate(['/dashboard']);
							}
							return res;
						},
						e => false
					);
				}
				else {
					authService.postLoginRedirection = dstUrl;
					router.navigate(['/info/privacy']);
					return false;
				}
			},
			(e: any) => {
				return false;
			}
		);
	}
	else {
		const dstUrl =
			(route as any)._routerState.url
			//route.pathFromRoot.map(v => v.url.map(segment => segment.toString()).join('/')).join('/')
		;
		console.log({accesso_a: dstUrl});
		authService.postLoginRedirection = dstUrl;
		router.navigate(['login']);
		return false;
	}
};


const profiliGovernance = ['AMMI', 'RESP'];
const authGuardGovernance = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
	const sessionManager: SessionManagerService = inject(SessionManagerService);
	const authService = inject(BaseAuthService);
	const router = inject(Router);

	const dstUrl =
		(route as any)._routerState.url
	;
	console.log({accesso_a: dstUrl});

	if (authService.isAuthenticated()) {
		return authService.cercaAccettazionePrivacy().then(
			(p: boolean) => {
				if (p) {
					//return true;
					return sessionManager.getCurrProfilo().then(
						p => {
							const res = profiliGovernance.includes(p.tipo);
							if (!res) {
								alert('Con il profilo attuale non è possibile accedere ai dati della governance!');
								router.navigate(['/dashboard']);
							}
							return res;
						},
						e => false
					);
				}
				else {
					authService.postLoginRedirection = dstUrl;
					router.navigate(['/info/privacy']);
					return false;
				}
			},
			(e: any) => {
				return false;
			}
		);
	}
	else {
		const dstUrl =
			(route as any)._routerState.url
			//route.pathFromRoot.map(v => v.url.map(segment => segment.toString()).join('/')).join('/')
		;
		console.log({accesso_a: dstUrl});
		authService.postLoginRedirection = dstUrl;
		router.navigate(['login']);
		return false;
	}
};

export function baseCanActivate (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) : Promise<boolean | UrlTree> | boolean | UrlTree {
	return true;
}


const childIstanze = [
	{
		path: '',
		component: GestioneIstanzaComponent
	},
	{
		path: 'compila',
		component: CompilaIstanzaComponent,
		data: {
			isReadOnly: false
		}
	},
	{
		path: 'consulta',
		component: CompilaIstanzaComponent,
		data: {
			isReadOnly: true
		}
	},
	{
		path: 'consulta-modulistica',
		component: DocumentazioneIstanzaComponent,
		data: {
			isReadOnly: true
		}
	},
	{
		path: 'prepara-modulistica',
		component: DocumentazioneIstanzaComponent,
		data: {
			isReadOnly: false
		}
	},
	{
		path: 'assegnazioneIstruttore/:enteDomanda',
		component: GestioneAssegnaIstruttoreComponent,
	},
	{
		path: 'assegna-gestore',
		component: GestioneCambiaGestoreComponent,
	},
	{
		path: 'invio',
		component: GestioneIstanzaInviaComponent
	},
	{
		path: 'proroga',
		component: RichiestaProrogaComponent
	},
	{
		path: 'valutazione',
		component: ValutaIstanzaComponent,
		data: {
			isReadOnly: false
		}
	},
	{
		path: 'consulta-valutazione',
		component: ValutaIstanzaComponent,
		data: {
			isReadOnly: true
		}
	}
];


const routes: Routes = [
	{
		path: '',
		component: HomeComponent,
		pathMatch: 'prefix'
	},
	{
		path: 'login',
		component: LoginComponent,
		pathMatch: 'prefix'
	},
	{
		path: 'dashboard',
		component: DashboardComponent,
		canActivate: [authGuardBase],
		pathMatch: 'prefix'
	},
	{
		path: 'info',
		pathMatch: 'prefix',
		children: [
			{
				path: 'privacy',
				component: AccettazionePrivacyComponent,
				pathMatch: 'prefix'
			},
			{
				path: 'dichiarazione-accessibilita',
				component: DichiarazioneAccessibilitaComponent,
				pathMatch: 'prefix'
			},
			{
				path: 'note-legali',
				component: NoteLegaliComponent,
				pathMatch: 'prefix'
			},
			{
				path: 'credits',
				component: CreditsComponent,
				pathMatch: 'prefix'
			},
			{
				path: 'contatti',
				component: ContactsComponent,
				pathMatch: 'prefix'
			}
		]
	},
	{
		path: 'monitoraggio',
		pathMatch: 'prefix',
		canActivate: [authGuardBase],
		children: [
			{
				path: 'richieste',
				component: MonitoraggioComponent,
				pathMatch: 'prefix'
			},
			{
				path: 'richieste/nuova',
				component: ModificaRichiestaMonitoraggioComponent,
				pathMatch: 'prefix',
				data: {
					canEdit: true
				}
			},
			{
				path: 'richieste/:idRichiesta',
				component: GestioneRichiestaMonitoraggioComponent
			},
			{
				path: 'richieste/:idRichiesta/modifica',
				component: ModificaRichiestaMonitoraggioComponent,
				data: {
					canEdit: true
				}
			}
		]
	},
	// {
	// 	path: 'privacy',
	// 	component: AccettazionePrivacyComponent,
	// 	pathMatch: 'prefix'
	// },
	// {
	// 	path: 'dichiarazione-accessibilita',
	// 	component: DichiarazioneAccessibilitaComponent,
	// 	pathMatch: 'prefix'
	// },
	{
		path: 'istanze',
		pathMatch: 'prefix',
		canActivate: [authGuardIstanze],
		data: {
			ambito: 'pubblico'
		},
		children: [
			{
				path: '',
				//redirectTo: '/unimplemented',
				component: RicercaIstanzeComponent,
				pathMatch: 'prefix'
			},
			{
				path: 'nuova',
				component: NuovaIstanzaComponent,
				pathMatch: 'prefix'
			},
			{
				path: ':codIstanza',
				children: [
					...childIstanze
				]
			}
		]
	},
	{
		path: 'cruscotto-pa',
		pathMatch: 'prefix',
		canActivate: [authGuardCruscottoPA],
		data: {
			ambito: 'cruscotto-pa'
		},
		children: [
			{
				path: '',
				//redirectTo: '/unimplemented',
				component: RicercaIstanzeComponent,
				pathMatch: 'prefix'
			},
			{
				path: ':codIstanza',
				children: childIstanze
			}
		]
	},
	{
		path: 'vigilanza',
		pathMatch: 'prefix',
		canActivate: [authGuardVigilanza],
		data: {
			ambito: 'vigilanza'
		},
		children: [
			{
				path: '',
				component: CruscottoVigilanzaComponent,
				pathMatch: 'prefix'
			},
			{
				path: ':codIstanza',
				children: childIstanze
			}
		]
	},
	{
		path: 'unauthorized',
		component: UnauthorizedComponent
	},
	{
		path: 'account',
		data: {
			ambito: 'account'
		},
		canActivate: [authGuardBase],
		children: [
			{
				path: '',
				//component: CurrProfiloComponent,
				component: FormUtenteComponent,
				pathMatch: 'prefix'
			},
			{
				path: 'richieste/:idRichiesta',
				component: FormRichiestaComponent
			},
			{
				path: 'richieste',
				component: ElencoRichiesteComponent
			},
			{
				path: 'istanze',
				component: RicercaIstanzeComponent
			},
			{
				path: 'richiesta-profilo',
				//component: RichiestaProfiloComponent,
				component: FormRichiestaComponent,
				pathMatch: 'prefix'
			},
			{
				path: 'ruoli',
				component: FormRuoliUtenteComponent,
				pathMatch: 'prefix'
			}
		]
	},
	{
		path: "governance",
		canActivate: [authGuardGovernance],
		data: {
			ambito: 'governance'
		},
		children: [
			{
				path: '',
				component: DashboardGovernanceComponent
			},
			{
				path: 'richieste',
				component: RichiesteGovernanceComponent,
				pathMatch: 'prefix'
			},
			{
				path: 'richieste/nuova',
				component: ModificaRichiestaGovernanceComponent,
				pathMatch: 'prefix',
				data: {
					canEdit: true
				}
			},
			{
				path: 'richieste/:idRichiesta',
				component: GestioneRichiestaGovernanceComponent
			},
			{
				path: 'richieste/:idRichiesta/modifica',
				component: ModificaRichiestaGovernanceComponent,
				data: {
					canEdit: true
				}
			}
		]	
	},
	{
		path: "amministrazione",
		canActivate: [authGuardBase],
		data: {
			profilo: 'amministratore',
			ambito: 'amministrazione'
		},
		children: [
			{
				path: '',
				component: PannelloAmministratoreComponent
			},
			{
				path: 'utenti',
				//component: SezioneUtentiComponent
				children: [
					{
						path: '',
						component: ElencoUtentiComponent
					},
					{
						path: ':username',
						//component: AmministrazioneProfiloComponent
						children: [
							{
								path: '',
								component: FormUtenteComponent
							},
							{
								path: 'ruoli',
								component: FormRuoliUtenteComponent
							},
							{
								path: 'richieste',
								children: [
									{
										path: '',
										component: ElencoRichiesteComponent
									},
									{
										path: ':idRichiesta',
										component: FormRichiestaComponent
									}
								]
							},
							{
								path: 'istanze',
								component: RicercaIstanzeComponent
							}
						]
					}
				]
			},
			{
				path: 'richieste',
				children: [
					{
						path: '',
						component: ElencoRichiesteComponent
					},
					{
						path: ':idRichiesta',
						component: FormRichiestaComponent
					}
				]
			}
		]
	}
];

@NgModule({
	imports: [
		RouterModule.forRoot(routes/*,
			{
				"enableTracing": true
			}*/
		)
	],
	exports: [RouterModule]
})
export class AppRoutingModule {
}
