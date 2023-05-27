import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppCommandExecutionsPage } from './app-command-executions-page.component';

describe('AppActiveRunsPageComponent', () => {
  let component: AppCommandExecutionsPage;
  let fixture: ComponentFixture<AppCommandExecutionsPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppCommandExecutionsPage ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppCommandExecutionsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
