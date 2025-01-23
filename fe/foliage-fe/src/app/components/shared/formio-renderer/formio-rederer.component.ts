import {
		Component, Input, ElementRef,
		OnChanges, SimpleChanges,
		Output, EventEmitter, OnInit
	} from '@angular/core';
import { getUrl } from './url-provider';
import { Formio, Templates, Providers, Utils  } from 'formiojs';
import { BaseAuthService, CsrsToken } from 'src/app/services/auth.service';
import { deepClone } from 'src/app/services/utils';
import { environment } from 'src/environments/environment';
import { IstanzaComponentInterface } from '../../interfaces/istanza-component-interface';
import { SessionManagerService } from 'src/app/services/session-manager.service';
import { modifyTemplate } from './patch/modify-template';

//console.log({Providers});

const opts = {
	language: 'it',
	i18n: {
		it: {
			"Add Another": "Aggiungi",
			"Add/Remove": "",
			"Actions": "Azioni",
			"File Name": "Nome File",
			"Size": "Dimensione",
			"Drop files to attach,": "Sposta qui i file da allegare,",
			"or": "oppure",
			"browse": "sfoglia",
			"Do you want to clear data?": "Vuoi annullare?",
			"Yes, delete it": "Si",
			"Cancel": "No",
			'File is the wrong type; it must be {{ pattern }}': "Tipologia del file non supportata. I formati accettati sono: {{pattern}}",
			invalid_email: "{{field}} deve essere un'email valida",
			required: '{{field}} è necessario'
		}
	}
}

function findParentInput(inst: any) : any {
	if (inst) {
		const parent = inst.parent;
		if (parent) {
			if (parent.component.input) {
				return parent;
			}
			else {
				return findParentInput(parent);
			}
		}
		else {
			return inst;
		}
	}
	else {
		throw new Error("Elemento non definito");
	}
}

function getElementPath(inst: any) : string[] {
	const key = inst.component.key;
	if (key) {
		const parent = findParentInput(inst);
		if (parent && parent !== inst) {
			return [...getElementPath(parent), key];
		}
		else {
			return [key];
		}
	}
	else {
		return [];
	}
}

Templates.framework = 'boostrap5';
let template = Templates.current;


//console.log("Questo è il template");
//console.log(template);

modifyTemplate(template);


//console.log("Questo è il nuovo template");
//console.log(template);


template.defaultIconset = 'bi'

const failureCallback = (e: Error) => {
	console.error(e);
	alert("Errore");
};

@Component({
	selector: 'app-formio-renderer',
	//templateUrl: './formio-renderer.component.html',
	template: ``,
	styleUrls: ['./formio-renderer.component.css']
})
export class FormioRendererComponent implements OnChanges, OnInit, IstanzaComponentInterface {
	private formController : any  = null;
	private datiModificati : boolean = false;
	//private nonCumChanges : any = {};
	private nonCumChanges : Record<string, any> = {};
	private cumChanges : Record<string, any>[] = [];
	// private currProfilo: any = undefined;

	@Input() isReadOnly: boolean = false;
	@Input() form?: Object;
	@Input() formData?: Object;
	@Input() dictionariesData?: Record<string, any>;
	@Input() context: any;

	@Output() dataChanged = new EventEmitter<any>();
	@Output() componentInit: EventEmitter<IstanzaComponentInterface> = new EventEmitter<IstanzaComponentInterface>();

	private profiloProm: Promise<any>;

	constructor(
		private nativeElement: ElementRef,
		private authService: BaseAuthService,
		private sessionManager: SessionManagerService
	) {
		this.profiloProm =sessionManager.getCurrProfilo();
		// this.profiloProm.then(
		// 	(prof: any) => {
		// 		console.log(prof);
		// 		this.currProfilo = prof;
		// 	}
		// );
	}
	ngOnInit(): void {
		this.componentInit.emit({getValidity: this.getValidity.bind(this)})
    }

