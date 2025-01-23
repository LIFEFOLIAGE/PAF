import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges, ViewChildren, QueryList, ElementRef, AfterViewInit } from '@angular/core';
import { IstanzaComponentInterface } from "../../interfaces/istanza-component-interface";
import { ExportValue } from '../../istanze/editor-istanza/editor-istanza.component';

export enum TipoDatiScheda {
	Array,
	Object
}

export type Scheda = {
	tipoDati: TipoDatiScheda,
	tipo: string,
	nome: string,
	conf: any,
	ignoredProps?: string[],
	dictionariesData?: Record<string, any>
}

export type ArrayValueChange = {
	initialValue: any,
	currentValue: any
}


// Type per rappresentare i cambiamenti in un array:
// inserimenti: array inserted
// cancellazioni: merges con valori undefined
// aggiornamenti: merges con il nuovo contenuto nei valori
export type TableChange = {
	merges?: Record<number, any>,
	inserted?: (any[]|undefined)
};


// Type per cambiamenti semplici in un array:
// inserimento singolo-> pos: undefined, value: nuovo valore
// aggiornamento singolo-> pos: posizine record da aggiornare, value: nuovo contenuto del record
// eliminazione singola -> pos: posizione record da eliminare, value: undefined
export type SimpleTableChange = {
	pos?: number,
	value?: any
};

// Type per cambiamenti semplici in un oggetto
// key: proprietà da aggiornare
// value: nuovo valore da impostare
export type SimpleObjectChange = Record<(string|number|symbol), any>;



@Component({
	selector: 'app-editor-scheda',
	templateUrl: './editor-scheda.component.html',
	styleUrls: ['./editor-scheda.component.css']
})
export class EditorSchedaComponent implements OnChanges, AfterViewInit {
	@Input() contesto: any;
	@Input() scheda?: Scheda;
	@Input() dati?: any;
	@Input() isReadOnly: boolean = false;
	@Input() dictionariesData?: Record<string, any>;
	@Input() isNewData: boolean = false;
	@Input() resources: any;
	//@Input() dati: any;
	//public changed: boolean = false;
	objChanges: Record<string, any> = {};
	arrChanges: Record<number, ArrayValueChange> = {};
	initialArrValues: Record<number, any> = {};
	private formController: any;
	onEdit: boolean = false;
	@Input() importedValue: any;

	@ViewChildren('container') elementRef!: QueryList<ElementRef>;
	

	//@Output() dataChanged = new EventEmitter<any>();
	//@Output() dataConfirmed = new EventEmitter<any>();
	@Input() dataChanged: (x: any) => Promise<void> = (x) => Promise.resolve();
	@Input() dataConfirmed: (x: any) => Promise<void> = (x) => Promise.resolve();
	@Input() cancelChanges: (x: any) => Promise<void> = (x) => Promise.resolve();

	@Output() elementCreated: EventEmitter<QueryList<ElementRef>> = new EventEmitter<QueryList<ElementRef>>();

	private _istanzaComponentInterface?: IstanzaComponentInterface;
	currentData: any;
	constructor() {
		console.log("editorScheda");
	}
	
	ngAfterViewInit() {
		this.elementCreated.emit(this.elementRef);
	}

	  
	// public getRealChanges() : (Record<string, any> | Record<number, ArrayValueChange> | undefined) {
	// 	const ignore = this.scheda.ignoredProps;
	// 	switch (this.scheda.tipoDati) {
	// 		case TipoDatiScheda.Object: {
	// 			if (ignore == undefined) {
	// 				return this.objChanges;
	// 			}
	// 			else {
	// 				if (this.objChanges == undefined) {
	// 					return this.objChanges;	
	// 				}
	// 				else {
	// 					return Object.keys(this.objChanges).filter((x:any) => !ignore.includes(x) );
	// 				}
					
	// 			}
	// 		}; break;
	// 		case TipoDatiScheda.Array: {
	// 			return this.arrChanges;
	// 		};
	// 	}

	// }

