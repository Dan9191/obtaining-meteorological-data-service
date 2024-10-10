package org.example.models;

import java.time.LocalDate;

/**
 * Модель для наполнения даннфх по дням.
 */
public class Day {

    private LocalDate date;
    private String temp;

    public Day() {

    }

    public Day(LocalDate date, String temp) {
        this.date = date;
        this.temp = temp;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTemp() {
        return this.temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return "Day{" +
                "date='" + date + '\'' +
                ", temp='" + temp + '\'' +
                '}';
    }
}
