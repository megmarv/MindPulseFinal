module org.project.mindpulse {
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;
    requires jdk.compiler;
    requires javafx.web;
    requires org.json;
    requires org.postgresql.jdbc;
    requires java.desktop;
    requires org.apache.commons.text;
    requires javafx.controls;
    requires okhttp3;

    opens org.project.mindpulse.Controllers to javafx.fxml;
    opens org.project.mindpulse.CoreModules to javafx.base;

    exports org.project.mindpulse.Application;
}