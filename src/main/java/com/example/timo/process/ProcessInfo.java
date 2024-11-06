package com.example.timo.process;

public class ProcessInfo {
    private final String name;
    private final int id;
    private final double memory;
    private final double cpu;
    private final String duration; // New field for running duration

    public ProcessInfo(String name, int id, double memory, double cpu, String duration) {
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
    public String getDuration() { return duration; } // Getter for duration

    @Override
    public String toString() {
        return String.format("Name: %s, PID: %d, Memory: %.2f MB, CPU: %.2f%%, Duration: %s", name, id, memory, cpu, duration);
    }
}

