import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AppCommandPageComponent} from './app-command-page.component';

describe('AppCommandPageComponent', () => {
  let component: AppCommandPageComponent;
  let fixture: ComponentFixture<AppCommandPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppCommandPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppCommandPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
