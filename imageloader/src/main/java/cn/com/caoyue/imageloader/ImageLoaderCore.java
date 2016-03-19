package cn.com.caoyue.imageloader;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/*package*/ class ImageLoaderCore {

    Handler handler;
    String url;
    ImageView view;
    ImageConfig imageConfig;
    ImageLoaderConfig defaultConfig;
    ImageLoaderListener listener;

    /*package*/ ImageLoaderCore(@NonNull String url, @NonNull ImageView view, @Nullable ImageConfig imageConfig, @Nullable ImageLoaderListener listener) {
        handler = new Handler(Looper.getMainLooper());
        this.url = url;
        this.view = view;
        this.imageConfig = imageConfig;
        this.defaultConfig = ImageLoader.getInstance().config;
        this.listener = listener;
    }

    /*package*/ void load() {

        Utils.runInNewThread(new Runnable() {
            @Override
            public void run() {
                checkConfig();
                onLoading();
                if (imageConfig.isCache == 1) {
                    Bitmap bitmap = ImageCache.bitmapCache.get(url);
                    if (bitmap != null) {
                        onGetBitmap(bitmap);
                        return;
                    } else {
                        try {
                            String path = ImageCache.getInternalStorageCache(url, defaultConfig.cachePath);
                            if (path != null) {
                                bitmap = Utils.getBitmapFromFile(path);
                                onGetBitmap(bitmap);
                                return;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    Bitmap bitmap = ImageCache.getFromNetwork(url);
                    if (bitmap != null) {
                        onGetBitmap(bitmap);
                    } else {
                        onFailure(new TimeoutException("Network time out"));
                    }
                } catch (Exception e) {
                    onFailure(e);
                }
            }
        });
    }

    protected void checkConfig() {
        if (imageConfig == null) {
            imageConfig = new ImageConfig();
            imageConfig.drawableOnLoading = defaultConfig.defaultDrawableOnLoading;
            imageConfig.drawableOnFailure = defaultConfig.defaultDrawableOnFailure;
            imageConfig.isCache = 0;
            imageConfig.isKeepRatio = 0;
            imageConfig.height = -1;
            imageConfig.width = -1;
        } else {
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
    }

    /*package*/ void onGetBitmap(final Bitmap bitmap) {
        Utils.runInUIThread(handler, new Runnable() {
            @Override
            public void run() {
                view.destroyDrawingCache();
                view.setImageBitmap(bitmap);
                if (listener != null) {
                    listener.onImageLoadSuccess();
                }
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
                if (listener != null) {
                    listener.onImageLoadFailure(t);
                }
            }
        });
    }

}
