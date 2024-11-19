package com.example.timo.Module;

import lombok.Getter;

import java.time.Duration;


@Getter
public class ProcessInfo {
    private String name;
    private int pid;
    private double memory;
    private double cpu;
    protected Duration duration; // Changed from String to Duration

    public ProcessInfo(String name, int pid, double memory, double cpu, Duration duration) {
        this.name = name;
        this.pid = pid;
        this.memory = memory;
        this.cpu = cpu;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return String.format("Name: %s, PID: %d, Memory: %.2f MB, CPU: %.2f%%, Duration: %s",
            name, pid, memory, cpu, formatDuration(duration));
    }

    // Helper method to format Duration as hh:mm:ss
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
