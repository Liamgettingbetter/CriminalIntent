package com.Liam.android.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;


/**
 * Created by Administrator on 2015/5/22.
 */
public class Crime
{
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";
    private static final String JSON_PHOTO = "photo";
    private static final String JSON_SUSPECT = "suspect";


    private UUID mId;
    private String mTitle;
    private Date mDate;
    private Photo mPhoto;
    private String mSuspect;

    private boolean mSolved;

    public Crime() {
        // 此处生成唯一标识符
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    // 第二个构造方法，重构了Crime()
    public Crime(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        if(json.has(JSON_TITLE))
           mTitle = json.getString(JSON_TITLE);
        mSolved = json.getBoolean(JSON_SOLVED);
        mDate = new Date(json.getLong(JSON_DATE));
        if(json.has(JSON_PHOTO))
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
        if(json.has(JSON_SUSPECT))
            mSuspect = json.getString(JSON_SUSPECT);
    }

    /*
        下面定义的方法
        将Crime类中的4个域，通过打包成键值对，转换成JSONObject的对象数据
        存放进json中，实现数据的封装。
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();

        json.put(JSON_ID, mId.toString());
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_SOLVED, mSolved);
        json.put(JSON_DATE, mDate.getTime());
        if(mPhoto != null)
            json.put(JSON_PHOTO, mPhoto.toJSON());
        json.put(JSON_SUSPECT, mSuspect);

        return json;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean mSolved) {
        this.mSolved = mSolved;
    }

    public Photo getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Photo p) {
        mPhoto = p;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String s) {
        mSuspect = s;
    }
}
