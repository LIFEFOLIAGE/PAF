import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GisTableComponent } from './gis-table.component';

describe('GisTableComponent', () => {
  let component: GisTableComponent;
  let fixture: ComponentFixture<GisTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GisTableComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GisTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
