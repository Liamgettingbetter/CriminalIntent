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
            // ��ʾ����ָʾ��
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // �����ļ���
            String filename = UUID.randomUUID().toString() + ".jpg";
            // ����JPEG���ݵ�����
            FileOutputStream os = null;
            boolean success = true;

            try {
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(data);
            } catch (Exception e) {
                Log.e(TAG, "д�ļ����� " + filename, e);
                success = false;
            } finally {
                try {
                    if(os != null)
                        os.close();
                } catch (Exception e) {
                    Log.e(TAG, "�����޷��ر��ļ� " + filename, e);
                    success = false;
                }
            }

            // ����Ƭ�ļ�������result_intent��
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
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);

        // ���ý�������ͼ��������Ϊ��ʱ����ʾ
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
            setType() �� SURFACE_TYPE_PUSH_BUFFERS ���Ǳ����õĲ���������һЩ
            3.0�汾֮ǰ���豸����Ҫ������ʵ�����Ԥ��
         */
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // ΪsurfaceHolder��ӻص�����
        holder.addCallback(new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                // �����ʹ��Surface�������Ԥ������
                try {
                    if(mCamera != null)
                        mCamera.setPreviewDisplay(holder);
                } catch (IOException exception) {
                    Log.e(TAG, "��������Ԥ����ʾ", exception);
                }
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                // ���ڲ��������surface����ʾ�ˣ�ֹͣԤ��
                if(mCamera != null)
                    mCamera.stopPreview();
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                if(mCamera == null)
                    return;

                // surface�ı��˳ߴ磬����surface��Ԥ���ߴ�
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);
                // ����Ԥ��Size
                parameters.setPreviewSize(s.width, s.height);
                // ���������Surface��ͼƬ�ߴ磬����õĳߴ�����Ϊ�����������ͼƬ�ߴ�
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), w, h);
                parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);

                try {
                    mCamera.startPreview();
                } catch (Exception e) {
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

        // ��Fragment��ͣ���ͷ������Դ
        if(mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


    /**
     * һ�������õ������óߴ���㷨��
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
