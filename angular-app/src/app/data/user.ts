export class User {
  public id = 0;
  public username = '';
  public admin = false;

  constructor(fields?: Partial<User>) {
    if (fields) {
      Object.assign(this, fields);
    }
  }
}
