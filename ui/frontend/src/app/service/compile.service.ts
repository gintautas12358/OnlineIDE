import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ErrorService} from './error.service';
import {catchError} from 'rxjs/operators';
import {SourceCode} from '../types/SourceCode';
import {BehaviorSubject, Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CompileService {

  private readonly compileUrl: string;
  private compiledSourceCode: BehaviorSubject<SourceCode> = new BehaviorSubject({code: "", fileName: "", stdout: "", stderr: "", compilable: false});
  public readonly compiledSourceCodeObservable: Observable<SourceCode> = this.compiledSourceCode.asObservable();

  constructor(private http: HttpClient, private errorService: ErrorService) {
    this.compileUrl = '/api/compiler/compile';
  }

  public compile(sourceCode: SourceCode): void {
    this.http.post<SourceCode>(this.compileUrl, sourceCode)
      .pipe(catchError(this.errorService.handleError))
      .subscribe(compiledSourceCode => {
        if(compiledSourceCode.compilable === true) {
          compiledSourceCode.stdout = "Successfully compiled...";
        }
        this.compiledSourceCode.next(
          compiledSourceCode
        );
      });
  }
}
