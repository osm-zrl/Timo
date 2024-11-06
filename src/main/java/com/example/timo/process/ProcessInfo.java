package com.example.timo.process;

public class ProcessInfo {
    private final String name;
    private final int id;
    private final double memory;
    private final double cpu;

    public ProcessInfo(String name, int id, double memory, double cpu) {
        this.name = name;
        this.id = id;
        this.memory = memory;
        this.cpu = cpu;
    }

    public String getName() { return name; }
    public int getId() { return id; }
    public double getMemory() { return memory; }
    public double getCpu() { return cpu; }

    @Override
    public String toString() {
        return String.format("Name: %s, PID: %d, Memory: %.2f MB, CPU: %.2f%%", name, id, memory, cpu);
    }
}

