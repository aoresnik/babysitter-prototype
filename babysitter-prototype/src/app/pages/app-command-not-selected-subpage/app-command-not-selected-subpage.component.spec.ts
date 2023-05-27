import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppCommandNotSelectedSubpageComponent } from './app-command-not-selected-subpage.component';

describe('AppCommandsListPageComponent', () => {
  let component: AppCommandNotSelectedSubpageComponent;
  let fixture: ComponentFixture<AppCommandNotSelectedSubpageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppCommandNotSelectedSubpageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppCommandNotSelectedSubpageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
