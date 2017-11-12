package com.jia.jdplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.jia.znjj2.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by lys on 16-9-18.
 */
public class JdPlayImageUtils {

    private ImageLoader mImageLoader;

    private Context mContext;
    private static JdPlayImageUtils mInstance;

    public static JdPlayImageUtils getInstance() {
        if (mInstance == null) {
            mInstance = new JdPlayImageUtils();
        }
        return mInstance;
    }

    public void initialize(Context context) {
        mContext = context;
        mImageLoader = ImageLoader.getInstance(); // Get singleton instance

        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.jdplay_music_default)
                .showImageForEmptyUri(R.drawable.jdplay_music_default)
                .showImageOnFail(R.drawable.jdplay_music_default).resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).build();

        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration.Builder(mContext)
                        .defaultDisplayImageOptions(displayImageOptions).build();

        // Initialize ImageLoader with configuration.
        mImageLoader.init(config);
    }

    public void displayImage(String uri, ImageView imageView) {
        mImageLoader.displayImage(uri, imageView);
    }

}
