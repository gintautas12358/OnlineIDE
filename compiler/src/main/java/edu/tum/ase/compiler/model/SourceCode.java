package edu.tum.ase.compiler.model;

import java.util.ArrayList;

public class SourceCode {
    private String code;
    private String fileName;
    private String stdout;
    private String stderr;
    private ArrayList<String> compileCmd = new ArrayList<>();
    private boolean compilable = false;

    public SourceCode() {

    }

    public SourceCode(String code, String fileName) {
        this.code = code;
        this.fileName = fileName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public ArrayList<String> getCompileCmd() {
        return compileCmd;
    }

    public void setCompileCmd(ArrayList<String> compileCmd) {
        this.compileCmd = compileCmd;
    }

    public boolean isCompilable() {
        return compilable;
    }

    public void setCompilable(boolean compilable) {
        this.compilable = compilable;
    }

}

