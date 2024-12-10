package com.example.timo.Controller;

import com.example.timo.Module.ApplicationHistory;
import com.example.timo.Module.ProcessInfo;
import com.example.timo.Module.DatabaseModule;
import com.example.timo.Module.TrackedApplication;
import com.example.timo.process.FrontendProcessLister;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class ApplicationsController {
    public ArrayList<TrackedApplication> ApplicationsList = new ArrayList<>();
    private DatabaseModule dbModule;

    public static void main(String[] args) throws Exception {
        ApplicationsController context = new ApplicationsController();
        context.ListTrackedApplication();
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
            dbModule = new DatabaseModule();
        }catch(Exception e){
            System.err.println(e.getMessage());
        }


        //Get stored Applications with today's date
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        ArrayList<ApplicationHistory> storedApplicationsFromToday = dbModule.getDateSpecificStoredApplications(formattedDate);

        //Setting Limits and TotalDuration for today's use of each Application from stored data
        ApplicationsList.forEach((application)->{

            for(ApplicationHistory applicationHistory:storedApplicationsFromToday){
                if(application.getName().equals(applicationHistory.getName())){
                    application.setId(applicationHistory.getId());
                    application.setTotalDuration(application.getDuration().plus(Duration.ofSeconds(applicationHistory.getDuration())));
                    break;
                }
            }

            try {
                application.setDurationLimit(Duration.ofSeconds(dbModule.getStoredApplicationLimit(application.getName())));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

    //Killing TrackedApplications
    public void KillTrackedApp(TrackedApplication trackedApplication){

        if(trackedApplication.getId()!=null){
            try {
                dbModule.incrementDurationStoredApplication(trackedApplication.getId(), trackedApplication.getTotalDuration().toSeconds());
            } catch (Exception e) {
                System.err.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }else{
            // Get today's date
            LocalDate today = LocalDate.now();

            // Define the desired format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Format the date to 'yyyy-MM-dd' format
            String formattedDate = today.format(formatter);

            try {
                dbModule.insertApplication(new ApplicationHistory(trackedApplication,formattedDate));
            } catch (Exception e) {
                System.err.println("error inserting new application in database while terminating TA "+e.getMessage());
                throw new RuntimeException(e);
            }

        }

        System.out.println("Application with name " + trackedApplication.getName()+" Terminated");

    }

    //Update Tracked Applications List
    public void updateProcesses() {
        ArrayList<ProcessInfo> currentProcesses = FrontendProcessLister.getProcessList();
        ArrayList<Integer> processesWithTrackedAppIndexes = new ArrayList<>();
        ArrayList<TrackedApplication> trackedApplicationsToTerminate = new ArrayList<>();

        // Updating existing Tracked Applications
        ApplicationsList.forEach(application -> {
            boolean applicationRunning = false;

            for (ProcessInfo processInfo : currentProcesses) {
                if (application.getName().equals(processInfo.getName())) {
                    applicationRunning = true;
                    processesWithTrackedAppIndexes.add(currentProcesses.indexOf(processInfo));

                    // Getting passed duration from last check
                    Duration addedDuration = processInfo.getDuration().minus(application.getDuration());

                    // Updating properties
                    application.setCpu(processInfo.getCpu());
                    application.setMemory(processInfo.getMemory());
                    application.addDuration(addedDuration);
                    application.setDuration(processInfo.getDuration());

                    if (application.checkDurationLimit()) {
                        System.out.println("Usage limit reached for " + application.getName());
                        // Handle user alert here if needed
                    }

                    break;
                }
            }

            if (!applicationRunning) {
                trackedApplicationsToTerminate.add(application);
                KillTrackedApp(application);
            }
        });


        // Removing terminated trackedApplications
        ApplicationsList.removeAll(trackedApplicationsToTerminate);

        // Removing processed items from currentProcesses
        ArrayList<ProcessInfo> remainingProcesses = new ArrayList<>();
        for (int i = 0; i < currentProcesses.size(); i++) {
            if (!processesWithTrackedAppIndexes.contains(i)) {
                remainingProcesses.add(currentProcesses.get(i));
            }
        }

        // Starting monitoring for the remaining processes
        try {
            startMonitoringApplications(remainingProcesses);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public void startMonitoringApplications(ArrayList<ProcessInfo> processes) throws Exception{
        for(ProcessInfo processInfo: processes){
            TrackedApplication trackedApplication = new TrackedApplication(processInfo);

            //Adding Usage Duration From Database(Today) if exists
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = currentDate.format(formatter);

            ApplicationHistory applicationHistory = dbModule.getDateSpecificStoredApplication(trackedApplication.getName(),formattedDate);

            if(applicationHistory!=null){
                trackedApplication.setId(applicationHistory.getId());
                trackedApplication.addDuration(Duration.ofSeconds(applicationHistory.getDuration()));
            }

            //Getting Limit
            trackedApplication.setDurationLimit(Duration.ofSeconds(dbModule.getStoredApplicationLimit(trackedApplication.getName())));

            ApplicationsList.add(trackedApplication);
        }
    }
    //Viewed current trackedApplications with style
    public void ListTrackedApplication(){
        System.out.println("\n");
        System.out.println("Current Tracked Applications:");
        System.out.printf("%-20s%-15s%-15s%-15s%-15s%-15s%-10s\n", "Name", "CPU", "Memory", "Duration", "Limit", "Total Duration", "ID");

        for(TrackedApplication trackedApplication: ApplicationsList){
            System.out.printf("%-20s%-15s%-15s%-15s%-15s%-15s%-10s\n", trackedApplication.getName(), trackedApplication.getCpu(), trackedApplication.getMemory(), trackedApplication.formatDuration(trackedApplication.getDuration()), trackedApplication.formatDuration(trackedApplication.getDurationLimit()), trackedApplication.formatDuration(trackedApplication.getTotalDuration()), trackedApplication.getId());
        }
    }
}