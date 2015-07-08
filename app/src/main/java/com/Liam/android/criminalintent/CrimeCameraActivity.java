package com.Liam.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2015/6/24.
 */
public class CrimeCameraActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new CrimeCameraFragment();
    }

}
