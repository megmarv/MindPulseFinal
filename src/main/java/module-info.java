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

    opens org.project.mindpulse.Controllers to javafx.fxml;

    exports org.project.mindpulse.Application;
}