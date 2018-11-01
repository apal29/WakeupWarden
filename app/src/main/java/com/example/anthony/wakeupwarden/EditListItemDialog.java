package com.example.anthony.wakeupwarden;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import com.example.anthony.wakeupwarden.Database.DatabaseHelper;


public class EditListItemDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener{



    private static final String TAG = "EditAlarmDialog";
    EditText editText;
    Button cancelButton;
    Button saveButton;
    Button deleteButton;
    Button timeButton;
    Switch enableSwitch;
    boolean enable;
    String time;
    String title;
    long Id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflator, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflator.inflate(R.layout.edit_delete_mode, container, false);
      //  super.onCreate(savedInstanceState);
    //    setContentView(R.layout.edit_delete_mode);//here is your xml with EditText and 'Ok' and 'Cancel' buttons
        editText = view.findViewById(R.id.alarmTitleEditText);
        cancelButton = view.findViewById(R.id.cancelButton);
        saveButton = view.findViewById(R.id.saveButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        enableSwitch = view.findViewById(R.id.enableButton);
        timeButton = view.findViewById(R.id.timeButton);

        DatabaseHelper dbhelper = new DatabaseHelper(getActivity());
        // retrieves information sent by the chosen List Item (id of the alarm)
        String dummyId = getArguments().getString("alarmId");
        Id = new Long(dummyId);
        //dismisses the dialog
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onclick: cancel button");
                getDialog().dismiss();
            }
        });





        // sets all the parameters displays them on the dialog using the Id of the alarm
        timeButton.setText(dbhelper.getAlarmById(Id).getTime());
        editText.setText(dbhelper.getAlarmById(Id).getTitle());
        enable = dbhelper.getAlarmById(Id).getEnable();
        enableSwitch.setChecked(enable);

        //opens a set time dialog
        // Listener is added so that the time set will return to the dialog and not the mainActivity
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "floating button onclick");
                EditAlarmDialog dialog = new EditAlarmDialog();
                dialog.show(getFragmentManager(), "Update alarm");
                dialog.setListener(EditListItemDialog.this);
            }

        });
        //saves changes made on the enable switch
        enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on){
                if(on)
                {
                    enable = true;
                }
                else
                {
                    enable = false;
                }
            }
        });
        //saves all changes made in the dialogue
        //dismisses the dailogue once changes are saved to the database
        saveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = editText.getText().toString();



                DatabaseHelper dbhelper = new DatabaseHelper(getActivity());
                String time = String.valueOf(timeButton.getText());
                Alarm alarm = new Alarm(Id, title, time , enable);
                dbhelper.updateAlarmInfo(alarm);
                ((MainActivity) getActivity()).loadListView();
                getDialog().dismiss();


            }
        });
        // Deletes the alarm
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper dbhelper = new DatabaseHelper(getActivity());
                dbhelper.deleteAlarmByID(Long.toString(Id));
                ((MainActivity) getActivity()).loadListView();
                getDialog().dismiss();
            }
        });



/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_delete_mode);//here is your xml with EditText and 'Ok' and 'Cancel' buttons
        editText = findViewById(R.id.AlarmTitleEditText);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        enableSwitch = findViewById(R.id.enableButton);
    }*/

/*       @Override
        public void onClick (View v){
            editText.getText().toString();//here is your updated(or not updated) text
            dismiss();
        }*/


    return view;
    }
// needed to retrieve the time set on the time dialogue
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        //store values in string
        //TextView text = (TextView)findViewById(R.id.text);
        if(minute < 10){
            time =  String.valueOf(hourOfDay) +":0" + String.valueOf(minute);
        }
        else
            time =  String.valueOf(hourOfDay) +":" + String.valueOf(minute);
        timeButton.setText(time);
        // not needed for the ALARM MANAGER function that is not used
/*        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);*/
        //updateTime(c);
        // startAlarm(c);
    }
}


