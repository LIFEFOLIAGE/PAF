import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { ComponentRecordType, ComponentType } from "../ComponentInterface";
import { SimpleObjectChange } from "../../../shared/editor-scheda/editor-scheda.component";
import { IstanzaComponentInterface } from "../../../interfaces/istanza-component-interface";
import { SessionManagerService } from "../../../../services/session-manager.service";
import { DataFormat } from 'src/app/modules/table/table.component';
import { SceltaVincolisticaWizard, VincolisticaWizard } from "./models/VincolisticaWizard";
import { Router } from '@angular/router';
import { ExportValue } from 'src/app/components/istanze/editor-istanza/editor-istanza.component';

const tipoAllegati: string = "application/pdf, image/*, .p7m";

@Component({
	selector: 'app-inquadramento-vincolistica',
	templateUrl: './inquadramento-vincolistica.component.html',
	styleUrls: ['./inquadramento-vincolistica.component.css']
})
export class InquadramentoVincolisticaComponent implements ComponentType<SimpleObjectChange>, OnInit, OnChanges {
	@Input() dati: ComponentRecordType = {};
	@Input() context: any;
	@Input() resources: any;
	@Input() isReadOnly: boolean = false;
	@Input() componentOptions: any;
	@Input() dictionariesData?: Record<string, any>;

	@Output() changeEdit: EventEmitter<boolean> = new EventEmitter<boolean>();
	@Output() dataChanged: EventEmitter<SimpleObjectChange> = new EventEmitter<SimpleObjectChange>();
	@Output() componentInit: EventEmitter<IstanzaComponentInterface> = new EventEmitter<IstanzaComponentInterface>();
	@Output() export: EventEmitter<ExportValue> = new EventEmitter<ExportValue>();

	isReady = false;
	//initialData: ComponentRecordType = {};
	currentData: ComponentRecordType = {};
	currentWizard: any = undefined;
	DataFormat = DataFormat;
	codIstanza!: string;
	tipoIstanza?: string;
	datiSovrapposizioni: any[] = [];
	avvisiArray: string[] = [];

	arrayAllDomandeWizard: VincolisticaWizard[] = [];
	arrayCurrentDomandeWizard: VincolisticaWizard[] = [];
	selectedWizardLink?: string;

	isWizardReadonly = true;

	//datiForm: any = {};
	readonly uploadVincaForm = {
		"display": "form",
		"components": [
			{
				"label": "Elaborato VIncA",
				"tableView": false,
				"storage": "base64",
				"webcam": false,
				"capture": false,
				"filePattern": tipoAllegati,
				"key": "fileVinca",
				"conditional": {
					"show": true
				},
				"fileMaxSize": "5MB",
				"type": "file",
				"input": true
			}
		]
	};

	elementiWizard: any[] = [];
	nextElementoWizard: any;
	errori: Record<string, (string|boolean)> = {};

	constructor(
		private sessionManager: SessionManagerService,
		private router: Router
	) {
	}

	ngOnChanges(changes: SimpleChanges): void {
		let scelteUtente: string[]|undefined = undefined;
		for (let propName in changes) {
			const v = changes[propName].currentValue;
			switch (propName) {
				case "dati": {
					//this.initialData = { ...v };
					this.currentData = { ...v };
					scelteUtente = (v.scelteUtente??[]).slice();
					//this.currentData = { scelteUtente: scelteUtente};
				}; break;
				case "context": {
					this.codIstanza = v.codIstanza;
					this.tipoIstanza = v.codTipoIstanzaSpecifico;
					scelteUtente = (this.currentData['scelteUtente']??[]).slice();
				}
			}
		}
		this.componentInit.emit({ getValidity: this.getValidity.bind(this) });

		if (scelteUtente) {
			this.loadWizard().then(
				() => {
					if (scelteUtente) {
						scelteUtente.forEach(
							(v: string) => {
								this.registraUltimaRisposta(v);
							}
						);
					}
					this.dataChanged.emit({});
				}
			);
		}
	}


	ngOnInit(): void {
		this.componentInit.emit({ getValidity: this.getValidity.bind(this) });
	}

