module com.example.timo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;


    opens com.example.timo to javafx.fxml;
    exports com.example.timo;
    exports com.example.timo.Controller;
    opens com.example.timo.Controller to javafx.fxml;
}
