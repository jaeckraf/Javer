package ch.zhaw.it.pm4.javer.gui;



import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class GuiController {

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
        }
        compilerOutput.appendText("Mock compiling...\n");
        compilerOutput.appendText("Compiler finished successfully.\n");

        consoleInput.clear();
    }
}
