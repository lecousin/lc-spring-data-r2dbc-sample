import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Book } from 'src/app/data/book';
import { BookSearchRequest, BookService } from 'src/app/service/book.service';

@Component({
  selector: 'app-book-search-page',
  templateUrl: './book-search-page.component.html',
  styleUrls: ['./book-search-page.component.scss']
})
export class BookSearchPageComponent {

  displayedColumns = ['title', 'year', 'authors', 'publisher'];
  isLoadingResults = true;
  filter = new BookSearchRequest();
  results$ = new BehaviorSubject<Book[]>([]);

  constructor(
    private bookService: BookService
  ) {
    this.search();
  }

  public search() {
    this.isLoadingResults = true;
    this.bookService.searchBooks(this.filter).subscribe(books => {
      this.isLoadingResults = false;
      this.results$.next(books);
    });
  }

  public resetPaging() {
    // TODO
  }

}
