import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormioRendererComponent } from './formio-rederer.component';

describe('FormioRedererComponent', () => {
  let component: FormioRendererComponent;
  let fixture: ComponentFixture<FormioRendererComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FormioRendererComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormioRendererComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
