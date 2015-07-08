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
            setType() 和 SURFACE_TYPE_PUSH_BUFFERS 都是被弃用的参数，但在一些
            3.0版本之前的设备上需要他们来实现相机预览
         */
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback()
        {
            public void surfaceCreated(SurfaceHolder holder)
            {
                //让相机使用Surface填充它的预览界面
                try {
                    if(mCamera != null)
                        mCamera.setPreviewDisplay(holder);
                } catch (IOException exception)
                {
                    Log.e(TAG, "错误：设置预览显示", exception);
                }
            }

            public void surfaceDestroyed(SurfaceHolder holder)
            {
                //现在不能在这个surface上显示了，停止预览
                if(mCamera != null)
                    mCamera.stopPreview();
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
            {
                if(mCamera == null)
                    return;

                //surface改变了尺寸，更新surface的预览尺寸
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s = null;
                //下面来改变Size
                parameters.setPreviewSize(s.width, s.height);
                mCamera.setParameters(parameters);

                try{
                 mCamera.startPreview();
                } catch (Exception e)
                {
                    Log.e(TAG, "错误：不能开启预览", e);
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
