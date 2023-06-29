package com.dywl.familybed.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {

    private static OkHttpClient client;
    private static OkHttpUtils okHttpUtils;
    private OkHttpCallback callback;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://异常
                    IOException e = (IOException) msg.obj;
                    callback.onError(e);
                    break;
                case 2://成功
                    String result = (String) msg.obj;
                    callback.onResponse(result);
                    break;
            }
        }
    };

    /**
     * http请求
     */
    public static OkHttpUtils build() {
        OkHttpClient.Builder sBuilder = new OkHttpClient.Builder();
        client = sBuilder.build();
        okHttpUtils = new OkHttpUtils();
        return okHttpUtils;
    }

    //设置回调方法
    public OkHttpUtils setCallback(OkHttpCallback callback) {
        this.callback = callback;
        return okHttpUtils;
    }

    //post请求
    public OkHttpUtils postOkHttp(String url, Map<String, Object> params) {
        FormBody.Builder builder = new FormBody.Builder();
        String temp = "";
        for (String key : params.keySet()) {
            builder.add(key, String.valueOf(params.get(key)));
            temp += (key + "=" + String.valueOf(params.get(key)) + "&");
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = e;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Message msg = Message.obtain();
                msg.what = 2;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });
        return okHttpUtils;
    }


    public OkHttpUtils postAsync(String url, Map<String, Object> params) {
        RequestBody requestBody;
        if (params == null) {
            params = new HashMap<>();
        }
        /**
         * OKhttp3.0之后版本
         */
        FormBody.Builder builder = new FormBody.Builder();
        /**
         * 在这对添加的参数进行遍历，map遍历有四种方式，如果想要了解的可以网上查找
         */
        String temp = "";
        for (Map.Entry<String, Object> map : params.entrySet()) {
            String key = map.getKey();
            Object value;
            value = map.getValue() == null ? "" : map.getValue();
            builder.add(key, String.valueOf(value));
            temp += (key + "=" + String.valueOf(value));
        }
        requestBody = builder.build();
        final Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = e;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Message msg = Message.obtain();
                msg.what = 2;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });
        return okHttpUtils;
    }

    //请求回调接口
    public interface OkHttpCallback {
        void onError(Exception e);
        void onResponse(String result);
    }

    /**
     * 判断是否有网络连接
     */
    private boolean isNetworkConnected(@NonNull Context context) {
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return mNetworkInfo != null && mNetworkInfo.isAvailable();
    }

    /**
     * @param url      下载连接
     * @param saveDir  储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public void download(final String url, final String saveDir, final OnDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = isExistDir(saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath, getNameFromUrl(url));
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100); // 下载中
                        listener.onDownloading(progress);
                    }
                    fos.flush(); // 下载完成
                    listener.onDownloadSuccess(file);
                } catch (Exception e) {
                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /**
     * 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * 从下载连接中解析出文件名
     */
    @NonNull
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(File file);
        /**
         * 下载进度
         */
        void onDownloading(int progress);
        /**
         * 下载失败
         */
        void onDownloadFailed();
    }
}