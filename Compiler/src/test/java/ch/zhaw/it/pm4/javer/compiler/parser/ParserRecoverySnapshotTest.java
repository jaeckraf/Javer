package ch.zhaw.it.pm4.javer.compiler.parser;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.lexer.Lexer;
import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class ParserRecoverySnapshotTest {

    private static final String SOURCE_FILE_EXTENSION = ".javer";
    private static final String DIAGNOSTICS_FILE_EXTENSION = ".diagnostics";

    private static final Path DEFAULT_RESOURCE_ROOT = defaultResourceRoot();
    private static final Path OUTPUT_ROOT = Path.of("target", "parser-recovery-output");

    private static Path defaultResourceRoot() {
        Path fromProjectRoot = Path.of("Compiler", "src", "test", "resources", "parser-recovery");
        if (Files.isDirectory(fromProjectRoot)) {
            return fromProjectRoot;
        }

        return Path.of("src", "test", "resources", "parser-recovery");
    }

    @TestFactory
    Stream<DynamicTest> parserRecoveryFixturesMatchSnapshots() throws Exception {
        Path resourceRoot = Path.of(System.getProperty("parser.recovery.resources", DEFAULT_RESOURCE_ROOT.toString()));

        try (Stream<Path> stream = Files.walk(resourceRoot)) {
            List<Path> sourceFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(SOURCE_FILE_EXTENSION))
                    .sorted()
                    .toList();

            assertFalse(sourceFiles.isEmpty(), "No parser recovery fixtures found in " + resourceRoot);

            return sourceFiles.stream()
                    .map(sourceFile -> dynamicTest(
                            resourceRoot.relativize(sourceFile).toString(),
                            () -> assertSnapshot(sourceFile)
                    ));
        }
    }

    private void assertSnapshot(Path sourceFile) throws Exception {
        Path expectedFile = expectedDiagnosticsFile(sourceFile);
        DiagnosticBag diagnosticBag = parse(sourceFile);

        String expected = normalizeDiagnostics(Files.readString(expectedFile, StandardCharsets.UTF_8));
        String actual = normalizeDiagnostics(diagnosticBag.dumpReport());

        Path actualFile = OUTPUT_ROOT.resolve(sourceFile.getFileName().toString().replace(SOURCE_FILE_EXTENSION, DIAGNOSTICS_FILE_EXTENSION));
        Files.createDirectories(actualFile.getParent());
        Files.writeString(actualFile, actual + "\n", StandardCharsets.UTF_8);

        assertEquals(expected, actual, "Parser recovery diagnostics mismatch for " + sourceFile
                + ". Actual output was also written to " + actualFile);
    }

    private DiagnosticBag parse(Path sourceFile) {
        SourceCache sourceCache = new SourceCache(sourceFile.toString());
        DiagnosticBag diagnosticBag = new DiagnosticBag(
                sourceFile.toString(),
                50,
                CompilationPhase.PARSING,
                sourceCache
        );
        List<Token> tokens = new Lexer(sourceCache.getSourceCode(), diagnosticBag).lexSourcecode();
        new Parser(tokens, diagnosticBag).parse();
        return diagnosticBag;
    }

    private Path expectedDiagnosticsFile(Path sourceFile) {
        String fileName = sourceFile.getFileName().toString();
        String baseName = fileName.substring(0, fileName.length() - SOURCE_FILE_EXTENSION.length());
        return sourceFile.resolveSibling(baseName + DIAGNOSTICS_FILE_EXTENSION);
    }

    private String normalizeDiagnostics(String text) {
        return normalize(text)
                .replace("\\", "/")
                .replaceAll("(?m)(File: )(?:.*/)?Compiler/src/test/resources/parser-recovery/", "$1src/test/resources/parser-recovery/")
                .replaceAll("(?m)(File: )(?:.*/)?src/test/resources/parser-recovery/", "$1src/test/resources/parser-recovery/")
                .replaceAll("(?m)( {2}--> )(?:.*/)?Compiler/src/test/resources/parser-recovery/", "$1src/test/resources/parser-recovery/")
                .replaceAll("(?m)( {2}--> )(?:.*/)?src/test/resources/parser-recovery/", "$1src/test/resources/parser-recovery/");
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replaceAll("[ \t]+\\n", "\n")
                .strip();
    }
}
