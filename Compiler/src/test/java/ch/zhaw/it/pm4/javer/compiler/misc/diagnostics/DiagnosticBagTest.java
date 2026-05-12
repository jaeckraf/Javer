package ch.zhaw.it.pm4.javer.compiler.misc.diagnostics;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DiagnosticBagTest {

    @TempDir
    Path tempDir;

    @Test
    void dumpReportIncludesSourceLineAndTabAwareMarker() throws Exception {
        Path sourceFile = tempDir.resolve("input.jv");
        Files.writeString(sourceFile, "\tif();\n", StandardCharsets.UTF_8);

        SourceCache sourceCache = new SourceCache(sourceFile.toString());
        DiagnosticBag bag = new DiagnosticBag(sourceFile.toString(), 10, CompilationPhase.PARSING, sourceCache);

        bag.add(new SourceLocation(8, 8, 1), Severity.ERROR, "Expected: Literal: integer; but found: ).");

        String report = bag.dumpReport();

        assertTrue(report.contains("[ERROR] Expected: Literal: integer; but found: )."));
        assertTrue(report.contains(sourceFile + ":1:8"));
        assertTrue(report.contains("1 |     if();"));
        assertTrue(report.contains("  |        ^"));
    }
}
