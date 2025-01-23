import { Injectable } from "@angular/core";
import { OAuthStorage } from "angular-oauth2-oidc";
import { environment } from "src/environments/environment";

@Injectable()
export class FoliageStorageService extends OAuthStorage{
	public readonly regione: string;
	public readonly origin: string;


	constructor() {
		super();
		this.regione = environment.regione;
		this.origin = window.location.origin;
	}
	getKey(key: string): string {
		return `${this.regione}.${key}@${this.origin}`;
	}
	override getItem(key: string): string | null {
		return sessionStorage.getItem(this.getKey(key));
	}
	override removeItem(key: string): void {
		return sessionStorage.removeItem(this.getKey(key));
	}
	override setItem(key: string, data: string): void {
		return sessionStorage.setItem(this.getKey(key), data);
	}

}