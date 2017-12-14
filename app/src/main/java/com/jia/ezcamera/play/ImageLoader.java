package com.jia.ezcamera.play;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


import vv.ppview.PpviewClientInterface;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jia.znjj2.R;


public class ImageLoader {
	private static final String TAG = "ImageLoader";
	private static final int MAX_CAPACITY = 10;//
	// private static final long DELAY_BEFORE_PURGE = 10 * 1000;
	private boolean bSDCardOK = false;
	private static ImageLoader instance = null;
	PpviewClientInterface myinterface = null;
	private Context myContext;
	tool_file tf;

	public static synchronized ImageLoader getInstance(Context c) {
		if (instance == null) {
			instance = new ImageLoader(c);
		}
		return instance;
	}

	ImageLoader(Context c) {
		this.myContext = c;
		tf = tool_file.getInstance(myContext);
		bSDCardOK = tf.IsSDCardOK();
		myinterface = PpviewClientInterface.getInstance();
	}

	public void release() {
		mFirstLevelCache.clear();
		mSecondLevelCache.clear();
		// instance = null;
	}

	public void saveBitmap(Bitmap bm, String pic_id) {
		// Log.i(TAG, "------------saveBitmap    0");
		Bitmap m_bm = bm;
		if (m_bm == null) {
			return;
		}
		if (bSDCardOK == true) {
			try {
				String filename = tf.GetJpgPath() + pic_id + ".jpg";
				saveMyBitmap(m_bm, filename);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		addImage2Cache(pic_id, m_bm);
		// Log.i(TAG, "------------saveBitmap    1");
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
	

	public void loadImage(String user, String pass, String chanid,
			String pic_id, BaseAdapter adapter, ImageView mImageView) {
		// resetPurgeTimer();
		// System.out.println("loadImage  from catch......");
		Bitmap bitmap = getBitmapFromCache(chanid);
		// Log.i("info", "imageLoader    chanid=" + chanid + "    pic_id="
		// + pic_id + "    bitmap=" + bitmap);
		if (bitmap == null && bSDCardOK == true) {
			// System.out.println("loadImage  from local......");
			try {
				bitmap = getBitmapByPath(chanid, 1);
				if (bitmap != null)
					addImage2Cache(chanid, bitmap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				// System.out.println("loadImage  from local....catch..");
				e.printStackTrace();
			}
		}

		if (bitmap == null && pic_id != null) {
			mImageView.setImageResource(R.drawable.png_carempic);
			ImageLoadTask imageLoadTask = new ImageLoadTask(user, pass, chanid,
					adapter);
			imageLoadTask.execute("", adapter);
		} else if (bitmap != null) {
			if (!bitmap.isRecycled())
				mImageView.setImageBitmap(bitmap);
		} else {
			mImageView.setImageResource(R.drawable.png_carempic);
		}

	}

	public void loadEventImage(String user, String pass, String eventId,
			String pic_id, BaseAdapter adapter, ImageView mImageView) {
		// resetPurgeTimer();
		// System.out.println("loadImage  from catch......");
		Bitmap bitmap = getBitmapFromCache(eventId);
		// Log.i("info", "imageLoader    chanid=" + chanid + "    pic_id="
		// + pic_id + "    bitmap=" + bitmap);
		if (bitmap == null && bSDCardOK == true) {
			try {
				bitmap = getBitmapByPath(eventId, 1);
				if (bitmap != null) {
					addImage2Cache(eventId, bitmap);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				// System.out.println("loadImage  from local....catch..");
				e.printStackTrace();
			}
		}

		if (bitmap == null && pic_id != null) {
			mImageView.setImageResource(R.drawable.pic_nopic);
			EventImageLoadTask imageLoadTask = new EventImageLoadTask(user,
					pass, eventId, adapter);
			imageLoadTask.execute("", adapter);
		} else if (bitmap != null) {
			if (!bitmap.isRecycled())
				mImageView.setImageBitmap(bitmap);
		} else {
			mImageView.setImageResource(R.drawable.pic_nopic);
		}

	}

	public void addImage2Cache(String chanid, Bitmap value) {
		// Log.i(TAG, "------------addImage2Cache     0");
		if (value == null || chanid == null) {
			return;
		}
		synchronized (mFirstLevelCache) {
			mFirstLevelCache.put(chanid, value);
		}
		// Log.i(TAG, "------------addImage2Cache     1");
	}

	class ImageLoadTask extends AsyncTask<Object, Void, Bitmap> {
		BaseAdapter m_adapter;

		String m_user;
		String m_pass;
		String m_picid;
		String m_strfilename;

		public ImageLoadTask(String user, String pass, String picid,
				BaseAdapter adapter) {
			// TODO Auto-generated constructor stub
			this.m_user = user;
			this.m_pass = pass;
			this.m_picid = picid;
			this.m_adapter = (BaseAdapter) adapter;
			// System.out.println("DownLoadImage, url="+url);

			m_strfilename = tf.GetJpgPath() + picid + ".jpg";

		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			Bitmap drawable = null;
			drawable = myinterface.c2s_get_cam_pic(m_user, m_pass, m_picid);
			return drawable;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result == null) {
				// System.out.println("onPostExecute.......null.."+m_strfilename);
				return;
			}
			if (bSDCardOK == true) {
				try {
					saveMyBitmap(result, m_strfilename);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			addImage2Cache(m_picid, result);
			if (m_adapter != null)
				m_adapter.notifyDataSetChanged();
		}

	}

	class EventImageLoadTask extends AsyncTask<Object, Void, Bitmap> {
		BaseAdapter m_adapter;

		String m_user;
		String m_pass;
		String m_eventid;
		String m_strfilename;

		public EventImageLoadTask(String user, String pass, String eventId,
				BaseAdapter adapter) {
			// TODO Auto-generated constructor stub
			this.m_user = user;
			this.m_pass = pass;
			this.m_eventid = eventId;
			this.m_adapter = (BaseAdapter) adapter;
			// System.out.println("DownLoadImage, url="+url);

			m_strfilename = tf.GetJpgPath() + m_eventid + ".jpg";

		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			Bitmap drawable = null;
			drawable = myinterface.c2s_get_event_pic(m_user, m_pass, m_eventid);
			// drawable = loadImageFromInternet(m_url);
			return drawable;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result == null) {
				// System.out.println("onPostExecute.......null.."+m_strfilename);
				return;
			}
			if (bSDCardOK == true) {
				try {
					saveMyBitmap(result, m_strfilename);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			addImage2Cache(m_eventid, result);
			if (m_adapter != null)
				m_adapter.notifyDataSetChanged();
		}

	}

	public Bitmap loadImageFromInternet(String url) {
		Bitmap bitmap = null;
		HttpClient client = AndroidHttpClient.newInstance("Android");
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSocketBufferSize(params, 3000);
		HttpResponse response = null;
		InputStream inputStream = null;
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(url);
			response = client.execute(httpGet);
			int stateCode = response.getStatusLine().getStatusCode();
			if (stateCode != HttpStatus.SC_OK) {
				Log.d(TAG, "func [loadImage] stateCode=" + stateCode);
				return bitmap;
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				try {
					inputStream = entity.getContent();
					return bitmap = BitmapFactory.decodeStream(inputStream);
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (ClientProtocolException e) {
			httpGet.abort();
			e.printStackTrace();
		} catch (IOException e) {
			httpGet.abort();
			e.printStackTrace();
		} finally {
			((AndroidHttpClient) client).close();
		}

		return bitmap;
	}

	public void saveMyBitmap(Bitmap mBitmap, String bitName) throws IOException {
		// Log.i(TAG, "------------saveMyBitmap     0");
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
		// Log.i(TAG, "------------saveMyBitmap     1");
	}

	public Bitmap getBitmapByPath(String picid, int rate)
			throws FileNotFoundException {

		String filename = picid + ".jpg";
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
}
