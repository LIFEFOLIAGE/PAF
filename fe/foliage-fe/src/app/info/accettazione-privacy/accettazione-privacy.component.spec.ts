import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccettazionePrivacyComponent } from './accettazione-privacy.component';

describe('AccettazionePrivacyComponent', () => {
  let component: AccettazionePrivacyComponent;
  let fixture: ComponentFixture<AccettazionePrivacyComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AccettazionePrivacyComponent]
    });
    fixture = TestBed.createComponent(AccettazionePrivacyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
