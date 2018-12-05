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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class EditListItemDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener{


    //initialize the global variables
    private static final String TAG = "EditAlarmDialog";
    EditText editText;
    Button cancelButton;
    Button saveButton;
    Button deleteButton;
    TextView timeButton;
    boolean enable;
    String time;
    String title;
    String Id;
    Alarm alarm;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflator, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //set view of fragment class
        View view = inflator.inflate(R.layout.edit_delete_mode, container, false);
        //initialize the global variables
        editText = view.findViewById(R.id.alarmTitleEditText);
        cancelButton = view.findViewById(R.id.cancelButton);
        saveButton = view.findViewById(R.id.saveButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        timeButton = view.findViewById(R.id.alarmView);



        //dismisses the dialog
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onclick: cancel button");
                getDialog().dismiss();
            }
        });

        // retrieves information sent by the chosen List Item (id of the alarm)
        //Id retrieved by the bundle
        Id = getArguments().getString("alarmId");

        //referencing the child "alarm" from the wakeupwarden cloud database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("alarms");
        //Id contains the key passed through the bundle
        //the keys are filtered with the Id that contains a key
        //NOTE Id is equal to the key
        //NOTE listener below is performed knowing that there will be 1 key match
        ref.child(Id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //retrieve data from the map of the item
                String time = dataSnapshot.child("time").getValue(String.class);
                String title = dataSnapshot.child("title").getValue(String.class);
                Boolean alarmEnable = dataSnapshot.child("enable").getValue(Boolean.class);
                enable = alarmEnable;
                //define new alarm
                alarm = new Alarm(Id, title, time, alarmEnable);

                timeButton.setText(alarm.getTime());
                editText.setText(alarm.getTitle());


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
                getDialog().dismiss();

            }
        });




        // sets all the parameters displays them on the dialog using the Id of the alarm

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

        //saves all changes made in the dialogue
        //dismisses the dialogue once changes are saved to the database
        saveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time = String.valueOf(timeButton.getText());


                title = editText.getText().toString();


                Alarm alarm = new Alarm(Id, title, time , enable);

                //save the changes to the alarm cloud database
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("alarms");
                ref.child(Id).setValue(alarm);

                getDialog().dismiss();
            }
        });
        // Deletes the alarm
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("alarms");
                ref.child(Id).setValue(null);
                getDialog().dismiss();
            }
        });


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

    }

}
