import { AfterViewInit, Component } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { User } from 'src/app/data/user';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-users-page',
  templateUrl: './users-page.component.html',
  styleUrls: ['./users-page.component.scss']
})
export class UsersPageComponent implements AfterViewInit {

  displayedColumns = ['username', 'admin'];
  isLoadingResults = true;
  results$ = new BehaviorSubject<User[]>([]);

  constructor(
    private userService: UserService,
    private router: Router
  ) {
  }

  ngAfterViewInit(): void {
    this.search();
  }

  public search() {
    this.isLoadingResults = true;
    this.userService.getUsers().subscribe(response => {
      this.isLoadingResults = false;
      this.results$.next(response);
    });
  }

  public selectUser(user: User): void {
    this.router.navigateByUrl('/user/' + user.id);
  }

  public newUser(): void {
    this.router.navigateByUrl('/user/new');
  }

}
