import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { AuthService } from './auth.service';
import { Observable, throwError, EMPTY } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable()
export class TokenInjector implements HttpInterceptor {
  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const renew = this.auth.getRenewing();
    if (renew) {
      return new Observable<HttpEvent<any>>(observer => {
        renew.then(() => {
          this.intercept(request, next).subscribe((event) => {
            observer.next(event);
          }, (error) => {
            observer.error(error);
          }, () => {
            observer.complete();
          });
        });
      });
    }
    if (this.auth.isAuthenticated()) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${this.auth.getToken()}`
        }
      });
    }
    return next.handle(request).pipe(
      catchError((err) => {
        if ((<HttpErrorResponse>err).status === 401) {
          if (request.method === 'DELETE' && request.url.endsWith('/auth/v1')) {
            this.auth.loggedOut();
            return EMPTY;
          } else {
            this.auth.logout();
            this.router.navigateByUrl('/login');
            return EMPTY;
          }
        }
        console.log(err);
        return throwError((<HttpErrorResponse>err).message);
      })
    );
  }
}
