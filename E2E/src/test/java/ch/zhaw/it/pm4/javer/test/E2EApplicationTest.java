package ch.zhaw.it.pm4.javer.test;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class E2EApplicationTest {

    private static final Path DEFAULT_CASE_ROOT = defaultCaseRoot();
    private static final Path DEFAULT_OUTPUT_ROOT = Path.of("target", "e2e-test-output");

    private static final String DEFAULT_COMPILER_MAIN_CLASS =
            "ch.zhaw.it.pm4.javer.compiler.Compiler";

    private static final String DEFAULT_VM_MAIN_CLASS =
            "ch.zhaw.it.pm4.javer.vm.VM";

    private static final long DEFAULT_TIMEOUT_SECONDS = 10;

    public static void main(String[] args) throws Exception {
        int exitCode = run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    @Test
    void runE2EWithMaven() throws Exception {
        assertEquals(0, run(new String[0]));
    }

    public static int run(String[] args) throws Exception {
        Path caseRoot = args.length >= 1
                ? Path.of(args[0])
                : Path.of(System.getProperty("e2e.resources", DEFAULT_CASE_ROOT.toString()));

        Path outputRoot = args.length >= 2
                ? Path.of(args[1])
                : Path.of(System.getProperty("e2e.output", DEFAULT_OUTPUT_ROOT.toString()));

        long timeoutSeconds = Long.parseLong(
                System.getProperty("e2e.timeout.seconds", String.valueOf(DEFAULT_TIMEOUT_SECONDS))
        );

        if (!Files.isDirectory(caseRoot)) {
            throw new IllegalArgumentException("E2E case directory does not exist: " + caseRoot);
        }

        Files.createDirectories(outputRoot);

        List<Path> caseDirectories = discoverCaseDirectories(caseRoot);

        List<CaseResult> results = new ArrayList<>();

        for (Path caseDirectory : caseDirectories) {
            CaseResult result = runCase(caseRoot, caseDirectory, outputRoot, timeoutSeconds);
            results.add(result);

            if (result.passed()) {
                System.out.println("[PASS] " + result.caseName());
            } else {
                System.out.println("[FAIL] " + result.caseName());
            }
        }

        System.out.println();
        System.out.println("E2E tests executed: " + results.size());

        long failedCount = results.stream().filter(result -> !result.passed()).count();

        if (failedCount > 0) {
            System.err.println();
            System.err.println("E2E test failures: " + failedCount);

            for (CaseResult result : results) {
                if (result.passed()) {
                    continue;
                }

                System.err.println();
                System.err.println("════════════════════════════════════════");
                System.err.println("FAILED CASE: " + result.caseName());
                System.err.println("TYPE: " + result.testType());
                System.err.println("════════════════════════════════════════");

                for (String failure : result.failures()) {
                    System.err.println();
                    System.err.println(failure);
                }
            }

            return 1;
        }

        System.out.println();
        System.out.println("All E2E tests passed.");
        return 0;
    }

    private static CaseResult runCase(
            Path caseRoot,
            Path caseDirectory,
            Path outputRoot,
            long timeoutSeconds
    ) {
        String caseName = caseRoot.relativize(caseDirectory).toString().replace('\\', '/');
        List<String> failures = new ArrayList<>();

        try {
            Path sourceInput = caseDirectory.resolve("input.jv");
            Path bytecodeInput = caseDirectory.resolve("input.bytecode");

            boolean hasSourceInput = Files.exists(sourceInput);
            boolean hasBytecodeInput = Files.exists(bytecodeInput);

            if (!hasSourceInput && !hasBytecodeInput) {
                failures.add("Case has neither input.jv nor input.bytecode");
                return new CaseResult(caseName, "INVALID", failures);
            }

            if (hasSourceInput) {
                return runCompilerAndVmCase(
                        caseDirectory,
                        outputRoot,
                        timeoutSeconds,
                        caseName,
                        sourceInput,
                        failures
                );
            }

            return runVmOnlyCase(
                    caseDirectory,
                    timeoutSeconds,
                    caseName,
                    bytecodeInput,
                    failures
            );
        } catch (Exception exception) {
            failures.add("Case crashed: " + exception.getMessage());
            return new CaseResult(caseName, "CRASHED", failures);
        }
    }

    private static CaseResult runCompilerAndVmCase(
            Path caseDirectory,
            Path outputRoot,
            long timeoutSeconds,
            String caseName,
            Path sourceInput,
            List<String> failures
    ) throws Exception {
        Path caseOutputDirectory = outputRoot.resolve(caseName);
        Files.createDirectories(caseOutputDirectory);

        Path generatedBytecodeFile = caseOutputDirectory.resolve("output.bytecode");

        RunResult compilerResult = runCompilerProcess(sourceInput, generatedBytecodeFile, timeoutSeconds);

        compareOptionalFile(
                caseDirectory.resolve("expected.compiler.stdout"),
                compilerResult.stdout(),
                caseName + " compiler stdout mismatch",
                failures
        );

        compareOptionalFile(
                caseDirectory.resolve("expected.compiler.stderr"),
                compilerResult.stderr(),
                caseName + " compiler stderr mismatch",
                failures
        );

        compareOptionalFile(
                caseDirectory.resolve("expected.diagnostics"),
                compilerResult.stderr(),
                caseName + " diagnostics mismatch",
                failures
        );

        compareOptionalExitFile(
                caseDirectory.resolve("expected.compiler.exit"),
                compilerResult.exitCode(),
                caseName + " compiler exit mismatch",
                failures
        );

        boolean expectsDiagnostics = Files.exists(caseDirectory.resolve("expected.diagnostics"));
        boolean compilerFailed = compilerResult.exitCode() != 0 || !compilerResult.stderr().isBlank();

        RunResult vmResult = new RunResult("", "", compilerResult.exitCode());

        if (!expectsDiagnostics && !compilerFailed) {
            if (!Files.exists(generatedBytecodeFile)) {
                failures.add(caseName + " compiler did not create bytecode file: " + generatedBytecodeFile);
                return new CaseResult(caseName, "COMPILER+VM", failures);
            }

            vmResult = runVmProcess(generatedBytecodeFile, timeoutSeconds);

            int failuresBeforeVmStdout = failures.size();

            compareOptionalFile(
                    caseDirectory.resolve("expected.vm.stdout"),
                    vmResult.stdout(),
                    caseName + " VM stdout mismatch",
                    failures
            );

            if (failures.size() > failuresBeforeVmStdout) {
                failures.add(caseName + " generated bytecode:"
                        + System.lineSeparator()
                        + readFileIfExists(generatedBytecodeFile));
            }

            compareOptionalFile(
                    caseDirectory.resolve("expected.vm.stderr"),
                    vmResult.stderr(),
                    caseName + " VM stderr mismatch",
                    failures
            );

            compareOptionalExitFile(
                    caseDirectory.resolve("expected.vm.exit"),
                    vmResult.exitCode(),
                    caseName + " VM exit mismatch",
                    failures
            );
        }

        int overallExit = compilerFailed ? compilerResult.exitCode() : vmResult.exitCode();

        compareOptionalExitFile(
                caseDirectory.resolve("expected.exit"),
                overallExit,
                caseName + " overall exit mismatch",
                failures
        );

        return new CaseResult(caseName, "COMPILER+VM", failures);
    }

    private static CaseResult runVmOnlyCase(
            Path caseDirectory,
            long timeoutSeconds,
            String caseName,
            Path bytecodeInput,
            List<String> failures
    ) throws Exception {
        RunResult vmResult = runVmProcess(bytecodeInput, timeoutSeconds);

        int failuresBeforeVmStdout = failures.size();

        compareOptionalFile(
                caseDirectory.resolve("expected.vm.stdout"),
                vmResult.stdout(),
                caseName + " VM stdout mismatch",
                failures
        );

        if (failures.size() > failuresBeforeVmStdout) {
            failures.add(caseName + " input bytecode:"
                    + System.lineSeparator()
                    + readFileIfExists(bytecodeInput));
        }

        compareOptionalFile(
                caseDirectory.resolve("expected.vm.stderr"),
                vmResult.stderr(),
                caseName + " VM stderr mismatch",
                failures
        );

        compareOptionalExitFile(
                caseDirectory.resolve("expected.vm.exit"),
                vmResult.exitCode(),
                caseName + " VM exit mismatch",
                failures
        );

        compareOptionalExitFile(
                caseDirectory.resolve("expected.exit"),
                vmResult.exitCode(),
                caseName + " overall exit mismatch",
                failures
        );

        return new CaseResult(caseName, "VM-ONLY", failures);
    }

    private static List<Path> discoverCaseDirectories(Path caseRoot) throws Exception {
        try (Stream<Path> stream = Files.walk(caseRoot)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String fileName = path.getFileName().toString();
                        return fileName.equals("input.jv") || fileName.equals("input.bytecode");
                    })
                    .map(Path::getParent)
                    .distinct()
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
        }
    }

    private static RunResult runCompilerProcess(
            Path inputFile,
            Path bytecodeFile,
            long timeoutSeconds
    ) throws Exception {
        List<String> command = commandFromProperty(
                "e2e.compiler.command",
                "e2e.compiler.jar",
                "e2e.compiler.mainClass",
                DEFAULT_COMPILER_MAIN_CLASS
        );

        command.add("--in-file");
        command.add(inputFile.toString());
        command.add("--out-file");
        command.add(bytecodeFile.toString());
        command.add("--no-logging");

        return runProcess(command, timeoutSeconds);
    }

    private static RunResult runVmProcess(
            Path bytecodeFile,
            long timeoutSeconds
    ) throws Exception {
        List<String> command = commandFromProperty(
                "e2e.vm.command",
                "e2e.vm.jar",
                "e2e.vm.mainClass",
                DEFAULT_VM_MAIN_CLASS
        );

        command.add(bytecodeFile.toString());

        return runProcess(command, timeoutSeconds);
    }

    private static List<String> commandFromProperty(
            String commandProperty,
            String jarProperty,
            String mainClassProperty,
            String defaultMainClass
    ) {
        String explicitCommand = System.getProperty(commandProperty);
        if (explicitCommand != null && !explicitCommand.isBlank()) {
            return new ArrayList<>(splitCommand(explicitCommand));
        }

        String jarPath = System.getProperty(jarProperty);
        if (jarPath != null && !jarPath.isBlank()) {
            return new ArrayList<>(List.of(
                    javaExecutable(),
                    "-jar",
                    jarPath
            ));
        }

        String mainClass = System.getProperty(mainClassProperty, defaultMainClass);

        return new ArrayList<>(List.of(
                javaExecutable(),
                "-cp",
                System.getProperty("java.class.path"),
                mainClass
        ));
    }

    private static RunResult runProcess(List<String> command, long timeoutSeconds) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        Process process = processBuilder.start();

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        Thread stdoutThread = copyAsync(process.getInputStream(), stdout);
        Thread stderrThread = copyAsync(process.getErrorStream(), stderr);

        boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly();
            throw new IllegalStateException("Process timed out: " + String.join(" ", command));
        }

        stdoutThread.join();
        stderrThread.join();

        return new RunResult(
                stdout.toString(StandardCharsets.UTF_8),
                stderr.toString(StandardCharsets.UTF_8),
                process.exitValue()
        );
    }

    private static Thread copyAsync(InputStream inputStream, ByteArrayOutputStream outputStream) {
        Thread thread = new Thread(() -> {
            try (inputStream; outputStream) {
                inputStream.transferTo(outputStream);
            } catch (Exception ignored) {
                // Process-level failures are reported by the caller.
            }
        });

        thread.setDaemon(true);
        thread.start();

        return thread;
    }

    private static void compareOptionalFile(
            Path expectedFile,
            String actual,
            String message,
            List<String> failures
    ) throws Exception {
        if (!Files.exists(expectedFile)) {
            return;
        }

        String expected = readNormalized(expectedFile);
        String normalizedActual = normalize(actual);

        if (!Objects.equals(expected, normalizedActual)) {
            failures.add(message
                    + System.lineSeparator()
                    + "--- expected ---"
                    + System.lineSeparator()
                    + expected
                    + System.lineSeparator()
                    + "--- actual ---"
                    + System.lineSeparator()
                    + normalizedActual);
        }
    }

    private static void compareOptionalExitFile(
            Path expectedExitFile,
            int actualExit,
            String message,
            List<String> failures
    ) throws Exception {
        if (!Files.exists(expectedExitFile)) {
            return;
        }

        String expectedText = readNormalized(expectedExitFile);
        int expectedExit = Integer.parseInt(expectedText);

        if (expectedExit != actualExit) {
            failures.add(message + ": expected " + expectedExit + ", actual " + actualExit);
        }
    }

    private static String readNormalized(Path file) throws Exception {
        return normalize(Files.readString(file, StandardCharsets.UTF_8));
    }

    private static String readFileIfExists(Path file) {
        try {
            if (!Files.exists(file)) {
                return "<file does not exist: " + file + ">";
            }
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            return "<could not read file: " + file + ">";
        }
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

    private static String javaExecutable() {
        Path javaHome = Path.of(System.getProperty("java.home"));
        Path java = javaHome.resolve("bin").resolve(isWindows() ? "java.exe" : "java");
        return java.toString();
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private static List<String> splitCommand(String command) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = 0; i < command.length(); i++) {
            char character = command.charAt(i);

            if (character == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
                continue;
            }

            if (character == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
                continue;
            }

            if (Character.isWhitespace(character) && !inSingleQuote && !inDoubleQuote) {
                if (!current.isEmpty()) {
                    parts.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }

            current.append(character);
        }

        if (!current.isEmpty()) {
            parts.add(current.toString());
        }

        return parts;
    }

    private static Path defaultCaseRoot() {
        Path fromProjectRootMain = Path.of("E2E", "src", "main", "resources", "cases");
        if (Files.isDirectory(fromProjectRootMain)) {
            return fromProjectRootMain;
        }

        Path fromModuleRootMain = Path.of("src", "main", "resources", "cases");
        if (Files.isDirectory(fromModuleRootMain)) {
            return fromModuleRootMain;
        }

        Path fromProjectRootTest = Path.of("E2E", "src", "test", "resources", "cases");
        if (Files.isDirectory(fromProjectRootTest)) {
            return fromProjectRootTest;
        }

        return Path.of("src", "test", "resources", "cases");
    }

    private record RunResult(String stdout, String stderr, int exitCode) {
    }

    private record CaseResult(String caseName, String testType, List<String> failures) {
        boolean passed() {
            return failures.isEmpty();
        }
    }
}