package com.mipt.angelikaliber.maps;

import java.util.*;

class Student implements Comparable <Student> {
    public int id;
    public String name;
    public double grade;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(Student o) {
        if (this.id > o.id){
            return 1;
        } else if (this.id < o.id) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", grade=" + grade +
                '}';
    }

    public static List<Student> findStudentsByGradeRange(Map<Integer, Student> map, double minGrade, double maxGrade) {
        List<Student> resultList = new LinkedList<Student>();
        for (Student s : map.values()) {
            if (s.grade >= minGrade && s.grade <= maxGrade) {
                resultList.add(s);
            }
        }
        return resultList;
    }

    public static List<Student> getTopNStudents(TreeMap<Integer, Student> map, int n) {
        return map.values().stream().limit(n).toList();
    }

    public Student(int id, String name, double grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }
}