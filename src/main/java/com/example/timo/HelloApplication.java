package com.example.timo;

import com.example.timo.Controller.ApplicationsController;
import com.example.timo.Module.SQLiteConnection;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class HelloApplication extends Application {

    public static SQLiteConnection db;
    private ScheduledExecutorService scheduler;
    public ApplicationsController applicationsController;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();


        //Background connection backend thread
        /*Task<Void> dbConnectionTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {


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
        new Thread(dbConnectionTask).start();*/

        //Create Scheduler instance
        scheduler = Executors.newSingleThreadScheduledExecutor();

        Task<Void> initialiseApplications = new Task<Void>() {
            @Override
            protected Void call() throws Exception{
                try {
                    db = new SQLiteConnection();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    throw e;
                }
                applicationsController = new ApplicationsController();


                return null;
            }

            @Override
            protected void succeeded() {
                System.out.println("Application initialized");
            }

            @Override
            protected void failed() {
                System.out.println("Application failed to initialize");
            }
        };
        scheduler.scheduleAtFixedRate(this::testScheduler, 0, 5, TimeUnit.SECONDS);

    }

    public void testScheduler(){
        System.out.println("scheduler executed");
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        scheduler.shutdown();
    }
}
