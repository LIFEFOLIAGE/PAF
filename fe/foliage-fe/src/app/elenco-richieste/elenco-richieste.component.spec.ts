import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ElencoRichiesteComponent } from './elenco-richieste.component';

describe('ElencoRichiesteComponent', () => {
  let component: ElencoRichiesteComponent;
  let fixture: ComponentFixture<ElencoRichiesteComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ElencoRichiesteComponent]
    });
    fixture = TestBed.createComponent(ElencoRichiesteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
