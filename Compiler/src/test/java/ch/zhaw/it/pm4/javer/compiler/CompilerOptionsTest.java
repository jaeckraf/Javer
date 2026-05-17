package ch.zhaw.it.pm4.javer.compiler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompilerOptionsTest {

    @Test
    void acceptsJaverInputAndAppendsBytecodeExtension() {
        CompilerOptions options = CompilerOptions.create(
                "--in-file", "src/main.javer",
                "--out-file", "target/main",
                "--logging"
        );

        assertEquals("src/main.javer", options.getInputFilePath());
        assertEquals("target/main.jbc", options.getOutputFilePath());
        assertTrue(options.isLoggingEnabled());
    }

    @Test
    void loggingIsDisabledByDefault() {
        CompilerOptions options = CompilerOptions.create(
                "--in-file", "src/main.javer",
                "--out-file", "target/main"
        );

        assertFalse(options.isLoggingEnabled());
    }

    @Test
    void rejectsNonJaverInputExtension() {
        assertThrows(IllegalArgumentException.class, () -> CompilerOptions.create(
                "--in-file", "src/main.jv",
                "--out-file", "target/main"
        ));
    }

    @Test
    void rejectsOutputPathWithBytecodeExtension() {
        assertThrows(IllegalArgumentException.class, () -> CompilerOptions.create(
                "--in-file", "src/main.javer",
                "--out-file", "target/main.jbc"
        ));
    }

    @Test
    void rejectsOutputPathWithAnyFileExtension() {
        assertThrows(IllegalArgumentException.class, () -> CompilerOptions.create(
                "--in-file", "src/main.javer",
                "--out-file", "target/main.txt"
        ));
    }
}
