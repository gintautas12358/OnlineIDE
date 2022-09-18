import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-new-file-dialog',
  templateUrl: './new-file-dialog.component.html',
  styleUrls: ['./new-file-dialog.component.css']
})
export class NewFileDialogComponent{
  private localData: any;

  constructor(
    public dialogRef: MatDialogRef<NewFileDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data) {
    this.localData = {...data};
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  isFileType(name: string): boolean {
    let c_type = '.c';
    let java_type = '.java';
    let isC = this.findType(c_type, name);
    let isJava = this.findType(java_type, name);
    return isC || isJava;
  }

  findType(type: string, name: string): boolean {
    let size = name.length;
    return type === name.substring(size - type.length, size);
  }

  isUnique(name: string): boolean {
    const f = (e) => e.name === name;
    if (this.localData.files.find(f)) {
      return false;
    }
    return true;
  }



}
