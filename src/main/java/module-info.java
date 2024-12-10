module com.example.timo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.timo to javafx.fxml;
    exports com.example.timo;
    exports com.example.timo.view to javafx.graphics;
}