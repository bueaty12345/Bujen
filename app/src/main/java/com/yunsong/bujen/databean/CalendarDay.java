package com.yunsong.bujen.databean;

import java.time.LocalDate;

public class CalendarDay {
    public int day;
    public boolean isToday;
    public boolean hasPray;
    public LocalDate date;

    public CalendarDay(int day, boolean isToday, boolean hasPray, LocalDate date) {
        this.day = day;
        this.isToday = isToday;
        this.hasPray = hasPray;
        this.date = date;
    }
}
