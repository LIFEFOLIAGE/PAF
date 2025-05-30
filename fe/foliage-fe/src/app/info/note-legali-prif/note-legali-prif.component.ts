import { Component, OnInit } from "@angular/core";
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-note-legali-prif',
	templateUrl: './note-legali-prif.component.html',
	//styleUrls: ['./note-legali.component.css']
})
export class NoteLegaliPrifComponent implements OnInit {
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
			"Note Legali PRIF"
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = "Note Legali PRIF";
	}
}