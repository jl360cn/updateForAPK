package jlsky.updater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import java.io.File;

import jlsky.updater.http.IHttpManager;
import jlsky.updater.http.OkHttpManager;

public class utils {
    static IHttpManager manager = new OkHttpManager();
    /*public static IHttpManager getHttpManager() {
        return manager;
    }
    public static void setHttpManager(IHttpManager httpManager){
        manager = httpManager;
    }*/

    static Long getVerCode(Context context){
        PackageManager manager = context.getPackageManager();
        try{
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),0);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
                return (long) info.versionCode;
            }else{
                return info.getLongVersionCode();  //api >= 28
            }
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return (long) -1;
    }

    static void installApk(Activity activity, File apkFile){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //Uri uri = Uri.fromFile(apkFile);
        Uri uri = null;
        //todo N FileProvider
        //todo O install permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uri = androidx.core.content.FileProvider.getUriForFile(activity,activity.getPackageName()+".fileprovider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }else{
            uri = Uri.fromFile(apkFile);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        activity.startActivity(intent);
    }
}
