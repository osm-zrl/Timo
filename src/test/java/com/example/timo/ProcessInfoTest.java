package com.example.timo;

import com.example.timo.Module.ProcessInfo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

class ProcessInfoTest {

    @Test
    void testConstructor() {
        ProcessInfo processInfo = new ProcessInfo("TestApp", 1234, 100.0, 10.0, Duration.ofSeconds(30));
        
        assertEquals("TestApp", processInfo.getName());
        assertEquals(1234, processInfo.getPid());
        assertEquals(100.0, processInfo.getMemory());
        assertEquals(10.0, processInfo.getCpu());
        assertEquals(Duration.ofSeconds(30), processInfo.getDuration());
    }

    @Test
    void testSettersAndGetters() {
        ProcessInfo processInfo = new ProcessInfo("TestApp", 1234, 100.0, 10.0, Duration.ofSeconds(30));
        
        processInfo.setMemory(200.0);
        processInfo.setCpu(20.0);
        
        assertEquals(200.0, processInfo.getMemory());
        assertEquals(20.0, processInfo.getCpu());
    }

    @Test
    void testToString() {
        ProcessInfo processInfo = new ProcessInfo("TestApp", 1234, 100.0, 10.0, Duration.ofSeconds(30));
        
        String expectedString = "Name: TestApp, PID: 1234, Memory: 100.00 MB, CPU: 10.00%, Duration: 00:00:30";
        assertEquals(expectedString, processInfo.toString());
    }
}
