import { DOCUMENT } from '@angular/common';
import {
	Component,
	Input,
	Directive,
	ContentChild,
	ContentChildren,
	TemplateRef,
	OnChanges,
	SimpleChanges,
	OnDestroy,
	Output,
	EventEmitter,
	OnInit,
	Renderer2,
	ElementRef,
	ViewChild,
	Inject,
	AfterViewInit
} from '@angular/core';
import { TableRowComponent } from './table-row/table-row.component';
import { LocalDateTime, DateTimeFormatter, LocalDate } from '@js-joda/core';
import { BaseIstanzaComponent, IstanzaComponentInterface } from 'src/app/components/interfaces/istanza-component-interface';
import { TipoDatiScheda } from 'src/app/components/shared/editor-scheda/editor-scheda.component';
import { BaseAuthService } from 'src/app/services/auth.service';
import { deepClone } from 'src/app/services/utils';


@Component({
	selector: 'app-page-button',
	template: `<button type="button" (click)="this.onClick()" class="btn pulsante" style="margin-left: auto" [disabled]="disabled">{{this.text}}</button>`,
	styles: [
		`
.pageButton {
	background: none;
	border: none;
	cursor: pointer;
	margin-left: 5px;
	margin-right: 5px;
	text-decoration: underline;
}
		`,
		`
.pageButton:disabled {
	cursor: initial;
	margin-left: 10px;
	margin-right: 10px;
	text-decoration: unset;
}
		`
	]
})
export class PageButtonComponent {
	@Input() disabled!: boolean;
	@Input() text: string = "";
	@Output() click = new EventEmitter<void>();
	onClick() {
		console.log("click");
		this.click.emit();
	}
}


const sortIcons : Record<string, string> = {
	"-1": "sort-desc-header",
	"0": "sort-header",
	"1": "sort-asc-header"
};

const sortIcons2 : Record<string, string> = {
	"-1": "/assets/images/chevron-bar-down.svg",
	"0": "/assets/images/chevron-bar-expand.svg",
	"1": "/assets/images/chevron-bar-up.svg"
};


class SortConf {
	sortField?: number;
	sortDirection: number = 0;

	getIcon(): string {
		return sortIcons[this.sortDirection];
	}
	
	getIcon2(): string {
		return sortIcons2[this.sortDirection];
	}
}

export enum DataFormat {
	LongDate = "LongDate",
	ShortDate = "ShortDate",
	Decimal = "Decimal",
	Integer = "Integer",
	Ettari = "Ettari",
	Default = "Default"
}

export type RowData = {
	idx: number,
	data: any
};
export type RowEvent = (event: Event, row: any, idxRow: number) => void;
export interface RowHandler {
	selection: boolean;
	highlight: boolean;
	click?: RowEvent;
	enter?: RowEvent;
	leave?: RowEvent;
}

export enum TableMenuStdAction {
	New = "New"
};

export enum TableRowMenuStdAction {
	Remove = "Remove",
	Edit = "Edit",
	View = "View"
}

export type TableRowMenuAction = (TableRowMenuStdAction | ((data : RowData[], idx: number) => void ));
export type TableMenuAction = (TableMenuStdAction | ((data : RowData[]) => void ));

export type TableMenuOptions = {
	element: {
		label: string,
		action: TableRowMenuAction
	}[],
	general: {
		label: string,
		action: TableMenuAction
	}[]
}

// effettua le chiamate ai gestori di un evento in cascata
const pipeRowEvents = (...fns: RowEvent[]) => (event: Event, row: any, idxRow: number) => fns.reduce(
		(vPrev: any, f: RowEvent) => f(event, row, idxRow), undefined
	);


@Directive({
	selector: 'column'
})
export class Column /*implements AfterViewInit*/{
	@Input() headerText? : string;
	@Input('cellTemplate') cellTemplate!: TemplateRef<any>;
	@Input('headerTemplate') headerTemplate!: TemplateRef<any>;
	@Input('cellValue') cellValue?: (rowData: any, resources: any) => string;
	@Input() sortEnabled: boolean = false;
	@Input() dataField?: string;
	@Input() dataFormat?: DataFormat;
	@Input() colHeadSpan?: string;

	constructor(
	) {
	}

	// ngAfterViewInit() {
	// 	console.log('Values on ngAfterViewInit():');
	// 	console.log({cellTemplate:this.cellTemplate});
	// }


