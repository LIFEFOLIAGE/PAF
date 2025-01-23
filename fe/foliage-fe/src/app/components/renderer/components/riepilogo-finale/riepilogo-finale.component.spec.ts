import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RiepilogoFinaleComponent } from './riepilogo-finale.component';

describe('RiepilogoFinaleComponent', () => {
  let component: RiepilogoFinaleComponent;
  let fixture: ComponentFixture<RiepilogoFinaleComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RiepilogoFinaleComponent]
    });
    fixture = TestBed.createComponent(RiepilogoFinaleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
