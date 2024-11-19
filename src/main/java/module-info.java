module com.example.timo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
<<<<<<< HEAD
=======
    requires static lombok;
>>>>>>> 7671974ac0c0f023fe0040c40ef20fa61c5ce1b2


    opens com.example.timo to javafx.fxml;
    exports com.example.timo;
}
