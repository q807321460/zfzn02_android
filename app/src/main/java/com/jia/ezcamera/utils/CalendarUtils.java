package com.jia.ezcamera.utils;


public interface CalendarUtils {
    public void seletData(String year, String month, String day);

    public void doNext(String year, String month, String dat);

    public void doPrevious(String year, String month, String dat);

    public void doGetNextDay(String year, String month, String dat);
}
