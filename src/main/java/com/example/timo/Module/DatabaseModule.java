package com.example.timo.Module;

import com.example.timo.Database.DatabaseConnection;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DatabaseModule {

    public static void main(String[] args){
        try{
            DatabaseModule module = new DatabaseModule();

            module.storeApplicationLimit("opera", 300);
            System.out.println(module.getStoredApplicationLimit("chrome"));
            module.modifyApplicationLimit("chrome", 600);
            System.out.println(module.getStoredApplicationLimit("chrome"));
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public DatabaseModule() throws Exception {
        // Create a connection to the database
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                // Create a Statement object to execute queries
                Statement stmt = conn.createStatement();

                String createApplicationsTableSQL = "CREATE TABLE IF NOT EXISTS Applications (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT, " +
                    "duration INTEGER, " +
                    "date TEXT CHECK (date = strftime('%Y-%m-%d', date))," +
                    "start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "end_time TIMESTAMP" +
                    ")";
                stmt.executeUpdate(createApplicationsTableSQL);

                String createApplicationsUsageLimitTableSQL = "CREATE TABLE IF NOT EXISTS ApplicationsUsageLimit (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT, " +
                    "usage_limit INTEGER NOT NULL" +
                    ")";
                stmt.executeUpdate(createApplicationsUsageLimitTableSQL);
            } else {
                System.out.println("Error: could not create table");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Get all stored applications ordered by date desc
    public List<ApplicationHistory> getStoredApplications() throws Exception {
        String sql = "SELECT * FROM Applications ORDER BY date DESC";
        List<ApplicationHistory> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int duration = rs.getInt("duration");
                String date = rs.getString("date");
                Timestamp start_time = rs.getTimestamp("start_time");
                Timestamp end_time = rs.getTimestamp("end_time");

                ApplicationHistory app = new ApplicationHistory(id, name, date, duration, start_time, end_time);
                list.add(app);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return list;
    }

    // Get all stored applications with a specific date
    public ArrayList<ApplicationHistory> getDateSpecificStoredApplications(String dateInput) throws Exception {
        String sql = "SELECT * FROM Applications WHERE date = ?";
        ArrayList<ApplicationHistory> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dateInput);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ApplicationHistory app = new ApplicationHistory(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getInt("duration"),
                        rs.getTimestamp("start_time"),
                        rs.getTimestamp("end_time")
                    );
                    list.add(app);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return list;
    }

    //Get specifc record of application of specific date
    public ApplicationHistory getDateSpecificStoredApplication(String name, String dateInput) throws Exception {
        String sql = "SELECT * FROM Applications WHERE name = ? AND date = ?";
        ApplicationHistory app = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the parameters for the prepared statement
            stmt.setString(1, name);
            stmt.setString(2, dateInput);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Retrieve data from the result set
                    int id = rs.getInt("id");
                    String appName = rs.getString("name");
                    int duration = rs.getInt("duration");
                    String date = rs.getString("date");
                    Timestamp start_time = rs.getTimestamp("start_time");
                    Timestamp end_time = rs.getTimestamp("end_time");

                    // Create a new ApplicationHistory object
                    app = new ApplicationHistory(id, appName, date, duration, start_time, end_time);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return app;
    }

    // Increment stored application's duration by an amount
    public boolean incrementDurationStoredApplication(int id, long duration) throws Exception {
        String sql = "UPDATE Applications SET duration = ?, end_time = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1,(int)duration);
            stmt.setInt(2, id);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // Retrieve a single stored application
    public ApplicationHistory getStoredApplication(int id) throws Exception {
        String sql = "SELECT * FROM Applications WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ApplicationHistory(id, rs.getString("name"),
                     rs.getString("date"), rs.getInt("duration"),
                     rs.getTimestamp("start_time"), rs.getTimestamp("end_time"));
                } else {
                    return null;
                }
            }
        }
    }

    //Store new ApplicationHistory in database
    public void insertApplication(ApplicationHistory applicationHistory) throws SQLException {
        // SQL query to insert a new row into the Applications table
        String sql = "INSERT INTO Applications (name, duration, date, start_time, end_time) VALUES (?, ?, ?, datetime('now', 'localtime'), datetime('now', 'localtime'))";

        // Establish the database connection and execute the insert query
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the values for the placeholder parameters
            pstmt.setString(1, applicationHistory.getName());  // Set application name
            pstmt.setInt(2, applicationHistory.getDuration());  // Set duration
            pstmt.setString(3, applicationHistory.getDate());   // Set date (formatted as 'YYYY-MM-DD')

            // Execute the update (insert)
            pstmt.executeUpdate();
            System.out.println("Application inserted successfully!");
        } catch (SQLException e) {
            System.err.println("Error inserting application: " + e.getMessage());
        }
    }

    // Store application limit in the ApplicationsUsageLimit table
    public void storeApplicationLimit(String name, int usage_limit) throws Exception {
        String sql = "INSERT INTO ApplicationsUsageLimit (name, usage_limit) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setInt(2, usage_limit);
            stmt.executeUpdate();
        }
    }

    // Get stored application limit
    public int getStoredApplicationLimit(String name) throws Exception {
        String sql = "SELECT usage_limit FROM ApplicationsUsageLimit WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("usage_limit");
                } else {
                    return 0;
                }
            }
        }
    }

    // Modify application limit
    public void modifyApplicationLimit(String name, int usage_limit) throws Exception {
        String sql = "UPDATE ApplicationsUsageLimit SET usage_limit = ? WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usage_limit);  // Set the new usage limit
            stmt.setString(2, name);      // Set the name to match
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new Exception("No application limit found with the name: " + name);
            }
        }
    }

    public double getActualUsageHours(String date) {
        try {
            // First get all apps for that date
            String query = "SELECT SUM(duration) as total_duration FROM Applications WHERE date = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, date);
                ResultSet rs = stmt.executeQuery();
    
                if (rs.next()) {
                    // Convert seconds to hours
                    double totalSeconds = rs.getDouble("total_duration");
                    return totalSeconds / 3600.0; // Convert seconds to hours
                }
            }
            return 0.0;
        } catch (SQLException e) {
            System.err.println("Error calculating usage hours: " + e.getMessage());
            return 0.0;
        }
    }

}