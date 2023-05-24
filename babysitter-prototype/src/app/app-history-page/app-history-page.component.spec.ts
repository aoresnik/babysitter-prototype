import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppHistoryPageComponent } from './app-history-page.component';

describe('AppHistoryPageComponent', () => {
  let component: AppHistoryPageComponent;
  let fixture: ComponentFixture<AppHistoryPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppHistoryPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppHistoryPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
