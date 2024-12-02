package com.example.timo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.timo.process.FrontendProcessLister;
import com.example.timo.Module.ProcessInfo;

public class FrontendProcessListerTest {

    @Test
    public void testGetProcessList() {
        // Test the retrieval of the process list
        ProcessInfo[] processes = FrontendProcessLister.getProcessList();
        assertNotNull(processes);
        assertTrue(processes.length > 0, "Process list should not be empty");
    }
}
