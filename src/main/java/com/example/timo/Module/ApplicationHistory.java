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
    private Timestamp startTime;
    private Timestamp endTime;

    public ApplicationHistory(int id, String name, String date, int duration, Timestamp startTime, Timestamp end_time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = end_time;
    }

    public ApplicationHistory(TrackedApplication trackedApplication, String date){
        this(
            trackedApplication.getId() == null ? 0 : trackedApplication.getId(),
            trackedApplication.getName(),
            date,
            (int)trackedApplication.getTotalDuration().toSeconds(),
            // Format timestamp in readable format
            Timestamp.valueOf(java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
            Timestamp.valueOf(java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        );
    }
}
