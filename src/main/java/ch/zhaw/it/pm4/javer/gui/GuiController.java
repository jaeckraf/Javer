package ch.zhaw.it.pm4.javer.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class GuiController {

    private static final Path CONSOLE_INPUT_FILE = Path.of("console-input.txt");

    @FXML
    private TextArea compilerOutput;

    @FXML
    private TextArea virtualMachineOutput;

    @FXML
    private TextArea consoleInput;

    @FXML
    private Button runCompilerButton;

    @FXML
    protected void onRunCompilerClick() {
        String input = consoleInput.getText();

        compilerOutput.appendText("Starting compiler...\n");
        if (input != null && !input.isBlank()) {
            compilerOutput.appendText("Input: " + input + "\n");
            writeInputToFile(input);
        } else {
            compilerOutput.appendText("No input entered.\n");
        }
        compilerOutput.appendText("Mock compiling...\n");
        compilerOutput.appendText("Compiler finished successfully.\n");

        consoleInput.clear();
    }

    private void writeInputToFile(String input) {
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
    }
}
