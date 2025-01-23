import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DateTimeFormatter, LocalDate, LocalDateTime } from '@js-joda/core';
import { Ambito } from '../selettore-ambito/selettore-ambito.component';
import { BaseAuthService } from '../services/auth.service';
import { SessionManagerService } from '../services/session-manager.service';
import { default as formTitolare } from './formTitolare';
//import { default as configurazioneWizard } from './wizardSceltaIstanza.json';
import { DomandaWizard, SceltaWizard } from "./models/DomandaWizard";
import { FormioRendererComponent } from "../components/shared/formio-renderer/formio-rederer.component";
import { IstanzaComponentInterface } from "../components/interfaces/istanza-component-interface";
import { environment } from 'src/environments/environment';
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
	selector: 'app-nuova-istanza',
	templateUrl: './nuova-istanza.component.html',
	styleUrls: ['./nuova-istanza.component.css']
})
export class NuovaIstanzaComponent implements OnInit {
	formTitolare: any = formTitolare;
	datiTitolare: any = {};
	// formTitolareController: any = undefined;
	isTitolare: string = "no";
	vTrue: boolean = true;
	vFalse: boolean = false;

	arrayAllDomandeWizard: DomandaWizard[] = [];
	arrayCurrentDomandeWizard: DomandaWizard[] = [];
	selectedWizardCodTipoIstanza?: string;
	canSelWizard: boolean = false;

	openWizardDialog: boolean = false;

	private _istanzaComponentInterface?: IstanzaComponentInterface;

	onDataTitolareChanged(data: any) {
		console.log({ data });
		this.datiTitolare = data;
	}

	// onFormTitolareInit(fc: any) {
	// 	this.formTitolareController = fc;
	// 	fc.redraw(); //TODO: spostato in form io renderer
	// }
	onComponentInit(comp: IstanzaComponentInterface) {
		this._istanzaComponentInterface= comp;

	}

	currProfilo: any = undefined;
	userData: any = undefined;
	tipiIstanza: any[] = [];
	tipiSpecifici: any[] = [];
	mapSpecifici: Record<string, any[]> = {};

	tipoAmbitoTerr: string = "";
	idAmbitoTerr: number = -1;
	ambitiTerr: string[] = [];

	onChangeAmbito(event: Ambito) {
		this.tipoAmbitoTerr = event.tipo;
		this.idAmbitoTerr = event.value;
	}

	getAmbitoVisibility(): string {
		const outVal = this.selTipoIstanza == undefined ? 'hidden' : 'visible';
		//console.log(outVal);
		return outVal;
	}

	getCheckSottoSogliaVisibility(): string {
		const outVal = this.IsMenuSottoSogliaVisible() ? 'visible' : 'hidden';
		//console.log(outVal);
		return outVal;
	}

	getMenuSpecificoVisibility(): string {
		const outVal = this.tipiSpecifici.length > 1 ? 'visible' : 'hidden';
		//console.log(outVal);
		return outVal;
	}

	IsMenuSottoSogliaVisible(): boolean {
		//const outVal = (this.selTipoIstanza?.codTipoIsta == 'SOTTO_SOGLIA');
		const outVal = (!this.currProfilo.isSenior && this.currProfilo.tipo == 'PROF');
		return outVal;
	}

	isInGestione: boolean = false;
	nome: string = "";
	note?: string = undefined;
	//note: string = "";

	selTipoIstanza: any;

	setTipoInstanza(tipo: any) {

		if (tipo) {
			this.ambitiTerr = [tipo.tipo_ente];
			this.tipiSpecifici = this.mapSpecifici[tipo.idTipo];

			//this.ambitiTerr = [tipo.tipo_ente];
		}
		else {
			this.tipiSpecifici = [];
			this.ambitiTerr = [];
		}
		if (this.selTipoIstanza != undefined && this.selTipoIstanza.tipo_ente != tipo.tipo_ente) {
			this.idAmbitoTerr = -1;
		}
		this.selTipoIstanza = tipo;
		if (this.tipiSpecifici.length == 1) {
			this.selTipoSpecifico = this.tipiSpecifici[0];
		}
		else {
			this.selTipoSpecifico = undefined;
		}
	}

	selTipoSpecifico: any;

	//selTipoProprieta: any;
	//selTipoNaturaProprieta: any;

