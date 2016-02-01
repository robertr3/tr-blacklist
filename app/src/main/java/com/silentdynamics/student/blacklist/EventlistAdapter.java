package com.silentdynamics.student.blacklist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Student on 31.01.2016.
 */
public class EventlistAdapter extends BaseAdapter implements ListAdapter{
    private static final String TAG = EventlistAdapter.class.getSimpleName();
    private ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    private Context context;
    DBController controller;


    public EventlistAdapter(ArrayList<HashMap<String, String>> list, Context context) {
        this.list = list;
        this.context = context;
        controller = new DBController(context);

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
      //  return list.get(pos).getId();
        //just return 0 if your list items do not have an Id variable.
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.view_event_entry, null);
        }

        //Handle TextView and display string from your list
        TextView eventId = (TextView)view.findViewById(R.id.eventId);
        TextView eventName = (TextView) view.findViewById(R.id.eventName);
        TextView eventTopic = (TextView)view.findViewById(R.id.eventTopic);
        eventId.setText(list.get(position).get("id"));
        eventName.setText(list.get(position).get("name"));
        eventTopic.setText(list.get(position).get("topic1"));

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        Button addBtn = (Button)view.findViewById(R.id.edit_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                controller.deleteEvent(list.get(position).get("id"));
                list.remove(position);
                ArrayList<HashMap<String, String>> eventList =  controller.getAllEvents();
                for (int i = 0; i < eventList.size(); i++){
                    Log.d(TAG, "eventList " + i + ": " + eventList.get(i));
                }
                notifyDataSetChanged();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // do something
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
