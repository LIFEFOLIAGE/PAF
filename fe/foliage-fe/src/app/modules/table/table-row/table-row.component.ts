declare var bootstrap: any;

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

import { LocalDateTime, DateTimeFormatter, LocalDate } from '@js-joda/core';
import { DataFormat, RowEvent, Column, RowEventNames } from '../table.component';



@Directive({
	selector: '[bTooltip]'
})
export class TooltipDirective implements AfterViewInit, OnDestroy {
	private tooltip: any;

	constructor(private elementRef: ElementRef) {
	}

	ngAfterViewInit() {
		const domElement: HTMLElement = this.elementRef.nativeElement;
		this.tooltip = new bootstrap.Tooltip(domElement);
	}

	ngOnDestroy(): void {
		this.tooltip.dispose();
	}
}


@Component({
	selector: '[app-row]',
	templateUrl: './table-row.component.html',
	styleUrls: ['../table.component.css']
})
export class TableRowComponent implements OnChanges, OnDestroy {
	// formattatore di date
	static df = DateTimeFormatter.ofPattern('dd/MM/yyyy');
	// formattatore di date con orario
	static longDf = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm:ss');
	static parseJodaDate(x: any) {
		let dt : (LocalDateTime | undefined) = undefined;
		let d : (LocalDate | undefined) = undefined;
		try {
			d = LocalDate.parse(x, DateTimeFormatter.ISO_LOCAL_DATE);
		}
		catch(e) {
			try {
				dt = LocalDateTime.parse(x, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
			}
			catch(eFin) {
				console.error(eFin);
			}
		}
		//const t = LocalDateTime.parse(x, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		if (d) {
			return d;
		}
		else {
			if (dt) {
				const ov = dt.toLocalDate()
				return ov;
			}
			return undefined;
		}
	}

	static parseJodaLongDate(x: any) {
		let dt : (LocalDateTime | undefined) = undefined;
		dt = LocalDateTime.parse(x, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		//const t = LocalDateTime.parse(x, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		if (dt) {
			return dt;
		}
		else {
			return undefined;
		}
	}

	public formatValue(row: any, idx: number) {
		const cols = this.columns;//Array.from(this.columns);
		const col = cols[idx];
		return col.formatValue(row, this.resources);
	}
	DataFormat = DataFormat;
	@Input() rowSelection?: RowEvent;
	@Input() rowEnter?: RowEvent;
	@Input() rowLeave?: RowEvent;
	@Input() show: boolean = false;
	@Input() data: any;
	@Input() index!: number;

	@Input() rowErrors?: { icon: string, message: string }[] = [];

	// _columns: Column[] = []
	// @Input() set columns(value: Column[]) {
	// 	this._columns = [...value];
	// }
	// get columns(): Column[] {
	// 	return this._columns;
	// }
	@Input() columns: Column[] = [];
	@Input() resources: any;

	undoEvents: Record<string, undefined | (() => void)> = {
		rowSelection: undefined,
		rowEnter: undefined,
		rowLeave: undefined
	}

	constructor(private renderer: Renderer2,
				private elementRef: ElementRef) {
	}

	ngOnDestroy() {
		Object.values(this.undoEvents).forEach(
			x => {
				if (x) {
					x();
				}
			}
		);
	}

	ngOnChanges(changes: SimpleChanges): void {
		const x : any = this;
		//console.log({rowChanges: changes, "this": x});
		Object.entries(this.undoEvents).forEach(
		 	([event, undoEvent]) => {
				if (undoEvent) {
					undoEvent();
				};
				if (x[event]) {
					//console.log({index: this.index, event});
					let call : RowEvent = x[event];
					let evCall: (event: any) => boolean | void = (e) => call(e, this.data, this.index);
					this.undoEvents[event] = this.renderer.listen(this.elementRef.nativeElement, RowEventNames[event], evCall);
				}
			}
		);
		if (this.show) {
			this.show = false;
			this.elementRef.nativeElement.scrollIntoView(
				{
					behavior: "smooth",
					block: "nearest",
					inline: "nearest"
					//block: "start"
				}
			);
			// setTimeout(
			// 	() => {
			// 		this.elementRef.nativeElement.scrollIntoView(
			// 			{
			// 				behavior: "smooth",
			// 				block: "start"
			// 			}
			// 		);
			// 	}
			// );
		}
	}
}