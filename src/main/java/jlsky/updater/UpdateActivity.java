package jlsky.updater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import jlsky.updater.http.IDownloadCallback;
import jlsky.updater.http.IHttpCallback;
import jlsky.updater.pojo.VersionInfo;

public class UpdateActivity extends Activity {

    private static final String TAG = "UpdateActivity";
    Button btnUpdate;
    TextView tvold,tvnew,tvdesc,tvinfo;
    int state = 0;  //btn status, 0-init, 1-checked, 2-download, 3-reinstall
    VersionInfo info = null;  // get checked result
    Long currentVerCode = Long.valueOf(-1);
    Context mctx;
    Activity activity;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib_updater);
        mctx = this;
        activity = this;

        Intent intent = getIntent();
        final String updateUrl = intent.getStringExtra("update");
        Log.i(TAG, updateUrl);
        currentVerCode = utils.getVerCode(mctx);
        final File apk = new File(getCacheDir(), "target.apk");

        tvold = findViewById(R.id.lib_update_oldvercode);
        tvnew = findViewById(R.id.lib_update_vercode);
        tvdesc = findViewById(R.id.lib_update_desc);
        tvinfo = findViewById(R.id.lib_update_status);
        tvold.setText(getString(R.string.lib_current_vercode)+ currentVerCode);
        btnUpdate = findViewById(R.id.btn_lib_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(state){
                    case 0:
                        btnUpdate.setText("检查新版本");
                        utils.manager.get(updateUrl, verCallback);
                        break;
                    case 1:
                        assert info != null;
                        Log.i(TAG, "VersionInfo.getUrl: " + info.getUrl());
                        utils.manager.download(info.getUrl(), apk, downloadCallback);
                        state = 2;
                        break;
                    case 2:
                        //to install
                        utils.installApk(activity,apk);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + state);
                }
            }
        });
    }

    private IDownloadCallback downloadCallback = new IDownloadCallback() {
        @Override
        public void success(File apkFile) {
            Log.i(TAG, "success: apkFile downloaded - " + apkFile.getAbsolutePath());
        }

        @Override
        public void progress(int progress) {
            Log.i(TAG, "progress: "+ progress);
            btnUpdate.setText("下载中 "+ progress + "%");
            if(progress == 100){
                btnUpdate.setText("下载完成,继续安装");
            }
        }

        @Override
        public void failed(Throwable e) {
            e.printStackTrace();
        }
    };

    private IHttpCallback verCallback = new IHttpCallback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void success(String response) {
            Log.i(TAG, "success: " + response);
            info = VersionInfo.parse(response);
            state = 1;
            btnUpdate.setText("下载新版本");
            tvdesc.setText(info.getDesc());
            tvnew.setText(getString(R.string.lib_new_vercode) + info.getVersion());
            tvinfo.setText("校验码："+info.getMd5());
        }
        @Override
        public void failed(Throwable e) {
            e.printStackTrace();
        }
    };
}
