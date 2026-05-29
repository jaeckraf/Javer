package ch.zhaw.it.pm4.javer.vm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class VMTest {

    private static final int MAX_STACK_SIZE_BYTES = 16 * 1024 * 1024;

    @TempDir
    Path tempDir;

    @Test
    void executesIntegerArithmeticAndPrintsResult() throws Exception {
        String output = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHI, 3
                PUSHI, 4
                IADD
                PUSHI, 2
                IMUL
                PRINTI
                RET, 0

                .data
                """);

        assertEquals("14", output);
    }

    @Test
    void executesConditionalAndUnconditionalJumps() throws Exception {
        String output = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHI, 0
                JUMPF, else_branch
                PUSHI, 1
                PRINTI
                JUMP, end
                else_branch:
                PUSHI, 2
                PRINTI
                end:
                RET, 0

                .data
                """);

        assertEquals("2", output);
    }

    @Test
    void executesFunctionCallAndReturnsValueToCaller() throws Exception {
        String output = runProgram("""
                .code
                _main:
                ENTER, 0
                CALL, _answer, 0
                PRINTI
                RET, 0
                _answer:
                ENTER, 0
                PUSHI, 42
                RET, 4

                .data
                """);

        assertEquals("42", output);
    }

    @Test
    void storesAndLoadsLocalValuesWithDifferentWidths() throws Exception {
        String output = runProgram("""
                .code
                _main:
                ENTER, 16
                LOCAL, 0
                PUSHI, 65
                STORE1
                LOCAL, 0
                LOAD1
                PRINTI
                LOCAL, 4
                PUSHI, 123456
                STORE4
                LOCAL, 4
                LOAD4
                PRINTI
                LOCAL, 8
                PUSHD, 2.5
                STORE8
                LOCAL, 8
                LOAD8
                PRINTD
                RET, 0

                .data
                """);

        assertEquals("651234562.5", output);
    }

    @Test
    void printsAndComparesDataStrings() throws Exception {
        String output = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHR, greeting
                PRINTS
                PUSHR, greeting
                PUSHR, copy
                STREQ
                PRINTI
                RET, 0

                .data
                greeting 1 02,00,00,00,48,00,69,00
                copy 1 02,00,00,00,48,00,69,00
                """);

        assertEquals("Hi1", output);
    }

    @Test
    void allocatesArrayWritesAndReadsElement() throws Exception {
        String output = runProgram("""
                .code
                _main:
                ENTER, 4
                LOCAL, 0
                PUSHI, 3
                NEWA, 4
                STORE4
                LOCAL, 0
                LOAD4
                PUSHI, 1
                BOUNDS, 4
                PUSHI, 4
                IMUL
                PUSHI, 4
                IADD
                IADD
                PUSHI, 99
                STORE4
                LOCAL, 0
                LOAD4
                PUSHI, 1
                BOUNDS, 4
                PUSHI, 4
                IMUL
                PUSHI, 4
                IADD
                IADD
                LOAD4
                PRINTI
                RET, 0

                .data
                """);

        assertEquals("99", output);
    }

    @Test
    void copiesDataBytesIntoHeapMemory() throws Exception {
        String output = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHI, 4
                NEW
                DUP, 4
                PUSHR, answer
                PUSHI, 4
                MEMCPY
                LOAD4
                PRINTI
                RET, 0

                .data
                answer 4 2A
                """);

        assertEquals("42", output);
    }

    @Test
    void runtimeErrorIncludesInstructionLineNumber() throws Exception {
        RuntimeException exception = assertRuntimeError("""
                .code
                _main:
                ENTER, 0
                PUSHI, 1
                PUSHI, 0
                IDIV
                RET, 0

                .data
                """);

        assertEquals("Line 6: Division by zero", exception.getMessage());
    }

    @Test
    void stackUnderflowIncludesInstructionLineNumber() throws Exception {
        RuntimeException exception = assertRuntimeError("""
                .code
                _main:
                ENTER, 0
                POP, 8
                POP, 8
                POP, 4
                RET, 0

                .data
                """);

        assertEquals("Line 6: Stack underflow while popping 4 byte(s)", exception.getMessage());
    }

    @Test
    void stackOverflowIncludesInstructionLineNumber() throws Exception {
        Path bytecodeFile = writeProgram("stack-overflow.jbc", """
                .code
                _main:
                ENTER, 0
                PUSHI, 1
                PUSHI, 2
                RET, 0

                .data
                """);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> new VM(bytecodeFile.toString(), 20).run());

        assertEquals("Line 5: Stack overflow while pushing 4 byte(s)", exception.getMessage());
    }

    @Test
    void nullMemoryAccessIncludesInstructionLineNumber() throws Exception {
        RuntimeException exception = assertRuntimeError("""
                .code
                _main:
                ENTER, 0
                PUSHI, 0
                LOAD4
                RET, 0

                .data
                """);

        assertEquals("Line 5: Null reference", exception.getMessage());
    }

    @Test
    void rejectsWritesToReadOnlyDataRegion() throws Exception {
        RuntimeException exception = assertRuntimeError("""
                .code
                _main:
                ENTER, 0
                PUSHR, value
                PUSHI, 1
                STORE4
                RET, 0

                .data
                value 4 2A
                """);

        assertEquals("Line 6: data:value is read-only", exception.getMessage());
    }

    @Test
    void rejectsNegativeArrayLength() throws Exception {
        RuntimeException exception = assertRuntimeError("""
                .code
                _main:
                ENTER, 0
                PUSHI, -1
                NEWA, 4
                RET, 0

                .data
                """);

        assertEquals("Line 5: Negative array length: -1", exception.getMessage());
    }

    @Test
    void rejectsArrayIndexOutOfBounds() throws Exception {
        RuntimeException exception = assertRuntimeError("""
                .code
                _main:
                ENTER, 0
                PUSHI, 3
                NEWA, 4
                PUSHI, 3
                BOUNDS, 4
                RET, 0

                .data
                """);

        assertEquals("Line 7: Array index out of bounds: index=3, length=3", exception.getMessage());
    }

    @Test
    void rejectsArrayReferenceThatDoesNotPointToAllocationStart() throws Exception {
        RuntimeException exception = assertRuntimeError("""
                .code
                _main:
                ENTER, 0
                PUSHI, 3
                NEWA, 4
                PUSHI, 4
                IADD
                PUSHI, 0
                BOUNDS, 4
                RET, 0

                .data
                """);

        assertTrue(exception.getMessage().contains("Line 9: Array reference does not point to allocation start"));
    }

    @Test
    void constructorRejectsInvalidStackSizes() throws Exception {
        Path bytecodeFile = writeProgram("valid-program.jbc", minimalProgram());

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new VM(bytecodeFile.toString(), 0)),
                () -> assertThrows(IllegalArgumentException.class, () -> new VM(bytecodeFile.toString(), MAX_STACK_SIZE_BYTES + 1))
        );
    }

    @Test
    void parserRejectsMissingDataSection() throws Exception {
        Exception exception = assertConstructionFails("""
                .code
                _main:
                ENTER, 0
                RET, 0
                """);

        assertTrue(exception.getMessage().contains("Missing required '.data' section"));
    }

    @Test
    void parserRejectsUnknownInstruction() throws Exception {
        Exception exception = assertConstructionFails("""
                .code
                _main:
                ENTER, 0
                NOPE
                RET, 0

                .data
                """);

        assertTrue(exception.getMessage().contains("Line 4: unknown instruction 'NOPE'"));
    }

    @Test
    void parserRejectsWrongOperandCount() throws Exception {
        Exception exception = assertConstructionFails("""
                .code
                _main:
                ENTER, 0
                PUSHI
                RET, 0

                .data
                """);

        assertTrue(exception.getMessage().contains("Line 4: instruction 'PUSHI' expects 1 operand(s), got 0"));
    }

    @Test
    void parserRejectsUnknownJumpLabel() throws Exception {
        Exception exception = assertConstructionFails("""
                .code
                _main:
                ENTER, 0
                JUMP, missing
                RET, 0

                .data
                """);

        assertTrue(exception.getMessage().contains("Line 4: unknown label 'missing'"));
    }

    @Test
    void parserRejectsEnterThatDoesNotDirectlyFollowFunctionLabel() throws Exception {
        Exception exception = assertConstructionFails("""
                .code
                _main:
                PUSHI, 1
                ENTER, 0
                RET, 0

                .data
                """);

        assertTrue(exception.getMessage().contains("Line 4: ENTER must come directly after a function label"));
    }

    @Test
    void parserRejectsDuplicateDataSymbol() throws Exception {
        Exception exception = assertConstructionFails("""
                .code
                _main:
                ENTER, 0
                RET, 0

                .data
                value 4 01
                value 4 02
                """);

        assertTrue(exception.getMessage().contains("Line 8: duplicate data symbol 'value'"));
    }

    @Test
    void mainPrintsUsageWhenNoArgumentsAreProvided() throws Exception {
        CapturedOutput output = captureOutput(() -> VM.main(new String[0]));

        assertTrue(output.stdout().contains("Usage: java VM [options] <filePath>"));
        assertEquals("", output.stderr());
    }

    @Test
    void mainRejectsInvalidStackSizeOption() throws Exception {
        CapturedOutput output = captureOutput(() -> VM.main(new String[]{"--stack-size", "0", "program.jbc"}));

        assertTrue(output.stderr().contains("--stack-size must be between 1 and " + MAX_STACK_SIZE_BYTES + " bytes"));
        assertTrue(output.stderr().contains("Usage: java VM [options] <filePath>"));
        assertEquals("", output.stdout());
    }

    @Test
    void mainAcceptsStackSizeUnitsAndRunsProgram() throws Exception {
        Path bytecodeFile = writeProgram("main-stack-size.jbc", """
                .code
                _main:
                ENTER, 0
                PUSHI, 7
                PRINTI
                RET, 0

                .data
                """);

        CapturedOutput output = captureOutput(() -> VM.main(new String[]{
                "--stack-size=1_024",
                bytecodeFile.toString()
        }));

        assertEquals("7", output.stdout());
        assertEquals("", output.stderr());
    }

    @Test
    void mainDumpsStateWhenRuntimeErrorOccursAndDumpOptionIsEnabled() throws Exception {
        Path bytecodeFile = writeProgram("dump-on-error.jbc", """
                .code
                _main:
                ENTER, 0
                PUSHI, 1
                PUSHI, 0
                IDIV
                RET, 0

                .data
                """);

        CapturedOutput output = captureOutput(() -> VM.main(new String[]{
                "--dump-on-error",
                bytecodeFile.toString()
        }));

        assertEquals("", output.stdout());
        assertTrue(output.stderr().contains("Runtime error: Line 6: Division by zero"));
        assertTrue(output.stderr().contains("VM STATE DUMP"));
        assertTrue(output.stderr().contains("=== MEMORY REGIONS ==="));
    }

    private RuntimeException assertRuntimeError(String bytecode) throws Exception {
        Path bytecodeFile = writeProgram("runtime-error.jbc", bytecode);
        return assertThrows(RuntimeException.class, () -> new VM(bytecodeFile.toString()).run());
    }

    private Exception assertConstructionFails(String bytecode) throws Exception {
        Path bytecodeFile = writeProgram("parse-error.jbc", bytecode);
        Exception[] exception = new Exception[1];
        captureOutput(() -> exception[0] = assertThrows(Exception.class, () -> new VM(bytecodeFile.toString())));
        return exception[0];
    }

    private String runProgram(String bytecode) throws Exception {
        Path bytecodeFile = writeProgram("program.jbc", bytecode);
        return captureOutput(() -> new VM(bytecodeFile.toString()).run()).stdout();
    }

    private Path writeProgram(String fileName, String bytecode) throws Exception {
        Path bytecodeFile = tempDir.resolve(fileName);
        Files.writeString(bytecodeFile, bytecode, StandardCharsets.UTF_8);
        return bytecodeFile;
    }

    private String minimalProgram() {
        return """
                .code
                _main:
                ENTER, 0
                RET, 0

                .data
                """;
    }

    private CapturedOutput captureOutput(ThrowingRunnable action) throws Exception {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        try (
                PrintStream capturedOut = new PrintStream(stdout, true, StandardCharsets.UTF_8);
                PrintStream capturedErr = new PrintStream(stderr, true, StandardCharsets.UTF_8)
        ) {
            System.setOut(capturedOut);
            System.setErr(capturedErr);
            action.run();
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        return new CapturedOutput(
                stdout.toString(StandardCharsets.UTF_8),
                stderr.toString(StandardCharsets.UTF_8)
        );
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private record CapturedOutput(String stdout, String stderr) {
    }
}
