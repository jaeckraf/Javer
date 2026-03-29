package ch.zhaw.it.pm4.javer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import java.io.IOException;

@ExtendWith(ApplicationExtension.class)
public class CompilerUITest {

    @Start
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("compiler-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void should_compile_entered_code(FxRobot robot) {
        // Type code in editor
        robot.clickOn("#codeEditor").write("let x = 5;");
        
        // Click Compile
        robot.clickOn("#compileButton");
        
        // Verify output area shows success (stub always succeeds)
        FxAssert.verifyThat("#outputArea", TextInputControlMatchers.hasText(
                org.hamcrest.Matchers.containsString("Build Successful")
        ));
        
        // Verify status label
        FxAssert.verifyThat("#statusLabel", LabeledMatchers.hasText("Build Successful"));
    }

    @Test
    void should_show_error_on_empty_input(FxRobot robot) {
        // Clear editor (it's already empty)
        robot.clickOn("#codeEditor").eraseText(20);
        
        // Click Compile
        robot.clickOn("#compileButton");
        
        // Verify output area shows error message
        FxAssert.verifyThat("#outputArea", TextInputControlMatchers.hasText(
                org.hamcrest.Matchers.containsString("Please provide some source code")
        ));
        
        // Verify status label
        FxAssert.verifyThat("#statusLabel", LabeledMatchers.hasText("No Input"));
    }
}
