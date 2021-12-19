export class Session {

  public token: string = '';
  public uuid: string = '';
  public username: string = '';
  public expiration: number = 0;

  constructor(fields?: Partial<Session>) {
    if (fields) {
      Object.assign(this, fields);
    }
  }

}