	public formatValue(row: any, resources: any) {
		const inpVal = this.dataField ? row[this.dataField] : row;
		if (this.cellValue) {
			return this.cellValue(inpVal, resources);
		}
		else {
			return (this.dataField) ? ((this.dataFormat) ? TableComponent.dataFormats[this.dataFormat](row[this.dataField]) : row[this.dataField]) : "";
		}
	}
}


@Directive({
	selector: 'columns'
})
export class Columns {
	_columnsList: Column[] = [];
	@ContentChildren(Column) set columnsList(value: Column[]){
		this._columnsList = [...value];
	};
	get columnsList(): Column[] {
		return this._columnsList;
	}
}


function valueFilter(data: any, text: string) : boolean{
	const res : boolean = data && data.toString().includes(text);
//	console.log({valueFilter: {data, text, res }});
	return res;
}
function arrayFilter(data: any[], text: string) : boolean {
	const res : boolean = data && data.find(x => valueFilter(x, text) /* x && x.toString().includes(text)*/);
//	console.log({arrayFilter: {data, text, res }});
	return res;
}
function objectFilter(data: object, text: string) : boolean {
	const res : boolean = data && arrayFilter(Object.values(data), text); //.find(x => valueFilter(x, text) /* x && x.toString().includes(text)*/);
//	console.log({objectFilter: {data, text, res }});
	return res;
}
function columnFilter(columns: Column[], data: object, text: string, resources: any) {
	const res = data && columns && arrayFilter(columns.map(col => (col.formatValue(data, resources))), text);
//	console.log({objectFilter: {data, text, res }});
	return res;
}

export interface PaginationOptions {
	pageSizes: number[],
	defaultPageSize?: number
}

export const UnlimitedPage : number = 0;



function compare(de1: any, de2: any, sortDirection: number) {
	if (de1 && de2) {
		if (de1 == de2) {
			return 0;
		}
		else if (de1 < de2) {
			return -sortDirection;
		}
		else if (de1 > de2) {
			return sortDirection;
		}
		else {
			return 0;
		}
	}
	else {
		if (de1) {
			return sortDirection;
		}
		else if (de2) {
			return -sortDirection;
		}
		else {
			return 0;
		}
	}
}

export const RowEventNames : Record<string, string> = {
	rowSelection: "click",
	rowEnter: "mouseenter",
	rowLeave: "mouseleave"
}

// @Component({
// 	selector: '[app-row]',
// 	templateUrl: './table-row/table-row.component.html',
// 	styleUrls: ['./table.component.css']
// })
// export class TableRowComponent implements OnChanges, OnDestroy {
// 	// formattatore di date
// 	static df = DateTimeFormatter.ofPattern('dd/MM/yyyy');
// 	// formattatore di date con orario
// 	static longDf = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm:ss');
// 	static parseJodaDate(x: any) {
// 		let dt : (LocalDateTime | undefined) = undefined;
// 		let d : (LocalDate | undefined) = undefined;
// 		try {
// 			d = LocalDate.parse(x, DateTimeFormatter.ISO_LOCAL_DATE);
// 		}
// 		catch(e) {
// 			try {
// 				dt = LocalDateTime.parse(x, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
// 			}
// 			catch(eFin) {
// 				console.error(eFin);
// 			}
// 		}
// 		//const t = LocalDateTime.parse(x, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
// 		if (d) {
// 			return d;
// 		}
// 		else {
// 			if (dt) {
// 				const ov = dt.toLocalDate()
// 				return ov;
// 			}
// 			return undefined;
// 		}
// 	}

// 	static parseJodaLongDate(x: any) {
// 		let dt : (LocalDateTime | undefined) = undefined;
// 		dt = LocalDateTime.parse(x, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
// 		//const t = LocalDateTime.parse(x, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
// 		if (dt) {
// 			return dt;
// 		}
// 		else {
// 			return undefined;
// 		}
// 	}

// 	public formatValue(row: any, idx: number) {
// 		const cols = this.columns;//Array.from(this.columns);
// 		const col = cols[idx];
// 		return col.formatValue(row, this.resources);
// 	}
// 	DataFormat = DataFormat;
// 	@Input() rowSelection?: RowEvent;
// 	@Input() rowEnter?: RowEvent;
// 	@Input() rowLeave?: RowEvent;
// 	@Input() show: boolean = false;
// 	@Input() data: any;
// 	@Input() index!: number;

// 	@Input() rowErrors?: { icon: string, message: string }[] = [];

// 	// _columns: Column[] = []
// 	// @Input() set columns(value: Column[]) {
// 	// 	this._columns = [...value];
// 	// }
// 	// get columns(): Column[] {
// 	// 	return this._columns;
// 	// }
// 	@Input() columns: Column[] = [];
// 	@Input() resources: any;

