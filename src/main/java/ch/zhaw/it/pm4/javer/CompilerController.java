package ch.zhaw.it.pm4.javer;

import ch.zhaw.it.pm4.javer.compiler.CompilationResult;
import ch.zhaw.it.pm4.javer.compiler.CompilerService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

/**
 * Controller for the Javer compiler UI.
 * Connects user interface actions to the compilation backend.
 */
public class CompilerController {

    private final CompilerService compilerService;

    @FXML
    private TextArea codeEditor;

    @FXML
    private TextArea outputArea;

    @FXML
    private Label statusLabel;

    /**
     * Constructs a new CompilerController with a default CompilerService.
     */
    public CompilerController() {
        this.compilerService = new CompilerService();
    }

    /**
     * Handless the "Compile" button click action.
     * Triggers the compilation process and displays results in the output area.
     */
    @FXML
    protected void onCompileButtonClick() {
        String sourceCode = codeEditor.getText();
        
        if (sourceCode == null || sourceCode.isBlank()) {
            outputArea.setText("Please provide some source code to compile.");
            statusLabel.setText("No Input");
            return;
        }

        statusLabel.setText("Compiling...");
        CompilationResult result = compilerService.compile(sourceCode);

        displayResult(result);
    }

    /**
     * Updates UI elements to reflect the compilation outcome.
     * 
     * @param result Result record containing success/failure data.
     */
    private void displayResult(CompilationResult result) {
        StringBuilder sb = new StringBuilder();
        
        if (result.success()) {
            sb.append("Build Successful.\n\n");
            sb.append("Abstract Syntax Tree (CST) root generated successfully.");
            
            statusLabel.setText("Build Successful");
            statusLabel.setTextFill(Color.WHITE);
        } else {
            sb.append("Build Failed.\n\n");
            sb.append("Errors:\n");
            for (String error : result.errors()) {
                sb.append(" - ").append(error).append("\n");
            }
            
            statusLabel.setText("Build Failed (" + result.errors().size() + " errors)");
            statusLabel.setTextFill(Color.TOMATO);
        }

        outputArea.setText(sb.toString());
    }
}
