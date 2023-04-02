import { Injectable } from '@angular/core';
import {from, Observable, of} from 'rxjs';
import {catchError, tap, map} from 'rxjs/operators';
import {HttpClient, HttpHeaders} from "@angular/common/http";

// TODO: stderr, stout, timestamp?
export class ScriptResult
{

  constructor(lines: string[]) {
    this.lines = lines;
  }

  lines: string[];
}

export class ScriptError
{
  constructor(errorMsg: string) {
    this.errorMsg = errorMsg;
  }

  errorMsg: string;
}

@Injectable({
  providedIn: 'root'
})
export class ScriptsServiceService {

  serverRootUrl: string = "http://localhost:8080"

  constructor(private http: HttpClient) {
  }

  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };

  getScripts(): Observable<string[]> {
    return this.http.get<string[]>(`${ this.serverRootUrl }/api/v1/scripts`, this.httpOptions ).pipe(
      tap(res => console.log('Loaded list of scripts')),
      catchError(this.handleError<any>('saveNewState'))
    );
  }

  runScript(script: string): Observable<ScriptResult | ScriptError> {
    return this.http.post<string[]>(`${ this.serverRootUrl }/api/v1/scripts/${script}/run`, this.httpOptions ).pipe(
      tap(res => console.log(`Run script ${ script }`)),
      map(res => {
        return new ScriptResult(res);
      }),
      catchError(err => {
        console.log(err);
        return of(new ScriptError(err.error.details));
      })
    );
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T): (error: any) => Observable<T> {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

}
