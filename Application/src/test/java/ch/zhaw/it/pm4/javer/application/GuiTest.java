package ch.zhaw.it.pm4.javer.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;

import java.io.IOException;

@ExtendWith(ApplicationExtension.class)
class GuiTest {

    @Test
    void shouldRenderAllMainGuiControls() {
        FxAssert.verifyThat("#consoleInput", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#runCompilerButton", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#compilerOutput", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#virtualMachineOutput", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#vmStackSizeOption", NodeMatchers.isNotNull());
        FxAssert.verifyThat("#vmDumpOnErrorOption", NodeMatchers.isNotNull());
    }

}
