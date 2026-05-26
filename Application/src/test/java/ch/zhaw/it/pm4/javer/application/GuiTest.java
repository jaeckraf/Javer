package ch.zhaw.it.pm4.javer.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ApplicationExtension.class)
class GuiTest {

    private Map<String, Object> namespace;

    @Start
    void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GuiApplication.class.getResource("gui-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        namespace = fxmlLoader.getNamespace();
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void shouldLoadMainGuiControlsFromFxml() {
        assertNotNull(namespace.get("consoleInput"));
        assertNotNull(namespace.get("consoleInputLineNumbers"));
        assertNotNull(namespace.get("fileMenuButton"));
        assertNotNull(namespace.get("expertModeOption"));
        assertNotNull(namespace.get("runCompilerButton"));
        assertNotNull(namespace.get("compilerOutput"));
        assertNotNull(namespace.get("virtualMachineOutput"));
        assertNotNull(namespace.get("vmStackSizeValueOption"));
        assertNotNull(namespace.get("vmStackSizeKbOption"));
        assertNotNull(namespace.get("vmStackSizeMbOption"));
        assertNotNull(namespace.get("vmDumpOnErrorOption"));
    }
}
