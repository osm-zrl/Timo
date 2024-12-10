package com.example.timo.Module;

import lombok.Getter;
import lombok.Setter;
import java.time.Duration;


@Getter
@Setter
public class TrackedApplication extends ProcessInfo{

    private Integer id;
    private Duration totalDuration;
    private Duration durationLimit;

    public TrackedApplication(Integer id,String name, Integer pid, double memory, double cpu, Duration duration) {
        super(name, pid, memory, cpu, duration);
        this.id = id;
        this.durationLimit = duration;
    }

    //Constructor with ProcessInfo class as param
    public TrackedApplication(ProcessInfo processInfo){
        this(null,processInfo.getName(), processInfo.getPid(), processInfo.getMemory(), processInfo.getCpu(), processInfo.getDuration());
        this.totalDuration = processInfo.getDuration();
    }

    //Constructor with ApplicationHistory Class as Param
    public TrackedApplication(ApplicationHistory applicationHistory){
        this(applicationHistory.getId(), applicationHistory.getName(), null,0,0,Duration.ZERO);
        this.totalDuration = Duration.ofSeconds(applicationHistory.getDuration());

    }

    public void resetDuration(){this.duration = Duration.ZERO;}

    public void addDuration(Duration addedDuration){
        this.totalDuration = this.totalDuration.plus(addedDuration);
    }

    public boolean checkDurationLimit(){
        if(this.durationLimit == Duration.ZERO){
            return false;
        }
        return this.totalDuration.toSeconds()>this.durationLimit.toSeconds();
    }

    @Override
    public String toString() {
        return "TrackedApplication{" +
            "name='" + name + '\'' + // Name is often important for identifying the application
            ", cpu=" + cpu +          // CPU usage is critical for performance monitoring
            ", memory=" + memory +    // Memory usage is also essential for performance analysis
            ", duration=" + formatDuration(duration) + // Duration of the application run
            ", totalDuration=" + formatDuration(totalDuration) + // Total running duration
            ", durationLimit=" + formatDuration(durationLimit) + // Duration limit
            ", id=" + id +            // ID can be useful but usually less critical in the context of monitoring
            '}';
    }

    public String formatDuration(Duration duration) {
        if (duration == null) {
            return "00:00:00";
        }

        long seconds = duration.getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        // Format the result as hh:mm:ss
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }
}
