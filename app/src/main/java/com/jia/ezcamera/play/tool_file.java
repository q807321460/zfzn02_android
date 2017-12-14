package com.jia.ezcamera.play;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.jia.znjj2.R;

public class tool_file {
	private boolean bSDCardOk = false;
	private File mJpgFilePath;
	private File mLocalAlarmFilePath;
	private File mInfoPath;
	private static tool_file instance = null;
	private Context myContext;

	public static synchronized tool_file getInstance(Context c) {
		if (instance == null) {
			instance = new tool_file(c);
		}
		return instance;
	}

	private tool_file(Context c) {
		this.myContext = c;
		FileInit();
	}

	public boolean IsSDCardOK() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public boolean isFileExist(String filename) {

		File f = new File(filename);
		boolean bIsExist = f.exists();

		return bIsExist;

	}

	public String getJpgFileName(String filename) {
		String AbsulateName = mJpgFilePath + "/" + filename;
		return AbsulateName;
	}

	public void delJpgFolder() {
		String folderPath = GetJpgPath();
		try {
			delAllFile(folderPath);
			/*
			 * String filePath = folderPath; filePath = filePath.toString();
			 * java.io.File myFilePath = new java.io.File(filePath);
			 * myFilePath.delete();
			 */
		} catch (Exception e) {
			System.out.println("");
			e.printStackTrace();
		}
	}

	public void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}

		}
	}

	public boolean FileInit() {
		bSDCardOk = IsSDCardOK();
		if (bSDCardOk == true) {
			mJpgFilePath = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/"+myContext.getString(R.string.events_path) + "/");
			if (!mJpgFilePath.exists()) {
				mJpgFilePath.mkdirs();
			}
			mLocalAlarmFilePath = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/"+"LocalAlarm" + "/");
			if (!mLocalAlarmFilePath.exists()) {
				mLocalAlarmFilePath.mkdirs();
			}
			mInfoPath = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()  + "/"+ myContext.getString(R.string.eventsinfo_path) + "/");
			if (!mInfoPath.exists()) {
				mInfoPath.mkdirs();
			}

		} else {

			return false;
		}

		return true;
	}

	@SuppressLint("SimpleDateFormat")
	public String GetJpgFileName() {
		String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date()) + ".jpg";
		String AbsulateName = mJpgFilePath + "/" + fileName;
		return AbsulateName;
	}

	public String GetJpgPath() {
		if (bSDCardOk == false)
			return null;
		String AbsulateName = mJpgFilePath + "/";
		return AbsulateName;
	}
	
	public String GetLocalAlarmPath() {
		if (bSDCardOk == false)
			return null;
		String AbsulateName = mLocalAlarmFilePath + "/";
		return AbsulateName;
	}

	public String GetInfoPath() {
		if (bSDCardOk == false)
			return null;
		String AbsulateName = mInfoPath + "/";
		return AbsulateName;
	}

	public void DeletePic(String pic_id) {
		if (bSDCardOk == false)
			return;
		String filename = GetJpgPath() + "/" + pic_id + ".jpg";
		File file = new File(filename);
		if (!file.exists()) {
			return;
		}
		file.delete();
	}

	
}