	annulla() {
		this.router.navigate(['istanze']);
	}

	crea() {
		const tipoInsta = this.selTipoIstanza;
		const sottotipoIsta = this.selTipoSpecifico;
		const idAmbitoTerr = this.idAmbitoTerr;
		// const tipoProprieta = this.selTipoProprieta;
		// const tipoNaturaProprieta = this.selTipoNaturaProprieta;
		let err: string = "";
		if (tipoInsta) {
			if (sottotipoIsta == undefined) {
				err = "Occorre selezionare un tipo di istanza specifico";
			}
			else {
				if (idAmbitoTerr >= 0) {
					if ((!this.IsMenuSottoSogliaVisible() || this.isInGestione ) ) {
						if (this.nome) {
							if (this.note) {
								if (this.currProfilo.tipo == 'PROF' && this.isTitolare == "no") {
									let isValid = this._istanzaComponentInterface?.getValidity() ?? false
									if (typeof isValid === 'string') {
										isValid = isValid == "";
									}
									if (!isValid) {
										err = "I dati del titolare non sono validi! Controlla i campi per cui sono evidenziati errori.";
									}
								}
							}
							else {
								err = "Occorre assegnare una descrizione all'istanza";	
							}
						}
						else {
							err = "Occorre assegnare un nome all'istanza";
						}
					}
					else {
						err = "Occorre selezionare che si tratta di proprietà privata di dimensioni medio piccole";
					}
				}
				else {
					err = "Occorre indicare l'ente gestore dell'istanza";
				}

			}
		}
		else {
			err = "Occorre selezionare un tipo di istanza";
		}
		if (err) {
			alert(err);
			throw err;
		}
		const datiCreazione: any = {
			tipoInsta: sottotipoIsta.codTipoIsta,
			idEnte: idAmbitoTerr,
			nomeIsta: this.nome,
			noteIsta: this.note
		};

		if (this.currProfilo.tipo == 'PROF' && this.isTitolare == "no") {
			datiCreazione.datiTitolare = { ...this.datiTitolare };
			datiCreazione.datiTitolare.dataDiNascita = LocalDate.parse(datiCreazione.datiTitolare.dataDiNascita, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
		const reqBody = JSON.stringify(datiCreazione);
		this.sessionManager.profileCsrsFetch(
			'/istanza',
			{
				method: 'PUT',
				headers: {
					"Content-Type": "application/json",
				},
				body: reqBody
			}
		).then(
			({ codIsta }) => {
				alert(`È stata creata una nuova istanza con il codice ${codIsta}`);
				this.router.navigate([`/istanze/${codIsta}`]);
			},
			(err) => {
				alert("Si è verificato un errore durante la creazione dell'istanza");
			}
		);
	}

	canSee(seniorOnly: boolean, abil: string): boolean {
		return (
			(!seniorOnly || this.currProfilo.isSenior)
			&&
			(
				abil == undefined
				|| abil.length == 0
				|| abil == this.currProfilo.tipo
			)
		);
	}

	constructor(
		private router: Router,
		private authService: BaseAuthService,
		private sessionManager: SessionManagerService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
	) {
		this.sessionManager.getCurrProfilo().then(
			(res: any) => {
				console.log(res);
				this.currProfilo = res;
			}
		);
		this.authService.getUserData().then(
			(userData: any) => {
				this.userData = userData;
			}
		);
		
		const breadcrumbModel = new BreadcrumbModel(
			[
				{
					icon: 'bi bi-house',
					url: ['/']
				},
				{
					label: 'Istanze',
					url: ['istanze']
				}
			],
			"Nuova"
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = "Crea Nuova Istanza";
	}

	ngOnInit(): void {
		const idxNull = Object.entries(this.userData).findIndex(([k, v]) => (v == null || v == undefined || v == "")) 
		if (idxNull >= 0) {
			if (confirm("Non è possibile creare una nuova istanza poiché, i dati del tuo account non sono completi! Vuoi inserirli adesso?")) {
				this.router.navigate(['account']);
			}
			else {
				this.router.navigate(['istanze']);
			}
		}

		this.authService.authFetch(
			'/istanze/lista-tipi-istanza'
		).then(
			(results: any) => {
				this.tipiIstanza = results.map(
					(x: any) => (
						{
							idTipo: x.id_cist,
							codTipoIsta: x.cod_tipo_istanza,
							abil: x.tipo_auth,
							seniorOnly: x.flag_senior,
							nome: x.nome,
							descrizione: x.descrizione_lunga,
							tipo_ente: x.tipo_ente
						}
					)
				);
			}
		);
		this.authService.authFetch(
			'/istanze/lista-tipi-istanza-specifici'
		).then(
			(results: any) => {

				this.mapSpecifici = {};

				results.forEach(
					(x: any) => {
						const prev: any[] = this.mapSpecifici[x.id_cist];
						const elem = {
							idCist: x.id_cist,
							idTipo: x.id_tipo_istanza,
							codTipoIsta: x.cod_tipo_istanza_specifico,
							nome: x.nome_istanza_specifico
						};
						if (prev) {
							prev.push(elem);
						}
						else {
							this.mapSpecifici[x.id_cist] = [elem];
						}
					}
				);
			}
		);
	}

	openWizard() {
		const configurazioneWizardArray = Object.entries(environment.wizardNuovaDomanda);
		this.arrayAllDomandeWizard = configurazioneWizardArray.map(item => {
			let key = item[0] as any;
			let value = item[1] as any;
			let scelteElement: SceltaWizard[] | null = null;
			if (value.scelte) {
				scelteElement = [
					new class implements SceltaWizard {
						nextOpzione: number = value.scelte.si;
						testoScelta: string = (environment.wizardNuovaDomanda as any)["" + value.scelte.si].desc;
					},

					new class implements SceltaWizard {
						nextOpzione: number = value.scelte.no;
						testoScelta: string = (environment.wizardNuovaDomanda as any)["" + value.scelte.no].desc;
					}
				];
			}

			let element = new class implements DomandaWizard {
				indexDomanda: number = +key;
				testoDomanda: string = value.desc;
			};

			if (value?.codTipoIstanza) {
				(element as any).codTipoIstanza = value?.codTipoIstanza;
			}

			if (scelteElement) {
				(element as any).scelte = scelteElement;
			}

			return element;
		});

		this.arrayCurrentDomandeWizard.length = 0;
		this.selectedWizardCodTipoIstanza = undefined;
		this.arrayCurrentDomandeWizard.push({ ...this.arrayAllDomandeWizard[0] });

		this.openWizardDialog = true;
	}

	annullaWizard() {
		this.openWizardDialog = false;
	}

	confermaWizard() {
		console.log("endWizard", this.selectedWizardCodTipoIstanza);
		if (this.selectedWizardCodTipoIstanza) {

			let tipoIstanzaToSelect = this.tipiIstanza.filter(value => {
				return value.codTipoIsta == this.selectedWizardCodTipoIstanza;
			});

			if (tipoIstanzaToSelect.length == 1) {
				const tipoIstanza = tipoIstanzaToSelect[0];
				if (this.canSee(tipoIstanza.seniorOnly, tipoIstanza.abil)) {
					//this.selTipoIstanza = tipoIstanza;
					this.setTipoInstanza(tipoIstanza);
					this.openWizardDialog = false;
				}
				else {
					alert("Non puoi avviare un istanza del tipo selezionato");
				}
			}
			else {
				alert("Non è possibile avviare un istanza di questo tipo");
			}
		}
	}
	wizardDidSelect(sceltaWizard: SceltaWizard, indexDomanda: number) {
		this.canSelWizard = false;
		const indexDomandaArray = indexDomanda + 1;
		const indexNextDomanda = sceltaWizard.nextOpzione - 1;
		this.arrayCurrentDomandeWizard.length = indexDomandaArray;
		const nextDomanda = { ...this.arrayAllDomandeWizard[indexNextDomanda] };
		this.arrayCurrentDomandeWizard.push(nextDomanda);
		this.selectedWizardCodTipoIstanza = nextDomanda.codTipoIstanza;
		if (this.selectedWizardCodTipoIstanza) {
			const tipoIst = this.tipiIstanza.find(x => x.codTipoIsta == this.selectedWizardCodTipoIstanza);
			if (tipoIst) {
				if (tipoIst.abil) {
					this.canSelWizard =  (!tipoIst.seniorOnly || this.currProfilo.isSenior) && tipoIst.abil.includes(this.currProfilo.authority);
				}
				else {
					this.canSelWizard = true;
				}
			}
		}
	}
}
