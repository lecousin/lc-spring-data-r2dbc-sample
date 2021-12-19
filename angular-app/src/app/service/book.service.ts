import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Book } from '../data/book';

export class BookSearchRequest {

  public bookTitle?: string;
  public authorName?: string;
  public publisherName?: string;
  public yearFrom?: number;
  public yearTo?: number;

}

@Injectable({
  providedIn: 'root'
})
export class BookService {

  constructor(
    private http: HttpClient
  ) { }

  public searchBooks(request: BookSearchRequest): Observable<Book[]> {
    return this.http.post<Book[]>(environment.apiUrl + '/book/v1/search', request).pipe(
      map(books => books.map(book => new Book(book)))
    );
  }

}
