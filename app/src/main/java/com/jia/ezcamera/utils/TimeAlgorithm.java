package com.jia.ezcamera.utils;


import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2017/1/18.
 */
public class TimeAlgorithm {
    private String mTime;

    public TimeAlgorithm(String _mTime) {
        // TODO Auto-generated constructor stub
        mTime = _mTime;
    }

    public TimeAlgorithm addOrSub(int _sec) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse("2015-07-07 " + mTime);
            Calendar calendarObj = Calendar.getInstance();
            calendarObj.setTime(date);
            calendarObj.add(Calendar.SECOND, _sec);
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
            TimeAlgorithm mt = new TimeAlgorithm(sdf2.format(calendarObj
                    .getTime()));
            return mt;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public int mod(int _timeInterval) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = sdf.parse("2015-07-07 " + mTime);
            Calendar calendarObj = Calendar.getInstance(Locale.CHINA);
            calendarObj.setTime(date);
            int m = calendarObj.get(Calendar.MINUTE);
            int s = calendarObj.get(Calendar.SECOND);
            return (60 * m + s) % _timeInterval;
        } catch (ParseException e) {
            return -1;
        }

    }

    public long compareTime(TimeAlgorithm _mt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = sdf.parse("2015-07-07 " + mTime);
            Calendar calendarObj = Calendar.getInstance();
            calendarObj.setTime(date);
            Long d = calendarObj.getTimeInMillis();
            date = sdf.parse("2015-07-07 " + _mt.mTime);
            calendarObj.setTime(date);
            Long _d = calendarObj.getTimeInMillis();
            return d - _d;
        } catch (ParseException e) {
            return 111111;
        }

    }

    public String getData() {
        return mTime;
    }

    public String getData2() {

        return mTime.replace("-","").replace(" ","").replace(":","");
    }
    public int getMin() {
        int min = 0;
        String mins[] = mTime.split(":");
        if(mins.length == 3){
            min = Integer.parseInt(mins[0])*60+Integer.parseInt(mins[1]);
            if(Integer.parseInt(mins[2])>0){
                min++;
            }
        }
        return min;
    }


    public long getSec(String _date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = sdf.parse(_date+" " + mTime);
            Calendar calendarObj = Calendar.getInstance();
            calendarObj.setTime(date);
            Long d=calendarObj.getTimeInMillis()/1000;
            return d ;
        } catch (ParseException e) {
            return -1;
        }
    }
}
