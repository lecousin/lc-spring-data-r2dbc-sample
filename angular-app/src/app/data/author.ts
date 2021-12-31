export class Author {

  id = 0;
  name = '';

  constructor(fields?: Partial<Author>) {
    if (fields) {
      Object.assign(this, fields);
    }
  }

}
