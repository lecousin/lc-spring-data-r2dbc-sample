import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { Book } from 'src/app/data/book';
import { AuthService } from 'src/app/service/auth.service';
import { BookService } from 'src/app/service/book.service';

@Component({
  selector: 'app-book-page',
  templateUrl: './book-page.component.html',
  styleUrls: ['./book-page.component.scss']
})
export class BookPageComponent implements OnInit {

  book$ = new BehaviorSubject<Book | undefined>(undefined);

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const bookId = params.get('bookId');
      if (bookId != null) {
        const id = parseInt(bookId);
        if (!isNaN(id)) {
          this.bookService.getBook(id).subscribe(book => this.book$.next(book));
        } else if (bookId === 'new') {
          this.book$.next(new Book());
        }
      }
    });
  }

}