// 	undoEvents: Record<string, undefined | (() => void)> = {
// 		rowSelection: undefined,
// 		rowEnter: undefined,
// 		rowLeave: undefined
// 	}

// 	constructor(private renderer: Renderer2,
// 				private elementRef: ElementRef) {
// 	}

// 	ngOnDestroy() {
// 		Object.values(this.undoEvents).forEach(
// 			x => {
// 				if (x) {
// 					x();
// 				}
// 			}
// 		);
// 	}

// 	ngOnChanges(changes: SimpleChanges): void {
// 		const x : any = this;
// 		//console.log({rowChanges: changes, "this": x});
// 		Object.entries(this.undoEvents).forEach(
// 		 	([event, undoEvent]) => {
// 				if (undoEvent) {
// 					undoEvent();
// 				};
// 				if (x[event]) {
// 					//console.log({index: this.index, event});
// 					let call : RowEvent = x[event];
// 					let evCall: (event: any) => boolean | void = (e) => call(e, this.data, this.index);
// 					this.undoEvents[event] = this.renderer.listen(this.elementRef.nativeElement, RowEventNames[event], evCall);
// 				}
// 			}
// 		);
// 		if (this.show) {
// 			this.show = false;
// 			this.elementRef.nativeElement.scrollIntoView(
// 				{
// 					behavior: "smooth",
// 					block: "nearest",
// 					inline: "nearest"
// 					//block: "start"
// 				}
// 			);
// 			// setTimeout(
// 			// 	() => {
// 			// 		this.elementRef.nativeElement.scrollIntoView(
// 			// 			{
// 			// 				behavior: "smooth",
// 			// 				block: "start"
// 			// 			}
// 			// 		);
// 			// 	}
// 			// );
// 		}
// 	}
// }


export type TableTriggers = {
	delete?: (caller: any, oldRow: any) => Promise<any>,
	update?: (caller: any, oldRow: any, newRow: any) => Promise<any>,
	insert?: (caller: any, newRow: any) => Promise<any>,
	split?: (caller: any, oldRow: any, newRows: any[]) => Promise<any>,
	init?: (caller: any, resources: any) => Promise<any>,
	dataSaveCb?: (data: any, caller: any, resources: any) => Promise<any>,
	dataLoadCb?: (data: any, caller: any, resources: any) => Promise<any>,
};

export type TableTotal = {
	label: string,
	startVal: any,
	formula: (prevValue: any, currValue: {data: any, idx: number}) => any,
	formatValue?: (v: any) => string,
	alignment?: string
}

export type TableTemplateContext = {
	resources: any,
	col: Column,
	idxCol: number,
	row: RowData,
	idxRow: number
};

const hidden = {display: "none"};
@Component({
	selector: 'app-table',
	templateUrl: './table.component.html',
	styleUrls: ['./table.component.css']
})
export class TableComponent implements OnChanges, OnInit, OnDestroy, BaseIstanzaComponent {
	public static dataFormats: Record<DataFormat, (x: any) => any> = {
		ShortDate: (x: any) => {
			if (x) {
				let d = TableRowComponent.parseJodaDate(x);
				if (d) {
					return d.format(TableRowComponent.df);
				}
				else {
					return undefined;
				}
			}
			else {
			  return x;
			}
		},
		LongDate: (x: any) => {
			if (x) {
				let d = TableRowComponent.parseJodaLongDate(x);
				if (d) {
					return d.format(TableRowComponent.longDf);;
				}
				else {
					return undefined;
				}
			}
			else {
				return x;
			}
		},
		Decimal: (x: any) => {
			const num = Number.parseFloat(x);
			const dec = num.toFixed(2);
			return dec;
		},
		Integer: (x: any) => {
			const num = Number.parseFloat(x);
			const dec = num.toFixed(0);
			return dec;
		},
		Ettari: (x: any) => {
			const ettari = (x ?? 0) / 10000;
			return Number(ettari.toFixed(2));
		},
		Default: (x: any) => {
			return x;
		}
	}
	static idGen = 0;
	id = TableComponent.idGen++;
	@Input() hideHeader : boolean = false;
	@Input() filter : boolean = false;
	//_resources: any = undefined;
	@Input() resources: any;


	@Input() dictionariesData?: Record<string, any>;

