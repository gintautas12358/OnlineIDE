package edu.tum.ase.compiler.model;

import java.util.ArrayList;

public class Java_Code extends SourceCode {
    public Java_Code(SourceCode sourceCode) {
        super(
                sourceCode.getCode(),
                sourceCode.getFileName()
        );
        ArrayList<String> commands = new ArrayList<>();
        commands.add("javac");
        this.setCompileCmd(commands);
    }
}
