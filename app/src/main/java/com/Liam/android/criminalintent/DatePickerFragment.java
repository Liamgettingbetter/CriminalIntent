package com.Liam.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Administrator on 2015/6/3.
 */
public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE =
            "com.Liam.android.criminalintent.date";

    private Date mDate;

    // 设置类方法 --- 返回DatePickerFragment的新实例
    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private void sendResult(int resultCode) {
        if(getTargetFragment() == null)
            return;

        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, mDate);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = (Date)getArguments().getSerializable(EXTRA_DATE);

        // Create a Calendar to get the year, month, and day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
     
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);

        // 时间选择器，及监听日期变化的监听器
        DatePicker datePicker = (DatePicker)v.findViewById(R.id.dialog_date_datePicker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
           public void onDateChanged(DatePicker view, int year, int month, int day) {
               //使用Calendar对象，将年,月,日信息转换成Date
               // getTime()返回一个Date对象
               mDate = new GregorianCalendar(year, month, day).getTime();

               // update argument to preserve selected value on rotation
               getArguments().putSerializable(EXTRA_DATE, mDate);
           }
        });

        return new AlertDialog.Builder(getActivity()).setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                }).create();
    }

}
