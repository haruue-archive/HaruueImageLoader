package cn.com.caoyue.imageloader;

import android.content.Context;
import android.support.annotation.DrawableRes;

/**
 * ImageLoader 的初始化和默认设置类，此类的默认设置可在实际加载时被 ImageConfig 覆盖
 * @see ImageLoader
 * @see ImageConfig
 */
public class ImageLoaderConfig {

    protected static ImageLoaderConfig config;

    @DrawableRes /*package*/ int defaultDrawableOnLoading = -1;
    @DrawableRes /*package*/ int defaultDrawableOnFailure = -1;
    @DrawableRes /*package*/ int defaultDrawableOnCancel = -1;
    /*package*/ boolean isCache = true;
    /*package*/ String cachePath;
    /*package*/ Context context;
    /*packege*/ boolean isFillView = false;

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

    /**
     * 开始初始化操作
     * @param context 如果在 Application 类中，可直接传入 this
     * @return 这个类的实例以便继续操作
     */
    public static ImageLoaderConfig start(Context context) {
        getInstance();
        config.context = context;
        config.cachePath = context.getCacheDir().getPath() + "/imageCache";
        return config;
    }

    /**
     * 结束初始化操作
     * @return ImageLoader 类的实例
     * @see ImageLoader
     */
    public ImageLoader build() {
        return ImageLoader.getInstance().setConfig(getInstance());
    }

    /**
     * 设置默认的加载时 Drawable
     * @param resId 加载时 Drawable 的 resource id
     * @return 这个类的实例以便继续操作
     */
    public ImageLoaderConfig setDefaultDrawableOnLoading(@DrawableRes int resId) {
        this.defaultDrawableOnLoading = resId;
        return this;
    }

    /**
     * 设置默认的加载失败时 Drawable
     * @param resId 加载失败时 Drawable 的 resource id
     * @return 这个类的实例以便继续操作
     */
    public ImageLoaderConfig setDefaultDrawableOnFailure(@DrawableRes int resId) {
        this.defaultDrawableOnFailure = resId;
        return this;
    }

    /**
     * 设置默认的取消加载时 Drawable
     * @param resId 取消加载时 Drawable 的 resource id
     * @return 这个类的实例以便继续操作
     */
    public ImageLoaderConfig setDefaultDrawableOnCancel(@DrawableRes int resId) {
        this.defaultDrawableOnCancel = resId;
        return this;
    }

    /**
     * 设置默认是否缓存
     * @param isCache 是否缓存
     * @return 这个类的实例以便继续操作
     */
    public ImageLoaderConfig setIsCache(boolean isCache) {
        this.isCache = isCache;
        return this;
    }

    /**
     * 设置缓存目录
     * @param path 缓存目录
     * @return 这个类的实例以便继续操作
     */
    public ImageLoaderConfig setCachePath(String path) {
        this.cachePath = path;
        return this;
    }

    /**
     * 设置默认显示完整图片而不是将图片覆盖整个 ImageView
     * @return 这个类的实例以便继续操作
     */
    public ImageLoaderConfig setDefaultFullImage() {
        this.isFillView = false;
        return this;
    }

    /**
     * 设置默认将图片覆盖整个 ImageView 而不是显示完整图片
     * @return 这个类的实例以便继续操作
     */
    public ImageLoaderConfig setDefaultFillView() {
        this.isFillView = true;
        return this;
    }

}
