module ch.zhaw.it.pm4.javer {
    requires javafx.controls;
    requires javafx.fxml;


    opens ch.zhaw.it.pm4.javer to javafx.fxml;
    exports ch.zhaw.it.pm4.javer;
}