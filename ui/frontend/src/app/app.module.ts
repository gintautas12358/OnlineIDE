import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {ProjectManagementComponent} from './Pages/project-management/project-management.component';
import {EditorComponent} from './editor/editor.component';
import {Route, RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ProjectDetailsComponent} from './Pages/project-details/project-details.component';
import {WelcomePageComponent} from './Pages/welcome-page/welcome-page.component';
import {MonacoEditorModule} from 'ngx-monaco-editor';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NavigationBarComponent} from './navigation-bar/navigation-bar.component';
import {LayoutModule} from '@angular/cdk/layout';
import {MatInputModule} from '@angular/material/input';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatIconModule} from '@angular/material/icon';
import {MatListModule} from '@angular/material/list';
import {MatTableModule} from '@angular/material/table';
import {MatSortModule} from '@angular/material/sort';
import {MatDialogModule} from '@angular/material/dialog';
import {DialogComponent} from './dialog/dialog.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {HttpClientModule} from '@angular/common/http';
import {MatSelectModule} from '@angular/material/select';
import {EditNameDialogComponent} from './dialog/edit-name-dialog/edit-name-dialog.component';
import {AngularSplitModule} from 'angular-split';
import { NewFileDialogComponent } from './dialog/new-file-dialog/new-file-dialog.component';
import {AuthGuard} from './auth.guard';

const routes: Route[] = [
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'home', component: WelcomePageComponent},
  {path: 'manage-projects', component: ProjectManagementComponent, canActivate: [AuthGuard]},
  {path: 'ide/:projectId', component: ProjectDetailsComponent, canActivate: [AuthGuard]},
  {path: '**', redirectTo: "manage-projects"}
];

@NgModule({
  declarations: [
    AppComponent,
    ProjectManagementComponent,
    EditorComponent,
    ProjectDetailsComponent,
    WelcomePageComponent,
    NavigationBarComponent,
    DialogComponent,
    EditNameDialogComponent,
    NewFileDialogComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    FormsModule,
    MonacoEditorModule.forRoot(),
    BrowserAnimationsModule,
    HttpClientModule,
    LayoutModule,
    MatInputModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    MatTableModule,
    MatSortModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule,
    AngularSplitModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
