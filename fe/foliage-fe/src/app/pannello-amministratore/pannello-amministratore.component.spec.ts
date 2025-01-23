import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PannelloAmministratoreComponent } from './pannello-amministratore.component';

describe('PannelloAmministratoreComponent', () => {
  let component: PannelloAmministratoreComponent;
  let fixture: ComponentFixture<PannelloAmministratoreComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PannelloAmministratoreComponent]
    });
    fixture = TestBed.createComponent(PannelloAmministratoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
