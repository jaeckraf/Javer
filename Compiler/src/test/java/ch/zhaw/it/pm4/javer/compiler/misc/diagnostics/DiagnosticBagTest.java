package ch.zhaw.it.pm4.javer.compiler.misc.diagnostics;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiagnosticBagTest {

    @TempDir
    Path tempDir;

    @Test
    void dumpReportIncludesSourceLineAndTabAwareMarker() throws Exception {
        Path sourceFile = tempDir.resolve("input.javer");
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

    @Test
    void getDiagnosticsReturnsImmutableSnapshotInInsertionOrder() {
        DiagnosticBag bag = new DiagnosticBag("input.javer", 10, CompilationPhase.PARSING, null);

        Diagnostic first = new Diagnostic(new SourceLocation(1, 1, 1), Severity.ERROR, "first");
        Diagnostic second = new Diagnostic(new SourceLocation(2, 2, 1), Severity.WARNING, "second");

        bag.add(first);
        List<Diagnostic> snapshot = bag.getDiagnostics();
        bag.add(second);

        assertEquals(List.of(first), snapshot);
        assertEquals(List.of(first, second), bag.getDiagnostics());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(second));
    }
}
