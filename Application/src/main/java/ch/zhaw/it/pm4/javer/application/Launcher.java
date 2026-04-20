package ch.zhaw.it.pm4.javer.application;

import javafx.application.Application;

// Main Launcher to start Javer
public class Launcher {
    public static void main(String[] args) {
        // Load JAR configuration from application.properties
        JarConfigLoader.loadConfiguration();
        
        Application.launch(GuiApplication.class, args);
    }
}
