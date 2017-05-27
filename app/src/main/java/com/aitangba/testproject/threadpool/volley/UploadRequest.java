package com.aitangba.testproject.threadpool.volley;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by fhf11991 on 2017/5/27.
 */

public class UploadRequest extends Request {

    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码

    private HttpURLConnection mConn;

    private final String mUrl;
    private final String mFileAddress;

    public UploadRequest(String url, String fileAddress, Listener listener) {
        super(listener);
        mUrl = url;
        mFileAddress = fileAddress;
    }

    @Override
    public String performRequest() {
        File file = new File(mFileAddress);

        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        try {
            URL url = new URL(mUrl);
            mConn = (HttpURLConnection) url.openConnection();
            mConn.setReadTimeout(TIME_OUT);
            mConn.setConnectTimeout(TIME_OUT);
            mConn.setDoInput(true); // 允许输入流
            mConn.setDoOutput(true); // 允许输出流
            mConn.setUseCaches(false); // 不允许使用缓存
            mConn.setRequestMethod("POST"); // 请求方式
            mConn.setRequestProperty("Charset", CHARSET); // 设置编码
            mConn.setRequestProperty("connection", "keep-alive");
            mConn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (file != null) {
                DataOutputStream dos = new DataOutputStream(mConn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type:image/jpeg" + LINE_END); // 这里配置的Content-type很重要的 ，用于服务器端辨别文件的类型的
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream fileInputStream = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = fileInputStream.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                fileInputStream.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();

                int responseCode = mConn.getResponseCode();
                if (responseCode == 200) {
                    InputStream responseInputStream = mConn.getInputStream();
                    String response = getStringFromInputStream(responseInputStream);
                    return response;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            mConn.disconnect();
            mConn = null;
        }

        return null;
    }

    @Override
    public void close() {
        if(mConn != null) {
            mConn.disconnect();
        }
    }
}
