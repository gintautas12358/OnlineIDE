import {Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import {FileService} from '../service/file.service';
import {File} from '../types/File';
import {CompileService} from '../service/compile.service';
import {SourceCode} from '../types/SourceCode';
import {interval, Subscription} from 'rxjs';
import {startWith, switchMap} from 'rxjs/operators';
import {PollingService} from '../service/polling.service';

@Component({
  selector: 'app-editor',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.css'],
})

export class EditorComponent implements OnInit, OnChanges, OnDestroy {

  @Input() files: File[];
  @Input() selectedFileIndex: number;
  private timeInterval: Subscription;

  constructor(private fileService: FileService, private compileService: CompileService, private pollingService: PollingService) {
  }

  themes = ['vs-light', 'vs-dark'];
  editorOptions = {theme: this.themes[0], language: 'text/plain'};
  codeOutput: Array<string>;
  oldFileCodes: Array<string>;
  isSavePressed: Array<boolean>;

  // Credits for Polling code: https://medium.com/weekly-webtips/polling-in-angular-how-to-use-rxjs-in-angular-for-polling-14c519f4c218

  ngOnInit(): void {
    this.initSavePressed();
    this.initOldFileCodes();
    this.setLanguage();
    this.codeOutput = this.initOutput();
    this.compileService.compiledSourceCodeObservable.subscribe(compiled => {
      this.codeOutput[this.selectedFileIndex] = compiled.stderr + compiled.stdout;
    });
    this.timeInterval = interval(3000).pipe(
      startWith(0),
      switchMap(() => this.pollingService.getDarkmodeActivated())
    ).subscribe(result => {
      if (result === true && this.editorOptions.theme === this.themes[0]) {
        this.editorOptions = {...this.editorOptions, theme: this.themes[1]};
      } else if (result === false && this.editorOptions.theme === this.themes[1]) {
        this.editorOptions = {...this.editorOptions, theme: this.themes[0]};
      }
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    for (const propName in changes) {
      if (changes.hasOwnProperty(propName)) {
        switch (propName) {
          case 'files':
          case 'selectedFileIndex':
            this.setLanguage();
            break;
        }
      }
    }
  }

  ngOnDestroy(): void {
    this.timeInterval.unsubscribe();
  }

  initOldFileCodes(): void {
    const codes = new Array<string>();
    for (const f of this.files) {
      codes.push(f.code);
    }
    this.oldFileCodes = codes;
  }

  initSavePressed(): void {
    const a = new Array<boolean>(3);
    for (let e of a) {
      e = false;
    }
    this.isSavePressed = a;
  }

  initOutput(): Array<string> {
    const codeOutput: Array<string> = [];
    for (const f of this.files) {
      codeOutput.push('');
    }
    return codeOutput;
  }

  setLanguage(): void {
    if (this.files[this.selectedFileIndex] === undefined) {
      return;
    }
    const file = this.files[this.selectedFileIndex];
    //The line below throws an "undefined" error but there is a filename if you log it
    const fileName = file.name;
    const language = fileName.split('.').pop();
    switch (language) {
      case "java":
      case "c":
        this.editorOptions = {...this.editorOptions, language: language};
        break;
      default:
        this.editorOptions = {...this.editorOptions, language: 'text/plain'};
        break;
    }
  }

  compileCode(): void {
    if (!this.files[this.selectedFileIndex].name.endsWith('.java') &&
      !this.files[this.selectedFileIndex].name.endsWith('.c')) {
      this.codeOutput[this.selectedFileIndex] = 'Compiler only accepts .java or .c files';
      return;
    }

    const sourceCode: SourceCode = {
      fileName: this.files[this.selectedFileIndex].name,
      code: this.files[this.selectedFileIndex].code,
      stderr: '',
      stdout: '',
      compilable: false
    };
    this.compileService.compile(sourceCode);
  }

  saveCode() {
    this.fileService.saveCodeOnSelectedFile({
      code: this.files[this.selectedFileIndex].code,
      index: this.selectedFileIndex
    });
    this.isSavePressed[this.selectedFileIndex] = true;
    this.oldFileCodes[this.selectedFileIndex] = this.files[this.selectedFileIndex].code;
  }
}
