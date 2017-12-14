package com.jia.ezcamera.play;


import java.io.File;



import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.jia.znjj2.R;


public class CJpgFileTool {
	private static final String TAG = "CJpgFileTool";
	private boolean bSDCardOk = false;
	private String m_file_path;

	private static CJpgFileTool instance = null;

	private CJpgFileTool() {

	}

	public static synchronized CJpgFileTool getInstance() {
		if (instance == null) {
			instance = new CJpgFileTool();
		}
		return instance;
	}

	public boolean IsSDCardOK() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public boolean IsCanUseSdCard() {
		try {
			return Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean init(Context context) {
		bSDCardOk = IsSDCardOK();
		File m_file_store;
		if (bSDCardOk == true) {
			m_file_store = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/"+context.getString(R.string.jpg_path) + "/");
			if (!m_file_store.exists()) {
				m_file_store.mkdirs();
			}
		} else {
			m_file_store = context.getDir("p2pviewjpg", Context.MODE_PRIVATE
					| Context.MODE_WORLD_READABLE
					| Context.MODE_WORLD_WRITEABLE);
		}
		m_file_path = m_file_store.getAbsolutePath() + "/";
		// Log.i("info", "filetool   bSDCardOk=" + bSDCardOk +
		// "    m_file_path="
		// + m_file_path);
		return true;
	}

	public String getPath() {
		Log.i(TAG, "   m_file_path=" + m_file_path);
		return m_file_path;
	}

	public String getCatchPicturePath() {
		return m_file_path + "CatchPIcture/";
	}

	public String getEventPath(Context context){
		return context.getString(R.string.jpg_path) + "/"+"CatchPIcture/"+ "event" + "/";
	}

	public boolean eventPicIsExists(Context context,String filename){
		try{
			File f=new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/"+getEventPath(context)+filename);
			if(!f.exists()){
				return false;
			}

		}catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	public String getCatchVideoPath() {
		return m_file_path + "CatchVideo/";
	}

	public long getFreeSpace() {
		long lsize = 0;
		if (bSDCardOk == true) {
			lsize = GetSDCardFreeSpace();
		} else {
			lsize = GetSystemFreeSpace();
		}
		return lsize;
	}

	private long GetSDCardFreeSpace() {
		long lsize = 0;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			// long blockCount = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();
			// float nSDTotalSpace=(blockSize*blockCount)/1024;
			lsize = availCount * blockSize;// /1024;
		}
		return lsize;
	}

	private long GetSystemFreeSpace() {
		File root = Environment.getRootDirectory();
		StatFs sf = new StatFs(root.getPath());
		long blockSize = sf.getBlockSize();
		// long blockCount = sf.getBlockCount();
		long availCount = sf.getAvailableBlocks();
		long lsize = availCount * blockSize;
		return lsize;

	}

}
