import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompilaIstanzaComponent } from './compila-istanza.component';

describe('CompilaIstanzaComponent', () => {
  let component: CompilaIstanzaComponent;
  let fixture: ComponentFixture<CompilaIstanzaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CompilaIstanzaComponent]
    });
    fixture = TestBed.createComponent(CompilaIstanzaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
