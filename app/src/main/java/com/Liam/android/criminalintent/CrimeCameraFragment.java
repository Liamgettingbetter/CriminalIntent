package com.Liam.android.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2015/6/24.
 */
public class CrimeCameraFragment extends Fragment {
    private static final String TAG = "CrimeCameraFragment";
    public static final String EXTRA_PHOTO_FILENAME =
            "com.Liam.android.criminalintent.photo_filename";

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            // 显示进度指示条
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // 创建文件名
            String filename = UUID.randomUUID().toString() + ".jpg";
            // 保存JPEG数据到磁盘
            FileOutputStream os = null;
            boolean success = true;

            try {
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(data);
            } catch (Exception e) {
                Log.e(TAG, "写文件错误 " + filename, e);
                success = false;
            } finally {
                try {
                    if(os != null)
                        os.close();
                } catch (Exception e) {
                    Log.e(TAG, "错误：无法关闭文件 " + filename, e);
                    success = false;
                }
            }

            // 把照片文件名放入result_intent中
            if(success) {
               Intent i = new Intent();
                i.putExtra(EXTRA_PHOTO_FILENAME, filename);
                getActivity().setResult(Activity.RESULT_OK, i);
            } else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }

            getActivity().finish();
        }
    };

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInsanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);

        // 设置进度条视图，且设置为暂时不显示
        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        Button takePictureButton = (Button)v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              if(mCamera != null) {
                  mCamera.takePicture(mShutterCallback, null, mJpegCallback);
              }
            }
        });

        mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        /*
            setType() 和 SURFACE_TYPE_PUSH_BUFFERS 都是被弃用的参数，但在一些
            3.0版本之前的设备上需要他们来实现相机预览
         */
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // 为surfaceHolder添加回调函数
        holder.addCallback(new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                // 让相机使用Surface填充它的预览界面
                try {
                    if(mCamera != null)
                        mCamera.setPreviewDisplay(holder);
                } catch (IOException exception) {
                    Log.e(TAG, "错误：设置预览显示", exception);
                }
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                // 现在不能在这个surface上显示了，停止预览
                if(mCamera != null)
                    mCamera.stopPreview();
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                if(mCamera == null)
                    return;

                // surface改变了尺寸，更新surface的预览尺寸
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);
                // 设置预览Size
                parameters.setPreviewSize(s.width, s.height);
                // 获得适用于Surface的图片尺寸，将获得的尺寸设置为相机将创建的图片尺寸
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), w, h);
                parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);

                try {
                    mCamera.startPreview();
                } catch (Exception e) {
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
    public void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            mCamera = Camera.open(0);
        else
            mCamera = Camera.open();
    }

    @Override
    public void onPause() {
        super.onPause();

        // 当Fragment暂停，释放相机资源
        if(mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


    /**
     * 一个用来得到最大可用尺寸的算法。
     */
    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for(Camera.Size s: sizes) {
            int area  = s.width * s.height;
            if(area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }
}
