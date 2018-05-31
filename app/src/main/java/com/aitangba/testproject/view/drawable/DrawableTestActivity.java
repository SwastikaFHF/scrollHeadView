package com.aitangba.testproject.view.drawable;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2017/8/24.
 */

public class DrawableTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setBackgroundDrawable(createInnerStrokesDrawable(R.color.colorAccent));
    }

    private Drawable createInnerStrokesDrawable(final int color) {
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());

        final int bottomStrokeColor = darkenColor(color);
        final int bottomStrokeColorHalfTransparent = halfTransparent(bottomStrokeColor);
        final int topStrokeColor = lightenColor(color);
        final int topStrokeColorHalfTransparent = halfTransparent(topStrokeColor);

        final Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        shapeDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                Log.d("Drawable", "resize --->  width = " + width + " height = " + height);
                return new LinearGradient(width / 2, 0, width / 2, height,
                        new int[] { topStrokeColor, topStrokeColorHalfTransparent, color, bottomStrokeColorHalfTransparent, bottomStrokeColor },
                        new float[] { 0f, 0.2f, 0.5f, 0.8f, 1f },
                        Shader.TileMode.CLAMP
                );
            }
        });

        return shapeDrawable;
    }

    private int darkenColor(int argb) {
        return adjustColorBrightness(argb, 0.9f);
    }

    private int lightenColor(int argb) {
        return adjustColorBrightness(argb, 1.1f);
    }

    private int adjustColorBrightness(int argb, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(argb, hsv);

        hsv[2] = Math.min(hsv[2] * factor, 1f);

        return Color.HSVToColor(Color.alpha(argb), hsv);
    }

    private int halfTransparent(int argb) {
        return Color.argb(
                Color.alpha(argb) / 2,
                Color.red(argb),
                Color.green(argb),
                Color.blue(argb)
        );
    }
}
