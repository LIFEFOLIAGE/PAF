import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CruscottoVigilanzaComponent } from './cruscotto-vigilanza.component';

describe('CruscottoVigilanzaComponent', () => {
  let component: CruscottoVigilanzaComponent;
  let fixture: ComponentFixture<CruscottoVigilanzaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CruscottoVigilanzaComponent]
    });
    fixture = TestBed.createComponent(CruscottoVigilanzaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
