import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppCommandViewComponent } from './app-command-view.component';

describe('AppCommandViewComponent', () => {
  let component: AppCommandViewComponent;
  let fixture: ComponentFixture<AppCommandViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppCommandViewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppCommandViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
