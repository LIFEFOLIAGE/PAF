import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RicercaIstanzeComponent } from './ricerca-istanze.component';

describe('RicercaIstanzeComponent', () => {
  let component: RicercaIstanzeComponent;
  let fixture: ComponentFixture<RicercaIstanzeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RicercaIstanzeComponent]
    });
    fixture = TestBed.createComponent(RicercaIstanzeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
