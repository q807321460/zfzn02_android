package com.jia.ezcamera.alarm;


import java.io.Serializable;

/**
 * Created by ls on 15/1/28.
 */
public class playbackItem implements Serializable{
    public String eventId;
    public long event_time;//����ʱ��
    public int event_type;//����
    public int rec_sec;//ʱ��
    public Object cam;
    public int snap;//����ͼƬ
}