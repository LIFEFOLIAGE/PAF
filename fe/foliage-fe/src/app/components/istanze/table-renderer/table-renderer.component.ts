// import {
// 	AfterViewInit,
// 	OnChanges, SimpleChanges,
// 	Output, EventEmitter,
// 	Compiler,
// 	ComponentRef,
// 	Component,
// 	NgModule,
// 	ViewChild,
// 	ViewContainerRef,
// 	Input, OnInit
// } from '@angular/core';
// import { BrowserModule } from '@angular/platform-browser';
// import { AppModule } from 'src/app/app.module';
// //import { TableModule } from '../../../modules/table/table.module';
// import { DataFormat, RowData, TableTriggers } from 'src/app/modules/table/table.component';
// import { IstanzaComponentInterface } from "../../interfaces/istanza-component-interface";


// const attrs = [
// 	{
// 		from: "header",
// 		to: "headerText"
// 	},
// 	{
// 		from: "dataField",
// 		to: "dataField"
// 	},
// 	// {
// 	//   from: "dataType"
// 	// },
// 	// {
// 	//   from: "cellValue",
// 	//   to: "cellValue",
// 	//   bind: true
// 	// },
// 	// {
// 	//   from: "template",
// 	//   to: "cellTemplate"
// 	// },
// 	{
// 		from: "dataFormat",
// 		to: "dataFormat"
// 	}
// ];

// @Component({
// 	selector: 'app-table-renderer',
// 	templateUrl: './table-renderer.component.html',
// 	styleUrls: ['./table-renderer.component.css']
// })
// export class TableRendererComponent implements AfterViewInit, OnChanges {
// 	static id = 0;
// 	@ViewChild('container', {read: ViewContainerRef, static: false}) container!: ViewContainerRef;

// 	constructor(private compiler: Compiler) {
// 	}
// 	componentRef: any;
// 	@Input() conf: any = {};
// 	@Input() dati: any[] = [];
// 	@Input() selectedRow: any;
// 	@Input() highlightRow: any;
// 	@Input() isReadOnly: boolean = false;
// 	@Input() context: any;
// 	@Input() resources: any;

// 	@Output() readonly highlight = new EventEmitter<(RowData|undefined)>();
// 	@Output() readonly selection = new EventEmitter<(RowData|undefined)>();
// 	@Output() readonly changeEdit = new EventEmitter<boolean>();

// 	@Output() dataChanged = new EventEmitter<{pos?: number, value: any}>();

// 	renderTable(
// 		templateString: string,
// 		cellValuesInp: Record<number, (rowData: any) => string>,
// 		schedaInfo: any,
// 		isReadOnly: boolean,
// 		dictionariesData?: Record<string, any>
// 	) {
// 		this.compiler.clearCache();
// 		const dd = dictionariesData;
// 		//console.log(templateString);
// 		//this.container.clear();
// 		const parent = this;
// 		// Define the component using Component decorator.
// 		const component = Component({
// 			template: templateString,
// 			host: { 'id': (TableRendererComponent.id++).toString()}
// 		})(
// 			class {
// 				cellValues: Record<number, (rowData: any) => string> = cellValuesInp;
// 				parent = parent;
// 				isReadOnly = isReadOnly;
// 				contesto: any = undefined;
// 				schedaInfo: any = undefined;
// 				selectedRow: any = undefined;
// 				highlightRow: any = undefined;
// 				dictionariesData?: Record<string, any> = dd;
// 			}
// 		);

// 		// Define the module using NgModule decorator.
// 		const module = NgModule({
// 			declarations: [component],
// 			imports: [
// 				BrowserModule,
// 				AppModule
// 			]
// 		})(class {
// 		});


// 		// Asynchronously (recommended) compile the module and the component.
// 		this.compiler.compileModuleAndAllComponentsAsync(module)
// 			.then(
// 				factories => {
// 					this.container.clear();

// 					// Get the component factory.
// 					const componentFactory = factories.componentFactories[0];
// 					// Create the component and add to the view.
// 					const componentRef = this.container.createComponent(componentFactory);

// 					componentRef.instance.dati = this.dati;
// 					componentRef.instance.cellValues = cellValuesInp;
// 					componentRef.instance.parent = parent;
// 					componentRef.instance.isReadOnly = isReadOnly;
// 					componentRef.instance.schedaInfo = schedaInfo;
// 					componentRef.instance.context = this.context;
// 					componentRef.instance.dictionariesData = dd;


