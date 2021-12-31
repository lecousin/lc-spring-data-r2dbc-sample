import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Author } from '../data/author';
import { Book } from '../data/book';
import { Publisher } from '../data/publisher';

export class BookSearchRequest {

  public offset?: number;
  public limit?: number;
  public countTotal = true;

  public orderBy?: string;
  public orderAsc = true;

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

  public getBook(bookId: number): Observable<Book> {
    return this.http.get<Book>(environment.apiUrl + '/book/v1/book/' + bookId).pipe(
      map(book => new Book(book))
    );
  }

  public searchBooks(request: BookSearchRequest): Observable<BookSearchResponse> {
    return this.http.post<BookSearchResponse>(environment.apiUrl + '/book/v1/search/book', request).pipe(
      map(response => new BookSearchResponse(response.books.map(book => new Book(book)), response.count))
    );
  }

  public searchAuthors(name: string): Observable<Author[]> {
    return this.http.get<Author[]>(environment.apiUrl + '/book/v1/search/author', { params: new HttpParams().append('name', name)});
  }

  public searchPublishers(name: string): Observable<Publisher[]> {
    return this.http.get<Publisher[]>(environment.apiUrl + '/book/v1/search/publisher', { params: new HttpParams().append('name', name)});
  }

  public saveBook(book: Book) : Observable<Book> {
    return this.http.post<Book>(environment.apiUrl + '/book/v1/book', book)
      .pipe(
        map(book => new Book(book))
      );
  }

  public deleteBook(bookId: number): Observable<void> {
    return this.http.delete<void>(environment.apiUrl + '/book/v1/book/' + bookId);
  }

}
