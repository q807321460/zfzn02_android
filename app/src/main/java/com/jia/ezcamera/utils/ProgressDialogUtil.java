package com.jia.ezcamera.utils;




import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.jia.znjj2.R;


public class ProgressDialogUtil {
    private static final String TAG = ProgressDialogUtil.class.getSimpleName();
    private static ProgressDialogUtil instance = null;
    private MyDialog waitDialog = null;

    private ProgressDialogUtil() {

    }

    public static ProgressDialogUtil getInstance() {
        if (instance == null) {
            instance = new ProgressDialogUtil();
        }
        return instance;
    }

    public void showDialog(Context context, String strMsg) {
    	cancleDialog();
        if (waitDialog == null&&context!=null) {
            waitDialog = new MyDialog(context, R.style.style_dlg_groupnodes);
            waitDialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					cancleDialog();
				}
			});
            waitDialog.show();
            waitDialog.setText(strMsg);
        }
    }

    public void showDialog(Context context, String strMsg, boolean back) {
        cancleDialog();
        if (waitDialog == null&&context!=null) {
            waitDialog = new MyDialog(context, R.style.style_dlg_groupnodes);
            waitDialog.setCancelable(back);
            waitDialog.setCanceledOnTouchOutside(back);
            waitDialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					cancleDialog();
				}
			});
        	Log.e("DEBUG", "showDialog");

            waitDialog.show();
            waitDialog.setText(strMsg);
        }
    }
    
    public void showDialog(Context context, String strMsg , OnDismissListener listener,
    		OnCancelListener cancelListener) {
    	cancleDialog();
    	if (waitDialog == null&&context!=null) {
            waitDialog = new MyDialog(context, R.style.style_dlg_groupnodes);
            waitDialog.show();
            waitDialog.setOnDismissListener(listener);
            waitDialog.setOnCancelListener(cancelListener);
            waitDialog.setText(strMsg);
        }
    }

    public void cancleDialog() {
        if (waitDialog != null) {
        	Log.e("DEBUG", "cancleDialog");
        	waitDialog.setOnDismissListener(null);
            waitDialog.cancel();
            waitDialog = null;
            
        }
    }
    
    public void setDialogText(String text) {
        if (waitDialog != null) {
        	waitDialog.setText(text);
        }
    }

    public class MyDialog extends Dialog {

        private Context mContext = null;
        private TextView text;

        public MyDialog(Context context) {
            super(context);
            this.mContext = context;
        }

        public MyDialog(Context context, int theme) {
            super(context, theme);
            this.mContext = context;
        }

        protected MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
            this.mContext = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            Log.i(TAG, "-----onCreate");
            init();
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            getWindow().setAttributes(params);
        }

        @Override
        protected void onStart() {
            super.onStart();
//            Log.i(TAG,"-----onStart");
        }

        @Override
        protected void onStop() {
            super.onStop();
//            Log.i(TAG,"-----onStop");
        }

        private void init() {
//            Log.i(TAG,"-----init");
            View view = View.inflate(mContext, R.layout.progressbar_my, null);
            text = (TextView) view.findViewById(R.id.progressbar_text);
            setContentView(view);
        }

        public void setText(String str) {
            if (text != null) {
                text.setText(str);
            }
        }

    }
}
