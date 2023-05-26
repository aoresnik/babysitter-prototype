import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppCommandsListPageComponent } from './app-commands-list-page.component';

describe('AppCommandsListPageComponent', () => {
  let component: AppCommandsListPageComponent;
  let fixture: ComponentFixture<AppCommandsListPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppCommandsListPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppCommandsListPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
