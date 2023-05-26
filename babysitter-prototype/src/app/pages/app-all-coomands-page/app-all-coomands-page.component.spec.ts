import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppAllCoomandsPageComponent } from './app-all-coomands-page.component';

describe('AppAllCoomandsPageComponent', () => {
  let component: AppAllCoomandsPageComponent;
  let fixture: ComponentFixture<AppAllCoomandsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppAllCoomandsPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppAllCoomandsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
