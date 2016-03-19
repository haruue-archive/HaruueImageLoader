package cn.com.caoyue.imageloader;

import android.support.annotation.DrawableRes;

/**
 * 单张图片加载时的配置，本配置可以覆盖 ImageLoaderConfig 的默认配置
 * @see ImageLoaderConfig
 */
public class ImageConfig {

    @DrawableRes /*package*/ int drawableOnLoading = -1;
    @DrawableRes /*package*/ int drawableOnFailure = -1;
    @DrawableRes /*package*/ int drawableOnCancel = -1;
    /*package*/ int width = -1;
    /*package*/ int height = -1;
    /*package*/ int isKeepRatio = 0; // 0:default, 1:true, -1:false
    /*package*/ int isCache = 0;  // 0:default, 1:true, -1:false
    /*package*/ int isFillView = 0; // 0:default, 1:true, -1 false
    /*package*/ int isRefresh = 0; // 0:default, 1:true, -1:false

    /**
     * 设置加载时 Drawable
     * @param drawableOnLoading 加载时 Drawable 的 resource id
     * @return 这个类的实例以便继续操作
     */
    public ImageConfig setDrawableOnLoading(int drawableOnLoading) {
        this.drawableOnLoading = drawableOnLoading;
        return this;
    }

    /**
     * 设置加载失败时 Drawable
     * @param drawableOnFailure 加载失败时 Drawable 的 resource id
     * @return 这个类的实例以便继续操作
     */
    public ImageConfig setDrawableOnFailure(int drawableOnFailure) {
        this.drawableOnFailure = drawableOnFailure;
        return this;
    }

    /**
     * 设置取消加载时 Drawable
     * @param drawableOnCancel 取消加载时 Drawable
     * @return 这个类的实例以便继续操作
     */
    public ImageConfig setDrawableOnCancel(int drawableOnCancel) {
        this.drawableOnCancel = drawableOnCancel;
        return this;
    }

    /**
     * 设置宽度 <br>
     * 如果同时设置高度将会自动<b>不保持比例</b>
     * @param width 宽度
     * @return 这个类的实例以便继续操作
     */
    public ImageConfig setWidth(int width) {
        this.width = width;
        if (this.width != -1 && this.height != -1) {
            isKeepRatio = -1;
        }
        return this;
    }

    /**
     * 设置高度 <br>
     * 如果同时设置宽度将会自动<b>不保持比例</b>
     * @param height 高度
     * @return 这个类的实例以便继续操作
     */
    public ImageConfig setHeight(int height) {
        this.height = height;
        if (this.width != -1 && this.height != -1) {
            isKeepRatio = -1;
        }
        return this;
    }

    /**
     * 设置是否保持比例 <br>
     * 如果同时设置了宽度和高度，那么<b>不保持比例</b>也会被自动设置 <br>
     * 如果同时设置了宽度和高度，又手动设置了需要保持比例则将会在加载时抛出异常
     * @param isKeepRatio 是否保持比例
     * @return 这个类的实例以便继续操作
     */
    public ImageConfig setIsKeepRatio(boolean isKeepRatio) {
        this.isKeepRatio = isKeepRatio ? 1 : -1;
        return this;
    }

    /**
     * 设置是否缓存
     * @param isCache 是否缓存
     * @return 这个类的实例以便继续操作
     */
    public ImageConfig setIsCache(boolean isCache) {
        this.isCache = isCache ? 1 : -1;
        return this;
    }

    /**
     * 将图片覆盖整个 ImageView ，即使这可能导致不能显示完整图片
     * @return 这个类的实例以便继续操作
     */
    public ImageConfig setFillView() {
        this.isFillView = 1;
        return this;
    }

    /**
     * 显示完整图片，即使这可能导致图片不能覆盖整个 ImageView
     * @return 这个类的实例以便继续操作
     */
    public ImageConfig setFullImage() {
        this.isFillView = -1;
        return this;
    }

    /**
     * 刷新（重新载入）图片<br>
     * 所有与此图片相关的缓存都会被无视并被新缓存覆盖（在设置了需要缓存的情况下）
     * @return 这个类的实例以便继续操作
     */
    public ImageConfig setRefresh() {
        this.isRefresh = 1;
        return this;
    }

}
