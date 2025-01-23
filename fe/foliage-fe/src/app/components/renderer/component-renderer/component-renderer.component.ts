import {
	Component, ComponentRef,
	EventEmitter,
	Input,
	OnChanges,
	Output,
	SimpleChanges, Type,
	ViewChild,
} from '@angular/core';
import { SimpleObjectChange, TableChange } from '../../shared/editor-scheda/editor-scheda.component';
import { ComponentHostDirective } from "../components/component-host.directive";
import { ComponentType, ComponentDataType, ComponentConfiguration } from "../components/ComponentInterface";
import { IstanzaComponentInterface } from "../../interfaces/istanza-component-interface";
import { ExportValue } from '../../istanze/editor-istanza/editor-istanza.component';

@Component({
	selector: 'app-component-renderer',
	templateUrl: './component-renderer.component.html',
	styleUrls: ['./component-renderer.component.css']
})
export class ComponentRendererComponent<T extends (SimpleObjectChange|TableChange)> implements OnChanges {
	@Input() componentConf!: ComponentConfiguration;
	@Input() dati!: ComponentDataType;
	@Input() isReadOnly: boolean = false;
	@Input() context: any;
	@Input() resources: any;
	@Input() dictionariesData?: Record<string, any>;

	@Output() readonly changeEdit = new EventEmitter<boolean>();
	@Output() readonly dataChanged = new EventEmitter<T>();
	@Output() readonly componentInit: EventEmitter<IstanzaComponentInterface> = new EventEmitter<IstanzaComponentInterface>();
	@Output() readonly export: EventEmitter<ExportValue> = new EventEmitter<ExportValue>();

	// @ViewChild('container', { read: ViewContainerRef, static: false }) container!: ViewContainerRef;

	@ViewChild(ComponentHostDirective, { static: true }) componentHost!: ComponentHostDirective;

	private currentComponent: Type<any> | undefined = undefined;
	private componentRef: ComponentRef<ComponentType<T>> | null = null;

	constructor() {
	}

	ngOnChanges(changes: SimpleChanges): void {

		if (this.componentRef == undefined) {
			const viewContainerRef = this.componentHost.viewContainerRef;
			viewContainerRef.clear();
			const componentRef =
				viewContainerRef.createComponent<ComponentType<T>>(this.componentConf.component);

			this.componentRef = componentRef;
			this.currentComponent = this.componentConf.component;
		}

		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			if (propName == "componentConf") {
				const viewContainerRef = this.componentHost.viewContainerRef;
				viewContainerRef.clear();

				this.currentComponent = currValue.component;
				this.componentRef = viewContainerRef.createComponent<ComponentType<T>>(currValue.component);
				this.componentRef.instance.componentOptions = currValue.options;
				this.componentRef.instance.changeEdit = this.changeEdit;
				this.componentRef.instance.dataChanged = this.dataChanged;
				this.componentRef.instance.componentInit = this.componentInit;
				this.componentRef.instance.export = this.export;

				this.componentRef.setInput("resources", this.resources);
				this.componentRef.setInput("context", this.context);
				this.componentRef.setInput("dati", this.dati);
				this.componentRef.setInput("dictionariesData", this.dictionariesData);
				this.componentRef.setInput("isReadOnly", this.isReadOnly);
			}
			else {
				console.log(propName);
				this.componentRef.setInput(propName, currValue);
			}
			// switch (propName) {
			// 	case "dati": {
			// 		this.componentRef.setInput("dati", currValue);
			// 	};
			// }
		}



		// if (this.componentRef && this.currentComponent == this.componentConf.component) {
		// 	this.setComponentData(this.componentRef);

		// 	return;
		// }

		// this.setComponentData(componentRef);

	}

	// private setComponentData(componentRef: ComponentRef<ComponentType<T>>) {
	// 	if (this.dati) {
	// 		// this.componentRef.instance.dati = this.dati;
	// 		//TODO: questo triggera ngOnChanges del componente quella di prima no
	// 		componentRef.setInput("dati", this.dati);
	// 		componentRef.setInput("context", this.context);
	// 		componentRef.setInput("isReadOnly", this.isReadOnly);
	// 	}

	// 	if (this.componentConf?.options){
	// 		componentRef.setInput("componentOptions", this.componentConf.options);
	// 	}

	// 	//TODO: questo triggera il setter della variabile
	// 	componentRef.instance.changeEdit = this.changeEdit;
	// 	componentRef.instance.dataChanged = this.dataChanged;
	// 	componentRef.instance.resources = this.resources;
	// 	componentRef.instance.dictionariesData = this.dictionariesData;
	// 	componentRef.instance.onComponentInit = this.onComponentInit;
	// 	//componentRef.instance.isReadOnly = this.isReadOnly;
	// 	//componentRef.instance.context = this.context;
	// }
}
