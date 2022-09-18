import {Component, OnInit, ViewChild} from '@angular/core';
import {Project} from '../../types/Project';

import {MatDialog} from '@angular/material/dialog';
import {DialogComponent} from '../../dialog/dialog.component';
import {MatTable, MatTableDataSource} from '@angular/material/table';
import {ProjectService} from '../../service/project.service';
import {AuthService} from "../../auth.service";

@Component({
  selector: 'app-project-management',
  templateUrl: './project-management.component.html',
  styleUrls: ['./project-management.component.css']
})
export class ProjectManagementComponent implements OnInit {
  displayedColumns: string[] = ['id', 'name', 'userIds', 'action'];
  dataSource: MatTableDataSource<Project>;

  @ViewChild('projectTable', {static: true}) table: MatTable<Project>;

  constructor(private dialog: MatDialog, private projectService: ProjectService, public authService: AuthService) {
  }

  ngOnInit() {
    this.projectService.fetchProjects();
    this.projectService.projectsObservable.subscribe(
      ps => {
        this.dataSource = new MatTableDataSource<Project>();
        this.dataSource.data = ps;
      });
  }

  openDialog(action: string, project: Project): void {
    const tmp = {
      id: project.id,
      name: project.name,
      users: project.userIds,
      className: 'Project',
      action: action
    };
    const dialogRef = this.dialog.open(DialogComponent, {
      width: '250px',
      disableClose: true,
      data: tmp
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result.event === 'Add') {
        const project = {
          name: result.data.name
        };
        this.projectService.createProject(project);
      } else if (result.event === 'Update') {
        this.projectService.editProject(<Project>{id: project.id, name: result.data.name, userIds: project.userIds});
      } else if (result.event === 'Delete') {
        this.projectService.deleteProject(result.data.id);
      } else if (result.event === 'Share') {
        this.projectService.shareProject(<Project>{
          id: project.id,
          name: result.data.name,
          userIds: project.userIds
        }, result.data.newUser);
      }
    });
    this.table.renderRows();
  }
}
