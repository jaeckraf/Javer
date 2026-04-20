package ch.zhaw.it.pm4.javer.application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

public class GuiController {

    private static final Path CONSOLE_INPUT_FILE = Path.of("console-input.txt");
    private static final Path VM_INPUT_FILE = Path.of("vm-input.txt");

    @FXML
    private TextArea compilerOutput;

    @FXML
    private TextArea virtualMachineOutput;

    @FXML
    private TextArea consoleInput;

    @FXML
    private Button runCompilerButton;

    // TODO one or two buttons? runCompiler and runVM
    @FXML
    protected void onRunCompilerClick() {
        String input = consoleInput.getText();

        compilerOutput.appendText("Starting compiler...\n");
        if (input != null && !input.isBlank()) {
            compilerOutput.appendText("Input: " + input + "\n");
            String inputPath = writeInputToFile(input);

            // Run compiler and VM in a background thread to avoid blocking the JavaFX UI thread
            new Thread(() -> {
                try {
                    Path compilerJar = findShadedJar("Compiler");
                    if (compilerJar == null) {
                        appendCompilerOutput("Could not locate Compiler shaded jar. Build the project (mvn -pl Compiler package) and try again.\n");
                        return;
                    }

                    Path vmJar = findShadedJar("VM");
                    if (vmJar == null) {
                        appendCompilerOutput("Could not locate VM shaded jar. Build the project (mvn -pl VM package) and try again.\n");
                        return;
                    }

                    // compiler will write output to VM_INPUT_FILE
                    ProcessBuilder compilerPb = new ProcessBuilder(
                            "java",
                            "-jar",
                            compilerJar.toAbsolutePath().toString(),
                            inputPath,
                            VM_INPUT_FILE.toAbsolutePath().toString()
                    );
                    compilerPb.redirectErrorStream(true);
                    Process compilerProc = compilerPb.start();
                    String compilerResult = readProcessOutput(compilerProc);
                    boolean finished = compilerProc.waitFor(30, TimeUnit.SECONDS);
                    if (!finished) {
                        compilerProc.destroyForcibly();
                        appendCompilerOutput("Compiler timed out.\n");
                        return;
                    }
                    appendCompilerOutput("Compiler finished:\n" + compilerResult + "\n");

                    // Run VM using the produced bytecode (the Compiler currently may not produce a real file,
                    // but VM has a default HALT instruction and will run when provided an input path)
                    ProcessBuilder vmPb = new ProcessBuilder(
                            "java",
                            "-jar",
                            vmJar.toAbsolutePath().toString(),
                            VM_INPUT_FILE.toAbsolutePath().toString()
                    );
                    vmPb.redirectErrorStream(true);
                    Process vmProc = vmPb.start();
                    String vmResult = readProcessOutput(vmProc);
                    boolean vmFinished = vmProc.waitFor(30, TimeUnit.SECONDS);
                    if (!vmFinished) {
                        vmProc.destroyForcibly();
                        appendVMOutput("VM timed out.\n");
                        return;
                    }
                    appendVMOutput(vmResult + "\n");

                } catch (IOException | InterruptedException e) {
                    appendCompilerOutput("Failed to run compiler/VM: " + e.getMessage() + "\n");
                }
            }).start();
        } else {
            compilerOutput.appendText("No input entered.\n");
        }
    }

    //TODO write the file into temp folder
    private String writeInputToFile(String input) {
        try {
            Files.writeString(
                    CONSOLE_INPUT_FILE,
                    input + System.lineSeparator(),
                    StandardCharsets.US_ASCII,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            compilerOutput.appendText("Saved input to " + CONSOLE_INPUT_FILE.toAbsolutePath() + "\n");
        } catch (IOException exception) {
            compilerOutput.appendText("Failed to save input: " + exception.getMessage() + "\n");
        }
        return CONSOLE_INPUT_FILE.toString();
    }

    private Path findShadedJar(String moduleName) {
        // search upward from user.dir for <moduleName>/target/*-all.jar
        Path cwd = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        Path current = cwd;
        for (int i = 0; i < 6; i++) {
            Path candidate = current.resolve(moduleName).resolve("target");
            if (Files.exists(candidate) && Files.isDirectory(candidate)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(candidate, "*-all.jar")) {
                    for (Path p : stream) {
                        return p.toAbsolutePath();
                    }
                } catch (IOException ignored) {
                }
            }
            current = current.getParent();
            if (current == null) break;
        }
        return null;
    }

    private String readProcessOutput(Process process) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    private void appendCompilerOutput(String text) {
        Platform.runLater(() -> compilerOutput.appendText(text));
    }

    private void appendVMOutput(String text) {
        Platform.runLater(() -> virtualMachineOutput.appendText(text));
    }
}
