package ch.zhaw.it.pm4.javer.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;

import java.io.IOException;

@ExtendWith(ApplicationExtension.class)
public class GuiTest {

    @Start
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GuiApplication.class.getResource("gui-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void should_click_button_and_change_text(FxRobot robot) {
        robot.clickOn("#runCompilerButton");
        FxAssert.verifyThat("#consoleOutput", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#consoleOutput", (TextArea ta) -> ta.getText().contains("Compiler finished successfully."));
    }
}