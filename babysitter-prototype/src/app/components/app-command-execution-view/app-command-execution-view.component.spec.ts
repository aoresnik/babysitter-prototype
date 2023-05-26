import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppCommandExecutionViewComponent } from './app-command-execution-view.component';

describe('AppCommandExecutionViewComponent', () => {
  let component: AppCommandExecutionViewComponent;
  let fixture: ComponentFixture<AppCommandExecutionViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppCommandExecutionViewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppCommandExecutionViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
