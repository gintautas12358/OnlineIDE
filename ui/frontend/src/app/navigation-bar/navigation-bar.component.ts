import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {Observable} from 'rxjs';
import {map, shareReplay} from 'rxjs/operators';
import {File} from '../types/File';
import {FileService} from '../service/file.service';
import {ActivatedRoute} from '@angular/router';
import {EditNameDialogComponent} from '../dialog/edit-name-dialog/edit-name-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {NewFileDialogComponent} from "../dialog/new-file-dialog/new-file-dialog.component";

@Component({
  selector: 'app-navigation-bar',
  templateUrl: './navigation-bar.component.html',
  styleUrls: ['./navigation-bar.component.css'],
})
export class NavigationBarComponent implements OnInit {

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  projectId: string;

  @Output() fileSelect = new EventEmitter();
  @Output() fileDelete = new EventEmitter();
  @Output() fileAdd = new EventEmitter();

  @Input() files: File[];
  selectedFileIndex: number;

  fileSelected(f: File) {
    this.selectedFileIndex = this.files.indexOf(f);
    this.fileSelect.emit({file: f, index: this.selectedFileIndex});
  }

  fileDeleted() {
    let index = this.files.length - 2
    this.fileDelete.emit({oldIndex: this.selectedFileIndex, newIndex: index});
    this.selectedFileIndex = index;
  }

  constructor(private breakpointObserver: BreakpointObserver,
              private route: ActivatedRoute,
              private fileService: FileService, public dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.projectId = this.route.snapshot.paramMap.get('projectId');
    this.fileService.fetchFilesByProjectId(this.projectId);
  }

  fileAdded(fileName: string): void {
    this.selectedFileIndex = this.files.length;
    this.fileAdd.emit({index: this.selectedFileIndex, name: fileName});
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(EditNameDialogComponent, {
      width: '250px',
      data: {fileName: this.files[this.selectedFileIndex].name}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        let currentFile = this.files[this.selectedFileIndex];
        currentFile.name = result;
        this.fileService.editFile(currentFile);
      }
    });
  }

  openNewFileDialog(): void {
    const dialogRef = this.dialog.open(NewFileDialogComponent, {
      width: '250px',
      data: {fileName: '', files: this.files}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.fileAdded(result);
      }
    });
  }
}


