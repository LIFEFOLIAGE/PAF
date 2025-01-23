import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CaricaAllegatiComponent } from './carica-allegati.component';

describe('CaricaAllegatiComponent', () => {
  let component: CaricaAllegatiComponent;
  let fixture: ComponentFixture<CaricaAllegatiComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CaricaAllegatiComponent]
    });
    fixture = TestBed.createComponent(CaricaAllegatiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
