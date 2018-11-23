//https://demonuts.com/listview-button/
package com.example.danie.wakeupwarden;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.app.TimePickerDialog;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    //initialize the global variables

    private static final String TAG = "MainActivity";
    protected FloatingActionButton insertAlarmButton;
    protected ListView alarmsListView;
    Boolean enable;
    private custom_list_adapter customAdapter;





    //Array Alarm used to store the alarms in the database
   public static ArrayList<Alarm> Alarm = new ArrayList<>();
    //Array String alarmList used for the sorted Array
    ArrayList<String> alarmList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //defining the views in the xml
        setContentView(R.layout.activity_main);
        alarmsListView = findViewById(R.id.AlarmListView);
        insertAlarmButton = findViewById(R.id.InsertAlarmButton);

        // opens alarm dialog to set the alarm
        insertAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "floating button onclick");
                InsertAlarmDialog dialog = new InsertAlarmDialog();
                dialog.show(getSupportFragmentManager(), "Insert alarm");

            }

        });


        // opens edit alarm dialog
alarmsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //opens the dialog
                        EditListItemDialog dialog = new EditListItemDialog();
                        dialog.show(getSupportFragmentManager(), "EditAlarmDialog");
                        // sends the id of the alarm to the dialogue in a bundle
                        Bundle bundle = new Bundle();
                        //Alarm.get(position).getId())) gets the id of the alarm based on its position in the listView which corresponds to its index in the array
                        bundle.putString("alarmId", String.valueOf(Alarm.get(position).getId()));
                        // set MyFragment Arguments
                        dialog.setArguments(bundle);

                }








        });



               //sets the wakeupwarden database reference
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("alarms");
        //addListenerForSingleValueEvent: Used to retrieve the values in the database for a single instance at the given reference
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //dataSnapshot stores the data retrieved (the entire database)
                        //loadListView adapts the data and displays the data on the ListView

                            loadListView((Map<String, Object>) dataSnapshot.getValue());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        //value even Listener used to update the list when a value is changed in the database
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //loadListView adapts the data and updates the data on the ListView
                loadListView((Map<String,Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addValueEventListener(listener);
    }

    // needed function to retrieve the data from the time set dialog
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //store values in string
        //TextView text = (TextView)findViewById(R.id.text);
        String time;
        if(minute < 10){
            time =  String.valueOf(hourOfDay) +":0" + String.valueOf(minute);
        }
        else
            time =  String.valueOf(hourOfDay) +":" + String.valueOf(minute);

        if(hourOfDay < 10){
            time =  "0" + time;
        }


        DatabaseReference databaseAlarms;
        //sets the wakeupwarden database reference alarm
        databaseAlarms = FirebaseDatabase.getInstance().getReference("alarms");
        // gets a key that is not used
        String key = databaseAlarms.push().getKey();
        //saves the alarm to the database with the key generated above
        //NOTE: ID OF THE ALARM IS THE KEY GENERATED
        databaseAlarms.child(key).setValue(new Alarm(key, "", time, true));
        //NOTE valueEventListener will realize this change in database


    }


    // loads the list View
    protected void loadListView(Map<String,Object> alarms) {




        // dummy List to set the global array Alarm to the same order as the alarmList displayed on the user interface
        ArrayList<Alarm> AlarmList = new ArrayList<>();

        //iterate through each alarm
        if(alarms==null) {
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alarmList);
            alarmsListView.setAdapter(adapter);
            return;

        }
        else {

            for (Map.Entry<String, Object> entry : alarms.entrySet()) {

                //Get alarm map
                Map alarmEntry = (Map) entry.getValue();


                //adds the an alarm to the alarm list
                AlarmList.add(new Alarm((String) alarmEntry.get("id"), (String) alarmEntry.get("title"), (String) alarmEntry.get("time"), (Boolean) alarmEntry.get("enable")));
            }
        }
        //sets the Alarm array to the correspond in the same order as the alarm list (global array)
        Alarm = AlarmList;
        //sorts the times
        Collections.sort(Alarm, new Comparator<com.example.danie.wakeupwarden.Alarm>() {
            @Override
            public int compare(Alarm o1, Alarm o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });

        customAdapter = new custom_list_adapter(this);
        alarmsListView.setAdapter(customAdapter);



    }





}
