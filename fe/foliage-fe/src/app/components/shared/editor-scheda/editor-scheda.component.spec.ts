import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditorSchedaComponent } from './editor-scheda.component';

describe('EditorSezionePraticaComponent', () => {
  let component: EditorSchedaComponent;
  let fixture: ComponentFixture<EditorSchedaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditorSchedaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditorSchedaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
