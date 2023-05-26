import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppAllElementsTestPageComponent } from './app-all-elements-test-page.component';

describe('AppCommandsListPaneComponent', () => {
  let component: AppAllElementsTestPageComponent;
  let fixture: ComponentFixture<AppAllElementsTestPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppAllElementsTestPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppAllElementsTestPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
