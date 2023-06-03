import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {WebsocketTestService} from "./websocket-test.service";
import {Observable, of} from "rxjs";
import {catchError, map, tap} from "rxjs/operators";
import {environment} from "../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ScriptExecutionsService {
  serverRootUrl: string = environment.serverRootURL;

  constructor(private http: HttpClient, private scriptRunWSService: WebsocketTestService) {
  }

  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };

  getScriptExecutions(): Observable<any[]> {
    return this.http.get<string[]>(`${ this.serverRootUrl }/api/v1/executions`, this.httpOptions ).pipe(
      tap(res => console.log('Loaded list of script executions')),
      catchError(this.handleError<any>('saveNewState'))
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
