package ch.zhaw.it.pm4.javer.compiler.parser;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.lexer.Lexer;
import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserDiagnosticsTest {

    @TempDir
    Path tempDir;

    @Test
    void missingStatementSemicolonReportsInsertionPointAfterExpression() throws Exception {
        String source = """
                fn void main() {
                    while(false){
                    1 + 2 - a
                }
                """;
        Path sourceFile = tempDir.resolve("input.javer");
        Files.writeString(sourceFile, source, StandardCharsets.UTF_8);

        SourceCache sourceCache = new SourceCache(sourceFile.toString());
        DiagnosticBag diagnosticBag = new DiagnosticBag(
                sourceFile.toString(),
                10,
                CompilationPhase.PARSING,
                sourceCache
        );

        List<Token> tokens = new Lexer(sourceCache.getSourceCode(), diagnosticBag).lexSourcecode();
        new Parser(tokens, diagnosticBag).parse();

        String report = diagnosticBag.dumpReport();

        assertTrue(report.contains("[ERROR] Expected: ';'; but found: }."));
        assertTrue(report.contains(sourceFile + ":3:14"));
        assertTrue(report.contains("3 |     1 + 2 - a"));
        assertTrue(report.contains("  |              ^"));
        assertFalse(report.contains(sourceFile + ":4:1"));
    }
}
