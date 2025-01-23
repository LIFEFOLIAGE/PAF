import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormRichiestaComponent } from './form-richiesta.component';

describe('FormRichiestaComponent', () => {
  let component: FormRichiestaComponent;
  let fixture: ComponentFixture<FormRichiestaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FormRichiestaComponent]
    });
    fixture = TestBed.createComponent(FormRichiestaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
