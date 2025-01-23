import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViabilitaForestaleComponent } from './viabilita-forestale.component';

describe('ViabilitaForestaleComponent', () => {
  let component: ViabilitaForestaleComponent;
  let fixture: ComponentFixture<ViabilitaForestaleComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ViabilitaForestaleComponent]
    });
    fixture = TestBed.createComponent(ViabilitaForestaleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
