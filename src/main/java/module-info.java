module cobra.te {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org to javafx.fxml;
    opens org.controllers to javafx.fxml;
    opens org.models to javafx.base;
    opens org.utils to javafx.fxml;

    exports org;
    exports org.controllers;
    exports org.utils;
}