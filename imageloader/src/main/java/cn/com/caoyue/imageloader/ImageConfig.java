package cn.com.caoyue.imageloader;

import android.support.annotation.DrawableRes;

public class ImageConfig {

    @DrawableRes /*package*/ int drawableOnLoading = -1;
    @DrawableRes /*package*/ int drawableOnFailure = -1;
    /*package*/ int width = -1;
    /*package*/ int height = -1;
    /*package*/ int isKeepRatio = 0; // 0:default, 1:true, -1:false
    /*package*/ int isCache = 0;  // 0:default, 1:true, -1:false

    public ImageConfig setDrawableOnLoading(int drawableOnLoading) {
        this.drawableOnLoading = drawableOnLoading;
        return this;
    }

    public ImageConfig setDrawableOnFailure(int drawableOnFailure) {
        this.drawableOnFailure = drawableOnFailure;
        return this;
    }

    public ImageConfig setWidth(int width) {
        this.width = width;
        if (this.width != -1 && this.height != -1) {
            isKeepRatio = -1;
        }
        return this;
    }

    public ImageConfig setHeight(int height) {
        this.height = height;
        if (this.width != -1 && this.height != -1) {
            isKeepRatio = -1;
        }
        return this;
    }

    public ImageConfig setIsKeepRatio(boolean isKeepRatio) {
        this.isKeepRatio = isKeepRatio ? 1 : -1;
        return this;
    }

    public ImageConfig setIsCache(boolean isCache) {
        this.isCache = isCache ? 1 : -1;
        return this;
    }

}
