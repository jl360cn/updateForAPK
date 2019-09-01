package jlsky.updater.http;

import java.io.File;

public interface IDownloadCallback {
    void success(File apkFile);
    void progress(int progress);
    void failed(Throwable throwable);
}