// 					this.componentRef = componentRef;
// 				}
// 			);
// 	}
// 	colProps : Record<number, string[]> = {}
// 	templates : string[] = [];
// 	cellValues: Record<number, (rowData: any) => string> = {};
// 	template?: string = undefined;
// 	triggers?: TableTriggers;

// 	ngOnChanges(changes: SimpleChanges): void {
// 		//throw new Error('Method not implemented.');
// 		//console.log(changes);

// 		for (let propName in changes) {
// 			const currValue = changes[propName].currentValue;
// 			switch (propName) {
// 				case "selectedRow": {
// 					if (this.componentRef) {
// 						this.componentRef.instance.selectedRow = currValue;
// 					}
// 				}; break;
// 				case "highlightRow": {
// 					if (this.componentRef) {
// 						this.componentRef.instance.highlightRow = currValue;
// 					}
// 				}; break;
// 				case "dati": {
// 					if (this.componentRef) {
// 						this.componentRef.instance.dati = currValue;
// 					}
// 				}; break;
// 				case "conf": {
// 					this.colProps = {};
// 					this.templates = [];
// 					this.cellValues = {};
// 					this.triggers = currValue.triggers;
// 					currValue.columns.forEach(
// 						(col: any, idxCol: number) => {
// 							this.colProps[idxCol] = [];
// 							const templ = col.template;
// 							if (templ) {
// 								this.templates.push(
// `
// 	<ng-template #cellTemplateCol${idxCol}
// 			let-resources="resources"
// 			let-col="col"
// 			let-idxCol="idxCol"
// 			let-row="row"
// 			let-idxRow="idxRow"
// 		>
// 		${templ.replaceAll("\n", "\n		")}
// 	</ng-template>`
// 								);
// 								this.colProps[idxCol].push(`[cellTemplate]="cellTemplateCol${idxCol}"`);
// 							}
// 							const cellValue = col.cellValue;
// 							if (cellValue) {
// 								this.cellValues[idxCol] = cellValue;
// 								this.colProps[idxCol].push(`[cellValue]="this.cellValues[${idxCol}]"`);
// 							}
// 							attrs.forEach(
// 								x => {
// 									const v = col[x.from];
// 									if (v) {
// 										let lPart = x.to;
// 										// if (x.bind) {
// 										//   lPart = `[${lPart}]`;
// 										// }
// 										this.colProps[idxCol].push(`${lPart}='${v}'`);
// 									}
// 								}
// 							);
// 						}
// 					);

// 					this.template =
// `<app-table [context]="this.contesto"
// 		[triggers]="this.parent.triggers"
// 		[resources]="this.parent.resources"
// 		[data]="this.dati"
// 		[hideHeader]="false"
// 		[pagination]="{pageSizes: [0, 5, 10, 15], defaultPageSize: 0}"
// 		[filter]="true"
// 		[selectedRow]="this.parent.selectedRow"
// 		[highlightRow]="this.parent.highlightRow"
// 		[rowHandler]="{selection: true, highlight: true}"
// 		[editForm]="this.schedaInfo"
// 		[isReadOnly]="this.isReadOnly"
// 		[menuOptions]="this.parent.conf.menuOptions"
// 		(selection)="this.parent.onSelection($event)"
// 		(highlight)="this.parent.onHighlight($event)"
// 		(dataChanged)="this.parent.onDataChanged($event)"
// 		(changeEdit)="this.parent.changeEdit.emit($event)"
// 	>${this.templates.join("")}
// 	<columns>${
// 			currValue.columns/*.filter((col: any) => col.dataField != undefined)*/.map(
// 				(col: any, idxCol: number) => {
// 					return `
// 		<column ${this.colProps[idxCol].join(" ")}/>`
// 				}
// 			).join("")
// 		}
// 	</columns>
// </app-table>`;
// 					this.renderTable(this.template, this.cellValues, currValue.schedaInfo, this.isReadOnly, currValue.dictionariesData);
// 				}
// 			}
// 		}
// 	}

// 	ngAfterViewInit(): void {
// 		//this.renderTable(templateString);
// 	}

// 	public onSelection(row?: RowData) : void {
// 		//console.log({row});
// 		this.selectedRow = row?.data;
// 		this.selection.emit(row);
// 	}
// 	public onHighlight(row?: RowData) : void {
// 		//console.log({idx, row});
// 		this.highlightRow = row?.data;
// 		this.highlight.emit(row);
// 	}
// 	public onDataChanged(x: {pos?: number, value: any}) : void {
// 		//console.log({idx, row});
// 		this.dataChanged.emit(x);
// 	}

// }
