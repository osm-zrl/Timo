package com.example.timo.Controller;

import com.example.timo.Module.ProcessInfo;
import com.example.timo.process.FrontendProcessLister;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class VueController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        ProcessInfo[] processes = FrontendProcessLister.getProcessList();

        System.out.println("Liste des processus avec interface graphique :");
        for (ProcessInfo processInfo : processes) {
            System.out.println(processInfo.getPid());
        }

    }
}
