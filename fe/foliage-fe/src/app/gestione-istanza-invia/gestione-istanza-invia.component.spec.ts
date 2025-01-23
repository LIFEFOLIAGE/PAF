import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestioneIstanzaInviaComponent } from './gestione-istanza-invia.component';

describe('GestioneIstanzaInviaComponent', () => {
  let component: GestioneIstanzaInviaComponent;
  let fixture: ComponentFixture<GestioneIstanzaInviaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GestioneIstanzaInviaComponent]
    });
    fixture = TestBed.createComponent(GestioneIstanzaInviaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
