package ch.zhaw.it.pm4.javer.vm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VMTest {

    @TempDir
    Path tempDir;

    @Test
    void printsInteger() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHI, 42
                PRINTI
                RET
                
                .data
                """);

        assertEquals("42", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void printsChar() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHC, 65
                PRINTC
                RET
                
                .data
                """);

        assertEquals("A", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void printsByteAsCharacter() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHB, 65
                PRINTB
                RET
                
                .data
                """);

        assertEquals("A", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void printsDouble() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHD, 3.5
                PRINTD
                RET
                
                .data
                """);

        assertEquals("3.5", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void printsDataString() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                DPRINTS, hello
                RET
                
                .data
                hello 2 0048,0065,006C,006C,006F,0000
                """);

        assertEquals("Hello", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void addsIntegers() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHI, 20
                PUSHI, 22
                IADD
                PRINTI
                RET
                
                .data
                """);

        assertEquals("42", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void subtractsIntegersInCorrectOrder() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHI, 50
                PUSHI, 8
                ISUB
                PRINTI
                RET
                
                .data
                """);

        assertEquals("42", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void multipliesIntegers() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHI, 6
                PUSHI, 7
                IMUL
                PRINTI
                RET
                
                .data
                """);

        assertEquals("42", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void dividesIntegersInCorrectOrder() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHI, 84
                PUSHI, 2
                IDIV
                PRINTI
                RET
                
                .data
                """);

        assertEquals("42", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void moduloIntegersInCorrectOrder() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHI, 44
                PUSHI, 43
                IMOD
                PRINTI
                RET
                
                .data
                """);

        assertEquals("1", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void comparesIntegersLessThan() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHI, 1
                PUSHI, 2
                ILT
                PRINTB
                RET
                
                .data
                """);

        assertEquals(String.valueOf((char) 1), result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void jumpsWhenConditionIsTrue() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHB, 1
                JUMPT, yes
                PUSHI, 0
                PRINTI
                RET
                yes:
                PUSHI, 42
                PRINTI
                RET
                
                .data
                """);

        assertEquals("42", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void jumpsWhenConditionIsFalse() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                PUSHB, 0
                JUMPF, no
                PUSHI, 1
                PRINTI
                RET
                no:
                PUSHI, 42
                PRINTI
                RET
                
                .data
                """);

        assertEquals("42", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void callsFunctionAndReturnsInteger() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                CALL, _answer, 0
                PRINTI
                RET
                
                _answer:
                ENTER, 0
                PUSHI, 42
                RETI
                
                .data
                """);

        assertEquals("42", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void storesAndLoadsLocalInteger() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 4
                PUSHI, 42
                FSTORE4, 0
                FLOAD4, 0
                PRINTI
                RET
                
                .data
                """);

        assertEquals("42", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void loadsIntegerFromDataSection() throws Exception {
        RunResult result = runProgram("""
                .code
                _main:
                ENTER, 0
                LOADI, answer, 0
                PRINTI
                RET
                
                .data
                answer 4 0000002A
                """);

        assertEquals("42", result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void reportsMissingCodeSection() throws Exception {
        RunResult result = runMain("""
                .data
                value 4 0000002A
                """);

        assertEquals("", result.stdout());
        assertTrue(result.stderr().contains("Missing required '.code' section"));
        assertTrue(result.stderr().contains("Program contains parse errors. Execution aborted."));
    }

    @Test
    void reportsMissingDataSection() throws Exception {
        RunResult result = runMain("""
                .code
                _main:
                ENTER, 0
                RET
                """);

        assertEquals("", result.stdout());
        assertTrue(result.stderr().contains("Missing required '.data' section"));
        assertTrue(result.stderr().contains("Program contains parse errors. Execution aborted."));
    }

    @Test
    void reportsUnknownInstruction() throws Exception {
        RunResult result = runMain("""
                .code
                _main:
                ENTER, 0
                DOES_NOT_EXIST
                RET
                
                .data
                """);

        assertEquals("", result.stdout());
        assertTrue(result.stderr().contains("unknown instruction 'DOES_NOT_EXIST'"));
        assertTrue(result.stderr().contains("Program contains parse errors. Execution aborted."));
    }

    @Test
    void reportsUnknownJumpLabel() throws Exception {
        RunResult result = runMain("""
                .code
                _main:
                ENTER, 0
                JUMP, missing
                
                .data
                """);

        assertEquals("", result.stdout());
        assertTrue(result.stderr().contains("unknown label 'missing'"));
        assertTrue(result.stderr().contains("Program contains parse errors. Execution aborted."));
    }

    @Test
    void reportsRuntimeErrorWhenMainIsMissing() throws Exception {
        RunResult result = runMain("""
                .code
                _notMain:
                ENTER, 0
                RET
                
                .data
                """);

        assertEquals("", result.stdout());
        assertTrue(result.stderr().contains("Runtime error: No _main function found"));
    }

    @Test
    void reportsRuntimeErrorOnDivisionByZero() throws Exception {
        RunResult result = runMain("""
                .code
                _main:
                ENTER, 0
                PUSHI, 42
                PUSHI, 0
                IDIV
                PRINTI
                RET
                
                .data
                """);

        assertEquals("", result.stdout());
        assertTrue(result.stderr().contains("Runtime error: Division by zero"));
    }

    @Test
    void reportsUsageWhenNoArgumentsArePassed() {
        RunResult result = captureMainWithoutFile();

        assertEquals("Usage: java VM <filePath>", normalize(result.stdout()));
        assertEquals("", result.stderr());
    }

    private RunResult runProgram(String bytecode) throws Exception {
        Path program = writeProgram(bytecode);

        return captureOutput(() -> {
            VM vm = new VM(program.toString());
            vm.run();
        });
    }

    private RunResult runMain(String bytecode) throws Exception {
        Path program = writeProgram(bytecode);

        return captureOutput(() -> VM.main(new String[]{program.toString()}));
    }

    private RunResult captureMainWithoutFile() {
        return captureOutput(() -> VM.main(new String[]{}));
    }

    private Path writeProgram(String bytecode) throws Exception {
        Path program = tempDir.resolve("program.bytecode");
        Files.writeString(program, normalizeProgram(bytecode), StandardCharsets.UTF_8);
        return program;
    }

    private RunResult captureOutput(ThrowingRunnable runnable) {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();

        try (
                PrintStream capturedOut = new PrintStream(stdoutBuffer, true, StandardCharsets.UTF_8);
                PrintStream capturedErr = new PrintStream(stderrBuffer, true, StandardCharsets.UTF_8)
        ) {
            System.setOut(capturedOut);
            System.setErr(capturedErr);

            runnable.run();

            capturedOut.flush();
            capturedErr.flush();

            return new RunResult(
                    stdoutBuffer.toString(StandardCharsets.UTF_8),
                    stderrBuffer.toString(StandardCharsets.UTF_8)
            );
        } catch (Exception exception) {
            throw new AssertionError("VM test execution failed unexpectedly", exception);
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    private static String normalizeProgram(String text) {
        return text.stripIndent().strip() + System.lineSeparator();
    }

    private static String normalize(String text) {
        return text
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .strip();
    }

    private record RunResult(String stdout, String stderr) {
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}