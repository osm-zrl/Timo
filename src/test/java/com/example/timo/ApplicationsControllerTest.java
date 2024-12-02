package com.example.timo;

import com.example.timo.Controller.ApplicationsController;
import com.example.timo.Module.ProcessInfo;
import com.example.timo.Module.TrackedApplication;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.ArrayList;

class ApplicationsControllerTest {

    @Test
    void testInitialization() throws Exception {
        ApplicationsController controller = new ApplicationsController();
        
        assertFalse(controller.ApplicationsList.isEmpty(), "ApplicationsList should not be empty after initialization");
    }

    @Test
    void testUpdateProcesses() throws Exception {
        ApplicationsController controller = new ApplicationsController();
        
        // Simulate adding a process
        ProcessInfo processInfo = new ProcessInfo("TestApp", 1234, 100.0, 10.0, Duration.ofSeconds(30));
        controller.ApplicationsList.add(new TrackedApplication(processInfo));
        
        // Call updateProcesses
        controller.updateProcesses();
        
        // Check if the application is still tracked
        assertFalse(controller.ApplicationsList.isEmpty(), "ApplicationsList should still contain applications after update");
    }
}
