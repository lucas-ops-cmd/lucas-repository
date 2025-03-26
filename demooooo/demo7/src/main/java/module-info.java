module com.example.demo7 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.demo7 to javafx.fxml;
    opens com.example.demo7.controllers to javafx.fxml;
    opens com.example.demo7.models to javafx.base;

    exports com.example.demo7.views; // ✅ Correct
    exports com.example.demo7.controllers;
}
