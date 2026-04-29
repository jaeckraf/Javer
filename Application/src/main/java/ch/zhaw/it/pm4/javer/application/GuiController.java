package ch.zhaw.it.pm4.javer.application;

import ch.zhaw.it.pm4.misc.JaverLogger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
        createOrClearFile(vmInputFile, "VM input file");

        List<String> command = buildCompilerCommand(inputPath);
        if (command == null) {
            return;
        }

        logCommand("Compiler", command);

        boolean started = compilerRunner.start(command);

        if (!started) {
            JaverLogger.error("Compiler is already running.\n");
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

        boolean started = vmRunner.start(command);

        if (!started) {
            JaverLogger.error("VM is already running.\n");
        }
    }

    @FXML
    public void onStopVMClick(ActionEvent actionEvent) {
        vmRunner.stop();
    }

    @FXML
    public void onStopCompilerClick(ActionEvent actionEvent) {
        compilerRunner.stop();
    }

    @FXML
    public void onRunCompilerAndVMClick(ActionEvent actionEvent) {
        if (compilerRunner.isRunning() || vmRunner.isRunning()) {
            JaverLogger.error("Compiler or VM is already running.\n");
            return;
        }

        compilerOutput.clear();
        virtualMachineOutput.clear();

        String inputPath = writeInputToFile(consoleInput.getText());
        createOrClearFile(vmInputFile, "VM input file");

        List<String> compilerCommand = buildCompilerCommand(inputPath);
        List<String> vmCommand = buildVmCommand();

        if (compilerCommand == null || vmCommand == null) {
            return;
        }

        Thread chainThread = new Thread(() -> {
            logCommand("Compiler", compilerCommand);

            boolean compilerStarted = compilerRunner.start(compilerCommand);

            if (!compilerStarted) {
                JaverLogger.error("Compiler is already running.\n");
                return;
            }

            waitUntilFinished(compilerRunner);

            if (vmRunner.isRunning()) {
                JaverLogger.error("VM is already running.\n");
                return;
            }

            logCommand("VM", vmCommand);
            vmRunner.start(vmCommand);
        }, "compiler-vm-chain");

        chainThread.setDaemon(true);
        chainThread.start();
    }

    private List<String> buildCompilerCommand(String inputPath) {
        Path compilerExe = resolveReleaseExecutable(RELEASE_COMPILER_EXE, "Compiler");
        if (compilerExe != null) {
            return List.of(
                    compilerExe.toString(),
                    inputPath,
                    vmInputFile.toAbsolutePath().toString()
            );
        }

        Path compilerJar = resolveJarFromProperty("javer.compiler.jar");
        if (compilerJar == null) {
            JaverLogger.error("Compiler executable and IDE jar are both unavailable.\n");
            return null;
        }

        return List.of(
                "java",
                "-jar",
                compilerJar.toString(),
                inputPath,
                vmInputFile.toAbsolutePath().toString()
        );
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
            JaverLogger.error("VM executable and IDE jar are both unavailable.\n");
            return null;
        }

        return List.of(
                "java",
                "-jar",
                vmJar.toString(),
                vmInputFile.toAbsolutePath().toString()
        );
    }

    private void waitUntilFinished(ManagedProcessRunner runner) {
        while (runner.isRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                JaverLogger.error("Chained execution interrupted.\n");
                return;
            }
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
            JaverLogger.error("Created source input file: " + consoleInputFile.toAbsolutePath() + "\n");
        } catch (IOException exception) {
            JaverLogger.error("Failed to write source input file: " + exception.getMessage() + "\n");
        }

        return consoleInputFile.toAbsolutePath().toString();
    }

    private void createOrClearFile(Path path, String label) {
        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(
                    path,
                    "",
                    StandardCharsets.US_ASCII,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            JaverLogger.error("Created/cleared " + label + ": " + path.toAbsolutePath() + "\n");
        } catch (IOException e) {
            JaverLogger.error("Failed to create/clear " + label + ": " + e.getMessage() + "\n");
        }
    }

    private Path resolveReleaseExecutable(Path relativePath, String label) {
        Path path = runtimeDirectory.resolve(relativePath).toAbsolutePath().normalize();

        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            JaverLogger.info(label + " packaged executable not found at: " + path);
            return null;
        }

        JaverLogger.error("Resolved " + label + " executable to: " + path + "\n");
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
            JaverLogger.error("ERROR: Missing system property: " + propertyName + "\n");
            JaverLogger.error("Please ensure JarConfigLoader.loadConfiguration() is called at startup or set the property manually.\n");
            return null;
        }

        Path path = Path.of(value).toAbsolutePath().normalize();
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            JaverLogger.error("ERROR: Configured jar does not exist: " + path + "\n");
            JaverLogger.error("Make sure the jar file is present at the expected location.\n");
            JaverLogger.error("Property '" + propertyName + "' is set to: " + value + "\n");
            return null;
        }

        JaverLogger.error("Resolved " + propertyName + " to: " + path.toAbsolutePath() + "\n");
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
            runCompilerAndVMButton.setDisable(running);
        });
    }

    private void updateVMButtons(boolean running) {
        Platform.runLater(() -> {
            runVMButton.setDisable(running);
            stopVMButton.setDisable(!running);
        });
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
