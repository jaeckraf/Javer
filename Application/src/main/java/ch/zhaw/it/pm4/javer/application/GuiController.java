package ch.zhaw.it.pm4.javer.application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class GuiController {

    private static final Path CONSOLE_INPUT_FILE = Path.of("console-input.txt");
    private static final Path VM_INPUT_FILE = Path.of("vm-input.txt");

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

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
        compilerRunner = new ManagedProcessRunner(
                "Compiler",
                this::appendCompilerOutput,
                this::appendStatus,
                this::appendStatus,
                this::updateCompilerButtons
        );

        vmRunner = new ManagedProcessRunner(
                "VM",
                this::appendVMOutput,
                this::appendStatus,
                this::appendStatus,
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
            appendStatus("Compiler jar not configured. Set system property 'javer.compiler.jar'.\n");
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
            appendStatus("Compiler is already running.\n");
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
            appendStatus("VM jar not configured. Set system property 'javer.vm.jar'.\n");
            return;
        }

        boolean started = vmRunner.start(List.of(
                "java",
                "-jar",
                vmJar.toString(),
                VM_INPUT_FILE.toAbsolutePath().toString()
        ));

        if (!started) {
            appendStatus("VM is already running.\n");
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
            appendStatus("Compiler or VM is already running.\n");
            return;
        }

        compilerOutput.clear();
        virtualMachineOutput.clear();

        String inputPath = writeInputToFile(consoleInput.getText());
        createOrClearFile(VM_INPUT_FILE, "VM input file");

        Path compilerJar = resolveJarFromProperty("javer.compiler.jar");
        Path vmJar = resolveJarFromProperty("javer.vm.jar");

        if (compilerJar == null) {
            appendStatus("Compiler jar not configured. Set system property 'javer.compiler.jar'.\n");
            return;
        }

        if (vmJar == null) {
            appendStatus("VM jar not configured. Set system property 'javer.vm.jar'.\n");
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
                appendStatus("Compiler is already running.\n");
                return;
            }

            waitUntilFinished(compilerRunner);

            if (vmRunner.isRunning()) {
                appendStatus("VM is already running.\n");
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
                appendStatus("Chained execution interrupted.\n");
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
            appendStatus("Created source input file: " + CONSOLE_INPUT_FILE.toAbsolutePath() + "\n");
        } catch (IOException exception) {
            appendStatus("Failed to write source input file: " + exception.getMessage() + "\n");
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
            appendStatus("Created/cleared " + label + ": " + path.toAbsolutePath() + "\n");
        } catch (IOException e) {
            appendStatus("Failed to create/clear " + label + ": " + e.getMessage() + "\n");
        }
    }

    private Path resolveJarFromProperty(String propertyName) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            appendStatus("ERROR: Missing system property: " + propertyName + "\n");
            appendStatus("Please ensure JarConfigLoader.loadConfiguration() is called at startup or set the property manually.\n");
            return null;
        }
        
        Path path = Path.of(value).toAbsolutePath().normalize();
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            appendStatus("ERROR: Configured jar does not exist: " + path + "\n");
            appendStatus("Make sure the jar file is present at the expected location.\n");
            appendStatus("Property '" + propertyName + "' is set to: " + value + "\n");
            return null;
        }
        
        appendStatus("Resolved " + propertyName + " to: " + path.toAbsolutePath() + "\n");
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
        String timestamp = LocalTime.now().format(TIME_FORMAT);
        String formatted = "[" + timestamp + "]: " + text;

        Platform.runLater(() -> {
            statusOutput.appendText(formatted);
            statusOutput.setScrollTop(Double.MAX_VALUE);
        });
    }
}