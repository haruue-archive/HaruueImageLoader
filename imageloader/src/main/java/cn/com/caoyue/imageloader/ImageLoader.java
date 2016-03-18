package cn.com.caoyue.imageloader;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {

    protected static ImageLoader imageLoader;

    protected ImageLoaderConfig config;

    protected ImageLoader() {

    }

    public static ImageLoader getInstance() {
        synchronized (ImageLoader.class) {
            if (imageLoader == null) {
                imageLoader = new ImageLoader();
            }
            return imageLoader;
        }
    }

    protected static void checkHasConfig() {
        if (imageLoader.config == null) {
            throw new IllegalStateException("You have to initialize ImageLoader by ImageLoaderConfig.getInstance(getApplication()).build() before use it");
        }
    }

    /*package*/ ImageLoader setConfig(ImageLoaderConfig config) {
        this.config = config;
        return this;
    }

    public synchronized void loadImage(@NonNull String url, @NonNull ImageView view, @Nullable ImageConfig config, @Nullable ImageLoaderListener listener) {
        new ImageLoaderCore(url, view, config, listener, this.config).load();
    }

    public synchronized void loadImage(@NonNull String url, @NonNull ImageView view, @Nullable ImageLoaderListener listener) {
        loadImage(url, view, null, listener);
    }

    public synchronized void loadImage(@NonNull String url, @NonNull ImageView view, @Nullable ImageConfig config) {
        loadImage(url, view, config, null);
    }

    public synchronized void loadImage(@NonNull String url, @NonNull ImageView view) {
        loadImage(url, view, null, null);
    }

    public synchronized void stopLoad(@NonNull String url) {

    }


}
