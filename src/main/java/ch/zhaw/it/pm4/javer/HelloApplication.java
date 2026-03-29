package ch.zhaw.it.pm4.javer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("compiler-view.fxml"));
        // Standard IDE dimensions
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Javer Compiler IDE");
        stage.setScene(scene);
        stage.show();
    }
}