	private resetRisposte() {
		delete this.errori['wizard'];
		const arrScelte = this.currentData['scelteUtente'];
		arrScelte.length = 0;
		this.elementiWizard.length = 0;
		this.nextElementoWizard = this.currentWizard[1];
	}
	registraRisposta(risposta: string, idxRisposta: number) {
		const arrScelte = this.currentData['scelteUtente'];
		arrScelte.length = idxRisposta;
		this.nextElementoWizard = this.elementiWizard[idxRisposta];
		this.elementiWizard.length = idxRisposta;
		this.registraUltimaRisposta(risposta);
	}
	registraUltimaRisposta(risposta: string) {
		delete this.errori['wizard'];
		try {
			if (this.nextElementoWizard) {
				const scelte = this.nextElementoWizard.scelte;
				if (scelte) {
					const arrScelte = this.currentData['scelteUtente'];
					const destRisposta = scelte[risposta];
					const nextElem = this.currentWizard[destRisposta];
					if (nextElem != undefined) {
						this.elementiWizard.push(this.nextElementoWizard);
						arrScelte.push(risposta);
						this.nextElementoWizard = nextElem;
						const fileVinca = nextElem.link == 'upload-vinca' ? this.currentData['fileVinca'] : undefined;
						this.dataChanged.emit({...this.currentData, scelteUtente: arrScelte, fileVinca});
					}
					else {
						throw new Error("Problema nella configurazione del wizard - risposta non prevista");
					}
				}
				else {
					throw new Error("L'elemento corrente non prevede risposte");
				}
			}
		}
		finally {
			this.checkErrori();
		}
	}


	private loadWizard() {
		this.isReady = false;
		if (this.codIstanza) {
			return this.sessionManager.profileFetch(
				`/istanze/${this.codIstanza}/inquadramento-vincolistica`
			).then(
				(results: any) => {
					this.datiSovrapposizioni = results.sovrapposizioni;
					this.avvisiArray = results.avvisi;
					const tipoWizard = results.tipoWizard;
					if (this.tipoIstanza != undefined && !['IN_DEROGA', 'ATTUAZIONE_PIANI'].includes(this.tipoIstanza)) {
						this.currentWizard = this.componentOptions.wizards[tipoWizard];
						this.resetRisposte();
						const arrScelte = this.dati['scelteUtente'];
						if (arrScelte != undefined) {
							arrScelte.forEach(
								(v: string, idx: number) => {
									this.registraUltimaRisposta(v);
								}
							);
						}
					}
					else {
						this.currentWizard = undefined;
					}

					this.isReady = true;
				},
				(err) => {
					console.log("errore recupero vincolistica istanza", err);
				}
			);
		}
		else {
			this.isReady = true;
			return Promise.resolve();
		}
	}

	trasformaInIstanzaInDeroga() {
		this.sessionManager.profileCsrsFetch(
			`/istanze/${this.codIstanza}/trasforma`,
			{
				method: "POST",
				body: JSON.stringify('IN_DEROGA'),
				credentials: 'include'
			}
		).then(
			() => {
				alert("L'istanza è stata variata correttamente");
				this.router.navigate(['istanze', this.codIstanza]);
			},
			(err) => {
				alert("Si è verificato un problema nella variazione dell'istanza");
			}
		);
	}

	setHasVisioneVincoli(event: boolean) {
		delete this.errori['wizard'];
		this.currentData['hasVisioneVincoli'] = event;
		this.checkErrori();
		this.dataChanged.emit({...this.currentData});
	}

	checkErrori() {
		let errore: (string | undefined) = undefined
		if (this.datiSovrapposizioni.length != 0 && this.currentWizard != undefined) {
			const nextWizard = this.nextElementoWizard;
			if (nextWizard == undefined) {
				errore = 'Stato imprevisto del questionario';
			}
			else {
				const link = nextWizard.link;
				if (link == undefined) {
					errore = 'Questionario non completato';
				}
				else {
					switch (link) {
						case "upload-vinca": {
							const fileVinca = this.currentData['fileVinca'];
							if (fileVinca == undefined) {
								errore = "Occorre allegare una copia dell'elaborato VIncA";
							}
							else {
								if (fileVinca.length == 0) {
									errore = "Occorre allegare una copia dell'elaborato VIncA";
								}
								else {
									errore = undefined;
								}
							}
						}; break;
						case "link-deroga": {
							errore = 'Le risposte date al questionario non permettono di proseguire senza variare il tipo di istanza';
						}; break;
					}
				}
			}
		}
		else {
			if (!this.currentData['hasVisioneVincoli']) {
				errore = 'Occorre indicare di aver preso visione';
			}
		}
		if (errore == undefined) {
			delete this.errori['wizard'];
		}
		else {
			this.errori['wizard'] = errore;
		}
	}
	getValidity: () => boolean = () => {
		this.checkErrori();
		if (this.errori['wizard']) {
			return false;
		}
		else {
			return true;
		}
	};

	onDataFormChanged(changes: any) {
		delete this.errori['wizard'];
		const fileVinca = changes;
		console.log("changes", fileVinca);

		this.currentData = { ...this.currentData, ...changes }
		this.dataChanged.emit(this.currentData);
	}
}
