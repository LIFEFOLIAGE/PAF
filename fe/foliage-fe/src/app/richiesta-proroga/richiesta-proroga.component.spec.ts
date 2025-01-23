import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RichiestaProrogaComponent } from './richiesta-proroga.component';

describe('RichiestaProrogaComponent', () => {
  let component: RichiestaProrogaComponent;
  let fixture: ComponentFixture<RichiestaProrogaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RichiestaProrogaComponent]
    });
    fixture = TestBed.createComponent(RichiestaProrogaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
