package com.jia.ezcamera.utils;



import android.content.Context;

import com.jia.znjj2.R;


public class ErrorToastUtils {
    private static final int ERROR_MIUNS_1 = -1;
    private static final int ERROR_203 = 203;
    private static final int ERROR_400 = 400;
    private static final int ERROR_404 = 404;
    private static final int ERROR_405 = 405;
    private static final int ERROR_406 = 406;
    private static final int ERROR_410 = 410;
    private static final int ERROR_412 = 412;

    /**
     * 录像回放
     *
     * @param context
     * @param result
     */
    // = 0 成功
    // -1=创建连接失败
    // -2=p2p连接失败
    // -3=发送请求失败
    // -4=中断
    // -5=接收回复失败
    // -6=超时
    // -7=接收到错误的回复
    // 203=非法请求
    // 204=设备还未就绪
    // 404=设备无法开启回放流
    // 500=请求参数错误
    public static void PlayBackFail(Context context, int result) {
        String strError = context.getResources().getString(
                R.string.placback_fail);
        switch (result) {
            case ERROR_MIUNS_1:
                strError = strError
                        + context.getResources().getString(R.string.play_bust);
                break;
            case ERROR_404:
                strError = strError
                        + context.getResources().getString(R.string.playback_404);
                break;
            default:
                strError = strError + result;
                break;
        }
        ToastUtils.show(context, strError);
    }
    
    
    
    public static void RepassEmail(Context context, int result) {
        String strError = context.getResources().getString(
                R.string.reset_pass_faild);
        switch (result) {
            case 400:
                strError = strError
                        + context.getResources().getString(R.string.register_400);
                break;
            case 203:
                strError = strError
                        + context.getResources().getString(R.string.register_203);
                break;
            case 402:
                strError = strError
                        + context.getResources().getString(R.string.reset_pass_email_notfind);
                break;
            default:
                strError = strError + result;
                break;
        }
        ToastUtils.show(context, strError);
    }
    
    
    public static void RepassPhone(Context context, int result) {
        String strError = context.getResources().getString(
                R.string.reset_pass_faild);
        switch (result) {
            case 400:
                strError = strError
                        + context.getResources().getString(R.string.register_400);
                break;
            case 203:
                strError = strError
                        + context.getResources().getString(R.string.register_203);
                break;
            case 402:
                strError = strError
                        + context.getResources().getString(R.string.reset_pass_phone_notfind);
                break;
            case 403:
                strError = strError
                        + context.getResources().getString(R.string.reset_pass_code_error);
                break;
            default:
                strError = strError + result;
                break;
        }
        ToastUtils.show(context, strError);
    }
    
    
    public static void GetUserInfo(Context context, int result) {
        String strError = context.getResources().getString(
                R.string.get_userinfo_faild);
        switch (result) {
            case 400:
                strError = strError
                        + context.getResources().getString(R.string.register_400);
                break;
            case 203:
                strError = strError
                        + context.getResources().getString(R.string.register_203);
                break;

            default:
                strError = strError + result;
                break;
        }
        ToastUtils.show(context, strError);
    }
}
