module ch.zhaw.it.pm4.javer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jdk.jshell;


    opens ch.zhaw.it.pm4.javer to javafx.fxml;
    exports ch.zhaw.it.pm4.javer;
    exports ch.zhaw.it.pm4.javer.compiler.lexer;
    opens ch.zhaw.it.pm4.javer.compiler.lexer to javafx.fxml;
}