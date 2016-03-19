package cn.com.caoyue.imageloader;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
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

    public static Bitmap get(String url) {
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
}
