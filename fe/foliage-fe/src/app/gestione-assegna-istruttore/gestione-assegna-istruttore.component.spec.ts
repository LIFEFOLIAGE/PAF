import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestioneAssegnaIstruttoreComponent } from './gestione-assegna-istruttore.component';

describe('GestioneAssegnaIstruttoreComponent', () => {
  let component: GestioneAssegnaIstruttoreComponent;
  let fixture: ComponentFixture<GestioneAssegnaIstruttoreComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GestioneAssegnaIstruttoreComponent]
    });
    fixture = TestBed.createComponent(GestioneAssegnaIstruttoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
