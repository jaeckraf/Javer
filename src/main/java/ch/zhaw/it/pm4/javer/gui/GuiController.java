package ch.zhaw.it.pm4.javer.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class GuiController {
    @FXML
    private TextArea consoleOutput;

    @FXML
    private Button runCompilerButton;

    @FXML
    protected void onRunCompilerClick() {
        // Mock compiler output
        consoleOutput.appendText("Starting compiler...\n");
        consoleOutput.appendText("Mock compiling...\n");
        consoleOutput.appendText("Compiler finished successfully.\n");
    }
}
