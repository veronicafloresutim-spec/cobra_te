module org.example.cobra_te {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org.example.cobra_te to javafx.fxml;
    opens org.example.cobra_te.controllers to javafx.fxml;
    opens org.example.cobra_te.models to javafx.base;
    opens org.example.cobra_te.utils to javafx.fxml;

    exports org.example.cobra_te;
    exports org.example.cobra_te.controllers;
    exports org.example.cobra_te.utils;
}