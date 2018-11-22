package com.example.danie.wakeupwarden;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;




import java.util.Calendar;

public class InsertAlarmDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //set clock to show the current hour minute always
        Calendar cal =  Calendar.getInstance();

        int hour = cal.get(cal.HOUR_OF_DAY);
        int min = cal.get(cal.MINUTE);




        //the current time will be shown on the fragment
        return new TimePickerDialog(getActivity(),(TimePickerDialog.OnTimeSetListener) getActivity(), hour, min, DateFormat.is24HourFormat(getActivity()));

    }
}