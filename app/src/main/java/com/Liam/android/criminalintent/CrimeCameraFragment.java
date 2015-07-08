package com.Liam.android.criminalintent;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;

/**
 * Created by Administrator on 2015/6/24.
 */
public class CrimeCameraFragment extends Fragment
{
     private static final String TAG = "CrimeCameraFragment";

    private Camera mCamera;
    private SurfaceView mSurfaceView;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInsanceState)
    {
        View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);

        Button takePictureButton = (Button)v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                getActivity().finish();
            }
        });

        mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        /*
            setType() �� SURFACE_TYPE_PUSH_BUFFERS ���Ǳ����õĲ���������һЩ
            3.0�汾֮ǰ���豸����Ҫ������ʵ�����Ԥ��
         */
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback()
        {
            public void surfaceCreated(SurfaceHolder holder)
            {
                //�����ʹ��Surface�������Ԥ������
                try {
                    if(mCamera != null)
                        mCamera.setPreviewDisplay(holder);
                } catch (IOException exception)
                {
                    Log.e(TAG, "��������Ԥ����ʾ", exception);
                }
            }

            public void surfaceDestroyed(SurfaceHolder holder)
            {
                //���ڲ��������surface����ʾ�ˣ�ֹͣԤ��
                if(mCamera != null)
                    mCamera.stopPreview();
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
            {
                if(mCamera == null)
                    return;

                //surface�ı��˳ߴ磬����surface��Ԥ���ߴ�
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s = null;
                //�������ı�Size
                parameters.setPreviewSize(s.width, s.height);
                mCamera.setParameters(parameters);

                try{
                 mCamera.startPreview();
                } catch (Exception e)
                {
                    Log.e(TAG, "���󣺲��ܿ���Ԥ��", e);
                    mCamera.release();
                    mCamera = null;
                }
            }
        });

        return v;
    }

    @TargetApi(9)
    @Override
    public void onResume()
    {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            mCamera = Camera.open(0);
        else
            mCamera = Camera.open();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if(mCamera != null)
        {
            mCamera.release();
            mCamera = null;
        }
    }

}
