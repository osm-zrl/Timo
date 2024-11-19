package com.example.timo;

<<<<<<< HEAD
import com.example.timo.database.DatabaseConnection;
import com.example.timo.process.FrontendProcessLister;
import com.example.timo.process.ProcessInfo;
=======
import com.example.timo.Module.SQLiteConnection;
>>>>>>> 7671974ac0c0f023fe0040c40ef20fa61c5ce1b2
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

<<<<<<< HEAD
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

=======
>>>>>>> 7671974ac0c0f023fe0040c40ef20fa61c5ce1b2
public class HelloApplication extends Application {

    public static SQLiteConnection db;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
<<<<<<< HEAD
=======


        //Background connection backend thread
        Task<Void> dbConnectionTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try {
                    db = new SQLiteConnection();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    throw e;
                }
                return null;
            }

            @Override
            protected void succeeded() {
                System.out.println("SQLite database initialized: connection success");
            }

            @Override
            protected void failed() {
                System.out.println("SQLite database failed to initialize: connection failed: ");
            }
        };
        new Thread(dbConnectionTask).start();


>>>>>>> 7671974ac0c0f023fe0040c40ef20fa61c5ce1b2
    }

    public static void main(String[] args) {
        launch();
        DatabaseConnection.connect();
    }
}
