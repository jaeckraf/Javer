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

    private static final Path CONSOLE_INPUT_FILE = Path.of("console-input.txt");
    private static final Path VM_INPUT_FILE = Path.of("vm-input.txt");

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
        createOrClearFile(VM_INPUT_FILE, "VM input file");

        Path compilerJar = resolveJarFromProperty("javer.compiler.jar");
        if (compilerJar == null) {
            JaverLogger.error("Compiler jar not configured. Set system property 'javer.compiler.jar'.\n");
            return;
        }

        boolean started = compilerRunner.start(List.of(
                "java",
                "-jar",
                compilerJar.toString(),
                inputPath,
                VM_INPUT_FILE.toAbsolutePath().toString()
        ));

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
        createOrClearFile(VM_INPUT_FILE, "VM input file");

        Path vmJar = resolveJarFromProperty("javer.vm.jar");
        if (vmJar == null) {
            JaverLogger.error("VM jar not configured. Set system property 'javer.vm.jar'.\n");
            return;
        }

        boolean started = vmRunner.start(List.of(
                "java",
                "-jar",
                vmJar.toString(),
                VM_INPUT_FILE.toAbsolutePath().toString()
        ));

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
        createOrClearFile(VM_INPUT_FILE, "VM input file");

        Path compilerJar = resolveJarFromProperty("javer.compiler.jar");
        Path vmJar = resolveJarFromProperty("javer.vm.jar");

        if (compilerJar == null) {
            JaverLogger.error("Compiler jar not configured. Set system property 'javer.compiler.jar'.\n");
            return;
        }

        if (vmJar == null) {
            JaverLogger.error("VM jar not configured. Set system property 'javer.vm.jar'.\n");
            return;
        }

        Thread chainThread = new Thread(() -> {
            boolean compilerStarted = compilerRunner.start(List.of(
                    "java",
                    "-jar",
                    compilerJar.toString(),
                    inputPath,
                    VM_INPUT_FILE.toAbsolutePath().toString()
            ));

            if (!compilerStarted) {
                JaverLogger.error("Compiler is already running.\n");
                return;
            }

            waitUntilFinished(compilerRunner);

            if (vmRunner.isRunning()) {
                JaverLogger.error("VM is already running.\n");
                return;
            }

            vmRunner.start(List.of(
                    "java",
                    "-jar",
                    vmJar.toString(),
                    VM_INPUT_FILE.toAbsolutePath().toString()
            ));
        }, "compiler-vm-chain");

        chainThread.setDaemon(true);
        chainThread.start();
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
                    CONSOLE_INPUT_FILE,
                    content,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            JaverLogger.error("Created source input file: " + CONSOLE_INPUT_FILE.toAbsolutePath() + "\n");
        } catch (IOException exception) {
            JaverLogger.error("Failed to write source input file: " + exception.getMessage() + "\n");
        }

        return CONSOLE_INPUT_FILE.toAbsolutePath().toString();
    }

    private void createOrClearFile(Path path, String label) {
        try {
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