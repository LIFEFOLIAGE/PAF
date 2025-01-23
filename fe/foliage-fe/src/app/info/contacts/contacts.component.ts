import { Component, OnInit } from "@angular/core";
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-contacts',
	templateUrl: './contacts.component.html',
	//styleUrls: ['./note-legali.component.css']
})
export class ContactsComponent implements OnInit {
	constructor(
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}

	ngOnInit(): void {
		const breadcrumbModel = new BreadcrumbModel(
			[
				{
					icon: 'bi bi-house',
					url: ['/']
				}
			],
			"Contatti"
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = "Contatti";
	}
}