	ngOnChanges(changes: SimpleChanges) {
		console.log(changes);
		//let formData : object | null = changes['formData']?.currentValue;


		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case "context": {
					this.authService.getCsrsToken().then(
						csrsToken => {
							Providers.providers.storage["url"] = getUrl(
								{
									dictionariesData: this.dictionariesData,
									context: currValue,
									environment: environment,
									getAccessToken: this.getAccessTokenBuond,
									csrsToken: csrsToken
								}
							);
						}
					);
				}; break;
			}
		}
		this.drawForm();
	}

	private onChange(subData: any, arg1: any, arg2: any, arg3: any, arg4: any) : void {
		console.log("onChange");
		if (!arg2 || !(arg2.fromSubmission == true)) {
			this.datiModificati = true;
			console.count("change");
			console.log({subData, arg1, arg2, arg3, arg4});
			const changed = arg2.changed;
			const instFired = arg2.changed.instance;
			if (instFired) {
				const parInput = findParentInput(instFired);
				console.log("Parent input:");
				console.log(parInput);

				if (parInput.component.type == 'form') {
					//creazione array elementi modificati
					const component = changed.component;
					let currChanges = arg2.changes.map(
							(c: any) => (
								[c.component.key, c.value]
							)
						);
					console.log("modifiche appena rilevate:");
					console.log(currChanges);

					console.log("dati iniziali");
					console.log(subData);

					// creazione array elementi ripristinati al valore iniziale
					let restores = currChanges.filter(
						([k, v]: [string, any]) => (!(typeof subData[k] === 'object') && (v === subData[k] || (!v && !subData[k])))
					);
					console.log("Ripristinati:");
					console.log(restores);

					currChanges.forEach(
						([k, v]: [string, any]) => {
							console.log(
								{
									v, k,
									vv: subData[k],
									ck: (v === subData[k])
								}
							);
						}
					);

					console.log(
						currChanges.map(
							([k, v]: [string, any]) => (
								{
									v, k,
									vv: subData[k],
									ck: (v === subData[k])
								}
							)
						)
					);

					// aggiornamento modifiche rilevate ed eliminazione delle modifiche che hanno ripristinato i vecchi valori
					const newChange = Object.fromEntries(currChanges);

					this.nonCumChanges = {...this.nonCumChanges, ...newChange};

					this.cumChanges.push(newChange);
					restores.forEach(([k, v]: [string, any]) => {
							delete this.nonCumChanges[k];
						}
					);
				}
				else {
					const path : string[] = getElementPath(parInput);
					console.log("Path modifica");
					console.log(path);
					const key = path[0];
					const value = arg1.data[key];
					const newChange = Object.fromEntries([[key, value]]);
					console.log({newChange});
					this.nonCumChanges = {...this.nonCumChanges, ...newChange};
				}

				console.log("modifiche cumulate:");
				console.log(this.nonCumChanges);

				// generazione evento di cambiamento
				this.dataChanged.emit(this.nonCumChanges);
			}
			else {
				throw new Error("Instanza modificata non trovata");
			}

		}
	};
	deepClone = deepClone;
	getFormPromise(options: any) : Promise<any> {
		const opts = options as Object;
		console.log(opts);
		return Formio.createForm(
			this.nativeElement.nativeElement,
			this.form as Object,
			opts
		);
	}

	getAccessToken() {
		if (this.authService) {
			return `Bearer ${this.authService.getAccessToken()}`;
		}
		else {
			return undefined;
		}
	}
	getAccessTokenBuond = this.getAccessToken.bind(this);
	thenCallback (formIo: any) {
		this.formController = formIo;

		let subData : any = this.formData ? this.deepClone(this.formData) : {};
		console.log({formSchema: formIo.schema, subData});

		this.profiloProm.then(
			(prof: any) => {
				const metadata = {
					dictionariesData: this.dictionariesData,
					context: this.context,
					environment: environment,
					getAccessToken: this.getAccessTokenBuond,
					authority: prof.authority,
					authScope: prof.ambito
				};
				console.log({metadata});
				formIo.submission = {
					data: subData,
					metadata
				};
		
				const callChange : (arg1 : any, arg2 : any, arg3 : any, arg4 : any) => any = (arg1, arg2, arg3, arg4) => {
					this.onChange(subData, arg1, arg2, arg3, arg4);
				};
				const idChange = formIo.on('change', callChange);
				console.log(formIo.ready);
				formIo.ready.then(
					() => {
						formIo.redraw();
						console.log("ok");
					},
					(e: any) => {
						console.error(e);
					}
				);
			}
		);
	}
	thenCallbackBound = this.thenCallback.bind(this);

	drawForm() {
		console.log("drawForm");
		this.nonCumChanges = {};

		const options: any = {...opts, readOnly: this.isReadOnly};
		this.profiloProm.then(
			(prof: any) => {

				options.submission = {
					dictionariesData: this.dictionariesData,
					context: this.context,
					environment: environment,
					getAccessToken: this.getAccessTokenBuond,
					authority: prof.authority,
					authScope: prof.ambito
				};
		
				if (
					this.nativeElement.nativeElement != null
					&& this.form != null
				) {
					// console.log(
					// 	{
					// 		form: this.form,
					// 		data: this.formData
					// 	}
					// );
					this.datiModificati = true;
					//this.nativeElement.nativeElement.style.padding = "15px";
		
		
					let promise : Promise<any> = this.getFormPromise(options);
		
					promise.then(
						this.thenCallbackBound,
						failureCallback
					);
				}
			}
		);

	}


	getValidity: () => boolean = () => {
		return this.formController && this.formController.checkValidity(null, true, null, false);
	}
}
// function _interopRequireDefault(module: any) : any {
// 	const
// 		isCJSModule = module && module.__esModule,
// 		cjsStyedModule = { default: module };

// 	return isCJSModule ? module: cjsStyedModule;
// }
