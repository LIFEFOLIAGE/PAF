import { Component, Directive, ElementRef, Input, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BaseAuthService, CsrsToken } from '../../../services/auth.service';
import { SessionManagerService } from 'src/app/services/session-manager.service';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { environment } from 'src/environments/environment';
import { BaseMap } from './base-map';
import { RequestService } from 'src/app/services/request.service';



@Directive({
	selector: '[olTavMap]'
})
export class OlTavolaMapDirective {
	constructor(public elem: ElementRef) {
		//console.log(elem);
	}
}


@Component({
	selector: 'app-mappa-tavola',
	templateUrl: './mappa-tavola.component.html',
	styleUrls: ['../../../../../node_modules/ol/ol.css', './mappa-tavola.component.css']
})
export class MappaTavolaComponent {
	arrDimensioni: any[] = [
		{
			nome: 'A0',
			x: 1189,
			y: 841
		},
		{
			nome: 'A1',
			x: 841,
			y: 594
		},
		{
			nome: 'A2',
			x: 594,
			y: 420
		},
		{
			nome: 'A3',
			x: 420,
			y: 297
		},
		{
			nome: 'A4',
			x: 297,
			y: 210
		},
		{
			nome: 'A5',
			x: 210,
			y: 148
		}
	];
	arrRisoluzioni: number[] = [72, 150, 200, 300];
	arrScale: number[] = [2000, 5000];
	arrOrientamento: string[] = ['H', 'V'];
	dimensione: number = 4;
	scala: number = 0;
	risoluzione: number = 0;
	orientamento: number = 0;
	
	heigth: string = "50vh";
	width: string = "calc(50vh * 1.48)"
	disableDownload: boolean = false;
	
	map: BaseMap = new BaseMap();

	@Input()
	layersData?: Record<string, any[]>;

	@Input()
	layers?: string[];
	isOpen: boolean = false;
	loadOk: boolean = true;
	
	mapElement?: ElementRef;
	@ViewChild(OlTavolaMapDirective)
	set olMap(directive: OlTavolaMapDirective) {
		//console.log('set olTavMap');
		if (directive) {
			//console.log({olTavMap: directive});
			this.mapElement = directive.elem;
			this.drawMap();
		}
	};

	constructor(private requestService: RequestService) {
	}
	toggle() {
		this.isOpen = !this.isOpen;
		
		if (this.isOpen && this.map.mapController) {
			const map: any = this.map.mapController as any;
			setTimeout(
				() => {
					//map.renderSync();
					const zoom = map.getView().getZoom();
					map.getView().setZoom(1);
					map.getView().setZoom(zoom);
				},
				100
			);
		}
	}
	
	drawMap() {
		if (this.mapElement){
			//console.log("draw map");
			//console.log({layers:this.layers, layersData: this.layersData});
			try {
				this.map.drawMap(
					this.mapElement.nativeElement,
					this.layers, this.layersData,
					true
				);
				this.loadOk = true;
			}
			catch(e) {
				console.error(e);
				this.loadOk = false;
			}
			
		}
	}
	

	onDimensioneChange(value: number) {
		this.dimensione = value;
	}
	onRisoluzioneChange(value: number) {
		this.risoluzione = value;
	}
	onScalaChange(value: number) {
		this.scala = value;
	}
	onOrientamentoChange(value: number) {
		this.orientamento = value;
		if (this.arrOrientamento[value] == 'H') {
			this.heigth = "50vh";
			this.width = "calc(50vh * 1.48)";
		}
		else {
			this.heigth = "calc(50vh * 1.48)";
			this.width = "50vh";
		}
	}
	downloadTavola() {
		if (this.map.isReady) {
			
			this.disableDownload = true;
			this.requestService.progressRequest(
				this.map.downloadPdf(
					this.arrDimensioni[this.dimensione],
					this.arrRisoluzioni[this.risoluzione],
					this.arrScale[this.scala],
					this.arrOrientamento[this.orientamento]
				).then(
					() => {
						this.disableDownload = false;
					}
				)
			);
		}
	}
}