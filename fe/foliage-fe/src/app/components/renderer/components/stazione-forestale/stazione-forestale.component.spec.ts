import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StazioneForestaleComponent } from './stazione-forestale.component';

describe('StazioneForestaleComponent', () => {
  let component: StazioneForestaleComponent;
  let fixture: ComponentFixture<StazioneForestaleComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StazioneForestaleComponent]
    });
    fixture = TestBed.createComponent(StazioneForestaleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
