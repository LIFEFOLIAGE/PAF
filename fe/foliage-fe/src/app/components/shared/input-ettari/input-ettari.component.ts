import { Component, EventEmitter, Input, Output, SimpleChanges } from "@angular/core";

@Component({
	selector: 'input-ettari',
	template: `
		<div class="input-group">
			<input [id]="this.idInput"
					[ngModel]="this.valoreEttari"
					[disabled]="this.disabled"
					[class]="this.cssInput"
					(ngModelChange)="this.onModelChange($event)"
					type="number"/>
			<span class="input-group-text">ha</span>
		</div>`
})
export class EttariInput {
	@Input() idInput: any;
	@Input() cssInput: any;
	@Input() disabled: boolean = false;
	@Input() valoreMetriQ?: number;
	@Output() valoreMetriQChange: EventEmitter<(number|undefined)> = new EventEmitter();

	valoreEttari?: number;

	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;

			switch (propName) {
				case "valoreMetriQ": {
					if (currValue == undefined) {
						this.valoreEttari = undefined;
					}
					else {
						// this.valoreEttari = currValue / 10000;
						const tmpValue = currValue / 10000;
						this.valoreEttari = Number(tmpValue.toFixed(2));
					}
				}; break;
			}
		}
	}
	onModelChange(event?: number){
		//console.log(event);
		this.valoreEttari = event;
		if (this.valoreEttari == undefined) {
			this.valoreMetriQChange.emit(undefined);
		}
		else {
			this.valoreMetriQChange.emit(this.valoreEttari*10000);
		}
	}
}
