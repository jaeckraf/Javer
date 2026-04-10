package ch.zhaw.it.pm4.javer.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
class HelloUITest {

    @Start
    public void start(Stage stage) throws IOException {
        // We load the main view exactly how HelloApplication.java does it
        FXMLLoader fxmlLoader = new FXMLLoader(GuiApplication.class.getResource("gui-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void should_click_button_and_show_compiler_texts(FxRobot robot) {
        robot.clickOn("#runCompilerButton");

        TextArea consoleOutput = robot.lookup("#compilerOutput")
                .queryAs(TextArea.class);

        String text = consoleOutput.getText();

        assertTrue(text.contains("Starting compiler..."));
        assertTrue(text.contains("Mock compiling..."));
        assertTrue(text.contains("Compiler finished successfully."));
    }
}