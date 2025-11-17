package com.mipt.angelikaliber.collections;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CollectionPerformanceTester {

    public class Table {
        public static void print(String title, String[] headers, Object[][] rows) {
            System.out.println("=== " + title + " ===");

            int[] widths = new int[headers.length];
            for (int c = 0; c < headers.length; c++) {
                widths[c] = headers[c].length();
            }
            for (Object[] row : rows) {
                for (int c = 0; c < row.length; c++) {
                    widths[c] = Math.max(widths[c], row[c].toString().length());
                }
            }

            StringBuilder line = new StringBuilder();
            for (int w : widths) line.append("+").append("-".repeat(w + 2));
            line.append("+");

            System.out.println(line);
            System.out.print("|");
            for (int c = 0; c < headers.length; c++)
                System.out.printf(" %-"+widths[c]+"s |", headers[c]);
            System.out.println();
            System.out.println(line);

            for (Object[] row : rows) {
                System.out.print("|");
                for (int c = 0; c < row.length; c++)
                    System.out.printf(" %-"+widths[c]+"s |", row[c]);
                System.out.println();
            }

            System.out.println(line);
        }
    }

    String title = "CollectionPerformanceTester results в мкс";
    String[] headers = {
            "Операция", "ArrayList", "LinkedList"
    };
    static Object[][] rows = {
            {"Добавление в конец", 0, 0},
            {"Добавление в начало", 0, 0},
            {"Вставка в середину", 0, 0},
            {"Доступ по индексу", 0, 0},
            {"Удаление из начала", 0, 0},
            {"Удаление из конца", 0, 0}
    };

    //ArrayList
    @Order(1)
    @Test
    void ArrayList_addToEnd() {
        ArrayList testArrayList = new ArrayList();
        for (int i = 0; i < 10000; i++){
            testArrayList.add(i);
        }
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++){
            testArrayList.add(i);
        }
        long endTime = System.nanoTime();
        rows[0][1] = endTime - startTime;
    }

    @Order(2)
    @Test
    void ArrayList_addToStart() {
        ArrayList testArrayList = new ArrayList();
        for (int i = 0; i < 10000; i++){
            testArrayList.add(i);
        }
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++){
            testArrayList.add(0, i);
        }
        long endTime = System.nanoTime();
        rows[1][1] = endTime - startTime;
    }

    @Order(3)
    @Test
    void ArrayList_addToMid() {
        ArrayList testArrayList = new ArrayList();
        for (int i = 0; i < 10000; i++){
            testArrayList.add(i);
        }
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++){
            testArrayList.add((int)(testArrayList.size() / 2), i);
        }
        long endTime = System.nanoTime();
        rows[2][1] = endTime - startTime;
    }

    @Order(4)
    @Test
    void ArrayList_toindex() {
        ArrayList testArrayList = new ArrayList();
        for (int i = 0; i < 10000; i++){
            testArrayList.add(i);
        }
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++){
            testArrayList.get(i);
        }
        long endTime = System.nanoTime();
        rows[3][1] = endTime - startTime;
    }

    @Order(5)
    @Test
    void ArrayList_removeFromStart() {
        ArrayList testArrayList = new ArrayList();
        for (int i = 0; i < 10000; i++){
            testArrayList.add(i);
        }
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++){
            testArrayList.remove(0);
        }
        long endTime = System.nanoTime();
        rows[4][1] = endTime - startTime;
    }

    @Order(6)
    @Test
    void ArrayList_removeFromEnd() {
        ArrayList testArrayList = new ArrayList();
        for (int i = 0; i < 10000; i++){
            testArrayList.add(i);
        }
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++){
            testArrayList.remove(testArrayList.size() - 1);
        }
        long endTime = System.nanoTime();
        rows[5][1] = endTime - startTime;
    }

    //LinkedList
    @Order(7)
    @Test
    void LinkedList_addToEnd() {
        LinkedList testLinkedList = new LinkedList();
        for (int i = 0; i < 10000; i++){
            testLinkedList.add(i);
        }
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++){
            testLinkedList.add(i);
        }
        long endTime = System.nanoTime();
        rows[0][2] = endTime - startTime;
    }

    @Order(8)
    @Test
    void LinkedList_addToStart() {
        LinkedList testLinkedList = new LinkedList();
        for (int i = 0; i < 10000; i++){
            testLinkedList.add(i);
        }
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++){
            testLinkedList.add(0, i);
        }
        long endTime = System.nanoTime();
        rows[1][2] = endTime - startTime;
    }

    @Order(9)
    @Test
    void LinkedList_addToMid() {
        LinkedList testLinkedList = new LinkedList();
        for (int i = 0; i < 10000; i++){
            testLinkedList.add(i);
        }
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++){
            testLinkedList.add((int)(testLinkedList.size() / 2), i);
        }
        long endTime = System.nanoTime();
        rows[2][2] = endTime - startTime;
    }

    @Order(10)
    @Test
    void LinkedList_toindex() {
        LinkedList testLinkedList = new LinkedList();
        for (int i = 0; i < 10000; i++){
            testLinkedList.add(i);
        }
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++){
            testLinkedList.get(i);
        }
        long endTime = System.nanoTime();
        rows[3][2] = endTime - startTime;
    }

    @Order(11)
    @Test
    void LinkedList_removeFromStart() {
        LinkedList testLinkedList = new LinkedList();
        for (int i = 0; i < 10000; i++){
            testLinkedList.add(i);
        }
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++){
            testLinkedList.remove(0);
        }
        long endTime = System.nanoTime();
        rows[4][2] = endTime - startTime;
    }

    @Order(12)
    @Test
    void LinkedList_removeFromEnd() {
        LinkedList testLinkedList = new LinkedList();
        for (int i = 0; i < 10000; i++){
            testLinkedList.add(i);
        }
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++){
            testLinkedList.remove(testLinkedList.size() - 1);
        }
        long endTime = System.nanoTime();
        rows[5][2] = endTime - startTime;
    }

    @Order(13)
    @Test
    void result (){
        Table.print(title, headers, rows);
    }

}