	@Input() pageButtons : number = 2;
	@Input() rowDataPath?: (x: any) => any = undefined;
	@Input() rowHandler? : RowHandler = undefined;
	@Input() selectedRow? : any;
	@Input() highlightRow? : any;
	@Input() context: any;
	@Input() isReadOnly: boolean = false;
	@Input() triggers?: TableTriggers;
	@Input() template: TemplateRef<TableTemplateContext>[] = [];
	@Input() constraints: any[] = [];
	@Input() totals?: TableTotal[];

	evalTotal(tot: TableTotal) {
		const res = this._data.filter(x => x.data != undefined).reduce(
			tot.formula,
			tot.startVal
		);
		return tot.formatValue ? tot.formatValue(res) : res;
	}

	@Input() menuOptions?: TableMenuOptions;

	selectedRowData? : RowData;
	highlightedRowData? : RowData;

	@ContentChild(Columns) columns!: Columns;

	newRecord: any;
	isEditing: boolean = false;
	isReadOnlyEdit: boolean = false;

	@Output() readonly dataChanged = new EventEmitter<{pos?: number, value: any, isValid: boolean}>();
	@Output() readonly changeEdit = new EventEmitter<boolean>();
	@Output() readonly componentInit: EventEmitter<IstanzaComponentInterface> = new EventEmitter<IstanzaComponentInterface>();

	pages: number = 1;
	// errors: any[] = [];
	errorsDict: Record<number, any[]> = {};

	DataFormat = DataFormat;
	constructor(
		@Inject(DOCUMENT) private document: Document,
		public authService: BaseAuthService
	) {

	}


	ngOnDestroy(): void {
		this.document.removeEventListener("click", this.docClick);
	}
	//@HostListener('document:click')
	documentClick(): void {
		console.log("click");
		this.closeContextMenu();
	}
	docClick = this.documentClick.bind(this);
	ngOnInit(): void {
		//console.log(this.sortEnabled);
		this.evalPages();
		this.paginate();
		this.componentInit.emit(
			{
				getValidity: this.checkValidity.bind(this)
			}
		);
	}

	checkValidity(): boolean {
		if (this.constraints) {
			const errorsDict : Record<number, any[]> = this.errorsDict = {};
			function addError(idx: number, errMess: string, className: string) {
				const error = {
					icon: className,
					message: errMess
				}
				if (errorsDict[idx]) {
					errorsDict[idx].push(error)
				} else {
					errorsDict[idx] = [error]
				}
			}
			this.constraints.forEach(
				(constr) => {
					const mess = constr.messaggio;
					if (this._data != undefined) {
						if (constr.notNull != undefined) {
							let nullRows: any[] = [];
							const arrKo = this._data.filter(
								row => row != undefined && row.data != undefined && constr.notNull.find((field: string) => row.data[field] == undefined) != undefined
							);
							nullRows.push(
								...arrKo
							);
							nullRows.forEach(
								value => {
									addError(value.idx, mess, 'text-danger bi-exclamation-octagon-fill');
								}
							);
						}
						else {
							if (constr.unique != undefined) {
								let nonUniqueRows: any[] = [];
								let sort = this._data.filter(x => x.data != undefined).slice();
								sort = sort.sort(
									(rowA, rowB) => {
										const res = constr.unique.reduce(
											(prev: number, field: string | number)  => {
												if (prev != 0) {
													return prev
												}
												else {
													if (rowA.data[field] == rowB.data[field]) {
														return 0
													}
													else {
														const a = rowA.data[field];
														const b = rowB.data[field];
														if (a == undefined) {
															return 1
														}
														else {
															if (b == undefined) {
																return -1;
															}
															else {
																const app = ((a < b) ? -1 : 1);
																return app
															}
														}
													}
												}
											},
											0
										)
	
										return res;
									}
								);
	
								let startRep: boolean = false;
								for (let idx = 1; idx < sort.length; idx++) {
									const field = constr.unique.find((field: string) => sort[idx].data[field] != sort[idx - 1].data[field]);
									if (field == undefined) {
										if (!startRep) {
											startRep = true;
											nonUniqueRows.push(sort[idx - 1]);
										}
										nonUniqueRows.push(sort[idx]);
									} else {
										startRep = false;
									}
								}
								nonUniqueRows.forEach(
									value => {
										addError(value.idx, mess, 'text-warning bi-exclamation-triangle-fill');
									}
								);
							}
							else {
								if (constr.customRow != undefined) {
									this._data.filter(x => x.data != undefined).forEach(
										({idx, data}: RowData) => {
											let errRes = constr.customRow(data);
											if (errRes) {
												addError(idx, (errRes == true) ? mess : errRes, 'text-danger bi-exclamation-octagon-fill');
											}
										}
									)
								}
							}
						}
					}
				}
			);
		}
		return Object.keys(this.errorsDict).length == 0;
	}

