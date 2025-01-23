import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableRendererComponent2 } from './table-renderer2.component';

describe('TableRendererComponent', () => {
  let component: TableRendererComponent2;
  let fixture: ComponentFixture<TableRendererComponent2>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TableRendererComponent2 ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TableRendererComponent2);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
