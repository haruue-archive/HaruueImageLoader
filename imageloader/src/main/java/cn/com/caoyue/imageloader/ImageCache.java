package cn.com.caoyue.imageloader;

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

    public static Uri getInternalStorageCache(String url, String cachePath) throws IOException {
        String urlSha1 = Utils.SHA1(url);
        File cacheFolder = new File(cachePath);
        if (!cacheFolder.exists()) {
            cacheFolder.mkdirs();
        }
        if (!cacheFolder.isDirectory()) {
            throw new IOException("Can\'t access " + cachePath + " for it may be not a directory");
        }
        Map<String, String> fileList = new HashMap<String, String>(0);
        File[] files = cacheFolder.listFiles();
        for (File i: files) {
            if (!i.isDirectory()) {
                fileList.put(i.getAbsolutePath().substring(i.getAbsolutePath().lastIndexOf("/") + 1), i.getAbsolutePath());
            }
        }
        if (fileList.containsKey(urlSha1)) {
            return Uri.parse("file://" + fileList.get(urlSha1));
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
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(saveFile));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    public static Bitmap getFromNetwork(String url) {
        return Utils.get(url);
    }
}
