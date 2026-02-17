import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ProjectService, Project } from '../../../core/services/project.service';

@Component({
  selector: 'app-show-project',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './show-project.html',
  styleUrl: './show-project.scss',
})
export class ShowProject implements OnInit {
  project: Project | null = null;
  isLoading = true;
  errorMessage: string | null = null;
  id!: number;

  constructor(
    private route: ActivatedRoute,
    private projectService: ProjectService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    if (!this.id || Number.isNaN(this.id)) {
      this.errorMessage = 'Invalid project.';
      this.isLoading = false;
      this.cdr.detectChanges();
      return;
    }
    this.loadProject();
  }

  loadProject(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.cdr.detectChanges();

    this.projectService.getProjectById(this.id).subscribe({
      next: (res: Project | null) => {
        this.project = res ?? null;
        if (!this.project) {
          this.errorMessage = 'Project not found.';
        }
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Failed to load project details.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  getSkills(): string[] {
    const skills = this.project?.skillsRequiered;
    if (!skills) return [];
    const str = Array.isArray(skills) ? (skills as string[]).join(',') : String(skills);
    return str
      .split(',')
      .map((s: string) => s.trim())
      .filter((s: string) => s.length > 0);
  }
}
