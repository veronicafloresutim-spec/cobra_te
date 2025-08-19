module org.example.cobra_te {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // Requerimientos para la base de datos
    requires java.sql;

    opens org.example.cobra_te to javafx.fxml;
    opens org.example.cobra_te.controllers to javafx.fxml;
    opens org.example.cobra_te.models to javafx.base;

    exports org.example.cobra_te;

    // Exportar paquetes de base de datos
    exports org.example.cobra_te.database;
    exports org.example.cobra_te.models;
    exports org.example.cobra_te.dao;
    exports org.example.cobra_te.examples;
    exports org.example.cobra_te.controllers;
    exports org.example.cobra_te.utils;
}