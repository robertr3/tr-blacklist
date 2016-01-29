package com.silentdynamics.student.blacklist.dummy;

import java.util.ArrayList;
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
        // Add 3 sample items.
        addItem(new DummyItem("1", "Item 1", "Fußball", 51.0389224, 13.7598763));
        addItem(new DummyItem("2", "Item 2", "Fußball", 51.0431384, 13.7667263));
        addItem(new DummyItem("3", "Item 3", "Politik", 51.0313414, 13.7766913));
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
        public String content;
        public String topic;
        public double lat;
        public double lng;

        public DummyItem(String id, String content, String topic, double lat, double lng) {
            this.id = id;
            this.content = content;
            this.topic = topic;
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
