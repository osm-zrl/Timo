package com.example.timo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // --- Menu vertical ---
        VBox navMenu = new VBox();
        navMenu.setPrefWidth(200);
        navMenu.setStyle("-fx-background-color: #347877;");

        Button btnDashboard = new Button("Dashboard");
        Button btnTask = new Button("Task");
        Button btnSettings = new Button("Settings");

        String buttonStyle = "-fx-background-color: #347877; -fx-text-fill: white; -fx-font-size: 14px; "
                + "-fx-pref-width: 180px; -fx-pref-height: 40px; -fx-border-color: transparent;";
        String buttonHoverStyle = "-fx-background-color: #1E4848; -fx-text-fill: white;";

        // Appliquer le style
        btnDashboard.setStyle(buttonStyle);
        btnTask.setStyle(buttonStyle);
        btnSettings.setStyle(buttonStyle);

        btnDashboard.setOnMouseEntered(e -> btnDashboard.setStyle(buttonHoverStyle));
        btnDashboard.setOnMouseExited(e -> btnDashboard.setStyle(buttonStyle));
        btnTask.setOnMouseEntered(e -> btnTask.setStyle(buttonHoverStyle));
        btnTask.setOnMouseExited(e -> btnTask.setStyle(buttonStyle));
        btnSettings.setOnMouseEntered(e -> btnSettings.setStyle(buttonHoverStyle));
        btnSettings.setOnMouseExited(e -> btnSettings.setStyle(buttonStyle));

        navMenu.getChildren().addAll(btnDashboard, btnTask, btnSettings);
        navMenu.setSpacing(10);
        navMenu.setAlignment(Pos.TOP_CENTER);
        navMenu.setPadding(new Insets(20, 0, 0, 0));

        // --- Contenu principal ---
        BorderPane mainContent = new BorderPane();
        mainContent.setPadding(new Insets(10));
        mainContent.setCenter(createDashboard()); // Ajout du tableau de bord par défaut

        // Actions des boutons
        btnDashboard.setOnAction(e -> mainContent.setCenter(createDashboard()));
        btnTask.setOnAction(e -> mainContent.setCenter(new Label("Page des Tâches")));
        btnSettings.setOnAction(e -> mainContent.setCenter(new Label("Page des Paramètres")));

        // --- Conteneur principal ---
        HBox root = new HBox(navMenu, mainContent);
        Scene scene = new Scene(root, 1000, 600);


        primaryStage.setTitle("Dashboard Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- Création du tableau de bord ---
    // --- Création du tableau de bord ---
    private VBox createDashboard() {
        // --- Création du graphique circulaire ---
        PieChart pieChart = new PieChart();
        pieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Google", 40),
                new PieChart.Data("VS Code", 32),
                new PieChart.Data("YouTube", 28)
        ));
        pieChart.setTitle("Application Time");

        // --- Création du graphique en barres ---
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Days");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Usage (hours)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Weekly Usage");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Last Week");
        series.getData().addAll(
                new XYChart.Data<>("Mon", 2),
                new XYChart.Data<>("Tue", 3),
                new XYChart.Data<>("Wed", 4),
                new XYChart.Data<>("Thu", 2.5),
                new XYChart.Data<>("Fri", 5),
                new XYChart.Data<>("Sat", 4.5),
                new XYChart.Data<>("Sun", 3)
        );
        barChart.getData().add(series);

        // --- Création du tableau des données ---
        TableView<AppUsage> table = new TableView<>();
        ObservableList<AppUsage> data = FXCollections.observableArrayList(
                new AppUsage("Google", "14 Feb 2024 12:30", "17 Feb 2024 12:30", "2h 03m", "2000MB", "12.5%", "90%"),
                new AppUsage("VS Code", "14 Feb 2024 12:30", "14 Feb 2024 12:30", "2h 03m", "2000MB", "86.35%", "80%"),
                new AppUsage("YouTube", "14 Feb 2024 12:30", "14 Feb 2024 12:30", "2h 03m", "2000MB", "210.91%", "55%"),
                new AppUsage("Spotify", "14 Feb 2024 12:30", "14 Feb 2024 12:30", "2h 03m", "2000MB", "1.73%", "40%"),
                new AppUsage("Instagram", "14 Feb 2024 12:30", "14 Feb 2024 12:30", "2h 03m", "2000MB", "1.73%", "12%")
        );

        table.setItems(data);
        table.getColumns().addAll(
                createTableColumn("Title", "title"),
                createTableColumn("Start Date", "startDate"),
                createTableColumn("End Date", "endDate"),
                createTableColumn("Duration", "duration"),
                createTableColumn("Memory", "memory"),
                createTableColumn("GPU", "gpu"),
                createTableColumn("%", "percentage")
        );

        // Mise en page avec VBox
        VBox dashboard = new VBox(20, pieChart, barChart, table);
        dashboard.setPadding(new Insets(20));

        // Faire en sorte que le tableau prenne toute la taille restante
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(table, Priority.ALWAYS);  // Permet à la table de s'étendre horizontalement

        return dashboard;
    }




    // --- Création d'une colonne de tableau ---
    private TableColumn<AppUsage, String> createTableColumn(String title, String property) {
        TableColumn<AppUsage, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> cellData.getValue().property(property));
        return column;
    }

    // --- Classe pour les données de tableau ---
    public static class AppUsage {
        private final SimpleStringProperty title;
        private final SimpleStringProperty startDate;
        private final SimpleStringProperty endDate;
        private final SimpleStringProperty duration;
        private final SimpleStringProperty memory;
        private final SimpleStringProperty gpu;
        private final SimpleStringProperty percentage;

        public AppUsage(String title, String startDate, String endDate, String duration, String memory, String gpu, String percentage) {
            this.title = new SimpleStringProperty(title);
            this.startDate = new SimpleStringProperty(startDate);
            this.endDate = new SimpleStringProperty(endDate);
            this.duration = new SimpleStringProperty(duration);
            this.memory = new SimpleStringProperty(memory);
            this.gpu = new SimpleStringProperty(gpu);
            this.percentage = new SimpleStringProperty(percentage);
        }

        public SimpleStringProperty property(String name) {
            switch (name) {
                case "title": return title;
                case "startDate": return startDate;
                case "endDate": return endDate;
                case "duration": return duration;
                case "memory": return memory;
                case "gpu": return gpu;
                case "percentage": return percentage;
                default: return null;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

