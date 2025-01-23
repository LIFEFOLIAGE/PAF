import { EventEmitter, Input, Output, Type } from "@angular/core";
import { SimpleObjectChange, TableChange } from "../../shared/editor-scheda/editor-scheda.component";
import { BaseIstanzaComponent } from "../../interfaces/istanza-component-interface";
import { ExportValue } from "../../istanze/editor-istanza/editor-istanza.component";

export type ComponentConfiguration = { component: Type<any>, options: any };

export type ComponentRecordType = Record<(string | number | symbol), any>
export type ComponentListType = { pos: number, value: any }[]
export type ComponentDataType = ComponentRecordType | ComponentListType;

export interface ComponentType<T extends (SimpleObjectChange | TableChange)> extends BaseIstanzaComponent {
	dati: ComponentDataType,
	isReadOnly: boolean,
	context: any,
	resources: any,
	componentOptions: any;
	dictionariesData?: Record<string, any>;

	changeEdit: EventEmitter<boolean>;
	dataChanged: EventEmitter<T>;
	export: EventEmitter<ExportValue>;
}
