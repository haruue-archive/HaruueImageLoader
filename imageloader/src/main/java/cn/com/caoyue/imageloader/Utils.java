package cn.com.caoyue.imageloader;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

/**
 * 工具类
 */
/*package*/ class Utils {

    /**
     * 获取一个字符串的 sha1 值，用于在缓存目录中的文件名
     * @param decript 需要取 sha1 的字符串
     * @return 对参数 sha1 的结果
     */
    /*package*/ static String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 在新线程中运行
     * @param runnable 用于运行的 Runnable 实例
     * @return 新建的线程
     */
    /*package*/ static Thread runInNewThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
        return thread;
    }


    /**
     * 在 UI 线程（主线程）中运行
     * @param handler 用于将 Runnable 传送到 UI 线程消息队列的 Handler
     * @param runnable 用于运行的 Runnable 实例
     */
    /*package*/static void runInUIThread(Handler handler, Runnable runnable) {
        handler.post(runnable);
    }

    /**
     * 使用 GET 方法从 Internet 获取图片
     * @param url 图片的 URL
     * @return 获取到的图片的 Bitmap
     * @throws NetworkErrorException 网络错误异常
     */
    /*package*/ static Bitmap get(String url) throws NetworkErrorException {
        try {
            if (!isNetworkConnected(ImageLoader.getInstance().config.context)) {
                throw new NetworkErrorException("Can't connect network");
            }
        } catch (Exception e) {
            if (e.getClass().equals(NetworkErrorException.class)) {
                throw e;
            }
        }
        if (url.substring(0, url.indexOf(":")).toLowerCase().equals("http")) {
            HttpURLConnection conn = null;
            try {
                URL mURL = new URL(url);
                conn = (HttpURLConnection) mURL.openConnection();

                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(10000);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {

                    InputStream is = conn.getInputStream();
                    return getBitmapFromInputStream(is);
                } else {
                    throw new NetworkErrorException("response status is " + responseCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (conn != null) {
                    conn.disconnect();
                }
            }

        } else if (url.substring(0, url.indexOf(":")).toLowerCase().equals("https")) {
            HttpsURLConnection conn = null;
            try {
                URL mURL = new URL(url);
                conn = (HttpsURLConnection) mURL.openConnection();

                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(10000);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {

                    InputStream is = conn.getInputStream();
                    return getBitmapFromInputStream(is);
                } else {
                    throw new NetworkErrorException("response status is " + responseCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        return null;
    }

    /**
     * 从输入流读取 Bitmap
     * @param is 输入流
     * @return 读取到的 Bitmap
     * @throws IOException 读取输入流失败
     */
    /*package*/ static Bitmap getBitmapFromInputStream(InputStream is)
            throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        is.close();
        return bitmap;
    }

    /**
     * 从文件读取 Bitmap
     * @param path 文件路径
     * @return 读取到的 Bitmao
     */
    /*package*/ static Bitmap getBitmapFromFile(String path) {
        return BitmapFactory.decodeFile(path);
    }

    /**
     * 判断网络可用性
     * @param context Application Context
     * @return 网络是否可用
     */
    /*package*/ static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 缩放 Bitmap
     * @param bm 图片的 Bitmap
     * @param newWidth 图片需要缩放到的新宽度
     * @param newHeight 图片需要缩放到的新高度
     * @return 缩放完成的图片的 Bitmap
     */
    /*package*/ static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 删除文件或目录（即使目录下面有文件也会被递归删除），相当于 <code>rm -r file</code>
     * @param file 需要删除的目录或文件的 File 对象
     */
    /*package*/ static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for (File childFile : childFiles) {
                delete(childFile);
            }
            file.delete();
        }
    }

}
