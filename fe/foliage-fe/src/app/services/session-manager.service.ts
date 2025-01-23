import { Injectable } from '@angular/core';
import { BaseAuthService } from './auth.service';

@Injectable({
	providedIn: 'root'
})
export class SessionManagerService {
	static idGen : number = 0;
	public readonly id: number = SessionManagerService.idGen++;
	constructor(
		private authService: BaseAuthService
	) {

	}

	_currProfilo : any = undefined;
	getCurrProfilo() : Promise<any> {
		if (this._currProfilo) {
			return Promise.resolve(this._currProfilo);
		}
		else {
			const strProfilo = sessionStorage.getItem('currProfilo');
			if (strProfilo) {
				this._currProfilo = JSON.parse(strProfilo);
			}
			if (this._currProfilo) {
				return Promise.resolve(this._currProfilo);
			}
			else {
				const prom = this.authService.fetchProfiloDefault();
				prom.then(
					(x: any) => {
						if (x != undefined) {
							this._currProfilo = x;
							sessionStorage.setItem('currProfilo', JSON.stringify(x));
						}
						return x;
					}
				);
				return prom;
			} 
		}
	}
	setCurrProfilo(value: any) {
		this._currProfilo = value;
		sessionStorage.setItem('currProfilo', JSON.stringify(value));
		Object.values(this.listeners["changeProfilo"].callbacks).forEach(
			(cb: (newProfilo: any) => void) => {
				cb(value);
			}
		);
	}
	_profiliUtente?: any[] = undefined;
	getProfiliUtente() : Promise<any[]> {
		if (this._profiliUtente) {
			return Promise.resolve(this._profiliUtente);
		}
		else {
			const strProfs = sessionStorage.getItem('profiliUtente');
			if (strProfs) {
				this._profiliUtente = JSON.parse(strProfs);
			}
			if (this._profiliUtente) {
				return Promise.resolve(this._profiliUtente);
			}
			else {
				return this.updateProfiliUtente();
			} 
		}
	}
	updateProfiliUtente() {
		const prom = this.authService.fetchProfiliUtente();
		prom.then(
			(x: any[]) => {
				if (x && x.length > 0) {
					this._profiliUtente = x;
					sessionStorage.setItem('profiliUtente', JSON.stringify(x));
				}
				return x;
			}
		);
		return prom;

	}
	listeners: Record<
		string, {
			currIdx: number, 
			callbacks: Record<number, (newProfilo: any) => void>
		}
	> = {
		changeProfilo: {
			currIdx: 0,
			callbacks: {
			}
		}
	};

	addListener(eventName: string, callback: (newProfilo: any) => void) : number {
		const entry : any = this.listeners[eventName];
		if (entry) {
			const nextId = entry.currIdx++;
			entry.callbacks[nextId] = callback;
			return nextId;
		}
		else {
			throw new Error(`Evento non gestito: ${eventName}`);
		}
	}

	removeListener(eventName: string, idx: number) {
		const entry : any = this.listeners[eventName];
		if (entry) {
			delete entry.callbacks[idx];
		}
		else {
			throw new Error(`Evento non gestito: ${eventName}`);
		}
	}
	
	logout() { // (3)
		console.log("logout");
		sessionStorage.removeItem("profiliUtente");
		sessionStorage.removeItem("currProfilo");
	}

	profileFetch(api: string, opts?: any) {
		return this.getCurrProfilo().then(
			(x: any) => {
				const appChar = (api.indexOf("?") > 0) ? "&" : "?";
				return this.authService.authFetch(`${api}${appChar}authority=${encodeURIComponent(x.authority)}&authScope=${x.ambito}`, opts);
			}
		);
	}
	
	profileCsrsFetch(api: string, opts?: any) {
		return this.getCurrProfilo().then(
			(x: any) => {
				const appChar = (api.indexOf("?") > 0) ? "&" : "?";
				return this.authService.csrsFetch(`${api}${appChar}authority=${encodeURIComponent(x.authority)}&authScope=${x.ambito}`, opts);
			}
		)
	}

}
