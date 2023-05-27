import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppCommandExecutionNotSelectedSubpageComponent } from './app-command-execution-not-selected-subpage.component';

describe('AppCommandExecutionNotSelectedSubpageComponent', () => {
  let component: AppCommandExecutionNotSelectedSubpageComponent;
  let fixture: ComponentFixture<AppCommandExecutionNotSelectedSubpageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppCommandExecutionNotSelectedSubpageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppCommandExecutionNotSelectedSubpageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
