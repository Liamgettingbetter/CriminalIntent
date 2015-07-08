package com.Liam.android.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Administrator on 2015/5/29.
 */
public class CrimeLab
{
    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    private ArrayList<Crime> mCrimes;
    private CriminalIntentJSONSerializer mSerializer;
    private Context mAppContext;

    private static CrimeLab sCrimeLab;


    private CrimeLab(Context appContext)
    {
        mAppContext = appContext;
        mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);

        try {
            mCrimes = mSerializer.loadCrimes();
        } catch (Exception e) {
            mCrimes = new ArrayList<Crime>();
            Log.e(TAG, "Error loading criems: ", e);
        }
    }

    public static CrimeLab get(Context c)
    {
        if(sCrimeLab == null)
        {
            sCrimeLab = new CrimeLab(c.getApplicationContext());
        }
        return sCrimeLab;
    }

    public void addCrime(Crime c)
    {
        mCrimes.add(c);
    }

    public void deleteCrime(Crime d)
    {
        mCrimes.remove(d);
    }


    public boolean saveCrimes()
    {
        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "crimes saved to file");
            return true;
        } catch(Exception e){
            Log.e(TAG, "Error saving crimes: ", e);
            return false;
        }
    }

    public ArrayList<Crime> getCrimes()
    {
        return mCrimes;
    }

    /*�����������������˼����ֻ�������Ӧ��ID��
      ����������ܷ��ظ���һ��Crime����
      �˴�������Java��for eachѭ�����ŵ㣺���������
      ���Ǵ�ͳ�Ľṹ�ͳ���������ԣ�û������˵�ľ���C��������ѭ�������߱�������
     */
    public Crime getCrime(UUID id)
    {
        for(Crime c : mCrimes)
        {
            if(c.getId().equals(id))
                return c;
        }
        return null;
    }
}