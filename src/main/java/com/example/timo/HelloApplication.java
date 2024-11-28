package com.example.timo;

import com.example.timo.Controller.ApplicationsController;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class HelloApplication extends Application {


    private ScheduledExecutorService scheduler;
    public ApplicationsController applicationsController;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();


        //ApplicationsController initialization thread
        Task<Void> initialiseApplications = new Task<Void>() {
            @Override
            protected Void call() throws Exception{
                applicationsController = new ApplicationsController();

                System.out.println(applicationsController.ApplicationsList);

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
        Thread thread = new Thread(initialiseApplications);
        thread.setDaemon(true);
        thread.start();

        //Create Scheduler instance
        scheduler = Executors.newSingleThreadScheduledExecutor();
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
