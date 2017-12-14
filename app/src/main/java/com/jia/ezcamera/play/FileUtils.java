package com.jia.ezcamera.play;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ls on 15/1/28.
 */
public class FileUtils {
    private static final String TAG=FileUtils.class.getSimpleName();

    private static FileUtils instance=null;

    private FileUtils(){

    }

    public static FileUtils getInstance(){
        if (instance==null){
            instance=new FileUtils();
        }
        return instance;
    }

    public static String getFolderName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }
        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    /**
     * 创建文件夹
     * @param filePath
     * @return
     */
    public static boolean makeFolders(String filePath) {
        return makeDirs(filePath);
    }

    /**
     * 判断文件是否存在
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * 获取目录列表
     * @param folderName
     * @return
     */
    public ArrayList<File> getDirectories(String folderName) {
        if (TextUtils.isEmpty(folderName)){
            return null;
        }
        File folder = new File(folderName);
        if(folder.exists()&&folder.isDirectory()) {
            int mSize = folder.list().length;
            if (mSize < 1) {
                return null;
            }
            ArrayList<File> folderList = new ArrayList<File>();
            File[] fList=folder.listFiles();
            for (int i = 0; i < mSize; i++) {
                if (fList[i].isDirectory()) {
                    File f = fList[i];
                    if (!folderList.contains(f)) {
                        folderList.add(f);
                    }
                }
            }
            return folderList;
//            if (folderCb != null) {
//                folderCb.getFile(folderName, fileList);
//            }
        }
        return null;
    }

//    public interface GetFolderCallback {
//        public void getFile(String folderName,ArrayList<File> file);
//    }
//
//    private GetFolderCallback folderCb =null;
//
//    public void setGetFolderCallback(GetFolderCallback cb){
//        folderCb =cb;
//    }

    /**
     * 获取文件列表
     * @param folderName
     * @return
     */
    public ArrayList<File> getFiles(String folderName) {
        if (TextUtils.isEmpty(folderName)) {
            return null;
        }
        File folder = new File(folderName);
        if (folder.exists() && folder.isDirectory()) {
            int mSize = folder.list().length;
            if (mSize < 1) {
                return null;
            }
            ArrayList<File> fileList = new ArrayList<File>();
            File[] fList = folder.listFiles();
            for (int i = 0; i < mSize; i++) {
                if (fList[i].isFile()) {
                    File f = fList[i];
                    if (!fileList.contains(f)) {
                        fileList.add(f);
                    }
                }
            }
            return fileList;
//            if (fileCb != null) {
//                fileCb.getFile(folderName, fileList);
//            }
        }
        return null;
    }

//    public interface GetFileCallback {
//        public void getFile(String folderName,ArrayList<File> file);
//    }
//
//    private GetFileCallback fileCb =null;
//
//    public void setGetFileCallback(GetFileCallback cb){
//        fileCb =cb;
//    }

    /**
     * 删除文件夹及其目录下所有的文件
     * @param folderName
     */
    public void deleteFloder(String folderName,Context myContext) {
        File myFlie = new File(folderName);
        if (myFlie.isDirectory()) {
            File files[] = myFlie.listFiles(); // 声明目录下所有的文件 files[];
            int fSize=files.length;
            for (int j = 0; j < fSize; j++) { // 遍历目录下所有的文件
                deleteFile(files[j],myContext); // 把每个文件 用这个方法进行迭代
            }
        }
    }

    /**
     * 删除文件
     * @param file
     * @param myContext
     */
    public static void deleteFile(File file,Context myContext) {
        String where = "";
        String path = file.getAbsolutePath().replaceFirst(".*/?sdcard",
                "/mnt/sdcard");
        where = MediaStore.Images.Media.DATA + "='" + path + "'";
        myContext.getContentResolver().delete(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, where, null);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    public static void deleteFile(String filePath,Context myContext) {
        if (TextUtils.isEmpty(filePath)){
            return;
        }
        Log.i(TAG,"filePath="+filePath);
        File file=new File(filePath);
        deleteFile(file,myContext);
    }

    /**
     *  发送存储图片成功的消息
     * @param filePath
     */
    public void sendMessage(String filePath){
        if (TextUtils.isEmpty(filePath)){
            return;
        }
        Intent intent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
    }

    /**
     * 进到图库
     * @param myContext
     * @param fileName
     */
    public static void jumpToGallery(Context myContext,String fileName){
        if (myContext!=null&&!TextUtils.isEmpty(fileName)) {
            final String FILE_TYPE = "image/*";
            File file = new File(fileName);
            Intent it = new Intent(Intent.ACTION_VIEW);
            Uri mUri = Uri.parse("file://" + file.getPath());
            it.setDataAndType(mUri, FILE_TYPE);
            myContext.startActivity(it);
        }
    }

}
