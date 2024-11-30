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

                return null;
            }

            @Override
            protected void succeeded() {
                System.out.println("Application initialized");
                applicationsController.ListTrackedApplication();

                // Create Scheduler instance
                scheduler = Executors.newSingleThreadScheduledExecutor();
                try {
                    // Pass a Runnable to the scheduler
                    scheduler.scheduleAtFixedRate(() -> {
                        try {
                            applicationsController.updateProcesses();
                        } catch (Exception e) {
                            System.err.println("Error updating processes: " + e);
                            throw new RuntimeException(e);
                        }

                        applicationsController.ListTrackedApplication();
                    }, 0, 20, TimeUnit.SECONDS); // 0 delay, repeat every 10 seconds
                } catch (Exception e) {
                    System.err.println("Error scheduling task: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void failed() {
                System.out.println("Application failed to initialize");
            }
        };
        Thread thread = new Thread(initialiseApplications);
        thread.setDaemon(true);
        thread.start();


    }


    public static void main(String[] args) {
        launch();
    }
    @Override
    public void stop() {
        scheduler.shutdown();
    }
}
