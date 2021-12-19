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
  inprogress?: string;
  error?: string;
  invalid = false;

  constructor(
    private service: AuthService,
    private router: Router,
    private http: HttpClient
  ) { }

  public signin(): void {
    this.inprogress = 'Signing in';
    this.invalid = false;
    this.service.login(this.username, this.password)
    .subscribe(ok => {
      if (!ok) {
        this.invalid = true;
      } else {
        this.router.navigateByUrl('/');
      }
      this.inprogress = undefined;
      this.error = undefined;
    }, error => {
      this.error = error;
      this.inprogress = undefined;
    });
  }

  public initdb(): void {
    this.inprogress = 'Initializing database';
    this.http.get(environment.apiUrl + '/initdb', { responseType: 'text' }).subscribe(response => this.inprogress = undefined);
  }

}
