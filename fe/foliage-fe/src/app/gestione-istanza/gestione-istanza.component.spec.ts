import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestioneIstanzaComponent } from './gestione-istanza.component';

describe('GestioneIstanzaComponent', () => {
  let component: GestioneIstanzaComponent;
  let fixture: ComponentFixture<GestioneIstanzaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GestioneIstanzaComponent]
    });
    fixture = TestBed.createComponent(GestioneIstanzaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
