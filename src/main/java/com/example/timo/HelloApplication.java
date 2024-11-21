package com.example.timo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.paint.Color; 
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;
import javafx.beans.property.SimpleStringProperty;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // --- Menu vertical ---
        VBox navMenu = new VBox();
        navMenu.setMinWidth(150); // Fixed width for navbar
        navMenu.setStyle("-fx-background-color: #347877;");

        Button btnDashboard = new Button("Dashboard");
        Button btnTask = new Button("Task");
        Button btnSettings = new Button("Settings");
        Button btnProfile = new Button("Profile");

        String buttonStyle = "-fx-background-color: #347877; -fx-text-fill: white; -fx-font-size: 14px; "
                + "-fx-pref-width: 180px; -fx-pref-height: 40px; -fx-border-color: transparent;";
        String buttonHoverStyle = "-fx-background-color: #1E4848; -fx-text-fill: white;";

        // Apply styles
        btnDashboard.setStyle(buttonStyle);
        btnTask.setStyle(buttonStyle);
        btnSettings.setStyle(buttonStyle);
        btnProfile.setStyle(buttonStyle);

        btnDashboard.setOnMouseEntered(e -> btnDashboard.setStyle(buttonHoverStyle));
        btnDashboard.setOnMouseExited(e -> btnDashboard.setStyle(buttonStyle));
        btnTask.setOnMouseExited(e -> btnTask.setStyle(buttonStyle));
        btnSettings.setOnMouseExited(e -> btnSettings.setStyle(buttonStyle));
        btnProfile.setOnMouseExited(e -> btnProfile.setStyle(buttonStyle));

        navMenu.getChildren().addAll(btnDashboard, btnTask, btnSettings, btnProfile);
        navMenu.setSpacing(15);
        navMenu.setAlignment(Pos.TOP_CENTER);
        navMenu.setPadding(new Insets(20, 0, 0, 0));

        // --- Main Content ---
        BorderPane mainContent = new BorderPane();
        mainContent.setPadding(new Insets(10));
        mainContent.setCenter(createDashboard(primaryStage));

        // Button actions
        btnDashboard.setOnAction(e -> mainContent.setCenter(createDashboard(primaryStage)));
        btnTask.setOnAction(e -> mainContent.setCenter(new Label("Task Page")));
        btnSettings.setOnAction(e -> mainContent.setCenter(new Label("Settings Page")));
        btnProfile.setOnAction(e -> mainContent.setCenter(new Label("Profile Page")));

        // --- Main Container ---
        HBox root = new HBox(navMenu, mainContent);
        HBox.setHgrow(mainContent, Priority.ALWAYS); // Main content takes all available space

        // --- Responsive NavMenu ---
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() < 400) {
                navMenu.setPrefWidth(0);
                navMenu.setVisible(false);
            } else {
                navMenu.setPrefWidth(200);
                navMenu.setVisible(true);
            }
        });

        Scene scene = new Scene(root, 1000, 600);

        primaryStage.setTitle(" TIMO");
        Image icon = new Image(getClass().getResourceAsStream("/Timo-1.jpg"));
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- Create Dashboard ---
    private VBox createDashboard(Stage primaryStage) {
        // --- Pie Chart ---
        PieChart pieChart = new PieChart();
        pieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Google", 40),
                new PieChart.Data("VS Code", 42),
                new PieChart.Data("YouTube", 28)
        ));
        pieChart.setTitle("Application Time");
        pieChart.getData().get(0).getNode().setStyle("-fx-pie-color: #33FF57;");
        pieChart.getData().get(1).getNode().setStyle("-fx-pie-color: #3357FF;");
        pieChart.getData().get(2).getNode().setStyle("-fx-pie-color: red;");
        pieChart.setLegendVisible(false);

        // --- Bar Chart ---
        CategoryAxis xAxis = new CategoryAxis();
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
        barChart.setLegendVisible(false);

        // --- Table ---
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

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1;");
        table.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        table.getColumns().forEach(column -> {
            column.setStyle("-fx-font-size: 14px; -fx-background-color: #f5f5f5;");
        });


        // --- Responsive Layout ---
        VBox chartsBox = new VBox(20, pieChart, barChart);
        VBox dashboard = new VBox(20, chartsBox, table);
        dashboard.setPadding(new Insets(20));

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() < 800) {
                chartsBox.getChildren().clear();
                chartsBox.getChildren().addAll(pieChart, barChart); // Vertical stack
            } else {
                chartsBox.getChildren().clear();
                HBox horizontalChartsBox = new HBox(100, pieChart, barChart);
                chartsBox.getChildren().add(horizontalChartsBox); // Horizontal stack
            }
        });

        return dashboard;
    }

    private TableColumn<AppUsage, String> createTableColumn(String title, String property) {
        TableColumn<AppUsage, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> cellData.getValue().property(property));
        return column;
    }

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
            return switch (name) {
                case "title" -> title;
                case "startDate" -> startDate;
                case "endDate" -> endDate;
                case "duration" -> duration;
                case "memory" -> memory;
                case "gpu" -> gpu;
                case "percentage" -> percentage;
                default -> null;
            };
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
