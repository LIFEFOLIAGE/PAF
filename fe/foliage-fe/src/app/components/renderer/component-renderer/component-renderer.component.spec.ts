import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComponentRendererComponent } from './component-renderer.component';

describe('ComponentRendererComponent', () => {
  let component: ComponentRendererComponent<any>;
  let fixture: ComponentFixture<ComponentRendererComponent<any>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ComponentRendererComponent]
    });
    fixture = TestBed.createComponent(ComponentRendererComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
