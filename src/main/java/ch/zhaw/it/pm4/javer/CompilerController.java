package ch.zhaw.it.pm4.javer;

import ch.zhaw.it.pm4.javer.compiler.CompilationResult;
import ch.zhaw.it.pm4.javer.compiler.CompilerService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

/**
 * Controller for the Javer compiler UI.
 * Connects user interface actions to the compilation backend.
 */
public class CompilerController {

    private final CompilerService compilerService;

    @FXML private TextArea codeEditor;
    @FXML private TextArea outputArea;
    @FXML private Label statusLabel;
    @FXML private Button compileButton;


    @FXML private Label guiStatusIcon;
    @FXML private Label guiStatusLabel;
    @FXML private Label compilerStatusIcon;
    @FXML private Label compilerStatusLabel;
    @FXML private Label vmStatusIcon;
    @FXML private Label vmStatusLabel;

    /**
     * Constructs a new CompilerController with a default CompilerService.
     */
    public CompilerController() {
        this.compilerService = new CompilerService();
    }

    /**
     * Handles the "Compile" button click action.
     * Triggers the compilation process and displays results in the output area asynchronously.
     */
    @FXML
    protected void onCompileButtonClick() {
        String sourceCode = codeEditor.getText();
        
        if (sourceCode == null || sourceCode.isBlank()) {
            outputArea.setText("Please provide some source code to compile.");
            statusLabel.setText("No Input");
            return;
        }

        if (compileButton != null) compileButton.setDisable(true);
        resetStatusIndicators();

        Task<Void> executionTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> setStageStatus(guiStatusIcon, guiStatusLabel, "GUI Prepared", Color.LIGHTSKYBLUE));
                Thread.sleep(400); 
                Platform.runLater(() -> setStageStatus(guiStatusIcon, guiStatusLabel, "GUI Done", Color.LIGHTGREEN));

                Platform.runLater(() -> setStageStatus(compilerStatusIcon, compilerStatusLabel, "Compiling...", Color.LIGHTSKYBLUE));
                Thread.sleep(400);
                CompilationResult result = compilerService.compile(sourceCode);
                
                if (result.success()) {
                    Platform.runLater(() -> setStageStatus(compilerStatusIcon, compilerStatusLabel, "Compiled", Color.LIGHTGREEN));
                    
                    Platform.runLater(() -> setStageStatus(vmStatusIcon, vmStatusLabel, "VM Executing...", Color.LIGHTSKYBLUE));
                    
                    // TODO: Replace this simulated delay with actual VM module invocation when ready
                    Thread.sleep(1200); 
                    
                    Platform.runLater(() -> {
                        setStageStatus(vmStatusIcon, vmStatusLabel, "VM Finished", Color.LIGHTGREEN);
                        displayResult(result);
                    });
                } else {
                    Platform.runLater(() -> {
                        setStageStatus(compilerStatusIcon, compilerStatusLabel, "Compile Failed", Color.TOMATO);
                        setStageStatus(vmStatusIcon, vmStatusLabel, "VM Skipped", Color.GRAY);
                        displayResult(result);
                    });
                }

                return null;
            }
        };

        executionTask.setOnSucceeded(e -> {
            if (compileButton != null) compileButton.setDisable(false);
        });
        
        executionTask.setOnFailed(e -> {
            if (compileButton != null) compileButton.setDisable(false);
            statusLabel.setText("Execution Error");
            statusLabel.setTextFill(Color.TOMATO);
            e.getSource().getException().printStackTrace();
        });

        new Thread(executionTask).start();
    }

    private void resetStatusIndicators() {
        Color pendingColor = Color.GRAY;
        setStageStatus(guiStatusIcon, guiStatusLabel, "GUI Preparing", pendingColor);
        setStageStatus(compilerStatusIcon, compilerStatusLabel, "Compiler", pendingColor);
        setStageStatus(vmStatusIcon, vmStatusLabel, "VM Execution", pendingColor);
        statusLabel.setText("Running...");
        statusLabel.setTextFill(Color.WHITE);
        outputArea.clear();
    }

    private void setStageStatus(Label icon, Label label, String text, Color color) {
        if (icon != null) icon.setTextFill(color);
        if (label != null) {
            label.setText(text);
            label.setTextFill(color);
        }
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
            sb.append("Abstract Syntax Tree (CST) root generated successfully.\n");
            sb.append("VM Execution completed simulated loop.\n");
            
            statusLabel.setText("Execution Successful");
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
