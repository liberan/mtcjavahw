package com.mipt.angelikaliber.generic;

public class ArrayUtils {
    public static <T> int findFirst(T[] array, T element) {
        for (int i = 0; i < array.length; i++) {
            if ((array[i] == null && element != null)) {
                continue;
            }
            if ((array[i] == null && element == null) || array[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        // пример использования
        final String[] names = {"Alice", "Bob", "Charlie"};
        final int index = ArrayUtils.findFirst(names, "Bob"); // Ожидаем: 1 (тк нумерация в массиве начинается с нуля)
        System.out.println(index);
    }
}