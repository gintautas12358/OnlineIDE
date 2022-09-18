package edu.tum.ase.compiler.model;

import java.util.ArrayList;
import java.util.Arrays;

public class C_Code extends SourceCode {
    public C_Code(SourceCode sourceCode) {
        super(
                sourceCode.getCode(),
                sourceCode.getFileName()
        );
        int lastIndexOf = sourceCode.getFileName().lastIndexOf(".");
        ArrayList<String> commands = new ArrayList<>();
        commands.add("gcc");
        commands.add("-o");
        commands.add(sourceCode.getFileName().substring(0, lastIndexOf));
        this.setCompileCmd(commands);
    }
}
