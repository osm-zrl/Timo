module com.example.timo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.timo to javafx.fxml;
    exports com.example.timo;
}