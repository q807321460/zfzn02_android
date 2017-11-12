package com.jia.data;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;

public class NetworkProber
{
    public static final int NETWORK_MOBILE = 2;
    public static final int NETWORK_NONE = 0;
    public static final int NETWORK_WIFI = 1;
    private static OnNetChangedListener mListener;
    public static int mWifiState;

    public static void doWirelessSeting(Context paramContext)
    {
        if (paramContext != null)
        {
            Intent localIntent = new Intent("android.settings.WIRELESS_SETTINGS");
            if (Build.VERSION.SDK_INT > 13) {
                localIntent = new Intent("android.settings.WIFI_SETTINGS");
            }
            localIntent.setFlags(335544320);
            paramContext.startActivity(localIntent);
        }
    }

    public static int getNetworkState(Context paramContext)
    {
        int i = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager)paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        State localState = connectivityManager.getNetworkInfo(1).getState();
        if ((localState == State.CONNECTED) || (localState == State.CONNECTING)) {
            i=1;
            return i;
        }
        localState = connectivityManager.getNetworkInfo(0).getState();
        if((localState == State.CONNECTED) || (localState == State.CONNECTING))
        {
            i=2;
            return i;
        }
        return i;
    }

    public static boolean isNetLegal(Context paramContext)
    {
//        if (DataControl.mRemoteLogin) {}
//        while (1 == mWifiState) {
//            return true;
//        }
        return false;
    }

    public static boolean isNetworkAvailable(Context paramContext)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWIFIAvailable(Context paramContext)
    {
        State state = ((ConnectivityManager)paramContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(1).getState();
        return State.CONNECTED == state;
    }

    public static void setOnNetChangedLister(OnNetChangedListener paramOnNetChangedListener)
    {
        mListener = paramOnNetChangedListener;
    }

    public static void setWifiState(int paramInt)
    {
        if (mWifiState != paramInt)
        {
            mWifiState = paramInt;
            if (mListener != null) {
                mListener.onChanged();
            }
        }
    }
}
