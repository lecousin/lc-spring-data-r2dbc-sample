import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { defaultIfEmpty, map } from 'rxjs/operators';
import { Session } from '../data/session';

const localStorageSession = 'example-app.session';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private session$ = new BehaviorSubject<Session | undefined>(undefined);

  private autoRenewTimeout?: any;
  private renewing?: Promise<void>;

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {
    const session = localStorage.getItem(localStorageSession);
    if (session) {
      this.loggedIn(new Session(JSON.parse(session)));
    } else {
      this.loggedOut();
    }
  }

  public isAuthenticated(): boolean {
    return this.session$.value !== undefined;
  }

  public getSession$(): BehaviorSubject<Session | undefined> {
    return this.session$;
  }

  public login(username: string, password: string): Observable<boolean> {
    return new Observable<boolean>(subscriber => {
      this.logout().then(() => {
        this.http.post(environment.apiUrl + '/auth/v1', { username, password }, { responseType: 'text' })
        .pipe(
          defaultIfEmpty(''),
          map(response => {
            if (response === '') {
              return false;
            }
            const session = new Session(JSON.parse(atob(response)));
            session.token = response;
            this.loggedIn(session);
            return this.isAuthenticated();
          })
        ).subscribe(subscriber);
      });
    });
  }

  public logout(): Promise<void> {
    return new Promise<void>((resolve) => {
      if (!this.session$.value) {
        resolve();
        return;
      }
      if (this.autoRenewTimeout) {
        clearTimeout(this.autoRenewTimeout);
        this.autoRenewTimeout = null;
      }
      this.http.delete(environment.apiUrl + '/auth/v1')
      .subscribe(() => {
        this.loggedOut();
        resolve();
      });
    });
  }

  public loggedOut() {
    console.log('Logged out');
    localStorage.removeItem(localStorageSession);
    this.session$.next(undefined);
    this.router.navigateByUrl('/login');
  }

  public getRenewing(): Promise<void> | undefined {
    return this.renewing;
  }

  public getToken(): string | undefined {
    return this.session$.value?.token;
  }

  private renew() {
    this.renewing = new Promise(resolve => {
      this.http.get(environment.apiUrl + '/auth/v1', { responseType: 'text' })
      .subscribe(token => {
        if (token && this.session$.value) {
          const decoded = JSON.parse(atob(token));
          this.session$.value.token = token;
          this.session$.value.expiration = decoded.expiration;
          this.loggedIn(this.session$.value);
        }
        resolve();
        this.renewing = undefined;
      });
    });
    this.renewing.then(() => {});
  }

  private loggedIn(session: Session) {
    console.log('user token: ' + session.token + ' / username: ' + session.username + ' / uuid: ' + session.uuid);
    if (!session.expiration) {
      this.loggedOut();
      return;
    }
    const now = Date.now();
    if (session.expiration <= now) {
      this.loggedOut();
      return;
    }
    localStorage.setItem(localStorageSession, JSON.stringify(session));
    this.session$.next(session);
    this.autoRenew(session.expiration - now);
  }

  private autoRenew(remainingTime: number) {
    if (remainingTime < 60000) {
      remainingTime = 0;
    } else {
      remainingTime -= 60000;
    }
    console.log('Session will be renewed in ' + (remainingTime / 60000) + ' minute(s), at ' + new Date(Date.now() + remainingTime));
    this.autoRenewTimeout = setTimeout(() => {
      this.autoRenewTimeout = null;
      this.renew();
    }, remainingTime);
  }

}
