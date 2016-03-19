package cn.com.caoyue.imageloader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.IOException;

/**
 * ImageLoader 内核，实际加载行为的执行类
 */
/*package*/ class ImageLoaderCore {

    Thread loadingThread;
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

        view.post(new Runnable() {
            @Override
            public void run() {
                loadingThread = Utils.runInNewThread(new Runnable() {
                    @Override
                    public void run() {
                        checkConfig();
                        onLoading();
                        if (imageConfig.isRefresh >= 0) {
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
                                            if (imageConfig.isKeepRatio >= 0 && imageConfig.isFillView > 0) {
                                                //不存储形变图
                                                ImageCache.bitmapCache.remove(url);
                                                ImageCache.bitmapCache.put(url, bitmap);
                                            }
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
                            if (imageConfig.isKeepRatio >= 0 && imageConfig.isFillView > 0) {
                                //不存储形变图
                                ImageCache.bitmapCache.remove(url);
                                ImageCache.bitmapCache.put(url, bitmap);
                                ImageCache.putInternalStorageCache(url, bitmap, defaultConfig.cachePath);
                            }
                            onGetBitmap(bitmap);
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
            imageConfig.drawableOnCancel = defaultConfig.defaultDrawableOnCancel;
            imageConfig.isCache = defaultConfig.isCache ? 1 : -1;
            imageConfig.isKeepRatio = 0;
            imageConfig.isFillView = defaultConfig.isFillView ? 1 : -1;
            imageConfig.height = -1;
            imageConfig.width = -1;
        }
        imageConfig.drawableOnLoading = (imageConfig.drawableOnLoading == -1) ? defaultConfig.defaultDrawableOnLoading : imageConfig.drawableOnLoading;
        imageConfig.drawableOnFailure = (imageConfig.drawableOnFailure == -1) ? defaultConfig.defaultDrawableOnFailure : imageConfig.drawableOnFailure;
        imageConfig.drawableOnCancel = (imageConfig.drawableOnCancel == -1) ? defaultConfig.defaultDrawableOnCancel : imageConfig.drawableOnCancel;
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
        if (imageConfig.isFillView == 0) {
            imageConfig.isFillView = defaultConfig.isFillView ? 1 : -1;
        }
    }

    /*package*/ void onGetBitmap(final Bitmap bitmap) {
        Utils.runInUIThread(handler, new Runnable() {
            @Override
            public void run() {
                view.destroyDrawingCache();
                view.setImageBitmap(bitmap);
                if (listener != null) {
                    listener.onImageLoadSuccess(url);
                }
            }
        });
        ImageLoader.getInstance().loadingImage.remove(url);
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
                    listener.onImageLoadFailure(url, t);
                }
            }
        });
        ImageLoader.getInstance().loadingImage.remove(url);
    }

    /*package*/ void onLoadCancel() {
        loadingThread.interrupt();
        Utils.runInUIThread(handler, new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onImageLoadCancel(url);
                }
            }
        });
        ImageLoader.getInstance().loadingImage.remove(url);
    }

    protected Bitmap checkBitmap(Bitmap bitmap, boolean isFromNetwork) {
        if (bitmap == null) {
            return null;
        }
        if (imageConfig.isKeepRatio < 0) {
            int width = (imageConfig.width == -1) ? view.getWidth() : imageConfig.width;
            int height = (imageConfig.height == -1) ? view.getHeight() : imageConfig.height;
            bitmap = Utils.zoomImg(bitmap, width, height);
        } else {
            if (imageConfig.width == -1 && imageConfig.height == -1) {
                int width, height;
                double widthRatio = (double) bitmap.getWidth() / (double) view.getWidth();
                double heightRatio = (double) bitmap.getHeight() / (double) view.getHeight();
                if (Math.abs(widthRatio - 1) > Math.abs(heightRatio -1)) {
                    width = (int) (bitmap.getWidth() / widthRatio);
                    height = (int) ((bitmap).getWidth() / widthRatio);
                } else {
                    width = (int) (bitmap.getWidth() / heightRatio);
                    height = (int) ((bitmap).getWidth() / heightRatio);
                }
                bitmap = Utils.zoomImg(bitmap, width, height);
            } else if (imageConfig.width == -1) {
                int width, height;
                double heightRatio = (double) bitmap.getHeight() / (double) imageConfig.height;
                width = (int) (bitmap.getWidth() / heightRatio);
                height = (int) (bitmap.getHeight() / heightRatio);
                bitmap = Utils.zoomImg(bitmap, width, height);
            } else if (imageConfig.height == -1) {
                int width, height;
                double widthRatio = (double) bitmap.getWidth() / (double) imageConfig.width;
                width = (int) (bitmap.getWidth() / widthRatio);
                height = (int) (bitmap.getHeight() / widthRatio);
                bitmap = Utils.zoomImg(bitmap, width, height);
            }
        }
        if (imageConfig.isFillView > 0) {
            if (bitmap.getHeight() < view.getHeight() || bitmap.getWidth() < view.getWidth()) {
                int width, height;
                double widthRatio = (double) bitmap.getWidth() / (double) view.getWidth();
                double heightRatio = (double) bitmap.getHeight() / (double) view.getHeight();
                if (widthRatio >= heightRatio) {
                    width = (int) (bitmap.getWidth() / heightRatio);
                    height = (int) (bitmap.getHeight() / heightRatio);
                } else {
                    width = (int) (bitmap.getWidth() / widthRatio);
                    height = (int) (bitmap.getHeight() / widthRatio);
                }
                bitmap = Utils.zoomImg(bitmap, width, height);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, view.getWidth(), view.getHeight(), null, false);
        }
        return bitmap;
    }
}
