import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormRuoliUtenteComponent } from './form-ruoli-utente.component';

describe('FormRuoliUtenteComponent', () => {
  let component: FormRuoliUtenteComponent;
  let fixture: ComponentFixture<FormRuoliUtenteComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FormRuoliUtenteComponent]
    });
    fixture = TestBed.createComponent(FormRuoliUtenteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
