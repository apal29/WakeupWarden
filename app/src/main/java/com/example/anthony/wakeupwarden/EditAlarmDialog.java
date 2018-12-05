package com.example.anthony.wakeupwarden;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

// time dialogue called by the edit alarm dialogue
public class EditAlarmDialog extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //set clock to show the current hour minute always
        Calendar cal = Calendar.getInstance();

        int hour = cal.get(cal.HOUR_OF_DAY);
        int min = cal.get(cal.MINUTE);


       // String time = String.valueOf(hour) + ":" + String.valueOf(min);


        //the current time will be shown on the fragment
        return new TimePickerDialog(getContext(), this.listener, hour, min, DateFormat.is24HourFormat(getContext()));

    }

        public void setListener( final TimePickerDialog.OnTimeSetListener listener) {
            this.listener = listener;
        }



}

