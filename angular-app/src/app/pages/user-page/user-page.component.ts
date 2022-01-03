import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { User } from 'src/app/data/user';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./user-page.component.scss']
})
export class UserPageComponent implements OnInit {

  user$ = new BehaviorSubject<User | undefined>(undefined);

  constructor(
    private route: ActivatedRoute,
    private userService: UserService
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const userId = params.get('userId');
      if (userId != null) {
        if (userId === 'new') {
          this.user$.next(new User());
        } else if (userId === 'me') {
          this.userService.getMyUser().subscribe(user => this.user$.next(user));
        } else {
          const id = parseInt(userId);
          if (!isNaN(id)) {
            this.userService.getUser(id).subscribe(user => this.user$.next(user));
          }
        }
      }
    });
  }

}
