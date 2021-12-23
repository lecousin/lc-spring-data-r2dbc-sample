import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Book } from 'src/app/data/book';
import { BookSearchRequest, BookService } from 'src/app/service/book.service';

@Component({
  selector: 'app-book-search-page',
  templateUrl: './book-search-page.component.html',
  styleUrls: ['./book-search-page.component.scss']
})
export class BookSearchPageComponent implements AfterViewInit {

  displayedColumns = ['book.title', 'book.year', 'author.name', 'publisher.name'];
  isLoadingResults = true;
  filter = new BookSearchRequest();
  results$ = new BehaviorSubject<Book[]>([]);
  count = 0;

  @ViewChild(MatPaginator) paginator?: MatPaginator;
  @ViewChild(MatSort) sort?: MatSort;

  constructor(
    private bookService: BookService
  ) {
  }

  ngAfterViewInit(): void {
    this.search(true);
    this.sort?.sortChange.subscribe(() => {
      if (this.paginator) {
        this.paginator.pageIndex = 0;
        this.search(false);
      }
    });
  }

  public criteriaUpdated(): void {
    if (this.paginator) {
      this.paginator.pageIndex = 0;
      this.search(true);
    }
  }

  public search(updateCount: boolean) {
    this.isLoadingResults = true;
    this.filter.countTotal = updateCount;
    if (this.paginator) {
      this.filter.offset = this.paginator.pageIndex * this.paginator.pageSize;
      this.filter.limit = this.paginator.pageSize;
    }
    if (this.sort) {
      if (this.sort.direction != '') {
        this.filter.orderBy = this.sort.active;
        this.filter.orderAsc = this.sort.direction == 'asc';
      }
    }
    this.bookService.searchBooks(this.filter).subscribe(response => {
      this.isLoadingResults = false;
      if (response.count !== undefined && response.count !== null)
        this.count = response.count;
      this.results$.next(response.books);
    });
  }

  public resetPaging() {
    // TODO
  }

}
