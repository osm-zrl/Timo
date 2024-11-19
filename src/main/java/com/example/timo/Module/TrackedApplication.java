package com.example.timo.Module;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;


@Getter
@Setter
@ToString
public class TrackedApplication extends ProcessInfo{

    private Integer id;
    private Duration totalDuration;
    private Duration durationLimit;

    public TrackedApplication(Integer id,String name, int pid, double memory, double cpu, Duration duration) {
        super(name, pid, memory, cpu, duration);
        this.id = id;
    }

    //Constructor with ProcessInfo class as param
    public TrackedApplication(ProcessInfo processInfo){
        this(null,processInfo.getName(), processInfo.getPid(), processInfo.getMemory(), processInfo.getCpu(), processInfo.getDuration());
    }

    public void resetDuration(){this.duration = Duration.ZERO;}

    public void addDuration(){
        this.totalDuration = this.totalDuration.plus(duration);
        this.resetDuration();
    }

    public boolean checkDurationLimit(){
        return this.totalDuration.compareTo(this.durationLimit) > 0;
    }

}
