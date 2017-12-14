package com.jia.ezcamera.utils;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by ls on 15/1/28.
 */
public class SrceensUtils {

    /**
     * 竖屏 w:h=2:1
     * @param mView
     */
    public static void setVertical_2_1(View mView,int mWhith) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        lp.width = mWhith;
        lp.height = (int) (lp.width / 2);
        mView.setLayoutParams(lp);
    }

    /**
     * 竖屏 w:h=4:3
     * @param v1
     */
    public static void setVertical_4_3(View v1,int mWhith) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        lp.width = mWhith;
        lp.height = (int) (lp.width * (float) 3 / 4);
    }

    /**
     * 横屏 全屏
     * @param mView
     */
    public static void setHorizontal(View mView,int srceenWidth,int srceenHeight) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        lp.width = srceenWidth;
        lp.height = srceenHeight;
        mView.setLayoutParams(lp);
    }

    /**
     * 设置屏幕按比率缩放－－横屏(根据屏幕比率变化)
     * @param v
     * @param viewWidth
     * @param viewHeight
     * @param srceenWidth
     * @param srceenHeight
     */
    public static void ChangeSfvWidthAndHeight_Horizontal(View v,int viewWidth,int viewHeight ,int srceenWidth, int srceenHeight) {
        if (v==null||srceenWidth<=0||srceenHeight<=0){
            return;
        }
        int sfvWidth = viewWidth;
        int sfvHeight = viewHeight;
        if (sfvWidth == 0 || sfvHeight == 0) {
            sfvWidth = srceenWidth;
            sfvHeight = srceenHeight;
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        if (((float) srceenWidth / srceenHeight) < ((float) sfvWidth / sfvHeight)) {
            lp.width = srceenWidth;
            lp.height = (int) (sfvHeight * ((float)srceenWidth/sfvWidth));
        } else if (((float) srceenWidth / srceenHeight) > ((float) sfvWidth / sfvHeight)) {
            lp.height = srceenHeight;
            lp.width = (int) (sfvWidth * ((float) srceenHeight / sfvHeight));
        } else {
            lp.width = srceenWidth;
            lp.height = srceenHeight;
        }
        v.setLayoutParams(lp);
    }

    /**
     * 设置屏幕按比率缩放－－竖屏(宽度不变,高度改变)
     * @param v
     * @param viewWidth
     * @param viewHeight
     * @param srceenWidth
     * @param srceenHeight
     */
    public static void ChangeSfvWidthAndHeight_Vertical(View v,int viewWidth,int viewHeight ,int srceenWidth, int srceenHeight) {
        if (v==null||srceenWidth<=0||srceenHeight<=0){
            return;
        }
        int sfvWidth = viewWidth;
        int sfvHeight = viewHeight;
        if (sfvWidth == 0 || sfvHeight == 0) {
            sfvWidth = srceenWidth;
            sfvHeight = srceenHeight;
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        lp.width=srceenWidth;
        lp.height=(int)(sfvHeight*((float)srceenWidth/sfvWidth));
        v.setLayoutParams(lp);
    }

    /**
     * 设置屏幕按比率缩放－－竖屏 (宽:高=4:3)
     * @param v
     * @param viewWidth
     * @param viewHeight
     * @param srceenWidth
     * @param srceenHeight
     */
    public static void ChangeSfvWidthAndHeight_Vertical_4_3(View v,int viewWidth,int viewHeight ,int srceenWidth, int srceenHeight) {
        if (v==null||srceenWidth<=0||srceenHeight<=0){
            return;
        }
        int sfvWidth = viewWidth;
        int sfvHeight = viewHeight;
        if (sfvWidth == 0 || sfvHeight == 0) {
            sfvWidth = srceenWidth;
            sfvHeight = srceenHeight;
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        if (((float) 4 / 3) < ((float) sfvWidth / sfvHeight)) {
            lp.width = srceenWidth;
            lp.height = (int) (sfvHeight * ((float)srceenWidth/sfvWidth));
            //(int) (lp.width / ((float) sfvWidth / sfvHeight));
        } else if (((float) 4 / 3) > ((float) sfvWidth / sfvHeight)) {
            lp.height = srceenHeight;
            lp.width = (int) ((srceenWidth/((float) sfvWidth / sfvHeight)));
        } else {
            lp.width = srceenWidth;
            lp.height = srceenHeight;
        }
        v.setLayoutParams(lp);
    }

    /**
     * －－－－－－－屏幕缩放控制－－－－－－－
     */

}
