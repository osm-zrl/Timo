package com.example.timo.Module;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

class ProcessInfoTest {
    
    @Test
    void testProcessInfoConstructorAndGetters() {
        // Arrange
        String name = "TestProcess";
        int pid = 123;
        double memory = 256.0;
        double cpu = 25.5;
        Duration duration = Duration.ofMinutes(5);
        
        // Act
        ProcessInfo processInfo = new ProcessInfo(name, pid, memory, cpu, duration);
        
        // Assert
        assertEquals(name, processInfo.getName(), "Process name should match");
        assertEquals(pid, processInfo.getPid(), "Process ID should match");
        assertEquals(memory, processInfo.getMemory(), "Memory usage should match");
        assertEquals(cpu, processInfo.getCpu(), "CPU usage should match");
        assertEquals(duration, processInfo.getDuration(), "Duration should match");
    }
    
    @Test
    void testProcessInfoWithZeroValues() {
        // Arrange & Act
        ProcessInfo processInfo = new ProcessInfo("EmptyProcess", 0, 0.0, 0.0, Duration.ZERO);
        
        // Assert
        assertEquals(0.0, processInfo.getMemory(), "Memory should be zero");
        assertEquals(Duration.ZERO, processInfo.getDuration(), "Duration should be zero");
    }

    @Test
    void testToStringFormat() {
        // Arrange
        String name = "TestApp";
        int pid = 1234;
        double memory = 512.5;
        double cpu = 45.7;
        Duration duration = Duration.ofHours(2).plusMinutes(30).plusSeconds(15);
        ProcessInfo processInfo = new ProcessInfo(name, pid, memory, cpu, duration);
        
        // Act
        String result = processInfo.toString();
        
        // Assert
        assertTrue(result.contains("Name: TestApp"), "Should contain process name");
        assertTrue(result.contains("PID: 1234"), "Should contain PID");
        assertTrue(result.contains("Memory: 512.50 MB"), "Should contain memory");
        assertTrue(result.contains("CPU: 45.70%"), "Should contain CPU percentage");
        assertTrue(result.contains("Duration: 02:30:15"), "Should contain formatted duration");
    }
}
