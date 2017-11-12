package com.jia.aux1;

import android.widget.Spinner;

/**
 * Created by Administrator on 2016/11/9.
 */
public class SpData implements Comparable  {
    private int time;
    private String password;
    public SpData(){}

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int compareTo(Object another) {
        if(another instanceof SpData){
            return time - ((SpData)another).getTime();
        }
        return 0;
    }
}
