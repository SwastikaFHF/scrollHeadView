package com.aitangba.testproject.upload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 林冠宏 on 2016/4/30.
 * new PicUpLoadExecutor(3)// 并发数
 　　　　  .withUpLoadUrl(url)   // 服务端接口文件的url
           .withHandler(handler) // 发完后发消息的handler
           .exec(picBitmaps);    // 要上传的图片bitmaps
 */

public class PicUpLoadExecutor {

    private static final String TAG = "PicUpLoadHelper";
    public static final int UpLoadFinish = 0x321;

    /**
     * 如果你不想内存不足是它们被gc掉，请换为强引用
     */
    private SoftReference<ExecutorService> fixedThreadPool = null;

    private Handler handler = null;
    private String url = null;

    public PicUpLoadExecutor(short poolSize) {
        fixedThreadPool = new SoftReference<ExecutorService>(Executors.newFixedThreadPool(poolSize));
    }

    /**
     * 设置图片上传路径
     */
    public PicUpLoadExecutor withUpLoadUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * 设置handler
     */
    public PicUpLoadExecutor withHandler(Handler handler) {
        this.handler = handler;
        return this;
    }

    public void exec(final Bitmap[] bitmaps) {
        if (bitmaps == null) {
            return;
        }
        int picNum = bitmaps.length;
        for (int i = 0; i < picNum; i++) {
            /** 默认执行上传任务 */
            final int picIndex = i;
            fixedThreadPool.get().execute(new Runnable() {
                @Override
                public void run() {
                    /** 批量 上传 图片，此静态函数若有使用全局变量，必须要 加 synchronized */
                    String json = uploadPic
                            (
                                    url,
                                    "" + picIndex + ".jpg", /** 我自己情况的上传 */
                                    bitmaps[picIndex]       /** 对应的图片流 */
                            );
                    if (json != null) {
                        /** 服务器上传成功返回的标示, 自己修改吧，我这里是我的情况 */
                        if (json.trim().equals("yes")) {
                            /** UpLoadFinish 是每次传完一张发信息的信息标示 */
                            handler.sendEmptyMessage(UpLoadFinish);
                        }
                    }
                    Log.d(TAG, "pic " + picIndex + " upLoad json ---> " + json);
                }
            });
        }
    }

    public void exec(final String[] paths) {
        if (paths == null) {
            return;
        }
        int picNum = paths.length;
        for (int i = 0; i < picNum; i++) {
            final int picIndex = i;
            fixedThreadPool.get().execute(new Runnable() {
                @Override
                public void run() {
                    String json = uploadPic(url, paths[picIndex]);
                    if (json != null) {
                        if (json.trim().equals("yes")) {
                            handler.sendEmptyMessage(UpLoadFinish);
                        }
                    }
                    Log.d(TAG, "pic " + picIndex + " upLoad json ---> " + json);
                }
            });
        }
    }

    private static String uploadPic(String uploadUrl, String url) {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        Bitmap bmp = BitmapFactory.decodeFile(url);
        return uploadPic(uploadUrl, fileName, bmp);
    }

    /** 若有依赖全局变量必须加 synchronized */
    /**
     * 此函数采用 tcp 数据包传输
     */
    private static String uploadPic(String uploadUrl, String filename, Bitmap bit) {
        String end = "\r\n"; /** 结束符 */
        String twoHyphens = "--";
        String boundary = "******"; /** 数据包头，设置格式没强性要求 */
        int compress = 100; /** 压缩初始值 */
        try {
            HttpURLConnection httpURLConnection
                    = (HttpURLConnection) new URL(uploadUrl).openConnection();
            /** 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃 */
            /** 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。*/
            httpURLConnection.setChunkedStreamingMode(256 * 1024);// 256K

            httpURLConnection.setConnectTimeout(10 * 1000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("token", "10051:abc"); //header
            /** tcp链接，防止丢包，需要进行长链接设置 */
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            /** 发送报头操作，dos 也是流发送体 */
            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            /** uploadedfile 是接口文件的接受流的键，client 和 server 要同步 */
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
                    + filename.substring(filename.lastIndexOf("/") + 1)
                    + "\""
                    + end);
            dos.writeBytes(end);

            /** 下面是压缩操作 */
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bit.compress(Bitmap.CompressFormat.JPEG, compress, baos);
            while (baos.toByteArray().length / 1024 > 500) {
                Log.d(TAG, "compress time ");
                baos.reset();
                compress -= 10;
                if (compress == 0) {
                    bit.compress(Bitmap.CompressFormat.JPEG, compress, baos);
                    break;
                }
                bit.compress(Bitmap.CompressFormat.JPEG, compress, baos);
            }

            /** 发送比特流 */
            InputStream fis = new ByteArrayInputStream(baos.toByteArray());
            byte[] buffer = new byte[10 * 1024]; // 8k+2k
            int count;
            while ((count = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, count);
            }
            fis.close();
            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            /** 获取返回值 */
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();

            Log.d(TAG, "send pic result " + result);
            dos.close();
            is.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
            return null;
        }
    }
}