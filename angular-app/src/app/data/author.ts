export class Author {

  name = '';

  constructor(fields?: Partial<Author>) {
    if (fields) {
      Object.assign(this, fields);
    }
  }

}
