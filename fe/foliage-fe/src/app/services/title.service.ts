import { Injectable } from '@angular/core';
import { NavigationEnd, NavigationStart, Router } from '@angular/router';

@Injectable({
	providedIn: 'root'
})
export class TitleService {
	_title?: string;
	get title(): string|undefined {
		return this._title;
	}
	set title(value: string | undefined) {
		this.setAll(value);
		// Promise.resolve().then(
		// 	() => {
		// 		this._title = value;
		// 		this.isXl = false;
		// 		if (value) {
		// 			document.title = `Foliage: ${value}`;
		// 		}
		// 		else {
		// 			document.title = 'Foliage';
		// 		}
		// 	}
		// );
	}
	isXl: boolean = false;
	setAll(title?: string, isXl?: boolean) {
		Promise.resolve().then(
			() => {
				this._title = title;
				this.isXl = isXl??false;
				if (title) {
					document.title = `Foliage: ${title}`;
				}
				else {
					document.title = 'Foliage';
				}
			}
		);
	}
	constructor(router: Router) {
		router.events.forEach(
			(event) => {
				if(event instanceof NavigationEnd) {
					this.title = undefined;
				}
			}
		);
	}

}
