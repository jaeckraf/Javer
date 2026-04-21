package ch.zhaw.it.pm4.javer.application;

import javafx.application.Application;

// Main Launcher to start Javer
public class Launcher {
    public static void main(String[] args) {
        System.setProperty("MODULE", "app");
        System.setProperty("LOG_LEVEL", "INFO");
        JarConfigLoader.loadConfiguration();
        Application.launch(GuiApplication.class, args);
    }
}
