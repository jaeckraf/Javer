package ch.zhaw.it.pm4.javer.compiler;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class PipelineTest {

    private static final Path DEFAULT_RESOURCE_ROOT = defaultResourceRoot();
    private static final Path DEFAULT_OUTPUT_ROOT = Path.of("target", "pipelinetest-output");

    private static final String DIAGNOSTICS_DIRECTORY = "diagnostics";
    private static final String LEXER_DIRECTORY = "lexer";
    private static final String AST_DIRECTORY = "ast";
    private static final String AST_SYMBOL_TABLE_DIRECTORY = "astSymboltable";
    private static final String COMPLETE_PIPELINE_DIRECTORY = "completePipeline";

    public static void main(String[] args) throws Exception {
        int exitCode = run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    @Test
    void runPipelineTestsWithMaven() throws Exception {
        assertEquals(0, run(new String[0]));
    }

    public static int run(String[] args) throws Exception {
        disableLogging();

        Path resourceRoot = args.length >= 1
                ? Path.of(args[0])
                : Path.of(System.getProperty("pipeline.resources", DEFAULT_RESOURCE_ROOT.toString()));

        Path outputRoot = args.length >= 2
                ? Path.of(args[1])
                : Path.of(System.getProperty("pipeline.output", DEFAULT_OUTPUT_ROOT.toString()));

        if (!Files.isDirectory(resourceRoot)) {
            throw new IllegalArgumentException("Pipeline test resource directory does not exist: " + resourceRoot);
        }

        Files.createDirectories(outputRoot);

        List<Path> sourceFiles;
        try (Stream<Path> stream = Files.walk(resourceRoot)) {
            sourceFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".jv"))
                    .sorted()
                    .toList();
        }

        List<String> failures = new ArrayList<>();
        List<String> executedTests = new ArrayList<>();

        for (Path sourceFile : sourceFiles) {
            Fixture fixture = Fixture.from(sourceFile);
            Path relativeSourceFile = resourceRoot.relativize(sourceFile);
            runCategorizedTest(fixture, relativeSourceFile, outputRoot, executedTests, failures);
        }

        System.out.println();
        System.out.println("Pipeline tests executed: " + executedTests.size());

        if (executedTests.isEmpty()) {
            System.out.println("No pipeline tests were discovered.");
        } else {
            System.out.println();
            System.out.println("Executed pipeline tests:");
            for (String executedTest : executedTests) {
                System.out.println(" - " + executedTest);
            }
        }

        if (!failures.isEmpty()) {
            System.err.println();
            System.err.println("Pipeline test failures: " + failures.size());

            for (int i = 0; i < failures.size(); i++) {
                System.err.println();
                System.err.println("────────────────────────────────────────");
                System.err.println("Failure " + (i + 1) + " of " + failures.size());
                System.err.println("────────────────────────────────────────");
                System.err.println(failures.get(i));
            }

            return 1;
        }

        System.out.println();
        System.out.println("All pipeline tests passed.");
        return 0;
    }

    private static void runCategorizedTest(
            Fixture fixture,
            Path relativeSourceFile,
            Path outputRoot,
            List<String> executedTests,
            List<String> failures
    ) {
        if (relativeSourceFile.getNameCount() < 2) {
            failures.add("Pipeline test source must be below a category directory: " + fixture.sourceFile());
            return;
        }

        String category = relativeSourceFile.getName(0).toString();

        switch (category) {
            case DIAGNOSTICS_DIRECTORY -> {
                executedTests.add("DIAGNOSTICS      " + relativeSourceFile);
                if (requireExpectedFile(fixture.diagnosticsFile(), "DIAGNOSTICS", fixture, failures)) {
                    runDiagnosticsSnapshotTest(fixture, outputRoot, failures);
                }
            }
            case LEXER_DIRECTORY -> {
                executedTests.add("LEXER            " + relativeSourceFile);
                if (requireExpectedFile(fixture.tokensFile(), "LEXER", fixture, failures)) {
                    runDumpSnapshotTest(
                            "LEXER",
                            fixture,
                            outputRoot,
                            List.of("--dump-lexer"),
                            "LEXER TOKENS",
                            fixture.tokensFile(),
                            failures
                    );
                }
            }
            case AST_DIRECTORY -> {
                executedTests.add("AST              " + relativeSourceFile);
                if (requireExpectedFile(fixture.astFile(), "AST", fixture, failures)) {
                    runDumpSnapshotTest(
                            "AST",
                            fixture,
                            outputRoot,
                            List.of("--dump-ast"),
                            "AST",
                            fixture.astFile(),
                            failures
                    );
                }
            }
            case AST_SYMBOL_TABLE_DIRECTORY -> {
                executedTests.add("AST SYMBOLTABLE  " + relativeSourceFile);
                if (requireExpectedFile(fixture.symbolsFile(), "AST SYMBOLTABLE", fixture, failures)) {
                    runDumpSnapshotTest(
                            "AST_SYMBOLTABLE",
                            fixture,
                            outputRoot,
                            List.of("--dump-ast-symboltable"),
                            "AST SYMBOL TABLE",
                            fixture.symbolsFile(),
                            failures
                    );
                }
            }
            case COMPLETE_PIPELINE_DIRECTORY -> {
                executedTests.add("COMPLETE         " + relativeSourceFile);
                if (requireExpectedFile(fixture.pipelineFile(), "COMPLETE PIPELINE", fixture, failures)) {
                    runPipelineSnapshotTest(fixture, outputRoot, failures);
                }
                if (Files.exists(fixture.bytecodeFile())) {
                    executedTests.add("BYTECODE         " + relativeSourceFile);
                    runBytecodeSnapshotTest(fixture, outputRoot, failures);
                }
            }
            default -> failures.add("Unknown pipeline test category '" + category + "' for " + fixture.sourceFile());
        }
    }

    private static boolean requireExpectedFile(
            Path expectedFile,
            String testName,
            Fixture fixture,
            List<String> failures
    ) {
        if (Files.exists(expectedFile)) {
            return true;
        }

        failures.add(testName + " expected snapshot file is missing for "
                + fixture.sourceFile()
                + ": "
                + expectedFile);
        return false;
    }

    private static void runDumpSnapshotTest(
            String testName,
            Fixture fixture,
            Path outputRoot,
            List<String> dumpFlags,
            String sectionName,
            Path expectedFile,
            List<String> failures
    ) {
        try {
            Path outputFile = createOutputPath(outputRoot, fixture, testName.toLowerCase() + ".jbc");

            List<String> compilerArgs = new ArrayList<>();
            compilerArgs.add("--in-file");
            compilerArgs.add(fixture.sourceFile().toString());
            compilerArgs.add("--out-file");
            compilerArgs.add(outputFile.toString());
            compilerArgs.add("--no-logging");
            compilerArgs.addAll(dumpFlags);

            RunResult result = runCompilerInProcess(compilerArgs);

            String actual = extractSection(result.stdout(), sectionName);
            String expected = readNormalized(expectedFile);

            assertSnapshotEquals(expected, actual, testName + " snapshot mismatch for " + fixture.sourceFile(), failures);
        } catch (Exception exception) {
            failures.add(testName + " crashed for " + fixture.sourceFile() + ": " + exception.getMessage());
        }
    }

    private static void runBytecodeSnapshotTest(
            Fixture fixture,
            Path outputRoot,
            List<String> failures
    ) {
        try {
            Path outputFile = createOutputPath(outputRoot, fixture, "actual.jbc");

            List<String> compilerArgs = List.of(
                    "--in-file", fixture.sourceFile().toString(),
                    "--out-file", outputFile.toString(),
                    "--no-logging"
            );

            RunResult result = runCompilerInProcess(compilerArgs);

            if (!result.stderr().isBlank()) {
                failures.add("BYTECODE compilation produced diagnostics for " + fixture.sourceFile()
                        + System.lineSeparator() + result.stderr());
                return;
            }

            if (!Files.exists(outputFile)) {
                failures.add("BYTECODE output file was not created for " + fixture.sourceFile());
                return;
            }

            String expected = readNormalized(fixture.bytecodeFile());
            String actual = readNormalized(outputFile);

            assertSnapshotEquals(expected, actual, "BYTECODE snapshot mismatch for " + fixture.sourceFile(), failures);
        } catch (Exception exception) {
            failures.add("BYTECODE crashed for " + fixture.sourceFile() + ": " + exception.getMessage());
        }
    }

    private static void runDiagnosticsSnapshotTest(
            Fixture fixture,
            Path outputRoot,
            List<String> failures
    ) {
        try {
            Path outputFile = createOutputPath(outputRoot, fixture, "diagnostics.jbc");

            List<String> compilerArgs = List.of(
                    "--in-file", fixture.sourceFile().toString(),
                    "--out-file", outputFile.toString(),
                    "--no-logging"
            );

            RunResult result = runCompilerInProcess(compilerArgs);

            String expected = normalizeDiagnostics(readNormalized(fixture.diagnosticsFile()));
            String actual = normalizeDiagnostics(result.stderr());

            assertSnapshotEquals(expected, actual, "DIAGNOSTICS snapshot mismatch for " + fixture.sourceFile(), failures);
        } catch (Exception exception) {
            failures.add("DIAGNOSTICS crashed for " + fixture.sourceFile() + ": " + exception.getMessage());
        }
    }

    private static void runPipelineSnapshotTest(
            Fixture fixture,
            Path outputRoot,
            List<String> failures
    ) {
        try {
            Path outputFile = createOutputPath(outputRoot, fixture, "pipeline.jbc");

            List<String> compilerArgs = List.of(
                    "--in-file", fixture.sourceFile().toString(),
                    "--out-file", outputFile.toString(),
                    "--no-logging",
                    "--dump-lexer",
                    "--dump-ast",
                    "--dump-ast-symboltable"
            );

            RunResult result = runCompilerInProcess(compilerArgs);

            String actual = normalize(result.stdout());
            String expected = readNormalized(fixture.pipelineFile());

            assertSnapshotEquals(expected, actual, "PIPELINE snapshot mismatch for " + fixture.sourceFile(), failures);
        } catch (Exception exception) {
            failures.add("PIPELINE crashed for " + fixture.sourceFile() + ": " + exception.getMessage());
        }
    }

    private static RunResult runCompilerInProcess(List<String> compilerArgs) {
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

            CompilerOptions options = CompilerOptions.create(compilerArgs.toArray(String[]::new));
            new Compiler(options).compile();

            capturedOut.flush();
            capturedErr.flush();

            return new RunResult(
                    stdoutBuffer.toString(StandardCharsets.UTF_8),
                    stderrBuffer.toString(StandardCharsets.UTF_8),
                    0
            );
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    private static Path createOutputPath(Path outputRoot, Fixture fixture, String suffix) throws Exception {
        Path relativeParent = fixture.sourceFile().getParent() == null
                ? Path.of("")
                : fixture.sourceFile().getParent().getFileName();

        Path directory = outputRoot.resolve(relativeParent);
        Files.createDirectories(directory);

        return directory.resolve(fixture.baseName() + "." + suffix);
    }

    private static String extractSection(String output, String sectionName) {
        String normalized = normalize(output);
        String header = "=== " + sectionName + " ===";

        int start = normalized.indexOf(header);
        if (start < 0) {
            return "";
        }

        start += header.length();

        if (start < normalized.length() && normalized.charAt(start) == '\n') {
            start++;
        }

        int nextSection = normalized.indexOf("\n=== ", start);
        String section = nextSection >= 0
                ? normalized.substring(start, nextSection)
                : normalized.substring(start);

        section = section.replaceFirst("\\nCompilation Successful\\s*$", "");

        return normalize(section);
    }

    private static String readNormalized(Path file) throws Exception {
        return normalize(Files.readString(file, StandardCharsets.UTF_8));
    }

    private static String normalize(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replaceAll("[ \t]+\\n", "\n")
                .strip();
    }

    private static String normalizeDiagnostics(String text) {
        return normalize(text)
                .replace("\\", "/")
                .replaceAll("File: (?:.*/)?Compiler/src/test/resources/", "File: src/test/resources/")
                .replaceAll("File: src/test/resources/", "File: src/test/resources/");
    }

    private static void assertSnapshotEquals(
            String expected,
            String actual,
            String message,
            List<String> failures
    ) {
        if (Objects.equals(expected, actual)) {
            return;
        }

        failures.add(message
                + System.lineSeparator()
                + "--- expected ---"
                + System.lineSeparator()
                + expected
                + System.lineSeparator()
                + "--- actual ---"
                + System.lineSeparator()
                + actual);
    }

    private static void disableLogging() {
        LoggerContext loggerContext =
                (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();

        loggerContext
                .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
                .setLevel(Level.OFF);
    }

    private static Path defaultResourceRoot() {
        Path fromProjectRoot = Path.of("Compiler", "src", "test", "resources", "pipelinetests");
        if (Files.isDirectory(fromProjectRoot)) {
            return fromProjectRoot;
        }

        return Path.of("src", "test", "resources", "pipelinetests");
    }

    record RunResult(String stdout, String stderr, int exitCode) {
    }

    record Fixture(Path sourceFile, String baseName) {

        static Fixture from(Path sourceFile) {
            String fileName = sourceFile.getFileName().toString();
            String baseName = fileName.substring(0, fileName.length() - ".jv".length());
            return new Fixture(sourceFile, baseName);
        }

        Path tokensFile() {
            return sibling(".tokens");
        }

        Path astFile() {
            return sibling(".ast");
        }

        Path symbolsFile() {
            return sibling(".symbols");
        }

        Path bytecodeFile() {
            return sibling(".jbc");
        }

        Path diagnosticsFile() {
            return sibling(".diagnostics");
        }

        Path pipelineFile() {
            return sibling(".pipeline");
        }

        private Path sibling(String extension) {
            return sourceFile.resolveSibling(baseName + extension);
        }
    }
}
