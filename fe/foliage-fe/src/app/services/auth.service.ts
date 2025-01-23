import { Injectable } from '@angular/core';
import { AuthConfig, OAuthInfoEvent, OAuthService, OAuthStorage } from 'angular-oauth2-oidc';
import { environment } from "src/environments/environment";
import { backendRequest, ngReloadComponent } from './utils';
import { RequestService } from './request.service';
import { Router, UrlSegment } from '@angular/router';
import { FoliageStorageService } from './auth.storage';
import { LocationStrategy } from '@angular/common';




export type CsrsToken = {
	parameterName: string,
	token: string,
	headerName: string
};

export abstract class BaseAuthService {
	protected csrsToken? : CsrsToken;
	public abstract getAccessToken() : string;
	public abstract load(): Promise<void>;
	public abstract login(): void;
	public abstract isAuthenticated(): boolean;
	public abstract getUserTokenData(): any;
	public abstract get userName() : string;

	// public get loginProm(): Promise<void> {
	// 	console.log("10 secondi");
	// 	return new Promise(resolve => setTimeout(resolve, 10000));
	// }

	get postLoginRedirection() : string | undefined {
		const strValue = this.storageService.getItem("postLoginRedirection");
		if (strValue) {
			return strValue;
		}
		else {
			return undefined;
		}
	}
	set postLoginRedirection(value: string | undefined) {
		if (value) {
			this.storageService.setItem("postLoginRedirection", value);
		}
		else {
			this.storageService.removeItem("postLoginRedirection");
		}
	}

	private revealDisconnection!: () => void;
	private disconnectionPromise! : Promise<void> ;	

	constructor(
		private requestService: RequestService,
		private storageService: OAuthStorage
	) {
		this.buildDisconnectionPromise();
	}

	buildDisconnectionPromise() {
		this.disconnectionPromise = new Promise<void>(
			(resolve, reject) => {
				this.revealDisconnection = function() {
					console.log("Rilevata disconnessione");
					resolve();
					this.buildDisconnectionPromise();
				};
			}
		);
	}

	public onDisconnection() {
		return this.disconnectionPromise;
	}
	firstAttempt?: Promise<any> = undefined;
	public authFetch(api: string, opts?: any) : Promise<any> {
		if (this.isAuthenticated()) {
			const headers: any = (opts && opts.headers) ? {...opts.headers} : {};
			headers['Authorization'] = `Bearer ${this.getAccessToken()}`;
			if (this.firstAttempt) {
				
				return this.firstAttempt.then(
					() => {
						return this.requestService.progressRequest(backendRequest(api, {...opts, headers}));
					},
					(e) => {
						this.logout();
						return Promise.reject();
					}
				);
				//return this.requestService.progressRequest(backendRequest(api, {...opts, headers}));
				
			}
			else {
				//this.firstAttempt = backendRequest(api, {...opts, headers});

				this.firstAttempt = backendRequest(api, {...opts, headers});
				return this.firstAttempt;
			}
		}
		else {
			const errMsg = "Utente non autenticato";
			if (!environment.production) {
				alert(errMsg);
			}
			this.revealDisconnection();
			throw new Error(errMsg);
		}
	}

	private _reqUserData: number = 0;
	private _userDataProm?: Promise<any> = undefined;

	public clearUserData(): void {
		this._userDataProm = undefined;
	}
	public logout(): void {
		this.firstAttempt = undefined;
		this.clearUserData();
		this.revealDisconnection();
	}

	private _userData: any = undefined;
	public getUserData() : Promise<any> {
		if (this._userDataProm == undefined) {
			const req = ++this._reqUserData;
			return this._userDataProm = this.authFetch('/corrente').then(
				(userData:any) => {
					if (req >= this._reqUserData) {
						this._userData = userData;
					}
					return userData;
				}
			);
		}
		else {
			return this._userDataProm;
		}
	}
	public resetUserData() {
		this._userDataProm = undefined;
	}

	public getCsrsToken() : Promise<CsrsToken> {
		if (this.csrsToken == undefined) {
			const outVal: Promise<CsrsToken> = this.authFetch(
				'/csrs'
			);
			outVal.then(
				(tok: CsrsToken) => {
					this.csrsToken = tok;
				},
				(e) => {
					throw new Error("Problemi nel recupero del token csrs", {cause: e});
				}
			);
			return Promise.resolve(outVal);
		}
		else {
			return Promise.resolve(this.csrsToken);
		}
	}

	public csrsFetch(api: string, opts?: any) : Promise<any> {
		return this.getCsrsToken().then(
			(tok: CsrsToken) => {
				const headers = (opts?.headers?? {});
				if (tok && tok.headerName && tok.token) {
					headers[tok.headerName] = tok.token;
				}
				return this.authFetch(api, {...opts, headers});
			}
		);
	}

	cercaAccettazionePrivacy() : Promise<boolean> {
		if (this.getAccessToken()) {
			return this.getUserData().then(
				(userData: any) => {
					return userData?.flagAccettazione == true;
				}
			);
		}
		else {
			return Promise.reject(false);
		}
	}
	
	public fetchProfiloDefault() : Promise<any> {
		return this.authFetch('/corrente/profilo-default');
	}
	public fetchProfiliUtente() : Promise<any[]> {
		return this.authFetch('/corrente/profili-selezionabili');
	}
};

type tokenDataType = Record<string, any>;
type mockUserType = {
	token: string,
	userData: tokenDataType
};

