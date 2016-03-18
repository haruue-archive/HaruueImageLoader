package cn.com.caoyue.imageloader;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.IOException;

/*package*/ class ImageLoaderCore {

    Handler handler;
    String url;
    ImageView view;
    ImageConfig imageConfig;
    ImageLoaderConfig defaultConfig;
    ImageLoaderListener listener;

    /*package*/ ImageLoaderCore(@NonNull String url, @NonNull ImageView view, @Nullable ImageConfig imageConfig, @Nullable ImageLoaderListener listener, @NonNull ImageLoaderConfig defaultConfig) {
        handler = new Handler(defaultConfig.context.getMainLooper());
        this.url = url;
        this.view = view;
        this.imageConfig = imageConfig;
        this.defaultConfig = defaultConfig;
        this.listener = listener;
    }

    /*package*/ void load() {

        Utils.runInNewThread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                checkConfig();
                onLoading();
                if (ImageCache.bitmapCache.get(url) != null) {
                    onGetBitmap(ImageCache.bitmapCache.get(url));
                    return;
                }
                if (imageConfig.isCache == 1) {
                    try {
                        Uri uri = ImageCache.getInternalStorageCache(url, defaultConfig.cachePath);
                        if (uri != null) {
                            onGetImageUri(uri);
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    onGetBitmap(ImageCache.getFromNetwork(url));
                } catch (Exception e) {
                    onFailure(e);
                }


            }
        });

    }

    protected void checkConfig() {
        imageConfig.drawableOnLoading = (imageConfig.drawableOnLoading == -1) ? defaultConfig.defaultDrawableOnLoading : imageConfig.drawableOnLoading;
        imageConfig.drawableOnFailure = (imageConfig.drawableOnFailure == -1) ? defaultConfig.defaultDrawableOnFailure : imageConfig.drawableOnFailure;
        if (imageConfig.isKeepRatio == 1) {
            if (imageConfig.height != -1 && imageConfig.width != -1) {
                throw new IllegalArgumentException("You can\'t set both width and height if you want to keep ratio");
            }
        } else if (imageConfig.isKeepRatio == 0) {
            imageConfig.isKeepRatio = 1;
        }
        if (imageConfig.isCache == 0) {
            imageConfig.isCache = defaultConfig.isCache ? 1 : -1;
        }
    }

    /*package*/ void onGetBitmap(final Bitmap bitmap) {
        Utils.runInUIThread(handler, new Runnable() {
            @Override
            public void run() {
                view.setImageBitmap(bitmap);
                listener.onImageLoadSuccess();
            }
        });
    }

    /*package*/ void onGetImageUri(final Uri uri) {
        Utils.runInUIThread(handler, new Runnable() {
            @Override
            public void run() {
                view.setImageURI(uri);
                listener.onImageLoadSuccess();
            }
        });
    }

    /*package*/ void onLoading() {
        Utils.runInUIThread(handler, new Runnable() {
            @Override
            public void run() {
                if (imageConfig.drawableOnLoading != -1) {
                    view.setImageResource(imageConfig.drawableOnLoading);
                }
            }
        });
    }

    /*package*/ void onFailure(final Throwable t) {
        Utils.runInUIThread(handler, new Runnable() {
            @Override
            public void run() {
                if (imageConfig.drawableOnFailure != -1) {
                    view.setImageResource(imageConfig.drawableOnFailure);
                }
                listener.onImageLoadFailure(t);
            }
        });
    }
}
