import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Project} from '../../types/Project';
import {FileService} from '../../service/file.service';
import {File} from '../../types/File';
import {ProjectService} from '../../service/project.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.css']
})
export class ProjectDetailsComponent implements OnInit {
  project: Project;
  files: File[];
  selectedFileIndex: number;

  constructor(private route: ActivatedRoute, private fileService: FileService, private projectService: ProjectService) {
  }

  ngOnInit(): void {
    const projectIdFromRoute = this.route.snapshot.paramMap.get('projectId');
    this.projectService.projectsObservable.subscribe(result => {
      this.project = result.find(pr => pr.id === projectIdFromRoute);
    });
    this.fileService.filesObservable.subscribe(
      result => {
        this.files = result;
      }
    );
    this.selectedFileIndex = 0;
  }

  fileSelected(result: {file: File, index: number}) {
    this.selectedFileIndex = result.index;
  }

  addFile(data: {index: number, name: string}): void {
    this.fileService.createFile({name: data.name, projectId: this.project.id});
    this.selectedFileIndex = data.index;
  }

  deleteFile(result): void {
    this.fileService.deleteFile(this.files[result.oldIndex].id);
    this.selectedFileIndex = result.newIndex;
  }
}
