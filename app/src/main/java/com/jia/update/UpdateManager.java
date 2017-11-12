package com.jia.update;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Administrator on 2016/11/22.
 * 下载调度管理器，调用UpdateDownloadRequest
 */
public class UpdateManager {
    private static UpdateManager manager;
    private ThreadPoolExecutor threadPoolExecutor;
    private UpdateDownloadRequest request;
    private UpdateManager(){
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    static {
        manager = new UpdateManager();
    }
    public static UpdateManager getInstance(){
        return manager;
    }

    public void startDownloads(String downloadUrl, String loadPath,
                               UpdateDownloadListener listener) {
        if(request != null){
            return;
        }
        checkLocalFilePath(loadPath);
        //开始真正的下载任务
        request = new UpdateDownloadRequest(downloadUrl, loadPath, listener);
        Future<?> future = threadPoolExecutor.submit(request);
    }

    //用来检查文件路径是否存在
    private void checkLocalFilePath(String path){
        File dir = new File(path.substring(0, path.lastIndexOf("/") + 1));
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
