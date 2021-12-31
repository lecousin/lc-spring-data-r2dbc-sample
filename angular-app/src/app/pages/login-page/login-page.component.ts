import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss']
})
export class LoginPageComponent {

  username: string = "";
  password: string = "";
  signingin = false;
  initDbTodo = 0;
  initDbDone = 0;
  initDbMessage?: string;
  error?: string;
  invalid = false;

  constructor(
    private service: AuthService,
    private router: Router,
    private http: HttpClient
  ) { }

  public signin(): void {
    this.signingin = true;
    this.invalid = false;
    this.service.login(this.username, this.password)
    .subscribe(ok => {
      if (!ok) {
        this.invalid = true;
      } else {
        this.router.navigateByUrl('/');
      }
      this.signingin = false;
      this.error = undefined;
    }, error => {
      this.error = error;
      this.signingin = false;
    });
  }

  public initdb(): void {
    this.initDbMessage = 'Initializing database';
    this.initDbTodo = 1;
    this.initDbDone = 0;
    this.http.get<string[]>(environment.apiUrl + '/initdb').subscribe(list => {
      this.initDbTodo = list.length + 1;
      this.initDbDone = 1;
      this.initDbStep(list, 0);
    }, error => {
      this.error = error;
      this.initDbMessage = undefined;
    });
  }

  private initDbStep(list: string[], index: number) {
    this.initDbMessage = 'Creating ' + list[index];
    this.http.get(environment.apiUrl + '/initdb/' + list[index]).subscribe(ok => {
      this.initDbDone++;
      setTimeout(() => {
        if (index == list.length - 1) {
          this.initDbMessage = undefined;
        } else {
          this.initDbStep(list, index + 1);
        }
      }, 0);
    }, error => {
      this.error = error;
      this.initDbMessage = undefined;
    });
  }

}
