package cn.com.caoyue.imageloader;

import android.accounts.NetworkErrorException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.LruCache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    public static Map<String, Thread> threadMap = new HashMap<String, Thread>(0);
    public static LruCache<String, Bitmap> bitmapCache = new LruCache<>((int) Runtime.getRuntime().totalMemory()/8);

    public static String getInternalStorageCache(String url, String cachePath) throws IOException {
        String urlSha1 = Utils.SHA1(url);
        File cacheFolder = new File(cachePath);
        if (!cacheFolder.exists()) {
            cacheFolder.mkdirs();
        }
        if (!cacheFolder.isDirectory()) {
            throw new IOException("Can\'t access " + cachePath + " for it may be not a directory");
        }
        File[] files = cacheFolder.listFiles();
        for (File i: files) {
            if (!i.isDirectory()) {
                if (i.getAbsolutePath().substring(i.getAbsolutePath().lastIndexOf("/") + 1).equals(urlSha1)) {
                    return i.getAbsolutePath();
                }
            }
        }
        return null;
    }

    public static void putInternalStorageCache(String url, Bitmap bitmap, String cachePath) throws IOException {
        String urlSha1 = Utils.SHA1(url);
        File cacheFolder = new File(cachePath);
        if (!cacheFolder.exists()) {
            cacheFolder.mkdirs();
        }
        if (!cacheFolder.isDirectory()) {
            throw new IOException("Can\'t access " + cachePath + " for it may be not a directory");
        }
        File saveFile = new File(cachePath + "/" + urlSha1);
        if (saveFile.exists()) {
            saveFile.delete();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(saveFile));
        bitmap.compress(Bitmap.CompressFormat.WEBP, 80, bos);
        bos.flush();
        bos.close();
    }

    public static Bitmap getFromNetwork(String url) throws NetworkErrorException {
        return Utils.get(url);
    }
}
