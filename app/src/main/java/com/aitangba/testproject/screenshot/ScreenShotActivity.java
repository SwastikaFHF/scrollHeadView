package com.aitangba.testproject.screenshot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.aitangba.testproject.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by fhf11991 on 2018/4/27.
 */

public class ScreenShotActivity extends AppCompatActivity {

    private ImageView imageView;
    private static final String TAG = "ScreenShotActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot);

        imageView = findViewById(R.id.image);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getWindow().getDecorView();
                view.setDrawingCacheEnabled(true);

                Bitmap originBitmap = view.getDrawingCache();
                Log.d("ScreenShot", "originBitmap size = " + (float)getBitmapSize(originBitmap) / 1024 / 1024 + "M");

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                originBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, null);
                Log.d("ScreenShot", "bitmap size = " + (float)getBitmapSize(bitmap) / 1024 / 1024 + "M");

                view.setDrawingCacheEnabled(false);

                imageView.setImageBitmap(bitmap);
            }
        });

        findViewById(Window.ID_ANDROID_CONTENT).addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                Log.d(TAG, "onViewAttachedToWindow");
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                Log.d(TAG, "onViewDetachedFromWindow");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public static Bitmap getScreenShotSync(View v) {
        Bitmap bitmap = null;
        // 将绘图缓存得到的,注意这里得到的只是一个图像的引用
        final Bitmap cacheBitmap = getMagicDrawingCache(v);
        if (cacheBitmap == null) {
            return bitmap;
        }
        bitmap = Bitmap.createBitmap(cacheBitmap, 0, 0, cacheBitmap.getWidth(),
                cacheBitmap.getHeight(), null, false);
        if (!cacheBitmap.isRecycled()) {
            cacheBitmap.recycle();
        }
        return bitmap;
    }

    //这里的两个参数宽和高一般是ImageView的宽和高
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap getMagicDrawingCache(View view) {
        Bitmap bitmap = null;
//        if (view.getWidth() + view.getHeight() == 0) {
//            view.measure(
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            view.layout(0, 0, AppEnv.SCREEN_WIDTH, AppEnv.SCREEN_HEIGHT);
//        }
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.RGB_565);
        bitmap.eraseColor(view.getResources().getColor(android.R.color.white));
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }
}
