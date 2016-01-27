package com.silentdynamics.student.blacklist;

import android.provider.BaseColumns;

/**
 * Created by Student on 27.01.2016.
 */
public final class EventsContract {

    public EventsContract() {
    }

    public static abstract class EventsEntry implements BaseColumns {
        public static final String TABLE_NAME = "eventslocal";
        public static final String COLUMN_NAME_ENTRY_ID = "eventid";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_TOPIC = "topic";
        public static final String COLUMN_NAME_UPDATE = "updateStatus";
    }
}
