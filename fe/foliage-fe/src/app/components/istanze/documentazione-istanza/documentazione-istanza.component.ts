import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment'; 
import { ActivatedRoute, Router } from '@angular/router';
import { BaseAuthService, CsrsToken } from '../../../services/auth.service';
import { SessionManagerService } from 'src/app/services/session-manager.service';
import { HtmlService } from 'src/app/services/html.service';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';
import { FileAllegatoComponent } from '../../file-allegato/file-allegato.component';


const schede = environment.tavole;
const accept = "application/pdf, image/*, .p7m";


@Component({
	selector: 'app-documentazione-istanza',
	templateUrl: './documentazione-istanza.component.html'
})
export class DocumentazioneIstanzaComponent implements OnInit {
	schede: any[] = schede;//.map(s => ({...s, dimensione: 0}));
	files: any[] = [];
	note?: string = undefined;
	tavole: any[] = [];
	isSottosoglia: boolean = true;
	isBozza: boolean = true;
	ambito: string = "";
	isReadOnly: boolean = true;
	codIstanza: string = "";
	layersData: any = {}; 
	ready: boolean = false;
	accept: string = accept;

	constructor(
		public html: HtmlService,
		private router : Router,
		private route: ActivatedRoute,
		private authService: BaseAuthService,
		private sessionManager: SessionManagerService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
	}


	ngOnInit(): void {
		const sessionData = this.route.snapshot.data;
		this.ambito = sessionData["ambito"];
		this.isReadOnly = sessionData["isReadOnly"];
		this.codIstanza = this.route.snapshot.params['codIstanza'];

		let root: string|undefined = undefined;
		let rootLabel: string|undefined = undefined;
		//const root = (this.ambito == 'pubblico' ? 'istanze' : 'cruscotto-pa');
		//const rootLabel = (this.ambito == 'pubblico' ? 'Istanze' : 'Cruscotto P.A.');

		switch (this.ambito) {
			case "pubblico": {
				root = 'istanze';
				rootLabel = 'Istanze';
			}; break;
			case "cruscotto-pa": {
				root = 'cruscotto-pa';
				rootLabel = 'Cruscotto P.A.';
			}; break;
			case "vigilanza":{
				root = 'vigilanza';
				rootLabel = 'Vigilanza';
			}; break;
			default: {
				const errMsg = "Ambito non gestito";
				alert(errMsg);
				throw new Error(errMsg);
			}
		}
		const breadcrumbModel = new BreadcrumbModel(
			[
				{
					icon: 'bi bi-house',
					url: ['/']
				},
				{
					label: rootLabel,
					url: [root]
				},
				{
					label: this.codIstanza,
					url: [root, `${(this.codIstanza)}`]
				}
			],
			"Modulistica"
		);
		
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		const prefix = this.isReadOnly ? "Consulta" : "Compila"
		this.titleService.title = `Istanza ${this.codIstanza} - ${prefix} Modulistica`;


		this.sessionManager.profileFetch(`/istanze/${this.codIstanza}/dati-modulistica`).then(
			(res: any)  => {
				//console.log(res);
				Object.entries(res).forEach(
					([k, v]: [string, any]) => {
						switch (k) {
							case 'files': {
								this.files = v;
							}; break;
							case 'tavole': {
								this.tavole = v;
								Object.entries(v).forEach(
									([kk, vv]: [any, any]) => {
										const idx = kk as number;
										if (idx) {
											this.tavole[idx] = vv[0];
										}
									}
								)
							}; break;
							case 'note': {
								this.note = v;
							}; break;
							case 'isSottosoglia': {
								this.isSottosoglia = v;
							}; break;
							case 'isBozza': {
								this.isBozza = v;
							}; break;
							default: {
								this.layersData[k] = v;
							}
						};
						// if (k == 'files') {
						// 	this.files = v;
						// }
						// else {
						// 	this.layersData[k] = v;
						// }
					}
				);
				this.ready = true;
			}
		)
	}
	downloadPdf() {
		this.sessionManager.profileFetch(`/istanze/${this.codIstanza}/modulo-istanza`).then(
			(blob) => {
				if (blob != null) {
					var url = window.URL.createObjectURL(blob);
					var a = document.createElement('a');
					a.href = url;
					a.download = `${this.codIstanza}-istanza.pdf`;
					document.body.appendChild(a);
					a.click();
					a.remove();
				}
			}
		)
	}
	downloadBozzaPdf() {
		this.sessionManager.profileFetch(`/istanze/${this.codIstanza}/bozza-modulo-istanza`).then(
			(blob) => {
				if (blob != null) {
					var url = window.URL.createObjectURL(blob);
					var a = document.createElement('a');
					a.href = url;
					a.download = `${this.codIstanza}-istanza.pdf`;
					document.body.appendChild(a);
					a.click();
					a.remove();
				}
			}
		)
	}
	rimuoviTavola(idx: number) {
		if (confirm(`Vuoli elminare la tavola ${idx + 1}?`)) {
			this.sessionManager.profileCsrsFetch(
				`/istanze/${this.codIstanza}/tavole/${idx}`,
				{
					method: "DELETE"
				}
			).then(
				(cont: any) => {
					this.tavole[idx] = undefined;
					alert("Tavola elimnata")
				}
			);	
		}
	}
	
	allegaTavola(idx: number, fileList: (""| FileList| null)) {
		console.log(idx);
		if (this.tavole && fileList != null && fileList != "") {
			
			for (let i = 0; i < fileList.length; i++) {
				const file: (File | null) = fileList.item(i);
				if (file != null) {
					console.log(file.type);
					
					if (
						//file.type == "application/pdf" || file.type.startsWith("image/") || file.name.endsWith('.p7m')
						FileAllegatoComponent.validatePattern(file, this.accept)
					) {
						let newTavola: any = {
							name: file.name,
							originalName: file.name,
							size: file.size,
							storage: "base64",
							type: file.type,// "application/pdf",
							hash: ""
						};
						// this.tavole[idx].nomeFile = file.name;
						// this.tavole[idx].dimensioneFile = file.size;
						const reader = new FileReader();
						
						reader.addEventListener(
							"load",
							() => {
								// convert image file to base64 string
								if (this.tavole && reader.result) {
									newTavola.url = reader.result;
									const dataToSend = [newTavola];
									this.sessionManager.profileCsrsFetch(
										`/istanze/${this.codIstanza}/tavole/${idx}`,
										{
											method: "PUT",
											headers: {
												"Content-Type": "application/json",
											},
											body: JSON.stringify(dataToSend)
										}
									).then(
										(cont: any) => {
											this.tavole[idx] = newTavola;
											alert("Tavola Caricata")
										}
									);
								}
							},
							false,
						);
						reader.readAsDataURL(file);
					}
					else {
						
						//const mess = "È possibile caricare soltanto file pdf, p7m o immagini";

						//const mess = `Tipologia del file non supportata. I formati accettati sono: ${this.accept}`;
						const mess = `Tipologia del file non supportata!`;
						alert(mess);
						throw new Error(mess);
					}
				}
	
			}
		}
	}
}