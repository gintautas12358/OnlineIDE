export interface SourceCode {
  code: string;
  fileName: string;
  stdout: string;
  stderr: string;
  compilable: boolean;
}
