package ch.zhaw.it.pm4.javer.compiler;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerTest {

    @Test
    void compileReturnsSuccessForCurrentImplementedPipeline() throws IOException {
        Path input = Files.createTempFile("javer-compiler-test", ".jv");
        Files.writeString(input, "");

        Compiler compiler = new Compiler(CompilerOptions.create(input.toString(), "out.bin"));

        assertEquals("Compilation Successful", compiler.compile());

        Files.deleteIfExists(input);
    }
}

