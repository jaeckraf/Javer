package ch.zhaw.it.pm4.javer.compiler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;

@JacocoGenerated("jacoco-ignore")
public final class PipelineTest {

    private static final Path DEFAULT_RESOURCE_ROOT =
            Path.of("Compiler", "src", "test", "resources", "snapshots");

    private static final Path DEFAULT_OUTPUT_ROOT =
            Path.of("target", "pipeline-test-output");

    private PipelineTest() {
    }

    public static void main(String[] args) throws Exception {
        disableLogging();

        Path resourceRoot = args.length >= 1
                ? Path.of(args[0])
                : Path.of(System.getProperty("pipeline.resources", DEFAULT_RESOURCE_ROOT.toString()));

        Path outputRoot = args.length >= 2
                ? Path.of(args[1])
                : Path.of(System.getProperty("pipeline.output", DEFAULT_OUTPUT_ROOT.toString()));

        if (!Files.isDirectory(resourceRoot)) {
            throw new IllegalArgumentException("Snapshot resource directory does not exist: " + resourceRoot);
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
        int executed = 0;

        for (Path sourceFile : sourceFiles) {
            Fixture fixture = Fixture.from(sourceFile);

            if (Files.exists(fixture.tokensFile())) {
                executed++;
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

            if (Files.exists(fixture.astFile())) {
                executed++;
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

            if (Files.exists(fixture.symbolsFile())) {
                executed++;
                runDumpSnapshotTest(
                        "SYMBOLS",
                        fixture,
                        outputRoot,
                        List.of("--dump-ast-symboltable"),
                        "AST SYMBOL TABLE",
                        fixture.symbolsFile(),
                        failures
                );
            }

            if (Files.exists(fixture.bytecodeFile())) {
                executed++;
                runBytecodeSnapshotTest(fixture, outputRoot, failures);
            }

            if (Files.exists(fixture.diagnosticsFile())) {
                executed++;
                runDiagnosticsSnapshotTest(fixture, outputRoot, failures);
            }

            if (Files.exists(fixture.pipelineFile())) {
                executed++;
                runPipelineSnapshotTest(fixture, outputRoot, failures);
            }
        }

        System.out.println();
        System.out.println("Pipeline snapshot tests executed: " + executed);

        if (!failures.isEmpty()) {
            System.err.println();
            System.err.println("Pipeline snapshot test failures: " + failures.size());
            failures.forEach(failure -> System.err.println(" - " + failure));
            System.exit(1);
        }

        System.out.println("All pipeline snapshot tests passed.");
    }

    private static void disableLogging() {
        LoggerContext loggerContext =
                (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();

        loggerContext
                .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
                .setLevel(Level.OFF);
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
            Path outputFile = createOutputPath(outputRoot, fixture, testName.toLowerCase() + ".bytecode");

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

            assertEquals(expected, actual, testName + " snapshot mismatch for " + fixture.sourceFile(), failures);
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
            Path outputFile = createOutputPath(outputRoot, fixture, "actual.bytecode");

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

            assertEquals(expected, actual, "BYTECODE snapshot mismatch for " + fixture.sourceFile(), failures);
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
            Path outputFile = createOutputPath(outputRoot, fixture, "diagnostics.bytecode");

            List<String> compilerArgs = List.of(
                    "--in-file", fixture.sourceFile().toString(),
                    "--out-file", outputFile.toString(),
                    "--no-logging"
            );

            RunResult result = runCompilerInProcess(compilerArgs);

            String expected = readNormalized(fixture.diagnosticsFile());
            String actual = normalize(result.stderr());

            assertEquals(expected, actual, "DIAGNOSTICS snapshot mismatch for " + fixture.sourceFile(), failures);
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
            Path outputFile = createOutputPath(outputRoot, fixture, "pipeline.bytecode");

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

            assertEquals(expected, actual, "PIPELINE snapshot mismatch for " + fixture.sourceFile(), failures);
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

    private static void assertEquals(
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

    private record RunResult(String stdout, String stderr, int exitCode) {
    }

    private record Fixture(Path sourceFile, String baseName) {

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
            return sibling(".bytecode");
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