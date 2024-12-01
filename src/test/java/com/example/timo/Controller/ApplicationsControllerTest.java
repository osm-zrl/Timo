package com.example.timo.Controller;

import com.example.timo.Module.TrackedApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

class ApplicationsControllerTest {
    
    private TrackedApplication testApp1;
    private TrackedApplication testApp2;
    
    @BeforeEach
    void setUp() {
        // Clear the static list before each test
        ApplicationsController.ApplicationsList.clear();
        
        // Create test applications
        testApp1 = new TrackedApplication(1, "TestApp1", 100, 256.0, 10.0, Duration.ofMinutes(30));
        testApp2 = new TrackedApplication(2, "TestApp2", 200, 512.0, 20.0, Duration.ofMinutes(45));
    }
    
    @Test
    void testAddApplication() {
        // Act
        ApplicationsController.AddApplication(testApp1);
        
        // Assert
        assertEquals(1, ApplicationsController.ApplicationsList.size(), "List should contain one application");
        assertTrue(ApplicationsController.ApplicationsList.contains(testApp1), "List should contain testApp1");
    }
    
    @Test
    void testAddMultipleApplications() {
        // Act
        ApplicationsController.AddApplication(testApp1);
        ApplicationsController.AddApplication(testApp2);
        
        // Assert
        assertEquals(2, ApplicationsController.ApplicationsList.size(), "List should contain two applications");
        assertTrue(ApplicationsController.ApplicationsList.contains(testApp1), "List should contain testApp1");
        assertTrue(ApplicationsController.ApplicationsList.contains(testApp2), "List should contain testApp2");
    }
    
    @Test
    void testRemoveApplication() {
        // Arrange
        ApplicationsController.AddApplication(testApp1);
        ApplicationsController.AddApplication(testApp2);
        
        // Act
        ApplicationsController.RemoveApplication(testApp1);
        
        // Assert
        assertEquals(1, ApplicationsController.ApplicationsList.size(), "List should contain one application");
        assertFalse(ApplicationsController.ApplicationsList.contains(testApp1), "List should not contain testApp1");
        assertTrue(ApplicationsController.ApplicationsList.contains(testApp2), "List should still contain testApp2");
    }
    
    @Test
    void testRemoveNonExistentApplication() {
        // Arrange
        ApplicationsController.AddApplication(testApp1);
        TrackedApplication nonExistentApp = new TrackedApplication(3, "NonExistent", 300, 128.0, 5.0, Duration.ofMinutes(15));
        
        // Act
        ApplicationsController.RemoveApplication(nonExistentApp);
        
        // Assert
        assertEquals(1, ApplicationsController.ApplicationsList.size(), "List size should remain unchanged");
        assertTrue(ApplicationsController.ApplicationsList.contains(testApp1), "List should still contain testApp1");
    }
}
