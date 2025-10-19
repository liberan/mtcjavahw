package com.mipt.angelikaliber.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionUtils {
    public static <T> List<T> mergeLists(List<? extends T> list1,
                                         List<? extends T> list2) {
        List<T> resultList = new ArrayList<>();
        resultList.addAll(list1);
        resultList.addAll(list2);
        return resultList;
    }

    public static <T> void addAll(List<? super T> destination,
                                  List<? extends T> source) {
        destination.addAll(source);
    }

    public static void main(String[] args) {
        final List<Integer> list1 = Arrays.asList(1, 2, 3);
        final List<Double> list2 = Arrays.asList(4.5, 5.6);
        final List<Number> merged = CollectionUtils.mergeLists(list1, list2);

        final List<Object> destination = new ArrayList<>();
        CollectionUtils.addAll(destination, list1);

        System.out.println(merged);
        System.out.println(destination);
    }
}
