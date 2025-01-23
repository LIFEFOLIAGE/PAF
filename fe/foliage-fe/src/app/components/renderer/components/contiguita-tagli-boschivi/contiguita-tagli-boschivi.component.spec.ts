import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContiguitaTagliBoschiviComponent } from './contiguita-tagli-boschivi.component';

describe('ContiguitaTagliBoschiviComponent', () => {
  let component: ContiguitaTagliBoschiviComponent;
  let fixture: ComponentFixture<ContiguitaTagliBoschiviComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ContiguitaTagliBoschiviComponent]
    });
    fixture = TestBed.createComponent(ContiguitaTagliBoschiviComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
