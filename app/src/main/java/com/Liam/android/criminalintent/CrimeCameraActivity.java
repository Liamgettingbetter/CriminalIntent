package com.Liam.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Administrator on 2015/6/24.
 */
public class CrimeCameraActivity extends SingleFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // ���ر��ⴰ��
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // ����״̬������������ϵͳ���chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }

}
