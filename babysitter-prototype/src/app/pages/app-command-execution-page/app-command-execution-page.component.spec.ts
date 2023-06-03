import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AppCommandExecutionPageComponent} from './app-command-execution-page.component';

describe('AppCommandExecutionPageComponent', () => {
  let component: AppCommandExecutionPageComponent;
  let fixture: ComponentFixture<AppCommandExecutionPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppCommandExecutionPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppCommandExecutionPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
