import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { User } from '../data/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(
    private http: HttpClient
  ) { }

  public getUsers(): Observable<User[]> {
    return this.http.get<User[]>(environment.apiUrl + '/user/v1').pipe(
      map(users => users.map(user => new User(user)))
    );
  }

  public getUser(userId: number): Observable<User> {
    return this.http.get<User>(environment.apiUrl + '/user/v1/user/' + userId).pipe(
      map(user => new User(user))
    );
  }

  public createUser(username: string, password: string, admin: boolean): Observable<User> {
    return this.http.post<User>(environment.apiUrl + '/user/v1', {username, password, admin}).pipe(
      map(user => new User(user))
    );
  }

  public resetUserPassword(userId: number): Observable<string> {
    return this.http.get(environment.apiUrl + '/user/v1/user/' + userId + '/resetpassword', {responseType: 'text'});
  }

  public setUserAdministrator(userId: number, admin: boolean): Observable<User> {
    return this.http.put<User>(environment.apiUrl + '/user/v1/user/' + userId + '/admin', null, { params: new HttpParams().append('admin', admin ? 'true' : 'false')}).pipe(
      map(user => new User(user))
    );
  }

  public getMyUser(): Observable<User> {
    return this.http.get<User>(environment.apiUrl + '/user/v1/me').pipe(
      map(user => new User(user))
    );
  }

  public changeMyPassword(newPassword: string): Observable<User> {
    return this.http.put<User>(environment.apiUrl + '/user/v1/me/password', newPassword).pipe(
      map(user => new User(user))
    );
  }

}
