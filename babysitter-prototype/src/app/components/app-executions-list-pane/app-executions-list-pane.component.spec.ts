import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AppExecutionsListPaneComponent} from './app-executions-list-pane.component';

describe('AppExecutionsListPaneComponent', () => {
  let component: AppExecutionsListPaneComponent;
  let fixture: ComponentFixture<AppExecutionsListPaneComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppExecutionsListPaneComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppExecutionsListPaneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
