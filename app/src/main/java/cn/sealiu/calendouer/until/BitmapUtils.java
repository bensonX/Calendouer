package cn.sealiu.calendouer.until;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;


/**
 * Created by art2cat
 * on 16-7-15.
 */
public class BitmapUtils {
    private static final String TAG = "BitmapUtils";
    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;

    public BitmapUtils() {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
    }

    public void disPlay(ImageView ivPic, String url) {
        Bitmap bitmap;
        //内存缓存
        bitmap = mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap != null) {
            ivPic.setImageBitmap(bitmap);
            System.out.println("从内存获取图片啦.....");
            return;
        }

        //本地缓存
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
        if (bitmap != null) {
            ivPic.setImageBitmap(bitmap);
            System.out.println("从本地获取图片啦.....");
            //从本地获取图片后,保存至内存中
            mMemoryCacheUtils.setBitmapToMemory(url, bitmap);
            return;
        }
        //网络缓存
        mNetCacheUtils.getBitmapFromNet(ivPic, url);
    }

    public Bitmap getBitmap(String url) {
        Bitmap bitmap, bitmap1, bitmap2, bitmap3;
        bitmap = null;
        //内存缓存
        bitmap1 = mMemoryCacheUtils.getBitmapFromMemory(url);
        Log.i(TAG, "从内存获取图片啦.....");

        //本地缓存
        bitmap2 = mLocalCacheUtils.getBitmapFromLocal(url);
        Log.i(TAG, "从本地获取图片啦.....");

        //网络缓存
        //mNetCacheUtils.(url);
        if (bitmap1 != null) {
            bitmap = bitmap1;
        } else if (bitmap2 != null) {
            bitmap = bitmap2;
        }

        return bitmap;
    }
}
