package ch.zhaw.it.pm4.javer.compiler.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArgumentParserTest {
    private ArgumentParser parser;

    @BeforeEach
    void setUp() {
        parser = new ArgumentParser();
    }

    @Test
    void parse_EmptyArguments_ReturnsEmptyCompilerArguments() {
        String[] args = {};
        CompilerArguments result = parser.parse(args);

        assertFalse(result.hasInputFile(), "Input file should be null or empty.");
        assertNull(result.outputFile(), "Output file should be null.");
    }

    @Test
    void parse_OneFileArgument_SetsInputFile() {
        String[] args = {"main.jav"};
        CompilerArguments result = parser.parse(args);

        assertEquals("main.jav", result.inputFile());
        assertNull(result.outputFile());
    }

    @Test
    void parse_TwoFileArguments_SetsInputAndOutputFile() {
        String[] args = {"main.jav", "output.jbc"};
        CompilerArguments result = parser.parse(args);

        assertEquals("main.jav", result.inputFile());
        assertEquals("output.jbc", result.outputFile());
    }

    @Test
    void parse_ArgumentStartingWithDash_ThrowsIllegalArgumentException() {
        String[] args = {"-v", "main.jav"};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(args);
        });

        assertTrue(exception.getMessage().contains("Unknown compiler option"));
    }

    @Test
    void parse_TooManyArguments_ThrowsIllegalArgumentException() {
        String[] args = {"main.jav", "out.jbc", "extra.txt"};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(args);
        });

        assertTrue(exception.getMessage().contains("Too many arguments provided"));
    }

    @Test
    @Disabled("To be implemented when we support specific compiler flags like --version or --help")
    void parse_HelpFlag_SetsHelpConfiguration() {
        fail("Implement when flags are supported.");
    }
}
