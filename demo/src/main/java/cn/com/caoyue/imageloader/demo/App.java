package cn.com.caoyue.imageloader.demo;

import android.app.Application;

import cn.com.caoyue.imageloader.ImageLoader;
import cn.com.caoyue.imageloader.ImageLoaderConfig;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderConfig.start(this)
                .setDefaultDrawableOnLoading(R.drawable.ic_hourglass_empty_black_24dp)
                .setDefaultDrawableOnFailure(R.drawable.ic_broken_image_black_24dp)
                .build();
    }
}
