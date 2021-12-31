export class Publisher {

  id = 0;
  name = '';

  constructor(fields?: Partial<Publisher>) {
    if (fields) {
      Object.assign(this, fields);
    }
  }

}
