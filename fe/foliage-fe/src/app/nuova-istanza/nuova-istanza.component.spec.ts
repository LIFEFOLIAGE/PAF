import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NuovaIstanzaComponent } from './nuova-istanza.component';

describe('NuovaIstanzaComponent', () => {
  let component: NuovaIstanzaComponent;
  let fixture: ComponentFixture<NuovaIstanzaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NuovaIstanzaComponent]
    });
    fixture = TestBed.createComponent(NuovaIstanzaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
