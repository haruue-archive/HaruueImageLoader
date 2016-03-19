package cn.com.caoyue.imageloader;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片加载器
 */
public class ImageLoader {

    protected static ImageLoader imageLoader;

    /*package*/ Map<String, ImageLoaderCore> loadingImage = new HashMap<String, ImageLoaderCore>(0);

    /*package*/ ImageLoaderConfig config;

    protected Handler handler;

    /**
     * 由于需要设置，因此不公有构造器
     */
    protected ImageLoader() {

    }

    /**
     * 获取实例
     * @return ImageLoader 实例
     */
    public static ImageLoader getInstance() {
        synchronized (ImageLoader.class) {
            if (imageLoader == null) {
                imageLoader = new ImageLoader();
            }
            return imageLoader;
        }
    }

    /**
     * 检查是否初始化，需要在 Application 的子类中使用 <code>ImageLoaderConfig.getInstance(getApplication()).build()</code> 对 ImageLoader 进行初始化
     * @throws IllegalStateException 如果没有初始化就会抛出此异常
     */
    protected static void checkHasConfig() throws IllegalStateException {
        if (imageLoader.config == null) {
            throw new IllegalStateException("You have to initialize ImageLoader by ImageLoaderConfig.getInstance(getApplication()).build() before use it");
        }
    }

    /*package*/ ImageLoader setConfig(ImageLoaderConfig config) {
        this.config = config;
        this.handler = new Handler(Looper.getMainLooper());
        return this;
    }

    /**
     * 加载图片
     * @param url 图片的 URL
     * @param view 需要加载到的 ImageView 实例
     * @param config 针对此次加载的配置
     * @param listener 加载监听器
     * @see ImageLoaderConfig
     * @see ImageLoader
     */
    public synchronized void loadImage(@NonNull String url, @NonNull ImageView view, @Nullable ImageConfig config, @Nullable ImageLoaderListener listener) {
        checkHasConfig();
        new ImageLoaderCore(url, view, config, listener).load();
    }

    /**
     * 加载图片
     * @param url 图片的 URL
     * @param view 需要加载到的 ImageView 实例
     * @param listener 加载监听器
     * @see ImageLoaderListener
     */
    public synchronized void loadImage(@NonNull String url, @NonNull ImageView view, @Nullable ImageLoaderListener listener) {
        loadImage(url, view, null, listener);
    }

    /**
     * 加载图片
     * @param url 图片的 URL
     * @param view 需要加载到的 ImageView 实例
     * @param config 针对此次加载的配置
     * @see ImageConfig
     */
    public synchronized void loadImage(@NonNull String url, @NonNull ImageView view, @Nullable ImageConfig config) {
        loadImage(url, view, config, null);
    }

    /**
     * 加载图片
     * @param url 图片的 URL
     * @param view 需要加载到的 ImageView 实例
     */
    public synchronized void loadImage(@NonNull String url, @NonNull ImageView view) {
        loadImage(url, view, null, null);
    }

    /**
     * 停止（取消）加载 <br>
     * 强制中断一张图片的加载（若图片已加载完成则无任何作用）
     * @param url 图片的 URL
     */
    public synchronized void stopLoad(@NonNull String url) {
        if (loadingImage.containsKey(url)) {
            loadingImage.get(url).onLoadCancel();
        }
    }

    /**
     * 清除缓存 <br>
     * 清除所有已加载图片的缓存
     */
    public void clearCache() {
        checkHasConfig();
        synchronized (ImageLoader.class) {
            ImageCache.bitmapCache.evictAll();
            if (config.cachePath != null) {
                Utils.runInUIThread(handler, new Runnable() {
                    @Override
                    public void run() {
                        Utils.delete(new File(config.cachePath));
                    }
                });
            }
        }
    }

    /**
     * 清除内存图片缓存
     */
    public void clearRAMCache() {
        synchronized (ImageLoader.class) {
            ImageCache.bitmapCache.evictAll();
        }
    }

}
