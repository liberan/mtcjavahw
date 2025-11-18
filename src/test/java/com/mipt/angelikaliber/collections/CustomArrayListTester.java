package com.mipt.angelikaliber.collections;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

//add get remove size isEmpty

public class CustomArrayListTester {
    @Test
    void testAdd_ElementAddition() {
        CustomArrayList<String> data = new CustomArrayList<String>(2);
        data.add("1");
        data.add("2");
        assertEquals(data.toString(), "1 2 ");
    }

    @Test
    void testGet_ElementGetting() {
        CustomArrayList<String> data = new CustomArrayList<String>(2);
        data.add("1");
        data.add("2");
        assertEquals(data.get(0), "1");
    }

    @Test
    void testRemove_ElementRemoving() {
        CustomArrayList<String> data = new CustomArrayList<String>(3);
        data.add("1");
        data.add("2");
        data.add("3");
        data.remove(1);
        assertEquals(data.toString(), "1 3 ");
    }

    @Test
    void testIsEmpty_isListEmpty() {
        CustomArrayList<String> data = new CustomArrayList<String>(3);
        assertEquals(data.isEmpty(), true);
        data.add("1");
        assertEquals(data.isEmpty(), false);
    }

    @Test
    void testSize_ListsSize() {
        CustomArrayList<String> data = new CustomArrayList<String>(3);
        data.add("1");
        assertEquals(data.size(), 1);
        data.add("1");
        assertEquals(data.size(), 2);
    }
}
