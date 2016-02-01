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
 * Created by Student on 01.02.2016.
 */
public class BookmarklistAdapter extends BaseAdapter implements ListAdapter {
    private static final String TAG = BookmarklistAdapter.class.getSimpleName();
    private ArrayList<HashMap<String, String>> bookmarklist = new ArrayList<HashMap<String, String>>();
    private Context context;
    DBController controller;


    public BookmarklistAdapter(ArrayList<HashMap<String, String>> list, Context context) {
        this.bookmarklist = list;
        this.context = context;
        controller = new DBController(context);

    }

    @Override
    public int getCount() {
        return bookmarklist.size();
    }

    @Override
    public Object getItem(int pos) {
        return bookmarklist.get(pos);
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
        eventId.setText(bookmarklist.get(position).get("id"));
        eventName.setText(bookmarklist.get(position).get("name"));
        eventTopic.setText(bookmarklist.get(position).get("topic1"));

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        Button addBtn = (Button)view.findViewById(R.id.edit_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                controller.switchBookmark(bookmarklist.get(position).get("id"));
                bookmarklist.remove(position);
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
                //do something
                notifyDataSetChanged();
            }
        });

        return view;
    }
}