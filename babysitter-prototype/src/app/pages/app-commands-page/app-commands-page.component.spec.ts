import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppCommandsPageComponent } from './app-commands-page.component';

describe('AppAllCoomandsPageComponent', () => {
  let component: AppCommandsPageComponent;
  let fixture: ComponentFixture<AppCommandsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppCommandsPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppCommandsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
