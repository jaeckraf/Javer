package ch.zhaw.it.pm4.javer.gui;

import ch.zhaw.it.pm4.javer.HelloApplication;
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

import java.io.IOException;

@ExtendWith(ApplicationExtension.class)
public class HelloUITest {

    @Start
    public void start(Stage stage) throws IOException {
        // We load the main view exactly how HelloApplication.java does it
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void should_click_button_and_change_text(FxRobot robot) {
        robot.clickOn("#helloButton");
        FxAssert.verifyThat("#welcomeText", LabeledMatchers.hasText("Welcome to JavaFX Application!"));
    }
}