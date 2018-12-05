package com.example.anthony.wakeupwarden;

import android.app.TimePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    //initialize the global variables

    private static final String TAG = "MainActivity";
    protected FloatingActionButton insertAlarmButton;
    protected ListView alarmsListView;
    protected TextView wifiStatus;
    private custom_list_adapter customAdapter;
    static public NetworkInfo wifiCheck;
    //set path for the cloud firebase real time database
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("alarms");
    SwipeRefreshLayout mSwipeRefreshLayout;

    //Array Alarm used to store the alarms in the database locally
   public static ArrayList<Alarm> Alarm = new ArrayList<>();
    //Array AlarmnoWifi used to view the added alarms when phone is not connected to wifi
    public static ArrayList<Alarm> AlarmnoWifi = new ArrayList<>();
    //Array Alarm used to store the alarms (AM_PM form) locally
    ArrayList<Alarm> alarmListAM_PM = new ArrayList<>();
    //Array Alarm used to store the alarms (AM_PM form) locally when phone is not connected to wifi
    public static ArrayList<Alarm> AlarmnoWifiAMPM = new ArrayList<>();
    //timeview is the view form for AM/PM or Army Time
    //true is AM/PM view
    //false is army time
    static Boolean timeView = true;
    //wifi is the wifi connectivity status
    //true is wifi connected
    //false is wifi is not connected
    static public Boolean wifi;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //defining the views in the xml
        setContentView(R.layout.activity_main);
        alarmsListView = findViewById(R.id.AlarmListView);
        insertAlarmButton = findViewById(R.id.InsertAlarmButton);
        wifiStatus = findViewById(R.id.wifiStatus);

        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        //add a refresh feature to the activity
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                     @Override
                                                     public void onRefresh() {
                                                         recreate();
                                                     }
                                                 });

        //retrieve wifi status
       ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
       wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        // perform if wifi is connected
        if (wifiCheck.isConnected()) {
            wifi = true;
            //sets the wakeupwarden database reference
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
            //clear the no wifi arrays
            AlarmnoWifi.clear();
            AlarmnoWifiAMPM.clear();





        }
        //perform if wifi is not connected
        else {
            wifi = false;
            wifiStatus.setText("Connect to WIFI to sync your Alarms");
            loadListViewNoWifi();
        }




        // opens alarm dialog to set the alarm
        insertAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "floating button onclick");
                InsertAlarmDialog dialog = new InsertAlarmDialog();
                dialog.show(getSupportFragmentManager(), "Insert alarm");

            }

        });


        // opens edit alarm dialog when selecting a list view
        alarmsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        // wifi check done in the case wifi is disconnected
                        // wifi status will refresh and no bugs will appear
                        if (wifiCheck.isConnected()) {
                                if (timeView == false) {
                                    //opens the dialog
                                    EditListItemDialog dialog = new EditListItemDialog();
                                    dialog.show(getSupportFragmentManager(), "EditAlarmDialog");
                                    // sends the id of the alarm to the dialogue in a bundle
                                    Bundle bundle = new Bundle();
                                    //Alarm.get(position).getId())) gets the id of the alarm based on its position in the listView which corresponds to its index in the array
                                    bundle.putString("alarmId", String.valueOf(Alarm.get(position).getId()));
                                    // set MyFragment Arguments
                                    dialog.setArguments(bundle);
                                } else {
                                    //opens the dialog
                                    EditListItemDialog dialog = new EditListItemDialog();
                                    dialog.show(getSupportFragmentManager(), "EditAlarmDialog");
                                    // sends the id of the alarm to the dialogue in a bundle
                                    Bundle bundle = new Bundle();
                                    //Alarm.get(position).getId())) gets the id of the alarm based on its position in the listView which corresponds to its index in the array
                                    bundle.putString("alarmId", String.valueOf(alarmListAM_PM.get(position).getId()));
                                    // set MyFragment Arguments
                                    dialog.setArguments(bundle);
                                }
                            }

                    else{ // wifi is not connected
                    Toast toast = Toast.makeText(MainActivity.this, "Connect to Wifi to edit your alarm", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }








                });




        




    }
    //empty so the user cannot go back to the HomeActivity  (Load Page)
    @Override
    public void onBackPressed() {

    }
    // needed function to retrieve the data from the time set dialog
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //store values in string
        //set the time to a string of proper form 00:00
        String time;
        if (minute < 10) {
            time = String.valueOf(hourOfDay) + ":0" + String.valueOf(minute);
        } else
            time = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);

        if (hourOfDay < 10) {
            time = "0" + time;
        }

        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // again checks wifi to either save directly in database
        // if no wifi is connected
        // alarm will be added to the a local array to be later displayed and added to the database that will when connected to wifi will be added to the cloud database
        if (wifiCheck.isConnected()) {
        DatabaseReference databaseAlarms;
        //sets the wakeupwarden database reference alarm
        databaseAlarms = FirebaseDatabase.getInstance().getReference("alarms");
        // gets a key that is not used
        String key = databaseAlarms.push().getKey();
        //saves the alarm to the database with the key generated above
        //NOTE: ID OF THE ALARM IS THE KEY GENERATED
        databaseAlarms.child(key).setValue(new Alarm(key, "", time, true));


        }


        else{
            DatabaseReference databaseAlarms;
            //sets the wakeupwarden database reference alarm
            databaseAlarms = FirebaseDatabase.getInstance().getReference("alarms");
            // gets a key that is not used
            String key = databaseAlarms.push().getKey();
            //saves the alarm to the database with the key generated above
            //NOTE: ID OF THE ALARM IS THE KEY GENERATED
            databaseAlarms.child(key).setValue(new Alarm(key, "", time, true));
            AlarmnoWifi.add(new Alarm("x", "", time, true));
            AlarmnoWifiAMPM.add(new Alarm("x", "", time, true));
            loadListViewNoWifi();
            //NOTE valueEventListener will realize this change in database

        }


    }


    // loads the list View
    // datasnapshot with the parameters returns a Map of all the alarms
    protected void loadListView(Map<String,Object> alarms) {




        // dummy List to set the global array Alarm to the same order as the alarmList displayed on the user interface
        ArrayList<Alarm> AlarmList = new ArrayList<>();
        ArrayList<Alarm> AlarmListAMPM = new ArrayList<>();

        //iterate through each alarm
        if(alarms!=null) {
            for (Map.Entry<String, Object> entry : alarms.entrySet()) {

                //Get alarm map
                Map alarmEntry = (Map) entry.getValue();


                //adds the an alarm to the alarm list
                AlarmList.add(new Alarm((String) alarmEntry.get("id"), (String) alarmEntry.get("title"), (String) alarmEntry.get("time"), (Boolean) alarmEntry.get("enable")));
                AlarmListAMPM.add(new Alarm((String) alarmEntry.get("id"), (String) alarmEntry.get("title"), (String) alarmEntry.get("time"), (Boolean) alarmEntry.get("enable")));

            }


            //sets the Alarm array to the correspond in the same order as the alarm list (global array)
            Alarm = AlarmList;


            //sorts the times
            Collections.sort(Alarm, new Comparator<com.example.anthony.wakeupwarden.Alarm>() {
                @Override
                public int compare(Alarm o1, Alarm o2) {
                    return o1.getTime().compareTo(o2.getTime());
                }
            });
            Collections.sort(AlarmListAMPM, new Comparator<com.example.anthony.wakeupwarden.Alarm>() {
                @Override
                public int compare(Alarm o1, Alarm o2) {
                    return o1.getTime().compareTo(o2.getTime());
                }
            });
            // time is converted to the AM PM Type and saved in a local array

            for (int i = 0; i < AlarmListAMPM.size(); i++) {
                String str = AlarmListAMPM.get(i).getTime().substring(0, 2);
                int hour = Integer.valueOf(str);
                // check for times before noon (12 PM)
                if (hour < 12) {
                    if (hour == 0){ // if midnight set to AM/PM form
                        hour = 12;
                        String str1 = AlarmListAMPM.get(i).getTime().substring(3, 5);
                        AlarmListAMPM.set(i, new Alarm(AlarmListAMPM.get(i).getId(), AlarmListAMPM.get(i).getTitle(),  String.valueOf(hour) + ":" + str1 + "AM", AlarmListAMPM.get(i).getEnable()));

                    }
                    else
                        AlarmListAMPM.set(i, new Alarm(AlarmListAMPM.get(i).getId(), AlarmListAMPM.get(i).getTitle(), AlarmListAMPM.get(i).getTime() + "AM", AlarmListAMPM.get(i).getEnable()));

                }
                else {
                    //subtracts 12 in order to get AM/PM format
                    String str1 = AlarmListAMPM.get(i).getTime().substring(3, 5);
                    if (hour != 12) {
                        hour = hour - 12;
                        if (hour < 10) {
                            AlarmListAMPM.set(i, new Alarm(AlarmListAMPM.get(i).getId(), AlarmListAMPM.get(i).getTitle(), "0" + String.valueOf(hour) + ":" + str1 + "PM", AlarmListAMPM.get(i).getEnable()));
                        } else
                            AlarmListAMPM.set(i, new Alarm(AlarmListAMPM.get(i).getId(), AlarmListAMPM.get(i).getTitle(), String.valueOf(hour) + ":" + str1 + "PM", AlarmListAMPM.get(i).getEnable()));


                    } else {
                        AlarmListAMPM.set(i, new Alarm(AlarmListAMPM.get(i).getId(), AlarmListAMPM.get(i).getTitle(), String.valueOf(hour) + ":" + str1 + "PM", AlarmListAMPM.get(i).getEnable()));

                    }
                }

            }
            //check view type Alarm
            if (timeView == false) {
                alarmListAM_PM = AlarmListAMPM;
                customAdapter = new custom_list_adapter(this, Alarm);
                alarmsListView.setAdapter(customAdapter);
            }
            else {
                alarmListAM_PM = AlarmListAMPM;
                customAdapter = new custom_list_adapter(this, alarmListAM_PM);
                alarmsListView.setAdapter(customAdapter);
            }
        }
        else
            recreate();




    }
    protected void loadListViewNoWifi() {
    // dummy List to set the global array Alarm to the same order as the alarmList displayed on the user interface
    if (AlarmnoWifiAMPM.size() > 0 && AlarmnoWifi.size()>0) {
        //sorts the times
        Collections.sort(AlarmnoWifi, new Comparator<com.example.anthony.wakeupwarden.Alarm>() {
            @Override
            public int compare(Alarm o1, Alarm o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });

        for (int i = 0; i < AlarmnoWifi.size(); i++) {

                String str = AlarmnoWifi.get(i).getTime().substring(0, 2);
                int hour = Integer.valueOf(str);
                if (hour < 12) {
                    if (hour == 0){ // if midnight set to AM/PM form
                        hour = 12;
                        String str1 = AlarmnoWifiAMPM.get(i).getTime().substring(3, 5);
                        AlarmnoWifiAMPM.set(i, new Alarm(AlarmnoWifiAMPM.get(i).getId(), AlarmnoWifiAMPM.get(i).getTitle(),  String.valueOf(hour) + ":" + str1 + "AM", AlarmnoWifiAMPM.get(i).getEnable()));

                    }
                    else
                        AlarmnoWifiAMPM.set(i, new Alarm(AlarmnoWifi.get(i).getId(), AlarmnoWifi.get(i).getTitle(), AlarmnoWifi.get(i).getTime() + "AM", AlarmnoWifi.get(i).getEnable()));
                } else {
                    String str1 = AlarmnoWifi.get(i).getTime().substring(3, 5);
                    if (hour != 12) {
                        hour = hour - 12;
                        if (hour < 10) {
                            AlarmnoWifiAMPM.set(i, new Alarm(AlarmnoWifi.get(i).getId(), AlarmnoWifi.get(i).getTitle(), "0" + String.valueOf(hour) + ":" + str1 + "PM", AlarmnoWifi.get(i).getEnable()));
                        } else
                            AlarmnoWifiAMPM.set(i, new Alarm(AlarmnoWifi.get(i).getId(), AlarmnoWifi.get(i).getTitle(), String.valueOf(hour) + ":" + str1 + "PM", AlarmnoWifi.get(i).getEnable()));


                    } else {
                        AlarmnoWifiAMPM.set(i, new Alarm(AlarmnoWifi.get(i).getId(), AlarmnoWifi.get(i).getTitle(), String.valueOf(hour) + ":" + str1 + "PM", AlarmnoWifi.get(i).getEnable()));

                    }
                }



    }

    if (timeView == false) {
        customAdapter = new custom_list_adapter(this, AlarmnoWifi);
        alarmsListView.setAdapter(customAdapter);
    } else {
        customAdapter = new custom_list_adapter(this,AlarmnoWifiAMPM );
        alarmsListView.setAdapter(customAdapter);
    }
}

    }


    // add the toolbar menu option
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.time_view, menu);

        return true;
    }
    //Actions when items are selected
    public boolean onOptionsItemSelected(MenuItem item) {
        if((Alarm.size()>0 && alarmListAM_PM.size()>0) || (AlarmnoWifi.size()>0 && AlarmnoWifiAMPM.size()>0)) {
            ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiCheck.isConnected()) {
                switch (item.getItemId()) {
                    case R.id.am_pm:
                        // User chose the "Settings" item, show the app settings UI...
                        if (timeView == false && alarmListAM_PM.size()>0) {
                            customAdapter = new custom_list_adapter(this, alarmListAM_PM);
                            alarmsListView.setAdapter(customAdapter);
                            timeView = true;
                        }
                        else
                            recreate();

                        return true;

                    case R.id.armyTime:
                        // User chose the "Favorite" action, mark the current item
                        // as a favorite...
                        if (timeView == true && Alarm.size()>0) {
                            customAdapter = new custom_list_adapter(this, Alarm);
                            if(Alarm.size()>0) {
                                alarmsListView.setAdapter(customAdapter);
                                timeView = false;
                            }
                            else
                                recreate();

                        }
                        return true;

                    default:
                        // If we got here, the user's action was not recognized.
                        // Invoke the superclass to handle it.
                        return super.onOptionsItemSelected(item);

                }
            } else {
                switch (item.getItemId()) {
                    case R.id.am_pm:
                        // User chose the "Settings" item, show the app settings UI...
                        if (timeView == false && AlarmnoWifiAMPM.size() > 0) {
                            customAdapter = new custom_list_adapter(this, AlarmnoWifiAMPM);
                            alarmsListView.setAdapter(customAdapter);
                            timeView = true;
                        }
                        else
                            recreate();

                        return true;

                    case R.id.armyTime:
                        // User chose the "Favorite" action, mark the current item
                        // as a favorite...
                        if (timeView == true && AlarmnoWifi.size()>0) {
                            customAdapter = new custom_list_adapter(this, AlarmnoWifi);
                            alarmsListView.setAdapter(customAdapter);
                            timeView = false;
                        }
                        else
                            recreate();
                        return true;

                    default:
                        // If we got here, the user's action was not recognized.
                        // Invoke the superclass to handle it.
                        return super.onOptionsItemSelected(item);

                }
            }
        }
        else
            return true;



    }

}
