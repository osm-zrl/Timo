package com.example.timo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class HelloApplicationTest {

    @Test
    public void testApplicationInitialization() {
        // Test the initialization of the HelloApplication
        HelloApplication app = new HelloApplication();
        assertNotNull(app);
    }

    // Additional tests for database connection can be added here
}
