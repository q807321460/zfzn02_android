package com.jia.update;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/11/22.
 * 真正的负责处理文件的下载和线程间的通信
 */
public class UpdateDownloadRequest implements Runnable {
    private String downloadUrl;
    private String localFilePath;
    private UpdateDownloadListener downloadListener;
    private boolean isDownloading = false;
    private long currentLength;
    private DownloadResponseHandler downloadHanler;
    public UpdateDownloadRequest(String downloadUrl, String localFilePath, UpdateDownloadListener downloadListener){
        this.downloadListener = downloadListener;
        this.downloadUrl = downloadUrl;
        this.localFilePath = localFilePath;
        this.isDownloading = true;
        this.downloadHanler = new DownloadResponseHandler();
    }

    //真正的去建立连接的方法
    private void makeRequest() throws IOException, InterruptedException{
        if(!Thread.currentThread().isInterrupted()){
            try {
                URL url = new URL(downloadUrl);
                System.out.println("下载地址："+downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.connect(); //阻塞我们当前的线程
                currentLength = connection.getContentLength();
                File cacheFile = new File(localFilePath);//设置参数
                cacheFile.createNewFile();//生成文件
                if( !Thread.currentThread().isInterrupted()){
                    //真正完成文件的下载
                    downloadHanler.sendResponseMessage(connection.getInputStream());
                }
            } catch (IOException e){
                throw e;
            }
        }
    }
    @Override
    public void run() {
        try{
            makeRequest();
        }catch (IOException e){

        }catch (InterruptedException e){

        }
    }

    /**
     * 格式化数字
     * @param value
     * @return
     */
    public String getTwoPointFloatStr(float value) {
        DecimalFormat fnum = new DecimalFormat("0.00");
        return fnum.format(value);
    }

    /**
     * 包含了下载过程中所有可能出现的异常情况
     */
    public enum FailureCode{
        UnKnownHost, Socket,SocketTimeOut,ConnectionTimeout,
        IO,HttpResponse,JSON,Interrupted
    }

    /**
     * 用来真正的去下载文件，并发送消息和回调的接口
     */
    public class DownloadResponseHandler {
        protected static final int SUCCESS_MEAAGE = 0;
        protected static final int FAILURE_MEAAGE = 1;
        protected static final int START_MEAAGE = 2;
        protected static final int FINISH_MEAAGE = 3;
        protected static final int NETWORK_OFF = 4;
        protected static final int PROGRESS_CHANGED = 5;

        private int mCompleteSize = 0;
        private int progress = 0;
        private Handler handler;
        public DownloadResponseHandler(){
            handler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    handleSelfMessage(msg);
                }
            };
        }

        /**
         * 用来发送不同的消息对象
         */
        protected void sendFinishMessage(){
            sendMessage(obtainMessage(FINISH_MEAAGE, null));
        }

        protected void sendProgressChangedMessage(int progress){
            sendMessage(obtainMessage(PROGRESS_CHANGED, new Object[]{progress}));
        }

        protected void sendFailureMessage(FailureCode failureCode){
            sendMessage(obtainMessage(FAILURE_MEAAGE, new Object[]{failureCode}));
        }

        protected void sendMessage(Message msg) {
            if (handler != null) {
                handler.sendMessage(msg);
            } else {
                handleSelfMessage(msg);
            }
        }

        /**
         * 获取一个消息对象
         * @param responseMessage
         * @param response
         * @return
         */
        protected Message obtainMessage(int responseMessage, Object response) {
            Message msg = null;
            if (handler != null) {
                msg = handler.obtainMessage(responseMessage, response);
            } else {
                msg = Message.obtain();
                msg.what = responseMessage;
                msg.obj = response;
            }
            return msg;
        }

        private void handleSelfMessage(Message msg) {
            Object[] response;
            switch (msg.what) {
                case FAILURE_MEAAGE:
                    response = (Object[]) msg.obj;
                    handleFailureMessage((FailureCode) response[0]);
                    break;
                case PROGRESS_CHANGED:
                    response = (Object[]) msg.obj;
                    handleProgressChangedMessage(((Integer)response[0]).intValue());
                    break;
                case FINISH_MEAAGE:
                    onFinish();
                    break;

            }
        }

        /**
         * 各种消息的处理逻辑
         */
        protected void handleProgressChangedMessage(int progress) {
            downloadListener.onProgressChanged(progress);
        }

        protected void handleFailureMessage(FailureCode failureCode){
            onFailure(failureCode);
        }

        /**
         * 外部接口的回调
         */
        public void onFinish() {
            downloadListener.onFinished(mCompleteSize, "");
        }

        public void onFailure(FailureCode failureCode) {
            downloadListener.onFailure();
        }

        //文件下载方法,会发送各种类型的事件
        void sendResponseMessage(InputStream is) {
            RandomAccessFile randomAccessFile = null;
            mCompleteSize = 0;
            try {
                byte[] buffer = new byte[1024];
                int length = -1;
                int limit = 0;
                System.out.println("下载地址：" + localFilePath);

                randomAccessFile = new RandomAccessFile(localFilePath, "rwd");
                while ((length = is.read(buffer)) != -1){
                    if(isDownloading){
                        randomAccessFile.write(buffer,0,length);
                        mCompleteSize += length;
                        if (mCompleteSize <= currentLength) {
                            progress = (int)Float.parseFloat(
                                    getTwoPointFloatStr(100*mCompleteSize/currentLength));
                            System.out.println("已下载大小" + mCompleteSize);
                            System.out.println("已下载进度" + progress);
                            if(limit % 10 == 0 && progress <= 100){
                                //为了现在一下我们notification的更新频率
                                sendProgressChangedMessage(progress);
                            }
                            limit++;
                        }


                    }
                }
                sendFinishMessage();
            } catch (IOException e) {
                sendFailureMessage(FailureCode.IO);
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException e){
                    sendFailureMessage(FailureCode.IO);
                }
            }
        }


    }




}
