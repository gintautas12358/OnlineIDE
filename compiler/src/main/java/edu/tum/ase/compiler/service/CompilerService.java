package edu.tum.ase.compiler.service;

import edu.tum.ase.compiler.model.C_Code;
import edu.tum.ase.compiler.model.Java_Code;
import edu.tum.ase.compiler.model.SourceCode;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

@Service
public class CompilerService {

    private final String JAVA_FILE = ".java";
    private final String C_FILE = ".c";

    public SourceCode compile(SourceCode sourceCode) throws IOException, InterruptedException {
            // First, create a file containing the source code
            File file = createFile(sourceCode);

            // Determine the language of provided sourcecode according to the file extension
            sourceCode = determineCodeLanguage(sourceCode);

            // Next, compile the created file
            Runtime rt = Runtime.getRuntime();
            ArrayList<String> commandsList = sourceCode.getCompileCmd();
            commandsList.add(file.getPath());
            String[] commands = commandsList.toArray(new String[0]);
            Process proc = rt.exec(commands);

            // Wait for proc to end, returnCode = 0, if successful and 1, if not successful
            int returnCode = proc.waitFor();
            if(returnCode == 0) {
                sourceCode.setCompilable(true);
            }

            // Read stdInput (Output of proc) and stdError
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            sourceCode.setStdout(captureStream(stdInput));
            sourceCode.setStderr(captureStream(stdError));
            stdInput.close();
            stdError.close();

            // Delete created file
            Files.deleteIfExists(file.toPath());

        return sourceCode;
    }

    private File createFile(SourceCode sourceCode) {
        File classFile = new File(sourceCode.getFileName());
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(classFile));
            bw.write(sourceCode.getCode());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classFile;
    }

    private SourceCode determineCodeLanguage(SourceCode sourceCode) throws IOException {
        int lastIndexOf = sourceCode.getFileName().lastIndexOf(".");
        if (lastIndexOf == -1 ){
            throw new IOException("File ending missing.");
        }
        String fileExtension = sourceCode.getFileName().substring(lastIndexOf);
        switch (fileExtension) {
            case JAVA_FILE:
                if (sourceCode.getFileName().length() <= JAVA_FILE.length()) {
                    throw new IOException("File name is missing before the file ending.");
                }
                return new Java_Code(sourceCode);
            case C_FILE:
                if (sourceCode.getFileName().length() <= C_FILE.length()) {
                    throw new IOException("File name is missing before the file ending.");
                }
                return new C_Code(sourceCode);
            default:
                throw new IOException("Compiler only accepts .java or .c files");
        }
    }

    private String captureStream(BufferedReader br) {
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
