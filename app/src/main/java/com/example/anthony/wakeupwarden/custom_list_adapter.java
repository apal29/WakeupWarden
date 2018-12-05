package com.example.anthony.wakeupwarden;

//////////////////////////
// Custom Adapter was created with inspiration from the following site:  https://demonuts.com/listview-button/

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class custom_list_adapter extends BaseAdapter {

    //initialize the global variables
    private Context context;
    ArrayList<Alarm> Alarm;


    //Initialize a constructor custom list adapter
    public custom_list_adapter(Context context, ArrayList<Alarm> Alarm) {

        this.context = context;
        this.Alarm = Alarm;
    }


    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return Alarm.size();
    }

    @Override
    public Object getItem(int position) {
        return Alarm.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //set view of the listview
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list, null, true);


            holder.time = (TextView) convertView.findViewById(R.id.Time);
            holder.title = (TextView) convertView.findViewById(R.id.Title);
            holder.enable = (Switch) convertView.findViewById(R.id.enableButton);


            convertView.setTag(holder);
        } else {
            // getTag returns convertView
            holder = (ViewHolder) convertView.getTag();
        }
        //set the text of the Alarm Array
        holder.time.setText(Alarm.get(position).getTime());
        holder.title.setText(Alarm.get(position).getTitle());
        holder.enable.setChecked(Alarm.get(position).getEnable());

        //used in order to make the listView clickable for the fragment
        holder.enable.setFocusable(false);
        //save toggle
        //check to see if wifi is connected
        // if wifi connected, the toggle will be saved
        //if the wifi is not connected the toggle option will locked and toast will be displayed to connect to wifi
       if (MainActivity.wifiCheck.isConnected()) {
           holder.enable.setClickable(true);
           holder.enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton cb, boolean on) {
                   Boolean changeEnable;
                   if (on) {
                       changeEnable = true;


                   } else {
                       changeEnable = false;
                   }
                   holder.enable.setChecked(changeEnable);
                   //save changes to cloud database
                   Alarm newAlarm = new Alarm(MainActivity.Alarm.get(position).getId(), MainActivity.Alarm.get(position).getTitle(), MainActivity.Alarm.get(position).getTime(), changeEnable);
                   DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("alarms");
                   ref.child(MainActivity.Alarm.get(position).getId()).setValue(newAlarm);

               }
           });
       }
       else {
           holder.enable.setClickable(false);
           Toast toast = Toast.makeText(parent.getContext(), "Connect to Wifi to edit your alarm", Toast.LENGTH_SHORT);
           //toast.show();
           //holder.enable.setClickable(true);
       }


        return convertView;
    }


    private class ViewHolder {

        protected Switch enable;
        private TextView time, title;

    }
}
