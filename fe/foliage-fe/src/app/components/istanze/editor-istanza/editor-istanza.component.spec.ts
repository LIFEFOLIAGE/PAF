import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditorIstanzaComponent } from './editor-istanza.component';

describe('EditorPraticaComponent', () => {
  let component: EditorIstanzaComponent;
  let fixture: ComponentFixture<EditorIstanzaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditorIstanzaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditorIstanzaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
