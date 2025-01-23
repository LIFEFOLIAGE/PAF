import { Injectable } from '@angular/core';
import { NavigationEnd, NavigationStart, Router } from '@angular/router';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';

// const conf = {
// 	controlli: {
// 		dett: {
// 			label: 'Controlli',
// 			target: '_self'
// 		},
// 		url: 'controlli'
// 	}
// }

@Injectable({
	providedIn: 'root'
})
export class BreadcrumbService {
	breadcrumb?: BreadcrumbModel;

	constructor(router: Router) {
		router.events.forEach(
			(event) => {
				if(event instanceof NavigationEnd) {
					this.breadcrumb = undefined;
				}
			}
		);
	}	 
}
