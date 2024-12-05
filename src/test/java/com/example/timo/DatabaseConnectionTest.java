package com.example.timo;

import com.example.timo.Database.DatabaseConnection;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

class DatabaseConnectionTest {

    @Test
    void testGetConnection() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        assertNotNull(conn, "Connection should not be null");
        DatabaseConnection.closeConnection(); // Clean up
    }

    @Test
    void testCloseConnection() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        DatabaseConnection.closeConnection();
        assertTrue(conn.isClosed(), "Connection should be closed");
    }
}
