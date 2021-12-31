import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BookPageComponent } from './pages/book-page/book-page.component';
import { BookSearchPageComponent } from './pages/book-search-page/book-search-page.component';
import { LoginPageComponent } from './pages/login-page/login-page.component';
import { MainPageComponent } from './pages/main-page/main-page.component';
import { AuthGuard } from './service/auth.guard';

const routes: Routes = [
  { path: 'login', component: LoginPageComponent },
  { path: '', component: MainPageComponent, canActivate: [AuthGuard], canActivateChild: [AuthGuard], children: [
    { path: 'book', component: BookSearchPageComponent },
    { path: 'book/:bookId', component: BookPageComponent },
    { path: '**', redirectTo: 'book' }
  ]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
