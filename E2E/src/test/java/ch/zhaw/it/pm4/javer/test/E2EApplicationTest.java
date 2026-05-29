package ch.zhaw.it.pm4.javer.test;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class E2EApplicationTest {

    private static final Path DEFAULT_CASE_ROOT = defaultCaseRoot();
    private static final Path DEFAULT_OUTPUT_ROOT = Path.of("target", "e2e-output");

    private static final String DEFAULT_COMPILER_MAIN_CLASS =
            "ch.zhaw.it.pm4.javer.compiler.Compiler";

    private static final String DEFAULT_VM_MAIN_CLASS =
            "ch.zhaw.it.pm4.javer.vm.VM";

    private static final String COMPILER_MODULE = "Compiler";
    private static final String MISC_MODULE = "Misc";
    private static final String VM_MODULE = "VM";

    private static final List<String> PROJECT_MODULES = List.of(
            MISC_MODULE,
            COMPILER_MODULE,
            VM_MODULE,
            "Application",
            "E2E"
    );

    private static final String INPUT_SOURCE_FILE = "input.javer";
    private static final String COMPILER_STDOUT_FILE = "expected.compiler.stdout";
    private static final String COMPILER_STDERR_FILE = "expected.compiler.stderr";
    private static final String VM_STDOUT_FILE = "expected.vm.stdout";
    private static final String VM_STDERR_FILE = "expected.vm.stderr";
    private static final String ACTUAL_BYTECODE_FILE = "output.jbc";
    private static final String EXPECTED_BYTECODE_FILE = "expected_output.jbc";
    private static final String COMPILATION_SUCCESSFUL = "Compilation Successful";
    private static final String BYTECODE_FILE_EXTENSION = ".jbc";

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
                System.err.println("========================================");
                System.err.println("FAILED CASE: " + result.caseName());
                System.err.println("TYPE: " + result.testType());
                System.err.println("========================================");

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
            requireFiles(
                    caseDirectory,
                    failures,
                    INPUT_SOURCE_FILE,
                    COMPILER_STDOUT_FILE,
                    COMPILER_STDERR_FILE
            );

            if (!failures.isEmpty()) {
                return new CaseResult(caseName, "INVALID", failures);
            }

            boolean expectsCompilerSuccess = expectsCompilerSuccess(caseDirectory);

            if (expectsCompilerSuccess) {
                requireFiles(
                        caseDirectory,
                        failures,
                        VM_STDOUT_FILE,
                        VM_STDERR_FILE,
                        EXPECTED_BYTECODE_FILE
                );
            }

            if (!failures.isEmpty()) {
                return new CaseResult(caseName, "INVALID", failures);
            }

            return runCompilerAndMaybeVmCase(
                    caseDirectory,
                    outputRoot,
                    timeoutSeconds,
                    caseName,
                    expectsCompilerSuccess,
                    failures
            );
        } catch (Exception exception) {
            failures.add("Case crashed: " + exception.getMessage());
            return new CaseResult(caseName, "CRASHED", failures);
        }
    }

    private static CaseResult runCompilerAndMaybeVmCase(
            Path caseDirectory,
            Path outputRoot,
            long timeoutSeconds,
            String caseName,
            boolean expectsCompilerSuccess,
            List<String> failures
    ) throws Exception {
        Path generatedBytecodeFile = generatedBytecodeFile(outputRoot, caseName);
        prepareGeneratedBytecodeFile(generatedBytecodeFile);

        RunResult compilerResult = runCompilerProcess(
                caseDirectory.resolve(INPUT_SOURCE_FILE),
                generatedBytecodeFile,
                timeoutSeconds
        );

        compareRequiredFile(
                caseDirectory.resolve(COMPILER_STDOUT_FILE),
                compilerResult.stdout(),
                caseName + " compiler stdout mismatch",
                failures
        );

        compareRequiredFile(
                caseDirectory.resolve(COMPILER_STDERR_FILE),
                compilerResult.stderr(),
                caseName + " compiler stderr mismatch",
                failures
        );

        if (!failures.isEmpty()) {
            return new CaseResult(caseName, expectsCompilerSuccess ? "COMPILER+VM" : "COMPILER-ERROR", failures);
        }

        if (!expectsCompilerSuccess) {
            if (compilerResult.exitCode() == 0) {
                failures.add(caseName + " compiler was expected to fail but exited with 0");
            }
            if (Files.exists(generatedBytecodeFile)) {
                failures.add(caseName + " compiler created bytecode although compilation was expected to fail: "
                        + generatedBytecodeFile);
            }
            return new CaseResult(caseName, "COMPILER-ERROR", failures);
        }

        boolean compilerActuallySucceeded =
                compilerResult.exitCode() == 0
                        && normalize(compilerResult.stdout()).equals(COMPILATION_SUCCESSFUL)
                        && normalize(compilerResult.stderr()).isBlank();

        if (!compilerActuallySucceeded) {
            failures.add(caseName + " compiler was expected to succeed, so VM was not started");
            return new CaseResult(caseName, "COMPILER+VM", failures);
        }

        if (!Files.exists(generatedBytecodeFile)) {
            failures.add(caseName + " compiler did not create bytecode file: " + generatedBytecodeFile);
            return new CaseResult(caseName, "COMPILER+VM", failures);
        }

        compareRequiredFile(
                caseDirectory.resolve(EXPECTED_BYTECODE_FILE),
                readFileIfExists(generatedBytecodeFile),
                caseName + " bytecode mismatch",
                failures
        );

        if (!failures.isEmpty()) {
            return new CaseResult(caseName, "COMPILER+VM", failures);
        }

        RunResult vmResult = runVmProcess(generatedBytecodeFile, timeoutSeconds);

        compareRequiredFile(
                caseDirectory.resolve(VM_STDOUT_FILE),
                vmResult.stdout(),
                caseName + " VM stdout mismatch",
                failures
        );

        compareRequiredFile(
                caseDirectory.resolve(VM_STDERR_FILE),
                vmResult.stderr(),
                caseName + " VM stderr mismatch",
                failures
        );

        if (vmResult.exitCode() != 0) {
            failures.add(caseName + " VM exited with non-zero code: " + vmResult.exitCode());
        }

        return new CaseResult(caseName, "COMPILER+VM", failures);
    }

    private static Path generatedBytecodeFile(Path outputRoot, String caseName) throws Exception {
        Path caseOutputDirectory = outputRoot.resolve(Path.of(caseName));
        Files.createDirectories(caseOutputDirectory);
        return caseOutputDirectory.resolve(ACTUAL_BYTECODE_FILE);
    }

    private static void prepareGeneratedBytecodeFile(Path generatedBytecodeFile) throws Exception {
        Path parent = generatedBytecodeFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.deleteIfExists(generatedBytecodeFile);
    }

    private static List<Path> discoverCaseDirectories(Path caseRoot) throws Exception {
        try (Stream<Path> stream = Files.walk(caseRoot)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals(INPUT_SOURCE_FILE))
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
                "e2e.compiler.classpath",
                "e2e.compiler.mainClass",
                DEFAULT_COMPILER_MAIN_CLASS,
                defaultCompilerClassPath()
        );

        command.add("--in-file");
        command.add(inputFile.toString());
        command.add("--out-file");
        command.add(bytecodeOutputBasePath(bytecodeFile).toString());

        return runProcess(command, timeoutSeconds);
    }

    private static RunResult runVmProcess(
            Path bytecodeFile,
            long timeoutSeconds
    ) throws Exception {
        List<String> command = commandFromProperty(
                "e2e.vm.command",
                "e2e.vm.jar",
                "e2e.vm.classpath",
                "e2e.vm.mainClass",
                DEFAULT_VM_MAIN_CLASS,
                defaultVmClassPath()
        );

        command.add(bytecodeFile.toString());

        return runProcess(command, timeoutSeconds);
    }

    private static List<String> commandFromProperty(
            String commandProperty,
            String jarProperty,
            String classPathProperty,
            String mainClassProperty,
            String defaultMainClass,
            String defaultClassPath
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
        String classPath = System.getProperty(classPathProperty);
        if (classPath == null || classPath.isBlank()) {
            classPath = defaultClassPath;
        }

        return new ArrayList<>(List.of(
                javaExecutable(),
                "-cp",
                classPath,
                mainClass
        ));
    }

    private static String defaultCompilerClassPath() {
        return isolatedClassPath(Set.of(COMPILER_MODULE, MISC_MODULE));
    }

    private static String defaultVmClassPath() {
        return isolatedClassPath(Set.of(VM_MODULE));
    }

    private static String isolatedClassPath(Set<String> includedProjectModules) {
        List<String> classPathEntries = splitClassPath(System.getProperty("java.class.path", ""));
        List<Path> projectRoots = stablePathRoots();
        List<String> filteredEntries = new ArrayList<>();
        Set<String> seenEntries = new LinkedHashSet<>();

        for (String classPathEntry : classPathEntries) {
            if (classPathEntry.isBlank() || !seenEntries.add(classPathEntry)) {
                continue;
            }

            ProjectClasspathEntry projectClasspathEntry = projectClasspathEntry(classPathEntry, projectRoots);
            if (projectClasspathEntry == null
                    || (includedProjectModules.contains(projectClasspathEntry.moduleName())
                    && projectClasspathEntry.runtimeEntry())) {
                filteredEntries.add(classPathEntry);
            }
        }

        return String.join(File.pathSeparator, filteredEntries);
    }

    private static List<String> splitClassPath(String classPath) {
        if (classPath == null || classPath.isBlank()) {
            return List.of();
        }

        return Stream.of(classPath.split(java.util.regex.Pattern.quote(File.pathSeparator)))
                .filter(entry -> !entry.isBlank())
                .toList();
    }

    private static ProjectClasspathEntry projectClasspathEntry(String classPathEntry, List<Path> projectRoots) {
        Path path;
        try {
            path = Path.of(classPathEntry).toAbsolutePath().normalize();
        } catch (InvalidPathException exception) {
            return null;
        }

        for (Path projectRoot : projectRoots) {
            for (String projectModule : PROJECT_MODULES) {
                Path moduleDirectory = projectRoot.resolve(projectModule).normalize();
                if (isSameOrChild(path, moduleDirectory)) {
                    return new ProjectClasspathEntry(
                            projectModule,
                            isRuntimeEntry(path, moduleDirectory, projectModule)
                    );
                }
            }
        }

        String artifactModule = projectArtifactModuleName(path);
        if (artifactModule != null) {
            return new ProjectClasspathEntry(artifactModule, true);
        }

        return null;
    }

    private static boolean isRuntimeEntry(Path path, Path moduleDirectory, String moduleName) {
        Path targetDirectory = moduleDirectory.resolve("target").normalize();
        Path testClassesDirectory = targetDirectory.resolve("test-classes").normalize();

        if (path.equals(testClassesDirectory) || path.startsWith(testClassesDirectory)) {
            return false;
        }

        return path.equals(targetDirectory.resolve("classes").normalize())
                || (path.startsWith(moduleDirectory)
                && isModuleJar(path, targetDirectory, moduleName));
    }

    private static boolean isModuleJar(Path path, Path targetDirectory, String moduleName) {
        Path parent = path.getParent();
        Path fileName = path.getFileName();
        if (parent == null || fileName == null || !parent.normalize().equals(targetDirectory)) {
            return false;
        }

        String name = fileName.toString();
        return name.startsWith(moduleName + "-")
                && name.endsWith(".jar")
                && !name.contains("-sources")
                && !name.contains("-javadoc")
                && !name.contains("-tests");
    }

    private static String projectArtifactModuleName(Path path) {
        Path fileName = path.getFileName();
        if (fileName == null) {
            return null;
        }

        String name = fileName.toString();
        for (String projectModule : PROJECT_MODULES) {
            if (name.startsWith(projectModule + "-")
                    && name.endsWith(".jar")
                    && isProjectArtifactPath(path, projectModule)) {
                return projectModule;
            }
        }

        return null;
    }

    private static boolean isProjectArtifactPath(Path path, String projectModule) {
        String normalizedPath = path.toString().replace('\\', '/');
        return normalizedPath.contains("/ch/zhaw/it/pm4/" + projectModule + "/");
    }

    private static boolean isSameOrChild(Path path, Path parent) {
        return path.equals(parent) || path.startsWith(parent);
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

    private static void requireFiles(Path caseDirectory, List<String> failures, String... fileNames) {
        for (String fileName : fileNames) {
            Path file = caseDirectory.resolve(fileName);
            if (!Files.isRegularFile(file)) {
                failures.add("Required case file is missing: " + file);
            }
        }
    }

    private static boolean expectsCompilerSuccess(Path caseDirectory) throws Exception {
        return readNormalized(caseDirectory.resolve(COMPILER_STDOUT_FILE)).equals(COMPILATION_SUCCESSFUL);
    }

    private static void compareRequiredFile(
            Path expectedFile,
            String actual,
            String message,
            List<String> failures
    ) throws Exception {
        if (!Files.exists(expectedFile)) {
            failures.add("Required expected file is missing: " + expectedFile);
            return;
        }

        String expected = readNormalized(expectedFile);
        String normalizedActual = normalize(actual);

        if (!Objects.equals(expected, normalizedActual)) {
            failures.add(message
                    + System.lineSeparator()
                    + System.lineSeparator()
                    + "[INFO] --- expected ---"
                    + System.lineSeparator()
                    + System.lineSeparator()
                    + expected
                    + System.lineSeparator()
                    + System.lineSeparator()
                    + "[INFO] --- actual ---"
                    + System.lineSeparator()
                    + System.lineSeparator()
                    + normalizedActual);
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

    private static Path bytecodeOutputBasePath(Path bytecodeFile) {
        String fileName = bytecodeFile.getFileName().toString();
        if (!fileName.endsWith(BYTECODE_FILE_EXTENSION)) {
            return bytecodeFile;
        }

        String baseName = fileName.substring(0, fileName.length() - BYTECODE_FILE_EXTENSION.length());
        return bytecodeFile.resolveSibling(baseName);
    }

    private static String normalize(String text) {
        if (text == null) {
            return "";
        }

        text = text
                .replace("\r\n", "\n")
                .replace('\r', '\n');

        // Remove JAVA_TOOL_OPTIONS and other JVM startup messages
        text = filterJavaToolMessages(text);

        return normalizePathReferences(text
                .replaceAll("[ \t]+\\n", "\n"))
                .strip();
    }

    private static String filterJavaToolMessages(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        String[] lines = text.split("\n", -1);

        for (String line : lines) {
            // Skip JVM startup messages
            if (line.contains("Picked up JAVA_TOOL_OPTIONS")
                    || line.startsWith("Picked up ")
                    || line.isEmpty()) {
                continue;
            }

            if (result.length() > 0) {
                result.append("\n");
            }
            result.append(line);
        }

        return result.toString();
    }

    private static String normalizePathReferences(String text) {
        String[] lines = text.split("\n", -1);
        StringBuilder normalized = new StringBuilder(text.length());

        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                normalized.append('\n');
            }

            normalized.append(normalizePathReferenceLine(lines[i]));
        }

        return normalized.toString();
    }

    private static String normalizePathReferenceLine(String line) {
        String normalized = normalizePathReferenceLine(line, "File: ");
        normalized = normalizePathReferenceLine(normalized, "  --> ");
        normalized = normalizePathReferenceLine(normalized, "Error reading file: ");
        return normalized;
    }

    private static String normalizePathReferenceLine(String line, String prefix) {
        if (!line.startsWith(prefix)) {
            return line;
        }

        return prefix + normalizePathReference(line.substring(prefix.length()));
    }

    private static String normalizePathReference(String pathReference) {
        String normalized = pathReference.replace('\\', '/');

        for (Path stableRoot : stablePathRoots()) {
            String root = stableRoot.toAbsolutePath().normalize().toString().replace('\\', '/');
            normalized = normalized.replace(root + "/", "");
        }

        if (normalized.startsWith("./")) {
            normalized = normalized.substring(2);
        }

        return normalized
                .replace("E2E/src/test/resources/", "src/test/resources/")
                .replace("E2E/target/", "target/");
    }

    private static List<Path> stablePathRoots() {
        Path userDirectory = Path.of("").toAbsolutePath().normalize();
        List<Path> roots = new ArrayList<>();
        roots.add(userDirectory);

        if (userDirectory.getFileName() != null && userDirectory.getFileName().toString().equals("E2E")) {
            roots.add(userDirectory.getParent());
        } else {
            roots.add(userDirectory.resolve("E2E"));
        }

        return roots.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
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
        Path fromProjectRootTestcases = Path.of("E2E", "src", "test", "resources", "testcases");
        if (Files.isDirectory(fromProjectRootTestcases)) {
            return fromProjectRootTestcases;
        }

        Path fromModuleRootTestcases = Path.of("src", "test", "resources", "testcases");
        if (Files.isDirectory(fromModuleRootTestcases)) {
            return fromModuleRootTestcases;
        }

        Path fromProjectRootCases = Path.of("E2E", "src", "test", "resources", "cases");
        if (Files.isDirectory(fromProjectRootCases)) {
            return fromProjectRootCases;
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

    private record ProjectClasspathEntry(String moduleName, boolean runtimeEntry) {
    }
}
