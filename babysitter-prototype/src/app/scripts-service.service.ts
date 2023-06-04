import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {WebsocketTestService} from "./websocket-test.service";
import {environment} from "../environments/environment";

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

  serverRootUrl: string = environment.serverRootURL;

  constructor(private http: HttpClient, private scriptRunWSService: WebsocketTestService) {
  }

  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };

  getScripts(): Observable<any[]> {
    return this.http.get<string[]>(`${ this.serverRootUrl }/api/v1/commands`, this.httpOptions ).pipe(
      tap(res => console.log('Loaded list of scripts')),
      catchError(this.handleError<any>('saveNewState'))
    );
  }

  getMostUsedCommands(): Observable<any[]> {
    return this.http.get<string[]>(`${ this.serverRootUrl }/api/v1/commands/most-used`, this.httpOptions ).pipe(
      tap(res => console.log('Loaded list of scripts')),
      catchError(this.handleError<any>('saveNewState'))
    );
  }

  getLastUsedCommands(): Observable<any[]> {
    return this.http.get<string[]>(`${ this.serverRootUrl }/api/v1/commands/last-used`, this.httpOptions ).pipe(
      tap(res => console.log('Loaded list of scripts')),
      catchError(this.handleError<any>('saveNewState'))
    );
  }

  runScriptAsync(commandSourceId: number, script: string): Observable<string> {
    return this.http.post<string>(`${ this.serverRootUrl }/api/v1/commands/sources/${commandSourceId}/${script}/run-async`, this.httpOptions ).pipe(
      tap(res => console.log(`Run script ${ script }`)),
      map(res => {
        return res;
      }),
      catchError(err => {
        console.log(err);
        return of('');
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
