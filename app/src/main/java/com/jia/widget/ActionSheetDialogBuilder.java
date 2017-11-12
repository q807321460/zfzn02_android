package com.jia.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jia.znjj2.R;

/**
 * 三个按钮的对话框
 *
 * @author android_xc
 * @date 2014-12-10
 */
public class ActionSheetDialogBuilder implements View.OnClickListener {
    private Dialog dialog;
    private Context context;
    // 三个按钮
    private TextView textViewButton1, textViewButton2, textViewButton3;
    // 头部的提示内容
    private TextView textViewMessage;
    private DialogInterface.OnClickListener listener;

    public static final int BUTTON1 = 0;
    public static final int BUTTON2 = 1;
    public static final int BUTTON3 = 2;


    public ActionSheetDialogBuilder(Context context) {
        this.context = context;

        dialog = new Dialog(context, R.style.ActionSheet);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = w.getAttributes();
        layoutParams.x = 0;
        layoutParams.y = -1000;
        layoutParams.gravity = Gravity.BOTTOM;
        dialog.onWindowAttributesChanged(layoutParams);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_action_sheet);

        initView();
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V getView(int resId) {
        return (V) dialog.findViewById(resId);
    }

    private void initView() {
        textViewButton1 = getView(R.id.textView_button1);
        textViewButton2 = getView(R.id.textView_button2);
        textViewButton3 = getView(R.id.textView_button3);

        textViewMessage = getView(R.id.textView_message);
    }

    /**
     * 设置标题
     */
    public ActionSheetDialogBuilder setTitleMessage(String titleMessage) {
        textViewMessage.setText(titleMessage);
        return this;
    }

    /**
     * 设置标题
     */
    public ActionSheetDialogBuilder setTitleMessage(int titleMessage) {
        textViewMessage.setText(getString(titleMessage));
        return this;
    }

    /**
     * 是否隐藏标题消息栏
     * @param visible 是否可见
     */
    public void setTitleVisibility(boolean visible) {
        textViewMessage.setVisibility(visible ? View.VISIBLE : View.GONE);
        textViewButton1.setBackgroundResource(R.drawable.selector_actionsheet_top);
    }

    /**
     * 设置按钮
     */
    public ActionSheetDialogBuilder setButtons(String buttonStr1, String buttonStr2, String buttonStr3,
                                               DialogInterface.OnClickListener listener) {
        this.listener = listener;

        if (buttonStr1 != null) {
            textViewButton1.setText(buttonStr1);
            textViewButton1.setOnClickListener(this);
        }

        if (buttonStr2 != null) {
            textViewButton2.setText(buttonStr2);
            textViewButton2.setOnClickListener(this);
        }

        if (buttonStr3 != null) {
            textViewButton3.setText(buttonStr3);
            textViewButton3.setOnClickListener(this);
        }

        return this;
    }

    public ActionSheetDialogBuilder setButtons(int buttonResId1, int buttonResId2, int buttonResId3,
                                               DialogInterface.OnClickListener listener) {
        setButtons(getString(buttonResId1), getString(buttonResId2), getString(buttonResId3), listener);
        return this;
    }

    /**
     * 监听对话的取消操作
     */
    public ActionSheetDialogBuilder setOnDismissListener(DialogInterface.OnCancelListener listener) {
        if (dialog == null || listener == null) return this;
        dialog.setOnCancelListener(listener);
        return this;
    }

    @Override
    public void onClick(View view) {

        if (listener == null) return;

        int id = view.getId();
        if (id == R.id.textView_button1) {
            listener.onClick(dialog, BUTTON1);

        } else if (id == R.id.textView_button2) {
            listener.onClick(dialog, BUTTON2);

        } else if (id == R.id.textView_button3) {
            listener.onClick(dialog, BUTTON3);
        }

        /*switch (view.getId()) {
            case R.id.textView_button1:
                listener.onClick(dialog, BUTTON1);
                break;
            case R.id.textView_button2:
                listener.onClick(dialog, BUTTON2);
                break;
            case R.id.textView_button3:
                listener.onClick(dialog, BUTTON3);
                break;
        }*/

        dialog.dismiss();
    }

    public Dialog create() {
        return dialog;
    }

    /**
     * 获取资源字符串
     * @param resId
     * @return
     */
    private String getString(int resId) {
        return context.getResources().getString(resId);
    }
}
