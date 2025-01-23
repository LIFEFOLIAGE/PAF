import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelettoreAmbitoComponent } from './selettore-ambito.component';

describe('SelettoreAmbitoComponent', () => {
  let component: SelettoreAmbitoComponent;
  let fixture: ComponentFixture<SelettoreAmbitoComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SelettoreAmbitoComponent]
    });
    fixture = TestBed.createComponent(SelettoreAmbitoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
