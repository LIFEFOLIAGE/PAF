import { Component, OnInit } from "@angular/core";
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-note-legali',
	templateUrl: './note-legali.component.html',
	//styleUrls: ['./note-legali.component.css']
})
export class NoteLegaliComponent implements OnInit {
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
			"Note Legali"
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = "Note Legali";
	}
}