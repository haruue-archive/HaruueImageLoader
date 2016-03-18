package cn.com.caoyue.imageloader;

import android.content.Context;
import android.support.annotation.DrawableRes;

public class ImageLoaderConfig {

    protected static ImageLoaderConfig config;

    @DrawableRes /*package*/ int defaultDrawableOnLoading = -1;
    @DrawableRes /*package*/ int defaultDrawableOnFailure = -1;
    /*package*/ boolean isCache = true;
    /*package*/ String cachePath;
    /*package*/ Context context;

    protected ImageLoaderConfig() {

    }

    protected static ImageLoaderConfig getInstance() {
        synchronized (ImageLoaderConfig.class) {
            if (config == null) {
                config = new ImageLoaderConfig();
            }
            return config;
        }
    }

    public static ImageLoaderConfig start(Context context) {
        getInstance().context = context;
        getInstance().cachePath = context.getCacheDir().getPath() + "/imageCache";
        return getInstance();
    }

    public ImageLoader build() {
        return ImageLoader.getInstance().setConfig(getInstance());
    }

    public ImageLoaderConfig setDefaultDrawableOnLoading(@DrawableRes int ResId) {
        this.defaultDrawableOnLoading = ResId;
        return this;
    }

    public ImageLoaderConfig setDefaultDrawableOnFailure(@DrawableRes int ResId) {
        this.defaultDrawableOnFailure = ResId;
        return this;
    }

    public ImageLoaderConfig setIsCache(boolean isCache) {
        this.isCache = isCache;
        return this;
    }

    public ImageLoaderConfig setCachePath(String path) {
        this.cachePath = path;
        return this;
    }

}
