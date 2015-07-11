package com.Liam.android.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/7/10.
 */
public class Photo {
    private static final String JSON_FILENAME = "filename";

    private String mFilename;
    // 构造一个Photo对象,表示磁盘上的现存文件
    public Photo(String filename) {
        mFilename = filename;
    }

    // 第二个构造方法
    public Photo(JSONObject json) throws JSONException {
        mFilename = json.getString(JSON_FILENAME);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILENAME, mFilename);
        return json;
    }

    public String getFilename() {
        return mFilename;
    }
}
