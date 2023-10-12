package com.iecube.community.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuickSort {

    public static void quickSort(List<Integer> list) {
        if (list == null || list.size() <= 1) {
            return ;
        }
        quickSort(list, 0, list.size() - 1);
    }

    private static void quickSort(List<Integer> list, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(list, low, high);
            quickSort(list, low, pivotIndex - 1);
            quickSort(list, pivotIndex + 1, high);
        }
    }

    private static int partition(List<Integer> list, int low, int high) {
        int pivot = list.get(high);  // 取最后一个元素作为基准
        int i = low;
        for (int j = low; j < high; j++) {
            if (list.get(j) < pivot) {
                swap(list, i, j);
                i++;
            }
        }
        swap(list, i, high);
        return i;
    }

    private static void swap(List<Integer> list, int i, int j) {
        int temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