@Injectable({
	providedIn: 'root'
})
export class MockAuthService extends BaseAuthService{
	static mockUserKey = "mockLoggedUser";
	private _loggedUser?: mockUserType;
	get loggedUser() : (mockUserType|undefined) {
		if (this._loggedUser == undefined) {
			const strUser = sessionStorage.getItem(MockAuthService.mockUserKey);
			if (strUser != undefined) {
				this._loggedUser = JSON.parse(strUser);
			}
		}
		return this._loggedUser;
	}


	constructor(
		private requestService2: RequestService,
		private storageService2: OAuthStorage,
		private locationStrategy: LocationStrategy,
		private router: Router
	) {
		super(requestService2, storageService2);
	}
	public get userName() : string {
		return (this.loggedUser?.userData["upn"]??"");
	}
	public getAccessToken(): string {
		return this.loggedUser?.token??"";
	}
	public load(): Promise<void> {
		return Promise.resolve();
	}
	public login(): void {

		if (environment.mockUsers) {
			if (environment.mockUsers.loggedUser) {
				this._loggedUser = environment.mockUsers.users[environment.mockUsers.loggedUser];
				if (this.loggedUser != undefined) {
					sessionStorage.setItem(MockAuthService.mockUserKey, JSON.stringify(this.loggedUser));
				}
			}
		}
		//window.location.reload();
		//ngReloadComponent(this.router);
		window.open(this.locationStrategy.getBaseHref(), '_self');
		// const redUrl = this.postLoginRedirection;
		// this.postLoginRedirection = undefined;
		// if (redUrl) {
		// 	this.router.navigate([redUrl]);
		// }
		// else {
		// 	ngReloadComponent(this.router);
		// }
	}
	public override logout(): void {
		super.logout();
		sessionStorage.removeItem(MockAuthService.mockUserKey);
		this._loggedUser = undefined;
	}
	public isAuthenticated(): boolean {
		
		return this.loggedUser != undefined;
	}
	public getUserTokenData() {
		return this.loggedUser?.userData;
	}

}




const authConfig: AuthConfig = environment.iamConfig;


@Injectable({
	providedIn: 'root'
})
export class AuthService extends BaseAuthService {
	static nInstanze : number = 1; 

	constructor(
		private readonly oauthService: OAuthService,
		private storageService2: OAuthStorage,
		private requestService2: RequestService
	) {
		super(requestService2, storageService2);
		console.log("costruttore " + (AuthService.nInstanze++));
	}
	private loadAndTryLogin() {
		this.oauthService.configure(authConfig);
		this.oauthService.setupAutomaticSilentRefresh();
		return this.oauthService.loadDiscoveryDocumentAndTryLogin({}).then(
			(x) => {
				this.oauthService.events.subscribe(
					e => {
						console.log({evento: e.type});
						console.log(e);
						switch (e.type) {
							case "token_expires": {
								const eInfo : OAuthInfoEvent = e as OAuthInfoEvent;
								if (eInfo.info == 'access_token') {
									this.oauthService.refreshToken();
								}
							}; break;
							case "token_received": {
								this.oauthService.loadUserProfile();
							}; break;
							case "token_refresh_error": {
								//this.oauthService.loadUserProfile();
							}
						}
					}
				);
			}
		);
	}

	private getIdentityClaims(): any {
		return this.oauthService.getIdentityClaims();
	}

	public load(): Promise<void> { // (1)
		return this.loadAndTryLogin();
	}
	public login() : void { // (2)
		console.log("login");
		this.oauthService.initLoginFlow();
	}
	public override logout() { // (3)
		super.logout();
		console.log("logout");
		//this.oauthService.logOut(false);
		this.oauthService.revokeTokenAndLogout();
	}

	public isAuthenticated(): boolean {
		if (
			this.oauthService.hasValidAccessToken() &&
			this.oauthService.hasValidIdToken() &&
			this.oauthService.getAccessTokenExpiration() > Date.now()
		) {
			return true;
		} else {
			return false;
		}
	}


	// private canNavigate() {
	// 	return this.isAuthenticated() && this._userData.flagAccettazione;
	// }
	
	public getAccessToken() : string {
		return this.oauthService.getAccessToken();
	}
	public get userName(): string {
		const claims: any = this.oauthService.getIdentityClaims();
		if (!claims) return "";
		return claims["given_name"];
	}

	public getUserTokenData(): any {
		if (this.isAuthenticated()) {
			const claims = this.getIdentityClaims();
			return {
				cf: claims.sub,
				username: claims.organization,
				nome: claims.nickname,
				cognome: claims.family_name
			};
		}
		else {
			return undefined;
		}
	}

	private loadUserData(cb: (inp: any) => void): Promise<any> {
		return this.getUserData()
			.then(cb);
	}

	// public authFetch(api: string, opts?: any) : Promise<any> {
	// 	if (this.isAuthenticated()) {
	// 		const headers: any = (opts && opts.headers) ? {...opts.headers} : {};
	// 		headers['Authorization'] = `Bearer ${this.oauthService.getAccessToken()}`;

	// 		return backendRequest(api, {...opts, headers});
	// 	}
	// 	else {
	// 		const errMsg = "Utente non autenticato";	
	// 		if (!environment.production) {
	// 			alert(errMsg);
	// 		}
	// 		throw new Error(errMsg);
	// 	}
	// }
}
