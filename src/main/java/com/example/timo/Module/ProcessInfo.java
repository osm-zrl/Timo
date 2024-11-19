package com.example.timo.Module;

import java.time.Duration;

public class ProcessInfo {
    private final String name;
    private final int id;
    private final double memory;
    private final double cpu;
    private final Duration duration; // Changed from String to Duration

    public ProcessInfo(String name, int id, double memory, double cpu, Duration duration) {
        this.name = name;
        this.id = id;
        this.memory = memory;
        this.cpu = cpu;
        this.duration = duration;
    }

    public String getName() { return name; }
    public int getId() { return id; }
    public double getMemory() { return memory; }
    public double getCpu() { return cpu; }
    public Duration getDuration() { return duration; } // Updated getter for Duration

    @Override
    public String toString() {
        return String.format("Name: %s, PID: %d, Memory: %.2f MB, CPU: %.2f%%, Duration: %s",
            name, id, memory, cpu, formatDuration(duration));
    }

    // Helper method to format Duration as hh:mm:ss
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
