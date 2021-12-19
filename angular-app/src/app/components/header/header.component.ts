import { Component } from '@angular/core';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {

  username: string;

  constructor(
    private auth: AuthService
  ) {
    this.username = auth.getSession$().value?.username || '';
  }

  public logout(): void {
    this.auth.logout();
  }

}
