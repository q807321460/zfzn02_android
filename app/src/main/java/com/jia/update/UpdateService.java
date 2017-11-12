package com.jia.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.jia.znjj2.R;

import java.io.File;

/**
 * Created by Administrator on 2016/11/22.
 */
public class UpdateService extends Service {
    private String url = "http://101.201.211.87:8080/zfzn02-";
    private String apkUrl;
    private String filePath;
    private NotificationManager notificationManager;
    private Notification mNotification;

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        filePath = Environment.getExternalStorageDirectory() + "/Download/zfzn02.apk";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            notifyUser("下载失败","失败", 0);
        }
        apkUrl = url + intent.getStringExtra("lastVersion")+".apk";
        System.out.println("**********:"+apkUrl);
        notifyUser("开始下载","开始下载",0);
        startDownload();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notifyUser(String result, String reason, int progress) {
        System.out.println("getUpdateServices已下载进度" + progress);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                .setContentTitle(getString(R.string.app_name));
        if(progress > 0 && progress < 100){
            builder.setProgress(100, progress, false);
        }else {
            builder.setProgress(0, 0, false);
        }
        builder.setAutoCancel(false);
        builder.setWhen(System.currentTimeMillis());
        builder.setTicker(result);
        builder.setContentIntent(progress >= 100 ? getContentIntent()
                : PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
        mNotification = builder.build();
        notificationManager.notify(0, mNotification);
    }

    private PendingIntent getContentIntent(){
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkFile.getAbsolutePath()), "application/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;

    }

    private void startDownload(){
        UpdateManager.getInstance().startDownloads(apkUrl, filePath, new UpdateDownloadListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgressChanged(int progerss) {
                notifyUser("正在下载","正在下载",progerss);

            }

            @Override
            public void onFinished(int completeSize, String downloadUrl) {
                notifyUser("下载完成","下载完成",100);
                stopSelf();
            }

            @Override
            public void onFailure() {
                notifyUser("下载失败","下载失败",0);
                stopSelf();
            }
        });
    }
}
