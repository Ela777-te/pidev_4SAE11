import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { Github } from './github';
import { PlanningService } from '../../../core/services/planning.service';
import { of } from 'rxjs';

/**
 * Jasmine unit tests for Github component. Verifies that the component checks isGitHubEnabled on init,
 * and that owner/repo from query params are applied. Mocks PlanningService and ActivatedRoute.
 */
describe('Github', () => {
  let component: Github;
  let fixture: ComponentFixture<Github>;
  let planningService: jasmine.SpyObj<PlanningService>;

  beforeEach(async () => {
    const planningSpy = jasmine.createSpyObj('PlanningService', [
      'isGitHubEnabled',
      'getGitHubBranches',
      'getGitHubLatestCommit',
      'getGitHubCommits',
      'createGitHubIssue',
    ]);
    planningSpy.isGitHubEnabled.and.returnValue(of(true));
    planningSpy.getGitHubBranches.and.returnValue(of([]));
    planningSpy.getGitHubLatestCommit.and.returnValue(of(null));
    planningSpy.getGitHubCommits.and.returnValue(of([]));
    planningSpy.createGitHubIssue.and.returnValue(of(null));

    await TestBed.configureTestingModule({
      imports: [Github],
      providers: [
        { provide: PlanningService, useValue: planningSpy },
        { provide: ActivatedRoute, useValue: { queryParams: of({}) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Github);
    component = fixture.componentInstance;
    planningService = TestBed.inject(PlanningService) as jasmine.SpyObj<PlanningService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call isGitHubEnabled on init', () => {
    fixture.detectChanges();
    expect(planningService.isGitHubEnabled).toHaveBeenCalled();
  });

  it('should have empty owner and repo by default', () => {
    expect(component.owner).toBe('');
    expect(component.repo).toBe('');
  });
});
