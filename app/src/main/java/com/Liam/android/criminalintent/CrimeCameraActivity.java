package com.Liam.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Administrator on 2015/6/24.
 */
public class CrimeCameraActivity extends SingleFragmentActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 隐藏标题窗口
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏和其他操作系统层的chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
    }
    @Override
    protected Fragment createFragment()
    {
        return new CrimeCameraFragment();
    }

}
