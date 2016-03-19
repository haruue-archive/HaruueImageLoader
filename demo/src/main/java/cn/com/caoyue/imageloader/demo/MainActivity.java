package cn.com.caoyue.imageloader.demo;

import android.location.GpsStatus;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import cn.com.caoyue.imageloader.ImageConfig;
import cn.com.caoyue.imageloader.ImageLoader;
import cn.com.caoyue.imageloader.ImageLoaderListener;

public class MainActivity extends AppCompatActivity {

    String url1 = "https://calcwiki.org/logowiki.png";
    String url2 = "http://www.caoyue.com.cn/images/haruue120x120.png";
    String url3 = "https://vps.caoyue.com.cn/IMG_1327.JPG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView image1 = (ImageView) findViewById(R.id.image1);
        ImageView image2 = (ImageView) findViewById(R.id.image2);
        ImageView image3 = (ImageView) findViewById(R.id.image3);
        ImageLoader.getInstance().loadImage(url1, image1, new Listener());
        ImageLoader.getInstance().loadImage(url2, image2, new Listener());
        ImageLoader.getInstance().loadImage(url3, image3, new Listener());
        image1.setOnClickListener(new Listener());
        image2.setOnClickListener(new Listener());
        image3.setOnClickListener(new Listener());
    }

    class ReloadListener implements View.OnClickListener {

        ImageView v;
        String url;

        public ReloadListener(ImageView v, String url) {
            this.v = v;
            this.url = url;
        }

        @Override
        public void onClick(View v) {
            ImageLoader.getInstance().loadImage(url, this.v, new ImageConfig().setRefresh(), new Listener());
        }
    }

    class Listener implements ImageLoaderListener, View.OnClickListener {
        @Override
        public void onImageLoadSuccess(String url) {
            Toast.makeText(getApplicationContext(), "成功: " + url, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onImageLoadFailure(String url, Throwable t) {
            Toast.makeText(getApplicationContext(), "失败: " + url, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onImageLoadCancel(String url) {
            Toast.makeText(getApplicationContext(), "取消: " + url, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image1:
                    ImageLoader.getInstance().stopLoad(url1);
                    v.setOnClickListener(new ReloadListener((ImageView) v, url1));
                    break;
                case R.id.image2:
                    ImageLoader.getInstance().stopLoad(url2);
                    v.setOnClickListener(new ReloadListener((ImageView) v, url2));
                    break;
                case R.id.image3:
                    ImageLoader.getInstance().stopLoad(url3);
                    v.setOnClickListener(new ReloadListener((ImageView) v, url3));
                    break;
            }
        }
    }
}
