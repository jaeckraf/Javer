module ch.zhaw.it.pm4.javer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jdk.jshell;
    requires java.logging;


    opens ch.zhaw.it.pm4.javer.gui to javafx.fxml;
    exports ch.zhaw.it.pm4.javer;
    exports ch.zhaw.it.pm4.javer.gui;
    exports ch.zhaw.it.pm4.javer.compiler.lexer;
    opens ch.zhaw.it.pm4.javer.compiler.lexer to javafx.fxml;

    exports ch.zhaw.it.pm4.javer.compiler.diagnostics;
    opens ch.zhaw.it.pm4.javer.compiler.diagnostics to javafx.fxml;
}