package com.Liam.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2015/5/29.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    // 覆盖getLayoutResId()
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

}
