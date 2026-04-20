package ch.zhaw.it.pm4.javer.application;

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

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
class GuiTest {

    @Start
    void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GuiApplication.class.getResource("gui-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void shouldRenderAllMainGuiControls() {
        FxAssert.verifyThat("#consoleInput", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#runCompilerButton", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#compilerOutput", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#virtualMachineOutput", NodeMatchers.isNotNull());
    }

    @Test
    void shouldShowHelpfulMessageWhenInputIsBlank(FxRobot robot) {
        robot.clickOn("#consoleInput");
        robot.eraseText(20);
        robot.clickOn("#runCompilerButton");

        TextArea compilerOutput = robot.lookup("#compilerOutput").queryAs(TextArea.class);
        assertTrue(compilerOutput.getText().contains("Starting compiler..."));
        assertTrue(compilerOutput.getText().contains("No input entered."));
    }
}