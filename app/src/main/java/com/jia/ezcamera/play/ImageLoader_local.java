package com.jia.ezcamera.play;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.jia.znjj2.R;


public class ImageLoader_local {
	// private static final String TAG = "ImageLoader_local";
	private static final int MAX_CAPACITY = 10;//
	// private static final long DELAY_BEFORE_PURGE = 10 * 1000;
	private boolean bSDCardOK = false;
	private static ImageLoader_local instance = null;
	private Context myContext;
	tool_file tf;

	public static synchronized ImageLoader_local getInstance(Context c) {
		if (instance == null) {
			instance = new ImageLoader_local(c);
		}
		return instance;
	}

	ImageLoader_local(Context c) {
		this.myContext = c;
		tf = tool_file.getInstance(myContext);
		tf.FileInit();
		bSDCardOK = tf.IsSDCardOK();
	}

	public void release() {
		mFirstLevelCache.clear();
		mSecondLevelCache.clear();
		// instance = null;
	}

	public void saveBitmap(Bitmap bm, String pic_id) {
		// System.out.println("ImageLoader_local,  save bitmap,  id="+pic_id);
		if (bm == null) {
			return;
		}

		if (bSDCardOK == true) {
			try {
				String filename = tf.GetJpgPath() + "local_" + pic_id + ".jpg";
				saveMyBitmap(bm, filename);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		addImage2Cache(pic_id, bm);// ���뻺��
	}

	public void loadImage(String pic_id, ImageView mImageView) {
		// resetPurgeTimer();
		// System.out.println("ImageLoader_local  id=" + pic_id);
		Bitmap bitmap = getBitmapFromCache(pic_id);
		if (bitmap == null && bSDCardOK == true) {
			// System.out.println("loadImage  from local......");
			try {
				bitmap = getBitmapByPath(pic_id, 1);
				if (bitmap != null)
					addImage2Cache(pic_id, bitmap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				// System.out.println("loadImage  from local....catch..");
				e.printStackTrace();
			}
		}

		if (bitmap == null) {
			mImageView.setImageResource(R.drawable.png_carempic);
		} else {
			mImageView.setImageBitmap(bitmap);
		}

	}

	public void DeletePic(String pic_id) {
		if (bSDCardOK == false)
			return;
		String filename = tf.GetJpgPath() + "local_" + pic_id + ".jpg";
		File file = new File(filename);
		if (!file.exists()) {
			return;
		}
		// System.out.println("DeletePic=" + filename);
		file.delete();
	}

	private HashMap<String, Bitmap> mFirstLevelCache = new LinkedHashMap<String, Bitmap>(
			MAX_CAPACITY / 2, 0.75f, true) {
		private static final long serialVersionUID = 1L;

		protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
			if (size() > MAX_CAPACITY) {
				mSecondLevelCache.put(eldest.getKey(),
						new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			}
			return false;
		};
	};

	private ConcurrentHashMap<String, SoftReference<Bitmap>> mSecondLevelCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			MAX_CAPACITY / 2);

	/*
	 * private Runnable mClearCache = new Runnable() {
	 * 
	 * @Override public void run() { clear(); } };
	 * 
	 * private Handler mPurgeHandler = new Handler(); private void
	 * resetPurgeTimer() { mPurgeHandler.removeCallbacks(mClearCache);
	 * mPurgeHandler.postDelayed(mClearCache, DELAY_BEFORE_PURGE); }
	 */
	/*
	 * private void clear() { mFirstLevelCache.clear();
	 * mSecondLevelCache.clear(); } 8/ /**
	 * 
	 * @param url
	 * 
	 * @return
	 */
	public Bitmap getBitmapFromCache(String chanid) {
		// Log.i("info", "imageloader_local     chanid=" + chanid);
		Bitmap bitmap = null;
		bitmap = getFromFirstLevelCache(chanid);
		if (bitmap != null) {
			return bitmap;
		}
		bitmap = getFromSecondLevelCache(chanid);
		return bitmap;
	}

	private Bitmap getFromSecondLevelCache(String chanid) {
		Bitmap bitmap = null;
		SoftReference<Bitmap> softReference = mSecondLevelCache.get(chanid);
		if (softReference != null) {
			bitmap = softReference.get();
			if (bitmap == null) {
				mSecondLevelCache.remove(chanid);
			}
		}
		return bitmap;
	}

	private Bitmap getFromFirstLevelCache(String chanid) {
		Bitmap bitmap = null;
		synchronized (mFirstLevelCache) {
			bitmap = mFirstLevelCache.get(chanid);
			if (bitmap != null) {
				mFirstLevelCache.remove(chanid);
				mFirstLevelCache.put(chanid, bitmap);
			}
		}
		return bitmap;
	}

	public void addImage2Cache(String chanid, Bitmap value) {
		if (value == null || chanid == null) {
			return;
		}
		synchronized (mFirstLevelCache) {
			mFirstLevelCache.put(chanid, value);
		}
	}

	public Bitmap getBitmapByPath(String picid, int rate)
			throws FileNotFoundException {

		String filename = "local_" + picid + ".jpg";
		String pathName = tf.GetJpgPath() + filename;
		// System.out.println("getBitmapByPath,  pathName="+pathName);
		if (tf.isFileExist(pathName) == false) {
			// System.out.println("getBitmapByPath,  null pathName="+pathName);
			return null;
		}
		// System.out.println("getBitmapByPath,  exist pathName="+pathName);
		FileInputStream fis = new FileInputStream(pathName);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inSampleSize = rate;

		return BitmapFactory.decodeStream(fis, null, options);
	}

	private void saveMyBitmap(Bitmap mBitmap, String bitName)
			throws IOException {
	    System.out.println("saveMyBitmap,  bitName....="+bitName);
		File f = new File(bitName);
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			// System.out.println("saveMyBitmap,  FileOutputStream  catch....");
			e.printStackTrace();
			return;
		}
		if (fOut != null) {
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			try {
				fOut.flush();
			} catch (IOException e) {
				// System.out.println("saveMyBitmap,  compress  catch....");
				e.printStackTrace();
			}
			try {
				fOut.close();
			} catch (IOException e) {
				// System.out.println("saveMyBitmap,  close  catch....");
				e.printStackTrace();
			}
		}
	}

}
