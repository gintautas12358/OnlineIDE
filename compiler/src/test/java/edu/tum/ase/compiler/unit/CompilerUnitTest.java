package edu.tum.ase.compiler.unit;

import edu.tum.ase.compiler.controller.CompilerController;
import edu.tum.ase.compiler.model.Java_Code;
import edu.tum.ase.compiler.model.SourceCode;
import edu.tum.ase.compiler.service.CompilerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
public class CompilerUnitTest {

    @Autowired
    private CompilerController systemUnderTest;

    @MockBean
    private CompilerService compilerService;

    @Test
    public void should_ReturnSuccessfulCompilationOfJavaFile() throws IOException, InterruptedException {
        // given
        Java_Code java_code_toBeTested = new Java_Code(new SourceCode(
                "class validFile_E2E {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\"); \n" +
                "    }\n" +
                "}",
                "validFile_UnitTest.java"));

        Java_Code java_code_expectedOutput = new Java_Code(new SourceCode(
                "class validFile_E2E {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\"); \n" +
                "    }\n" +
                "}",
                "validFile_UnitTest.java"));
        java_code_expectedOutput.setCompilable(true);
        java_code_expectedOutput.setStderr("");
        java_code_expectedOutput.setStdout("");

        given(compilerService.compile(java_code_toBeTested)).willReturn(java_code_expectedOutput);

        // when
        SourceCode result = systemUnderTest.compile(java_code_toBeTested);

        // then
        then(result.isCompilable()).isEqualTo(java_code_expectedOutput.isCompilable());
        then(result.getStderr()).isEqualTo(java_code_expectedOutput.getStderr());
        then(result.getStdout()).isEqualTo(java_code_expectedOutput.getStdout());
    }

    @TestConfiguration
    static class CompilerControllerTestsConfiguration {

        @Bean
        public CompilerController systemUnderTest() {
            return new CompilerController();
        }
    }
}