	abortDocClick?: AbortController;
	openContextMenu(e: MouseEvent) {
		this.contextmenuStyle = {
			"display": "block",
			"position": "fixed",
			"z-index": "100",
			"background-color": "white",
			"left": `${e.clientX}px`,
			"top": `${e.clientY}px`
		};

		this.abortDocClick?.abort();
		this.abortDocClick = new AbortController();
		this.document.addEventListener(
			"click",
			this.docClick,
			{
				signal: this.abortDocClick.signal
			}
		);
	}
	closeContextMenu() {
		this.contextmenuStyle = hidden;
		this.abortDocClick?.abort();
		//this.document.removeEventListener("click", this.docClick);
	}
	toggleContextMenu(e: MouseEvent) {
		if(e) {
			this.openContextMenu(e);
		}
		else {
			this.closeContextMenu();
		}
	}

	setEditing(val: boolean) {
		this.isEditing = val;
		this.changeEdit.emit(val);
	}
	@ViewChild('contextmenu') contextmenu!: ElementRef;
	contextmenuStyle: Record<string, string> = hidden;
	displayContextMenu(e: MouseEvent) {
		if (this.highlightRow && this.highlightRow != this.selectedRow) {
			this.select(this.highlightedRowData);
		}

		//e.preventDefault();
		const disp = this.contextmenuStyle["display"];
		if (disp) {
			this.contextmenuStyle = {
				"display": "block",
				"position": "fixed",
				"z-index": "100",
				"background-color": "white",
				"left": `${e.clientX}px`,
				"top": `${e.clientY}px`
			};
		}
		else {
			this.contextmenuStyle = hidden;
		}
		e.stopPropagation();
	}

	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const v = changes[propName].currentValue;
			switch (propName) {
				case "resources": {
					if (this.triggers && this.triggers.init) {
						this.triggers.init(this, v);
					}
				}; break;
				case "menuOptions": {
					if (v != undefined && Object.values(v).length > 0) {
						this.document.addEventListener("click", this.docClick);
					}
					else {
						this.document.removeEventListener("click", this.docClick);
					}
				}; break;
				case "selectedRow": {
					if (v === this.selectedRowData?.data) {
						// se la riga è già selezionata non occorre far nulla
					}
					else {
						if (v == undefined) {
							this.selectedRowData = undefined;
						}
						else {
							this.setEditing(false);
							this.selectedRowData = this._data.find(x => x.data == v);
						}
					}
				}; break;
				case "highlightRow": {
					if (v === this.highlightedRowData?.data) {
						// se la riga è già evidenziata non occorre far nulla
					}
					else {
						if (v == undefined) {
							this.highlightedRowData = undefined;
						}
						else {
							this.highlightedRowData = this.pagedData.find(x => x.data == v);
						}
					}
				}; break;
				case "rowHandler": {
					if (v) {
						let x : undefined | RowEvent = undefined;
						this.selectedRowData = undefined;
						let arrClick : RowEvent[] = [];
						let arrEnter : RowEvent[] = [];
						let arrLeave : RowEvent[] = [];

						if (v.selection) {
							arrClick.push(this.selectionClick.bind(this));
						}
						if (v.click) {
							arrClick.push(v.click);
						}

						if (v.highlight) {
							arrEnter.push(this.highlightEnter.bind(this));
							arrLeave.push(this.highlightLeave.bind(this));
						}
						if (v.enter) {
							arrEnter.push(v.enter);
						}
						if (v.leave) {
							arrLeave.push(v.leave);
						}


						if (arrClick.length > 0) {
							this.onClickRow = pipeRowEvents(...arrClick);
						}
						if (arrLeave.length == 1) {
							this.onLeaveRow = pipeRowEvents(...arrLeave);
							//this.onLeaveRow = arrLeave[0];
						}
						if (arrEnter.length == 1) {
							this.onEnterRow = pipeRowEvents(...arrEnter);
							//this.onEnterRow = arrEnter[0];
						}
					}
				}
			}
		}

	}


	_editForm: any = undefined;
	@Input()
	set editForm(value: any) {
		this._editForm = value;
		// this._editForm = {
		// 	nome: "Scheda Info",
		// 	tipo: "formio",
		// 	conf: value,
		// 	tipoDati: TipoDatiScheda.Object
		// };

		//this.dictionariesData = value?.dictionariesData;
	}
	get editForm(): any {
		return this._editForm;
	}


	onEditDataConfirmedBuond = this.onEditDataConfirmed.bind(this);
	onEditDataConfirmed(evento: any) {
		console.log(evento);
		this.setEditing(false);
		if (this.selectedRowData) {
			const idx = this.selectedRowData.idx;
			const oldData = this.selectedRowData.data;
			const postCb0 = (newData: any) => {
				const postCb = () => {
					this.dataChanged.emit(
						{
							pos: idx,
							value: newData,
							isValid: evento.isValid
						}
					);
				};
				if (newData) {
					if (this.triggers && this.triggers.update) {
						this.triggers.update(this, oldData, newData).then(postCb);
					}
					else {
						postCb();
					}
				}
				else {
					if (this.triggers && this.triggers.delete) {
						this.triggers.delete(this, oldData).then(postCb);
					}
					else {
						postCb();
					}
				}
			};
			if (this.triggers && this.triggers.dataSaveCb) {
				this.triggers.dataSaveCb(evento.dati, this, this.resources ).then(postCb0);
			}
			else {
				postCb0(evento.dati);
			}
		}
		else {
			const postCb0 = (newData: any) => {
				const postCb = () => {
					this.dataChanged.emit(
						{
							pos: undefined,
							value: newData,
							isValid: evento.isValid
						}
					);
				};
				if (this.triggers && this.triggers.insert) {
					this.triggers.insert(this, newData).then(postCb);
				}
				else {
					postCb();
				}
			};
			if (this.triggers && this.triggers.dataSaveCb) {
				this.triggers.dataSaveCb(evento.dati, this, this.resources ).then(postCb0);
			}
			else {
				postCb0(evento.dati);
			}
		}
		return Promise.resolve();
	}



	menuAction(action: TableMenuAction) {
		this.contextmenuStyle = hidden;
		switch (action) {
			case TableMenuStdAction.New: {
				this.select(undefined);
				this.setEditing(true);
				this.isReadOnlyEdit = false;
				this.newRecord = {};
			}; break;
		}
	}
	isReadOnlyAction(action: TableMenuAction|TableRowMenuAction) : boolean{
		return action == TableRowMenuStdAction.View;
	}
	rowMenuAction(action: TableRowMenuAction) {

		const prepareNewRecord = () => {
			const passData = deepClone(this.selectedRowData?.data);
			const cb = () => {
				this.newRecord = passData;
			};
			if (this.triggers && this.triggers.dataLoadCb) {
				this.triggers.dataLoadCb(passData, this, this.resources).then(
					cb
				);
			}
			else {
				cb();
			}
		}


		this.contextmenuStyle = hidden;
		switch(action) {
			case TableRowMenuStdAction.Edit: {
				this.setEditing(true);
				this.isReadOnlyEdit = false;
				//this.newRecord = JSON.parse(JSON.stringify(this.selectedRowData?.data));
				//this.newRecord = deepClone(this.selectedRowData?.data);
				prepareNewRecord();
			}; break;
			case TableRowMenuStdAction.View: {
				this.setEditing(true);
				this.isReadOnlyEdit = true;
				//this.newRecord = JSON.parse(JSON.stringify(this.selectedRowData?.data));
				//this.newRecord = deepClone(this.selectedRowData?.data);
				prepareNewRecord();
			}; break;
			case TableRowMenuStdAction.Remove: {
				if (this.selectedRowData) {
					this.dataChanged.emit(
						{
							pos: this.selectedRowData.idx,
							value: undefined,
							isValid: true
						}
					);
				}
				else {
					alert('Nessun elemento selezionato');
				}
			}
		}
	}
	setPagination(value: PaginationOptions | undefined) {
		if (value) {
			function escape(caller: TableComponent) {
				const size = defaultPageSize ?? UnlimitedPage;

				caller._pagination = {
					defaultPageSize: size,
					pageSizes: [size]
				};
				caller.pageSize = size;
				caller._currPage = 0;
			}

			const oldPosition: number = (this.pageSize == UnlimitedPage) ? 0 : (this._currPage * this.pageSize);


			// this.pageSize = UnlimitedPage;
			// this._currPage = 0;
			// this._pagination = undefined;

			let pagesArray: number[] = value.pageSizes;
			let defaultPageSize = value.defaultPageSize;
			if (pagesArray.length > 0) {
				let idxAll = pagesArray.indexOf(UnlimitedPage);
				let flagDef = defaultPageSize && pagesArray.indexOf(defaultPageSize) >= 0;
				pagesArray = pagesArray.filter(x => x > 0);
				if (pagesArray.length > 0) {
					if (defaultPageSize && defaultPageSize <= 0) {
						defaultPageSize = pagesArray[0];
					}

					if (!flagDef && defaultPageSize) {
						pagesArray.push(defaultPageSize);
					}
					pagesArray = pagesArray.sort((e1, e2) => e1 - e2);
					if (idxAll >= 0) {
						pagesArray.push(UnlimitedPage);
					}
					this._pagination = {
						defaultPageSize,
						pageSizes: pagesArray
					};
					this.pageSize = defaultPageSize as number;
					this._currPage = Math.ceil(oldPosition / this.pageSize);
				}
				else {
					escape(this);
				}
			}
			else {
				escape(this);
				// const size = defaultPageSize ?? 10;

				// this._pagination = {
				// 	defaultPageSize: size,
				// 	pageSizes: [size]
				// };
				// this.pageSize = size;
			}
			this.paginate();
		}
		else {
			alert("Paginazione non valida")
		}
	}

	private _pagination?: PaginationOptions;
	@Input()
	set pagination (value: PaginationOptions | undefined) {
		this.setPagination(value);
	}
	get pagination() : PaginationOptions | undefined {
		return this._pagination;
	}

	changeSort(idx: number) {
		//console.log({idx, sortField: this.sortConf.sortField, sortDirection: this.sortConf.sortDirection});
		if (this.sortConf.sortField == idx) {
			if (this.sortConf.sortDirection == 1) {
				this.sortConf.sortDirection = -1;
			}
			else {
				this.sortConf.sortDirection = this.sortConf.sortDirection + 1;
			}
		}
		else {
			this.sortConf.sortField = idx;
			this.sortConf.sortDirection = 1;
		}
		this.sort();
		//console.log({idx, sortField: this.sortConf.sortField, sortDirection: this.sortConf.sortDirection});
	}

	keyOnSort(event: KeyboardEvent, idx:number) {
		//console.log(event);
		if (['Space', 'Enter'].includes(event.code) ) {
			this.changeSort(idx);
		}
	}

	@Input() sortEnabled: boolean = false;
	sortConf: SortConf = new SortConf();


	_data: RowData[] = [];
	@Input()
	set data (value: any[] | undefined) {
		this._data = value ?
			(
				this.rowDataPath ? (
					value.map(
						(data: any, idx: number) => ({
							idx,
							data: (data ? this.rowDataPath!(data) : null)
						})
					)
				) : (
					value.map(
						(data: any, idx: number) => ({
							idx,
							data
						})
					)
				)
			)
			: [];
		if (this.selectedRowData) {
			this.selectedRowData = this._data.find(x => x.data == this.selectedRowData!.data);
		}
		if (this.highlightedRowData) {
			this.highlightedRowData = this._data.find(x => x.data == this.highlightedRowData!.data);
		}

		this.filterData();
		this.filterDataForErrors(false);
		this.checkValidity();
	}
	get data() : RowData[] {
		return this._data;
	}

	evalPages() : void {
		if (this._pageSize != UnlimitedPage) {
			this.pages = Math.ceil(this.filteredData.length / this._pageSize);
		}
		else {
			this.pages = 0;
		}
	}

	setPageSize(value: number) {
		const oldPosition: number = (this._pageSize == UnlimitedPage) ? 0 : (this._currPage * this.pageSize);

		if (value >= 0) {
			this._pageSize = value;
		}
		else {
			this._pageSize = UnlimitedPage;
		}

		this._currPage = (value == UnlimitedPage) ? 0 : Math.floor(oldPosition / value)

		this.evalPages();
		this.paginate();
	}

	_pageSize: number = UnlimitedPage;
	set pageSize(value: number) {
		this.setPageSize(value);
	}
	get pageSize() : number {
		return this._pageSize;
	}

	filteredData: RowData[] = [];
	sortedData: RowData[] = [];
	pagedData: RowData[] = [];

	filterData(searchText?: string) {
		//console.log(`filtro: ${searchText}`);
		if (searchText && searchText != "") {
			let inpData: any = (searchText.includes(this.searchText)) ? this.filteredData : this.data.filter(x => x.data);
			this.filteredData = inpData.filter(
				(x: RowData) => columnFilter(this.columns.columnsList, x.data, searchText, this.resources)
			);
			this._searchText = searchText;
		}
		else {
			this.filteredData = this.data.filter(x => x.data);
			this._searchText = "";
		}
		this.currPage = 0;
		this.evalPages();
		this.sort();
	}

	filterDataForErrors($event: any) {
		const enable = typeof $event == "boolean" ? $event : $event.target.checked
		this._searchText = "";
		if (enable) {
			this.errorFilterEnabled = true;
			this.textFilterEnabled = false;
			this.filteredData = this._data.filter(
				(x: RowData) => this.errorsDict[x.idx] != undefined
			);
		} else {
			this.errorFilterEnabled = false;
			this.textFilterEnabled = true;
			this.filterData();
		}
		this.currPage = 0;
		this.evalPages();
		this.sort();
	}

	sort() : void {
		if (this.sortConf.sortField != undefined && this.sortConf.sortDirection != 0) {
			const sortDirection = this.sortConf.sortDirection;
			const sortIdx = this.sortConf.sortField;
			const columns = Array.from(this.columns.columnsList);
			const fieldIdx : string | undefined = columns[sortIdx].dataField;
			function tabCompare(e1: RowData, e2: RowData) {
				let xe1 = e1.data;
				let xe2 = e2.data;

				let de1 : any = xe1;
				let de2 : any = xe2;
				if (fieldIdx) {
					de1 = xe1[fieldIdx];
					de2 = xe2[fieldIdx];
				}
				return compare(de1, de2, sortDirection);
			}
			this.sortedData = [...this.filteredData].sort(tabCompare);
		}
		else {
			this.sortedData = this.filteredData;
		}
		this.paginate();
	}

	paginate() : void {
		if (this.pageSize == UnlimitedPage) {
			this.pagedData = this.sortedData;
		}
		else {
			let stIdx = this.currPage * this.pageSize;
			let endIdx = stIdx + this.pageSize;
			this.pagedData = this.sortedData.slice(stIdx, endIdx);
		}
		this.selectedRowData = (this.data && this.selectedRow) ? this.data.find(x => x == this.selectedRow) : undefined;
		this.highlightedRowData = (this.data && this.highlightRow) ? this.data.find(x => x == this.highlightRow) : undefined;
	}

	textFilterEnabled: boolean = true;
	errorFilterEnabled: boolean = false;
	_searchText: string = "";
	set searchText(value: string | undefined) {
		this.filterData(value??"");
	}
	get searchText() : string {
		return this._searchText;
	}

	preRange: number[] = [];

	_currPage: number = 0;
	set currPage(value: number) {
		if (value >= 0 && value < this.pages)
		{
			this._currPage = value;
			this.paginate();
		}
		this.preRange = this.getRange(this.max(0, this.currPage - this.pageButtons), this.currPage);
	}
	get currPage() : number {
		return this._currPage;
	}

	gotoPage(page: number, event?: any, idx?: number) {
		console.log(idx);
		console.log(event);
		console.log(page);
		this.currPage = page;
	}


	getRange(start: number, end: number): number[] {
		let ans : number[] = [];
		for (let i = start; i < end; i++) {
			ans.push(i);
		}
		return ans;
	}
	max(...values: number[]) : number{
		return Math.max(...values);
	}
	min(...values: number[]) : number{
		return Math.min(...values);
	}
	closeDialog() {
		this.setEditing(false);
	}
	select(row?: RowData) {
		if (this.selectedRowData == row) {
			if (row == undefined) {
			}
			else {
				//console.log('emitSelection');
				this.selection.emit(undefined);
				this.selectedRowData = undefined;
			}
		}
		else {
			this.selection.emit(row);
			this.setEditing(false);
			this.selectedRowData = row;
			//console.log('emitSelection');
		}
	}

	@Output() selection = new EventEmitter<(RowData|undefined)>();
	selectionClick(ev : Event, row?: RowData) {
		//console.log({selection: ev, row});
		this.contextmenuStyle = hidden;
		this.select(row);
	}


	@Output() highlight = new EventEmitter<(RowData|undefined)>();
	highlightEnter(ev : Event, row?: RowData) {
		//console.log({enter: ev, row, idx});
		if (this.highlightedRowData == row) {
			// se la riga è già evidenziata non occorre far nulla
			//console.log('uguale');
		}
		else {
			if (row == this.selectedRowData) {
				// se la riga è già selezionata non occorre far nulla
			}
			else {
				this.highlightedRowData = row;
				this.highlight.emit(row);
			}
		}
	}
	highlightLeave(ev : Event, row?: RowData) {
		this.highlightedRowData = undefined;
		this.highlight.emit(undefined);
	}


	onClickRow? : RowEvent = undefined;
	onEnterRow? : RowEvent = undefined;
	onLeaveRow? : RowEvent = undefined;

	protected readonly Object = Object;

	
}
