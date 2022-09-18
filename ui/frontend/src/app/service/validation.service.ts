import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ValidationService {

  private errorMsg: string = "Input should be minimum 3 characters (whitespace excluded).";

  public validateNamingInput(name: string): boolean {
    const withOutWhitespace = name.trim();
    return withOutWhitespace.length >= 3;
  }

  public getErrorMsg(): string {
    return this.errorMsg;
  }
}
