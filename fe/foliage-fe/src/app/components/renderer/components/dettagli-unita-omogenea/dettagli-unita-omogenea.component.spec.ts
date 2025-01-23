import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DettagliUnitaOmogeneaComponent } from './dettagli-unita-omogenea.component';

describe('DettagliUnitaOmogeneaComponent', () => {
  let component: DettagliUnitaOmogeneaComponent;
  let fixture: ComponentFixture<DettagliUnitaOmogeneaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DettagliUnitaOmogeneaComponent]
    });
    fixture = TestBed.createComponent(DettagliUnitaOmogeneaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
