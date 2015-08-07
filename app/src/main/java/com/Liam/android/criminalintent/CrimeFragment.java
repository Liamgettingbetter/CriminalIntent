package com.Liam.android.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Date;
import java.util.UUID;


/**
 * Created by Administrator on 2015/5/22.
 * This is a Fragment-based activity.
 * We use it to operate fragment and put relative FragmentView on main activity.
 */

/*
    ��Fragment�ǵ���ѡ��Crimeʱ��ʹ�õģ���������һ������Crime����ͼ
 */
public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID =
            "com.Liam.android.criminalIntent.crime_id";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_IMAGE = "image";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CONTACT = 2;
    
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Button mSuspectButton;

    // 实现类方法 --- 返回一个新的CrimeFragment实例
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public void updateDate() {
        // 在日期按钮上设置mCrime对象当前的日期
        mDateButton.setText(mCrime.getDate().toString());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

        setHasOptionsMenu(true);
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        // 事件的标题栏，以及监听标题文本变化事件的监听器
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                //This space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                //this one too
            }
        });

        // 日期按钮，及监听按钮点击事件的监听器
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });


        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set the crime's solved property
                mCrime.setSolved(isChecked);
            }
        });

        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 当点击该ImageButton时，使用Intent跳转到CrimeCameraActivity类中
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        // ��Ӳ����֧������豸��������������
        PackageManager pm = getActivity().getPackageManager();
        // 使用一个boolean参数来判断运行该APP的设备是否具有相机功能
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD ||
                Camera.getNumberOfCameras() > 0;
        if(!hasACamera) {
            // 如果硬件不提供摄像功能，则禁用该ImageButton
            mPhotoButton.setEnabled(false);
        }

        mPhotoView = (ImageView)v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if(p == null) {
                    return;
                }

                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
            }
        });

        Button reportButton = (Button)v.findViewById(R.id.crime_reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        mSuspectButton = (Button)v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });

        if(mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        return v;
    }

    private void showPhoto() {
        // ������Ƭ�ĳߴ磬����ͼ��ť
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;

        if(p != null) {
            String path = getActivity()
                    .getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }
        mPhotoView.setImageDrawable(b);
    }

    @Override
    public void onStart() {
        super.onStart();
        // ��ʾͼƬ
        showPhoto();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // ���ǲ��������أ���������
        if(resultCode != Activity.RESULT_OK)
            return;
        if(requestCode == REQUEST_DATE) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            // ����һ���µ���Ƭ���󣬲�����������crime��
            String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if(filename != null) {
                Photo p = new Photo(filename);
                mCrime.setPhoto(p);
                showPhoto();
            }
        } else if(requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();

            // ָ������Ҫ�ôη��ʷ���ֵ����
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            // ִ�з���
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            // ���μ��--ȷ���õ��˷���ֵ
            if(c.getCount() == 0) {
                c.close();
                return;
            }

            // �������ݵ����У�������ϵ�˵�����
            c.moveToFirst();
            String suspect = c.getString(0);
            mCrime.setSuspect(suspect);
            mSuspectButton.setText(suspect);
            c.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                if(NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // ����Crime
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onStop() {
        super.onStop();
        // ж��ͼƬ
        PictureUtils.cleanImageView(mPhotoView);
    }

    private String getCrimeReport() {
        String solvedString = null;

        // �����û��Ƿ�ѡ"���"��ȷ��solvedString
        if(mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}
