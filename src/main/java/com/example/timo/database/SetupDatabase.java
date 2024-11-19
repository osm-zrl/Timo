package com.example.timo.database;

import java.sql.Connection;
import java.sql.Statement;

public class SetupDatabase {
    /// we can add and update the database tables for later
    public static void createTables() {
        String createAppsTable = """
            CREATE TABLE IF NOT EXISTS apps (
                app_id INTEGER PRIMARY KEY AUTOINCREMENT,
                app_name TEXT NOT NULL
            );
        """;

        String createUsageLogsTable = """
            CREATE TABLE IF NOT EXISTS usage_logs (
                log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                app_id INTEGER NOT NULL,
                start_time TEXT NOT NULL,
                end_time TEXT,
                FOREIGN KEY (app_id) REFERENCES apps (app_id)
            );
        """;

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createAppsTable);
            stmt.execute(createUsageLogsTable);
            System.out.println("Tables created successfully.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
    }

}
