package com.silentdynamics.student.blacklist.dummy;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    static {
        //SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        //Date d = new Date(ft.parse());
        // Add 3 sample items.
        addItem(new DummyItem("1", "Best Event ever", "Fußball", 51.0389224, 13.7598763, "18:30", "22:00"));
        addItem(new DummyItem("2", "Spieleabend", "Fußball", 51.0431384, 13.7667263, "20:00", "24:00"));
        addItem(new DummyItem("3", "Sauftour", "Politik", 51.0313414, 13.776691, "20:30", "0:30"));
        addItem(new DummyItem("4", "Party!! yay", "Politik", 51.0413414, 13.7666913, "21:15", "02:00"));
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String id;
        public String name;
        public String topic;
        public double lat;
        public double lng;
        public String start;
        public String end;

        public DummyItem(String id, String name, String topic, double lat, double lng, String start, String end) {
            this.id = id;
            this.name = name;
            this.topic = topic;
            this.lat = lat;
            this.lng = lng;
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
