import { Component, OnInit } from "@angular/core";
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-dichiarazione-accessibilita',
	templateUrl: './dichiarazione-accessibilita.component.html'
})
export class DichiarazioneAccessibilitaComponent implements OnInit {
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
			"Dichiarazione accessibilità"
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = "Dichiarazione di Accessibilità";
	}
}
