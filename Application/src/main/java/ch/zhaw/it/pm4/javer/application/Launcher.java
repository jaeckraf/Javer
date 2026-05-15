package ch.zhaw.it.pm4.javer.application;

import javafx.application.Application;

import java.net.URL;

/**
 * Native-launcher-friendly entry point that configures logging and starts the
 * JavaFX application.
 */
public final class Launcher {

    private Launcher() {
    }

    /**
     * Configures application logging, loads tool locations, and launches the
     * JavaFX runtime.
     *
     * @param args command-line arguments passed through to JavaFX
     */
    public static void main(String[] args) {
        URL logbackConfig = Launcher.class.getResource(
                "/ch/zhaw/it/pm4/javer/application/logback.xml"
        );

        if (logbackConfig != null) {
            System.setProperty("logback.configurationFile", logbackConfig.toExternalForm());
        } else {
            System.err.println("Application logback.xml not found.");
        }

        System.setProperty("MODULE", "app");
        System.setProperty("LOG_LEVEL", "INFO");

        JarConfigLoader.loadConfiguration();
        Application.launch(GuiApplication.class, args);
    }
}
