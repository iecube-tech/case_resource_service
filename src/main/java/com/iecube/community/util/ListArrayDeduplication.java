package com.iecube.community.util;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * List 去重工具
 */
public class ListArrayDeduplication {
    public static List<Integer> removeDuplicates(List<Integer> list) {
        return list.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public static void test(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(4);

        System.out.println("原始列表: " + list);
        List<Integer> deduplicatedList = removeDuplicates(list);
        System.out.println("去重后的列表: " + deduplicatedList);
    }
}