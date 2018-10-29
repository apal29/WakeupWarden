package com.example.anthony.wakeupwarden;

// code taken from moodle and modified
import android.app.AlarmManager;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TimePicker;
import android.app.TimePickerDialog;
import android.view.ContextMenu;
import android.widget.Toast;

import com.example.danie.wakeupwarden.Database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    //initialize the global variables

    private static final String TAG = "MainActivity";
    protected FloatingActionButton insertAlarmButton;
    protected ListView alarmsListView;
    String time;




    List<Alarm> alarms = null;
    List<Alarm> alarmsId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmsListView = findViewById(R.id.AlarmListView);

        insertAlarmButton = findViewById(R.id.InsertAlarmButton);



        loadListView();

        insertAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "floating button onclick");
                InsertAlarmDialog dialog = new InsertAlarmDialog();
                dialog.show(getSupportFragmentManager(), "Insert alarm");

            }

        });


        alarmsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        alarmsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               DatabaseHelper dbhelper = new DatabaseHelper(MainActivity.this);

               alarmsId = dbhelper.getAllAlarms();//get all courses from database
               ArrayList<Long> listAlarmsId = new ArrayList<>(); // dummy array used to view list

               for (int i = 0; i < alarms.size(); i++) {
                   listAlarmsId.add(alarms.get(i).getId());
               }



               EditListItemDialog dialog = new EditListItemDialog();
               dialog.show(getSupportFragmentManager(), "EditAlarmDialog");
               Bundle bundle = new Bundle();
               bundle.putString("alarmId", String.valueOf(listAlarmsId.get(position)));
               // set MyFragment Arguments
               dialog.setArguments(bundle);



            }

        });

    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //store values in string
        //TextView text = (TextView)findViewById(R.id.text);
        if(minute < 10){
            time =  String.valueOf(hourOfDay) +":0" + String.valueOf(minute);
        }
        else
            time =  String.valueOf(hourOfDay) +":" + String.valueOf(minute);
        DatabaseHelper dbhelper = new DatabaseHelper(this);
        dbhelper.insertAlarm(new Alarm(-1, "", time, true));
        loadListView();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        //updateTime(c);
       // startAlarm(c);

    }



    protected void loadListView() {
        DatabaseHelper dbhelper = new DatabaseHelper(this);
        alarms = dbhelper.getAllAlarms();//get all courses from database
        ArrayList<String> listAlarms = new ArrayList<>(); // dummy array used to view list

        for (int i = 0; i < alarms.size(); i++) {
                listAlarms.add(alarms.get(i).getTime());

            }
                        //send the list to the adatper to be viewed by the list view
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listAlarms);
        alarmsListView.setAdapter(adapter);

        }



/*
    private void startAlarm(Calendar c) {

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent= new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        //initalize alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
*/

    }

