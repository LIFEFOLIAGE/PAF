import { Component, EventEmitter, Input, Output, SimpleChanges } from "@angular/core";
import { environment } from 'src/environments/environment';

const decimaliEttari = environment.decimaliEttari;
const coeffEttari = environment.coeffEttari;

@Component({
	selector: 'input-ettari',
	template: `
		<div class="input-group">
			<input [id]="this.idInput"
					placeholder="0,0000"
					[value]="this.valoreEttari"
					[disabled]="this.disabled"
					[class]="this.cssInput"
					(change)="this.onModelChange($event)"
					type="text"/>
			<span class="input-group-text">ha</span>
		</div>`
})
export class EttariInput {
	@Input() idInput: any;
	@Input() cssInput: any;
	@Input() disabled: boolean = false;
	@Input() valoreMetriQ?: number;
	@Output() valoreMetriQChange: EventEmitter<(number|undefined)> = new EventEmitter();

	valoreEttari: string = '';

	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;

			switch (propName) {
				case "valoreMetriQ": {
					if (currValue == undefined) {
						this.valoreEttari = '';
					}
					else {
						const tmpValue = currValue / coeffEttari;
						this.valoreEttari = tmpValue.toFixed(decimaliEttari).replace('.', ',');
					}
				}; break;
			}
		}
	}
	onModelChange(event: any){
		//console.log(event);
		//const eventkey = event.key;

		const oldVal = (this.valoreEttari == undefined) ? undefined : Number.parseFloat(this.valoreEttari);
		const proposedVal = (event.target.value == undefined) ? undefined : event.target.value.replace(',', '.');
		event.preventDefault();
		
		const numVal = (proposedVal == undefined) ? event : Number(proposedVal);
				
		let newVal: (number | undefined) = undefined;
		if (numVal == undefined) {
			newVal = oldVal;
			event.target.value = oldVal;
			return;
		}
		else {
			if (Number.isNaN(numVal)) {
				newVal = oldVal;
				event.target.value = oldVal;
				return;
			}
			else {
				newVal = Math.trunc(numVal*coeffEttari)/coeffEttari;
			}
		}
		
		if (newVal == undefined) {
			this.valoreMetriQ = undefined;
		}
		else {
			this.valoreMetriQ = newVal*coeffEttari;
		}
		this.valoreMetriQChange.emit(this.valoreMetriQ);
	}
}
