import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerificaLayerVincolatiComponent } from './verifica-layer-vincolati.component';

describe('VerificaLayerVincolatiComponent', () => {
  let component: VerificaLayerVincolatiComponent;
  let fixture: ComponentFixture<VerificaLayerVincolatiComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VerificaLayerVincolatiComponent]
    });
    fixture = TestBed.createComponent(VerificaLayerVincolatiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
