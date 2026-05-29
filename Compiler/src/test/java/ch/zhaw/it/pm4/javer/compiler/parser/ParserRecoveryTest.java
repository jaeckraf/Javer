package ch.zhaw.it.pm4.javer.compiler.parser;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.lexer.Lexer;
import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Diagnostic;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserRecoveryTest {

    @TempDir
    Path tempDir;

    private int sourceCounter;

    @Test
    void validDeclarationsParseWithoutDiagnostics() throws Exception {
        ParseResult result = parseSource("""
                enum Color { RED, GREEN = 2 }
                struct Point { int x; int y; }
                fn int add(int left, int right) {
                    return left + right;
                }
                """);

        assertNoDiagnostics(result);
        assertEquals(3, result.root().getDeclarations().size());

        FunctionDeclaration function = assertInstanceOf(
                FunctionDeclaration.class,
                result.root().getDeclarations().get(2)
        );
        assertEquals("add", function.getName());
        assertEquals(2, function.getParameters().size());
    }

    @Test
    void validNestedControlFlowParsesWithoutDiagnostics() throws Exception {
        ParseResult result = parseSource("""
                fn void main() {
                    let int i = 0;
                    while (i < 3) {
                        if (i == 1) {
                            i++;
                        } else {
                            i += 1;
                        }
                    }
                }
                """);

        assertNoDiagnostics(result);
    }

    @Test
    void topLevelGarbageIsReportedAndParserContinuesToNextDeclaration() throws Exception {
        ParseResult result = parseSource("""
                garbage
                fn void main() {}
                """);

        assertDiagnosticMessagesContain(result, "Expected one of:", "but found: garbage.");
        assertEquals(1, result.root().getDeclarations().size());
        FunctionDeclaration function = assertInstanceOf(
                FunctionDeclaration.class,
                result.root().getDeclarations().getFirst()
        );
        assertEquals("main", function.getName());
    }

    @Test
    void missingStatementSemicolonIsStoredAtExpressionEnd() throws Exception {
        Diagnostic diagnostic = assertSingleDiagnostic("""
                fn void main() {
                    1 + 2
                }
                """, "Expected: ';'; but found: }.");

        assertLocation(diagnostic, 2, 10);
    }

    @Test
    void missingClosingBlockIsStoredAtEndOfLastStatement() throws Exception {
        Diagnostic diagnostic = assertSingleDiagnostic("""
                fn void main() {
                    let int x = 1;
                """, "Expected: '}'; but found: end of file.");

        assertLocation(diagnostic, 2, 19);
    }

    @Test
    void missingStructFieldSemicolonIsReported() throws Exception {
        Diagnostic diagnostic = assertSingleDiagnostic(
                "struct Point { int x }",
                "Expected: ';'; but found: }."
        );

        assertLocation(diagnostic, 1, 21);
    }

    @Test
    void nonIntegerEnumValueIsReportedThroughDiagnosticBag() throws Exception {
        ParseResult result = parseSource("enum Color { RED = true }");

        assertDiagnosticMessagesContain(
                result,
                "Expected one of: Literal:",
                "but found: true.",
                "Invalid integer literal"
        );
    }

    @Test
    void missingFunctionNameIsReportedThroughParse() throws Exception {
        ParseResult result = parseSource("fn void () {}");

        assertDiagnosticMessagesContain(result, "Expected: 'Identifier'; but found: (.");
    }

    @Test
    void missingVariableNameIsReportedThroughParse() throws Exception {
        ParseResult result = parseSource("fn void main() { let int = 1; }");

        assertDiagnosticMessagesContain(result, "Expected: 'Identifier'; but found: =.");
    }

    @Test
    void missingVariableInitializerExpressionIsReported() throws Exception {
        Diagnostic diagnostic = assertSingleDiagnostic(
                "fn void main() { let int x = ; }",
                "Expected one of:"
        );

        assertMessageContains(diagnostic, "but found: ;.");
    }

    @Test
    void missingIfConditionExpressionIsReported() throws Exception {
        Diagnostic diagnostic = assertSingleDiagnostic(
                "fn void main() { if () {} }",
                "Expected one of:"
        );

        assertMessageContains(diagnostic, "but found: ).");
    }

    @Test
    void missingWhileOpeningParenthesisIsReportedThroughParse() throws Exception {
        ParseResult result = parseSource("fn void main() { while true) {} }");

        assertDiagnosticMessagesContain(result, "Expected: '('; but found: true.");
    }

    @Test
    void missingCallFunctionNameIsReportedThroughParse() throws Exception {
        ParseResult result = parseSource("fn void main() { call (); }");

        assertDiagnosticMessagesContain(result, "Expected: 'Identifier'; but found: (.");
    }

    @Test
    void missingCallClosingParenthesisIsReportedThroughParse() throws Exception {
        ParseResult result = parseSource("fn void main() { call print(1; }");

        assertDiagnosticMessagesContain(result, "Expected: ')'; but found: ;.");
    }

    @Test
    void missingIndexExpressionIsReported() throws Exception {
        Diagnostic diagnostic = assertSingleDiagnostic(
                "fn void main() { values[]; }",
                "Expected one of:"
        );

        assertMessageContains(diagnostic, "but found: ].");
    }

    @Test
    void missingTernaryColonIsReportedThroughParse() throws Exception {
        ParseResult result = parseSource("fn void main() { let int x = true ? 1 2; }");

        assertDiagnosticMessagesContain(result, "Expected: ':'; but found: 2.");
    }

    @Test
    void missingSwitchCaseColonIsReportedThroughParse() throws Exception {
        ParseResult result = parseSource("""
                fn void main() {
                    switch(value) {
                        case 1 return;
                    }
                }
                """);

        assertDiagnosticMessagesContain(result, "Expected: ':'; but found: return.");
    }

    @Test
    void emptyNewArrayDimensionIsReported() throws Exception {
        Diagnostic diagnostic = assertSingleDiagnostic(
                "fn void main() { let int[] values = new int[]; }",
                "Array dimensions in a new expression must contain an expression."
        );

        assertLocation(diagnostic, 1, 45);
    }

    @Test
    void newPrimitiveWithoutDimensionsIsReported() throws Exception {
        assertSingleDiagnostic(
                "fn void main() { let int value = new int; }",
                "'new' for primitive values requires array dimensions."
        );
    }

    @Test
    void newStringIsReported() throws Exception {
        assertSingleDiagnostic(
                "fn void main() { let string value = new string; }",
                "'new string' is not allowed."
        );
    }

    @Test
    void newEnumIsReported() throws Exception {
        assertSingleDiagnostic(
                "fn void main() { let enum Color value = new enum Color; }",
                "'new' can only allocate structs or arrays, not enum values."
        );
    }

    @Test
    void structInitializerInNewExpressionIsReported() throws Exception {
        assertSingleDiagnostic(
                "fn void main() { let struct Point value = new struct Point { 1 }; }",
                "Struct initializers are not allowed; initializers only apply to arrays."
        );
    }

    @Test
    void arrayInitializerWithoutDimensionsIsReported() throws Exception {
        assertSingleDiagnostic(
                "fn void main() { let int[] values = new int { 1, 2 }; }",
                "Array initializer in a new expression requires explicit dimensions."
        );
    }

    @Test
    void overflowingIntegerLiteralIsReportedByParser() throws Exception {
        assertSingleDiagnostic(
                "fn void main() { let int value = 999999999999999999999; }",
                "Invalid integer literal: 999999999999999999999."
        );
    }

    @Test
    void parserContinuesAfterBrokenVariableDeclaration() throws Exception {
        ParseResult result = parseSource("""
                fn void main() {
                    let int x = ;
                    return;
                }
                """);

        assertDiagnosticMessagesContain(result, "Expected one of:", "but found: ;.");
        FunctionDeclaration function = assertInstanceOf(
                FunctionDeclaration.class,
                result.root().getDeclarations().getFirst()
        );
        assertEquals(2, function.getBody().getStatements().size());
    }

    private ParseResult parseSource(String source) throws Exception {
        Path sourceFile = tempDir.resolve("input-" + sourceCounter++ + ".javer");
        Files.writeString(sourceFile, source, StandardCharsets.UTF_8);
        return parseSource(sourceFile);
    }

    private ParseResult parseSource(Path sourceFile) {
        SourceCache sourceCache = new SourceCache(sourceFile.toString());
        DiagnosticBag diagnosticBag = new DiagnosticBag(
                sourceFile.toString(),
                50,
                CompilationPhase.PARSING,
                sourceCache
        );
        List<Token> tokens = new Lexer(sourceCache.getSourceCode(), diagnosticBag).lexSourcecode();
        CompilationUnit root = new Parser(tokens, diagnosticBag).parse();
        return new ParseResult(root, diagnosticBag);
    }

    private void assertNoDiagnostics(ParseResult result) {
        assertFalse(result.diagnosticBag().hasErrors(), result.diagnosticBag()::dumpReport);
        assertTrue(result.diagnostics().isEmpty(), result.diagnosticBag()::dumpReport);
    }

    private Diagnostic assertSingleDiagnostic(String source, String expectedMessageFragment) throws Exception {
        ParseResult result = parseSource(source);
        List<Diagnostic> diagnostics = result.diagnostics();
        assertEquals(1, diagnostics.size(), result.diagnosticBag()::dumpReport);
        Diagnostic diagnostic = diagnostics.getFirst();
        assertMessageContains(diagnostic, expectedMessageFragment);
        return diagnostic;
    }

    private void assertDiagnosticMessagesContain(ParseResult result, String... expectedFragments) {
        String allMessages = String.join("\n", result.diagnostics().stream().map(Diagnostic::getMessage).toList());
        for (String expectedFragment : expectedFragments) {
            assertTrue(
                    allMessages.contains(expectedFragment),
                    () -> "Expected diagnostics to contain '%s' but got:%n%s%n%n%s"
                            .formatted(expectedFragment, allMessages, result.diagnosticBag().dumpReport())
            );
        }
    }

    private void assertMessageContains(Diagnostic diagnostic, String expectedFragment) {
        assertTrue(
                diagnostic.getMessage().contains(expectedFragment),
                () -> "Expected diagnostic to contain '%s' but got '%s'"
                        .formatted(expectedFragment, diagnostic.getMessage())
        );
    }

    private void assertLocation(Diagnostic diagnostic, int lineNumber, int startColumn) {
        SourceLocation location = diagnostic.getLocation();
        assertNotNull(location);
        assertEquals(lineNumber, location.lineNumber());
        assertEquals(startColumn, location.startColumn());
    }

    private record ParseResult(CompilationUnit root, DiagnosticBag diagnosticBag) {
        private List<Diagnostic> diagnostics() {
            return diagnosticBag.getDiagnostics();
        }
    }
}
