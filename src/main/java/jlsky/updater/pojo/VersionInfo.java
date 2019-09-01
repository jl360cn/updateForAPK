package jlsky.updater.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class VersionInfo {
    private String version;
    private String md5;
    private String url;
    private String desc;

    public static VersionInfo parse(String resp){
        try{
            JSONObject json = new JSONObject(resp);
            String version = json.optString("version");
            String md5 = json.optString("md5");
            String url = json.optString("url");
            String desc = json.optString("desc");
            VersionInfo info = new VersionInfo();
            info.setVersion(version);
            info.setMd5(md5);
            info.setUrl(url);
            info.setDesc(desc);
            return info;
        }catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
