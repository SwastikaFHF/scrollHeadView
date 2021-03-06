package com.aitangba.testproject.ubb;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2016/12/22.
 */

public class UbbActivity extends AppCompatActivity {

    private static final String str = "（本小题13分）\n  [color]A、B、C[/color]三个班共有100 [color]100[/color]名学生，为调查[u]他们的体育锻[/u]炼情况，通过分层"
            + "[strong]抽样获得了部分学生一周的[/strong]锻炼时间，数据[em]如下表（单位：小时）[/em]：[img] "
            + "http://latex.codecogs.com/gif.latex?%5Cfrac%7B1%7D%7Bx%7D-%5Cfrac%7B1%7D%7By%7D%3E0[/img]\n（I）试估计C[img]"
            + "http://t11.baidu.com/it/u=1524605158,2062122205&fm=76[/img]班的学生人数；  \n（II）从A班和C班抽出的学生中，各随机选取一人，"
            + "A班选出的人记为甲，C班选出的人记为乙，假设所有学生的锻炼时间相对独立，求该周甲的锻炼[color]时间比乙的锻炼时间长的[/color]概"
            + "率； \n（III）再从A、B、C三个X[up]Z2[/up]班中各随机抽取一名学生，他们该周的锻炼时间分别是7，9，8.25（单位：小时），这3个新"
            + "数据与表格中的数据构成的新样本的平均数记为μ1，表格中数据的平均数记为μ0，试判断μ0和μ1的大小，<b>（结论不要求证明）</b>";
    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());

        mTextView.setText(Html.fromHtml(UbbUtils.ubb2Html(str), mImageGetter, null));
    }

    Html.ImageGetter mImageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            URLDrawable urlDrawable = new URLDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
            urlDrawable.setTextView(mTextView);
            ImageGetterAsyncTask getterTask = new ImageGetterAsyncTask(urlDrawable, source);
            getterTask.execute();
            return urlDrawable;
        }
    };
}
