package com.example.timo.process;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class FrontendProcesses {
    public static void main(String[] args) {
        String command = "powershell.exe -Command \"Get-Process | Where-Object { $_.MainWindowHandle -ne 0 -and $_.Name -notin @('TextInputHost', 'ApplicationFrameHost') } | ForEach-Object { '{0},{1},{2:N2},{3:N2}' -f $_.Name, $_.Id, ($_.WorkingSet64 / 1MB), $_.CPU }\"";

        List<ProcessInfo> processList = new ArrayList<>();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                // Split the output line by commas to extract process info
                String[] details = line.split(",");
                if (details.length == 4) {
                    String name = details[0].replace("\"", "").trim();
                    int id = Integer.parseInt(details[1].trim());
                    double memory = Double.parseDouble(details[2].trim());
                    double cpu = Double.parseDouble(details[3].trim());

                    // Create ProcessInfo object and add to the list
                    ProcessInfo processInfo = new ProcessInfo(name, id, memory, cpu);
                    processList.add(processInfo);
                }
            }

            process.waitFor();

            // Display the list of processes
            System.out.println("Liste des processus avec interface graphique :");
            for (ProcessInfo processInfo : processList) {
                System.out.println(processInfo);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
