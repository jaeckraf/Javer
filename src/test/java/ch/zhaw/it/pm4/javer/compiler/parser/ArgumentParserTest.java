package ch.zhaw.it.pm4.javer.compiler.parser;

import ch.zhaw.it.pm4.javer.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArgumentParserTest {
    private ArgumentParser parser;
    private DiagnosticBag diagnostics;

    @BeforeEach
    void setUp() {
        diagnostics = new DiagnosticBag();
        parser = new ArgumentParser(diagnostics);
    }

    @Test
    void parse_EmptyArguments_ReturnsEmptyCompilerArguments() {
        String[] args = {};
        CompilerArguments result = parser.parse(args);

        assertFalse(result.hasInputFile(), "Input file should be null or empty.");
        assertNull(result.outputFile(), "Output file should be null.");
        assertFalse(diagnostics.hasErrors(), "Should not report errors for empty arguments.");
    }

    @Test
    void parse_OnlyInputFlagProvided_DefaultsOutputFile() {
        String[] args = {"-i", "main.jav"};
        CompilerArguments result = parser.parse(args);

        assertEquals("main.jav", result.inputFile());
        assertEquals("main.jbc", result.outputFile());
        assertFalse(diagnostics.hasErrors(), "Should not report errors for valid input.");
    }

    @Test
    void parse_InputFileWithoutExtension_DefaultsOutputFile() {
        String[] args = {"-i", "myScript"};
        CompilerArguments result = parser.parse(args);

        assertEquals("myScript", result.inputFile());
        assertEquals("myScript.jbc", result.outputFile());
        assertFalse(diagnostics.hasErrors(), "Should not report errors for valid input.");
    }

    @Test
    void parse_InputAndOutputFlagsProvided_SetsBothFiles() {
        String[] args = {"-i", "main.jav", "-o", "output.jbc"};
        CompilerArguments result = parser.parse(args);

        assertEquals("main.jav", result.inputFile());
        assertEquals("output.jbc", result.outputFile());
        assertFalse(diagnostics.hasErrors(), "Should not report errors for valid input.");
    }

    @Test
    void parse_OutputBeforeInput_SetsBothFilesCorrectly() {
        String[] args = {"-o", "out.jbc", "-i", "code.jav"};
        CompilerArguments result = parser.parse(args);

        assertEquals("code.jav", result.inputFile());
        assertEquals("out.jbc", result.outputFile());
        assertFalse(diagnostics.hasErrors(), "Should not report errors for valid input.");
    }

    @Test
    void parse_MissingValueForInputFlag_ReportsError() {
        String[] args = {"-i"};
        parser.parse(args);

        assertTrue(diagnostics.hasErrors(), "Should report an error for missing value.");
        assertTrue(diagnostics.getErrors().contains("Missing value for option: '-i'"));
    }

    @Test
    void parse_OnlyOutputFlagProvided_ReportsError() {
        String[] args = {"-o", "output.jbc"};
        parser.parse(args);

        assertTrue(diagnostics.hasErrors(), "Should report an error for missing input file.");
        assertTrue(diagnostics.getErrors().contains("An input file (-i or --input) is required."));
    }

    @Test
    void parse_UnknownFlag_ReportsError() {
        String[] args = {"-v", "main.jav"};
        parser.parse(args);

        assertTrue(diagnostics.hasErrors(), "Should report an error for unknown flag.");
        assertTrue(diagnostics.getErrors().contains("Unknown compiler option: '-v'"));
    }

    @Test
    void parse_PositionalArgumentWithoutFlag_ReportsError() {
        String[] args = {"main.jav"};
        parser.parse(args);

        assertTrue(diagnostics.hasErrors(), "Should report an error for positional argument.");
        // Use anyMatch since the error message contains dynamic strings
        assertTrue(diagnostics.getErrors().stream().anyMatch(e -> e.contains("Unexpected argument")));
    }

    @Test
    @Disabled("To be implemented when we support specific compiler flags like --version or --help")
    void parse_HelpFlag_SetsHelpConfiguration() {
        fail("Implement when flags are supported.");
    }
}