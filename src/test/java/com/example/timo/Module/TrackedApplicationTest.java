package com.example.timo.Module;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

class TrackedApplicationTest {
    
    @Test
    void testConstructorWithProcessInfo() {
        // Arrange
        ProcessInfo processInfo = new ProcessInfo("TestApp", 100, 256.0, 10.0, Duration.ofMinutes(30));
        
        // Act
        TrackedApplication trackedApp = new TrackedApplication(processInfo);
        
        // Assert
        assertNull(trackedApp.getId(), "ID should be null when created from ProcessInfo");
        assertEquals("TestApp", trackedApp.getName(), "Name should match ProcessInfo");
        assertEquals(100, trackedApp.getPid(), "PID should match ProcessInfo");
        assertEquals(256.0, trackedApp.getMemory(), "Memory should match ProcessInfo");
        assertEquals(10.0, trackedApp.getCpu(), "CPU should match ProcessInfo");
        assertEquals(Duration.ofMinutes(30), trackedApp.getDuration(), "Duration should match ProcessInfo");
    }
    
    @Test
    void testResetDuration() {
        // Arrange
        TrackedApplication trackedApp = new TrackedApplication(1, "TestApp", 100, 256.0, 10.0, Duration.ofMinutes(30));
        
        // Act
        trackedApp.resetDuration();
        
        // Assert
        assertEquals(Duration.ZERO, trackedApp.getDuration(), "Duration should be reset to zero");
    }
    
    @Test
    void testAddDuration() {
        // Arrange
        TrackedApplication trackedApp = new TrackedApplication(1, "TestApp", 100, 256.0, 10.0, Duration.ofMinutes(30));
        trackedApp.setTotalDuration(Duration.ofHours(1)); // Set initial total duration
        
        // Act
        trackedApp.addDuration();
        
        // Assert
        assertEquals(Duration.ofHours(1).plus(Duration.ofMinutes(30)), trackedApp.getTotalDuration(), 
            "Total duration should be increased by current duration");
        assertEquals(Duration.ZERO, trackedApp.getDuration(), 
            "Current duration should be reset after adding to total");
    }
    
    @Test
    void testCheckDurationLimit() {
        // Arrange
        TrackedApplication trackedApp = new TrackedApplication(1, "TestApp", 100, 256.0, 10.0, Duration.ofMinutes(30));
        trackedApp.setTotalDuration(Duration.ofHours(3));
        trackedApp.setDurationLimit(Duration.ofHours(2));
        
        // Act & Assert
        assertTrue(trackedApp.checkDurationLimit(), "Should return true when total duration exceeds limit");
        
        // Test when under limit
        trackedApp.setTotalDuration(Duration.ofHours(1));
        assertFalse(trackedApp.checkDurationLimit(), "Should return false when total duration is under limit");
    }
}
