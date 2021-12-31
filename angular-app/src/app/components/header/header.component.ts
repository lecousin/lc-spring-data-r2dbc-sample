import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';

class MenuItem {

  constructor(
    public title: string,
    public route: string,
  ) {
  }

}

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {

  menu: MenuItem[];
  username: string;
  isAdmin: boolean;

  constructor(
    private auth: AuthService,
    private router: Router
  ) {
    this.username = auth.getSession$().value?.username || '';
    this.isAdmin = auth.getSession$().value?.admin || false;
    this.menu = [
      new MenuItem('Books', '/book')
    ];
  }

  public isActive(item: MenuItem): boolean {
    if (this.router.url === item.route || this.router.url.startsWith(item.route + '/')) {
      return true;
    }
    return false;
  }

  public logout(): void {
    this.auth.logout();
  }

}
