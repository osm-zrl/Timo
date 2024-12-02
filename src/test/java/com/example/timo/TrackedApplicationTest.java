package com.example.timo;

import com.example.timo.Module.TrackedApplication;
import com.example.timo.Module.ProcessInfo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

class TrackedApplicationTest {

    @Test
    void testConstructors() {
        ProcessInfo processInfo = new ProcessInfo("TestApp", 1234, 100.0, 10.0, Duration.ofSeconds(30));
        TrackedApplication trackedApp = new TrackedApplication(processInfo);
        
        assertEquals("TestApp", trackedApp.getName());
        assertEquals(1234, trackedApp.getPid());
        assertEquals(Duration.ofSeconds(30), trackedApp.getDuration());
    }

    @Test
    void testAddDuration() {
        TrackedApplication trackedApp = new TrackedApplication(1, "TestApp", 1234, 100.0, 10.0, Duration.ofSeconds(30));
        trackedApp.addDuration(Duration.ofSeconds(10));
        
        assertEquals(Duration.ofSeconds(10), trackedApp.getTotalDuration());
    }

    @Test
    void testResetDuration() {
        TrackedApplication trackedApp = new TrackedApplication(1, "TestApp", 1234, 100.0, 10.0, Duration.ofSeconds(30));
        trackedApp.addDuration(Duration.ofSeconds(10));
        trackedApp.resetDuration();
        
        assertEquals(Duration.ZERO, trackedApp.getDuration());
    }

    @Test
    void testCheckDurationLimit() {
        TrackedApplication trackedApp = new TrackedApplication(1, "TestApp", 1234, 100.0, 10.0, Duration.ofSeconds(30));
        trackedApp.setDurationLimit(Duration.ofSeconds(20));
        trackedApp.addDuration(Duration.ofSeconds(25));
        
        assertTrue(trackedApp.checkDurationLimit(), "Duration limit should be exceeded");
    }

    @Test
    void testToString() {
        TrackedApplication trackedApp = new TrackedApplication(1, "TestApp", 1234, 100.0, 10.0, Duration.ofSeconds(30));
        String expectedString = "TrackedApplication{name='TestApp', cpu=10.0, memory=100.0, duration=00:00:30, totalDuration=00:00:00, durationLimit=00:00:00, id=1}";
        
        assertEquals(expectedString, trackedApp.toString());
    }
}
