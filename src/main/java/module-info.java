module cobra.te {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens org to javafx.fxml;
    opens org.controllers to javafx.fxml;
    opens org.models to javafx.base;
    opens org.utils to javafx.fxml;
    opens org.database to java.sql;

    exports org;
    exports org.controllers;
    exports org.utils;
    exports org.database;
    exports org.models;
    exports org.dao;
}