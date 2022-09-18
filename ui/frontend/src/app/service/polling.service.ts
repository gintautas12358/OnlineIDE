import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PollingService {

  constructor(private http: HttpClient) {
  }

  public getDarkmodeActivated(): Observable<boolean> {
    return this.http.get<boolean>('/dark-mode/');
  }

}
