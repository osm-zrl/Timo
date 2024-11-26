package com.example.timo.Controller;

import com.example.timo.HelloApplication;
import com.example.timo.Module.ApplicationHistory;
import com.example.timo.Module.ProcessInfo;
import com.example.timo.Module.SQLiteConnection;
import com.example.timo.Module.TrackedApplication;
import com.example.timo.process.FrontendProcessLister;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ApplicationsController {
    public ArrayList<TrackedApplication> ApplicationsList = new ArrayList<>();
    public ArrayList<ProcessInfo> ProcessInfoList = new ArrayList<>();
    private SQLiteConnection db;

    public static void main(String[] args) throws Exception {
        ApplicationsController context = new ApplicationsController();
        System.out.println(context.ApplicationsList);
    }

    //Constructor
    public ApplicationsController() throws Exception {

        //Getting Current Processes and converting them to TrackedApplications
        ArrayList<ProcessInfo> currentProcessInfoList = FrontendProcessLister.getProcessList();
        currentProcessInfoList.forEach((processInfo)->{
            ApplicationsList.add(new TrackedApplication(processInfo));
        });

        //Initialize Database Model
        try{
            db = new SQLiteConnection();
        }catch(Exception e){
            System.err.println(e.getMessage());
        }

        //Get stored Applications with today's date
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        ArrayList<ApplicationHistory> storedApplicationsFromToday = db.getDateSpecificStoredApplications(formattedDate);

        //Setting Limits and TotalDuration for today's use of each Application from stored data
        ApplicationsList.forEach((application)->{

            for(ApplicationHistory applicationHistory:storedApplicationsFromToday){
                if(application.getName().equals(applicationHistory.getName())){
                    application.setId(applicationHistory.getId());
                    application.setTotalDuration(applicationHistory.getDuration());
                    break;
                }
            }

            try {
                application.setDurationLimit(Duration.ofSeconds(db.getStoredApplicationLimit(application.getName())));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
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
