# HaruueImageLoader
简易的图片加载器 - 实验版本    
使用多线程加载图片，支持内存缓存和本地缓存。    

## Download
``` Gradle
repositories {
    maven {
        url 'https://dl.bintray.com/haruue/maven/'
    }
}

dependencies {
    compile 'cn.com.caoyue.util:imageloader:0.9.9-experiment'
}

```

## Usage
在 `Application` 子类的 `onCreate()` 方法中初始化    

``` Java
public void onCreate() {
    super.onCreate();
    ImageLoaderConfig.start(this)
            .setDefaultDrawableOnLoading(R.drawable.ic_hourglass_empty_black_24dp)       //指定加载时显示的 Drawable
            .setDefaultDrawableOnFailure(R.drawable.ic_broken_image_black_24dp)     //指定加载失败时显示的 Drawable
            .build();   
}

```    
平常这样就好。    
还可以在初始化时指定更多细节，请参考 Javadoc 中的 [`ImageLoaderConfig`](http://haruue.github.io/HaruueImageLoader/cn/com/caoyue/imageloader/ImageLoaderConfig.html) 类。    

加载图片时    

``` Java
String url = "http://www.caoyue.com.cn/images/haruue120x120.png";
ImageView imageView = (ImageView) findViewById(R.id.image);
ImageLoader.getInstance().loadImage(url, imageView);

```    

可以在加载图片时使用 [`ImageConfig`](http://haruue.github.io/HaruueImageLoader/cn/com/caoyue/imageloader/ImageConfig.html) 指定额外的设置来覆盖初始化时指定的默认设置    

``` Java
ImageLoader.getInstance().loadImage(url, this.v, new ImageConfig().setRefresh().setFillView());

```    

提供 [`ImageLoaderListener`](http://haruue.github.io/HaruueImageLoader/cn/com/caoyue/imageloader/ImageLoaderListener.html) 接口，可以在图片加载完成、出错、取消时回调    

``` Java
ImageLoader.getInstance().loadImage(url, imageView, new ImageLoaderListener() {
    @Override
    public void onImageLoadSuccess(String url) {
            
    }

    @Override
    public void onImageLoadFailure(String url, Throwable t) {

    }

    @Override
    public void onImageLoadCancel(String url) {

    }
});

```    

提供 [`stopLoad()`](http://haruue.github.io/HaruueImageLoader/cn/com/caoyue/imageloader/ImageLoader.html#stopLoad-java.lang.String-) 方法，随时取消图片加载    

``` Java 
ImageLoader.getInstance().stopLoad(url);

```

## Documentation
[Javadoc](http://haruue.github.io/HaruueImageLoader/)    

## Known BUG
在加载过大的图片时会失败（demo 的第三张图）    

## Developer
Haruue Icymoon <haruue@caoyue.com.cn>

## License

```License
Copyright 2016 Haruue Icymoon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
