import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {File} from '../types/File';
import {ErrorService} from './error.service';
import {catchError} from 'rxjs/operators';
import {ProjectService} from './project.service';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  private filesUrl: string;

  private files: BehaviorSubject<File[]> = new BehaviorSubject([]);
  public readonly filesObservable: Observable<File[]> = this.files.asObservable();

  constructor(private http: HttpClient, private errorService: ErrorService, private projectService: ProjectService) {
    this.filesUrl = '/api/project/sourceFiles';
  }

  public saveCodeOnSelectedFile(args: {code: string, index: number}): void {
    let currentFile: File = this.files.getValue()[args.index];
    currentFile.code = args.code;
    this.editFile(currentFile);
  }

  public fetchFilesByProjectId(projectId: string): void {
    this.http.get<File[]>(this.projectService.getProjectsUrl() + "/" + projectId + "/sourceFiles")
      .pipe(catchError(this.errorService.handleError)).subscribe(
      resultFiles => this.files.next(resultFiles)
    );
  }

  public createFile(file: { name: string, projectId: string }): void {
    let fileHTTP = {
      project: this.projectService.getProjectById(file.projectId),
      name: file.name,
      // should not be an empty string, otherwise editor won't appear
      code: '// Welcome to the editor'
    };
    this.http.post<File>(this.filesUrl, fileHTTP)
      .pipe(catchError(this.errorService.handleError))
      .subscribe(createdFile => this.files.next(this.files.getValue().concat(createdFile)));
  }

  public editFile(file: File): void {
    this.http.put<File>(this.filesUrl + '/' + file.id, file)
      .pipe(catchError(this.errorService.handleError))
      .subscribe(updatedFile => this.replaceFile(updatedFile));
  }

  private replaceFile(file: File): void {
    let newFiles = this.files.getValue();
    let indexOfToBeUpdated = this.files.getValue().findIndex(f => f.id === file.id);
    newFiles[indexOfToBeUpdated] = file;
    this.files.next(newFiles);
  }

  public deleteFile(id: string): void {
    this.http.delete(this.filesUrl + '/' + id)
      .pipe(catchError(this.errorService.handleError))
      .subscribe(() => {
        const index = this.files.getValue().findIndex(p => p.id === id);
        let tmp: File[] = this.files.getValue();
        tmp.splice(index, 1);
        this.files.next(tmp);
      });
  }
}
