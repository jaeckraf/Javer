package ch.zhaw.it.pm4.javer.compiler;

import java.io.IOException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CompilerTest {

    @Test
    void compileReturnsSuccessForCurrentImplementedPipeline() throws IOException {
        Path input = Files.createTempFile("javer-compiler-test", ".jv");
        Files.writeString(input, "");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        try {
            Compiler compiler = new Compiler(CompilerOptions.create(input.toString(), "out.bin"));
            compiler.compile();
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(output.toString().contains("Compilation Successful"));

        Files.deleteIfExists(input);
    }
}
