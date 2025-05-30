import { Component, OnInit } from "@angular/core";
import { BreadcrumbService } from "../services/breadcrumb.service";
import { TitleService } from "../services/title.service";
import { BreadcrumbModel } from "../models/breadcrumb";
import { environment } from "src/environments/environment";

@Component({
	selector: 'app-download-apk-prif',
	templateUrl: './download-apk-prif.component.html'
})
export class DownloadApkPrifComponent implements OnInit {
	urlPrifApk: string = environment.urlPrifApk;
	
	constructor(
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}
	ngOnInit(): void {
		this.breadcrumbService.breadcrumb = new BreadcrumbModel(
			[
				{
					icon: 'bi bi-house',
					url: ['/']
				}
			],
			"Rilievi in campo"
		);
		this.titleService.title = "Rilievi in campo";
	}
}