import {
	Component, ComponentRef,
	EventEmitter,
	Input,
	OnChanges,
	Output,
	SimpleChanges, TemplateRef, Type,
	ViewChild,
} from '@angular/core';
import { RowData, TableComponent, TableTriggers } from 'src/app/modules/table/table.component';
import { BaseIstanzaComponent, IstanzaComponentInterface } from '../../interfaces/istanza-component-interface';
import { ComponentHostDirective } from '../../renderer/components/component-host.directive';
import { ExportValue } from '../editor-istanza/editor-istanza.component';



const attrs = [
	{
		from: "header",
		to: "headerText"
	},
	{
		from: "dataField",
		to: "dataField"
	},
	// {
	//   from: "dataType"
	// },
	// {
	//   from: "cellValue",
	//   to: "cellValue",
	//   bind: true
	// },
	// {
	//   from: "template",
	//   to: "cellTemplate"
	// },
	{
		from: "dataFormat",
		to: "dataFormat"
	}
];


@Component({
	selector: 'app-table-renderer2',
	templateUrl: './table-renderer2.component.html'
})
export class TableRendererComponent2 implements BaseIstanzaComponent {
	@ViewChild(ComponentHostDirective, { static: true }) componentHost!: ComponentHostDirective;
	@Input() conf: any = {};
	@Input() dati: any[] = [];
	@Input() selectedRow: any;
	@Input() highlightRow: any;
	@Input() isReadOnly: boolean = false;
	@Input() context: any;
	@Input() resources: any;
	@Input() dictionariesData?: Record<string, any>;

	@Output() readonly highlight = new EventEmitter<(RowData|undefined)>();
	@Output() readonly selection = new EventEmitter<(RowData|undefined)>();
	@Output() readonly changeEdit = new EventEmitter<boolean>();

	@Output() componentInit: EventEmitter<IstanzaComponentInterface> = new EventEmitter<IstanzaComponentInterface>();
	@Output() dataChanged = new EventEmitter<{pos?: number, value: any}>();
	@Output() export = new EventEmitter<ExportValue>();
	colProps?: Record<string, any>[] = undefined;


	confProperties: any;

	constructor() {
	}
	trackCol(index: number, item: Record<string, any>){
		return index;
	}
	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case "selectedRow": {
				}; break;
				case "highlightRow": {
				}; break;
				case "dati": {
				}; break;
				case "conf": {

					this.colProps = [];
					const triggers: (TableTriggers|undefined) = currValue.triggers;
					const schedaInfo = currValue.schedaInfo;
					const constraints = currValue.constraints;
					const totals = currValue.totals;
					const cols = currValue.columns;
					if (cols) {
						this.colProps = cols.map(
							(col: any, colIdx: number) => {
								const res: Record<string, any> = {};
								const cellValue = col.cellValue;
								res['cellValue'] = cellValue;
								attrs.forEach(
									x => {
										const v = col[x.from];
										let lPart = x.to;
										res[lPart]=v;
									}
								);
								return res;
							}
						);
					}
				

					const dictionariesData = currValue.dictionariesData;
					const menuOptions = currValue.menuOptions;
							
							
					this.confProperties = {
						dictionariesData,
						triggers, 
						schedaInfo,
						constraints,
						totals,
						menuOptions
					};

				}	
			}
		}

	}

	public onSelection(row?: RowData) : void {
		//console.log({row});
		this.selectedRow = row?.data;
		this.selection.emit(row);
	}
	public onHighlight(row?: RowData) : void {
		//console.log({idx, row});
		this.highlightRow = row?.data;
		this.highlight.emit(row);
	}
	public onDataChanged(x: {pos?: number, value: any}) : void {
		//console.log({idx, row});
		this.dataChanged.emit(x);
	}


}

