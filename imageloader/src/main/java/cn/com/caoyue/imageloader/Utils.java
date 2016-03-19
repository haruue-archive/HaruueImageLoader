package cn.com.caoyue.imageloader;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

/*package*/ class Utils {

    /*package*/
    static String SHA1(String decript) {
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

    /*package*/
    static Thread runInNewThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
        return thread;
    }

    /*package*/
    static void runInUIThread(Handler handler, Runnable runnable) {
        handler.post(runnable);
    }

    public static Bitmap get(String url) throws NetworkErrorException {
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

    /*package*/ static Bitmap getBitmapFromInputStream(InputStream is)
            throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        is.close();
        return bitmap;
    }

    /*package*/ static Bitmap getBitmapFromFile(String path) {
        return BitmapFactory.decodeFile(path);
    }

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

}
