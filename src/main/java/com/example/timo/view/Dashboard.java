package com.example.timo.view;

import com.example.timo.Controller.ApplicationsController;
import com.example.timo.Module.ApplicationHistory;
import com.example.timo.Module.DatabaseModule;
import com.example.timo.Module.ProcessInfo;
import com.example.timo.process.FrontendProcessLister;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Dashboard extends Application {

    private TableView<AppUsage> table;  
    private ScheduledExecutorService scheduler;
    public ApplicationsController applicationsController;
    private HBox chartsContainer; // Add this field
    private ScrollPane scrollPane; // Add this field

    @Override
    public void start(Stage primaryStage) throws Exception {
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
        applicationsController = new ApplicationsController();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
        try {
            // Update database first
            applicationsController.updateProcesses();
            
            // Then update UI on JavaFX thread
            Platform.runLater(() -> {
                try {
                 // Update process table
                updateProcessTable();
                
                // Get container and update charts
                HBox chartsContainer = (HBox) ((VBox) table.getParent().getParent()).getChildren().get(0);
                
                // Create and style new charts
                PieChart newPieChart = createDynamicPieChart();
                BarChart<String, Number> newBarChart = createWeeklyBarChart();
                
                newPieChart.setMinSize(300, 200);
                newBarChart.setMinSize(300, 200);
                
                String chartStyle = "-fx-background-color: white; -fx-padding: 15px; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);";
                
                newPieChart.setStyle(chartStyle);
                newBarChart.setStyle(chartStyle);
                
                // Update both charts
                if (chartsContainer != null && chartsContainer.getChildren().size() >= 2) {
                    chartsContainer.getChildren().set(0, newPieChart);
                    chartsContainer.getChildren().set(1, newBarChart);
                }
            } catch (Exception e) {
                System.err.println("Error updating UI: " + e.getMessage());
            }
        });
    } catch (Exception e) {
        System.err.println("Error in scheduler: " + e.getMessage());
    }
    }, 0, 5, TimeUnit.SECONDS);

        primaryStage.setOnCloseRequest(e -> {
            if (scheduler != null) {
                scheduler.shutdown();
            }
        });
    }

    // --- Create Dashboard ---
    private Node createDashboard(Stage primaryStage) {
        // --- Pie Chart ---
       PieChart pieChart = createDynamicPieChart(); // Add this line where you want the pie chart
        pieChart.setMinSize(300, 200);
        pieChart.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
        
        pieChart.setTitle("Application Time");
        pieChart.getData().get(0).getNode().setStyle("-fx-pie-color: #81C784;");
        pieChart.getData().get(1).getNode().setStyle("-fx-pie-color: #64B5F6;");
        pieChart.getData().get(2).getNode().setStyle("-fx-pie-color: #E57373;");
        pieChart.setLegendVisible(false);
        
        // Style and size the pie chart
        pieChart.setMinSize(300, 200);
        pieChart.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
        HBox.setHgrow(pieChart, Priority.ALWAYS); // Allow horizontal growth

        // --- Bar Chart ---
        BarChart<String, Number> barChart = createWeeklyBarChart();
        barChart.setMinSize(300, 200);
        
        // Style and size the bar chart
        barChart.setMinSize(300, 200);
        barChart.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
        HBox.setHgrow(barChart, Priority.ALWAYS); // Allow horizontal growth


       
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
        table.setPrefHeight(300);
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
        chartsContainer = new HBox(20);
        chartsContainer.setId("chartsContainer"); // Add ID
        chartsContainer.setAlignment(Pos.CENTER);
        chartsContainer.setPadding(new Insets(20));
        chartsContainer.getChildren().addAll(pieChart, barChart);
        HBox.setHgrow(chartsContainer, Priority.ALWAYS);
        
        // Create main dashboard container
        VBox dashboard = new VBox(20);
        dashboard.setAlignment(Pos.TOP_CENTER);
        dashboard.setPadding(new Insets(20));
        dashboard.setStyle("-fx-background-color: #f8f9fa;");
        dashboard.setFillWidth(true); // Make children fill width
        
        // Add components to dashboard
        dashboard.getChildren().addAll(chartsContainer, tableContainer);

        // Create main container that will hold either dashboard or scrollPane
        StackPane mainContainer = new StackPane();
        
        // Create ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setContent(dashboard);
        
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
                pieChart.setPrefSize(width - 40, 250);
                barChart.setPrefSize(width - 40, 250);
                
                // Switch to scrollable layout
                mainContainer.getChildren().clear();
                mainContainer.getChildren().add(scrollPane);
            } else {
                // Display charts horizontally
                chartsContainer.getChildren().clear();
                chartsContainer.getChildren().addAll(pieChart, barChart);
                
                // Adjust chart sizes for horizontal layout
                double chartWidth = (width - 40) / 2;
                pieChart.setPrefSize(chartWidth, 300);
                barChart.setPrefSize(chartWidth, 300);
                
                // Switch to normal layout
                mainContainer.getChildren().clear();
                mainContainer.getChildren().add(dashboard);
            }
            
            // Adjust table width
            table.setPrefWidth(width - 40);
        });

        // Initial setup based on window width
        if (primaryStage.getWidth() < 900) {
            mainContainer.getChildren().add(scrollPane);
        } else {
            mainContainer.getChildren().add(dashboard);
        }

        return mainContainer;
    }
    public PieChart createDynamicPieChart() {
    PieChart pieChart = new PieChart();
    DatabaseModule dbModule;
    
    try {
        dbModule = new DatabaseModule();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        
        // Get both stored and currently running applications
        ArrayList<ApplicationHistory> storedApps = dbModule.getDateSpecificStoredApplications(formattedDate);
        ArrayList<ProcessInfo> runningProcesses = FrontendProcessLister.getProcessList();
        
        // Create a map to combine durations of stored and running apps
        Map<String, Long> combinedDurations = new HashMap<>();
        
        // Add stored applications durations
        for (ApplicationHistory app : storedApps) {
            combinedDurations.put(app.getName(), (long)app.getDuration());
        }
        
        // Add or update with running applications durations
        for (ProcessInfo process : runningProcesses) {
            String name = process.getName();
            long runningDuration = process.getDuration().getSeconds();
            combinedDurations.merge(name, runningDuration, Long::sum);
        }
        
        // Convert to sorted list
        List<Map.Entry<String, Long>> sortedApps = combinedDurations.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toList());
        
        // Calculate total duration
        long totalDuration = combinedDurations.values().stream().mapToLong(Long::valueOf).sum();
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        // Add top 3 apps
        for (int i = 0; i < Math.min(3, sortedApps.size()); i++) {
            Map.Entry<String, Long> app = sortedApps.get(i);
            double percentage = (app.getValue() * 100.0) / totalDuration;
            pieChartData.add(new PieChart.Data(
                app.getKey() + " (" + formatDuration(Duration.ofSeconds(app.getValue())) + ")",
                percentage
            ));
        }
        
        // Add others
        if (sortedApps.size() > 3) {
            long othersDuration = sortedApps.subList(3, sortedApps.size()).stream()
                .mapToLong(Map.Entry::getValue)
                .sum();
            double othersPercentage = (othersDuration * 100.0) / totalDuration;
            pieChartData.add(new PieChart.Data(
                "Others (" + formatDuration(Duration.ofSeconds(othersDuration)) + ")",
                othersPercentage
            ));
        }
        
        pieChart.setData(pieChartData);
        pieChart.setTitle("Application Usage Today");
        pieChart.setLabelsVisible(true);
        pieChart.setLabelLineLength(10);
        pieChart.setLegendVisible(false);
        
        // Apply colors
        String[] colors = {"#81C784", "#64B5F6", "#E57373", "#90A4AE"};
        int colorIndex = 0;
        for (PieChart.Data data : pieChart.getData()) {
            data.getNode().setStyle("-fx-pie-color: " + colors[colorIndex % colors.length] + ";");
            colorIndex++;
        }
        
    } catch (Exception e) {
        System.err.println("Error creating pie chart: " + e.getMessage());
    }
    
    return pieChart;
}

    private void updatePieChart() {
        Platform.runLater(() -> {
            try {
                if (chartsContainer != null) {
                    // Create new pie chart with latest data
                    PieChart newPieChart = createDynamicPieChart();
                    newPieChart.setMinSize(300, 200);
                    newPieChart.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
                    
                    // Replace old pie chart
                    if (!chartsContainer.getChildren().isEmpty()) {
                        chartsContainer.getChildren().set(0, newPieChart);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error updating pie chart: " + e.getMessage());
            }
        });
    }
    

    private void updateProcessTable() {
        try {
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

        } catch (Exception e) {
            System.err.println("Error updating process table: " + e.getMessage());
        }
    }

    private BarChart<String, Number> createWeeklyBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        
        // Configure y-axis
        yAxis.setLabel("Usage (h)");
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number object) {
                return String.format("%.1f", object.doubleValue());
            }
        });
        yAxis.setTickUnit(2); // Show tick marks every 2 hours
        yAxis.setMinorTickCount(1); // Show minor ticks between major ticks
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Weekly Usage");
        barChart.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
        barChart.setCategoryGap(10);
        barChart.setBarGap(0);
        
        try {
            DatabaseModule dbModule = new DatabaseModule();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            double maxHours = 0;
            
            // Get data for last 7 days
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");
            
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                String formattedDate = date.format(formatter);
                String dayName = date.format(dayFormatter);
                
                ArrayList<ApplicationHistory> apps = dbModule.getDateSpecificStoredApplications(formattedDate);
                double totalHours = apps.stream()
                    .mapToDouble(app -> app.getDuration() / 3600.0)
                    .sum();
                    
                if (i == 0) {
                    ArrayList<ProcessInfo> runningApps = FrontendProcessLister.getProcessList();
                    double runningHours = runningApps.stream()
                        .mapToDouble(app -> app.getDuration().toSeconds() / 3600.0)
                        .sum();
                    totalHours += runningHours;
                }
                
                XYChart.Data<String, Number> data = new XYChart.Data<>(dayName, totalHours);
                series.getData().add(data);
                maxHours = Math.max(maxHours, totalHours);
                
                // Add tooltip
                data.nodeProperty().addListener((ov, oldNode, newNode) -> {
                    if (newNode != null) {
                        Tooltip tooltip = new Tooltip(
                            String.format("%.2f hours", data.getYValue().doubleValue())
                        );
                        Tooltip.install(newNode, tooltip);
                    }
                });
            }
            
            // Set y-axis range based on max usage
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(Math.ceil(maxHours) + 1);
            
            barChart.getData().add(series);
            barChart.setLegendVisible(false);
            
            // Style bars
            series.getData().forEach(data -> 
                data.getNode().setStyle("-fx-bar-fill: #80CBC4;")
            );
            
        } catch (Exception e) {
            System.err.println("Error creating bar chart: " + e.getMessage());
        }
        
        return barChart;
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