package edu.tum.ase.compiler.integration;

import edu.tum.ase.compiler.model.SourceCode;
import edu.tum.ase.compiler.service.CompilerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Integration tests
 */
@RunWith(SpringRunner.class)
public class CompilerServiceTest {

    @Autowired
    private CompilerService systemUnderTest;

    @Test
    public void should_ReturnIOException_When_InvalidJavaFile() {
        // given
        SourceCode invalidJavaFile = new SourceCode("", "file..java.");

        // when
        try {
            SourceCode res = systemUnderTest.compile(invalidJavaFile);
            fail("Exception expected.");
        } catch (Exception e) {
            // then
            assertThat(e, instanceOf(IOException.class));
        }
    }

    @Test
    public void should_ReturnIOException_When_InvalidCFile() {
        SourceCode invalidCFile = new SourceCode("", "file.c.");
        try {
            SourceCode res = systemUnderTest.compile(invalidCFile);
            fail("Exception expected.");
        } catch (Exception e) {
            assertThat(e, instanceOf(IOException.class));
        }
    }

    @Test
    public void should_ReturnIOException_When_InvalidFileName() {
        SourceCode invalidFileName = new SourceCode("", "java");
        try {
            SourceCode res = systemUnderTest.compile(invalidFileName);
            fail("Exception expected.");
        } catch (Exception e) {
            assertThat(e, instanceOf(IOException.class));
        }
    }

    @Test
    public void should_ReturnIOException_When_InvalidMissingFileNameWithEnding() {
        SourceCode invalidFileName = new SourceCode("", ".java");
        try {
            SourceCode res = systemUnderTest.compile(invalidFileName);
            fail("Exception expected.");
        } catch (Exception e) {
            assertThat(e, instanceOf(IOException.class));
        }
    }

    @Test
    public void should_ReturnIOException_When_InvalidEllipsesFileEnding() {
        SourceCode invalidEllipsesFileEnding = new SourceCode("", "...");
        try {
            SourceCode res = systemUnderTest.compile(invalidEllipsesFileEnding);
            fail("Exception expected.");
        } catch (Exception e) {
            assertThat(e, instanceOf(IOException.class));
        }
    }

    @Test
    public void should_ReturnIOException_When_InvalidPythonFileName() {
        SourceCode invalidPythonFileName = new SourceCode("", "test.py");
        try {
            SourceCode res = systemUnderTest.compile(invalidPythonFileName);
            fail("Exception expected.");
        } catch (Exception e) {
            assertThat(e, instanceOf(IOException.class));
        }
    }

    @Test
    public void should_ReturnStdErr_When_InvalidCode() {
        //given
        SourceCode invalidCode = new SourceCode("console.log(\"Hello World\")", "file.java");

        //when
        try {
            SourceCode result = systemUnderTest.compile(invalidCode);
            assert (!result.getStderr().isEmpty());
        } catch (Exception e) {
            fail();
        }
    }

    @TestConfiguration
    static class CompilerControllerTestsConfiguration {

        @Bean
        public CompilerService systemUnderTest() {
            return new CompilerService();
        }
    }
}
