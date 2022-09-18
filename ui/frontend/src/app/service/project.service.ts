import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {Project} from '../types/Project';
import {ErrorService} from './error.service';
import {catchError} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  private readonly projectsUrl: string;

  private projects: BehaviorSubject<Project[]> = new BehaviorSubject([]);
  public readonly projectsObservable: Observable<Project[]> = this.projects.asObservable();

  constructor(private http: HttpClient, private errorService: ErrorService) {
    this.projectsUrl = '/api/project/projects';
  }

  public getProjectsUrl(): string {
    return this.projectsUrl;
  }

  public getProjectById(id: string): Project {
    return this.projects.getValue().find(p => p.id === id);
  }

  public fetchProjects(): void {
    this.http.get<Project[]>(this.projectsUrl)
      .pipe(catchError(this.errorService.handleError))
      .subscribe(projectList => this.projects.next(projectList));
  }

  public createProject(project: { name: string }): void {
    this.http.post<Project>(this.projectsUrl, project)
      .pipe(catchError(this.errorService.handleError))
      .subscribe(createdProject => this.projects.next(
        this.projects.getValue().concat(createdProject)
      ));
  }

  public editProject(project: Project): void {
    this.http.put<Project>(this.projectsUrl + '/' + project.id, project)
      .pipe(catchError(this.errorService.handleError))
      .subscribe(updatedProject => this.replaceProject(updatedProject));
  }

  private replaceProject(project: Project): void {
    let newProjects = this.projects.getValue();
    let indexOfToBeUpdated = this.projects.getValue().findIndex(p => p.id === project.id);
    newProjects[indexOfToBeUpdated] = project;
    this.projects.next(newProjects);
  }

  public deleteProject(id: string): void {
    this.http.delete(this.projectsUrl + '/' + id)
      .pipe(catchError(this.errorService.handleError))
      .subscribe(() => {
        const index = this.projects.getValue().findIndex(p => p.id === id);
        let tmp: Project[] = this.projects.getValue();
        tmp.splice(index, 1);
        this.projects.next(tmp);
      });
  }

  public shareProject(project: Project, newUser: string): void {
    this.http.put<Project>(this.projectsUrl + '/' + project.id + '/' + newUser, project)
      .pipe(catchError(this.errorService.handleError))
      .subscribe(updatedProject => this.replaceProject(updatedProject));
  }
}
