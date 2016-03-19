package cn.com.caoyue.imageloader.demo;

import android.location.GpsStatus;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import cn.com.caoyue.imageloader.ImageLoader;
import cn.com.caoyue.imageloader.ImageLoaderListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView image1 = (ImageView) findViewById(R.id.image1);
        ImageView image2 = (ImageView) findViewById(R.id.image2);
        ImageView image3 = (ImageView) findViewById(R.id.image3);
        ImageLoader.getInstance().loadImage("https://calcwiki.org/logowiki.png", image1, new Listener());
        ImageLoader.getInstance().loadImage("http://www.caoyue.com.cn/images/haruue120x120.png", image2, new Listener());
        ImageLoader.getInstance().loadImage("https://avatars1.githubusercontent.com/u/10269126?v=3&s=400", image3, new Listener());

    }

    class Listener implements ImageLoaderListener {

        @Override
        public void onImageLoadSuccess() {
            Toast.makeText(getApplicationContext(), "加载完成", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onImageLoadFailure(Throwable t) {
            Log.e("ImageLoader", t.getMessage());
            Log.e("ImageLoader", t.getLocalizedMessage());
            Log.e("ImageLoader", t.getSuppressed().toString());

            t.printStackTrace();
            Toast.makeText(getApplicationContext(), "Load failure", Toast.LENGTH_LONG).show();
        }
    }
}
