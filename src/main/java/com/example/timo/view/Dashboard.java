package com.example.timo.view;

import com.example.timo.Module.ProcessInfo;
import com.example.timo.process.FrontendProcessLister;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Dashboard extends Application {

    private TableView<AppUsage> table;  
    private ScheduledExecutorService scheduler;

    @Override
    public void start(Stage primaryStage) {
        // --- Menu vertical ---
        VBox navMenu = new VBox();
        navMenu.setMinWidth(150);
        navMenu.setStyle("-fx-background-color: #34495E;");

        Button btnDashboard = new Button("Dashboard");
        Button btnTask = new Button("Task");
        Button btnSettings = new Button("Settings");
        Button btnProfile = new Button("Profile");

        String buttonStyle = "-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-size: 14px; "
                + "-fx-pref-width: 180px; -fx-pref-height: 40px; -fx-border-color: transparent;";

        // Hover style (using a lighter background color for visibility)
        String buttonHoverStyle = "-fx-background-color: #5D6D7E; -fx-text-fill: white; -fx-font-size: 14px; "
        + "-fx-pref-width: 180px; -fx-pref-height: 40px; -fx-border-color: transparent;";

        // Apply styles
        applyButtonHoverEffect(btnDashboard, buttonStyle, buttonHoverStyle);
        applyButtonHoverEffect(btnTask, buttonStyle, buttonHoverStyle);
        applyButtonHoverEffect(btnSettings, buttonStyle, buttonHoverStyle);
        applyButtonHoverEffect(btnProfile, buttonStyle, buttonHoverStyle);

        navMenu.getChildren().addAll(btnDashboard, btnTask, btnSettings, btnProfile);
        navMenu.setSpacing(15);
        navMenu.setAlignment(Pos.TOP_CENTER);
        navMenu.setPadding(new Insets(20, 0, 0, 0));

        // --- Main Content ---
        BorderPane mainContent = new BorderPane();
        mainContent.setPadding(new Insets(80,40,50,40));
        mainContent.setCenter(createDashboard(primaryStage));

        // Button actions
        btnDashboard.setOnAction(e -> mainContent.setCenter(createDashboard(primaryStage)));
        btnTask.setOnAction(e -> mainContent.setCenter(new Label("Task Page")));
        btnSettings.setOnAction(e -> mainContent.setCenter(new Label("Settings Page")));
        btnProfile.setOnAction(e -> mainContent.setCenter(new Label("Profile Page")));

        // --- Main Container ---
        HBox root = new HBox(navMenu, mainContent);
        HBox.setHgrow(mainContent, Priority.ALWAYS);

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

        // Initialize the scheduler
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(this::updateProcessTable);
        }, 0, 5, TimeUnit.SECONDS);

        primaryStage.setOnCloseRequest(e -> {
            if (scheduler != null) {
                scheduler.shutdown();
            }
        });
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
        pieChart.getData().get(0).getNode().setStyle("-fx-pie-color: #81C784;");
        pieChart.getData().get(1).getNode().setStyle("-fx-pie-color: #64B5F6;");
        pieChart.getData().get(2).getNode().setStyle("-fx-pie-color: #E57373;");
        pieChart.setLegendVisible(false);
        
        // Style and size the pie chart
        pieChart.setPrefSize(400, 300);
        pieChart.setMinSize(400, 300);
        pieChart.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
        HBox.setHgrow(pieChart, Priority.ALWAYS); // Allow horizontal growth

        // --- Bar Chart ---
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Usage (hours)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Weekly Usage");
        
        // Style and size the bar chart
        barChart.setPrefSize(400, 300);
        barChart.setMinSize(400, 300);
        barChart.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
        HBox.setHgrow(barChart, Priority.ALWAYS); // Allow horizontal growth

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Last Week");
        series.getData().addAll(FXCollections.observableArrayList(
                new XYChart.Data<>("Mon", 2),
                new XYChart.Data<>("Tue", 3),
                new XYChart.Data<>("Wed", 4),
                new XYChart.Data<>("Thu", 2.5),
                new XYChart.Data<>("Fri", 5),
                new XYChart.Data<>("Sat", 4.5),
                new XYChart.Data<>("Sun", 3)
        ));

        barChart.getData().add(series);
        barChart.setLegendVisible(false);

        String barColor = "-fx-bar-fill: #80CBC4;";
        series.getData().forEach(data -> data.getNode().setStyle(barColor));

        // --- Table ---
        table = new TableView<>();
        
        table.getColumns().addAll(
                createTableColumn("Name", "name"),
                createTableColumn("PID", "pid"),
                createTableColumn("Memory", "memory"),
                createTableColumn("CPU", "cpu"),
                createTableColumn("Duration", "duration")
        );

        // Table styling and configuration
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);
        table.setMaxHeight(500);
        table.setMinHeight(200);
        
        // Style the table container
        VBox tableContainer = new VBox(table);
        tableContainer.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
        tableContainer.setPadding(new Insets(20));
        VBox.setVgrow(tableContainer, Priority.ALWAYS); // Allow vertical growth
        HBox.setHgrow(tableContainer, Priority.ALWAYS); // Allow horizontal growth
        
        // Remove empty rows
        table.setFixedCellSize(40);
        table.setPlaceholder(new Label("No processes found"));
        
        // Style the table
        table.setStyle("-fx-border-color: #cccccc; " +
                      "-fx-border-width: 1; " +
                      "-fx-font-size: 14px; " +
                      "-fx-text-fill: #333333;");

        // Style the columns and cells
        String columnStyle = "-fx-alignment: CENTER; " +
                           "-fx-font-size: 14px; " +
                           "-fx-font-weight: bold; " +
                           "-fx-background-color: #f5f5f5; " +
                           "-fx-padding: 10px;";

        String cellStyle = "-fx-alignment: CENTER; " +
                         "-fx-padding: 10px;";

        table.getColumns().forEach(column -> {
            column.setStyle(columnStyle);
            column.setPrefWidth(USE_COMPUTED_SIZE); // Let columns adjust their width
            ((TableColumn)column).setCellFactory(tc -> {
                TableCell cell = new TableCell() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item.toString());
                        }
                        setStyle(cellStyle);
                    }
                };
                return cell;
            });
        });

        updateProcessTable();

        // Create charts container with horizontal layout
        HBox chartsContainer = new HBox(20);
        chartsContainer.setAlignment(Pos.CENTER);
        chartsContainer.setPadding(new Insets(20));
        chartsContainer.getChildren().addAll(pieChart, barChart);
        HBox.setHgrow(chartsContainer, Priority.ALWAYS); // Allow horizontal growth
        
        // Create main dashboard container
        VBox dashboard = new VBox(20);
        dashboard.setAlignment(Pos.TOP_CENTER);
        dashboard.setPadding(new Insets(20));
        dashboard.setStyle("-fx-background-color: #f8f9fa;");
        dashboard.setFillWidth(true); // Make children fill width
        
        // Add components to dashboard
        dashboard.getChildren().addAll(chartsContainer, tableContainer);

        // Make the layout responsive
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            if (width < 900) {
                // Stack charts vertically
                chartsContainer.getChildren().clear();
                VBox verticalCharts = new VBox(20);
                verticalCharts.setAlignment(Pos.CENTER);
                verticalCharts.getChildren().addAll(pieChart, barChart);
                VBox.setVgrow(verticalCharts, Priority.ALWAYS);
                chartsContainer.getChildren().add(verticalCharts);
                
                // Adjust chart sizes for vertical layout
                pieChart.setPrefSize(width - 100, 300);
                barChart.setPrefSize(width - 100, 300);
            } else {
                // Display charts horizontally
                chartsContainer.getChildren().clear();
                chartsContainer.getChildren().addAll(pieChart, barChart);
                
                // Adjust chart sizes for horizontal layout
                double chartWidth = (width - 140) / 2; // Account for padding and spacing
                pieChart.setPrefSize(chartWidth, 300);
                barChart.setPrefSize(chartWidth, 300);
            }
            
            // Adjust table width
            table.setPrefWidth(width - 80); // Account for padding
        });

        return dashboard;
    }

    private void updateProcessTable() {
        ArrayList<ProcessInfo> processes = FrontendProcessLister.getProcessList();
        ObservableList<AppUsage> data = FXCollections.observableArrayList();
        
        for (ProcessInfo process : processes) {
            data.add(new AppUsage(
                process.getName(),
                process.getPid().toString(),
                String.format("%.2f MB", process.getMemory()),
                String.format("%.2f%%", process.getCpu()),
                formatDuration(process.getDuration())
            ));
        }
        
        table.setItems(data);
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%dh %02dm", hours, minutes);
    }

    private void applyButtonHoverEffect(Button button, String normalStyle, String hoverStyle) {
        button.setStyle(normalStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle)); // Apply hover style
        button.setOnMouseExited(e -> button.setStyle(normalStyle)); // Revert to normal style
    }
    

    private TableColumn<AppUsage, String> createTableColumn(String title, String property) {
        TableColumn<AppUsage, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> cellData.getValue().property(property));
        return column;
    }

    public static class AppUsage {
        private final SimpleStringProperty name;
        private final SimpleStringProperty pid;
        private final SimpleStringProperty memory;
        private final SimpleStringProperty cpu;
        private final SimpleStringProperty duration;

        public AppUsage(String name, String pid, String memory, String cpu, String duration) {
            this.name = new SimpleStringProperty(name);
            this.pid = new SimpleStringProperty(pid);
            this.memory = new SimpleStringProperty(memory);
            this.cpu = new SimpleStringProperty(cpu);
            this.duration = new SimpleStringProperty(duration);
        }

        public SimpleStringProperty property(String name) {
            switch (name) {
                case "name": return this.name;
                case "pid": return this.pid;
                case "memory": return this.memory;
                case "cpu": return this.cpu;
                case "duration": return this.duration;
                default: return null;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}