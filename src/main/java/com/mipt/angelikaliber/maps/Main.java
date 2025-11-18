package com.mipt.angelikaliber.maps;

import java.io.FilterOutputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        HashMap hashMap = new HashMap<Integer, Student>();
        hashMap.put(123, new Student(123, "Ivanov", 10));
        hashMap.put(124, new Student(124, "Ivanova", 8));
        hashMap.put(125, new Student(125, "Ivanovo", 9));
        hashMap.put(126, new Student(126, "Korolev", 7));
        hashMap.put(127, new Student(127, "Koroleva", 8));

        TreeMap treeMap = new TreeMap<Integer, Student>(Comparator.reverseOrder());
        treeMap.put(123, new Student(123, "Ivanov", 10));
        treeMap.put(124, new Student(124, "Ivanova", 8));
        treeMap.put(125, new Student(125, "Ivanovo", 9));
        treeMap.put(126, new Student(126, "Korolev", 7));
        treeMap.put(127, new Student(127, "Koroleva", 8));

        System.out.println(treeMap);

        System.out.println(Student.findStudentsByGradeRange(treeMap, 8, 9));
        System.out.println("getTopNStudents");
        System.out.println(Student.getTopNStudents(treeMap, 2));
    }
}
