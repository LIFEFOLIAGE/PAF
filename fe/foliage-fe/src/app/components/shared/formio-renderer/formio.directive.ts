import { Directive, ElementRef } from '@angular/core';

@Directive({
  selector: '[appFormio]'
})
export class FormioDirective {
  constructor(public elem: ElementRef) { 
    console.log(elem);
  }
}
