package com.example.timo.Controller;

import com.example.timo.HelloApplication;
import com.example.timo.Module.ApplicationHistory;
import com.example.timo.Module.ProcessInfo;
import com.example.timo.Module.TrackedApplication;
import com.example.timo.process.FrontendProcessLister;

import java.util.ArrayList;

public class ApplicationsController {
    public ArrayList<TrackedApplication> ApplicationsList = new ArrayList<>();
    public ArrayList<ProcessInfo> ProcessInfoList = new ArrayList<>();

    public ApplicationsController() throws Exception {
        ArrayList<ProcessInfo> currentProcessInfoList = FrontendProcessLister.getProcessList();
        ArrayList<ApplicationHistory> storedApplications =  HelloApplication.db.getDateSpecificStoredApplications("2024-11-26");
    }

    public void AddApplication(TrackedApplication application) {
        ApplicationsList.add(application);
    }
    public void RemoveApplication(TrackedApplication application) {
        ApplicationsList.remove(application);
    }

    public void StartMonitoring(ProcessInfo processInfo){
         ApplicationsList.add(new TrackedApplication(processInfo));
    }

    public void StopMonitoring(TrackedApplication trackedApplication){
        //
    }

    public boolean checkDurationLimit(TrackedApplication trackedApplication){
        return trackedApplication.checkDurationLimit();
    }

    public void updateProcesses(){
        //Getting the current running processes
        ArrayList<ProcessInfo> currentProcessInfoList = FrontendProcessLister.getProcessList();



    }
}
