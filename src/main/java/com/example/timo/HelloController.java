package com.example.timo;

import com.example.timo.process.FrontendProcessLister;
import com.example.timo.process.ProcessInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        /*ProcessInfo[] processes = FrontendProcessLister.getProcessList();

        System.out.println("Liste des processus avec interface graphique :");
        for (ProcessInfo processInfo : processes) {
            System.out.println(processInfo);
        }*/

        System.out.println(HelloApplication.db);
    }
}
