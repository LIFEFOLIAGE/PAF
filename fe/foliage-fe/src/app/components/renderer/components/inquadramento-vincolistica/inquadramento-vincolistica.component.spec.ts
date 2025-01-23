import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InquadramentoVincolisticaComponent } from './inquadramento-vincolistica.component';

describe('InquadramentoVincolisticaComponent', () => {
  let component: InquadramentoVincolisticaComponent;
  let fixture: ComponentFixture<InquadramentoVincolisticaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InquadramentoVincolisticaComponent]
    });
    fixture = TestBed.createComponent(InquadramentoVincolisticaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
