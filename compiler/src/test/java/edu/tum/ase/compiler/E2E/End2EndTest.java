package edu.tum.ase.compiler.E2E;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tum.ase.compiler.model.Java_Code;
import edu.tum.ase.compiler.model.SourceCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class End2EndTest {

    private final String URL = "/compile";

    @Autowired
    private MockMvc systemUnderTest;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void should_ReturnSuccessfulCompilation_When_PostWithCompileJavaCode() throws Exception {
        // given
        Java_Code java_code_toBeTested = new Java_Code(new SourceCode(
                "class validFile_E2E {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\"); \n" +
                "    }\n" +
                "}", "validFile_E2E.java"));

        Java_Code java_code_expectedOutput = new Java_Code(new SourceCode("class validFile_E2E {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\"); \n" +
                "    }\n" +
                "}", "validFile_E2E.java"));
        java_code_expectedOutput.setCompilable(true);
        java_code_expectedOutput.setStderr("");
        java_code_expectedOutput.setStdout("");

        // when
        ResultActions result = systemUnderTest.perform(post(URL)
                .content(objectMapper.writeValueAsString(java_code_toBeTested))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andExpect(status().isOk())
                .andExpect((MockMvcResultMatchers.jsonPath("$.compilable").value(java_code_expectedOutput.isCompilable())))
                .andExpect((MockMvcResultMatchers.jsonPath("$.stderr").value(java_code_expectedOutput.getStderr())))
                .andExpect((MockMvcResultMatchers.jsonPath("$.stdout").value(java_code_expectedOutput.getStdout())))
                .andExpect((MockMvcResultMatchers.jsonPath("$.fileName").value(java_code_expectedOutput.getFileName())));

    }

}

