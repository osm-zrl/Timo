package com.example.timo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.timo.Module.ApplicationHistory;

import java.time.Duration;

public class ApplicationHistoryTest {

    @Test
    public void testApplicationHistoryCreation() {
        // Test the creation of ApplicationHistory object
        ApplicationHistory appHistory = new ApplicationHistory(1, "TestApp", "2023-01-01", 3600);
        assertNotNull(appHistory);
        assertEquals(1, appHistory.getId());
        assertEquals("TestApp", appHistory.getName());
        assertEquals(Duration.ofSeconds(3600), appHistory.getDuration());
    }
}
