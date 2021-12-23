import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Book } from '../data/book';

export class BookSearchRequest {

  public offset?: number;
  public limit?: number;
  public countTotal = true;

  public bookTitle?: string;
  public authorName?: string;
  public publisherName?: string;
  public yearFrom?: number;
  public yearTo?: number;

}

export class BookSearchResponse {

  constructor(public books: Book[], public count?: number) {}

}

@Injectable({
  providedIn: 'root'
})
export class BookService {

  constructor(
    private http: HttpClient
  ) { }

  public searchBooks(request: BookSearchRequest): Observable<BookSearchResponse> {
    return this.http.post<BookSearchResponse>(environment.apiUrl + '/book/v1/search', request).pipe(
      map(response => new BookSearchResponse(response.books.map(book => new Book(book)), response.count))
    );
  }

}
