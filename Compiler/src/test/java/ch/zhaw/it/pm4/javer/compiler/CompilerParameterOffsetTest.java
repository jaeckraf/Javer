package ch.zhaw.it.pm4.javer.compiler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CompilerParameterOffsetTest {

    @TempDir
    Path tempDir;

    @Test
    void parameterOffsetsIncludeLocalFrameBytesInGeneratedBytecode() throws Exception {
        Path sourceFile = tempDir.resolve("parameter-offset.javer");
        Path outputBase = tempDir.resolve("parameter-offset");

        Files.writeString(sourceFile, """
                fn void main() {
                    call useFirst(1, 2);
                }

                fn int useFirst(int first, int second) {
                    let int copy = first;
                    return copy;
                }
                """, StandardCharsets.UTF_8);

        new Compiler(CompilerOptions.create(
                "--in-file", sourceFile.toString(),
                "--out-file", outputBase.toString()
        )).compile();

        String bytecode = Files.readString(outputBase.resolveSibling("parameter-offset.jbc"), StandardCharsets.UTF_8)
                .replace("\r\n", "\n");
        assertTrue(bytecode.contains("""
                _useFirst:
                ENTER, 4
                LOCAL, 0
                LOCAL, 24
                LOAD4
                STORE4
                """), bytecode);
    }
}
