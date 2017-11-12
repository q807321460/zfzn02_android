package com.jia.update;

/**
 * Created by Administrator on 2016/11/22.
 */
public interface UpdateDownloadListener {

    /**
     * 下载开始回调
     */
    public void onStarted();

    /**
     * 进度更新回调
     * @param progerss
     */
    public void onProgressChanged(int progerss);

    /**
     * 下载完成回调
     * @param completeSize
     * @param downloadUrl
     */
    public void onFinished(int completeSize, String downloadUrl);

    /**
     * 下载失败回调
     */
    public void onFailure();
}
