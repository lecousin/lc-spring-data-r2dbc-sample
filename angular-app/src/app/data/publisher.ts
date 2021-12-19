export class Publisher {

  name = '';

  constructor(fields?: Partial<Publisher>) {
    if (fields) {
      Object.assign(this, fields);
    }
  }

}
