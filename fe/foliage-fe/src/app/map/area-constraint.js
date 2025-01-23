import {Control} from 'ol/control';

const CLASS_UNSELECTABLE = 'ol-unselectable';

export class AreaConstraintControl extends Control {
	options;
	constrValue;
	evaluator;
	minValue;
	maxValue;
	decimals;
	unit;
	constrainedAreaText;
	totalEditingAreaText;
	valueDescription;
	wrongValueErrorMessage;
	missingValueErrorMessage;
	
	currArea;
	_nextArea;
	set nextArea(value) {
		this._nextArea = value;
	}

	value;

	constructor(context, options) {

		const constrValue = options.contextValue
			? context[options.contextValue] 
			: (
				options.fixedValue
					? options.fixedValue
					: (
						options.contextFunction
							? options.contextFunction(context)
							: undefined
					)
			);
		
		const evaluator = options.getValue;

		if (constrValue != undefined && evaluator != undefined ) {
			
			const element = document.createElement('div');
			element.style.pointerEvents = 'none';
		
			super({
			  element: element,
			  render: options.render,
			  target: options.target,
			});

			this.constrValue = constrValue;
			this.evaluator = evaluator;
			
			this.minValue = options.minAcceptedValue;
			this.maxValue = options.maxAcceptedValue;
			this.decimals = options.displayedDecimals;

			this.unit = options.unit;

			this.constrainedAreaText = options.constrainedAreaText ?? 'Superficie vincolata';
			this.totalEditingAreaText = options.totalEditingAreaText ?? 'Superficie totale';
			this.valueDescription = options.valueDescription ?? 'Valore attuale';
			
			this.wrongValueErrorMessage = options.wrongValueErrorMessage ?? 'Valore non consentito';
			this.missingValueErrorMessage = options.missingValueErrorMessage ?? 'Valore non disponibile';

			this.options = options ? options : {};


			const className = 'ol-area-constraint';
			this.element.className = className + ' ' + CLASS_UNSELECTABLE;

			this.errorDiv = document.createElement('div');
			this.errorDiv.style['grid-column-start'] = 1;
			this.errorDiv.style['grid-column-end'] = 3;
			this.errorDiv.style['color'] = 'red';
			this.errorDiv.style['background-color'] = 'black';

			this.errorDiv.style['font-weight'] = 'bold';
			this.errorDiv.style['font-size'] = 'large';
			this.errorDiv.style['padding'] = '10px';

			this.element.appendChild(this.errorDiv);

			if (this.constrValue > 0) {
				this.errorDiv.style['display'] = 'none';
				const constraintDiv1 = document.createElement('div');
				constraintDiv1.style['grid-column'] = 1;
				constraintDiv1.innerHTML = `${this.constrainedAreaText}:`;
				
				const constraintDiv2 = document.createElement('div');
				constraintDiv2.style['grid-column'] = 2;
				constraintDiv2.style['text-align'] = 'right';
				constraintDiv2.innerHTML = this.formatUnit(this.constrValue);
	
				this.element.appendChild(constraintDiv1);
				this.element.appendChild(constraintDiv2);
	
				
				this.constraintDiv3 = document.createElement('div');
				this.constraintDiv3.style['grid-column'] = 1;
				this.constraintDiv3.style['color'] = 'blue';
				this.constraintDiv3.innerHTML = `${this.totalEditingAreaText}:`;
				
				
				this.constraintDiv4 = document.createElement('div');
				this.constraintDiv4.style['grid-column'] = 2;
				this.constraintDiv4.style['color'] = 'blue';
				this.constraintDiv4.style['text-align'] = 'right';
				//this.constraintDiv4.innerHTML = `N.D.`;
				
				this.element.appendChild(this.constraintDiv3);
				this.element.appendChild(this.constraintDiv4);
	
				
				this.constraintDiv5 = document.createElement('div');
				this.constraintDiv5.style['grid-column'] = 1;
				//this.constraintDiv5.style['color'] = 'blue';
				this.constraintDiv5.innerHTML = `${this.valueDescription}:`;
				
				
				this.constraintDiv6 = document.createElement('div');
				this.constraintDiv6.style['grid-column'] = 2;
				this.constraintDiv6.style['text-align'] = 'right';
				//this.constraintDiv6.style['color'] = 'blue';
				//this.constraintDiv6.innerHTML = `N.D.`;
	
				
	
				this.element.appendChild(this.constraintDiv5);
				this.element.appendChild(this.constraintDiv6);
				this.updateElement();
			}
			else {
				this.onError = true;
				this.errorDiv.innerHTML = this.missingValueErrorMessage;
			}
		}
		else {
			throw new Error('bad options');
		}
	}

	formatUnit(value) {
		if (this.unit == 'ha') {
			return this.formatValue(value/10000);
		}
		else {
			return this.formatValue(value);
		}
	}
	formatValue(value) {
		if (this.decimals) {
			return value.toLocaleString('it-IT', {minimumFractionDigits: this.decimals, maximumFractionDigits: this.decimals});
		}
		else {
			return value.toLocaleString('it-IT');
		}
	}

	updateElement() {
		if (this._nextArea != this.currArea) {
			if (this._nextArea) {
				this.currArea = this._nextArea;
				this.value = this.evaluator(this.currArea, this.constrValue);
				this.onError = (this.value < this.minValue || this.value > this.maxValue);

				this.constraintDiv4.innerHTML = this.formatUnit(this.currArea);
				this.constraintDiv6.innerHTML = this.formatValue(this.value);

				if (this.onError) {
					this.constraintDiv5.style['color'] = 'red';
					this.constraintDiv6.style['color'] = 'red';
					this.errorDiv.style['display'] = 'unset';
					this.errorDiv.innerHTML = this.wrongValueErrorMessage;
				}
				else {
					this.constraintDiv5.style['color'] = 'green';
					this.constraintDiv6.style['color'] = 'green';
					this.errorDiv.style['display'] = 'none';
					this.errorDiv.innerHTML = '';
				}
			}
			else {
				this.currArea = undefined;
				this.value = undefined;
				
				this.constraintDiv4.innerHTML = 'N.D.';
				this.constraintDiv6.innerHTML = 'N.D.';
				
				this.constraintDiv5.style['color'] = 'orange';
				this.constraintDiv6.style['color'] = 'orange';

				this.errorDiv.style['display'] = 'none';
				this.errorDiv.innerHTML = '';
			}
		}
	}

	render(mapEvent) {
		if (this.constrValue > 0) {
			this.updateElement();
		}
	}
}