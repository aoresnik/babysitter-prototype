import { Injectable } from '@angular/core';
import {from, Observable, of} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {HttpClient, HttpHeaders} from "@angular/common/http";

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

  getScripts(): Observable<any> {
    return this.http.get(`${ this.serverRootUrl }/api/v1/scripts`, this.httpOptions ).pipe(
      tap(res => console.log('Loaded list of scripts')),
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
