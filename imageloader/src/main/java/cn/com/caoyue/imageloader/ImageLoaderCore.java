package cn.com.caoyue.imageloader;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;

/*package*/ class ImageLoaderCore {

    Handler handler;
    String url;
    ImageView view;
    ImageConfig imageConfig;
    ImageLoaderConfig defaultConfig;
    ImageLoaderListener listener;
    int needWidth;
    int needHeight;

    /*package*/ ImageLoaderCore(@NonNull String url, @NonNull ImageView view, @Nullable ImageConfig imageConfig, @Nullable ImageLoaderListener listener) {
        handler = new Handler(Looper.getMainLooper());
        this.url = url;
        this.view = view;
        this.imageConfig = imageConfig;
        this.defaultConfig = ImageLoader.getInstance().config;
        this.listener = listener;
    }

    /*package*/ void load() {

        view.post(new Runnable() {
            @Override
            public void run() {
                Utils.runInNewThread(new Runnable() {
                    @Override
                    public void run() {
                        checkConfig();
                        onLoading();
                        if (imageConfig.isCache >= 0) {
                            Bitmap bitmap = checkBitmap(ImageCache.bitmapCache.get(url), false);
                            if (bitmap != null) {
                                onGetBitmap(bitmap);
                                return;
                            } else {
                                try {
                                    String path = ImageCache.getInternalStorageCache(url, defaultConfig.cachePath);
                                    if (path != null) {
                                        bitmap = checkBitmap(Utils.getBitmapFromFile(path), false);
                                        if (bitmap != null) {
                                            onGetBitmap(bitmap);
                                            ImageCache.bitmapCache.remove(url);
                                            ImageCache.bitmapCache.put(url, bitmap);
                                            return;
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        try {
                            Bitmap bitmap = checkBitmap(ImageCache.getFromNetwork(url), true);
                            onGetBitmap(bitmap);
                            ImageCache.bitmapCache.remove(url);
                            ImageCache.bitmapCache.put(url, bitmap);
                            ImageCache.putInternalStorageCache(url, bitmap, defaultConfig.cachePath);
                        } catch (Exception e) {
                            onFailure(e);
                        }
                    }
                });
            }
        });
    }

    protected void checkConfig() {
        if (imageConfig == null) {
            imageConfig = new ImageConfig();
            imageConfig.drawableOnLoading = defaultConfig.defaultDrawableOnLoading;
            imageConfig.drawableOnFailure = defaultConfig.defaultDrawableOnFailure;
            imageConfig.isCache = defaultConfig.isCache ? 1 : -1;
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

    protected Bitmap checkBitmap(Bitmap bitmap, boolean isFromNetwork) {
        if (bitmap == null) {
            return null;
        }
        needWidth = imageConfig.width;
        needHeight = imageConfig.height;
        if (imageConfig.isKeepRatio >= 0) {
            double widthRatio = (double) view.getWidth() / (double) bitmap.getWidth();
            double heightRatio = (double) view.getHeight() / (double) bitmap.getHeight();
            if (needHeight == -1 && needWidth == -1) {
                if (widthRatio > heightRatio) {
                    needWidth = (int) (bitmap.getWidth() * heightRatio);
                    needHeight = view.getHeight();
                } else {
                    needWidth = view.getWidth();
                    needHeight = (int) (bitmap.getHeight() * widthRatio);
                }
            } else if (needHeight == -1) {
                needHeight = (int) (bitmap.getHeight() * widthRatio);
            } else if (needWidth == -1) {
                needWidth = (int) (bitmap.getWidth() * heightRatio);
            }
        }
        if (!isFromNetwork && (needWidth > bitmap.getWidth() || needHeight > bitmap.getHeight())) {
            return null;
        } else {
            return Utils.zoomImg(bitmap, needWidth, needHeight);
        }
    }

}
