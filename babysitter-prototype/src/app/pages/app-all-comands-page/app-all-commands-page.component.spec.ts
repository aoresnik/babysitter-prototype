import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppAllCommandsPageComponent } from './app-all-commands-page.component';

describe('AppAllCoomandsPageComponent', () => {
  let component: AppAllCommandsPageComponent;
  let fixture: ComponentFixture<AppAllCommandsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppAllCommandsPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppAllCommandsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
