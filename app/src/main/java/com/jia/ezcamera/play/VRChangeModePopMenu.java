package com.jia.ezcamera.play;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.jia.znjj2.R;

import vv.playlib.CPlayerEx;
import vv.playlib.render.fishrender.VideoVRRender;

/**
 * Created by Administrator on 2017/7/12.
 */
public class VRChangeModePopMenu {
    private View popView;//popview 布局
    private PopupWindow popupWindow;
    private Activity mActivity;
    public VRChangeModePopMenu(Activity activity){
        mActivity = activity;
    }
    /**
     * 显示修改码流的pop
     */
    public void showStreamPopView(final Button showButton, final CPlayerEx cplayer) {
        popView = View.inflate(mActivity, R.layout.pop_vrchange, null);
        ImageView ball = (ImageView) popView.findViewById(R.id.vr_ball);
        ImageView blow = (ImageView) popView.findViewById(R.id.vr_blow);
        ImageView cylinder = (ImageView) popView.findViewById(R.id.vr_cylinder);
        ImageView plane = (ImageView) popView.findViewById(R.id.vr_plane);

        ball.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cplayer.changeFishEyeMode(VideoVRRender.MODE_BALL);
                showButton.setBackgroundResource(R.drawable.png_vrball);
                popupWindow.dismiss();
            }
        });
        blow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cplayer.changeFishEyeMode(VideoVRRender.MODE_BOWL);
                showButton.setBackgroundResource(R.drawable.png_vrblow);
                popupWindow.dismiss();
            }
        });
        cylinder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cplayer.changeFishEyeMode(VideoVRRender.MODE_CYLINDER);
                showButton.setBackgroundResource(R.drawable.png_vrcylinder);
                popupWindow.dismiss();
            }
        });
        plane.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cplayer.changeFishEyeMode(VideoVRRender.MODE_PLANE);
                showButton.setBackgroundResource(R.drawable.png_vrplane);
                popupWindow.dismiss();
            }
        });


        //int[] location = new int[2];
        //play_ll_buttonId.getLocationOnScreen(location);
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        popupWindow = new PopupWindow(popView,
                // (int) (dm.widthPixels / 5)
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // Background不能设置为null，dismiss会失效
        // popupWindow.setBackgroundDrawable(null);
        popupWindow.update();

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {

            }
        });
        if (!popupWindow.isShowing()) {
            int[] location = new int[2];
            showButton.getLocationOnScreen(location);
            //在控件上方显示
            popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupHeight = popView.getMeasuredHeight();
            int popupWidth = popView.getMeasuredWidth();

            popupWindow.showAtLocation(showButton, Gravity.NO_GRAVITY, (location[0] + showButton.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
        } else {
            popupWindow.dismiss();
        }
    }
}
