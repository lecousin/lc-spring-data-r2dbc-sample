import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.scss']
})
export class LoadingComponent {

  @Input() message?: string;
  @Input() todo = 0;
  @Input() done = 0;
  @Input() type = 'spinner';

  constructor() { }

}
