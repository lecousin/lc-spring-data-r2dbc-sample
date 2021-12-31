import { Author } from "./author";
import { Publisher } from "./publisher";

export class Book {

  id = 0;
  title = '';
  year?: number;
  authors?: Author[];
  publisher?: Publisher;

  constructor(fields?: Partial<Book>) {
    if (fields) {
      Object.assign(this, fields);
      if (this.authors) {
        this.authors = this.authors.map(a => new Author(a));
      }
      if (this.publisher) {
        this.publisher = new Publisher(this.publisher);
      }
    }
  }

}
