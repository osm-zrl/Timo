package com.example.timo.process;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FrontendProcessLister {

    public static ProcessInfo[] getProcessList() {
        // PowerShell command to get process name, ID, memory (MB), CPU usage, and running duration (hh:mm:ss)
        String command = "powershell.exe -Command \"Get-Process | Where-Object { $_.MainWindowHandle -ne 0 -and $_.Name -notin @('TextInputHost', 'ApplicationFrameHost') } | ForEach-Object { '{0},{1},{2:N2},{3:N2},{4}' -f $_.Name, $_.Id, ($_.WorkingSet64 / 1MB), $_.CPU, ((Get-Date) - $_.StartTime).ToString('hh\\:mm\\:ss') }\"";

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
                if (details.length == 5) {
                    String name = details[0].replace("\"", "").trim();
                    int id = Integer.parseInt(details[1].trim());
                    double memory = Double.parseDouble(details[2].trim());
                    double cpu = Double.parseDouble(details[3].trim());
                    String durationString = details[4].trim(); // Running duration in hh:mm:ss format

                    // Parse the duration string into a Duration object
                    String[] timeParts = durationString.split(":");
                    int hours = Integer.parseInt(timeParts[0]);
                    int minutes = Integer.parseInt(timeParts[1]);
                    int seconds = Integer.parseInt(timeParts[2]);
                    Duration duration = Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);

                    // Create ProcessInfo object and add to the list
                    ProcessInfo processInfo = new ProcessInfo(name, id, memory, cpu, duration);
                    processList.add(processInfo);
                }
            }

            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // Convert List to Array and return
        return processList.toArray(new ProcessInfo[0]);
    }
}
