module ch.zhaw.it.pm4.javer {
    requires java.logging;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens ch.zhaw.it.pm4.javer to javafx.fxml;
    exports ch.zhaw.it.pm4.javer;
}