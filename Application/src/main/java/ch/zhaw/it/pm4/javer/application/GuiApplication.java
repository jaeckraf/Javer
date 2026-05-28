package ch.zhaw.it.pm4.javer.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX application shell that loads the GUI view and delegates shutdown to
 * the controller.
 */
public class GuiApplication extends Application {

    private GuiController controller;

    /**
     * Creates the JavaFX application instance.
     */
    public GuiApplication() {
        // This constructor is intentionally empty because JavaFX requires a public no-arg constructor.
    }

    /**
     * Loads the FXML view, wires the controller, and displays the main stage.
     *
     * @param stage the primary JavaFX stage
     * @throws IOException if the FXML view cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GuiApplication.class.getResource("gui-view.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();

        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("Javer - the future of programming");
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Stops managed child processes and removes runtime files through the
     * controller.
     */
    @Override
    public void stop() {
        if (controller != null) {
            controller.shutdown();
        }
    }
}
