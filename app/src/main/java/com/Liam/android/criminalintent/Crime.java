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

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public Crime()
    {
        //�˴�����Ψһ��ʶ��
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(JSONObject json) throws JSONException
    {
        mId = UUID.fromString(json.getString(JSON_ID));
        if(json.has(JSON_TITLE))
           mTitle = json.getString(JSON_TITLE);
        mSolved = json.getBoolean(JSON_SOLVED);
        mDate = new Date(json.getLong(JSON_DATE));
    }

    /*
        ���涨��ķ���
        ��Crime���е�4����ͨ������ɼ�ֵ�ԣ�ת����JSONObject�Ķ�������
        ��Ž�json�У�ʵ�����ݵķ�װ��
     */
    public JSONObject toJSON() throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_SOLVED, mSolved);
        json.put(JSON_DATE, mDate.getTime());

        return json;
    }

    @Override
    public String toString()
    {
        return mTitle;
    }

    public UUID getId()
    {
        return mId;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String mTitle)
    {
        this.mTitle = mTitle;
    }

    public Date getDate()
    {
        return mDate;
    }

    public void setDate(Date mDate)
    {
        this.mDate = mDate;

    }

    public boolean isSolved()
    {
        return mSolved;
    }

    public void setSolved(boolean mSolved)
    {
        this.mSolved = mSolved;
    }


}
