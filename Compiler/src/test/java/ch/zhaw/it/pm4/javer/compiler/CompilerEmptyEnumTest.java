package ch.zhaw.it.pm4.javer.compiler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompilerEmptyEnumTest {

    @TempDir
    Path tempDir;

    @Test
    void emptyEnumRemainsInSymbolTableWithoutDataEntry() throws Exception {
        Path sourceFile = tempDir.resolve("empty-enum.javer");
        Path outputBase = tempDir.resolve("empty-enum");

        Files.writeString(sourceFile, """
                enum E {
                }

                fn void main() {
                }
                """, StandardCharsets.UTF_8);

        RunResult result = compile(
                "--in-file", sourceFile.toString(),
                "--out-file", outputBase.toString(),
                "--dump-symboltable");

        assertTrue(result.stderr().isBlank(), result.stderr());
        assertTrue(result.stdout().contains("| enum | E      | enum | 'enum_E_values' | 0         |"), result.stdout());
        assertTrue(result.stdout().contains("| table: enum E | data label: 'enum_E_values' | element bytes: 4 |"), result.stdout());

        String bytecode = Files.readString(outputBase.resolveSibling("empty-enum.jbc"), StandardCharsets.UTF_8);
        assertTrue(bytecode.contains(".data"), bytecode);
        assertFalse(bytecode.contains("enum_E_values"), bytecode);
    }

    private static RunResult compile(String... args) {
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

            CompilerOptions options = CompilerOptions.create(args);
            new Compiler(options).compile();

            capturedOut.flush();
            capturedErr.flush();

            return new RunResult(
                    stdoutBuffer.toString(StandardCharsets.UTF_8),
                    stderrBuffer.toString(StandardCharsets.UTF_8)
            );
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    private record RunResult(String stdout, String stderr) {
    }
}
