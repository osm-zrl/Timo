package com.example.timo.Module;

import com.example.timo.Database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    "date TEXT CHECK (date = strftime('%Y-%m-%d', date))" +
                    ")";
                stmt.executeUpdate(createApplicationsTableSQL);

                String createApplicationsUsageLimitTableSQL = "CREATE TABLE IF NOT EXISTS ApplicationsUsageLimit (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT, " +
                    "usage_limit INTEGER NOT NULL" +
                    ")";
                stmt.executeUpdate(createApplicationsUsageLimitTableSQL);

                String createSystemUptimeTableSQL = "CREATE TABLE IF NOT EXISTS SystemUptime (" +
                    "id INTEGER PRIMARY KEY, " +
                    "date TEXT CHECK (date = strftime('%Y-%m-%d', date)), " +
                    "uptime_seconds INTEGER, " +
                    "last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
                    ")";
                stmt.executeUpdate(createSystemUptimeTableSQL);
            } else {
                System.out.println("Error: could not create table");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateDailyUptime(String date, long uptimeSeconds) {
        // First check if we need to update
        String checkSql = "SELECT uptime_seconds, strftime('%s', 'now') - strftime('%s', last_update) as seconds_since_update " +
                         "FROM SystemUptime WHERE date = ?";
        String insertSql = "INSERT OR REPLACE INTO SystemUptime (date, uptime_seconds, last_update) " +
                          "VALUES (?, ?, datetime('now', 'localtime'))";
                          
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Begin transaction
    
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, date);
                ResultSet rs = checkStmt.executeQuery();
                
                boolean shouldUpdate = true;
                if (rs.next()) {
                    long secondsSinceLastUpdate = rs.getLong("seconds_since_update");
                    long lastUptime = rs.getLong("uptime_seconds");
                    
                    // Update only if:
                    // 1. More than 5 minutes since last update OR
                    // 2. Uptime changed significantly
                    shouldUpdate = secondsSinceLastUpdate > 300 || 
                                 Math.abs(uptimeSeconds - lastUptime) > 60;
                }
                
                if (shouldUpdate) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, date);
                        insertStmt.setLong(2, uptimeSeconds);
                        insertStmt.executeUpdate();
                    }
                }
            }
            
            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            System.err.println("Error updating uptime: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Map<String, Long> getWeeklyUptime() {
        Map<String, Long> uptimeData = new HashMap<>();
        String sql = "SELECT date, uptime_seconds FROM SystemUptime " +
                    "WHERE date >= date('now', '-6 days') " +
                    "ORDER BY date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String date = rs.getString("date");
                long seconds = rs.getLong("uptime_seconds");
                System.out.println("Retrieved uptime for " + date + ": " + seconds + " seconds"); // Debug log
                uptimeData.put(date, seconds);
            }
        } catch (SQLException e) {
            System.err.println("Error getting weekly uptime: " + e.getMessage());
        }
        return uptimeData;
    }

    public void cleanupOldRecords() {
        String sql = "DELETE FROM SystemUptime WHERE date < date('now', '-7 days')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int deletedRows = stmt.executeUpdate();
            System.out.println("Cleaned up " + deletedRows + " old uptime records");
        } catch (SQLException e) {
            System.err.println("Error cleaning up old records: " + e.getMessage());
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

                ApplicationHistory app = new ApplicationHistory(id, name, date, duration);
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
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int duration = rs.getInt("duration");
                    String date = rs.getString("date");

                    ApplicationHistory app = new ApplicationHistory(id, name, date, duration);
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

                    // Create a new ApplicationHistory object
                    app = new ApplicationHistory(id, appName, date, duration);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return app;
    }

    // Increment stored application's duration by an amount
    public boolean incrementDurationStoredApplication(int id, long duration) throws Exception {
        String sql = "UPDATE Applications SET duration = ? WHERE id = ?";

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
                    return new ApplicationHistory(id, rs.getString("name"), rs.getString("date"), rs.getInt("duration"));
                } else {
                    return null;
                }
            }
        }
    }

    //Store new ApplicationHistory in database
    public void insertApplication(ApplicationHistory applicationHistory) throws SQLException {
        // SQL query to insert a new row into the Applications table
        String sql = "INSERT INTO Applications (name, duration, date) VALUES (?, ?, ?)";

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
}