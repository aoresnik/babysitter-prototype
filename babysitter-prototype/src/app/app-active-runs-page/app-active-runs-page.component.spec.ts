import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppActiveRunsPageComponent } from './app-active-runs-page.component';

describe('AppActiveRunsPageComponent', () => {
  let component: AppActiveRunsPageComponent;
  let fixture: ComponentFixture<AppActiveRunsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppActiveRunsPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppActiveRunsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
