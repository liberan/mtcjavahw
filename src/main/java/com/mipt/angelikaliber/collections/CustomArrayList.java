package com.mipt.angelikaliber.collections;

public class CustomArrayList<A extends  Object> implements CustomList<A> {
    private Object[] data;
    private int capacity;

    CustomArrayList(int initialCapacity) {
        this.data = new Object[initialCapacity];
        this.capacity = 0;
    }

    @Override
    public void add(A element) {
        if (this.capacity == this.data.length) {
            Object[] data2 = new Object[(int)(this.capacity * 1.5)];
            for (int i =0; i < this.capacity; i++) {
                data2[i] = this.data[i];
            }
            this.data = data2;
        }
        this.data[this.capacity] = element;
        this.capacity++;
    }

    public A get(int index) {
        return (A)(this.data[index]);
    }

    public void remove(int index) {
        this.capacity--;
        for (int i = index; i < this.capacity; i++) {
            data[i]=data[i + 1];
        }
    }

    public int size() {
        return this.capacity;
    }

    public boolean isEmpty() {
        return (this.capacity == 0);
    }

    public String toString() {
        String str = "";
        for (int i = 0; i < this.capacity; i++) {
            str += this.data[i].toString();
            str += " ";
        }
        return str;
    }

    public static void main( String[] args) {
       CustomArrayList<String> data = new CustomArrayList<String>(2);
        System.out.println(data.isEmpty());
       data.add("1");
        System.out.println(data.size());
        System.out.println(data);
       data.add("2");
        System.out.println(data);
       data.add("3");
        System.out.println(data);
        System.out.println(data.size());
       data.add("4");
        System.out.println(data);
       data.add("5");
        System.out.println(data);
        System.out.println(data.size());
       data.add("6");
        System.out.println(data);
       data.add("1");
       System.out.println(data);
       data.remove(0);
        System.out.println(data);
        System.out.println(data.size());
        System.out.println(data.isEmpty());
        System.out.println(data.get(4));
    }
}

