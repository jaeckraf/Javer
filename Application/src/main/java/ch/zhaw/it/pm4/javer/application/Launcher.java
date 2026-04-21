package ch.zhaw.it.pm4.javer.application;

import javafx.application.Application;

import java.net.URL;

public class Launcher {
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