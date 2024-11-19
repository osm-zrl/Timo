package com.example.timo;

import com.example.timo.Module.SQLiteConnection;
import com.example.timo.process.FrontendProcessLister;
import com.example.timo.process.ProcessInfo;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static SQLiteConnection db;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();


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


    }

    public static void main(String[] args) {
        launch();
    }
}
