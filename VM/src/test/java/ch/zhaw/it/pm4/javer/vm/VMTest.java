package ch.zhaw.it.pm4.javer.vm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class VMTest {

    @TempDir
    Path tempDir;

    @Test
    void runtimeErrorIncludesInstructionLineNumber() throws Exception {
        Path bytecodeFile = tempDir.resolve("division-by-zero.jbc");
        Files.writeString(bytecodeFile, """
                .code
                _main:
                ENTER, 0
                PUSHI, 1
                PUSHI, 0
                IDIV
                RET, 0

                .data
                """, StandardCharsets.UTF_8);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> new VM(bytecodeFile.toString()).run());

        assertEquals("Line 6: Division by zero", exception.getMessage());
    }
}
