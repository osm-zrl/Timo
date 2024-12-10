
package com.example.timo;

        import javafx.application.Application;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.stage.Stage;

public class Task extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger le fichier FXML
            Parent root = FXMLLoader.load(getClass().getResource("TEST_ma.fxml")); // Remplacez par le chemin correct
            // Créer une scène
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());


            // Définir le titre et afficher la scène
            primaryStage.setTitle("Application JavaFX");
            primaryStage.setScene(scene);

            primaryStage.setFullScreen(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
