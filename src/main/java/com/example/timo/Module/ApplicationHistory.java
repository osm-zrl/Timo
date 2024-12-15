package com.example.timo.Module;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;

@Setter
@ToString
@Getter
public class ApplicationHistory {

    private int id;
    private String name;
    private String date;
    private Integer duration;

    public ApplicationHistory(int id, String name, String date, int duration) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.duration = duration;
    }

    public ApplicationHistory(TrackedApplication trackedApplication, String date){
        this(trackedApplication.getId()==null ? 0 : trackedApplication.getId(), trackedApplication.getName(),date,(int)trackedApplication.getTotalDuration().toSeconds());
    }
}
