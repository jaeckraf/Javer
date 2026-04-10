package ch.zhaw.it.pm4.javer.gui;

import ch.zhaw.it.pm4.javer.compiler.Compiler;
import ch.zhaw.it.pm4.javer.compiler.CompilerOptions;
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
            String path = writeInputToFile(input);
            CompilerOptions options = CompilerOptions.create(
                    path,
                    "output.bin"
            );
            Compiler compiler = new Compiler(options);
            String compileResult = compiler.compile();

            compilerOutput.setText(compileResult);
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
}