	getRealObjChanges(changes?: Record<string, any>) : (Record<string, any> | undefined) {
		if (this.scheda) {
			const ignore = this.scheda.ignoredProps;
			switch (this.scheda.tipoDati) {
				case TipoDatiScheda.Object: {
					if (ignore == undefined) {
						return changes;
					}
					else {
						if (changes == undefined) {
							return changes;	
						}
						else {
							return Object.fromEntries(Object.entries(changes).filter(([k, v]:[string, string]) => !ignore.includes(k)));
							//return Object.keys(changes).filter((x:any) => !ignore.includes(x) );
						}
						
					}
				}; break;
				case TipoDatiScheda.Array: {
					throw new Error("Bad call");
				};
			}
		}
		else {
			throw new Error("Bad call");
		}
	}
	public get changed () : boolean {
		if (this.isNewData) {
			return true;
		}
		else {
			if (this.scheda) {
				const changes = (this.scheda.tipoDati == TipoDatiScheda.Object) ? this.objChanges : this.arrChanges;
				if (changes == undefined) {
					return false;
				}
				else {
					return Object.keys(changes).length > 0;
				}
			}
			else {
				throw new Error("Bad call");
			}
		}
	}

	private initialData?: string;
	ngOnChanges(changes: SimpleChanges) {
		console.log("ngOnChanges");
		console.log({schedaChanges: changes});
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case "scheda": {
					this.formController = undefined;
					this.onEdit = false;
				}; break;
				case "dati": {
					this.currentData = currValue;
					this.initialData = JSON.stringify(currValue);
				}; break;
			}
		}

	}

	onFormInit(formController: any) {
		this.formController = formController;
	}
	invalidLines: Set<number>  = new Set<number>();

	setEdit(val: boolean) {
		//console.log({setEdit: val});
		this.onEdit = val;
	}

	handleTableChange(isValid: boolean, pos: number, value: any) {

		let eleChange = null;
		if (pos >= 0) {
			eleChange = this.arrChanges[pos];
			if (eleChange) {
				eleChange.currentValue = value;
			}
			else {
				this.arrChanges[pos] = eleChange = {
					initialValue: this.dati[pos],
					currentValue: value
				}
			}
			if (value) {
				if (!this.dati[pos]) {
					const msg = 'Rilevato aggiornamento di un elemento eliminato';
					throw new Error(msg);
				}

				// aggiornamento record
				let newDati = this.dati.slice();
				newDati[pos] = value;
				this.dati = newDati;

				if (isValid != undefined && !isValid) {
					this.invalidLines.add(pos);
				}
				else {
					this.invalidLines.delete(pos);
				}
			}
			else {
				// cancellazione record

				//this.dati = this.dati.filter((v: any, idx: number) => (idx != pos));

				let newDati = this.dati.slice();
				newDati[pos] = undefined;
				this.dati = newDati;
			}
		}
		else {

			// inserimento nuovo record
			if (value) {
				let newDati = this.dati.slice();
				newDati.push(value);

				if (!isValid) {
					this.invalidLines.add(newDati.length);
				}
				this.dati = newDati;
				eleChange = this.arrChanges[newDati.length] = {
					initialValue: undefined,
					currentValue: value
				}
			}
			else {
				// non si dovrebbe mai aggiungere un elemento null
			}

		}
		console.log({eleChange});
		return eleChange;
	}

	onTableChanged(changes: any[]) {
		const emChanges = changes.map(
			(change: any) => {
				const {pos, value} = change;
				return this.handleTableChange(true, pos, value);
			}
		);
		//this.dataChanged.emit(emChanges);
		this.dataChanged(emChanges);
	}
	onDataChanged(changes: any) {
		if (this.scheda) {
			console.log(changes);
			switch (this.scheda.tipoDati) {
				case TipoDatiScheda.Object: {
					this.objChanges = this.getRealObjChanges(changes) as SimpleObjectChange;
					//this.dataChanged.emit(changes);
					if (this.objChanges != undefined) {
						this.dataChanged(this.objChanges);
					}
				}; break;
				case TipoDatiScheda.Array: {
					// TODO:
					const {pos, value} = changes as SimpleTableChange;
					const change = this.handleTableChange(true, pos??-1, value);
	
					//this.dataChanged.emit([change]);
					this.dataChanged([change]);
				}
			}
		}
		else {
			throw new Error("Bad call");
		}
	}
	resetChanges() {
		console.log("resetChanges");
		this.objChanges = {};
		this.arrChanges = {};

		//this.dataChanged.emit(undefined);
		this.dataChanged(undefined);
	}
	confermaDati() {
		if (this.scheda) {
			let validityFromComponent = this._istanzaComponentInterface?.getValidity();
			console.log("validityFromComponent", validityFromComponent);
			let errorMessage: string = "";
			// let isValid: boolean  = true;
			// if (validityFromComponent != undefined) {
			// 	if (typeof validityFromComponent === 'string') {
			// 		errorMessage = validityFromComponent;
			// 		isValid = errorMessage == "";
			// 	} else {
			// 		isValid = validityFromComponent;
			// 	}
			// }
			if (!validityFromComponent) {
				let confirmMessage = "Non è possibile proseguire con il salvataggio. I dati non sono validi!"
				if (errorMessage.length > 0) {
					confirmMessage += `\n${errorMessage}`
				}
				//isValid = 
				alert(confirmMessage)
				throw new Error(confirmMessage);
			}
			switch (this.scheda.tipoDati) {
				case TipoDatiScheda.Object: {
					// let isValid: boolean = this.formController == undefined ? true : this.formController.checkValidity(null, false, null, true);
					// if (isValid || confirm("I dati non sono validi, vuoi confermare comunque?")) {
					//let oldDati = this.dati;
					let newDati = { ...this.dati, ...this.objChanges };
					
					//this.dataConfirmed.emit({modifiche: this.objChanges, dati: newDati});
					//this.dati = newDati;
					//this.resetChanges();
					this.dataConfirmed({ modifiche: this.objChanges, dati: newDati }).then(
						() => {
							this.dati = newDati;
							this.resetChanges();
						},
						// (e) => {
						// 	this.dati = oldDati
						// }
					);
					// }
				};break;
				case TipoDatiScheda.Array: {
					// TODO:
					const saveData = (this.dati as any[]);
					if (saveData != undefined) {
						if (this.invalidLines.size == 0) {
							const effData = saveData.filter(x => x != undefined);
	
							let errMess: string|undefined = undefined;
							const maxSize = this.scheda.conf.maxSize;
							if (maxSize != undefined) {
								const value = maxSize.value;
								if (value != undefined && effData.length > value) {
									errMess = maxSize.errorMessage??`Quest'elenco non può avere più di ${value} elementi!`;
								}
							}
							if (errMess == undefined) {
								const minSize = this.scheda.conf.minSize;
								if (minSize != undefined) {
									const value = minSize.value;
									if (value != undefined && effData.length < value) {
										errMess = minSize.errorMessage??`Quest'elenco non può avere meno di ${value} elementi!`;
									}
								}
							}
		
							if (errMess == undefined) {
								let newDati = [...saveData];
								this.dati = newDati;
			
								//this.dataConfirmed.emit({modifiche: this.arrChanges, dati: newDati});
								//this.resetChanges();
			
								this.dataConfirmed({ modifiche: this.arrChanges, dati: newDati }).then(
									() => {
										this.resetChanges();
									}
								);
							}
							else {
								alert(errMess);
							}
						}
						else {
							alert("Occorre correggere i dati invalidi");
						}
					}
					else {
						alert("Si è verificato un problema");
					}
				}
			}
		}
		else {
			throw new Error("Bad call");
		}
	}
	annullaModifiche() {
		if (this.initialData) {
			let newDati = JSON.parse(this.initialData);
			this.dati = newDati;
			this.resetChanges();
		}
		else {
			throw new Error("No initial data");
		}
		this.cancelChanges(this.dati);
	}

	onComponentInit($event: IstanzaComponentInterface) {
		this._istanzaComponentInterface= $event;
	}

	@Output() export = new EventEmitter<ExportValue>();
	onExport(value: ExportValue) {
		console.log(value);
		this.export.emit(value);
	}
}
