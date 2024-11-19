package com.example.timo.Controller;

import com.example.timo.Module.TrackedApplication;

import java.util.ArrayList;

public abstract class ApplicationsController {
    public static ArrayList<TrackedApplication> ApplicationsList = new ArrayList<>();

    public static void AddApplication(TrackedApplication application) {
        ApplicationsList.add(application);
    }
    public static void RemoveApplication(TrackedApplication application) {
        ApplicationsList.remove(application);
    }


}
