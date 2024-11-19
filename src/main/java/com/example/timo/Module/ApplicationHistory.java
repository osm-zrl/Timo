package com.example.timo.Module;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class ApplicationHistory {
    private int id;
    private String name;
    private LocalDate date;
    private Duration duration;

    public ApplicationHistory(int id, String name, String date, int duration) {
        this.id = id;
        this.name = name;
        this.date = LocalDate.parse(date);
        this.duration = Duration.ofSeconds(duration);
    }
}
