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
    void parse_OnlyInputFlagProvided_DefaultsOutputFile() {
        String[] args = {"-i", "main.jav"};
        CompilerArguments result = parser.parse(args);

        assertEquals("main.jav", result.inputFile());
        assertEquals("main.jbc", result.outputFile());
    }

    @Test
    void parse_InputFileWithoutExtension_DefaultsOutputFile() {
        String[] args = {"-i", "myScript"};
        CompilerArguments result = parser.parse(args);

        assertEquals("myScript", result.inputFile());
        assertEquals("myScript.jbc", result.outputFile());
    }

    @Test
    void parse_InputAndOutputFlagsProvided_SetsBothFiles() {
        String[] args = {"-i", "main.jav", "-o", "output.jbc"};
        CompilerArguments result = parser.parse(args);

        assertEquals("main.jav", result.inputFile());
        assertEquals("output.jbc", result.outputFile());
    }

    @Test
    void parse_OutputBeforeInput_SetsBothFilesCorrectly() {
        String[] args = {"-o", "out.jbc", "-i", "code.jav"};
        CompilerArguments result = parser.parse(args);

        assertEquals("code.jav", result.inputFile());
        assertEquals("out.jbc", result.outputFile());
    }

    @Test
    void parse_MissingValueForInputFlag_ThrowsIllegalArgumentException() {
        String[] args = {"-i"};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> parser.parse(args));

        assertEquals("Missing value for option: '-i'", exception.getMessage());
    }

    @Test
    void parse_OnlyOutputFlagProvided_ThrowsIllegalArgumentException() {
        String[] args = {"-o", "output.jbc"};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> parser.parse(args));

        assertEquals("An input file (-i or --input) is required.", exception.getMessage());
    }

    @Test
    void parse_UnknownFlag_ThrowsIllegalArgumentException() {
        String[] args = {"-v", "main.jav"};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
        assertTrue(exception.getMessage().contains("Unknown compiler option"));
    }

    @Test
    void parse_PositionalArgumentWithoutFlag_ThrowsIllegalArgumentException() {
        String[] args = {"main.jav"};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> parser.parse(args));

        assertTrue(exception.getMessage().contains("Unexpected argument"));
    }

    @Test
    @Disabled("To be implemented when we support specific compiler flags like --version or --help")
    void parse_HelpFlag_SetsHelpConfiguration() {
        fail("Implement when flags are supported.");
    }
}
