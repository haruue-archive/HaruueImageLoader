package cn.com.caoyue.imageloader;

/**
 * 图片加载状态回调接口
 */
public interface ImageLoaderListener {

    /**
     * 如果图片加载完成，则此方法会被调用
     * @param url 图片的 URL
     */
    void onImageLoadSuccess(String url);

    /**
     * 如果图片加载失败，则此方法会被调用
     * @param url 图片的 URL
     * @param t 导致图片加载失败的异常
     */
    void onImageLoadFailure(String url, Throwable t);

    /**
     * 如果图片加载被取消，则此方法会被调用
     * @param url 图片的 URL
     */
    void onImageLoadCancel(String url);
}
