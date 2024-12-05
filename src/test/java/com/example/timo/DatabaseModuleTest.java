package com.example.timo;

import com.example.timo.Module.DatabaseModule;
import com.example.timo.Module.ApplicationHistory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class DatabaseModuleTest {

    @Test
    void testDatabaseInitialization() throws Exception {
        DatabaseModule module = new DatabaseModule();
        assertNotNull(module.getStoredApplications(), "Database should be initialized and return applications");
    }

    @Test
    void testStoreApplicationLimit() throws Exception {
        DatabaseModule module = new DatabaseModule();
        module.storeApplicationLimit("TestApp", 300);
        int limit = module.getStoredApplicationLimit("TestApp");
        assertEquals(300, limit, "Stored application limit should match the expected value");
    }

    @Test
    void testGetStoredApplications() throws Exception {
        DatabaseModule module = new DatabaseModule();
        List<ApplicationHistory> applications = module.getStoredApplications();
        assertNotNull(applications, "Stored applications should not be null");
    }

    @Test
    void testModifyApplicationLimit() throws Exception {
        DatabaseModule module = new DatabaseModule();
        module.storeApplicationLimit("TestApp", 300);
        module.modifyApplicationLimit("TestApp", 600);
        int limit = module.getStoredApplicationLimit("TestApp");
        assertEquals(600, limit, "Application limit should be updated correctly");
    }
}
