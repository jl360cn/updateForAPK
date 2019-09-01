package jlsky.updater.http;

public interface IHttpCallback {
    void success(String response);
    void failed(Throwable throwable);
}
