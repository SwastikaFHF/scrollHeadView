package com.aitangba.testproject.apk;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aitangba.testproject.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by XBeats on 2019/10/14
 */
public class ApkTestActivity extends AppCompatActivity {

    private static final String TAG = "ApkTestTag";
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_test);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ApkTestActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                    return;
                }

                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ApkTestActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                    return;
                }

                try {
                    ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo("com.xiaomi.market", 0);
                    if (applicationInfo == null) {
                        Log.d(TAG, "applicationInfo == null");
                        return;
                    }
                    File file = new File(applicationInfo.sourceDir);
                    if (file.exists()) {
                        Log.d(TAG, "exists " + file.getAbsolutePath());
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                copyFile(file.getAbsolutePath(), Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS) + "/" + "market.apk");
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ApkTestActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }.start();
                    } else {
                        Log.d(TAG, "not exists");
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "复制单个文件操作出错");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {

        }
    }
}
