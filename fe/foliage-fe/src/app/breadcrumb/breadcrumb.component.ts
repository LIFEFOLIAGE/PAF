import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';

@Component({
	selector: 'app-breadcrumb',
	templateUrl: './breadcrumb.component.html',
	styleUrls: ['./breadcrumb.component.css']
})
export class BreadcrumbComponent {
	@Input({ required: true })model?: BreadcrumbModel = undefined;
	constructor(private router: Router) {
	}
	goTo(url: string[]) {
		this.router.navigate(url);
	}
}
