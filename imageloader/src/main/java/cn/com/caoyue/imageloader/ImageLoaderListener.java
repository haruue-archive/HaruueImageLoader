package cn.com.caoyue.imageloader;

public interface ImageLoaderListener {
    void onImageLoadSuccess();
    void onImageLoadFailure(Throwable t);
}
