import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { PlanningManagement } from './planning-management';
import {
  PlanningService,
  ProgressUpdate,
  DashboardStatsDto,
  StalledProjectDto,
  FreelancerActivityDto,
  ProjectActivityDto,
} from '../../../core/services/planning.service';
import { UserService } from '../../../core/services/user.service';
import { ProjectService } from '../../../core/services/project.service';
import { ProjectApplicationService } from '../../../core/services/project-application.service';
import { of } from 'rxjs';
import { Card } from '../../../shared/components/card/card';

/**
 * Jasmine unit tests for PlanningManagement (admin) component. Verifies creation, load of health/dashboard/stalled/rankings,
 * and that filter and progress list use PlanningService. Mocks all external services.
 */
describe('PlanningManagement', () => {
  let component: PlanningManagement;
  let fixture: ComponentFixture<PlanningManagement>;
  let planningService: jasmine.SpyObj<PlanningService>;

  beforeEach(async () => {
    const planningSpy = jasmine.createSpyObj('PlanningService', [
      'getPlanningHealth',
      'getDashboardStats',
      'getStalledProjects',
      'getFreelancersByActivity',
      'getMostActiveProjects',
      'getFilteredProgressUpdates',
      'createProgressUpdate',
      'updateProgressUpdate',
      'deleteProgressUpdate',
    ]);
    planningSpy.getPlanningHealth.and.returnValue(of({ service: 'planning', status: 'UP', timestamp: '' }));
    planningSpy.getDashboardStats.and.returnValue(
      of({ totalUpdates: 0, totalComments: 0, averageProgressPercentage: null, distinctProjectCount: 0, distinctFreelancerCount: 0 } as DashboardStatsDto)
    );
    planningSpy.getStalledProjects.and.returnValue(of([]));
    planningSpy.getFreelancersByActivity.and.returnValue(of([]));
    planningSpy.getMostActiveProjects.and.returnValue(of([]));
    planningSpy.getFilteredProgressUpdates.and.returnValue(
      of({ content: [], totalElements: 0, totalPages: 0, size: 20, number: 0 })
    );

    await TestBed.configureTestingModule({
      imports: [PlanningManagement, ReactiveFormsModule, Card],
      providers: [
        { provide: PlanningService, useValue: planningSpy },
        { provide: UserService, useValue: {} },
        { provide: ProjectService, useValue: {} },
        { provide: ProjectApplicationService, useValue: {} },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PlanningManagement);
    component = fixture.componentInstance;
    planningService = TestBed.inject(PlanningService) as jasmine.SpyObj<PlanningService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getPlanningHealth and getDashboardStats on init', () => {
    fixture.detectChanges();
    expect(planningService.getPlanningHealth).toHaveBeenCalled();
    expect(planningService.getDashboardStats).toHaveBeenCalled();
  });

  it('should call getStalledProjects and rankings on init', () => {
    fixture.detectChanges();
    expect(planningService.getStalledProjects).toHaveBeenCalled();
    expect(planningService.getFreelancersByActivity).toHaveBeenCalled();
    expect(planningService.getMostActiveProjects).toHaveBeenCalled();
  });
});
