import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/data/user';
import { AuthService } from 'src/app/service/auth.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.scss']
})
export class UserFormComponent implements OnInit {

  @Input() user?: User;

  isAdmin: boolean;
  isNew = false;
  myUsername = '';
  loadingMessage?: string;
  errorMessage?: string;

  newUsername = '';
  newPassword = '';
  newAdmin = false;

  generatedPassword?: string;
  changeMyPassword = false;

  constructor(
    auth: AuthService,
    private userService: UserService,
    private router: Router
  ) {
    this.isAdmin = auth.getSession$().value?.admin || false;
    this.myUsername = auth.getSession$().value?.username || '';
  }

  ngOnInit(): void {
    this.update();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.update();
  }

  private update(): void {
    this.errorMessage = undefined;
    if (this.user) {
      this.isNew = !(this.user.id > 0);
    }
  }

  public revokeAdmin(): void {
    this.errorMessage = undefined;
    this.loadingMessage = undefined;
    if (this.user) {
      this.loadingMessage = 'Revoking administrator role';
      this.userService.setUserAdministrator(this.user.id, false).subscribe(user => {
        this.loadingMessage = undefined;
        this.user = user;
      }, error => {
        this.loadingMessage = undefined;
        this.errorMessage = error;
      });
    }
  }

  public giveAdmin(): void {
    this.errorMessage = undefined;
    if (this.user) {
      this.loadingMessage = 'Giving administrator role';
      this.userService.setUserAdministrator(this.user.id, true).subscribe(user => {
        this.loadingMessage = undefined;
        this.user = user;
      }, error => {
        this.loadingMessage = undefined;
        this.errorMessage = error;
      });
    }
  }

  public resetPassword(): void {
    this.errorMessage = undefined;
    if (this.user) {
      this.loadingMessage = 'Resetting password';
      this.userService.resetUserPassword(this.user.id).subscribe(pwd => {
        this.loadingMessage = undefined;
        this.generatedPassword = pwd;
      }, error => {
        this.loadingMessage = undefined;
        this.errorMessage = error;
      });
    }
  }

  public saveMyPassword(): void {
    this.errorMessage = undefined;
    if (this.user) {
      this.loadingMessage = 'Saving password';
      this.userService.changeMyPassword(this.newPassword).subscribe(response => {
        this.loadingMessage = undefined;
        this.newPassword = '';
        this.changeMyPassword = false;
      }, error => {
        this.loadingMessage = undefined;
        this.errorMessage = error;
      });
    }
  }

  public saveNewUser(): void {
    this.errorMessage = undefined;
    this.loadingMessage = 'Creating user';
    this.userService.createUser(this.newUsername, this.newPassword, this.newAdmin).subscribe(user => {
      this.loadingMessage = undefined;
      this.router.navigateByUrl('/user/' + user.id);
    }, error => {
      this.loadingMessage = undefined;
      this.errorMessage = error;
    })
  }

}
