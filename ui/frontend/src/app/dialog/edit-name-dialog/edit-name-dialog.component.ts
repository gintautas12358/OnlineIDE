import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ValidationService} from "../../service/validation.service";

@Component({
  selector: 'app-edit-name-dialog',
  templateUrl: './edit-name-dialog.component.html',
  styleUrls: ['./edit-name-dialog.component.css']
})
export class EditNameDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<EditNameDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data, public validationService: ValidationService) {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
