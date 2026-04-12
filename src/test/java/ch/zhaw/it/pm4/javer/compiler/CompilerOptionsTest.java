package ch.zhaw.it.pm4.javer.compiler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompilerOptionsTest {

    @Test
    void createBuildsDefaultCompilerOptions() {
        CompilerOptions options = CompilerOptions.create("in.jv", "out.bin");

        assertEquals("in.jv", options.getInputFilePath());
        assertEquals("out.bin", options.getOutputFilePath());
        assertTrue(options.isLoggingEnabled());
        assertFalse(options.isDumpAst());
        assertEquals(0, options.getOptimizationLevel());
    }

    @Test
    void createFailsIfArgumentsAreMissing() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> CompilerOptions.create("only-input"));
    }
}

