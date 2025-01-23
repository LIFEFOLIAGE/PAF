import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InterventiInAmbitiNonForestaliComponent } from './interventi-in-ambiti-non-forestali.component';

describe('InterventiInAmbitiNonForestaliComponent', () => {
  let component: InterventiInAmbitiNonForestaliComponent;
  let fixture: ComponentFixture<InterventiInAmbitiNonForestaliComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InterventiInAmbitiNonForestaliComponent]
    });
    fixture = TestBed.createComponent(InterventiInAmbitiNonForestaliComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
