package jlsky.updater.http;

import java.io.File;

public interface IHttpManager {
    void get(String url,IHttpCallback callback);
    void download(String url, File apkFile, IDownloadCallback callback);
    //apkFile,下载后存放的位置。
}
