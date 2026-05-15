package ch.zhaw.it.pm4.javer.application;

import ch.zhaw.it.pm4.misc.JaverLogger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class GuiController {

    private static final String CONSOLE_INPUT_FILE_NAME = "console-input.javer";
    private static final String VM_INPUT_FILE_NAME = "vm-input.jbc";
    private static final String BYTECODE_FILE_EXTENSION = ".jbc";

    // Packaged release layout inside Application app image
    private static final Path RELEASE_COMPILER_EXE = Path.of("app", "tools", "Compiler", "javer-compiler.exe");
    private static final Path RELEASE_VM_EXE = Path.of("app", "tools", "VM", "javer-vm.exe");

    private final Path runtimeDirectory = resolveRuntimeDirectory();
    private final Path consoleInputFile = runtimeDirectory.resolve(CONSOLE_INPUT_FILE_NAME);
    private final Path vmInputFile = runtimeDirectory.resolve(VM_INPUT_FILE_NAME);

    private ManagedProcessRunner compilerRunner;
    private ManagedProcessRunner vmRunner;

    @FXML
    public Button runVMButton;

    @FXML
    public Button stopCompilerButton;

    @FXML
    public Button stopVMButton;

    @FXML
    public Button runCompilerAndVMButton;

    @FXML
    private TextArea compilerOutput;

    @FXML
    private TextArea virtualMachineOutput;

    @FXML
    private TextArea consoleInput;

    @FXML
    private Button runCompilerButton;

    @FXML
    private TextArea statusOutput;

    @FXML
    public void initialize() {
        GuiLogAppender.setConsumer(this::appendStatus);

        JaverLogger.info("GUI initialized");
        JaverLogger.info("Working directory: " + Path.of("").toAbsolutePath().normalize());
        JaverLogger.info("Runtime file directory: " + runtimeDirectory);

        compilerRunner = new ManagedProcessRunner(
                "Compiler",
                this::appendCompilerOutput,
                this::appendCompilerOutput,
                this::updateCompilerButtons
        );

        vmRunner = new ManagedProcessRunner(
                "VM",
                this::appendVMOutput,
                this::appendVMOutput,
                this::updateVMButtons
        );

        updateCompilerButtons(false);
        updateVMButtons(false);
    }

    @FXML
    protected void onRunCompilerClick() {
        if (compilerRunner.isRunning()) {
            return;
        }

        compilerOutput.clear();

        String inputPath = writeInputToFile(consoleInput.getText());
        if (inputPath == null || !deleteFileIfExists(vmInputFile, "VM input file")) {
            return;
        }

        List<String> command = buildCompilerCommand(inputPath);
        if (command == null) {
            return;
        }

        logCommand("Compiler", command);

        if (compilerRunner.start(command).isEmpty()) {
            JaverLogger.warning("Compiler is already running.");
        }
    }

    @FXML
    protected void onRunVMClick() {
        if (vmRunner.isRunning()) {
            return;
        }

        virtualMachineOutput.clear();

        List<String> command = buildVmCommand();
        if (command == null) {
            return;
        }

        logCommand("VM", command);

        if (vmRunner.start(command).isEmpty()) {
            JaverLogger.warning("VM is already running.");
        }
    }

    @FXML
    public void onStopVMClick() {
        vmRunner.stop();
    }

    @FXML
    public void onStopCompilerClick() {
        compilerRunner.stop();
    }

    public void shutdown() {
        JaverLogger.info("Application shutdown started.");

        if (compilerRunner != null) {
            compilerRunner.stopAndWait();
        }
        if (vmRunner != null) {
            vmRunner.stopAndWait();
        }

        deleteRuntimeFileIfExists(consoleInputFile, "source input file");
        deleteRuntimeFileIfExists(vmInputFile, "VM input file");
        GuiLogAppender.clearConsumer();
    }

    @FXML
    public void onRunCompilerAndVMClick() {
        if (compilerRunner.isRunning() || vmRunner.isRunning()) {
            JaverLogger.warning("Compiler or VM is already running.");
            return;
        }

        compilerOutput.clear();
        virtualMachineOutput.clear();

        String inputPath = writeInputToFile(consoleInput.getText());
        if (inputPath == null || !deleteFileIfExists(vmInputFile, "VM input file")) {
            return;
        }

        List<String> compilerCommand = buildCompilerCommand(inputPath);
        List<String> vmCommand = buildVmCommand();

        if (compilerCommand == null || vmCommand == null) {
            return;
        }

        logCommand("Compiler", compilerCommand);

        var compilerRun = compilerRunner.start(compilerCommand);
        if (compilerRun.isEmpty()) {
            JaverLogger.warning("Compiler is already running.");
            return;
        }

        compilerRun.get().thenAccept(result -> startVmAfterSuccessfulCompilation(result, vmCommand));
    }

    private List<String> buildCompilerCommand(String inputPath) {
        String compilerOutputPath = bytecodeOutputBasePath(vmInputFile).toAbsolutePath().toString();

        Path compilerExe = resolveReleaseExecutable(RELEASE_COMPILER_EXE, "Compiler");
        if (compilerExe != null) {
            return List.of(
                    compilerExe.toString(),
                    inputPath,
                    compilerOutputPath
            );
        }

        Path compilerJar = resolveJarFromProperty("javer.compiler.jar");
        if (compilerJar == null) {
            JaverLogger.error("Compiler executable and IDE jar are both unavailable.");
            return null;
        }

        return List.of(
                "java",
                "-jar",
                compilerJar.toString(),
                inputPath,
                compilerOutputPath
        );
    }

    private Path bytecodeOutputBasePath(Path bytecodeFile) {
        String fileName = bytecodeFile.getFileName().toString();
        if (!fileName.endsWith(BYTECODE_FILE_EXTENSION)) {
            return bytecodeFile;
        }

        String baseName = fileName.substring(0, fileName.length() - BYTECODE_FILE_EXTENSION.length());
        return bytecodeFile.resolveSibling(baseName);
    }

    private List<String> buildVmCommand() {
        Path vmExe = resolveReleaseExecutable(RELEASE_VM_EXE, "VM");
        if (vmExe != null) {
            return List.of(
                    vmExe.toString(),
                    vmInputFile.toAbsolutePath().toString()
            );
        }

        Path vmJar = resolveJarFromProperty("javer.vm.jar");
        if (vmJar == null) {
            JaverLogger.error("VM executable and IDE jar are both unavailable.");
            return null;
        }

        return List.of(
                "java",
                "-jar",
                vmJar.toString(),
                vmInputFile.toAbsolutePath().toString()
        );
    }

    private void startVmAfterSuccessfulCompilation(
            ManagedProcessRunner.ProcessResult compilerResult,
            List<String> vmCommand
    ) {
        if (!compilerResult.isSuccess()) {
            JaverLogger.warning("Compiler did not finish successfully. VM will not be started.");
            return;
        }

        if (!isBytecodeFileReady()) {
            JaverLogger.error("Compiler finished, but the VM input file is missing or empty: "
                    + vmInputFile.toAbsolutePath());
            return;
        }

        if (vmRunner.isRunning()) {
            JaverLogger.warning("VM is already running.");
            return;
        }

        logCommand("VM", vmCommand);
        if (vmRunner.start(vmCommand).isEmpty()) {
            JaverLogger.warning("VM is already running.");
        }
    }

    private boolean isBytecodeFileReady() {
        try {
            return Files.isRegularFile(vmInputFile) && Files.size(vmInputFile) > 0;
        } catch (IOException exception) {
            JaverLogger.error("Failed to inspect VM input file: " + exception.getMessage());
            return false;
        }
    }

    private String writeInputToFile(String input) {
        String content = input == null ? "" : input;

        try {
            Files.writeString(
                    consoleInputFile,
                    content,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            JaverLogger.info("Created source input file: " + consoleInputFile.toAbsolutePath());
            return consoleInputFile.toAbsolutePath().toString();
        } catch (IOException exception) {
            JaverLogger.error("Failed to write source input file: " + exception.getMessage());
            return null;
        }
    }

    private boolean deleteFileIfExists(Path path, String label) {
        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.deleteIfExists(path)) {
                JaverLogger.info("Deleted previous " + label + ": " + path.toAbsolutePath());
            }
            return true;
        } catch (IOException e) {
            JaverLogger.error("Failed to delete previous " + label + ": " + e.getMessage());
            return false;
        }
    }

    private void deleteRuntimeFileIfExists(Path path, String label) {
        try {
            if (Files.deleteIfExists(path)) {
                JaverLogger.info("Deleted " + label + ": " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            JaverLogger.warning("Failed to delete " + label + ": " + e.getMessage());
        }
    }

    private Path resolveReleaseExecutable(Path relativePath, String label) {
        Path path = runtimeDirectory.resolve(relativePath).toAbsolutePath().normalize();

        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            JaverLogger.debug(label + " packaged executable not found at: " + path);
            return null;
        }

        JaverLogger.info("Resolved " + label + " executable to: " + path);
        return path;
    }

    private static Path resolveRuntimeDirectory() {
        Path workingDirectory = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path codeLocation = getCodeLocation();
        Path packagedRoot = findPackagedAppRoot(codeLocation);
        if (packagedRoot != null) {
            return packagedRoot;
        }

        Path projectRoot = findProjectRoot(workingDirectory);
        if (projectRoot != null) {
            return projectRoot;
        }

        projectRoot = findProjectRoot(codeLocation);
        if (projectRoot != null) {
            return projectRoot;
        }

        return workingDirectory;
    }

    private static Path getCodeLocation() {
        try {
            return Path.of(GuiController.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()).toAbsolutePath().normalize();
        } catch (Exception exception) {
            return Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        }
    }

    private static Path findPackagedAppRoot(Path start) {
        Path path = Files.isRegularFile(start) ? start.getParent() : start;
        while (path != null) {
            Path fileName = path.getFileName();
            if (fileName != null
                    && "app".equalsIgnoreCase(fileName.toString())
                    && Files.isDirectory(path.resolve("tools"))) {
                return path.getParent();
            }
            path = path.getParent();
        }
        return null;
    }

    private static Path findProjectRoot(Path start) {
        Path path = Files.isRegularFile(start) ? start.getParent() : start;
        while (path != null) {
            if (Files.isRegularFile(path.resolve("pom.xml"))
                    && Files.isDirectory(path.resolve("Application"))
                    && Files.isDirectory(path.resolve("Compiler"))
                    && Files.isDirectory(path.resolve("VM"))) {
                return path;
            }
            path = path.getParent();
        }
        return null;
    }

    private Path resolveJarFromProperty(String propertyName) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            JaverLogger.error("Missing system property: " + propertyName);
            JaverLogger.error("Please ensure JarConfigLoader.loadConfiguration() is called at startup or set the property manually.");
            return null;
        }

        Path path = Path.of(value).toAbsolutePath().normalize();
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            JaverLogger.error("Configured jar does not exist: " + path);
            JaverLogger.error("Make sure the jar file is present at the expected location.");
            JaverLogger.error("Property '" + propertyName + "' is set to: " + value);
            return null;
        }

        JaverLogger.info("Resolved " + propertyName + " to: " + path.toAbsolutePath());
        return path;
    }

    private void logCommand(String processName, List<String> command) {
        JaverLogger.info("Starting " + processName + " with command:");
        for (String part : command) {
            JaverLogger.info("  " + part);
        }
    }

    private void updateCompilerButtons(boolean running) {
        Platform.runLater(() -> {
            runCompilerButton.setDisable(running);
            stopCompilerButton.setDisable(!running);
            updateRunCompilerAndVMButtonState();
        });
    }

    private void updateVMButtons(boolean running) {
        Platform.runLater(() -> {
            runVMButton.setDisable(running);
            stopVMButton.setDisable(!running);
            updateRunCompilerAndVMButtonState();
        });
    }

    private void updateRunCompilerAndVMButtonState() {
        runCompilerAndVMButton.setDisable(isRunnerRunning(compilerRunner) || isRunnerRunning(vmRunner));
    }

    private boolean isRunnerRunning(ManagedProcessRunner runner) {
        return runner != null && runner.isRunning();
    }

    private void appendCompilerOutput(String text) {
        Platform.runLater(() -> compilerOutput.appendText(text));
    }

    private void appendVMOutput(String text) {
        Platform.runLater(() -> virtualMachineOutput.appendText(text));
    }

    private void appendStatus(String text) {
        Platform.runLater(() -> {
            statusOutput.appendText(text);
            statusOutput.setScrollTop(Double.MAX_VALUE);
        });
    }
}
