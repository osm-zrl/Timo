package com.example.timo.Module;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteConnection {
    private String sqlite_file_url = "jdbc:sqlite:timo.db";
    private Connection conn;

    public static void main(String[] args){
        try{
            SQLiteConnection module = new SQLiteConnection();

            module.storeApplicationLimit("opera",300);
            System.out.println(module.getStoredApplicationLimit("chrome"));
            module.modifyApplicationLimit("chrome", 600);
            System.out.println(module.getStoredApplicationLimit("chrome"));

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public SQLiteConnection() throws Exception{

        // Create a connection to the database
        try {
            // Establish the connection
            this.conn = DriverManager.getConnection(sqlite_file_url);

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

                String createApplicationsLimitTableSQL = "CREATE TABLE IF NOT EXISTS ApplicationsLimit (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT, " +
                    "usage_limit INTEGER NOT_NULL" +
                    ")";
                stmt.executeUpdate(createApplicationsLimitTableSQL);
            }else {
                System.out.println("Error: could not create table");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Get all stored applications ordered by date desc
    public List<ApplicationHistory> getStoredApplications() throws Exception{
        String sql = "SELECT * FROM Applications ORDER BY date DESC";
        List<ApplicationHistory> list = new ArrayList<>();

        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                int id = rs.getInt("id");
                String name = rs.getString("name");
                int duration = rs.getInt("duration");
                String date = rs.getString("date");

                ApplicationHistory test = new ApplicationHistory(id,name,date,duration);
                list.add(test);

            }


        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return list;
    }

    //Get all stored applications With a specific date
    public ArrayList<ApplicationHistory> getDateSpecificStoredApplications(String dateInput) throws Exception{
        String sql = "SELECT * FROM Applications WHERE date = '" + dateInput + "'";
        ArrayList<ApplicationHistory> list = new ArrayList<>();

        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                int id = rs.getInt("id");
                String name = rs.getString("name");
                int duration = rs.getInt("duration");
                String date = rs.getString("date");

                ApplicationHistory test = new ApplicationHistory(id,name,date,duration);
                list.add(test);

            }


        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return list;
    }

    //increment stored app's duration by an amount
    public boolean incrementDurationStoredApplication(int id, int duration) throws Exception{
        String sql = "UPDATE Applications SET duration= duration+? WHERE id=?";

        try (var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1,duration);
            stmt.setInt(2,id);

            stmt.executeUpdate();

            return true;
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    //Retrieve a single stored app
    public ApplicationHistory getStoredApplication(int id) throws Exception{
        String sql = "SELECT * FROM Applications WHERE id=?";

        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1,id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ApplicationHistory(id,rs.getString("name"),rs.getString("date"),rs.getInt("duration"));
            }else{
                return null;
            }
        }
    }


    //ApplicationLimits table's methodes
    public void storeApplicationLimit(String name, int usage_limit) throws Exception {
        String sql = "INSERT INTO ApplicationsLimit (name, usage_limit) VALUES (?, ?)";

        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, usage_limit);
            stmt.executeUpdate();
        }
    }

    public int getStoredApplicationLimit(String name) throws Exception{
        String sql = "SELECT usage_limit FROM ApplicationsLimit WHERE name=?";

        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("usage_limit");
            }else{
                return 0;
            }
        }
    }

    public void modifyApplicationLimit(String name, int usage_limit) throws Exception {
        String sql = "UPDATE ApplicationsLimit SET usage_limit = ? WHERE name = ?";

        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usage_limit);  // Set the new usage limit
            stmt.setString(2, name);      // Set the name to match
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new Exception("No application limit found with the name: " + name);
            }
        }
    }
}
