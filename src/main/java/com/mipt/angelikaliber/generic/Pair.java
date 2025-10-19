package com.mipt.angelikaliber.generic;

public class Pair<K, V> {
    private K key;
    private V value;
    Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(K key){
        this.key = key;
    }

    public void setValue( V value) {
        this.value = value;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public Pair<V, K> swap() {
        return new Pair<>(this.value, this.key);
    }

    public String toString() {
        return "Pair{key=" + key + ", value=" + value + "}";
    }

    public static void main(String[] args) {
        // пример использования
        final Pair<String, Integer> pair = new Pair<>("Age", 25);
        System.out.println(pair); // Ожидаем: Pair{key=Age, value=25}
        final Pair<Integer, String> swapped = pair.swap();
        System.out.println(swapped); // Ожидаем: Pair{key=25, value=Age}
    }
}