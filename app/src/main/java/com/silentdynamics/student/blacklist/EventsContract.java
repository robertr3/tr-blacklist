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
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_TOPIC = "topic1";
        public static final String COLUMN_NAME_TIMESTART = "timestart";
        public static final String COLUMN_NAME_TIMEEND = "timeend";
        public static final String COLUMN_NAME_LCOATION = "location";
        public static final String COLUMN_NAME_PRIVACY = "privacy";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_UPDATE = "updateStatus";
    }
}
