import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { AbstractControl, FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { debounceTime, switchMap, tap } from 'rxjs/operators';
import { Author } from 'src/app/data/author';
import { Book } from 'src/app/data/book';
import { Publisher } from 'src/app/data/publisher';
import { AuthService } from 'src/app/service/auth.service';
import { BookService } from 'src/app/service/book.service';

@Component({
  selector: 'app-book-form',
  templateUrl: './book-form.component.html',
  styleUrls: ['./book-form.component.scss']
})
export class BookFormComponent implements OnInit, OnChanges {

  @Input() book?: Book;

  isAdmin: boolean;
  isNew = false;
  isEditing = false;
  form = new FormGroup({});
  loadingMessage?: string;
  errorMessage?: string;

  constructor(
    auth: AuthService,
    private bookService: BookService,
    private router: Router
  ) {
    this.isAdmin = auth.getSession$().value?.admin || false;
  }

  ngOnInit(): void {
    this.update();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.update();
  }

  private update(): void {
    if (this.book) {
      this.isNew = !(this.book.id > 0);
      if (this.isNew) {
        this.edit();
      } else {
        this.cancelEdit();
      }
    }
  }

  public edit(): void {
    this.form = new FormGroup({
      title: new FormControl(this.book?.title, [Validators.required, Validators.maxLength(100)]),
      year: new FormControl(this.book?.year),
      authors: new FormArray(this.createAuthorsFormArray()),
      publisher: this.createPublisherFormGroup(this.book?.publisher)
    });
    this.isEditing = true;
  }

  public cancelEdit(): void {
    this.form = new FormGroup({});
    this.isEditing = false;
  }

  private createAuthorsFormArray(): FormGroup[] {
    let array: FormGroup[] = [];
    if (this.book && this.book.authors) {
      for (const author of this.book.authors) {
        array.push(this.createAuthorFormGroup(author));
      }
    }
    return array;
  }

  private createAuthorFormGroup(author?: Author): FormGroup {
    let group = new FormGroup({});
    let name = new FormControl(author?.name, [Validators.maxLength(100)]);
    group.addControl('name', name);
    let id = new FormControl(author?.id);
    group.addControl('id', id);
    let options = new FormControl(author ? [author]: []);
    group.addControl('options', options);

    const updateId = (value: string) => {
      id.setValue(undefined);
      const authors = options.value;
      if (authors) {
        for (const a of authors) {
          if (a.name === value) {
            id.setValue(a.id);
            break;
          }
        }
      }
    };

    name.valueChanges
      .pipe(
        tap(updateId),
        debounceTime(500),
        tap(() => {
          options.setValue([]);
        }),
        switchMap(value => this.bookService.searchAuthors(value)
          .pipe(
            tap(authors => {
              options.setValue(authors);
              updateId(value);
            })
          )
        )
      )
      .subscribe();

    return group;
  }

  public getAuthorsFormArray(): FormArray {
    return <FormArray>this.form.get('authors');
  }

  public getAuthorOptions(authorForm: AbstractControl): Author[] {
    return <Author[]>authorForm.get('options')?.value;
  }

  public newAuthor(): void {
    this.getAuthorsFormArray().controls.push(this.createAuthorFormGroup());
  }

  public removeAuthor(index: number) {
    this.getAuthorsFormArray().controls.splice(index, 1);
  }

  private createPublisherFormGroup(publisher?: Publisher): FormGroup {
    let group = new FormGroup({});
    let name = new FormControl(publisher?.name, [Validators.maxLength(100)]);
    group.addControl('name', name);
    let id = new FormControl(publisher?.id);
    group.addControl('id', id);
    let options = new FormControl(publisher ? [publisher]: []);
    group.addControl('options', options);

    const updateId = (value: string) => {
      id.setValue(undefined);
      const publishers = options.value;
      if (publishers) {
        for (const p of publishers) {
          if (p.name === value) {
            id.setValue(p.id);
            break;
          }
        }
      }
    };

    name.valueChanges
      .pipe(
        tap(updateId),
        debounceTime(500),
        tap(() => {
          options.setValue([]);
        }),
        switchMap(value => this.bookService.searchPublishers(value)
          .pipe(
            tap(publishers => {
              options.setValue(publishers);
              updateId(value);
            })
          )
        )
      )
      .subscribe();

    return group;
  }

  public getPublisherFormGroup(): FormGroup {
    return <FormGroup>this.form.get('publisher');
  }

  public getPublisherOptions(): Publisher[] {
    return <Publisher[]>this.getPublisherFormGroup().get('options')?.value;
  }

  public saveBook(): void {
    this.errorMessage = undefined;
    this.loadingMessage = 'Saving book';
    const book = new Book();
    book.id = this.book?.id || 0;
    book.title = this.form.get('title')?.value;
    book.year = this.form.get('year')?.value;
    book.authors = [];
    for (const group of this.getAuthorsFormArray().controls) {
      const author = new Author();
      author.name = group.get('name')?.value;
      if (!author.name || author.name.length == 0) {
        continue;
      }
      author.id = group.get('id')?.value;
      book.authors.push(author);
    }
    const group = this.getPublisherFormGroup();
    let publisher: Publisher | undefined = new Publisher();
    publisher.name = group.get('name')?.value;
    if (!publisher.name || publisher.name.length == 0) {
      publisher = undefined;
    } else {
      publisher.id = group.get('id')?.value;
    }
    book.publisher = publisher;
    this.bookService.saveBook(book).subscribe(book => {
      this.loadingMessage = undefined;
      this.book = book;
      this.cancelEdit();
    }, error => {
      this.loadingMessage = undefined;
      this.errorMessage = error;
    });
  }

  public deleteBook(): void {
    if (this.book && this.book.id > 0 && confirm('Delete this book ?')) {
      this.errorMessage = undefined;
      this.loadingMessage = 'Deleting book';
      this.bookService.deleteBook(this.book?.id).subscribe(ok => {
        this.router.navigateByUrl('/book');
      }, error => {
        this.loadingMessage = undefined;
        this.errorMessage = error;
      });
    }
  }
}
