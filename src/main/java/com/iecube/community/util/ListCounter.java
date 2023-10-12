package com.iecube.community.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

public class ListCounter {

    public static class Occurrence {
        private final String item;
        private final int count;

        public Occurrence(String item, int count) {
            this.item = item;
            this.count = count;
        }

        public String getItem() {
            return item;
        }

        public int getCount() {
            return count;
        }

        @Override
        public String toString() {
            return "{item:'" + item + "', count:" + count + "}";
        }
    }

    public static List<Occurrence> countOccurrences(List<String> list) {
        Map<String, Integer> countMap = new HashMap<>();
        for (String item : list) {
            countMap.put(item, countMap.getOrDefault(item, 0) + 1);
        }

        List<Occurrence> occurrences = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            occurrences.add(new Occurrence(entry.getKey(), entry.getValue()));
        }
        // 根据count对occurrences进行排序
        occurrences.sort(Comparator.comparingInt(Occurrence::getCount).reversed());

        return occurrences;
    }
}
