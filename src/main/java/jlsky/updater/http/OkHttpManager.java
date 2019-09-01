package jlsky.updater.http;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpManager implements IHttpManager {
    private static Handler handler = new Handler(Looper.getMainLooper());  //get main process
    private static OkHttpClient httpClient;
    static{
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(5,TimeUnit.SECONDS);
        httpClient = builder.build();
        // https ,sign by self
        // builder.sslSocketFactory()
    }
    @Override
    public void get(String url, final IHttpCallback callback) {
        Log.i("OkHttpManager", "get: "+ url);
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).get().build();
        Call call = httpClient.newCall(request);
        // Response resp = call.execute(request);  //sync function
        call.enqueue(new Callback() {
            // not ui process
            @Override
            public void onFailure(final Call call, final IOException e) {
                handler.post(new Runnable(){
                   public void run(){
                       callback.failed(e);
                   }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try{
                    final String result = response.body().string();
                    handler.post(new Runnable(){
                        public void run(){
                            callback.success(result);
                        }
                    });
                }catch (Throwable e){
                    e.printStackTrace();
                    callback.failed(e);
                }
            }
        });
    }

    @Override
    public void download(String url, final File apkFile, final IDownloadCallback callback) {
        if(!apkFile.exists()){
            apkFile.getParentFile().mkdirs();
        }
        Request.Builder builder = new Request.Builder();
        final Request request = builder.url(url).get().build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call,final @NotNull IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.failed(e);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (InputStream is = response.body().byteStream(); OutputStream os = new FileOutputStream(apkFile)) {
                    final long totalLen = response.body().contentLength();
                    byte[] buffer = new byte[1024 * 1024];
                    long currentLen = 0;
                    int bufferLen = 0;
                    while ((bufferLen = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bufferLen);
                        os.flush();
                        currentLen += bufferLen;
                        final long finalLen = currentLen;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.progress((int) (finalLen * 100 * 1.0f / totalLen));
                            }
                        });
                    }
                    try {
                        apkFile.setExecutable(true, false);
                        apkFile.setReadable(true, false);
                        apkFile.setWritable(true, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.success(apkFile);
                        }
                    });
                } catch (final Throwable e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.failed(e);
                        }
                    });
                }
            }
        });
    }
}
