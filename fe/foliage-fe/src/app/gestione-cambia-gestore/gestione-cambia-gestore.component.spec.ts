import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestioneCambiaGestoreComponent } from './gestione-cambia-gestore.component';

describe('GestioneCambiaGestoreComponent', () => {
  let component: GestioneCambiaGestoreComponent;
  let fixture: ComponentFixture<GestioneCambiaGestoreComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GestioneCambiaGestoreComponent]
    });
    fixture = TestBed.createComponent(GestioneCambiaGestoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
