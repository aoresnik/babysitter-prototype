import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppCommandsListPaneComponent } from './app-commands-list-pane.component';

describe('AppCommandsListPaneComponent', () => {
  let component: AppCommandsListPaneComponent;
  let fixture: ComponentFixture<AppCommandsListPaneComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppCommandsListPaneComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppCommandsListPaneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
